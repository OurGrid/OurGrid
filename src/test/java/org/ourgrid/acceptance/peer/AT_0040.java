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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0040 extends PeerAcceptanceTestCase {

	private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());
	
	/**
	 * This test contains the following steps:
	 *  1. A local consumer requests 1 worker - The worker is commanded to work for that consumer
	 *  2. The local consumer pauses the request
	 *  3. The worker changes its status to ALLOCATED_FOR_BROKER - Expect the Worker to be delivered to the local consumer that paused the request
     */
 	@ReqTest(test="AT-0040", reqs="REQ117")
	@Test public void test_AT_0040_PauseRequestWithWorkerNotDelivered() throws Exception {
 		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user01", "server011", "011011");
		
		//Start the peer
		component = req_010_Util.startPeer();

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);

		//Local consumer login and requests one worker with the requestID 1
		String brokerAPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerAPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerAPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    long request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADeploymentID);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, workerAllocationA);
	    
	    //Local consumer pauses the request
	    req_117_Util.pauseRequest(component, lwpcOID, request1ID, null);
	    
	    //Worker A sends a status changed message - Expect the peer to deliver the Worker to L1
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID,
				workerADeploymentID, workerSpecA, requestSpec1);
 	}
 	
 	@Category(JDLCompliantTest.class)
 	@Test public void test_AT_0040_PauseRequestWithWorkerNotDeliveredwithJDL() throws Exception {
 		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user01", "server011", "011011");
		
		//Start the peer
		component = req_010_Util.startPeer();

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);

		//Local consumer login and requests one worker with the requestID 1
		String brokerAPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerAPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(component, user1, brokerAPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
	    long request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements(null), 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADeploymentID);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, workerAllocationA);
	    
	    //Local consumer pauses the request
	    req_117_Util.pauseRequest(component, lwpcOID, request1ID, null);
	    
	    //Worker A sends a status changed message - Expect the peer to deliver the Worker to L1
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID,
				workerADeploymentID, workerSpecA, requestSpec1);
 	}
}