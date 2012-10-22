package org.ourgrid.worker.response;

import org.ourgrid.worker.communication.sender.WorkerResponseConstants;


public class StartTransferResponseTO extends OutgoingTransferResponseTO {
	
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.START_TRANSFER;

	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
}
