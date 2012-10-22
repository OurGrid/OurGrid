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
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_112_Util;
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
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0021 extends PeerAcceptanceTestCase {

	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_112_Util req_112_Util = new Req_112_Util(getComponentContext());

	/**
	 * Verify if the consumer peer deliver the remote worker to Broker, 
	 * when the remote worker change its status to Allocated for Broker.
	 */
	@ReqTest(test="AT-0021", reqs="")
	@Test public void test_AT_0021_1_RemoteWorkerAllocatedForBroker() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME, "dsPublicKey"), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerPublicKey";
		
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
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "os = windows AND mem > 256", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("u1", "s1");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		//Remote worker provider client receive a remote worker
		TestStub remoteWorkerStub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpec, "workerPublicKey", brokerPubKey);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpec, remoteWorkerStub, lwpcOID, requestSpec);
		
		ObjectDeployment rmwOD = req_020_Util.getRemoteWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, remoteWorkerStub.getDeploymentID().getServiceID(), rmwOD.getDeploymentID()));
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0021_1_RemoteWorkerAllocatedForBrokerWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME, "dsPublicKey"), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		String brokerPubKey = "brokerPublicKey";
		
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
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(">", 256, "==", "windows"), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("u1", "s1", 512, "windows");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		//Remote worker provider client receive a remote worker
		TestStub remoteWorkerStub = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpec, "workerPublicKey", brokerPubKey);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpec, remoteWorkerStub, lwpcOID, requestSpec);
		
		ObjectDeployment rmwOD = req_020_Util.getRemoteWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, remoteWorkerStub.getDeploymentID().getServiceID(), rmwOD.getDeploymentID()));
	}
}