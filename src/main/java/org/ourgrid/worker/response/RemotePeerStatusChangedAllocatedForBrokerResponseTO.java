package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class RemotePeerStatusChangedAllocatedForBrokerResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.REMOTE_PEER_STATUS_CHANGED_ALLOCATED_FOR_BROKER;

	
	private String remotePeerAddress;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setRemotePeerAddress(String remotePeerAddress) {
		this.remotePeerAddress = remotePeerAddress;
	}

	public String getRemotePeerAddress() {
		return remotePeerAddress;
	}
}
