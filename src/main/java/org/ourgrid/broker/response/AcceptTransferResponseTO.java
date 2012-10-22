package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;

public class AcceptTransferResponseTO implements IResponseTO {
	
	private static final String RESPONSE_TYPE = BrokerResponseConstants.ACCEPT_TRANSFER;
	
	
	private long id;
	private String description;
	private String senderContainerID;
	private long fileSize;
	private boolean executable;
	private boolean readable;
	private boolean writable;


	private String localFilePath;

	private String logicalFileName;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSenderContainerID() {
		return senderContainerID;
	}

	public void setSenderContainerID(String senderContainerID) {
		this.senderContainerID = senderContainerID;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	public void setLogicalFileName(String logicalFileName) {
		this.logicalFileName = logicalFileName;
	}
	
	public String getLocalFilePath() {
		return localFilePath;
	}

	public String getLogicalFileName() {
		return logicalFileName;
	}

	public boolean isExecutable() {
		return executable;
	}

	public void setExecutable(boolean executable) {
		this.executable = executable;
	}

	public boolean isReadable() {
		return readable;
	}

	public void setReadable(boolean readable) {
		this.readable = readable;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}
}
