package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.internal.IRequestTO;

public class HereIsFileInfoProcessorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = BrokerRequestConstants.HERE_IS_FILE_INFO_PROCESSOR;
	
	
	private String workerAddress;
	private String workerContainerID;
	private long handlerId;
	private FileInfo fileInfo;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setWorkerContainerID(String workerContainerID) {
		this.workerContainerID = workerContainerID;
	}

	public String getWorkerContainerID() {
		return workerContainerID;
	}

	public void setHandlerId(long handlerId) {
		this.handlerId = handlerId;
	}

	public long getHandlerId() {
		return handlerId;
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
}