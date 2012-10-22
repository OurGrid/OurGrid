package org.ourgrid.worker.response;

import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class StatusChangedAllocatedForPeerResponseTO extends StatusChangedResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.STATUS_CHANGED_ALLOCATED_FOR_PEER;
	
	private String remotePeerPubKey;
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public String getRemotePeerPubKey() {
		return remotePeerPubKey;
	}

	public void setRemotePeerPubKey(String remotePeerPubKey) {
		this.remotePeerPubKey = remotePeerPubKey;
	}

}
