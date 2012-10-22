/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.acceptance.peer;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ015")
public class Req_015_Test extends PeerAcceptanceTestCase {

	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private PeerAcceptanceUtil peerAcceptanceUtil = new PeerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    
	public static final String COMM_FILE_PATH = "req_015"+File.separator;
	
	private PeerComponent peerComponent;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
		PeerAcceptanceUtil.setUp();
	}
	
	/**
	 * Verifies if the peer give the worker back to Broker, because the 
	 * request is not paused.
	 */
	@ReqTest(test="AT-015.1", reqs="REQ015")
	@Test public void test_AT_015_1_BrokerDisposesLocalWorker() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Worker login
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		//Worker login
		DeploymentID wmOID = req_019_Util.createWorkerManagementDeploymentID(workerPublicKey, workerSpecA);
		req_010_Util.workerLogin(peerComponent, workerSpecA, wmOID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, wmOID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubkey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
		//Request a worker for the logged user
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
	    WorkerAllocation allocationA = new WorkerAllocation(wmOID);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), requestSpec1, allocationA);
		
		//Change worker status to ALLOCATED FOR BROKER
		Worker worker = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpcOID, wmOID, workerSpecA, requestSpec1).getObject();
		
		//The user disposes the worker without pausing the request - expect to allocate the worker for him again
		allocationA.addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpcOID).addWinnerConsumer(lwpcOID);
		
		req_015_Util.localConsumerDisposesLocalWorker(peerComponent, worker, allocationA);
		
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(wmOID.getServiceID()));
		
		//Verify if the client was marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.CONSUMING);
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfo = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		List<WorkerInfo> workersInfo = AcceptanceTestUtil.createList(workerInfo);
		req_036_Util.getLocalWorkersStatus(workersInfo);
	}

	/**
	 * Verifies if the peer give the remote worker back to Broker, because the 
	 * request is not paused.
	 */
	@ReqTest(test="AT-015.2", reqs="REQ015")
	@Test public void test_AT_015_2_BrokerDisposesRemoteWorker() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magodosnos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "os = windows AND mem > 256", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		TestStub rmwStub = 
			req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec, "workerPK", brokerPubKey);
		
		DeploymentID rmwOID = rmwStub.getDeploymentID();
		
		ObjectDeployment rmwMonitorOD = peerAcceptanceUtil.getRemoteWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, rmwOID.getServiceID(), rmwMonitorOD.getDeploymentID()));
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(peerComponent, lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		TestStub workerStub = req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rmwStub.getDeploymentID(), workerSpec, requestSpec1);		
		
		//The user disposes the worker without pausing the request - expect to allocate the worker for him again
		WorkerAllocation allocation = new WorkerAllocation(rmwOID);
		allocation.addLoserConsumer(lwpcOID).addWinnerConsumer(lwpcOID).addLoserRequestSpec(requestSpec1);
		req_015_Util.localDisposeRemoteWorker(peerComponent, workerStub, allocation, false);
		
		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, rmwOID.getServiceID(), rmwMonitorOD.getDeploymentID()));
	}
	
	/**
	 * Verifies if the peer command the worker to stop working, after be 
	 * diposed by a remote peer.
	 */
	@ReqTest(test="AT-015.3", reqs="REQ015")
	@Test public void test_AT_015_3_RemotePeerDisposesLocalWorker() throws Exception {
		//PeerAcceptanceUtil.recreateSchema();
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"015_blank.xml");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		
		//Worker login
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		//Worker login
		DeploymentID wmOID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerPublicKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, wmOID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, wmOID);

		
		//Request a worker for the remote client
		WorkerAllocation allocationA = new WorkerAllocation(wmOID);
		
		DeploymentID remoteClientOID = new DeploymentID(new ContainerID("remoteUser", "server", PeerConstants.MODULE_NAME, "peerPublicKey"), 
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(peerComponent, remoteClientOID, requestSpec, 0, allocationA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnRemoteClient(remoteClientOID.getServiceID()));
		
		//Change worker status to ALLOCATED FOR PEER
		RemoteWorkerManagement rwm = 
			req_025_Util.changeWorkerStatusToAllocatedForPeer(peerComponent, rwpc, wmOID, workerSpecA, remoteClientOID);
		
		//The remote peer disposes the worker - expect to command the worker to stop working
		req_015_Util.remoteDisposeLocalWorker(peerComponent, remoteClientOID, rwm, wmOID);
		assertFalse(AcceptanceTestUtil.isInterested(peerComponent, wmOID.getServiceID(), remoteClientOID));
		
		//Verify if the worker A was marked as IDLE
		WorkerInfo workerInfo = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		List<WorkerInfo> workersInfo = AcceptanceTestUtil.createList(workerInfo);
		req_036_Util.getLocalWorkersStatus(workersInfo);
	}
	
	/**
	 * Verifies if the peer ignores a worker dispose, on these scenarios:
	 *
	 * The message sender is an unknown Broker;
	 * The message sender is a not logged Broker;
	 * For a logged Broker:
	 *	o The worker is null;
	 *	o The local worker is unknown;
	 *	o The local worker is not recovered;
	 *	o The local worker is owner;
	 *	o The local worker is idle;
	 *	o The local worker is allocated for other Broker;
	 *	o The local worker is allocated for a remote Peer;
	 *	o The remote worker is allocated for other Broker;
	 * The message sender is an unknown remote Peer;
	 * For a valid remote Peer:
	 *	o The local worker is null;
	 *	o The local worker is unknown;
	 *	o The local worker is not recovered;
	 *	o The local worker is idle;
	 *	o The local worker is owner;
	 *	o The local worker is allocated for a Broker;
	 *	o The local worker is allocated for other remote Peer;
	 *	o The worker is remote.
	 * @throws Exception
	 */
	@ReqTest(test="AT-015.4", reqs="REQ015")
	@Test public void test_AT_015_4_InputValidation() throws Exception {
		//PeerAcceptanceUtil.recreateSchema();
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"015_blank.xml");
		
		//Create users accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		XMPPAccount user3 = req_101_Util.createLocalUser("user011_3", "server011", "011011");
		XMPPAccount user4 = req_101_Util.createLocalUser("user011_4", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient3 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID3 = new DeploymentID(new ContainerID("peerClient3", "peerClientServer3", "broker"), "broker");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID3, peerControlClient3, PeerControlClient.class);

		try {
			peerControl.addUser(peerControlClient3, user3.getUsername() + "@" + user3.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient4 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID4 = new DeploymentID(new ContainerID("peerClient4", "peerClientServer4", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID4, peerControlClient4, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient4, user4.getUsername() + "@" + user4.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
	   
		//A unknown consumer disposes a worker - expect to log the warn
		DeploymentID workerID = 
			createWorkerDeploymentID(workerAcceptanceUtil.createWorkerSpec("unknown", "unknown"), "workerPubKey");
		Worker worker = getMock(NOT_NICE, Worker.class);
		peerAcceptanceUtil.createStub(worker, Worker.class, workerID);
		
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		String unknownPublicKey = "unknownPublicKey"; 
		
		loggerMock.warn(
				"Ignoring an unknown consumer which disposed a worker. Consumer public key: " + unknownPublicKey);
		replayActiveMocks();
		
		DeploymentID brokerID = new DeploymentID(new ContainerID("brokerUser", "brokerServer", "broker", unknownPublicKey), "broker");
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, brokerID);
		lwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Login with a valid user and fail him
		String broker1PubKey = "broker1Pubkey";
		DeploymentID lwpc1OID = req_108_Util.login(peerComponent, user1, broker1PubKey);
		loggerMock.info("The local consumer [" + lwpc1OID.getContainerID() + "] with publicKey [" + broker1PubKey + "] has failed. Canceling his requests.");
		replayActiveMocks();
		
		peerAcceptanceUtil.getClientMonitor().doNotifyFailure((LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID), 
				lwpc1OID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		//A not logged consumer disposes a worker - expect to log the warn
		loggerMock.warn("Ignoring a not logged consumer which disposed a worker. Consumer public key: " + 
				broker1PubKey); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		lwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Login again with the valid user
		lwpc1OID = req_108_Util.login(peerComponent, user1, broker1PubKey);
		
		//A consumer disposes a null worker - expect to log the warn
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed a null worker. This disposal was ignored."); 
		replayActiveMocks();
		
		lwp.disposeWorker(null);
		verifyActiveMocks();
		resetActiveMocks();
		
		//A consumer disposes a unknown worker - expect to log the warn
		workerID = 
			createWorkerDeploymentID(workerAcceptanceUtil.createWorkerSpec("unknown", "unknown"), "workerPubKey");
		peerAcceptanceUtil.createStub(worker, Worker.class, workerID);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed an unknown worker. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		lwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_MEM, "32");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S1");
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createWorkerSpec("U6", "S1");
		
		
		//A consumer disposes a not l worker - expect to log the warn
		Worker workerA = getMock(NOT_NICE, Worker.class);
		String workerAPublicKey = "workerAPublicKey";
		
		workerID = createWorkerDeploymentID(workerSpecA, workerAPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, workerA, Worker.class);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed an unknown worker. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		
		lwp.disposeWorker(workerID.getServiceID());
		resetActiveMocks();
		
		//Worker login
		DeploymentID wmAOID = req_019_Util.createWorkerManagementDeploymentID(workerAPublicKey, workerSpecA);
		req_010_Util.workerLogin(peerComponent, workerSpecA, wmAOID);
		
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + wmAOID.getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();

		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		
		lwp.disposeWorker(wmAOID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, wmAOID);
		
		//A consumer disposes an idle worker - expect to log the warn
		workerID = createWorkerDeploymentID(workerSpecA, "workerAPublicKey");
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, workerA, Worker.class);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + wmAOID.getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		
		lwp.disposeWorker(wmAOID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Allocate the worker for other user
		DeploymentID lwpc2OID = req_108_Util.login(peerComponent, user2, "broker2Pubkey");
		
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
				
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(wmAOID);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpc2OID, lwpc2), requestSpec1, allocationA);
		req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpc2OID, wmAOID, workerSpecA, 
				requestSpec1);
		
		//A consumer disposes a worker allocated for other consumer - expect to log the warn
		workerID = createWorkerDeploymentID(workerSpecA, "workerAPublicKey");
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, workerA, Worker.class);
		EasyMock.reset(loggerMock);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + wmAOID.getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		lwp.disposeWorker(wmAOID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Allocate a worker for a remote peer
		String workerBPublicKey = "workerBPublicKey";
		DeploymentID wmBOID = req_019_Util.createWorkerManagementDeploymentID(workerBPublicKey, workerSpecB);
		req_010_Util.workerLogin(peerComponent, workerSpecB, wmBOID);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, wmBOID);
		WorkerAllocation allocationB = new WorkerAllocation(wmBOID);
		String remoteClientPublicKey = "remoteClientPublicKey";
		
		DeploymentID remoteClientID = new DeploymentID(new ContainerID("remoteClient", "peerServer", 
				PeerConstants.MODULE_NAME, remoteClientPublicKey),"rwp");
		
		remoteClientID.setPublicKey(remoteClientPublicKey);
		
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(peerComponent, remoteClientID, requestSpec2, 0, allocationB);
		req_025_Util.changeWorkerStatusToAllocatedForPeer(peerComponent, rwpc, wmBOID, workerSpecB, remoteClientID);
		
		//A consumer disposes a worker allocated for a remote peer - expect to log the warn 
		Worker workerB = EasyMock.createMock(Worker.class);
		workerID = createWorkerDeploymentID(workerSpecB, workerBPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, workerB, Worker.class);
		EasyMock.reset(loggerMock);
		peerComponent.setLogger(loggerMock);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + wmBOID.getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		lwp.disposeWorker(wmBOID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String broker3PublicKey = "broker3PublicKey";
		DeploymentID lwpc3OID = req_108_Util.login(peerComponent, user3, broker3PublicKey);
		
		LocalWorkerProviderClient lwpc3 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc3OID);
		
		RequestSpecification remoteRequestSpec = new RequestSpecification(0, new JobSpecification("label"), 3, "mem > 256", 1, 0, 0);	
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpc3OID, lwpc3), remoteRequestSpec);
		
		WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createWorkerSpec("U1", "S2");
		remoteWorkerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		
		RemoteWorkerProvider rwp = EasyMock.createMock(RemoteWorkerProvider.class);
		String rwpPublicKey = "rwpPublicKey";
		
		DeploymentID rwpOID = new DeploymentID(new ContainerID("rwpUser", "rwpServer", PeerConstants.MODULE_NAME, rwpPublicKey),
				PeerConstants.REMOTE_WORKER_PROVIDER);
		
		replayActiveMocks();
		
		//Remote worker provider client receive a remote worker
		AcceptanceTestUtil.publishTestObject(peerComponent, rwpOID, rwp, RemoteWorkerProvider.class);
		DeploymentID rwmOID = req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpOID,
				remoteWorkerSpec, "remoteWorkerPublicKey", broker3PublicKey).getDeploymentID();
		verifyActiveMocks();
		resetActiveMocks();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpc3OD = new ObjectDeployment(peerComponent, lwpc3OID, AcceptanceTestUtil.getBoundObject(lwpc3OID));
		
		TestStub workerStub = req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpc3OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, remoteWorkerSpec, 
				remoteRequestSpec);
		
		//A consumer disposes a remote worker allocated for other consumer - expect to log the warn
		AcceptanceTestUtil.publishTestObject(peerComponent, workerStub.getDeploymentID(), workerStub.getObject(), Worker.class, false);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + workerStub.getDeploymentID().getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, broker1PubKey);
		lwp.disposeWorker(workerStub.getDeploymentID().getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//A unknown remote consumer disposes a worker - expect to log the warn
		RemoteWorkerManagement remoteWorkerC = getMock(NOT_NICE, RemoteWorkerManagement.class);
		String workerCPublicKey = "workerCPublicKey"; 
		workerID = createWorkerDeploymentID(workerSpecC, workerCPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, remoteWorkerC, RemoteWorkerManagement.class);
		loggerMock.warn("Ignoring an unknown remote consumer which disposed a worker. Remote consumer public key: " + 
				unknownPublicKey); 
		replayActiveMocks();
		
		rwp = peerAcceptanceUtil.getRemoteWorkerProviderProxy();
		ObjectDeployment rwpOD = peerAcceptanceUtil.getRemoteWorkerProviderDeployment();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, unknownPublicKey);
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Worker login
		DeploymentID wmCOID = req_019_Util.createWorkerManagementDeploymentID(workerCPublicKey, workerSpecC);
		req_010_Util.workerLogin(peerComponent, workerSpecC, wmCOID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(peerComponent, workerSpecC, wmCOID, dsID);
		
		//Request a worker for the remote client
		RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), 4, "", 1, 0, 0);
		String rwpcPubKey = "rwpcPubKey";
		DeploymentID rwpcOID = new DeploymentID(new ContainerID("rwpcUser", "rwpcServer", PeerConstants.MODULE_NAME, rwpcPubKey),
				"RWPC");
		
		WorkerAllocation allocationC = new WorkerAllocation(wmCOID);
		rwpc = req_011_Util.requestForRemoteClient(peerComponent, rwpcOID, 
				requestSpec4, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(peerComponent, rwpc, wmCOID, workerSpecC, rwpcOID);
		
		//A remote consumer disposes a null worker - expect to log the warn
		resetActiveMocks();
		peerComponent.setLogger(loggerMock);
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed a null worker. This dispose was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpOD, rwpcPubKey);
		rwp.disposeWorker(null);
		verifyActiveMocks();
		resetActiveMocks();
		
		//A remote consumer disposes a unknown worker - expect to log the warn
		RemoteWorkerManagement remoteWorkerE = getMock(NOT_NICE, RemoteWorkerManagement.class);
		String workerEPublicKey = "workerEPublicKey"; 
		workerID = createWorkerDeploymentID(
				workerAcceptanceUtil.createWorkerSpec("unknown", "unknown"), workerEPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, remoteWorkerE, RemoteWorkerManagement.class);
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + 
				"] disposed a unknown worker. This disposal was ignored."); 
		replayActiveMocks();
		
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//A remote consumer disposes a not recovered worker - expect to log the warn
		RemoteWorkerManagement remoteWorkerD = getMock(NOT_NICE, RemoteWorkerManagement.class);
		String workerDPublicKey = "workerDPublicKey"; 
		workerID = createWorkerDeploymentID(workerSpecD, workerDPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, remoteWorkerD, RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed a unknown worker. This disposal was ignored."); 
		replayActiveMocks();
		
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Worker login
		DeploymentID wmDOID = req_019_Util.createWorkerManagementDeploymentID(workerDPublicKey, workerSpecD);;
		req_010_Util.workerLogin(peerComponent, workerSpecD, wmDOID);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID()
				+ "] disposed the worker [" + workerID.getServiceID() + "], " +
				"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpOD, rwpcPubKey);
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(peerComponent, workerSpecD, wmDOID, dsID);
		
		//A remote consumer disposes an ownered worker - expect to log the warn
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID,
				remoteWorkerD, RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID()
				+ "] disposed the worker [" + workerID.getServiceID() + "], " +
				"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpOD, rwpcPubKey);
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Worker login
		String workerFPublicKey = "workerFPublicKey"; 
		DeploymentID wmFOID = req_019_Util.createWorkerManagementDeploymentID(workerFPublicKey, workerSpecF);
		req_010_Util.workerLogin(peerComponent, workerSpecF, wmFOID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(peerComponent, workerSpecF, wmFOID, dsID);
		
		//Allocate the worker for a local user
		String broker4PubKey = "broker4PubKey";
		DeploymentID lwpc4OID = req_108_Util.login(peerComponent, user4, broker4PubKey);
		LocalWorkerProviderClient lwpc4 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc4OID);
		
		WorkerAllocation allocationF = new WorkerAllocation(wmFOID);
		
		RequestSpecification requestSpec5 = new RequestSpecification(0, new JobSpecification("label"), 5, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpc4OID, lwpc4), requestSpec5, allocationF);
		req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpc4OID, wmFOID, workerSpecF, 
				requestSpec5);
		
		//A remote consumer disposes a worker allocated for a local consumer - expect to log the warn
		RemoteWorkerManagement remoteWorkerF = getMock(NOT_NICE, RemoteWorkerManagement.class);
		
		workerID = createWorkerDeploymentID(workerSpecF, workerFPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID,
				remoteWorkerF, RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed the worker [" + workerID.getServiceID() + "], " +
		"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpOD, rwpcPubKey);
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//A remote consumer disposes a worker allocated for other remote consumer - expect to log the warn
		RemoteWorkerManagement remoteWorkerB = getMock(NOT_NICE, RemoteWorkerManagement.class);
		
		workerID = createWorkerDeploymentID(workerSpecB, workerBPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID,
				remoteWorkerB, RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed the worker [" + workerID.getServiceID() + "], " +
		"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//A remote consumer disposes a remote worker - expect to log the warn
		RemoteWorkerManagement remoteWorkerR = getMock(NOT_NICE, RemoteWorkerManagement.class);
		
		workerID = createWorkerDeploymentID(workerAcceptanceUtil.createWorkerSpec("unknown", "unknown"), "remoteWorkerPublicKey");
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, remoteWorkerR,
				RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed the worker [" + workerID.getServiceID() + "], " +
		"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
	}

	/**
	 * Verifies if the peer give the worker back to Broker, because the 
	 * request is not paused.
	 */
	@ReqTest(test="AT-015.1", reqs="REQ015")
	@Category(JDLCompliantTest.class) @Test public void test_AT_015_1_BrokerDisposesLocalWorkerWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Worker login
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		DeploymentID wmDID = req_019_Util.createWorkerManagementDeploymentID(workerPublicKey, workerSpecA);
		req_010_Util.workerLogin(peerComponent, workerSpecA, wmDID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, wmDID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubkey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
		//Request a worker for the logged user
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
	    WorkerAllocation allocationA = new WorkerAllocation(wmDID);
	    req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), requestSpec1, allocationA);
		
		//Change worker status to ALLOCATED FOR BROKER
		Worker worker = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpcOID, wmDID, workerSpecA, requestSpec1).getObject();
		
		//The user disposes the worker without pausing the request - expect to allocate the worker for him again
		allocationA.addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpcOID).addWinnerConsumer(lwpcOID);
		
		req_015_Util.localConsumerDisposesLocalWorker(peerComponent, worker, allocationA);
		
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(wmDID.getServiceID()));
		
		//Verify if the client was marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.CONSUMING);
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfo = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		List<WorkerInfo> workersInfo = AcceptanceTestUtil.createList(workerInfo);
		req_036_Util.getLocalWorkersStatus(workersInfo);
	}

	/**
	 * Verifies if the peer give the remote worker back to Broker, because the 
	 * request is not paused.
	 */
	@ReqTest(test="AT-015.2", reqs="REQ015")
	@Category(JDLCompliantTest.class) @Test public void test_AT_015_2_BrokerDisposesRemoteWorkerWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magodosnos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "[Requirements=(other.MainMemory > 256 && other.OS == \"windows\");Rank=0]", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", 512, "windows");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		TestStub rmwStub = 
			req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec, "workerPK", brokerPubKey);
		
		DeploymentID rmwOID = rmwStub.getDeploymentID();
		
		ObjectDeployment rmwMonitorOD = peerAcceptanceUtil.getRemoteWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, rmwOID.getServiceID(), rmwMonitorOD.getDeploymentID()));
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(peerComponent, lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		TestStub workerStub = req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rmwStub.getDeploymentID(), workerSpec, requestSpec1);		
		
		//The user disposes the worker without pausing the request - expect to allocate the worker for him again
		WorkerAllocation allocation = new WorkerAllocation(rmwOID);
		allocation.addLoserConsumer(lwpcOID).addWinnerConsumer(lwpcOID).addLoserRequestSpec(requestSpec1);
		req_015_Util.localDisposeRemoteWorker(peerComponent, workerStub, allocation, false);
		
		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, rmwOID.getServiceID(), rmwMonitorOD.getDeploymentID()));
	}

	/**
	 * Verifies if the peer command the worker to stop working, after be 
	 * diposed by a remote peer.
	 */
	@ReqTest(test="AT-015.3", reqs="REQ015")
	@Category(JDLCompliantTest.class) @Test public void test_AT_015_3_RemotePeerDisposesLocalWorkerWithJDL() throws Exception {
		//PeerAcceptanceUtil.recreateSchema();
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"015_blank.xml");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		
		//Worker login
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		DeploymentID wmOID = req_019_Util.createWorkerManagementDeploymentID(workerPublicKey, workerSpecA);
		req_010_Util.workerLogin(peerComponent, workerSpecA, wmOID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, wmOID);
		
		//Request a worker for the remote client
		WorkerAllocation allocationA = new WorkerAllocation(wmOID);
		
		DeploymentID remoteClientOID = new DeploymentID(new ContainerID("remoteUser", "server", PeerConstants.MODULE_NAME, "peerPublicKey"), 
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(peerComponent, remoteClientOID, requestSpec, 0, allocationA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnRemoteClient(remoteClientOID.getServiceID()));
		
		//Change worker status to ALLOCATED FOR PEER
		RemoteWorkerManagement rwm = 
			req_025_Util.changeWorkerStatusToAllocatedForPeer(peerComponent, rwpc, wmOID, workerSpecA, remoteClientOID);
		
		//The remote peer disposes the worker - expect to command the worker to stop working
		req_015_Util.remoteDisposeLocalWorker(peerComponent, remoteClientOID, rwm, wmOID);
		assertFalse(AcceptanceTestUtil.isInterested(peerComponent, wmOID.getServiceID(), remoteClientOID));
		
		//Verify if the worker A was marked as IDLE
		WorkerInfo workerInfo = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		List<WorkerInfo> workersInfo = AcceptanceTestUtil.createList(workerInfo);
		req_036_Util.getLocalWorkersStatus(workersInfo);
	}

	/**
	 * Verifies if the peer ignores a worker dispose, on these scenarios:
	 *
	 * The message sender is an unknown Broker;
	 * The message sender is a not logged Broker;
	 * For a logged Broker:
	 *	o The worker is null;
	 *	o The local worker is unknown;
	 *	o The local worker is not recovered;
	 *	o The local worker is owner;
	 *	o The local worker is idle;
	 *	o The local worker is allocated for other Broker;
	 *	o The local worker is allocated for a remote Peer;
	 *	o The remote worker is allocated for other Broker;
	 * The message sender is an unknown remote Peer;
	 * For a valid remote Peer:
	 *	o The local worker is null;
	 *	o The local worker is unknown;
	 *	o The local worker is not recovered;
	 *	o The local worker is idle;
	 *	o The local worker is owner;
	 *	o The local worker is allocated for a Broker;
	 *	o The local worker is allocated for other remote Peer;
	 *	o The worker is remote.
	 * @throws Exception
	 */
	@ReqTest(test="AT-015.4", reqs="REQ015")
	@Category(JDLCompliantTest.class) @Test public void test_AT_015_4_InputValidationWithJDL() throws Exception {
		//PeerAcceptanceUtil.recreateSchema();
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"015_blank.xml");
		
		//Create users accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		XMPPAccount user3 = req_101_Util.createLocalUser("user011_3", "server011", "011011");
		XMPPAccount user4 = req_101_Util.createLocalUser("user011_4", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient3 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID3 = new DeploymentID(new ContainerID("peerClient3", "peerClientServer3", "broker"), "broker");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID3, peerControlClient3, PeerControlClient.class);
	
		try {
			peerControl.addUser(peerControlClient3, user3.getUsername() + "@" + user3.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient4 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID4 = new DeploymentID(new ContainerID("peerClient4", "peerClientServer4", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID4, peerControlClient4, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient4, user4.getUsername() + "@" + user4.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
	
		//A unknown consumer disposes a worker - expect to log the warn
		DeploymentID workerID = 
			createWorkerDeploymentID(workerAcceptanceUtil.createClassAdWorkerSpec("unknown", "unknown", null, null), "workerPubKey");
		Worker worker = getMock(NOT_NICE, Worker.class);
		peerAcceptanceUtil.createStub(worker, Worker.class, workerID);
		
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		String unknownPublicKey = "unknownPublicKey"; 
		
		loggerMock.warn(
				"Ignoring an unknown consumer which disposed a worker. Consumer public key: " + unknownPublicKey);
		replayActiveMocks();
		
		DeploymentID brokerID = new DeploymentID(new ContainerID("brokerUser", "brokerServer", "broker", unknownPublicKey), "broker");
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, brokerID);
		lwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Login with a valid user and fail him
		String broker1PubKey = "broker1Pubkey";
		DeploymentID lwpc1OID = req_108_Util.login(peerComponent, user1, broker1PubKey);
		loggerMock.info("The local consumer [" + lwpc1OID.getContainerID() + "] with publicKey [" + broker1PubKey + "] has failed. Canceling his requests.");
		replayActiveMocks();
		
		peerAcceptanceUtil.getClientMonitor().doNotifyFailure((LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID), 
				lwpc1OID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		//A not logged consumer disposes a worker - expect to log the warn
		loggerMock.warn("Ignoring a not logged consumer which disposed a worker. Consumer public key: " + 
				broker1PubKey); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		lwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Login again with the valid user
		lwpc1OID = req_108_Util.login(peerComponent, user1, broker1PubKey);
		
		//A consumer disposes a null worker - expect to log the warn
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed a null worker. This disposal was ignored."); 
		replayActiveMocks();
		
		lwp.disposeWorker(null);
		verifyActiveMocks();
		resetActiveMocks();
		
		//A consumer disposes a unknown worker - expect to log the warn
		workerID = 
			createWorkerDeploymentID(workerAcceptanceUtil.createClassAdWorkerSpec("unknown", "unknown", null, null), "workerPubKey");
		peerAcceptanceUtil.createStub(worker, Worker.class, workerID);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed an unknown worker. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		lwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", 512, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", 32, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S1", null, null);
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createClassAdWorkerSpec("U6", "S1", null, null);
		
		//A consumer disposes a not recovered worker - expect to log the warn
		Worker workerA = getMock(NOT_NICE, Worker.class);
		String workerAPublicKey = "workerAPublicKey";
		
		workerID = createWorkerDeploymentID(workerSpecA, workerAPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, workerA, Worker.class);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed an unknown worker. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		
		lwp.disposeWorker(workerID.getServiceID());
		resetActiveMocks();
		
		//Worker login
		DeploymentID wmADID = req_019_Util.createWorkerManagementDeploymentID(workerAPublicKey, workerSpecA);
		req_010_Util.workerLogin(peerComponent, workerSpecA, wmADID);
		
		//A consumer disposes an ownered worker - expect to log the warn
		//workerID = createWorkerDeploymentID(workerSpecA, "workerAPublicKey");
		//AcceptanceTestUtil.publishTestObject(peerComponent, workerID, workerA);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + wmADID.getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
	
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		
		lwp.disposeWorker(wmADID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, wmADID);
		
		//A consumer disposes an idle worker - expect to log the warn
		workerID = createWorkerDeploymentID(workerSpecA, "workerAPublicKey");
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, workerA, Worker.class);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + wmADID.getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		
		lwp.disposeWorker(wmADID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Allocate the worker for other user
		DeploymentID lwpc2OID = req_108_Util.login(peerComponent, user2, "broker2Pubkey");
		
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
				
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpc2OID, lwpc2), requestSpec1, allocationA);
		req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpc2OID, wmADID, workerSpecA, 
				requestSpec1);
		
		//A consumer disposes a worker allocated for other consumer - expect to log the warn
		workerID = createWorkerDeploymentID(workerSpecA, "workerAPublicKey");
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, workerA, Worker.class);
		EasyMock.reset(loggerMock);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + wmADID.getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		lwp.disposeWorker(wmADID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Allocate a worker for a remote peer
		String workerBPublicKey = "workerBPublicKey";
		DeploymentID wmBDID = req_019_Util.createWorkerManagementDeploymentID(workerBPublicKey, workerSpecB);
		req_010_Util.workerLogin(peerComponent, workerSpecB, wmBDID);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, wmBDID);
		WorkerAllocation allocationB = new WorkerAllocation(wmBDID);
		String remoteClientPublicKey = "remoteClientPublicKey";
		
		DeploymentID remoteClientID = new DeploymentID(new ContainerID("remoteClient", "peerServer", 
				PeerConstants.MODULE_NAME, remoteClientPublicKey),"rwp");
		
		remoteClientID.setPublicKey(remoteClientPublicKey);
		
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(peerComponent, remoteClientID, requestSpec2, 0, allocationB);
		req_025_Util.changeWorkerStatusToAllocatedForPeer(peerComponent, rwpc, wmBDID, workerSpecB, remoteClientID);
		
		//A consumer disposes a worker allocated for a remote peer - expect to log the warn 
		Worker workerB = EasyMock.createMock(Worker.class);
		workerID = createWorkerDeploymentID(workerSpecB, workerBPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, workerB, Worker.class);
		EasyMock.reset(loggerMock);
		peerComponent.setLogger(loggerMock);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + wmBDID.getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, lwpc1OID);
		lwp.disposeWorker(wmBDID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String broker3PublicKey = "broker3PublicKey";
		DeploymentID lwpc3OID = req_108_Util.login(peerComponent, user3, broker3PublicKey);
		
		LocalWorkerProviderClient lwpc3 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc3OID);
		
		RequestSpecification remoteRequestSpec = new RequestSpecification(0, new JobSpecification("label"), 3, PeerAcceptanceTestCase.buildRequirements(">", 256, null, null), 1, 0, 0);	
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpc3OID, lwpc3), remoteRequestSpec);
		
		WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S2", 512, null);
		
		RemoteWorkerProvider rwp = EasyMock.createMock(RemoteWorkerProvider.class);
		String rwpPublicKey = "rwpPublicKey";
		
		DeploymentID rwpDID = new DeploymentID(new ContainerID("rwpUser", "rwpServer", PeerConstants.MODULE_NAME, rwpPublicKey),
				PeerConstants.REMOTE_WORKER_PROVIDER);
		
		replayActiveMocks();
		
		//Remote worker provider client receive a remote worker
		AcceptanceTestUtil.publishTestObject(peerComponent, rwpDID, rwp, RemoteWorkerProvider.class);
		DeploymentID rwmDID = req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpDID,
				remoteWorkerSpec, "remoteWorkerPublicKey", broker3PublicKey).getDeploymentID();
		verifyActiveMocks();
		resetActiveMocks();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpc3OD = new ObjectDeployment(peerComponent, lwpc3OID, AcceptanceTestUtil.getBoundObject(lwpc3OID));
		
		TestStub workerStub = req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpc3OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmDID, remoteWorkerSpec, 
				remoteRequestSpec);
		
		//A consumer disposes a remote worker allocated for other consumer - expect to log the warn
		AcceptanceTestUtil.publishTestObject(peerComponent, workerStub.getDeploymentID(), workerStub.getObject(), Worker.class, false);
		loggerMock.warn("The consumer [" + lwpc1OID.getServiceID() + "] disposed the worker [" + workerStub.getDeploymentID().getServiceID() + 
				"], that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, broker1PubKey);
		lwp.disposeWorker(workerStub.getDeploymentID().getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//A unknown remote consumer disposes a worker - expect to log the warn
		RemoteWorkerManagement remoteWorkerC = getMock(NOT_NICE, RemoteWorkerManagement.class);
		String workerCPublicKey = "workerCPublicKey"; 
		workerID = createWorkerDeploymentID(workerSpecC, workerCPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, remoteWorkerC, RemoteWorkerManagement.class);
		loggerMock.warn("Ignoring an unknown remote consumer which disposed a worker. Remote consumer public key: " + 
				unknownPublicKey); 
		replayActiveMocks();
		
		rwp = peerAcceptanceUtil.getRemoteWorkerProviderProxy();
		ObjectDeployment rwpOD = peerAcceptanceUtil.getRemoteWorkerProviderDeployment();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, lwpOD, unknownPublicKey);
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Worker login
		DeploymentID wmCDID = req_019_Util.createWorkerManagementDeploymentID(workerCPublicKey, workerSpecC);
		req_010_Util.workerLogin(peerComponent, workerSpecC, wmCDID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(peerComponent, workerSpecC, wmCDID, dsID);
		
		//Request a worker for the remote client
		RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), 4, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		String rwpcPubKey = "rwpcPubKey";
		DeploymentID rwpcOID = new DeploymentID(new ContainerID("rwpcUser", "rwpcServer", PeerConstants.MODULE_NAME, rwpcPubKey),
				"RWPC");
		
		WorkerAllocation allocationC = new WorkerAllocation(wmCDID);
		rwpc = req_011_Util.requestForRemoteClient(peerComponent, rwpcOID, 
				requestSpec4, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(peerComponent, rwpc, wmCDID, workerSpecC, rwpcOID);
		
		//A remote consumer disposes a null worker - expect to log the warn
		resetActiveMocks();
		peerComponent.setLogger(loggerMock);
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed a null worker. This dispose was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpOD, rwpcPubKey);
		rwp.disposeWorker(null);
		verifyActiveMocks();
		resetActiveMocks();
		
		//A remote consumer disposes a unknown worker - expect to log the warn
		RemoteWorkerManagement remoteWorkerE = getMock(NOT_NICE, RemoteWorkerManagement.class);
		String workerEPublicKey = "workerEPublicKey"; 
		workerID = createWorkerDeploymentID(
				workerAcceptanceUtil.createClassAdWorkerSpec("unknown", "unknown", null, null), workerEPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, remoteWorkerE, RemoteWorkerManagement.class);
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + 
				"] disposed a unknown worker. This disposal was ignored."); 
		replayActiveMocks();
		
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//A remote consumer disposes a not recovered worker - expect to log the warn
		RemoteWorkerManagement remoteWorkerD = getMock(NOT_NICE, RemoteWorkerManagement.class);
		String workerDPublicKey = "workerDPublicKey"; 
		workerID = createWorkerDeploymentID(workerSpecD, workerDPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, remoteWorkerD, RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed a unknown worker. This disposal was ignored."); 
		replayActiveMocks();
		
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Worker login
		DeploymentID wmDDID = req_019_Util.createWorkerManagementDeploymentID(workerDPublicKey, workerSpecD);
		req_010_Util.workerLogin(peerComponent, workerSpecD, wmDDID);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID()
				+ "] disposed the worker [" + workerID.getServiceID() + "], " +
				"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpOD, rwpcPubKey);
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(peerComponent, workerSpecD, wmDDID, dsID);
		
		//A remote consumer disposes an ownered worker - expect to log the warn
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID,
				remoteWorkerD, RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID()
				+ "] disposed the worker [" + workerID.getServiceID() + "], " +
				"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpOD, rwpcPubKey);
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//Worker login
		String workerFPublicKey = "workerFPublicKey"; 
		DeploymentID wmFDID = req_019_Util.createWorkerManagementDeploymentID(workerFPublicKey, workerSpecF);
		req_010_Util.workerLogin(peerComponent, workerSpecF, wmFDID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(peerComponent, workerSpecF, wmFDID, dsID);
		
		//Allocate the worker for a local user
		String broker4PubKey = "broker4PubKey";
		DeploymentID lwpc4OID = req_108_Util.login(peerComponent, user4, broker4PubKey);
		LocalWorkerProviderClient lwpc4 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc4OID);
		
		WorkerAllocation allocationF = new WorkerAllocation(wmFDID);
		
		RequestSpecification requestSpec5 = new RequestSpecification(0, new JobSpecification("label"), 5, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpc4OID, lwpc4), requestSpec5, allocationF);
		req_025_Util.changeWorkerStatusToAllocatedForBroker(peerComponent, lwpc4OID, wmFDID, workerSpecF, 
				requestSpec5);
		
		//A remote consumer disposes a worker allocated for a local consumer - expect to log the warn
		RemoteWorkerManagement remoteWorkerF = getMock(NOT_NICE, RemoteWorkerManagement.class);
		
		workerID = createWorkerDeploymentID(workerSpecF, workerFPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID,
				remoteWorkerF, RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed the worker [" + workerID.getServiceID() + "], " +
		"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpOD, rwpcPubKey);
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//A remote consumer disposes a worker allocated for other remote consumer - expect to log the warn
		RemoteWorkerManagement remoteWorkerB = getMock(NOT_NICE, RemoteWorkerManagement.class);
		
		workerID = createWorkerDeploymentID(workerSpecB, workerBPublicKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID,
				remoteWorkerB, RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed the worker [" + workerID.getServiceID() + "], " +
		"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
		
		//A remote consumer disposes a remote worker - expect to log the warn
		RemoteWorkerManagement remoteWorkerR = getMock(NOT_NICE, RemoteWorkerManagement.class);
		
		workerID = createWorkerDeploymentID(workerAcceptanceUtil.createClassAdWorkerSpec("unknown", "unknown", null, null), "remoteWorkerPublicKey");
		AcceptanceTestUtil.publishTestObject(peerComponent, workerID, remoteWorkerR,
				RemoteWorkerManagement.class);
		
		loggerMock.warn("The remote consumer [" + rwpcOID.getServiceID() + "] disposed the worker [" + workerID.getServiceID() + "], " +
		"that is not allocated for him. This disposal was ignored."); 
		replayActiveMocks();
		
		rwp.disposeWorker(workerID.getServiceID());
		verifyActiveMocks();
		resetActiveMocks();
	}
}