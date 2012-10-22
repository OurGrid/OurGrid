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
import org.ourgrid.acceptance.util.peer.Req_014_Util;
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_016_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_116_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.acceptance.util.peer.Req_118_Util;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
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

public class AT_0037 extends PeerAcceptanceTestCase {

	private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_014_Util req_014_Util = new Req_014_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_016_Util req_016_Util = new Req_016_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_116_Util req_116_Util = new Req_116_Util(getComponentContext());
    private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());
    private Req_118_Util req_118_Util = new Req_118_Util(getComponentContext());

    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
	
	/**
	* Tests the Request State Machine, performing all possible actions that schedule and cancel request repetition. Test's steps (and request state):
	   1. A local consumer requests 10 workers - the peer have not idle workers, so pass the request for community and schedule the request for repetition (necessity = 10);
	   2. The local worker 1 becomes idle and is allocated for the local consumer (necessity = 9);
	   3. The peer receives a remote worker R1, which is allocated for the local consumer (necessity = 8);
	   4. The local consumer updates the request needing 4 workers (necessity = 2);
	   5. The local consumer set the remote worker R1 as unwanted - expect the peer to dispose the remote worker R1 (necessity = 3);
	   6. The local worker 2 becomes idle and is allocated for the local consumer (necessity = 2);
	   7. The local consumer updates the request needing 2 workers - expect the peer to cancel the schedule repetition (necessity = 0);
	   8. The local consumer updates the request needing 1 worker (necessity = -1);
	   9. The local consumer set the worker 1 as unwanted - expect the peer to command the worker 1 to stop working (necessity = 0);
	  10. The local consumer updates the request needing 3 workers - expect the peer to schedule the request repetition (necessity = 2);
	  11. The local worker 3 becomes idle and is allocated for the local consumer (necessity = 1);
	  12. The local worker 4 becomes idle and is allocated for the local consumer - expect the peer to cancel the schedule repetition (necessity = 0);
	  13. The local consumer set the worker 2 as unwanted - expect the peer to command the worker 2 to stop working and to schedule the request repetition (necessity = 1);
	  14. The peer receives a remote worker R2, which is allocated for the local consumer - expect the peer to cancel the schedule repetition (necessity = 0);
	  15. The local consumer set the remote worker R2 as unwanted - expect the peer to dispose the remote worker R1 and to schedule the request repetition (necessity = 1);
	  16. The local consumer pauses the request - expect the peer to cancel the schedule repetition (necessity = 1, paused);
	  17. The local consumer updates the request needing 6 workers (necessity = 4, paused);
	  18. The local consumer disposes the worker 3 - expect to command the worker 3 to stop working (necessity = 5, paused);
	  19. The local consumer set the worker 4 as unwanted - expect the peer to command the worker 4 to stop working (necessity = 6, paused);
	  20. The local consumer resumes the request - expect the peer to schedule the request repetition (necessity = 6);
	  21. The local worker 5 becomes idle and is allocated for the local consumer (necessity = 5);
	  22. The peer receives a remote worker R3, which is allocated for the local consumer (necessity = 4);
	  23. The local worker 6 becomes idle and is allocated for the local consumer (necessity = 3);
	  24. The peer receives a remote worker R4, which is allocated for the local consumer (necessity = 2);
	  25. The local worker 7 becomes idle and is allocated for the local consumer (necessity = 1);
	  26. The local consumer pauses the request - expect the peer to cancel the schedule repetition (necessity = 1, paused);
	  27. The local consumer updates the request needing 5 workers (necessity = 0, paused);
	  28. The local consumer updates the request needing 3 workers (necessity = -2, paused);
	  29. The local consumer disposes the worker 5 - expect to command the worker 5 to stop working (necessity = -1, paused);
	  30. The local consumer set the remote worker R3 as unwanted - expect the peer to dispose the remote worker R3 (necessity = 0, paused);
	  31. The local consumer updates the request needing 4 workers (necessity = 1, paused);
	  32. The local consumer updates the request needing 3 workers (necessity = 0, paused);
	  33. The local consumer disposes the remote worker R4 - expect to dispose the remote worker R4 (necessity = 1, paused);
	  34. The local consumer updates the request needing 2 workers (necessity = 0, paused);
	  35. The local consumer set the worker 6 as unwanted - expect the peer to command the worker 6 to stop working (necessity = 1, paused);
	  36. The local consumer updates the request needing 1 worker (necessity = 0, paused);
	  37. The local consumer resumes the request (necessity = 0);
	  38. The local consumer pauses the request (necessity = 0, paused);
	  39. The local consumer finishes the request - expect the peer to command the worker 7 to stop working.
    */
	@ReqTest(test="AT-0037", reqs="REQ014, REQ116, REQ117, REQ118")
	@Test public void test_AT_0037_CancellingAndRescheduleRequest() throws Exception {
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");

		// Workers login
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("U1","S1");
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("U2","S1");
		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createWorkerSpec("U3","S1");
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createWorkerSpec("U4","S1");
		WorkerSpecification workerSpec5 = workerAcceptanceUtil.createWorkerSpec("U5","S1");
		WorkerSpecification workerSpec6 = workerAcceptanceUtil.createWorkerSpec("U6","S1");
		WorkerSpecification workerSpec7 = workerAcceptanceUtil.createWorkerSpec("U7","S1");
		
		String worker1PublicKey = "worker1PublicKey";
		String worker2PublicKey = "worker2PublicKey";
		String worker3PublicKey = "worker3PublicKey";
		String worker4PublicKey = "worker4PublicKey";
		String worker5PublicKey = "worker5PublicKey";
		String worker6PublicKey = "worker6PublicKey";
		String worker7PublicKey = "worker7PublicKey";
		
		DeploymentID w1ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec1, worker1PublicKey);
		req_010_Util.workerLogin(component, workerSpec1, w1ID);

		DeploymentID w2ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec2, worker2PublicKey);
		req_010_Util.workerLogin(component, workerSpec2, w2ID);

		DeploymentID w3ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec3, worker3PublicKey);
		req_010_Util.workerLogin(component, workerSpec3, w3ID);

		DeploymentID w4ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec4, worker4PublicKey);
		req_010_Util.workerLogin(component, workerSpec4, w4ID);

		DeploymentID w5ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec5, worker5PublicKey);
		req_010_Util.workerLogin(component, workerSpec5, w5ID);

		DeploymentID w6ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec6, worker6PublicKey);
		req_010_Util.workerLogin(component, workerSpec6, w6ID);

		DeploymentID w7ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec7, worker7PublicKey);
		req_010_Util.workerLogin(component, workerSpec7, w7ID);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
	    //Client login and request 10 workers
		String broker1PubKey = "publicKey1";
		
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
		
	    long request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 10, 0, 0);
	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1);
	    
	    //Change worker 1 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w1ID, lwpc1OID, component);
	    
		//Change worker status to ALLOCATED FOR BROKER
		TestStub workerStub = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w1ID, workerSpec1, requestSpec1);
		
		Worker worker1 = (Worker) workerStub.getObject();
		
	    //GIS client receive a remote worker provider
        WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("rU1", "rS1");
        
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker 1
		DeploymentID rwm1OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "rworker1PK", broker1PubKey).getDeploymentID();
		
		//Change remote worker 1 status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpc1OID, AcceptanceTestUtil.getBoundObject(lwpc1OID));
		
		Worker rw1 = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm1OID, workerSpecR1, requestSpec1).getObject();
		
		//Update the request: need 4 workers
		RequestSpecification newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 4, 0, 0);
		ScheduledFuture<?> future2 = req_116_Util.updateRequest(component, newRequestSpec1, true, future1, lwpc1OID);

		//The consumer set the remote worker 1 as unwanted - expect the peer to dispose the remote worker
		WorkerAllocation allocationR1 = new WorkerAllocation(rwm1OID);
		allocationR1.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		req_016_Util.unwantedWorker(component, rw1, allocationR1, requestSpec1, lwpc1OID, false, null, rwp, rwpID);
		
	    //Change worker 2 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w2ID, lwpc1OID, component);
	    
		//Change worker 2 status to ALLOCATED FOR BROKER
		TestStub workerStub2 = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w2ID, workerSpec2, newRequestSpec1);
		
		Worker worker2 = (Worker) workerStub2.getObject();
		
		//Update the request: need 2 workers
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		req_116_Util.updateRequest(component, newRequestSpec1, false, future2, lwpc1OID);
		
		//Update the request: need 1 worker
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
		
		//The consumer set the worker 1 as unwanted - expect to command the worker to stop working
		WorkerAllocation allocation1 = new WorkerAllocation(w1ID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub.getDeploymentID(), workerStub.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, worker1, allocation1, requestSpec1, lwpc1OID, false, null, null, null);
		
		//Update the request: need 3 workers
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 3, 0, 0);
		ScheduledFuture<?> future3 = req_116_Util.updateRequest(component, newRequestSpec1, true, lwpc1OID);
	
	    //Change worker 3 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w3ID, lwpc1OID, component);
	    
		//Change worker 3 status to ALLOCATED FOR BROKER
	    TestStub workerStub3 = 
	    	req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w3ID, workerSpec3, newRequestSpec1);
	    
	    Worker worker3 = (Worker) workerStub3.getObject();
		
	    //Change worker 4 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w4ID, lwpc1OID, future3, component);
	    
		//Change worker 4 status to ALLOCATED FOR BROKER
		TestStub workerStub4 = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w4ID, workerSpec4, newRequestSpec1);
		
		Worker worker4 = (Worker) workerStub4.getObject();
		
		//The consumer set the worker 2 as unwanted - expect to command the worker to stop working
		WorkerAllocation allocation2 = new WorkerAllocation(w2ID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub2.getDeploymentID(), workerStub2.getObject(),
				Worker.class);
		ScheduledFuture<?> future4 = req_016_Util.unwantedWorker(component, worker2, allocation2, 
				newRequestSpec1, lwpc1OID, true, null, rwp, rwpID);
		
		//Remote worker provider client receive a remote worker 2
		
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createWorkerSpec("rU2", "rS1");
		DeploymentID rwm2OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR2, "rworker2PK", broker1PubKey, future4).getDeploymentID();
		
		//Change remote worker 2 status to ALLOCATED FOR BROKER
		Worker rw2 = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(),
				rwm2OID, workerSpecR2, newRequestSpec1).getObject();

		//The consumer set the remote worker 2 as unwanted - expect the peer to dispose the remote worker
		WorkerAllocation allocationR2 = new WorkerAllocation(rwm2OID);
		allocationR2.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		ScheduledFuture<?> future5 = req_016_Util.unwantedWorker(component, rw2, allocationR2, 
				newRequestSpec1, lwpc1OID, true, null, rwp, rwpID);
		
		//Pause the request
		req_117_Util.pauseRequest(component, lwpc1OID, request1ID, future5);
		
		//Update the request: need 6 workers
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 6, 0, 0);
		req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
		
		//The user disposes the worker 3 - expect to command the worker to stop working
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		WorkerAllocation allocation3 = new WorkerAllocation(w3ID).addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpc1OID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub3.getDeploymentID(), workerStub3.getObject(),
				Worker.class);
		req_015_Util.localConsumerDisposesLocalWorker(component, worker3, allocation3);
		
		//The consumer set the worker 4 as unwanted - expect to command the worker to stop working
		WorkerAllocation allocation4 = new WorkerAllocation(w4ID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub4.getDeploymentID(), workerStub4.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, worker4, allocation4, newRequestSpec1, lwpc1OID, false, null, null, null);
		
		//Resume the request
		ScheduledFuture<?> future6 = req_118_Util.resumeRequest(newRequestSpec1, lwpc1OID, component);
		
		//Change worker 5 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w5ID, lwpc1OID, component);
	    
		//Change worker 5 status to ALLOCATED FOR BROKER
	    TestStub workerStub5 = 
	    	req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w5ID, workerSpec5, newRequestSpec1);
	    
	    Worker worker5 = (Worker) workerStub5.getObject();
		
		//Remote worker provider client receive a remote worker 3
	    EasyMock.reset(rwp);
	    EasyMock.replay(rwp);
	    
	    WorkerSpecification workerSpecR3 = workerAcceptanceUtil.createWorkerSpec("rU3", "rS1");
		DeploymentID rwm3OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR3, "rworker3PK", broker1PubKey).getDeploymentID();
		
		//Change remote worker 3 status to ALLOCATED FOR BROKER
		TestStub rwStub3 = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm3OID, workerSpecR3, newRequestSpec1);
		
		Worker rw3 = (Worker) rwStub3.getObject();
		
		//Change worker 6 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w6ID, lwpc1OID, component);
	    
		//Change worker 6 status to ALLOCATED FOR BROKER
	    TestStub workerStub6 = 
	    	req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w6ID, workerSpec6, newRequestSpec1);
	    
	    Worker worker6 = (Worker) workerStub6.getObject();

	    //Remote worker provider client receive a remote worker 4	   
	    WorkerSpecification workerSpecR4 = workerAcceptanceUtil.createWorkerSpec("rU4", "rS1");
	    DeploymentID rwm4OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR4, "rworker4PK", broker1PubKey).getDeploymentID();
		
		//Change remote worker 4 status to ALLOCATED FOR BROKER
		TestStub rwStub4 = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm4OID, workerSpecR4, newRequestSpec1);
		
