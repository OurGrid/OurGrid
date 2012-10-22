package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class UpdateWorkerSpecListenerResponseTO implements IResponseTO {

	
	private static final String RESPONSE_TYPE = WorkerResponseConstants.UPDATE_WORKER_SPEC_LISTENER;
	
	
	private WorkerSpecification workerSpec;
	private String masterPeerAddress;
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	public void setWorkerSpec(WorkerSpecification workerSpec) {
		this.workerSpec = workerSpec;
	}

	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}


	public void setMasterPeerAddress(String masterPeerAddress) {
		this.masterPeerAddress = masterPeerAddress;
	}
	
	public String getMasterPeerAddress() {
		return masterPeerAddress;
	}
}
