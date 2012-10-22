package org.ourgrid.worker.request;

import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class ExecutionErrorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.EXECUTION_ERROR;
	
	
	private ExecutorException error;
	
	
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
