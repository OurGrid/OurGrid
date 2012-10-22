package org.ourgrid.system.condition;

import org.ourgrid.system.units.PeerUnit;

public class PeerSetWorkersCondition implements Condition {

	private final int expectedNumWorkers;

	private final PeerUnit peerUnit;

	private int actualNumWorkers;


	public PeerSetWorkersCondition( PeerUnit peerUnit, int numberOfWorkers ) {

		this.peerUnit = peerUnit;
		this.expectedNumWorkers = numberOfWorkers;
		this.actualNumWorkers = -1;
	}


	public boolean isConditionMet() throws Exception {

		actualNumWorkers = this.peerUnit.getLocalWorkerStatus().size();
		return actualNumWorkers == this.expectedNumWorkers;
	}


	public String detailMessage() {

		return "Number of workers expected: " + expectedNumWorkers + ", actual: " + actualNumWorkers;
	}

}
