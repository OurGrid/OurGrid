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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

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
import org.ourgrid.acceptance.util.peer.Req_016_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_037_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
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

@ReqTest(reqs = "REQ016")
public class Req_016_Test extends PeerAcceptanceTestCase {
	
	public static final String COMM_FILE_PATH = "req_016"+File.separator;

	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_016_Util req_016_Util = new Req_016_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    private Req_037_Util req_037_Util = new Req_037_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    
	private CommuneLogger loggerMock;
	private PeerComponent component;
	private String dsServer = "ds.com";
	private String dsUser = "ds.user";
	private XMPPAccount user;
	
	private final String userName = "user011";
	private final String serverName = "server011";
	private final String password = "011011";
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		//Start peer
		component = req_010_Util.startPeer();
		
		//Create an user account
		user = req_101_Util.createLocalUser(userName, serverName, password);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("peerClient", "peerClientServer", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Set log mock
		loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
	}

	/**
	 * Verifies if the peer commands an unwanted local worker to stop working, 
	 * when there are no more consumers for it.
	 */
	@Test public void test_AT_016_1_localWorker_workerStopsWorking() throws Exception {
		// Worker login
		String workerUserName = "workerA.ourgrid.org";
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec(workerUserName, workerServerName);

		
		String workerPublicKey = "workerPublicKey";
		DeploymentID workerID = req_019_Util.createWorkerManagementDeploymentID(workerPublicKey, workerSpec);
		req_010_Util.workerLogin(component, workerSpec, workerID);

		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerID);

		// Login with a valid user
		String brokerPublicKey = "brokerPublicKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);

