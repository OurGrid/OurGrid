/* Created at 15/12/2006 */

package org.ourgrid.system;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.system.condition.BrokerJobFinishedCondition;
import org.ourgrid.system.condition.BrokerJobRunningCondition;
import org.ourgrid.system.condition.BrokerSetPeersCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.condition.WorkerStatusCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

public class RequirementsSystemTest extends AbstractSystemTest {

	/**
	 * 1 broker 1 peer 2 workers (one os:windows and other os:linux ) 1 job with 2
	 * tasks (os==windows) wait until job is running wait until job finished
	 * verify if the windows worker has run all tasks The job sleep must be a
	 * sleep with enough time
	 */
	@Test
	public void testRequirements1() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 2);
		unitManager.addUnit(brokerUnit);

		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.addProperty( "os", "windows" );
		WorkerUnit otherWorkerUnit = unitManager.buildNewUnit(WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		otherWorkerUnit.addProperty( "os", "linux" );

//		workerUnit.start();
//		otherWorkerUnit.start();
//
//		peerUnit.start();
//		brokerUnit.start();

		System.out.println( "===> Remote Entities Started (1 Workers, 1 Peer, 1 Broker) <===" );

//		peerUnit.setWorkers( workerUnit, otherWorkerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, otherWorkerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Peers has 2 workers in IDLE status <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		
		// we are assuming that in 30 seconds the Broker has had scheduled all
		// job
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		System.out.println( "===> Broker knows the peer <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJobWithRequirements( 2, 15, "os = windows" ) );
		System.out.println( "===> Job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ), 1000, 30 );
		System.out.println( "===> Job running <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ), 1000, 50 );
		System.out.println( "===> Job finished <===" );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		for ( TaskStatusInfo task : job.getTasks() ) {
			assertEquals( GridProcessState.FINISHED, task.getState() );
			assertEquals( workerUnit.getLocation(), task.getGridProcesses().iterator().next().getWorkerInfo().getWorkerSpec()
				.getLocation());
		}
		System.out.println( "===> Asserts done <===" );
	}


	/**
	 * 1 broker 1 peer 2 workers (one os:windows and other os:linux ) 1 job with 2
	 * tasks (os!=windows) wait until job is running wait until job finished
	 * verify if the windows worker has run all tasks The job sleep must be a
	 * sleep with enough time
	 */
	@Test
	public void testRequirements2() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 2);
		unitManager.addUnit(brokerUnit);

		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.addProperty( "os", "windows" );
		WorkerUnit otherWorkerUnit = unitManager.buildNewUnit(WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		otherWorkerUnit.addProperty( "os", "linux" );

		workerUnit.initKeys();
		otherWorkerUnit.initKeys();

		peerUnit.initKeys();
		brokerUnit.initKeys();

		System.out.println( "===> Remote Entities Started (1 Workers, 1 Peer, 1 Broker) <===" );

//		peerUnit.setWorkers( workerUnit, otherWorkerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, otherWorkerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Peers has 2 workers in IDLE status <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		
		// we are assuming that in 30 seconds the Broker has had scheduled all
		// job
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		System.out.println( "===> Broker knows the peer <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJobWithRequirements( 2, 15, "os != windows" ) );
		System.out.println( "===> Job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ), 1000, 50 );
		System.out.println( "===> Job running <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ), 1000, 50 );
		System.out.println( "===> Job finished <===" );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		for ( TaskStatusInfo task : job.getTasks() ) {
			assertEquals( GridProcessState.FINISHED, task.getState() );
			assertEquals( otherWorkerUnit.getLocation(), task.getGridProcesses().iterator().next().getWorkerInfo()
					.getWorkerSpec().getLocation() );
		}
		System.out.println( "===> Asserts done <===" );
	}


	/**
	 * 1 broker 1 peer 2 workers (os:windows and os:linux) 1 big job with 2 tasks
	 * (requirements: os == windows || os == linux) wait until job is running
	 * verify if the two tasks are running verify if both workers are in
	 * allocated state
	 */
	@Test
	public void testRequirements3() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
	
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 2);
		unitManager.addUnit(brokerUnit);

		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.addProperty( "os", "windows" );
		WorkerUnit otherWorkerUnit = unitManager.buildNewUnit(WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		otherWorkerUnit.addProperty( "os", "linux" );

		System.out.println( "===> Remote Entities Started (2 Workers, 1 Peer, 1 Broker) <===" );

//		peerUnit.setWorkers( workerUnit, otherWorkerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, otherWorkerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Peers has 2 workers in IDLE status <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		
		// we are assuming that in 30 seconds the Broker has had scheduled all
		// job
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		System.out.println( "===> Broker knows the peer <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJobWithRequirements( 2, 1,
			"os = windows || os = linux" ) );
		System.out.println( "===> Job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		System.out.println( "===>Job running <===" );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		conditionExpecter
			.waitUntilConditionIsMet( new WorkerStatusCondition( otherWorkerUnit, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		System.out.println( "===> Asserts done <===" );
	}


	/**
	 * 1 broker 1 peer 2 workers (with two properties each one, os and mem ) 1 big
	 * job with 2 tasks (2 different requirements, for instance os and mem) wait
	 * until job is running verify if only one task is running verify if the
	 * windows worker is running the task
	 */
	@Test
	public void testRequirements4() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 2);
		unitManager.addUnit(brokerUnit);

		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.addProperty( "os", "windows" );
		workerUnit.addProperty( "mem", "400" );
		WorkerUnit otherWorkerUnit = unitManager.buildNewUnit(WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		otherWorkerUnit.addProperty( "os", "windows" );
		otherWorkerUnit.addProperty( "mem", "300" );

//		workerUnit.initKeys();
//		otherWorkerUnit.initKeys();
//
//		peerUnit.initKeys();
//		brokerUnit.initKeys();

		System.out.println( "===> Remote Entities Started (2 Workers, 1 Peer, 1 Broker) <===" );

//		peerUnit.setWorkers( workerUnit, otherWorkerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, otherWorkerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Peers has 2 workers in IDLE status <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		
		// we are assuming that in 30 seconds the Broker has had scheduled all
		// job
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		System.out.println( "===> Broker knows the peer <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJobWithRequirements( 2, 15,
			"os = windows && mem > 350" ) );
		System.out.println( "===> Job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ), 1000, 30 );
		System.out.println( "===> Job running <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ), 1000, 50 );
		System.out.println( "===> Job finished <===" );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		for ( TaskStatusInfo task : job.getTasks() ) {
			assertEquals( GridProcessState.FINISHED, task.getState() );
			assertEquals( workerUnit.getLocation(), task.getGridProcesses().iterator().next().getWorkerInfo().
					getWorkerSpec().getLocation() );
		}
		System.out.println( "===> Asserts done <===" );
	}

}
