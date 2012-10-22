package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;




public class WorkerDoNotifyRecoveryRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.WORKER_DO_NOTIFY_RECOVERY;
	
	
	private String workerPublicKey;
	private String workerDeploymentID;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
		
	}
	
	public String getWorkerDeploymentID() {
		return workerDeploymentID;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public void setWorkerDeploymentID(String workerDeploymentID) {
		this.workerDeploymentID = workerDeploymentID;
	}
}
