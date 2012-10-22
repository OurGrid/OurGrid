package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;




public class PreemptedWorkerRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.PREEMPTED_WORKER;
	
	
	private String workerContainerID;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public String getWorkerContainerID() {
		return workerContainerID;
	}

	public void setWorkerContainerID(String workerContainerID) {
		this.workerContainerID = workerContainerID;
	}
}
