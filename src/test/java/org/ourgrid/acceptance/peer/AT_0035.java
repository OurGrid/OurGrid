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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.JDLUtils;
import org.ourgrid.acceptance.util.RemoteAllocation;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_022_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_027_Util;
import org.ourgrid.acceptance.util.peer.Req_035_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.worker.WorkerAcceptanceTestCase;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessResultInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0035 extends PeerAcceptanceTestCase {

	private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_022_Util req_022_Util = new Req_022_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_027_Util req_027_Util = new Req_027_Util(getComponentContext());
    private Req_035_Util req_035_Util = new Req_035_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
	
    @After
	public void tearDown() throws Exception{
		super.tearDown();
		peerAcceptanceUtil.deleteNOFRankingFile();
	}
	
    /**
     * Test plan:
     * 
     *    1. The local peer has one idle worker (workerA);
     *    2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
     *    3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
     *    4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
     *    5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
     *    6. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
     *    7. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
     *    8. Expect the Network of favours status to be empty;
     *    9. The local consumer fails - expect the request schedule to be cancelled, the workerA to stop working and the remote worker to be disposed;
     *   10. Expect the Network of favours status to be:
     *          1. rwp1Id, 8, 9
     *   11. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=5) - expect the accounting to be ignored, the warn logged and the Network of favors status to be:
     *          1. rwp1Id, 8, 9
     * 
     */
	@ReqTest(test="AT-0035", reqs="")
	@Test public void test_AT_0035_AccountingReceivedFavorForFailedLocalConsumer() throws Exception {
		// Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		// Start peer
		component = req_010_Util.startPeer();
		
		// Set mocks for logger and timer
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);

		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", "xmpp.ourgrid.org");
		
		String workerAPubKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// Login with a valid user
		String brokerPubKey = "mgPubKey";
		
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
		
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
	    
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
	    // Request five workers for the logged user
		for (int i = 0; i < 5; i++) {
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			tasks.add(task);
			
		}
		
		
	    RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", "", tasks), 1, "", 5, 0, 0);
	    WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
	    
	    ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec, workerAllocA);
	    
	    assertTrue(future != null);
	    
	    EasyMock.reset(future);
	    EasyMock.replay(future);
	    
	    // Change worker A status to ALLOCATED FOR BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec);
	    
	    // GIS client receives a remote worker provider (rwp)
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("workerR1", "unknownServer");
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker (R1 - rwp)
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID workerR1ID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), workerR1ID, workerSpecR1, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		// Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, workerR1ID.toString(), 
				workerR1ID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecR1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa1, false);
		
		// Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, workerR1ID.toString(), 
				workerR1ID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecR1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa2, false);
		
		// Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, workerAID.toString(), 
				workerAID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa3, true);
		
		// Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, workerAID.toString(), 
				workerAID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa4, true);
	    
		// Expect the Network of favours status to be empty
		Map<String, PeerBalance> knownPeerBalances = new HashMap<String, PeerBalance>();
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		// Notify the failure of the local consumer
		// Expect the info to be logged
		// Expect the request schedule to be canceled
		// Expect the remote worker to be disposed and the local worker to be stopped
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(new WorkerAllocation(workerR1ID)));
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA), stubs, future);
		
		// Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> knownPeerBalances2 = new HashMap<String, PeerBalance>();
		knownPeerBalances2.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(knownPeerBalances2);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
		// Report a received favour
		GridProcessAccounting acc1 = new GridProcessAccounting(requestSpec, workerR1ID.toString(), 
				workerR1ID.getPublicKey(), 5, 5, GridProcessState.FINISHED, workerSpecR1);
		acc1.setTaskSequenceNumber(5);
		reportReplicaAccountingWithWarn(component, lwpcID, acc1);
		
		// Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> knownPeerBalances3 = new HashMap<String, PeerBalance>();
		knownPeerBalances3.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
		NetworkOfFavorsStatus nofStatus3 = new NetworkOfFavorsStatus(knownPeerBalances3);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus3);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0035_AccountingReceivedFavorForFailedLocalConsumerWithJDL() throws Exception {
		// Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");

		// Start peer
		component = req_010_Util.startPeer();
		
		// Set mocks for logger and timer
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);

		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.ourgrid.org", null, null);
	
		String workerAPubKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		// Login with a valid user
		String brokerPubKey = "mgPubKey";
		
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
		
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
	    
	    // Request five workers for the logged user
