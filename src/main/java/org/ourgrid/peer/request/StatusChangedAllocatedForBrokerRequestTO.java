package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class StatusChangedAllocatedForBrokerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.STATUS_CHANGED_ALLOCATED_FOR_BROKER;
	
	
	private String senderPublicKey;
	private String senderUserAtServer;
	private String workerAddress;
	private String brokerPublicKey;


	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderUserAtServer(String senderUserAtServer) {
		this.senderUserAtServer = senderUserAtServer;
	}

	public String getSenderUserAtServer() {
		return senderUserAtServer;
	}

	public String getBrokerPublicKey() {
		return brokerPublicKey;
	}

	public void setBrokerPublicKey(String brokerPublicKey) {
		this.brokerPublicKey = brokerPublicKey;
	}

}
