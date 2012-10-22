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

import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
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

/**
 * It intends to assert Condor matchmaking capabilities.
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
@ReqTest(reqs="REQ111")
public class Req_111_new_Test extends PeerAcceptanceTestCase {
	
	private PeerComponent component; 
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());
    
	@Test public void test_AT_111_1_simpleOperators() throws Exception {
		//Creating user's account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");
		
		//Start the peer
		component = req_010_Util.startPeer();
		
		//Login with a valid user
		String brokerPublicKey = "publicKeyA";
		
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
		
		//Worker server
		String workerServer = "xmpp.ourgrid.org";
			
		//Workers login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("worker0", workerServer);
		DeploymentID worker0ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker0PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker0ID);
		
		workerSpec = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker1ID);
		 
		workerSpec = workerAcceptanceUtil.createWorkerSpec("worker2", workerServer);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker2ID);
		
		workerSpec = workerAcceptanceUtil.createWorkerSpec("worker3", workerServer);
		DeploymentID worker3ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker3PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker3ID);
		
		//Changing workers status to idle
		req_025_Util.changeWorkerStatusToIdle(component, worker0ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker3ID);
		
		//Request workers with memory size lower than 512
		requestWorkers(component, 1, "mem < 512", 3, lwpcID, worker1ID);
		
		//Recovering worker4 to replace worker1
		DeploymentID worker4ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker4", workerServer) , "worker4PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker4ID);
		
		//Requesting workers with memory size greater than 512
		requestWorkers(component,2, "mem > 512", 3, lwpcID, worker3ID);
		
		//Recovering worker5 to replace worker3
		DeploymentID worker5ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker5", workerServer) , "worker5PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker5ID);
		
		//Requesting workers with memory size lower or equals 512
		requestWorkers(component, 3, "mem <= 512", 3, lwpcID, worker2ID, worker4ID);
		
		//Recovering worker6 and worker7 to replace worker2 and worker4
		DeploymentID worker6ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker6", workerServer) , "worker6PublicKey");
		DeploymentID worker7ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker7", workerServer) , "worker7PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker6ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker7ID);
		
		//Requesting workers with memory size greater or equals 512
		requestWorkers(component, 4, "mem >= 512", 3, lwpcID, worker5ID, worker7ID);
		
		//Recovering worker8 and worker9 to replace worker5 and worker7
		DeploymentID worker8ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker8", workerServer) , "worker8PublicKey");
		DeploymentID worker9ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker9", workerServer) , "worker9PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker8ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker9ID);
		
		//Requesting workers with memory size equals 256
		requestWorkers(component, 5, "mem = 256", 3, lwpcID, worker6ID);
		
		//Recovering worker10 to replace worker6
		DeploymentID worker10ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker10", workerServer) , "worker10PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker10ID);
		
		//Requesting workers with memory size equals 1024
		requestWorkers(component, 6, "mem == 1024", 3, lwpcID, worker9ID);
		
		//Recovering worker11 to replace worker9
		DeploymentID worker11ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker11", workerServer) , "worker11PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker11ID);

		//Requesting workers with memory size not equals 512
		requestWorkers(component, 7, "mem != 512", 3, lwpcID, worker10ID, worker11ID);
		
		//Recovering worker12 and worker13 to replace worker10 and worker11
		DeploymentID worker12ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker12", workerServer) , "worker12PublicKey");
		DeploymentID worker13ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker13", workerServer) , "worker13PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker12ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker13ID);
			
		//Requesting workers with os equals linux
		requestWorkers(component, 8, "OS = linux", 3, lwpcID, worker8ID, worker12ID);
		
		//Recovering worker14 and worker15 to replace worker8 and worker12
		DeploymentID worker14ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker14", workerServer) , "worker14PublicKey");
		DeploymentID worker15ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker15", workerServer) , "worker15PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker14ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker15ID);
					
		//Requesting workers with os equals windows
		requestWorkers(component, 9, "OS = windows", 3, lwpcID, worker13ID);
		DeploymentID worker16ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker16", workerServer) , "worker16PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker16ID);
		
		//Requesting workers with os not equals linux
		requestWorkers(component, 10, "OS != linux", 3, lwpcID, worker16ID);
	}

	private void requestWorkers(PeerComponent component, int requestID, String requirements, int requiredWorkers, DeploymentID lwpcID, 
			DeploymentID... workerIDs) {
		
		WorkerAllocation[] allocations = new WorkerAllocation[workerIDs.length];
		int counter = 0;
		for (DeploymentID DeploymentID : workerIDs) {
			allocations[counter] = new WorkerAllocation(DeploymentID);
			counter++;
		}
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, requirements, requiredWorkers, 0, 0);
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec, allocations);
		
		req_117_Util.pauseRequest(component, lwpcID, requestID, future);
	}
	
	@Test public void test_AT_111_2_negationOperators() throws Exception {
		//Creating user's account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");
		
		//Start the peer
		component = req_010_Util.startPeer();
		
		//Login with a valid user
		String brokerPublicKey = "publicKeyA";
		
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
		
		//Worker server
		String workerServer = "xmpp.ourgrid.org";
			
		//Workers login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("worker0", workerServer);
		DeploymentID worker0ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker0PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker0ID);
		
		workerSpec = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker1ID);
		 
		workerSpec = workerAcceptanceUtil.createWorkerSpec("worker2", workerServer);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker2ID);
		
		workerSpec = workerAcceptanceUtil.createWorkerSpec("worker3", workerServer);
		DeploymentID worker3ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker3PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker3ID);
		
		
		//Change workers status to idle
		req_025_Util.changeWorkerStatusToIdle(component, worker0ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker3ID);
		
		//Request workers with memory size lower than 512
		requestWorkers(component, 1, "NOT (mem >= 512)", 3, lwpcID, worker1ID);
		
		//Recovering worker4 to replace worker1
		DeploymentID worker4ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker4", workerServer) , "worker4PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker4ID);
		
		//Request workers with memory size greater than 512
		requestWorkers(component, 2, "!(mem <= 512)", 3, lwpcID, worker3ID);
		
		//Recovering worker5 to replace worker3
		DeploymentID worker5ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker5", workerServer) , "worker5PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker5ID);
		
		//Request workers with memory size lower or equals 512
		requestWorkers(component, 3, "NOT(mem > 512)", 3, lwpcID, worker2ID, worker4ID);
		
		//Recovering worker6 and worker7 to replace worker2 and worker4
		DeploymentID worker6ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker6", workerServer) , "worker6PublicKey");
		DeploymentID worker7ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker7", workerServer) , "worker7PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker6ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker7ID);
		
		//Request workers with memory size greater or equals 512
		requestWorkers(component, 4, "!(mem < 512)", 3, lwpcID, worker5ID, worker7ID);
		
		//Recovering worker8 and worker9 to replace worker5 and worker7
		DeploymentID worker8ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker8", workerServer) , "worker8PublicKey");
		DeploymentID worker9ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker9", workerServer) , "worker9PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker8ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker9ID);
		
		//Request workers with memory size equals 256
		requestWorkers(component, 5, "NOT(mem != 256)", 3, lwpcID, worker6ID);
		
		//Recovering worker10 to replace worker6
		DeploymentID worker10ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker10", workerServer) , "worker10PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker10ID);
		
		//Request workers with memory size equals 1024
		requestWorkers(component, 6, "!(mem != 1024)", 3, lwpcID, worker9ID);
		
		//Recovering worker11 to replace worker9
		DeploymentID worker11ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker11", workerServer) , "worker11PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker11ID);

		//Request workers with memory size not equals 512
		requestWorkers(component, 7, "!(mem = 512)", 3, lwpcID, worker10ID, worker11ID);
		
		//Recovering worker12 and worker 13 to replace worker10 and worker11
		DeploymentID worker12ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker12", workerServer) , "worker12PublicKey");
		DeploymentID worker13ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker13", workerServer) , "worker13PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker12ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker13ID);
			
		//Request workers with os equals linux
		requestWorkers(component, 8, "NOT(OS != linux)", 3, lwpcID, worker8ID, worker12ID);
		
		//Recovering worker14 and worker15 to replace worker8 and worker12
		DeploymentID worker14ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker14", workerServer) , "worker14PublicKey");
		DeploymentID worker15ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker15", workerServer) , "worker15PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker14ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker15ID);
					
		//Request workers with os equals windows
		requestWorkers(component, 9, "!(OS != windows)", 3, lwpcID, worker13ID);
		
		//Recovering worker16 to replace worker13
		DeploymentID worker16ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker16", workerServer) , "worker16PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker16ID);
		
		//Request workers with os not equals linux
		requestWorkers(component, 10, "NOT(OS = linux)", 3, lwpcID, worker16ID);
	}
	
	@Test public void test_AT_111_3_unionAndIntersectionOperators() throws Exception {
		//Creating user's account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");
		
		//Starting peer
		component = req_010_Util.startPeer();
		
		//Login with a valid user
		String brokerPublicKey = "publicKeyA";
		
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
		
		//Worker server
		String workerServer = "xmpp.ourgrid.org";
			
		//Workers login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("worker0", workerServer);
		DeploymentID worker0ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker0PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker0ID);
		
		workerSpec = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker1ID);
		 
		workerSpec = workerAcceptanceUtil.createWorkerSpec("worker2", workerServer);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker2ID);
		
		workerSpec = workerAcceptanceUtil.createWorkerSpec("worker3", workerServer);
		DeploymentID worker3ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker3PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker3ID);
		
		
		//Changing workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker0ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker3ID);
		

		//Requesting workers with memory size greater than 256 AND OS equals Windows
		requestWorkers(component, 1, "mem > 256 && OS = windows", 3, lwpcID, worker3ID);
		
		//Recovering worker4 to replace worker3
		DeploymentID worker4ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker4", workerServer) , "worker4PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker4ID);
		
		//Requesting workers with memory size lower than 512 AND OS equals Linux
		requestWorkers(component, 2, "mem < 512 AND OS == linux", 3, lwpcID, worker1ID);
		
		//Recovering worker5 to replace worker1
		DeploymentID worker5ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker5", workerServer) , "worker5PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker5ID);
		
		//Requesting workers with memory size greater or equals 512 OR OS equals Windows
		requestWorkers(component, 3, "mem >= 512 || OS == windows", 3, lwpcID, worker2ID, worker4ID);
		
		//Recovering worker6 and worker7 to replace worker2 and worker4
		DeploymentID worker6ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker6", workerServer) , "worker6PublicKey");
		DeploymentID worker7ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker7", workerServer) , "worker7PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker6ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker7ID);
	
		//Requesting workers with memory size lower than 512 OR OS equals Linux
		requestWorkers(component, 4, "mem < 512 OR OS = linux", 3, lwpcID, worker5ID, worker6ID);
	}
	
	@Test public void test_AT_111_4_operatorsPrecedence() throws Exception {
		//Creating an user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");
		
		//Starting peer
		component = req_010_Util.startPeer();
		
		//Loging with a valid user
		String brokerPublicKey = "publicKeyA";
		
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
		
		//Workers login
		String workerServer = "xmpp.ourgrid.org";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker1ID);
		
		//Changing worker1 status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Requesting a worker with a true complex expression - expect AND has greater precedence than OR
		requestWorkers(component, 1, "OS = windows && mem >= 512 || environment = brams", 1, lwpcID, worker1ID);
		
		//Recovering worker2 to replace worker1
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker2", workerServer) , "worker2PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		
		//Requesting a worker with other true complex expression - expect AND has greater precedence than OR
		requestWorkers(component, 2, "environment = brams OR mem >= 512 AND OS = windows", 1, lwpcID, worker2ID);
	}
	
	@Test public void test_AT_111_5_parenthesesPrecedence() throws Exception{
		//Creating a user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");
		
		//Starting the peer
		component = req_010_Util.startPeer();
		
		//Loging in with a valid user
		String brokerPublicKey = "publicKeyA";
		
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
		
		//Workers login
		String workerServer = "xmpp.ourgrid.org";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker1ID);

		//Changing worker1 status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Request a worker with a false complex expression - expect the parenthesis give precedence to OR
		requestWorkers(component, 1, "OS = windows && (mem >= 512 || environment = brams)", 1, lwpcID);
		
		//Requesting a worker with a true complex expression - expect the parenthesis give precedence to AND
		requestWorkers(component, 2, "(OS = windows && mem >= 512) || environment = brams", 1, lwpcID, worker1ID);

		//Recovering worker2 to replace worker1
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker2", workerServer) , "worker2PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);

		//Requesting a worker with other false complex expression - expect the parenthesis give precedence to OR
		requestWorkers(component, 3, "(environment = brams OR mem >= 512) AND OS = windows", 1, lwpcID);
		
		//Requesting a worker with other true complex expression - expect the parenthesis give precedence to AND		
		requestWorkers(component, 4, "environment = brams OR (mem >= 512 AND OS = windows)", 1, lwpcID, worker2ID);
	}
	
	@Test public void test_AT_111_6_typingErrors() throws Exception {
		//Creating user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");
		
		//Starting the peer
		component = req_010_Util.startPeer();
		
		//Loging in with a valid user
		String brokerPublicKey = "publicKeyA";
		
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
		
		//Workers login
		String workerServer = "xmpp.ourgrid.org";
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker1ID);

		//Changing worker1 status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		
		//Requesting a worker with a wrongly typed expression - expect the expression be evaluated to false
		requestWorkers(component, 1, "environment >= 512", 1, lwpcID);
				
		//Requesting a worker with a true wrongly typed expression - expect the expression be evaluated to true
		requestWorkers(component, 2, "mem >= 512 OR environment > 1", 1, lwpcID, worker1ID);
	}
	
	@Test public void test_AT_111_7_caseSensitive() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");
		
		//Start peer  
		component = req_010_Util.startPeer();
		
		//Login with a valid user
		String brokerPublicKey = "publicKeyA";
		
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
		
		String workerServer = "xmpp.ourgrid.org";
		
		//worker1 login
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer);
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_OS, "linux");
		
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec, worker1ID);
		
		//Change worker1 status to idle
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		
		//Request a worker - Test attributes case
		requestWorkers(component, 1, "os = linux && MEM >= 512", 1, lwpcID, worker1ID);

		//Recovering worker2 to replace worker1
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerAcceptanceUtil.createWorkerSpec("worker2", workerServer) , "worker2PublicKey");
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		
		//Request a worker - Test values case
		requestWorkers(component, 2, "os = LInux", 1, lwpcID, worker2ID);
	}
}