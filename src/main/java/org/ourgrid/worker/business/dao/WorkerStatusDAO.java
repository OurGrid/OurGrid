/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.worker.business.dao;

import java.security.cert.X509Certificate;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.peer.business.controller.voms.VomsAuthorisationStrategy;
import org.ourgrid.reqtrace.Req;

/**
 * Manages worker's data.
 */
@Req("REQ094")
public class WorkerStatusDAO {
	
	/**
	 * The worker's status.
	 */
	private WorkerStatus status;
	
	private String loginError;
	
	private String masterPeerAddress;
	
	private String masterPeerPublicKey;
	
	/**
	 * The client's public key.
	 */
	private String consumerPubKey;
	
	
	private String remotePeerDeploymentID;
	
	/**
	 * The remote peer's public key.
	 */
	private String remotePeerPubKey;
	
	/**
	 * The worker's client.
	 */
	private String consumerAddress;
	
	private String consumerDeploymentID;
	
	private boolean fileTransferErrorState;
	
	private boolean preparingAllocationState;
	
	private boolean workingState;

	private List<String> usersDN;
	
	private List<X509Certificate> caCertificates;

	private String remotePeerDN;
	
	
    @Req("REQ094")
    /**
     * Builds a new Worker DAO. The worker's beginning status is OWNER.
     */
	WorkerStatusDAO() {
		status = WorkerStatus.OWNER;
		remotePeerPubKey = null;
		consumerPubKey = null;
		consumerAddress = null;
		consumerDeploymentID = null;
		masterPeerAddress = null;
		masterPeerPublicKey = null;
		loginError = "Peer Down";
		fileTransferErrorState = false;
		preparingAllocationState = false;
		workingState = false;
	}
    
	@Req("REQ094")
	/**
	 * Returns the worker's status.
	 * @return the worker's status.
	 */
	public WorkerStatus getStatus() {
		return status;
	}
	
	@Req({"REQ094", "REQ087"})
	/**
	 * Sets a new status for the worker.
	 * @param newStatus the worker's new status.
	 */
	public void setStatus(WorkerStatus newStatus) {
		status = newStatus;
	}

	@Req("REQ093")
	
	public void setRemotePeerConsumer(String remotePeerDID) {
		remotePeerDeploymentID = remotePeerDID;
	}
	
	public void masterPeerFails() {
		masterPeerPublicKey = null;
		loginError = "Peer Down";
	}
	
	@Req("REQ093")
	/**
	 * Returns the consumer's public key.
	 * @return the consumer's public key.
	 */
	public String getConsumerPublicKey() {
		return consumerPubKey;
	}
	
	@Req("REQ079")
	/**
	 * Sets the consumer's public key.
	 * @param consumerPublicKey the new consumer's public key
	 */
	public void setConsumerPublicKey(String consumerPublicKey) {
		consumerPubKey = consumerPublicKey;
	}
	
	@Req("REQ121")
	/**
	 * Returns the remote peer's public key.
	 * @return the remote peer's public key.
	 */
	public String getRemotePeerPublicKey() {
		return remotePeerPubKey;
	}
	
	@Req("REQ121")
	/**
	 * Sets the remote peer's public key.
	 * @param remotePeerPublicKey the new remote peer's public key.
	 */
	public void setRemotePeerPublicKey(String remotePeerPublicKey) {
		remotePeerPubKey = remotePeerPublicKey;
	}
	
	public void setRemotePeerDN(String remotePeerDN) {
		this.remotePeerDN = remotePeerDN;
	}
	
	@Req({"REQ079", "REQ082"})
	/**
	 * Returns the worker's client.
	 * @return the worker's client.
	 */
	public String getConsumerAddress() {
		return consumerAddress;
	}
	
	@Req({"REQ079", "REQ082"})
	/**
	 * Sets the worker's client.
	 * @param workerClient new worker's client
	 */
	public void setConsumerAddress(String workerClientAddress) {
		consumerAddress = workerClientAddress;
	}
	
	@Req({"REQ079", "REQ082"})
	/**
	 * Verifies if the worker is working.
	 * @return <code>true</code> if it is working, <code>false</code> otherwise.
	 */
	public boolean hasConsumer() {
		return (consumerDeploymentID != null);
	}
	
	@Req("REQ091")
	/**
	 * Verifies if the worker is allocated.
	 * @return <code>true</code> if it is allocated, <code>false</code> otherwise.
	 */
	public boolean isAllocated() {
		return (isAllocatedForRemotePeer() || isAllocatedForBroker());
	}
	
	public boolean isFileTransferErrorState() {
		return fileTransferErrorState;
	}

	public void setFileTransferErrorState(boolean fileTransferErrorState) {
		this.fileTransferErrorState = fileTransferErrorState;
	}
	
	public boolean isAllocatedForRemotePeer() {
		return getRemotePeerPublicKey() != null;
	}

	public boolean isAllocatedForBroker() {
		return getConsumerPublicKey() != null;
	}

	/**
	 * @param usersDN the usersDN to set
	 */
	public void setUsersDN(List<String> usersDN) {
		this.usersDN = usersDN;
	}

	/**
	 * @return the usersDN
	 */
	public List<String> getUsersDN() {
		return usersDN;
	}

	public boolean containsUsersDN(String userDN) {
		for (String eachUserDN : usersDN) {
			if (VomsAuthorisationStrategy.dnEquals(userDN, eachUserDN)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param caCertificates the caCertificates to set
	 */
	public void setCaCertificates(List<X509Certificate> caCertificates) {
		this.caCertificates = caCertificates;
	}

	/**
	 * @return the caCertificates
	 */
	public List<X509Certificate> getCaCertificates() {
		return caCertificates;
	}

	public boolean isAuthorisedForBroker() {
		return caCertificates != null && usersDN != null;
	}
	
	public String getRemotePeerDeploymentID() {
		return remotePeerDeploymentID;
	}
	
	public boolean isErrorState() {
		return status.equals(WorkerStatus.ERROR);
	}

	public boolean isWorkingState() {
		return workingState;
	}
	
	public void setWorkingState(boolean workingState) {
		this.workingState = workingState;
	}
	
	public boolean isPreparingAllocationState() {
		return preparingAllocationState;
	}

	public void setPreparingAllocationState(boolean preparingState) {
		this.preparingAllocationState = preparingState;
	}
	
	public boolean isLogged() {
		return masterPeerPublicKey != null;
	}

	public String getMasterPeerAddress() {
		return masterPeerAddress;
	}

	public void setMasterPeerAddress(String masterPeerAddress) {
		this.masterPeerAddress = masterPeerAddress;
	}

	public void setConsumerDeploymentID(String consumerDeploymentID) {
		this.consumerDeploymentID = consumerDeploymentID;
	}

	public String getConsumerDeploymentID() {
		return consumerDeploymentID;
	}

	public String getRemotePeerDN() {
		return remotePeerDN;
	}

	public String getMasterPeerPublicKey() {
		return masterPeerPublicKey;
	}

	public void setMasterPeerPublicKey(String masterPeerPublicKey) {
		this.masterPeerPublicKey = masterPeerPublicKey;
	}

	public String getLoginError() {
		return loginError;
	}

	public void setLoginError(String loginError) {
		this.loginError = loginError;
	}

}
