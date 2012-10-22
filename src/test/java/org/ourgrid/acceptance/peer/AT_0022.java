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

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_037_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
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

public class AT_0022 extends PeerAcceptanceTestCase {

	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_037_Util req_037_Util = new Req_037_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());

	/**
	 * Verify if the consumer peer performs a remote worker preemption.
	 */
	@ReqTest(test="AT-0022", reqs="")
	@Test public void test_AT_0022_2_RemoteWorkerPreemption() throws Exception{
	
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user22_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user22_2", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME, "dsPublicKey"), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client1 login and request workers after ds recovery - expect OG peer to query ds
		String broker1PubKey = "broker1PublicKey";
		
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
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider
		WorkerSpecification worker1Spec = workerAcceptanceUtil.createWorkerSpec("u1", "s1");
		WorkerSpecification worker2Spec = workerAcceptanceUtil.createWorkerSpec("u2", "s1");
		
		TestStub rwpStub = new Req_020_Util(getComponentContext()).receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", worker1Spec, worker2Spec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive two remote workers
		String worker1PubKey = "worker1PubKey";
		DeploymentID rwm1ID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, worker1Spec, worker1PubKey, broker1PubKey).getDeploymentID();		
		
		String worker2PubKey = "worker2PubKey";
		DeploymentID rwm2ID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, worker2Spec, worker2PubKey, broker1PubKey).getDeploymentID();		
		
		//Change worker status to ALLOCATED FOR BROKER
		LocalWorkerProviderClient lwpcOD = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		ObjectDeployment rwmcOD = req_018_Util.getRemoteWorkerManagementClientDeployment();
		RemoteWorkerManagementClient rwmc = req_018_Util.getRemoteWorkerManagementClient();
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, new ObjectDeployment(component, lwpc1OID, lwpcOD), rwmcOD, 
				rwm1ID, worker1Spec, requestSpec1);
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, new ObjectDeployment(component, lwpc1OID, lwpcOD), rwmcOD, 
				rwm2ID, worker2Spec, requestSpec1);
		
		//Client2 login and request a worker
		//Expect peer to command work for Broker on worker R2
		
		String broker2PubKey = "broker2PublicKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2OID = new Req_108_Util(getComponentContext()).login(component, user2, broker2PubKey);
		
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		WorkerAllocation allocationR2 = new WorkerAllocation(rwm2ID).addLoserConsumer(lwpc1OID).
			addRemoteWorkerManagementClient(rwmc).addLoserRequestSpec(requestSpec1);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, allocationR2);
		
		assertTrue(req_018_Util.isPeerInterestedOnRemoteWorker(rwm2ID.getServiceID()));
		
		//Change worker status to ALLOCATED FOR BROKER
		
		LocalWorkerProviderClient lwpcOD2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, new ObjectDeployment(component, lwpc2OID, lwpcOD2),
				rwmcOD, rwm2ID, worker2Spec, requestSpec2);
		
		AcceptanceTestUtil.publishTestObject(component, rwpStub.getDeploymentID(), rwpStub.getObject(),
				RemoteWorkerProvider.class);
		//Verify remote workers status
		req_037_Util.getRemoteWorkersStatus(
				new RemoteWorkerInfo(worker1Spec, rwpID.getServiceID().toString(), lwpc1OID.getServiceID().toString()),
				new RemoteWorkerInfo(worker2Spec, rwpID.getServiceID().toString(), lwpc2OID.getServiceID().toString()));
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0022_2_RemoteWorkerPreemptionWithJDL() throws Exception{
		
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user22_1", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user22_2", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME, "dsPublicKey"), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client1 login and request workers after ds recovery - expect OG peer to query ds
		String broker1PubKey = "broker1PublicKey";
		
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
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 2, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider
		WorkerSpecification worker1Spec = workerAcceptanceUtil.createClassAdWorkerSpec("u1", "s1", null, null);
		WorkerSpecification worker2Spec = workerAcceptanceUtil.createClassAdWorkerSpec("u2", "s1", null, null);
		
		TestStub rwpStub = new Req_020_Util(getComponentContext()).receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", worker1Spec, worker2Spec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive two remote workers
		String worker1PubKey = "worker1PubKey";
		DeploymentID rwm1ID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, worker1Spec, worker1PubKey, broker1PubKey).getDeploymentID();		
		
		String worker2PubKey = "worker2PubKey";
		DeploymentID rwm2ID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, worker2Spec, worker2PubKey, broker1PubKey).getDeploymentID();		
		
		//Change worker status to ALLOCATED FOR BROKER
		LocalWorkerProviderClient lwpcOD = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		ObjectDeployment rwmcOD = req_018_Util.getRemoteWorkerManagementClientDeployment();
		RemoteWorkerManagementClient rwmc = req_018_Util.getRemoteWorkerManagementClient();
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, new ObjectDeployment(component, lwpc1OID, lwpcOD), rwmcOD, 
				rwm1ID, worker1Spec, requestSpec1);
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, new ObjectDeployment(component, lwpc1OID, lwpcOD), rwmcOD, 
				rwm2ID, worker2Spec, requestSpec1);
		
		//Client2 login and request a worker
		//Expect peer to command work for Broker on worker R2
		
		String broker2PubKey = "broker2PublicKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2OID = new Req_108_Util(getComponentContext()).login(component, user2, broker2PubKey);
		
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, buildRequirements(null), 1, 0, 0);
		WorkerAllocation allocationR2 = new WorkerAllocation(rwm2ID).addLoserConsumer(lwpc1OID).
			addRemoteWorkerManagementClient(rwmc).addLoserRequestSpec(requestSpec1);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, allocationR2);
		
		assertTrue(req_018_Util.isPeerInterestedOnRemoteWorker(rwm2ID.getServiceID()));
		
		//Change worker status to ALLOCATED FOR BROKER
		
		LocalWorkerProviderClient lwpcOD2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, new ObjectDeployment(component, lwpc2OID, lwpcOD2),
				rwmcOD, rwm2ID, worker2Spec, requestSpec2);
		
		AcceptanceTestUtil.publishTestObject(component, rwpStub.getDeploymentID(), rwpStub.getObject(),
				RemoteWorkerProvider.class);
		//Verify remote workers status
		req_037_Util.getRemoteWorkersStatus(
				new RemoteWorkerInfo(worker1Spec, rwpID.getServiceID().toString(), lwpc1OID.getServiceID().toString()),
				new RemoteWorkerInfo(worker2Spec, rwpID.getServiceID().toString(), lwpc2OID.getServiceID().toString()));
	}
	
}
