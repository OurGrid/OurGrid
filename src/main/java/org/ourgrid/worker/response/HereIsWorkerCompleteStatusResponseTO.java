package org.ourgrid.worker.response;

import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.internal.response.AbstractStatusResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;



public class HereIsWorkerCompleteStatusResponseTO extends AbstractStatusResponseTO {
	
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.HERE_IS_COMPLETE_STATUS;

	
	private WorkerCompleteStatus completeStatus;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	
	public void setCompleteStatus(WorkerCompleteStatus completeStatus) {
		this.completeStatus = completeStatus;
	}

	public WorkerCompleteStatus getCompleteStatus() {
		return completeStatus;
	}
}
