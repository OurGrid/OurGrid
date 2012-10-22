package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;




public class IncomingTransferFailedRequestTO extends AbstractIncomingTransferRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.INCOMING_TRANSFER_FAILED;
	
	
	private String failCauseMessage;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public void setFailCauseMessage(String failCauseMessage) {
		this.failCauseMessage = failCauseMessage;
	}

	public String getFailCauseMessage() {
		return failCauseMessage;
	}
}
