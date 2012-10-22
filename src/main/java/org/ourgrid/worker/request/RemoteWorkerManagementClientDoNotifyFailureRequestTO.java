package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class RemoteWorkerManagementClientDoNotifyFailureRequestTO implements IRequestTO {

	private String monitorableAddress;
	private String publicKey;
	private String monitorableID;

	@Override
	public String getRequestType() {
		return WorkerRequestConstants.REMOTE_WORKER_MANAGEMENT_CLIENT_DO_NOTIFY_FAILURE;
	}

	public String getMonitorableAddress() {
		return monitorableAddress;
	}
	
	public void setMonitorableAddress(String monitorableAddress) {
		this.monitorableAddress = monitorableAddress;
	}

	public String getMonitorablePublicKey() {
		return publicKey;
	}
	
	public void setMonitorablePublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getMonitorableID() {
		return monitorableID;
	}
	
	public void setMonitorableID(String monitorableID) {
		this.monitorableID = monitorableID;
	}
}
