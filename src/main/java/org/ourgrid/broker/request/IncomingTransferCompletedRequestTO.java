package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;




public class IncomingTransferCompletedRequestTO extends AbstractIncomingTransferRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.INCOMING_TRANSFER_COMPLETED;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}
}
