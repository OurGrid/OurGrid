package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class PauseWorkerResponseTO implements IResponseTO {
	
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.PAUSE_WORKER;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
}
