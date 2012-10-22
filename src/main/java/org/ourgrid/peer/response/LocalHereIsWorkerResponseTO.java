package org.ourgrid.peer.response;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class LocalHereIsWorkerResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.LOCAL_HERE_IS_WORKER;
	
	
	private String workerAddress;
	private String lwpcAddress;
	private String workerPublicKey;
	private RequestSpecification requestSpec;
	private WorkerSpecification workerSpec;
	

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setWorkerSpec(WorkerSpecification workerSpec) {
		this.workerSpec = workerSpec;
	}

	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setRequestSpec(RequestSpecification requestSpec) {
		this.requestSpec = requestSpec;
	}

	public RequestSpecification getRequestSpec() {
		return requestSpec;
	}

	public void setLwpcAddress(String lwpcAddress) {
		this.lwpcAddress = lwpcAddress;
	}

	public String getLwpcAddress() {
		return lwpcAddress;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}
	
}
