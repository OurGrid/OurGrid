package org.ourgrid.broker.response.to;

public class TransferProgressTO {
	
	private long handleID;
	private String localFileName;
	private long fileSize;
	private String description;
	private String id;
	private String newStatus;
	private long amountWritten;
	private double progress;
	private double transferRate;
	private boolean outgoing;
	
	
	public long getHandleID() {
		return handleID;
	}
	
	public void setHandleID(long handleID) {
		this.handleID = handleID;
	}
	
	public String getLocalFileName() {
		return localFileName;
	}
	
	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNewStatus() {
		return newStatus;
	}
	
	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}
	
	public long getAmountWritten() {
		return amountWritten;
	}
	
	public void setAmountWritten(long amountWritten) {
		this.amountWritten = amountWritten;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public void setProgress(double progress) {
		this.progress = progress;
	}
	
	public double getTransferRate() {
		return transferRate;
	}
	
	public void setTransferRate(double transferRate) {
		this.transferRate = transferRate;
	}
	
	public boolean isOutgoing() {
		return outgoing;
	}
	
	public void setOutgoing(boolean outgoing) {
		this.outgoing = outgoing;
	}
}
