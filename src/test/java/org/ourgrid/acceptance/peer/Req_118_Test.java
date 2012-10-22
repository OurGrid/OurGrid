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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.acceptance.util.peer.Req_118_Util;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs = "REQ118")
public class Req_118_Test extends PeerAcceptanceTestCase {

	private ScheduledExecutorService timer;
	private CommuneLogger loggerMock;
	private PeerComponent component;
	private DeploymentID dsID;
	private XMPPAccount user;
	
	private final String userName = "user011";
	private final String serverName = "server011";
	private final String password = "011011";
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());
    private Req_118_Util req_118_Util = new Req_118_Util(getComponentContext());

    @Before
	public void setUp() throws Exception {
		super.setUp();
		
		//Setup DisoveryService
		dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		//Create an user account
		user = req_101_Util.createLocalUser(userName, serverName, password);
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);

		loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 5 workers - the peer have not idle workers, so pass the request for community and schedule the request for repetition;
	 *    2. The local consumer pauses the request - expect the peer to cancel the schedule repetition;
	 *    3. A local worker becomes idle, which is not allocated for the local consumer;
	 *    4. The local consumer resumes the request - expect the peer to schedule the request repetition;
	 *    5. The peer receives a remote worker, which is allocated for the local consumer.
	 * 
	 */
	@Test public void test_AT_118_1_resumeRequestThatNeedsWorkers() throws Exception {
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();

		// user's public key
		String brokerPublicKey = "brokerPublicKey";
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", brokerPublicKey), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);

		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		// Workers login
		String workerAUserName = "workerA.ourgrid.org";
		String workerAServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerASpec = workerAcceptanceUtil.createWorkerSpec(workerAUserName, workerAServerName);

		String workerBUserName = "workerB.ourgrid.org";
		String workerBServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerBSpec = workerAcceptanceUtil.createWorkerSpec(workerBUserName, workerBServerName);

		String workerAPublicKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerASpec, workerAPublicKey);

		String workerBPublicKey = "publicKeyB";
		req_019_Util.createAndPublishWorkerManagement(component, workerBSpec, workerBPublicKey);
		req_010_Util.workerLogin(component, workerASpec, workerAID);

		// DisoveryService recovery
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);

		// Consumer login and request five workers
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);

		int requestID = 1;
		resetActiveMocks();

		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, "", 5, 0, 0);
		
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec);
		
		// Pause the request
		req_117_Util.pauseRequest(component, lwpcID, requestID, future1);
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerASpec, workerAID, dsID);
		
		// Resume the request
		req_118_Util.resumeRequest(requestSpec, lwpcID, component);
		
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec(workerR1UserName, workerR1ServerName);
				
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		String workerR1PubKey = "workerR1PubKey";
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey, brokerPublicKey);
	}
	

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 worker - the peer have not workers, so pass the request for community and schedule the request for repetition;
	 *    2. The peer receives a remote worker, which is allocated for the local consumer - expect the peer to cancel the schedule repetition;
	 *    3. The local consumer pauses the request;
	 *    4. The local consumer resumes the request;
	 *    5. A local worker becomes idle, which is not allocated for the local consumer;
	 *    6. The peer receives a remote worker, which is disposed.
	 *  
	 */
	@Test public void test_AT_118_2_resumeRequestThatDoesNotNeedWorkers() throws Exception {

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();

		// user's public key
		String brokerPublicKey = "brokerPublicKey";
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", brokerPublicKey), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);

		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		String workerAUserName = "workerA.ourgrid.org";
		String workerAServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerASpec = workerAcceptanceUtil.createWorkerSpec(workerAUserName, workerAServerName);
		
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerASpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerASpec, workerAID);

		// DisoveryService recovery
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login and request one worker
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);

		int request1ID = 1;
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
		
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec);
				
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec(workerR1UserName, workerR1ServerName);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		String workerR1PubKey = "workerR1PubKey";
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey, brokerPublicKey, future1);
		
		// Pause the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		LocalWorkerProvider lwp = (LocalWorkerProvider) lwpOD.getObject();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpcID);
		lwp.pauseRequest(request1ID);
		
		// Resume the request
		req_118_Util.resumeRequestWithNoReschedule(requestSpec, lwpcID, component);
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerASpec, workerAID, dsID);
		
		// Remote worker provider client receives a remote worker
		String workerR2UserName = "workerR2.ourgrid.org";
		String workerR2ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createWorkerSpec(workerR2UserName, workerR2ServerName);
		
		String workerR2PubKey = "workerR2PubKey";
		req_018_Util.receiveAndDisposeRemoteWorker(component, rwp, rwpID, workerSpecR2, workerR2PubKey, brokerPublicKey);
	}
	
	
	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 worker - the peer have not workers, so pass the request for community and schedule the request for repetition;
	 *    2. The peer receives a remote worker, which is allocated for the local consumer - expect the peer to cancel the schedule repetition;
	 *    3. The local consumer pauses the request;
	 *    4. The local consumer disposes the remote worker;
	 *    5. The local consumer resumes the request - expect the peer to schedule the request repetition;
	 *    6. A local worker becomes idle, which is allocated for the local consumer - expect the peer to cancel the schedule repetition.
	 * 
	 */
	@Test public void test_AT_118_3_resumeRequestThatLostWorkerWhilePaused() throws Exception {
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();

		// user's public key
		String brokerPublicKey = "brokerPublicKey";
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", brokerPublicKey), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);

		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		String workerAUserName = "workerA.ourgrid.org";
		String workerAServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerASpec = workerAcceptanceUtil.createWorkerSpec(workerAUserName, workerAServerName);
		
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerASpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerASpec, workerAID);

		// DisoveryService recovery
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login and request one worker
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
 
		int request1ID = 1;
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
		
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec);
				
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec(workerR1UserName, workerR1ServerName);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID rwmOID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey, brokerPublicKey, future1).getDeploymentID();
		
		// Pause the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		LocalWorkerProvider lwp = (LocalWorkerProvider) lwpOD.getObject();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpcID);
		lwp.pauseRequest(request1ID);
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		
		TestStub workerStubR1 = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(),
				rwmOID, workerSpecR1, requestSpec);
		
		// The user disposes the worker after pausing the request - expect the peer to dispose the remote worker
		WorkerAllocation allocationR1 = new WorkerAllocation(rwmOID).addLoserRequestSpec(requestSpec).addLoserConsumer(lwpcID);
		req_015_Util.localDisposeRemoteWorker(component, workerStubR1, allocationR1, rwp, rwpID, false);
		
		// Resume the request
		ScheduledFuture<?> future2 = req_118_Util.resumeRequest(requestSpec, lwpcID, component);
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdleWorkingForBroker(workerAID, lwpcID, future2, component);
	}
	
	/**
	 * Verifies if the peer ignores request resumes, in these scenarios:
	 * 
	 *     * Unknown consumer;
	 *     * Unknown request;
	 *     * Request from other consumer;
	 *     * A not paused request;
	 *     * An already resumed request.
	 * 
	 */
	@Test public void test_AT_118_4_inputValidation() throws Exception {
		// Create another user account
		String anotherUserName = "user002";
		XMPPAccount user2 = req_101_Util.createLocalUser(anotherUserName, serverName, password);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
		// user's public key
		String mg1PubKey = "publicKey1";
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", mg1PubKey), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// user2's public key
		String mg2PubKey = "publicKey2";
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker", mg2PubKey), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// An unknown consumer resumes a request - expect to log warn
		String unknownPubKey = "unknownPubKey";
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy(); 
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, unknownPubKey);

		loggerMock.warn("Ignoring an unknown consumer that resumed a request. Sender public key: " + unknownPubKey);
		replayActiveMocks();
		
		long requestID = 1;
		
		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		// Login with the consumer1
		DeploymentID lwpc1ID = req_108_Util.login(component, user, mg1PubKey);
		
		// A local consumer resumes an unknown request - expect to log warn
		loggerMock.warn("The consumer [" + lwpc1ID.getServiceID() + "] resumed the unknown request" +
				" [" + requestID + "]. This message was ignored.");
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		
		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		// Login with the consumer2
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, mg2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for the consumer2
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, "", 1, 0, 0);
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec);
		
		// A local consumer resumes a request done by other consumer - expect to log warn
		loggerMock.warn("The consumer [" + lwpc1ID.getServiceID() + "] resumed the unknown request" +
				" [" + requestID + "]. This message was ignored.");
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		 
		replayActiveMocks();

		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer resumes a not paused request - expect to log warn
		loggerMock.warn("The consumer [" + lwpc2ID.getServiceID() + "] resumed the request" +
				" [" + requestID + "], that was not paused. This message was ignored.");
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2ID);
		replayActiveMocks();
		
		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		// Pause the request
		req_117_Util.pauseRequest(component, lwpc2ID, requestID, future);
		
		// Resume the request
		req_118_Util.resumeRequest(requestSpec, lwpc2ID, component);
		
		// Resume the request again
		loggerMock.warn("The consumer [" + lwpc2ID.getServiceID() + "] resumed the request" +
				" [" + requestID + "], that was not paused. This message was ignored.");
		replayActiveMocks();
		
		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 5 workers - the peer have not idle workers, so pass the request for community and schedule the request for repetition;
	 *    2. The local consumer pauses the request - expect the peer to cancel the schedule repetition;
	 *    3. A local worker becomes idle, which is not allocated for the local consumer;
	 *    4. The local consumer resumes the request - expect the peer to schedule the request repetition;
	 *    5. The peer receives a remote worker, which is allocated for the local consumer.
	 * 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_118_1_resumeRequestThatNeedsWorkersWithJDL() throws Exception {
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
	
		// user's public key
		String brokerPublicKey = "brokerPublicKey";
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", brokerPublicKey), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
	
		// Workers login
		String workerAUserName = "workerA.ourgrid.org";
		String workerAServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerASpec = workerAcceptanceUtil.createClassAdWorkerSpec(workerAUserName, workerAServerName, null, null);
	
		String workerBUserName = "workerB.ourgrid.org";
		String workerBServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerBSpec = workerAcceptanceUtil.createClassAdWorkerSpec(workerBUserName, workerBServerName, null, null);
	
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerASpec, workerAPublicKey);
	
		String workerBPublicKey = "publicKeyB";
		req_019_Util.createAndPublishWorkerManagement(component, workerBSpec, workerBPublicKey);
		req_010_Util.workerLogin(component, workerASpec, workerAID);

		
		// DisoveryService recovery
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
	
		// Consumer login and request five workers
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
	
		int requestID = 1;
		resetActiveMocks();
	
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, buildRequirements( null ), 5, 0, 0);
		
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec);
		
		// Pause the request
		req_117_Util.pauseRequest(component, lwpcID, requestID, future1);
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerASpec, workerAID, dsID);
		
		// Resume the request
		req_118_Util.resumeRequest(requestSpec, lwpcID, component);
		
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec(workerR1UserName, workerR1ServerName, null, null);
				
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		String workerR1PubKey = "workerR1PubKey";
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey, brokerPublicKey);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 worker - the peer have not workers, so pass the request for community and schedule the request for repetition;
	 *    2. The peer receives a remote worker, which is allocated for the local consumer - expect the peer to cancel the schedule repetition;
	 *    3. The local consumer pauses the request;
	 *    4. The local consumer resumes the request;
	 *    5. A local worker becomes idle, which is not allocated for the local consumer;
	 *    6. The peer receives a remote worker, which is disposed.
	 *  
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_118_2_resumeRequestThatDoesNotNeedWorkersWithJDL() throws Exception {
	
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
	
		// user's public key
		String brokerPublicKey = "brokerPublicKey";
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", brokerPublicKey), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		String workerAUserName = "workerA.ourgrid.org";
		String workerAServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerASpec = workerAcceptanceUtil.createClassAdWorkerSpec(workerAUserName, workerAServerName, null, null);
		
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerASpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerASpec, workerAID);
		
		// DisoveryService recovery
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login and request one worker
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
	
		int request1ID = 1;
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( null ), 1, 0, 0);
		
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec);
				
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec(workerR1UserName, workerR1ServerName, null, null);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		String workerR1PubKey = "workerR1PubKey";
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey, brokerPublicKey, future1);
		
		// Pause the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		LocalWorkerProvider lwp = (LocalWorkerProvider) lwpOD.getObject();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpcID);
		lwp.pauseRequest(request1ID);
		
		// Resume the request
		req_118_Util.resumeRequestWithNoReschedule(requestSpec, lwpcID, component);
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerASpec, workerAID, dsID);
		
		// Remote worker provider client receives a remote worker
		String workerR2UserName = "workerR2.ourgrid.org";
		String workerR2ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createClassAdWorkerSpec(workerR2UserName, workerR2ServerName, null, null);
		
		String workerR2PubKey = "workerR2PubKey";
		req_018_Util.receiveAndDisposeRemoteWorker(component, rwp, rwpID, workerSpecR2, workerR2PubKey, brokerPublicKey);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 worker - the peer have not workers, so pass the request for community and schedule the request for repetition;
	 *    2. The peer receives a remote worker, which is allocated for the local consumer - expect the peer to cancel the schedule repetition;
	 *    3. The local consumer pauses the request;
	 *    4. The local consumer disposes the remote worker;
	 *    5. The local consumer resumes the request - expect the peer to schedule the request repetition;
	 *    6. A local worker becomes idle, which is allocated for the local consumer - expect the peer to cancel the schedule repetition.
	 * 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_118_3_resumeRequestThatLostWorkerWhilePausedWithJDL() throws Exception {
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
	
		// user's public key
		String brokerPublicKey = "brokerPublicKey";
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", brokerPublicKey), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		String workerAUserName = "workerA.ourgrid.org";
		String workerAServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerASpec = workerAcceptanceUtil.createClassAdWorkerSpec(workerAUserName, workerAServerName, null, null);
		
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerASpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerASpec, workerAID);
		
		// DisoveryService recovery
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login and request one worker
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
	
		int request1ID = 1;
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( null ), 1, 0, 0);
		
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec);
				
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec(workerR1UserName, workerR1ServerName, null, null);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID rwmOID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey, brokerPublicKey, future1).getDeploymentID();
		
		// Pause the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		LocalWorkerProvider lwp = (LocalWorkerProvider) lwpOD.getObject();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpcID);
		lwp.pauseRequest(request1ID);
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		
		TestStub workerStubR1 = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(),
				rwmOID, workerSpecR1, requestSpec);
		
		// The user disposes the worker after pausing the request - expect the peer to dispose the remote worker
		WorkerAllocation allocationR1 = new WorkerAllocation(rwmOID).addLoserRequestSpec(requestSpec).addLoserConsumer(lwpcID);
		req_015_Util.localDisposeRemoteWorker(component, workerStubR1, allocationR1, rwp, rwpID, false);
		
		// Resume the request
		ScheduledFuture<?> future2 = req_118_Util.resumeRequest(requestSpec, lwpcID, component);
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdleWorkingForBroker(workerAID, lwpcID, future2, component);
	}

	/**
	 * Verifies if the peer ignores request resumes, in these scenarios:
	 * 
	 *     * Unknown consumer;
	 *     * Unknown request;
	 *     * Request from other consumer;
	 *     * A not paused request;
	 *     * An already resumed request.
	 * 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_118_4_inputValidationWithJDL() throws Exception {
		// Create another user account
		String anotherUserName = "user002";
		XMPPAccount user2 = req_101_Util.createLocalUser(anotherUserName, serverName, password);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
		// user's public key
		String mg1PubKey = "publicKey1";
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", mg1PubKey), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// user2's public key
		String mg2PubKey = "publicKey2";
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker", mg2PubKey), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// An unknown consumer resumes a request - expect to log warn
		String unknownPubKey = "unknownPubKey";
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy(); 
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, unknownPubKey);
	
		loggerMock.warn("Ignoring an unknown consumer that resumed a request. Sender public key: " + unknownPubKey);
		replayActiveMocks();
		
		long requestID = 1;
		
		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		// Login with the consumer1
		DeploymentID lwpc1ID = req_108_Util.login(component, user, mg1PubKey);
		
		// A local consumer resumes an unknown request - expect to log warn
		loggerMock.warn("The consumer [" + lwpc1ID.getServiceID() + "] resumed the unknown request" +
				" [" + requestID + "]. This message was ignored.");
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		
		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		// Login with the consumer2
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, mg2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for the consumer2
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, buildRequirements( null ), 1, 0, 0);
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec);
		
		// A local consumer resumes a request done by other consumer - expect to log warn
		loggerMock.warn("The consumer [" + lwpc1ID.getServiceID() + "] resumed the unknown request" +
				" [" + requestID + "]. This message was ignored.");
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		 
		replayActiveMocks();
	
		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer resumes a not paused request - expect to log warn
		loggerMock.warn("The consumer [" + lwpc2ID.getServiceID() + "] resumed the request" +
				" [" + requestID + "], that was not paused. This message was ignored.");
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2ID);
		replayActiveMocks();
		
		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		// Pause the request
		req_117_Util.pauseRequest(component, lwpc2ID, requestID, future);
		
		// Resume the request
		req_118_Util.resumeRequest(requestSpec, lwpc2ID, component);
		
		// Resume the request again
		loggerMock.warn("The consumer [" + lwpc2ID.getServiceID() + "] resumed the request" +
				" [" + requestID + "], that was not paused. This message was ignored.");
		replayActiveMocks();
		
		lwp.resumeRequest(requestID);
		
		verifyActiveMocks();
	}
}