package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class RemoteStatusChangedAllocatedForBrokerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.REMOTE_STATUS_CHANGED_ALLOCATED_FOR_BROKER;
	
	
	private String wmPublicKey;
	private String workerAddress;


	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setWmPublicKey(String wmPublicKey) {
		this.wmPublicKey = wmPublicKey;
	}

	public String getWmPublicKey() {
		return wmPublicKey;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

}