		// Request a worker for the logged user
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocation = new WorkerAllocation(workerID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec, allocation);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerID.getServiceID()));
		
		// Change worker status to ALLOCATED FOR BROKER
		Worker worker = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerID, workerSpec, 
					requestSpec).getObject();

		// The consumer set the worker as unwanted - expect to command the worker to stop working
		req_016_Util.unwantedWorker(component, worker, allocation, requestSpec, lwpcID, true, null, null, null);
		
		// Verify if the worker A was marked as IDLE
		List<WorkerInfo> workerInfoList = new ArrayList<WorkerInfo>(1);
		WorkerInfo workerInfo = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, null);
		workerInfoList.add(workerInfo);
		req_036_Util.getLocalWorkersStatus(workerInfoList); 
	}
	
	/**
	 * Verifies if the peer commands an unwanted local worker to work for other
	 * consumer.
	 */
	@Test public void test_AT_016_2_localWorker_workerAllocatedToAnotherClientsRequest() throws Exception {
		// Create another user account
		String userName2 = "user002";
		XMPPAccount user2 = req_101_Util.createLocalUser(userName2 , serverName, password);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker", "broker2PublicKey"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");

		
		String workerPublicKey = "workerPublicKey";
		DeploymentID workerID = req_019_Util.createWorkerManagementDeploymentID(workerPublicKey, workerSpec);
		req_010_Util.workerLogin(component, workerSpec, workerID);
		
		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerID);
		
		// Login the users
		String broker1PublicKey = "broker1PublicKey";
		DeploymentID lwpc1ID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, "broker2PublicKey");
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for consumer1
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocation = new WorkerAllocation(workerID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc), requestSpec, allocation);
		
		// Change worker status to ALLOCATED FOR BROKER
		Worker worker = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1ID, workerID, workerSpec, 
					requestSpec).getObject();
		
		// Request a worker for consumer2
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2);
		
		// The consumer set the worker as unwanted - expect to command the worker to work for consumer2
		allocation.addWinnerConsumer(lwpc2ID);
		req_016_Util.unwantedWorker(component, worker, allocation, requestSpec, lwpc1ID, true, null, null, null);
		
		// Verify if the worker A was allocated for consumer2
		List<WorkerInfo> workerInfoList = new ArrayList<WorkerInfo>(1);
		WorkerInfo workerInfo = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString());
		workerInfoList.add(workerInfo);
		req_036_Util.getLocalWorkersStatus(workerInfoList);
	}
	
	/**
	 * Verifies if the peer commands an unwanted local worker to work for other
	 * request of the same consumer.
	 */
	@Test public void test_AT_016_3_localWorker_workerAllocatedToSameClientsRequest() throws Exception {
		// Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");

		
		String workerPublicKey = "workerPublicKey";
		DeploymentID workerID = req_019_Util.createWorkerManagementDeploymentID(workerPublicKey, workerSpec);
		req_010_Util.workerLogin(component, workerSpec, workerID);
		
		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerID);
		
		// Login consumer1
		String broker1PublicKey = "broker1PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request a worker for consumer1
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocation = new WorkerAllocation(workerID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec, allocation);
		
		// Change worker status to ALLOCATED FOR BROKER
		Worker worker = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerID, 
				workerSpec, requestSpec).getObject();
		
		// Request a worker for consumer1 again
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec2);
		
		// The consumer set the worker as unwanted - expect to command the worker to work for consumer1,
		// within the other request
		allocation.addWinnerConsumer(lwpcID);
		req_016_Util.unwantedWorker(component, worker, allocation, requestSpec, lwpcID, true, null, null, null);
		
		// Change worker status to ALLOCATED FOR BROKER
		worker = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerID, 
				workerSpec, requestSpec2).getObject();

		// Verify if worker A was allocated for consumer1
		List<WorkerInfo> workerInfoList = new ArrayList<WorkerInfo>(1);
		WorkerInfo workerInfo = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, lwpcID.getServiceID().toString());
		workerInfoList.add(workerInfo);
		req_036_Util.getLocalWorkersStatus(workerInfoList);
	}
	
	/**
	 * Verifies if the peer disposes an unwanted remote worker, when there are 
	 * no more consumers for it.
	 */
	@Test public void test_AT_016_4_remoteWorker_workerSentBack() throws Exception {
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(dsUser, dsServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Client login
		String brokerGridPublicKey = "brokerPublicKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerGridPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);

		// Request workers after ds recovery - expect OG peer to query ds
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "os = windows AND mem > 256", 1, 0, 0);
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), 
				requestSpec);
				
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("workerR1.ourgrid.org", "xmpp.ourgrid.org");
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		DeploymentID rwmOID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "workerR1PubKey", brokerGridPublicKey, 
					future1).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		
		Worker workerR1 = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec).getObject();
		
		// The consumer set the remote worker as unwanted - expect the peer to dispose the remote worker
		WorkerAllocation allocationR1 = new WorkerAllocation(rwmOID);
		allocationR1.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		req_016_Util.unwantedWorker(component, workerR1, allocationR1, requestSpec, lwpcID, true, null, rwp, rwpID);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
	}
	
	/**
	 * Verifies if the peer commands an unwanted remote worker to work for 
	 * other consumer.
	 */
	@Test public void test_AT_016_5_remoteWorker_workerAllocatedToAnotherClientsRequest() throws Exception {
		// Create another user account
		XMPPAccount user2 = req_101_Util.createLocalUser("user002" , serverName, password);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(dsUser, dsServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Login the users
		String broker1PublicKey = "brokerPublicKey";
		DeploymentID lwpc1ID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);

		String broker2PublicKey = "broker2PublicKey";
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for consumer1
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc), requestSpec);

		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("workerR1.ourgrid.org", "xmpp.ourgrid.org");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser",
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		DeploymentID rwmOID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "workerR1PubKey", broker1PublicKey).getDeploymentID();
		
		assertTrue(req_018_Util.isPeerInterestedOnRemoteWorkerProvider(rwpID.getServiceID()));
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpc1ID, AcceptanceTestUtil.getBoundObject(lwpc1ID));
		
		Worker workerR1 = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec).getObject();
		
		// Request a worker for consumer2
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 2, 0, 0);
		
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2, rwps);
		
		// The consumer set the remote worker as unwanted - expect to command the remote worker to
		// work for consumer2
		
		WorkerAllocation allocation = new WorkerAllocation(rwmOID).addWinnerConsumer(lwpc2ID);
		allocation.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClient());
		req_016_Util.unwantedWorker(component, workerR1, allocation, requestSpec, lwpc1ID, true, null, rwp, rwpID);
		
		// Verify remote workers status
		RemoteWorkerInfo workerInfo = 
			new RemoteWorkerInfo(workerSpecR1, rwpID.getServiceID().toString(), lwpc2ID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(workerInfo);
	}
	
	/**
	 * Verifies if the peer commands an unwanted remote worker to work for 
	 * other request of the same consumer.
	 */
	@Test public void test_AT_016_6_remoteWorker_workerAllocatedToSameClientsRequest() throws Exception {
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(dsUser, dsServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Login the consumer1
		String brokerPublicKey = "brokerPublicKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request a worker for consumer1
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec);
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("workerR1.ourgrid.org", "xmpp.ourgrid.org");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		DeploymentID rwmOID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "workerR1PubKey", brokerPublicKey).getDeploymentID();
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(rwmOID.getServiceID()));
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		
		Worker workerR1 = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec).getObject();
		
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		
		// Request a worker for consumer1 again
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec2, rwps);
		
		// The consumer set the remote worker as unwanted - expect to command the remote worker to work for consumer1, within the other request
		WorkerAllocation allocationR1 = new WorkerAllocation(rwmOID).addWinnerConsumer(lwpcID);
		allocationR1.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClient());
		req_016_Util.unwantedWorker(component, workerR1, allocationR1, requestSpec, lwpcID, true, null, rwp, rwpID);
		
		// Verify remote workers status
		RemoteWorkerInfo workerInfo = 
			new RemoteWorkerInfo(workerSpecR1, rwpID.getServiceID().toString(), lwpcID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(workerInfo);
	}
	
	/**
	 * Verifies if the Peer ignores commands to set a local worker as unwanted,
	 * in the following scenarios:
	 * 
	 *     * An unknown local consumer;
	 *     * A null worker;
	 *     * An unknown worker - worker X;
	 *     * A local worker that is not allocated for this consumer - workers 1 and 2;
	 *     * A null request;
	 *     * A request made by another consumer.
	 * 
	 */
	@Test public void test_AT_016_7_localWorker_inputValidation() throws Exception {
		
		// Set trusted communities file
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH + "016_blank.xml");
		
		// Create another user account
		XMPPAccount user2 = req_101_Util.createLocalUser("user002" , serverName, password);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Workers login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", workerServerName);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB.ourgrid.org", workerServerName);
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_OS, "linux");

		
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);
		
		// Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// An unknown consumer sets a worker as unwanted - expect to log warn
		String unknownPublicKey = "unknownPublicKey";
		
		resetActiveMocks();
		loggerMock.warn("Ignoring an unknown consumer that set a worker as unwanted. Consumer public key: "
				+ unknownPublicKey);
		replayActiveMocks();
		
		DeploymentID unknownID = new DeploymentID(new ContainerID("unknown", "unknown", "broker", unknownPublicKey), "broker");
		
		req_016_Util.unwantedWorker(component, null, null, unknownID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// Login with the consumer1
		String broker1PublicKey = "broker1PublicKey";
		DeploymentID lwpc1ID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		// A local consumer set a null worker as unwanted - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set a null worker as unwanted.");
		replayActiveMocks();
		
		req_016_Util.unwantedWorker(component, null, null, lwpc1ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer sets an unknown worker as unwanted - expect to log warn
		Worker workerX = EasyMock.createMock(Worker.class);
		
		DeploymentID workerXID = new DeploymentID(new ContainerID("workerXUser", "workerXServer", "workerXModule", "workerXPubKey"),
				"workerXName");
		
		peerAcceptanceUtil.createStub(workerX, Worker.class, workerXID);
		
		EasyMock.replay(workerX);
			
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set the unknown worker [" + workerXID.getServiceID() + "]"
				+ " as unwanted.");
		replayActiveMocks();
		
		req_016_Util.unwantedWorker(component, workerXID.getServiceID(), null, lwpc1ID);
		EasyMock.verify(workerX);
		verifyActiveMocks();
		resetActiveMocks();
		
		// Login with the consumer2
		String broker2PublicKey = "broker2PublicKey";
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for consumer2
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec,
				new WorkerAllocation(workerAID));
		
		// Change worker status to ALLOCATED FOR BROKER
		TestStub workerStubA = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerAID, workerSpecA, 
					requestSpec);
		Worker workerA = (Worker) workerStubA.getObject();
		
		// A local consumer set a worker, that is allocated for other local consumer, as unwanted - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set a not allocated worker " +
				"[" + workerStubA.getDeploymentID().getServiceID() + "] as unwanted.");
		replayActiveMocks();
		
		req_016_Util.unwantedWorker(component, workerStubA.getDeploymentID().getServiceID(), null, lwpc1ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// Request a worker for the remote client
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 1000, "", 1, 0, 0);
		DeploymentID rwpcID = PeerAcceptanceUtil.createRemoteConsumerID("rwpcUser", "rwpcServer", "rwpcPubKey");
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, rwpcID, requestSpec2, 0, 
					new WorkerAllocation(workerBID));
		
		// Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBID, workerSpecB, rwpcID);
		
		// A local consumer sets a local worker, that is allocated for a remote consumer, as unwanted - expect to log warn
		Worker worker = EasyMock.createMock(Worker.class);
		DeploymentID workerID = new DeploymentID(new ContainerID("workerB.ourgrid.org", workerServerName, "workerModule", workerBID.getPublicKey()),
				"workerName");
		
		peerAcceptanceUtil.createStub(worker, Worker.class, workerID);
		
		EasyMock.replay(worker);
		
		EasyMock.reset(loggerMock);
		component.setLogger(loggerMock);
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set a not allocated worker " +
				"[" + workerID.getServiceID() + "] as unwanted.");
		replayActiveMocks();
		
		req_016_Util.unwantedWorker(component, workerID.getServiceID(), null, lwpc1ID);
		verifyActiveMocks();
		EasyMock.verify(worker);
		resetActiveMocks();
		
		// A local consumer sets a local worker as unwanted for a null request - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc2ID.getServiceID() + "] that set the worker [" + workerStubA.getDeploymentID().getServiceID() + "]" +
				" as unwanted for a null request.");
		replayActiveMocks();
		
		AcceptanceTestUtil.publishTestObject(component, workerStubA.getDeploymentID(), workerA, Worker.class);
		
		req_016_Util.unwantedWorker(component, workerStubA.getDeploymentID().getServiceID(), null, lwpc2ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// Request a worker for the consumer1
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 2, "os = linux", 1, 0, 0);
		WorkerAllocation workerBAlloc = new WorkerAllocation(workerBID).addLoserConsumer(rwpcID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc), requestSpec3, workerBAlloc);
		
		// A local consumer sets a local worker as unwanted for a request from other consumer - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc2ID.getServiceID() + "] that set the worker [" + workerStubA.getDeploymentID().getServiceID() + "]" +
				" as unwanted for an invalid request [" + requestSpec3.getRequestId() + "].");
		replayActiveMocks();

		AcceptanceTestUtil.publishTestObject(component, workerStubA.getDeploymentID(), workerA, Worker.class);
		req_016_Util.unwantedWorker(component, workerStubA.getDeploymentID().getServiceID(), requestSpec3, lwpc2ID);
		verifyActiveMocks();
	}
	
	/**
	 * Verifies if the Peer ignores a command to set a remote worker as 
	 * unwanted, in the following scenarios:
	 * 
	 *     * A remote worker that is not allocated for this consumer - worker 1;
	 *     * An already disposed remote worker - worker 2;
	 *     * A null request;
	 *     * A request made by other consumer.
	 * 
	 */
	@Test public void test_AT_016_8_remoteWorker_inputValidation() throws Exception {
		// Create another user account
		XMPPAccount user2 = req_101_Util.createLocalUser("user002" , serverName, password);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(dsUser, dsServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Login with the consumer1
		String broker1PublicKey = "broker1PublicKey";
		DeploymentID lwpc1ID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		// Login with the consumer2
		String broker2PublicKey = "broker2PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker", broker2PublicKey), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for the consumer2
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		ScheduledFuture<?> future1 = 
			req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec);
				
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("workerR1.ourgrid.org", "xmpp.ourgrid.org");
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createWorkerSpec("workerR2.ourgrid.org", "xmpp.ourgrid.org");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1, workerSpecR2);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Request a worker for the consumer1
		
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		
		ScheduledFuture<?> future2 = 
			req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc), requestSpec2, rwps);

		// Remote worker provider client receives a remote worker
		DeploymentID rwmOID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "workerR1PubKey", broker2PublicKey, 
					future1).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpc2ID, AcceptanceTestUtil.getBoundObject(lwpc2ID));
		
		TestStub workerR1Stub = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec);
		
		Worker workerR1 = (Worker) workerR1Stub.getObject();
		
		// Remote worker provider client receives another remote worker
		DeploymentID rwm2OID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR2, "workerR2PubKey", broker1PublicKey, 
					future2).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpc1OD = new ObjectDeployment(component, lwpc1ID, AcceptanceTestUtil.getBoundObject(lwpc1ID));
		
		TestStub workerR2Stub = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwm2OID, workerSpecR2, requestSpec2);
		
		Worker workerR2 = (Worker) workerR2Stub.getObject();
		
		// A local consumer set a remote worker, that is allocated for other local consumer, as unwanted - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set a not allocated worker [" + 
				workerR1Stub.getDeploymentID().getServiceID() + "]" + " as unwanted.");
		replayActiveMocks();

		AcceptanceTestUtil.publishTestObject(component, workerR1Stub.getDeploymentID(), workerR1, Worker.class);
		req_016_Util.unwantedWorker(component, workerR1Stub.getDeploymentID().getServiceID(), null, lwpc1ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer set a remote worker, that has already been disposed, as unwanted - expect to log warn
		WorkerAllocation allocationR2 = new WorkerAllocation(rwm2OID);
		allocationR2.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		
		AcceptanceTestUtil.publishTestObject(component, workerR2Stub.getDeploymentID(), workerR2, Worker.class);
		req_016_Util.unwantedWorker(component, workerR2, allocationR2, requestSpec2, lwpc1ID, true, null, rwp, rwpID);
		
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set the unknown worker [" + workerR2Stub.getDeploymentID().getServiceID() + "]"
				+ " as unwanted.");
		replayActiveMocks();

		AcceptanceTestUtil.publishTestObject(component, workerR2Stub.getDeploymentID(), workerR2, Worker.class);
		req_016_Util.unwantedWorker(component, workerR2Stub.getDeploymentID().getServiceID(), null, lwpc1ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer set a remote worker as unwanted for a null request - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc2ID.getServiceID() + "] that set the remote worker " +
				"[" + workerR1Stub.getDeploymentID().getServiceID() + "] as unwanted for a null request.");
		replayActiveMocks();

		AcceptanceTestUtil.publishTestObject(component, workerR1Stub.getDeploymentID(), workerR1, Worker.class);
		req_016_Util.unwantedWorker(component, workerR1Stub.getDeploymentID().getServiceID(), null, lwpc2ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer set a remote worker as unwanted for a request from other consumer - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc2ID.getServiceID() + "] that set the remote worker " +
				"[" + workerR1Stub.getDeploymentID().getServiceID() + "] as unwanted for an invalid request [" + requestSpec2.getRequestId() + "].");
		replayActiveMocks();

		AcceptanceTestUtil.publishTestObject(component, workerR1Stub.getDeploymentID(), workerR1, Worker.class);
		req_016_Util.unwantedWorker(component, workerR1Stub.getDeploymentID().getServiceID(), requestSpec2, lwpc2ID);
		verifyActiveMocks();
	}

	/**
	 * Verifies if the peer commands an unwanted local worker to stop working, 
	 * when there are no more consumers for it.
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_016_1_localWorker_workerStopsWorkingWithJDL() throws Exception {
		// Worker login
		String workerUserName = "workerA.ourgrid.org";
		String workerServerName = "xmpp.ourgrid.org";
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec(workerUserName, workerServerName, null, null);
		DeploymentID workerID = req_019_Util.createWorkerManagementDeploymentID(workerPublicKey, workerSpec);
		req_010_Util.workerLogin(component, workerSpec, workerID);
	
	
		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerID);
	
		// Login with a valid user
		String brokerPublicKey = "brokerPublicKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
	
		// Request a worker for the logged user
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocation = new WorkerAllocation(workerID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec, allocation);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerID.getServiceID()));
		
		// Change worker status to ALLOCATED FOR BROKER
		Worker worker = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerID, workerSpec, 
					requestSpec).getObject();
	
		// The consumer set the worker as unwanted - expect to command the worker to stop working
		req_016_Util.unwantedWorker(component, worker, allocation, requestSpec, lwpcID, true, null, null, null);
		
		// Verify if the worker A was marked as IDLE
		List<WorkerInfo> workerInfoList = new ArrayList<WorkerInfo>(1);
		WorkerInfo workerInfo = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, null);
		workerInfoList.add(workerInfo);
		req_036_Util.getLocalWorkersStatus(workerInfoList); 
	}

	/**
	 * Verifies if the peer commands an unwanted local worker to work for other
	 * consumer.
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_016_2_localWorker_workerAllocatedToAnotherClientsRequestWithJDL() throws Exception {
		// Create another user account
		String userName2 = "user002";
		XMPPAccount user2 = req_101_Util.createLocalUser(userName2 , serverName, password);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker", "broker2PublicKey"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Worker login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
		String workerPublicKey = "workerPublicKey";
		DeploymentID workerID = req_019_Util.createWorkerManagementDeploymentID(workerPublicKey, workerSpec);
		req_010_Util.workerLogin(component, workerSpec, workerID);
	
		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerID);
		
		// Login the users
		String broker1PublicKey = "broker1PublicKey";
		DeploymentID lwpc1ID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, "broker2PublicKey");
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for consumer1
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocation = new WorkerAllocation(workerID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc), requestSpec, allocation);
		
		// Change worker status to ALLOCATED FOR BROKER
		Worker worker = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1ID, workerID, workerSpec, 
					requestSpec).getObject();
		
		// Request a worker for consumer2
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2);
		
		// The consumer set the worker as unwanted - expect to command the worker to work for consumer2
		allocation.addWinnerConsumer(lwpc2ID);
		req_016_Util.unwantedWorker(component, worker, allocation, requestSpec, lwpc1ID, true, null, null, null);
		
		// Verify if the worker A was allocated for consumer2
		List<WorkerInfo> workerInfoList = new ArrayList<WorkerInfo>(1);
		WorkerInfo workerInfo = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, lwpc2ID.getServiceID().toString());
		workerInfoList.add(workerInfo);
		req_036_Util.getLocalWorkersStatus(workerInfoList);
	}

	/**
	 * Verifies if the peer commands an unwanted local worker to work for other
	 * request of the same consumer.
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_016_3_localWorker_workerAllocatedToSameClientsRequestWithJDL() throws Exception {
		// Worker login
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
		DeploymentID workerID = req_019_Util.createWorkerManagementDeploymentID(workerPublicKey, workerSpec);
		req_010_Util.workerLogin(component, workerSpec, workerID);
		
		
		
		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerID);
		
		// Login consumer1
		String broker1PublicKey = "broker1PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request a worker for consumer1
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocation = new WorkerAllocation(workerID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec, allocation);
		
		// Change worker status to ALLOCATED FOR BROKER
		Worker worker = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerID, 
				workerSpec, requestSpec).getObject();
		
		// Request a worker for consumer1 again
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec2);
		
		// The consumer set the worker as unwanted - expect to command the worker to work for consumer1,
		// within the other request
		allocation.addWinnerConsumer(lwpcID);
		req_016_Util.unwantedWorker(component, worker, allocation, requestSpec, lwpcID, true, null, null, null);
		
		// Change worker status to ALLOCATED FOR BROKER
		worker = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerID, 
				workerSpec, requestSpec2).getObject();
	
		// Verify if worker A was allocated for consumer1
		List<WorkerInfo> workerInfoList = new ArrayList<WorkerInfo>(1);
		WorkerInfo workerInfo = new WorkerInfo(workerSpec, LocalWorkerState.IN_USE, lwpcID.getServiceID().toString());
		workerInfoList.add(workerInfo);
		req_036_Util.getLocalWorkersStatus(workerInfoList);
	}

	/**
	 * Verifies if the peer disposes an unwanted remote worker, when there are 
	 * no more consumers for it.
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_016_4_remoteWorker_workerSentBackWithJDL() throws Exception {
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(dsUser, dsServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Client login
		String brokerPublicKey = "brokerPublicKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
	
		// Request workers after ds recovery - expect OG peer to query ds
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "[Requirements=(other.MainMemory > 256 && other.OS == \"windows\");Rank=0]", 1, 0, 0);
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), 
				requestSpec);
				
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1.ourgrid.org", "xmpp.ourgrid.org", 512, "windows");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		DeploymentID rwmOID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "workerR1PubKey", brokerPublicKey, 
					future1).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		
		Worker workerR1 = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec).getObject();
		
		// The consumer set the remote worker as unwanted - expect the peer to dispose the remote worker
		WorkerAllocation allocationR1 = new WorkerAllocation(rwmOID);
		allocationR1.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		req_016_Util.unwantedWorker(component, workerR1, allocationR1, requestSpec, lwpcID, true, null, rwp, rwpID);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
	}

	/**
	 * Verifies if the peer commands an unwanted remote worker to work for 
	 * other consumer.
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_016_5_remoteWorker_workerAllocatedToAnotherClientsRequestWithJDL() throws Exception {
		// Create another user account
		XMPPAccount user2 = req_101_Util.createLocalUser("user002" , serverName, password);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(dsUser, dsServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Login the users
		String broker1PublicKey = "brokerPublicKey";
		DeploymentID lwpc1ID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
	
		String broker2PublicKey = "broker2PublicKey";
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for consumer1
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc), requestSpec);
	
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1.ourgrid.org", "xmpp.ourgrid.org", null, null);
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser",
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		DeploymentID rwmOID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "workerR1PubKey", broker1PublicKey).getDeploymentID();
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(rwmOID.getServiceID()));
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpc1ID, AcceptanceTestUtil.getBoundObject(lwpc1ID));
		
		Worker workerR1 = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec).getObject();
		
		// Request a worker for consumer2
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2, rwps);
		
		// The consumer set the remote worker as unwanted - expect to command the remote worker to
		// work for consumer2
		
		WorkerAllocation allocation = new WorkerAllocation(rwmOID).addWinnerConsumer(lwpc2ID);
		allocation.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClient());
		req_016_Util.unwantedWorker(component, workerR1, allocation, requestSpec, lwpc1ID, true, null, rwp, rwpID);
		
		// Verify remote workers status
		RemoteWorkerInfo workerInfo = 
			new RemoteWorkerInfo(workerSpecR1, rwpID.getServiceID().toString(), lwpc2ID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(workerInfo);
	}

	/**
	 * Verifies if the peer commands an unwanted remote worker to work for 
	 * other request of the same consumer.
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_016_6_remoteWorker_workerAllocatedToSameClientsRequestWithJDL() throws Exception {
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(dsUser, dsServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Login the consumer1
		String brokerPublicKey = "brokerPublicKey";
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request a worker for consumer1
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec);
		
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1.ourgrid.org", "xmpp.ourgrid.org", null, null);
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		DeploymentID rwmOID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "workerR1PubKey", brokerPublicKey).getDeploymentID();
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(rwmOID.getServiceID()));
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		
		Worker workerR1 = 
			(Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec).getObject();
		
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		
		// Request a worker for consumer1 again
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec2, rwps);
		
		// The consumer set the remote worker as unwanted - expect to command the remote worker to work for consumer1, within the other request
		WorkerAllocation allocationR1 = new WorkerAllocation(rwmOID).addWinnerConsumer(lwpcID);
		allocationR1.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClient());
		req_016_Util.unwantedWorker(component, workerR1, allocationR1, requestSpec, lwpcID, true, null, rwp, rwpID);
		
		// Verify remote workers status
		RemoteWorkerInfo workerInfo = 
			new RemoteWorkerInfo(workerSpecR1, rwpID.getServiceID().toString(), lwpcID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(workerInfo);
	}

	/**
	 * Verifies if the Peer ignores commands to set a local worker as unwanted,
	 * in the following scenarios:
	 * 
	 *     * An unknown local consumer;
	 *     * A null worker;
	 *     * An unknown worker - worker X;
	 *     * A local worker that is not allocated for this consumer - workers 1 and 2;
	 *     * A null request;
	 *     * A request made by another consumer.
	 * 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_016_7_localWorker_inputValidationWithJDL() throws Exception {
		
		// Set trusted communities file
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH + "016_blank.xml");
		
		// Create another user account
		XMPPAccount user2 = req_101_Util.createLocalUser("user002" , serverName, password);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker"), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		// Workers login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", workerServerName, null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB.ourgrid.org", workerServerName, null, "linux");
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);
	
		
		// Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// An unknown consumer sets a worker as unwanted - expect to log warn
		String unknownPublicKey = "unknownPublicKey";
		
		resetActiveMocks();
		loggerMock.warn("Ignoring an unknown consumer that set a worker as unwanted. Consumer public key: "
				+ unknownPublicKey);
		replayActiveMocks();
		
		DeploymentID unknownID = new DeploymentID(new ContainerID("unknown", "unknown", "broker", unknownPublicKey), "broker");
		
		req_016_Util.unwantedWorker(component, null, null, unknownID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// Login with the consumer1
		String broker1PublicKey = "broker1PublicKey";
		DeploymentID lwpc1ID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		// A local consumer set a null worker as unwanted - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set a null worker as unwanted.");
		replayActiveMocks();
		
		req_016_Util.unwantedWorker(component, null, null, lwpc1ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer sets an unknown worker as unwanted - expect to log warn
		Worker workerX = EasyMock.createMock(Worker.class);
		
		DeploymentID workerXID = new DeploymentID(new ContainerID("workerXUser", "workerXServer", "workerXModule", "workerXPubKey"),
				"workerXName");
		
		peerAcceptanceUtil.createStub(workerX, Worker.class, workerXID);
		
		EasyMock.replay(workerX);
			
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set the unknown worker [" + workerXID.getServiceID() + "]"
				+ " as unwanted.");
		replayActiveMocks();
		
		req_016_Util.unwantedWorker(component, workerXID.getServiceID(), null, lwpc1ID);
		EasyMock.verify(workerX);
		verifyActiveMocks();
		resetActiveMocks();
		
		// Login with the consumer2
		String broker2PublicKey = "broker2PublicKey";
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for consumer2
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec,
				new WorkerAllocation(workerAID));
		
		// Change worker status to ALLOCATED FOR BROKER
		TestStub workerStubA = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2ID, workerAID, workerSpecA, 
					requestSpec);
		Worker workerA = (Worker) workerStubA.getObject();
		
		// A local consumer set a worker, that is allocated for other local consumer, as unwanted - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set a not allocated worker " +
				"[" + workerStubA.getDeploymentID().getServiceID() + "] as unwanted.");
		replayActiveMocks();
		
		req_016_Util.unwantedWorker(component, workerStubA.getDeploymentID().getServiceID(), null, lwpc1ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// Request a worker for the remote client
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 1000, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		DeploymentID rwpcID = PeerAcceptanceUtil.createRemoteConsumerID("rwpcUser", "rwpcServer", "rwpcPubKey");
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, rwpcID, requestSpec2, 0, 
					new WorkerAllocation(workerBID));
		
		// Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBID, workerSpecB, rwpcID);
		
		// A local consumer sets a local worker, that is allocated for a remote consumer, as unwanted - expect to log warn
		Worker worker = EasyMock.createMock(Worker.class);
		DeploymentID workerID = new DeploymentID(new ContainerID("workerB.ourgrid.org", workerServerName, "workerModule", workerBID.getPublicKey()),
				"workerName");
		
		peerAcceptanceUtil.createStub(worker, Worker.class, workerID);
		
		EasyMock.replay(worker);
		
		EasyMock.reset(loggerMock);
		component.setLogger(loggerMock);
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set a not allocated worker " +
				"[" + workerID.getServiceID() + "] as unwanted.");
		replayActiveMocks();
		
		req_016_Util.unwantedWorker(component, workerID.getServiceID(), null, lwpc1ID);
		verifyActiveMocks();
		EasyMock.verify(worker);
		resetActiveMocks();
		
		// A local consumer sets a local worker as unwanted for a null request - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc2ID.getServiceID() + "] that set the worker [" + workerStubA.getDeploymentID().getServiceID() + "]" +
				" as unwanted for a null request.");
		replayActiveMocks();
		
		AcceptanceTestUtil.publishTestObject(component, workerStubA.getDeploymentID(), workerA, Worker.class);
		
		req_016_Util.unwantedWorker(component, workerStubA.getDeploymentID().getServiceID(), null, lwpc2ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// Request a worker for the consumer1
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), 2, "[Requirements=other.OS == \"linux\";Rank=0]", 1, 0, 0);
		WorkerAllocation workerBAlloc = new WorkerAllocation(workerBID).addLoserConsumer(rwpcID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc), requestSpec3, workerBAlloc);
		
		// A local consumer sets a local worker as unwanted for a request from other consumer - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc2ID.getServiceID() + "] that set the worker [" + workerStubA.getDeploymentID().getServiceID() + "]" +
				" as unwanted for an invalid request [" + requestSpec3.getRequestId() + "].");
		replayActiveMocks();
	
		AcceptanceTestUtil.publishTestObject(component, workerStubA.getDeploymentID(), workerA, Worker.class);
		req_016_Util.unwantedWorker(component, workerStubA.getDeploymentID().getServiceID(), requestSpec3, lwpc2ID);
		verifyActiveMocks();
	}

	/**
	 * Verifies if the Peer ignores a command to set a remote worker as 
	 * unwanted, in the following scenarios:
	 * 
	 *     * A remote worker that is not allocated for this consumer - worker 1;
	 *     * An already disposed remote worker - worker 2;
	 *     * A null request;
	 *     * A request made by other consumer.
	 * 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_016_8_remoteWorker_inputValidationWithJDL() throws Exception {
		// Create another user account
		XMPPAccount user2 = req_101_Util.createLocalUser("user002" , serverName, password);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(dsUser, dsServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Login with the consumer1
		String broker1PublicKey = "broker1PublicKey";
		DeploymentID lwpc1ID = req_108_Util.login(component, user, broker1PublicKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		// Login with the consumer2
		String broker2PublicKey = "broker2PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("peerClient2", "peerClientServer2", "broker", broker2PublicKey), "broker");
	    AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
	    
	    try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for the consumer2
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		ScheduledFuture<?> future1 = 
			req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec);
				
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1.ourgrid.org", "xmpp.ourgrid.org", null, null);
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR2.ourgrid.org", "xmpp.ourgrid.org", null, null);
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1, workerSpecR2);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Request a worker for the consumer1
		
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		
		ScheduledFuture<?> future2 = 
			req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc), requestSpec2, rwps);
	
		// Remote worker provider client receives a remote worker
		DeploymentID rwmOID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "workerR1PubKey", broker2PublicKey, 
					future1).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpc2ID, AcceptanceTestUtil.getBoundObject(lwpc2ID));
		
		TestStub workerR1Stub = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec);
		
		Worker workerR1 = (Worker) workerR1Stub.getObject();
		
		// Remote worker provider client receives another remote worker
		DeploymentID rwm2OID = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR2, "workerR2PubKey", broker1PublicKey, 
					future2).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpc1OD = new ObjectDeployment(component, lwpc1ID, AcceptanceTestUtil.getBoundObject(lwpc1ID));
		
		TestStub workerR2Stub = 
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwm2OID, workerSpecR2, requestSpec2);
		
		Worker workerR2 = (Worker) workerR2Stub.getObject();
		
		// A local consumer set a remote worker, that is allocated for other local consumer, as unwanted - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set a not allocated worker [" + 
				workerR1Stub.getDeploymentID().getServiceID() + "]" + " as unwanted.");
		replayActiveMocks();
	
		AcceptanceTestUtil.publishTestObject(component, workerR1Stub.getDeploymentID(), workerR1, Worker.class);
		req_016_Util.unwantedWorker(component, workerR1Stub.getDeploymentID().getServiceID(), null, lwpc1ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer set a remote worker, that has already been disposed, as unwanted - expect to log warn
		WorkerAllocation allocationR2 = new WorkerAllocation(rwm2OID);
		allocationR2.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClientProxy());
		
		AcceptanceTestUtil.publishTestObject(component, workerR2Stub.getDeploymentID(), workerR2, Worker.class);
		req_016_Util.unwantedWorker(component, workerR2, allocationR2, requestSpec2, lwpc1ID, true, null, rwp, rwpID);
		
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that set the unknown worker [" + workerR2Stub.getDeploymentID().getServiceID() + "]"
				+ " as unwanted.");
		replayActiveMocks();
	
		AcceptanceTestUtil.publishTestObject(component, workerR2Stub.getDeploymentID(), workerR2, Worker.class);
		req_016_Util.unwantedWorker(component, workerR2Stub.getDeploymentID().getServiceID(), null, lwpc1ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer set a remote worker as unwanted for a null request - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc2ID.getServiceID() + "] that set the remote worker " +
				"[" + workerR1Stub.getDeploymentID().getServiceID() + "] as unwanted for a null request.");
		replayActiveMocks();
	
		AcceptanceTestUtil.publishTestObject(component, workerR1Stub.getDeploymentID(), workerR1, Worker.class);
		req_016_Util.unwantedWorker(component, workerR1Stub.getDeploymentID().getServiceID(), null, lwpc2ID);
		verifyActiveMocks();
		resetActiveMocks();
		
		// A local consumer set a remote worker as unwanted for a request from other consumer - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc2ID.getServiceID() + "] that set the remote worker " +
				"[" + workerR1Stub.getDeploymentID().getServiceID() + "] as unwanted for an invalid request [" + requestSpec2.getRequestId() + "].");
		replayActiveMocks();
	
		AcceptanceTestUtil.publishTestObject(component, workerR1Stub.getDeploymentID(), workerR1, Worker.class);
		req_016_Util.unwantedWorker(component, workerR1Stub.getDeploymentID().getServiceID(), requestSpec2, lwpc2ID);
		verifyActiveMocks();
	}
	
}