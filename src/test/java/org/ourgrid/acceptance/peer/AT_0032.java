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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
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

public class AT_0032 extends PeerAcceptanceTestCase {

	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests one worker and do not obtain it;
	 *    2. A remote peer provides a worker, which is allocated for the first local consumer;
	 *    3. The local consumer pauses the request;
	 *    4. Other local consumer requests one worker and do not obtain it;
	 *    5. The local consumer disposes the remote worker - expect the peer to allocate this worker for the second local consumer.
	 * 
	 */
	@ReqTest(test="AT-0032", reqs="REQ015, REQ112, REQ117")
	@Test public void test_AT_0032_BrokerReleasesRemoteWorkerAllocatedToAnotherBroker() throws Exception {
		// Create user accounts
		String userName1 = "user01";
		String userName2 = "user02";
		String serverName = "server011";
		String password = "011011";
		
		XMPPAccount user1 = req_101_Util.createLocalUser(userName1, serverName, password);
		XMPPAccount user2 = req_101_Util.createLocalUser(userName2, serverName, password);
		
		// Start Peer
		component = req_010_Util.startPeer();
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Logins
		String broker1PublicKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc1ID = req_108_Util.login(component, user1, broker1PublicKey);
		
		LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		String broker2PublicKey = "publicKeyB";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for the first user
		int request1ID = 1;
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);

		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc1),
				requestSpec);
		
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec(workerR1UserName, workerR1ServerName);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID rwmOID = req_018_Util.receiveRemoteWorker(component, 
				rwp, rwpID, workerSpecR1, workerR1PubKey, broker1PublicKey, future1).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpc2OD = new ObjectDeployment(component, lwpc1ID, AcceptanceTestUtil.getBoundObject(lwpc1ID));
		
		TestStub workerStubR1 = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec);
		
		// Pause the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		LocalWorkerProvider lwp= (LocalWorkerProvider)lwpOD.getObject();
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		lwp.pauseRequest(request1ID);
		
		// Request a worker for the second user
		int request2ID = 2;
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, "", 1, 0, 0);
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2, rwps);
			
		// The user disposes the worker after pausing the request - expect the peer
		// to allocate the worker for the second local consumer
		WorkerAllocation allocation = new WorkerAllocation(rwmOID);
		allocation.addLoserConsumer(lwpc1ID).addWinnerConsumer(lwpc2ID).addLoserRequestSpec(requestSpec);
		req_015_Util.localDisposeRemoteWorker(component, workerStubR1, allocation, false);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0032_BrokerReleasesRemoteWorkerAllocatedToAnotherBrokerWithJDL() throws Exception {
		// Create user accounts
		String userName1 = "user01";
		String userName2 = "user02";
		String serverName = "server011";
		String password = "011011";
		
		XMPPAccount user1 = req_101_Util.createLocalUser(userName1, serverName, password);
		XMPPAccount user2 = req_101_Util.createLocalUser(userName2, serverName, password);
		
		// Start Peer
		component = req_010_Util.startPeer();
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Logins
		String broker1PublicKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc1ID = req_108_Util.login(component, user1, broker1PublicKey);
		
		LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		String broker2PublicKey = "publicKeyB";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request a worker for the first user
		int request1ID = 1;
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements(null), 1, 0, 0);

		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc1),
				requestSpec);
		
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec(workerR1UserName, workerR1ServerName, null, null);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID rwmOID = req_018_Util.receiveRemoteWorker(component, 
				rwp, rwpID, workerSpecR1, workerR1PubKey, broker1PublicKey, future1).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpc2OD = new ObjectDeployment(component, lwpc1ID, AcceptanceTestUtil.getBoundObject(lwpc1ID));
		
		TestStub workerStubR1 = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, requestSpec);
		
		// Pause the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		LocalWorkerProvider lwp= (LocalWorkerProvider)lwpOD.getObject();
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		lwp.pauseRequest(request1ID);
		
		// Request a worker for the second user
		int request2ID = 2;
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, buildRequirements(null), 1, 0, 0);
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2, rwps);
			
		// The user disposes the worker after pausing the request - expect the peer
		// to allocate the worker for the second local consumer
		WorkerAllocation allocation = new WorkerAllocation(rwmOID);
		allocation.addLoserConsumer(lwpc1ID).addWinnerConsumer(lwpc2ID).addLoserRequestSpec(requestSpec);
		req_015_Util.localDisposeRemoteWorker(component, workerStubR1, allocation, false);
	}
}