package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class HereIsWorkerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.HERE_IS_WORKER;
	
	
	private String senderPublicKey;
	private String providerAddress;
	private String providerContainerID;
	private String workerAddress;
	private String workerClientAddress;
	private String workerContainerID;
	private String certSubjectDN;
	private String myUserAtServer; //serviceManager.getMyDeploymentID().getContainerID().getUserAtServer()
	private WorkerSpecification workerSpec;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}

	public String getProviderAddress() {
		return providerAddress;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setProviderContainerID(String providerContainerID) {
		this.providerContainerID = providerContainerID;
	}

	public String getProviderContainerID() {
		return providerContainerID;
	}

	public void setWorkerSpec(WorkerSpecification workerSpec) {
		this.workerSpec = workerSpec;
	}

	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}

	public void setWorkerContainerID(String workerContainerID) {
		this.workerContainerID = workerContainerID;
	}

	public String getWorkerContainerID() {
		return workerContainerID;
	}

	public void setProviderCertSubjectDN(String certSubjectDN) {
		this.certSubjectDN = certSubjectDN;
	}

	public String getProviderCertSubjectDN() {
		return certSubjectDN;
	}

	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}

	public String getMyUserAtServer() {
		return myUserAtServer;
	}

	public void setWorkerClientAddress(String workerClientAddress) {
		this.workerClientAddress = workerClientAddress;
	}

	public String getWorkerClientAddress() {
		return workerClientAddress;
	}
}
