package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class ResumeWorkerResponseTO implements IResponseTO {
	
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.RESUME_WORKER;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
}
