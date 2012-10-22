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
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0024 extends PeerAcceptanceTestCase {

    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
	private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
	private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
	private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
	private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());

	@Before
	public void setUp() throws Exception{
		super.setUp();
		component = req_010_Util.startPeer();
	}
	
	/**
	* This test contains the following steps:
	* 1. A local consumer requests one worker and obtain a local one;
   	* 2. The local consumer pauses the request;
   	* 3. Other local consumer requests one worker and do not obtain it;
   	* 4. The local consumer disposes the local worker - expect the peer to allocate the worker for the second local consumer.
    */
	@ReqTest(test="AT-0024", reqs="15, 25, 117")
	@Test public void test_AT_0024_BrokerDisposeLocalWorkerAllocatedToAnotherBroker() throws Exception {
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Logins
		String broker1PubKey = "publicKeyA";
		String broker2PubKey = "publicKeyB";
		
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
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		//Request a worker for the first user
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec1, allocationWorkerA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));
	    
	    //Change worker status to ALLOCATED FOR BROKER
		Worker worker = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerADeploymentID, workerSpecA, requestSpec1).getObject();
		
		//Pause the request
		req_117_Util.pauseRequest(component, lwpc1OID, request1ID, null);
		
		//Request a worker for the second user
	    int request2ID = 2;
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, "", 1, 0, 0);
	    ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc2OID.getServiceID()));		
	    
	    //The user disposes the worker after pausing the request - expect the peer to allocate the worker for the second local consumer
		WorkerAllocation allocation = new WorkerAllocation(workerADeploymentID);
		allocation.addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpc1OID).addWinnerConsumer(lwpc2OID);
		req_015_Util.localConsumerDisposesLocalWorker(component, worker, allocation, future2);
		
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADeploymentID.getServiceID()));
		
		//Verify if the worker A was marked as IN_USE
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2OID.getServiceID().toString());
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0024_BrokerDisposeLocalWorkerAllocatedToAnotherBrokerWithJDL() throws Exception {
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user011_2", "server011", "011011");
		
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Logins
		String broker1PubKey = "publicKeyA";
		String broker2PubKey = "publicKeyB";
		
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
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		//Request a worker for the first user
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 1, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec1, allocationWorkerA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc1OID.getServiceID()));
	    
	    //Change worker status to ALLOCATED FOR BROKER
		Worker worker = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerADeploymentID, workerSpecA, requestSpec1).getObject();
		
		//Pause the request
		req_117_Util.pauseRequest(component, lwpc1OID, request1ID, null);
		
		//Request a worker for the second user
	    int request2ID = 2;
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, buildRequirements(null), 1, 0, 0);
	    ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2);
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpc2OID.getServiceID()));		
	    
	    //The user disposes the worker after pausing the request - expect the peer to allocate the worker for the second local consumer
		WorkerAllocation allocation = new WorkerAllocation(workerADeploymentID);
		allocation.addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpc1OID).addWinnerConsumer(lwpc2OID);
		req_015_Util.localConsumerDisposesLocalWorker(component, worker, allocation, future2);
		
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADeploymentID.getServiceID()));
		
		//Verify if the worker A was marked as IN_USE
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc2OID.getServiceID().toString());
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
}