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
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerInfo;
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

public class AT_0013 extends PeerAcceptanceTestCase {

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
	* Verify peer behavior after setWorkers command on 5 scenarios:
    * Old worker A: allocated and present in new workers list - Worker A continues allocated
    * Old worker B: not recovered and not present in new workers list - Peer unregister interest on Worker B recovery
    * Old worker C: recovered, not allocated and not present in new workers list - Peer unregister interest
    * on Worker C failure
    * Old worker D: allocated and not present in new workers list - Peer unregister interest on Worker D failure 
    * and command it to stop working
    * New worker E: Peer register interest on Worker E recovery
    */
	@ReqTest(test="AT-0013", reqs="")
	@Test public void test_AT_0013_WorkersLogin() throws Exception {
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Set a mock log
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login A, C and D
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S1");
		
		String workerAPublicKey = "workerApublicKey";
		String workerDPublicKey = "workerDPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDDeploymentID);
		
		//Change workers A and D status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerDDeploymentID);
		
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
	    
	    //Request two workers for the logged user
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    WorkerAllocation allocationWorkerD = new WorkerAllocation(workerDDeploymentID);
	    
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), spec, allocationWorkerD, allocationWorkerA);
	    assertTrue(req_025_Util.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
	    
	    //Change workers D status to ALLOCATED_FOR_BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerDDeploymentID, workerSpecD, spec);
	    
	    //Change workers A status to ALLOCATED_FOR_BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerADeploymentID, workerSpecA, spec);
	    
		//Worker C login
		String workerCPublicKey = "workerCpublicKey";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);
		
		//Change workers C status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		// Workers login A and E 
		String workerBPublicKey = "workerBPublicKey";
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		String workerEPublicKey = "workerEPublicKey";
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createClassAdWorkerSpec("U5", "S1", null, null);
		DeploymentID workerEDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEDeploymentID);
		
		WorkerManagement wmB = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerBDeploymentID);
		WorkerManagement wmD = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerDDeploymentID);
		EasyMock.reset(wmB);
		EasyMock.reset(wmD);
		
	    ObjectDeployment workerMOD = req_010_Util.getWorkerMonitorDeployment();
	    
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), workerMOD.getDeploymentID()));
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerCDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerDDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
		assertTrue(AcceptanceTestUtil.isInterested(component, workerEDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
		
		//Verify workers' status
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
        WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
        WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IDLE, null);
        WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.OWNER, null);
        WorkerInfo workerInfoE = new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null);
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoD, workerInfoC,
        		workerInfoB, workerInfoE);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0013_WorkersLoginWithJDL() throws Exception {
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		//Set a mock log
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login A, C and D
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S1", null, null);
		
		String workerAPublicKey = "workerApublicKey";
		String workerDPublicKey = "workerDPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDDeploymentID);
		
		//Change workers A and D status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerDDeploymentID);
		
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
	    
	    //Request two workers for the logged user
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    WorkerAllocation allocationWorkerD = new WorkerAllocation(workerDDeploymentID);
	    
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 2, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), spec, allocationWorkerD, allocationWorkerA);
	    assertTrue(req_025_Util.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
	    
	    //Change workers D status to ALLOCATED_FOR_BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerDDeploymentID, workerSpecD, spec);
	    
	    //Change workers A status to ALLOCATED_FOR_BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerADeploymentID, workerSpecA, spec);
	    
	    //Worker C login
		String workerCPublicKey = "workerCpublicKey";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);
		
		//Change workers C status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		// Workers login B and E 
		String workerBPublicKey = "workerBPublicKey";
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		String workerEPublicKey = "workerEPublicKey";
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createClassAdWorkerSpec("U5", "S1", null, null);
		DeploymentID workerEDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEDeploymentID);
		
		WorkerManagement wmB = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerBDeploymentID);
		WorkerManagement wmD = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerDDeploymentID);
		EasyMock.reset(wmB);
		EasyMock.reset(wmD);
		
	    ObjectDeployment workerMOD = req_010_Util.getWorkerMonitorDeployment();
	    
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), workerMOD.getDeploymentID()));
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerCDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
	    assertTrue(AcceptanceTestUtil.isInterested(component, workerDDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
		assertTrue(AcceptanceTestUtil.isInterested(component, workerEDeploymentID.getServiceID() , workerMOD.getDeploymentID()));
		
		//Verify workers' status
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
        WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.IN_USE, lwpcOID.getServiceID().toString());
        WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IDLE, null);
        WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.OWNER, null);
        WorkerInfo workerInfoE = new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null);
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoD, workerInfoC,
        		workerInfoB, workerInfoE);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
}