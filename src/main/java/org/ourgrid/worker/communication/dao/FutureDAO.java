package org.ourgrid.worker.communication.dao;

import java.util.concurrent.Future;

public class FutureDAO {

	private Future<?> beginAllocationFuture;
	private Future<?> executionActionFuture;
	private Future<?> reportAccountingActionFuture;
	
	public FutureDAO() {
		beginAllocationFuture = null;
		executionActionFuture = null;
		reportAccountingActionFuture = null;
	}
	
	public void setBeginAllocationFuture(Future<?> beginAllocationFuture) {
		this.beginAllocationFuture = beginAllocationFuture;
	}

	public Future<?> getBeginAllocationFuture() {
		return beginAllocationFuture;
	}

	public void setExecutionActionFuture(Future<?> executionActionFuture) {
		this.executionActionFuture = executionActionFuture;
	}

	public Future<?> getExecutionActionFuture() {
		return executionActionFuture;
	}

	public void setReportAccountingActionFuture(
			Future<?> reportAccountingActionFuture) {
		this.reportAccountingActionFuture = reportAccountingActionFuture;
	}

	public Future<?> getReportAccountingActionFuture() {
		return reportAccountingActionFuture;
	}
	
}
