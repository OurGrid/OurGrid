package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class RemoveWorkerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.REMOVE_WORKER;
	
	
	private String senderPubKey;
	private boolean canComponentBeUsed;
	private boolean isThisMyPublicKey;
	private String clientAddress;
	private String workerUserAtServer;
	
	/**
	 * @return the canComponentBeUsed
	 */
	public boolean canComponentBeUsed() {
		return canComponentBeUsed;
	}


	/**
	 * @param canComponentBeUsed the canComponentBeUsed to set
	 */
	public void setCanComponentBeUsed(boolean canComponentBeUsed) {
		this.canComponentBeUsed = canComponentBeUsed;
	}


	/**
	 * @return the senderPubKey
	 */
	public String getSenderPubKey() {
		return senderPubKey;
	}


	/**
	 * @param senderPubKey the senderPubKey to set
	 */
	public void setSenderPubKey(String senderPubKey) {
		this.senderPubKey = senderPubKey;
	}


	public String getRequestType() {
		return REQUEST_TYPE;
	}


	/**
	 * @return the isThisMyPublicKey
	 */
	public boolean isThisMyPublicKey() {
		return isThisMyPublicKey;
	}


	/**
	 * @param isThisMyPublicKey the isThisMyPublicKey to set
	 */
	public void setThisMyPublicKey(boolean isThisMyPublicKey) {
		this.isThisMyPublicKey = isThisMyPublicKey;
	}


	/**
	 * @return the clientAddress
	 */
	public String getClientAddress() {
		return clientAddress;
	}


	/**
	 * @param clientAddress the clientAddress to set
	 */
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}


	public void setWorkerUserAtServer(String workerUserAtServer) {
		this.workerUserAtServer = workerUserAtServer;
	}


	public String getWorkerUserAtServer() {
		return workerUserAtServer;
	}




}
