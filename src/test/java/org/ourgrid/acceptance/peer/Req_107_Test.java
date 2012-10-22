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

import java.io.File;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

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
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_037_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_107_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ107")
public class Req_107_Test extends PeerAcceptanceTestCase {
	
	public static final String COMM_FILE_PATH = "req_107"+File.separator;
	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_037_Util req_037_Util = new Req_037_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_107_Util req_107_Util = new Req_107_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    
	@Before
	public void setUp() throws Exception {
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.createNewFile(); 
		super.setUp();
		component = req_010_Util.startPeer();
	} 
	
	@After
	public void tearDown() throws Exception {
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.delete();
		super.tearDown();
	}
	
	/**
	 * This test contains the following steps:
	 *
	 *  1. The peer sets a Worker A with the following workerSpec:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         os=debian
	 *  2. This worker recovers
	 *  3. This worker updates its spec with the following new attributes:
	 *         servername=xmpp.anyServer.com
	 *         memuse=0.4
	 *  4. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         os=debian
	 *         memuse=0.4
	 *  5. This worker changes its status to IDLE
	 *  6. This worker updates its spec with the following new attributes:
	 *         username=null
	 *         cpubal=0.6
	 *  7. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         os=debian
	 *         memuse=0.4
	 *         cpubal=0.6
	 *  8. The peer sets a Worker A with the following workerSpec:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         os=linux
	 *  9. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         os=linux
	 * 10. A remote client requests 1 worker - The local Worker A is commanded to Work for that Peer
	 * 11. This worker updates its spec with the following new attributes:
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 12. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         os=linux
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 13. This worker changes its status to ALLOCATED_FOR_PEER and is delivered to the remote consumer
	 * 14. This worker updates its spec with the following new attributes:
	 *         os=null
	 * 15. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 16. This worker updates its spec with the following new attributes:
	 *         os=ubuntu
	 * 17. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         memuse=0.9
	 *         cpubal=0.2
	 *         os=ubuntu
	 * 18. The worker A is disposed by the remote consumer
	 * 19. This worker updates its spec with the following new attributes - expect the peer not to log the message
	 *         username=null
	 * 20. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         memuse=0.9
	 *         cpubal=0.2
	 *         os=ubuntu
	 * 21. It is notified the failure of the Worker A
	 * 22. This worker updates its spec with the following new attributes:
	 *         memuse=0.0
	 * 23. Expect the Peer to ignore this message and log the debug message;
	 * 24. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.ourgrid.org
	 *         os=ubuntu
	 *
	 */
	@ReqTest(test="AT-107.1", reqs="REQ107")
	@Test public void test_AT_107_1_LocalWorkerSpecChange() throws Exception {
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"107_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Login of worker with special attibutes
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", "xmpp.ourgrid.org");
		workerSpecA.putAttribute("os", "debian");
		
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		WorkerManagement worker = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerADeploymentID);
		
