package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class StatusChangedAllocatedForPeerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.STATUS_CHANGED_ALLOCATED_FOR_PEER;
	
	
	private String workerPublicKey;
	private String workerUserAtServer;
	private String remoteWorkerManagementAddress;
	private String peerPublicKey;


	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

	public void setWorkerUserAtServer(String workerUserAtServer) {
		this.workerUserAtServer = workerUserAtServer;
	}

	public String getWorkerUserAtServer() {
		return workerUserAtServer;
	}

	public void setRemoteWorkerManagementAddress(
			String remoteWorkerManagementAddress) {
		this.remoteWorkerManagementAddress = remoteWorkerManagementAddress;
	}

	public String getRemoteWorkerManagementAddress() {
		return remoteWorkerManagementAddress;
	}

	public String getPeerPublicKey() {
		return peerPublicKey;
	}

	public void setPeerPublicKey(String peerPublicKey) {
		this.peerPublicKey = peerPublicKey;
	}

}
