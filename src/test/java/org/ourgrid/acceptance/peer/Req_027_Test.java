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
import org.ourgrid.acceptance.util.peer.Req_015_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_027_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_112_Util;
import org.ourgrid.acceptance.worker.WorkerAcceptanceTestCase;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessResultInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ027")
public class Req_027_Test extends PeerAcceptanceTestCase {

	public static final String COMM_FILE_PATH = "req_027"+File.separator;
	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_027_Util req_027_Util = new Req_027_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_112_Util req_112_Util = new Req_112_Util(getComponentContext());
    
	@Before
	public void setUp() throws Exception {
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.createNewFile(); 
		super.setUp();
	} 
	
	@After
	public void tearDown() throws Exception {
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.delete();
		super.tearDown();
	}    
	
    /**
	 * Verifies if the peer does not accept replica accountings in the 
	 * following scenarios: 
	 * - Received from an unknown client; 
	 * - Without replica information;
	 * - With a null request; 
	 * - With an unknown request; 
	 * - With a null worker; 
	 * - With an unknown worker; 
	 * - With the remote worker management id instead of worker id; 
	 * - With zero CPU time accounting; 
	 * - With a negative Data transfered accounting.
	 */
	@ReqTest(test="AT-027.1", reqs="REQ027")
	@Test public void test_AT_027_1_AccountingReceivingFavour_InputValidation() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		resetActiveMocks();		
		
		//Report a received favour with an unknown client
		String unknownPublicKey = "unknownPublicKey";
		
		DeploymentID brokerID = new DeploymentID(new ContainerID("unknown", "unknown", "broker", unknownPublicKey), "broker");
		
		loggerMock.warn("Ignoring a replica accounting from a unknown user with this public key: " + unknownPublicKey);
		replayActiveMocks();
		req_027_Util.reportReplicaAccounting(component, null, brokerID);
		verifyActiveMocks();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login
		String brokerPubKey = "brokerPublicKey";
		
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
		
