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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.After;
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
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ011")
public class Req_011_Test extends PeerAcceptanceTestCase{

	
	public static final String COMM_FILE_PATH = "req_011"+File.separator;
	private PeerComponent peerComponent;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private PeerAcceptanceUtil peerAcceptanceUtil = new PeerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
	private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
	private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
	private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
	private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
	private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
	private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
	private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
	private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
	
	@Before
	public void setUp() throws Exception {
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.delete();
		super.setUp();
	} 
	
	@After
	public void tearDown() throws Exception {
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.delete();
		super.tearDown();
	}
	
	/**
	 * Validate worker's requests:
	 * A client cannot use other public key, distinct of the one used on login, to request workers;
	 * A client cannot request less than one worker;
	 * A client cannot do a null request;
	 * A request cannot be done without a public key.
	 */
	@ReqTest(test="AT-011.1", reqs="REQ011")
	@Test public void test_at_011_1_Request_Input_Validation() throws Exception {
	
		//create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();

		//Login with a valid user
		String brokerPubKey = "publicKeyA";
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		
		//Request workers for the logged user with other sender public key
		long requestID = 1;
		String unknowPubKey = "publicKeyB";
		loggerMock.warn("Request " + requestID + ": request ignored because its public key is unknown: " + unknowPubKey);
		
		LocalWorkerProviderClient lwpc = EasyMock.createMock(LocalWorkerProviderClient.class);
		
		EasyMock.replay(loggerMock);
		EasyMock.replay(lwpc);
		
		LocalWorkerProvider peer = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		ObjectDeployment peerDeployment = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		DeploymentID peerID = new DeploymentID(new ContainerID("peer", "peer", "peer", unknowPubKey), "");
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerDeployment, peerID);
		
		peer.requestWorkers(new RequestSpecification(0, new JobSpecification("label"), 1, "Req", 1, 0, 0));
		
		EasyMock.verify(loggerMock);
		EasyMock.verify(lwpc);
		EasyMock.reset(loggerMock);
		EasyMock.reset(lwpc);
		
		//Request no workers for the logged user
		int request2ID = 2; 
		
