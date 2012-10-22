package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.internal.IRequestTO;

public class HereIsGridProcessResultProcessorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = BrokerRequestConstants.HERE_IS_GRID_PROCESS_RESULT;
	
	
	private String workerAddress;
	private String workerContainerID;
	private ExecutorResult result;
	

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

	public void setResult(ExecutorResult result) {
		this.result = result;
	}

	public ExecutorResult getResult() {
		return result;
	}
}