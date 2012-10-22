package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class ScheduleRequestResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.SCHEDULE_REQUEST;
	private long requestId;
	private int delay;
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public long getRequestId() {
		return requestId;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
		
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
		
	}
}
