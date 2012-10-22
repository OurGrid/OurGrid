package org.ourgrid.broker.controlws.gatewayws.transferserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.ourgrid.broker.controlws.gatewayws.Broker3GConstants;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.context.ModuleContext;

public class Broker3GTransferServer implements Runnable {

	private final CommuneLogger logger = CommuneLoggerFactory.getInstance().getLogger("WS3GAPPENDER");
	private final ModuleContext context;

	public Broker3GTransferServer(ModuleContext context) {
		this.context = context;
	}

	public void run() {

		int port = Integer.parseInt(this.context.getProperty(
				Broker3GConstants.BROKER_3G_TRANSFERPORT_PROP));

		ServerSocket socket;
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			this.logger.error("Error on server socket creation", e);
			throw new RuntimeException(e);
		}

		while (true) {
			try {
				Socket accept = socket.accept();	
				runSocketThread(accept);
			} catch (IOException e) {
				logger.warnException(e);
				continue;
			}				
		}
	}

	public static void sendFile(String remoteBaseDir, String logicalName, String inputPath, String fileTransferServerAddress, int fileTransferServerPort)
	throws IOException
	{
		Socket socket = null;
		try {
			socket = new Socket(fileTransferServerAddress, fileTransferServerPort);

			TransferHeader transferHeader = new TransferHeader(TransferHeader.PUT, remoteBaseDir, logicalName);
			TransferUtil.writeTransferHeader(transferHeader, socket.getOutputStream());
			TransferUtil.writeToStream(new File(inputPath), socket.getOutputStream());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

	public static void getFile(String remoteBaseDir, String logicalName, String outputPath, String fileTransferServerAddress, int fileTransferServerPort)
	throws IOException
	{
		Socket socket = null;
		try {
			socket = new Socket(fileTransferServerAddress, fileTransferServerPort);

			TransferHeader transferHeader = new TransferHeader(TransferHeader.GET, remoteBaseDir, logicalName);
			TransferUtil.writeTransferHeader(transferHeader, socket.getOutputStream());
			TransferUtil.writeFromStream(new File(outputPath), socket.getInputStream());
		}
		finally {
			if (socket != null){
				socket.close();
			}
		}
	}

	public static void sendFinishTransferMessage(String fileTransferServerAddress, int fileTransferServerPort)
	throws IOException
	{
		Socket socket = null;
		try {
			socket = new Socket(fileTransferServerAddress, fileTransferServerPort);
			TransferHeader transferHeader = new TransferHeader(TransferHeader.CLOSE);
			TransferUtil.writeTransferHeader(transferHeader, socket.getOutputStream());
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	protected void runSocketThread(Socket accept)
	{
		try
		{
			InputStream inputStream = accept.getInputStream();
			TransferHeader transferHeader = TransferUtil.readTransferHeader(inputStream);

			if (transferHeader.getOperationType() == TransferHeader.CLOSE) {
				return;
			}

			String baseDir = transferHeader.getRemoteBaseDir();

			String jobDir = this.context.getProperty(Broker3GConstants.BROKER_3G_TMPDIR_PROP) + 
			File.separator + baseDir;

			new File(jobDir).mkdirs();

			String fullFilePath = jobDir + File.separator + transferHeader.getFileName();

			switch (transferHeader.getOperationType())
			{
			case TransferHeader.PUT:
				TransferUtil.writeFromStream(new File(fullFilePath), inputStream);
				break;

			case TransferHeader.GET:
				TransferUtil.writeToStream(new File(fullFilePath), accept.getOutputStream());
				break;				

			default:
				throw new IOException("Stream in wrong format");
			}
		} catch (Exception e) {
			this.logger.warnException(e);
		} finally {
			try {
				accept.close();
			} catch (IOException e) {
				this.logger.warnException(e);
			}
		}
	}
}