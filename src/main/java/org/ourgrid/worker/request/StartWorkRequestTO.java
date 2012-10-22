package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

import sun.security.provider.certpath.X509CertPath;


public class StartWorkRequestTO implements IRequestTO {
	
	private String REQUEST_TYPE = WorkerRequestConstants.START_WORK;
	
	private String brokerPublicKey;
	private String senderPublicKey;
	private String clientDeploymentID;
	
	private X509CertPath senderCerthPath;
	private String playpenRoot;
	private String storageRoot;

	public String getBrokerPublicKey() {
		return brokerPublicKey;
	}

	public void setBrokerPublicKey(String brokerPublicKey) {
		this.brokerPublicKey = brokerPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public void setSenderCerthPath(X509CertPath senderCerthPath) {
		this.senderCerthPath = senderCerthPath;
	}

	public X509CertPath getSenderCerthPath() {
		return senderCerthPath;
	}

	public void setPlaypenRoot(String playpenRoot) {
		this.playpenRoot = playpenRoot;
	}

	public String getPlaypenRoot() {
		return playpenRoot;
	}

	public void setStorageRoot(String storageRoot) {
		this.storageRoot = storageRoot;
	}

	public String getStorageRoot() {
		return storageRoot;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public String getClientDeploymentID() {
		return clientDeploymentID;
	}

	public void setClientDeploymentID(String clientDeploymentID) {
		this.clientDeploymentID = clientDeploymentID;
	}
	
}
