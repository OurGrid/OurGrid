package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;




public class HereIsWorkerRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.HERE_IS_WORKER;
	
	
	private String senderPublicKey;
	private String workerAddress;
	private String workerPublicKey;
	private String peerID;
	private String workerID;
	private WorkerSpecification workerSpec;
	private RequestSpecification requestSpec;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setWorkerSpec(WorkerSpecification workerSpec) {
		this.workerSpec = workerSpec;
	}

	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}

	public void setRequestSpec(RequestSpecification requestSpec) {
		this.requestSpec = requestSpec;
	}

	public RequestSpecification getRequestSpec() {
		return requestSpec;
	}

	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}

	public String getWorkerID() {
		return workerID;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerID = peerAddress;
	}

	public String getPeerAddress() {
		return peerID;
	}
}
