package org.ourgrid.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.system.condition.BrokerAllJobsFinishedCondition;
import org.ourgrid.system.condition.BrokerJobFinishedCondition;
import org.ourgrid.system.condition.BrokerJobRunningCondition;
import org.ourgrid.system.condition.BrokerJobStateCondition;
import org.ourgrid.system.condition.BrokerNumberOfWorkersCondition;
import org.ourgrid.system.condition.BrokerSetPeersCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.condition.WorkerSetPeerCondition;
import org.ourgrid.system.condition.WorkerStatusCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class SimpleExecutionSystemTest extends AbstractSystemTest {
	
	@Test
	public void testStartUnits() throws Exception {
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		
		//		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
	}

	@Test
	public void testSimpleCaseAfterTheJobFinished() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		
		System.out.println( "===> Remote Entities Started (1 Workers, 1 Peer, 1 Broker) <===" );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Peers has the worker in IDLE status <===" );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit ) );
		System.out.println( "===> Worker has the master peer set <===" );
		DeploymentID masterPeer = workerUnit.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit.getStatus().getStatus() );
		System.out.println( "===> The master peer has been set correctly <===" );
		
		peerUnit.addUser(brokerUnit.getLogin());

		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
//			assertEquals( State.UP, brokerUnit.getPeers().iterator().next().getState() );
		assertTrue(brokerUnit.getPeers().iterator().next().isLogged());
		System.out.println( "===> Broker knows the peer <===" );

		brokerUnit.showStatus();

		Collection<LocalConsumerInfo> localConsumerStatus = peerUnit.getLocalConsumerStatus();
		Collection<ConsumerInfo> remoteConsumerStatus = peerUnit.getRemoteConsumerStatus();
		Collection<WorkerInfo> localWorkerStatus = peerUnit.getLocalWorkerStatus();
		assertNull( localWorkerStatus.iterator().next().getConsumerID() );
		assertTrue( localConsumerStatus.isEmpty() );
		assertTrue( remoteConsumerStatus.isEmpty() );
		
		int jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "ok" ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );
		assertEquals( GridProcessState.FINISHED, brokerUnit.getJob( jobid ).getState() );
		System.out.println( "===> Job finished <===" );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition(workerUnit, WorkerStatus.IDLE) );
		assertEquals( WorkerStatus.IDLE, workerUnit.getStatus().getStatus() );
		System.out.println( "===> Worker is idle <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerNumberOfWorkersCondition( brokerUnit, 0 ) );
		System.out.println( "===> Broker has no workers <===" );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Peers has the worker in IDLE status <===" );

		System.out.println( "===> Testing consumer information <===" );
		localConsumerStatus = peerUnit.getLocalConsumerStatus();
		remoteConsumerStatus = peerUnit.getRemoteConsumerStatus();
		localWorkerStatus = peerUnit.getLocalWorkerStatus();
		assertNull( localWorkerStatus.iterator().next().getConsumerID() );
		assertTrue( localConsumerStatus.isEmpty() );
		assertTrue( remoteConsumerStatus.isEmpty() );

	}

	@Test
	public void testSimpleCaseDuringTheJobExecution() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit ) );
		DeploymentID masterPeer = workerUnit.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );

		Collection<LocalConsumerInfo> localConsumerStatus = peerUnit.getLocalConsumerStatus();
		Collection<ConsumerInfo> remoteConsumerStatus = peerUnit.getRemoteConsumerStatus();
		Collection<WorkerInfo> localWorkerStatus = peerUnit.getLocalWorkerStatus();
		assertNull( localWorkerStatus.iterator().next().getConsumerID() );
		assertTrue( localConsumerStatus.isEmpty() );
		assertTrue( remoteConsumerStatus.isEmpty() );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IN_USE ) );
		localWorkerStatus = peerUnit.getLocalWorkerStatus();
		assertTrue( localWorkerStatus.size() == 1 );
		assertTrue( peerUnit.getLocalConsumerStatus().size() == 1 );
		assertEquals( LocalWorkerState.IN_USE, localWorkerStatus.iterator().next().getStatus() );
		System.out.println( "===> Worker is IN_USE for Peer <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerNumberOfWorkersCondition( brokerUnit, 1, jobid ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit,jobid ) );
		Set<WorkerStatusInfo> workers = brokerUnit.getWorkersByJob().get( jobid );
		assertTrue( workers.size() == 1 );
		WorkerStatusInfo workerEntry = workers.iterator().next();
		assertEquals( GridProcessState.RUNNING.toString(), workerEntry.getProcessState() );
		System.out.println( "===> The replica is running for Broker <===" );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		localWorkerStatus = peerUnit.getLocalWorkerStatus();
		assertEquals( WorkerStatus.ALLOCATED_FOR_BROKER, workerUnit.getStatus().getStatus() );

		System.out.println( "===> Worker is in Allocated state<===" );
		
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		System.out.println( "===> Job is running <===" );

		System.out.println( "===> Testing consumer information <===" );
		localConsumerStatus = peerUnit.getLocalConsumerStatus();
		remoteConsumerStatus = peerUnit.getRemoteConsumerStatus();
		assertTrue( remoteConsumerStatus.isEmpty() );
		assertTrue( 1 == localConsumerStatus.size() );
		String brokerURL = brokerUnit.getJabberUserName() + "@" + brokerUnit.getJabberServerHostname();
		assertEquals( brokerURL, localConsumerStatus.iterator().next().getConsumerIdentification() );
		assertEquals( brokerURL, localWorkerStatus.iterator().next().getConsumerID() );

		System.out.println( "===> Test finished <===" );
	}


	@Test
	public void testSimpleCaseAfterTheJobFinishedWithReplicationOneWorker() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 3);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit ) );
		DeploymentID masterPeer = workerUnit.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit.getStatus().getStatus() );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
