package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;
import org.ourgrid.worker.communication.processors.handle.RemoteExecuteMessageHandle;

public class RemoteExecuteProcessorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.REMOTE_EXECUTE_PROCESSOR;
	
	private RemoteExecuteMessageHandle handle;
	private String senderPublicKey;
	private boolean isExecutionClientDeployed;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setHandle(RemoteExecuteMessageHandle handle) {
		this.handle = handle;
	}

	public RemoteExecuteMessageHandle getHandle() {
		return handle;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public boolean isExecutionClientDeployed() {
		return isExecutionClientDeployed;
	}

	public void setExecutionClientDeployed(boolean isExecutionClientDeployed) {
		this.isExecutionClientDeployed = isExecutionClientDeployed;
	}
	
}
