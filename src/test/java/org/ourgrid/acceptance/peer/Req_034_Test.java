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

import java.io.File;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_034_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_119_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ034")
public class Req_034_Test extends PeerAcceptanceTestCase {

	public static final String COMM_FILE_PATH = "req_034"+File.separator;
	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_034_Util req_034_Util = new Req_034_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_119_Util req_119_Util = new Req_119_Util(getComponentContext());
    
	@Before
	public void setUp() throws Exception { 
		super.setUp();
		
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.delete();
	}
	
	@After
	public void tearDown() throws Exception {
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.delete();
		super.tearDown();
	}
	
	/**
	 * A peer without remote consumers
	 */
	@ReqTest(test="AT-034.1.1", reqs="REQ034")
	@Test public void test_AT_034_1_withoutRemoteConsumers() throws Exception {
		//Start the peer
		component = req_010_Util.startPeer();
		
		//Verify if the peer has not consumers
		List<ConsumerInfo> expectedResult = AcceptanceTestUtil.createList();
		req_034_Util.getRemoteConsumersStatus(expectedResult);
	}
	
	/**
	 * A peer without remote consumers (explosion)
	 */
	@ReqTest(test="AT-034.1.2", reqs="REQ034")
	@Test public void test_AT_034_2_withoutRemoteConsumers2() throws Exception {
		//Start the peer
		component = req_010_Util.startPeer();
		
		//Verify if the peer has not consumers
		List<ConsumerInfo> expectedResult = AcceptanceTestUtil.createList();
		assertTrue(expectedResult.isEmpty());
		req_034_Util.getRemoteConsumersStatus(expectedResult);
	}
	
	/**
	 * A peer with two remote consumers
	 */
	@ReqTest(test="AT-034.2", reqs="REQ034")
	@Test public void test_AT_034_3_withRemoteConsumers() throws Exception {
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"034_blank.xml");
		
		//Start the peer
		component = req_010_Util.startPeer();
		
		//3 Workers login
		String server = "server";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", server);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB", server);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("workerC", server);
		
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "workerCPublicKey");
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);
		
		//Request a worker for the remote consumer 1
		String consumer1PublicKey = "consumerPublicKey1";
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("consumer1", server, consumer1PublicKey);
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0,  allocationC);
		
		//Request two workers for the remote consumer 2
		String consumer2PublicKey = "consumerPublicKey2";
		DeploymentID consumer2ID = PeerAcceptanceUtil.createRemoteConsumerID("consumer2", server, consumer2PublicKey);
		
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 2, 0, 0);
		req_011_Util.requestForRemoteClient(component, consumer2ID, requestSpec2, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationB, allocationA);
		
		//Verify if the peer has two consumers
		ConsumerInfo consumer1info = new ConsumerInfo(1, consumer1ID.getServiceID().toString());
		ConsumerInfo consumer2info = new ConsumerInfo(2, consumer2ID.getServiceID().toString());
		List<ConsumerInfo> expectedResult = AcceptanceTestUtil.createList(consumer1info, consumer2info);
		req_034_Util.getRemoteConsumersStatus(expectedResult);
	}
	
	/**
	 * A peer with two remote consumers
	 */
	@ReqTest(test="AT-034.2", reqs="REQ034")
	@Test public void test_AT_034_4_withRemoteConsumers2() throws Exception {
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"034_blank.xml");
		
		//Start the peer
		component = req_010_Util.startPeer();
		
		//3 Workers login
		String server = "server";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", server);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB", server);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("workerC", server);
		
		
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "workerCPublicKey");
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);
		
		//Request a worker for the remote consumer 1
		String consumer1PublicKey = "consumerPublicKey1";
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("consumer1", server, consumer1PublicKey);
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		RemoteWorkerProviderClient rwpc1 = req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0,  allocationC);
		
		RemoteWorkerProviderClient rwpcMock = EasyMock.createMock(RemoteWorkerProviderClient.class);
		peerAcceptanceUtil.createStub(rwpcMock, RemoteWorkerProviderClient.class, consumer1ID);
		
		req_119_Util.notifyRemoteConsumerFailure(component, null, consumer1ID, false, allocationC);
		
		//Request two workers for the remote consumer 2
		String consumer2PublicKey = "consumerPublicKey2";
		DeploymentID consumer2ID = new DeploymentID(new ContainerID("consumer2", server, PeerConstants.MODULE_NAME, consumer2PublicKey),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT); 
			
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, createJobSpec("label"), 2, "", 2, 0, 0);
		
		req_011_Util.requestForRemoteClient(component, consumer2ID, requestSpec2, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationB, allocationC);
		
		//Verify if the peer has two consumers
		ConsumerInfo consumer2info = new ConsumerInfo(2, consumer2ID.getServiceID().toString());
		List<ConsumerInfo> expectedResult = AcceptanceTestUtil.createList(consumer2info);