		//Report a received favour without replica information
		resetActiveMocks();
		
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpcOID.getServiceID() + 
				"], because there is not replica information.");
		replayActiveMocks();
		req_027_Util.reportReplicaAccounting(component, null, lwpcOID);
		verifyActiveMocks();
		
		//Report a received favour with a null request
		resetActiveMocks();
		loggerMock.warn("Ignoring a replica accounting from the user [" +  lwpcOID.getServiceID() + "], because the request 0 does not exists.");
		replayActiveMocks();
		GridProcessAccounting replicaAccounting = new GridProcessAccounting(new RequestSpecification(), null, null, 0, 0,
				GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		
		//Report a received favour with an unknown request
		resetActiveMocks();
		RequestSpecification requestSpec1000 = new RequestSpecification(0, new JobSpecification("label"), 1000, "", 1, 0, 0);
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpcOID.getServiceID() + "], because the request " + 
				requestSpec1000.getRequestId() + " " + "does not exists.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1000, null,null,0,0,GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Client request workers
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "os = windows AND mem > 256", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
		resetActiveMocks();
		
		//Report a received favour with a null worker
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpcOID.getServiceID() + 
				"], because there is not worker reference.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, null, null,0, 0,GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Report a received favour with an unknown worker
		Worker unknownWorker = getMock(NOT_NICE, Worker.class);
		DeploymentID unknownWorkerOID = new DeploymentID(new ContainerID("a", "a", "a", "unknownWorkerPublicKey"),"a");
		
		peerAcceptanceUtil.createStub(unknownWorker, Worker.class, unknownWorkerOID);
		
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpcOID.getServiceID()+"], because the worker " +
				"["+unknownWorkerOID+"] is not allocated for him.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, unknownWorkerOID.toString(), unknownWorkerOID.getPublicKey(),
				0, 0,GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("u1", "s1");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
        String workerPubKey = "workerPublicKey";
        TestStub remoteWorkerStub = 
        	req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpec, workerPubKey, brokerPubKey);
		
		//Report a received favour with a remote worker management id
		Worker remoteWorkerIf = getMock(NOT_NICE, Worker.class);
		DeploymentID remoteWorkerOID = remoteWorkerStub.getDeploymentID();
		peerAcceptanceUtil.createStub(remoteWorkerIf, Worker.class, remoteWorkerOID);
		
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpcOID.getServiceID()+"], because the worker " +
				"[" + remoteWorkerOID + "] is not allocated for him.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, remoteWorkerOID.toString(), remoteWorkerOID.getPublicKey(), 
				0, 0,GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpec, remoteWorkerStub, lwpcOID, requestSpec1);
		
		//Report a received favour with zero CPU time accounting
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpcOID.getServiceID()+"] referring to worker [" + 
				remoteWorkerOID + "], because the CPU accounting must be positive.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, remoteWorkerOID.toString(), 
				remoteWorkerOID.getPublicKey(), -1, 0,GridProcessState.FINISHED, workerSpec);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Report a received favour with negative data transfered accounting
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpcOID.getServiceID()+"] referring to worker [" + 
				remoteWorkerOID + "], because the DATA accounting must not be negative.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, remoteWorkerOID.toString(), 
				remoteWorkerOID.getPublicKey(), 5, -1,GridProcessState.FINISHED, workerSpec);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
	}
	
	/**
	 * Verifies if the peer logs an received favour of finished and aborted 
	 * replicas.
	 */
	@ReqTest(test="AT-027.2", reqs="REQ027")
	@Test public void test_AT_027_2_AccountingReceivedFavour() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login
		String brokerPubKey = "brokerPublicKey";
		
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
		
		//Client request workers
		IOBlock initBlock = new IOBlock();
		initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
		IOBlock finalBlock = new IOBlock();
		finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
		finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
		TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
		task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
		
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		tasks.add(task);
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label", "", tasks), 1, "os = windows AND mem > 256", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("u1", "s1");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_OS, "windows");
		workerSpec.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		String workerPubKey = "workerPublicKey";
		TestStub remoteWorkerStub = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpec, workerPubKey, brokerPubKey);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpec, remoteWorkerStub, lwpcOID, requestSpec1);
		
		DeploymentID remoteWorkerOID = remoteWorkerStub.getDeploymentID();
		
		//Report a received favour of a finished replica - expect the peer to log it
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		GridProcessAccounting replicaAccounting = 
			new GridProcessAccounting(requestSpec1, remoteWorkerOID.getServiceID().toString(), 
					remoteWorkerOID.getPublicKey(), 10, 0, GridProcessState.FINISHED, workerSpec);
		replicaAccounting.setTaskSequenceNumber(1);
		replicaAccounting.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, replicaAccounting);
		
		//Report a received favour of a aborted replica - expect the peer to log it
		replicaAccounting = new GridProcessAccounting(requestSpec1, remoteWorkerOID.getServiceID().toString(), 
				remoteWorkerOID.getPublicKey(),
				6, 5, GridProcessState.ABORTED, workerSpec);
		replicaAccounting.setTaskSequenceNumber(1);
		replicaAccounting.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, replicaAccounting);
	}
	
	/**
	 * Verifies if the peer does not accept replica accountings from a client 
	 * that does not have the worker and/or the request informed.
	 */
	@ReqTest(test="AT-027.3", reqs="REQ027")
	@Test public void test_AT_027_3_AccountingReceivingFavour_UnallocatedWorker() throws Exception {
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user012", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);

		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Clients login
		String broker1PubKey = "broker1PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		String broker2PubKey = "broker2PublicKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		//Client1 request workers
		int requestID1 = 1;
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1);
		
		//Client2 request workers
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2);
		
		//GIS client 1 receive a remote worker provider
		WorkerSpecification worker1Spec = workerAcceptanceUtil.createWorkerSpec("u1", "s1");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", worker1Spec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		String worker1PubKey = "workerPublicKey";
		TestStub remoteWorkerStub = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, worker1Spec, worker1PubKey, broker1PubKey);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, worker1Spec, remoteWorkerStub, lwpc1OID, 
					requestSpec1);
		resetActiveMocks();
		
		DeploymentID remoteWorkerOID = remoteWorkerStub.getDeploymentID();
		
		//Report a received favour with other request - expect the peer to log the warn
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpc2OID.getServiceID() + "], because the request " + 
				requestID1 + " does not belong to him.");
		replayActiveMocks();
		GridProcessAccounting replicaAccounting = 
			new GridProcessAccounting(requestSpec1, null, null, 10., 0., GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc2OID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Report a received favour with a not allocated worker - expect the peer to log the warn
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpc2OID.getServiceID() + "], because the worker [" + 
				remoteWorkerOID + "] is not allocated for him.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec2, remoteWorkerOID.toString(), remoteWorkerOID.getPublicKey(),
				10., 0., GridProcessState.FINISHED, worker1Spec);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc2OID);
		verifyActiveMocks();
	}
	
	/**
	 * Verifies if the peer logs an local favour of finished and aborted 
	 * replicas.
	 */
	@ReqTest(test="AT-027.4", reqs="REQ027")
	@Test public void test_AT_027_4_AccountingLocalFavour() throws Exception {
		
		//PeerAcceptanceUtil.recreateSchema();
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"027_blank.xml");
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		//Worker login
		String workerAPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");


		DeploymentID workerAOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAOID);
		
		//Change worker status to IDLE
		peerAcceptanceUtil.getWorkerManagementClientProxy();
		req_025_Util.changeWorkerStatusToIdle(component, workerAOID);
		
		//Login with a valid user
		String broker1PubKey = "broker1PublicKey";
		
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
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		//Request a worker for the logged user
		IOBlock initBlock = new IOBlock();
		initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
		IOBlock finalBlock = new IOBlock();
		finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
		finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
		TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
		task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
		
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		tasks.add(task);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label", "", tasks), 1, "", 1, 0, 0);
		WorkerAllocation allocation = new WorkerAllocation(workerAOID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec, allocation);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID,  
				workerAOID, workerSpecA, requestSpec);
		
		//Report a local favour of a finished replica - expect the peer to log it
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		GridProcessAccounting replicaAccounting = 
			new GridProcessAccounting(requestSpec, workerAOID.toString(), workerAOID.getPublicKey(), 
					10, 0, GridProcessState.FINISHED, workerSpecA);
		replicaAccounting.setTaskSequenceNumber(1);
		replicaAccounting.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, replicaAccounting, true);
		
		//Report a local favour of a aborted replica - expect the peer to log it
		replicaAccounting = new GridProcessAccounting(requestSpec, workerAOID.toString(), workerAOID.getPublicKey(),
				6, 5, GridProcessState.ABORTED, workerSpecA);
		replicaAccounting.setTaskSequenceNumber(1);
		replicaAccounting.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, replicaAccounting, true);

	}
	
	@ReqTest(test="AT-027.5", reqs="REQ027")
	@Test public void test_AT_027_5_AccountingLocalFavour_UnallocatedWorker() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"027_blank.xml");
		
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user012", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		resetActiveMocks();
		
		//Worker login
		String workerAPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		
		DeploymentID workerAOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAOID);
		
		//Change worker status to IDLE
		peerAcceptanceUtil.getWorkerManagementClientProxy();
		req_025_Util.changeWorkerStatusToIdle(component, workerAOID);
		
		//Clients login
		String broker1PubKey = "broker1PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		String broker2PubKey = "broker2PublicKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		//Client1 request workers
	    WorkerAllocation allocation = new WorkerAllocation(workerAOID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), spec, allocation);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, 
				workerAOID, workerSpecA, spec);
		
		//Client2 request workers
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, "", 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2);
	    resetActiveMocks();
	    
		//Report a received favour with other request - expect the peer to log the warn
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpc2OID.getServiceID() + 
				"], because the request 1 does not belong to him.");
		replayActiveMocks();
		GridProcessAccounting replicaAccounting = new GridProcessAccounting(spec, null, null, 10, 0, GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc2OID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Report a received favour with other request - expect the peer to log the warn
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpc2OID.getServiceID()+"], " +
				"because the local worker ["+workerAOID.getServiceID()+"] is not allocated for him.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec2, workerAOID.getServiceID().toString(), workerAOID.getPublicKey(),
				10, 0, GridProcessState.FINISHED, workerSpecA);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc2OID);
		verifyActiveMocks();
	}
	
	/**
	 *Verifies if the peer does not accept work accountings in the following scenarios:
	 * 	Received from an unknown worker;
	 * 	With no consumer;
	 * 	With zero CPU time accounting;
	 *	With a negative Data transfered accounting.
	 *And also verifies if the peer accepts them in the following scenarios:
	 * 	Received from an IDLE worker;
	 * 	Received from an OWNERED worker;
	 * 	Received from an almost allocated worker (before status changed);
	 * 	Received from a worker allocated to a different peer than reported.
	 */
	@ReqTest(test="AT-027.6", reqs="REQ027")
	@Test public void test_AT_027_6_AccountingDonatedFavour_InputValidation() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"027_blank.xml");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Report a donated favour from an unknown worker - expect the peer to log the warn
		String unknownPublicKey = "unknownPublicKey"; 
		loggerMock.warn("Ignoring a work accounting from an unknown worker with this public key: "+unknownPublicKey);
		replayActiveMocks();
		
		String publicKeyA = "publicKeyA";
		WorkAccounting workAccounting = new WorkAccounting(publicKeyA, 10, 12);
		
		DeploymentID workerID = new DeploymentID(new ContainerID("unknown", "unknown", "worker", unknownPublicKey), "worker");
		
		req_027_Util.reportWorkAccounting(component, workerID, workAccounting);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		//Worker login
		String workerPublicKey = "workerAPublicKey";
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("WorkerA", workerServerName);

		
		DeploymentID workerManagOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerPublicKey);
		WorkerManagement workerManagement = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerManagOID);  
		req_010_Util.workerLogin(component, workerSpecA, workerManagOID);
		
		//Report a donated favour from an ownered worker - expect the peer to log it
	    AcceptanceTestUtil.notifyRecovery(component, workerManagOID);
	    
	    workAccounting = new WorkAccounting("DN=RWP1", 10, 12);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting, workerManagOID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerManagOID);
		
		//Report a donated favour from an idle worker - expect the peer to log it
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting, workerManagOID);
		
		//Request a worker for the remote client
		DeploymentID rwpcOID = PeerAcceptanceUtil.createRemoteConsumerID("u1", "s1", publicKeyA);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(workerManagOID);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, rwpcOID, requestSpec, 0, allocationA);
		
		//Report a donated favour from an almost allocated worker (before status changed) - expect the peer to log it
		WorkAccounting workAccounting2 = new WorkAccounting(publicKeyA, 10, 12);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting2, workerManagOID);
		
		//Change worker status to ALLOCATED FOR PEER
		RemoteWorkerManagement rwm = req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerManagOID, workerSpecA, rwpcOID);
		
		//Report a work accounting without consumer public key - expect the peer to log the warn
		WorkAccounting workAccounting3 = new WorkAccounting(null, 10, 12);
		req_027_Util.reportWorkAccoutingWithoutConsumer(component, workAccounting3, workerManagOID);
		
		//Report a donated favor from a worker with a different consumer - expect the peer to log it
		WorkAccounting workAccounting4 = new WorkAccounting("publicKeyB", 10, 12);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting4, workerManagOID);
		
		//Remote client disposes local worker
		req_015_Util.remoteDisposeLocalWorker(component, rwpcOID, rwm, workerManagOID);
		
		AcceptanceTestUtil.publishTestObject(component, workerManagOID, workerManagement, WorkerManagement.class);

		//Report a donated favor from a worker without consumer - expect the peer to log it
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting, workerManagOID);
		
		//Report a donated favor from a worker without CPU time accounting - expect the peer to log the warn
		WorkAccounting workAccounting5 = new WorkAccounting(publicKeyA, 0, 12);
		req_027_Util.reportWorkAccoutingWithIllegalCPUTime(component, workAccounting5, workerManagOID, publicKeyA);
		
		//Report a donated favour from a worker with an negative Data transfered accounting - expect the peer to log the warn
		WorkAccounting workAccounting6 = new WorkAccounting(publicKeyA, 10, -1);
		req_027_Util.reportWorkAccoutingWithIllegalData(component, workAccounting6, workerManagOID, publicKeyA);

	}
	
	/**
	 * Verifies if the peer logs a donated favour.
	 */
	@ReqTest(test="AT-027.7", reqs="REQ027")
	@Test public void test_AT_027_7_AccountingDonatedFavour() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH + "027_blank.xml");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		//Worker login
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		
		DeploymentID wmOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmOID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, wmOID);
		
		//Request a worker for the remote client
		String peerPublicKey = "peerPublicKey";
		DeploymentID remoteConsumerOID = PeerAcceptanceUtil.createRemoteConsumerID("u1", "s1", peerPublicKey);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(wmOID);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, remoteConsumerOID, requestSpec, 0, allocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, wmOID, workerSpecA, remoteConsumerOID);
		
		//Report a donated favour - expect the peer to log it
		WorkAccounting workAccounting = new WorkAccounting(peerPublicKey, 10, 12);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting, wmOID);
	}
	
	/**
	 * Verifies if the peer ignores the accounting of a favour donated locally.
	 */
	@ReqTest(test="AT-027.8", reqs="REQ027")
	@Test public void test_AT_027_8_AccountingFavourDonatedLocally() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"027_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		resetActiveMocks();
		
		//Worker login
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		
		
		DeploymentID wmOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmOID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, wmOID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubkey";
		
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
		resetActiveMocks();
		
		//Request a worker for the logged user
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 1, 0, 0);
		WorkerAllocation allocation = new WorkerAllocation(wmOID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec, allocation);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, 
				wmOID, workerSpecA, requestSpec);
		
		//Report a donated favour - expect the peer to log it
		resetActiveMocks();
		loggerMock.warn("Ignoring a work accounting from the worker [" + wmOID.getServiceID() + 
				"] referring to this local peer as the consumer");
		replayActiveMocks();
		
		WorkAccounting workAccounting = new WorkAccounting(
				CertificationUtils.getCertSubjectDN(component.getMyCertPath()), 10, 12);
		req_027_Util.reportWorkAccountingLocal(component, workAccounting, wmOID);
	}

	/**
	 * Verifies if the peer does not accept replica accountings in the 
	 * following scenarios: 
	 * - Received from an unknown client; 
	 * - Without replica information;
	 * - With a null request; 
	 * - With an unknown request; 
	 * - With a null worker; 
	 * - With an unknown worker; 
	 * - With the remote worker management id instead of worker id; 
	 * - With zero CPU time accounting; 
	 * - With a negative Data transfered accounting.
	 */
	@ReqTest(test="AT-027.1", reqs="REQ027")
	@Category(JDLCompliantTest.class) @Test public void test_AT_027_1_AccountingReceivingFavour_InputValidationWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		resetActiveMocks();		
		
		//Report a received favour with an unknown client
		String unknownPublicKey = "unknownPublicKey";
		
		DeploymentID brokerID = new DeploymentID(new ContainerID("unknown", "unknown", "broker", unknownPublicKey), "broker");
		
		loggerMock.warn("Ignoring a replica accounting from a unknown user with this public key: " + unknownPublicKey);
		replayActiveMocks();
		req_027_Util.reportReplicaAccounting(component, null, brokerID);
		verifyActiveMocks();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login
		String brokerPubKey = "brokerPublicKey";
		
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
		
		//Report a received favour without replica information
		resetActiveMocks();
		
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpcOID.getServiceID() + 
				"], because there is not replica information.");
		replayActiveMocks();
		req_027_Util.reportReplicaAccounting(component, null, lwpcOID);
		verifyActiveMocks();
		
		//Report a received favour with a null request
		resetActiveMocks();
		loggerMock.warn("Ignoring a replica accounting from the user [" +  lwpcOID.getServiceID() + "], because the request 0 does not exists.");
		replayActiveMocks();
		GridProcessAccounting replicaAccounting = new GridProcessAccounting(new RequestSpecification(), null, null, 0, 0,
				GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		
		//Report a received favour with an unknown request
		resetActiveMocks();
		RequestSpecification requestSpec1000 = new RequestSpecification(0, new JobSpecification("label"), 1000, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpcOID.getServiceID() + "], because the request " + 
				requestSpec1000.getRequestId() + " " + "does not exists.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1000, null,null,0,0,GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Client request workers
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, "[Requirements=(other.MainMemory > 256 && other.OS == \"windows\");Rank=0]", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
		resetActiveMocks();
		
		//Report a received favour with a null worker
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpcOID.getServiceID() + 
				"], because there is not worker reference.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, null, null,0, 0,GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Report a received favour with an unknown worker
		Worker unknownWorker = getMock(NOT_NICE, Worker.class);
		DeploymentID unknownWorkerOID = new DeploymentID(new ContainerID("a", "a", "a", "unknownWorkerPublicKey"),"a");
		
		peerAcceptanceUtil.createStub(unknownWorker, Worker.class, unknownWorkerOID);
		
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpcOID.getServiceID()+"], because the worker " +
				"["+unknownWorkerOID+"] is not allocated for him.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, unknownWorkerOID.toString(), unknownWorkerOID.getPublicKey(),
				0, 0,GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("u1", "s1", 512, "windows");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
	    String workerPubKey = "workerPublicKey";
	    TestStub remoteWorkerStub = 
	    	req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpec, workerPubKey, brokerPubKey);
		
		//Report a received favour with a remote worker management id
		Worker remoteWorkerIf = getMock(NOT_NICE, Worker.class);
		DeploymentID remoteWorkerOID = remoteWorkerStub.getDeploymentID();
		peerAcceptanceUtil.createStub(remoteWorkerIf, Worker.class, remoteWorkerOID);
		
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpcOID.getServiceID()+"], because the worker " +
				"[" + remoteWorkerOID + "] is not allocated for him.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, remoteWorkerOID.toString(), remoteWorkerOID.getPublicKey(), 
				0, 0,GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Change worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpec, remoteWorkerStub, lwpcOID, requestSpec1);
		
		//Report a received favour with zero CPU time accounting
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpcOID.getServiceID()+"] referring to worker [" + 
				remoteWorkerOID + "], because the CPU accounting must be positive.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, remoteWorkerOID.toString(), 
				remoteWorkerOID.getPublicKey(), -1, 0,GridProcessState.FINISHED, workerSpec);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Report a received favour with negative data transfered accounting
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpcOID.getServiceID()+"] referring to worker [" + 
				remoteWorkerOID + "], because the DATA accounting must not be negative.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec1, remoteWorkerOID.toString(), 
				remoteWorkerOID.getPublicKey(), 5, -1,GridProcessState.FINISHED, workerSpec);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpcOID);
		verifyActiveMocks();
	}

	/**
	 * Verifies if the peer logs an received favour of finished and aborted 
	 * replicas.
	 */
	@ReqTest(test="AT-027.2", reqs="REQ027")
	@Category(JDLCompliantTest.class) @Test public void test_AT_027_2_AccountingReceivedFavourWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login
		String brokerPubKey = "brokerPublicKey";
		
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
		
		//Client request workers
		IOBlock initBlock = new IOBlock();
		initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
		IOBlock finalBlock = new IOBlock();
		finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
		finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
		TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
		task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
		
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		tasks.add(task);
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label", PeerAcceptanceTestCase.buildRequirements(null, null, null, null), tasks), 1, "[Requirements=(other.MainMemory > 256 && other.OS == \"windows\");Rank=0]", 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec1);
		
		//GIS client receive a remote worker provider
		WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("u1", "s1", 512, "windows");
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", workerSpec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		String workerPubKey = "workerPublicKey";
		TestStub remoteWorkerStub = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, workerSpec, workerPubKey, brokerPubKey);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, workerSpec, remoteWorkerStub, lwpcOID, requestSpec1);
		
		DeploymentID remoteWorkerOID = remoteWorkerStub.getDeploymentID();
		
		//Report a received favour of a finished replica - expect the peer to log it
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		
		GridProcessAccounting replicaAccounting = 
			new GridProcessAccounting(requestSpec1, remoteWorkerOID.getServiceID().toString(), 
					remoteWorkerOID.getPublicKey(), 10, 0, GridProcessState.FINISHED, workerSpec);
		replicaAccounting.setTaskSequenceNumber(1);
		replicaAccounting.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, replicaAccounting);
		
		//Report a received favour of a aborted replica - expect the peer to log it
		replicaAccounting = new GridProcessAccounting(requestSpec1, remoteWorkerOID.getServiceID().toString(), 
				remoteWorkerOID.getPublicKey(),
				6, 5, GridProcessState.ABORTED, workerSpec);
		replicaAccounting.setTaskSequenceNumber(1);
		replicaAccounting.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpcOID, replicaAccounting);
	}

	/**
	 * Verifies if the peer does not accept replica accountings from a client 
	 * that does not have the worker and/or the request informed.
	 */
	@ReqTest(test="AT-027.3", reqs="REQ027")
	@Category(JDLCompliantTest.class) @Test public void test_AT_027_3_AccountingReceivingFavour_UnallocatedWorkerWithJDL() throws Exception {
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user012", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
	
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Clients login
		String broker1PubKey = "broker1PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		String broker2PubKey = "broker2PublicKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		//Client1 request workers
		int requestID1 = 1;
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), 1, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1);
		
		//Client2 request workers
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2);
		
		//GIS client 1 receive a remote worker provider
		WorkerSpecification worker1Spec = workerAcceptanceUtil.createClassAdWorkerSpec("u1", "s1", null, null);
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", worker1Spec);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		String worker1PubKey = "workerPublicKey";
		TestStub remoteWorkerStub = 
			req_018_Util.receiveRemoteWorker(component, rwp, rwpID, worker1Spec, worker1PubKey, broker1PubKey);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_112_Util.remoteWorkerStatusChanged(component, worker1Spec, remoteWorkerStub, lwpc1OID, 
					requestSpec1);
		resetActiveMocks();
		
		DeploymentID remoteWorkerOID = remoteWorkerStub.getDeploymentID();
		
		//Report a received favour with other request - expect the peer to log the warn
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpc2OID.getServiceID() + "], because the request " + 
				requestID1 + " does not belong to him.");
		replayActiveMocks();
		GridProcessAccounting replicaAccounting = 
			new GridProcessAccounting(requestSpec1, null, null, 10., 0., GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc2OID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Report a received favour with a not allocated worker - expect the peer to log the warn
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpc2OID.getServiceID() + "], because the worker [" + 
				remoteWorkerOID + "] is not allocated for him.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec2, remoteWorkerOID.toString(), remoteWorkerOID.getPublicKey(),
				10., 0., GridProcessState.FINISHED, worker1Spec);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc2OID);
		verifyActiveMocks();
	}

	/**
	 * Verifies if the peer logs an local favour of finished and aborted 
	 * replicas.
	 */
	@ReqTest(test="AT-027.4", reqs="REQ027")
	@Category(JDLCompliantTest.class) @Test public void test_AT_027_4_AccountingLocalFavourWithJDL() throws Exception {
		
		//PeerAcceptanceUtil.recreateSchema();
		
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"027_blank.xml");
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		//Worker login
		String workerAPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		DeploymentID workerAOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAOID);
		
		//Change worker status to IDLE
		peerAcceptanceUtil.getWorkerManagementClientProxy();
		req_025_Util.changeWorkerStatusToIdle(component, workerAOID);
		
		//Login with a valid user
		String broker1PubKey = "broker1PublicKey";
		
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
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		//Request a worker for the logged user
		IOBlock initBlock = new IOBlock();
		initBlock.putEntry(new IOEntry("PUT", WorkerAcceptanceTestCase.RESOURCE_DIR + "file.txt", "Class.class"));
		IOBlock finalBlock = new IOBlock();
		finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
		finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
		TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
		task.setSourceDirPath(WorkerAcceptanceTestCase.RESOURCE_DIR);
		
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		tasks.add(task);
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
					PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
			    		
		WorkerAllocation allocation = new WorkerAllocation(workerAOID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec, allocation);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID,  
				workerAOID, workerSpecA, requestSpec);
		
		//Report a local favour of a finished replica - expect the peer to log it
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		resultInfo.setExitValue(0);
		GridProcessAccounting replicaAccounting = 
			new GridProcessAccounting(requestSpec, workerAOID.toString(), workerAOID.getPublicKey(), 
					10, 0, GridProcessState.FINISHED, workerSpecA);
		replicaAccounting.setTaskSequenceNumber(1);
		replicaAccounting.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, replicaAccounting, true);
		
		//Report a local favour of a aborted replica - expect the peer to log it
		replicaAccounting = new GridProcessAccounting(requestSpec, workerAOID.toString(), workerAOID.getPublicKey(),
				6, 5, GridProcessState.ABORTED, workerSpecA);
		replicaAccounting.setTaskSequenceNumber(1);
		replicaAccounting.setResultInfo(resultInfo);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, replicaAccounting, true);

	}

	@ReqTest(test="AT-027.5", reqs="REQ027")
	@Category(JDLCompliantTest.class) @Test public void test_AT_027_5_AccountingLocalFavour_UnallocatedWorkerWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"027_blank.xml");
		
		//Create user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user011", "server011", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user012", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		resetActiveMocks();
		
		//Worker login
		String workerAPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		DeploymentID workerAOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAOID);
		
		//Change worker status to IDLE
		peerAcceptanceUtil.getWorkerManagementClientProxy();
		req_025_Util.changeWorkerStatusToIdle(component, workerAOID);
		
		//Clients login
		String broker1PubKey = "broker1PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		String broker2PubKey = "broker2PublicKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		//Client1 request workers
	    WorkerAllocation allocation = new WorkerAllocation(workerAOID);
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, 
					PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), spec, allocation);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, 
				workerAOID, workerSpecA, spec);
		
		//Client2 request workers
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), 2, 
				PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2);
	    resetActiveMocks();
	    
		//Report a received favour with other request - expect the peer to log the warn
		loggerMock.warn("Ignoring a replica accounting from the user [" + lwpc2OID.getServiceID() + 
				"], because the request 1 does not belong to him.");
		replayActiveMocks();
		GridProcessAccounting replicaAccounting = new GridProcessAccounting(spec, null, null, 10, 0, GridProcessState.FINISHED, null);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc2OID);
		verifyActiveMocks();
		resetActiveMocks();
		
		//Report a received favour with other request - expect the peer to log the warn
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpc2OID.getServiceID()+"], " +
				"because the local worker ["+workerAOID.getServiceID()+"] is not allocated for him.");
		replayActiveMocks();
		replicaAccounting = new GridProcessAccounting(requestSpec2, workerAOID.getServiceID().toString(), workerAOID.getPublicKey(),
				10, 0, GridProcessState.FINISHED, workerSpecA);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc2OID);
		verifyActiveMocks();
	}

	/**
	 *Verifies if the peer does not accept work accountings in the following scenarios:
	 * 	Received from an unknown worker;
	 * 	With no consumer;
	 * 	With zero CPU time accounting;
	 *	With a negative Data transfered accounting.
	 *And also verifies if the peer accepts them in the following scenarios:
	 * 	Received from an IDLE worker;
	 * 	Received from an OWNERED worker;
	 * 	Received from an almost allocated worker (before status changed);
	 * 	Received from a worker allocated to a different peer than reported.
	 */
	@ReqTest(test="AT-027.6", reqs="REQ027")
	@Category(JDLCompliantTest.class) @Test public void test_AT_027_6_AccountingDonatedFavour_InputValidationWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"027_blank.xml");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//Report a donated favour from an unknown worker - expect the peer to log the warn
		String unknownPublicKey = "unknownPublicKey"; 
		loggerMock.warn("Ignoring a work accounting from an unknown worker with this public key: "+unknownPublicKey);
		replayActiveMocks();
		
		String publicKeyA = "publicKeyA";
		WorkAccounting workAccounting = new WorkAccounting(publicKeyA, 10, 12);
		
		DeploymentID workerID = new DeploymentID(new ContainerID("unknown", "unknown", "worker", unknownPublicKey), "worker");
		
		req_027_Util.reportWorkAccounting(component, workerID, workAccounting);
		
		verifyActiveMocks();
		resetActiveMocks();
		
		//Worker login
		String workerPublicKey = "workerAPublicKey";
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("WorkerA", workerServerName, null, null);
		DeploymentID workerManagOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerPublicKey);
		WorkerManagement workerManagement = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerManagOID);  
		req_010_Util.workerLogin(component, workerSpecA, workerManagOID);
		
		//Report a donated favour from an ownered worker - expect the peer to log it
	    AcceptanceTestUtil.notifyRecovery(component, workerManagOID);
	    
	    workAccounting = new WorkAccounting("DN=RWP1", 10, 12);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting, workerManagOID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerManagOID);
		
		//Report a donated favour from an idle worker - expect the peer to log it
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting, workerManagOID);
		
		//Request a worker for the remote client
		DeploymentID rwpcOID = PeerAcceptanceUtil.createRemoteConsumerID("u1", "s1", publicKeyA);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
					PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(workerManagOID);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, rwpcOID, requestSpec, 0, allocationA);
		
		//Report a donated favour from an almost allocated worker (before status changed) - expect the peer to log it
		WorkAccounting workAccounting2 = new WorkAccounting(publicKeyA, 10, 12);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting2, workerManagOID);
		
		//Change worker status to ALLOCATED FOR PEER
		RemoteWorkerManagement rwm = req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerManagOID, workerSpecA, rwpcOID);
		
		//Report a work accounting without consumer public key - expect the peer to log the warn
		WorkAccounting workAccounting3 = new WorkAccounting(null, 10, 12);
		req_027_Util.reportWorkAccoutingWithoutConsumer(component, workAccounting3, workerManagOID);
		
		//Report a donated favor from a worker with a different consumer - expect the peer to log it
		WorkAccounting workAccounting4 = new WorkAccounting("publicKeyB", 10, 12);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting4, workerManagOID);
		
		//Remote client disposes local worker
		req_015_Util.remoteDisposeLocalWorker(component, rwpcOID, rwm, workerManagOID);
		
		AcceptanceTestUtil.publishTestObject(component, workerManagOID, workerManagement, WorkerManagement.class);

		//Report a donated favor from a worker without consumer - expect the peer to log it
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting, workerManagOID);
		
		//Report a donated favor from a worker without CPU time accounting - expect the peer to log the warn
		WorkAccounting workAccounting5 = new WorkAccounting(publicKeyA, 0, 12);
		req_027_Util.reportWorkAccoutingWithIllegalCPUTime(component, workAccounting5, workerManagOID, publicKeyA);
		
		//Report a donated favour from a worker with an negative Data transfered accounting - expect the peer to log the warn
		WorkAccounting workAccounting6 = new WorkAccounting(publicKeyA, 10, -1);
		req_027_Util.reportWorkAccoutingWithIllegalData(component, workAccounting6, workerManagOID, publicKeyA);
	}

	/**
	 * Verifies if the peer logs a donated favour.
	 */
	@ReqTest(test="AT-027.7", reqs="REQ027")
	@Category(JDLCompliantTest.class) @Test public void test_AT_027_7_AccountingDonatedFavourWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH + "027_blank.xml");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		//Worker login
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		DeploymentID wmOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmOID);
		
		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, wmOID);
		
		//Request a worker for the remote client
		String peerPublicKey = "peerPublicKey";
		DeploymentID remoteConsumerOID = PeerAcceptanceUtil.createRemoteConsumerID("u1", "s1", peerPublicKey);
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
					PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocationA = new WorkerAllocation(wmOID);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, remoteConsumerOID, requestSpec, 0, allocationA);
		
		//Change worker status to ALLOCATED FOR PEER
		req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, wmOID, workerSpecA, remoteConsumerOID);
		
		//Report a donated favour - expect the peer to log it
		WorkAccounting workAccounting = new WorkAccounting(peerPublicKey, 10, 12);
		req_027_Util.reportWorkAccoutingWithoutReceivedRemoteWorkProvider(component, workAccounting, wmOID);
	}

	/**
	 * Verifies if the peer ignores the accounting of a favour donated locally.
	 */
	@ReqTest(test="AT-027.8", reqs="REQ027")
	@Category(JDLCompliantTest.class) @Test public void test_AT_027_8_AccountingFavourDonatedLocallyWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"027_blank.xml");
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set a mock log
		component = req_010_Util.startPeer();
		
		CommuneLogger loggerMock = getMock(NOT_NICE, CommuneLogger.class);
		component.setLogger(loggerMock);
		resetActiveMocks();
		
		//Worker login
		String workerPublicKey = "workerPublicKey";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		DeploymentID wmOID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, wmOID);

		//Change worker status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, wmOID);
		
		//Login with a valid user
		String brokerPubKey = "brokerPubkey";
		
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
		resetActiveMocks();
		
		//Request a worker for the logged user
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
					PeerAcceptanceTestCase.buildRequirements(null, null, null, null), 1, 0, 0);
		WorkerAllocation allocation = new WorkerAllocation(wmOID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), requestSpec, allocation);
		
		//Change worker status to ALLOCATED FOR BROKER
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOID, 
				wmOID, workerSpecA, requestSpec);
		
		//Report a donated favour - expect the peer to log it
		resetActiveMocks();
		loggerMock.warn("Ignoring a work accounting from the worker [" + wmOID.getServiceID() + 
				"] referring to this local peer as the consumer");
		replayActiveMocks();
		
		WorkAccounting workAccounting = new WorkAccounting(
				CertificationUtils.getCertSubjectDN(component.getMyCertPath()), 10, 12);
		req_027_Util.reportWorkAccountingLocal(component, workAccounting, wmOID);
	}
}