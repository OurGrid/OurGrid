package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;


public class ScheduleDelayedInterestOnDSResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = PeerResponseConstants.SCHEDULE_DELAYED_INTEREST_ON_DS_ACTION;
	private String dsAddress;
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public String getDsAddress() {
		return dsAddress;
	}
	
	public void setDsAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}

}