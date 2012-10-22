package org.ourgrid.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.system.condition.BrokerJobRunningCondition;
import org.ourgrid.system.condition.BrokerJobStateCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.condition.WorkerStatusCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

public class BrokerBasicActionsTest extends AbstractSystemTest {

	
	private BrokerUnit brokerUnit;

	private PeerUnit peerUnit;

	private WorkerUnit workerUnit;

	private WorkerUnit workerUnit2;
	
	@Before
	public void setUp() throws Exception {

		super.setUp();
		brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1);
		unitManager.addUnit(brokerUnit);
		System.out.println( "==> Broker start" );
		peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		System.out.println( "==> Peer start" );
		workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		System.out.println( "==> Worker 1 start" );
		workerUnit2 = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		System.out.println( "==> Worker 2 start" );
		
		peerUnit.addUser(brokerUnit.getLogin());
	}
	
	/**
	 * Add 2 jobs, cancel both
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddJobAndCancelJob() throws Exception {

		int job1 = brokerUnit.addJob( UnitUtil.buildASleepJob( 1, Integer.MAX_VALUE ) );
		int job2 = brokerUnit.addJob( UnitUtil.buildASleepJob( 1, Integer.MAX_VALUE ) );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, job1 ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, job2 ) );

		brokerUnit.cancelJob( job1 );
		assertEquals( GridProcessState.CANCELLED, brokerUnit.getJob( job1 ).getState() );

		brokerUnit.cancelJob( job2 );
		assertEquals( GridProcessState.CANCELLED, brokerUnit.getJob( job2 ).getState() );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
	}
	
	/**
	 * Add and cancel 4 jobs
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddJobAndCancelJob2() throws Exception {

		final int jobsToRunAndCancel = 4;

		for ( int i = 0; i < jobsToRunAndCancel; i++ ) {
			int jobID = brokerUnit.addJob( UnitUtil.buildASleepJob( 1, Integer.MAX_VALUE ) );
			conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobID ) );
			brokerUnit.cancelJob( jobID );
			assertEquals( GridProcessState.CANCELLED, brokerUnit.getJob( jobID ).getState() );
			conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
			conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
		}
	}

	/*@Test
	public void testWaitForJob() throws Exception {

		int jobID = brokerUnit.addJob( UnitUtil.buildASleepJob( 1, 30 ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobID ) );

		brokerUnit.waitForJob( jobID );
		assertEquals( GridProcessState.FINISHED, brokerUnit.getJob( jobID ).getState() );

		try {
			brokerUnit.waitForJob( 129624 );
			fail();
		} catch ( Exception e ) {}
	}*/

	@Test
	public void testCleanJob1() throws Exception {

		// Test cleaning canceled job
		int job1 = brokerUnit.addJob( UnitUtil.buildASleepJob( 2, Integer.MAX_VALUE ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, job1 ) );
		brokerUnit.cancelJob( job1 );
		assertEquals( GridProcessState.CANCELLED, brokerUnit.getJob( job1 ).getState() );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		brokerUnit.cleanJob( job1 );
		assertNull( brokerUnit.getJob( job1 ) );

		// Test cleaning of finished job
		int job2 = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "OK" ) );
		System.out.println( "job 2 added" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, job2,
				GridProcessState.FINISHED ) );
		System.out.println( brokerUnit.getJob( job2 ).getState() );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );

		brokerUnit.cleanJob( job2 );
		assertNull( brokerUnit.getJob( job2 ) );
	}

	@Test
	public void testCleanJob2() throws Exception {

		// Try to clean a running job
		int job3 = brokerUnit.addJob( UnitUtil.buildASleepJob( 1, Integer.MAX_VALUE ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, job3 ) );
		
		brokerUnit.cleanJob( job3 );
		assertEquals(GridProcessState.RUNNING, brokerUnit.getJob(job3).getState());
		assertNotNull( brokerUnit.getJob( job3 ) );
	}
	
}
