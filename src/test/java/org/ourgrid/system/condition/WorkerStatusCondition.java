package org.ourgrid.system.condition;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.system.units.WorkerUnit;


public class WorkerStatusCondition implements Condition {
	
	private final WorkerUnit workerUnit;

	private final WorkerStatus expectedStatus;

	private WorkerStatus actualStatus;


	public WorkerStatusCondition( WorkerUnit workerUnit, WorkerStatus status ) {

		this.workerUnit = workerUnit;
		this.expectedStatus = status;
		this.actualStatus = null;
	}


	public boolean isConditionMet() throws Exception {

		actualStatus = workerUnit.getStatus().getStatus();
		return actualStatus == expectedStatus;
	}


	public String detailMessage() {

		return "Expected status: " + expectedStatus + ", actual: " + actualStatus;
	}
}
