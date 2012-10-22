package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class StopWorkingResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.STOP_WORKING;
	
	
	private String wmAddress;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setWmAddress(String wmAddress) {
		this.wmAddress = wmAddress;
	}

	public String getWmAddress() {
		return wmAddress;
	}

}
