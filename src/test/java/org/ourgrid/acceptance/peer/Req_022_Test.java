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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.RemoteAllocation;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_022_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_037_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_112_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.reqtrace.ReqTest;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ022")
public class Req_022_Test extends PeerAcceptanceTestCase {

    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_022_Util req_022_Util = new Req_022_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_037_Util req_037_Util = new Req_037_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
    private Req_112_Util req_112_Util = new Req_112_Util(getComponentContext());
    private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    
    private PeerComponent component;
    
    private XMPPAccount user;
    
    private CommuneLogger logger;
    
    private ScheduledExecutorService timer;
	
    @Before
	public void setUp() throws Exception {
		super.setUp();
		
        component = req_010_Util.startPeer();
        
        user = req_101_Util.createLocalUser("user01", "server01", "011011");
        
        logger = EasyMock.createMock(CommuneLogger.class);
        component.setLogger(logger);
        
        timer = EasyMock.createMock(ScheduledExecutorService.class);
        component.setTimer(timer);
        
        EasyMock.replay(timer);
	}
	
    @After
	public void tearDown() throws Exception {
    	EasyMock.verify(timer);
		super.tearDown();
	}
	
    /**
     * Verify if the peer ignores the failures of local consumers in these situations:
     * 
     *     * An unknown local consumer;
     *     * An offline local consumer;
     *     * A local consumer with a wrong public key.
     * 
     * Also verifies if the peer logs the messages.
     */
	@ReqTest(test="AT-022.1", reqs="REQ022")
	@Test public void test_AT_022_1_InputValidation() throws Exception {
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker", "brokerPubKey"), "user01");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Notify the failure of an unknown consumer
		// Expect the debug to be logged
		DeploymentID unknownConsumerID = new DeploymentID(new ContainerID("unknownUser", "unknownServer", "unknownModule", "unknownPubKey"),
				"unknown");
		
		logger.debug("Failure of an unknown local consumer [" + unknownConsumerID.getContainerID() + "]." +
				" This notification was ignored.");
		EasyMock.replay(logger);
		
		req_022_Util.notifyBrokerFailure(unknownConsumerID);
		
		EasyMock.verify(logger);
		EasyMock.reset(logger);
		
		// Notify the failure of an offline consumer
		DeploymentID offlineConsumerID = new DeploymentID(new ContainerID(user.getUsername(), user.getServerAddress(), "brokerModule", 
				""), "unknown");
		
		logger.debug("Failure of an offline local consumer [" + offlineConsumerID.getContainerID() + "]." +
				" This notification was ignored.");
		EasyMock.replay(logger);

		req_022_Util.notifyBrokerFailure(offlineConsumerID);

		EasyMock.verify(logger);
		EasyMock.reset(logger);
		
		// Login with the user01
		String brokerPublicKey = "brokerPubKey";
		req_108_Util.login(component, user, brokerPublicKey);
		
		// Notify the failure of an online consumer with a wrong public key
		// Expect the warn to be logged
		String wrongPubKey = "wrongPubKey";
		
		DeploymentID wrongConsumerID = new DeploymentID(new ContainerID(user.getUsername(), user.getServerAddress(), "brokerModule", 
				wrongPubKey), "unknown");
		
		logger.debug("Failure of a local consumer [" + wrongConsumerID.getContainerID() + "], with a wrong public " +
				"key. This notification was ignored. Wrong consumer public key: [" + wrongPubKey + "].");
		EasyMock.replay(logger);

		req_022_Util.notifyBrokerFailure(wrongConsumerID);

		EasyMock.verify(logger);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *    1. It is notified the failure of an online consumer with no requests;
	 *    2. The failure is expected to be logged;
	 *    3. The status of this local consumer is expected to be OFFLINE.
	 * 
	 * @throws Exception
	 */
	@ReqTest(test="AT-022.2", reqs="REQ022")
	@Test public void test_AT_022_2_LocalConsumerFailureWithoutRequests() throws Exception {
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user01");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Login with the user01
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		
		// Notify the failure of user01
		// Expect the info to be logged
		req_022_Util.notifyBrokerFailure(component, lwpcID);
		
		// Verify if the consumer was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(1);
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		
		req_106_Util.getUsersStatus(expectedResult);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 worker - the peer does not have idle workers, so schedule the request for repetition;
	 *    2. It is notified the failure of this local consumer - expect the peer to cancel the schedule repetition;
	 *    3. The failure is expected to be logged;
	 *    4. The status of this local consumer is expected to be OFFLINE.
	 * 
	 */
	@ReqTest(test="AT-022.3", reqs="REQ022")
	@Test public void test_AT_022_3_LocalConsumerFailureWithRequestsButNoWorkers() throws Exception {

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker", "brokerPubKey"), "user01");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Login with a valid user
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request a worker for the logged user
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec);

		// Notify the failure of the local consumer - expect to cancel the schedule repetition
		// Expect the info to be logged
		req_022_Util.notifyBrokerFailure(component, lwpcID, future);
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(1);
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 2 workers and a local idle worker is commanded to work for him - expect the peer to schedule the request repetition;
	 *    2. The peer receives a remote worker, which is commanded to work for the local consumer - expect the peer to cancel the schedule repetition;
	 *    3. The local consumer fails: - expect the peer to:
	 *          1. Log the failure;
	 *          2. Cancel the consumer request;
	 *          3. Dispose the remote worker;
	 *          4. Command the local worker to stop working;
	 *          5. Register interest in the failure of the local worker
	 *          6. Mark the consumer as OFFLINE.
	 *    4. Verify if the remote worker is not on the remote workers status;
	 *    5. Verify if the local worker is marked as IDLE;
	 *    6. The remote worker sends a status changed message (ALLOCATED_FOR_BROKER) - Expect the Peer not to deliver this Worker to the failed consumer;
	 *    7. The local worker sends a status changed message (ALLOCATED_FOR_BROKER) - Expect the Peer not to deliver this Worker to the failed consumer;
	 * 
	 */
	@ReqTest(test="AT-022.4", reqs="REQ022")
	@Test public void test_AT_022_4_LocalConsumerFailureWithUndeliveredWorkers() throws Exception {
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerA");
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		
		
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerManagementID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
        req_010_Util.workerLogin(component, workerSpecA, workerManagementID);

		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerManagementID);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request two workers
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerManagementID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = new WorkerSpecification();
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerR1");
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "unknownServer");
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID workerR1ID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPublicKey, future).getDeploymentID();
		
