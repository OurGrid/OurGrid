package org.ourgrid.broker.response.to;

public abstract class OperationTO {
	
	private int jobID;
	private int taskID;
	private int processID;
	private long requestID2;
	private String workerID2;
	private String localFilePath;
	private String remoteFilePath;
	private String transferDescription;
	private long initTime;
	private long endTime;
	private long fileSize;
	
	public int getJobID() {
		return jobID;
	}
	
	public void setJobID(int jobID) {
		this.jobID = jobID;
	}
	
	public int getTaskID() {
		return taskID;
	}
	
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	
	public int getProcessID() {
		return processID;
	}
	
	public void setProcessID(int processID) {
		this.processID = processID;
	}
	
	public long getRequestID2() {
		return requestID2;
	}
	
	public void setRequestID2(long requestID2) {
		this.requestID2 = requestID2;
	}
	
	public String getWorkerID2() {
		return workerID2;
	}
	
	public void setWorkerID2(String workerID2) {
		this.workerID2 = workerID2;
	}
	
	public String getLocalFilePath() {
		return localFilePath;
	}
	
	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}
	
	public String getRemoteFilePath() {
		return remoteFilePath;
	}
	
	public void setRemoteFilePath(String remoteFilePath) {
		this.remoteFilePath = remoteFilePath;
	}
	
	public String getTransferDescription() {
		return transferDescription;
	}
	
	public void setTransferDescription(String transferDescription) {
		this.transferDescription = transferDescription;
	}
	
	public long getInitTime() {
		return initTime;
	}
	
	public void setInitTime(long initTime) {
		this.initTime = initTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getFileSize() {
		return fileSize;
	}
}
