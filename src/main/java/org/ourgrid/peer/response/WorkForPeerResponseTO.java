package org.ourgrid.peer.response;

import java.util.List;

import org.ourgrid.common.internal.IResponseTO;

public class WorkForPeerResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.WORK_FOR_PEER;
	
	
	private List<String> usersDN;
	private String peerPublicKey;
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

	public List<String> getUsersDN() {
		return usersDN;
	}

	public void setUsersDN(List<String> usersDN) {
		this.usersDN = usersDN;
	}

	public String getPeerPublicKey() {
		return peerPublicKey;
	}

	public void setPeerPublicKey(String peerPublicKey) {
		this.peerPublicKey = peerPublicKey;
	}
}
