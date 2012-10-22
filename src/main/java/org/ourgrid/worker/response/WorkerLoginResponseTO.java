package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class WorkerLoginResponseTO implements IResponseTO {

	private final String RESPONSE_TYPE = WorkerResponseConstants.LOGIN_AT_PEER;
	private String workerManagementClientAddress;
	private WorkerSpecification workerSpecification;
	
	@Override
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setWorkerManagementClientAddress(
			String workerManagementClientAddress) {
		this.workerManagementClientAddress = workerManagementClientAddress;
	}

	public String getWorkerManagementClientAddress() {
		return workerManagementClientAddress;
	}

	public WorkerSpecification getWorkerSpecification() {
		return workerSpecification;
	}

	public void setWorkerSpecification(WorkerSpecification workerSpecification) {
		this.workerSpecification = workerSpecification;
	}

}
