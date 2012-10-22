package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;


public class ScheduleDSActionResponseTO implements IResponseTO {
	

	private final String RESPONSE_TYPE = PeerResponseConstants.SCHEDULE_DS_ACTION;
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

}