package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class MasterPeerStatusChangedAllocatedForBrokerResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.MASTER_PEER_STATUS_CHANGED_ALLOCATED_FOR_BROKER;

	
	private String masterPeerAddress;
	private String brokerPublicKey;

	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setMasterPeerAddress(String masterPeerAddress) {
		this.masterPeerAddress = masterPeerAddress;
	}

	public String getMasterPeerAddress() {
		return masterPeerAddress;
	}

	public String getBrokerPublicKey() {
		return brokerPublicKey;
	}

	public void setBrokerPublicKey(String brokerPublicKey) {
		this.brokerPublicKey = brokerPublicKey;
	}
}
