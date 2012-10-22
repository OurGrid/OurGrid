package org.ourgrid.worker.response;

import org.ourgrid.common.internal.response.AbstractStatusResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;



public class HereIsMasterPeerResponseTO extends AbstractStatusResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.HERE_IS_MASTER_PEER;

	private String masterPeerAddress;

	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public String getMasterPeerAddress() {
		return masterPeerAddress;
	}

	public void setMasterPeerAddress(String masterPeerAddress) {
		this.masterPeerAddress = masterPeerAddress;
	}

}
