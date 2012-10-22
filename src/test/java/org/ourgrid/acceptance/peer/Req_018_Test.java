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

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
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

@ReqTest(reqs="REQ018")
public class Req_018_Test extends PeerAcceptanceTestCase {
	
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
	
	private PeerComponent peerComponent;

	/**
	 * When a consumer peer receives a remote worker, it must:
	 * Register interest on provider peer failure;
	 * Register interest on remote worker failure;
	 * Deliver the worker to Broker.
	 * @throws Exception
	 */
	@ReqTest(test="AT-018.1", reqs="REQ018")
	@Test public void test_AT_018_1_RemoteWorkerReceiving() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
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
		
		//DS recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		TestStub lwpcStub = new TestStub(lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		int requestID = 1;
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, "os = windows AND mem > 256", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub, requestSpec);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");

		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID remoteWorkerOID = req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec, "workerPK", brokerPubKey).getDeploymentID();
		
		ObjectDeployment remoteWorkerMonitorOD = peerAcceptanceUtil.getRemoteWorkerMonitorDeployment();
	
		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, remoteWorkerOID.getServiceID(), remoteWorkerMonitorOD.getDeploymentID()));

		//Verify if the client was marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.CONSUMING);
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		
		req_106_Util.getUsersStatus(usersInfo);
	}
	
	/**
	 * Validate remote worker receiving:
	 * The provider must be requested for worker before delivering them;
	 * The parameters worker and workerSpec are mandatory;
	 * The worker must not be already allocated for this peer.
	 * @throws Exception
	 */
	@ReqTest(test="AT-018.2", reqs="REQ018")
	@Test public void test_AT_018_2_InputValidation() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
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
		
		ScheduledExecutorService timerMock = getMock(NOT_NICE, ScheduledExecutorService.class);
		peerComponent.setTimer(timerMock);
		
		//ds recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		TestStub lwpcStub = new TestStub(lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		/*LocalWorkerProviderClient lwpc = getMock(NOT_NICE, LocalWorkerProviderClient.class);
		peerAcceptanceUtil.createStub(lwpc, lwpcOID);*/
		
		int requestID = 1;
		int requiredWorkers = 1;
		String requirements = "os = windows AND mem > 256";
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, requirements, requiredWorkers, 0, 0);
		
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub, requestSpec);
		
		//GIS client receive a remote worker provider
		String rwpPublicKey = "rwpPublicKey";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		TestStub rwpStub = 
			req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec, "rwpUser", "rwpServer", rwpPublicKey, workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();

		AcceptanceTestUtil.publishTestObject(peerComponent, rwpID, rwp, RemoteWorkerProvider.class);
		
		//Remote worker provider client receive a null remote worker
		RemoteWorkerProviderClient rwpc = peerAcceptanceUtil.getRemoteWorkerProviderClientProxy();
		ObjectDeployment rwpcOD = peerAcceptanceUtil.getRemoteWorkerProviderClientDeployment();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpcOD, rwpPublicKey);
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		loggerMock.warn("Ignoring a null worker, which was received from the provider: " + rwpID.getContainerID());
		
		replayActiveMocks();
		
		rwpc.hereIsWorker(rwp, null, workerSpec);
				
		verifyActiveMocks();
		resetActiveMocks();
		
		//Remote worker provider client receive a remote worker without specification
		RemoteWorkerManagement remoteWorker = getMock(NOT_NICE, RemoteWorkerManagement.class);
		
		DeploymentID remoteWorkerOID = new DeploymentID(new ContainerID("us", "server", WorkerConstants.MODULE_NAME), 
				"remoteWorkerObjName");
		peerAcceptanceUtil.createStub(remoteWorker, RemoteWorkerManagement.class, remoteWorkerOID);
		
		rwpID = rwpStub.getDeploymentID();
		rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		AcceptanceTestUtil.publishTestObject(peerComponent, rwpID, rwp, RemoteWorkerProvider.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpcOD, rwpID);
		
		loggerMock.warn("Ignoring a worker without specification, which was received from the provider: " + rwpID.getContainerID());
		
		replayActiveMocks();
		
		rwpc.hereIsWorker(rwp, remoteWorkerOID.getServiceID(), null);
		
		ObjectDeployment remoteWorkerMonitorOD = peerAcceptanceUtil.getRemoteWorkerMonitorDeployment();
		
		assertFalse(AcceptanceTestUtil.isInterested(peerComponent, remoteWorkerOID.getServiceID(), remoteWorkerMonitorOD.getDeploymentID()));
		
		verifyActiveMocks();
		resetActiveMocks();
		
		
		//
		rwpID = rwpStub.getDeploymentID();
		rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		AcceptanceTestUtil.publishTestObject(peerComponent, rwpID, rwp, RemoteWorkerProvider.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpcOD, rwpID);
		
		//Remote worker provider client receive a remote worker
		loggerMock.debug("Received a worker ["+ remoteWorkerOID.getServiceID() +"] from a remote worker provider [" 
				+ rwpID.getServiceID() + "].");
		peerAcceptanceUtil.createStub(remoteWorker, RemoteWorkerManagement.class, remoteWorkerOID);
		
		RemoteWorkerManagementClient rwmc = peerAcceptanceUtil.getRemoteWorkerManagementClient();
		remoteWorker.workForBroker(rwmc, brokerPubKey);
		
		replayActiveMocks();
		
		rwpc.hereIsWorker(rwp, remoteWorkerOID.getServiceID(), workerSpec);

		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, remoteWorkerOID.getServiceID(), remoteWorkerMonitorOD.getDeploymentID()));
		req_018_Util.notifyRemoteWorkerRecovery(remoteWorkerOID);
		
		verifyActiveMocks();
		resetActiveMocks();

		//Remote worker provider client receive an already allocated remote worker
		loggerMock.warn("Receiving a remote worker ["+ remoteWorkerOID.getServiceID() +"] " +
				"that is already allocated in this peer. This message was ignored.");
		peerAcceptanceUtil.createStub(remoteWorker, RemoteWorkerManagement.class, remoteWorkerOID);
		
		replayActiveMocks();
		
		rwpc.hereIsWorker(rwp, remoteWorkerOID.getServiceID(), workerSpec);
		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, remoteWorkerOID.getServiceID(), remoteWorkerMonitorOD.getDeploymentID()));
		req_018_Util.notifyRemoteWorkerRecovery(remoteWorkerOID);
		
		verifyActiveMocks();
		resetActiveMocks();

	}
	
	
	/**
	 * Verify if the peer redistributes the remote workers equally.
	 * @throws Exception
	 */
	@ReqTest(test="AT-018.3", reqs="REQ018")
	@Test public void test_AT_018_3_LocalRedistribution() throws Exception{
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user18_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user18_2", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
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
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Clients login and request workers after ds recovery - expect OG peer to query ds
		//user1
		String brokerPubKey1 = "brokerPublicKey1";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, brokerPubKey1);
		TestStub lwpcStub1 = new TestStub(lwpcOID1, AcceptanceTestUtil.getBoundObject(lwpcOID1));
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub1, requestSpec);
		
		//user2
		String brokerPubKey2 = "brokerPublicKey2";
		DeploymentID lwpcOID2 = req_108_Util.login(peerComponent, user2, brokerPubKey2);
		TestStub lwpcStub2 = new TestStub(lwpcOID2, AcceptanceTestUtil.getBoundObject(lwpcOID2));
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub2, requestSpec2);
		
		//GIS client1 receive a remote worker provider
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec1, workerSpec2);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive two remote workers - expect to give one for each Broker
		//user1
		req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec1, "worker1PK", brokerPubKey1);
		
		//user2
		req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec2, "worker2PK", brokerPubKey2);
		
		//Verify if both clients were marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), brokerPubKey1, UserState.CONSUMING);
		UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), brokerPubKey2, UserState.CONSUMING);
		
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2);
		
		req_106_Util.getUsersStatus(usersInfo);

	}
	
	/**
	 * Verifies if the peer disposes a remote worker that does not match any request.
	 * @throws Exception
	 */
	@ReqTest(test="AT-018.4", reqs="REQ018")
	@Test public void test_AT_018_4_WorkersDontMatchRequirements() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerAcceptanceUtil.getPeerControlDeployment(), pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		ScheduledExecutorService timerMock = getMock(NOT_NICE, ScheduledExecutorService.class);
		peerComponent.setTimer(timerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerGridPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		TestStub lwpcStub = new TestStub(lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "os = windows AND mem > 256", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub, requestSpec);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "128");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(
				peerComponent, requestSpec, "rwpUser", "rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker which does not match the request
		req_018_Util.receiveAndDisposeRemoteWorker(peerComponent, rwp, rwpID, workerSpec, "workerPublicKey", brokerPubKey);

	}
	
	// ----------------------------------------------Qualification tests-----------------------------------------------------
	
	/**
	 * 
	 * 
	 * Description: 
	 * @author 
	 * @Data
	 */
	@Test public void test_AT_018_3_Qualification_LocalRedistribution() throws Exception{
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user18_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user18_2", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
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
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Clients login and request workers after ds recovery - expect OG peer to query ds
		//user1
		String brokerPubKey1 = "brokerPublicKey1";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, brokerPubKey1);
		TestStub lwpcStub1 = new TestStub(lwpcOID1, AcceptanceTestUtil.getBoundObject(lwpcOID1));
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub1, requestSpec);
		
		//user2
		String brokerPubKey2 = "brokerPublicKey2";
		DeploymentID lwpcOID2 = req_108_Util.login(peerComponent, user2, brokerPubKey2);
		TestStub lwpcStub2 = new TestStub(lwpcOID2, AcceptanceTestUtil.getBoundObject(lwpcOID2));
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub2, requestSpec2);
		
		//GIS client1 receive a remote worker provider
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
					
		//user1
		req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec1, "worker1PK", brokerPubKey1);
		
		//user2
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec2, "worker2PK", brokerPubKey2);
		
		//Verify if one client is just logged and other client is consuming
		//Two worker were requested. Both users states must be CONSUMING, not LOGGED
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), brokerPubKey1, UserState.CONSUMING);
		UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), brokerPubKey2, UserState.CONSUMING);
		
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2);
		
		req_106_Util.getUsersStatus(usersInfo);

	}

	/**
	 * When a consumer peer receives a remote worker, it must:
	 * Register interest on provider peer failure;
	 * Register interest on remote worker failure;
	 * Deliver the worker to Broker.
	 * @throws Exception
	 */
	@ReqTest(test="AT-018.1", reqs="REQ018")
	@Category(JDLCompliantTest.class) @Test public void test_AT_018_1_RemoteWorkerReceivingWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
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
		
		//DS recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		TestStub lwpcStub = new TestStub(lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		int requestID = 1;
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, "[Requirements=(other.MainMemory > 256 && other.OS == \"windows\");Rank=0]", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub, requestSpec);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", 512, "windows");
	
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID remoteWorkerOID = req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec, "workerPK", brokerPubKey).getDeploymentID();
		
		ObjectDeployment remoteWorkerMonitorOD = peerAcceptanceUtil.getRemoteWorkerMonitorDeployment();
	
		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, remoteWorkerOID.getServiceID(), remoteWorkerMonitorOD.getDeploymentID()));
	
		//Verify if the client was marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.CONSUMING);
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		
		req_106_Util.getUsersStatus(usersInfo);
	}

	/**
	 * Validate remote worker receiving:
	 * The provider must be requested for worker before delivering them;
	 * The parameters worker and workerSpec are mandatory;
	 * The worker must not be already allocated for this peer.
	 * @throws Exception
	 */
	@ReqTest(test="AT-018.2", reqs="REQ018")
	@Category(JDLCompliantTest.class) @Test public void test_AT_018_2_InputValidationWithJDL() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
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
		
		ScheduledExecutorService timerMock = getMock(NOT_NICE, ScheduledExecutorService.class);
		peerComponent.setTimer(timerMock);
		
		//ds recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		
		TestStub lwpcStub = new TestStub(lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		/*LocalWorkerProviderClient lwpc = getMock(NOT_NICE, LocalWorkerProviderClient.class);
		peerAcceptanceUtil.createStub(lwpc, lwpcOID);*/
		
		int requestID = 1;
		int requiredWorkers = 1;
		String requirements = "[Requirements=(other.MainMemory > 256 && other.OS == \"windows\");Rank=0]";
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, requirements, requiredWorkers, 0, 0);
		
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub, requestSpec);
		
		//GIS client receive a remote worker provider
		String rwpPublicKey = "rwpPublicKey";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", 512, "windows");
		TestStub rwpStub = 
			req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec, "rwpUser", "rwpServer", rwpPublicKey, workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
	
		AcceptanceTestUtil.publishTestObject(peerComponent, rwpID, rwp, RemoteWorkerProvider.class);
		
		//Remote worker provider client receive a null remote worker
		RemoteWorkerProviderClient rwpc = peerAcceptanceUtil.getRemoteWorkerProviderClientProxy();
		ObjectDeployment rwpcOD = peerAcceptanceUtil.getRemoteWorkerProviderClientDeployment();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpcOD, rwpPublicKey);
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		peerComponent.setLogger(loggerMock);
		loggerMock.warn("Ignoring a null worker, which was received from the provider: " + rwpID.getContainerID());
		
		replayActiveMocks();
		
		rwpc.hereIsWorker(rwp, null, workerSpec);
				
		verifyActiveMocks();
		resetActiveMocks();
		
		//Remote worker provider client receive a remote worker without specification
		RemoteWorkerManagement remoteWorker = getMock(NOT_NICE, RemoteWorkerManagement.class);
		
		DeploymentID remoteWorkerOID = new DeploymentID(new ContainerID("us", "server", WorkerConstants.MODULE_NAME), 
				"remoteWorkerObjName");
		peerAcceptanceUtil.createStub(remoteWorker, RemoteWorkerManagement.class, remoteWorkerOID);
		
		rwpID = rwpStub.getDeploymentID();
		rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		AcceptanceTestUtil.publishTestObject(peerComponent, rwpID, rwp, RemoteWorkerProvider.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpcOD, rwpID);
		
		loggerMock.warn("Ignoring a worker without specification, which was received from the provider: " + rwpID.getContainerID());
		
		replayActiveMocks();
		
		rwpc.hereIsWorker(rwp, remoteWorkerOID.getServiceID(), null);
		
		ObjectDeployment remoteWorkerMonitorOD = peerAcceptanceUtil.getRemoteWorkerMonitorDeployment();
		
		assertFalse(AcceptanceTestUtil.isInterested(peerComponent, remoteWorkerOID.getServiceID(), remoteWorkerMonitorOD.getDeploymentID()));
		
		verifyActiveMocks();
		resetActiveMocks();
		
		
		//
		rwpID = rwpStub.getDeploymentID();
		rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		AcceptanceTestUtil.publishTestObject(peerComponent, rwpID, rwp, RemoteWorkerProvider.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwpcOD, rwpID);
		
		//Remote worker provider client receive a remote worker
		loggerMock.debug("Received a worker ["+ remoteWorkerOID.getServiceID() +"] from a remote worker provider [" 
				+ rwpID.getServiceID() + "].");
		peerAcceptanceUtil.createStub(remoteWorker, RemoteWorkerManagement.class, remoteWorkerOID);
		
		RemoteWorkerManagementClient rwmc = peerAcceptanceUtil.getRemoteWorkerManagementClient();
		remoteWorker.workForBroker(rwmc, brokerPubKey);
		
		replayActiveMocks();
		
		rwpc.hereIsWorker(rwp, remoteWorkerOID.getServiceID(), workerSpec);
		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, remoteWorkerOID.getServiceID(), remoteWorkerMonitorOD.getDeploymentID()));
		req_018_Util.notifyRemoteWorkerRecovery(remoteWorkerOID);
		
		verifyActiveMocks();
		resetActiveMocks();
	
		//Remote worker provider client receive an already allocated remote worker
		loggerMock.warn("Receiving a remote worker ["+ remoteWorkerOID.getServiceID() +"] " +
				"that is already allocated in this peer. This message was ignored.");
		peerAcceptanceUtil.createStub(remoteWorker, RemoteWorkerManagement.class, remoteWorkerOID);
		
		replayActiveMocks();
		
		rwpc.hereIsWorker(rwp, remoteWorkerOID.getServiceID(), workerSpec);
		assertTrue(AcceptanceTestUtil.isInterested(peerComponent, remoteWorkerOID.getServiceID(), remoteWorkerMonitorOD.getDeploymentID()));
		req_018_Util.notifyRemoteWorkerRecovery(remoteWorkerOID);
		
		verifyActiveMocks();
		resetActiveMocks();
	
	}

	/**
	 * Verify if the peer redistributes the remote workers equally.
	 * @throws Exception
	 */
	@ReqTest(test="AT-018.3", reqs="REQ018")
	@Category(JDLCompliantTest.class) @Test public void test_AT_018_3_LocalRedistributionWithJDL() throws Exception{
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user18_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user18_2", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
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
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Clients login and request workers after ds recovery - expect OG peer to query ds
		//user1
		String brokerPubKey1 = "brokerPublicKey1";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, brokerPubKey1);
		TestStub lwpcStub1 = new TestStub(lwpcOID1, AcceptanceTestUtil.getBoundObject(lwpcOID1));
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub1, requestSpec);
		
		//user2
		String brokerPubKey2 = "brokerPublicKey2";
		DeploymentID lwpcOID2 = req_108_Util.login(peerComponent, user2, brokerPubKey2);
		TestStub lwpcStub2 = new TestStub(lwpcOID2, AcceptanceTestUtil.getBoundObject(lwpcOID2));
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub2, requestSpec2);
		
		//GIS client1 receive a remote worker provider
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec1, workerSpec2);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive two remote workers - expect to give one for each Broker
		//user1
		req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec1, "worker1PK", brokerPubKey1);
		
		//user2
		req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec2, "worker2PK", brokerPubKey2);
		
		//Verify if both clients were marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), brokerPubKey1, UserState.CONSUMING);
		UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), brokerPubKey2, UserState.CONSUMING);
		
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2);
		
		req_106_Util.getUsersStatus(usersInfo);
	
	}

	/**
	 * Verifies if the peer disposes a remote worker that does not match any request.
	 * @throws Exception
	 */
	@ReqTest(test="AT-018.4", reqs="REQ018")
	@Category(JDLCompliantTest.class) @Test public void test_AT_018_4_WorkersDontMatchRequirementsWithJDL() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		peerComponent = req_010_Util.startPeer();
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
		PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("pcc1", "broker", "broker"), "user1");
		AcceptanceTestUtil.publishTestObject(peerComponent, pccID1, peerControlClient1, PeerControlClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerAcceptanceUtil.getPeerControlDeployment(), pccID1);
		
		try {
			peerControl.addUser(peerControlClient1, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		ScheduledExecutorService timerMock = getMock(NOT_NICE, ScheduledExecutorService.class);
		peerComponent.setTimer(timerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerGridPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(peerComponent, user, brokerPubKey);
		TestStub lwpcStub = new TestStub(lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "[Requirements=(other.MainMemory > 256 && other.OS == \"windows\");Rank=0]", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub, requestSpec);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", 128, null);
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec, "rwpUser", "rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker which does not match the request
		req_018_Util.receiveAndDisposeRemoteWorker(peerComponent, rwp, rwpID, workerSpec, "workerPublicKey", brokerPubKey);
	
	}

	// ----------------------------------------------Qualification tests-----------------------------------------------------
	
	/**
	 * 
	 * 
	 * Description: 
	 * @author 
	 * @Data
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_018_3_Qualification_LocalRedistributionWithJDL() throws Exception{
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user18_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user18_2", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
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
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(peerComponent, dsID);
		
		//Clients login and request workers after ds recovery - expect OG peer to query ds
		//user1
		String brokerPubKey1 = "brokerPublicKey1";
		DeploymentID lwpcOID1 = req_108_Util.login(peerComponent, user1, brokerPubKey1);
		TestStub lwpcStub1 = new TestStub(lwpcOID1, AcceptanceTestUtil.getBoundObject(lwpcOID1));
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub1, requestSpec);
		
		//user2
		String brokerPubKey2 = "brokerPublicKey2";
		DeploymentID lwpcOID2 = req_108_Util.login(peerComponent, user2, brokerPubKey2);
		TestStub lwpcStub2 = new TestStub(lwpcOID2, AcceptanceTestUtil.getBoundObject(lwpcOID2));
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(peerComponent, lwpcStub2, requestSpec2);
		
		//GIS client1 receive a remote worker provider
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(peerComponent, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
					
		//user1
		req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec1, "worker1PK", brokerPubKey1);
		
		//user2
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		req_018_Util.receiveRemoteWorker(peerComponent, rwp, rwpID, workerSpec2, "worker2PK", brokerPubKey2);
		
		//Verify if one client is just logged and other client is consuming
		//Two worker were requested. Both users states must be CONSUMING, not LOGGED
		UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), brokerPubKey1, UserState.CONSUMING);
		UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), brokerPubKey2, UserState.CONSUMING);
		
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2);
		
		req_106_Util.getUsersStatus(usersInfo);
	
	}
	
}