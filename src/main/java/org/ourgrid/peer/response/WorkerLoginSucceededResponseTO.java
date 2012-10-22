package org.ourgrid.peer.response;

import org.ourgrid.common.WorkerLoginResult;
import org.ourgrid.common.internal.IResponseTO;

public class WorkerLoginSucceededResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.WORKER_LOGIN_SUCCEEDED;
	
	
	private WorkerLoginResult loginResult;
	private String workerManagementAddress;
	

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setLoginResult(WorkerLoginResult workerLoginResult) {
		this.loginResult = workerLoginResult;
	}

	public WorkerLoginResult getLoginResult() {
		return loginResult;
	}

	public void setWorkerManagementAddress(String workerManagementAddress) {
		this.workerManagementAddress = workerManagementAddress;
	}

	public String getWorkerManagementAddress() {
		return workerManagementAddress;
	}
}