		//Worker A updates spec
		WorkerSpecification updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("servername", "xmpp.anyServer.com");
		updateWorkerSpec.putAttribute("memuse", "0.4");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerADeploymentID, false);
		
		//Verify local workers status
		workerSpecA.putAttribute("memuse", "0.4");
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.OWNER);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("username", null);
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.6");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerADeploymentID, false);
		
		//Verify local workers status
		workerSpecA.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.6");
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		worker = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerADeploymentID);
		
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Verify local workers status
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);
		
		//Request a worker for the remote Consumer
		int request1ID = 1;
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
		
		WorkerAllocation workerAllocationA = new WorkerAllocation(workerADeploymentID);
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1, 0, workerAllocationA);
		
		//Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("memuse", "0.9");
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.2");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerADeploymentID, false);
		
		//Verify local workers status
		workerSpecA.putAttribute("memuse", "0.9");
		workerSpecA.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.2");
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, remoteClientOID.getServiceID().toString());
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//Change worker A status to ALLOCATED FOR PEER
		RemoteWorkerManagement rWorkerManag = req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerADeploymentID, 
				workerSpecA, remoteClientOID);
		
		//Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("os", null);
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerADeploymentID, false);
		
		//Verify local workers status
		workerSpecA.removeAttribute("os");
		
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, remoteClientOID.getServiceID().toString());
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("os", "ubuntu");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerADeploymentID, false);
		
		//Verify local workers status
		workerSpecA.putAttribute("os", "ubuntu");
		
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, remoteClientOID.getServiceID().toString());
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//The remote peer disposes the worker
		req_015_Util.remoteDisposeLocalWorker(component, remoteClientOID, rWorkerManag, workerADeploymentID);
		
		//Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("username", null);
		
		AcceptanceTestUtil.publishTestObject(component, workerADeploymentID, worker, WorkerManagement.class);
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerADeploymentID, false);
		
		//Verify local workers status
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//The Worker fails
		peerAcceptanceUtil.getWorkerMonitor().doNotifyFailure((WorkerManagement) AcceptanceTestUtil.getBoundObject(workerADeploymentID), 
				workerADeploymentID);
		
		//Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("memuse", "0.0");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerADeploymentID, true);
		
		//Verify local workers status
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
		localWorkersInfo = AcceptanceTestUtil.createList();
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	/**
	 * This test contains the following steps:
	 *  1. One local client request one Worker - There is no available Worker, so the request is forwarded to the community
	 *  2. The peer receives a remote Worker A with the following workerSpec:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         os=linux
	 *  3. This worker is commanded to work for the local consumer;
	 *  4. This worker updates its spec with the following new attributes:
	 *         servername=xmpp.anyServer.com
	 *         memuse=0.4
	 *  5. Check remote workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         os=linux
	 *         memuse=0.4
	 *  6. This worker changes its status to ALLOCATED_FOR_BROKER, and is allocated to the local consumer
	 *  7. This worker updates its spec with the following new attributes:
	 *         username=null
	 *         cpubal=0.6
	 *  8. Check remote workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         os=linux
	 *         memuse=0.4
	 *         cpubal=0.6
	 *  9. This worker updates its spec with the following new attributes:
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 10. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         os=linux
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 11. This worker updates its spec with the following new attributes:
	 *         os=null
	 * 12. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 13. This worker updates its spec with no new attributes - do not expect logging
	 * 14. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 15. The local consumer disposes the Worker A
	 * 16. This worker updates its spec with the following new attributes:
	 *         memuse=0.0
	 *17. Expect the Peer to ignore this message and log the debug message;
	 */
	@ReqTest(test="AT-107.2", reqs="REQ107")
	@Test public void test_AT_107_2_RemoteWorkerSpecChange() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		String brokerPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//set a mock log and timer
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
	
		//Client login and request 1 worker after ds recovery - expect OG peer to query ds
	    DeploymentID lwpcOID = req_108_Util.login(component, user, brokerPubKey);
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "os = linux", 1, 0, 0);
	    
	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider
        WorkerSpecification remoteWorkerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", "xmpp.remoteSite.net");
        remoteWorkerSpecA.putAttribute("os", "linux");
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", "rwpServer", 
				"rwpPublicKey", remoteWorkerSpecA);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID remoteWorkerAOID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpecA, "rworkerAPK", 
				brokerPubKey, future1).getDeploymentID();
		
		//Remote Worker A updates spec
		WorkerSpecification updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("servername", "xmpp.anyServer.com");
		updateWorkerSpec.putAttribute("memuse", "0.4");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerSpecA.putAttribute("memuse", "0.4");
		RemoteWorkerInfo remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);

		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		TestStub remoteWorker = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), remoteWorkerAOID, remoteWorkerSpecA, requestSpec1);
		
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("username", null);
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.6");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerSpecA.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.6");
		remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);
		
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("memuse", "0.9");
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.2");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerSpecA.putAttribute("memuse", "0.9");
		remoteWorkerSpecA.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.2");
		remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);
		
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("os", null);
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerSpecA.removeAttribute("os");

		remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);
		
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);
		
		//Consumer disposes the Remote Worker A
		WorkerAllocation remoteWorkerAllocation = new WorkerAllocation(remoteWorkerAOID);
		remoteWorkerAllocation.addLoserConsumer(lwpcOID).addLoserRequestSpec(requestSpec1);
		
	    req_015_Util.localDisposeRemoteWorker(component, remoteWorker, 
	    		remoteWorkerAllocation, rwp, rwpID, true);
	    
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("memuse", "0.0");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, true);
	}
	
	/**
	 * This test contains the following steps:
	 *   1. The peer sets the Worker A, B, and C, with the following workerSpec:
	 *          WORKER A:
	 *          username=workerA
	 *          servername=xmpp.ourgrid.org
	 *          mem=128
	 *          WORKER B:
	 *          username=workerB
	 *          servername=xmpp.ourgrid.org
	 *          mem=128
	 *          WORKER C:
	 *          username=workerC
	 *          servername=xmpp.ourgrid.org
	 *          mem=512
	 *   2. The workers recover and then change their status to IDLE;
	 *   3. The worker B updates its spec with the following new attributes:
	 *          mem=256
	 *   4. A remote consumer requests 2 workers with the following requirement "mem >= 256";
	 *          The workers B and C are commanded to work for that Peer;
	 *   5. The workers B and C changes their status to ALLOCATED_FOR_PEER and are delivered to the remote consumer;
	 *   6. The worker A updates its spec with the following new attributes:
	 *          mem=512
	 *   7. The worker B updates its spec with the following new attributes:
	 *          mem=128
	 *   8. The worker C updates its spec with the following new attributes:
	 *          mem=128
	 *   9. A local consumer requests 1 worker with the following requirement "mem > 128";
	 *          The worker A is commanded to work for that Broker;
	 *  10. The worker A changes its status to ALLOCATED_FOR_BROKER and are delivered to the local consumer;
	 *  11. The worker B updates its spec with the following new attributes:
	 *          brams=yes
	 *  12. A local consumer requests 1 worker with the following requirement "brams = yes";
	 *          The worker B is commanded to work for that Broker;
	 */
	@ReqTest(test="AT-107.3", reqs="REQ107")
	@Test public void test_AT_107_3_LocalAndRemoteWorkerSpecChangeWithRedistribution() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"107_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Workers A, B and C login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", "xmpp.ourgrid.org");
		workerSpecA.putAttribute("mem", "128");
		
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB", "xmpp.ourgrid.org");
		workerSpecB.putAttribute("mem", "128");
		
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("workerC", "xmpp.ourgrid.org");
		workerSpecC.putAttribute("mem", "512");
		
		
		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);

		//Workers change status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		//Worker B updates spec
		WorkerSpecification updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("mem", "256");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerBDeploymentID, false);
		workerSpecB.putAttribute("mem", "256");
	
		//The remote client requests two workers
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);
		
		int request1ID = 1;
		RequestSpecification requestSpec1 = new RequestSpecification(0, createJobSpec("label"), request1ID, "mem >= 256", 2, 0, 0);
		
		WorkerAllocation workerAllocationB = new WorkerAllocation(workerBDeploymentID);
		WorkerAllocation workerAllocationC = new WorkerAllocation(workerCDeploymentID);
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1, 0,
				workerAllocationB, workerAllocationC);
		
		//Change worker B and C status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerBDeploymentID, workerSpecB, remoteClientOID);
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerCDeploymentID, workerSpecC, remoteClientOID);
	
		//Worker A updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("mem", "512");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerADeploymentID, false);
		
		//Worker B updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("mem", "128");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerBDeploymentID, false);
		
		//Worker C updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("mem", "128");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerCDeploymentID, false);
		
		//Local client login and request 1 worker
	    String brokerPubKey = "publicKeyA";
	    
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    DeploymentID lwpcOID = req_108_Util.login(component, user, brokerPubKey);
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "mem > 128", 1, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec2, allocationWorkerA);
	    
	    //Change worker A status to ALLOCATED FOR BROKER
	    workerSpecA.putAttribute("mem", "512");
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerADeploymentID, workerSpecA, requestSpec2);
		
	    //Worker B updates spec
		updateWorkerSpec = new WorkerSpecification();
		updateWorkerSpec.putAttribute("brams", "yes");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerBDeploymentID, false);
		
		//Local client request 1 worker
	    RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, "brams=yes", 1, 0, 0);
	    WorkerAllocation allocationWorkerB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(remoteClientOID);;
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec3, allocationWorkerB);
	}

	/**
	 * This test contains the following steps:
	 *  1. One local client request one Worker - There is no available Worker, so the request is forwarded to the community
	 *  2. The peer receives a remote Worker A with the following workerSpec:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         os=linux
	 *  3. This worker is commanded to work for the local consumer;
	 *  4. This worker updates its spec with the following new attributes:
	 *         servername=xmpp.anyServer.com
	 *         memuse=0.4
	 *  5. Check remote workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         os=linux
	 *         memuse=0.4
	 *  6. This worker changes its status to ALLOCATED_FOR_BROKER, and is allocated to the local consumer
	 *  7. This worker updates its spec with the following new attributes:
	 *         username=null
	 *         cpubal=0.6
	 *  8. Check remote workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         os=linux
	 *         memuse=0.4
	 *         cpubal=0.6
	 *  9. This worker updates its spec with the following new attributes:
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 10. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         os=linux
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 11. This worker updates its spec with the following new attributes:
	 *         os=null
	 * 12. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 13. This worker updates its spec with no new attributes - do not expect logging
	 * 14. Check local workers. Verify if the Worker A has the following attributes:
	 *         username=workerA
	 *         servername=xmpp.remotesite.net
	 *         memuse=0.9
	 *         cpubal=0.2
	 * 15. The local consumer disposes the Worker A
	 * 16. This worker updates its spec with the following new attributes:
	 *         memuse=0.0
	 *17. Expect the Peer to ignore this message and log the debug message;
	 */
	@ReqTest(test="AT-107.2", reqs="REQ107")
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_107_2_RemoteWorkerSpecChangeWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		String brokerPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//set a mock log and timer
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		ScheduledExecutorService timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
	
		//Client login and request 1 worker after ds recovery - expect OG peer to query ds
	    DeploymentID lwpcOID = req_108_Util.login(component, user, brokerPubKey);
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "[Requirements=other.OS == \"linux\";Rank=0]", 1, 0, 0);
	    
	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider
	    WorkerSpecification remoteWorkerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.remoteSite.net", null, "linux");
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", "rwpServer", 
				"rwpPublicKey", remoteWorkerSpecA);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID remoteWorkerAOID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpecA, "rworkerAPK", 
				brokerPubKey, future1).getDeploymentID();
		
		//Remote Worker A updates spec
		WorkerSpecification updateWorkerSpec = new WorkerSpecification(remoteWorkerSpecA.getExpression());
		updateWorkerSpec.putAttribute( OurGridSpecificationConstants.SERVERNAME, "xmpp.anyServer.com");
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.FREE_MAIN_MEMORY, "0.4");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerSpecA.putAttribute(WorkerSpecificationConstants.FREE_MAIN_MEMORY, "0.4");
		RemoteWorkerInfo remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);
	
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		TestStub remoteWorker = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), remoteWorkerAOID, remoteWorkerSpecA, requestSpec1);
		
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification(remoteWorkerSpecA.getExpression());
		updateWorkerSpec.removeAttribute( OurGridSpecificationConstants.USERNAME );
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.6");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerSpecA.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.6");
		remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);
		
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification(remoteWorkerSpecA.getExpression());
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.FREE_MAIN_MEMORY, "0.9");
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.2");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerSpecA.putAttribute(WorkerSpecificationConstants.FREE_MAIN_MEMORY, "0.9");
		remoteWorkerSpecA.putAttribute(WorkerSpecificationConstants.CPU_LOAD, "0.2");
		remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);
		
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification(remoteWorkerSpecA.getExpression());
		updateWorkerSpec.removeAttribute(WorkerSpecificationConstants.OS);
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerSpecA.removeAttribute(WorkerSpecificationConstants.OS);
	
		remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);
		
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification(remoteWorkerSpecA.getExpression());
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, false);
		
		//Verify remote workers status
		remoteWorkerInfoA = new RemoteWorkerInfo(remoteWorkerSpecA, rwpID.getServiceID().toString(), lwpcOID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(remoteWorkerInfoA);
		
		//Consumer disposes the Remote Worker A
		WorkerAllocation remoteWorkerAllocation = new WorkerAllocation(remoteWorkerAOID);
		remoteWorkerAllocation.addLoserConsumer(lwpcOID).addLoserRequestSpec(requestSpec1);
		
	    req_015_Util.localDisposeRemoteWorker(component, remoteWorker, 
	    		remoteWorkerAllocation, rwp, rwpID, true);
	    
		//Remote Worker A updates spec
		updateWorkerSpec = new WorkerSpecification(remoteWorkerSpecA.getExpression());
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.FREE_MAIN_MEMORY, "0.0");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, remoteWorkerAOID, true);
	}

	/**
	 * This test contains the following steps:
	 *   1. The peer sets the Worker A, B, and C, with the following workerSpec:
	 *          WORKER A:
	 *          username=workerA
	 *          servername=xmpp.ourgrid.org
	 *          mem=128
	 *          WORKER B:
	 *          username=workerB
	 *          servername=xmpp.ourgrid.org
	 *          mem=128
	 *          WORKER C:
	 *          username=workerC
	 *          servername=xmpp.ourgrid.org
	 *          mem=512
	 *   2. The workers recover and then change their status to IDLE;
	 *   3. The worker B updates its spec with the following new attributes:
	 *          mem=256
	 *   4. A remote consumer requests 2 workers with the following requirement "mem >= 256";
	 *          The workers B and C are commanded to work for that Peer;
	 *   5. The workers B and C changes their status to ALLOCATED_FOR_PEER and are delivered to the remote consumer;
	 *   6. The worker A updates its spec with the following new attributes:
	 *          mem=512
	 *   7. The worker B updates its spec with the following new attributes:
	 *          mem=128
	 *   8. The worker C updates its spec with the following new attributes:
	 *          mem=128
	 *   9. A local consumer requests 1 worker with the following requirement "mem > 128";
	 *          The worker A is commanded to work for that Broker;
	 *  10. The worker A changes its status to ALLOCATED_FOR_BROKER and are delivered to the local consumer;
	 *  11. The worker B updates its spec with the following new attributes:
	 *          brams=yes
	 *  12. A local consumer requests 1 worker with the following requirement "brams = yes";
	 *          The worker B is commanded to work for that Broker;
	 */
	@ReqTest(test="AT-107.3", reqs="REQ107")
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_107_3_LocalAndRemoteWorkerSpecChangeWithRedistributionWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"107_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Workers A, B and C login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.ourgrid.org", 128, null);
		
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB", "xmpp.ourgrid.org", 128, null);
		
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("workerC", "xmpp.ourgrid.org", 512, null);
		
		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);
		
		//Workers change status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		//Worker B updates spec
		WorkerSpecification updateWorkerSpec = new WorkerSpecification(workerSpecB.getExpression());
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, "256");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerBDeploymentID, false);
		workerSpecB.putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, "256");
	
		//The remote client requests two workers
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);
		
		int request1ID = 1;
		RequestSpecification requestSpec1 = new RequestSpecification(0, createJobSpec("label"), request1ID, PeerAcceptanceTestCase.buildRequirements(">=", 256, null, null), 2, 0, 0);
		
		WorkerAllocation workerAllocationB = new WorkerAllocation(workerBDeploymentID);
		WorkerAllocation workerAllocationC = new WorkerAllocation(workerCDeploymentID);
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1, 0,
				workerAllocationB, workerAllocationC);
		
		//Change worker B and C status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerBDeploymentID, workerSpecB, remoteClientOID);
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerCDeploymentID, workerSpecC, remoteClientOID);
	
		//Worker A updates spec
		updateWorkerSpec = new WorkerSpecification(workerSpecA.getExpression());
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, "512");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerADeploymentID, false);
		
		//Worker B updates spec
		updateWorkerSpec = new WorkerSpecification(workerSpecB.getExpression());
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, "128");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerBDeploymentID, false);
		
		//Worker C updates spec
		updateWorkerSpec = new WorkerSpecification(workerSpecC.getExpression());
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, "128");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerCDeploymentID, false);
		
		//Local client login and request 1 worker
	    String brokerPubKey = "publicKeyA";
	    
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    DeploymentID lwpcOID = req_108_Util.login(component, user, brokerPubKey);
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(">", 128, null, null), 1, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec2, allocationWorkerA);
	    
	    //Change worker A status to ALLOCATED FOR BROKER
	    workerSpecA.putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, "512");
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerADeploymentID, workerSpecA, requestSpec2);
		
	    //Worker B updates spec
		updateWorkerSpec = new WorkerSpecification(workerSpecB.getExpression());
		updateWorkerSpec.putAttribute("brams", "[name=\"brams\";];");
		updateWorkerSpec.putAttribute(WorkerSpecificationConstants.SOFTWARE, "{brams}");
		
		req_107_Util.updateWorkerSpec(component, updateWorkerSpec, workerBDeploymentID, false);
		
		//Local client request 1 worker
	    RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 3, buildRequirements( "member(\"brams\", other.software.name)" ), 1, 0, 0);
	    WorkerAllocation allocationWorkerB = new WorkerAllocation(workerBDeploymentID).addLoserConsumer(remoteClientOID);;
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec3, allocationWorkerB);
	}
}
