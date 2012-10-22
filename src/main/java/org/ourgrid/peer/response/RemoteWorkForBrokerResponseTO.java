package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class RemoteWorkForBrokerResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.REMOTE_WORK_FOR_BROKER;
	
	private String workerManagementClientAddress;
	private String brokerPublicKey;
	private String workerManagementAddress;
	
	public String getWorkerManagementClientAddress() {
		return workerManagementClientAddress;
	}

	public void setWorkerManagementClientAddress(
			String workerManagementClientAddress) {
		this.workerManagementClientAddress = workerManagementClientAddress;
	}

	public String getBrokerPublicKey() {
		return brokerPublicKey;
	}

	public void setBrokerPublicKey(String brokerPublicKey) {
		this.brokerPublicKey = brokerPublicKey;
	}

	public String getWorkerManagementAddress() {
		return workerManagementAddress;
	}

	public void setWorkerManagementAddress(String workerManagementAddress) {
		this.workerManagementAddress = workerManagementAddress;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

}
