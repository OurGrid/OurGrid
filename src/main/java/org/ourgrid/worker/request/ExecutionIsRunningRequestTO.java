package org.ourgrid.worker.request;

import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class ExecutionIsRunningRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.EXECUTION_IS_RUNNING;
	
	
	private ExecutorHandle handle;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setHandle(ExecutorHandle handle) {
		this.handle = handle;
	}

	public ExecutorHandle getHandle() {
		return handle;
	}
}
