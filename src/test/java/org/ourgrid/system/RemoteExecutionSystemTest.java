package org.ourgrid.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.system.condition.BrokerHasAPeerInTheState;
import org.ourgrid.system.condition.BrokerJobFinishedCondition;
import org.ourgrid.system.condition.BrokerJobRunningCondition;
import org.ourgrid.system.condition.DiscoveryServiceNumberOfPeersCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.condition.WorkerSetPeerCondition;
import org.ourgrid.system.condition.WorkerStatusCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.DiscoveryServiceUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class RemoteExecutionSystemTest extends AbstractSystemTest {

	/**
	 * Test the execution of a simple job running on a remote worker. 2 peers
	 * (one local and one remote) 1 broker (connected to local peer) 1 worker
	 * (connected to the remote peer) 1 discovery service (that knows the 2
	 * peers) add job wait until the job finishes and the remote worker becomes
	 * idle
	 */
	@Test
	public void testSimpleRemoteExecution() throws Exception {

		DiscoveryServiceUnit dsUnit = unitManager.buildNewUnit( DiscoveryServiceUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		PeerUnit localPeerUnit = unitManager.buildNewUnit( PeerUnit.class );
		localPeerUnit.setDiscoveryService( dsUnit );
		PeerUnit remotePeerUnit = unitManager.buildNewUnit( PeerUnit.class );
		remotePeerUnit.setDiscoveryService( dsUnit );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		System.out.println( "===> Units created <===" );

		dsUnit.initKeys();
		brokerUnit.initKeys();
		localPeerUnit.initKeys();
		remotePeerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Units started <===" );

		conditionExpecter.waitUntilConditionIsMet( new DiscoveryServiceNumberOfPeersCondition( dsUnit, 2 ) );
		System.out.println( "===> Discovery Service knows 2 peers <===" );

//		remotePeerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit ) );
		System.out.println( "===> Remote peer knows one worker <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, localPeerUnit, PeerTestState.UP ) );
		System.out.println( "===> Broker knows the local peer <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( 5 ) );
		
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		
		localPeerUnit.showStatus();
		
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );
		assertEquals( GridProcessState.FINISHED, brokerUnit.getJob( jobid ).getState() );
		System.out.println( "===> Job finished <===" );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit, WorkerStatus.IDLE ) );
		assertEquals( WorkerStatus.IDLE, workerUnit.getStatus() );
		System.out.println( "===> Remote worker is IDLE <===" );
	}

	@Test
	public void testRemoteExecution() throws Exception {

		DiscoveryServiceUnit dsUnit = unitManager.buildNewUnit( DiscoveryServiceUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );
		PeerUnit localPeerUnit = unitManager.buildNewUnit( PeerUnit.class );
		localPeerUnit.setDiscoveryService( dsUnit );
		PeerUnit remotePeerUnit = unitManager.buildNewUnit( PeerUnit.class );
		remotePeerUnit.setDiscoveryService( dsUnit );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		System.out.println( "===> Units created <===" );

		dsUnit.initKeys();
		brokerUnit.initKeys();
		localPeerUnit.initKeys();
		remotePeerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Units started <===" );

//		remotePeerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new DiscoveryServiceNumberOfPeersCondition( dsUnit, 2 ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit ) );
		DeploymentID masterPeer = workerUnit.getMasterPeer();

		assertEquals( remotePeerUnit.getLocation(), masterPeer.getContainerLocation() );
		assertEquals( PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME, masterPeer.getContainerName() );
		assertEquals( WorkerStatus.IDLE, workerUnit.getStatus() );

		Collection<LocalConsumerInfo> localConsumerStatus = remotePeerUnit.getLocalConsumerStatus();
		Collection<ConsumerInfo> remoteConsumerStatus = remotePeerUnit.getRemoteConsumerStatus();
		assertTrue( localConsumerStatus.isEmpty() );
		assertTrue( remoteConsumerStatus.isEmpty() );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( Integer.MAX_VALUE ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		assertEquals( GridProcessState.RUNNING, brokerUnit.getJob( jobid ).getState() );

		localConsumerStatus = remotePeerUnit.getLocalConsumerStatus();
		remoteConsumerStatus = remotePeerUnit.getRemoteConsumerStatus();
		assertTrue( localConsumerStatus.isEmpty() );
		assertEquals( 1, remoteConsumerStatus.size() );
		String localPeerURL = localPeerUnit.getJabberUserName() + "@" + localPeerUnit.getJabberServerHostname();
		assertEquals( localPeerURL, remoteConsumerStatus.iterator().next().getConsumerIdentification() );
	}

	@Test
	public void testRemoteExecutionsWithThreePeers() throws Exception {

		DiscoveryServiceUnit dsUnit = unitManager.buildNewUnit( DiscoveryServiceUnit.class );
		
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 2);
		unitManager.addUnit(brokerUnit);

		PeerUnit localPeerUnit = unitManager.buildNewUnit( PeerUnit.class );
		PeerUnit remotePeerUnit1 = unitManager.buildNewUnit( PeerUnit.class );
		PeerUnit remotePeerUnit2 = unitManager.buildNewUnit( PeerUnit.class );

		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit3 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit4 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit5 = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit6 = unitManager.buildNewUnit( WorkerUnit.class );

		localPeerUnit.setDiscoveryService( dsUnit );
		remotePeerUnit1.setDiscoveryService( dsUnit );
		remotePeerUnit2.setDiscoveryService( dsUnit );


		dsUnit.initKeys();
		localPeerUnit.initKeys();
		remotePeerUnit1.initKeys();
		remotePeerUnit2.initKeys();
		workerUnit1.initKeys();
		workerUnit2.initKeys();
		workerUnit3.initKeys();
		workerUnit4.initKeys();
		workerUnit5.initKeys();
		workerUnit6.initKeys();
		brokerUnit.initKeys();

//		localPeerUnit.setWorkers( workerUnit1, workerUnit2 );
//		remotePeerUnit1.setWorkers( workerUnit3, workerUnit4 );
//		remotePeerUnit2.setWorkers( workerUnit5, workerUnit6 );

		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit1 ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit2 ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit3 ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit4 ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit5 ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerSetPeerCondition( workerUnit6 ) );

		Collection<WorkerInfo> localPeerLocalWorkerStatus = localPeerUnit.getLocalWorkerStatus();
		for ( WorkerInfo localWorkerStatusEntry : localPeerLocalWorkerStatus ) {
			assertEquals( LocalWorkerState.IDLE, localWorkerStatusEntry.getStatus() );
		}
		Collection<RemoteWorkerInfo> localPeerRemoteWorkerStatus = localPeerUnit.getRemoteWorkerStatus();
		assertTrue( localPeerRemoteWorkerStatus.isEmpty() );
		Collection<LocalConsumerInfo> localPeerLocalConsumerStatus = localPeerUnit.getLocalConsumerStatus();
		assertTrue( localPeerLocalConsumerStatus.isEmpty() );
		Collection<ConsumerInfo> localPeerRemoteConsumerStatus = localPeerUnit.getRemoteConsumerStatus();
		assertTrue( localPeerRemoteConsumerStatus.isEmpty() );

		Collection<WorkerInfo> remotePeerLocalWorkerStatus1 = remotePeerUnit1.getLocalWorkerStatus();
		for ( WorkerInfo localWorkerStatusEntry : remotePeerLocalWorkerStatus1 ) {
			assertEquals( LocalWorkerState.IDLE, localWorkerStatusEntry.getStatus() );
		}

		Collection<WorkerInfo> remotePeerLocalWorkerStatus2 = remotePeerUnit2.getLocalWorkerStatus();
		for ( WorkerInfo localWorkerStatusEntry : remotePeerLocalWorkerStatus2 ) {
			assertEquals( LocalWorkerState.IDLE, localWorkerStatusEntry.getStatus() );
		}

		int job1 = brokerUnit.addJob( UnitUtil.buildASleepJob( 6, Integer.MAX_VALUE ) );

		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit1, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit2, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit3, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit4, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit5, WorkerStatus.ALLOCATED_FOR_BROKER ) );
		conditionExpecter.waitUntilConditionIsMet( new WorkerStatusCondition( workerUnit6, WorkerStatus.ALLOCATED_FOR_BROKER ) );

		localPeerLocalWorkerStatus = localPeerUnit.getLocalWorkerStatus();
		for ( WorkerInfo localWorkerStatusEntry : localPeerLocalWorkerStatus ) {
			assertEquals( LocalWorkerState.IN_USE, localWorkerStatusEntry.getStatus() );
		}
		localPeerRemoteWorkerStatus = localPeerUnit.getRemoteWorkerStatus();
		for ( RemoteWorkerInfo remoteWorkerStatusEntry : localPeerRemoteWorkerStatus ) {
			assertEquals( LocalWorkerState.IN_USE, remoteWorkerStatusEntry.getStatus() );
		}
		localPeerLocalConsumerStatus = localPeerUnit.getLocalConsumerStatus();
		assertFalse( localPeerLocalConsumerStatus.isEmpty() );
		assertEquals( 1, localPeerLocalConsumerStatus.size() );

		localPeerRemoteConsumerStatus = localPeerUnit.getRemoteConsumerStatus();
		assertTrue( localPeerRemoteConsumerStatus.isEmpty() );

		remotePeerLocalWorkerStatus1 = remotePeerUnit1.getLocalWorkerStatus();
		for ( WorkerInfo localWorkerStatusEntry : remotePeerLocalWorkerStatus1 ) {
			assertEquals( LocalWorkerState.DONATED, localWorkerStatusEntry.getStatus() );
		}
		assertEquals( 4, localPeerRemoteWorkerStatus.size() );

		remotePeerLocalWorkerStatus2 = remotePeerUnit2.getLocalWorkerStatus();
		for ( WorkerInfo localWorkerStatusEntry : remotePeerLocalWorkerStatus2 ) {
			assertEquals( LocalWorkerState.DONATED, localWorkerStatusEntry.getStatus() );
		}

		brokerUnit.cancelJob( job1 );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( localPeerUnit, workerUnit1,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( localPeerUnit, workerUnit2,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( remotePeerUnit1, workerUnit3,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( remotePeerUnit1, workerUnit4,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( remotePeerUnit2, workerUnit5,
			LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( remotePeerUnit2, workerUnit6,
			LocalWorkerState.IDLE ) );

		localPeerLocalWorkerStatus = localPeerUnit.getLocalWorkerStatus();
		for ( WorkerInfo localWorkerStatusEntry : localPeerLocalWorkerStatus ) {
			assertEquals( LocalWorkerState.IDLE, localWorkerStatusEntry.getStatus() );
		}
		localPeerLocalConsumerStatus = localPeerUnit.getLocalConsumerStatus();
		assertTrue( localPeerLocalConsumerStatus.isEmpty() );
		localPeerRemoteConsumerStatus = localPeerUnit.getRemoteConsumerStatus();
		assertTrue( localPeerRemoteConsumerStatus.isEmpty() );

		localPeerRemoteWorkerStatus = localPeerUnit.getRemoteWorkerStatus();
		assertTrue( localPeerRemoteWorkerStatus.isEmpty() );

		remotePeerLocalWorkerStatus1 = remotePeerUnit1.getLocalWorkerStatus();
		for ( WorkerInfo localWorkerStatusEntry : remotePeerLocalWorkerStatus1 ) {
			assertEquals( LocalWorkerState.IDLE, localWorkerStatusEntry.getStatus() );
		}

		remotePeerLocalWorkerStatus2 = remotePeerUnit2.getLocalWorkerStatus();
		for ( WorkerInfo localWorkerStatusEntry : remotePeerLocalWorkerStatus2 ) {
			assertEquals( LocalWorkerState.IDLE, localWorkerStatusEntry.getStatus() );
		}
	}
}
