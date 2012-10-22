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
import java.util.concurrent.ScheduledExecutorService;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.JDLUtils;
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
import org.ourgrid.acceptance.util.peer.Req_034_Util;
import org.ourgrid.acceptance.util.peer.Req_035_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_119_Util;
import org.ourgrid.acceptance.worker.WorkerAcceptanceTestCase;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
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

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0038 extends PeerAcceptanceTestCase {
	//FIXME
	public static final String COMM_FILE_PATH = "it_0038"+File.separator;
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
    private Req_034_Util req_034_Util = new Req_034_Util(getComponentContext());
    private Req_035_Util req_035_Util = new Req_035_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_119_Util req_119_Util = new Req_119_Util(getComponentContext());

    @After 
	public void tearDown() throws Exception{
    	super.tearDown();
		peerAcceptanceUtil.deleteNOFRankingFile();
	}
	
	/**
	* This test contains the following steps:
	*
	*  1. The local peer has one idle worker (workerA);
	*  2. A local consumer requests 2 workers and obtains the workerA - the local peer do not have all necessary workers, so pass the request for community and schedule the request for repetition;
	*  3. The peer receives one remote worker (R1) from remote peer rwp1, which is allocated for the local consumer;
	*  4. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=5, data=4);
	*  5. The local consumer reports a replica accounting for the request 1 (workerR1, cpu=7, data=5);
	*  6. The local consumer reports a replica accounting for the request 1 (workerA, cpu=3, data=6);
	*  7. The local consumer reports a replica accounting for the request 1 (workerA, cpu=5, data=4);
	*  8. The local consumer finishes the request 1 - expect the peer to command the workerA to stop working and dispose the remote worker;
	*  9. Expect the Network of favours status to be:
	*        1. rwp1Id, 8, 9
	* 10. The remote peer rwp1 requests one worker and obtains it;
	* 11. The donated worker reports a work accounting for the remote peer 1 (cpu=4, data=4);
	* 12. Expect the Network of favours status to be:
	*        1. rwp1Id, 4, 5
	* 13. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=3);
	* 14. The remote consumer fails - Expect the Peer to:
	*        1. Log the info
	*        2. Command the Worker allocated to this consumer to stop working
	* 15. Expect the remote consumer NOT to be listed in the consumers status
	* 16. Expect the Network of favours status to be:
	*        1. rwp1Id, 0, 2
	* 17. The donated worker reports a work accounting for the remote peer 1 (cpu=5, data=10);
	* 18. Expect the Network of favours status to be empty.
    */
	@ReqTest(test="AT-0038", reqs="REQ119, REQ027")
	@Test public void test_AT_0038_AccountingDonatedFavourForRemoteConsumerThatFailed() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"0038_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		// Start peer
		component = req_010_Util.startPeer();

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);

		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1","S1");
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

	    //Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
	    
	    //Login with a valid user
		String brokerAPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerAPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(component, user, brokerAPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
		//Request two workers for the logged user
	    long request1ID = 1;
	    
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
		
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label", "", tasks), request1ID, "", 4, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADeploymentID);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, workerAllocationA);
	   
		//Change worker A status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerADeploymentID, workerSpecA, requestSpec1);
	    
		//GIS client receive a remote worker provider(rwp1)
        WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createWorkerSpec("rU1", "rS1");
        String rwp1PubKey = "rwpPublicKey";
        
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", rwp1PubKey, workerSpecR1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker 1
		DeploymentID rwm1OID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpecR1, "rworker1PK", brokerAPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwm1OID, workerSpecR1, requestSpec1);
	    
		//Report a received favour
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec1, rwm1OID.toString(), 
				rwm1OID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecR1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec1, rwm1OID.toString(), 
				rwm1OID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecR1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec1, workerADeploymentID.toString(), 
				workerADeploymentID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, gpa3, true);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec1, workerADeploymentID.toString(), 
				workerADeploymentID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, gpa4, true);
	
		RemoteAllocation remotePeer = new RemoteAllocation(rwp1, AcceptanceTestUtil.createList(new WorkerAllocation(rwm1OID)));
		
		//Finish Request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD,
				brokerAPubKey, lwpcOID.getServiceID(), requestSpec1, AcceptanceTestUtil.createList(workerAllocationA), new TestStub(rwp1ID, remotePeer));
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		String rwp1DN = AcceptanceTestUtil.getCertificateDN(rwp1ID);
		statusMap.put(rwp1DN, new PeerBalance(8., 9.));
		
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Request a worker for the remote client
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rwpUser", "rwpServer", "rwpPublicKey");
	  
		int request2ID = 2;
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, "", 1, 0, 0);
	    
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec2,
				0, workerAllocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerADeploymentID, workerSpecA, remoteClientOID);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwp1DN, 4., 4.),
				workerADeploymentID);
		
		//Expect the Network of favours status to be: rwp1Id, 4, 5
		statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(rwp1DN, new PeerBalance(4., 5.));
		
		nofStatus = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwp1DN, 5., 3.),
				workerADeploymentID);
		
		//Notify the failure of the remote consumer
		//Expect the local worker to be stopped
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, true, allocationA);
		
		//Expect this consumer NOT to be listed in the consumers status
		req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());
        
        //Expect the Network of favours status to be: rwp1Id, 0, 2
		statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(rwp1DN, new PeerBalance(0., 2.));
		
		nofStatus = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		peerAcceptanceUtil.getClientMonitor();

		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwp1DN, 5., 10.),
				workerADeploymentID);
		
		//Expect the Network of favours status to be empty
		statusMap = new HashMap<String, PeerBalance>();
		nofStatus = new NetworkOfFavorsStatus(statusMap);
		
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
      }
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0038_AccountingDonatedFavourForRemoteConsumerThatFailedWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"0038_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		// Start peer
		component = req_010_Util.startPeer();

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);

		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Worker login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1","S1", null, null);
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
	    //Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
	    
	    //Login with a valid user
		String brokerAPubKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerAPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(component, user, brokerAPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
		//Request two workers for the logged user
	    long request1ID = 1;
	    
//	    IOBlock initBlock = new IOBlock();
//		initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
//		IOBlock finalBlock = new IOBlock();
//		finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
//		finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
//		TaskSpec task = new TaskSpec(initBlock, "echo Hello World", finalBlock, "echo");
//		task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
//		
//		List<TaskSpec> tasks = new ArrayList<TaskSpec>();
//		tasks.add(task);
	    JobSpecification jobSpec = createJobSpecJDL(JDLUtils.JAVA_IO_JOB, 4);
		
	    RequestSpecification requestSpec1 = new RequestSpecification(0, jobSpec, request1ID, buildRequirements(null), 4, 0, 0);
	    
	    WorkerAllocation workerAllocationA = new WorkerAllocation(workerADeploymentID);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1, workerAllocationA);
	   
		//Change worker A status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, workerADeploymentID, workerSpecA, requestSpec1);
	    
		//GIS client receive a remote worker provider(rwp1)
        WorkerSpecification workerSpecR1 = workerAcceptanceUtil.createClassAdWorkerSpec("rU1", "rS1", null, null);
        String rwp1PubKey = "rwpPublicKey";
        
		TestStub rwp1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", rwp1PubKey, workerSpecR1);
		
		RemoteWorkerProvider rwp1 = (RemoteWorkerProvider) rwp1Stub.getObject();
		
		DeploymentID rwp1ID = rwp1Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker 1
		DeploymentID rwm1OID = req_018_Util.receiveRemoteWorker(component, rwp1, rwp1ID, workerSpecR1, "rworker1PK", brokerAPubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpcOID, AcceptanceTestUtil.getBoundObject(lwpcOID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD,
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), rwm1OID, workerSpecR1, requestSpec1);
	    
		//Report a received favour
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		GridProcessAccounting gpa1 = new GridProcessAccounting(requestSpec1, rwm1OID.toString(), 
				rwm1OID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecR1);
		gpa1.setTaskSequenceNumber(1);
		gpa1.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, gpa1, false);
		
		//Report a received favour
		GridProcessAccounting gpa2 = new GridProcessAccounting(requestSpec1, rwm1OID.toString(), 
				rwm1OID.getPublicKey(), 7., 5., GridProcessState.FINISHED, workerSpecR1);
		gpa2.setTaskSequenceNumber(2);
		gpa2.setResultInfo(resultInfo);
		AcceptanceTestUtil.publishTestObject(component, rwp1ID, rwp1, RemoteWorkerProvider.class);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, gpa2, false);
		
		//Report a received favour
		GridProcessAccounting gpa3 = new GridProcessAccounting(requestSpec1, workerADeploymentID.toString(), 
				workerADeploymentID.getPublicKey(), 3., 6., GridProcessState.FINISHED, workerSpecA);
		gpa3.setTaskSequenceNumber(3);
		gpa3.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, gpa3, true);
		
		//Report a received favour
		GridProcessAccounting gpa4 = new GridProcessAccounting(requestSpec1, workerADeploymentID.toString(), 
				workerADeploymentID.getPublicKey(), 5., 4., GridProcessState.FINISHED, workerSpecA);
		gpa4.setTaskSequenceNumber(4);
		gpa4.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, gpa4, true);
	
		RemoteAllocation remotePeer = new RemoteAllocation(rwp1, AcceptanceTestUtil.createList(new WorkerAllocation(rwm1OID)));
		
		//Finish Request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD,
				brokerAPubKey, lwpcOID.getServiceID(), requestSpec1, AcceptanceTestUtil.createList(workerAllocationA), new TestStub(rwp1ID, remotePeer));
		
		//Expect the Network of favours status to be: rwp1Id, 8, 9
		Map<String, PeerBalance> statusMap = new HashMap<String, PeerBalance>();
		String rwp1DN = AcceptanceTestUtil.getCertificateDN(rwp1ID);
		statusMap.put(rwp1DN, new PeerBalance(8., 9.));
		
		NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Request a worker for the remote client
		DeploymentID remoteClientOID = PeerAcceptanceUtil.createRemoteConsumerID("rwpUser", "rwpServer", "rwpPublicKey");
	  
		int request2ID = 2;
	    RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, buildRequirements(null), 1, 0, 0);
	    
		RemoteWorkerProviderClient remoteClient = req_011_Util.requestForRemoteClient(component, remoteClientOID, requestSpec2,
				0, workerAllocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, remoteClient, workerADeploymentID, workerSpecA, remoteClientOID);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwp1DN, 4., 4.),
				workerADeploymentID);
		
		//Expect the Network of favours status to be: rwp1Id, 4, 5
		statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(rwp1DN, new PeerBalance(4., 5.));
		
		nofStatus = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		
		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwp1DN, 5., 3.),
				workerADeploymentID);
		
		//Notify the failure of the remote consumer
		//Expect the local worker to be stopped
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		req_119_Util.notifyRemoteConsumerFailure(component, null, remoteClientOID, true, allocationA);
		
		//Expect this consumer NOT to be listed in the consumers status
		req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());
        
        //Expect the Network of favours status to be: rwp1Id, 0, 2
		statusMap = new HashMap<String, PeerBalance>();
		statusMap.put(rwp1DN, new PeerBalance(0., 2.));
		
		nofStatus = new NetworkOfFavorsStatus(statusMap);
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
		peerAcceptanceUtil.getClientMonitor();

		//Report a donated favour
		req_027_Util.reportWorkAccounting(component, new WorkAccounting(rwp1DN, 5., 10.),
				workerADeploymentID);
		
		//Expect the Network of favours status to be empty
		statusMap = new HashMap<String, PeerBalance>();
		nofStatus = new NetworkOfFavorsStatus(statusMap);
		
		req_035_Util.getNetworkOfFavoursStatus(nofStatus);
      }
}