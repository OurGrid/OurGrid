package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class ExecutorKillPreparingAllocationResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.EXECUTOR_KILL_PREPARING_ALLOCATION;
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

}
