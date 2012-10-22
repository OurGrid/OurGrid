package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class NotifyWorkerManagementFailureRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.NOTIFY_WORKER_MANAGEMENT_FAILURE;
	private String failedWorkerAddress;
	private String failedWorkerPublicKey;


	public String getRequestType() {
		return REQUEST_TYPE;
	}


	public String getFailedWorkerAddress() {
		return failedWorkerAddress;
	}

	public void setFailedWorkerAddress(String failedWorkerAddress) {
		this.failedWorkerAddress = failedWorkerAddress;
	}

	public String getFailedWorkerPublicKey() {
		return failedWorkerPublicKey;
	}

	public void setFailedWorkerPublicKey(String failedWorkerPublicKey) {
		this.failedWorkerPublicKey = failedWorkerPublicKey;
	}
}