//		assertEquals(1, expectedResult.size());
		req_034_Util.getRemoteConsumersStatus(expectedResult);
	}
	
	/**
	 * A peer with a local and a remote consumers
	 */
	@ReqTest(test="AT-034.3", reqs="REQ034")
	@Test public void test_AT_034_5_withLocalAndRemoteConsumers() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"034_blank.xml");
		
		//Create an user account
		String serverName = "server011";
		XMPPAccount user = req_101_Util.createLocalUser("user011", serverName, "011011");
	
		//Start the peer
		component = req_010_Util.startPeer();
		
		//3 Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", serverName);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB", serverName);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("workerC", serverName);
		
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "workerCPublicKey");
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);
		
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
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerCID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), spec, allocation);
		
		//Request a worker for the remote consumer 1
		String consumer1PublicKey = "consumerPublicKey1";
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("consumer1", serverName, consumer1PublicKey);
		
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0, allocationB);
		
		//Verify if the peer has only one remote consumer
		ConsumerInfo consumer1info = new ConsumerInfo(1, consumer1ID.getServiceID().toString());
		List<ConsumerInfo> expectedResult = AcceptanceTestUtil.createList(consumer1info);
		req_034_Util.getRemoteConsumersStatus(expectedResult);
	}

	/**
	 * A peer with two remote consumers
	 */
	@ReqTest(test="AT-034.2", reqs="REQ034")
	@Category(JDLCompliantTest.class) @Test public void test_AT_034_3_withRemoteConsumersWithJDL() throws Exception {
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"034_blank.xml");
		
		//Start the peer
		component = req_010_Util.startPeer();
		
		//3 Workers login
		String server = "server";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", server, null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB", server, null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("workerC", server, null, null);
		
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "workerCPublicKey");
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);
		
		//Request a worker for the remote consumer 1
		String consumer1PublicKey = "consumerPublicKey1";
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("consumer1", server, consumer1PublicKey);
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0,  allocationC);
		
		//Request two workers for the remote consumer 2
		String consumer2PublicKey = "consumerPublicKey2";
		DeploymentID consumer2ID = PeerAcceptanceUtil.createRemoteConsumerID("consumer2", server, consumer2PublicKey);
		
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		req_011_Util.requestForRemoteClient(component, consumer2ID, requestSpec2, 
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationB, allocationA);
		
		//Verify if the peer has two consumers
		ConsumerInfo consumer1info = new ConsumerInfo(1, consumer1ID.getServiceID().toString());
		ConsumerInfo consumer2info = new ConsumerInfo(2, consumer2ID.getServiceID().toString());
		List<ConsumerInfo> expectedResult = AcceptanceTestUtil.createList(consumer1info, consumer2info);
		req_034_Util.getRemoteConsumersStatus(expectedResult);
	}

	/**
		 * A peer with two remote consumers
		 */
		@ReqTest(test="AT-034.2", reqs="REQ034")
		@Category(JDLCompliantTest.class) @Test public void test_AT_034_4_withRemoteConsumers2WithJDL() throws Exception {
			
			PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"034_blank.xml");
			
			//Start the peer
			component = req_010_Util.startPeer();
			
			//3 Workers login
			String server = "server";
			WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", server, null, null);
			WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB", server, null, null);
			WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("workerC", server, null, null);
			
			DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
			req_010_Util.workerLogin(component, workerSpecA, workerAID);

			DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
			req_010_Util.workerLogin(component, workerSpecB, workerBID);

			DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "workerCPublicKey");
			req_010_Util.workerLogin(component, workerSpecC, workerCID);

			//Change worker status to IDLE
			req_025_Util.changeWorkerStatusToIdle(component, workerAID);
			req_025_Util.changeWorkerStatusToIdle(component, workerBID);
			req_025_Util.changeWorkerStatusToIdle(component, workerCID);
			
			//Request a worker for the remote consumer 1
			String consumer1PublicKey = "consumerPublicKey1";
			DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("consumer1", server, consumer1PublicKey);
			
			WorkerAllocation allocationC = new WorkerAllocation(workerCID);
			RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
			RemoteWorkerProviderClient rwpc1 = req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0,  allocationC);
			
			RemoteWorkerProviderClient rwpcMock = EasyMock.createMock(RemoteWorkerProviderClient.class);
			peerAcceptanceUtil.createStub(rwpcMock, RemoteWorkerProviderClient.class, consumer1ID);
			
			req_119_Util.notifyRemoteConsumerFailure(component, null, consumer1ID, false, allocationC);
			
			//Request two workers for the remote consumer 2
			String consumer2PublicKey = "consumerPublicKey2";
			DeploymentID consumer2ID = new DeploymentID(new ContainerID("consumer2", server, PeerConstants.MODULE_NAME, consumer2PublicKey),
					PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT); 
				
			WorkerAllocation allocationB = new WorkerAllocation(workerBID);
			
			RequestSpecification requestSpec2 = new RequestSpecification(0, createJobSpec("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
			
			req_011_Util.requestForRemoteClient(component, consumer2ID, requestSpec2, 
					Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, allocationB, allocationC);
			
			//Verify if the peer has two consumers
			ConsumerInfo consumer2info = new ConsumerInfo(2, consumer2ID.getServiceID().toString());
			List<ConsumerInfo> expectedResult = AcceptanceTestUtil.createList(consumer2info);
	//		assertEquals(1, expectedResult.size());
			req_034_Util.getRemoteConsumersStatus(expectedResult);
		}

	/**
	 * A peer with a local and a remote consumers
	 */
	@ReqTest(test="AT-034.3", reqs="REQ034")
	@Category(JDLCompliantTest.class) @Test public void test_AT_034_5_withLocalAndRemoteConsumersWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"034_blank.xml");
		
		//Create an user account
		String serverName = "server011";
		XMPPAccount user = req_101_Util.createLocalUser("user011", serverName, "011011");
	
		//Start the peer
		component = req_010_Util.startPeer();
		
		//3 Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", serverName, null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB", serverName, null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("workerC", serverName, null, null);
		
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPublicKey");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPublicKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "workerCPublicKey");
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);
		
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
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		//Request a worker for the logged user
		WorkerAllocation allocation = new WorkerAllocation(workerCID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), spec, allocation);
		
		//Request a worker for the remote consumer 1
		String consumer1PublicKey = "consumerPublicKey1";
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("consumer1", serverName, consumer1PublicKey);
		
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0, allocationB);
		
		//Verify if the peer has only one remote consumer
		ConsumerInfo consumer1info = new ConsumerInfo(1, consumer1ID.getServiceID().toString());
		List<ConsumerInfo> expectedResult = AcceptanceTestUtil.createList(consumer1info);
		req_034_Util.getRemoteConsumersStatus(expectedResult);
	}
}