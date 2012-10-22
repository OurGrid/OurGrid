package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class HereIsWorkerSpecProcessorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = BrokerRequestConstants.HERE_IS_WORKER_SPEC;
	
	
	private String workerAddress;
	private WorkerSpecification workerSpec;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setWorkerSpec(WorkerSpecification workerSpec) {
		this.workerSpec = workerSpec;
	}

	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}
}