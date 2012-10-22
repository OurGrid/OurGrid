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
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ112")
public class Req_112_Test extends PeerAcceptanceTestCase {

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
	 * Verify if the consumer peer ignore unknown remote workers and 
	 * null remote workers, on status change "Allocated for Broker".
	 * @throws Exception
	 */
	@ReqTest(test="AT-112.1", reqs="REQ112")
	@Test public void test_AT_112_1_InputValidation() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Change the unknown worker status to ALLOCATED FOR BROKER
		String workerPublicKey = "publicKey1";
		
		loggerMock.warn("An unknown worker changed its status to Allocated for Broker. It will be ignored. " +
				"Worker public key: " + workerPublicKey);
		EasyMock.replay(loggerMock);
		
		req_112_Util.unknownRemoteWorkerStatusChanged("a", "b", workerPublicKey, component);

		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
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
		
		/*LocalWorkerProviderClient lwpc = getMock(NOT_NICE, LocalWorkerProviderClient.class);
		peerAcceptanceUtil.createStub(lwpc, lwpcOID);*/

		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "os = windows AND mem > 256", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec);
		
		//GIS client receive a remote worker provider
		WorkerSpecification unknownWorkerSpec = workerAcceptanceUtil.createWorkerSpec("1", "2");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp", 
				"rwpS", "rwpPublicKey", unknownWorkerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Change the unknown worker status to ALLOCATED FOR BROKER again
		loggerMock.warn("An unknown worker changed its status to Allocated for Broker. " +
				"It will be ignored. Worker public key: "+workerPublicKey);
		EasyMock.replay(loggerMock);
		req_112_Util.unknownRemoteWorkerStatusChanged(unknownWorkerSpec, workerPublicKey, component);
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Remote worker provider client receive a remote worker
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		TestStub remoteWorkerStub = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpec, workerPublicKey, brokerPubKey);

		//Change the worker status to ALLOCATED FOR BROKER without a worker reference
		EasyMock.replay(loggerMock);
		req_112_Util.remoteWorkerStatusChanged(component, remoteWorkerStub);
		EasyMock.verify(loggerMock);
		
		ObjectDeployment rwmOD = peerAcceptanceUtil.getRemoteWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, 
				remoteWorkerStub.getDeploymentID().getServiceID(), rwmOD.getDeploymentID()));	
	}

	/**
	 * Verify if the consumer peer ignore unknown remote workers and 
	 * null remote workers, on status change "Allocated for Broker".
	 * @throws Exception
	 */
	@ReqTest(test="AT-112.1", reqs="REQ112")
	@Category(JDLCompliantTest.class) @Test public void test_AT_112_1_InputValidationWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Change the unknown worker status to ALLOCATED FOR BROKER
		String workerPublicKey = "publicKey1";
		
		loggerMock.warn("An unknown worker changed its status to Allocated for Broker. It will be ignored. " +
				"Worker public key: " + workerPublicKey);
		EasyMock.replay(loggerMock);
		
		req_112_Util.unknownRemoteWorkerStatusChanged("a", "b", workerPublicKey, component);
	
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
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
		
		/*LocalWorkerProviderClient lwpc = getMock(NOT_NICE, LocalWorkerProviderClient.class);
		peerAcceptanceUtil.createStub(lwpc, lwpcOID);*/
	
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements( ">", 256, "==", "windows" ), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec);
		
		//GIS client receive a remote worker provider
		WorkerSpecification unknownWorkerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("1", "2", null, null);
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp", 
				"rwpS", "rwpPublicKey", unknownWorkerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Change the unknown worker status to ALLOCATED FOR BROKER again
		loggerMock.warn("An unknown worker changed its status to Allocated for Broker. " +
				"It will be ignored. Worker public key: "+workerPublicKey);
		EasyMock.replay(loggerMock);
		req_112_Util.unknownRemoteWorkerStatusChanged(unknownWorkerSpec, workerPublicKey, component);
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Remote worker provider client receive a remote worker
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", 512, "windows");
		TestStub remoteWorkerStub = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpec, workerPublicKey, brokerPubKey);
	
		//Change the worker status to ALLOCATED FOR BROKER without a worker reference
		EasyMock.replay(loggerMock);
		req_112_Util.remoteWorkerStatusChanged(component, remoteWorkerStub);
		EasyMock.verify(loggerMock);
		
		ObjectDeployment rwmOD = peerAcceptanceUtil.getRemoteWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, 
				remoteWorkerStub.getDeploymentID().getServiceID(), rwmOD.getDeploymentID()));	
	}
}