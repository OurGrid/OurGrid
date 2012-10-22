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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.RemoteAllocation;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_014_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_027_Util;
import org.ourgrid.acceptance.util.peer.Req_035_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_115_Util;
import org.ourgrid.acceptance.worker.WorkerAcceptanceTestCase;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessResultInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
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

@ReqTest(reqs="REQ115")
public class Req_115_Test extends PeerAcceptanceTestCase {

	public static final String DATA_FILE_PATH = "test" + File.separator + "acceptance" + File.separator + 
			"req_115" + File.separator;
	
	public static final String INEXISTANT_FILE_NAME = "doesNotExist.dat";
	
	public static final String EMPTY_FILE_NAME = "empty.dat";
	
	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_014_Util req_014_Util = new Req_014_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_027_Util req_027_Util = new Req_027_Util(getComponentContext());
    private Req_035_Util req_035_Util = new Req_035_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_115_Util req_115_Util = new Req_115_Util(getComponentContext());
    
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		
		File file = new File(DATA_FILE_PATH + INEXISTANT_FILE_NAME);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * This test contains the following steps:
	 * 
	 *     * Expect the Network of favours status to be empty.
	 * 
	 */
	@Test public void test_AT_115_1_startingPeerWithoutNetworkOfFavorsFile() throws Exception {
		// Start the peer
		req_010_Util.startPeer();
		
		// Expect the Network of favours status to be empty
		Map<String, PeerBalance> knownPeerBalances = new HashMap<String, PeerBalance>();
		NetworkOfFavorsStatus networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
		
	}
	
	/**
	 * This test contains the following steps:
	 * 	   * Start the peer
	 *     * Network of favours setup:
	 *           o The local peer has one idle worker (workerA);
	 *           o A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *           o _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *           o _The peer receives one remote worker (R2) from remote peer rwp2, which is allocated for the local consumer;
	 *           o The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR2, cpu=7, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR2, cpu=9, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *           o The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote workers;
	 *     * Save the Network of favors data;
	 *     * Expect the Network of favours status to be:
	 *           o rwp1Id, 6, 9
	 *           o rwp2Id, 6, 8
	 *     * Stop the Peer;
	 *     * Start the peer with again with the property peer.rankingfile set to a file that was previously used;
	 *     * Expect the peer to schedule the file save of the network of favors data;
	 *     * Expect the Network of favours status to be:
	 *           o rwp1Id, 6, 9
	 *           o rwp2Id, 6, 8
	 * 
	 * OBS: based on test AT-035.6
	 *  
	 */
	@Test public void test_AT_115_5_savingAndLoadingNetworkOfFavorsData() throws Exception {
		// Start the peer
		component = req_010_Util.startPeer();
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Worker login
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerA");
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, workerAID, dsID);
		
