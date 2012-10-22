package org.ourgrid.worker.response;

import java.util.List;

import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;

public class ReportWorkAccountingActionResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = WorkerResponseConstants.REPORT_WORK_ACCOUNTING;

	
	private String masterPeerAddress;
	private List<WorkAccounting> workAccountings;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setMasterPeerAddress(String masterPeerAddress) {
		this.masterPeerAddress = masterPeerAddress;
	}

	public String getMasterPeerAddress() {
		return masterPeerAddress;
	}

	public void setWorkAccountings(List<WorkAccounting> workAccountings) {
		this.workAccountings = workAccountings;
	}

	public List<WorkAccounting> getWorkAccountings() {
		return workAccountings;
	}
}
