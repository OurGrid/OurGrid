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
import java.util.List;
import java.util.Map;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
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
import org.ourgrid.acceptance.worker.WorkerAcceptanceTestCase;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessResultInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkAccounting;
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

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

/**
 *
 */
public class Req_035_Test extends PeerAcceptanceTestCase {

	private static final String COMM_FILE_PATH = "req_035"+File.separator;
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

    @After
	public void tearDown() throws Exception{
		peerAcceptanceUtil.deleteNOFRankingFile();
		super.tearDown();
	}
    
	/**
	 * Verifies a peer without network of favours data. 
	 * It must return an empty list of remote peers.
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.1", reqs="REQ035")
	@Test public void test_AT_035_1_PeerWithoutDonatedFavours() throws Exception{
		
		//Start peer
		component = req_010_Util.startPeer();
		
		//Expect the Netwolocalhostrk of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);

		component = req_010_Util.query(component, "SELECT * from Balance", "No data available");

	}
	
	/**
	 * This test contains the following steps:
	 *	1. The local peer has one idle worker (workerA);
	 *	2. A remote peer 1 requests one worker and obtains it;
	 *	3. The donated worker reports a work accounting for the remote peer 1 (cpu=10, data=12);
	 *	4. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=2);
	 *	5. Expect the Network of favours status to be empty.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.2", reqs="REQ035")
	@Test public void test_AT_035_2_PeerWithDonatedFavoursOnly() throws Exception{
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"035_blank.xml");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, wmADID);
		
		//Request a worker for the remote client
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		String clientPubKey = "clientPubKey";
		
		RemoteWorkerProvider remotePeer = EasyMock.createMock(RemoteWorkerProvider.class);
		DeploymentID remotePeerID = new DeploymentID(new ContainerID("rwpUser", "rwpServer", "PEER"), "PEER");
		
		X509CertPath certificateMock = AcceptanceTestUtil.getCertificateMock(remotePeerID);
		peerAcceptanceUtil.getRemoteWorkerProviderClient().workerProviderIsUp(remotePeer, remotePeerID, 
				certificateMock);
		
		String certSubjectDN = CertificationUtils.getCertSubjectDN(certificateMock);
		
		DeploymentID rwpDID = PeerAcceptanceUtil.createRemoteConsumerID("rwpUser", "rwpServer", clientPubKey);
		
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, rwpDID, requestSpec, 0, allocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, wmADID, workerSpecA, rwpDID);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(certSubjectDN, 10, 12), wmADID);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(certSubjectDN, 5, 2), wmADID);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
	
	}
	
	/**
	 * This test contains the following steps:
   	 *	1. The local peer has one idle worker (workerA);
   	 *	2. A local consumer requests one worker and obtains it;
   	 *	3. The local consumer reports a replica accounting for the request 1 (workerA, cpu=10, data=5);
   	 *	4. The local consumer reports an aborted replica accounting for the request 1 (workerA, cpu=3, data=10);
   	 *	5. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working;
   	 *	6. Expect the Network of favours status to be empty.
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.3", reqs="REQ035")
	@Test public void test_AT_035_3_PeerWithLocallyReceivedFavoursOnly() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, wmADID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request a worker for the logged user
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec1, allocationA);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec1);
		
		//Report a received favour
		req_027_Util.reportReplicaAccounting(component, lwpcDID, new GridProcessAccounting(requestSpec1, 
				wmADID.toString(), wmADID.getPublicKey(), 10., 5., GridProcessState.FINISHED, workerSpecA), true);
		
		//Report a received favour (aborted)
		
		req_027_Util.reportReplicaAccounting(component, lwpcDID, new GridProcessAccounting(requestSpec1, 
				wmADID.toString(), wmADID.getPublicKey(), 10., 3., GridProcessState.ABORTED, workerSpecA), true);
		
		//Finish the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, brokerPubKey, 
				lwpcDID.getServiceID(), requestSpec1, 
				AcceptanceTestUtil.createList(allocationA));
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
	}
	
	/**
	 * This test contains the following steps:
   	 *	1. A local consumer requests 5 workers - the local peer do not have idle workers, 
   	 *		so pass the request for community and schedule the request for repetition;
   	 *	2. _The peer receives two remote workers (R1 e R2) from remote peer rwp1, which are allocated for the local consumer;
   	 *	3. _The peer receives two remote workers (R3 e R4) from remote peer rwp2, which are allocated for the local consumer;
   	 *	4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=1, data=4);
   	 *	5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=3, data=4);
   	 *	6. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=5, data=5);
   	 *	7. The local consumer reports a replica accounting for the request 1 (workerR3, cpu=7, data=4);
   	 *	8. The local consumer reports an aborted replica accounting for the request 1 (workerR4, cpu=4, data=2);
   	 *	9. The local consumer reports a replica accounting for the request 1 (workerR4, cpu=9, data=4);
   	 *	10. Expect the Network of favours status to be empty;
   	 *	11. The local consumer finishes the request 1 - expect the peer to dispose the remote workers;
   	 *	12. Expect the Network of favours status to be:
     *    1. rwp1Id, 15, 13
     *    2. rwp2Id, 12.5, 10
     *
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.4", reqs="REQ035")
	@Test public void test_AT_035_4_PeerWithRemotelyReceivedFavoursOnly() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login
		String broker1PubKey = "broker1PubKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Local consumer request workers
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 6; i++) {
			
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			
			tasks.add(task);
		}
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label", "", tasks), 6, "", 5, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		
		String rwp1PublicKey = "rwp1PublicKey";
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp1User", 
				"rwp1Server", rwp1PublicKey, workerSpec1, workerSpec2);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		//GIS client receive a remote worker provider(rwp2)
		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createWorkerSpec("U3", "S2");
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createWorkerSpec("U4", "S2");
		
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp2User", 
				"rwp2Server", rwp2PublicKey, workerSpec3, workerSpec4);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String worker1PubKey = "worker1PubKey";
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpec1, worker1PubKey, broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpc1OD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm1DID, workerSpec1, requestSpec1);
		
		//Remote worker provider client receive a remote worker (R2 - rwp1)
		String worker2PubKey = "worker2PubKey";
		DeploymentID rwm2DID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpec2, worker2PubKey, broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm2DID, workerSpec2, requestSpec1);
	
		//Remote worker provider client receive a remote worker (R3 - rwp2)
		String worker3PubKey = "worker3PubKey";
		DeploymentID rwm3DID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpec3, worker3PubKey, broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm3DID, workerSpec3, requestSpec1);
	
		//Remote worker provider client receive a remote worker (R4 - rwp2)
		String worker4PubKey = "worker4PubKey";
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		DeploymentID rwm4DID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpec4, worker4PubKey, broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm4DID, workerSpec4, requestSpec1);
	
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec1, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 1., 4., GridProcessState.FINISHED, workerSpec1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec1, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 3., 4., GridProcessState.FINISHED, workerSpec1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);

		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec1, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 5., 5., GridProcessState.FINISHED, workerSpec2);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, false);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec1, rwm3DID.toString(), 
				rwm3DID.getPublicKey(), 7., 4., GridProcessState.FINISHED, workerSpec3);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, false);
		
		//Report a received favour
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec1, rwm4DID.toString(), 
				rwm4DID.getPublicKey(), 4., 2., GridProcessState.ABORTED, workerSpec4);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa5, false);
		
		//Report a received favour
		GridProcessAccounting gpa6 = new GridProcessAccounting(requestSpec1, rwm4DID.toString(), 
				rwm4DID.getPublicKey(), 9., 4., GridProcessState.FINISHED, workerSpec4);
		gpa6.setTaskSequenceNumber(6);
		gpa6.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa6, false);
		
		//Expect the Network of favours status to be empty
		/*NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<ServiceID, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);*/
		
