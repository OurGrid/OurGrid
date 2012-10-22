package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class PreemptedWorkerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.PREEMPTED_WORKER;
	private String remoteWorkerPublicKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public String getRemoteWorkerPublicKey() {
		return remoteWorkerPublicKey;
	}

	public void setRemoteWorkerPublicKey(String remoteWorkerPublicKey) {
		this.remoteWorkerPublicKey = remoteWorkerPublicKey;
	}
}
