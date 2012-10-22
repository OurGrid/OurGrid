package org.ourgrid.peer.request;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IRequestTO;

public class UnwantedWorkerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.UNWANTED_WORKER;
	
	
	private String senderPublicKey;
	private RequestSpecification requestSpec;
	private String workerAddress;
	private String workerPublicKey;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setRequestSpec(RequestSpecification requestSpec) {
		this.requestSpec = requestSpec;
	}

	public RequestSpecification getRequestSpec() {
		return requestSpec;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}
}
