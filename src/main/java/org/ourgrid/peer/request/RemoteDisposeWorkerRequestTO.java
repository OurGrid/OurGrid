package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class RemoteDisposeWorkerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.REMOTE_DISPOSE_WORKER;

	private String consumerPublicKey;
	private String workerAddress;
	private String workerUserAtServer;
	private String workerPublicKey;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setConsumerPublicKey(String brokerPublicKey) {
		this.consumerPublicKey = brokerPublicKey;
	}

	public String getConsumerPublicKey() {
		return consumerPublicKey;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setWorkerUserAtServer(String workerUserAtServer) {
		this.workerUserAtServer = workerUserAtServer;
	}

	public String getWorkerUserAtServer() {
		return workerUserAtServer;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

	
}
