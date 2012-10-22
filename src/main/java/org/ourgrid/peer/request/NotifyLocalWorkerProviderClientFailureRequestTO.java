package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class NotifyLocalWorkerProviderClientFailureRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.NOTIFY_LWPC_FAILURE;
	private String myCertPathDN;
	private String brokerContainerID;
	private String brokerUserAtServer;
	private String brokerPublicKey;
	private String brokerAddress;
	
	public String getMyCertPathDN() {
		return myCertPathDN;
	}

	public void setMyCertPathDN(String myCertPathDN) {
		this.myCertPathDN = myCertPathDN;
	}

	public String getBrokerContainerID() {
		return brokerContainerID;
	}

	public void setBrokerContainerID(String brokerContainerID) {
		this.brokerContainerID = brokerContainerID;
	}

	public String getBrokerUserAtServer() {
		return brokerUserAtServer;
	}

	public void setBrokerUserAtServer(String brokerUserAtServer) {
		this.brokerUserAtServer = brokerUserAtServer;
	}

	public String getBrokerPublicKey() {
		return brokerPublicKey;
	}

	public void setBrokerPublicKey(String brokerPublicKey) {
		this.brokerPublicKey = brokerPublicKey;
	}

	public String getBrokerAddress() {
		return brokerAddress;
	}

	public void setBrokerAddress(String brokerAddress) {
		this.brokerAddress = brokerAddress;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	

}
