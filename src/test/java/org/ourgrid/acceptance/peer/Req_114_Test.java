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

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_114_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
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

@ReqTest(reqs="REQ114")
public class Req_114_Test extends PeerAcceptanceTestCase {

    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_114_Util req_114_Util = new Req_114_Util(getComponentContext());
    
    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
    /**
    * An unknown remote worker fails - Expect the Peer to ignore the failure and log the debug message.
    */
	@ReqTest(test="AT-114.1", reqs="REQ114")
	@Test public void test_AT_114_1_UnkonownRemoteWorkerFail() throws Exception {
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Notify the failure of an unknown remote Worker
		//Expect the debug to be logged
		DeploymentID unknownWorkerID = new DeploymentID(new ContainerID("a", "a", "a", "unknownPubKey"),"a");
		
		RemoteWorkerManagement unknownWorker = getMock(NOT_NICE, RemoteWorkerManagement.class);
		peerAcceptanceUtil.createStub(unknownWorker, RemoteWorkerManagement.class, unknownWorkerID);
		
		loggerMock.debug("Failure of an unknown or already disposed remote Worker [" + unknownWorkerID.getServiceID()  + "]. This notification was ignored.");
		replayActiveMocks();
		
		peerAcceptanceUtil.getRemoteWorkerMonitor().doNotifyFailure(unknownWorker, unknownWorkerID);
		
		verifyActiveMocks();
	}
	
	/**
	 * This test contains the following steps:
	 *
	 * 1. A local consumer requests one Worker;
	 * 2. There is no workers, so the request is forwarded to the community;
	 * 3. The peer receives one remote Worker from a Worker Provider. This worker is allocated to the local consumer - Expect the Peer to:
	 *       1. Register interest in the failure of the Worker;
	 *       2. Command the Worker to work for the local consumer;
	 * 4. The monitored remote Worker fails - Expect the Peer to:
	 *       1. Log the info;
	 *       2. Dispose the Worker;
	 */
	@ReqTest(test="AT-114.2", reqs="REQ114")
	@Test public void test_AT_114_2_RemoteWorkerFailBeforeDelivered() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerAPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerAPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(component, user, brokerAPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    long request1ID = 1;
	   
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
	  
	    //GIS client receive a remote worker provider
        WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("rU1", "rS1");
        
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "rworker1PK", brokerAPubKey).getDeploymentID();
		
		//The remote Worker fails
		//Expect the info to be logged
		//Expect the Worker to be disposed
		req_114_Util.notifyRemoteWorkerFailure(component, rwm1DID, rwp, rwpID);
	}
	
	/**
	 * This test contains the following steps:
	 *  1. A local consumer requests one Worker;
	 *  2. There is no workers, so the request is forwarded to the community;
	 *  3. The peer receives one remote Worker from a Worker Provider. This worker is allocated to the local consumer - Expect the Peer to command the Worker to work for the local consumer;
	 *  4. The worker sends a status changed message (allocated for broker) - The peer delivers the worker to the local consumer;
	 *  5. This remote Worker fails - Expect the Peer to ignore the failure and log the debug message.
	 */
	@ReqTest(test="AT-114.3", reqs="REQ114")
	@Test public void test_AT_114_3_RemoteWorkerFailAfterDelivered() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerAPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerAPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(component, user, brokerAPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    long request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
		
	    //GIS client receive a remote worker provider
        WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("rU1", "rS1");
        
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "rworker1PK", brokerAPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwm1DID, workerSpecR1, requestSpec1);
		
		req_114_Util.notifyRemoteWorkerFailure(component, true, rwm1DID, rwp, rwpID);
	}

	/**
	 * This test contains the following steps:
	 *
	 * 1. A local consumer requests one Worker;
	 * 2. There is no workers, so the request is forwarded to the community;
	 * 3. The peer receives one remote Worker from a Worker Provider. This worker is allocated to the local consumer - Expect the Peer to:
	 *       1. Register interest in the failure of the Worker;
	 *       2. Command the Worker to work for the local consumer;
	 * 4. The monitored remote Worker fails - Expect the Peer to:
	 *       1. Log the info;
	 *       2. Dispose the Worker;
	 */
	@ReqTest(test="AT-114.2", reqs="REQ114")
	@Category(JDLCompliantTest.class) @Test public void test_AT_114_2_RemoteWorkerFailBeforeDeliveredWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
	
		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerAPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerAPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(component, user, brokerAPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    long request1ID = 1;
	
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( null ), 1, 0, 0);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
	
	    //GIS client receive a remote worker provider
	    WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("rU1", "rS1", null, null);
	    
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "rworker1PK", brokerAPubKey).getDeploymentID();
		
		//The remote Worker fails
		//Expect the info to be logged
		//Expect the Worker to be disposed
		req_114_Util.notifyRemoteWorkerFailure(component, rwm1DID, rwp, rwpID);
	}

	/**
	 * This test contains the following steps:
	 *  1. A local consumer requests one Worker;
	 *  2. There is no workers, so the request is forwarded to the community;
	 *  3. The peer receives one remote Worker from a Worker Provider. This worker is allocated to the local consumer - Expect the Peer to command the Worker to work for the local consumer;
	 *  4. The worker sends a status changed message (allocated for broker) - The peer delivers the worker to the local consumer;
	 *  5. This remote Worker fails - Expect the Peer to ignore the failure and log the debug message.
	 */
	@ReqTest(test="AT-114.3", reqs="REQ114")
	@Category( JDLCompliantTest.class) @Test public void test_AT_114_3_RemoteWorkerFailAfterDeliveredWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
	
		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerAPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerAPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(component, user, brokerAPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    long request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( null ), 1, 0, 0);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
		
	    //GIS client receive a remote worker provider
	    WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("rU1", "rS1", null, null);
	    
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "rworker1PK", brokerAPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwm1DID, workerSpecR1, requestSpec1);
		
		//Notify the failure of the Worker that was already delivered
		req_114_Util.notifyRemoteWorkerFailure(component, true, rwm1DID, rwp, rwpID);
	}
}
