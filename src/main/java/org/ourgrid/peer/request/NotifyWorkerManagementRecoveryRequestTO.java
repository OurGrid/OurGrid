package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class NotifyWorkerManagementRecoveryRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.NOTIFY_WORKER_MANAGEMENT_RECOVERY;
	private String recoveredWorkerPublicKey;
	private String recoveredWorkerAddress;


	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setRecoveredWorkerPublicKey(String recoveredWorkerPublicKey) {
		this.recoveredWorkerPublicKey = recoveredWorkerPublicKey;
	}

	public String getRecoveredWorkerPublicKey() {
		return recoveredWorkerPublicKey;
	}

	public void setRecoveredWorkerAddress(String recoveredWorkerAddress) {
		this.recoveredWorkerAddress = recoveredWorkerAddress;
	}

	public String getRecoveredWorkerAddress() {
		return recoveredWorkerAddress;
	}
}
