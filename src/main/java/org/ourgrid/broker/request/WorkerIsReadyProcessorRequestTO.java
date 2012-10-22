package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;

public class WorkerIsReadyProcessorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = BrokerRequestConstants.WORKER_IS_READY_PROCESSOR;
	
	
	private String workerAddress;
	private String workerContainerID;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setWorkerContainerID(String workerContainerID) {
		this.workerContainerID = workerContainerID;
	}

	public String getWorkerContainerID() {
		return workerContainerID;
	}
}