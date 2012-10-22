package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class WorkerClientIsUpRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.WORKER_CLIENT_IS_UP;
	
	
	private String clientAddress;

	private String clientDeploymentID;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}
	
	public String getClientDeploymentID() {
		return clientDeploymentID;
	}

	public void setClientDeploymentID(String clientDeploymentID) {
		this.clientDeploymentID = clientDeploymentID;
	}
}
