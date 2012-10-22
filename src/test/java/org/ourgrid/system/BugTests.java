package org.ourgrid.system;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.system.condition.BrokerAllJobsFinishedCondition;
import org.ourgrid.system.condition.BrokerHasAPeerInTheState;
import org.ourgrid.system.condition.BrokerJobFinishedCondition;
import org.ourgrid.system.condition.BrokerJobStateCondition;
import org.ourgrid.system.condition.DiscoveryServiceNumberOfPeersCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.condition.WorkerSetPeerCondition;
import org.ourgrid.system.condition.WorkerStatusCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.DiscoveryServiceUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

/**
 * This tests were created from users' bug reports
 */
public class BugTests extends AbstractSystemTest {

	public static final String LARGE_JOB = RESOURCE_DIR + File.separator + "jobv4.jdf";

	public static final String SIMPLE_JOB = "examples" + File.separator + "addJob" + File.separator + "simplejob.jdf";

	/**
	 * When JIC had a bug on message fragmentation, this tests wouldn't pass
	 */
	@Test
	public void testLargeJob() throws Exception {

		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		peerUnit.addUser(brokerUnit.getLogin());

		JobSpecification spec = DescriptionFileCompile.compileJDF( LARGE_JOB );

		int job1 = brokerUnit.addJob( spec );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, job1,
			GridProcessState.UNSTARTED ) );

		brokerUnit.showStatus();
	}

	@Test
	public void testStatus() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );

		peerUnit.addUser(brokerUnit.getLogin());

		int jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "ok" ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobid,
			GridProcessState.UNSTARTED ) );

		brokerUnit.showStatus();
	}

	@Test
	public void testWorkerRecovery() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		peerUnit.addUser(brokerUnit.getLogin());

//		peerUnit.setWorkers( workerUnit );

		int jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( 2, "abc" ) );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );

		workerUnit.kill();
		workerUnit.start();
		
//		peerUnit.setWorkers( workerUnit );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( 2, "abc" ) );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );

		List<GridProcessStatusInfo> gridProcesses1 = brokerUnit.getJob( jobid ).getTaskByID( 1 ).getGridProcesses();
		List<GridProcessStatusInfo> gridProcesses2 = brokerUnit.getJob( jobid ).getTaskByID( 2 ).getGridProcesses();

		brokerUnit.showStatus();

		AbstractSystemTest.assertAllReplicasFinished( gridProcesses1 );
		AbstractSystemTest.assertAllReplicasFinished( gridProcesses2 );

		brokerUnit.kill();

		brokerUnit.initKeys();

		jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( 2, "abc" ) );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );

		gridProcesses1 = brokerUnit.getJob( jobid ).getTaskByID( 1 ).getGridProcesses();
		gridProcesses2 = brokerUnit.getJob( jobid ).getTaskByID( 2 ).getGridProcesses();

		AbstractSystemTest.assertAllReplicasFinished( gridProcesses1 );
		AbstractSystemTest.assertAllReplicasFinished( gridProcesses2 );
	}

	@Test
	public void testConnectionError() throws Exception {

		final int JOBTOADD = 6;

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 2);
		unitManager.addUnit(brokerUnit);

		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );

