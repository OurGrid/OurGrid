package org.ourgrid.broker.controlws.gatewayws.transferserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TransferUtil {
	private static final String ERROR_TOKEN = "#ERROR#";
	private static final String EOF = "#EOF#";
	private static final int PACKET_SIZE = 1024;

	public static void writeTransferHeader(TransferHeader header, OutputStream outputStream)
	throws IOException {
		writeToStream(header.toString(), outputStream);
	}

	public static TransferHeader readTransferHeader(InputStream inputStream) throws IOException {
		return TransferHeader.parseHeader(readFromStream(inputStream));
	}

	private static void writeToStream(String string, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[PACKET_SIZE];
		byte[] strBytes = (string + EOF).getBytes();

		for (int i = 0; i < strBytes.length; ++i) {
			buffer[i] = strBytes[i];
		}

		outputStream.write(buffer);
	}

	public static void writeToStream(File sourceFile, OutputStream outputStream) throws IOException
	{
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(sourceFile);

			byte[] buffer = new byte[PACKET_SIZE];
			int len = 0;

			boolean reachEnd = false;
			int i;
			while ((len = inputStream.read(buffer)) > 0) {
				if (len < PACKET_SIZE) {
					byte[] eofBytes = EOF.getBytes();
					for (i = len; i < len + eofBytes.length; ++i) {
						buffer[i] = eofBytes[(i - len)];
					}
					reachEnd = true;
				}
				outputStream.write(buffer);
				buffer = new byte[PACKET_SIZE];
			}
			if (!(reachEnd)) {
				buffer = new byte[PACKET_SIZE];
				byte[] strBytes = EOF.getBytes();
				for (i = 0; i < strBytes.length; ++i) {
					buffer[i] = strBytes[i];
				}

				outputStream.write(buffer);
			}

			inputStream.close();
		}
		catch (IOException e) {
			outputStream.write(ERROR_TOKEN.getBytes());
			throw e;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		FileOutputStream outputStream = new FileOutputStream("teste.tar");
		writeTransferHeader(new TransferHeader(1, "remotedir", "filename"), outputStream);
		writeToStream(new File("spin520.tar.gz"), outputStream);
		outputStream.close();

		FileInputStream inputStream = new FileInputStream("teste.tar");
		System.out.println(readTransferHeader(inputStream));
		writeFromStream(new File("teste2.tar"), inputStream);
		inputStream.close();
	}

	public static String readFromStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[PACKET_SIZE];
		inputStream.read(buffer);

		String bytesToString = bytesToString(buffer, buffer.length);
		return bytesToString.substring(0, bytesToString.indexOf(EOF)); 
	} 
	
	public static void writeFromStream(File destinationFile, InputStream inputStream) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(destinationFile);
            byte buffer[] = new byte[1024];
            while (true) {
            	
                inputStream.read(buffer);
                String packet = bytesToString(buffer, buffer.length);
                
                if(packet.contains(ERROR_TOKEN)) {
                	throw new IOException((new StringBuilder("Error while receiving file ")).append(destinationFile).toString());
                }
                
                if(packet.contains(EOF)) {
                    for(int i = 0; i < buffer.length - EOF.getBytes().length; i++) {
                        byte copyOfRange[] = copyOfRange(buffer, i, i + EOF.getBytes().length);
                        if(bytesToString(copyOfRange, copyOfRange.length).equals("#EOF#")) {
                        	fileOutputStream.write(buffer, 0, i);
                        }
                    }

                    break;
                }
                
                fileOutputStream.write(buffer);
                buffer = new byte[1024];
                
            }
        } finally {
        	if(fileOutputStream != null) {
        		fileOutputStream.close();
        	}
        }
	}
	
	private static String bytesToString(byte[] buffer, int len) { 
		return new String(buffer, 0, len);
	}
	
	private static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);
		
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));

		return copy;
	}
}