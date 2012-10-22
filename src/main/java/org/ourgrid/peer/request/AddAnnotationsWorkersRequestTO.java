package org.ourgrid.peer.request;


import java.util.List;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class AddAnnotationsWorkersRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.ADD_ANNOTATIONS_WORKERS;
	private String senderPubKey;
	private boolean canComponentBeUsed;
	private List< WorkerSpecification > newWorkersAnnotations;
	private String ClientAddress;
	

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
	 * @return the newWorkersAnnotations
	 */
	public List<WorkerSpecification> getNewWorkersAnnotations() {
		return newWorkersAnnotations;
	}


	/**
	 * @param newWorkersAnnotations the newWorkersAnnotations to set
	 */
	public void setNewWorkersAnnotations(List<WorkerSpecification> newWorkersAnnotations) {
		this.newWorkersAnnotations = newWorkersAnnotations;
	}
	
}
