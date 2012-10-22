package org.ourgrid.broker.controlws.gatewayws.transferserver;

public class TransferHeader
{
	public static final int PUT = 0;
	public static final int GET = 1;
	public static final int CLOSE = 2;
	private final int operationType;
	private final String remoteBaseDir;
	private final String fileName;

	public TransferHeader(int operationType, String remoteBaseDir, String fileName)
	{
		this.operationType = operationType;
		this.remoteBaseDir = remoteBaseDir;
		this.fileName = fileName;
	}

	public TransferHeader(int operationType) {
		this(operationType, "", "");
	}

	public int getOperationType() {
		return this.operationType; }

	public String getRemoteBaseDir() {
		return this.remoteBaseDir;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String toString() {
		return this.operationType + ":" + this.remoteBaseDir + ":" + this.fileName;
	}

	public static TransferHeader parseHeader(String stringHeader)
	{
		String[] splittedChunk = stringHeader.split(":");

		if (splittedChunk.length != 3) {
			throw new IllegalArgumentException("Header in wrong format");
		}

		int operationType = Integer.valueOf(splittedChunk[0]).intValue();
		String baseDir = splittedChunk[1];
		String fileName = splittedChunk[2];

		return new TransferHeader(operationType, baseDir, fileName);
	}
}