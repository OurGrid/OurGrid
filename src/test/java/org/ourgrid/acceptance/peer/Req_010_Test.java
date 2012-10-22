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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.matchers.ControlOperationResultMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleAlreadyStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleStoppedException;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


/**
 * @author melina
 * @author gustavopf
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
@ReqTest(reqs="REQ010")
public class Req_010_Test extends PeerAcceptanceTestCase {

	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
	private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
	
    @After
	public void tearDown() throws Exception {
		
		if (component != null && component.getContainerDAO().isStarted()) {
			req_010_Util.niceStopPeer(component);
		}
		
		super.tearDown();
	}

	/**
	 * Verify if the Peer's module was created and if Peer Control was bound.
	 */
	@ReqTest(test="AT-010.1", reqs="REQ010")
	@Test public void test_at_010_1_PeerCreation() throws Exception {
	    component = peerAcceptanceUtil.createPeerComponent(getComponentContext());
		
		assertTrue(isModuleStarted(component, PeerConstants.MODULE_NAME));
		assertTrue(isBound(component, Module.CONTROL_OBJECT_NAME, PeerControl.class));
		
	}
	
	/**
	 * Start the Peer and verify if:
     * Peer and Community obtainer's modules were created
     * Peer control, peer worker provider client, worker management client, 
     * worker manager, status, accounting, client monitor, provider monitor, 
     * ds monitor, lock manager, local worker provider and remote worker provider 
     * were bound on Peer's module
     * Community obtainer was bound on Community obtainer's module
	 */
	@ReqTest(test="AT-010.2", reqs="REQ010")
	@Test public void test_at_010_2_StartCommand() throws Exception {
	    component = req_010_Util.startPeer();
		verifyPeerModulesAndBinds();
	 }

	private void verifyPeerModulesAndBinds() {
		//Verify modules
		 assertTrue(isModuleStarted(component, PeerConstants.MODULE_NAME));

		 //Verify bound objects and types
		 assertTrue(isBound(component, Module.CONTROL_OBJECT_NAME, PeerControl.class));
		 assertTrue(isBound(component, PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT, 
				 RemoteWorkerProviderClient.class)); 
		 assertTrue(isBound(component, PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME, 
				 WorkerManagementClient.class)); 
		 assertTrue(isBound(component, PeerConstants.LOCAL_WORKER_PROVIDER, 
				 LocalWorkerProvider.class));
		 assertTrue(isBound(component, PeerConstants.REMOTE_WORKER_PROVIDER, RemoteWorkerProvider.class));
		 
		 assertTrue(isBound(component, PeerConstants.DS_CLIENT, 
				 DiscoveryServiceClient.class));
	}
	 
    /**
	 *  Execute start command twice, receive a peer already starter operation
	 *  result and verify if the created modules and 
	 *  bound objects weren't affected.
	 */
	@ReqTest(test="AT-010.4", reqs="REQ010")
	@Test public void test_at_010_4_StartCommandTwice() throws Exception{
        component = req_010_Util.startPeer();

		//Create mock and get bound object
		PeerControlClient peerControlClientMock = EasyMock.createMock(PeerControlClient.class);
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		 
		//Begin mock record
		resetActiveMocks();
		peerControlClientMock.operationSucceed(
				ControlOperationResultMatcher.eqType(ModuleAlreadyStartedException.class));
		EasyMock.replay(peerControlClientMock);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "pccServer", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClientMock, PeerControlClient.class);
		
		//Start peer again
		peerControl.start(peerControlClientMock);
		
		//Verify mock calls - Peer must return an error to client
		EasyMock.verify(peerControlClientMock);

		verifyPeerModulesAndBinds();
	}
	 
	/**
	 * Verify Peer's modules destruction and if the client received an 
	 * operation succeed result
	 */
	@ReqTest(test="AT-010.7", reqs="REQ010")
	@Test public void test_at_010_7_stopCommand() throws Exception{
		component = req_010_Util.startPeer();

		//Create mock and get bound object
		PeerControlClient peerControlClientMock = EasyMock.createMock(PeerControlClient.class);
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		 
		//Begin mock record
		resetActiveMocks();
		peerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
		EasyMock.replay(peerControlClientMock);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "pccServer", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClientMock, PeerControlClient.class);
		//Stop peer
		peerControl.stop(false, false, peerControlClientMock);
		
		//Verify mock calls - Peer must return an operation succeeded result
		EasyMock.verify(peerControlClientMock);
		EasyMock.reset(peerControlClientMock);

		//Verify modules destruction
