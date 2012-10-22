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

public class AT_0028 extends PeerAcceptanceTestCase {

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
	 * Verifies if the peer ignores a remote worker status change, when it had
	 * already been disposed.
	 */
	@ReqTest(test="AT-0028", reqs="REQ015, REQ112, REQ117")
	@Test public void test_AT_0028_RemoteWorkerChangesStateAfterBeingDisposed() throws Exception {
		// Create an user account
		String userName = "user011";
		String serverName = "server011";
		String password = "011011";
		XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);
		
		// Start Peer
		component = req_010_Util.startPeer();
		
		// Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME, "dsPublicKey"), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Client login
		String brokerPublicKey = "brokerPublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
			
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request workers after ds recovery - expect OG peer to query ds
		int requestID = 1;
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "os = windows AND mem > 256", 1, 0, 0);
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), spec);
		
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec(workerR1UserName, workerR1ServerName);
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
				
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, spec, "rwpUser", "rwpServer", 
				"rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID rwmOID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1,
				workerR1PubKey, brokerPublicKey, future).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		
		TestStub workerR1Stub = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, spec);
		
		// Pause the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		LocalWorkerProvider lwp = (LocalWorkerProvider) lwpOD.getObject();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpcID);
		lwp.pauseRequest(requestID);
		
		// The user disposes the worker after pausing the request - expect the peer to dispose the remote worker
		WorkerAllocation allocation = new WorkerAllocation(rwmOID).addLoserRequestSpec(spec).addLoserConsumer(lwpcID);
		req_015_Util.localDisposeRemoteWorker(component, workerR1Stub, allocation, rwp, rwpID, false);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(rwmOID.getServiceID()));
		
		// The worker change its status - expect the peer to ignore this message
		EasyMock.reset(loggerMock);
		loggerMock.warn("An unknown worker changed its status to Allocated for Broker. " +
					"It will be ignored. Worker public key: " + workerR1Stub.getDeploymentID().getPublicKey());
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), workerR1Stub.getDeploymentID());
		
		peerAcceptanceUtil.getRemoteWorkerManagementClient().statusChangedAllocatedForBroker(workerR1Stub.getDeploymentID().getServiceID());
		
		EasyMock.verify(loggerMock);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0028_RemoteWorkerChangesStateAfterBeingDisposedWithJDL() throws Exception {
		// Create an user account
		String userName = "user011";
		String serverName = "server011";
		String password = "011011";
		XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);
		
		// Start Peer
		component = req_010_Util.startPeer();
		
		// Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME, "dsPublicKey"), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Client login
		String brokerPublicKey = "brokerPublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
			
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request workers after ds recovery - expect OG peer to query ds
		int requestID = 1;
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(">", 256, "==", "windows"), 1, 0, 0);
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), spec);
		
		// GIS client receives a remote worker provider
		String workerR1UserName = "workerR1.ourgrid.org";
		String workerR1ServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec(workerR1UserName, workerR1ServerName, 512, "windows");
				
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, spec, "rwpUser", "rwpServer", 
				"rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives a remote worker
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID rwmOID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1,
				workerR1PubKey, brokerPublicKey, future).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		
		TestStub workerR1Stub = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmOID, workerSpecR1, spec);
		
		// Pause the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		LocalWorkerProvider lwp = (LocalWorkerProvider) lwpOD.getObject();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpcID);
		lwp.pauseRequest(requestID);
		
		// The user disposes the worker after pausing the request - expect the peer to dispose the remote worker
		WorkerAllocation allocation = new WorkerAllocation(rwmOID).addLoserRequestSpec(spec).addLoserConsumer(lwpcID);
		req_015_Util.localDisposeRemoteWorker(component, workerR1Stub, allocation, rwp, rwpID, false);
		assertFalse(peerAcceptanceUtil.isPeerInterestedOnRemoteWorker(rwmOID.getServiceID()));
		
		// The worker change its status - expect the peer to ignore this message
		EasyMock.reset(loggerMock);
		loggerMock.warn("An unknown worker changed its status to Allocated for Broker. " +
					"It will be ignored. Worker public key: " + workerR1Stub.getDeploymentID().getPublicKey());
		EasyMock.replay(loggerMock);
		
		AcceptanceTestUtil.setExecutionContext(component, peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), workerR1Stub.getDeploymentID());
		
		peerAcceptanceUtil.getRemoteWorkerManagementClient().statusChangedAllocatedForBroker(workerR1Stub.getDeploymentID().getServiceID());
		
		EasyMock.verify(loggerMock);
	}
}