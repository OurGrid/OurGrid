package org.ourgrid.worker.response;

import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class ExecutorKillCommandResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.EXECUTOR_KILL_COMMAND;
	
	private ExecutorHandle handle;
	
	public ExecutorHandle getHandle() {
		return handle;
	}

	public void setHandle(ExecutorHandle handle) {
		this.handle = handle;
	}

	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

}
