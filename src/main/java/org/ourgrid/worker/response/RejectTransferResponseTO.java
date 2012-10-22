package org.ourgrid.worker.response;

import org.ourgrid.worker.communication.sender.WorkerResponseConstants;


public class RejectTransferResponseTO extends AbstractIncomingTransferResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.REJECT_TRANSFER;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
}
