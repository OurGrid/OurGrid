package org.ourgrid.peer.request;

import java.util.List;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class SetWorkersRequestTO implements IRequestTO{

	
	private static final String REQUEST_TYPE = PeerRequestConstants.SET_WORKERS;
	
	
	private boolean canComponentBeUsed;
	private String senderPubKey;
	private List<WorkerSpecification> newWorkers;
	private boolean isThisMyPublicKey;
	private String clientAddress;
	private String myUserAtServer;
	
	
	public boolean isThisMyPublicKey() {
		return isThisMyPublicKey;
	}

	public void setThisMyPublicKey(boolean isThisMyPublicKey) {
		this.isThisMyPublicKey = isThisMyPublicKey;
	}

	public List<WorkerSpecification> getNewWorkers() {
		return newWorkers;
	}

	public void setNewWorkers(List<WorkerSpecification> newWorkers) {
		this.newWorkers = newWorkers;
	}

	public String getSenderPubKey() {
		return senderPubKey;
	}

	public void setSenderPubKey(String senderPubKey) {
		this.senderPubKey = senderPubKey;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public boolean canComponentBeUsed() {
		return canComponentBeUsed;
	}

	public void setCanComponentBeUsed(boolean canComponentBeUsed) {
		this.canComponentBeUsed = canComponentBeUsed;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}

	public String getMyUserAtServer() {
		return myUserAtServer;
	}

}
