package org.ourgrid.peer.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class UpdateWorkerSpecRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.UPDATE_WORKER_SPEC;
	
	private WorkerSpecification workerSpec; 
	private String workerPublicKey;
	private String workerUserAtServer; 
	private String myUserAtServer;
	
	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}

	public void setWorkerSpec(WorkerSpecification workerSpec) {
		this.workerSpec = workerSpec;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerUserAtServer() {
		return workerUserAtServer;
	}

	public void setWorkerUserAtServer(String workerUserAtServer) {
		this.workerUserAtServer = workerUserAtServer;
	}

	public String getMyUserAtServer() {
		return myUserAtServer;
	}

	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

}
