package org.ourgrid.common.internal.response;

import org.ourgrid.common.internal.OurGridResponseConstants;
import org.ourgrid.worker.response.AbstractIncomingTransferResponseTO;


public class CancelIncomingTransferResponseTO extends AbstractIncomingTransferResponseTO {
	
	private final String RESPONSE_TYPE = OurGridResponseConstants.CANCEL_INCOMING_TRANSFER;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
}
