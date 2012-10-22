package org.ourgrid.worker.response;

import java.io.File;

import org.ourgrid.worker.communication.sender.WorkerResponseConstants;


public class AcceptTransferResponseTO extends AbstractIncomingTransferResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.ACCEPT_TRANSFER;
	private File file;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
}
