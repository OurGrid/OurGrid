package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

import sun.security.provider.certpath.X509CertPath;

@SuppressWarnings("restriction")
public class WorkerLoginRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.WORKER_LOGIN;
	
	private X509CertPath workerCertPath;
	private String myPublicKey;
	private String workerAddress;
	private String workerPublicKey; 
	private WorkerSpecification workerSpecification;
	private String myUserAtServer;

	private boolean isVoluntary;
	
	@Override
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public X509CertPath getWorkerCertPath() {
		return workerCertPath;
	}

	public void setWorkerCertPath(X509CertPath workerCertPath) {
		this.workerCertPath = workerCertPath;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}
	
	public void setWorkerSpecification(WorkerSpecification workerSpecification) {
		this.workerSpecification = workerSpecification;
	}
	
	public WorkerSpecification getWorkerSpecification() {
		return workerSpecification;
	}

	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}
	
	public String getMyUserAtServer() {
		return myUserAtServer;
	}

	public String getMyPublicKey() {
		return myPublicKey;
	}

	public void setMyPublicKey(String myPublicKey) {
		this.myPublicKey = myPublicKey;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public void setVoluntary(boolean isVoluntary) {
		this.isVoluntary = isVoluntary;
	}
	
	public boolean isVoluntary() {
		return isVoluntary;
	}
}