//		assertEquals( State.UP, brokerUnit.getPeers().iterator().next().getState() );
		assertTrue(brokerUnit.getPeers().iterator().next().isLogged());

		int jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "ok" ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );
		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( GridProcessState.FINISHED, job.getState() );
		for ( GridProcessStatusInfo gridProcess : job.getTasks().iterator().next().getGridProcesses() ) {
			System.out.println( gridProcess + " => " + gridProcess.getState() );
		}
		// Only one replica because only one worker
		assertTrue( job.getTasks().iterator().next().getGridProcesses().size() == 1 );
		verifyOnlyOneReplicaFinishedOthersAborted( job.getTasks().iterator().next() );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
		assertEquals( WorkerStatus.IDLE, workerUnit.getStatus().getStatus() );
	}

	@Test
	public void testSimpleCaseAfterTheJobFinishedWithReplicationThreeWorkers() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 3);
		unitManager.addUnit(brokerUnit);

		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		WorkerUnit workerUnit3 = unitManager.buildNewUnit( WORKER3_PROPERTIES_FILENAME, WorkerUnit.class );

//		//		peerUnit.setWorkers( workerUnit1, workerUnit2, workerUnit3 );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit1,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit1 ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit2,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit2 ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit3,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit3 ) );

		DeploymentID masterPeer = workerUnit1.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit1.getStatus().getStatus() );

		masterPeer = workerUnit2.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit2.getStatus().getStatus() );

		masterPeer = workerUnit3.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit3.getStatus().getStatus() );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
