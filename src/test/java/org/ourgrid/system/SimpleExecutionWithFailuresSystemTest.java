package org.ourgrid.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.PeerStatusInfo;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.system.condition.AtLeastOneWorkerInAStatusCondition;
import org.ourgrid.system.condition.BrokerHasAPeerInTheState;
import org.ourgrid.system.condition.BrokerJobFinishedCondition;
import org.ourgrid.system.condition.BrokerJobRunningCondition;
import org.ourgrid.system.condition.BrokerNumberOfWorkersCondition;
import org.ourgrid.system.condition.BrokerSetPeersCondition;
import org.ourgrid.system.condition.BrokerUsingWorker;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.condition.PeerSetWorkersCondition;
import org.ourgrid.system.condition.WorkerSetPeerCondition;
import org.ourgrid.system.condition.WorkerStatusCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class SimpleExecutionWithFailuresSystemTest extends AbstractSystemTest {

	/**
	 * create 1 peer create 1 worker setWorker kill Worker worker must be in
	 * contacting state for peer
	 */
	@Test
	public void testPeerAndWorker_WorkerDies() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		//		workerUnit.initKeys();
		//		peerUnit.initKeys();
		System.out.println( "===> Entities started (1 peer, 1 worker) <===" );

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
		System.out.println( "===> Worker set <===" );

		workerUnit.kill();
		System.out.println( "===> Worker killed <===" );

		//		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
		//				LocalWorkerState.CONTACTING ) );
		assertTrue( peerUnit.getLocalWorkerStatus().iterator().next() == null);
		System.out.println( "===> Peer discovered that worker has died <===" );
	}


	/**
	 * create 1 peer create 1 worker setWorker kill Worker worker must be in
	 * contacting state for peer worker restart worker must be in idle state
	 */
	@Test
	public void testPeerAndWorker_WorkerDiesAndRestart() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		workerUnit.initKeys();
		peerUnit.initKeys();
		System.out.println( "===> Entities started (1 peer, 1 worker) <===" );

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit ) );
		System.out.println( "===> Worker set <===" );

		workerUnit.kill();
		System.out.println( "===> Worker killed <===" );

		//		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
		//		LocalWorkerState.CONTACTING ) );
		assertTrue( peerUnit.getLocalWorkerStatus().iterator().next() == null);
		System.out.println( "===> Peer discovered that worker has died <===" );

		workerUnit.initKeys();
		System.out.println( "===> Worker Start <===" );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
		assertEquals( LocalWorkerState.IDLE, peerUnit.getLocalWorkerStatus().iterator().next().getStatus() );

	}


	/**
	 * create mg create peer start peer start mg set peer peer must be in up
	 * state for mg kill peer peer must be in down state for mg restart peer
	 * peer must be in up state for mg
	 */
	@Test
	public void testPeerDieAndRestart_AndMGDetectsIt() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );

		peerUnit.initKeys();
		brokerUnit.initKeys();
		System.out.println( "===> Entities started (1 peer, 1 broker) <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		assertTrue(brokerUnit.getPeers().iterator().next().isNotLogged());
		System.out.println( "===> Set peers <===" );

		peerUnit.kill();
		System.out.println( "===> Peer killed <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, false ) );
		assertTrue(brokerUnit.getPeers().iterator().next().isDown());
		System.out.println( "===> Broker discovered that peer has been died <===" );

		peerUnit.initKeys();
		System.out.println( "===> Peer restarted <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		assertTrue(brokerUnit.getPeers().iterator().next().isNotLogged());
		System.out.println( "===> Broker discovered that peer has been restarted <===" );
	}


	/**
	 * create mg create peer start peer start mg set peer peer must be in up
	 * state for mg kill peer peer must be in down state for mg restart peer
	 * peer must be in up state for mg mg add job job must finish
	 */
	@Test
	public void testPeerDieAndRestart_AndMGUseIt() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		peerUnit.initKeys();
		workerUnit.initKeys();
		brokerUnit.initKeys();
		System.out.println( "===> Entities started (1 worker, 1 peer, 1 broker) <===" );

//		peerUnit.setWorkers( workerUnit );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		assertTrue(brokerUnit.getPeers().iterator().next().isNotLogged());
		System.out.println( "===> Set peers <===" );

		peerUnit.kill();
		System.out.println( "===> Peer killed <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, false ) );
		assertTrue(brokerUnit.getPeers().iterator().next().isDown());
		System.out.println( "===> Broker discovered that peer has been died <===" );

		peerUnit.initKeys();
		System.out.println( "===> Peer restarted <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		assertTrue(brokerUnit.getPeers().iterator().next().isNotLogged());
		System.out.println( "===> Broker discovered that peer has been restarted <===" );

//		peerUnit.setWorkers( workerUnit );

		int jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "ok" ) );
		System.out.println( "===> Job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid) );
		System.out.println( "===> Job is running <===" );

	}

	@Test
	public void testBrokerDieDuringJobExecution() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		workerUnit.initKeys();
		peerUnit.initKeys();
		brokerUnit.initKeys();
		System.out.println( "===> Entities started ( 1 worker, 1 peer, 1 broker ) <===" );

//		peerUnit.setWorkers( workerUnit );
		System.out.println( "===> Set workers done <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		assertTrue(brokerUnit.getPeers().iterator().next().isNotLogged());
		System.out.println( "===> Set peers done <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );
		System.out.println( "===> Job added <===" );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IN_USE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.ALLOCATED_FOR_BROKER) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		System.out.println( "===> Job running <===" );

		brokerUnit.kill();
		System.out.println( "===> Broker killed <===" );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
		assertEquals( WorkerStatus.IDLE, workerUnit.getStatus() );
		assertEquals( 1, peerUnit.getLocalWorkerStatus().size() );
		assertEquals( LocalWorkerState.IDLE, peerUnit.getLocalWorkerStatus().iterator().next().getStatus() );
		assertEquals( 0, peerUnit.getLocalConsumerStatus().size() );
	}

	@Test
	public void testPeerDieDuringJobExecution() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		workerUnit.initKeys();
		peerUnit.initKeys();
		brokerUnit.initKeys();

//		peerUnit.setWorkers( workerUnit );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		assertEquals( GridProcessState.RUNNING, brokerUnit.getJob( jobid ).getState() );

		peerUnit.kill();
		conditionExpecter.waitUntilConditionIsMet( new BrokerNumberOfWorkersCondition( brokerUnit, 0 ) );

		assertTrue(brokerUnit.getPeers().iterator().next().isDown() );
	}

	@Test
	public void testWorkerDieDuringJobExecution() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1);
		unitManager.addUnit(brokerUnit);
		brokerUnit.setMaxFails( 1 );
		brokerUnit.setNumberOfReplicaExecutors( 1 );

		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		workerUnit.initKeys();
		peerUnit.initKeys();
		brokerUnit.initKeys();

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit ) );
		DeploymentID masterPeer = workerUnit.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME, masterPeer.getContainerName() );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		assertTrue( brokerUnit.getPeers().iterator().next().isNotLogged() );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		assertEquals( WorkerStatus.ALLOCATED_FOR_BROKER, workerUnit.getStatus() );
		assertEquals( 1, peerUnit.getLocalWorkerStatus().size() );
		assertEquals( 1, peerUnit.getLocalConsumerStatus().size() );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IN_USE ) );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		assertEquals( GridProcessState.RUNNING, brokerUnit.getJob( jobid ).getState() );

		workerUnit.kill();

		// this kind of error not increment the number of fails, so, the job
		// will continue to execute.
		conditionExpecter.waitUntilConditionIsMet( new BrokerNumberOfWorkersCondition( brokerUnit, 0 ) );

		assertTrue( peerUnit.getLocalWorkerStatus().iterator().next() == null);
		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( GridProcessState.RUNNING, job.getState() );
		TaskStatusInfo task = job.getTasks().iterator().next();
		assertEquals( 1, job.getTasks().size() );
		assertEquals( GridProcessState.RUNNING, task.getState() );
		Collection<GridProcessStatusInfo> replicas = task.getGridProcesses();
		assertEquals( 1, replicas.size() );
		assertEquals( GridProcessState.FAILED, task.getGridProcesses().iterator().next().getState() );
	}

	@Test
	public void testTwoWorkersInOnePeer_AndOneWorkerDie_DuringJobExecution() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1);
		unitManager.addUnit(brokerUnit);
		brokerUnit.setMaxFails( 1 );
		brokerUnit.setNumberOfReplicaExecutors( 1 );

		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit otherWorkerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		workerUnit.initKeys();
		otherWorkerUnit.initKeys();
		peerUnit.initKeys();
		brokerUnit.initKeys();

		System.out.println( "===> Remote Entities Started (2 Workers, 1 Peer, 1 Broker) <===" );