//		peerUnit.setWorkers( workerUnit1, workerUnit2 );
		peerUnit.addUser(brokerUnit.getLogin());

		for ( int i = 1; i <= JOBTOADD; i++ ) {
			brokerUnit.addJob( UnitUtil.buildASleepJob( 2, 1 ) );
		}

		conditionExpecter.waitUntilConditionIsMet( new BrokerAllJobsFinishedCondition( brokerUnit ) );

		brokerUnit.showStatus();

		for ( int i = 1; i <= JOBTOADD; i++ ) {
			final JobStatusInfo job = brokerUnit.getJob( i );
			AbstractSystemTest.assertAllReplicasFinished( job.getTaskByID( 1 ).getGridProcesses() );
			AbstractSystemTest.assertAllReplicasFinished( job.getTaskByID( 2 ).getGridProcesses() );
		}
	}
	
	/**
	 * This test is meant to reproduce a connection error scenario.
	 * This scenario is compound by a discovery service, 
	 * a local peer, a remote peer, a remote worker and 
	 * a broker submitting sleeping jobs. 
	 * The test waits until all jobs are finished and remote worker is IDLE.  
	 * It also asserts that all replicas have failed or aborted.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConnectionError2() throws Exception {
		
		final int JOBTOADD = 6;
		
		DiscoveryServiceUnit dsUnit = unitManager.buildNewUnit( DiscoveryServiceUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		PeerUnit localPeerUnit = unitManager.buildNewUnit( PeerUnit.class );
		localPeerUnit.setDiscoveryService( dsUnit );
		PeerUnit remotePeerUnit = unitManager.buildNewUnit( PeerUnit.class );
		remotePeerUnit.setDiscoveryService( dsUnit );
		WorkerUnit remoteWorkerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		System.out.println( "===> Units created <===" );
		
		System.out.println( "===> Units started <===" );

		conditionExpecter.waitUntilConditionIsMet( new DiscoveryServiceNumberOfPeersCondition( dsUnit, 2 ) );
		System.out.println( "===> Discovery Service knows 2 peers <===" );

//		remotePeerUnit.setWorkers( remoteWorkerUnit );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( remoteWorkerUnit ) );
		System.out.println( "===> Remote peer knows one worker <===" );

		localPeerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, localPeerUnit, PeerTestState.UP ) );
		System.out.println( "===> Broker knows the local peer <===" );

		for (int i = 0; i < JOBTOADD; i++) {
			brokerUnit.addJob( UnitUtil.buildASleepJob( 2, 5 ) );
		}
		
		conditionExpecter.waitUntilConditionIsMet( new BrokerAllJobsFinishedCondition( brokerUnit ) );
		brokerUnit.showStatus();
		System.out.println( "===> Job finished <===" );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( remoteWorkerUnit, WorkerStatus.IDLE ) );
		assertEquals( WorkerStatus.IDLE, remoteWorkerUnit.getStatus() );
		System.out.println( "===> Remote worker is IDLE <===" );
	
		for ( int i = 1; i <= JOBTOADD; i++ ) {
			final JobStatusInfo job = brokerUnit.getJob( i );
			AbstractSystemTest.assertAllReplicasFinished( job.getTaskByID( 1 ).getGridProcesses() );
			AbstractSystemTest.assertAllReplicasFinished( job.getTaskByID( 2 ).getGridProcesses() );
		}
		
	}
	
	/**
	 * This test must reproduce a connection error scenario.
	 * This scenario is compound by a discovery service, 
	 * a local peer, a remote peer, a local worker, a remote worker and 
	 * a broker submitting sleeping jobs. 
	 * The test waits until all jobs are finished and remote worker is IDLE. 
	 * It also asserts that all replicas have failed or aborted.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConnectionError3() throws Exception{

		final int JOBTOADD = 6;
		
		DiscoveryServiceUnit dsUnit = unitManager.buildNewUnit( DiscoveryServiceUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		PeerUnit localPeerUnit = unitManager.buildNewUnit( PeerUnit.class );
		localPeerUnit.setDiscoveryService( dsUnit );
		PeerUnit remotePeerUnit = unitManager.buildNewUnit( PeerUnit.class );
		remotePeerUnit.setDiscoveryService( dsUnit );
		WorkerUnit remoteWorkerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit localWorkerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		System.out.println( "===> Units created <===" );
		
		System.out.println( "===> Units started <===" );
		
		conditionExpecter.waitUntilConditionIsMet( new DiscoveryServiceNumberOfPeersCondition( dsUnit, 2 ) );
		System.out.println( "===> Discovery Service knows 2 peers <===" );

//		localPeerUnit.setWorkers( localWorkerUnit );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( localWorkerUnit ) );
		System.out.println( "===> Local peer knows one worker <===" );
		
//		remotePeerUnit.setWorkers( remoteWorkerUnit );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( remoteWorkerUnit ) );
		System.out.println( "===> Remote peer knows one worker <===" );
		
		localPeerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, localPeerUnit, PeerTestState.UP ) );
		System.out.println( "===> Broker knows the local peer <===" );
		
		for (int i = 0; i < JOBTOADD; i++) {
			brokerUnit.addJob( UnitUtil.buildASleepJob( 2, 5 ) );
		}
		
		conditionExpecter.waitUntilConditionIsMet( new BrokerAllJobsFinishedCondition( brokerUnit ) );
		brokerUnit.showStatus();
		System.out.println( "===> Job finished <===" );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( remoteWorkerUnit, WorkerStatus.IDLE ) );
		assertEquals( WorkerStatus.IDLE, remoteWorkerUnit.getStatus() );
		System.out.println( "===> Remote worker is IDLE <===" );
	
		for ( int i = 1; i <= JOBTOADD; i++ ) {
			final JobStatusInfo job = brokerUnit.getJob( i );
			AbstractSystemTest.assertAllReplicasFinished( job.getTaskByID( 1 ).getGridProcesses() );
			AbstractSystemTest.assertAllReplicasFinished( job.getTaskByID( 2 ).getGridProcesses() );
		}
	}
}