//		Worker rw4 = (Worker) rwStub4.getObject();
		
		//Change worker 7 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w7ID, lwpc1OID, component);
	    
		//Change worker 7 status to ALLOCATED FOR BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w7ID, workerSpec7, newRequestSpec1);
		
		//Pause the request
	    req_117_Util.pauseRequest(component, lwpc1OID, request1ID, future6);
	    
		//Update the request: need 5 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 5, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
	    
	    //Update the request: need 3 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 3, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
	    
	    //The user disposes the worker 5 - expect to command the worker to stop working
		WorkerAllocation allocation5 = new WorkerAllocation(w5ID).addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpc1OID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub5.getDeploymentID(), workerStub5.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, worker5, allocation5, requestSpec1, lwpc1OID, false, null, null, null);
		
		//The consumer set the remote worker 3 as unwanted - expect the peer to dispose the remote worker
		WorkerAllocation allocationR3 = new WorkerAllocation(rwm3OID).addRemoteWorkerManagementClient(
				peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		
		AcceptanceTestUtil.publishTestObject(component, rwStub3.getDeploymentID(), rwStub3.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, rw3, allocationR3, newRequestSpec1, lwpc1OID, false, null, rwp, rwpID);
		
		//Update the request: need 4 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 4, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
	    
		//Update the request: need 3 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 3, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);

	    //The user disposes the remote worker 4 - expect the peer to dispose the remote worker
		WorkerAllocation allocationR4 = new WorkerAllocation(rwm4OID);
		allocationR4.addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpc1OID).addRemoteWorkerManagementClient(
				peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		
		AcceptanceTestUtil.publishTestObject(component, rwStub4.getDeploymentID(), rwStub4.getObject(),
				Worker.class);
		req_015_Util.localDisposeRemoteWorker(component, rwStub4, allocationR4, rwp, rwpID, false);
		
	    //Update the request: need 2 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
	    
	    //The consumer set the worker as unwanted - expect to command the worker to stop working
		WorkerAllocation allocation6 = new WorkerAllocation(w6ID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub6.getDeploymentID(), workerStub6.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, worker6, allocation6, newRequestSpec1, lwpc1OID, false, null, null, null);
		
		//Update the request: need 1 worker
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
		
		//Resume the request
		req_118_Util.resumeRequestWithNoReschedule(newRequestSpec1, lwpc1OID, component);
		
		//Pause the request
		req_117_Util.pauseRequest(component, lwpc1OID, request1ID, null);
		
		//Finish the request
		WorkerAllocation workerAllocation7 = new WorkerAllocation(w7ID);
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalWorkers(component, lwp, lwpOD, broker1PubKey, lwpc1OID.getServiceID(), newRequestSpec1, 
				AcceptanceTestUtil.createList(workerAllocation7));
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0037_CancellingAndRescheduleRequestWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");

		// Workers login
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("U1","S1", null, null);
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("U2","S1", null, null);
		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createClassAdWorkerSpec("U3","S1", null, null);
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createClassAdWorkerSpec("U4","S1", null, null);
		WorkerSpecification workerSpec5 = workerAcceptanceUtil.createClassAdWorkerSpec("U5","S1", null, null);
		WorkerSpecification workerSpec6 = workerAcceptanceUtil.createClassAdWorkerSpec("U6","S1", null, null);
		WorkerSpecification workerSpec7 = workerAcceptanceUtil.createClassAdWorkerSpec("U7","S1", null, null);
		
		String worker1PublicKey = "worker1PublicKey";
		String worker2PublicKey = "worker2PublicKey";
		String worker3PublicKey = "worker3PublicKey";
		String worker4PublicKey = "worker4PublicKey";
		String worker5PublicKey = "worker5PublicKey";
		String worker6PublicKey = "worker6PublicKey";
		String worker7PublicKey = "worker7PublicKey";
		
		DeploymentID w1ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec1, worker1PublicKey);
		req_010_Util.workerLogin(component, workerSpec1, w1ID);

		DeploymentID w2ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec2, worker2PublicKey);
		req_010_Util.workerLogin(component, workerSpec2, w2ID);

		DeploymentID w3ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec3, worker3PublicKey);
		req_010_Util.workerLogin(component, workerSpec3, w3ID);

		DeploymentID w4ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec4, worker4PublicKey);
		req_010_Util.workerLogin(component, workerSpec4, w4ID);

		DeploymentID w5ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec5, worker5PublicKey);
		req_010_Util.workerLogin(component, workerSpec5, w5ID);

		DeploymentID w6ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec6, worker6PublicKey);
		req_010_Util.workerLogin(component, workerSpec6, w6ID);

		DeploymentID w7ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec7, worker7PublicKey);
		req_010_Util.workerLogin(component, workerSpec7, w7ID);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
	    //Client login and request 10 workers
		String broker1PubKey = "publicKey1";
		
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
		
	    long request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements(null), 10, 0, 0);
	    ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1);
	    
	    //Change worker 1 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w1ID, lwpc1OID, component);
	    
		//Change worker status to ALLOCATED FOR BROKER
		TestStub workerStub = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w1ID, workerSpec1, requestSpec1);
		
		Worker worker1 = (Worker) workerStub.getObject();
		
	    //GIS client receive a remote worker provider
        WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("rU1", "rS1", null, null);
        
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker 1
		DeploymentID rwm1OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "rworker1PK", broker1PubKey).getDeploymentID();
		
		//Change remote worker 1 status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpc1OID, AcceptanceTestUtil.getBoundObject(lwpc1OID));
		
		Worker rw1 = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm1OID, workerSpecR1, requestSpec1).getObject();
		
		//Update the request: need 4 workers
		RequestSpecification newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 4, 0, 0);
		ScheduledFuture<?> future2 = req_116_Util.updateRequest(component, newRequestSpec1, true, future1, lwpc1OID);

		//The consumer set the remote worker 1 as unwanted - expect the peer to dispose the remote worker
		WorkerAllocation allocationR1 = new WorkerAllocation(rwm1OID);
		allocationR1.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		req_016_Util.unwantedWorker(component, rw1, allocationR1, requestSpec1, lwpc1OID, false, null, rwp, rwpID);
		
	    //Change worker 2 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w2ID, lwpc1OID, component);
	    
		//Change worker 2 status to ALLOCATED FOR BROKER
		TestStub workerStub2 = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w2ID, workerSpec2, newRequestSpec1);
		
		Worker worker2 = (Worker) workerStub2.getObject();
		
		//Update the request: need 2 workers
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 2, 0, 0);
		req_116_Util.updateRequest(component, newRequestSpec1, false, future2, lwpc1OID);
		
		//Update the request: need 1 worker
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 1, 0, 0);
		req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
		
		//The consumer set the worker 1 as unwanted - expect to command the worker to stop working
		WorkerAllocation allocation1 = new WorkerAllocation(w1ID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub.getDeploymentID(), workerStub.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, worker1, allocation1, requestSpec1, lwpc1OID, false, null, null, null);
		
		//Update the request: need 3 workers
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 3, 0, 0);
		ScheduledFuture<?> future3 = req_116_Util.updateRequest(component, newRequestSpec1, true, lwpc1OID);
	
	    //Change worker 3 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w3ID, lwpc1OID, component);
	    
		//Change worker 3 status to ALLOCATED FOR BROKER
	    TestStub workerStub3 = 
	    	req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w3ID, workerSpec3, newRequestSpec1);
	    
	    Worker worker3 = (Worker) workerStub3.getObject();
		
	    //Change worker 4 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w4ID, lwpc1OID, future3, component);
	    
		//Change worker 4 status to ALLOCATED FOR BROKER
		TestStub workerStub4 = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w4ID, workerSpec4, newRequestSpec1);
		
		Worker worker4 = (Worker) workerStub4.getObject();
		
		//The consumer set the worker 2 as unwanted - expect to command the worker to stop working
		WorkerAllocation allocation2 = new WorkerAllocation(w2ID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub2.getDeploymentID(), workerStub2.getObject(),
				Worker.class);
		ScheduledFuture<?> future4 = req_016_Util.unwantedWorker(component, worker2, allocation2, 
				newRequestSpec1, lwpc1OID, true, null, rwp, rwpID);
		
		//Remote worker provider client receive a remote worker 2
		
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createClassAdWorkerSpec("rU2", "rS1", null, null);
		DeploymentID rwm2OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR2, "rworker2PK", broker1PubKey, future4).getDeploymentID();
		
		//Change remote worker 2 status to ALLOCATED FOR BROKER
		Worker rw2 = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(),
				rwm2OID, workerSpecR2, newRequestSpec1).getObject();

		//The consumer set the remote worker 2 as unwanted - expect the peer to dispose the remote worker
		WorkerAllocation allocationR2 = new WorkerAllocation(rwm2OID);
		allocationR2.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		ScheduledFuture<?> future5 = req_016_Util.unwantedWorker(component, rw2, allocationR2, 
				newRequestSpec1, lwpc1OID, true, null, rwp, rwpID);
		
		//Pause the request
		req_117_Util.pauseRequest(component, lwpc1OID, request1ID, future5);
		
		//Update the request: need 6 workers
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 6, 0, 0);
		req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
		
		//The user disposes the worker 3 - expect to command the worker to stop working
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		WorkerAllocation allocation3 = new WorkerAllocation(w3ID).addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpc1OID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub3.getDeploymentID(), workerStub3.getObject(),
				Worker.class);
		req_015_Util.localConsumerDisposesLocalWorker(component, worker3, allocation3);
		
		//The consumer set the worker 4 as unwanted - expect to command the worker to stop working
		WorkerAllocation allocation4 = new WorkerAllocation(w4ID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub4.getDeploymentID(), workerStub4.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, worker4, allocation4, newRequestSpec1, lwpc1OID, false, null, null, null);
		
		//Resume the request
		ScheduledFuture<?> future6 = req_118_Util.resumeRequest(newRequestSpec1, lwpc1OID, component);
		
		//Change worker 5 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w5ID, lwpc1OID, component);
	    
		//Change worker 5 status to ALLOCATED FOR BROKER
	    TestStub workerStub5 = 
	    	req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w5ID, workerSpec5, newRequestSpec1);
	    
	    Worker worker5 = (Worker) workerStub5.getObject();
		
		//Remote worker provider client receive a remote worker 3
	    EasyMock.reset(rwp);
	    EasyMock.replay(rwp);
	    
	    WorkerSpecification workerSpecR3 = workerAcceptanceUtil.createClassAdWorkerSpec("rU3", "rS1", null, null);
		DeploymentID rwm3OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR3, "rworker3PK", broker1PubKey).getDeploymentID();
		
		//Change remote worker 3 status to ALLOCATED FOR BROKER
		TestStub rwStub3 = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm3OID, workerSpecR3, newRequestSpec1);
		
		Worker rw3 = (Worker) rwStub3.getObject();
		
		//Change worker 6 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w6ID, lwpc1OID, component);
	    
		//Change worker 6 status to ALLOCATED FOR BROKER
	    TestStub workerStub6 = 
	    	req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w6ID, workerSpec6, newRequestSpec1);
	    
	    Worker worker6 = (Worker) workerStub6.getObject();

	    //Remote worker provider client receive a remote worker 4	   
	    WorkerSpecification workerSpecR4 = workerAcceptanceUtil.createClassAdWorkerSpec("rU4", "rS1", null, null);
	    DeploymentID rwm4OID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR4, "rworker4PK", broker1PubKey).getDeploymentID();
		
		//Change remote worker 4 status to ALLOCATED FOR BROKER
		TestStub rwStub4 = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm4OID, workerSpecR4, newRequestSpec1);
		
