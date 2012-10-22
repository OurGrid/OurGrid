package org.ourgrid.peer.request;


import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IRequestTO;

public class StatusChangedRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.STATUS_CHANGED;
	
	
	private String workerPublicKey;
	private String workerUserAtServer;
	private String localWorkerProviderAddress;
	private String myCertPathDN;
	private WorkerStatus status;


	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

	public void setWorkerUserAtServer(String workerUserAtServer) {
		this.workerUserAtServer = workerUserAtServer;
	}

	public String getWorkerUserAtServer() {
		return workerUserAtServer;
	}

	public void setStatus(WorkerStatus status) {
		this.status = status;
	}

	public WorkerStatus getStatus() {
		return status;
	}

	public void setLocalWorkerProviderAddress(String localWorkerProviderAddress) {
		this.localWorkerProviderAddress = localWorkerProviderAddress;
	}

	public String getLocalWorkerProviderAddress() {
		return localWorkerProviderAddress;
	}

	public void setMyCertPathDN(String myCertPathDN) {
		this.myCertPathDN = myCertPathDN;
	}

	public String getMyCertPathDN() {
		return myCertPathDN;
	}
}