		//Finish the request
		WorkerAllocation allocation1 = new WorkerAllocation(rwm1DID);
		WorkerAllocation allocation2 = new WorkerAllocation(rwm2DID);
		WorkerAllocation allocation3 = new WorkerAllocation(rwm3DID);
		WorkerAllocation allocation4 = new WorkerAllocation(rwm4DID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, AcceptanceTestUtil.createList(allocation1, allocation2));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, AcceptanceTestUtil.createList(allocation3, allocation4));
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 

		req_014_Util.finishRequestWithRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), 
				lwpOD, broker1PubKey, lwpcDID.getServiceID(), 
				requestSpec1, new TestStub(rwp1ID, remotePeer1), new TestStub(rwp2ID, remotePeer2));
		
		//Expect the Network of favours status to be: rwp1Id, 15, 13; rwp2Id, 12.5, 10
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(15.0, 13.0));
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(12.5, 10.0));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
	}

	
	/**
	 * This test contains the following steps:
	 *	1. The local peer has one idle worker (workerA);
	 *	2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, 
	 *	so pass the request for community and schedule the request for repetition;
	 *	3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *	4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *	5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *	6. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *	7. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
	 *	8. Expect the Network of favours status to be empty;
	 *	9. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote worker;
	 *	10. Expect the Network of favours status to be:
     *    	1. rwp1Id, 8, 9
     *    
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.5", reqs="REQ035")
	@Test public void test_AT_035_5_RemotePeerRelativePowerCalculus() throws Exception{

		//PeerAcceptanceUtil.recreateSchema();
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 4; i++) {
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
		
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
		String rwpPublicKey = "rwpPublicKey";
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", rwpPublicKey, remoteWorkerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorkerPubKey = "remoteWorkerPubKey";
		DeploymentID rwmDID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec, remoteWorkerPubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwmDID, remoteWorkerSpec, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, rwmDID.toString(), 
				rwmDID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, rwmDID.toString(), 
				rwmDID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
				wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, true);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
				wmADID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, true);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
	
		//Finish the request
		WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
		WorkerAllocation remoteAllocation = new WorkerAllocation(rwmDID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation));

		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), requestSpec, 
				AcceptanceTestUtil.createList(localAllocation), new TestStub(rwpID, remotePeer1));
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
	}
	
	/**
	 * This test contains the following steps:
	 *	1. The local peer has one idle worker (workerA);
	 *	2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, 
	 *		so pass the request for community and schedule the request for repetition;
	 *	3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *	4. _The peer receives one remote worker (R2) from remote peer rwp2, which is allocated for the local consumer;
	 *	5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *  6. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *  7. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=7, data=4);
   	 *	8. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=9, data=4);
   	 *	9. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
   	 *	10. Expect the Network of favours status to be empty;
   	 *	11. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote workers;
   	 *	12. Expect the Network of favours status to be:
     *    1. rwp1Id, 6, 9
     *    2. rwp2Id, 6, 8
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.6", reqs="REQ035")
	@Test public void test_AT_035_6_RelativePowerCalculusWithTwoRemotePeers() throws Exception{ 
		
		//PeerAcceptanceUtil.recreateSchema();
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
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
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", "", tasks), 5, "", 5, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
		String rwp1PublicKey = "rwp1PublicKey";
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp1User", "rwp1Server", 
				rwp1PublicKey, remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		//GIS client receive a remote worker provider(rwp2)
		WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createWorkerSpec("U3", "S3");
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp2User", "rwp2Server", 
				rwp2PublicKey, remoteWorkerSpec2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorker1PubKey = "remoteWorker1PubKey";
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, remoteWorkerSpec1, remoteWorker1PubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm1DID, remoteWorkerSpec1, requestSpec);

		//Remote worker provider client receive a remote worker (R2 - rwp2)
		String remoteWorker2PubKey = "remoteWorker2PubKey";
		DeploymentID rwm2DID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, remoteWorkerSpec2, remoteWorker2PubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm2DID, remoteWorkerSpec2, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);

		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 7., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, false);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 9., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, false);
		
		//Report a received favour
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
				wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa5, true);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Finish the request
		WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
		WorkerAllocation remoteAllocation1 = new WorkerAllocation(rwm1DID);
		WorkerAllocation remoteAllocation2 = new WorkerAllocation(rwm2DID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, AcceptanceTestUtil.createList(remoteAllocation1));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, AcceptanceTestUtil.createList(remoteAllocation2));
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 

		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), null, requestSpec, AcceptanceTestUtil.createList(localAllocation), new TestStub(rwp1ID, remotePeer1), 
				new TestStub(rwp2ID, remotePeer2));
		
		//Expect the Network of favours status to be: rwp1Id, 6, 9; rwp2Id, 6, 8
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(6., 9.));
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(6., 8.));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
	}

	/**
	 * This test contains the following steps:

	 *   1. The local peer has one idle worker (workerA);
	 *   2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, 
	 *   	so pass the request for community and schedule the request for repetition;
	 *   3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *   4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *   5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *   6. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *   7. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
	 *   8. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote worker;
	 *   9. Expect the Network of favours status to be:
	 *         1. rwp1Id, 8, 9
	 *  10. A local consumer requests 5 workers again and obtains the workerA - the local peer do not have all necessary workers, 
	 *  	so pass the request for community and schedule the request for repetition;
	 *  11. _The peer receives one remote worker (R2) from remote peer rwp1, which is allocated for the local consumer;
	 *  12. The local consumer reports a replica accounting for the request 2 (workerR2, cpu=3, data=4);
	 *  13. The local consumer reports a replica accounting for the request 2 (workerR2, cpu=5, data=6);
	 *  14. The local consumer reports a replica accounting for the request 2 (workerA, cpu=5, data=6);
	 *  15. The local consumer reports a replica accounting for the request 2 (workerA, cpu=7, data=5);
	 *  16. Expect the Network of favours status to be:
	 *         1. rwp1Id, 8, 9
	 *  17. The local consumer finishes the request 2 - expect the peer to command the workerA to stop working and dispose the remote worker;
	 *  18. Expect the Network of favours status to be:
	 *         1. rwp1Id, 20, 19

	 * @throws Exception
	 */
	@ReqTest(test="AT-035.7", reqs="REQ035")
	@Test public void test_AT_035_7_DonatedFavoursFromRemotePeerForTwoRequests() throws Exception{ 
		
		//PeerAcceptanceUtil.recreateSchema();
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
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
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", "", tasks), 1, "", 5, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
		String rwpPublicKey = "rwpPublicKey";
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", "rwpServer", 
				rwpPublicKey, remoteWorkerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorkerPubKey = "remoteWorkerPubKey";
		DeploymentID rwmDID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec, remoteWorkerPubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwmDID, remoteWorkerSpec, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);

		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, rwmDID.toString(), 
				rwmDID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, rwmDID.toString(), 
				rwmDID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
				wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, true);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
				wmADID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, true);
		
		//Finish the request
		WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
		WorkerAllocation remoteAllocation = new WorkerAllocation(rwmDID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation));
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 

		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), requestSpec, 
				AcceptanceTestUtil.createList(localAllocation), new TestStub(rwpID, remotePeer1));
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
		
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Request five workers for the logged user
		tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 4; i++) {
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			
			tasks.add(task);
		}
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label", "", tasks), 4, "", 5, 0, 0);
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(rwpID, rwp));
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec2, rwps, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec2);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createWorkerSpec("U3", "S2");
//		rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec2, "rwpUser", "rwpServer", rwpPublicKey, remoteWorkerSpec2);
//		
//		rwp = (RemoteWorkerProvider) rwpStub.getObject();
//		
//		rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorker2PubKey = "remoteWorker2PubKey";
		DeploymentID rwm2DID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec2, remoteWorker2PubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwm2DID, remoteWorkerSpec2, requestSpec2);
		
		//Report a received favour
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec2, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 3., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa5.setTaskSequenceNumber(1);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa5, false);
		
		//Report a received favour
		GridProcessAccounting gpa6 = new GridProcessAccounting(requestSpec2, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 5., 6., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa6.setTaskSequenceNumber(2);
		gpa6.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa6, false);
		
		//Report a received favour
		GridProcessAccounting gpa7 = new GridProcessAccounting(requestSpec2, wmADID.toString(), 
				wmADID.getPublicKey(), 5., 6., GridProcessState.FINISHED, workerSpecA);
		gpa7.setTaskSequenceNumber(3);
		gpa7.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa7, true);
		
		//Report a received favour
		GridProcessAccounting gpa8 = new GridProcessAccounting(requestSpec2, wmADID.toString(), 
				wmADID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecA);
		gpa8.setTaskSequenceNumber(4);
		gpa8.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa8, true);
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9	
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Finish the request
		WorkerAllocation remoteAllocation2 = new WorkerAllocation(rwm2DID);
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation2));

		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), requestSpec2, 
				AcceptanceTestUtil.createList(localAllocation), new TestStub(rwpID, remotePeer2)); 
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap2 = new HashMap<String, PeerBalance>();
		statusMap2.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(20., 19.));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap2);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
	}
	
	/**
	 * This test contains the following steps:
	 *    1. The local peer has one idle worker (workerA);
	 *    2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *    3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *    4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *    5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *    6. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *    7. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
	 *    8. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote worker;
	 *    9. Expect the Network of favours status to be:
	 *          1. rwp1Id, 8, 9
	 *   10. The remote peer rwp1 requests one worker and obtains it;
 	 *   11. The donated worker reports a work accounting for the remote peer 1 (cpu=4, data=4);
	 *   12. Expect the Network of favours status to be:
	 *          1. rwp1Id, 4, 5
	 *   13. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=3);
	 *   14. Expect the Network of favours status to be:
	 *          1. rwp1Id, 0, 2
	 *   15. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=10);
	 *   16. Expect the Network of favours status to be empty.
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.8", reqs="REQ035")
	@Test public void test_AT_035_8_RemotePeerDonatesAndReceivesFavoursUntilClearsItsBalance() throws Exception{ 
		
		//PeerAcceptanceUtil.recreateSchema();
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"035_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
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
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", "", tasks), 1, "", 5, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
		String rwpPublicKey = "rwpPublicKey";
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", rwpPublicKey, remoteWorkerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorkerPubKey = "remoteWorkerPubKey";
		DeploymentID rwmDID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec, remoteWorkerPubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwmDID, remoteWorkerSpec, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, 
				rwmDID.toString(), rwmDID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, 
				rwmDID.toString(), rwmDID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, 
				wmADID.toString(), wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, true);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, 
				wmADID.toString(), wmADID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, true);
		
		//Finish the request
		WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
		WorkerAllocation remoteAllocation = new WorkerAllocation(rwmDID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation));

		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), requestSpec, 
				AcceptanceTestUtil.createList(localAllocation), new TestStub(rwpID, remotePeer1));
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		String certificateDN = AcceptanceTestUtil.getCertificateDN(rwpID);
		statusMap.put(certificateDN, new PeerBalance(8., 9.));
		
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("rwpUser", "rwpServer", rwpPublicKey);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec2, 0, allocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, wmADID, workerSpecA, clientID);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(certificateDN, 4, 4), wmADID);
		
		//Expect the Network of favours status to be: rwp1Id, 4, 5
		Map<String, PeerBalance> statusMap2 = new HashMap<String, PeerBalance>();
		statusMap2.put(certificateDN, new PeerBalance(4., 5.));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap2);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(certificateDN, 5, 3), wmADID);
		
		//Expect the Network of favours status to be: rwp1Id, 0, 2
		Map<String, PeerBalance> statusMap3 = new HashMap<String, PeerBalance>();
		statusMap3.put(certificateDN, new PeerBalance(0., 2.));
		
		NetworkOfFavorsStatus nofStatus3 = new NetworkOfFavorsStatus(statusMap3);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus3);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(certificateDN, 5, 10), wmADID);
		
		//Expect the Network of favours status to be empty
		Map<String, PeerBalance> statusMap4 = new HashMap<String, PeerBalance>();
		
		NetworkOfFavorsStatus nofStatus4 = new NetworkOfFavorsStatus(statusMap4);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus4);
		
	}
	
	/**
	 * This test contains the following steps:
	 *    1. The local peer has one idle worker (workerA);
	 *    2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *    3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *    4. _The peer receives one remote worker (R2) from remote peer rwp2, which is allocated for the local consumer;
	 *    5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *    6. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *    7. The local consumer reports an aborted replica accounting for the request 1 (workerR1, cpu=3, data=2);
	 *    8. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=7, data=4);
	 *    9. The local consumer reports an aborted replica accounting for the request 1 (workerR2, cpu=8, data=4);
	 *   10. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=9, data=4);
	 *   11. The local consumer reports an aborted replica accounting for the request 1 (workerA, cpu=1, data=2);
	 *   12. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *   13. Expect the Network of favours status to be empty;
	 *   14. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote workers;
	 *   15. Expect the Network of favours status to be:
	 *          1. rwp1Id, 7.5, 11
	 *          2. rwp2Id, 9, 12
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.9", reqs="REQ035")
	@Test public void test_AT_035_9_RelativePowerCalculusWithAbortedReplicas() throws Exception{ 
		 
		//PeerAcceptanceUtil.recreateSchema();
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 8; i++) {
			
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			
			tasks.add(task);
		}
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", "", tasks), 1, "", 8, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		TestStub localWorkerStub = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
		String rwp1PublicKey = "rwp1PublicKey";
		
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp1User", 
				"rwp1Server", rwp1PublicKey, remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		//GIS client receive a remote worker provider(rwp2)
		WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createWorkerSpec("U3", "S3");
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp2User", 
				"rwp2Server", rwp2PublicKey, remoteWorkerSpec2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorker1PubKey = "remoteWorker1PubKey";
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, remoteWorkerSpec1, remoteWorker1PubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm1DID, remoteWorkerSpec1, requestSpec);

		//Remote worker provider client receive a remote worker (R2 - rwp2)
		String remoteWorker2PubKey = "remoteWorker2PubKey";
		DeploymentID rwm2DID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, remoteWorkerSpec2, remoteWorker2PubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm2DID, remoteWorkerSpec2, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);

		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour (aborted)
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 3., 2., GridProcessState.ABORTED, remoteWorkerSpec1);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, false);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 7., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, false);
		
		//Report a received favour (aborted)
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 8., 4., GridProcessState.ABORTED, remoteWorkerSpec2);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa5, false);
		
		//Report a received favour
		GridProcessAccounting gpa6 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 9., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa6.setTaskSequenceNumber(6);
		gpa6.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa6, false);
		
		//Report a received favour (aborted)
		GridProcessAccounting gpa7 = new GridProcessAccounting(requestSpec, 
				localWorkerStub.getDeploymentID().toString(), localWorkerStub.getDeploymentID().getPublicKey(),
				1., 2., GridProcessState.ABORTED, workerSpecA);
		gpa7.setTaskSequenceNumber(7);
		gpa7.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID,gpa7, true);
		
		//Report a received favour
		GridProcessAccounting gpa8 = new GridProcessAccounting(requestSpec, 
				localWorkerStub.getDeploymentID().toString(), localWorkerStub.getDeploymentID().getPublicKey(), 
				3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa8.setTaskSequenceNumber(8);
		gpa8.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa8, true);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Finish the request
		WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
		WorkerAllocation remoteAllocation1 = new WorkerAllocation(rwm1DID);
		WorkerAllocation remoteAllocation2 = new WorkerAllocation(rwm2DID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, AcceptanceTestUtil.createList(remoteAllocation1));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, AcceptanceTestUtil.createList(remoteAllocation2));

		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), null, requestSpec, AcceptanceTestUtil.createList(localAllocation), new TestStub(rwp1ID, remotePeer1), 
				new TestStub(rwp2ID, remotePeer2));
		
		//Expect the Network of favours status to be: rwp1Id, 7.5, 11; rwp2Id, 9, 12
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(7.5, 11.));
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(9., 12.));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
	}
	
	/**
	 * This test contains the following steps:
	 *   1. The local peer has one idle worker (workerA);
	 *   2. The remote peer rwp1 requests one worker and obtains it;
	 *   3. The donated worker reports a work accounting for the remote peer 1 (cpu=4, data=4);
	 *   4. Expect the Network of favours status to be empty;
	 *   5. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=3);
	 *   6. Expect the Network of favours status to be empty;
	 *   7. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *   8. The donated worker reports a work accounting for the remote peer 1 (cpu=1, data=1);
	 *   9. Expect the Network of favours status to be empty;
	 *  10. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *  11. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *  12. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *  13. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *  14. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
	 *  15. Expect the Network of favours status to be empty;
	 *  16. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote worker;
	 *  17. Expect the Network of favours status to be:
	 *         1. rwp1Id, 8, 9
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.10", reqs="REQ035")
	@Test public void test_AT_035_10_RemotePeerReceivesAndDonatesFavours() throws Exception{
		
		//PeerAcceptanceUtil.recreateSchema();
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"035_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Request a worker for the remote client
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 4; i++) {
			
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			
			tasks.add(task);
		}
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", "", tasks), 2, "", 4, 0, 0);
		
		String remoteClientPublicKey = "remoteClientPublicKey";
		DeploymentID remoteClientDID = PeerAcceptanceUtil.createRemoteConsumerID("rwpUser", "rwpServer", remoteClientPublicKey);
		
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, remoteClientDID, requestSpec, 0, allocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, wmADID, workerSpecA, remoteClientDID);
		
		AcceptanceTestUtil.publishTestObject(component, remoteClientDID, rwpc, RemoteWorkerProviderClient.class,
				false);
		AcceptanceTestUtil.notifyRecovery(component, remoteClientDID);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, 
				new WorkAccounting(remoteClientPublicKey, 4., 4.), wmADID);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Report a donated favour
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, 
				new WorkAccounting(remoteClientPublicKey, 5., 3.), wmADID);
		
		//Expect the Network of favours status to be empty
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label", "", tasks), 1, "", 5, 0, 0);
		WorkerAllocation allocationPreemption = new WorkerAllocation(wmADID).addLoserConsumer(remoteClientDID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec2, allocationPreemption);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec2);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec2, "rwpUser", 
				"rwpServer", remoteClientPublicKey, remoteWorkerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorkerPubKey = "remoteWorkerPubKey";
		DeploymentID rwmDID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec, remoteWorkerPubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmDID, remoteWorkerSpec, requestSpec2);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec2, rwmDID.toString(), 
				rwmDID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec2, rwmDID.toString(), 
				rwmDID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec2, wmADID.toString(), 
				wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, true);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec2, wmADID.toString(), 
				wmADID.getPublicKey(),5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, true);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
		//Finish the request
		WorkerAllocation remoteAllocation = new WorkerAllocation(rwmDID);
		RemoteAllocation remotePeer = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation));

		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), requestSpec2, AcceptanceTestUtil.createList(allocationA), new TestStub(rwpID, remotePeer));
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
		
		NetworkOfFavorsStatus nofStatus3 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus3);
	}

	/**
	 * This test contains the following steps:
	 *	1. The local peer has one idle worker (workerA);
	 *	2. A remote peer 1 requests one worker and obtains it;
	 *	3. The donated worker reports a work accounting for the remote peer 1 (cpu=10, data=12);
	 *	4. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=2);
	 *	5. Expect the Network of favours status to be empty.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.2", reqs="REQ035")
	@Category(JDLCompliantTest.class) 
	@Test public void test_AT_035_2_PeerWithDonatedFavoursOnlyWithJDL() throws Exception{
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"035_blank.xml");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, wmADID);
		
		//Request a worker for the remote client
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		String clientPubKey = "clientPubKey";
		
		RemoteWorkerProvider remotePeer = EasyMock.createMock(RemoteWorkerProvider.class);
		DeploymentID remotePeerID = new DeploymentID(new ContainerID("rwpUser", "rwpServer", "PEER"), "PEER");
		
		X509CertPath certificateMock = AcceptanceTestUtil.getCertificateMock(remotePeerID);
		peerAcceptanceUtil.getRemoteWorkerProviderClient().workerProviderIsUp(remotePeer, remotePeerID, 
				certificateMock);
		
		String rwpDN = CertificationUtils.getCertSubjectDN(certificateMock);
		
		DeploymentID rwpDID = PeerAcceptanceUtil.createRemoteConsumerID("rwpUser", "rwpServer", clientPubKey);
		
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, rwpDID, requestSpec, 0, allocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, wmADID, workerSpecA, rwpDID);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwpDN, 10, 12), wmADID);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwpDN, 5, 2), wmADID);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
	
	}

	/**
	 * This test contains the following steps:
	 *	1. The local peer has one idle worker (workerA);
	 *	2. A local consumer requests one worker and obtains it;
	 *	3. The local consumer reports a replica accounting for the request 1 (workerA, cpu=10, data=5);
	 *	4. The local consumer reports an aborted replica accounting for the request 1 (workerA, cpu=3, data=10);
	 *	5. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working;
	 *	6. Expect the Network of favours status to be empty.
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.3", reqs="REQ035")
	@Category(JDLCompliantTest.class) 
	@Test public void test_AT_035_3_PeerWithLocallyReceivedFavoursOnlyWithJDL() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, wmADID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request a worker for the logged user
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec1, allocationA);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec1);
		
		//Report a received favour
		req_027_Util.reportReplicaAccounting(component, lwpcDID, new GridProcessAccounting(requestSpec1, 
				wmADID.toString(), wmADID.getPublicKey(), 10., 5., GridProcessState.FINISHED, workerSpecA), true);
		
		//Report a received favour (aborted)
		
		req_027_Util.reportReplicaAccounting(component, lwpcDID, new GridProcessAccounting(requestSpec1, 
				wmADID.toString(), wmADID.getPublicKey(), 10., 3., GridProcessState.ABORTED, workerSpecA), true);
		
		//Finish the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), 
				lwpOD, brokerPubKey, lwpcDID.getServiceID(), requestSpec1, 
				AcceptanceTestUtil.createList(allocationA));
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
	}

	/**
	 * This test contains the following steps:
	 *	1. A local consumer requests 5 workers - the local peer do not have idle workers, 
	 *		so pass the request for community and schedule the request for repetition;
	 *	2. _The peer receives two remote workers (R1 e R2) from remote peer rwp1, which are allocated for the local consumer;
	 *	3. _The peer receives two remote workers (R3 e R4) from remote peer rwp2, which are allocated for the local consumer;
	 *	4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=1, data=4);
	 *	5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=3, data=4);
	 *	6. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=5, data=5);
	 *	7. The local consumer reports a replica accounting for the request 1 (workerR3, cpu=7, data=4);
	 *	8. The local consumer reports an aborted replica accounting for the request 1 (workerR4, cpu=4, data=2);
	 *	9. The local consumer reports a replica accounting for the request 1 (workerR4, cpu=9, data=4);
	 *	10. Expect the Network of favours status to be empty;
	 *	11. The local consumer finishes the request 1 - expect the peer to dispose the remote workers;
	 *	12. Expect the Network of favours status to be:
	 *    1. rwp1Id, 15, 13
	 *    2. rwp2Id, 12.5, 10
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.4", reqs="REQ035")
	@Category(JDLCompliantTest.class) 
	@Test public void test_AT_035_4_PeerWithRemotelyReceivedFavoursOnlyWithJDL() throws Exception{
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login
		String broker1PubKey = "broker1PubKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Local consumer request workers
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 6; i++) {
			
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			
			tasks.add(task);
		}
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 6, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		
		String rwp1PublicKey = "rwp1PublicKey";
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp1User", 
				"rwp1Server", rwp1PublicKey, workerSpec1, workerSpec2);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		//GIS client receive a remote worker provider(rwp2)
		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S2", null, null);
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S2", null, null);
		
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp2User", 
				"rwp2Server", rwp2PublicKey, workerSpec3, workerSpec4);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String worker1PubKey = "worker1PubKey";
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpec1, worker1PubKey, broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpc1OD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm1DID, workerSpec1, requestSpec1);
		
		//Remote worker provider client receive a remote worker (R2 - rwp1)
		String worker2PubKey = "worker2PubKey";
		DeploymentID rwm2DID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpec2, worker2PubKey, broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm2DID, workerSpec2, requestSpec1);
	
		//Remote worker provider client receive a remote worker (R3 - rwp2)
		String worker3PubKey = "worker3PubKey";
		DeploymentID rwm3DID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpec3, worker3PubKey, broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm3DID, workerSpec3, requestSpec1);
	
		//Remote worker provider client receive a remote worker (R4 - rwp2)
		String worker4PubKey = "worker4PubKey";
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		DeploymentID rwm4DID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, workerSpec4, worker4PubKey, broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm4DID, workerSpec4, requestSpec1);
	
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec1, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 1., 4., GridProcessState.FINISHED, workerSpec1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec1, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 3., 4., GridProcessState.FINISHED, workerSpec1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
	
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec1, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 5., 5., GridProcessState.FINISHED, workerSpec2);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, false);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec1, rwm3DID.toString(), 
				rwm3DID.getPublicKey(), 7., 4., GridProcessState.FINISHED, workerSpec3);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, false);
		
		//Report a received favour
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec1, rwm4DID.toString(), 
				rwm4DID.getPublicKey(), 4., 2., GridProcessState.ABORTED, workerSpec4);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa5, false);
		
		//Report a received favour
		GridProcessAccounting gpa6 = new GridProcessAccounting(requestSpec1, rwm4DID.toString(), 
				rwm4DID.getPublicKey(), 9., 4., GridProcessState.FINISHED, workerSpec4);
		gpa6.setTaskSequenceNumber(6);
		gpa6.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa6, false);
		
		//Expect the Network of favours status to be empty
		/*NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<ServiceID, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);*/
		
		//Finish the request
		WorkerAllocation allocation1 = new WorkerAllocation(rwm1DID);
		WorkerAllocation allocation2 = new WorkerAllocation(rwm2DID);
		WorkerAllocation allocation3 = new WorkerAllocation(rwm3DID);
		WorkerAllocation allocation4 = new WorkerAllocation(rwm4DID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, AcceptanceTestUtil.createList(allocation1, allocation2));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, AcceptanceTestUtil.createList(allocation3, allocation4));
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
	
		req_014_Util.finishRequestWithRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), 
				lwpOD, broker1PubKey, lwpcDID.getServiceID(), 
				requestSpec1, new TestStub(rwp1ID, remotePeer1), new TestStub(rwp2ID, remotePeer2));
		
		//Expect the Network of favours status to be: rwp1Id, 15, 13; rwp2Id, 12.5, 10
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(15.0, 13.0));
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(12.5, 10.0));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
	}

	/**
	 * This test contains the following steps:
	 *	1. The local peer has one idle worker (workerA);
	 *	2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, 
	 *	so pass the request for community and schedule the request for repetition;
	 *	3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *	4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *	5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *	6. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *	7. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
	 *	8. Expect the Network of favours status to be empty;
	 *	9. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote worker;
	 *	10. Expect the Network of favours status to be:
	 *    	1. rwp1Id, 8, 9
	 *    
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.5", reqs="REQ035")
	@Category(JDLCompliantTest.class) 
	@Test public void test_AT_035_5_RemotePeerRelativePowerCalculusWithJDL() throws Exception{
	
		//PeerAcceptanceUtil.recreateSchema();
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);

		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 4; i++) {
			
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			
			tasks.add(task);
		}
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 4, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S2", null, null);
		String rwpPublicKey = "rwpPublicKey";
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", rwpPublicKey, remoteWorkerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorkerPubKey = "remoteWorkerPubKey";
		DeploymentID rwmDID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec, remoteWorkerPubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwmDID, remoteWorkerSpec, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, rwmDID.toString(), 
				rwmDID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, rwmDID.toString(), 
				rwmDID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
				wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, true);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
				wmADID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, true);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
	
		//Finish the request
		WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
		WorkerAllocation remoteAllocation = new WorkerAllocation(rwmDID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation));
	
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), requestSpec, 
				AcceptanceTestUtil.createList(localAllocation), new TestStub(rwpID, remotePeer1));
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
	}

	/**
	 * This test contains the following steps:
	 *	1. The local peer has one idle worker (workerA);
	 *	2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, 
	 *		so pass the request for community and schedule the request for repetition;
	 *	3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *	4. _The peer receives one remote worker (R2) from remote peer rwp2, which is allocated for the local consumer;
	 *	5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *  6. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *  7. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=7, data=4);
	 *	8. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=9, data=4);
	 *	9. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *	10. Expect the Network of favours status to be empty;
	 *	11. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote workers;
	 *	12. Expect the Network of favours status to be:
	 *    1. rwp1Id, 6, 9
	 *    2. rwp2Id, 6, 8
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.6", reqs="REQ035")
	@Category(JDLCompliantTest.class) 
	@Test public void test_AT_035_6_RelativePowerCalculusWithTwoRemotePeersWithJDL() throws Exception{ 
		
		//PeerAcceptanceUtil.recreateSchema();
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
	
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
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
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 5, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S2", null, null);
		String rwp1PublicKey = "rwp1PublicKey";
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp1User", "rwp1Server", 
				rwp1PublicKey, remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		//GIS client receive a remote worker provider(rwp2)
		WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S3", null, null);
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp2User", "rwp2Server", 
				rwp2PublicKey, remoteWorkerSpec2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorker1PubKey = "remoteWorker1PubKey";
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, remoteWorkerSpec1, remoteWorker1PubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm1DID, remoteWorkerSpec1, requestSpec);
	
		//Remote worker provider client receive a remote worker (R2 - rwp2)
		String remoteWorker2PubKey = "remoteWorker2PubKey";
		DeploymentID rwm2DID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, remoteWorkerSpec2, remoteWorker2PubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm2DID, remoteWorkerSpec2, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
	
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 7., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, false);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 9., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, false);
		
		//Report a received favour
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
				wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa5, true);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Finish the request
		WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
		WorkerAllocation remoteAllocation1 = new WorkerAllocation(rwm1DID);
		WorkerAllocation remoteAllocation2 = new WorkerAllocation(rwm2DID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, AcceptanceTestUtil.createList(remoteAllocation1));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, AcceptanceTestUtil.createList(remoteAllocation2));
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
	
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), null, requestSpec, 
				AcceptanceTestUtil.createList(localAllocation), new TestStub(rwp1ID, remotePeer1), 
				new TestStub(rwp2ID, remotePeer2));
		
		//Expect the Network of favours status to be: rwp1Id, 6, 9; rwp2Id, 6, 8
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(6., 9.));
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(6., 8.));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
	}

	/**
		 * This test contains the following steps:
	
		 *   1. The local peer has one idle worker (workerA);
		 *   2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, 
		 *   	so pass the request for community and schedule the request for repetition;
		 *   3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
		 *   4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
		 *   5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
		 *   6. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
		 *   7. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
		 *   8. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote worker;
		 *   9. Expect the Network of favours status to be:
		 *         1. rwp1Id, 8, 9
		 *  10. A local consumer requests 5 workers again and obtains the workerA - the local peer do not have all necessary workers, 
		 *  	so pass the request for community and schedule the request for repetition;
		 *  11. _The peer receives one remote worker (R2) from remote peer rwp1, which is allocated for the local consumer;
		 *  12. The local consumer reports a replica accounting for the request 2 (workerR2, cpu=3, data=4);
		 *  13. The local consumer reports a replica accounting for the request 2 (workerR2, cpu=5, data=6);
		 *  14. The local consumer reports a replica accounting for the request 2 (workerA, cpu=5, data=6);
		 *  15. The local consumer reports a replica accounting for the request 2 (workerA, cpu=7, data=5);
		 *  16. Expect the Network of favours status to be:
		 *         1. rwp1Id, 8, 9
		 *  17. The local consumer finishes the request 2 - expect the peer to command the workerA to stop working and dispose the remote worker;
		 *  18. Expect the Network of favours status to be:
		 *         1. rwp1Id, 20, 19
	
		 * @throws Exception
		 */
		@ReqTest(test="AT-035.7", reqs="REQ035")
		@Category(JDLCompliantTest.class) 
		@Test public void test_AT_035_7_DonatedFavoursFromRemotePeerForTwoRequestsWithJDL() throws Exception{ 
			
			//PeerAcceptanceUtil.recreateSchema();
			
			//Create an user account
			XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
			
			//Start peer and set mocks for logger and timer
			component = req_010_Util.startPeer();
			
			//DiscoveryService recovery
			DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
					DiscoveryServiceConstants.DS_OBJECT_NAME);
			req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
			
			//Worker login
			String workerAPublicKey = "workerAPublicKey";
			WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);

			DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
			req_010_Util.workerLogin(component, workerSpecA, wmADID);
			
			//Change worker status to IDLE
			req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
			
			//Login with a valid user
			String brokerPubKey = "brokerPubKey";
			
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
			
			DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
			LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
			
			//Request five workers for the logged user
			List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
			for (int i = 0; i < 4; i++) {
				
				IOBlock initBlock = new IOBlock();
				initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
				IOBlock finalBlock = new IOBlock();
				finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
				finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
				TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
				task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
				
				tasks.add(task);
			}
			
			RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 4, 0, 0);
			
			WorkerAllocation allocationA = new WorkerAllocation(wmADID);
			req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
			
			//Change worker A status to ALLOCATED FOR BROKER
			ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
			
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
			
			//GIS client receive a remote worker provider(rwp1)
			WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S2", null, null);
			String rwpPublicKey = "rwpPublicKey";
			TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", "rwpServer", 
					rwpPublicKey, remoteWorkerSpec);
			
			RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
			
			DeploymentID rwpID = rwpStub.getDeploymentID();
			
			//Remote worker provider client receive a remote worker (R1 - rwp1)
			String remoteWorkerPubKey = "remoteWorkerPubKey";
			DeploymentID rwmDID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec, remoteWorkerPubKey, brokerPubKey).getDeploymentID();
			
			//Change worker status to ALLOCATED FOR BROKER
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
					peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
					rwmDID, remoteWorkerSpec, requestSpec);
			
			GridProcessResultInfo resultInfo = new GridProcessResultInfo();
			resultInfo.setExitValue(0);
	
			//Report a received favour
			GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, rwmDID.toString(), 
					rwmDID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec);
			gpa1.setTaskSequenceNumber(1);
			gpa1.setResultInfo(resultInfo);
			AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
			req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
			
			//Report a received favour
			GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, rwmDID.toString(), 
					rwmDID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec);
			gpa2.setTaskSequenceNumber(2);
			gpa2.setResultInfo(resultInfo);
			AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
			req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
			
			//Report a received favour
			GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
					wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
			gpa3.setTaskSequenceNumber(3);
			gpa3.setResultInfo(resultInfo);
			AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
			req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, true);
			
			//Report a received favour
			GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, wmADID.toString(), 
					wmADID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
			gpa4.setTaskSequenceNumber(4);
			gpa4.setResultInfo(resultInfo);
			AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
			req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, true);
			
			//Finish the request
			WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
			WorkerAllocation remoteAllocation = new WorkerAllocation(rwmDID);
			
			RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation));
			
			ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
	
			req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
					brokerPubKey, lwpcDID.getServiceID(), requestSpec, 
					AcceptanceTestUtil.createList(localAllocation), new TestStub(rwpID, remotePeer1));
			
			//Expect the Network of favours status to be: rwp1Id, 8, 9
			Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
			statusMap.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
			
			NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(statusMap);
			req_035_Util.getNetworkOfFavoursStatus(nofStatus);
			
			//Request five workers for the logged user
			tasks = new ArrayList<TaskSpecification>();
			for (int i = 0; i < 4; i++) {
				
				IOBlock initBlock = new IOBlock();
				initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
				IOBlock finalBlock = new IOBlock();
				finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
				finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
				TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
				task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
				
				tasks.add(task);
			}
			
			RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 4, 0, 0);
			List<TestStub> rwps = new ArrayList<TestStub>();
			rwps.add(new TestStub(rwpID, rwp));
			req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec2, rwps, allocationA);
			
			//Change worker A status to ALLOCATED FOR BROKER
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec2);
			
			//GIS client receive a remote worker provider(rwp1)
			WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S2", null, null);
	//		rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec2, "rwpUser", "rwpServer", rwpPublicKey, remoteWorkerSpec2);
	//		
	//		rwp = (RemoteWorkerProvider) rwpStub.getObject();
	//		
	//		rwpID = rwpStub.getDeploymentID();
			
			//Remote worker provider client receive a remote worker (R1 - rwp1)
			String remoteWorker2PubKey = "remoteWorker2PubKey";
			DeploymentID rwm2DID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec2, remoteWorker2PubKey, brokerPubKey).getDeploymentID();
			
			//Change worker status to ALLOCATED FOR BROKER
			req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
					peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwm2DID, remoteWorkerSpec2, requestSpec2);
			
			//Report a received favour
			GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec2, rwm2DID.toString(), 
					rwm2DID.getPublicKey(), 3., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
			gpa5.setTaskSequenceNumber(1);
			gpa5.setResultInfo(resultInfo);
			AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
			req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa5, false);
			
			//Report a received favour
			GridProcessAccounting gpa6 = new GridProcessAccounting(requestSpec2, rwm2DID.toString(), 
					rwm2DID.getPublicKey(), 5., 6., GridProcessState.FINISHED, remoteWorkerSpec2);
			gpa6.setTaskSequenceNumber(2);
			gpa6.setResultInfo(resultInfo);
			AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
			req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa6, false);
			
			//Report a received favour
			GridProcessAccounting gpa7 = new GridProcessAccounting(requestSpec2, wmADID.toString(), 
					wmADID.getPublicKey(), 5., 6., GridProcessState.FINISHED, workerSpecA);
			gpa7.setTaskSequenceNumber(3);
			gpa7.setResultInfo(resultInfo);
			AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
			req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa7, true);
			
			//Report a received favour
			GridProcessAccounting gpa8 = new GridProcessAccounting(requestSpec2, wmADID.toString(), 
					wmADID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecA);
			gpa8.setTaskSequenceNumber(4);
			gpa8.setResultInfo(resultInfo);
			AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
			req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa8, true);
			
			//Expect the Network of favours status to be: rwp1Id, 8, 9	
			req_035_Util.getNetworkOfFavoursStatus(nofStatus);
			
			//Finish the request
			WorkerAllocation remoteAllocation2 = new WorkerAllocation(rwm2DID);
			RemoteAllocation remotePeer2 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation2));
	
			req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
					brokerPubKey, lwpcDID.getServiceID(), requestSpec2, 
					AcceptanceTestUtil.createList(localAllocation), new TestStub(rwpID, remotePeer2)); 
			
			//Expect the Network of favours status to be: rwp1Id, 8, 9
			Map<String, PeerBalance> statusMap2 = new HashMap<String, PeerBalance>();
			statusMap2.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(20., 19.));
			
			NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap2);
			req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
			
		}

	/**
	 * This test contains the following steps:
	 *    1. The local peer has one idle worker (workerA);
	 *    2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *    3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *    4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *    5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *    6. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *    7. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
	 *    8. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote worker;
	 *    9. Expect the Network of favours status to be:
	 *          1. rwp1Id, 8, 9
	 *   10. The remote peer rwp1 requests one worker and obtains it;
	 *   11. The donated worker reports a work accounting for the remote peer 1 (cpu=4, data=4);
	 *   12. Expect the Network of favours status to be:
	 *          1. rwp1Id, 4, 5
	 *   13. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=3);
	 *   14. Expect the Network of favours status to be:
	 *          1. rwp1Id, 0, 2
	 *   15. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=10);
	 *   16. Expect the Network of favours status to be empty.
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.8", reqs="REQ035")
	@Category(JDLCompliantTest.class) 
	@Test public void test_AT_035_8_RemotePeerDonatesAndReceivesFavoursUntilClearsItsBalanceWithJDL() throws Exception{ 
		
		//PeerAcceptanceUtil.recreateSchema();
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"035_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
	
		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 4; i++) {
			
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			
			tasks.add(task);
		}
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 4, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S2", null, null);
		String rwpPublicKey = "rwpPublicKey";
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwpUser", 
				"rwpServer", rwpPublicKey, remoteWorkerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorkerPubKey = "remoteWorkerPubKey";
		DeploymentID rwmDID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec, remoteWorkerPubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwmDID, remoteWorkerSpec, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, 
				rwmDID.toString(), rwmDID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, 
				rwmDID.toString(), rwmDID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, 
				wmADID.toString(), wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, true);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, 
				wmADID.toString(), wmADID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, true);
		
		//Finish the request
		WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
		WorkerAllocation remoteAllocation = new WorkerAllocation(rwmDID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation));
	
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), requestSpec, 
				AcceptanceTestUtil.createList(localAllocation), new TestStub(rwpID, remotePeer1));
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		String rwpDN = AcceptanceTestUtil.getCertificateDN(rwpID);
		statusMap.put(rwpDN, new PeerBalance(8., 9.));
		
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Request a worker for the remote client
		DeploymentID clientID = PeerAcceptanceUtil.createRemoteConsumerID("rwpUser", "rwpServer", rwpPublicKey);
		
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, clientID, requestSpec2, 0, allocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, wmADID, workerSpecA, clientID);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwpDN, 4, 4), wmADID);
		
		//Expect the Network of favours status to be: rwp1Id, 4, 5
		Map<String, PeerBalance> statusMap2 = new HashMap<String, PeerBalance>();
		statusMap2.put(rwpDN, new PeerBalance(4., 5.));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap2);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwpDN, 5, 3), wmADID);
		
		//Expect the Network of favours status to be: rwp1Id, 0, 2
		Map<String, PeerBalance> statusMap3 = new HashMap<String, PeerBalance>();
		statusMap3.put(rwpDN, new PeerBalance(0., 2.));
		
		NetworkOfFavorsStatus nofStatus3 = new NetworkOfFavorsStatus(statusMap3);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus3);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwpDN, 5, 10), wmADID);
		
		//Expect the Network of favours status to be empty
		Map<String, PeerBalance> statusMap4 = new HashMap<String, PeerBalance>();
		
		NetworkOfFavorsStatus nofStatus4 = new NetworkOfFavorsStatus(statusMap4);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus4);
		
	}

	/**
	 * This test contains the following steps:
	 *    1. The local peer has one idle worker (workerA);
	 *    2. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *    3. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *    4. _The peer receives one remote worker (R2) from remote peer rwp2, which is allocated for the local consumer;
	 *    5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *    6. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *    7. The local consumer reports an aborted replica accounting for the request 1 (workerR1, cpu=3, data=2);
	 *    8. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=7, data=4);
	 *    9. The local consumer reports an aborted replica accounting for the request 1 (workerR2, cpu=8, data=4);
	 *   10. The local consumer reports a replica accounting for the request 1 (workerR2, cpu=9, data=4);
	 *   11. The local consumer reports an aborted replica accounting for the request 1 (workerA, cpu=1, data=2);
	 *   12. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *   13. Expect the Network of favours status to be empty;
	 *   14. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote workers;
	 *   15. Expect the Network of favours status to be:
	 *          1. rwp1Id, 7.5, 11
	 *          2. rwp2Id, 9, 12
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.9", reqs="REQ035")
	@Category(JDLCompliantTest.class) 
	@Test public void test_AT_035_9_RelativePowerCalculusWithAbortedReplicasWithJDL() throws Exception{ 
		 
		//PeerAcceptanceUtil.recreateSchema();
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);

		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 8; i++) {
			
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			
			tasks.add(task);
		}
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 8, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec, allocationA);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		TestStub localWorkerStub = req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S2", null, null);
		String rwp1PublicKey = "rwp1PublicKey";
		
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp1User", 
				"rwp1Server", rwp1PublicKey, remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		//GIS client receive a remote worker provider(rwp2)
		WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S3", null, null);
		String rwp2PublicKey = "rwp2PublicKey";
		TestStub rwp2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec, "rwp2User", 
				"rwp2Server", rwp2PublicKey, remoteWorkerSpec2);
		
		RemoteWorkerProvider rwp2 = (RemoteWorkerProvider) rwp2Stub.getObject();
		
		DeploymentID rwp2ID = rwp2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorker1PubKey = "remoteWorker1PubKey";
		DeploymentID rwm1DID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, remoteWorkerSpec1, remoteWorker1PubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm1DID, remoteWorkerSpec1, requestSpec);
	
		//Remote worker provider client receive a remote worker (R2 - rwp2)
		String remoteWorker2PubKey = "remoteWorker2PubKey";
		DeploymentID rwm2DID = req_018_Util.receiveRemoteWorker(component, rwp2, rwp2ID, remoteWorkerSpec2, remoteWorker2PubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwm2DID, remoteWorkerSpec2, requestSpec);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
	
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour (aborted)
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec, rwm1DID.toString(), 
				rwm1DID.getPublicKey(), 3., 2., GridProcessState.ABORTED, remoteWorkerSpec1);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, false);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 7., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, false);
		
		//Report a received favour (aborted)
		GridProcessAccounting gpa5 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 8., 4., GridProcessState.ABORTED, remoteWorkerSpec2);
		gpa5.setTaskSequenceNumber(5);
		gpa5.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa5, false);
		
		//Report a received favour
		GridProcessAccounting gpa6 = new GridProcessAccounting(requestSpec, rwm2DID.toString(), 
				rwm2DID.getPublicKey(), 9., 4., GridProcessState.FINISHED, remoteWorkerSpec2);
		gpa6.setTaskSequenceNumber(6);
		gpa6.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp2ID, rwp2, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa6, false);
		
		//Report a received favour (aborted)
		GridProcessAccounting gpa7 = new GridProcessAccounting(requestSpec, 
				localWorkerStub.getDeploymentID().toString(), localWorkerStub.getDeploymentID().getPublicKey(),
				1., 2., GridProcessState.ABORTED, workerSpecA);
		gpa7.setTaskSequenceNumber(7);
		gpa7.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID,gpa7, true);
		
		//Report a received favour
		GridProcessAccounting gpa8 = new GridProcessAccounting(requestSpec, 
				localWorkerStub.getDeploymentID().toString(), localWorkerStub.getDeploymentID().getPublicKey(), 
				3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa8.setTaskSequenceNumber(8);
		gpa8.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa8, true);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Finish the request
		WorkerAllocation localAllocation = new WorkerAllocation(wmADID);
		WorkerAllocation remoteAllocation1 = new WorkerAllocation(rwm1DID);
		WorkerAllocation remoteAllocation2 = new WorkerAllocation(rwm2DID);
		
		RemoteAllocation remotePeer1 = new RemoteAllocation(rwp1, AcceptanceTestUtil.createList(remoteAllocation1));
		RemoteAllocation remotePeer2 = new RemoteAllocation(rwp2, AcceptanceTestUtil.createList(remoteAllocation2));
	
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), null, requestSpec, 
				AcceptanceTestUtil.createList(localAllocation), new TestStub(rwp1ID, remotePeer1), 
				new TestStub(rwp2ID, remotePeer2));
		
		//Expect the Network of favours status to be: rwp1Id, 7.5, 11; rwp2Id, 9, 12
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp1ID), new PeerBalance(7.5, 11.));
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwp2ID), new PeerBalance(9., 12.));
		
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
	}

	/**
	 * This test contains the following steps:
	 *   1. The local peer has one idle worker (workerA);
	 *   2. The remote peer rwp1 requests one worker and obtains it;
	 *   3. The donated worker reports a work accounting for the remote peer 1 (cpu=4, data=4);
	 *   4. Expect the Network of favours status to be empty;
	 *   5. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=3);
	 *   6. Expect the Network of favours status to be empty;
	 *   7. A local consumer requests 5 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	 *   8. The donated worker reports a work accounting for the remote peer 1 (cpu=1, data=1);
	 *   9. Expect the Network of favours status to be empty;
	 *  10. _The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	 *  11. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	 *  12. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	 *  13. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	 *  14. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
	 *  15. Expect the Network of favours status to be empty;
	 *  16. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote worker;
	 *  17. Expect the Network of favours status to be:
	 *         1. rwp1Id, 8, 9
	 * @throws Exception
	 */
	@ReqTest(test="AT-035.10", reqs="REQ035")
	@Category(JDLCompliantTest.class) 
	@Test public void test_AT_035_10_RemotePeerReceivesAndDonatesFavoursWithJDL() throws Exception{
		
		//PeerAcceptanceUtil.recreateSchema();
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"035_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		String workerAPublicKey = "workerAPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);

		DeploymentID wmADID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmADID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdleWithAdvert(component, workerSpecA, wmADID, dsID);
		
		//Request a worker for the remote client
		WorkerAllocation allocationA = new WorkerAllocation(wmADID);
		
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		for (int i = 0; i < 4; i++) {
			
			IOBlock initBlock = new IOBlock();
			initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
			IOBlock finalBlock = new IOBlock();
			finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
			finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
			TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
			task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
			
			tasks.add(task);
		}
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 4, 0, 0);
		
		String remoteClientPublicKey = "remoteClientPublicKey";
		DeploymentID remoteClientDID = PeerAcceptanceUtil.createRemoteConsumerID("rwpUser", "rwpServer", remoteClientPublicKey);
		
		RemoteWorkerProviderClient rwpc = req_011_Util.requestForRemoteClient(component, remoteClientDID, requestSpec, 0, allocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, wmADID, workerSpecA, remoteClientDID);
		
		AcceptanceTestUtil.publishTestObject(component, remoteClientDID, rwpc, RemoteWorkerProviderClient.class,
				false);
		AcceptanceTestUtil.notifyRecovery(component, remoteClientDID);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, 
				new WorkAccounting(remoteClientPublicKey, 4., 4.), wmADID);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Report a donated favour
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, 
				new WorkAccounting(remoteClientPublicKey, 5., 3.), wmADID);
		
		//Expect the Network of favours status to be empty
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubKey";
		
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
		
		DeploymentID lwpcDID = req_108_Util.login(component, user, brokerPubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcDID);
		
		//Request five workers for the logged user
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 5, 0, 0);
		WorkerAllocation allocationPreemption = new WorkerAllocation(wmADID).addLoserConsumer(remoteClientDID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcDID, lwpc), requestSpec2, allocationPreemption);
		
		//Change worker A status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcDID, AcceptanceTestUtil.getBoundObject(lwpcDID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcDID, wmADID, workerSpecA, requestSpec2);
		
		//GIS client receive a remote worker provider(rwp1)
		WorkerSpecification remoteWorkerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S2", null, null);
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec2, "rwpUser", 
				"rwpServer", remoteClientPublicKey, remoteWorkerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (R1 - rwp1)
		String remoteWorkerPubKey = "remoteWorkerPubKey";
		DeploymentID rwmDID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec, remoteWorkerPubKey, brokerPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwmDID, remoteWorkerSpec, requestSpec2);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		//Report a received favour
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec2, rwmDID.toString(), 
				rwmDID.getPublicKey(), 5., 4., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec2, rwmDID.toString(), 
				rwmDID.getPublicKey(), 7., 5., GridProcessState.FINISHED, remoteWorkerSpec);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec2, wmADID.toString(), 
				wmADID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa3, true);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec2, wmADID.toString(), 
				wmADID.getPublicKey(),5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcDID, gpa4, true);
		
		//Expect the Network of favours status to be empty
		NetworkOfFavorsStatus nofStatus2 = new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>());
		req_035_Util.getNetworkOfFavoursStatus(nofStatus2);
		
		//Finish the request
		WorkerAllocation remoteAllocation = new WorkerAllocation(rwmDID);
		RemoteAllocation remotePeer = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteAllocation));
	
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				brokerPubKey, lwpcDID.getServiceID(), requestSpec2, 
				AcceptanceTestUtil.createList(allocationA), new TestStub(rwpID, remotePeer));
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(AcceptanceTestUtil.getCertificateDN(rwpID), new PeerBalance(8., 9.));
		
		NetworkOfFavorsStatus nofStatus3 = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus3);
	}
}
