package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;




public class SchedulerActionRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.SCHEDULER_ACTION;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}
}
