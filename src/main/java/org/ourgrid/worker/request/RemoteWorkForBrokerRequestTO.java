package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class RemoteWorkForBrokerRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.REMOTE_WORK_FOR_BROKER;
	
	
	private String remotePeerDID;
	private String remotePeerPublicKey;
	private String senderPublicKey;
	private String consumerPublicKey;
	private boolean isWorkerDeployed;

	private String certSubjectDN;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	
	public void setRemotePeerDID(String remotePeerDID) {
		this.remotePeerDID = remotePeerDID;
	}

	public String getRemotePeerDID() {
		return remotePeerDID;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setRemotePeerPublicKey(String remotePeerPublicKey) {
		this.remotePeerPublicKey = remotePeerPublicKey;
	}

	public String getRemotePeerPublicKey() {
		return remotePeerPublicKey;
	}

	public void setConsumerPublicKey(String consumerPublicKey) {
		this.consumerPublicKey = consumerPublicKey;
	}

	public String getConsumerPublicKey() {
		return consumerPublicKey;
	}

	public void setWorkerDeployed(boolean isWorkerDeployed) {
		this.isWorkerDeployed = isWorkerDeployed;
	}

	public boolean isWorkerDeployed() {
		return isWorkerDeployed;
	}


	public void setRemotePeerDN(String certSubjectDN) {
		this.certSubjectDN = certSubjectDN;
	}

	public String getRemotePeerDN() {
		return certSubjectDN;
	}

}
