package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class WorkForBrokerResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.WORK_FOR_BROKER;
	
	
	private String brokerAddress;
	private String workerManagementAddress;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	public String getWorkerManagementAddress() {
		return workerManagementAddress;
	}

	public void setWorkerManagementAddress(String workerManagementAddress) {
		this.workerManagementAddress = workerManagementAddress;
	}

	public void setBrokerAddress(String brokerAddress) {
		this.brokerAddress = brokerAddress;
	}

	public String getBrokerAddress() {
		return brokerAddress;
	}
}
