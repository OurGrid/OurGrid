package org.ourgrid.common.interfaces.to;

import java.io.File;



public class OutgoingHandle extends GenericTransferHandle {

	
	private static final long serialVersionUID = 1L;

	
	private String destinationID;
	
	public OutgoingHandle(){}

	public OutgoingHandle(Long id, String localFileName, File localFile, String description, String destinationContainerID) {
		
		super(id, localFileName, localFile, description);
		this.setDestinationID(destinationContainerID);
		if (localFile != null) {
			setFileSize(localFile.length());
		}
	}
	
	public OutgoingHandle(String logicalFileName, File localFile, String description, String destinationContainerID) {
		this(randomID(), logicalFileName, localFile, description, destinationContainerID);
	}

	public String getDestinationID() {
		return destinationID;
	}
	
	@Override
	public String getOppositeID() {
		return getDestinationID();
	}

	public void setDestinationID(String destinationID) {
		this.destinationID = destinationID;
	}

}