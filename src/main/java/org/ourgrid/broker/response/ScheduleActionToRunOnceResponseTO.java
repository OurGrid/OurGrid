package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;


public class ScheduleActionToRunOnceResponseTO implements IResponseTO {
	

	private final String RESPONSE_TYPE = BrokerResponseConstants.SCHEDULED_ACTION_TO_RUN_ONCE;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
}