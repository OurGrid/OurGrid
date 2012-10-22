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

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
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
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0027 extends PeerAcceptanceTestCase {

    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());

    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
	/**
	* Verify if the redistributes local and remote workers equally.
    */
	@ReqTest(test="AT-0027", reqs="")
	@Test public void test_AT_0027_localRedistribution_LocalAndRemoteWorkers() throws Exception {
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011012");
		XMPPAccount user3 = req_101_Util.createLocalUser("user011_3", "server011", "011013");
		XMPPAccount user4 = req_101_Util.createLocalUser("user011_4", "server011", "011014");

		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);

		//Change workers  to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		//Login with four valid users
	    String broker1PubKey = "publicKey1";
	    String broker2PubKey = "publicKey2";
	    String broker3PubKey = "publicKey3";
	    String broker4PubKey = "publicKey4";
	    
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
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient3 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID3 = new DeploymentID(new ContainerID("pcc3", "broker3", "broker"), broker3PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID3, peerControlClient3, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID3);
		
		try {
			peerControl.addUser(peerControlClient3, user3.getUsername() + "@" + user3.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient4 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID4 = new DeploymentID(new ContainerID("pcc4", "broker4", "broker"), broker4PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID4, peerControlClient4, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID4);
		
		try {
			peerControl.addUser(peerControlClient4, user4.getUsername() + "@" + user4.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
	    
	    DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
	    LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
	    
	    DeploymentID lwpc3OID = req_108_Util.login(component, user3, broker3PubKey);
	    LocalWorkerProviderClient lwpc3 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc3OID);
	    
	    DeploymentID lwpc4OID = req_108_Util.login(component, user4, broker4PubKey);
	    LocalWorkerProviderClient lwpc4 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc4OID);
	    
	    //Request three workers for client1 - expect to obtain all of them
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 3, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    WorkerAllocation allocationWorkerB = new WorkerAllocation(workerBDeploymentID);
	    WorkerAllocation allocationWorkerC = new WorkerAllocation(workerCDeploymentID);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec1, 
	    		allocationWorkerC, allocationWorkerB, allocationWorkerA);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));
	    
	    //Request two workers for client2 - expect to obtain one of them - the most recently allocated on client1
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 2, 0, 0);
	    allocationWorkerA = new WorkerAllocation(workerADeploymentID).addLoserConsumer(lwpc1OID).addLoserRequestSpec(requestSpec1);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, allocationWorkerA);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc2OID.getServiceID()));
	    
	    //Request one worker for client3 - expect to obtain one of them - the most recently allocated on client1
	    allocationWorkerB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(lwpc1OID);
		RequestSpecification spec3 = new RequestSpecification(0, new JobSpecification("label"), 3, "", 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc3OID, lwpc3), spec3, allocationWorkerB);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc3OID.getServiceID()));
	    
	    //Request one worker for client4 - expect to obtain none of them
	    RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), 4, "", 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc4OID, lwpc4), requestSpec4);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc4OID.getServiceID()));
	    
	    //Verify the clients' status
        UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), broker1PubKey, UserState.CONSUMING);
        UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), broker2PubKey, UserState.CONSUMING);
        UserInfo userInfo3 = new UserInfo(user3.getUsername(), user3.getServerAddress(), broker3PubKey, UserState.CONSUMING);
        UserInfo userInfo4 = new UserInfo(user4.getUsername(), user4.getServerAddress(), broker4PubKey, UserState.CONSUMING);
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2, userInfo3, userInfo4);
        
        req_106_Util.getUsersStatus(usersInfo);
        
        //Verify the workers' status
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2OID.getServiceID().toString());
        WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc3OID.getServiceID().toString());
        WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc1OID.getServiceID().toString());
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
       
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME, "dsPublicKey"), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
        req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
    	
        //GIS client 1 receive a remote worker provider 
        WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createWorkerSpec("rU1", "rS1");
        
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser",
				"rwpServer", "rwpPublicKey", remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Receive a remote worker - expect to allocate it to client4
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec1, "rworker1PK", broker4PubKey);
		
		//Receive a remote worker - expect to allocate it to client1
        WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createWorkerSpec("rU2", "rS1");
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec2, "rWorker2PK", broker1PubKey);
		
		//Receive a remote worker - expect to allocate it to client2
		WorkerSpecification remoteWorkerSpec3 = workerAcceptanceUtil.createWorkerSpec("rU3", "rS1");
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec3, "rWorker3PK", broker2PubKey);
		
		//Receive a remote worker - expect to allocate it to client1
		WorkerSpecification remoteWorkerSpec4 = workerAcceptanceUtil.createWorkerSpec("rU4", "rS1");
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec4, "rWorker4PK", broker1PubKey);
		
		//Verify if the clients was marked as CONSUMING
        userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), broker1PubKey, UserState.CONSUMING);
        userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), broker2PubKey, UserState.CONSUMING);
        userInfo3 = new UserInfo(user3.getUsername(), user3.getServerAddress(), broker3PubKey, UserState.CONSUMING);
        userInfo4 = new UserInfo(user4.getUsername(), user4.getServerAddress(), broker4PubKey, UserState.CONSUMING);
        usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2, userInfo3, userInfo4);
        
        req_106_Util.getUsersStatus(usersInfo);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0027_localRedistribution_LocalAndRemoteWorkersWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011012");
		XMPPAccount user3 = req_101_Util.createLocalUser("user011_3", "server011", "011013");
		XMPPAccount user4 = req_101_Util.createLocalUser("user011_4", "server011", "011014");

		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);

		//Change workers  to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		//Login with four valid users
	    String broker1PubKey = "publicKey1";
	    String broker2PubKey = "publicKey2";
	    String broker3PubKey = "publicKey3";
	    String broker4PubKey = "publicKey4";
	    
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
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient3 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID3 = new DeploymentID(new ContainerID("pcc3", "broker3", "broker"), broker3PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID3, peerControlClient3, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID3);
		
		try {
			peerControl.addUser(peerControlClient3, user3.getUsername() + "@" + user3.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient4 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID4 = new DeploymentID(new ContainerID("pcc4", "broker4", "broker"), broker4PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID4, peerControlClient4, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID4);
		
		try {
			peerControl.addUser(peerControlClient4, user4.getUsername() + "@" + user4.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
	    
	    DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
	    LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
	    
	    DeploymentID lwpc3OID = req_108_Util.login(component, user3, broker3PubKey);
	    LocalWorkerProviderClient lwpc3 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc3OID);
	    
	    DeploymentID lwpc4OID = req_108_Util.login(component, user4, broker4PubKey);
	    LocalWorkerProviderClient lwpc4 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc4OID);
	    
	    //Request three workers for client1 - expect to obtain all of them
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 3, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    WorkerAllocation allocationWorkerB = new WorkerAllocation(workerBDeploymentID);
	    WorkerAllocation allocationWorkerC = new WorkerAllocation(workerCDeploymentID);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec1, 
	    		allocationWorkerC, allocationWorkerB, allocationWorkerA);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));
	    
	    //Request two workers for client2 - expect to obtain one of them - the most recently allocated on client1
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, buildRequirements(null), 2, 0, 0);
	    allocationWorkerA = new WorkerAllocation(workerADeploymentID).addLoserConsumer(lwpc1OID).addLoserRequestSpec(requestSpec1);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, allocationWorkerA);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc2OID.getServiceID()));
	    
	    //Request one worker for client3 - expect to obtain one of them - the most recently allocated on client1
	    allocationWorkerB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(lwpc1OID);
		RequestSpecification spec3 = new RequestSpecification(0, new JobSpecification("label"), 3, buildRequirements(null), 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc3OID, lwpc3), spec3, allocationWorkerB);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc3OID.getServiceID()));
	    
	    //Request one worker for client4 - expect to obtain none of them
	    RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), 4, buildRequirements(null), 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc4OID, lwpc4), requestSpec4);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc4OID.getServiceID()));
	    
	    //Verify the clients' status
        UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), broker1PubKey, UserState.CONSUMING);
        UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), broker2PubKey, UserState.CONSUMING);
        UserInfo userInfo3 = new UserInfo(user3.getUsername(), user3.getServerAddress(), broker3PubKey, UserState.CONSUMING);
        UserInfo userInfo4 = new UserInfo(user4.getUsername(), user4.getServerAddress(), broker4PubKey, UserState.CONSUMING);
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2, userInfo3, userInfo4);
        
        req_106_Util.getUsersStatus(usersInfo);
        
        //Verify the workers' status
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2OID.getServiceID().toString());
        WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc3OID.getServiceID().toString());
        WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc1OID.getServiceID().toString());
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB, workerInfoC);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
       
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME, "dsPublicKey"), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
        req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
    	
        //GIS client 1 receive a remote worker provider 
        WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("rU1", "rS1", null, null);
        
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser",
				"rwpServer", "rwpPublicKey", remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Receive a remote worker - expect to allocate it to client4
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec1, "rworker1PK", broker4PubKey);
		
		//Receive a remote worker - expect to allocate it to client1
        WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("rU2", "rS1", null, null);
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec2, "rWorker2PK", broker1PubKey);
		
		//Receive a remote worker - expect to allocate it to client2
		WorkerSpecification remoteWorkerSpec3 = workerAcceptanceUtil.createClassAdWorkerSpec("rU3", "rS1", null, null);
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec3, "rWorker3PK", broker2PubKey);
		
		//Receive a remote worker - expect to allocate it to client1
		WorkerSpecification remoteWorkerSpec4 = workerAcceptanceUtil.createClassAdWorkerSpec("rU4", "rS1", null, null);
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec4, "rWorker4PK", broker1PubKey);
		
		//Verify if the clients was marked as CONSUMING
        userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), broker1PubKey, UserState.CONSUMING);
        userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), broker2PubKey, UserState.CONSUMING);
        userInfo3 = new UserInfo(user3.getUsername(), user3.getServerAddress(), broker3PubKey, UserState.CONSUMING);
        userInfo4 = new UserInfo(user4.getUsername(), user4.getServerAddress(), broker4PubKey, UserState.CONSUMING);
        usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2, userInfo3, userInfo4);
        
        req_106_Util.getUsersStatus(usersInfo);
	}
}