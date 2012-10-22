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
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.LinkedList;
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
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_112_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_025_Test extends PeerAcceptanceTestCase {

	public static final String COMM_FILE_PATH = "req_025"+File.separator;

	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
	private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
	private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
	private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
	private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
	private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
	private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
	private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
	private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
	private Req_112_Util req_112_Util = new Req_112_Util(getComponentContext());
	private PeerComponent component;

	@Before
	public void setUp() throws Exception{
		super.setUp();

		component = req_010_Util.startPeer();
	}

	/**
	 * Verifies if the peer ignores an unknown idle worker.
	 */
	@ReqTest(test="AT-025.1", reqs="REQ025")
	@Test public void test_AT_025_1_ChangeStatusIDLE() throws Exception{
		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");


		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Notify DiscoveryService peer recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpec, workerDID, dsID);

		//Get peer's local workers status and expect worker A to be marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IDLE, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer doesn't adverts an unknown worker.
	 */
	@ReqTest(test="AT-025.2", reqs="REQ025")
	@Test public void test_AT_025_2_ChangeStatusUnknownWorker() throws Exception{
		//Notify DiscoveryService peer recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);

		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);

		//Forge changing worker status to IDLE
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerID = new DeploymentID(new ContainerID("u1", "s1", "m1", workerAPublicKey), "o1");

		req_025_Util.changeUnknowWorkerStatusToIdle(component, workerID, workerAPublicKey);

		//Get peer's local workers status and expect worker A to do not appear
		List<WorkerInfo> localWorkersInfo = new LinkedList<WorkerInfo>();
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer stop advertising an ownered worker. 
	 * @throws Exception
	 */
	@ReqTest(test="AT-025.3", reqs="REQ025")
	@Test public void test_AT_025_3_ChangeStatusIDLE_to_OWNER() throws Exception{

		//Worker login
		String workerAUserName = "workerA.ourgrid.org";
		String workerAServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec(workerAUserName, workerAServerName);


		String workerAPublicKey = "publicKeyA";
		DeploymentID workerID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerID);

		//Notify DiscoveryService peer recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(
				component, workerSpec, workerID, dsID);

		//Change worker status to OWNER and expect to cancel advert
		req_025_Util.changeWorkerStatusToOwner(component, workerID);

		//Get peer's local workers status and expect worker A to be marked as OWNER
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.OWNER, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Validate worker's status changes:
	 * 	A unknown local worker can not change status;
	 * 	A unknown remote worker can not change status;
	 * @throws Exception
	 */
	@ReqTest(test="AT-025.4", reqs="REQ025")
	@Test public void test_AT_025_4_InputValidation() throws Exception{
		String unknowPubKey = "unknownWorkerPublicKey";

		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);

		DeploymentID unknownID = new DeploymentID(new ContainerID("a", "a", "a", unknowPubKey), "a");

		//A unknown local worker change status to IDLE - expect to log the warn
		req_025_Util.changeUnknowWorkerStatusToIdle(component, unknownID, unknowPubKey);

		//A unknown remote worker change status to ALLOCATED FOR BROKER - expect to log the warn
		EasyMock.reset(loggerMock);
		loggerMock.warn(
				"An unknown worker changed its status to Allocated for Broker. It will be ignored. Worker public key: "
						+ unknowPubKey);
		EasyMock.replay(loggerMock);
		req_112_Util.unknownRemoteWorkerStatusChanged("", "", unknowPubKey, component);

		EasyMock.verify(loggerMock);
	}

	/** 
	 * Test change worker public key, during worker failure
	 * @throws Exception
	 */
	@ReqTest(test="AT-025.5", reqs="REQ025")
	@Test public void test_AT_025_5_PubKeyChange() throws Exception{
		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");


		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Notify worker failure
		req_019_Util.notifyWorkerFailure(workerDID, component);

		String workerASecondPublicKey = "workerANewPublicKey";
		workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerASecondPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE with the new public key - expect to log the information
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);
	}

	/** 
	 * Verifies if the peer give the worker to consumer, when the worker's state changes to allocated for broker.
	 */
	@ReqTest(test="AT-025.6", reqs="REQ025")
	@Test public void test_AT_025_6_Idle_to_WorkForBroker() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");

		//Worker login
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Login with a valid user
		String brokerPubKey = "brokerPublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		WorkerAllocation allocationWorker = new WorkerAllocation(workerDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocationWorker);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerDID, workerSpec, requestSpec1);

		//Verify if the client was marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), brokerPubKey, UserState.CONSUMING);
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		req_106_Util.getUsersStatus(usersInfo);

		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/** 
	 * Verifies if the peer ignore a worker status change, when the worker reference is null.
	 */
	@ReqTest(test="AT-025.7", reqs="REQ025")
	@Test public void test_AT_025_7_Idle_to_WorkForBroker_NullWorker() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");


		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Login with a valid user
		String brokerPubKey = "brokerPublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		WorkerAllocation allocationWorker = new WorkerAllocation(workerDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocationWorker);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.error("Unknown worker changed status: " + workerAPublicKey +"/"+ WorkerStatus.ALLOCATED_FOR_BROKER);

		EasyMock.replay(loggerMock);
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, workerAPublicKey);
		EasyMock.verify(loggerMock);
	}

	/** 
	 * Verifies the peer behavior, when it commands the worker to work for Broker, but the worker
	 * answer with a status changed different of Allocated for Broker.
	 * 
	 * Allocated for peer -> log a warn;
	 * Idle -> log a warn;
	 * Stopped -> ignore this message, for the peer is about to receive a notification of worker failure.
	 * Owner -> mark the worker as OWNER and remove the allocation data;
	 */
	@ReqTest(test="AT-025.8", reqs="REQ025")
	@Test public void test_AT_025_8_Idle_to_WorkForBroker() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");


		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Login with a valid user
		String brokerPubKey = "brokerPublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocation);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Create mocks
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		Worker worker = EasyMock.createMock(Worker.class);
		EasyMock.replay(worker);

		//Change worker status to ALLOCATED FOR PEER
		loggerMock.warn("The worker <" + workerDID.getContainerID() + "> (IN_USE) changed its status to ALLOCATED_FOR_PEER. " +
				"This status change was ignored.");
		EasyMock.replay(loggerMock);

		req_025_Util.changeStatusAllocatedForPeer(workerDID, null, component);
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);

		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to IDLE
		loggerMock.debug("Worker <" + workerDID.getContainerID() + "> is now IDLE");
		EasyMock.replay(loggerMock);

		req_025_Util.changeStatus(component, WorkerStatus.IDLE, workerDID);
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);

		//Verify if the worker A was marked as IN_USE
		workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IDLE, null);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to STOPPED
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerDID);

		//Verify if the worker A was marked as IN_USE
		workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IDLE, null);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to OWNER
		loggerMock.info("Worker <"+ workerDID.getContainerID() +"> is now OWNER");
		EasyMock.replay(loggerMock);

		req_025_Util.changeStatus(component, WorkerStatus.OWNER, workerDID);
		EasyMock.verify(loggerMock);

		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerDID.getServiceID()));

		//Verify if the worker A was marked as OWNER
		workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.OWNER);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer ignore the worker's state changes to idle, when it 
	 * did not command the worker to stop working. 
	 */
	@ReqTest(test="AT-025.9", reqs="REQ025")
	@Test public void test_AT_025_9_Changing_status_to_Idle_before_StopWorking() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");


		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Login with a valid user
		String brokerPubKey = "brokerPublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocation);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerDID, workerSpec, 
				requestSpec1);

		//Change worker status to IDLE - expect to log the warn
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.debug("Worker <" + workerDID.getContainerID() + "> is now IDLE");
		EasyMock.replay(loggerMock);
		req_025_Util.changeStatus(component, WorkerStatus.IDLE, workerDID);

		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer completes a worker preemption.
	 */
	@ReqTest(test="AT-025.10", reqs="REQ025")
	@Test public void test_AT_025_10_WorkerPreemption() throws Exception{
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user012", "server011", "011011");

		// Workers login
		String serverName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", serverName);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB.ourgrid.org", serverName);

		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);
		String workerBPublicKey = "workerBPublicKey";
		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);

		//Login with valid users
		String broker1PubKey = "broker1PublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);

		String broker2PubKey = "broker2PublicKey";

		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broke2r", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);

		try {
			peerControl.addUser(peerControlClient, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);

		//Request workers for user1
		WorkerAllocation allocationA = new WorkerAllocation(workerADID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, allocationB, allocationA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));

		//Change workers status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerBDID, workerSpecB, requestSpec1);
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerADID, workerSpecA, requestSpec1);

		//Request workers for user2
		allocationA = new WorkerAllocation(workerADID).addLoserConsumer(lwpc1OID).addLoserRequestSpec(requestSpec1);
		RequestSpecification requestSpec2 =new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, allocationA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc2OID.getServiceID()));

		//Change workerA status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OID, workerADID, workerSpecA, requestSpec2);

		//Verify if the workerA was preempted
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2OID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc1OID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer give the worker to consumer, when the worker's state changes to allocated for Peer.
	 */
	@ReqTest(test="AT-025.11", reqs="REQ025")
	@Test public void test_AT_025_11_IdleToAllocatedForPeer() throws Exception{

		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"025_blank.xml");

		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");

		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec,
				1, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADID, workerSpecA, clientID);

		//Verify if the worker A was marked as DONATED
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer ignore a worker status change, when the worker reference is null.
	 */
	@ReqTest(test="AT-025.12", reqs="REQ025")
	@Test public void test_AT_025_12_IdleToAllocatedForPeerAndWorkerNull() throws Exception{

		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"025_blank.xml");

		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");

		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		WorkerManagement workerManagement = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerADID);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.warn("Unknown worker changed status: " + workerAPublicKey +"/" + WorkerStatus.ALLOCATED_FOR_PEER);

		EasyMock.replay(loggerMock);
		replayActiveMocks();

		AcceptanceTestUtil.publishTestObject(component, workerADID, workerManagement, WorkerManagement.class);

		req_025_Util.changeStatusAllocatedForPeer(workerAPublicKey, component);
		EasyMock.verify(loggerMock);
		verifyActiveMocks();
	}

	/**
	 * Verifies the peer behavior, when it commands the worker to work for Peer, 
	 * but the worker answer with a status changed different of Allocated for Peer.
	 * Allocated for Broker -> log a warn;
	 * Idle -> log a warn;
	 * Stopped -> ignore this message, for the peer is about to receive a notification of worker failure.
	 * Owner -> mark the worker as OWNER and remove the allocation data;
	 */
	@ReqTest(test="AT-025.13", reqs="REQ025")
	@Test public void test_AT_025_13_IdleToAllocatedForPeerAndOtherStates() throws Exception{

		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"025_blank.xml");

		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");

		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		Worker worker = getMock(NOT_NICE, Worker.class);

		DeploymentID workerID = new DeploymentID(workerADID.getContainerID(), WorkerConstants.WORKER);

		AcceptanceTestUtil.publishTestObject(component, workerID, worker, Worker.class);
		//peerAcceptanceUtil.createStub(worker, Worker.class, workerADID);

		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.warn("The worker <" + workerADID.getContainerID()+ "> (DONATED) changed its status to ALLOCATED_FOR_BROKER. " +
				"This status change was ignored.");

		EasyMock.replay(loggerMock);
		replayActiveMocks();

		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, workerID.getServiceID(), null, workerADID,
				(WorkerManagement) AcceptanceTestUtil.getBoundObject(workerADID));
		EasyMock.verify(loggerMock);
		verifyActiveMocks();
		resetActiveMocks();
		EasyMock.reset(loggerMock);

		//Change worker status to IDLE
		loggerMock.debug("Worker <" + workerADID.getContainerID() + "> is now IDLE");
		EasyMock.replay(loggerMock);
		replayActiveMocks();

		req_025_Util.changeStatus(component, WorkerStatus.IDLE, workerADID);
		EasyMock.verify(loggerMock);
		verifyActiveMocks();
		resetActiveMocks();
		EasyMock.reset(loggerMock);

		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to STOPPED
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerADID);

		//Verify if the worker A was marked as DONATED
		new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to OWNER
		req_025_Util.changeStatus(component, WorkerStatus.OWNER, workerADID);

		//Verify if the worker A was marked as OWNER
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.OWNER);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 *Verifies if the peer ignore the worker's state changes to idle, when it did not command the worker to stop working.
	 */
	@ReqTest(test="AT-025.14", reqs="REQ025")
	@Test public void test_AT_025_14_DonatedWorkerChangeStatusToIdle() throws Exception{
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");

		String workerAPublicKey = "workerAPublicKey";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADID, workerSpecA, clientID);

		//Change worker status to IDLE - expect to log the warn
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.debug("Worker <" + workerADID.getContainerID() + "> is now IDLE");
		EasyMock.replay(loggerMock);
		replayActiveMocks();
		req_025_Util.changeStatus(component, WorkerStatus.IDLE, workerADID);
		EasyMock.verify(loggerMock);
		verifyActiveMocks();
		resetActiveMocks();

		//Verify if the worker A was marked as DONATED
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer completes a worker preemption.
	 * @throws Exception
	 */
	@ReqTest(test="AT-025.15", reqs="REQ025")
	@Test public void test_AT_025_15_AllocateForPeerPreemption() throws Exception{

		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"025_blank.xml");

		// Workers login
		String serverName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", serverName);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB.ourgrid.org", serverName);

		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);

		//Request two workers for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		WorkerAllocation allocationWB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWB, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change workers status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADID, workerSpecA, clientID);

		//worker B
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDID, workerSpecB, clientID);

		//Request a worker for user2
		DeploymentID client2ID = PeerAcceptanceUtil.createRemoteConsumerID("client2", "server", "pubKeyB");
		allocationWA = new WorkerAllocation(workerADID).addLoserConsumer(clientID);
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		rwpc = req_011_Util.requestForRemoteClient(component, client2ID, requestSpec2, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(client2ID.getServiceID()));

		//Change workerB status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component,rwpc, workerADID, workerSpecA, client2ID);

		//Verify if the workerB was preempted
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, client2ID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer completes a worker preemption.
	 */
	@ReqTest(test="AT-025.16", reqs="REQ025")
	@Test public void test_AT_025_16_WorkerPreemptionPeerToBroker() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		// Workers login
		String serverName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", serverName);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB.ourgrid.org", serverName);

		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);

		//Request two workers for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		WorkerAllocation allocationWB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		RemoteWorkerProviderClient rwpc = 
				req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWB, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change workers status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADID, workerSpecA, clientID);

		//worker B
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDID, workerSpecB, clientID);

		//Login with a valid user
		String broker1PubKey = "publicKeyL";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}


		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);

		//Request workers for user1
		WorkerAllocation allocationA = new WorkerAllocation(workerADID).addLoserConsumer(clientID);
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec2, allocationA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));

		//Change workers status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerADID, workerSpecA, requestSpec2);

		//Verify if the workerA was preempted
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc1OID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer remove the allocation data of worker whose status changed to OWNER.
	 */
	@ReqTest(test="AT-025.17", reqs="REQ025")
	@Test public void test_AT_025_17_WorkerIDLE_INUSE_DONATED_TO_OWNER() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		// Workers login
		String serverName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", serverName);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB.ourgrid.org", serverName);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("workerC.ourgrid.org", serverName);

		
		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		DeploymentID workerCDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDID);

		//Change workers status to IDLE

		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDID);

		//Login with a valid user
		String broker1PubKey = "publicKeyL";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);

		//Request workers for user1
		WorkerAllocation allocationC = new WorkerAllocation(workerCDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, allocationC);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));

		//Change workers status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerCDID, workerSpecC, requestSpec1);

		//Request a worker for the remote client
		resetActiveMocks();
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec2, 1, 
				allocationWB);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//worker B
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDID, workerSpecB, clientID);

		//Verify workers status
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc1OID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC, workerInfoB, workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to OWNER
		req_025_Util.changeWorkerStatusToOwner(component, workerADID);
		req_025_Util.changeWorkerStatusToOwner(component, workerBDID);
		req_025_Util.changeWorkerStatusToOwner(component, workerCDID);


		//Verify workers status
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.OWNER, null);
		workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.OWNER, null);
		workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.OWNER, null);

		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer ignore when a worker change its status to STOPPED.
	 */
	@ReqTest(test="AT-025.18", reqs="REQ025")
	@Test public void test_AT_025_18_ChangeToStopped() throws Exception{
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S1");

		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDID);

		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDOID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDID);

		//Change worker status to OWNER
		req_025_Util.changeWorkerStatusToOwner(component, workerDOID);

		//Login with a valid user
		String localUserPubKey = "publicKeyL";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), localUserPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, localUserPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		/*LocalWorkerProviderClient lwpc = getMock(NOT_NICE, LocalWorkerProviderClient.class);
		peerAcceptanceUtil.createStub(lwpc, lwpcOID);*/

		replayActiveMocks();
		WorkerAllocation workerCAllocation = new WorkerAllocation(workerCDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, workerCAllocation);

		verifyActiveMocks();
		resetActiveMocks();
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerCDID, workerSpecC, requestSpec1);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec2, 1, 
				allocationWB);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change workers status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDID, workerSpecB, clientID);

		//Verify workers status
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC, workerInfoB, workerInfoA,  
				workerInfoD);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to STOPPED
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerADID);
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerBDID);
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerCDID);
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerDOID);

		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null);

		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC, workerInfoB, workerInfoA, workerInfoD);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer ignores an unknown idle worker.
	 */
	@ReqTest(test="AT-025.1", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_1_ChangeStatusIDLEWithJDL() throws Exception{
		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);

		//Worker login
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Notify DiscoveryService peer recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpec, workerDID, dsID);

		//Get peer's local workers status and expect worker A to be marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IDLE, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer stop advertising an ownered worker. 
	 * @throws Exception
	 */
	@ReqTest(test="AT-025.3", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_3_ChangeStatusIDLE_to_OWNERWithJDL() throws Exception{

		//Worker login
		String workerAUserName = "workerA.ourgrid.org";
		String workerAServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec(workerAUserName, workerAServerName, null, null);

		//Worker login
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerID);

		//Notify DiscoveryService peer recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(
				component, workerSpec, workerID, dsID);

		//Change worker status to OWNER and expect to cancel advert
		req_025_Util.changeWorkerStatusToOwner(component, workerID);

		//Get peer's local workers status and expect worker A to be marked as OWNER
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.OWNER, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/** 
	 * Test change worker public key, during worker failure
	 * @throws Exception
	 */
	@ReqTest(test="AT-025.5", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_5_PubKeyChangeWithJDL() throws Exception{
		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);

		//Worker login
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Notify worker failure
		req_019_Util.notifyWorkerFailure(workerDID, component);

		//Worker login with new public key
		String workerASecondPublicKey = "workerANewPublicKey";
		workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerASecondPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE with the new public key - expect to log the information
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);
	}

	/** 
	 * Verifies if the peer give the worker to consumer, when the worker's state changes to allocated for Broker.
	 */
	@ReqTest(test="AT-025.6", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_6_Idle_to_WorkForBrokerWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);

		//Worker login
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Login with a valid user
		String brokerPubKey = "brokerPublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		WorkerAllocation allocationWorker = new WorkerAllocation(workerDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocationWorker);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerDID, workerSpec, requestSpec1);

		//Verify if the client was marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), brokerPubKey, UserState.CONSUMING);
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		req_106_Util.getUsersStatus(usersInfo);

		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/** 
	 * Verifies if the peer ignore a worker status change, when the worker reference is null.
	 */
	@ReqTest(test="AT-025.7", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_7_Idle_to_WorkForBroker_NullWorkerWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Worker Login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Login with a valid user
		String brokerPubKey = "brokerPublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		WorkerAllocation allocationWorker = new WorkerAllocation(workerDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocationWorker);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.error("Unknown worker changed status: " + workerAPublicKey +"/"+ WorkerStatus.ALLOCATED_FOR_BROKER);

		EasyMock.replay(loggerMock);
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, workerAPublicKey);
		EasyMock.verify(loggerMock);
	}

	/** 
	 * Verifies the peer behavior, when it commands the worker to work for Broker, but the worker
	 * answer with a status changed different of Allocated for Broker.
	 * 
	 * Allocated for peer -> log a warn;
	 * Idle -> log a warn;
	 * Stopped -> ignore this message, for the peer is about to receive a notification of worker failure.
	 * Owner -> mark the worker as OWNER and remove the allocation data;
	 */
	@ReqTest(test="AT-025.8", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_8_Idle_to_WorkForBrokerWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Login with a valid user
		String brokerPubKey = "brokerPublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerDID);
				RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocation);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Create mocks
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		Worker worker = EasyMock.createMock(Worker.class);
		EasyMock.replay(worker);

		//Change worker status to ALLOCATED FOR PEER
		loggerMock.warn("The worker <" + workerDID.getContainerID() + "> (IN_USE) changed its status to ALLOCATED_FOR_PEER. " +
				"This status change was ignored.");
		EasyMock.replay(loggerMock);

		req_025_Util.changeStatusAllocatedForPeer(workerDID, null, component);
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);

		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to IDLE
		loggerMock.debug("Worker <" + workerDID.getContainerID() + "> is now IDLE");
		EasyMock.replay(loggerMock);

		req_025_Util.changeStatus(component, WorkerStatus.IDLE, workerDID);
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);

		//Verify if the worker A was marked as IN_USE
		workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IDLE, null);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to STOPPED
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerDID);

		//Verify if the worker A was marked as IN_USE
		workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IDLE, null);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to OWNER
		loggerMock.info("Worker <"+ workerDID.getContainerID() +"> is now OWNER");
		EasyMock.replay(loggerMock);

		req_025_Util.changeStatus(component, WorkerStatus.OWNER, workerDID);
		EasyMock.verify(loggerMock);

		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerDID.getServiceID()));

		//Verify if the worker A was marked as OWNER
		workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.OWNER);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer ignore the worker's state changes to idle, when it 
	 * did not command the worker to stop working. 
	 */
	@ReqTest(test="AT-025.9", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_9_Changing_status_to_Idle_before_StopWorkingWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
		String workerAPublicKey = "publicKeyA";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);

		//Login with a valid user
		String brokerPubKey = "brokerPublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerDID);
				RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocation);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerDID, workerSpec, 
				requestSpec1);

		//Change worker status to IDLE - expect to log the warn
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.debug("Worker <" + workerDID.getContainerID() + "> is now IDLE");
		EasyMock.replay(loggerMock);
		req_025_Util.changeStatus(component, WorkerStatus.IDLE, workerDID);

		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpec, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer completes a worker preemption.
	 */
	@ReqTest(test="AT-025.10", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_10_WorkerPreemptionWithJDL() throws Exception{
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user012", "server011", "011011");

		//Workers login
		String serverName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", serverName, null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB.ourgrid.org", serverName, null, null);

		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);
		String workerBPublicKey = "workerBPublicKey";
		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);

		//Login with valid users
		String broker1PubKey = "broker1PublicKey";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);

		String broker2PubKey = "broker2PublicKey";

		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broke2r", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);

		try {
			peerControl.addUser(peerControlClient, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);

		//Request workers for user1
		WorkerAllocation allocationA = new WorkerAllocation(workerADID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, allocationB, allocationA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));

		//Change workers status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerBDID, workerSpecB, requestSpec1);
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerADID, workerSpecA, requestSpec1);

		//Request workers for user2
		allocationA = new WorkerAllocation(workerADID).addLoserConsumer(lwpc1OID).addLoserRequestSpec(requestSpec1);
		RequestSpecification requestSpec2 =new RequestSpecification(0, new JobSpecification("label"), 2, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, allocationA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc2OID.getServiceID()));

		//Change workerA status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OID, workerADID, workerSpecA, requestSpec2);

		//Verify if the workerA was preempted
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2OID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc1OID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer give the worker to consumer, when the worker's state changes to allocated for Peer.
	 */
	@ReqTest(test="AT-025.11", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_11_IdleToAllocatedForPeerWithJDL() throws Exception{

		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"025_blank.xml");

		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);

		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec,
				1, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADID, workerSpecA, clientID);

		//Verify if the worker A was marked as DONATED
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer ignore a worker status change, when the worker reference is null.
	 */
	@ReqTest(test="AT-025.12", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_12_IdleToAllocatedForPeerAndWorkerNullWithJDL() throws Exception{

		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"025_blank.xml");

		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);

		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		WorkerManagement workerManagement = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerADID);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.warn("Unknown worker changed status: " + workerAPublicKey +"/" + WorkerStatus.ALLOCATED_FOR_PEER);

		EasyMock.replay(loggerMock);
		replayActiveMocks();

		AcceptanceTestUtil.publishTestObject(component, workerADID, workerManagement, WorkerManagement.class);

		req_025_Util.changeStatusAllocatedForPeer(workerAPublicKey, component);
		EasyMock.verify(loggerMock);
		verifyActiveMocks();
	}

	/**
	 * Verifies the peer behavior, when it commands the worker to work for Peer, 
	 * but the worker answer with a status changed different of Allocated for Peer.
	 * Allocated for Broker -> log a warn;
	 * Idle -> log a warn;
	 * Stopped -> ignore this message, for the peer is about to receive a notification of worker failure.
	 * Owner -> mark the worker as OWNER and remove the allocation data;
	 */
	@ReqTest(test="AT-025.13", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_13_IdleToAllocatedForPeerAndOtherStatesWithJDL() throws Exception{

		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"025_blank.xml");

		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);

		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		Worker worker = getMock(NOT_NICE, Worker.class);

		DeploymentID workerID = new DeploymentID(workerADID.getContainerID(), WorkerConstants.WORKER);

		AcceptanceTestUtil.publishTestObject(component, workerID, worker, Worker.class);
		//peerAcceptanceUtil.createStub(worker, Worker.class, workerADID);

		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.warn("The worker <" + workerADID.getContainerID()+ "> (DONATED) changed its status to ALLOCATED_FOR_BROKER. " +
				"This status change was ignored.");

		EasyMock.replay(loggerMock);
		replayActiveMocks();

		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, workerID.getServiceID(), null, workerADID,
				(WorkerManagement) AcceptanceTestUtil.getBoundObject(workerADID));
		EasyMock.verify(loggerMock);
		verifyActiveMocks();
		resetActiveMocks();
		EasyMock.reset(loggerMock);

		//Change worker status to IDLE
		loggerMock.debug("Worker <" + workerADID.getContainerID() + "> is now IDLE");
		EasyMock.replay(loggerMock);
		replayActiveMocks();

		req_025_Util.changeStatus(component, WorkerStatus.IDLE, workerADID);
		EasyMock.verify(loggerMock);
		verifyActiveMocks();
		resetActiveMocks();
		EasyMock.reset(loggerMock);

		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to STOPPED
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerADID);

		//Verify if the worker A was marked as DONATED
		new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to OWNER
		req_025_Util.changeStatus(component, WorkerStatus.OWNER, workerADID);

		//Verify if the worker A was marked as OWNER
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.OWNER);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 *Verifies if the peer ignore the worker's state changes to idle, when it did not command the worker to stop working.
	 */
	@ReqTest(test="AT-025.14", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_14_DonatedWorkerChangeStatusToIdleWithJDL() throws Exception{
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);

		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADID, workerSpecA, clientID);

		//Change worker status to IDLE - expect to log the warn
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		loggerMock.debug("Worker <" + workerADID.getContainerID() + "> is now IDLE");
		EasyMock.replay(loggerMock);
		replayActiveMocks();
		req_025_Util.changeStatus(component, WorkerStatus.IDLE, workerADID);
		EasyMock.verify(loggerMock);
		verifyActiveMocks();
		resetActiveMocks();

		//Verify if the worker A was marked as DONATED
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer completes a worker preemption.
	 * @throws Exception
	 */
	@ReqTest(test="AT-025.15", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_15_AllocateForPeerPreemptionWithJDL() throws Exception{

		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"025_blank.xml");

		//Workers login
		String serverName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", serverName, null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB.ourgrid.org", serverName, null, null);

		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);

		//Request two workers for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		WorkerAllocation allocationWB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWB, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change workers status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADID, workerSpecA, clientID);

		//worker B
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDID, workerSpecB, clientID);

		//Request a worker for user2
		DeploymentID client2ID = PeerAcceptanceUtil.createRemoteConsumerID("client2", "server", "pubKeyB");
		allocationWA = new WorkerAllocation(workerADID).addLoserConsumer(clientID);
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		rwpc = req_011_Util.requestForRemoteClient(component, client2ID, requestSpec2, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(client2ID.getServiceID()));

		//Change workerB status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component,rwpc, workerADID, workerSpecA, client2ID);

		//Verify if the workerB was preempted
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, client2ID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer completes a worker preemption.
	 */
	@ReqTest(test="AT-025.16", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_16_WorkerPreemptionPeerToBrokerWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Workers login
		String serverName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", serverName, null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB.ourgrid.org", serverName, null, null);

		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);

		//Request two workers for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWA = new WorkerAllocation(workerADID);
		WorkerAllocation allocationWB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		RemoteWorkerProviderClient rwpc = 
				req_011_Util.requestForRemoteClient(component, clientID, requestSpec, 1, allocationWB, allocationWA);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change workers status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADID, workerSpecA, clientID);

		//worker B
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDID, workerSpecB, clientID);

		//Login with a valid user
		String broker1PubKey = "publicKeyL";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}


		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);

		//Request workers for user1
		WorkerAllocation allocationA = new WorkerAllocation(workerADID).addLoserConsumer(clientID);
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec2, allocationA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));

		//Change workers status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerADID, workerSpecA, requestSpec2);

		//Verify if the workerA was preempted
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc1OID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer remove the allocation data of worker whose status changed to OWNER.
	 */
	@ReqTest(test="AT-025.17", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_17_WorkerIDLE_INUSE_DONATED_TO_OWNERWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Workers login
		String serverName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", serverName, null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB.ourgrid.org", serverName, null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("workerC.ourgrid.org", serverName, null, null);

		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		DeploymentID workerCDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDID);

		//Change workers status to IDLE

		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDID);

		//Login with a valid user
		String broker1PubKey = "publicKeyL";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);

		//Request workers for user1
		WorkerAllocation allocationC = new WorkerAllocation(workerCDID);
				RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, allocationC);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));

		//Change workers status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerCDID, workerSpecC, requestSpec1);

		//Request a worker for the remote client
		resetActiveMocks();
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec2, 1, 
				allocationWB);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//worker B
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDID, workerSpecB, clientID);

		//Verify workers status
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc1OID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC, workerInfoB, workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to OWNER
		req_025_Util.changeWorkerStatusToOwner(component, workerADID);
		req_025_Util.changeWorkerStatusToOwner(component, workerBDID);
		req_025_Util.changeWorkerStatusToOwner(component, workerCDID);


		//Verify workers status
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.OWNER, null);
		workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.OWNER, null);
		workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.OWNER, null);

		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if the peer ignore when a worker change its status to STOPPED.
	 */
	@ReqTest(test="AT-025.18", reqs="REQ025")
	@Category(JDLCompliantTest.class) @Test public void test_AT_025_18_ChangeToStoppedWithJDL() throws Exception{
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");

		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S1", null, null);

		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDID);

		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDOID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDID);

		//Change worker status to OWNER
		req_025_Util.changeWorkerStatusToOwner(component, workerDOID);

		//Login with a valid user
		String localUserPubKey = "publicKeyL";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), localUserPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcOID = req_108_Util.login(component, user1, localUserPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);

		//Request a worker for the logged user
		/*LocalWorkerProviderClient lwpc = getMock(NOT_NICE, LocalWorkerProviderClient.class);
		peerAcceptanceUtil.createStub(lwpc, lwpcOID);*/

		replayActiveMocks();
		WorkerAllocation workerCAllocation = new WorkerAllocation(workerCDID);
				RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, workerCAllocation);

		verifyActiveMocks();
		resetActiveMocks();
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));

		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerCDID, workerSpecC, requestSpec1);

		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("client", "server", "pubKeyA");
		WorkerAllocation allocationWB = new WorkerAllocation(workerBDID);
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec2, 1, 
				allocationWB);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));

		//Change workers status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDID, workerSpecB, clientID);

		//Verify workers status
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC, workerInfoB, workerInfoA,  
				workerInfoD);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Change worker status to STOPPED
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerADID);
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerBDID);
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerCDID);
		req_025_Util.changeStatus(component, WorkerStatus.STOPPED, workerDOID);

		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null);

		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC, workerInfoB, workerInfoA, workerInfoD);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
}