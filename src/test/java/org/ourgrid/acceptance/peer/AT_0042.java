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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_037_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerInfo;
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

public class AT_0042 extends PeerAcceptanceTestCase {
	
	//FIXME
    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
    private Req_037_Util req_037_Util = new Req_037_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());

    @After
	public void tearDown() throws Exception{
		super.tearDown();
		peerAcceptanceUtil.deleteNOFRankingFile();
	}
	
    /**
     * This test contains the following steps:
     * 
     *    1. Start a peer with 6 workers
     *    2. Notify recovery of all workers and change the status of 2 workers to IDLE
     *    3. Verify local workers status
     *    4. Verify remote workers status
     *    5. A local consumer requests 5 workers - 2 workers are allocated to the consumer, and the request is forwarded to the community
     *    6. Verify local workers status
     *    7. Verify remote workers status
     *    8. The peer receives 1 remote worker which is allocated to the local consumer
     *    9. Verify local workers status
     *   10. Verify remote workers status
     *   11. One worker becomes IDLE
     *   12. Verify local workers status
     *   13. Verify remote workers status
     *   14. The peer receives 1 remote worker from another provider which is allocated to the local consumer
     *   15. Verify local workers status
     *   16. Verify remote workers status
     *   17. Change the status of the remaining 3 workers to IDLE
     *   18. Verify local workers status
     *   19. Verify remote workers status
     *   20. Another local consumer requests 4 workers - The 3 remaining workers are allocated to the consumer, as well as the last remote Worker received
     *   21. Verify local workers status
     *   22. Verify remote workers status
     * 
     */
	@ReqTest(test="AT-0042", reqs="")
	@Test public void test_AT_0042_LocalAndRemoteWorkersStatus() throws Exception {
		// Create user accounts
		XMPPAccount user01 = req_101_Util.createLocalUser("user01", "server011", "011011");
		XMPPAccount user02 = req_101_Util.createLocalUser("user02", "server011", "011011");

		// Start peer
		component = req_010_Util.startPeer();
		
		// Set mocks for logger and timer
		CommuneLogger logger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(logger);
		
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);

		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", "xmpp.ourgrid.org");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB", "xmpp.ourgrid.org");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("workerC", "xmpp.ourgrid.org");
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("workerD", "xmpp.ourgrid.org");
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createWorkerSpec("workerE", "xmpp.ourgrid.org");
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createWorkerSpec("workerF", "xmpp.ourgrid.org");

		
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPubKey");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPubKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "workerCPubKey");
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, "workerDPubKey");
		req_010_Util.workerLogin(component, workerSpecD, workerDID);

		DeploymentID workerEID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, "workerEPubKey");
		req_010_Util.workerLogin(component, workerSpecE, workerEID);

		DeploymentID workerFID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, "workerFPubKey");
		req_010_Util.workerLogin(component, workerSpecF, workerFID);
		
		// Change workers A and B status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		
		// Verify local workers status
		List<WorkerInfo> expectedResult = new ArrayList<WorkerInfo>(6);
		expectedResult.add(new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null));
		expectedResult.add(new WorkerInfo(workerSpecB, LocalWorkerState.IDLE, null));
		expectedResult.add(new WorkerInfo(workerSpecC, LocalWorkerState.OWNER, null));
		expectedResult.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login
		String broker01PubKey = "mg01PubKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker01PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user01.getUsername() + "@" + user01.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    DeploymentID lwpc01ID = req_108_Util.login(component, user01, broker01PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc01ID);
	    
	    // Request five workers
	    RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 5, 0, 0);
	    
	    WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
	    WorkerAllocation workerAllocB = new WorkerAllocation(workerBID);
	    
	    ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc01ID, lwpc),
	    		requestSpec, workerAllocA, workerAllocB);
	    
	    assertTrue(future != null);
	    
	    // Verify local workers status
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(6);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecC, LocalWorkerState.OWNER, null));
		expectedResult3.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult3.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult3.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();

		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("workerR1", "unknownServer");
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives 1 remote worker
		String workerR1PubKey = "workerR1PubKey";
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey, broker01PubKey);
		
		// Verify local workers status
		List<WorkerInfo> expectedResult5 = new ArrayList<WorkerInfo>(6);
		expectedResult5.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult5.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult5.add(new WorkerInfo(workerSpecC, LocalWorkerState.OWNER, null));
		expectedResult5.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult5.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult5.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult5);
		
		// Verify remote workers status
		RemoteWorkerInfo info1 = new RemoteWorkerInfo(workerSpecR1, rwpID.getServiceID().toString(),
				lwpc01ID.getServiceID().toString());
		
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_037_Util.getRemoteWorkersStatus(info1);
		
		// Worker C becomes IDLE
		req_025_Util.changeWorkerStatusToIdleWorkingForBroker(workerCID, lwpc01ID, component);
		
		// Verify local workers status
		List<WorkerInfo> expectedResult7 = new ArrayList<WorkerInfo>(6);
		expectedResult7.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult7.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult7.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult7.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult7.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult7.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult7);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus(info1);
		
		// GIS client receives another remote worker provider
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createWorkerSpec("workerR2", "unknownServer2");
		
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp2User", 
				"rwp2Server", "rwp2PubKey", workerSpecR2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		// Remote worker provider client receives 1 remote worker
		String workerR2PubKey = "workerR2PubKey";
		DeploymentID workerR2ID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpecR2, workerR2PubKey,
				broker01PubKey, future).getDeploymentID();
		
		// Verify local workers status
		List<WorkerInfo> expectedResult9 = new ArrayList<WorkerInfo>(6);
		expectedResult9.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult9.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult9.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult9.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult9.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult9.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult9);
		
		// Verify remote workers status
		RemoteWorkerInfo info2 = new RemoteWorkerInfo(workerSpecR2, rwp2ID.getServiceID().toString(),
				lwpc01ID.getServiceID().toString());
		
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_037_Util.getRemoteWorkersStatus(info1, info2);

		// Change worker D, E and F status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecD, workerDID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecE, workerEID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecF, workerFID, dsID);
		
		// Verify local workers status
		List<WorkerInfo> expectedResult11 = new ArrayList<WorkerInfo>(6);
		expectedResult11.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult11.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult11.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult11.add(new WorkerInfo(workerSpecD, LocalWorkerState.IDLE, null));
		expectedResult11.add(new WorkerInfo(workerSpecE, LocalWorkerState.IDLE, null));
		expectedResult11.add(new WorkerInfo(workerSpecF, LocalWorkerState.IDLE, null));
		req_036_Util.getLocalWorkersStatus(expectedResult11);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus(info1, info2);
		
		// Another consumer login
		String broker02PubKey = "mg02PubKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker02PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient, user02.getUsername() + "@" + user02.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    DeploymentID lwpc02ID = req_108_Util.login(component, user02, broker02PubKey);
	    
	    LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc02ID);
	    
	    // Request four workers
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 4, 0, 0);
	    
	    WorkerAllocation workerAllocD = new WorkerAllocation(workerDID);
	    WorkerAllocation workerAllocE = new WorkerAllocation(workerEID);
	    WorkerAllocation workerAllocF = new WorkerAllocation(workerFID);
	    WorkerAllocation workerAllocR2 = new WorkerAllocation(workerR2ID)
	    			.addLoserConsumer(lwpc01ID).addLoserRequestSpec(requestSpec)
	    			.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClient());
	    
	    ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc02ID, lwpc2),
	    		requestSpec2, workerAllocD, workerAllocE, workerAllocF, workerAllocR2);
	    
	    assertTrue(future2 == null);
	    
	    // Verify local workers status
		List<WorkerInfo> expectedResult13 = new ArrayList<WorkerInfo>(6);
		expectedResult13.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecD, LocalWorkerState.IN_USE, lwpc02ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecE, LocalWorkerState.IN_USE, lwpc02ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpc02ID.getServiceID().toString()));
		req_036_Util.getLocalWorkersStatus(expectedResult13);
		
		// Verify remote workers status
		RemoteWorkerInfo infoR2 = new RemoteWorkerInfo(workerSpecR2, rwp2ID.getServiceID().toString(),
				lwpc02ID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(info1, infoR2);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *    1. Start a peer with 6 workers
	 *    2. Notify recovery of all workers and change the status of 2 workers to IDLE
	 *    3. Verify local workers status
	 *    4. Verify remote workers status
	 *    5. A local consumer requests 5 workers - 2 workers are allocated to the consumer, and the request is forwarded to the community
	 *    6. Verify local workers status
	 *    7. Verify remote workers status
	 *    8. The peer receives 1 remote worker which is allocated to the local consumer
	 *    9. Verify local workers status
	 *   10. Verify remote workers status
	 *   11. One worker becomes IDLE
	 *   12. Verify local workers status
	 *   13. Verify remote workers status
	 *   14. The peer receives 1 remote worker from another provider which is allocated to the local consumer
	 *   15. Verify local workers status
	 *   16. Verify remote workers status
	 *   17. Change the status of the remaining 3 workers to IDLE
	 *   18. Verify local workers status
	 *   19. Verify remote workers status
	 *   20. Another local consumer requests 4 workers - The 3 remaining workers are allocated to the consumer, as well as the last remote Worker received
	 *   21. Verify local workers status
	 *   22. Verify remote workers status
	 * 
	 */
	@ReqTest(test="AT-0042", reqs="")
	@Test public void test_AT_0042_LocalAndRemoteWorkersStatusWithJDL() throws Exception {
		// Create user accounts
		XMPPAccount user01 = req_101_Util.createLocalUser("user01", "server011", "011011");
		XMPPAccount user02 = req_101_Util.createLocalUser("user02", "server011", "011011");
	
		// Start peer
		component = req_010_Util.startPeer();
		
		// Set mocks for logger and timer
		CommuneLogger logger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(logger);
		
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
	
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.ourgrid.org", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB", "xmpp.ourgrid.org", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("workerC", "xmpp.ourgrid.org", null, null);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("workerD", "xmpp.ourgrid.org", null, null);
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createClassAdWorkerSpec("workerE", "xmpp.ourgrid.org", null, null);
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createClassAdWorkerSpec("workerF", "xmpp.ourgrid.org", null, null);
		
		
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPubKey");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "workerBPubKey");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "workerCPubKey");
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, "workerDPubKey");
		req_010_Util.workerLogin(component, workerSpecD, workerDID);

		DeploymentID workerEID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, "workerEPubKey");
		req_010_Util.workerLogin(component, workerSpecE, workerEID);

		DeploymentID workerFID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, "workerFPubKey");
		req_010_Util.workerLogin(component, workerSpecF, workerFID);
		
		// Change workers A and B status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		
		// Verify local workers status
		// Verify local workers status
		List<WorkerInfo> expectedResult = new ArrayList<WorkerInfo>(6);
		expectedResult.add(new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null));
		expectedResult.add(new WorkerInfo(workerSpecB, LocalWorkerState.IDLE, null));
		expectedResult.add(new WorkerInfo(workerSpecC, LocalWorkerState.OWNER, null));
		expectedResult.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Consumer login
		String broker01PubKey = "mg01PubKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker01PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user01.getUsername() + "@" + user01.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    DeploymentID lwpc01ID = req_108_Util.login(component, user01, broker01PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc01ID);
	    
	    // Request five workers
	    RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements( null ), 5, 0, 0);
	    
	    WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
	    WorkerAllocation workerAllocB = new WorkerAllocation(workerBID);
	    
	    ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc01ID, lwpc),
	    		requestSpec, workerAllocA, workerAllocB);
	    
	    assertTrue(future != null);
	    
	    // Verify local workers status
		List<WorkerInfo> expectedResult3 = new ArrayList<WorkerInfo>(6);
		expectedResult3.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult3.add(new WorkerInfo(workerSpecC, LocalWorkerState.OWNER, null));
		expectedResult3.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult3.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult3.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult3);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus();
	
		// GIS client receives a remote worker provider
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1", "unknownServer", null, null);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receives 1 remote worker
		String workerR1PubKey = "workerR1PubKey";
		req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey, broker01PubKey);
		
		// Verify local workers status
		List<WorkerInfo> expectedResult5 = new ArrayList<WorkerInfo>(6);
		expectedResult5.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult5.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult5.add(new WorkerInfo(workerSpecC, LocalWorkerState.OWNER, null));
		expectedResult5.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult5.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult5.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult5);
		
		// Verify remote workers status
		RemoteWorkerInfo info1 = new RemoteWorkerInfo(workerSpecR1, rwpID.getServiceID().toString(),
				lwpc01ID.getServiceID().toString());
		
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_037_Util.getRemoteWorkersStatus(info1);
		
		// Worker C becomes IDLE
		req_025_Util.changeWorkerStatusToIdleWorkingForBroker(workerCID, lwpc01ID, component);
		
		// Verify local workers status
		List<WorkerInfo> expectedResult7 = new ArrayList<WorkerInfo>(6);
		expectedResult7.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult7.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult7.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult7.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult7.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult7.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult7);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus(info1);
		
		// GIS client receives another remote worker provider
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR2", "unknownServer2", null, null);
		
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp2User", 
				"rwp2Server", "rwp2PubKey", workerSpecR2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		// Remote worker provider client receives 1 remote worker
		String workerR2PubKey = "workerR2PubKey";
		DeploymentID workerR2ID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpecR2, workerR2PubKey,
				broker01PubKey, future).getDeploymentID();
		
		// Verify local workers status
		List<WorkerInfo> expectedResult9 = new ArrayList<WorkerInfo>(6);
		expectedResult9.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult9.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult9.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult9.add(new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null));
		expectedResult9.add(new WorkerInfo(workerSpecE, LocalWorkerState.OWNER, null));
		expectedResult9.add(new WorkerInfo(workerSpecF, LocalWorkerState.OWNER, null));
		req_036_Util.getLocalWorkersStatus(expectedResult9);
		
		// Verify remote workers status
		RemoteWorkerInfo info2 = new RemoteWorkerInfo(workerSpecR2, rwp2ID.getServiceID().toString(),
				lwpc01ID.getServiceID().toString());
		
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_037_Util.getRemoteWorkersStatus(info1, info2);
	
		// Change worker D, E and F status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecD, workerDID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecE, workerEID, dsID);
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecF, workerFID, dsID);
		
		// Verify local workers status
		List<WorkerInfo> expectedResult11 = new ArrayList<WorkerInfo>(6);
		expectedResult11.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult11.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult11.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult11.add(new WorkerInfo(workerSpecD, LocalWorkerState.IDLE, null));
		expectedResult11.add(new WorkerInfo(workerSpecE, LocalWorkerState.IDLE, null));
		expectedResult11.add(new WorkerInfo(workerSpecF, LocalWorkerState.IDLE, null));
		req_036_Util.getLocalWorkersStatus(expectedResult11);
		
		// Verify remote workers status
		req_037_Util.getRemoteWorkersStatus(info1, info2);
		
		// Another consumer login
		String broker02PubKey = "mg02PubKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker02PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient, user02.getUsername() + "@" + user02.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
	    DeploymentID lwpc02ID = req_108_Util.login(component, user02, broker02PubKey);
	    
	    LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc02ID);
	    
	    // Request four workers
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, buildRequirements( null ), 4, 0, 0);
	    
	    WorkerAllocation workerAllocD = new WorkerAllocation(workerDID);
	    WorkerAllocation workerAllocE = new WorkerAllocation(workerEID);
	    WorkerAllocation workerAllocF = new WorkerAllocation(workerFID);
	    WorkerAllocation workerAllocR2 = new WorkerAllocation(workerR2ID)
	    			.addLoserConsumer(lwpc01ID).addLoserRequestSpec(requestSpec)
	    			.addRemoteWorkerManagementClient(peerAcceptanceUtil.getRemoteWorkerManagementClient());
	    
	    ScheduledFuture<?> future2 = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc02ID, lwpc2),
	    		requestSpec2, workerAllocD, workerAllocE, workerAllocF, workerAllocR2);
	    
	    assertTrue(future2 == null);
	    
	    // Verify local workers status
		List<WorkerInfo> expectedResult13 = new ArrayList<WorkerInfo>(6);
		expectedResult13.add(new WorkerInfo(workerSpecA, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecB, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecC, LocalWorkerState.IN_USE, lwpc01ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecD, LocalWorkerState.IN_USE, lwpc02ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecE, LocalWorkerState.IN_USE, lwpc02ID.getServiceID().toString()));
		expectedResult13.add(new WorkerInfo(workerSpecF, LocalWorkerState.IN_USE, lwpc02ID.getServiceID().toString()));
		req_036_Util.getLocalWorkersStatus(expectedResult13);
		
		// Verify remote workers status
		RemoteWorkerInfo infoR2 = new RemoteWorkerInfo(workerSpecR2, rwp2ID.getServiceID().toString(),
				lwpc02ID.getServiceID().toString());
		req_037_Util.getRemoteWorkersStatus(info1, infoR2);
	}
	
}