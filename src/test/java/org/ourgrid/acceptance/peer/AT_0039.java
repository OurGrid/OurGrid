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

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_014_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.acceptance.util.peer.Req_118_Util;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
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

public class AT_0039 extends PeerAcceptanceTestCase {
	//FIXME
    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_014_Util req_014_Util = new Req_014_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());
    private Req_118_Util req_118_Util = new Req_118_Util(getComponentContext());

    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
	/**
	 * This test contains the following steps:
  	 *  1. Start a Peer with 5 recovered workers
	 *   2. A remote consumer requests 1 worker with the RequestID "1", and obtains it
	 *   3. The same remote consumer requests 1 worker with the RequestID "1", and obtains it
	 *   4. Another remote consumer requests 1 worker with the RequestID "1", and obtains it
	 *   5. A local consumer requests 1 worker with the RequestID "1", and obtains it
	 *   6. The same local consumer requests 1 worker with the RequestID "1", expect the peer to:
	 *         1. Ignore the request
	 *         2. Log the warn message
	 *   7. Another local consumer requests 1 worker with the RequestID "1", expect the peer to:
	 *         1. Ignore the request
	 *         2. Log the warn message
	 *   8. The first local consumer pauses the request
	 *   9. The other local consumer requests 1 worker with the RequestID "1", expect the peer to:
	 *         1. Ignore the request
	 *         2. Log the warn message
	 *  10. The first local consumer resumes the request
	 *  11. The other local consumer requests 1 worker with the RequestID "1", expect the peer to:
	 *         1. Ignore the request
	 *         2. Log the warn message
	 *  12. A remote consumer requests 1 worker with the RequestID "1", and obtains it
	 *  13. The local consumer finishes the request
	 *  14. The other local consumer requests 1 worker with the RequestID "1", and obtains it
     */
 	@ReqTest(test="AT-0039", reqs="REQ011")
	@Test public void test_AT_0039_RequestWorkersWithRequestIDRepetead() throws Exception {
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user01", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user02", "server011", "011011");

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S1");
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createWorkerSpec("U5", "S1");
		
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		String workerDPublicKey = "workerDPublicKey";
		String workerEPublicKey = "workerEPublicKey";
		
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
		
		//Change workers  to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerEDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerDDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Remote consumer R1 requests one worker with the requestID 1
		DeploymentID remoteClient1OID = PeerAcceptanceUtil.createRemoteConsumerID("r1UserName", "server", "consumerPublicKey1");

		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, createJobSpec("label"), request1ID, "", 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADeploymentID);
		RemoteWorkerProviderClient remoteClient1 = req_011_Util.requestForRemoteClient(component, remoteClient1OID, requestSpec1,
				0, workerAllocationA);
		
		//Worker A sends a status changed message - Expect the peer to deliver the Worker to R1
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient1, workerADeploymentID, workerSpecA, remoteClient1OID);
		
		//Remote consumer R1 requests another worker with the requestID 1
		WorkerAllocation workerAllocationB = new WorkerAllocation(workerBDeploymentID);
		req_011_Util.requestForRemoteClient(component, remoteClient1OID, requestSpec1, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, workerAllocationB);
		
		//Worker B sends a status changed message - Expect the peer to deliver the Worker to R1
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient1, workerBDeploymentID, workerSpecB, remoteClient1OID);
		
		//Remote consumer R2 requests one worker with the requestID 1
		String remoteClient2PublicKey = "consumer2PublicKey";
		DeploymentID remoteClient2OID = new DeploymentID(new ContainerID("r2UserName", "server", PeerConstants.MODULE_NAME, remoteClient2PublicKey),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		WorkerAllocation workerAllocationC = new WorkerAllocation(workerCDeploymentID);
		RemoteWorkerProviderClient remoteClient2 = req_011_Util.requestForRemoteClient(component, remoteClient2OID, requestSpec1, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, workerAllocationC);
 	
		//Worker C sends a status changed message - Expect the peer to deliver the Worker to R2
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient2, workerCDeploymentID, workerSpecC, remoteClient2OID);
		
		//Login with two valid users
		String broker1PubKey = "publicKeyL1";
		String broker2PubKey = "publicKeyL2";
		
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
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		
		//Local consumer L1 requests one worker with the requestID 1
		WorkerAllocation workerAllocationD = new WorkerAllocation(workerDDeploymentID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, workerAllocationD);
		
		//Worker D sends a status changed message - Expect the peer to deliver the Worker to L1
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID,
				workerDDeploymentID, workerSpecD, requestSpec1);
		
		//Local consumer L1 requests one worker with the requestID 1
		component.setLogger(loggerMock);
		EasyMock.reset(loggerMock);
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc1OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Local consumer L2 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc2OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//L1 pauses the request
		req_117_Util.pauseRequest(component, lwpc1OID, request1ID, null);
		
		//Local consumer L1 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc1OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Local consumer L2 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc2OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//L1 resumes the request
		req_118_Util.resumeRequestWithNoReschedule(requestSpec1, lwpc1OID, component);
		
		//Local consumer L1 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc1OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Local consumer L2 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc2OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Remote consumer R1 requests one worker with the requestID 1
		WorkerAllocation workerAllocationE = new WorkerAllocation(workerEDeploymentID);
		
		req_011_Util.requestForRemoteClient(component, remoteClient1OID, requestSpec1, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, workerAllocationE);
		
		//Worker E sends a status changed message - Expect the peer to deliver the Worker to R1
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient1, workerEDeploymentID, workerSpecE, remoteClient1OID);
		
		//L1 finish the request 1
		req_014_Util.finishRequestWithLocalWorkers(component, lwp, lwpOD, broker1PubKey, lwpc1OID.getServiceID(), requestSpec1,
				AcceptanceTestUtil.createList(workerAllocationD));
		
		//Local consumer L2 requests one worker with the requestID 1
		req_011_Util.requestForRemoteClient(component, remoteClient2OID, requestSpec1, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, workerAllocationD);
 	}

	/**
	 * This test contains the following steps:
	 *  1. Start a Peer with 5 recovered workers
	 *   2. A remote consumer requests 1 worker with the RequestID "1", and obtains it
	 *   3. The same remote consumer requests 1 worker with the RequestID "1", and obtains it
	 *   4. Another remote consumer requests 1 worker with the RequestID "1", and obtains it
	 *   5. A local consumer requests 1 worker with the RequestID "1", and obtains it
	 *   6. The same local consumer requests 1 worker with the RequestID "1", expect the peer to:
	 *         1. Ignore the request
	 *         2. Log the warn message
	 *   7. Another local consumer requests 1 worker with the RequestID "1", expect the peer to:
	 *         1. Ignore the request
	 *         2. Log the warn message
	 *   8. The first local consumer pauses the request
	 *   9. The other local consumer requests 1 worker with the RequestID "1", expect the peer to:
	 *         1. Ignore the request
	 *         2. Log the warn message
	 *  10. The first local consumer resumes the request
	 *  11. The other local consumer requests 1 worker with the RequestID "1", expect the peer to:
	 *         1. Ignore the request
	 *         2. Log the warn message
	 *  12. A remote consumer requests 1 worker with the RequestID "1", and obtains it
	 *  13. The local consumer finishes the request
	 *  14. The other local consumer requests 1 worker with the RequestID "1", and obtains it
	 */
	@ReqTest(test="AT-0039", reqs="REQ011")
	@Test public void test_AT_0039_RequestWorkersWithRequestIDRepeteadWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user01", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user02", "server011", "011011");
	
		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S1", null, null);
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createClassAdWorkerSpec("U5", "S1", null, null);
		
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		String workerDPublicKey = "workerDPublicKey";
		String workerEPublicKey = "workerEPublicKey";
		
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
		
		//Change workers  to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerEDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerDDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Remote consumer R1 requests one worker with the requestID 1
		DeploymentID remoteClient1OID = PeerAcceptanceUtil.createRemoteConsumerID("r1UserName", "server", "consumerPublicKey1");
	
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, createJobSpec("label"), request1ID, buildRequirements( null ), 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADeploymentID);
		RemoteWorkerProviderClient remoteClient1 = req_011_Util.requestForRemoteClient(component, remoteClient1OID, requestSpec1,
				0, workerAllocationA);
		
		//Worker A sends a status changed message - Expect the peer to deliver the Worker to R1
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient1, workerADeploymentID, workerSpecA, remoteClient1OID);
		
		//Remote consumer R1 requests another worker with the requestID 1
		WorkerAllocation workerAllocationB = new WorkerAllocation(workerBDeploymentID);
		req_011_Util.requestForRemoteClient(component, remoteClient1OID, requestSpec1, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, workerAllocationB);
		
		//Worker B sends a status changed message - Expect the peer to deliver the Worker to R1
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient1, workerBDeploymentID, workerSpecB, remoteClient1OID);
		
		//Remote consumer R2 requests one worker with the requestID 1
		String remoteClient2PublicKey = "consumer2PublicKey";
		DeploymentID remoteClient2OID = new DeploymentID(new ContainerID("r2UserName", "server", PeerConstants.MODULE_NAME, remoteClient2PublicKey),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		WorkerAllocation workerAllocationC = new WorkerAllocation(workerCDeploymentID);
		RemoteWorkerProviderClient remoteClient2 = req_011_Util.requestForRemoteClient(component, remoteClient2OID, requestSpec1, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, workerAllocationC);
	
		//Worker C sends a status changed message - Expect the peer to deliver the Worker to R2
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient2, workerCDeploymentID, workerSpecC, remoteClient2OID);
		
		//Login with two valid users
		String broker1PubKey = "publicKeyL1";
		String broker2PubKey = "publicKeyL2";
		
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
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		
		//Local consumer L1 requests one worker with the requestID 1
		WorkerAllocation workerAllocationD = new WorkerAllocation(workerDDeploymentID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1, workerAllocationD);
		
		//Worker D sends a status changed message - Expect the peer to deliver the Worker to L1
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID,
				workerDDeploymentID, workerSpecD, requestSpec1);
		
		//Local consumer L1 requests one worker with the requestID 1
		component.setLogger(loggerMock);
		EasyMock.reset(loggerMock);
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc1OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Local consumer L2 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc2OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//L1 pauses the request
		req_117_Util.pauseRequest(component, lwpc1OID, request1ID, null);
		
		//Local consumer L1 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc1OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Local consumer L2 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc2OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//L1 resumes the request
		req_118_Util.resumeRequestWithNoReschedule(requestSpec1, lwpc1OID, component);
		
		//Local consumer L1 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc1OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Local consumer L2 requests one worker with the requestID 1
		loggerMock.warn("Request " + request1ID + ": New request ignored because this request ID " +
				"number is already being used. Local consumer ID: [" + lwpc2OID.getServiceID() + "]");
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2OID);
		lwp.requestWorkers(requestSpec1);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Remote consumer R1 requests one worker with the requestID 1
		WorkerAllocation workerAllocationE = new WorkerAllocation(workerEDeploymentID);
		
		req_011_Util.requestForRemoteClient(component, remoteClient1OID, requestSpec1, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, workerAllocationE);
		
		//Worker E sends a status changed message - Expect the peer to deliver the Worker to R1
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient1, workerEDeploymentID, workerSpecE, remoteClient1OID);
		
		//L1 finish the request 1
		req_014_Util.finishRequestWithLocalWorkers(component, lwp, lwpOD, broker1PubKey, lwpc1OID.getServiceID(), requestSpec1,
				AcceptanceTestUtil.createList(workerAllocationD));
		
		//Local consumer L2 requests one worker with the requestID 1
		req_011_Util.requestForRemoteClient(component, remoteClient2OID, requestSpec1, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, workerAllocationD);
	}
}