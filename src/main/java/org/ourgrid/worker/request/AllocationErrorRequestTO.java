package org.ourgrid.worker.request;

import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class AllocationErrorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.ALLOCATION_ERROR;
	
	private ExecutorException error;
	
	private boolean isWorkerDeployed;
	
	public boolean isWorkerDeployed() {
		return isWorkerDeployed;
	}

	public void setWorkerDeployed(boolean isWorkerDeployed) {
		this.isWorkerDeployed = isWorkerDeployed;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setError(ExecutorException error) {
		this.error = error;
	}

	public ExecutorException getError() {
		return error;
	}
}
