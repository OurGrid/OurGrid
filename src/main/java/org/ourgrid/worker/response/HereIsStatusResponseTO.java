package org.ourgrid.worker.response;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.response.AbstractStatusResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;


public class HereIsStatusResponseTO extends AbstractStatusResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.HERE_IS_STATUS;

	
	private WorkerStatus status;

	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setStatus(WorkerStatus status) {
		this.status = status;
	}

	public WorkerStatus getStatus() {
		return status;
	}
}
