package org.ourgrid.worker.request;

import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class ExecutionResultRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.EXECUTION_RESULT;
	
	
	private ExecutorResult result;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setResult(ExecutorResult result) {
		this.result = result;
	}

	public ExecutorResult getResult() {
		return result;
	}
}