		peerID = new DeploymentID(new ContainerID("peer", "peer", "peer", brokerPubKey), "");
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerDeployment, lwpcOID);
		loggerMock.warn("Request "+request2ID+": request ignored because [" + lwpcOID.getServiceID() + "] requested less than 1 worker");
		
		EasyMock.replay(loggerMock); 
		peer.requestWorkers(new RequestSpecification(0, new JobSpecification("label"), request2ID, "", 0, 0, 0));
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Null request
		loggerMock.warn("Client [" + lwpcOID.getServiceID() + "] done a null request. This request was ignored.");
		EasyMock.replay(loggerMock);

		AcceptanceTestUtil.setExecutionContext(peerComponent, peerDeployment, peerID);
		peer.requestWorkers(null);

		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
	}
	
	/**
	 * A client requests one worker. Verify if:
	 * The worker received a work for my grid message;
	 * The peer is interested on client failure;
	 * The client was marked as CONSUMING;
	 * The worker was marked as IN_USE and has the client public key.
	 * @throws Exception
	 */
	@Test public void test_at_011_2_Local_Alocation() throws Exception {

		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
	    String brokerPubKey = "publicKeyA";
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("peerClient", "peerClientServer", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		//Notify recovery of WorkerA
		String workerAPubKey = "publicKeyWorkerA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);

		//Change workerA status to idle
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		
		//Login with a valid user
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		 
		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerADeploymentID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec, allocation);
		
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
		
		//Verify if the client was marked as CONSUMING
        UserInfo userInfo1 = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.CONSUMING);
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	/**
	 * Verifies if peer forwards the request to community, when the local redistribution do not satisfy the request.
	 * In this case, the request is also scheduled for repetition in 120 seconds.
	 * @throws Exception 
	 */
	@Test public void test_at_011_3_Forward_to_community() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_3", "server011", "011011");

		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();

		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		String brokerPubKey = "brokerPublicKey";
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//TODO req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);

		//Client login and request workers - expect to forward request to the community
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		 
		//Request a worker for the logged user
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec);
		
	}
	
	/**
	 * Verifies if the peer does not forward the request to community and does not schedule 
	 * the request for repetition, because the local redistribution satisfies the request.
	 * 
	 * @throws Exception
	 */
	@Test public void test_at_011_4_Local_workers_are_enough() throws Exception{

		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		//Worker login
		//Create Worker specs
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", workerServerName);
		
		//Notify recovery of WorkerA
		String workerAPubKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerAID);

		//Change workerA status to idle
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerAID);
		
		String brokerPubKey = "brokerPublicKey";
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("peerClient", "peerClientServer", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
        
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(getDSUserName(), getDSServerName(), 
				DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
        req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers - expect to do not forward request to the community
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		WorkerAllocation allocation = new WorkerAllocation(workerAID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc) , spec, allocation);
	}
	/**
	* Validate worker's requests:
	* A client cannot request less than one worker;
    * A client cannot do a null request;
    * A request cannot be done without cslient public key;
    * A request cannot be done without client.
    */
	@Test public void test_at_011_5_Request_Input_Remote_Request_And_Request_Spec_Validation() throws Exception {


		//Create an user account
		req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mock for logger
		peerComponent = req_010_Util.startPeer();
		
		//Request no workers for the remote client
		DeploymentID clientID = new DeploymentID(new ServiceID("client", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		
		RemoteWorkerProvider peer = peerAcceptanceUtil.getRemoteWorkerProviderProxy();
		RemoteWorkerProviderClient client = EasyMock.createMock(RemoteWorkerProviderClient.class);
		
		String pubKeyA = "pubKeyA";
		
		ObjectDeployment peerControlDeployment = getPeerControlDeployment(peerComponent);
		
		clientID.setPublicKey(pubKeyA);
		
		AcceptanceTestUtil.publishTestObject(peerComponent, clientID, client, RemoteWorkerProviderClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, clientID);
		
		long requestID = 1;
		loggerMock.warn("Request "+requestID+": request ignored because [" + 
				clientID.getServiceID().toString() + "] requested less than 1 worker");
		EasyMock.replay(loggerMock);
		EasyMock.replay(client);
		
		peer.requestWorkers(client, new RequestSpecification(0, new JobSpecification("label"), requestID, "", 0, 0, 0));
		
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));
		
		EasyMock.verify(loggerMock);
		EasyMock.verify(client);
		
		EasyMock.reset(loggerMock);
		EasyMock.reset(client);

		
		//Null request
		loggerMock.warn("Client [" + clientID.getServiceID().toString() + "] done a null request. This request was ignored.");
		EasyMock.replay(loggerMock);
		EasyMock.replay(client);
		
		AcceptanceTestUtil.publishTestObject(peerComponent, clientID, client, RemoteWorkerProviderClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, clientID);
		
		peer.requestWorkers(client, null);
		
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));
		EasyMock.verify(loggerMock);
		EasyMock.verify(client);

		EasyMock.reset(loggerMock);
		EasyMock.reset(client);
		
		//Request with a null client public key
		clientID.setPublicKey(null);
		long request2 = 2;
		loggerMock.warn("Request " + request2 + ": request ignored because it has not a public key.");
		EasyMock.replay(loggerMock);
		EasyMock.replay(client);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, clientID);
		AcceptanceTestUtil.publishTestObject(peerComponent, clientID, client, RemoteWorkerProviderClient.class);
		
		peer.requestWorkers(client, new RequestSpecification(0, new JobSpecification("label"), request2, "", 1, 0, 0));
		
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));
		EasyMock.verify(loggerMock);
		EasyMock.verify(client);

		EasyMock.reset(loggerMock);
		EasyMock.reset(client);
		
		//Request with a null client
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, pubKeyA);
		AcceptanceTestUtil.publishTestObject(peerComponent, clientID, client, RemoteWorkerProviderClient.class);
		long requestID3 = 3;
		loggerMock.warn("Request "+requestID3+": request ignored because it has not a client.");
		EasyMock.replay(loggerMock);
		EasyMock.replay(client);

		peer.requestWorkers(null, new RequestSpecification(0, new JobSpecification("label"), requestID3, "", 0, 0, 0));
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));
		EasyMock.verify(loggerMock);
		EasyMock.verify(client);

	}
	
	/**
	 * A remote client request one worker. Verify if:
	 * The worker received a work for peer message;
	 * The peer is interested on client failure;
	 * The worker was marked as DONATED and has the client public key.
	 * @throws Exception 
	 */
	@Test public void test_at_011_6_Request_Remote_Allocation() throws Exception {
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_blank.xml");
		
		//Worker login
		//Create Worker specs
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", workerServerName);
		
		//Notify recovery of WorkerA
		String workerAPubKey = "publicKeyA";
		DeploymentID workerADID = req_019_Util.createWorkerManagementDeploymentID(workerAPubKey, workerSpecA);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADID);

		//Change workerA status to idle
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADID);
		
		DeploymentID clientID = new DeploymentID(
                new ServiceID("client", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		String pubKeyClient1 = "pubKeyA";
		clientID.setPublicKey(pubKeyClient1);
		
		WorkerAllocation allocation = new WorkerAllocation(workerADID);		
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForRemoteClient(peerComponent, clientID, requestSpec, 1, allocation);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnRemoteClient(clientID.getServiceID()));
		
		//Verify if the worker A was marked as DONATED
		//Create expected result
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, 
				clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	@Test public void test_at_011_7_Local_Redistribution() throws Exception{

		peerComponent = req_010_Util.startPeer();
		
		//Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011-1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011-2", "server011", "011012");
		XMPPAccount user3 = req_101_Util.createLocalUser("user011-3", "server011", "011013");
		XMPPAccount user4 = req_101_Util.createLocalUser("user011-4", "server011", "011014");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker", "broker"), "user2");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID2, peerControlClient2, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient3 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID3 = new DeploymentID(new ContainerID("pcc3", "broker", "broker"), "user3");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID3, peerControlClient3, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID3);
		
		try {
			peerControl.addUser(peerControlClient3, user3.getUsername() + "@" + user3.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient4 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID4 = new DeploymentID(new ContainerID("pcc4", "broker", "broker"), "user4");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID4, peerControlClient4, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID4);
		
		try {
			peerControl.addUser(peerControlClient4, user4.getUsername() + "@" + user4.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		
		
		String workerAPubKey = "publicKeyA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		
		//Login with four valid users
		String client1PubKey = "user1PubKey";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, client1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID1);
		
		String client2PubKey = "user2PubKey";
		DeploymentID lwpcOID2 = req_108_Util.login(peerComponent, user2, client2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID2);
		
		String client3PubKey = "user3PubKey";
		DeploymentID lwpcOID3 = req_108_Util.login(peerComponent, user3, client3PubKey);
		LocalWorkerProviderClient lwpc3 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID3);
		
		String client4PubKey = "user4PubKey";
		DeploymentID lwpcOID4 = req_108_Util.login(peerComponent, user4, client4PubKey);
		LocalWorkerProviderClient lwpc4 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID4);
		
		
		//Request three workers for client1 - expect to obtain all of them
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 3, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID1, lwpc), spec, allocationC, allocationB, 
				allocationA);
				
		//Request two workers for client2 - expect to obtain one of them - the most recently allocated on client1
		allocationA = new WorkerAllocation(workerADeploymentID).addLoserConsumer(lwpcOID1).addLoserRequestSpec(spec);
		RequestSpecification spec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 2, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID2, lwpc2), spec2, allocationA);
		
		//Request one worker for client3 - expect to obtain one of them - the most recently allocated on client1
		
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(lwpcOID1);
		RequestSpecification spec3 = new RequestSpecification(0, new JobSpecification("label"), 3, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID3, lwpc3), spec3, allocationB);
		
		//Request one worker for client4 - expect to obtain none of them
		RequestSpecification spec4 = new RequestSpecification(0, new JobSpecification("label"), 4, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID4, lwpc4), spec4);
		
		//Verify the clients' status
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), client1PubKey, UserState.CONSUMING);
		UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), client2PubKey, UserState.CONSUMING);
		UserInfo userInfo3 = new UserInfo(user3.getUsername(), user3.getServerAddress(), client3PubKey, UserState.CONSUMING);
		UserInfo userInfo4 = new UserInfo(user4.getUsername(), user4.getServerAddress(), client4PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2, userInfo3, userInfo4);
	    
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID2.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpcOID3.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	@Test public void test_at_011_8_Local_Redistribution_WithoutNOF() throws Exception{
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_blank.xml");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		
		//Request three workers for client1 - expect to obtain all of them
		DeploymentID clientID = new DeploymentID(
                new ServiceID("client", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String client1PubKey = "client1PubKey";
		clientID.setPublicKey(client1PubKey);
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 3, 0, 0);
		
		req_011_Util.requestForRemoteClient(peerComponent, clientID, requestSpec, 1, 
				allocationC, allocationB, allocationA);
		
		//Request two workers for client2 - expect to obtain one of them - the most recently allocated on client1
		DeploymentID client2ID = new DeploymentID(
                new ServiceID("client2", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String client2pubKey = "client2PubKey";
		client2ID.setPublicKey(client2pubKey);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 2, 0, 0);
		
		allocationA = new WorkerAllocation(workerADeploymentID).addLoserConsumer(clientID);
		req_011_Util.requestForRemoteClient(peerComponent, client2ID, requestSpec2, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationA);
		
		//Request one worker for client3 - expect to obtain one of them - the most recently allocated on client1
		DeploymentID client3ID = new DeploymentID(
                new ServiceID("client3", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String client3PubKey = "client3PubKey";
		client3ID.setPublicKey(client3PubKey);
		
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, "", 1, 0, 0);
		
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(clientID);
		req_011_Util.requestForRemoteClient(peerComponent, client3ID, requestSpec3, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationB);
		
		//Request one worker for client4 - expect to obtain none of them
		DeploymentID client4ID = new DeploymentID(
                new ServiceID("client4", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String client4PubKey = "client4PubKey";
		client4ID.setPublicKey(client4PubKey);
		
		RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), 4, "", 1, 0, 0);
		
		req_011_Util.requestForRemoteClient(peerComponent, client4ID, requestSpec4,  
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES);
		
		//Verify the workers' status
		//Create expected result
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, client2ID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, client3ID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	@Test public void test_at_011_9_Local_And_Remote_Redistribution() throws Exception{
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_blank.xml");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		//Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011-1", "server011", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);

		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		
		//Login with a valid user
		String client1PubKey = "user1PubKey";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, client1PubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID1);
		
		//Request two workers for a remote client - expect to obtain two workers
		DeploymentID remoteClientID = new DeploymentID(
                new ServiceID("client", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String remoteClientPubKey = "remoteClientPubKey";
		remoteClientID.setPublicKey(remoteClientPubKey);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0); 
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, remoteClientID, requestSpec, 
				1, allocationC, allocationB);
		
		//Request one worker for a local client - expect to obtain two workers
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(remoteClientID);
		
		RequestSpecification spec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 2, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID1, lwpc), spec2, allocationB, allocationA);

		//Verify the clients' status 
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), client1PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, remoteClientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	@Test public final void test_at_011_10_SubCommunities_Redistribution() throws Exception{
		
		/**
			<trusts>
			    <trust>
			    	<name>subA</name>
			      	<priority>1</priority>
			        <peer> 
			        	<name>peer11</name>
			            <publickey>publicKey11</publickey>
			        </peer>
			    </trust>
			    <trust>
			    	<name>subB</name>
			    	<priority>2</priority>
			        <peer>
			    		<publickey>publicKey21</publickey> 
			    		<name>peer21</name>
			    	</peer>
			    	<peer>
			    		<publickey>publicKey22</publickey> 
			    		<name>peer22</name>
			    	</peer>
			    </trust>
			</trusts>
		 */
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_10.xml");
		
		//Start the peer with a well formed trust configuration file
		peerComponent = req_010_Util.startPeer();
		
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S1");
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);

		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);

		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);

		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecD, workerDDeploymentID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerDDeploymentID);
		
		//Request three workers for peer21 - expect to obtain three workers
		DeploymentID peer21DeploymentID = new DeploymentID(
                new ServiceID("peer21", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer21PubKey = "publicKey21";
		peer21DeploymentID.setPublicKey(peer21PubKey);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 3, 0, 0);
		
		WorkerAllocation allocationD = new WorkerAllocation(workerDDeploymentID);
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, peer21DeploymentID,
				requestSpec, 2, allocationD, allocationC, allocationB);
		
		//Request three workers for peer22 - expect to obtain two workers
		DeploymentID peer22DeploymentID = new DeploymentID(
                new ServiceID("peer22", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer22PubKey = "publicKey22";
		peer22DeploymentID.setPublicKey(peer22PubKey);

		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 3, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(peer21DeploymentID);
		req_011_Util.requestForRemoteClient(peerComponent, peer22DeploymentID, requestSpec2,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationA, allocationB);
		
		//Request two workers for peer11 - expect to obtain two workers
		DeploymentID peer11DeploymentID = new DeploymentID(
                new ServiceID("peer11", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer11PubKey = "publicKey11";
		peer11DeploymentID.setPublicKey(peer11PubKey);
		
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, "", 2, 0, 0);
		
		allocationC = new WorkerAllocation(workerCDeploymentID).addLoserConsumer(peer21DeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(peer22DeploymentID);
		req_011_Util.requestForRemoteClient(peerComponent, peer11DeploymentID, requestSpec3, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC, allocationB);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, peer22DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, peer11DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, peer11DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.DONATED, peer21DeploymentID.getServiceID().toString());
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC, workerInfoD);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	@Test public final void test_at_011_11_SubCommunities_Redistribution() throws Exception{
		
		/**
		 * 
		 * <trusts>
			    <trust>
			    	<name>subA</name>
			      	<priority>1</priority>
			        <peer> 
			        	<name>peer11</name>
			            <publickey>publicKey11</publickey>
			        </peer>
			    </trust>
			    <trust>
			    	<name>subB</name>
			    	<priority>2</priority>
			    	<peer>
			    		<publickey>publicKey21</publickey> 
			    		<name>peer21</name>
			    	</peer> 
			    </trust>
			    <trust>
			       	<name>subC</name>
			    	<priority>2</priority>
			    	<peer>
			    		<publickey>publicKey22</publickey> 
			    		<name>peer22</name>
			    	</peer>
			    </trust>
			</trusts>
		 * 
		 */
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_11.xml");
		
		//Start the peer with a well formed trust configuration file
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S1");
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);

		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);

		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);

		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecD, workerDDeploymentID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerDDeploymentID);
		
		//Request three workers for peer21 - expect to obtain three workers
		DeploymentID peer21DeploymentID = new DeploymentID(
                new ServiceID("peer21", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer21PubKey = "publicKey21";
		peer21DeploymentID.setPublicKey(peer21PubKey);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 3, 0, 0);
		
		WorkerAllocation allocationD = new WorkerAllocation(workerDDeploymentID);
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, peer21DeploymentID, requestSpec, 3, allocationD,
				allocationC, allocationB);
		
		//Request three workers for peer22 - expect to obtain two workers
		DeploymentID peer22DeploymentID = new DeploymentID(
                new ServiceID("peer22", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer22PubKey = "publicKey22";
		peer22DeploymentID.setPublicKey(peer22PubKey);

		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 3, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(peer21DeploymentID);
		req_011_Util.requestForRemoteClient(peerComponent, peer22DeploymentID, requestSpec2, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationA, allocationB);
		
		//Request two workers for peer11 - expect to obtain two workers
		DeploymentID peer11DeploymentID = new DeploymentID(
                new ServiceID("peer11", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer11PubKey = "publicKey11";
		peer11DeploymentID.setPublicKey(peer11PubKey);
		
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, "", 2, 0, 0);
		
		allocationC = new WorkerAllocation(workerCDeploymentID).addLoserConsumer(peer21DeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(peer22DeploymentID);
		req_011_Util.requestForRemoteClient(peerComponent, peer11DeploymentID, requestSpec3, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC, allocationB);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, peer22DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, peer11DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, peer11DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.DONATED, peer21DeploymentID.getServiceID().toString());
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC, workerInfoD);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	@Test public final void test_at_011_12_Local_Remote_And_SubCommunities_Redistribution() throws Exception {
		
		
		/**
		 * Contents of trust.xml:
			<trusts>
			    <trust>
			    	<name>subA</name>
			      	<priority>1</priority>
			        <peer> 
			        	<name>peer11</name>
			            <publickey>publicKey11</publickey>
			        </peer>
			    </trust>
			    <trust>
			    	<name>subB</name>
			    	<priority>2</priority>
			        <peer>
			    		<publickey>publicKey21</publickey> 
			    		<name>peer21</name>
			    	</peer>
			    	<peer>
			    		<publickey>publicKey22</publickey> 
			    		<name>peer22</name>
			    	</peer>
			    </trust>
			</trusts>
		 */
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_12.xml");
		
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011012");
		
		//Start the peer with a well formed trust configuration file
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S1");
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createWorkerSpec("U5", "S1");
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createWorkerSpec("U6", "S1");
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);

		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);

		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);

		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecD, workerDDeploymentID);

		String workerEPubKey = "publicKeyWE";
		DeploymentID workerEDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecE, workerEPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecE, workerEDeploymentID);

		String workerFPubKey = "publicKeyWF";
		DeploymentID workerFDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecF, workerFPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecF, workerFDeploymentID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerDDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerEDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerFDeploymentID);
		
		//Login with two valid users
		String localWP1PubKey = "localWP1";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), localWP1PubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user1, localWP1PubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
		String localWP2PubKey = "localWP2";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), localWP2PubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID2 = req_108_Util.login(peerComponent, user2, localWP2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID2);
		
		//Request one worker for local1
		WorkerAllocation allocationF = new WorkerAllocation(workerFDeploymentID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec, allocationF);
		
		//Request two workers for remote1
		DeploymentID remoteClient1ID = new DeploymentID(
                new ServiceID("client1", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String remoteClient1PubKey = "remoteClient1PubKey";
		remoteClient1ID.setPublicKey(remoteClient1PubKey);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 2, 0, 0);
		
		WorkerAllocation allocationE = new WorkerAllocation(workerEDeploymentID);
		WorkerAllocation allocationD = new WorkerAllocation(workerDDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, remoteClient1ID, requestSpec2, 2, allocationE,
				allocationD);
		
		//Request two workers for remote2
		DeploymentID remoteClient2ID = new DeploymentID(
                new ServiceID("client2", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String remoteClient2PubKey = "remoteClient2PubKey";
		remoteClient2ID.setPublicKey(remoteClient2PubKey);
		
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, "", 2, 0, 0);
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, remoteClient2ID, requestSpec3, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC, allocationB);
		
		//Verify the workers' status
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, remoteClient2ID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, remoteClient2ID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.DONATED, remoteClient1ID.getServiceID().toString());
		WorkerInfo workerInfoE = new WorkerInfo(workerSpecE, LocalWorkerState.DONATED, remoteClient1ID.getServiceID().toString());
		WorkerInfo workerInfoF = new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoF, workerInfoB, workerInfoC, 
				workerInfoD, workerInfoE, workerInfoA);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//Request two workers for sub11
		DeploymentID sub11RemoteClientID = new DeploymentID(
                new ServiceID("sub11", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String sub11RemoteClientPubKey = "publicKey11";
		sub11RemoteClientID.setPublicKey(sub11RemoteClientPubKey);

		RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), 4, "", 2, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(remoteClient2ID);
		req_011_Util.requestForRemoteClient(peerComponent, sub11RemoteClientID, requestSpec4,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationA, allocationB);
		
		//Request one worker for sub21
		DeploymentID sub21RemoteClientID = new DeploymentID(
                new ServiceID("sub21", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String sub21RemoteClientPubKey = "publicKey21";
		sub21RemoteClientID.setPublicKey(sub21RemoteClientPubKey);

		RequestSpecification requestSpec5 = new RequestSpecification(0, new JobSpecification("label"), 5, "", 1, 0, 0);
		
		allocationD = new WorkerAllocation(workerDDeploymentID).addLoserConsumer(remoteClient1ID);
		req_011_Util.requestForRemoteClient(peerComponent, sub21RemoteClientID, requestSpec5, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationD);
		
		//Request one worker for sub22
		DeploymentID sub22RemoteClientID = new DeploymentID(
                new ServiceID("sub22", "server", PeerConstants.MODULE_NAME, 
                									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String sub22RemoteClientPubKey = "publicKey22";
		sub22RemoteClientID.setPublicKey(sub22RemoteClientPubKey);
		
		RequestSpecification requestSpec6 = new RequestSpecification(0, new JobSpecification("label"), 6, "", 1, 0, 0);
		
		allocationC = new WorkerAllocation(workerCDeploymentID).addLoserConsumer(remoteClient2ID);
		req_011_Util.requestForRemoteClient(peerComponent, sub22RemoteClientID, requestSpec6, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC);
		
		//Verify the workers' status
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, sub11RemoteClientID.getServiceID().toString());
		workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, sub11RemoteClientID.getServiceID().toString());
		workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, sub22RemoteClientID.getServiceID().toString());
		workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.DONATED, sub21RemoteClientID.getServiceID().toString());
		workerInfoE = new WorkerInfo(workerSpecE, LocalWorkerState.DONATED, remoteClient1ID.getServiceID().toString());
		workerInfoF = new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoF, workerInfoA, workerInfoB,  
				workerInfoC, workerInfoD, workerInfoE);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);

		//Request four workers for local2
		allocationE = new WorkerAllocation(workerEDeploymentID).addLoserConsumer(remoteClient1ID);
		allocationC = new WorkerAllocation(workerCDeploymentID).addLoserConsumer(sub22RemoteClientID);
		allocationD = new WorkerAllocation(workerDDeploymentID).addLoserConsumer(sub21RemoteClientID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(sub11RemoteClientID);
		RequestSpecification spec7 = new RequestSpecification(0, new JobSpecification("label"), 7, "", 4, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID2, lwpc2), spec7, allocationE, allocationC, allocationD, allocationB);
		
		//Request two workers for local1
		allocationA = new WorkerAllocation(workerADeploymentID).addLoserConsumer(sub11RemoteClientID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(lwpcOID2).addLoserRequestSpec(
				new RequestSpecification(0, new JobSpecification("label"), 7, "", 4, 0, 0));
		RequestSpecification spec8 = new RequestSpecification(0, new JobSpecification("label"), 8, "", 2, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec8, allocationA, allocationB);

		//Verify the workers' status
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpcOID2.getServiceID().toString());
		workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.IN_USE, lwpcOID2.getServiceID().toString());
		workerInfoE = new WorkerInfo(workerSpecE, LocalWorkerState.IN_USE, lwpcOID2.getServiceID().toString());
		workerInfoF = new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB,  
				workerInfoC, workerInfoD, workerInfoE, workerInfoF);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}



