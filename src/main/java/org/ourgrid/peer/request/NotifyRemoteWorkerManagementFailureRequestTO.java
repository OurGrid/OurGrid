package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class NotifyRemoteWorkerManagementFailureRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.NOTIFY_RWM_FAILURE;
	private String remoteWorkerAddress;
	private String remoteWorkerPublicKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public String getRemoteWorkerAddress() {
		return remoteWorkerAddress;
	}

	public void setRemoteWorkerAddress(String remoteWorkerAddress) {
		this.remoteWorkerAddress = remoteWorkerAddress;
	}

	public String getRemoteWorkerPublicKey() {
		return remoteWorkerPublicKey;
	}

	public void setRemoteWorkerPublicKey(String remoteWorkerPublicKey) {
		this.remoteWorkerPublicKey = remoteWorkerPublicKey;
	}
}
