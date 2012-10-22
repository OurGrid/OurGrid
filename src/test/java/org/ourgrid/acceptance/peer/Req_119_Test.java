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
import java.util.LinkedList;
import java.util.List;

import org.easymock.classextension.EasyMock;
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
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_034_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_119_Util;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

@ReqTest(reqs="REQ119")
public class Req_119_Test extends PeerAcceptanceTestCase {

	public static final String COMM_FILE_PATH = "req_119"+File.separator;
    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_034_Util req_034_Util = new Req_034_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    private Req_119_Util req_119_Util = new Req_119_Util(getComponentContext());

    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
    /**
	* This test contains the following steps:
	*	
	*	 1. One remote consumer requests one Worker;
	*	 2. The Worker is allocated and it is registered interest in the failure of this remote consumer;
	*	 3. Expect this consumer to be listed in the consumers status
	*	 4. The remote consumer fails - Expect the Peer to:
	*	       1. Log the info
	*	       2. Command the Worker allocated to this consumer to stop working
	*	 5. Expect this consumer NOT to be listed in the consumers status
	*	 6. Verify if the Worker is marked as IDLE
	*	 7. The Worker sends a status changed message (ALLOCATED_FOR_PEER) - Expect the Peer not to deliver this Worker to the failed consumer
    */
	@ReqTest(test="AT-119.1", reqs="REQ119")
	@Test public void test_AT_119_1_RemoteConsumerFailWithAllocatedWorkerBeforeToBeDelivered() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");

		String workerAPublicKey = "workerAPublicKey";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker A to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		
	    //Request a worker for the remote Consumer
		String remoteClientPublicKey = "consumerPublicKey";
		