//		assertEquals( State.UP, brokerUnit.getPeers().iterator().next().getState() );
		assertTrue(brokerUnit.getPeers().iterator().next().isLogged());

		int jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "ok" ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );
		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( GridProcessState.FINISHED, job.getState() );

		assertTrue( job.getTasks().iterator().next().getGridProcesses().size() == 3 );
		verifyOnlyOneReplicaFinishedOthersAborted( job.getTasks().iterator().next() );
		verifyDifferentWorkers( job.getTasks().iterator().next() );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit1, WorkerStatus.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit2, WorkerStatus.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit3, WorkerStatus.IDLE ) );
	}

	@Test
	public void testSimpleCaseAfterTheJobFinishedWithReplicationThreeWorkersWithManyTasks() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 5);

		List<WorkerUnit> workerUnits = new ArrayList<WorkerUnit>();
		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		WorkerUnit workerUnit3 = unitManager.buildNewUnit( WORKER3_PROPERTIES_FILENAME, WorkerUnit.class );
//		List<WorkerUnit> workerUnits = unitManager.buildAndStartManyUnits( WorkerUnit.class, 3 );
		
		workerUnits.add(workerUnit1);
		workerUnits.add(workerUnit2);
		workerUnits.add(workerUnit3);

		peerUnit.initKeys();
		brokerUnit.initKeys();

//		peerUnit.setWorkers( workerUnits );

		peerUnit.addUser(brokerUnit.getLogin());
		int jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( 10, "ok" ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );
		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( GridProcessState.FINISHED, job.getState() );

		verifyOnlyOneReplicaFinishedOthersAborted( job.getTasks().iterator().next() );
		verifyDifferentWorkers( job.getTasks().iterator().next() );

		for ( Iterator<WorkerUnit> iter = workerUnits.iterator(); iter.hasNext(); ) {
			WorkerUnit workerUnit = iter.next();
			conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
			assertEquals( WorkerStatus.IDLE, workerUnit.getStatus().getStatus() );
		}
	}

	@Test
	public void testSimpleCaseAfterTheJobFinishedNoReplicationThreeJobs() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );

		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		WorkerUnit workerUnit3 = unitManager.buildNewUnit( WORKER3_PROPERTIES_FILENAME, WorkerUnit.class );

		workerUnit1.initKeys();
		workerUnit2.initKeys();
		workerUnit3.initKeys();
		peerUnit.initKeys();
		brokerUnit.initKeys();

//		//		peerUnit.setWorkers( workerUnit1, workerUnit2, workerUnit3 );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit1,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit1 ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit2,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit2 ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit3,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit3 ) );

		DeploymentID masterPeer = workerUnit1.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit1.getStatus().getStatus() );

		masterPeer = workerUnit2.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit2.getStatus().getStatus() );

		masterPeer = workerUnit3.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit3.getStatus().getStatus() );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );

		int jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "ok" ) );
		int jobid2 = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "ok" ) );
		int jobid3 = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "ok" ) );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid2 ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid3 ) );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit1, WorkerStatus.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit2, WorkerStatus.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit3, WorkerStatus.IDLE ) );
	}

	@Test
	public void testSimpleCaseDuringJobsNoReplicationThreeJobs() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );

		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		WorkerUnit workerUnit3 = unitManager.buildNewUnit( WORKER3_PROPERTIES_FILENAME, WorkerUnit.class );

//		//		peerUnit.setWorkers( workerUnit1, workerUnit2, workerUnit3 );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit1,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit1 ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit2,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit2 ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit3,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit3 ) );

		DeploymentID masterPeer = workerUnit1.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit1.getStatus().getStatus() );

		masterPeer = workerUnit2.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit2.getStatus().getStatus() );

		masterPeer = workerUnit3.getMasterPeer();
		assertEquals( peerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.MODULE_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit3.getStatus().getStatus() );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 1, true ) );
