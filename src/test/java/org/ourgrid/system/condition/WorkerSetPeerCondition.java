package org.ourgrid.system.condition;

import org.ourgrid.system.units.WorkerUnit;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class WorkerSetPeerCondition implements Condition {

	private final WorkerUnit workerUnit;


	public WorkerSetPeerCondition( WorkerUnit workerUnit ) {

		this.workerUnit = workerUnit;
	}


	public boolean isConditionMet() throws Exception {
		DeploymentID deplymentID = workerUnit.getMasterPeer();
		return deplymentID.getContainerName() != null && deplymentID.getContainerLocation() != null;
	}


	public String detailMessage() {

		return "No peer is set for this worker";
	}

}