// =================================== Qualification Tests ===================================

	/**
	 * TODO Javadoc
	 * @author melina
	 * @Date 23/04/2008   
	 */
	@Test public void test_invalid_request_negativeNumber() throws Exception {
		// create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_test", "server011",
				"011011");
		
		// Start peer and set a mock log
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

		// Login with a valid user
		String brokerPubKey = "publicKeyA";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);

		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		peerComponent.setLogger(loggerMock);

		LocalWorkerProvider peer = peerAcceptanceUtil.getLocalWorkerProviderProxy();

		// Request no workers for the logged user
		int request2ID = 5;
		
		ObjectDeployment peerControlDeployment = getPeerControlDeployment(peerComponent);
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, lwpcOID);
		loggerMock.warn("Request " + request2ID + ": request ignored because ["
				+ lwpcOID.getServiceID() + "] requested less than 1 worker");

		EasyMock.replay(loggerMock);
		
		peer.requestWorkers(new RequestSpecification(0, new JobSpecification("label"), request2ID, "", -1, 0, 0));

		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
	}


	/**
	 * TODO Javadoc
	 * @author melina
	 * @Date 23/04/2008   
	 */
	@Test public void test_invalid_wokers() throws Exception {

		// create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011",
				"011011");

		// Start peer and set a mock log
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
		
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		EasyMock.reset(loggerMock);

		// Login with a valid user
		String brokerPubKey = "publicKeyA";
		DeploymentID clientID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProvider peer = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		
		// Request no workers for the logged user
		int request2ID = 1;
		
		ObjectDeployment peerDeployment = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		DeploymentID peerID = new DeploymentID(new ContainerID("peer", "peer", "peer", brokerPubKey), "");
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerDeployment, peerID);
		loggerMock.warn("Request "+request2ID+": request ignored because [" + 
				clientID.getServiceID() + "] requested less than 1 worker");
		EasyMock.replay(loggerMock);
		
		peer.requestWorkers(new RequestSpecification(0, new JobSpecification("label"), request2ID, "", 0, 0, 0));
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
	}
	
	/**
	 * TODO javadoc
	 * @author gustavopf
	 * @author melina
	 * @author giovanni
	 * @Date 23/04/2008   
	 */
	@Test public void testRequestWorker2() throws Exception{

		//Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011-1", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", workerServerName);
		workerSpecA.putAttribute("mem", "256");
		
		
		String workerAPubKey = "publicKeyA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Login with four valid users
		String client1PubKey = "user1PubKey";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, client1PubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID1);

		
		//Request three workers for client1 - expect to obtain all of them
		
		
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "mem < -1", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID1, lwpc), spec);
				
		//Verify the clients' status
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), client1PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
	    
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify the workers' status	
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	/**
	 * TODO Javadoc
	 * @throws Exception
	 */
	@Test public void testRequestWorker3() throws Exception{

		//Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011-1", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", workerServerName);
		workerSpecA.putAttribute("mem", "256");
		
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", workerServerName);
		workerSpecB.putAttribute("mem", "156");
		
		
		String workerAPubKey = "publicKeyA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);

		
		String workerBPubKey = "publicKeyB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		
		//Login with four valid users
		String client1PubKey = "user1PubKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), client1PubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, client1PubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID1);

		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		
		//Request three workers for client1 - expect to obtain all of them
		
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "mem == 256", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID1, lwpc), spec, allocationA);
				
		//Verify the clients' status
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), client1PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
	    
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IDLE, null);
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	/**
	 * 
	 * @author gustavopf
	 * @Date 24/04/2008
	 */
	@Test public void test_at_011_PeerDisinterestedOnLocalWorkerFailure() throws Exception {

		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("worker1", "workerServer");
		String workerAPubKey = "publicKeyWorkerA";
		DeploymentID workerADID = req_019_Util.createWorkerManagementDeploymentID(workerAPubKey, workerSpecA);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADID);
		
		//Change workerA status to idle
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADID);
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
		
		//Login with a valid user
		String brokerPubKey = "publicKeyA";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerADID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec, allocation);
		
		// Verify if peer is interested in the client failure.
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
		
		// In this moment, the client is marked as CONSUMING and the worker
		// is market as IN_USE.		
		
		// Verify if peer is interested in the worker failure. 
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADID.getServiceID()));
	}

	/**
	 * Validate worker's requests:
	 * A client cannot use other public key, distinct of the one used on login, to request workers;
	 * A client cannot request less than one worker;
	 * A client cannot do a null request;
	 * A request cannot be done without a public key.
	 */
	@ReqTest(test="AT-011.1", reqs="REQ011")
	@Category(JDLCompliantTest.class) @Test public void test_at_011_1_Request_Input_ValidationWithJDL() throws Exception {
	
		//create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
	
		//Login with a valid user
		String brokerPubKey = "publicKeyA";
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		
		//Request workers for the logged user with other sender public key
		long requestID = 1;
		String unknowPubKey = "publicKeyB";
		loggerMock.warn("Request " + requestID + ": request ignored because its public key is unknown: " + unknowPubKey);
		
		LocalWorkerProviderClient lwpc = EasyMock.createMock(LocalWorkerProviderClient.class);
		
		EasyMock.replay(loggerMock);
		EasyMock.replay(lwpc);
		
		LocalWorkerProvider peer = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		ObjectDeployment peerDeployment = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		DeploymentID peerID = new DeploymentID(new ContainerID("peer", "peer", "peer", unknowPubKey), "");
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerDeployment, peerID);
		
		peer.requestWorkers(new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0));
		
		EasyMock.verify(loggerMock);
		EasyMock.verify(lwpc);
		EasyMock.reset(loggerMock);
		EasyMock.reset(lwpc);
		
		//Request no workers for the logged user
		int request2ID = 2; 
		
		peerID = new DeploymentID(new ContainerID("peer", "peer", "peer", brokerPubKey), "");
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerDeployment, lwpcOID);
		loggerMock.warn("Request "+request2ID+": request ignored because [" + lwpcOID.getServiceID() + "] requested less than 1 worker");
		
		EasyMock.replay(loggerMock); 
		peer.requestWorkers(new RequestSpecification(0, new JobSpecification("label"), request2ID, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 0, 0, 0));
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Null request
		loggerMock.warn("Client [" + lwpcOID.getServiceID() + "] done a null request. This request was ignored.");
		EasyMock.replay(loggerMock);
	
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerDeployment, peerID);
		peer.requestWorkers(null);
	
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
	}

	/**
	 * A client requests one worker. Verify if:
	 * The worker received a work for my grid message;
	 * The peer is interested on client failure;
	 * The client was marked as CONSUMING;
	 * The worker was marked as IN_USE and has the client public key.
	 * @throws Exception
	 */
	@Category(JDLCompliantTest.class) @Test public void test_at_011_2_Local_AlocationWithJDL() throws Exception {
	
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
	    String brokerPubKey = "publicKeyA";
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("peerClient", "peerClientServer", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec( "U1", "S1", null, null);
		
		//Notify recovery of WorkerA
		String workerAPubKey = "publicKeyWorkerA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);

		//Change workerA status to idle
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		
		//Login with a valid user
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		 
		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerADeploymentID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec, allocation);
		
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
		
		//Verify if the client was marked as CONSUMING
	    UserInfo userInfo1 = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify if the worker A was marked as IN_USE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies if peer forwards the request to community, when the local redistribution do not satisfy the request.
	 * In this case, the request is also scheduled for repetition in 120 seconds.
	 * @throws Exception 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_at_011_3_Forward_to_communityWithJDL() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_3", "server011", "011011");
	
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
	
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		String brokerPubKey = "brokerPublicKey";
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//TODO req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
	
		//Client login and request workers - expect to forward request to the community
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		 
		//Request a worker for the logged user
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec);
		
	}

	/**
	 * Verifies if the peer do not forward the request to community and do not schedule the request for repetition,
	 * when the local redistribution satisfies the request.
	 * 
	 * @throws Exception
	 */
	@Category(JDLCompliantTest.class) @Test public void test_at_011_4_Local_workers_are_enoughWithJDL() throws Exception{
	
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		//Worker login
		//Create Worker specs
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec( "U1", workerServerName, null, null);
		
		//Notify recovery of WorkerA
		String workerAPubKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerAID);

		//Change workerA status to idle
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerAID);
		
		String brokerPubKey = "brokerPublicKey";
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("peerClient", "peerClientServer", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
	    
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(getDSUserName(), getDSServerName(), 
				DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
	    req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers - expect to do not forward request to the community
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		WorkerAllocation allocation = new WorkerAllocation(workerAID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc) , spec, allocation);
	}

	/**
	* Validate worker's requests:
	* A client cannot request less than one worker;
	* A client cannot do a null request;
	* A request cannot be done without cslient public key;
	* A request cannot be done without client.
	*/
	@Category(JDLCompliantTest.class) @Test public void test_at_011_5_Request_Input_Remote_Request_And_Request_Spec_ValidationWithJDL() throws Exception {
	
	
		//Create an user account
		req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mock for logger
		peerComponent = req_010_Util.startPeer();
		
		//Request no workers for the remote client
		DeploymentID clientID = new DeploymentID(new ServiceID("client", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		
		RemoteWorkerProvider peer = peerAcceptanceUtil.getRemoteWorkerProviderProxy();
		RemoteWorkerProviderClient client = EasyMock.createMock(RemoteWorkerProviderClient.class);
		
		String pubKeyA = "pubKeyA";
		
		ObjectDeployment peerControlDeployment = getPeerControlDeployment(peerComponent);
		
		clientID.setPublicKey(pubKeyA);
		
		AcceptanceTestUtil.publishTestObject(peerComponent, clientID, client, RemoteWorkerProviderClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, clientID);
		
		long requestID = 1;
		loggerMock.warn("Request "+requestID+": request ignored because [" + 
				clientID.getServiceID().toString() + "] requested less than 1 worker");
		EasyMock.replay(loggerMock);
		EasyMock.replay(client);
		
		peer.requestWorkers(client, new RequestSpecification(0, new JobSpecification("label"), requestID, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 0, 0, 0));
		
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));
		
		EasyMock.verify(loggerMock);
		EasyMock.verify(client);
		
		EasyMock.reset(loggerMock);
		EasyMock.reset(client);
	
		
		//Null request
		loggerMock.warn("Client [" + clientID.getServiceID().toString() + "] done a null request. This request was ignored.");
		EasyMock.replay(loggerMock);
		EasyMock.replay(client);
		
		AcceptanceTestUtil.publishTestObject(peerComponent, clientID, client, RemoteWorkerProviderClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, clientID);
	
		peer.requestWorkers(client, null);
		
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));
		EasyMock.verify(loggerMock);
		EasyMock.verify(client);
	
		EasyMock.reset(loggerMock);
		EasyMock.reset(client);
		
		//Request with a null client public key
		clientID.setPublicKey(null);
		long request2 = 2;
		loggerMock.warn("Request "+request2+": request ignored because it has not a public key.");
		
		EasyMock.replay(loggerMock);
		EasyMock.replay(client);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, clientID);
		AcceptanceTestUtil.publishTestObject(peerComponent, clientID, client, RemoteWorkerProviderClient.class);
		
		peer.requestWorkers(client, new RequestSpecification(0, new JobSpecification("label"), request2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0));
		
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));
		EasyMock.verify(loggerMock);
		EasyMock.verify(client);
	
		EasyMock.reset(loggerMock);
		EasyMock.reset(client);
		
		//Request with a null client
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, pubKeyA);
		AcceptanceTestUtil.publishTestObject(peerComponent, clientID, client, RemoteWorkerProviderClient.class);
		long requestID3 = 3;
		loggerMock.warn("Request "+requestID3+": request ignored because it has not a client.");
		EasyMock.replay(loggerMock);
		EasyMock.replay(client);
	
		peer.requestWorkers(null, new RequestSpecification(0, new JobSpecification("label"), requestID3, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 0, 0, 0));
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(clientID.getServiceID()));
		EasyMock.verify(loggerMock);
		EasyMock.verify(client);
	
	}

	/**
	 * A remote client request one worker. Verify if:
	 * The worker received a work for peer message;
	 * The peer is interested on client failure;
	 * The worker was marked as DONATED and has the client public key.
	 * @throws Exception 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_at_011_6_Request_Remote_AllocationWithJDL() throws Exception {
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_blank.xml");
		
		//Worker login
		//Create Worker specs
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", workerServerName, null, null);
		
		//Notify recovery of WorkerA
		String workerAPubKey = "publicKeyA";
		DeploymentID workerADID = req_019_Util.createWorkerManagementDeploymentID(workerAPubKey, workerSpecA);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADID);

		//Change workerA status to idle
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADID);
		
		DeploymentID clientID = new DeploymentID(
	            new ServiceID("client", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		String pubKeyClient1 = "pubKeyA";
		clientID.setPublicKey(pubKeyClient1);
		
		WorkerAllocation allocation = new WorkerAllocation(workerADID);		
		
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForRemoteClient(peerComponent, clientID, requestSpec, 1, allocation);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnRemoteClient(clientID.getServiceID()));
		
		//Verify if the worker A was marked as DONATED
		//Create expected result
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, 
				clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	@Category(JDLCompliantTest.class) @Test public void test_at_011_7_Local_RedistributionWithJDL() throws Exception{
	
		peerComponent = req_010_Util.startPeer();
		
		//Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011-1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011-2", "server011", "011012");
		XMPPAccount user3 = req_101_Util.createLocalUser("user011-3", "server011", "011013");
		XMPPAccount user4 = req_101_Util.createLocalUser("user011-4", "server011", "011014");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker", "broker"), "user2");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID2, peerControlClient2, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient3 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID3 = new DeploymentID(new ContainerID("pcc3", "broker", "broker"), "user3");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID3, peerControlClient3, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID3);
		
		try {
			peerControl.addUser(peerControlClient3, user3.getUsername() + "@" + user3.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient4 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID4 = new DeploymentID(new ContainerID("pcc4", "broker", "broker"), "user4");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID4, peerControlClient4, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID4);
		
		try {
			peerControl.addUser(peerControlClient4, user4.getUsername() + "@" + user4.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		
		
		String workerAPubKey = "publicKeyA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		
		//Login with four valid users
		String client1PubKey = "user1PubKey";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, client1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID1);
		
		String client2PubKey = "user2PubKey";
		DeploymentID lwpcOID2 = req_108_Util.login(peerComponent, user2, client2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID2);
		
		String client3PubKey = "user3PubKey";
		DeploymentID lwpcOID3 = req_108_Util.login(peerComponent, user3, client3PubKey);
		LocalWorkerProviderClient lwpc3 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID3);
		
		String client4PubKey = "user4PubKey";
		DeploymentID lwpcOID4 = req_108_Util.login(peerComponent, user4, client4PubKey);
		LocalWorkerProviderClient lwpc4 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID4);
		
		
		//Request three workers for client1 - expect to obtain all of them
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 3, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID1, lwpc), spec, allocationC, allocationB, 
				allocationA);
				
		//Request two workers for client2 - expect to obtain one of them - the most recently allocated on client1
		allocationA = new WorkerAllocation(workerADeploymentID).addLoserConsumer(lwpcOID1).addLoserRequestSpec(spec);
		RequestSpecification spec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID2, lwpc2), spec2, allocationA);
		
		//Request one worker for client3 - expect to obtain one of them - the most recently allocated on client1
		
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(lwpcOID1);
		RequestSpecification spec3 = new RequestSpecification(0, new JobSpecification("label"), 3, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID3, lwpc3), spec3, allocationB);
		
		//Request one worker for client4 - expect to obtain none of them
		RequestSpecification spec4 = new RequestSpecification(0, new JobSpecification("label"), 4, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID4, lwpc4), spec4);
		
		//Verify the clients' status
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), client1PubKey, UserState.CONSUMING);
		UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), client2PubKey, UserState.CONSUMING);
		UserInfo userInfo3 = new UserInfo(user3.getUsername(), user3.getServerAddress(), client3PubKey, UserState.CONSUMING);
		UserInfo userInfo4 = new UserInfo(user4.getUsername(), user4.getServerAddress(), client4PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2, userInfo3, userInfo4);
	    
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID2.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpcOID3.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	@Category(JDLCompliantTest.class) @Test public void test_at_011_8_Local_Redistribution_WithoutNOFWithJDL() throws Exception{
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_blank.xml");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		
		//Request three workers for client1 - expect to obtain all of them
		DeploymentID clientID = new DeploymentID(
	            new ServiceID("client", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String client1PubKey = "client1PubKey";
		clientID.setPublicKey(client1PubKey);
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 3, 0, 0);
		
		req_011_Util.requestForRemoteClient(peerComponent, clientID, requestSpec, 1, 
				allocationC, allocationB, allocationA);
		
		//Request two workers for client2 - expect to obtain one of them - the most recently allocated on client1
		DeploymentID client2ID = new DeploymentID(
	            new ServiceID("client2", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String client2pubKey = "client2PubKey";
		client2ID.setPublicKey(client2pubKey);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		
		allocationA = new WorkerAllocation(workerADeploymentID).addLoserConsumer(clientID);
		req_011_Util.requestForRemoteClient(peerComponent, client2ID, requestSpec2, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationA);
		
		//Request one worker for client3 - expect to obtain one of them - the most recently allocated on client1
		DeploymentID client3ID = new DeploymentID(
	            new ServiceID("client3", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String client3PubKey = "client3PubKey";
		client3ID.setPublicKey(client3PubKey);
		
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(clientID);
		req_011_Util.requestForRemoteClient(peerComponent, client3ID, requestSpec3, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationB);
		
		//Request one worker for client4 - expect to obtain none of them
		DeploymentID client4ID = new DeploymentID(
	            new ServiceID("client4", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String client4PubKey = "client4PubKey";
		client4ID.setPublicKey(client4PubKey);
		
		RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), 4, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		
		req_011_Util.requestForRemoteClient(peerComponent, client4ID, requestSpec4,  
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES);
		
		//Verify the workers' status
		//Create expected result
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, client2ID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, client3ID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, clientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	@Category(JDLCompliantTest.class) @Test public void test_at_011_9_Local_And_Remote_RedistributionWithJDL() throws Exception{
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_blank.xml");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		//Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011-1", "server011", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		
		//Login with a valid user
		String client1PubKey = "user1PubKey";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, client1PubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID1);
		
		//Request two workers for a remote client - expect to obtain two workers
		DeploymentID remoteClientID = new DeploymentID(
	            new ServiceID("client", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String remoteClientPubKey = "remoteClientPubKey";
		remoteClientID.setPublicKey(remoteClientPubKey);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0); 
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, remoteClientID, requestSpec, 
				1, allocationC, allocationB);
		
		//Request one worker for a local client - expect to obtain two workers
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(remoteClientID);
		
		RequestSpecification spec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID1, lwpc), spec2, allocationB, allocationA);
	
		//Verify the clients' status 
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), client1PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, remoteClientID.getServiceID().toString());
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	@Category(JDLCompliantTest.class) @Test public final void test_at_011_10_SubCommunities_RedistributionWithJDL() throws Exception{
		
		/**
			<trusts>
			    <trust>
			    	<name>subA</name>
			      	<priority>1</priority>
			        <peer> 
			        	<name>peer11</name>
			            <publickey>publicKey11</publickey>
			        </peer>
			    </trust>
			    <trust>
			    	<name>subB</name>
			    	<priority>2</priority>
			        <peer>
			    		<publickey>publicKey21</publickey> 
			    		<name>peer21</name>
			    	</peer>
			    	<peer>
			    		<publickey>publicKey22</publickey> 
			    		<name>peer22</name>
			    	</peer>
			    </trust>
			</trusts>
		 */
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_10.xml");
		
		//Start the peer with a well formed trust configuration file
		peerComponent = req_010_Util.startPeer();
		
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S1", null, null);
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);
		
		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecD, workerDDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerDDeploymentID);
		
		//Request three workers for peer21 - expect to obtain three workers
		DeploymentID peer21DeploymentID = new DeploymentID(
	            new ServiceID("peer21", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer21PubKey = "publicKey21";
		peer21DeploymentID.setPublicKey(peer21PubKey);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 3, 0, 0);
		
		WorkerAllocation allocationD = new WorkerAllocation(workerDDeploymentID);
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, peer21DeploymentID,
				requestSpec, 2, allocationD, allocationC, allocationB);
		
		//Request three workers for peer22 - expect to obtain two workers
		DeploymentID peer22DeploymentID = new DeploymentID(
	            new ServiceID("peer22", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer22PubKey = "publicKey22";
		peer22DeploymentID.setPublicKey(peer22PubKey);
	
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 3, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(peer21DeploymentID);
		req_011_Util.requestForRemoteClient(peerComponent, peer22DeploymentID, requestSpec2,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationA, allocationB);
		
		//Request two workers for peer11 - expect to obtain two workers
		DeploymentID peer11DeploymentID = new DeploymentID(
	            new ServiceID("peer11", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer11PubKey = "publicKey11";
		peer11DeploymentID.setPublicKey(peer11PubKey);
		
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		
		allocationC = new WorkerAllocation(workerCDeploymentID).addLoserConsumer(peer21DeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(peer22DeploymentID);
		req_011_Util.requestForRemoteClient(peerComponent, peer11DeploymentID, requestSpec3, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC, allocationB);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, peer22DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, peer11DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, peer11DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.DONATED, peer21DeploymentID.getServiceID().toString());
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC, workerInfoD);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	@Category(JDLCompliantTest.class) @Test public final void test_at_011_11_SubCommunities_RedistributionWithJDL() throws Exception{
		
		/**
		 * 
		 * <trusts>
			    <trust>
			    	<name>subA</name>
			      	<priority>1</priority>
			        <peer> 
			        	<name>peer11</name>
			            <publickey>publicKey11</publickey>
			        </peer>
			    </trust>
			    <trust>
			    	<name>subB</name>
			    	<priority>2</priority>
			    	<peer>
			    		<publickey>publicKey21</publickey> 
			    		<name>peer21</name>
			    	</peer> 
			    </trust>
			    <trust>
			       	<name>subC</name>
			    	<priority>2</priority>
			    	<peer>
			    		<publickey>publicKey22</publickey> 
			    		<name>peer22</name>
			    	</peer>
			    </trust>
			</trusts>
		 * 
		 */
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_11.xml");
		
		//Start the peer with a well formed trust configuration file
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S1", null, null);
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);
		
		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecD, workerDDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerDDeploymentID);
		
		//Request three workers for peer21 - expect to obtain three workers
		DeploymentID peer21DeploymentID = new DeploymentID(
	            new ServiceID("peer21", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer21PubKey = "publicKey21";
		peer21DeploymentID.setPublicKey(peer21PubKey);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 3, 0, 0);
		
		WorkerAllocation allocationD = new WorkerAllocation(workerDDeploymentID);
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, peer21DeploymentID, requestSpec, 3, allocationD,
				allocationC, allocationB);
		
		//Request three workers for peer22 - expect to obtain two workers
		DeploymentID peer22DeploymentID = new DeploymentID(
	            new ServiceID("peer22", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer22PubKey = "publicKey22";
		peer22DeploymentID.setPublicKey(peer22PubKey);
	
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 3, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(peer21DeploymentID);
		req_011_Util.requestForRemoteClient(peerComponent, peer22DeploymentID, requestSpec2, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationA, allocationB);
		
		//Request two workers for peer11 - expect to obtain two workers
		DeploymentID peer11DeploymentID = new DeploymentID(
	            new ServiceID("peer11", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String peer11PubKey = "publicKey11";
		peer11DeploymentID.setPublicKey(peer11PubKey);
		
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		
		allocationC = new WorkerAllocation(workerCDeploymentID).addLoserConsumer(peer21DeploymentID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(peer22DeploymentID);
		req_011_Util.requestForRemoteClient(peerComponent, peer11DeploymentID, requestSpec3, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC, allocationB);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, peer22DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, peer11DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, peer11DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.DONATED, peer21DeploymentID.getServiceID().toString());
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC, workerInfoD);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	@Category(JDLCompliantTest.class) @Test public final void test_at_011_12_Local_Remote_And_SubCommunities_RedistributionWithJDL() throws Exception {
		
		
		/**
		 * Contents of trust.xml:
			<trusts>
			    <trust>
			    	<name>subA</name>
			      	<priority>1</priority>
			        <peer> 
			        	<name>peer11</name>
			            <publickey>publicKey11</publickey>
			        </peer>
			    </trust>
			    <trust>
			    	<name>subB</name>
			    	<priority>2</priority>
			        <peer>
			    		<publickey>publicKey21</publickey> 
			    		<name>peer21</name>
			    	</peer>
			    	<peer>
			    		<publickey>publicKey22</publickey> 
			    		<name>peer22</name>
			    	</peer>
			    </trust>
			</trusts>
		 */
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"011_12.xml");
		
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011012");
		
		//Start the peer with a well formed trust configuration file
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S1", null, null);
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createClassAdWorkerSpec("U5", "S1", null, null);
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createClassAdWorkerSpec("U6", "S1", null, null);
		
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDeploymentID);
		
		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecD, workerDDeploymentID);
		
		String workerEPubKey = "publicKeyWE";
		DeploymentID workerEDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecE, workerEPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecE, workerEDeploymentID);
		
		String workerFPubKey = "publicKeyWF";
		DeploymentID workerFDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecF, workerFPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecF, workerFDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerCDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerDDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerEDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerFDeploymentID);
		
		//Login with two valid users
		String localWP1PubKey = "localWP1";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), localWP1PubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user1, localWP1PubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
		String localWP2PubKey = "localWP2";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), localWP2PubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID2 = req_108_Util.login(peerComponent, user2, localWP2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID2);
		
		//Request one worker for local1
		WorkerAllocation allocationF = new WorkerAllocation(workerFDeploymentID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec, allocationF);
		
		//Request two workers for remote1
		DeploymentID remoteClient1ID = new DeploymentID(
	            new ServiceID("client1", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String remoteClient1PubKey = "remoteClient1PubKey";
		remoteClient1ID.setPublicKey(remoteClient1PubKey);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		
		WorkerAllocation allocationE = new WorkerAllocation(workerEDeploymentID);
		WorkerAllocation allocationD = new WorkerAllocation(workerDDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, remoteClient1ID, requestSpec2, 2, allocationE,
				allocationD);
		
		//Request two workers for remote2
		DeploymentID remoteClient2ID = new DeploymentID(
	            new ServiceID("client2", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String remoteClient2PubKey = "remoteClient2PubKey";
		remoteClient2ID.setPublicKey(remoteClient2PubKey);
		
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBDeploymentID);
		
		req_011_Util.requestForRemoteClient(peerComponent, remoteClient2ID, requestSpec3, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC, allocationB);
		
		//Verify the workers' status
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, remoteClient2ID.getServiceID().toString());
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, remoteClient2ID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.DONATED, remoteClient1ID.getServiceID().toString());
		WorkerInfo workerInfoE = new WorkerInfo(workerSpecE, LocalWorkerState.DONATED, remoteClient1ID.getServiceID().toString());
		WorkerInfo workerInfoF = new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoF, workerInfoB, workerInfoC, 
				workerInfoD, workerInfoE, workerInfoA);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//Request two workers for sub11
		DeploymentID sub11RemoteClientID = new DeploymentID(
	            new ServiceID("sub11", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String sub11RemoteClientPubKey = "publicKey11";
		sub11RemoteClientID.setPublicKey(sub11RemoteClientPubKey);
	
		RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), 4, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(remoteClient2ID);
		req_011_Util.requestForRemoteClient(peerComponent, sub11RemoteClientID, requestSpec4,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationA, allocationB);
		
		//Request one worker for sub21
		DeploymentID sub21RemoteClientID = new DeploymentID(
	            new ServiceID("sub21", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String sub21RemoteClientPubKey = "publicKey21";
		sub21RemoteClientID.setPublicKey(sub21RemoteClientPubKey);
	
		RequestSpecification requestSpec5 = new RequestSpecification(0, new JobSpecification("label"), 5, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		
		allocationD = new WorkerAllocation(workerDDeploymentID).addLoserConsumer(remoteClient1ID);
		req_011_Util.requestForRemoteClient(peerComponent, sub21RemoteClientID, requestSpec5, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationD);
		
		//Request one worker for sub22
		DeploymentID sub22RemoteClientID = new DeploymentID(
	            new ServiceID("sub22", "server", PeerConstants.MODULE_NAME, 
	            									PeerConstants.REMOTE_WORKER_PROVIDER));
		
		String sub22RemoteClientPubKey = "publicKey22";
		sub22RemoteClientID.setPublicKey(sub22RemoteClientPubKey);
		
		RequestSpecification requestSpec6 = new RequestSpecification(0, new JobSpecification("label"), 6, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		
		allocationC = new WorkerAllocation(workerCDeploymentID).addLoserConsumer(remoteClient2ID);
		req_011_Util.requestForRemoteClient(peerComponent, sub22RemoteClientID, requestSpec6, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationC);
		
		//Verify the workers' status
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, sub11RemoteClientID.getServiceID().toString());
		workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.DONATED, sub11RemoteClientID.getServiceID().toString());
		workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, sub22RemoteClientID.getServiceID().toString());
		workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.DONATED, sub21RemoteClientID.getServiceID().toString());
		workerInfoE = new WorkerInfo(workerSpecE, LocalWorkerState.DONATED, remoteClient1ID.getServiceID().toString());
		workerInfoF = new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoF, workerInfoA, workerInfoB,  
				workerInfoC, workerInfoD, workerInfoE);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	
		//Request four workers for local2
		allocationE = new WorkerAllocation(workerEDeploymentID).addLoserConsumer(remoteClient1ID);
		allocationC = new WorkerAllocation(workerCDeploymentID).addLoserConsumer(sub22RemoteClientID);
		allocationD = new WorkerAllocation(workerDDeploymentID).addLoserConsumer(sub21RemoteClientID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(sub11RemoteClientID);
		RequestSpecification spec7 = new RequestSpecification(0, new JobSpecification("label"), 7, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 4, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID2, lwpc2), spec7, allocationE, allocationC, allocationD, allocationB);
		
		//Request two workers for local1
		allocationA = new WorkerAllocation(workerADeploymentID).addLoserConsumer(sub11RemoteClientID);
		allocationB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(lwpcOID2).addLoserRequestSpec(
				new RequestSpecification(0, new JobSpecification("label"), 7, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 4, 0, 0));
		RequestSpecification spec8 = new RequestSpecification(0, new JobSpecification("label"), 8, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec8, allocationA, allocationB);
	
		//Verify the workers' status
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpcOID2.getServiceID().toString());
		workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.IN_USE, lwpcOID2.getServiceID().toString());
		workerInfoE = new WorkerInfo(workerSpecE, LocalWorkerState.IN_USE, lwpcOID2.getServiceID().toString());
		workerInfoF = new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
		
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB,  
				workerInfoC, workerInfoD, workerInfoE, workerInfoF);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	// =================================== Qualification Tests ===================================
	
	/**
	 * TODO Javadoc
	 * @author melina
	 * @Date 23/04/2008   
	 */
	@Category(JDLCompliantTest.class) @Test public void test_invalid_request_negativeNumberWithJDL() throws Exception {
		// create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_test", "server011",
				"011011");
		
		// Start peer and set a mock log
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
	
		// Login with a valid user
		String brokerPubKey = "publicKeyA";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
	
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
	
		LocalWorkerProvider peer = peerAcceptanceUtil.getLocalWorkerProviderProxy();
	
		// Request no workers for the logged user
		int request2ID = 5;
		
		ObjectDeployment peerControlDeployment = getPeerControlDeployment(peerComponent);
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerControlDeployment, lwpcOID);
		loggerMock.warn("Request " + request2ID + ": request ignored because ["
				+ lwpcOID.getServiceID() + "] requested less than 1 worker");
	
		EasyMock.replay(loggerMock);
		
		peer.requestWorkers(new RequestSpecification(0, new JobSpecification("label"), request2ID, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), -1, 0, 0));
	
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
	}

	/**
	 * TODO Javadoc
	 * @author melina
	 * @Date 23/04/2008   
	 */
	@Category(JDLCompliantTest.class) @Test public void test_invalid_wokersWithJDL() throws Exception {
	
		// create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011",
				"011011");
	
		// Start peer and set a mock log
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
		
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		EasyMock.reset(loggerMock);
	
		// Login with a valid user
		String brokerPubKey = "publicKeyA";
		DeploymentID clientID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProvider peer = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		
		// Request no workers for the logged user
		int request2ID = 1;
		
		ObjectDeployment peerDeployment = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		DeploymentID peerID = new DeploymentID(new ContainerID("peer", "peer", "peer", brokerPubKey), "");
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerDeployment, peerID);
		loggerMock.warn("Request "+request2ID+": request ignored because [" + 
				clientID.getServiceID() + "] requested less than 1 worker");
		EasyMock.replay(loggerMock);
		
		peer.requestWorkers(new RequestSpecification(0, new JobSpecification("label"), request2ID, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 0, 0, 0));
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
	}

	/**
	 * TODO javadoc
	 * @author gustavopf
	 * @author melina
	 * @author giovanni
	 * @Date 23/04/2008   
	 */
	@Category(JDLCompliantTest.class) @Test public void testRequestWorker2WithJDL() throws Exception{
	
		//Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011-1", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", workerServerName, 256, null);
		
		
		String workerAPubKey = "publicKeyA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Login with four valid users
		String client1PubKey = "user1PubKey";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, client1PubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID1);
	
		
		//Request three workers for client1 - expect to obtain all of them
		
		
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements("<", -1, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID1, lwpc), spec);
				
		//Verify the clients' status
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), client1PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
	    
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify the workers' status	
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC);
	
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * TODO Javadoc
	 * @throws Exception
	 */
	@Category(JDLCompliantTest.class) @Test public void testRequestWorker3WithJDL() throws Exception{
	
		//Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011-1", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		// Workers login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", workerServerName, 256, null);
		
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", workerServerName, 156, null);
		
		
		String workerAPubKey = "publicKeyA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		
		String workerBPubKey = "publicKeyB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDeploymentID);
		
		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerBDeploymentID);
		
		//Login with four valid users
		String client1PubKey = "user1PubKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), client1PubKey);
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, client1PubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID1);
	
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		
		//Request three workers for client1 - expect to obtain all of them
		
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements("==", 256, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID1, lwpc), spec, allocationA);
				
		//Verify the clients' status
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), client1PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
	    
		req_106_Util.getUsersStatus(usersInfo);
		
		//Verify the workers' status	
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IDLE, null);
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * 
	 * @author gustavopf
	 * @Date 24/04/2008
	 */
	@Category(JDLCompliantTest.class) @Test public void test_at_011_PeerDisinterestedOnLocalWorkerFailureWithJDL() throws Exception {
	
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		peerComponent = req_010_Util.startPeer();
		
		//Worker login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", workerServerName, null, null);
		
		//Notify recovery of WorkerA
		String workerAPubKey = "publicKeyWorkerA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADeploymentID);
		
		//Change workerA status to idle
		req_025_Util.changeWorkerStatusToIdle(peerComponent, workerADeploymentID);
		
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
		
		//Login with a valid user
		String brokerPubKey = "publicKeyA";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		 
		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerADeploymentID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, new TestStub(lwpcOID, lwpc), spec, allocation);
		
		// Verify if peer is interested in the client failure.
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
		
		// In this moment, the client is marked as CONSUMING and the worker
		// is market as IN_USE.		
		
		// Verify if peer is no more interested in the worker failure. 
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADeploymentID.getServiceID()));
		
	}
    
}