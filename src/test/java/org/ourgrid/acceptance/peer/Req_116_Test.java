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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_116_Util;
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

@ReqTest(reqs = "REQ116")
public class Req_116_Test extends PeerAcceptanceTestCase {

	DeploymentID dsID;

	private PeerComponent component;
	private ScheduledExecutorService timer;
	private CommuneLogger loggerMock;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
	private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
	private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
	private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
	private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
	private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
	private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
	private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
	private Req_116_Util req_116_Util = new Req_116_Util(getComponentContext());

	@Before
	public void setUp() throws Exception {
		super.setUp();

		dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);

		component = req_010_Util.startPeer();

		timer = getMock(NOT_NICE, ScheduledExecutorService.class);
		component.setTimer(timer);

		loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);

	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 5 workers - the peer have not idle workers, so pass the request for community and schedule the request for repetition;
	 *    2. The peer receives a remote worker, which is allocated for the local consumer;
	 *    3. The local consumer updates the request needing only 3 workers and with new requirements - expect the peer to cancel the schedule repetition and create a new schedule with the updated request;
	 *    4. The peer receives a remote worker, which is allocated for the local consumer;
	 *    5. The local consumer updates the request needing only 2 workers and with new requirements - expect the peer to cancel the schedule repetition;
	 *    6. The peer receives a remote worker, which is disposed;
	 *    7. The local consumer updates the request needing only 1 worker and with new requirements;
	 *    8. A local worker becomes idle, expect the peer to do not allocated it for the local consumer;
	 *    9. The local consumer updates the request needing 3 workers and with new requirements - expect the peer to create a new schedule with the updated request;
	 *   10. A local worker becomes idle, which is allocated for the local consumer.
	 * 
	 */
	@Test public void test_AT_116_1_statesMachine() throws Exception {
		// Create an user account
		String userName = "user011";
		String serverName = "server011";
		String password = "011011";
		XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);

		// Workers login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerASpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", workerServerName);

		WorkerSpecification workerBSpec = workerAcceptanceUtil.createWorkerSpec("workerB.ourgrid.org", workerServerName);

		String workerAPublicKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerASpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerASpec, workerAID);

		String workerBPublicKey = "publicKeyB";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerBSpec, workerBPublicKey);
		req_010_Util.workerLogin(component, workerBSpec, workerBID);

		// Notify ds recovery
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);

		// Consumer login and request five workers
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

		int request1ID = 1;
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, "os = windows AND mem > 256", 5, 0, 0);
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec);

		//GIS client receive a remote worker provider
		String workerR1UserName = "workerR1";
		String workerR1ServerName = "server";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec(workerR1UserName, workerR1ServerName);
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpecR1.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");

		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);

		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();

		DeploymentID rwpID = rwpStub.getDeploymentID();

		// Remote worker provider client receives a remote worker
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "rworker1PK", brokerPublicKey);

		// Update the request: need 3 workers and new requirements
		int newRequiredWorkers = 3;
		String newRequirements = "os = windows AND mem > 512";
		RequestSpecification newRequestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, newRequirements, newRequiredWorkers, 0, 0);

		ScheduledFuture<?> future2 = req_116_Util.updateRequest(component, newRequestSpec, true, future1, lwpcID);

		// Remote worker provider client receive a remote worker
		String workerR2UserName = "workerR2";
		String workerR2ServerName = "server2";
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createWorkerSpec(workerR2UserName, workerR2ServerName);
		workerSpecR2.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpecR2.putAttribute(OurGridSpecificationConstants.ATT_MEM, "1024");

		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR2, "rworker2PK", brokerPublicKey);

		// Update the request: need 2 workers and new requirements
		int new2RequiredWorkers = 2;
		String new2Requirements = "os = windows";
		RequestSpecification new2RequestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, new2Requirements, new2RequiredWorkers, 0, 0);

		req_116_Util.updateRequest(component, new2RequestSpec, false, future2, lwpcID);

		// Remote worker provider client receive a remote worker
		String workerR3UserName = "workerR3";
		String workerR3ServerName = "server3";
		WorkerSpecification workerSpecR3 = workerAcceptanceUtil.createWorkerSpec(workerR3UserName, workerR3ServerName);
		workerSpecR3.putAttribute("os", "windows");

		req_018_Util.receiveAndDisposeRemoteWorker(component, rwp, rwpID, workerSpecR3, "workerR3PublicKey", brokerPublicKey);

		// Update the request: need 1 workers and new requirements
		int new3RequiredWorkers = 1;
		String new3Requirements = "";
		RequestSpecification new3RequestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, new3Requirements, new3RequiredWorkers, 0, 0);

		req_116_Util.updateRequest(component, new3RequestSpec, false, lwpcID);

		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerASpec, workerAID, dsID);

		//Update the request: need 3 workers and new requirements
		int new4RequiredWorkers = 3;
		String new4Requirements = "os = windows AND mem > 1024";
		RequestSpecification new4RequestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, new4Requirements, new4RequiredWorkers, 0, 0);

		req_116_Util.updateRequest(component, new4RequestSpec, true, lwpcID);

		//Change worker B status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerBSpec, workerBID, dsID);

	}

	/**
	 *Verifies if the peer ignores request updates, in these scenarios:
	 *
	 *    * Unknown consumer;
	 *    * Null request;
	 *    * Unknown request;
	 *    * Request from other consumer;
	 *    * Number of workers needed lower or equals zero.
	 *	 
	 */
	@Test public void test_AT_116_2_inputValidation() throws Exception {
		//Create user accounts
		String userName = "user01";
		String user2Name = "user02";
		String serverName = "server01";
		String password = "011011";
		String pubKey1 = "publicKey1";
		String pubKey2 = "publicKey2";
		XMPPAccount user1 = req_101_Util.createLocalUser(userName, serverName, password);
		XMPPAccount user2 = req_101_Util.createLocalUser(user2Name, serverName, password);

		//A unknown consumer update a request - expect to log warn
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpOD.getDeploymentID());

		loggerMock.warn("Ignoring an unknown consumer that updated a request. Sender public key: "
				+ lwpOD.getDeploymentID().getPublicKey());

		replayActiveMocks();
		lwp.updateRequest(null);
		verifyActiveMocks();
		resetActiveMocks();

		//Login with the consumer1
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), pubKey1);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc1ID = req_108_Util.login(component, user1, pubKey1);

		//A local consumer update a null request - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that updated a null request.");

		replayActiveMocks();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		lwp.updateRequest(null);
		verifyActiveMocks();
		resetActiveMocks();

		//A local consumer update an unknown request - expect to log warn
		long requestID = 1;
		String requirements = buildRequirements( null, null, "==", "windows" );
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, requirements, 5, 0, 0);
		loggerMock.warn("The consumer [" + lwpc1ID.getServiceID() + "] updated the unknown request [" + requestID + "]." +
				" This message was ignored.");

		replayActiveMocks();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		lwp.updateRequest(requestSpec);
		verifyActiveMocks();
		resetActiveMocks();

		//Login with the consumer2
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, pubKey2);

		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), pubKey2);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);

		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc2ID = req_108_Util.login(component, user2, pubKey2);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);

		//Request a worker for the consumer2
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), requestID, buildRequirements( null ), 1, 0, 0);

		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2);

		resetActiveMocks();

		//A local consumer update a request done by other consumer - expect to log warn
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, pubKey1);

		loggerMock.warn("The consumer [" + lwpc1ID.getServiceID() + "] updated the unknown request [" + requestID + "]. " +
				"This message was ignored.");

		replayActiveMocks();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		lwp.updateRequest(requestSpec2);
		verifyActiveMocks();
		resetActiveMocks();

		//A local consumer update a request with zero needed workers - expect to log warn
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, pubKey2);

		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), requestID, requirements, 0, 0, 0);

		loggerMock.warn("The consumer [" + lwpc2ID.getServiceID() + "] updated the request [" + requestID + "] " +
				"needing lower or equals zero workers. This message was ignored.");

		replayActiveMocks();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2ID);
		lwp.updateRequest(requestSpec3);
		verifyActiveMocks();
		resetActiveMocks();
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. A local consumer requests 5 workers - the peer have not idle workers, so pass the request for community and schedule the request for repetition;
	 *    2. The peer receives a remote worker, which is allocated for the local consumer;
	 *    3. The local consumer updates the request needing only 3 workers and with new requirements - expect the peer to cancel the schedule repetition and create a new schedule with the updated request;
	 *    4. The peer receives a remote worker, which is allocated for the local consumer;
	 *    5. The local consumer updates the request needing only 2 workers and with new requirements - expect the peer to cancel the schedule repetition;
	 *    6. The peer receives a remote worker, which is disposed;
	 *    7. The local consumer updates the request needing only 1 worker and with new requirements;
	 *    8. A local worker becomes idle, expect the peer to do not allocated it for the local consumer;
	 *    9. The local consumer updates the request needing 3 workers and with new requirements - expect the peer to create a new schedule with the updated request;
	 *   10. A local worker becomes idle, which is allocated for the local consumer.
	 * 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_116_1_statesMachineWithJDL() throws Exception {
		// Create an user account
		String userName = "user011";
		String serverName = "server011";
		String password = "011011";
		XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);

		// Workers login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerASpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", workerServerName, null, null);

		WorkerSpecification workerBSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerB.ourgrid.org", workerServerName, null, null);

		String workerAPublicKey = "publicKeyA";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerASpec, workerAPublicKey);
		req_010_Util.workerLogin(component, workerASpec, workerAID);

		String workerBPublicKey = "publicKeyB";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerBSpec, workerBPublicKey);
		req_010_Util.workerLogin(component, workerBSpec, workerBID);

		// Notify ds recovery
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);

		// Consumer login and request five workers
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

		int request1ID = 1;
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements( ">", 256, "==", "windows" ), 5, 0, 0);
		ScheduledFuture<?> future1 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec);

		//GIS client receive a remote worker provider
		String workerR1UserName = "workerR1";
		String workerR1ServerName = "server";
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec(workerR1UserName, workerR1ServerName, 512, "windows");

		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpecR1);

		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();

		DeploymentID rwpID = rwpStub.getDeploymentID();

		// Remote worker provider client receives a remote worker
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, "rworker1PK", brokerPublicKey);

		// Update the request: need 3 workers and new requirements
		int newRequiredWorkers = 3;
		String newRequirements = buildRequirements( ">", 512, "==", "windows" );
		RequestSpecification newRequestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, newRequirements, newRequiredWorkers, 0, 0);

		ScheduledFuture<?> future2 = req_116_Util.updateRequest(component, newRequestSpec, true, future1, lwpcID);

		// Remote worker provider client receive a remote worker
		String workerR2UserName = "workerR2";
		String workerR2ServerName = "server2";
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createClassAdWorkerSpec(workerR2UserName, workerR2ServerName, 1024, "windows");

		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR2, "rworker2PK", brokerPublicKey);

		// Update the request: need 2 workers and new requirements
		int new2RequiredWorkers = 2;
		String new2Requirements = buildRequirements( null, null, "==", "windows" );
		RequestSpecification new2RequestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, new2Requirements, new2RequiredWorkers, 0, 0);

		req_116_Util.updateRequest(component, new2RequestSpec, false, future2, lwpcID);

		// Remote worker provider client receive a remote worker
		String workerR3UserName = "workerR3";
		String workerR3ServerName = "server3";
		WorkerSpecification workerSpecR3 = workerAcceptanceUtil.createClassAdWorkerSpec(workerR3UserName, workerR3ServerName, null, "windows");

		req_018_Util.receiveAndDisposeRemoteWorker(component, rwp, rwpID, workerSpecR3, "workerR3PublicKey", brokerPublicKey);

		// Update the request: need 1 workers and new requirements
		int new3RequiredWorkers = 1;
		String new3Requirements = buildRequirements( null );
		RequestSpecification new3RequestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, new3Requirements, new3RequiredWorkers, 0, 0);

		req_116_Util.updateRequest(component, new3RequestSpec, false, lwpcID);

		// Change worker A status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerASpec, workerAID, dsID);

		//Update the request: need 3 workers and new requirements
		int new4RequiredWorkers = 3;
		String new4Requirements = buildRequirements( ">", 1024, "==", "windows" );
		RequestSpecification new4RequestSpec = new RequestSpecification(0, new JobSpecification("label"), request1ID, new4Requirements, new4RequiredWorkers, 0, 0);

		req_116_Util.updateRequest(component, new4RequestSpec, true, lwpcID);

		//Change worker B status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerBSpec, workerBID, dsID);

	}

	/**
	 *Verifies if the peer ignores request updates, in these scenarios:
	 *
	 *    * Unknown consumer;
	 *    * Null request;
	 *    * Unknown request;
	 *    * Request from other consumer;
	 *    * Number of workers needed lower or equals zero.
	 *	 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_116_2_inputValidationWithJDL() throws Exception {
		//Create user accounts
		String userName = "user01";
		String user2Name = "user02";
		String serverName = "server01";
		String password = "011011";
		String pubKey1 = "publicKey1";
		String pubKey2 = "publicKey2";
		XMPPAccount user1 = req_101_Util.createLocalUser(userName, serverName, password);
		XMPPAccount user2 = req_101_Util.createLocalUser(user2Name, serverName, password);

		//A unknown consumer update a request - expect to log warn
		LocalWorkerProvider lwp = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpOD.getDeploymentID());

		loggerMock.warn("Ignoring an unknown consumer that updated a request. Sender public key: "
				+ lwpOD.getDeploymentID().getPublicKey());

		replayActiveMocks();
		lwp.updateRequest(null);
		verifyActiveMocks();
		resetActiveMocks();

		//Login with the consumer1
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), pubKey1);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc1ID = req_108_Util.login(component, user1, pubKey1);

		//A local consumer update a null request - expect to log warn
		loggerMock.warn("Ignoring the consumer [" + lwpc1ID.getServiceID() + "] that updated a null request.");

		replayActiveMocks();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		lwp.updateRequest(null);
		verifyActiveMocks();
		resetActiveMocks();

		//A local consumer update an unknown request - expect to log warn
		long requestID = 1;
		String requirements = "os = windows";
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, requirements, 5, 0, 0);
		loggerMock.warn("The consumer [" + lwpc1ID.getServiceID() + "] updated the unknown request [" + requestID + "]." +
				" This message was ignored.");

		replayActiveMocks();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		lwp.updateRequest(requestSpec);
		verifyActiveMocks();
		resetActiveMocks();

		//Login with the consumer2
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, pubKey2);

		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), pubKey2);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);

		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpc2ID = req_108_Util.login(component, user2, pubKey2);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);

		//Request a worker for the consumer2
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), requestID, "", 1, 0, 0);

		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2), requestSpec2);

		resetActiveMocks();

		//A local consumer update a request done by other consumer - expect to log warn
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, pubKey1);

		loggerMock.warn("The consumer [" + lwpc1ID.getServiceID() + "] updated the unknown request [" + requestID + "]. " +
				"This message was ignored.");

		replayActiveMocks();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc1ID);
		lwp.updateRequest(requestSpec2);
		verifyActiveMocks();
		resetActiveMocks();

		//A local consumer update a request with zero needed workers - expect to log warn
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, pubKey2);

		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), requestID, requirements, 0, 0, 0);

		loggerMock.warn("The consumer [" + lwpc2ID.getServiceID() + "] updated the request [" + requestID + "] " +
				"needing lower or equals zero workers. This message was ignored.");

		replayActiveMocks();

		AcceptanceTestUtil.setExecutionContext(component, lwpOD, lwpc2ID);
		lwp.updateRequest(requestSpec3);
		verifyActiveMocks();
		resetActiveMocks();
	}

}