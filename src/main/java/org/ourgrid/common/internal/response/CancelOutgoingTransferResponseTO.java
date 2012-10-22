package org.ourgrid.common.internal.response;

import org.ourgrid.common.internal.OurGridResponseConstants;
import org.ourgrid.worker.response.OutgoingTransferResponseTO;


public class CancelOutgoingTransferResponseTO extends OutgoingTransferResponseTO {
	
	
	private final String RESPONSE_TYPE = OurGridResponseConstants.CANCEL_OUTGOING_TRANSFER;

	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
}