//		Worker rw4 = (Worker) rwStub4.getObject();
		
		//Change worker 7 status to IDLE
	    req_025_Util.changeWorkerStatusToIdleWorkingForBroker(w7ID, lwpc1OID, component);
	    
		//Change worker 7 status to ALLOCATED FOR BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, w7ID, workerSpec7, newRequestSpec1);
		
		//Pause the request
	    req_117_Util.pauseRequest(component, lwpc1OID, request1ID, future6);
	    
		//Update the request: need 5 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 5, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
	    
	    //Update the request: need 3 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 3, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
	    
	    //The user disposes the worker 5 - expect to command the worker to stop working
		WorkerAllocation allocation5 = new WorkerAllocation(w5ID).addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpc1OID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub5.getDeploymentID(), workerStub5.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, worker5, allocation5, requestSpec1, lwpc1OID, false, null, null, null);
		
		//The consumer set the remote worker 3 as unwanted - expect the peer to dispose the remote worker
		WorkerAllocation allocationR3 = new WorkerAllocation(rwm3OID).addRemoteWorkerManagementClient(
				peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		
		AcceptanceTestUtil.publishTestObject(component, rwStub3.getDeploymentID(), rwStub3.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, rw3, allocationR3, newRequestSpec1, lwpc1OID, false, null, rwp, rwpID);
		
		//Update the request: need 4 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 4, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
	    
		//Update the request: need 3 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 3, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);

	    //The user disposes the remote worker 4 - expect the peer to dispose the remote worker
		WorkerAllocation allocationR4 = new WorkerAllocation(rwm4OID);
		allocationR4.addLoserRequestSpec(requestSpec1).addLoserConsumer(lwpc1OID).addRemoteWorkerManagementClient(
				peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		
		AcceptanceTestUtil.publishTestObject(component, rwStub4.getDeploymentID(), rwStub4.getObject(),
				Worker.class);
		req_015_Util.localDisposeRemoteWorker(component, rwStub4, allocationR4, rwp, rwpID, false);
		
	    //Update the request: need 2 workers
	    newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 2, 0, 0);
	    req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
	    
	    //The consumer set the worker as unwanted - expect to command the worker to stop working
		WorkerAllocation allocation6 = new WorkerAllocation(w6ID);
		
		AcceptanceTestUtil.publishTestObject(component, workerStub6.getDeploymentID(), workerStub6.getObject(),
				Worker.class);
		req_016_Util.unwantedWorker(component, worker6, allocation6, newRequestSpec1, lwpc1OID, false, null, null, null);
		
		//Update the request: need 1 worker
		newRequestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 1, 0, 0);
		req_116_Util.updateRequest(component, newRequestSpec1, false, lwpc1OID);
		
		//Resume the request
		req_118_Util.resumeRequestWithNoReschedule(newRequestSpec1, lwpc1OID, component);
		
		//Pause the request
		req_117_Util.pauseRequest(component, lwpc1OID, request1ID, null);
		
		//Finish the request
		WorkerAllocation workerAllocation7 = new WorkerAllocation(w7ID);
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalWorkers(component, lwp, lwpOD, broker1PubKey, lwpc1OID.getServiceID(), newRequestSpec1, 
				AcceptanceTestUtil.createList(workerAllocation7));
	}
}