//		peerUnit.setWorkers( workerUnit, otherWorkerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerSetWorkersCondition( peerUnit, 2 ) );
		System.out.println( "===> Peer has been set with two workers <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
		System.out.println( "===> Broker knows the peer <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );
		System.out.println( "===> Job " + jobid + " added <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		System.out.println( "===> Job is running <===" );

		conditionExpecter.waitUntilConditionIsMet( new AtLeastOneWorkerInAStatusCondition( WorkerStatus.ALLOCATED_FOR_BROKER,
				workerUnit, otherWorkerUnit ) );
		System.out.println( "===> At least one worker in the Allocated State <===" );

		conditionExpecter.waitUntilConditionIsMet( new AtLeastOneWorkerInAStatusCondition( WorkerStatus.IDLE,
				workerUnit, otherWorkerUnit ) );
		System.out.println( "===> At least one worker in the IDLE State <===" );

		WorkerUnit idleWorkerUnit = getWorkerInState( WorkerStatus.IDLE, workerUnit, otherWorkerUnit );
		assertNotNull( idleWorkerUnit );
		System.out.println( "===> The test knows the idle worker unit <===" );

		WorkerUnit allocatedWorkerUnit = getWorkerInState( WorkerStatus.ALLOCATED_FOR_BROKER, workerUnit, otherWorkerUnit );
		assertNotNull( allocatedWorkerUnit );
		System.out.println( "===> The test knows the allocated worker unit <===" );

		allocatedWorkerUnit.kill();
		System.out.println( "===> Allocated worker unit has been killed <===" );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, idleWorkerUnit,
				LocalWorkerState.IN_USE ) );
		System.out.println( "===> Ex Idle Worker is IN_USE for Peer <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerUsingWorker( jobid, brokerUnit, idleWorkerUnit ) );
		System.out.println( "===> Broker now is using the worker unit that was idle <===" );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( idleWorkerUnit, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		System.out.println( "===> The Survivor Worker now is allocated <===" );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( GridProcessState.RUNNING, job.getState() );
		TaskStatusInfo task = job.getTasks().iterator().next();
		assertEquals( 1, job.getTasks().size() );
		assertEquals( GridProcessState.RUNNING, task.getState() );
		Collection<GridProcessStatusInfo> replicas = task.getGridProcesses();
		assertEquals( 2, replicas.size() );
		assertTrue( removeReplica( GridProcessState.FAILED.toString(), replicas ) );
		assertTrue( removeReplica( GridProcessState.RUNNING.toString(), replicas ) );
	}


	/**
	 * This test has two peers, each one with one worker. Broker has the two
	 * Peers as local Peers. A big job (in time) will be added to MG. The
	 * replica will start in one worker, the test will identify this worker and
	 * kill the peer that owns this worker. After that, the task must be
	 * scheduled to the worker from the other peer.
	 */
	@Test
	public void testTwoPeeers_EachOneWithOneWorker_AndOnePeerDie_DuringJobExecution() throws Exception {

		PeerUnit peerUnit1 = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1);
		unitManager.addUnit(brokerUnit);
		brokerUnit.setMaxFails( 1 );
		brokerUnit.setNumberOfReplicaExecutors( 1 );

		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WorkerUnit.class );
		PeerUnit peerUnit2 = unitManager.buildNewUnit( PeerUnit.class );

		workerUnit1.initKeys();
		workerUnit2.initKeys();
		peerUnit1.initKeys();
		peerUnit2.initKeys();
		brokerUnit.initKeys();

		System.out.println( "===> Remote Entities Started (2 Workers, 2 Peers, 1 Broker) <===" );

//		peerUnit.setWorkers( workerUnit1 );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit1, workerUnit1,
				LocalWorkerState.IDLE ) );
		System.out.println( "===> A Peer knows 1 worker <===" );

