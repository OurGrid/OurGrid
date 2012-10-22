package org.ourgrid.system.condition;

import java.util.Map;
import java.util.Set;

import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.system.units.BrokerUnit;

public class BrokerNumberOfWorkersCondition implements Condition {

	private final int jobid;

	private final int numWorkersExpected;

	private final BrokerUnit brokerUnit;

	private int actualNumWorkers;


	public BrokerNumberOfWorkersCondition( BrokerUnit brokerUnit, int numWorkers, int jobid ) {

		this.jobid = jobid;
		this.numWorkersExpected = numWorkers;
		this.brokerUnit = brokerUnit;
		this.actualNumWorkers = -1;
	}


	public BrokerNumberOfWorkersCondition( BrokerUnit brokerUnit, int numWorkers ) {

		this( brokerUnit, numWorkers, -1 );
	}


	public boolean isConditionMet() throws Exception {

		Map<Integer,Set<WorkerStatusInfo>> workers = brokerUnit.getWorkersByJob();

		this.actualNumWorkers = 0;

		// No job has been specified
		if ( jobid == -1 ) {

			for ( Set<WorkerStatusInfo> set : workers.values() ) {
				actualNumWorkers += set.size();
			}
			return actualNumWorkers == numWorkersExpected;
		}
		final Set<WorkerStatusInfo> jobWorkers = workers.get( jobid );

		if ( jobWorkers == null ) {
			throw new Exception( "Job " + jobid + " does not exist" );
		}

		actualNumWorkers = jobWorkers.size();
		return actualNumWorkers == numWorkersExpected;
	}


	public String detailMessage() {

		return "Number of workers expected: [" + numWorkersExpected + "]. Actual: [" + actualNumWorkers + "]";
	}
}
