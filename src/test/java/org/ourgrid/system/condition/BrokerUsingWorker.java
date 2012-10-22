package org.ourgrid.system.condition;

import java.util.Set;

import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.WorkerUnit;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class BrokerUsingWorker implements Condition {
	
	private WorkerUnit workerUnit;

	private BrokerUnit borkerUnit;

	private final int jobID;

	public BrokerUsingWorker( int jobID, BrokerUnit brokerUnit, WorkerUnit workerUnit ) {

		this.jobID = jobID;
		this.borkerUnit = brokerUnit;
		this.workerUnit = workerUnit;
	}
	
	
	public String detailMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConditionMet() throws Exception {
		Set<WorkerStatusInfo> workerEntries = this.borkerUnit.getWorkersByJob().get( this.jobID );
		if ( workerEntries != null ) {
			DeploymentID id = null;
			for ( WorkerStatusInfo entry : workerEntries ) {
				id = new DeploymentID(entry.getWorkerID());
				if ( this.workerUnit.isTheSameEntity(id.getServiceID())) {
					return true;
				}
			}
		}	
		return false;
	}

}
