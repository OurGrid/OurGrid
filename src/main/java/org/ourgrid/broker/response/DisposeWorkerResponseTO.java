package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;

public class DisposeWorkerResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = BrokerResponseConstants.DISPOSE_WORKER;
	
	
	private String peerAddress;
	private String workerAddress;
	private String workerPublicKey;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}
	
	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}
}
