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

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.RemoteAllocation;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_014_Util;
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
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

@ReqTest(reqs="REQ014")
public class Req_014_Test extends PeerAcceptanceTestCase {


	private PeerComponent component;
	
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private PeerAcceptanceUtil peerAcceptanceUtil = new PeerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_014_Util req_014_Util = new Req_014_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());
    
    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
    /**
     * This test contains the following steps:
   	 *	1. A local consumer requests 1 worker - the peer have not idle workers, so schedule the request for repetition;
   	 *	2. The local consumer finishes the request - expect the peer to cancel the schedule repetition and mark the consumer as LOGGED.
     */
	@ReqTest(test="AT-014.1", reqs="REQ014")
	@Test public void test_AT_014_1_FinishRequestWithoutAllocatedWorkers() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Login with a valid user
	    String brokerPubKey = "publicKeyA";
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("peerClient", "peerClientServer", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    DeploymentID lwpcOID = req_108_Util.login(component, user, brokerPubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    //Request a worker for the logged user
	    int request1ID = 1;
	    
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    
	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
	    
	    //Finish the request - expect to cancel the request repetition
	    ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
	    
	    req_014_Util.finishRequestWithLocalWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, brokerPubKey, 
	    		lwpcOID.getServiceID(), future1, requestSpec1, new LinkedList<WorkerAllocation>());
	    
	    //Verify if the client was marked as LOGGED
        UserInfo userInfo = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.LOGGED);
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo);
        
        req_106_Util.getUsersStatus(usersInfo);
	}
	
    /**
	 * 1.  A local consumer requests 2 workers and obtain a local idle worker - expect the peer to schedule the request repetition;
	 * 2. The peer receives a remote worker, which is allocated for the local consumer - expect the peer to cancel the schedule repetition;
	 * 3. The local consumer finishes the request - expect the peer to:
	 *	   1. Dispose the remote worker;
	 *	   2. Command the local worker to stop working;
	 *	   3. Mark the consumer as LOGGED.
     */
	@ReqTest(test="AT-014.2", reqs="REQ014")
	@Test public void test_AT_014_2_FinishIsolatedRequestWithAllocatedWorkers() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		
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
		
		//Start peer and set mocks for logger and timer
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
	    //Set worker
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		//Change workers  to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magodosnos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Consumer login and request two workers
	    String brokerPubKey = "publicKey1";
	    DeploymentID lwpcOID = req_108_Util.login(component, user, brokerPubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
	    
	    
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, 
	    		  allocationWorkerA);
	    
	    //GIS client receive a remote worker provider
        WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createWorkerSpec("rU1", "rS1");
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID remoteWorkerOID = req_018_Util.receiveRemoteWorker(
				component, rwp, rwpID, remoteWorkerSpec1, "rworker1PK", brokerPubKey, future1).getDeploymentID();
		
		//Finish the request
		WorkerAllocation remoteWorkerAllocation = new WorkerAllocation(remoteWorkerOID).addRemoteWorkerManagementClient(
				peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		RemoteAllocation remotePeer = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteWorkerAllocation));
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, brokerPubKey,
				lwpcOID.getServiceID(), requestSpec1, AcceptanceTestUtil.createList(allocationWorkerA), new TestStub(rwpID, remotePeer));
	    
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADeploymentID.getServiceID()));
		
	    //Verify if the client was marked as LOGGED
        UserInfo userInfo = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.LOGGED);
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo);
        
        req_106_Util.getUsersStatus(usersInfo);
	}
	
	/**
	 * This test contains the following steps:
	   1. A local consumer requests 5 workers (mem > 128) and obtain a local idle worker (os = linux; mem = 256) - expect the peer to schedule the request repetition;
	   2. The peer receives a remote worker (os = windows; mem = 256), which is allocated for the local consumer;
	   3. The peer receives a remote worker (os = linux; mem = 256), which is allocated for the local consumer;
	   4. A local worker(os = windows; mem = 256) becomes idle, which is allocated for the local consumer;
	   5. Four local worker(os = linux; mem = 64) becomes idle, which are not allocated for the local consumer;
	   6. Other local consumer requests 6 workers (os = linux) and obtain four local idle workers (os = linux; mem = 64) - expect the peer to schedule the request repetition;
	   7. The first local consumer finishes the request - expect the peer to:
	         1. Cancel the first consumer's request repetition;
	         2. Command the local worker (os = windows; mem = 256) to stop working;
	         3. Allocate the remote worker (os = linux; mem = 256) for the other consumer;
	         4. Dispose the remote worker (os = windows; mem = 256);
	         5. Allocate the local worker (os = linux; mem = 256) for the other consumer - expect the peer to cancel the other consumer's request repetition;
	         6. Mark the first consumer as LOGGED.
	 */
	@ReqTest(test="AT-014.3", reqs="REQ014")
	@Test public void test_AT_014_3_FinishNonIsolatedRequestWithAllocatedWorkers() throws Exception {
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Start peer and set mocks for logger and timer
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
	    // Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		workerSpecA.putAttribute("os", "linux");
		workerSpecA.putAttribute("mem", "256");
		
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		workerSpecB.putAttribute("os", "windows");
		workerSpecB.putAttribute("mem", "256");
		
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		workerSpecC.putAttribute("os", "linux");
		workerSpecC.putAttribute("mem", "64");
		
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S1");
		workerSpecD.putAttribute("os", "linux");
		workerSpecD.putAttribute("mem", "64");
		
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createWorkerSpec("U5", "S1");
		workerSpecE.putAttribute("os", "linux");
		workerSpecE.putAttribute("mem", "64");
		
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createWorkerSpec("U6", "S1");
		workerSpecF.putAttribute("os", "linux");
		workerSpecF.putAttribute("mem", "64");
		
		
		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		String workerDPublicKey = "workerDPublicKey";
		String workerEPublicKey = "workerEPublicKey";
		String workerFPublicKey = "workerFPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);
		
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);

		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDDeploymentID);
		
		DeploymentID workerEDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEDeploymentID);
		
		DeploymentID workerFDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, workerFPublicKey);
		req_010_Util.workerLogin(component, workerSpecF, workerFDeploymentID);
		
		//Change worker A to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magodosnos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Consumer login and request five workers
	    String broker1PubKey = "publicKey1";
	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "mem > 128", 5, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);

	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, allocationWorkerA);
		
	    //GIS client receive a remote worker provider
        WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createWorkerSpec("rU1", "rS1");
        remoteWorkerSpec1.putAttribute("os", "windows");
        remoteWorkerSpec1.putAttribute("mem", "256");
        
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (os = windows; mem = 256)
		DeploymentID remoteWorker1OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec1, "rworker1PK", broker1PubKey).getDeploymentID();
		
		//Remote worker provider client receive a remote worker (os = linux; mem = 256)
        WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createWorkerSpec("rU2", "rS1");
        remoteWorkerSpec2.putAttribute("os", "linux");
        remoteWorkerSpec2.putAttribute("mem", "256");

        DeploymentID remoteWorker2OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec2, "rworker2PK", broker1PubKey).getDeploymentID();
        
        //Change worker B (os = windows; mem = 256) status to IDLE
        req_025_Util.changeWorkerStatusToIdleWorkingForBroker(workerBDeploymentID, lwpc1OID, component);
        
        //Change 4 workers (os = linux; mem = 64) status to IDLE
        req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecC, workerCDeploymentID, dsID);
        req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecD, workerDDeploymentID, dsID);
        req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecE, workerEDeploymentID, dsID);
        req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecF, workerFDeploymentID, dsID);
		
		//Other consumer login and request six workers
	    String broker2PubKey = "publicKey2";
	    DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
	    
	    LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "os = linux", 6, 0, 0);
	    WorkerAllocation allocationWorkerC = new WorkerAllocation(workerCDeploymentID);
	    WorkerAllocation allocationWorkerD = new WorkerAllocation(workerDDeploymentID);
	    WorkerAllocation allocationWorkerE = new WorkerAllocation(workerEDeploymentID);
	    WorkerAllocation allocationWorkerF = new WorkerAllocation(workerFDeploymentID);

	    //rwpID = AcceptanceTestUtil.getStubDeploymentID(rwp);
	    TestStub tStub = new TestStub(rwpID, rwp);
	    
	    AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
	    peerAcceptanceUtil.getRemoteWorkerProviderClient().workerProviderIsUp(rwp, rwpID, AcceptanceTestUtil.getCertificateMock(rwpID));
	    
	    List<TestStub> rwps = new ArrayList<TestStub>();
	    rwps.add(tStub);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, rwps, allocationWorkerC,
	    		allocationWorkerD, allocationWorkerE, allocationWorkerF);
	    
	    //Finish the request
	    RemoteWorkerManagementClient rwmc = peerAcceptanceUtil.getRemoteWorkerManagementClient();
	    WorkerAllocation remoteWorker1Allocation = new WorkerAllocation(remoteWorker1OID).addRemoteWorkerManagementClient(rwmc);
	    WorkerAllocation remoteWorker2Allocation = new WorkerAllocation(remoteWorker2OID).addRemoteWorkerManagementClient(rwmc).addWinnerConsumer(lwpc2OID);
	    
	    RemoteAllocation remotePeer = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteWorker1Allocation,
	    		remoteWorker2Allocation));
	    
	    allocationWorkerA = new WorkerAllocation(workerADeploymentID).addWinnerConsumer(lwpc2OID);
	    WorkerAllocation allocationWorkerB = new WorkerAllocation(workerBDeploymentID);
	    List<WorkerAllocation> localWorkers = AcceptanceTestUtil.createList(allocationWorkerA, allocationWorkerB); 
	    
	    ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
	    
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, broker1PubKey, 
				lwpc1OID.getServiceID(), future1,
				requestSpec1, localWorkers, new TestStub(rwpID, remotePeer));
    
	    //Verify if the client was marked as LOGGED
        UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), broker1PubKey, UserState.LOGGED);
        UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), broker2PubKey, UserState.CONSUMING);
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo2, userInfo1);
        
        req_106_Util.getUsersStatus(usersInfo);
	}
	
	/**
	 * This test contains the following steps:
	 * 1. A local consumer requests 1 worker and obtain 1 local idle worker;
	 * 2. Other local consumer requests 2 workers - expect the peer to schedule the request repetition;
	 * 3. The first local consumer pauses the request;
	 * 4. The first local consumer disposes the local worker, which is allocated for the other consumer;
	 * 5. The first local consumer finishes the request - expect the peer to:
	 *      1. Do not affect the disposed worker;
	 *      2. Mark the first consumer as LOGGED.
	 */
	@ReqTest(test="AT-014.4", reqs="REQ014")
	@Test public void test_AT_014_4_FinishRequestThatDisposedWorkerIsAlreadyAllocatedToAnotherConsumer() throws Exception {
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Start peer and set mocks for logger and timer
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
	    // Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");

		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		//test - worker A isn't IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Consumer login and request one worker
	    String broker1PubKey = "publicKeyA";
	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
	    
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, allocationWorkerA);
	    
	    //Change worker status to ALLOCATED FOR BROKER
		Worker worker = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerADeploymentID,
				workerSpecA, requestSpec1).getObject();
	    	    
	    //Other Consumer login and request two workers
	    String broker2PubKey = "publicKeyB";
	    DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
	    
	    LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
	    int request2ID = 2;
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, "", 2, 0, 0);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2);
	    
	    //The first Consumer pauses the request
	    req_117_Util.pauseRequest(component, lwpc1OID, request1ID, null);
	    
	    //The first Consumer disposes the worker - expect the peer to allocate the worker for the other local consumer
	    WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
	    allocationA.addLoserConsumer(lwpc1OID).addLoserRequestSpec(requestSpec1).addWinnerConsumer(lwpc2OID);
	    req_015_Util.localConsumerDisposesLocalWorker(component, worker, allocationA);
	    
	    //Finish the request 1
	    LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
	    lwp.finishRequest(requestSpec1);
	    
	    //Verify if the client was marked as LOGGED
        UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), broker1PubKey, UserState.LOGGED);
        UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), broker2PubKey, UserState.CONSUMING);
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo2, userInfo1);
        
        req_106_Util.getUsersStatus(usersInfo);
	}
	
	/**
	 * Verifies if the peer ignores finish request, in these scenarios:
	 *   Unknown consumer;
	 *   Unknown request;
	 *   Request from other consumer;
	 *   An already finished request.
	 */
	@ReqTest(test="AT-014.5", reqs="REQ014")
	@Test public void test_AT_014_5_InputValidation() throws Exception {
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Start peer and set mocks for logger and timer
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
		//A unknown consumer finishes a request - expect to log warn
		String unknownPubKey = "unknownPubKey";
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		DeploymentID unknownID = new DeploymentID(new ContainerID("broker", "broker", "broker", unknownPubKey),"broker");
		
		loggerMock.warn("Ignoring an unknown consumer that finished a request. Sender public key: " + unknownPubKey);
		replayActiveMocks();
		
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);

		LocalWorkerProvider lwp = (LocalWorkerProvider) lwpOD.getObject();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, unknownID);
		lwp.finishRequest(requestSpec1);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		//Login with the consumer1
	    String broker1PubKey = "publicKeyA";
	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    
	    //A local consumer finishes an unknown request - expect to log warn
		loggerMock.warn("The consumer ["+ lwpc1OID.getServiceID().toString() +"] finished the unknown request ["+ request1ID +"]. This message was ignored.");
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
		lwp.finishRequest(requestSpec1);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		//Login with the consumer2
	    String broker2PubKey = "publicKeyB";
	    DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
	    
	    //Request a worker for the consumer2
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc), requestSpec1);
	    
	    //A local consumer finishes a request done by other consumer - expect to log warn
	    EasyMock.reset(loggerMock);
		loggerMock.warn("The consumer ["+ lwpc1OID.getServiceID().toString() +"] finished the unknown request ["+ request1ID +"]. This message was ignored.");
		replayActiveMocks();
	    
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
	    lwp.finishRequest(requestSpec1);
	    
		verifyActiveMocks();
		resetActiveMocks();
		
		//Finish the request
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
		loggerMock.debug("Request " + request1ID + " finished by [" + lwpc2OID.getServiceID().toString() + "].");
		replayActiveMocks();
		
		lwp.finishRequest(requestSpec1);

		verifyActiveMocks();
		resetActiveMocks();
	    
	    //Finish the request again
	    loggerMock.warn("The consumer ["+ lwpc2OID.getServiceID().toString() +"] finished the unknown request ["+ request1ID +"]. This message was ignored.");
		replayActiveMocks();
	    
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
	    lwp.finishRequest(requestSpec1);
	    
		verifyActiveMocks();
		resetActiveMocks();
	}

	/**
	 * This test contains the following steps:
	 *	1. A local consumer requests 1 worker - the peer have not idle workers, so schedule the request for repetition;
	 *	2. The local consumer finishes the request - expect the peer to cancel the schedule repetition and mark the consumer as LOGGED.
	 */
	@ReqTest(test="AT-014.1", reqs="REQ014")
	@Category(JDLCompliantTest.class) @Test public void test_AT_014_1_FinishRequestWithoutAllocatedWorkersWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Login with a valid user
	    String brokerPubKey = "publicKeyA";
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("peerClient", "peerClientServer", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    DeploymentID lwpcOID = req_108_Util.login(component, user, brokerPubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    //Request a worker for the logged user
	    int request1ID = 1;
	    
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
	    
	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
	    
	    //Finish the request - expect to cancel the request repetition
	    ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
	    
	    req_014_Util.finishRequestWithLocalWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
	    		brokerPubKey, lwpcOID.getServiceID(), future1,
	    		requestSpec1, new LinkedList<WorkerAllocation>());
	    
	    //Verify if the client was marked as LOGGED
	    UserInfo userInfo = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.LOGGED);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo);
	    
	    req_106_Util.getUsersStatus(usersInfo);
	}

	/**
	 * 1.  A local consumer requests 2 workers and obtain a local idle worker - expect the peer to schedule the request repetition;
	 * 2. The peer receives a remote worker, which is allocated for the local consumer - expect the peer to cancel the schedule repetition;
	 * 3. The local consumer finishes the request - expect the peer to:
	 *	   1. Dispose the remote worker;
	 *	   2. Command the local worker to stop working;
	 *	   3. Mark the consumer as LOGGED.
	 */
	@ReqTest(test="AT-014.2", reqs="REQ014")
	@Category(JDLCompliantTest.class) @Test public void test_AT_014_2_FinishIsolatedRequestWithAllocatedWorkersWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		
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
		
		//Start peer and set mocks for logger and timer
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
	    //Set worker
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);

		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		//Change workers  to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magodosnos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Consumer login and request two workers
	    String brokerPubKey = "publicKey1";
	    DeploymentID lwpcOID = req_108_Util.login(component, user, brokerPubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
	    
	    
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, 
	    		  allocationWorkerA);
	    
	    //GIS client receive a remote worker provider
	    WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("rU1", "rS1", null, null);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID remoteWorkerOID = req_018_Util.receiveRemoteWorker(
				component, rwp, rwpID, remoteWorkerSpec1, "rworker1PK", brokerPubKey, future1).getDeploymentID();
		
		//Finish the request
		WorkerAllocation remoteWorkerAllocation = new WorkerAllocation(remoteWorkerOID).addRemoteWorkerManagementClient(
				peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		RemoteAllocation remotePeer = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteWorkerAllocation));
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), 
				lwpOD, brokerPubKey, lwpcOID.getServiceID(),
				requestSpec1, AcceptanceTestUtil.createList(allocationWorkerA), new TestStub(rwpID, remotePeer));
	    
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADeploymentID.getServiceID()));
		
	    //Verify if the client was marked as LOGGED
	    UserInfo userInfo = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.LOGGED);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo);
	    
	    req_106_Util.getUsersStatus(usersInfo);
	}

	/**
	 * This test contains the following steps:
	   1. A local consumer requests 5 workers (mem > 128) and obtain a local idle worker (os = linux; mem = 256) - expect the peer to schedule the request repetition;
	   2. The peer receives a remote worker (os = windows; mem = 256), which is allocated for the local consumer;
	   3. The peer receives a remote worker (os = linux; mem = 256), which is allocated for the local consumer;
	   4. A local worker(os = windows; mem = 256) becomes idle, which is allocated for the local consumer;
	   5. Four local worker(os = linux; mem = 64) becomes idle, which are not allocated for the local consumer;
	   6. Other local consumer requests 6 workers (os = linux) and obtain four local idle workers (os = linux; mem = 64) - expect the peer to schedule the request repetition;
	   7. The first local consumer finishes the request - expect the peer to:
	         1. Cancel the first consumer's request repetition;
	         2. Command the local worker (os = windows; mem = 256) to stop working;
	         3. Allocate the remote worker (os = linux; mem = 256) for the other consumer;
	         4. Dispose the remote worker (os = windows; mem = 256);
	         5. Allocate the local worker (os = linux; mem = 256) for the other consumer - expect the peer to cancel the other consumer's request repetition;
	         6. Mark the first consumer as LOGGED.
	 */
	@ReqTest(test="AT-014.3", reqs="REQ014")
	@Category(JDLCompliantTest.class) @Test public void test_AT_014_3_FinishNonIsolatedRequestWithAllocatedWorkersWithJDL() throws Exception {
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Start peer and set mocks for logger and timer
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
	    // Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", 256, "linux");
		
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", 256, "windows");
		
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", 64, "linux");
		
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S1", 64, "linux");
		
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createClassAdWorkerSpec("U5", "S1", 64, "linux");
		
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createClassAdWorkerSpec("U6", "S1", 64, "linux");
		
		
		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		String workerDPublicKey = "workerDPublicKey";
		String workerEPublicKey = "workerEPublicKey";
		String workerFPublicKey = "workerFPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);
		
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);
		
		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDDeploymentID);
		
		DeploymentID workerEDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEDeploymentID);
		
		DeploymentID workerFDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, workerFPublicKey);
		req_010_Util.workerLogin(component, workerSpecF, workerFDeploymentID);
		
		//Change worker A to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magodosnos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Consumer login and request five workers
	    String broker1PubKey = "publicKey1";
	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(">", 128, null, null), 5, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	
	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, allocationWorkerA);
		
	    //GIS client receive a remote worker provider
	    WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("rU1", "rS1", 256, "windows");
	    
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (os = windows; mem = 256)
		DeploymentID remoteWorker1OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec1, "rworker1PK", broker1PubKey).getDeploymentID();
		
		//Remote worker provider client receive a remote worker (os = linux; mem = 256)
	    WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("rU2", "rS1", 256, "linux");
	
	    DeploymentID remoteWorker2OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec2, "rworker2PK", broker1PubKey).getDeploymentID();
	    
	    //Change worker B (os = windows; mem = 256) status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(workerBDeploymentID, lwpc1OID, component);
	    
	    //Change 4 workers (os = linux; mem = 64) status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecC, workerCDeploymentID, dsID);
	    req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecD, workerDDeploymentID, dsID);
	    req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecE, workerEDeploymentID, dsID);
	    req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecF, workerFDeploymentID, dsID);
		
		//Other consumer login and request six workers
	    String broker2PubKey = "publicKey2";
	    DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
	    
	    LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "[Requirements=other.OS == \"linux\";Rank=0]", 6, 0, 0);
	    WorkerAllocation allocationWorkerC = new WorkerAllocation(workerCDeploymentID);
	    WorkerAllocation allocationWorkerD = new WorkerAllocation(workerDDeploymentID);
	    WorkerAllocation allocationWorkerE = new WorkerAllocation(workerEDeploymentID);
	    WorkerAllocation allocationWorkerF = new WorkerAllocation(workerFDeploymentID);
	
	    //rwpID = AcceptanceTestUtil.getStubDeploymentID(rwp);
	    TestStub tStub = new TestStub(rwpID, rwp);
	    
	    AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
	    peerAcceptanceUtil.getRemoteWorkerProviderClient().workerProviderIsUp(rwp, rwpID, AcceptanceTestUtil.getCertificateMock(rwpID));
	    
	    List<TestStub> rwps = new ArrayList<TestStub>();
	    rwps.add(tStub);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, rwps, allocationWorkerC,
	    		allocationWorkerD, allocationWorkerE, allocationWorkerF);
	    
	    //Finish the request
	    RemoteWorkerManagementClient rwmc = peerAcceptanceUtil.getRemoteWorkerManagementClient();
	    WorkerAllocation remoteWorker1Allocation = new WorkerAllocation(remoteWorker1OID).addRemoteWorkerManagementClient(rwmc);
	    WorkerAllocation remoteWorker2Allocation = new WorkerAllocation(remoteWorker2OID).addRemoteWorkerManagementClient(rwmc).addWinnerConsumer(lwpc2OID);
	    
	    RemoteAllocation remotePeer = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteWorker1Allocation,
	    		remoteWorker2Allocation));
	    
	    allocationWorkerA = new WorkerAllocation(workerADeploymentID).addWinnerConsumer(lwpc2OID);
	    WorkerAllocation allocationWorkerB = new WorkerAllocation(workerBDeploymentID);
	    List<WorkerAllocation> localWorkers = AcceptanceTestUtil.createList(allocationWorkerA, allocationWorkerB); 
	    
	    ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
	    
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, broker1PubKey, 
				lwpc1OID.getServiceID(), future1, requestSpec1, localWorkers, new TestStub(rwpID, remotePeer));
	
	    //Verify if the client was marked as LOGGED
	    UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), broker1PubKey, UserState.LOGGED);
	    UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), broker2PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo2, userInfo1);
	    
	    req_106_Util.getUsersStatus(usersInfo);
	}

	/**
	 * This test contains the following steps:
	 * 1. A local consumer requests 1 worker and obtain 1 local idle worker;
	 * 2. Other local consumer requests 2 workers - expect the peer to schedule the request repetition;
	 * 3. The first local consumer pauses the request;
	 * 4. The first local consumer disposes the local worker, which is allocated for the other consumer;
	 * 5. The first local consumer finishes the request - expect the peer to:
	 *      1. Do not affect the disposed worker;
	 *      2. Mark the first consumer as LOGGED.
	 */
	@ReqTest(test="AT-014.4", reqs="REQ014")
	@Category(JDLCompliantTest.class) @Test public void test_AT_014_4_FinishRequestThatDisposedWorkerIsAlreadyAllocatedToAnotherConsumerWithJDL() throws Exception {
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Start peer and set mocks for logger and timer
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
	    // Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
	
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		//test - worker A isn't IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Consumer login and request one worker
	    String broker1PubKey = "publicKeyA";
	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
	    
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, allocationWorkerA);
	    
	    //Change worker status to ALLOCATED FOR BROKER
		Worker worker = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerADeploymentID,
				workerSpecA, requestSpec1).getObject();
	    	    
	    //Other Consumer login and request two workers
	    String broker2PubKey = "publicKeyB";
	    DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
	    
	    LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
	    int request2ID = 2;
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2);
	    
	    //The first Consumer pauses the request
	    req_117_Util.pauseRequest(component, lwpc1OID, request1ID, null);
	    
	    //The first Consumer disposes the worker - expect the peer to allocate the worker for the other local consumer
	    WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
	    allocationA.addLoserConsumer(lwpc1OID).addLoserRequestSpec(requestSpec1).addWinnerConsumer(lwpc2OID);
	    req_015_Util.localConsumerDisposesLocalWorker(component, worker, allocationA);
	    
	    //Finish the request 1
	    LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
	    lwp.finishRequest(requestSpec1);
	    
	    //Verify if the client was marked as LOGGED
	    UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), broker1PubKey, UserState.LOGGED);
	    UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), broker2PubKey, UserState.CONSUMING);
	    List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo2, userInfo1);
	    
	    req_106_Util.getUsersStatus(usersInfo);
	}

	/**
	 * Verifies if the peer ignores finish request, in these scenarios:
	 *   Unknown consumer;
	 *   Unknown request;
	 *   Request from other consumer;
	 *   An already finished request.
	 */
	@ReqTest(test="AT-014.5", reqs="REQ014")
	@Category(JDLCompliantTest.class) @Test public void test_AT_014_5_InputValidationWithJDL() throws Exception {
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		
	    PeerControlClient peerControlClient1 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID1 = new DeploymentID(new ContainerID("peerClient1", "peerClientServer1", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID1, peerControlClient1, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Start peer and set mocks for logger and timer
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
		//A unknown consumer finishes a request - expect to log warn
		String unknownPubKey = "unknownPubKey";
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		DeploymentID unknownID = new DeploymentID(new ContainerID("broker", "broker", "broker", unknownPubKey),"broker");
		
		loggerMock.warn("Ignoring an unknown consumer that finished a request. Sender public key: " + unknownPubKey);
		replayActiveMocks();
		
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
	
		LocalWorkerProvider lwp = (LocalWorkerProvider) lwpOD.getObject();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, unknownID);
		lwp.finishRequest(requestSpec1);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		//Login with the consumer1
	    String broker1PubKey = "publicKeyA";
	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    
	    //A local consumer finishes an unknown request - expect to log warn
		loggerMock.warn("The consumer ["+ lwpc1OID.getServiceID().toString() +"] finished the unknown request ["+ request1ID +"]. This message was ignored.");
		replayActiveMocks();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
		lwp.finishRequest(requestSpec1);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		//Login with the consumer2
	    String broker2PubKey = "publicKeyB";
	    DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
	    
	    //Request a worker for the consumer2
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc), requestSpec1);
	    
	    //A local consumer finishes a request done by other consumer - expect to log warn
	    EasyMock.reset(loggerMock);
		loggerMock.warn("The consumer ["+ lwpc1OID.getServiceID().toString() +"] finished the unknown request ["+ request1ID +"]. This message was ignored.");
		replayActiveMocks();
	    
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
	    lwp.finishRequest(requestSpec1);
	    
		verifyActiveMocks();
		resetActiveMocks();
		
		//Finish the request
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
		loggerMock.debug("Request " + request1ID + " finished by [" + lwpc2OID.getServiceID().toString() + "].");
		replayActiveMocks();
		
		lwp.finishRequest(requestSpec1);

		verifyActiveMocks();
		resetActiveMocks();
	    
	    //Finish the request again
		loggerMock.warn("The consumer ["+ lwpc2OID.getServiceID().toString() +"] finished the unknown request ["+ request1ID +"]. This message was ignored.");
		replayActiveMocks();
	    
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
	    lwp.finishRequest(requestSpec1);
	    
		verifyActiveMocks();
		resetActiveMocks();
	}
}