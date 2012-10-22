package org.ourgrid.system;

import org.junit.Test;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.system.condition.BrokerHasAPeerInTheState;
import org.ourgrid.system.condition.BrokerJobFinishedCondition;
import org.ourgrid.system.condition.BrokerJobRunningCondition;
import org.ourgrid.system.condition.BrokerJobStateCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

public class StartOutOfOrderSystemTest extends AbstractSystemTest {
	/**
	 * create 1 Peer create 1 Workers start peer set worker start worker worker
	 * must be idle for peer
	 */
	@Test
	public void testStart_Worker_Peer() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		peerUnit.initKeys();
		System.out.println( "===> Peer is running <===" );
//		peerUnit.setWorkers( workerUnit );
		System.out.println( "===> Set worker <===" );
		workerUnit.initKeys();
		System.out.println( "===> Worker is running <===" );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Peer knows the worker <===" );
	}


	/**
	 * create Broker create 2 peers start Broker setPeers start peers Broker must know 2
	 * peers alive
	 */
	@Test
	public void testStart_Broker_2Peers() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		PeerUnit otherPeerUnit = unitManager.buildNewUnit( PEER2_PROPERTIES_FILENAME, PeerUnit.class );
		BrokerUnit brokerUnit = unitManager.buildNewUnit( BrokerUnit.class );

		brokerUnit.initKeys();
		System.out.println( "===> Broker is running <===" );
		peerUnit.addUser(brokerUnit.getLogin());
		otherPeerUnit.addUser(brokerUnit.getLogin());
		System.out.println( "===> Set peers <===" );
		peerUnit.initKeys();
		System.out.println( "===> One peer is running <===" );
		otherPeerUnit.initKeys();
		System.out.println( "===> Other peer is running <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, otherPeerUnit, PeerTestState.UP ) );
		System.out.println( "===> Broker knows that two peers are running <===" );
	}


	/**
	 * create 1 Broker create 1 Peer create 2 Workers start Broker Broker.setPeer( peer )
	 * Broker.addJob( echoJob ) start peer peer.setWorker start workers job is
	 * running
	 */
	@Test
	public void testStart_Broker_Peers_2Workers() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit otherWorkerUnit = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 2);
		unitManager.addUnit(brokerUnit);

		brokerUnit.initKeys();
		System.out.println( "===> Broker is running <===" );
		peerUnit.addUser(brokerUnit.getLogin());
		System.out.println( "===> Set peers <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( 2, Integer.MAX_VALUE ) );
		conditionExpecter
			.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobid, GridProcessState.UNSTARTED ) );

		peerUnit.initKeys();
		System.out.println( "===> Peer is running <===" );

//		peerUnit.setWorkers( workerUnit, otherWorkerUnit );
		System.out.println( "===> Set workers <===" );

		workerUnit.initKeys();
		otherWorkerUnit.initKeys();
		System.out.println( "===> Workers are running <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		System.out.println( "===> Job is running <===" );
	}


	/**
	 * Similar to previous, but adds a job before setting the peer. create 1 Broker
	 * create 1 Peer create 2 Workers start Broker Broker.addJob( echoJob ) Broker.setPeer(
	 * peer ) start peer peer.setWorker start workers job is running
	 */
	@Test
	public void testStart_Broker_Peers_2Workers_case2() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit otherWorkerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 2);
		unitManager.addUnit(brokerUnit);

		brokerUnit.initKeys();
		System.out.println( "===> Broker is running <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildASleepJob( 2, Integer.MAX_VALUE ) );
		conditionExpecter
			.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobid, GridProcessState.UNSTARTED ) );

		peerUnit.addUser(brokerUnit.getLogin());
		System.out.println( "===> Set peers <===" );

		peerUnit.initKeys();
		System.out.println( "===> Peer is running <===" );

//		peerUnit.setWorkers( workerUnit, otherWorkerUnit );
		System.out.println( "===> Set workers <===" );

		workerUnit.initKeys();
		otherWorkerUnit.initKeys();
		System.out.println( "===> Workers are running <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );
		System.out.println( "===> Job is running <===" );
	}


	/**
	 * create 1 Broker create 1 Peer create 1 Workers start Broker Broker.setPeer( peer )
	 * Broker.addJob( echoJob ) start peer peer.setWorker start workers job finished
	 */
	@Test
	public void testStart_Broker_Peers_Workers() throws Exception {

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 2);
		unitManager.addUnit(brokerUnit);

		brokerUnit.initKeys();
		System.out.println( "===> Broker is running <===" );
		peerUnit.addUser(brokerUnit.getLogin());
		System.out.println( "===> Set peers <===" );

		int jobid = brokerUnit.addJob( UnitUtil.buildAnEchoJob( "ok" ) );
		conditionExpecter
			.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobid, GridProcessState.UNSTARTED ) );

		peerUnit.initKeys();
		System.out.println( "===> Peer is running <===" );

//		peerUnit.setWorkers( workerUnit );
		System.out.println( "===> Set workers <===" );

		workerUnit.initKeys();
		System.out.println( "===> Workers are running <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobid ) );
		System.out.println( "===> Job is running <===" );
	}
}
