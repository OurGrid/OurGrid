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
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0023 extends PeerAcceptanceTestCase {

    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());

    @Before
	public void setUp() throws Exception {
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
	/**
	* Verifies if the peer commands the worker to stop working, because there is no more interest on the worker.
    */
	@ReqTest(test="AT-0023", reqs="")
	@Test public void test_AT_0023_BrokerDisposeIdleLocalWorker() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
	
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
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
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "", 1, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocationWorkerA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
	    
	    //Change worker status to ALLOCATED FOR BROKER
		Worker worker = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(
				component, lwpcOID, workerADeploymentID, workerSpecA, requestSpec1).getObject();
		
		//Pause the request
		req_117_Util.pauseRequest(component, lwpcOID, request1ID, null);
		
		//The user disposes the worker after pausing the request - expect to command the worker to stop working
		WorkerAllocation allocation = new WorkerAllocation(workerADeploymentID);
		allocation.addLoserConsumer(lwpcOID).addLoserRequestSpec(requestSpec1);
		req_015_Util.localConsumerDisposesLocalWorker(component, worker, allocation);
		
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADeploymentID.getServiceID()));
		
		//Verify if the worker A was marked as IDLE
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, null);
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//Verify if the client was marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.CONSUMING);
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		
		req_106_Util.getUsersStatus(usersInfo);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0023_BrokerDisposeIdleLocalWorkerWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011_1", "server011", "011011");
		
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
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
	    int request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements(null), 1, 0, 0);
	    WorkerAllocation allocationWorkerA = new WorkerAllocation(workerADeploymentID);
	    
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, allocationWorkerA);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
	    
	    //Change worker status to ALLOCATED FOR BROKER
		Worker worker = (Worker) req_025_Util.changeWorkerStatusToAllocatedForBroker(
				component, lwpcOID, workerADeploymentID, workerSpecA, requestSpec1).getObject();
		
		//Pause the request
		req_117_Util.pauseRequest(component, lwpcOID, request1ID, null);
		
		//The user disposes the worker after pausing the request - expect to command the worker to stop working
		WorkerAllocation allocation = new WorkerAllocation(workerADeploymentID);
		allocation.addLoserConsumer(lwpcOID).addLoserRequestSpec(requestSpec1);
		req_015_Util.localConsumerDisposesLocalWorker(component, worker, allocation);
		
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(lwpcOID.getServiceID()));
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnLocalWorker(workerADeploymentID.getServiceID()));
		
		//Verify if the worker A was marked as IDLE
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, null);
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//Verify if the client was marked as CONSUMING
		UserInfo userInfo1 = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.CONSUMING);
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
		
		req_106_Util.getUsersStatus(usersInfo);
	}
}