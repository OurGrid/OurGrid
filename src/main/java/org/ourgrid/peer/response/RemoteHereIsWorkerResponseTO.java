package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class RemoteHereIsWorkerResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.REMOTE_HERE_IS_WORKER;
	
	
	private String rwmAddress;
	private String rwpcAddress;
	private WorkerSpecification workerSpec;
	

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setWorkerSpec(WorkerSpecification workerSpec) {
		this.workerSpec = workerSpec;
	}

	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}

	public void setRwmAddress(String rwmAddress) {
		this.rwmAddress = rwmAddress;
	}

	public String getRwmAddress() {
		return rwmAddress;
	}

	public void setRwpcAddress(String rwpcAddress) {
		this.rwpcAddress = rwpcAddress;
	}

	public String getRwpcAddress() {
		return rwpcAddress;
	}
	
}