//		IOBlock initBlock = new IOBlock();
//		initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
//		IOBlock finalBlock = new IOBlock();
//		finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
//		finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
//		TaskSpec task = new TaskSpec(initBlock, "echo Hello World", finalBlock, "echo");
//		task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
//		
//		List<TaskSpec> tasks = new ArrayList<TaskSpec>();
//		tasks.add(task);
		JobSpecification jobSpec = createJobSpecJDL(JDLUtils.JAVA_IO_JOB, 5);
		
	    RequestSpecification requestSpec = new RequestSpecification(0, jobSpec, 2, buildRequirements(null), 5, 0, 0);
	    WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
	    
	    ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec, workerAllocA);
	    
	    assertTrue(future != null);
	    
	    EasyMock.reset(future);
	    EasyMock.replay(future);
	    
	    // Change worker A status to ALLOCATED FOR BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec);
	    
	    // GIS client receives a remote worker provider (rwp)
		WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1", "unknownServer", null, null);
		
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", "rwpPubKey", workerSpecR1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker (R1 - rwp)
		String workerR1PubKey = "workerR1PubKey";
		DeploymentID workerR1ID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpecR1, workerR1PubKey,
				brokerPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), workerR1ID, workerSpecR1, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		// Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, workerR1ID.toString(), 
				workerR1ID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecR1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa1, false);
		
		// Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, workerR1ID.toString(), 
				workerR1ID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecR1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa2, false);
		
		// Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, workerAID.toString(), 
				workerAID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa3, true);
		
		// Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, workerAID.toString(), 
				workerAID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa4, true);
	    
		// Expect the Network of favours status to be empty
		Map<String, PeerBalance> knownPeerBalances = new HashMap<String, PeerBalance>();
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		// Notify the failure of the local consumer
		// Expect the info to be logged
		// Expect the request schedule to be canceled
		// Expect the remote worker to be disposed and the local worker to be stopped
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(new WorkerAllocation(workerR1ID)));
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		
		TestStub stub = new TestStub(rwpID, remotePeer1);
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(stub);
		
		req_022_Util.notifyBrokerFailure(component, lwpcID, AcceptanceTestUtil.createList(allocationA), stubs, future);
		
		// Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> knownPeerBalances2 = new HashMap<String, PeerBalance>();
		knownPeerBalances2.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(knownPeerBalances2);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
		// Report a received favour
		GridProcessAccounting acc1 = new GridProcessAccounting(requestSpec, workerR1ID.toString(), 
				workerR1ID.getPublicKey(), 5, 5, GridProcessState.FINISHED, workerSpecR1);
		acc1.setTaskSequenceNumber(5);
		reportReplicaAccountingWithWarn(component, lwpcID, acc1);
		
		// Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> knownPeerBalances3 = new HashMap<String, PeerBalance>();
		knownPeerBalances3.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
		NetworkOfFavorsStatus nofStatus3 = new NetworkOfFavorsStatus(knownPeerBalances3);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus3);
	}
	
	private void reportReplicaAccountingWithWarn(PeerComponent component, DeploymentID lwpcID, GridProcessAccounting accounting) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		newLogger.warn("Ignoring a replica accounting from a not logged user. Sender public key: " + lwpcID.getPublicKey());
		EasyMock.replay(newLogger);
		
		req_027_Util.reportReplicaAccounting(component, accounting, lwpcID);
		
		EasyMock.verify(newLogger);
		component.setLogger(oldLogger);
	}
}