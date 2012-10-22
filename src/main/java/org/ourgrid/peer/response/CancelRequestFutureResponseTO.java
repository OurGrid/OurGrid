package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class CancelRequestFutureResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.CANCEL_REQUEST_FUTURE;

	
	private Long requestId;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}


	public Long getRequestId() {
		return requestId;
	}

}