		// Login with a valid user
		XMPPAccount user = req_101_Util.createLocalUser("userName", "serverName", "011011");
		String mgPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), mgPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcID = req_108_Util.login(component, user, mgPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request five workers for the logged user
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
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
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label", "", tasks), 1, "", 5, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec1, workerAllocA);
		
		// Change worker A status to ALLOCATED FOR BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec1);
	    
	    // GIS client receive a remote worker provider (rwp1)
	    WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("workerR1", "server1");
	    String rwp1PublicKey = "rwp1PublicKey";
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, 
				"rwp1UserName", "rwp1ServerName", rwp1PublicKey, workerSpecR1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		// GIS client receive a remote worker provider (rwp2)
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createWorkerSpec("workerR2", "server2");
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp2UserName", 
				"rwp2ServerName", rwp2PublicKey, workerSpecR2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker (R1 - rwp1)
	    String workerR1PubKey = "workerR1PubKey";
	    DeploymentID rwmR1ID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpecR1, workerR1PubKey, mgPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
	    ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
	    
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
	    		peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
	    		rwmR1ID, workerSpecR1, requestSpec1);
	    
		// Remote worker provider client receives a remote worker (R2 - rwp2)
		String workerR2PubKey = "workerR2PubKey";
		DeploymentID rwmR2ID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpecR2, workerR2PubKey, mgPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmR2ID, workerSpecR2, requestSpec1);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		// Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec1, rwmR1ID.toString(), 
				rwmR1ID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecR1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa1, false);
		
		// Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec1,
				rwmR1ID.toString(), rwmR1ID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecR1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa2, false);
		
		// Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec1,
				rwmR2ID.toString(), rwmR2ID.getPublicKey(), 7., 4., GridProcessState.FINISHED, workerSpecR2);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa3, false);
		
		// Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec1,
				rwmR2ID.toString(), rwmR2ID.getPublicKey(), 9., 4., GridProcessState.FINISHED, workerSpecR2);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa4, false);
		
		// Report a received favour
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec1,
				workerAID.toString(), workerAID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa5, true);
		
		// Expect the Network of favours status to be empty
		Map<String, PeerBalance> knownPeerBalances = new HashMap<String, PeerBalance>();
		NetworkOfFavorsStatus networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
		
		// Finish the request
		List<WorkerAllocation> localWorkers = new LinkedList<WorkerAllocation>();
		localWorkers.add(workerAllocA);
		
		List<WorkerAllocation> remoteWorkers1 = new ArrayList<WorkerAllocation>(1);
		remoteWorkers1.add(new WorkerAllocation(rwmR1ID));
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, remoteWorkers1);
		
		List<WorkerAllocation> remoteWorkers2 = new ArrayList<WorkerAllocation>(1);
		remoteWorkers2.add(new WorkerAllocation(rwmR2ID));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, remoteWorkers2);
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				mgPubKey, lwpcID.getServiceID(), future, requestSpec1, localWorkers, 
				new TestStub(rwp1ID, remotePeer1), new TestStub(rwp2ID, remotePeer2));
		
		// Save the Network of favors data
		req_115_Util.saveRanking(component);
		
		// Expect the Network of favours status to be: rwp1Id, 6, 9; rwp2Id, 6, 8
		knownPeerBalances = new HashMap<String, PeerBalance>();
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(6d, 9d));
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(6d, 8d));
		networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
		
		// Stop the Peer
		req_010_Util.notNiceStopPeer(component);
		
		// Start the peer
		component = req_010_Util.startPeer();
		
		// Expect the Network of favours status to be: rwp1Id, 6, 9; rwp2Id, 6, 8
		knownPeerBalances = new HashMap<String, PeerBalance>();
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(6d, 9d));
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(6d, 8d));
		networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *     * Start the peer
	 *     * Unknown sender try to save the Network of favors data;
	 *     * Expect the Peer not to save the Network of favors data and log the warning;
	 *     * Expect the Network of favors status to be empty.
	 * 
	 */
	@Test public void test_AT_115_6_unknownSenderSavesNetworkOfFavorsData() throws Exception {
		//PeerAcceptanceUtil.recreateSchema();
		
		// Start the peer
		component = req_010_Util.startPeer();
		
		CommuneLogger logger = component.getLogger();
		EasyMock.reset(logger);
			
		// Save the Network of favors data
		String unknownPubKey = "unknownPubKey";
		WorkerManagementClient workerManagementClient = peerAcceptanceUtil.getWorkerManagementClient(); 
		ObjectDeployment paOD = peerAcceptanceUtil.getWorkerManagementClientDeployment();
		AcceptanceTestUtil.setExecutionContext(component, paOD, unknownPubKey);
		
		logger.warn("An unknown sender tried to save the Network of favors data. This message was ignored. Sender public key: " + unknownPubKey);
		EasyMock.replay(logger);
			
		workerManagementClient.saveRanking();
		
		EasyMock.verify(logger);
		EasyMock.reset(logger);

		// Expect the Network of favours status to be empty
		EasyMock.replay(logger);
		
		Map<String, PeerBalance> knownPeerBalances = new HashMap<String, PeerBalance>();
		NetworkOfFavorsStatus networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
		
		EasyMock.verify(logger);
	}
	
	/**
	 * This test contains the following steps:
	 * 
	 *     * Start the peer
	 *     * Network of favours setup:
	 *           o The local peer has one idle worker (workerA);
	 *           o A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *           o _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *           o _The peer receives one remote worker (R2) from remote peer rwp2, which is allocated for the local consumer;
	 *           o The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR2, cpu=7, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR2, cpu=9, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *           o The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote workers;
	 *     * Stop the Peer;
	 *     * Start the peer with again with the property peer.rankingfile set to a file that was previously used;
	 *     * Expect the peer to schedule the file save of the network of favors data;
	 *     * Expect the Network of favours status to be:
	 *           o rwp1Id, 6, 9
	 *           o rwp2Id, 6, 8
	 * 
	 */
	@Test public void test_AT_115_8_stoppingPeerAndLoadingNetworkOfFavorsData() throws Exception {
		// Start the peer
		component = req_010_Util.startPeer();
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Worker login
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "workerA");
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, workerAID, dsID);
		
		// Login with a valid user
		XMPPAccount user = req_101_Util.createLocalUser("userName", "serverName", "011011");
		String mgPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), mgPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcID = req_108_Util.login(component, user, mgPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request five workers for the logged user
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
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
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label", "", tasks), 1, "", 5, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec1, workerAllocA);
		
		// Change worker A status to ALLOCATED FOR BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec1);
	    
	    // GIS client receive a remote worker provider (rwp1)
	    WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("workerR1", "server1");
	    String rwp1PublicKey = "rwp1PublicKey";
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp1UserName", 
				"rwp1ServerName", rwp1PublicKey, workerSpecR1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
	    // GIS client receive a remote worker provider (rwp2)
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createWorkerSpec("workerR2", "server2");
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp2UserName", 
				"rwp2ServerName", rwp2PublicKey, workerSpecR2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker (R1 - rwp1)
	    String workerR1PubKey = "workerR1PubKey";
	    DeploymentID rwmR1ID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpecR1, workerR1PubKey, mgPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
	    ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
	    
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
	    		peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
	    		rwmR1ID, workerSpecR1, requestSpec1);
	    
		// Remote worker provider client receives a remote worker (R2 - rwp2)
		String workerR2PubKey = "workerR2PubKey";
		DeploymentID rwmR2ID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpecR2, workerR2PubKey, mgPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmR2ID, workerSpecR2, requestSpec1);

		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		// Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec1, rwmR1ID.toString(), 
				rwmR1ID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecR1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa1, false);
		
		// Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec1,
				rwmR1ID.toString(), rwmR1ID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecR1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa2, false);
		
		// Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec1,
				rwmR2ID.toString(), rwmR2ID.getPublicKey(), 7., 4., GridProcessState.FINISHED, workerSpecR2);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa3, false);
		
		// Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec1,
				rwmR2ID.toString(), rwmR2ID.getPublicKey(), 9., 4., GridProcessState.FINISHED, workerSpecR2);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa4, false);
		
		// Report a received favour
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec1,
				workerAID.toString(), workerAID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa5, true);
		
		// Expect the Network of favours status to be empty
		Map<String, PeerBalance> knownPeerBalances = new HashMap<String, PeerBalance>();
		NetworkOfFavorsStatus networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
		
		// Finish the request
		List<WorkerAllocation> localWorkers = new LinkedList<WorkerAllocation>();
		localWorkers.add(workerAllocA);
		
		List<WorkerAllocation> remoteWorkers1 = new ArrayList<WorkerAllocation>(1);
		remoteWorkers1.add(new WorkerAllocation(rwmR1ID));
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, remoteWorkers1);
		
		List<WorkerAllocation> remoteWorkers2 = new ArrayList<WorkerAllocation>(1);
		remoteWorkers2.add(new WorkerAllocation(rwmR2ID));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, remoteWorkers2);
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD,
				mgPubKey, lwpcID.getServiceID(), future, requestSpec1, localWorkers, 
				new TestStub(rwp1ID, remotePeer1), new TestStub(rwp2ID, remotePeer2));
		
		// Stop the Peer
		req_010_Util.notNiceStopPeer(component);
		
		// Start the peer
		req_010_Util.startPeer();
		
		// Expect the Network of favours status to be: rwp1Id, 6, 9; rwp2Id, 6, 8
		knownPeerBalances = new HashMap<String, PeerBalance>();
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(6d, 9d));
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(6d, 8d));
		networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
	}

	/**
	 * This test contains the following steps:
	 * 	   * Start the peer
	 *     * Network of favours setup:
	 *           o The local peer has one idle worker (workerA);
	 *           o A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *           o _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *           o _The peer receives one remote worker (R2) from remote peer rwp2, which is allocated for the local consumer;
	 *           o The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR2, cpu=7, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR2, cpu=9, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *           o The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote workers;
	 *     * Save the Network of favors data;
	 *     * Expect the Network of favours status to be:
	 *           o rwp1Id, 6, 9
	 *           o rwp2Id, 6, 8
	 *     * Stop the Peer;
	 *     * Start the peer with again with the property peer.rankingfile set to a file that was previously used;
	 *     * Expect the peer to schedule the file save of the network of favors data;
	 *     * Expect the Network of favours status to be:
	 *           o rwp1Id, 6, 9
	 *           o rwp2Id, 6, 8
	 * 
	 * OBS: based on test AT-035.6
	 *  
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_115_5_savingAndLoadingNetworkOfFavorsDataWithJDL() throws Exception {
		// Start the peer
		component = req_010_Util.startPeer();
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.ourgrid.org", null, null);
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		
		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, workerAID, dsID);
		
		// Login with a valid user
		XMPPAccount user = req_101_Util.createLocalUser("userName", "serverName", "011011");
		String mgPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), mgPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcID = req_108_Util.login(component, user, mgPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request five workers for the logged user
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
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
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label", buildRequirements( null ), tasks), 1, buildRequirements( null ), 5, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec1, workerAllocA);
		
		// Change worker A status to ALLOCATED FOR BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec1);
	    
	    // GIS client receive a remote worker provider (rwp1)
	    WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1", "server1", null, null);
	    String rwp1PublicKey = "rwp1PublicKey";
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, 
				"rwp1UserName", "rwp1ServerName", rwp1PublicKey, workerSpecR1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		// GIS client receive a remote worker provider (rwp2)
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR2", "server2", null, null);
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp2UserName", 
				"rwp2ServerName", rwp2PublicKey, workerSpecR2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker (R1 - rwp1)
	    String workerR1PubKey = "workerR1PubKey";
	    DeploymentID rwmR1ID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpecR1, workerR1PubKey, mgPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
	    ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
	    
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
	    		peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
	    		rwmR1ID, workerSpecR1, requestSpec1);
	    
		// Remote worker provider client receives a remote worker (R2 - rwp2)
		String workerR2PubKey = "workerR2PubKey";
		DeploymentID rwmR2ID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpecR2, workerR2PubKey, mgPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmR2ID, workerSpecR2, requestSpec1);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		// Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec1, rwmR1ID.toString(), 
				rwmR1ID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecR1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa1, false);
		
		// Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec1,
				rwmR1ID.toString(), rwmR1ID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecR1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa2, false);
		
		// Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec1,
				rwmR2ID.toString(), rwmR2ID.getPublicKey(), 7., 4., GridProcessState.FINISHED, workerSpecR2);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa3, false);
		
		// Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec1,
				rwmR2ID.toString(), rwmR2ID.getPublicKey(), 9., 4., GridProcessState.FINISHED, workerSpecR2);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa4, false);
		
		// Report a received favour
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec1,
				workerAID.toString(), workerAID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa5, true);
		
		// Expect the Network of favours status to be empty
		Map<String, PeerBalance> knownPeerBalances = new HashMap<String, PeerBalance>();
		NetworkOfFavorsStatus networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
		
		// Finish the request
		List<WorkerAllocation> localWorkers = new LinkedList<WorkerAllocation>();
		localWorkers.add(workerAllocA);
		
		List<WorkerAllocation> remoteWorkers1 = new ArrayList<WorkerAllocation>(1);
		remoteWorkers1.add(new WorkerAllocation(rwmR1ID));
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, remoteWorkers1);
		
		List<WorkerAllocation> remoteWorkers2 = new ArrayList<WorkerAllocation>(1);
		remoteWorkers2.add(new WorkerAllocation(rwmR2ID));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, remoteWorkers2);
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				mgPubKey, lwpcID.getServiceID(), future, requestSpec1, localWorkers, 
				new TestStub(rwp1ID, remotePeer1), new TestStub(rwp2ID, remotePeer2));
		
		// Save the Network of favors data
		req_115_Util.saveRanking(component);
		
		// Expect the Network of favours status to be: rwp1Id, 6, 9; rwp2Id, 6, 8
		knownPeerBalances = new HashMap<String, PeerBalance>();
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(6d, 9d));
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(6d, 8d));
		networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
		
		// Stop the Peer
		req_010_Util.notNiceStopPeer(component);
		
		// Start the peer
		component = req_010_Util.startPeer();
		
		// Expect the Network of favours status to be: rwp1Id, 6, 9; rwp2Id, 6, 8
		knownPeerBalances = new HashMap<String, PeerBalance>();
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(6d, 9d));
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(6d, 8d));
		networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *     * Start the peer
	 *     * Unknown sender try to save the Network of favors data;
	 *     * Expect the Peer not to save the Network of favors data and log the warning;
	 *     * Expect the Network of favors status to be empty.
	 * 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_115_6_unknownSenderSavesNetworkOfFavorsDataWithJDL() throws Exception {
		//PeerAcceptanceUtil.recreateSchema();
		
		// Start the peer
		component = req_010_Util.startPeer();
		
		CommuneLogger logger = component.getLogger();
		EasyMock.reset(logger);
			
		// Save the Network of favors data
		String unknownPubKey = "unknownPubKey";
		WorkerManagementClient workerManagementClient = peerAcceptanceUtil.getWorkerManagementClient(); 
		ObjectDeployment paOD = peerAcceptanceUtil.getWorkerManagementClientDeployment();
		AcceptanceTestUtil.setExecutionContext(component, paOD, unknownPubKey);
		
		logger.warn("An unknown sender tried to save the Network of favors data. This message was ignored. Sender public key: " + unknownPubKey);
		EasyMock.replay(logger);
			
		workerManagementClient.saveRanking();
		
		EasyMock.verify(logger);
		EasyMock.reset(logger);
	
		// Expect the Network of favours status to be empty
		EasyMock.replay(logger);
		
		Map<String, PeerBalance> knownPeerBalances = new HashMap<String, PeerBalance>();
		NetworkOfFavorsStatus networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
		
		EasyMock.verify(logger);
	}

	/**
	 * This test contains the following steps:
	 * 
	 *     * Start the peer
	 *     * Network of favours setup:
	 *           o The local peer has one idle worker (workerA);
	 *           o A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *           o _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *           o _The peer receives one remote worker (R2) from remote peer rwp2, which is allocated for the local consumer;
	 *           o The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR2, cpu=7, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerR2, cpu=9, data=4);
	 *           o The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *           o The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote workers;
	 *     * Stop the Peer;
	 *     * Start the peer with again with the property peer.rankingfile set to a file that was previously used;
	 *     * Expect the peer to schedule the file save of the network of favors data;
	 *     * Expect the Network of favours status to be:
	 *           o rwp1Id, 6, 9
	 *           o rwp2Id, 6, 8
	 * 
	 */
	@Category(JDLCompliantTest.class) @Test public void test_AT_115_8_stoppingPeerAndLoadingNetworkOfFavorsDataWithJDL() throws Exception {
		// Start the peer
		component = req_010_Util.startPeer();
		
		// DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		// Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec( "workerA", "xmpp.ourgrid.org", null, null );
		
		String workerAPublicKey = "workerAPubKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		// Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, workerAID, dsID);
		
		// Login with a valid user
		XMPPAccount user = req_101_Util.createLocalUser("userName", "serverName", "011011");
		String mgPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), mgPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcID = req_108_Util.login(component, user, mgPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
		
		// Request five workers for the logged user
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
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
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label", buildRequirements( null ), tasks), 1, buildRequirements( null ), 5, 0, 0);
		WorkerAllocation workerAllocA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc),
				requestSpec1, workerAllocA);
		
		// Change worker A status to ALLOCATED FOR BROKER
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcID, workerAID, workerSpecA, requestSpec1);
	    
	    // GIS client receive a remote worker provider (rwp1)
	    WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR1", "server1", null, null);
	    String rwp1PublicKey = "rwp1PublicKey";
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp1UserName", 
				"rwp1ServerName", rwp1PublicKey, workerSpecR1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
	    // GIS client receive a remote worker provider (rwp2)
		WorkerSpecification workerSpecR2 = workerAcceptanceUtil.createClassAdWorkerSpec("workerR2", "server2", null, null);
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp2UserName", 
				"rwp2ServerName", rwp2PublicKey, workerSpecR2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		// Remote worker provider client receive a remote worker (R1 - rwp1)
	    String workerR1PubKey = "workerR1PubKey";
	    DeploymentID rwmR1ID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpecR1, workerR1PubKey, mgPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
	    ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcID, AcceptanceTestUtil.getBoundObject(lwpcID));
	    
	    req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
	    		peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
	    		rwmR1ID, workerSpecR1, requestSpec1);
	    
		// Remote worker provider client receives a remote worker (R2 - rwp2)
		String workerR2PubKey = "workerR2PubKey";
		DeploymentID rwmR2ID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpecR2, workerR2PubKey, mgPubKey).getDeploymentID();
		
		// Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmR2ID, workerSpecR2, requestSpec1);
	
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		// Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec1, rwmR1ID.toString(), 
				rwmR1ID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecR1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa1, false);
		
		// Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec1,
				rwmR1ID.toString(), rwmR1ID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecR1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa2, false);
		
		// Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec1,
				rwmR2ID.toString(), rwmR2ID.getPublicKey(), 7., 4., GridProcessState.FINISHED, workerSpecR2);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa3, false);
		
		// Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec1,
				rwmR2ID.toString(), rwmR2ID.getPublicKey(), 9., 4., GridProcessState.FINISHED, workerSpecR2);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa4, false);
		
		// Report a received favour
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec1,
				workerAID.toString(), workerAID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcID, gpa5, true);
		
		// Expect the Network of favours status to be empty
		Map<String, PeerBalance> knownPeerBalances = new HashMap<String, PeerBalance>();
		NetworkOfFavorsStatus networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
		
		// Finish the request
		List<WorkerAllocation> localWorkers = new LinkedList<WorkerAllocation>();
		localWorkers.add(workerAllocA);
		
		List<WorkerAllocation> remoteWorkers1 = new ArrayList<WorkerAllocation>(1);
		remoteWorkers1.add(new WorkerAllocation(rwmR1ID));
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, remoteWorkers1);
		
		List<WorkerAllocation> remoteWorkers2 = new ArrayList<WorkerAllocation>(1);
		remoteWorkers2.add(new WorkerAllocation(rwmR2ID));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, remoteWorkers2);
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD,
				mgPubKey, lwpcID.getServiceID(), future, requestSpec1, localWorkers, 
				new TestStub(rwp1ID, remotePeer1), new TestStub(rwp2ID, remotePeer2));
		
		// Stop the Peer
		req_010_Util.notNiceStopPeer(component);
		
		// Start the peer
		req_010_Util.startPeer();
		
		// Expect the Network of favours status to be: rwp1Id, 6, 9; rwp2Id, 6, 8
		knownPeerBalances = new HashMap<String, PeerBalance>();
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(6d, 9d));
		knownPeerBalances.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(6d, 8d));
		networkOfFavorsStatus = new NetworkOfFavorsStatus(knownPeerBalances);
		req_035_Util.getNetworkOfFavoursStatus(networkOfFavorsStatus);
	}

}