//TODO		assertFalse(isModuleStarted(component, PeerConstants.MODULE_NAME));
	}
	 
    /**
     * Test the Peer's states and transitions:
     * setWorkers before start - error: peer not initialized
     * stop before start - error: peer not initialized
     * start twice - error: peer already started
     * setWorkers after stop - error: peer finalized
     * stoptwice - error: peer finalized
     * start after stop - error: peer finalized
     */
	@ReqTest(test="AT-010.8", reqs="REQ010")
    @Test public void test_at_010_8_stateMachine() throws Exception {
		component = peerAcceptanceUtil.createPeerComponent(getComponentContext());
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment peerControlDeployment = peerAcceptanceUtil.getPeerControlDeployment();

		//Create mocks
		PeerControlClient peerControlClientMock = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("peerClient", "peerClientServer", "broker"), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClientMock, PeerControlClient.class);
		
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpecMock(new ServiceID("U1", "S1", "M1", "W1"));
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpecMock(new ServiceID("U2", "S2", "M2", "W2"));
		List<WorkerSpecification> workers = new LinkedList<WorkerSpecification>();
		workers.add(workerSpec1);
		workers.add(workerSpec2);

//		//setWorkers before start - error: peer not initialized 
//		EasyMock.reset(peerControlClientMock);
//		peerControlClientMock.operationSucceed(
//				ControlOperationResultMatcher.eqType(ModuleNotStartedException.class));
//		EasyMock.replay(peerControlClientMock);
//		peerControl.setWorkers(peerControlClientMock, workers);
//		EasyMock.verify(peerControlClientMock);

		//stop before start - error: peer not initialized 
		EasyMock.reset(peerControlClientMock);
		peerControlClientMock.operationSucceed(
				ControlOperationResultMatcher.eqType(ModuleNotStartedException.class)); 
		EasyMock.replay(peerControlClientMock);
		AcceptanceTestUtil.setExecutionContext(component, peerControlDeployment, 
				peerControlDeployment.getDeploymentID().getPublicKey());
		peerControl.stop(false, false, peerControlClientMock); 
		EasyMock.verify(peerControlClientMock);

		//start twice - no error / error: peer already started 
		EasyMock.reset(peerControlClientMock);
		peerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
		peerControlClientMock.operationSucceed(
				ControlOperationResultMatcher.eqType(ModuleAlreadyStartedException.class));
		EasyMock.replay(peerControlClientMock);
		peerControl.start(peerControlClientMock);
		peerControl.start(peerControlClientMock);
		EasyMock.verify(peerControlClientMock);

		//stop after start - no error
		EasyMock.reset(peerControlClientMock);
		peerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
		EasyMock.replay(peerControlClientMock);
		peerControl.stop(false, false, peerControlClientMock);
		EasyMock.verify(peerControlClientMock);
		
