package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;


public class WorkerManagementStopWorkingResponseTO implements IResponseTO {
	
	
	private final String RESPONSE_TYPE = PeerResponseConstants.WORKER_MANAGEMENT_STOP_WORKING;
	
	
	private String workerManagementAddress;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setWorkerManagementAddress(String workerManagementAddress) {
		this.workerManagementAddress = workerManagementAddress;
	}

	public String getWorkerManagementAddress() {
		return workerManagementAddress;
	}

}