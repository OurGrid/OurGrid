package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;




public class OutgoingTransferCancelledRequestTO extends AbstractOutgoingTransferRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.OUTGOING_TRANSFER_CANCELLED;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}
}
