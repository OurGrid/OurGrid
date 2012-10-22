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

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0014 extends PeerAcceptanceTestCase {

	private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());

    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
	/**
	* Verify WorkerSpec update after setWorkers command on 3 scenarios:
    * Old worker A: allocated and present in new workers list
    * Old worker B: not recovered and present in new workers list
    * Old worker C: recovered, not allocated and present in new workers list
	* The client received an operation succeded result
    */
	@ReqTest(test="AT-0014", reqs="")
	@Test public void test_AT_0014_WorkersLoginWithSpecChange() throws Exception {
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Set a mock log
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login A, B and C
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBpublicKey";
		String workerCPublicKey = "workerCPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);

		//Change workers A and C status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		//Login with a valid user
	    String brokerPubKey = "publicKeyA";
	    
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
		
	    //Request a worker for the logged user
	    WorkerAllocation allocationWorkerC = new WorkerAllocation(workerCDeploymentID);
	    
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), spec, allocationWorkerC);
	    assertTrue(req_019_Util.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
	   
	    //Change workers C status to ALLOCATED_FOR_BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerCDeploymentID, workerSpecC, spec);
	    
	    //Set new workers A, B and C
		WorkerSpecification newWorkerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		newWorkerSpecA.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		WorkerSpecification newWorkerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		newWorkerSpecB.putAttribute(OurGridSpecificationConstants.ATT_OS, "linux");
		WorkerSpecification newWorkerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		newWorkerSpecC.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		
		workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, newWorkerSpecA, workerAPublicKey);
		req_010_Util.workerLoginAgain(component, newWorkerSpecA, workerADeploymentID);
		
		workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, newWorkerSpecB, workerBPublicKey);
		req_010_Util.workerLoginAgain(component, newWorkerSpecB, workerBDeploymentID);

		workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, newWorkerSpecC, workerCPublicKey);
		req_010_Util.workerLoginAgain(component, newWorkerSpecC, workerCDeploymentID);
		
		//Verify workers' status
        WorkerInfo workerInfoA = new WorkerInfo(newWorkerSpecA, LocalWorkerState.IDLE, null);
        WorkerInfo workerInfoB = new WorkerInfo(newWorkerSpecB, LocalWorkerState.OWNER, null);
        WorkerInfo workerInfoC = new WorkerInfo(newWorkerSpecC, LocalWorkerState.IN_USE, lwpcOID.getContainerID().getUserAtServer());
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC, workerInfoA, workerInfoB);
        
        req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		ObjectDeployment workerMOD = req_010_Util.getWorkerMonitorDeployment();
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), workerMOD.getDeploymentID()));
		assertTrue(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
		assertTrue(AcceptanceTestUtil.isInterested(component, workerCDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
		
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0014_WorkersLoginWithSpecChangeWithJDL() throws Exception {
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Set a mock log
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login A, B and C
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", 512, null);
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBpublicKey";
		String workerCPublicKey = "workerCPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);
		
		//Change workers A and C status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		//Login with a valid user
	    String brokerPubKey = "publicKeyA";
	    
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
		
	    //Request a worker for the logged user
	    WorkerAllocation allocationWorkerC = new WorkerAllocation(workerCDeploymentID);
	    
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), spec, allocationWorkerC);
	    assertTrue(req_019_Util.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
	   
	    //Change workers C status to ALLOCATED_FOR_BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerCDeploymentID, workerSpecC, spec);
	    
	    //Set new workers A, B and C
		WorkerSpecification newWorkerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, "windows");
		WorkerSpecification newWorkerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, "linux");
		WorkerSpecification newWorkerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", 512, null);
		
		workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, newWorkerSpecA, workerAPublicKey);
		req_010_Util.workerLoginAgain(component, newWorkerSpecA, workerADeploymentID);
		
		workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, newWorkerSpecB, workerBPublicKey);
		req_010_Util.workerLoginAgain(component, newWorkerSpecB, workerBDeploymentID);

		workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, newWorkerSpecC, workerCPublicKey);
		req_010_Util.workerLoginAgain(component, newWorkerSpecC, workerCDeploymentID);
		
		//Verify workers' status
        WorkerInfo workerInfoA = new WorkerInfo(newWorkerSpecA, LocalWorkerState.IDLE, null);
        WorkerInfo workerInfoB = new WorkerInfo(newWorkerSpecB, LocalWorkerState.OWNER, null);
        WorkerInfo workerInfoC = new WorkerInfo(newWorkerSpecC, LocalWorkerState.IN_USE, lwpcOID.getContainerID().getUserAtServer());
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoC, workerInfoA, workerInfoB);
        
        req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		ObjectDeployment workerMOD = req_010_Util.getWorkerMonitorDeployment();
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), workerMOD.getDeploymentID()));
		assertTrue(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
		assertTrue(AcceptanceTestUtil.isInterested(component, workerCDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
	}
}