package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class AddWorkerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.ADD_WORKER;
	private String senderPubKey;
	private boolean canComponentBeUsed;
	private boolean isThisMyPublicKey;
	private WorkerSpecification workerSpec;
	private String ClientAddress;
	public String getMyUserAtServer() {
		return myUserAtServer;
	}


	private String myUserAtServer;
	
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
	public void setIsThisMyPublicKey(boolean isThisMyPublicKey) {
		this.isThisMyPublicKey = isThisMyPublicKey;
	}


	/**
	 * @return the workerSpec
	 */
	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}


	/**
	 * @param workerSpec the workerSpec to set
	 */
	public void setWorkerSpec(WorkerSpecification workerSpec) {
		this.workerSpec = workerSpec;
	}


	/**
	 * @return the clientAddress
	 */
	public String getClientAddress() {
		return ClientAddress;
	}


	/**
	 * @param clientAddress the clientAddress to set
	 */
	public void setClientAddress(String clientAddress) {
		ClientAddress = clientAddress;
	}


	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
		
	}




}
