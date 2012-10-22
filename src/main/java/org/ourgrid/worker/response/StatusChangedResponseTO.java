package org.ourgrid.worker.response;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class StatusChangedResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.STATUS_CHANGED;

	
	private String clientAddress;
	private WorkerStatus status;

	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setStatus(WorkerStatus status) {
		this.status = status;
	}

	public WorkerStatus getStatus() {
		return status;
	}
}
