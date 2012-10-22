package org.ourgrid.common.interfaces.to;

import java.io.File;


public class IncomingHandle extends GenericTransferHandle {

	
	private static final long serialVersionUID = 1L;
	
	
	private String senderContainerID;
	private String senderPublicKey;
	private boolean executable;
	private boolean readable;
	private boolean writable;

	
	public IncomingHandle() {}

	public IncomingHandle(String logicalFileName, String localFilePath, long fileSize, String description, 
			String senderContainerID) {
		this(randomID(), logicalFileName, new File(localFilePath), fileSize, 
				description, senderContainerID);
	}
	
	public IncomingHandle(Long id, String logicalFileName, long fileSize, String description, 
			String senderContainerID) {
		this(id, logicalFileName, null, fileSize, description, senderContainerID);
	}

	public IncomingHandle(Long id, String logicalFileName, File localFile, long fileSize, String description, 
			String senderContainerID) {
		super(id, logicalFileName, localFile, description);
		setFileSize(fileSize);
		this.senderContainerID = senderContainerID;
	}

	
	public String getSenderContainerID() {
		return senderContainerID;
	}

	public void setSenderContainerID(String senderID) {
		this.senderContainerID = senderID;
	}

	@Override
	public String getOppositeID() {
		return senderContainerID;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
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