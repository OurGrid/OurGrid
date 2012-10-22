package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

/**
 * Sender of the Executor shutdown method. It has the RESPONSE_TYPE field
 * which identifies itself.
 */
public class ExecutorShutdownResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.EXECUTOR_SHUTDOWN_COMMAND;
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

}