//		//setWorkers after stop - error: peer finalized 
//		EasyMock.reset(peerControlClientMock);
//		peerControlClientMock.operationSucceed(
//				ControlOperationResultMatcher.eqType(ModuleStoppedException.class)); 
//		EasyMock.replay(peerControlClientMock);
//		peerControl.setWorkers(peerControlClientMock, workers); 
//		EasyMock.verify(peerControlClientMock);

		//stop after stop - error: peer finalized 
		EasyMock.reset(peerControlClientMock);
		peerControlClientMock.operationSucceed(
				ControlOperationResultMatcher.eqType(ModuleStoppedException.class));
		EasyMock.replay(peerControlClientMock);
		peerControl.stop(false, false, peerControlClientMock);
		EasyMock.verify(peerControlClientMock);

		//start after stop - error: peer finalized 
		EasyMock.reset(peerControlClientMock);
		peerControlClientMock.operationSucceed(
				ControlOperationResultMatcher.eqType(ModuleStoppedException.class));
		EasyMock.replay(peerControlClientMock);
		peerControl.start(peerControlClientMock);
		EasyMock.verify(peerControlClientMock);

	}

    
    /**
	 * Verify WorkerSpec compilation on 4 scenarios:
	 * File 1: WorkerSpec A: only workerdefaults properties
	 * File 2: WorkerSpec B: workerdefaults and worker properties
	 * File 2: WorkerSpec C: workerdefaults and worker properties, 1 worker 
	 * property overriding workerdefaults property
	 * File 3: WorkerSpec D: only worker properties
	 */
	@ReqTest(test="AT-010.10", reqs="REQ010")
	@Category(JDLCompliantTest.class) @Test public void test_at_010_10_compileWorkerSpecsWithJDL() throws Exception {
		component = req_010_Util.startPeer();
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE1 );
		
		assertTrue(1 == specs.size());
		WorkerSpecification workerSpecA = specs.get(0);
		
		assertEquals("testserver", workerSpecA.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("testuser", workerSpecA.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("linux", workerSpecA.getAttribute(OurGridSpecificationConstants.ATT_OS));
		
		specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE2 );
		
		assertTrue(2 == specs.size());
		WorkerSpecification workerSpecB = specs.get(0);
		WorkerSpecification workerSpecC = specs.get(1);
		
		assertEquals("testserver", workerSpecB.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("testuserB", workerSpecB.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("linux", workerSpecB.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("128", workerSpecB.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		
		assertEquals("testserver", workerSpecC.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("testuserC", workerSpecC.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("windows", workerSpecC.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("256", workerSpecC.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		
		specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE3 );
		
		assertTrue(1 == specs.size());
		WorkerSpecification workerSpecD = specs.get(0);
	
		assertEquals("testserver", workerSpecD.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("testuser", workerSpecD.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("linux", workerSpecD.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("1024", workerSpecD.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
	}

	/**
     * Verify peer recovery interest on the logged workers.
     */
	@ReqTest(test="AT-010.11", reqs="REQ010")
    @Test public void test_at_010_11_SetWorkers() throws Exception{
    	//Start peer
		component = req_010_Util.startPeer();
		
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("testuserB", "testserver");
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerBDeploymentID);
		
		workerSpec = workerAcceptanceUtil.createWorkerSpec("testuserC", "testserver");
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerCDeploymentID);
		
		//Get Worker Monitor Object
		ObjectDeployment workerMonitorDeployment = peerAcceptanceUtil.getWorkerMonitorDeployment();
		
		//Verify if the monitor is recovery interested on the workers
		AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID(), workerMonitorDeployment.getDeploymentID());
		AcceptanceTestUtil.isInterested(component, workerCDeploymentID.getServiceID(), workerMonitorDeployment.getDeploymentID());
    }
	
	
	/**
     * Verify peer recovery interest on the logged workers.
     */
	@ReqTest(test="AT-010.11", reqs="REQ010")
    @Category(JDLCompliantTest.class) @Test public void test_at_010_11_SetWorkersWithJDL() throws Exception{
    	//Start peer
		component = req_010_Util.startPeer();
		
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("testuserB", "testserver");
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerBDeploymentID);
		
		workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("testuserC", "testserver");
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerCDeploymentID);
		
		//Get Worker Monitor Object
		ObjectDeployment workerMonitorDeployment = peerAcceptanceUtil.getWorkerMonitorDeployment();
		
		//Verify if the monitor is recovery interested on the workers
		AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID(), workerMonitorDeployment.getDeploymentID());
		AcceptanceTestUtil.isInterested(component, workerCDeploymentID.getServiceID(), workerMonitorDeployment.getDeploymentID());
    }
	
	
	// ===================== Qualification Tests ============================================
	
	
	/**
	 * This test verify if a Peer Without a configuration isn't created.
	 * @author gustavopf
	 * @author melina
	 * @Date: 14/04/08
	 * @throws Exception
	 */
	@ReqTest(test="AT-010.1", reqs="REQ010")
	@Test public void test_at_010_1_PeerCreationWithoutConfiguration() throws Exception {
		//component = req_010_Util.startPeer();
		
		String errorMessage = "The container context is mandatory";
		
		try {
			peerAcceptanceUtil.createPeerComponent(null);
			fail("Peer shoudn't be created without a configuration.");
		} catch (IllegalArgumentException e) {
			assertEquals(errorMessage, e.getMessage());
		}
		assertFalse(isModuleStarted(component, PeerConstants.MODULE_NAME));
		assertFalse(isBound(component, Module.CONTROL_OBJECT_NAME, PeerControl.class));
		
	}
}