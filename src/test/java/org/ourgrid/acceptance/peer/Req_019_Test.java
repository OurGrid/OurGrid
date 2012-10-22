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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.easymock.classextension.EasyMock;
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
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.communication.receiver.WorkerManagementClientReceiver;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.reqtrace.ReqTest;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

@ReqTest(reqs="REQ019")
public class Req_019_Test extends PeerAcceptanceTestCase {
	
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    
    public static final String COMM_FILE_PATH = "it_0019"+File.separator;    
    
    private PeerComponent component;
	
    @Before
	public void setUp() throws Exception{
		super.setUp();
		this.component = req_010_Util.startPeer();
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"0019_blank.xml");
	}
	
	/**
     * Recover a worker and verify if the peer sends a "setPeer" message to 
     * worker and register failure interest on it. Recover a worker twice 
     * and recover an unknown worker. Verify if the peer ignore them. 
     */
	@ReqTest(test="AT-019.1", reqs="REQ019")
	@Test public void test_AT_019_1_WorkerRecovery() throws Exception{
		//PART 1
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerADeploymentID);
	
		WorkerManagementClientReceiver workerMonitor = peerAcceptanceUtil.getWorkerMonitor();
		ObjectDeployment wmOD = peerAcceptanceUtil.getWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
	
		//PART 2
		WorkerManagement workerA = getMock(false, WorkerManagement.class);
		
		//Record mock behavior
		resetActiveMocks();
		peerAcceptanceUtil.createStub(workerA, WorkerManagement.class, workerADeploymentID);
		replayActiveMocks();
	
		//Notify recovery of WorkerA
		workerMonitor.doNotifyRecovery(workerA, workerADeploymentID);
		
		//Verify mock behavior
		verifyActiveMocks();
		
		//assertFalse(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));		
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
	
		//PART 3
		resetActiveMocks();
	
		WorkerManagement workerB = getMock(false, WorkerManagement.class);
	
		//Define worker B
		DeploymentID workerBDeploymentID = new DeploymentID(new ContainerID("workerB.xptolab.org", "xmpp.xptolab.org.lab",
				WorkerConstants.MODULE_NAME,  "otherPublicKey"),
				WorkerConstants.WORKER);
		
		//Record mock behaviour
		peerAcceptanceUtil.createStub(workerB, WorkerManagement.class, workerBDeploymentID);
		resetActiveMocks();
		replayActiveMocks();
		
		//Notify recovery of WorkerB
		workerMonitor.doNotifyRecovery(workerB, workerBDeploymentID);
		
		//Verify mock behaviour
		verifyActiveMocks();
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID(), wmOD.getDeploymentID()));
	}

	/**
	 * Fail a worker and verify if the peer do not register recovery interest on it. 
	 * Fail a worker twice and fail a unknown worker. 
	 * Verify if the peer ignore then. 
	 */
	@ReqTest(test="AT-019.2", reqs="REQ019")
	@Test public void test_AT_019_2_WorkerFailure() throws Exception{
		//PART 1
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerADeploymentID);
		
		WorkerManagementClientReceiver workerMonitor = peerAcceptanceUtil.getWorkerMonitor();
		ObjectDeployment wmOD = peerAcceptanceUtil.getWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
		
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
		
		//PART 2
		String message = "Failure of a non-existent worker: ";
		req_019_Util.notifyWorkerFailureForTestMessages(component, workerADeploymentID, message);

		assertFalse(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));

		//PART 3
		resetActiveMocks();

		WorkerManagement workerB = getMock(false, WorkerManagement.class);
		
		//Define worker B
		DeploymentID workerBDeploymentID = new DeploymentID(new ContainerID("workerB.xptolab.org", "xmpp.xptolab.org.lab",
				WorkerConstants.MODULE_NAME,  "otherPublicKey"),
				WorkerConstants.WORKER);
		
		peerAcceptanceUtil.createStub(workerB, WorkerManagement.class, workerBDeploymentID);
		
		//Record mock behavior
		resetActiveMocks();
		replayActiveMocks();
		
		//Notify recovery of WorkerB
		workerMonitor.doNotifyRecovery(workerB, workerBDeploymentID);
		
		//Verify mock behavior
		verifyActiveMocks();
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID(), wmOD.getDeploymentID()));
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	@ReqTest(test="AT-019.3", reqs="REQ019")
	@Test public void test_AT_019_3_WorkerRecovery2() throws Exception{
		//Set worker A
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerADeploymentID);

		WorkerManagementClientReceiver workerMonitor = peerAcceptanceUtil.getWorkerMonitor();
		ObjectDeployment wmOD = peerAcceptanceUtil.getWorkerMonitorDeployment();
		
		resetActiveMocks();

		WorkerManagement workerB = getMock(false, WorkerManagement.class);

		//Define worker B
		DeploymentID workerBDeploymentID = new DeploymentID(new ContainerID("workerB.xptolab.org", "xmpp.xptolab.org.lab",
				WorkerConstants.MODULE_NAME,  "otherPublicKey"),
				WorkerConstants.WORKER);
		
		peerAcceptanceUtil.createStub(workerB, WorkerManagement.class, workerBDeploymentID);
		
		//Record mock behaviour
		resetActiveMocks();
		replayActiveMocks();
		
		//Notify recovery of WorkerB
		workerMonitor.doNotifyRecovery(workerB, workerBDeploymentID);
		
		//Verify mock behaviour
		verifyActiveMocks();		
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID(), wmOD.getDeploymentID()));
	
		//workerA status not changes 
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
	}

	
	@ReqTest(test="AT-019.4", reqs="REQ019")
	@Test public void test_AT_019_4_WorkerRecovery3() throws Exception{

		//Set worker A
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerADeploymentID);

		ObjectDeployment wmOD = peerAcceptanceUtil.getWorkerMonitorDeployment();

		//test notify first time
		//assertFalse(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));

		//PART 2
		WorkerManagement workerA = getMock(false, WorkerManagement.class);
		
		//Record mock behavior
		resetActiveMocks();
		peerAcceptanceUtil.createStub(workerA, WorkerManagement.class, workerADeploymentID);
		replayActiveMocks();

		//Notify recovery of WorkerA again
		WorkerManagementClientReceiver workerMonitor = peerAcceptanceUtil.getWorkerMonitor();
		workerMonitor.doNotifyRecovery(workerA, workerADeploymentID);
				
		//Verify mock behavior
		verifyActiveMocks();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));		
	}
	
	
	@ReqTest(test="AT-019.6", reqs="REQ019")
	@Test public void test_AT_019_6_WorkerFailure2() throws Exception{
		//PART 1
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");
	    
		DeploymentID workerADeploymentID = req_019_Util.createWorkerManagementDeploymentID("publicKey1", workerSpec);
		
		String erroMessage = "Failure of a non-existent worker: ";
		
		req_019_Util.notifyWorkerFailureForTestMessages(component, workerADeploymentID, erroMessage);
		
	}
	
	@ReqTest(test="AT-019.7", reqs="REQ019")
	@Test public void test_AT_019_7_WorkerDonatedFails() throws Exception{
		//Set worker A

		//PART 1
	    WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");
	    workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_PROVIDER_PEER, getPeerAddress());
	    
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "publicKey1");
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Request a worker for the remote consumer 1
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("user", "server", "consumerPublicKey1");
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0, 
						new WorkerAllocation(workerADeploymentID));
		
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList();
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "publicKey1");
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec,
					Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, new WorkerAllocation(workerADeploymentID));
		
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, consumer1ID.getServiceID().toString());
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//Change workers status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADeploymentID, workerSpecA, consumer1ID);
		
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, consumer1ID.getServiceID().toString());
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		localWorkersInfo = AcceptanceTestUtil.createList();
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	@ReqTest(test="AT-019.4", reqs="REQ019")
	@Test public void test_AT_019_8_RemoteRequestTwiceWithDiffIds() throws Exception{
		//PART 1
	    WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");
	    workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_PROVIDER_PEER, getPeerAddress());
	    
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "publicKey1");
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Request a worker for the remote consumer 1
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("user", "server", "consumerPublicKey1");
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0, 
						new WorkerAllocation(workerADeploymentID));
		
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList();
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "publicKey1");
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("user", "server", "consumerPublicKey1");
		
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec,
					Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, new WorkerAllocation(workerADeploymentID));
		
		
		List<AllocableWorker> localAllocableWorkers = PeerDAOFactory.getInstance().getAllocationDAO().getLocalAllocableWorkers();
		
		for (AllocableWorker allocableWorker : localAllocableWorkers) {
			if (allocableWorker.getWorkerSpecification().equals(workerSpecA) && 
					!allocableWorker.getConsumer().getConsumerAddress().equals(consumer1ID.getServiceID().toString())) {
				fail();
			}
		}
	}

	/**
	 * Recover a worker and verify if the peer sends a "setPeer" message to 
	 * worker and register failure interest on it. Recover a worker twice 
	 * and recover an unknown worker. Verify if the peer ignore them. 
	 */
	@ReqTest(test="AT-019.1", reqs="REQ019")
	@Category(JDLCompliantTest.class) @Test public void test_AT_019_1_WorkerRecoveryWithJDL() throws Exception{
	
		//PART 1
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerADeploymentID);
	
		WorkerManagementClientReceiver workerMonitor = peerAcceptanceUtil.getWorkerMonitor();
		ObjectDeployment wmOD = peerAcceptanceUtil.getWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
	
		//PART 2
		WorkerManagement workerA = getMock(false, WorkerManagement.class);
		
		//Record mock behavior
		resetActiveMocks();
		peerAcceptanceUtil.createStub(workerA, WorkerManagement.class, workerADeploymentID);
		replayActiveMocks();
	
		//Notify recovery of WorkerA
		workerMonitor.doNotifyRecovery(workerA, workerADeploymentID);
		
		//Verify mock behavior
		verifyActiveMocks();
		
		//assertFalse(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));		
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
	
		//PART 3
		resetActiveMocks();
	
		WorkerManagement workerB = getMock(false, WorkerManagement.class);
	
		//Define worker B
		DeploymentID workerBDeploymentID = new DeploymentID(new ContainerID("workerB.xptolab.org", "xmpp.xptolab.org.lab",
				WorkerConstants.MODULE_NAME,  "otherPublicKey"),
				WorkerConstants.WORKER);
		
		//Record mock behaviour
		peerAcceptanceUtil.createStub(workerB, WorkerManagement.class, workerBDeploymentID);
		resetActiveMocks();
		replayActiveMocks();
		
		//Notify recovery of WorkerB
		workerMonitor.doNotifyRecovery(workerB, workerBDeploymentID);
		
		//Verify mock behaviour
		verifyActiveMocks();
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID(), wmOD.getDeploymentID()));
	}

	/**
	 * Fail a worker and verify if the peer register recovery interest on it. 
	 * Fail a worker twice and fail a unknown worker. 
	 * Verify if the peer ignore then. 
	 */
	@ReqTest(test="AT-019.2", reqs="REQ019")
	@Category(JDLCompliantTest.class) @Test public void test_AT_019_2_WorkerFailureWithJDL() throws Exception{
		//PART 1
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerADeploymentID);
		
		WorkerManagementClientReceiver workerMonitor = peerAcceptanceUtil.getWorkerMonitor();
		ObjectDeployment wmOD = peerAcceptanceUtil.getWorkerMonitorDeployment();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
		
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
		
		//PART 2
		String message = "Failure of a non-existent worker: ";
		req_019_Util.notifyWorkerFailureForTestMessages(component, workerADeploymentID, message);

		assertFalse(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));

		//PART 3
		resetActiveMocks();

		WorkerManagement workerB = getMock(false, WorkerManagement.class);
		
		//Define worker B
		DeploymentID workerBDeploymentID = new DeploymentID(new ContainerID("workerB.xptolab.org", "xmpp.xptolab.org.lab",
				WorkerConstants.MODULE_NAME,  "otherPublicKey"),
				WorkerConstants.WORKER);
		
		peerAcceptanceUtil.createStub(workerB, WorkerManagement.class, workerBDeploymentID);
		
		//Record mock behavior
		resetActiveMocks();
		replayActiveMocks();
		
		//Notify recovery of WorkerB
		workerMonitor.doNotifyRecovery(workerB, workerBDeploymentID);
		
		//Verify mock behavior
		verifyActiveMocks();
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID(), wmOD.getDeploymentID()));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@ReqTest(test="AT-019.3", reqs="REQ019")
	@Category(JDLCompliantTest.class) @Test public void test_AT_019_3_WorkerRecovery2WithJDL() throws Exception{
	
		//Set worker A
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerADeploymentID);

		WorkerManagementClientReceiver workerMonitor = peerAcceptanceUtil.getWorkerMonitor();
		ObjectDeployment wmOD = peerAcceptanceUtil.getWorkerMonitorDeployment();
		
		resetActiveMocks();

		WorkerManagement workerB = getMock(false, WorkerManagement.class);

		//Define worker B
		DeploymentID workerBDeploymentID = new DeploymentID(new ContainerID("workerB.xptolab.org", "xmpp.xptolab.org.lab",
				WorkerConstants.MODULE_NAME,  "otherPublicKey"),
				WorkerConstants.WORKER);
		
		peerAcceptanceUtil.createStub(workerB, WorkerManagement.class, workerBDeploymentID);
		
		//Record mock behaviour
		resetActiveMocks();
		replayActiveMocks();
		
		//Notify recovery of WorkerB
		workerMonitor.doNotifyRecovery(workerB, workerBDeploymentID);
		
		//Verify mock behaviour
		verifyActiveMocks();		
		
		assertFalse(AcceptanceTestUtil.isInterested(component, workerBDeploymentID.getServiceID(), wmOD.getDeploymentID()));
	
		//workerA status not changes 
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
	}

	@ReqTest(test="AT-019.1", reqs="REQ019")
	@Category(JDLCompliantTest.class) @Test public void test_AT_019_4_WorkerRecovery3WithJDL() throws Exception{
	
		//Set worker A
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, "publicKey1");
		req_010_Util.workerLogin(component, workerSpec, workerADeploymentID);

		ObjectDeployment wmOD = peerAcceptanceUtil.getWorkerMonitorDeployment();

		//test notify first time
		//assertFalse(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));

		//PART 2
		WorkerManagement workerA = getMock(false, WorkerManagement.class);
		
		//Record mock behavior
		resetActiveMocks();
		peerAcceptanceUtil.createStub(workerA, WorkerManagement.class, workerADeploymentID);
		replayActiveMocks();

		//Notify recovery of WorkerA again
		WorkerManagementClientReceiver workerMonitor = peerAcceptanceUtil.getWorkerMonitor();
		workerMonitor.doNotifyRecovery(workerA, workerADeploymentID);
				
		//Verify mock behavior
		verifyActiveMocks();
		
		assertTrue(AcceptanceTestUtil.isInterested(component, workerADeploymentID.getServiceID(), wmOD.getDeploymentID()));	
	}

	@ReqTest(test="AT-019.2", reqs="REQ019")
	@Category(JDLCompliantTest.class) @Test public void test_AT_019_6_WorkerFailure2WithJDL() throws Exception{
		//PART 1
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
	    
		DeploymentID workerADeploymentID = req_019_Util.createWorkerManagementDeploymentID("publicKey1", workerSpec);
		
		String erroMessage = "Failure of a non-existent worker: ";
		
		req_019_Util.notifyWorkerFailureForTestMessages(component, workerADeploymentID, erroMessage);
		
	}

	@ReqTest(test="AT-019.3", reqs="REQ019")
	@Category(JDLCompliantTest.class) @Test public void test_AT_019_7_WorkerDonatedFailsWithJDL() throws Exception{
		//PART 1
	    WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
	    workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_PROVIDER_PEER, getPeerAddress());
	    DeploymentID workerADeploymentID = req_019_Util.createWorkerManagementDeploymentID("publicKey1", workerSpecA);
	    req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Request a worker for the remote consumer 1
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("user", "server", "consumerPublicKey1");
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0, 
						new WorkerAllocation(workerADeploymentID));
		
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList();
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec,
					Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, new WorkerAllocation(workerADeploymentID));
		
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, consumer1ID.getServiceID().toString());
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		//Change workers status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerADeploymentID, workerSpecA, consumer1ID);
		
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, consumer1ID.getServiceID().toString());
		localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA);
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		
		workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		localWorkersInfo = AcceptanceTestUtil.createList();
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	@ReqTest(test="AT-019.4", reqs="REQ019")
	@Category(JDLCompliantTest.class) @Test public void test_AT_019_8_RemoteRequestTwiceWithDiffIdsWithJDL() throws Exception{
		//PART 1
	    WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
	    workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_PROVIDER_PEER, getPeerAddress());
	    DeploymentID workerADeploymentID = req_019_Util.createWorkerManagementDeploymentID("publicKey1", workerSpecA);
	    req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Request a worker for the remote consumer 1
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("user", "server", "consumerPublicKey1");
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 2, 0, 0);
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0, 
						new WorkerAllocation(workerADeploymentID));
		
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE);
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList();
		
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("user", "server", "consumerPublicKey1");
		
		req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec,
					Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, new WorkerAllocation(workerADeploymentID));
		
		
		List<AllocableWorker> localAllocableWorkers = PeerDAOFactory.getInstance().getAllocationDAO().getLocalAllocableWorkers();
		
		for (AllocableWorker allocableWorker : localAllocableWorkers) {
			if (allocableWorker.getWorkerSpecification().equals(workerSpecA) && 
					!allocableWorker.getConsumer().getConsumerAddress().equals(consumer1ID.getServiceID().toString())) {
				fail();
			}
		}
	}
}