//		assertEquals( State.UP, brokerUnit.getPeers().iterator().next().getState() );
		assertTrue(brokerUnit.getPeers().iterator().next().isLogged());

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );
		int jobid2 = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );
		int jobid3 = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit1, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit2, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit3, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		
		assertTrue( peerUnit.getLocalWorkerStatus().size() == 3 );
		for ( WorkerInfo workerInfo : peerUnit.getLocalWorkerStatus() ) {
			assertEquals( LocalWorkerState.IN_USE, workerInfo.getStatus() );
		}
		
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid2 ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid3 ) );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		JobStatusInfo job2 = brokerUnit.getJob( jobid2 );
		JobStatusInfo job3 = brokerUnit.getJob( jobid3 );

		assertTrue( job.getTasks().size() == 1 );
		assertTrue( job2.getTasks().size() == 1 );
		assertTrue( job3.getTasks().size() == 1 );

		TaskStatusInfo task = job.getTasks().iterator().next();
		TaskStatusInfo task2 = job2.getTasks().iterator().next();
		TaskStatusInfo task3 = job3.getTasks().iterator().next();

		assertTrue( task.getGridProcesses().size() == 1 );
		assertTrue( task2.getGridProcesses().size() == 1 );
		assertTrue( task3.getGridProcesses().size() == 1 );

		GridProcessStatusInfo gridProcess = task.getGridProcesses().iterator().next();
		GridProcessStatusInfo gridProcess2 = task2.getGridProcesses().iterator().next();
		GridProcessStatusInfo gridProcess3 = task3.getGridProcesses().iterator().next();

		brokerUnit.stop();

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit1, WorkerStatus.IDLE ) );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit2, WorkerStatus.IDLE ) );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit3, WorkerStatus.IDLE ) );

		verifyDifferentWorkers( gridProcess, gridProcess2, gridProcess3 );
	}

	@Test
	//TODO falta passar
	public void testSimpleCaseTwoWorkersManyShortJobs() throws Exception {

		final int JOBTOADD = 15;

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 3, 1, 10, 2);

		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit2 = unitManager.buildNewUnit(WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );

		//		peerUnit.setWorkers( workerUnit1, workerUnit2 );
		peerUnit.addUser(brokerUnit.getLogin());

		for ( int i = 1; i <= JOBTOADD; i++ ) {
			brokerUnit.addJob( UnitUtil.buildASleepJob( 2, 2 ) );
		}

		conditionExpecter.waitUntilConditionIsMet( new BrokerAllJobsFinishedCondition( brokerUnit ) );

		brokerUnit.showStatus();

		for ( int i = 1; i <= JOBTOADD; i++ ) {
			AbstractSystemTest.assertAllReplicasFinished( brokerUnit.getJob( i ).getTaskByID( 1 ).getGridProcesses() );
		}
	}


	/**
	 * Scenario: 1 Broker 4 Peers each one with one worker Broker connected with
	 * this four peers 1 Job with 4 tasks Result: The 4 peers must put their
	 * respective worker in IN_USE state
	 */
	@Test
	public void testFourTasks_FourPeers_EachOneWithOneWorker_AJobWith4Tasks() throws Exception {

		final int numberOfTaks = 4;
		final int numberOfReplicas = 1;

		PeerUnit peerUnit1 = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );
		PeerUnit peerUnit2 = unitManager.buildNewUnit( PEER2_PROPERTIES_FILENAME, PeerUnit.class );
		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		PeerUnit peerUnit3 = unitManager.buildNewUnit( PEER3_PROPERTIES_FILENAME, PeerUnit.class );
		WorkerUnit workerUnit3 = unitManager.buildNewUnit( WORKER3_PROPERTIES_FILENAME, WorkerUnit.class );
		PeerUnit peerUnit4 = unitManager.buildNewUnit( PEER4_PROPERTIES_FILENAME, PeerUnit.class );
		WorkerUnit workerUnit4 = unitManager.buildNewUnit( WORKER4_PROPERTIES_FILENAME, WorkerUnit.class );

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, numberOfReplicas);

		System.out.println( "===> Remote entities started (4 peers, 4 workers, 1 Broker) <===" );

