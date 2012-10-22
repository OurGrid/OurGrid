package org.ourgrid.system.condition;

import java.util.Collection;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.WorkerUnit;

public class PeerHasTheWorkerInStateCondition implements Condition {

	private PeerUnit peerUnit;

	private String workerLocation;

	private LocalWorkerState expectedState;

	private LocalWorkerState actualState;


	public PeerHasTheWorkerInStateCondition( PeerUnit peerUnit, WorkerUnit workerUnit, LocalWorkerState status ) {

		this.peerUnit = peerUnit;
		this.expectedState = status;
		this.workerLocation = workerUnit.getLocation();
	}


	public boolean isConditionMet() throws Exception {

		Collection<WorkerInfo> localWorkerStatus = this.peerUnit.getLocalWorkerStatus();
		for ( WorkerInfo workerInfo : localWorkerStatus ) {
			if ( workerInfo.getWorkerSpec().getLocation().equals( this.workerLocation ) ) {
				actualState = workerInfo.getStatus();
				if ( actualState.equals( this.expectedState ) ) {
					return true;
				}
			}
		}
		return false;
	}


	public String detailMessage() {

		return "Peer has the Worker [" + this.workerLocation + "] in state. Expected:  " + expectedState + ". Actual: "
				+ actualState;
	}

}