		DeploymentID remoteClientOID = new DeploymentID(new ContainerID("rUserName", "server", "module", remoteClientPublicKey),
				PeerConstants.REMOTE_WORKER_MANAGEMENT_CLIENT);
		
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADID);
		req_011_Util.requestForRemoteClient(component, 
				remoteClientOID, requestSpec1, 0, workerAllocationA);
		
		//Verify if is registered interest in the failure of this remote consumer
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(remoteClientOID.getServiceID()));
		
		//Expect this consumer to be listed in the consumers status
        ConsumerInfo remoteConsumerInfo = new ConsumerInfo(1, remoteClientOID.getServiceID().toString());
        req_034_Util.getRemoteConsumersStatus(AcceptanceTestUtil.createList(remoteConsumerInfo));

        //Notify the failure of the remote consumer
        //Expect the local worker to stop working
        WorkerAllocation allocationA = new WorkerAllocation(workerADID);
        req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, false, allocationA);
        
        //Verify if is registered interest in the failure of this remote consumer
        peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(remoteClientOID.getServiceID());
        
        //Expect this consumer NOT to be listed in the consumers status
        req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());

        //Verify if the Worker is marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
        //Worker sends a status changed message - Expect the peer to log the warn
		RemoteWorkerManagement worker = EasyMock.createMock(RemoteWorkerManagement.class);
		
		DeploymentID remoteWorkerID = new DeploymentID(new ContainerID("rUserName", "server", "module", workerAPublicKey),
				WorkerConstants.REMOTE_WORKER_MANAGEMENT);
		
		peerAcceptanceUtil.createStub(worker, RemoteWorkerManagement.class, remoteWorkerID);
		
		EasyMock.reset(loggerMock);
		loggerMock.warn("The worker <" + workerADID.getContainerID() + "> (IDLE) changed its status" +
				" to ALLOCATED_FOR_PEER. This status change was ignored.");
		replayActiveMocks();
		
		req_025_Util.changeStatusAllocatedForPeer(workerADID, null, component);

		verifyActiveMocks();
	}
	
	/**
	 * This test contains the following steps:
	 * 1. One remote consumer requests one Worker;
	 * 2. The Worker is allocated and it is registered interest in the failure of this remote consumer;
	 * 3. The Worker changes its status to ALLOCATED_FOR_PEER and is delivered to the remote consumer
	 * 4. Expect this consumer to be listed in the consumers status
	 * 5. The remote consumer fails - Expect the Peer to:
	 *       1. Log the info
	 *       2. Command the Worker allocated to this consumer to stop working
	 *       3. Register interest in the failure of this Worker
	 *  6. Verify if the Worker is marked as IDLE
	 *  7. Expect this consumer NOT to be listed in the consumers status
	 *
	 */
	@ReqTest(test="AT-119.2", reqs="REQ119")
	@Test public void test_AT_119_2_RemoteConsumerFailWithWorkerDelivered() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		
		String workerAPublicKey = "workerAPublicKey";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		//Change worker A to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);
	  
		//Request a worker for the remote Consumer
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADID);
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1, 0, workerAllocationA);
		
		//Worker sends a status changed message - Expect the peer to deliver the Worker to the consumer
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerADID, workerSpecA, remoteClientOID);
		
		//Verify if it was unregistered interest in the failure of this local worker
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADID.getServiceID()));
		
		//Verify if it is registered interest in the failure of this remote consumer
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(remoteClientOID.getServiceID()));
		
		//Expect this consumer to be listed in the consumers status
		ConsumerInfo remoteConsumerInfo = new ConsumerInfo(1, remoteClientOID.getServiceID().toString());
		req_034_Util.getRemoteConsumersStatus(AcceptanceTestUtil.createList(remoteConsumerInfo));

        //Notify the failure of the remote consumer
        //Expect the local worker to stop working
        WorkerAllocation allocationA = new WorkerAllocation(workerADID);
        req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, false, allocationA);
        
        //Verify if it was registered interest in the failure of the local worker
        assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADID.getServiceID()));
        
        //Expect this consumer NOT to be listed in the consumers status
        req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());

        //Verify if the Worker is marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	/**
	 * This test contains the following steps:
	 * 1. One remote consumer requests one Worker;
	 * 2. The Worker is allocated and it is registered interest in the failure of this remote consumer;
	 * 3. The Worker changes its status to ALLOCATED_FOR_PEER and is delivered to the remote consumer
	 * 4. The same remote consumer requests one more Worker;
	 * 5. The Worker is allocated to this consumer;
	 * 6. The Worker changes its status to ALLOCATED_FOR_PEER and is delivered to the remote consumer
	 * 7. Expect this consumer to be listed in the consumers status
	 * 8. The remote consumer fails - Expect the Peer to:
	 *       1. Log the info
	 *       2. Command the 2 Workers allocated to this consumer to stop working
	 *       3. Register interest in the failure of both Workers
	 * 9. Verify if the Worker is marked as IDLE
	 * 10. Expect this consumer NOT to be listed in the consumers status
	 */
	@ReqTest(test="AT-119.3", reqs="REQ119")
	@Test public void test_AT_119_3_RemoteConsumerFailWithMoreThanOneRequest() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		
		
		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);

		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);

		//Change workers to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);
		
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);

		//Request a worker for the remote consumer
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    
	    WorkerAllocation workerAllocationB = new WorkerAllocation(workerBDID);
		RemoteWorkerProviderClient remoteClientB = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1,
				0, workerAllocationB);
		
		//Worker B sends a status changed message - Expect the peer to deliver the Worker to the consumer
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClientB, workerBDID, workerSpecB, remoteClientOID);
		
		//Request another worker for the same remote consumer
		int request2ID = 2;
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, "", 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADID);
		RemoteWorkerProviderClient remoteClientA = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec2, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				workerAllocationA);
		
		//Worker A sends a status changed message - Expect the peer to deliver the Worker to the consumer
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClientA, workerADID, workerSpecA, remoteClientOID);
		
		//Expect this consumer to be listed in the consumers status
		ConsumerInfo remoteConsumerInfo = new ConsumerInfo(2, remoteClientOID.getServiceID().toString());
		req_034_Util.getRemoteConsumersStatus(AcceptanceTestUtil.createList(remoteConsumerInfo));
        
        //Notify the failure of the remote consumer
        //Expect the local workers to stop working
        WorkerAllocation allocationA = new WorkerAllocation(workerADID);
        WorkerAllocation allocationB = new WorkerAllocation(workerBDID);
        req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, false, allocationA, allocationB);
        
        //Verify if it was registered interest in the failure of the local worker
        assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADID.getServiceID()));
        assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBDID.getServiceID()));
        
        //Expect this consumer NOT to be listed in the consumers status
        req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());

        //Verify if the Worker is marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	/**
	 * This test contains the following steps:
	 *   1. One remote consumer requests one Worker;
	 *   2. The Worker is allocated and it is registered interest in the failure of this remote consumer;
	 *   3. The Worker changes its status to ALLOCATED_FOR_PEER and is delivered to the remote consumer
	 *   4. The remote consumer disposes the Worker
	 *   5. The remote consumer fails - Expect the Peer to ignore the failure and log the debug message
	 */
	@ReqTest(test="AT-119.4", reqs="REQ119")
	@Test public void test_AT_119_4_RemoteConsumerFailThatDisposedWorker() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		
		String workerPublicKey = "workerAPublicKey";
		
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);

		//Change worker to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);
		
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);

		//Request a worker for the remote consumer
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    
	    WorkerAllocation workerAllocation = new WorkerAllocation(workerDID);
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1,
				0, workerAllocation);
		
		//Worker sends a status changed message - Expect the peer to deliver the Worker to the consumer
		RemoteWorkerManagement rWorkerManag = req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerDID, 
				workerSpec, remoteClientOID);
		
		//The remote consumer disposes the worker - expect to command the worker to stop working
		req_015_Util.remoteDisposeLocalWorker(component, remoteClientOID, rWorkerManag, workerDID);
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerDID.getServiceID(), remoteClientOID));
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(remoteClientOID.getServiceID()));
		
		//do nothing (the RemoteWorkerProvider monitor will notice the Peer failure)
		peerAcceptanceUtil.getRemoteClientMonitor().doNotifyFailure(null, remoteClientOID);
	}
	
	/**
	* This test contains the following steps:
	*  1. One remote consumer requests two Workers;
	*  2. The Workers are allocated and it is registered interest in the failure of this remote consumer;
	*  3. The Workers changes its status to ALLOCATED_FOR_PEER and are delivered to the remote consumer
	*  4. The remote consumer disposes one of the Workers
	*  5. Verify if this consumer is listed in the consumers status
	*  6. The remote consumer fails - Expect the Peer to:
	*        1. Log the info
	*        2. Command the Worker still allocated to this consumer to stop working
	*        3. Register interest in the failure of this Worker
	*  7. Verify if the Worker is marked as IDLE
	*  8. Expect this consumer NOT to be listed in the consumers status
	*/
	@ReqTest(test="AT-119.5", reqs="REQ119")
	@Test public void test_AT_119_5_RemoteConsumerFailThatDisposedOneWorker() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		
		
		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);
		
		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);
		
		//Change workers to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);
		
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);

		//Request a worker for the remote consumer
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 2, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADID);
	    WorkerAllocation workerAllocationB = new WorkerAllocation(workerBDID);
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1,
				0, workerAllocationA, workerAllocationB);
		
		//Worker A sends a status changed message - Expect the peer to deliver the Worker to the consumer
		RemoteWorkerManagement rWorkerManagA = req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerADID, 
				workerSpecA, remoteClientOID);
		
		//Worker B sends a status changed message - Expect the peer to deliver the Worker to the consumer
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerBDID, workerSpecB, remoteClientOID);
		
		//The remote consumer disposes one of the workers - expect to command the worker to stop working
		req_015_Util.remoteDisposeLocalWorker(component, remoteClientOID, rWorkerManagA, workerADID);
		assertFalse(AcceptanceTestUtil.isInterested(component, workerADID.getServiceID(), remoteClientOID));
		
		//Expect this consumer to be listed in the consumers status
		ConsumerInfo remoteConsumerInfo = new ConsumerInfo(1, remoteClientOID.getServiceID().toString());
		req_034_Util.getRemoteConsumersStatus(AcceptanceTestUtil.createList(remoteConsumerInfo));
        
        //Notify the failure of the remote consumer
		WorkerAllocation allocationB = new WorkerAllocation(workerBDID);
		req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, false, allocationB);
		
		//Verify if it was registered interest in the failure of the local workers
		assertFalse(AcceptanceTestUtil.isInterested(component, workerADID.getServiceID(), remoteClientOID));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBDID.getServiceID()));
		
		//Expect this consumer NOT to be listed in the consumers status
		req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());

        //Verify if the Worker is marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	* This test contains the following steps:
	*	
	*	 1. One remote consumer requests one Worker;
	*	 2. The Worker is allocated and it is registered interest in the failure of this remote consumer;
	*	 3. Expect this consumer to be listed in the consumers status
	*	 4. The remote consumer fails - Expect the Peer to:
	*	       1. Log the info
	*	       2. Command the Worker allocated to this consumer to stop working
	*	 5. Expect this consumer NOT to be listed in the consumers status
	*	 6. Verify if the Worker is marked as IDLE
	*	 7. The Worker sends a status changed message (ALLOCATED_FOR_PEER) - Expect the Peer not to deliver this Worker to the failed consumer
	*/
	@ReqTest(test="AT-119.1", reqs="REQ119")
	@Category(JDLCompliantTest.class) @Test public void test_AT_119_1_RemoteConsumerFailWithAllocatedWorkerBeforeToBeDeliveredWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		String workerAPublicKey = "workerAPublicKey";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);
	
		//Change worker A to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		
	    //Request a worker for the remote Consumer
		String remoteClientPublicKey = "consumerPublicKey";
		
		DeploymentID remoteClientOID = new DeploymentID(new ContainerID("rUserName", "server", "module", remoteClientPublicKey),
				PeerConstants.REMOTE_WORKER_MANAGEMENT_CLIENT);
		
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( null ), 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADID);
		req_011_Util.requestForRemoteClient(component, 
				remoteClientOID, requestSpec1, 0, workerAllocationA);
		
		//Verify if is registered interest in the failure of this remote consumer
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(remoteClientOID.getServiceID()));
		
		//Expect this consumer to be listed in the consumers status
	    ConsumerInfo remoteConsumerInfo = new ConsumerInfo(1, remoteClientOID.getServiceID().toString());
	    req_034_Util.getRemoteConsumersStatus(AcceptanceTestUtil.createList(remoteConsumerInfo));
	
	    //Notify the failure of the remote consumer
	    //Expect the local worker to stop working
	    WorkerAllocation allocationA = new WorkerAllocation(workerADID);
	    req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, false, allocationA);
	    
	    //Verify if is registered interest in the failure of this remote consumer
	    peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(remoteClientOID.getServiceID());
	    
	    //Expect this consumer NOT to be listed in the consumers status
	    req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());
	
	    //Verify if the Worker is marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
	    //Worker sends a status changed message - Expect the peer to log the warn
		RemoteWorkerManagement worker = EasyMock.createMock(RemoteWorkerManagement.class);
		
		DeploymentID remoteWorkerID = new DeploymentID(new ContainerID("rUserName", "server", "module", workerAPublicKey),
				WorkerConstants.REMOTE_WORKER_MANAGEMENT);
		
		peerAcceptanceUtil.createStub(worker, RemoteWorkerManagement.class, remoteWorkerID);
		
		EasyMock.reset(loggerMock);
		loggerMock.warn("The worker <" + workerADID.getContainerID() + "> (IDLE) changed its status" +
				" to ALLOCATED_FOR_PEER. This status change was ignored.");
		replayActiveMocks();
		
		req_025_Util.changeStatusAllocatedForPeer(workerADID, null, component);
	
		verifyActiveMocks();
	}

	/**
	 * This test contains the following steps:
	 * 1. One remote consumer requests one Worker;
	 * 2. The Worker is allocated and it is registered interest in the failure of this remote consumer;
	 * 3. The Worker changes its status to ALLOCATED_FOR_PEER and is delivered to the remote consumer
	 * 4. Expect this consumer to be listed in the consumers status
	 * 5. The remote consumer fails - Expect the Peer to:
	 *       1. Log the info
	 *       2. Command the Worker allocated to this consumer to stop working
	 *       3. Register interest in the failure of this Worker
	 *  6. Verify if the Worker is marked as IDLE
	 *  7. Expect this consumer NOT to be listed in the consumers status
	 *
	 */
	@ReqTest(test="AT-119.2", reqs="REQ119")
	@Category(JDLCompliantTest.class) @Test public void test_AT_119_2_RemoteConsumerFailWithWorkerDeliveredWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		String workerAPublicKey = "workerAPublicKey";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);
	
		//Change worker A to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);
	
		//Request a worker for the remote Consumer
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( null ), 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADID);
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1, 0, workerAllocationA);
		
		//Worker sends a status changed message - Expect the peer to deliver the Worker to the consumer
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerADID, workerSpecA, remoteClientOID);
		
		//Verify if it was unregistered interest in the failure of this local worker
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADID.getServiceID()));
		
		//Verify if it is registered interest in the failure of this remote consumer
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(remoteClientOID.getServiceID()));
		
		//Expect this consumer to be listed in the consumers status
		ConsumerInfo remoteConsumerInfo = new ConsumerInfo(1, remoteClientOID.getServiceID().toString());
		req_034_Util.getRemoteConsumersStatus(AcceptanceTestUtil.createList(remoteConsumerInfo));
	
	    //Notify the failure of the remote consumer
	    //Expect the local worker to stop working
	    WorkerAllocation allocationA = new WorkerAllocation(workerADID);
	    req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, false, allocationA);
	    
	    //Verify if it was registered interest in the failure of the local worker
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADID.getServiceID()));
	    
	    //Expect this consumer NOT to be listed in the consumers status
	    req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());
	
	    //Verify if the Worker is marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * This test contains the following steps:
	 * 1. One remote consumer requests one Worker;
	 * 2. The Worker is allocated and it is registered interest in the failure of this remote consumer;
	 * 3. The Worker changes its status to ALLOCATED_FOR_PEER and is delivered to the remote consumer
	 * 4. The same remote consumer requests one more Worker;
	 * 5. The Worker is allocated to this consumer;
	 * 6. The Worker changes its status to ALLOCATED_FOR_PEER and is delivered to the remote consumer
	 * 7. Expect this consumer to be listed in the consumers status
	 * 8. The remote consumer fails - Expect the Peer to:
	 *       1. Log the info
	 *       2. Command the 2 Workers allocated to this consumer to stop working
	 *       3. Register interest in the failure of both Workers
	 * 9. Verify if the Worker is marked as IDLE
	 * 10. Expect this consumer NOT to be listed in the consumers status
	 */
	@ReqTest(test="AT-119.3", reqs="REQ119")
	@Category(JDLCompliantTest.class) @Test public void test_AT_119_3_RemoteConsumerFailWithMoreThanOneRequestWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		
		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);
		
		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);
		
		//Change workers to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);
		
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);
	
		//Request a worker for the remote consumer
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( null ), 1, 0, 0);
	    
	    WorkerAllocation workerAllocationB = new WorkerAllocation(workerBDID);
		RemoteWorkerProviderClient remoteClientB = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1,
				0, workerAllocationB);
		
		//Worker B sends a status changed message - Expect the peer to deliver the Worker to the consumer
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClientB, workerBDID, workerSpecB, remoteClientOID);
		
		//Request another worker for the same remote consumer
		int request2ID = 2;
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, buildRequirements( null ), 1, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADID);
		RemoteWorkerProviderClient remoteClientA = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec2, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				workerAllocationA);
		
		//Worker A sends a status changed message - Expect the peer to deliver the Worker to the consumer
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClientA, workerADID, workerSpecA, remoteClientOID);
		
		//Expect this consumer to be listed in the consumers status
		ConsumerInfo remoteConsumerInfo = new ConsumerInfo(2, remoteClientOID.getServiceID().toString());
		req_034_Util.getRemoteConsumersStatus(AcceptanceTestUtil.createList(remoteConsumerInfo));
	    
	    //Notify the failure of the remote consumer
	    //Expect the local workers to stop working
	    WorkerAllocation allocationA = new WorkerAllocation(workerADID);
	    WorkerAllocation allocationB = new WorkerAllocation(workerBDID);
	    req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, false, allocationA, allocationB);
	    
	    //Verify if it was registered interest in the failure of the local worker
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADID.getServiceID()));
	    assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBDID.getServiceID()));
	    
	    //Expect this consumer NOT to be listed in the consumers status
	    req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());
	
	    //Verify if the Worker is marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * This test contains the following steps:
	 *   1. One remote consumer requests one Worker;
	 *   2. The Worker is allocated and it is registered interest in the failure of this remote consumer;
	 *   3. The Worker changes its status to ALLOCATED_FOR_PEER and is delivered to the remote consumer
	 *   4. The remote consumer disposes the Worker
	 *   5. The remote consumer fails - Expect the Peer to ignore the failure and log the debug message
	 */
	@ReqTest(test="AT-119.4", reqs="REQ119")
	@Category(JDLCompliantTest.class) @Test public void test_AT_119_4_RemoteConsumerFailThatDisposedWorkerWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		String workerAPublicKey = "workerAPublicKey";
		
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpec, workerDID);
		
		//Change worker to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);
		
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);
	
		//Request a worker for the remote consumer
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( null ), 1, 0, 0);
	    
	    WorkerAllocation workerAllocation = new WorkerAllocation(workerDID);
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1,
				0, workerAllocation);
		
		//Worker sends a status changed message - Expect the peer to deliver the Worker to the consumer
		RemoteWorkerManagement rWorkerManag = req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerDID, 
				workerSpec, remoteClientOID);
		
		//The remote consumer disposes the worker - expect to command the worker to stop working
		req_015_Util.remoteDisposeLocalWorker(component, remoteClientOID, rWorkerManag, workerDID);
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerDID.getServiceID(), remoteClientOID));
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(remoteClientOID.getServiceID()));
		
		//do nothing (the RemoteWorkerProvider monitor will notice the Peer failure)
		peerAcceptanceUtil.getRemoteClientMonitor().doNotifyFailure(null, remoteClientOID);
	}

	/**
	* This test contains the following steps:
	*  1. One remote consumer requests two Workers;
	*  2. The Workers are allocated and it is registered interest in the failure of this remote consumer;
	*  3. The Workers changes its status to ALLOCATED_FOR_PEER and are delivered to the remote consumer
	*  4. The remote consumer disposes one of the Workers
	*  5. Verify if this consumer is listed in the consumers status
	*  6. The remote consumer fails - Expect the Peer to:
	*        1. Log the info
	*        2. Command the Worker still allocated to this consumer to stop working
	*        3. Register interest in the failure of this Worker
	*  7. Verify if the Worker is marked as IDLE
	*  8. Expect this consumer NOT to be listed in the consumers status
	*/
	@ReqTest(test="AT-119.5", reqs="REQ119")
	@Category(JDLCompliantTest.class) @Test public void test_AT_119_5_RemoteConsumerFailThatDisposedOneWorkerWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"119_blank.xml");
		
		//set a mock log
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		
		String workerAPublicKey = "workerAPublicKey";
		String workerBPublicKey = "workerBPublicKey";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADID);
		
		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDID);
	
		//Change workers to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDID);
		
		String remoteClientPublicKey = "consumerPublicKey";
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rUserName", "server", remoteClientPublicKey);
	
		//Request a worker for the remote consumer
		int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( null ), 2, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADID);
	    WorkerAllocation workerAllocationB = new WorkerAllocation(workerBDID);
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec1,
				0, workerAllocationA, workerAllocationB);
		
		//Worker A sends a status changed message - Expect the peer to deliver the Worker to the consumer
		RemoteWorkerManagement rWorkerManagA = req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerADID, 
				workerSpecA, remoteClientOID);
		
		//Worker B sends a status changed message - Expect the peer to deliver the Worker to the consumer
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerBDID, workerSpecB, remoteClientOID);
		
		//The remote consumer disposes one of the workers - expect to command the worker to stop working
		req_015_Util.remoteDisposeLocalWorker(component, remoteClientOID, rWorkerManagA, workerADID);
		assertFalse(AcceptanceTestUtil.isInterested(component, workerADID.getServiceID(), remoteClientOID));
		
		//Expect this consumer to be listed in the consumers status
		ConsumerInfo remoteConsumerInfo = new ConsumerInfo(1, remoteClientOID.getServiceID().toString());
		req_034_Util.getRemoteConsumersStatus(AcceptanceTestUtil.createList(remoteConsumerInfo));
	    
	    //Notify the failure of the remote consumer
		WorkerAllocation allocationB = new WorkerAllocation(workerBDID);
		req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, false, allocationB);
		
		//Verify if it was registered interest in the failure of the local workers
		assertFalse(AcceptanceTestUtil.isInterested(component, workerADID.getServiceID(), remoteClientOID));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerBDID.getServiceID()));
		
		//Expect this consumer NOT to be listed in the consumers status
		req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());
	
	    //Verify if the Worker is marked as IDLE
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoB);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
}
