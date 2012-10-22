package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class LocalPreemptedWorkerResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.LOCAL_PREEMPTED_WORKER;
	
	
	private String workerAddress;
	private String lwpcAddress;
	

	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setLwpcAddress(String lwpcAddress) {
		this.lwpcAddress = lwpcAddress;
	}

	public String getLwpcAddress() {
		return lwpcAddress;
	}
}