//		peerUnit2.setWorkers( workerUnit2 );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit2, workerUnit2,
				LocalWorkerState.IDLE ) );
		System.out.println( "===> Other Peer knows 1 worker <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 2, true ) );
		Collection<PeerStatusInfo> peers = brokerUnit.getPeers();
		assertEquals( 2, peers.size() );
		Iterator<PeerStatusInfo> peersIt = peers.iterator();
		assertTrue( peersIt.next().isNotLogged() );
		assertTrue( peersIt.next().isNotLogged() );
		System.out.println( "===> Broker knows the 2 peers <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );
		System.out.println( "===> Job " + jobid + " added <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerNumberOfWorkersCondition( brokerUnit, 1, jobid ) );
		Set<WorkerStatusInfo> workers = brokerUnit.getWorkersByJob().get( jobid );
		assertEquals( 1, workers.size() );
		System.out.println( "===> Broker has 1 Worker <=== " );

		WorkerStatusInfo workerEntry = workers.iterator().next();
		assertEquals( GridProcessState.RUNNING.toString(), workerEntry.getProcessState() );
		System.out.println( "===> The replica is running for Broker <===" );

		conditionExpecter.waitUntilConditionIsMet( new AtLeastOneWorkerInAStatusCondition( WorkerStatus.ALLOCATED_FOR_BROKER,
				workerUnit1, workerUnit2 ) );
		System.out.println( "===> At least one worker in the Allocated State <===" );

		conditionExpecter.waitUntilConditionIsMet( new AtLeastOneWorkerInAStatusCondition( WorkerStatus.IDLE,
				workerUnit1, workerUnit2 ) );
		System.out.println( "===> At least one worker in the IDLE State <===" );

		WorkerUnit idleWorkerUnit = getWorkerInState( WorkerStatus.IDLE, workerUnit1, workerUnit2 );
		assertNotNull( idleWorkerUnit );
		System.out.println( "===> The test knows the idle worker unit <===" );

		WorkerUnit allocatedWorkerUnit = getWorkerInState( WorkerStatus.ALLOCATED_FOR_BROKER, workerUnit1, workerUnit2 );
		assertNotNull( allocatedWorkerUnit );
		System.out.println( "===> The test knows the allocated worker unit <===" );

		DeploymentID allocatedPeerDeploymentID = allocatedWorkerUnit.getMasterPeer();
		PeerUnit usedPeerUnit = getPeerUnitFromDeploymentID( allocatedPeerDeploymentID.getContainerLocation(), peerUnit1,
				peerUnit2 );
		System.out.println( "===> About to kill peer <===" );
		usedPeerUnit.kill();
		System.out.println( "===> The Peer that commands the allocated worker has been killed <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerUsingWorker( jobid, brokerUnit, idleWorkerUnit ) );
		System.out.println( "===> Broker now is using the worker unit that was idle <===" );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( idleWorkerUnit, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		System.out.println( "===> The Survivor Worker now is allocated <===" );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( GridProcessState.RUNNING, job.getState() );
		TaskStatusInfo task = job.getTasks().iterator().next();
		assertEquals( 1, job.getTasks().size() );
		assertEquals( GridProcessState.RUNNING, task.getState() );
		Collection<GridProcessStatusInfo> replicas = task.getGridProcesses();
		assertEquals( 2, replicas.size() );
		assertTrue( removeReplica( GridProcessState.FAILED.toString(), replicas ) );
		assertTrue( removeReplica( GridProcessState.RUNNING.toString(), replicas ) );
	}

	@Test
	public void testPeerDieAndRestartDuringJobExecution() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		workerUnit.initKeys();
		peerUnit.initKeys();
		brokerUnit.initKeys();

//		peerUnit.setWorkers( workerUnit );
		System.out.println( "===> Set workers done <===" );
		System.out.println( "===> Set peers done <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );
		System.out.println( "===> Job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IN_USE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		System.out.println( "===> Job is running <===" );
		peerUnit.kill();
		System.out.println( "===> Peer unit killed Location: " + peerUnit.getLocation() + " <=== " );
		conditionExpecter.waitUntilConditionIsMet( new BrokerNumberOfWorkersCondition( brokerUnit, 0 ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.DOWN ) );
		System.out.println( "===> Broker knows that peer is down and has no workers any more <===" );

		peerUnit.initKeys();
		System.out.println( "===> Peer restarted Location: " + peerUnit.getLocation() + " <===\n\n\n\n\n\n\n\n" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Broker knows that peer is up again <===\n\n\n\n\n\n\n" );

//		peerUnit.setWorkers( workerUnit );
		System.out.println( "===> Broker knows that peer is up again <===\n\n\n\n##############\n\n\n" );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IN_USE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		System.out.println( "===> Job is running again <===" );
	}

	// public void testJIC() throws Exception{
	// Module m = JICModuleFactory.getInstance().buildNewModule("B");
	// A b = new B();
	// m.bind("b", b);
	// m.start();
	//		
	// Module m1 = JICModuleFactory.getInstance().buildNewModule("B1");
	// B b1 = new B();
	// m1.bind("b1", b1);
	// m1.start();
	//		
	// System.err.println("OBJECTS BOUND");
	//		
	// A b1Stub = m.lookup(b1.getDeploymentID(), A.class);
	// A bStub = m.lookup(b.getDeploymentID(), A.class);
	// m.getFailureDetector().registerFailureInterested(bStub, b1Stub);
	// System.err.println("STUBS GENERATED");
	//		
	// System.err.println("BEFORE FIRST METHOD INVOCATION");
	// b1Stub.a();
	//		
	// Thread.sleep(3000);
	//		
	// assertEquals(1, b1.getCounter());
	//		
	// m1.shutdown();
	// System.err.println("MODULE SHUTDOWN");
	// Thread.sleep(2000);
	//		
	//		
	// m1 = JICModuleFactory.getInstance().buildNewModule("B1");
	// b1 = new B();
	// m1.bind("b1", b1);
	// m1.start();
	//		
	// System.err.println("MOULE RESTARTED AND OBJECT BOUND");
	//		
	// b1Stub = m.lookup(b1.getDeploymentID(), A.class);
	// m.getFailureDetector().registerRecoveryInterested(bStub, b1Stub);
	// System.err.println("RECOVERY INTEREST REGISTERED");
	// Thread.sleep(2000);
	//		
	// m.getFailureDetector().registerFailureInterested(bStub, b1Stub);
	// System.err.println("SECOND FAILURE INTEREST REGISTERED");
	// System.err.println("BEFORE SECOND METHOD INVOCATION");
	// b1Stub.a();
	//		
	// Thread.sleep(2000);
	// b1Stub.a();
	// Thread.sleep(2000);
	// m.shutdown();
	// m1.shutdown();
	// System.err.println("MODULES SHUTDOWN");
	// }

	//	

	//	public interface A extends NotifiableEventProcessor, Serializable {
	//
	//		void a();
	//	}


	private WorkerUnit getWorkerInState( WorkerStatus status, WorkerUnit... workerUnits ) throws Exception {

		for ( WorkerUnit unit : workerUnits ) {
			if ( unit.getStatus().equals( status ) ) {
				return unit;
			}
		}
		return null;
	}


	private boolean removeReplica( String state, Collection<GridProcessStatusInfo> replicas ) {

		for ( Iterator<GridProcessStatusInfo> replicasIt = replicas.iterator(); replicasIt.hasNext(); ) {
			GridProcessStatusInfo replica = replicasIt.next();
			if ( replica.getState().equals(state) ) {
				replicasIt.remove();
				return true;
			}
		}
		return false;
	}


	private PeerUnit getPeerUnitFromDeploymentID( String peerLocation, PeerUnit... peerUnits ) throws Exception {

		for ( PeerUnit unit : peerUnits ) {
			String fullID = unit.getJabberUserName() + "@" + unit.getJabberServerHostname() + (unit.getModuleName() != null ? "/" + unit.getModuleName() : "");
			if (fullID.equals( peerLocation ) ) {
				return unit;
			}
		}
		return null;
	}
}