//		peerUnit1.setWorkers( workerUnit1 );
//		peerUnit2.setWorkers( workerUnit2 );
//		peerUnit3.setWorkers( workerUnit3 );
//		peerUnit4.setWorkers( workerUnit4 );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit1, workerUnit1,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit2, workerUnit2,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit3, workerUnit3,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit4, workerUnit4,
				LocalWorkerState.IDLE ) );
		System.out.println( "===> Each peer has one worker set <===" );
		
		peerUnit1.addUser(brokerUnit.getLogin());
		peerUnit2.addUser(brokerUnit.getLogin());
		peerUnit3.addUser(brokerUnit.getLogin());
		peerUnit4.addUser(brokerUnit.getLogin());
		
		conditionExpecter.waitUntilConditionIsMet( new BrokerSetPeersCondition( brokerUnit, 4, true ) );
		System.out.println( "===> Broker knows 4 peers <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( numberOfTaks, Integer.MAX_VALUE ) );
		System.out.println( "===> Job added <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerNumberOfWorkersCondition( brokerUnit, 4, jobid ) );
		System.out.println( "===> Broker is using 4 workers <===" );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit1, workerUnit1,
				LocalWorkerState.IN_USE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit2, workerUnit2,
				LocalWorkerState.IN_USE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit3, workerUnit3,
				LocalWorkerState.IN_USE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit4, workerUnit4,
				LocalWorkerState.IN_USE ) );
		System.out.println( "===> The 4 workers is in IN_USE state for each peer <===" );
	}

	@Test
	public void testRunSimpleJob() throws Exception {

		final String simpleJob = RESOURCE_DIR + File.separator + "SimpleJob2.jdf";

		JobSpecification spec = DescriptionFileCompile.compileJDF( simpleJob );

		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		//		peerUnit.setWorkers( workerUnit );
		peerUnit.addUser(brokerUnit.getLogin());

		int job1 = brokerUnit.addJob( spec );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, job1,
				GridProcessState.FINISHED ) );
	}


	private void verifyDifferentWorkers( TaskStatusInfo task ) throws Exception {

		HashSet<ServiceID> serviceIDs = new LinkedHashSet<ServiceID>();

		for ( GridProcessStatusInfo gridProcess : task.getGridProcesses() ) {
			serviceIDs.add( gridProcess.getWorkerInfo().getWorkerSpec().getServiceID() );
		}

		if ( serviceIDs.size() != task.getGridProcesses().size() )
			throw new Exception( "One worker executed more than one replica" );
	}


	private void verifyDifferentWorkers( GridProcessStatusInfo... gridProcesses ) throws Exception {

		HashSet<ServiceID> serviceIDs = new LinkedHashSet<ServiceID>();

		for ( GridProcessStatusInfo gridProcess : gridProcesses ) {
			serviceIDs.add( gridProcess.getWorkerInfo().getWorkerSpec().getServiceID() );
		}

		System.out.println( serviceIDs );
		System.out.println( Arrays.toString( gridProcesses ) );

		if ( serviceIDs.size() != gridProcesses.length )
			throw new Exception( "One worker executed more than one replica" );
	}


	private void verifyOnlyOneReplicaFinishedOthersAborted( TaskStatusInfo task ) throws Exception {

		int numFinished = 0;
		int numAborted = 0;

		for ( GridProcessStatusInfo gridProcess : task.getGridProcesses() ) {

			if ( gridProcess.getState().equals(GridProcessState.FINISHED.toString()) ) {
				++numFinished;
			}

			if ( gridProcess.getState().equals(GridProcessState.ABORTED.toString()) ) {
				++numAborted;
			}
		}

		if ( numFinished == 0 )
			throw new Exception( "Task Is Not Finished!" );
		if ( numFinished > 1 )
			throw new Exception( "More Than One Replica Finished, IMPOSSIBLE!" );
		if ( numFinished + numAborted != task.getGridProcesses().size() )
			throw new Exception( "Not All Replicas Were Aborted" );
	}

}
