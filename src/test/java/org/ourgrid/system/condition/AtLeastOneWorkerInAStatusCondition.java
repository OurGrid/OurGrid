package org.ourgrid.system.condition;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.system.units.WorkerUnit;

public class AtLeastOneWorkerInAStatusCondition implements Condition {

	private final WorkerStatus expectedStatus;
	
	private final WorkerUnit[] workerUnits;
	
	
	public AtLeastOneWorkerInAStatusCondition(WorkerStatus state, WorkerUnit... workerunits){
		this.expectedStatus = state;
		this.workerUnits = workerunits;
	}
	
	public String detailMessage() {
		return "No worker is in the " + expectedStatus + " state";
	}

	public boolean isConditionMet() throws Exception {
		
		for ( WorkerUnit unit : this.workerUnits ) {
			WorkerStatus workerUnitStatus = unit.getStatus().getStatus();
			if ( workerUnitStatus == this.expectedStatus ) {
				return true;
			}
		}
		return false;

	}

}
