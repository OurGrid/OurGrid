package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class DisposeRemoteWorkerResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.DISPOSE_REMOTE_WORKER;
	
	private String providerAddress;
	private String workerAddress;
	private String workerPublicKey;
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}

	public String getProviderAddress() {
		return providerAddress;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}


	public String getWorkerPublicKey() {
		return workerPublicKey;
	}
	
	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}
}