		// Notify the failure of the local consumer
		// Expect the info to be logged
		// Expect the remote worker to be disposed and the local worker to be stopped
		WorkerAllocation allocationA = new WorkerAllocation(workerManagementID);
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(new WorkerAllocation(workerR1ID)));
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA), stubs);
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(1);
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
		
		// Verify if the local Worker is marked as IDLE
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(1);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
		
		// Change local worker status to ALLOCATED FOR BROKER
		EasyMock.reset(logger);
		logger.warn("Allocation with a null consumer. The status change was ignored.");
		
		EasyMock.replay(logger);
		
		Worker worker = EasyMock.createMock(Worker.class);
		DeploymentID workerId = new DeploymentID(workerManagementID.getContainerID(), 
		WorkerConstants.WORKER);
		
		peerAcceptanceUtil.createStub(worker, Worker.class, workerId);
		EasyMock.replay(worker);
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, workerId.getServiceID(), null, workerManagementID,
				(WorkerManagement) AcceptanceTestUtil.getBoundObject(workerManagementID));
		
		EasyMock.verify(logger);
		EasyMock.verify(worker);
		
		// Change remote worker status to ALLOCATED FOR BROKER
		EasyMock.reset(logger);
		logger.warn("An unknown worker changed its status to Allocated for Broker. " +
				"It will be ignored. Worker public key: " + workerR1ID.getPublicKey());
		EasyMock.replay(logger);
		
		Worker worker2 = EasyMock.createMock(Worker.class);
		peerAcceptanceUtil.createStub(worker2, Worker.class, workerR1ID);
		EasyMock.replay(worker2);
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, workerR1ID.getServiceID(), workerR1ID,
				(RemoteWorkerManagement) AcceptanceTestUtil.getBoundObject(workerR1ID));
		
		EasyMock.verify(logger);
		EasyMock.verify(worker2);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 2 workers and a local idle worker is commanded to work for him - expect the peer to schedule the request repetition;
	 *    2. The local worker sends a status changed message (ALLOCATED_FOR_BROKER) - Expect this worker to be delivered;
	 *    3. The peer receives a remote worker, which is commanded to work for the local consumer - expect the peer to cancel the schedule repetition;
	 *    4. The remote worker sends a status changed message (ALLOCATED_FOR_BROKER) - Expect this worker to be delivered;
	 *    5. The local consumer fails: - expect the peer to:
	 *          1. Log the failure;
	 *          2. Cancel the consumer request;
	 *          3. Dispose the remote worker;
	 *          4. Command the local worker to stop working;
	 *          5. Mark the consumer as OFFLINE.
	 *    6. Verify if the remote worker is not on the remote workers status;
	 *    7. Verify if the local worker is marked as IDLE;
	 * 
	 */
	@ReqTest(test="AT-022.5", reqs="REQ022")
	@Test public void test_AT_022_5_LocalConsumerFailureWithDeliveredWorkers() throws Exception {

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerA");
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);

		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request two workers
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		assertTrue(future != null);
		
		EasyMock.reset(future);
		EasyMock.replay(future);
		
		// Change worker A status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec);
		
		// Verify if it was unregistered interest in the failure of the local worker
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = new WorkerSpecification();
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerR1");
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "unknownServer");
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		EasyMock.verify(future);
		EasyMock.reset(future);
		EasyMock.expect(future.cancel(true)).andReturn(true).once();
		EasyMock.replay(future);
		
		String workerR1PubKey = "workerR1PubKey";
		TestStub workerR1Stub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPublicKey, future);
		
		EasyMock.verify(future);
		
		// Change remote worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpecR1, workerR1Stub, lwpcID, requestSpec);
		
		// Notify the failure of the local consumer
		// Expect the info to be logged
		// Expect the remote worker to be disposed and the local worker to be stopped
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(new WorkerAllocation(workerR1Stub.getDeploymentID())));
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA), stubs);
		
		// Verify if it was registered interest in the failure of the local worker
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(1);
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
		
		// Verify if the local Worker is marked as IDLE
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(1);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, null));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 5 workers (mem > 128) and obtain a local idle worker (os = linux; mem = 256) - expect the peer to schedule the request repetition;
	 *    2. The peer receives a remote worker (os = windows; mem = 256), which is allocated for the local consumer;
	 *    3. The peer receives a remote worker (os = linux; mem = 256), which is allocated for the local consumer;
	 *    4. A local worker(os = windows; mem = 256) becomes idle, which is allocated for the local consumer;
	 *    5. Four local worker(os = linux; mem = 64) becomes idle, which are not allocated for the local consumer;
	 *    6. Other local consumer requests 6 workers (os = linux) and obtain four local idle workers (os = linux; mem = 64) - expect the peer to schedule the request repetition;
	 *    7. The first local consumer fails - expect the peer to:
	 *          1. Log the failure;
	 *          2. Cancel the consumer request;
	 *          3. Register interest in the failure of the local workers;
	 *          4. Register interest in the failure of the remote workers that will not be disposed;
	 *          5. Cancel the first consumer's request repetition;
	 *          6. Command the local worker (os = windows; mem = 256) to stop working;
	 *          7. Allocate the remote worker (os = linux; mem = 256) for the other consumer;
	 *          8. Dispose the remote worker (os = windows; mem = 256);
	 *          9. Allocate the local worker (os = linux; mem = 256) for the other consumer - expect the peer to cancel the other consumer's request repetition;
	 *         10. Mark the first consumer as OFFLINE.
	 *    8. Check remote workers status;
	 *    9. Check local workers status;
	 * 
	 */
	@ReqTest(test="AT-022.6", reqs="REQ022")
	@Test public void test_AT_022_6_LocalConsumerFailureWithWorkersRedistribution() throws Exception {

		// Create another user
		XMPPAccount user02 = req_101_Util.createLocalUser("user02", "server02", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user02.getUsername() + "@" + user02.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Workers login
		String serverName = "xmpp.ourgrid.org";
		
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerA");
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, serverName);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_MEM, "256");
		
		WorkerSpecification workerSpecB = new WorkerSpecification();
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerB");
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, serverName);
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_MEM, "256");
		
		WorkerSpecification workerSpecC = new WorkerSpecification();
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerC");
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, serverName);
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_MEM, "64");
		
		WorkerSpecification workerSpecD = new WorkerSpecification();
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerD");
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, serverName);
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_MEM, "64");
		
		WorkerSpecification workerSpecE = new WorkerSpecification();
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerE");
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, serverName);
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_MEM, "64");
		
		WorkerSpecification workerSpecF = new WorkerSpecification();
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerF");
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, serverName);
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_MEM, "64");
		
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		String workerBPublicKey = "workerBPubKey";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		String workerCPublicKey = "workerCPubKey";
		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		String workerDPublicKey = "workerDPubKey";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDID);

		String workerEPublicKey = "workerEPubKey";
		DeploymentID workerEID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEID);

		String workerFPublicKeyF = "workerFPubKey";
		DeploymentID workerFID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, workerFPublicKeyF);
		req_010_Util.workerLogin(component, workerSpecF, workerFID);

		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request five workers
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "mem > 128", 5, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		assertTrue(future != null);
		
		EasyMock.reset(future);
		EasyMock.replay(future);
		
		// Change worker A status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec);
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = new WorkerSpecification();
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerR1");
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "unknownServer");
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_MEM, "256");
		
		WorkerSpecification workerSpecR2 = new WorkerSpecification();
		workerSpecR2.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerR2");
		workerSpecR2.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "unknownServer");
		workerSpecR2.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecR2.putAttribute(OurGridSpecificationConstants.ATT_MEM, "256");
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		
		// Remote worker provider client receives a remote worker (os = windows; mem = 256)
		String workerR1PubKey = "workerR1PubKey";
		TestStub workerR1Stub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPublicKey);
		
		// Change remote worker 1 status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpecR1, workerR1Stub, lwpcID, requestSpec);
		
		// Remote worker provider client receives a remote worker (os = windows; mem = 256)
		String workerR2PubKey = "workerR2PubKey";
		TestStub workerR2Stub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR2, workerR2PubKey,
				brokerPublicKey);
		
		// Change remote worker 2 status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpecR2, workerR2Stub, lwpcID, requestSpec);
		
		// Change worker B (os = windows; mem = 256) status to IDLE
		req_025_Util.changeWorkerStatusToIdleWorkingForBroker(workerBID, lwpcID, component);
		
		// Change worker B status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerBID, workerSpecB, requestSpec);
		
		// Change 4 workers (os = linux; mem = 64) status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecC, workerCID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecD, workerDID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecE, workerEID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecF, workerFID, dsID);
		
		// Other consumer login
		String broker2PublicKey = "broker2PubKey";
		DeploymentID lwpc2ID = req_108_Util.login(component, user02, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request six workers
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "os = linux", 6, 0, 0);
		WorkerAllocation workerAllocC = new WorkerAllocation(workerCID);
		WorkerAllocation workerAllocD = new WorkerAllocation(workerDID);
		WorkerAllocation workerAllocE = new WorkerAllocation(workerEID);
		WorkerAllocation workerAllocF = new WorkerAllocation(workerFID);
		
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		peerAcceptanceUtil.getRemoteWorkerProviderClient().workerProviderIsUp(rwp, rwpID, AcceptanceTestUtil.getCertificateMock(rwpID));
		
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		
		ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2) ,
				requestSpec2, rwps, workerAllocC, workerAllocD, workerAllocE, workerAllocF);
		
		assertTrue(future2 != null);
		
		// Change worker C status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerCID, workerSpecC, requestSpec2);
		
		// Change worker D status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerDID, workerSpecD, requestSpec2);
		
		// Change worker E status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerEID, workerSpecE, requestSpec2);
		
		// Change worker F status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerFID, workerSpecF, requestSpec2);
		
		// Verify if it was unregistered failure interest on the workers delivered to the first consumer
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBID.getServiceID()));

		// Notify the failure of the first local consumer
		// Expect the info to be logged
		// Expect the request schedule to be canceled
		// Verify if Workers were redistributed
		WorkerAllocation allocationA = new WorkerAllocation(workerAID).addWinnerConsumer(lwpc2ID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		
		WorkerAllocation allocationR1 = new WorkerAllocation(workerR1Stub.getDeploymentID());
		WorkerAllocation allocationR2 = new WorkerAllocation(workerR2Stub.getDeploymentID());
		allocationR2.addWinnerConsumer(lwpc2ID).addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClient());
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(allocationR1, allocationR2));
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA, allocationB), stubs, future, future2);
		
		// Verify if it was registered failure interest on the workers
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(workerR2Stub.getDeploymentID().getServiceID()));
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(2);
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				broker2PublicKey, UserState.CONSUMING));
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
		
		// Check remote workers status
		req_037_Util.getRemoteWorkersStatus(new RemoteWorkerInfo(workerSpecR2, rwpID.getServiceID().toString(), 
				lwpc2ID.getServiceID().toString()));
		
		// Check local workers status
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(6);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecD, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecE, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 worker and obtain 1 local idle worker;
	 *    2. Other local consumer requests 2 workers - expect the peer to schedule the request repetition;
	 *    3. The first local consumer disposes the local worker, which is allocated for the other consumer;
	 *    4. The first local consumer fails - expect the peer to:
	 *          1. Log the failure
	 *          2. Do not affect the disposed worker;
	 *          3. Mark the first consumer as OFFLINE.
	 *    5. Check local workers status;
	 * 
	 */
	@ReqTest(test="AT-022.7", reqs="REQ022")
	@Test public void test_AT_022_7_LocalConsumerFailureWithRequestAndFreedWorker() throws Exception {
		// Create another user
		XMPPAccount user02 = req_101_Util.createLocalUser("user02", "server02", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user02.getUsername() + "@" + user02.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerA");
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request one worker
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		assertTrue(future == null);
		
		// Change worker A status to ALLOCATED FOR BROKER
		Worker workerA = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec).getObject();
		
		// Other Consumer login
		String broker2PublicKey = "broker2PubKey";
		DeploymentID lwpc2ID = req_108_Util.login(component, user02, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);

		// Request two workers
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 2, 0, 0);
		
		ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2);
		
		assertTrue(future2 != null);
		
		// The first consumer pauses the request
		req_117_Util.pauseRequest(component, lwpcID, requestSpec.getRequestId(), future);
		
		// The first Consumer disposes the worker - expect the peer to allocate the worker for the other local consumer
		WorkerAllocation allocation = new WorkerAllocation(workerAID);
		allocation.addLoserConsumer(lwpcID).addWinnerConsumer(lwpc2ID).addLoserRequestSpec(requestSpec);
		
		req_015_Util.localConsumerDisposesLocalWorker(component, workerA, allocation);
		
		// Notify the failure of the first local consumer
		// Expect the info to be logged
		req_022_Util.notifyBrokerFailure(component, lwpcID, future);
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(2);
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				broker2PublicKey, UserState.CONSUMING));
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);

		// Check local workers status
		List<WorkerInfo> expectedResult2 = new ArrayList<WorkerInfo>(1);
		expectedResult2.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		req_036_Util.getLocalWorkersStatus(expectedResult2);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 workers and obtain a local idle worker;
	 *    2. The same local consumer requests 2 workers and obtain a local idle worker - expect the peer to schedule the request repetition;
	 *    3. The peer receives a remote worker, which is allocated for the local consumer (request 2) - expect the peer to cancel the schedule repetition;
	 *    4. The local consumer fails: - expect the peer to:
	 *          1. Log the failure;
	 *          2. Register interest in the failure of the local workers;
	 *          3. Dispose the remote worker;
	 *          4. Command the local workers to stop working;
	 *          5. Mark the consumer as OFFLINE.
	 *    5. Check remote workers status;
	 *    6. Check local workers status;
	 * 
	 */
	@ReqTest(test="AT-022.8", reqs="REQ022")
	@Test public void test_AT_022_8_LocalConsumerFailureWithTwoRequests() throws Exception {

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user01");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Workers login
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerA");
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		
		WorkerSpecification workerSpecB = new WorkerSpecification();
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerB");
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		String workerBPublicKey = "workerBPubKey";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		// Change worker B status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);

		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);

		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request one worker
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		assertTrue(future == null);
		
		// Change worker A status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec);
		
		// Consumer request two workers, only one is delivered
		// The request is forwarded to community
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 2, 0, 0);
		WorkerAllocation workerAllocB = new WorkerAllocation(workerBID);
		
		ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec2, workerAllocB);
		
		assertTrue(future2 != null);
		
		// Change worker B status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerBID, workerSpecB, requestSpec2);
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = new WorkerSpecification();
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerR1");
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "unknownServer");
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec2, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		String workerR1PubKey = "workerR1PubKey";
		TestStub workerR1Stub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPublicKey, future2);
		
		// Change remote worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpecR1, workerR1Stub, lwpcID, requestSpec2);
		
		// Verify if it was unregistered interest in the failure of the local workers
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBID.getServiceID()));
		
		// Notify the failure of the local consumer
		// Expect the info to be logged
		// Expect the remote worker to be disposed and the local worker to be stopped
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(new WorkerAllocation(workerR1Stub.getDeploymentID())));
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA, allocationB), stubs);
		
		// Verify if it was registered interest in the failure of the local worker
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBID.getServiceID()));
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(1);
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
		
		// Check local workers status
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(2);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, null));
		expectedResult3.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, null));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
	}
	
	/**
	 * Verify how the consumers status behave when in each of these situations:
	 * 
	 *     * Consumer is added to the user file;
	 *     * Consumer logs;
	 *     * Consumer fails;
	 *     * Users file is changed.
	 * 
	 */
	@ReqTest(test="AT-022.9", reqs="REQ022")
	@Test public void test_AT_022_9_VerifyConsumersStateBehaviorWhenThereIsFailureAndUsersDataChange() throws Exception {
		// Create another user
		XMPPAccount user02 = req_101_Util.createLocalUser("user02", "server02", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();

		// Login with user01
		String broker01PubKey = "broker01PubKey";
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", broker01PubKey), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);

		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);

		try {
			peerControl.addUser(peerControlClient2, user02.getUsername() + "@" + user02.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Verify if both users are marked as NEVER_LOGGED
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		
		req_106_Util.getUsersStatus(expectedResult);
		
		DeploymentID lwpc01ID = req_108_Util.login(component, user, broker01PubKey);
		
		// Verify if user01 is marked as LOGGED and user02 as NEVER_LOGGED
		expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				broker01PubKey, UserState.LOGGED));
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		
		req_106_Util.getUsersStatus(expectedResult);
		
		// Notify the failure of user01
		req_022_Util.notifyBrokerFailure(lwpc01ID);
		
		// Verify if user01 is marked as OFFLINE and user02 as NEVER_LOGGED
		expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				broker01PubKey, UserState.OFFLINE));
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		
		req_106_Util.getUsersStatus(expectedResult);
		
		// Change user01 public key
		PeerControl peer = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		String broker011PubKey = "newbroker01pubKey";
		
		PeerUser pUser = new PeerUser(user.getUsername(), user.getServerAddress(), broker01PubKey, true);
		UserControl.getInstance().registerPublicKey(new ArrayList<IResponseTO>(), pUser, broker011PubKey);
		
		DeploymentID clientID = new DeploymentID(new ContainerID("a", "a", "a", broker011PubKey),"a");
		peerAcceptanceUtil.createStub(peerControlClient, PeerControlClient.class, clientID);
		
		// Change user02 public key
		String broker02PubKey = "broker02pubKey";
		
		PeerUser pUser2 = new PeerUser(user02.getUsername(), user02.getServerAddress(), broker02PubKey, 
				false);
		UserControl.getInstance().registerPublicKey(new ArrayList<IResponseTO>(), pUser2, broker02PubKey);
		
		clientID = new DeploymentID(new ContainerID("a", "a", "a", broker02PubKey),"a");
		peerAcceptanceUtil.createStub(peerControlClient, PeerControlClient.class, clientID);
		
		// Verify if both users are marked as OFFLINE
		expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				broker011PubKey, UserState.OFFLINE));
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				broker02PubKey, UserState.OFFLINE));
		
		req_106_Util.getUsersStatus(expectedResult);
		
		/*// Change user01 public key
		deployer.addUser(user, "");
		
		// Verify if user01 is NEVER_LOGGED and user02 is OFFLINE
		expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				broker02PubKey, UserState.OFFLINE));
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		
		req_106_Util.getUsersStatus(expectedResult);
		
		// Change user02 public key
		deployer.addUser(user02, "");
		
		// Verify if both users are NEVER_LOGGED
		expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		
		req_106_Util.getUsersStatus(expectedResult);*/
		
		// Remove user02
		peer.removeUser(peerControlClient, user02.getUsername() + "@" + user02.getServerAddress());
		
		// Verify if there is only one user and it is NEVER_LOGGED
		expectedResult = new ArrayList<UserInfo>(1);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				broker011PubKey, UserState.OFFLINE));
		
		req_106_Util.getUsersStatus(expectedResult);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 worker - the peer does not have idle workers, so schedule the request for repetition;
	 *    2. It is notified the failure of this local consumer - expect the peer to cancel the schedule repetition;
	 *    3. The failure is expected to be logged;
	 *    4. The status of this local consumer is expected to be OFFLINE.
	 * 
	 */
	@ReqTest(test="AT-022.3", reqs="REQ022")
	@Category(JDLCompliantTest.class) @Test public void test_AT_022_3_LocalConsumerFailureWithRequestsButNoWorkersWithJDL() throws Exception {
	
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker", "brokerPubKey"), "user01");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Login with a valid user
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request a worker for the logged user
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec);
	
		// Notify the failure of the local consumer - expect to cancel the schedule repetition
		// Expect the info to be logged
		req_022_Util.notifyBrokerFailure(component, lwpcID, future);
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(1);
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 2 workers and a local idle worker is commanded to work for him - expect the peer to schedule the request repetition;
	 *    2. The peer receives a remote worker, which is commanded to work for the local consumer - expect the peer to cancel the schedule repetition;
	 *    3. The local consumer fails: - expect the peer to:
	 *          1. Log the failure;
	 *          2. Cancel the consumer request;
	 *          3. Dispose the remote worker;
	 *          4. Command the local worker to stop working;
	 *          5. Register interest in the failure of the local worker
	 *          6. Mark the consumer as OFFLINE.
	 *    4. Verify if the remote worker is not on the remote workers status;
	 *    5. Verify if the local worker is marked as IDLE;
	 *    6. The remote worker sends a status changed message (ALLOCATED_FOR_BROKER) - Expect the Peer not to deliver this Worker to the failed consumer;
	 *    7. The local worker sends a status changed message (ALLOCATED_FOR_BROKER) - Expect the Peer not to deliver this Worker to the failed consumer;
	 * 
	 */
	@ReqTest(test="AT-022.4", reqs="REQ022")
	@Category(JDLCompliantTest.class) @Test public void test_AT_022_4_LocalConsumerFailureWithUndeliveredWorkersWithJDL() throws Exception {
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.ourgrid.org", null, null);
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerManagementID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerManagementID);
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerManagementID);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request two workers
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerManagementID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1", "unknownServer", null, null);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID workerR1ID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPublicKey, future).getDeploymentID();
		
		// Notify the failure of the local consumer
		// Expect the info to be logged
		// Expect the remote worker to be disposed and the local worker to be stopped
		WorkerAllocation allocationA = new WorkerAllocation(workerManagementID);
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(new WorkerAllocation(workerR1ID)));
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA), stubs);
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(1);
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
		
		// Verify if the local Worker is marked as IDLE
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(1);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
		
		// Change local worker status to ALLOCATED FOR BROKER
		EasyMock.reset(logger);
		logger.warn("Allocation with a null consumer. The status change was ignored.");
		
		EasyMock.replay(logger);
		
		Worker worker = EasyMock.createMock(Worker.class);
		DeploymentID workerId = new DeploymentID(workerManagementID.getContainerID(), 
		WorkerConstants.WORKER);
		
		peerAcceptanceUtil.createStub(worker, Worker.class, workerId);
		EasyMock.replay(worker);
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, workerId.getServiceID(), null, workerManagementID, 
				(WorkerManagement) AcceptanceTestUtil.getBoundObject(workerManagementID));
		
		EasyMock.verify(logger);
		EasyMock.verify(worker);
		
		// Change remote worker status to ALLOCATED FOR BROKER
		EasyMock.reset(logger);
		logger.warn("An unknown worker changed its status to Allocated for Broker. " +
				"It will be ignored. Worker public key: " + workerR1ID.getPublicKey());
		EasyMock.replay(logger);
		
		Worker worker2 = EasyMock.createMock(Worker.class);
		peerAcceptanceUtil.createStub(worker2, Worker.class, workerR1ID);
		EasyMock.replay(worker2);
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, workerR1ID.getServiceID(), workerR1ID,
				(RemoteWorkerManagement) AcceptanceTestUtil.getBoundObject(workerR1ID));
		
		EasyMock.verify(logger);
		EasyMock.verify(worker2);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 2 workers and a local idle worker is commanded to work for him - expect the peer to schedule the request repetition;
	 *    2. The local worker sends a status changed message (ALLOCATED_FOR_BROKER) - Expect this worker to be delivered;
	 *    3. The peer receives a remote worker, which is commanded to work for the local consumer - expect the peer to cancel the schedule repetition;
	 *    4. The remote worker sends a status changed message (ALLOCATED_FOR_BROKER) - Expect this worker to be delivered;
	 *    5. The local consumer fails: - expect the peer to:
	 *          1. Log the failure;
	 *          2. Cancel the consumer request;
	 *          3. Dispose the remote worker;
	 *          4. Command the local worker to stop working;
	 *          5. Mark the consumer as OFFLINE.
	 *    6. Verify if the remote worker is not on the remote workers status;
	 *    7. Verify if the local worker is marked as IDLE;
	 * 
	 */
	@ReqTest(test="AT-022.5", reqs="REQ022")
	@Test public void test_AT_022_5_LocalConsumerFailureWithDeliveredWorkersWithJDL() throws Exception {
	
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.ourgrid.org", null, null);
		
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request two workers
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		assertTrue(future != null);
		
		EasyMock.reset(future);
		EasyMock.replay(future);
		
		// Change worker A status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec);
		
		// Verify if it was unregistered interest in the failure of the local worker
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1", "unknownServer", null, null);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		EasyMock.verify(future);
		EasyMock.reset(future);
		EasyMock.expect(future.cancel(true)).andReturn(true).once();
		EasyMock.replay(future);
		
		String workerR1PubKey = "workerR1PubKey";
		TestStub workerR1Stub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPublicKey, future);
		
		EasyMock.verify(future);
		
		// Change remote worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpecR1, workerR1Stub, lwpcID, requestSpec);
		
		// Notify the failure of the local consumer
		// Expect the info to be logged
		// Expect the remote worker to be disposed and the local worker to be stopped
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(new WorkerAllocation(workerR1Stub.getDeploymentID())));
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA), stubs);
		
		// Verify if it was registered interest in the failure of the local worker
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(1);
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
		
		// Verify if the local Worker is marked as IDLE
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(1);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, null));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 5 workers (mem > 128) and obtain a local idle worker (os = linux; mem = 256) - expect the peer to schedule the request repetition;
	 *    2. The peer receives a remote worker (os = windows; mem = 256), which is allocated for the local consumer;
	 *    3. The peer receives a remote worker (os = linux; mem = 256), which is allocated for the local consumer;
	 *    4. A local worker(os = windows; mem = 256) becomes idle, which is allocated for the local consumer;
	 *    5. Four local worker(os = linux; mem = 64) becomes idle, which are not allocated for the local consumer;
	 *    6. Other local consumer requests 6 workers (os = linux) and obtain four local idle workers (os = linux; mem = 64) - expect the peer to schedule the request repetition;
	 *    7. The first local consumer fails - expect the peer to:
	 *          1. Log the failure;
	 *          2. Cancel the consumer request;
	 *          3. Register interest in the failure of the local workers;
	 *          4. Register interest in the failure of the remote workers that will not be disposed;
	 *          5. Cancel the first consumer's request repetition;
	 *          6. Command the local worker (os = windows; mem = 256) to stop working;
	 *          7. Allocate the remote worker (os = linux; mem = 256) for the other consumer;
	 *          8. Dispose the remote worker (os = windows; mem = 256);
	 *          9. Allocate the local worker (os = linux; mem = 256) for the other consumer - expect the peer to cancel the other consumer's request repetition;
	 *         10. Mark the first consumer as OFFLINE.
	 *    8. Check remote workers status;
	 *    9. Check local workers status;
	 * 
	 */
	@ReqTest(test="AT-022.6", reqs="REQ022")
	@Category(JDLCompliantTest.class) @Test public void test_AT_022_6_LocalConsumerFailureWithWorkersRedistributionWithJDL() throws Exception {
	
		// Create another user
		XMPPAccount user02 = req_101_Util.createLocalUser("user02", "server02", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user02.getUsername() + "@" + user02.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
	    
	    //Workers login
		String serverName = "xmpp.ourgrid.org";
		
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", serverName, 256, OurGridSpecificationConstants.OS_LINUX);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB", serverName, 256, OurGridSpecificationConstants.OS_WINDOWS);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("workerC", serverName, 64, OurGridSpecificationConstants.OS_LINUX);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("workerD", serverName, 64, OurGridSpecificationConstants.OS_LINUX);
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createClassAdWorkerSpec("workerE", serverName, 64, OurGridSpecificationConstants.OS_LINUX);
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createClassAdWorkerSpec("workerF", serverName, 64, OurGridSpecificationConstants.OS_LINUX);
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		
		String workerBPublicKey = "workerBPubKey";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		
		String workerCPublicKey = "workerCPubKey";
		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		
		String workerDPublicKey = "workerDPubKey";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		
		String workerEPublicKey = "workerEPubKey";
		DeploymentID workerEID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		
		String workerFPublicKeyF = "workerFPubKey";
		DeploymentID workerFID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, workerFPublicKeyF);
		
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);
		req_010_Util.workerLogin(component, workerSpecC, workerCID);
		req_010_Util.workerLogin(component, workerSpecD, workerDID);
		req_010_Util.workerLogin(component, workerSpecE, workerEID);
		req_010_Util.workerLogin(component, workerSpecF, workerFID);
		
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request five workers
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(">", 128, null, null), 5, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		assertTrue(future != null);
		
		EasyMock.reset(future);
		EasyMock.replay(future);
		
		// Change worker A status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec);
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1", "unknownServer", 256, OurGridSpecificationConstants.OS_WINDOWS);
		
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR2", "unknownServer", 256, OurGridSpecificationConstants.OS_LINUX);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		
		// Remote worker provider client receives a remote worker (os = windows; mem = 256)
		String workerR1PubKey = "workerR1PubKey";
		TestStub workerR1Stub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPublicKey);
		
		// Change remote worker 1 status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpecR1, workerR1Stub, lwpcID, requestSpec);
		
		// Remote worker provider client receives a remote worker (os = windows; mem = 256)
		String workerR2PubKey = "workerR2PubKey";
		TestStub workerR2Stub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR2, workerR2PubKey,
				brokerPublicKey);
		
		// Change remote worker 2 status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpecR2, workerR2Stub, lwpcID, requestSpec);
		
		// Change worker B (os = windows; mem = 256) status to IDLE
		req_025_Util.changeWorkerStatusToIdleWorkingForBroker(workerBID, lwpcID, component);
		
		// Change worker B status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerBID, workerSpecB, requestSpec);
		
		// Change 4 workers (os = linux; mem = 64) status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecC, workerCID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecD, workerDID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecE, workerEID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecF, workerFID, dsID);
		
		// Other consumer login
		String broker2PublicKey = "broker2PubKey";
		DeploymentID lwpc2ID = req_108_Util.login(component, user02, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request six workers
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "[Requirements=other.OS == \"linux\";Rank=0]", 6, 0, 0);
		WorkerAllocation workerAllocC = new WorkerAllocation(workerCID);
		WorkerAllocation workerAllocD = new WorkerAllocation(workerDID);
		WorkerAllocation workerAllocE = new WorkerAllocation(workerEID);
		WorkerAllocation workerAllocF = new WorkerAllocation(workerFID);
		
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		peerAcceptanceUtil.getRemoteWorkerProviderClient().workerProviderIsUp(rwp, rwpID, AcceptanceTestUtil.getCertificateMock(rwpID));
		
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		
		ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2) ,
				requestSpec2, rwps, workerAllocC, workerAllocD, workerAllocE, workerAllocF);
		
		assertTrue(future2 != null);
		
		// Change worker C status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerCID, workerSpecC, requestSpec2);
		
		// Change worker D status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerDID, workerSpecD, requestSpec2);
		
		// Change worker E status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerEID, workerSpecE, requestSpec2);
		
		// Change worker F status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerFID, workerSpecF, requestSpec2);
		
		// Verify if it was unregistered failure interest on the workers delivered to the first consumer
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBID.getServiceID()));
	
		// Notify the failure of the first local consumer
		// Expect the info to be logged
		// Expect the request schedule to be canceled
		// Verify if Workers were redistributed
		WorkerAllocation allocationA = new WorkerAllocation(workerAID).addWinnerConsumer(lwpc2ID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		
		WorkerAllocation allocationR1 = new WorkerAllocation(workerR1Stub.getDeploymentID());
		WorkerAllocation allocationR2 = new WorkerAllocation(workerR2Stub.getDeploymentID());
		allocationR2.addWinnerConsumer(lwpc2ID).addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClient());
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(allocationR1, allocationR2));
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA, allocationB), stubs, future, future2);
		
		// Verify if it was registered failure interest on the workers
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(workerR2Stub.getDeploymentID().getServiceID()));
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(2);
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				broker2PublicKey, UserState.CONSUMING));
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
		
		// Check remote workers status
		req_037_Util.getRemoteWorkersStatus(new RemoteWorkerInfo(workerSpecR2, rwpID.getServiceID().toString(), 
				lwpc2ID.getServiceID().toString()));
		
		// Check local workers status
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(6);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecD, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecE, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 worker and obtain 1 local idle worker;
	 *    2. Other local consumer requests 2 workers - expect the peer to schedule the request repetition;
	 *    3. The first local consumer disposes the local worker, which is allocated for the other consumer;
	 *    4. The first local consumer fails - expect the peer to:
	 *          1. Log the failure
	 *          2. Do not affect the disposed worker;
	 *          3. Mark the first consumer as OFFLINE.
	 *    5. Check local workers status;
	 * 
	 */
	@ReqTest(test="AT-022.7", reqs="REQ022")
	@Category(JDLCompliantTest.class) @Test public void test_AT_022_7_LocalConsumerFailureWithRequestAndFreedWorkerWithJDL() throws Exception {
		// Create another user
		XMPPAccount user02 = req_101_Util.createLocalUser("user02", "server02", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user02.getUsername() + "@" + user02.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.ourgrid.org", null, null);
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request one worker
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		assertTrue(future == null);
		
		// Change worker A status to ALLOCATED FOR BROKER
		Worker workerA = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec).getObject();
		
		// Other Consumer login
		String broker2PublicKey = "broker2PubKey";
		DeploymentID lwpc2ID = req_108_Util.login(component, user02, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
	
		// Request two workers
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		
		ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2);
		
		assertTrue(future2 != null);
		
		// The first consumer pauses the request
		req_117_Util.pauseRequest(component, lwpcID, requestSpec.getRequestId(), future);
		
		// The first Consumer disposes the worker - expect the peer to allocate the worker for the other local consumer
		WorkerAllocation allocation = new WorkerAllocation(workerAID);
		allocation.addLoserConsumer(lwpcID).addWinnerConsumer(lwpc2ID).addLoserRequestSpec(requestSpec);
		
		req_015_Util.localConsumerDisposesLocalWorker(component, workerA, allocation);
		
		// Notify the failure of the first local consumer
		// Expect the info to be logged
		req_022_Util.notifyBrokerFailure(component, lwpcID, future);
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(2);
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				broker2PublicKey, UserState.CONSUMING));
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
	
		// Check local workers status
		List<WorkerInfo> expectedResult2 = new ArrayList<WorkerInfo>(1);
		expectedResult2.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString()));
		req_036_Util.getLocalWorkersStatus(expectedResult2);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 1 workers and obtain a local idle worker;
	 *    2. The same local consumer requests 2 workers and obtain a local idle worker - expect the peer to schedule the request repetition;
	 *    3. The peer receives a remote worker, which is allocated for the local consumer (request 2) - expect the peer to cancel the schedule repetition;
	 *    4. The local consumer fails: - expect the peer to:
	 *          1. Log the failure;
	 *          2. Register interest in the failure of the local workers;
	 *          3. Dispose the remote worker;
	 *          4. Command the local workers to stop working;
	 *          5. Mark the consumer as OFFLINE.
	 *    5. Check remote workers status;
	 *    6. Check local workers status;
	 * 
	 */
	@ReqTest(test="AT-022.8", reqs="REQ022")
	@Category(JDLCompliantTest.class) @Test public void test_AT_022_8_LocalConsumerFailureWithTwoRequestsWithJDL() throws Exception {
	
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user01");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.ourgrid.org", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB", "xmpp.ourgrid.org", null, null);
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		String workerBPublicKey = "workerBPubKey";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);
		
		// Change worker B status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
	
		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
	
		// Consumer login
		String brokerPublicKey = "brokerPubKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request one worker
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec, workerAllocA);
		
		assertTrue(future == null);
		
		// Change worker A status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec);
		
		// Consumer request two workers, only one is delivered
		// The request is forwarded to community
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		WorkerAllocation workerAllocB = new WorkerAllocation(workerBID);
		
		ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec2, workerAllocB);
		
		assertTrue(future2 != null);
		
		// Change worker B status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerBID, workerSpecB, requestSpec2);
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1", "unknownServer", null, null);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec2, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker
		String workerR1PubKey = "workerR1PubKey";
		TestStub workerR1Stub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPublicKey, future2);
		
		// Change remote worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpecR1, workerR1Stub, lwpcID, requestSpec2);
		
		// Verify if it was unregistered interest in the failure of the local workers
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBID.getServiceID()));
		
		// Notify the failure of the local consumer
		// Expect the info to be logged
		// Expect the remote worker to be disposed and the local worker to be stopped
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(new WorkerAllocation(workerR1Stub.getDeploymentID())));
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA, allocationB), stubs);
		
		// Verify if it was registered interest in the failure of the local worker
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerAID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBID.getServiceID()));
		
		// Verify if the client was marked as OFFLINE
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(1);
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				brokerPublicKey, UserState.OFFLINE));
		req_106_Util.getUsersStatus(expectedResult);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
		
		// Check local workers status
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(2);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, null));
		expectedResult3.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, null));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
	}

	/**
	 * Verify how the consumers status behave when in each of these situations:
	 * 
	 *     * Consumer is added to the user file;
	 *     * Consumer logs;
	 *     * Consumer fails;
	 *     * Users file is changed.
	 * 
	 */
	@ReqTest(test="AT-022.9", reqs="REQ022")
	@Category(JDLCompliantTest.class) @Test public void test_AT_022_9_VerifyConsumersStateBehaviorWhenThereIsFailureAndUsersDataChangeWithJDL() throws Exception {
		// Create another user
		XMPPAccount user02 = req_101_Util.createLocalUser("user02", "server02", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
	
		// Login with user01
		String broker01PubKey = "broker01PubKey";
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker", broker01PubKey), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
	
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	
		try {
			peerControl.addUser(peerControlClient2, user02.getUsername() + "@" + user02.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Verify if both users are marked as NEVER_LOGGED
		List<UserInfo> expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		
		req_106_Util.getUsersStatus(expectedResult);
		
		DeploymentID lwpc01ID = req_108_Util.login(component, user, broker01PubKey);
		
		// Verify if user01 is marked as LOGGED and user02 as NEVER_LOGGED
		expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				broker01PubKey, UserState.LOGGED));
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		
		req_106_Util.getUsersStatus(expectedResult);
		
		// Notify the failure of user01
		req_022_Util.notifyBrokerFailure(lwpc01ID);
		
		// Verify if user01 is marked as OFFLINE and user02 as NEVER_LOGGED
		expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				broker01PubKey, UserState.OFFLINE));
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				"", UserState.NEVER_LOGGED));
		
		req_106_Util.getUsersStatus(expectedResult);
		
		// Change user01 public key
		PeerControl peer = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		String broker011PubKey = "newbroker01pubKey";
		
		PeerUser pUser = new PeerUser(user.getUsername(), user.getServerAddress(), broker01PubKey, true);
		UserControl.getInstance().registerPublicKey(new ArrayList<IResponseTO>(), pUser, broker011PubKey);
		
		DeploymentID clientID = new DeploymentID(new ContainerID("a", "a", "a", broker011PubKey),"a");
		peerAcceptanceUtil.createStub(peerControlClient, PeerControlClient.class, clientID);
		
		// Change user02 public key
		String broker02PubKey = "broker02pubKey";
		
		PeerUser pUser2 = new PeerUser(user02.getUsername(), user02.getServerAddress(), broker02PubKey, 
				false);
		UserControl.getInstance().registerPublicKey(new ArrayList<IResponseTO>(), pUser2, broker02PubKey);
		
		clientID = new DeploymentID(new ContainerID("a", "a", "a", broker02PubKey),"a");
		peerAcceptanceUtil.createStub(peerControlClient, PeerControlClient.class, clientID);
		
		// Verify if both users are marked as OFFLINE
		expectedResult = new ArrayList<UserInfo>(2);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				broker011PubKey, UserState.OFFLINE));
		expectedResult.add(new UserInfo(user02.getUsername(), user02.getServerAddress(),
				broker02PubKey, UserState.OFFLINE));
		
		req_106_Util.getUsersStatus(expectedResult);
		
		// Remove user02
		peer.removeUser(peerControlClient, user02.getUsername() + "@" + user02.getServerAddress());
		
		// Verify if there is only one user and it is NEVER_LOGGED
		expectedResult = new ArrayList<UserInfo>(1);
		
		expectedResult.add(new UserInfo(user.getUsername(), user.getServerAddress(),
				broker011PubKey, UserState.OFFLINE));
		
		req_106_Util.getUsersStatus(expectedResult);
	}
	
}