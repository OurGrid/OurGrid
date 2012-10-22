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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.JDLUtils;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.PeerDBTestUtil;
import org.ourgrid.acceptance.util.RemoteAllocation;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_014_Util;
import org.ourgrid.acceptance.util.peer.Req_016_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_027_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.broker.communication.operations.GetOperation;
import org.ourgrid.broker.communication.operations.InitOperation;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessExecutionResult;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessPhasesData;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.TransferTime;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.statistics.beans.aggregator.AG_Login;
import org.ourgrid.common.statistics.beans.aggregator.AG_User;
import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_WorkerStatusChange;
import org.ourgrid.common.statistics.beans.status.WorkerStatus;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0030 extends PeerAcceptanceTestCase {

	public static final String AUX_FILES_PATH = "it_0030" + File.separator;
//FIXME
	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_014_Util req_014_Util = new Req_014_Util(getComponentContext());
    private Req_016_Util req_016_Util = new Req_016_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_027_Util req_027_Util = new Req_027_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());

    @After
	public void tearDown() throws Exception {
		super.tearDown();
		peerAcceptanceUtil.deleteNOFRankingFile();
	}
	
	@ReqTest(test="AT-0030", reqs="REQ011, REQ016, REQ027, REQ110, REQ111")
	@Test public void test_AT_0030_LocalAndRemoteRedistributionWithSubcommunitiesConsideringMatchAndUnwantedWorkersAndNof()
			throws Exception {

		long time = System.currentTimeMillis();
		PeerDBTestUtil peerDBTest = new PeerDBTestUtil(getComponentContext(), false);
		
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user01", "server01", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user02", "server01", "011011");
		
		//Start the peer with a trust configuration file
		component = req_010_Util.startPeer();
		PeerAcceptanceUtil.copyTrustFile(AUX_FILES_PATH + "trust.xml");
		
		//Verify peer address
		String localPeerAddress = getComponentContext().getProperty(XMPPProperties.PROP_USERNAME) + "@"
			+ getComponentContext().getProperty(XMPPProperties.PROP_XMPP_SERVERNAME);
		
		peerDBTest.verifyPeerAdrees(component, time, AcceptanceTestUtil.createList(localPeerAddress));
		
		//Workers login
		String workerServerName = "xmpp.ourgrid.org";
		
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workera", workerServerName, 4, null);
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerb", workerServerName, 8, null);
		String workerBPublicKey = "workerBPublicKey";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);
		
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("workerc", workerServerName, 16, null);
		String workerCPublicKey = "workerCPublicKey";
		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCID);
		
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("workerd", workerServerName, 32, null);
		String workerDPublicKey = "workerDPublicKey";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDID);
		
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createWorkerSpec("workere", workerServerName, 64, null);
		String workerEPublicKey = "workerEPublicKey";
		DeploymentID workerEID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEID);
		
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createWorkerSpec("workerf", workerServerName, 128, null);
		String workerFPublicKey = "workerFPublicKey";
		DeploymentID workerFID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, workerFPublicKey);
		req_010_Util.workerLogin(component, workerSpecF, workerFID);
		
		WorkerSpecification workerSpecG = workerAcceptanceUtil.createWorkerSpec("workerg", workerServerName, 256, null);
		String workerGPublicKey = "workerGPublicKey";
		DeploymentID workerGID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecG, workerGPublicKey);
		req_010_Util.workerLogin(component, workerSpecG, workerGID);
		
		WorkerSpecification workerSpecH = workerAcceptanceUtil.createWorkerSpec("workerh", workerServerName, 512, null);
		String workerHPublicKey = "workerHPublicKey";
		DeploymentID workerHID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecH, workerHPublicKey);
		req_010_Util.workerLogin(component, workerSpecH, workerHID);
		
		WorkerSpecification workerSpecI = workerAcceptanceUtil.createWorkerSpec("workeri", workerServerName, 1024, null);
		String workerIPublicKey = "workerIPublicKey";
		DeploymentID workerIID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecI, workerIPublicKey);
		req_010_Util.workerLogin(component, workerSpecI, workerIID);
		
		WorkerSpecification workerSpecJ = workerAcceptanceUtil.createWorkerSpec("workerj", workerServerName, 2048, null);
		String workerJPublicKey = "workerJPublicKey";
		DeploymentID workerJID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecJ, workerJPublicKey);
		req_010_Util.workerLogin(component, workerSpecJ, workerJID);
		
		WorkerSpecification workerSpecK = workerAcceptanceUtil.createWorkerSpec("workerk", workerServerName, 4096, null);
		String workerKPublicKey = "workerKPublicKey";
		DeploymentID workerKID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecK, workerKPublicKey);
		req_010_Util.workerLogin(component, workerSpecK, workerKID);
		
		WorkerSpecification workerSpecL = workerAcceptanceUtil.createWorkerSpec("workerl", workerServerName, 8192, null);
		String workerLPublicKey = "workerLPublicKey";
		DeploymentID workerLID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecL, workerLPublicKey);
		req_010_Util.workerLogin(component, workerSpecL, workerLID);
		
		WorkerManagement workerManagementA = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerAID);
		
		//Define Status		
		List<AG_Worker> workers = new ArrayList<AG_Worker>();
		List<AG_WorkerStatusChange> workerStatusChangeInfo = new ArrayList<AG_WorkerStatusChange>();
		
		AG_Worker workerAInfo = PeerDBTestUtil.createWorkerInfo("workera", workerServerName, "4", localPeerAddress);
		workers.add(workerAInfo);
		AG_WorkerStatusChange statusChangeA = new AG_WorkerStatusChange();
		statusChangeA.setStatus(WorkerStatus.OWNER);
		statusChangeA.setWorker(workerAInfo);
		workerStatusChangeInfo.add(statusChangeA);
		
		AG_Worker workerBInfo = PeerDBTestUtil.createWorkerInfo("workerb", workerServerName, "8", localPeerAddress);
		workers.add(workerBInfo);
		AG_WorkerStatusChange statusChangeB = new AG_WorkerStatusChange();
		statusChangeB.setStatus(WorkerStatus.OWNER);
		statusChangeB.setWorker(workerBInfo);
		workerStatusChangeInfo.add(statusChangeB);
		
		AG_Worker workerCInfo = PeerDBTestUtil.createWorkerInfo("workerc", workerServerName, "16", localPeerAddress);
		workers.add(workerCInfo);
		AG_WorkerStatusChange statusChangeC = new AG_WorkerStatusChange();
		statusChangeC.setStatus(WorkerStatus.OWNER);
		statusChangeC.setWorker(workerCInfo);
		workerStatusChangeInfo.add(statusChangeC);
		
		AG_Worker workerDInfo = PeerDBTestUtil.createWorkerInfo("workerd", workerServerName, "32", localPeerAddress);
		workers.add(workerDInfo);
		AG_WorkerStatusChange statusChangeD = new AG_WorkerStatusChange();
		statusChangeD.setStatus(WorkerStatus.OWNER);
		statusChangeD.setWorker(workerDInfo);
		workerStatusChangeInfo.add(statusChangeD);
		
		AG_Worker workerEInfo = PeerDBTestUtil.createWorkerInfo("workere", workerServerName, "64", localPeerAddress);
		workers.add(workerEInfo);
		AG_WorkerStatusChange statusChangeE = new AG_WorkerStatusChange();
		statusChangeE.setStatus(WorkerStatus.OWNER);
		statusChangeE.setWorker(workerEInfo);
		workerStatusChangeInfo.add(statusChangeE);
		
		AG_Worker workerFInfo = PeerDBTestUtil.createWorkerInfo("workerf", workerServerName, "128", localPeerAddress);
		workers.add(workerFInfo);
		AG_WorkerStatusChange statusChangeF = new AG_WorkerStatusChange();
		statusChangeF.setStatus(WorkerStatus.OWNER);
		statusChangeF.setWorker(workerFInfo);
		workerStatusChangeInfo.add(statusChangeF);
		
		AG_Worker workerGInfo = PeerDBTestUtil.createWorkerInfo("workerg", workerServerName, "256", localPeerAddress);
		workers.add(workerGInfo);
		AG_WorkerStatusChange statusChangeG = new AG_WorkerStatusChange();
		statusChangeG.setStatus(WorkerStatus.OWNER);
		statusChangeG.setWorker(workerGInfo);
		workerStatusChangeInfo.add(statusChangeG);
		
		AG_Worker workerHInfo = PeerDBTestUtil.createWorkerInfo("workerh", workerServerName, "512", localPeerAddress);
		workers.add(workerHInfo);
		AG_WorkerStatusChange statusChangeH = new AG_WorkerStatusChange();
		statusChangeH.setStatus(WorkerStatus.OWNER);
		statusChangeH.setWorker(workerHInfo);
		workerStatusChangeInfo.add(statusChangeH);
		
		AG_Worker workerIInfo = PeerDBTestUtil.createWorkerInfo("workeri", workerServerName, "1024", localPeerAddress);
		workers.add(workerIInfo);
		AG_WorkerStatusChange statusChangeI = new AG_WorkerStatusChange();
		statusChangeI.setStatus(WorkerStatus.OWNER);
		statusChangeI.setWorker(workerIInfo);
		workerStatusChangeInfo.add(statusChangeI);
		
		AG_Worker workerJInfo = PeerDBTestUtil.createWorkerInfo("workerj", workerServerName, "2048", localPeerAddress);
		workers.add(workerJInfo);
		AG_WorkerStatusChange statusChangeJ = new AG_WorkerStatusChange();
		statusChangeJ.setStatus(WorkerStatus.OWNER);
		statusChangeJ.setWorker(workerJInfo);
		workerStatusChangeInfo.add(statusChangeJ);
		
		AG_Worker workerKInfo = PeerDBTestUtil.createWorkerInfo("workerk", workerServerName, "4096", localPeerAddress);
		workers.add(workerKInfo);
		AG_WorkerStatusChange statusChangeK = new AG_WorkerStatusChange();
		statusChangeK.setStatus(WorkerStatus.OWNER);
		statusChangeK.setWorker(workerKInfo);
		workerStatusChangeInfo.add(statusChangeK);
		
		AG_Worker workerLInfo = PeerDBTestUtil.createWorkerInfo("workerl", workerServerName, "8192", localPeerAddress);
		workers.add(workerLInfo);
		AG_WorkerStatusChange statusChangeL = new AG_WorkerStatusChange();
		statusChangeL.setStatus(WorkerStatus.OWNER);
		statusChangeL.setWorker(workerLInfo);
		workerStatusChangeInfo.add(statusChangeL);
		
		//Verify Workers and Workers Status
		peerDBTest.verifyWorkers(component, time, workers);
		peerDBTest.verifyWorkerStatusChange(component, time, workerStatusChangeInfo);
		
		//Login again
		time = System.currentTimeMillis();
		req_010_Util.workerLoginAgain(component, workerSpecA, workerAID);
		req_010_Util.workerLoginAgain(component, workerSpecB, workerBID);
		req_010_Util.workerLoginAgain(component, workerSpecC, workerCID);
		req_010_Util.workerLoginAgain(component, workerSpecD, workerDID);
		req_010_Util.workerLoginAgain(component, workerSpecE, workerEID);
		req_010_Util.workerLoginAgain(component, workerSpecF, workerFID);
		req_010_Util.workerLoginAgain(component, workerSpecG, workerGID);
		req_010_Util.workerLoginAgain(component, workerSpecH, workerHID);
		req_010_Util.workerLoginAgain(component, workerSpecI, workerIID);
		req_010_Util.workerLoginAgain(component, workerSpecJ, workerJID);
		req_010_Util.workerLoginAgain(component, workerSpecK, workerKID);
		req_010_Util.workerLoginAgain(component, workerSpecL, workerLID);
		
		
		//Verify Worker Status
		for (AG_WorkerStatusChange workerStatusChange : workerStatusChangeInfo)
			workerStatusChange.setStatus(WorkerStatus.OWNER);
		
		peerDBTest.verifyWorkerStatusChange(component, time, workerStatusChangeInfo);
		
		//Change workers status to IDLE
		time = System.currentTimeMillis();
		req_025_Util.changeWorkerStatusToIdle(component, workerLID);
		req_025_Util.changeWorkerStatusToIdle(component, workerKID);
		req_025_Util.changeWorkerStatusToIdle(component, workerJID);
		req_025_Util.changeWorkerStatusToIdle(component, workerIID);
		req_025_Util.changeWorkerStatusToIdle(component, workerHID);
		req_025_Util.changeWorkerStatusToIdle(component, workerGID);
		req_025_Util.changeWorkerStatusToIdle(component, workerFID);
		req_025_Util.changeWorkerStatusToIdle(component, workerEID);
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		//Verify Workers Status
		for (AG_WorkerStatusChange workerStatusChange : workerStatusChangeInfo)
			workerStatusChange.setStatus(WorkerStatus.IDLE);
		
		Collections.reverse(workerStatusChangeInfo);
		peerDBTest.verifyWorkerStatusChange(component, time, workerStatusChangeInfo);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Login with two valid users
		String broker1PubKey = "broker1PubKey"; 
		
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
		
		//Verify added users
		List<AG_User> users = new ArrayList<AG_User>();
		AG_User userInfo1 = PeerDBTestUtil.createUserInfo("user01@server01", localPeerAddress, null, "");
		users.add(userInfo1);
		
		peerDBTest.verifyUsers(component, time, users);
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		//Verify login
		userInfo1.setPublicKey(broker1PubKey);
		
		List<AG_Login> loginInfo = new ArrayList<AG_Login>();
		AG_Login login1 = new AG_Login();
		login1.setLoginResult("OK");
		login1.setUser(userInfo1);
		loginInfo.add(login1);
		
		peerDBTest.verifyLogin(component, time, loginInfo);
		peerDBTest.verifyUsers(component, time, users);
		
		String broker2PubKey = "broker2PubKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Verify added user
		AG_User userInfo2 = PeerDBTestUtil.createUserInfo("user02@server01", localPeerAddress, null, "");
		users.add(userInfo2);
		
		peerDBTest.verifyUsers(component, time, users);
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		//Verify login
		userInfo2.setPublicKey(broker2PubKey);
		userInfo2.setId(0);
		
		AG_Login login2 = new AG_Login();
		login2.setLoginResult("OK");
		login2.setUser(userInfo2);
		loginInfo.add(login2);
		
		peerDBTest.verifyLogin(component, time, loginInfo);
		peerDBTest.verifyUsers(component, time, users);
		
		//Request six workers for local1
		JobSpecification jobSpec = createJobSpec("label", 6);
		RequestSpecification requestSpec1 = new RequestSpecification(0, jobSpec, 1, "mem < 8", 6, 0, 0);
		
		time = System.currentTimeMillis();
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec1, allocationA);
		
		//Verify Workers Status
		statusChangeA.setStatus(WorkerStatus.IN_USE);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//GIS client receive a remote worker provider (sub11)
		time = System.currentTimeMillis();
		String sub11PublicKey = "publicKey11";
		WorkerSpecification remoteWorkerSpec11 = workerAcceptanceUtil.createWorkerSpec("U1", "SS11", 4, null);
		TestStub sub11Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp11User", 
				"rwp11Server", sub11PublicKey, remoteWorkerSpec11);
		
		RemoteWorkerProvider sub11 = (RemoteWorkerProvider) sub11Stub.getObject();
		
		DeploymentID sub11ID = sub11Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW11)
		String worker11PubKey = "worker11PubKey"; 
		DeploymentID rwm11OID = req_018_Util.receiveRemoteWorker(component, sub11, sub11ID, 
				remoteWorkerSpec11, worker11PubKey, broker1PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (sub2)
		String sub2PublicKey = "publicKey2";
		WorkerSpecification remoteWorkerSpec20 = workerAcceptanceUtil.createWorkerSpec("U1", "SS2", 4, null);
		
		TestStub sub2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp22User", 
				"rwp2Server", sub2PublicKey, remoteWorkerSpec20);
		
		RemoteWorkerProvider sub2 = (RemoteWorkerProvider) sub2Stub.getObject();
		
		DeploymentID sub2ID = sub2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW20)
		String worker20PubKey = "worker20PubKey"; 
		DeploymentID rwm20OID = req_018_Util.receiveRemoteWorker(component, sub2, sub2ID, remoteWorkerSpec20, worker20PubKey, broker1PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (r1)
		String r1PublicKey = "r1PublicKey";
		WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createWorkerSpec("U1", "S1", 4 , null);
		TestStub r1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp1User", 
				"rwp1Server", r1PublicKey, remoteWorkerSpec1);
		
		RemoteWorkerProvider r1 = (RemoteWorkerProvider) r1Stub.getObject();
		
		DeploymentID r1ID = r1Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (rw1)
		String worker1PubKey = "worker1PubKey"; 
		DeploymentID rwm1OID = req_018_Util.receiveRemoteWorker(component, r1, r1ID, remoteWorkerSpec1, worker1PubKey, broker1PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (r2)
		String r2PublicKey = "r2PublicKey";
		WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createWorkerSpec("U1", "S2", 4, null);
		TestStub r2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp2User", 
				"rwp2Server", r2PublicKey, remoteWorkerSpec2);
		
		RemoteWorkerProvider r2 = (RemoteWorkerProvider) r2Stub.getObject();
		
		DeploymentID r2ID = r2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (rw2)
		String worker2PubKey = "worker2PubKey"; 
		TestStub rwm2Stub = req_018_Util.receiveRemoteWorker(component, r2, r2ID, remoteWorkerSpec2, worker2PubKey, broker1PubKey);
		
		DeploymentID rwm2OID = rwm2Stub.getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER (workerA)
		time = System.currentTimeMillis();
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerAID, workerSpecA, requestSpec1);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Change worker status to ALLOCATED FOR BROKER (RW11)
		time = System.currentTimeMillis();
		ObjectDeployment lwpc1OD = new ObjectDeployment(component, lwpc1OID, AcceptanceTestUtil.getBoundObject(lwpc1OID));
		
		ObjectDeployment rwmcOD = peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment();
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, rwmcOD, 
				rwm11OID, remoteWorkerSpec11, requestSpec1);
		
		//Change worker status to ALLOCATED FOR BROKER (RW20)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, rwmcOD, 
				rwm20OID, remoteWorkerSpec20, requestSpec1);
		
		//Change worker status to ALLOCATED FOR BROKER (RW1)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, rwmcOD, 
				rwm1OID, remoteWorkerSpec1, requestSpec1);
		
		//Change worker status to ALLOCATED FOR BROKER (RW2)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, rwmcOD, 
				rwm2OID, remoteWorkerSpec2, requestSpec1);
		
		//Accounting
		GridProcessAccounting acc = new GridProcessAccounting(requestSpec1, workerAID.toString(), 
				workerAID.getPublicKey(), 100., 0., GridProcessState.FINISHED, workerSpecA);
		acc.setTaskSequenceNumber(1);
		
		Worker workerA = EasyMock.createMock(Worker.class);
		DeploymentID workerID = new DeploymentID(workerAID.getContainerID(), "WORKER");
		AcceptanceTestUtil.publishTestObject(component, workerID, workerA, Worker.class);
		
		InitOperation initOp = new InitOperation(new GridProcessHandle(1, 1, 1), 1, workerID.toString(), "localFile.txt", "remoteFile.txt",
				"PUT", new GridProcessExecutionResult(new GridProcessHandle(1, 1, 1)));
		
		TransferTime initTime = new TransferTime();
		initTime.setInitTime();
		Thread.sleep(100);
		initTime.setEndTime();
		
		Map<InitOperation, TransferTime> init = new LinkedHashMap<InitOperation, TransferTime>();
		init.put(initOp, initTime);
		
		GetOperation getOp = new GetOperation(new GridProcessHandle(1, 1, 1), 1, workerID.toString(), "localFile.txt", "remoteFile.txt",
				"GET", new GridProcessExecutionResult(new GridProcessHandle(1, 1, 1)));
		
		TransferTime getTime = new TransferTime();
		getTime.setInitTime();
		Thread.sleep(100);
		getTime.setEndTime();
		
		Map<GetOperation, TransferTime> get = new LinkedHashMap<GetOperation, TransferTime>();
		get.put(getOp, getTime);
		
		GridProcessPhasesData phasesData = new GridProcessPhasesData();
		phasesData.setInitBeginning(123456L);
		phasesData.setInitEnd(123460L);
		phasesData.setRemoteBeginning(123470L);
		phasesData.setRemoteEnd(123480L);
		phasesData.setFinalBeginning(123490L);
		phasesData.setFinalEnd(123500L);
		phasesData.setInitOperations(init);
		phasesData.setGetOperations(get);
		
		acc.setPhasesData(phasesData);
		
		OutgoingHandle handle = (OutgoingHandle) initOp.getHandle();
		
		OutgoingTransferHandle outgoing = new OutgoingTransferHandle(handle.getId(), 
				handle.getLogicalFileName(), handle.getLocalFile(), handle.getDescription(), 
				new DeploymentID(handle.getDestinationID()));
		
		TransferProgress initProgress = new TransferProgress(outgoing, "localFile.txt", 1000, Status.complete, 
				1000, 0.59D, 10D, true);
		
		IncomingHandle handleIn = (IncomingHandle) getOp.getHandle();
		ContainerID idIn = ContainerID.parse(handleIn.getSenderContainerID());
		
		IncomingTransferHandle incoming = new IncomingTransferHandle(handleIn.getId(),
				handleIn.getLogicalFileName(), handleIn.getDescription(), handleIn.getFileSize(), idIn);
		
		TransferProgress getProgress = new TransferProgress(incoming, "localFile.txt", 1000, Status.complete, 
				1000, 0.59D, 10D, true);
		
		Map<TransferHandle, TransferProgress> progress = new LinkedHashMap<TransferHandle, TransferProgress>();
		progress.put(initProgress.getHandle(), initProgress);
		progress.put(getProgress.getHandle(), getProgress);
		
		acc.setTransfersProgress(progress);
		
		//Report a received favour (workerA)
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, acc, true);
		
		//Report a received favour (RW11)
		GridProcessAccounting acc9 = new GridProcessAccounting(requestSpec1, rwm11OID.toString(), rwm11OID.getPublicKey(), 
				80., 0., GridProcessState.FINISHED, remoteWorkerSpec11);
		acc9.setTaskSequenceNumber(2);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc9, false);
		
		//Report a received favour (RW20)
		GridProcessAccounting acc10 = new GridProcessAccounting(requestSpec1, rwm20OID.toString(), rwm20OID.getPublicKey(), 
				150., 0., GridProcessState.FINISHED, remoteWorkerSpec20);
		acc10.setTaskSequenceNumber(3);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc10, false);
		
		//Report a received favour (RW1)
		GridProcessAccounting acc11 = new GridProcessAccounting(requestSpec1, rwm1OID.toString(), rwm1OID.getPublicKey(), 
				120., 0., GridProcessState.FINISHED, remoteWorkerSpec1);
		acc11.setTaskSequenceNumber(4);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc11, false);
		
		//Report a received favour (RW2)
		GridProcessAccounting acc12 = new GridProcessAccounting(requestSpec1, rwm2OID.toString(), rwm2OID.getPublicKey(), 
				120., 0., GridProcessState.FINISHED, remoteWorkerSpec2);
		acc12.setTaskSequenceNumber(5);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc12, false);
		
		//Finish the request
		WorkerAllocation remoteAllocation11 = new WorkerAllocation(rwm11OID);
		WorkerAllocation remoteAllocation20 = new WorkerAllocation(rwm20OID);
		WorkerAllocation remoteAllocation1 = new WorkerAllocation(rwm1OID);
		WorkerAllocation remoteAllocation2 = new WorkerAllocation(rwm2OID);
		
		RemoteAllocation remotePeer11 = new RemoteAllocation(sub11, AcceptanceTestUtil.createList(remoteAllocation11));
		RemoteAllocation remotePeer20 = new RemoteAllocation(sub2, AcceptanceTestUtil.createList(remoteAllocation20));
		RemoteAllocation remotePeer1 = new RemoteAllocation(r1, AcceptanceTestUtil.createList(remoteAllocation1));
		RemoteAllocation remotePeer2 = new RemoteAllocation(r2, AcceptanceTestUtil.createList(remoteAllocation2));
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		AcceptanceTestUtil.publishTestObject(component, workerAID, workerManagementA, WorkerManagement.class);
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				broker1PubKey, lwpc1OID.getServiceID(), null, requestSpec1, AcceptanceTestUtil.createList(allocationA), 
				new TestStub(sub11ID, remotePeer11), new TestStub(sub2ID, remotePeer20), new TestStub(r1ID, remotePeer1), 
				new TestStub(r2ID, remotePeer2));
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Request five workers for local2
		RequestSpecification requestSpec2 = new RequestSpecification(0, jobSpec, 2, "mem < 8", 6, 0, 0);
		List<TestStub> rwps = new ArrayList<TestStub>();
		
		time = System.currentTimeMillis();
		rwps.add(new TestStub(sub11ID, sub11));
		rwps.add(new TestStub(sub2ID, sub2));
		rwps.add(new TestStub(r1ID, r1));
		rwps.add(new TestStub(r2ID, r2));
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, rwps , allocationA);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//GIS client receive a remote worker provider (sub10)
		String sub10PublicKey = "publicKey10";
		WorkerSpecification remoteWorkerSpec10 = workerAcceptanceUtil.createWorkerSpec("U1", "SS10", 4, null);
		TestStub sub10Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec2, "rwp10User", 
				"rwp10Server", sub10PublicKey, remoteWorkerSpec10);
		
		RemoteWorkerProvider sub10 = (RemoteWorkerProvider) sub10Stub.getObject();
		
		DeploymentID sub10ID = sub10Stub.getDeploymentID();
		
		rwps.add(new TestStub(sub10ID, sub10));
		
		//Remote worker provider client receive a remote worker (RW10)
		String worker10PubKey = "worker10PubKey"; 
		DeploymentID rwm10OID = req_018_Util.receiveRemoteWorker(component, sub10, sub10ID, remoteWorkerSpec10, worker10PubKey, broker2PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (r2)
		WorkerSpecification remoteWorkerSpec4 = workerAcceptanceUtil.createWorkerSpec("U2", "S2", 4, null);
		
		//Remote worker provider client receive a remote worker (RW2)
		rwm2OID = req_018_Util.receiveRemoteWorker(component, r2, r2ID, remoteWorkerSpec2, worker2PubKey, broker2PubKey).getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW4)
		String worker4PubKey = "worker4PubKey"; 
		DeploymentID rwm4OID = req_018_Util.receiveRemoteWorker(component, r2, r2ID, remoteWorkerSpec4, worker4PubKey, broker2PubKey).getDeploymentID();
		
		
		//Change worker status to ALLOCATED FOR BROKER (workerA)
		time = System.currentTimeMillis();
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OID, workerAID, workerSpecA, requestSpec2);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Change worker status to ALLOCATED FOR BROKER (RW10)
		ObjectDeployment lwpc2OD = new ObjectDeployment(component, lwpc2OID, AcceptanceTestUtil.getBoundObject(lwpc2OID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OD, rwmcOD, 
				rwm10OID, remoteWorkerSpec10, requestSpec2);
		
		//Change worker status to ALLOCATED FOR BROKER (RW2)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OD, rwmcOD, 
				rwm2OID, remoteWorkerSpec2, requestSpec2);
		
		//Change worker status to ALLOCATED FOR BROKER (RW4)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OD, rwmcOD, 
				rwm4OID, remoteWorkerSpec4, requestSpec2);
		
		//Report a received favour (workerA)
		GridProcessAccounting acc13 = new GridProcessAccounting(requestSpec2, workerAID.toString(), workerAID.getPublicKey(), 
				100., 0., GridProcessState.FINISHED, workerSpecA);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, 
				acc13, true);
		
		//Report a received favour (RW10)
		GridProcessAccounting acc14 = new GridProcessAccounting(requestSpec2, rwm10OID.toString(), rwm10OID.getPublicKey(), 
				300., 0., GridProcessState.FINISHED, remoteWorkerSpec10);
		acc14.setTaskSequenceNumber(2);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, 
				acc14, false);
		
		//Report a received favour (RW4)
		GridProcessAccounting acc15 = new GridProcessAccounting(requestSpec2, rwm4OID.toString(), rwm4OID.getPublicKey(), 
				130., 0., GridProcessState.FINISHED, remoteWorkerSpec4);
		acc15.setTaskSequenceNumber(3);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, 
				acc15, false);
		
		//Report a received favour (RW2)
		GridProcessAccounting acc16 = new GridProcessAccounting(requestSpec2, rwm2OID.toString(), rwm2OID.getPublicKey(), 
				40., 0., GridProcessState.FINISHED, remoteWorkerSpec2);
		acc16.setTaskSequenceNumber(4);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, 
				acc16, false);
		
		//Finish the request
		WorkerAllocation remoteAllocation10 = new WorkerAllocation(rwm10OID);
		remoteAllocation2 = new WorkerAllocation(rwm2OID);
		WorkerAllocation remoteAllocation4 = new WorkerAllocation(rwm4OID);
		
		RemoteAllocation remotePeer10 = new RemoteAllocation(sub10, AcceptanceTestUtil.createList(remoteAllocation10));
		remotePeer2 = new RemoteAllocation(r2, AcceptanceTestUtil.createList(remoteAllocation2, remoteAllocation4));
		
		time = System.currentTimeMillis();
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				broker2PubKey, lwpc2OID.getServiceID(), null, requestSpec2, AcceptanceTestUtil.createList(allocationA), 
				new TestStub(sub10ID, remotePeer10), new TestStub(r2ID, remotePeer2));
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Request five workers for R3
		DeploymentID r3ClientID = new DeploymentID(new ContainerID("rwpc3User", "rwpc3server", PeerConstants.MODULE_NAME, "r3PublicKey"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 5);
		RequestSpecification requestSpec3 = new RequestSpecification(0, jobSpec, 3, "", 5, 0, 0);
		
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		WorkerAllocation allocationC = new WorkerAllocation(workerCID);
		WorkerAllocation allocationD = new WorkerAllocation(workerDID);
		WorkerAllocation allocationE = new WorkerAllocation(workerEID);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, r3ClientID, requestSpec3, 3, 
				allocationA, allocationB, allocationC, allocationD, allocationE);
		
		//Verify Workers Status
		statusChangeA.setStatus(WorkerStatus.DONATED);
		statusChangeB.setStatus(WorkerStatus.DONATED);
		statusChangeC.setStatus(WorkerStatus.DONATED);
		statusChangeD.setStatus(WorkerStatus.DONATED);
		statusChangeE.setStatus(WorkerStatus.DONATED);
		
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA, statusChangeB, 
				statusChangeC, statusChangeD, statusChangeE));
		
		//Request ten workers for R1
		//r1ID = new DeploymentID(new ContainerID("rwpc1User", "rwpc1server", PeerConstants.MODULE_NAME, "r1PublicKey"),
		//		PeerConstants.REMOTE_ACCESS_OBJECT_NAME);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 10);
		RequestSpecification requestSpec4 = new RequestSpecification(0, jobSpec, 4, "", 10, 0, 0);
		
		WorkerAllocation allocationF = new WorkerAllocation(workerFID);
		WorkerAllocation allocationG = new WorkerAllocation(workerGID);
		WorkerAllocation allocationH = new WorkerAllocation(workerHID);
		WorkerAllocation allocationI = new WorkerAllocation(workerIID);
		WorkerAllocation allocationJ = new WorkerAllocation(workerJID);
		WorkerAllocation allocationK = new WorkerAllocation(workerKID);
		WorkerAllocation allocationL = new WorkerAllocation(workerLID);
		
		allocationE.addLoserConsumer(r3ClientID);
		allocationD.addLoserConsumer(r3ClientID);
		allocationC.addLoserConsumer(r3ClientID);
		
		DeploymentID r1ClientID = new DeploymentID(r1ID.getContainerID(), 
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, r1ClientID, requestSpec4, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationF, allocationG, allocationH, allocationI, allocationJ, 
				allocationK, allocationL, allocationE, allocationD, allocationC);
		
		//Verify Workers Status
		statusChangeC.setStatus(WorkerStatus.DONATED);
		statusChangeD.setStatus(WorkerStatus.DONATED);
		statusChangeE.setStatus(WorkerStatus.DONATED);
		statusChangeF.setStatus(WorkerStatus.DONATED);
		statusChangeG.setStatus(WorkerStatus.DONATED);
		statusChangeH.setStatus(WorkerStatus.DONATED);
		statusChangeI.setStatus(WorkerStatus.DONATED);
		statusChangeJ.setStatus(WorkerStatus.DONATED);
		statusChangeK.setStatus(WorkerStatus.DONATED);
		statusChangeL.setStatus(WorkerStatus.DONATED);
		
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeC, statusChangeD, 
				statusChangeE, statusChangeF, statusChangeG, statusChangeH, statusChangeI, statusChangeJ, statusChangeK, 
				statusChangeL));
		
		//Request ten workers for R2
		//r2ID = new DeploymentID(new ContainerID("rwpc2User", "rwpc2server", PeerConstants.MODULE_NAME),
		//		PeerConstants.REMOTE_ACCESS_OBJECT_NAME);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 10);
		RequestSpecification requestSpec5 = new RequestSpecification(0, jobSpec, 5, "(mem < 4096) AND (mem > 4)", 10, 0, 0);
		
		allocationB.addLoserConsumer(r3ClientID);
		allocationC.addLoserConsumer(r1ClientID);
		allocationD.addLoserConsumer(r1ClientID);
		allocationE.addLoserConsumer(r1ClientID);
		allocationJ.addLoserConsumer(r1ClientID);
		allocationI.addLoserConsumer(r1ClientID);
		allocationH.addLoserConsumer(r1ClientID);
		allocationG.addLoserConsumer(r1ClientID);
		allocationF.addLoserConsumer(r1ClientID);
		
		DeploymentID r2ClientID = new DeploymentID(r2ID.getContainerID(),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, r2ClientID, requestSpec5, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationB, allocationC, allocationD, allocationE, 
				allocationJ, allocationI, allocationH, allocationG, allocationF);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeB, statusChangeC, statusChangeD, 
				statusChangeE, statusChangeF, statusChangeG, statusChangeH, statusChangeI, statusChangeJ));
		
		//Request four workers for Sub10
		//DeploymentID rsub10ID = new DeploymentID(new ContainerID("rwpc10User", "rwpc10server", 
		//		PeerConstants.MODULE_NAME, "publicKey10"),
		//		PeerConstants.REMOTE_ACCESS_OBJECT_NAME);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 4);
		RequestSpecification requestSpec6 = new RequestSpecification(0, jobSpec, 6, "mem > 64", 4, 0, 0);
		
		allocationF.addLoserConsumer(r2ClientID);
		allocationG.addLoserConsumer(r2ClientID);
		allocationH.addLoserConsumer(r2ClientID);
		allocationL.addLoserConsumer(r1ClientID);
		
		DeploymentID sub10ClientID = new DeploymentID(sub10ID.getContainerID(), 
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, sub10ClientID, requestSpec6, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationL, allocationF, allocationG, allocationH);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeF, statusChangeG, statusChangeH, 
				statusChangeL));
		
		//Request four workers for Sub2
		DeploymentID rsub2ClientID = new DeploymentID(new ContainerID("rwpc20User", "rwpc20server", 
				PeerConstants.MODULE_NAME, "publicKey2"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 4);
		RequestSpecification requestSpec7 = new RequestSpecification(0, jobSpec, 7, "mem > 256", 4, 0, 0);
		
		allocationI.addLoserConsumer(r2ClientID);
		allocationJ.addLoserConsumer(r2ClientID);
		allocationK.addLoserConsumer(r1ClientID);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, rsub2ClientID, requestSpec7, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationK, allocationI, allocationJ);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeI, statusChangeJ, statusChangeK));
		
		//Request four workers for Sub12
		DeploymentID rsub12ClientID = new DeploymentID(new ContainerID("rwpc12User", "rwpc12server", 
				PeerConstants.MODULE_NAME, "publicKey12"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 4);
		RequestSpecification requestSpec8 = new RequestSpecification(0, jobSpec, 8, "mem > 32", 4, 0, 0);
		
		allocationE.addLoserConsumer(r2ClientID);
		allocationK.addLoserConsumer(rsub2ClientID);
		allocationI.addLoserConsumer(rsub2ClientID);
		allocationJ.addLoserConsumer(rsub2ClientID);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, rsub12ClientID, requestSpec8, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationE, allocationI, allocationJ, allocationK);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeE, statusChangeI, 
				statusChangeJ, statusChangeK));
		
		//Request five workers for Sub11
		//DeploymentID rsub11ID = new DeploymentID(new ContainerID("rwpc11User", "rwpc11server", 
		//		PeerConstants.MODULE_NAME, "publicKey11"),
		//		PeerConstants.REMOTE_ACCESS_OBJECT_NAME);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 5);
		RequestSpecification requestSpec9 = new RequestSpecification(0, jobSpec, 9, "mem > 8", 5, 0, 0);
		
		allocationD.addLoserConsumer(r2ClientID);
		allocationC.addLoserConsumer(r2ClientID);
		allocationI.addLoserConsumer(rsub12ClientID);
		allocationJ.addLoserConsumer(rsub12ClientID);
		allocationK.addLoserConsumer(rsub12ClientID);
		
		DeploymentID sub11ClientID = new DeploymentID(sub11ID.getContainerID(),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, sub11ClientID, requestSpec9, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationD, allocationC, allocationK, allocationI, allocationJ);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeC, statusChangeD, 
				statusChangeI, statusChangeJ, statusChangeK));
		//Request seven workers for local1
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 7);
		RequestSpecification requestSpec10 = new RequestSpecification(0, jobSpec, 10, "", 7, 0, 0);
		
		allocationA.addLoserConsumer(r3ClientID);
		allocationB.addLoserConsumer(r2ClientID);
		allocationE.addLoserConsumer(rsub12ClientID);
		allocationK.addLoserConsumer(sub11ClientID);
		allocationJ.addLoserConsumer(sub11ClientID);
		allocationL.addLoserConsumer(sub10ClientID);
		allocationI.addLoserConsumer(sub11ClientID);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec10, 
				allocationA, allocationB, allocationE, allocationK,
				allocationJ, allocationL, allocationI);
		
		//Verify Workers Status
		statusChangeA.setStatus(WorkerStatus.IN_USE);
		statusChangeB.setStatus(WorkerStatus.IN_USE);
		statusChangeE.setStatus(WorkerStatus.IN_USE);
		statusChangeI.setStatus(WorkerStatus.IN_USE);
		statusChangeJ.setStatus(WorkerStatus.IN_USE);
		statusChangeK.setStatus(WorkerStatus.IN_USE);
		statusChangeL.setStatus(WorkerStatus.IN_USE);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA, statusChangeB, 
				statusChangeE, statusChangeI, statusChangeJ, statusChangeK, statusChangeL));
		
		//The consumer local1 set the worker A as unwanted
		allocationA = new WorkerAllocation(workerAID);
		
		time = System.currentTimeMillis();
		ScheduledFuture<?> future10 = req_016_Util.unwantedMockWorker(component, allocationA, requestSpec10, 
				lwpc1OID, true, null);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Request eight workers for local2
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 8);
		RequestSpecification requestSpec11 = new RequestSpecification(0, jobSpec, 11, "", 8, 0, 0);
		allocationH.addLoserConsumer(sub10ClientID);
		allocationC.addLoserConsumer(sub11ClientID);
		allocationG.addLoserConsumer(sub10ClientID);
		allocationD.addLoserConsumer(sub11ClientID);
		allocationF.addLoserConsumer(sub10ClientID);
		
		time = System.currentTimeMillis();
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec11, rwps,
				allocationA, allocationH, allocationC, 
				allocationG, allocationD, allocationF);
		
		//Verify Workers Status
		statusChangeC.setStatus(WorkerStatus.IN_USE);
		statusChangeD.setStatus(WorkerStatus.IN_USE);
		statusChangeF.setStatus(WorkerStatus.IN_USE);
		statusChangeG.setStatus(WorkerStatus.IN_USE);
		statusChangeH.setStatus(WorkerStatus.IN_USE);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA, statusChangeC, 
				statusChangeD, statusChangeF, statusChangeG, statusChangeH));
		
		//The consumer local2 set the worker A as unwanted
		time = System.currentTimeMillis();
		req_016_Util.unwantedMockWorker(component, allocationA, requestSpec11, lwpc2OID, false, null);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//The consumer local2 set the worker c as unwanted
		allocationC.addWinnerConsumer(lwpc1OID).addLoserConsumer(lwpc2OID);
		time = System.currentTimeMillis();
		req_016_Util.unwantedMockWorker(component, allocationC, requestSpec11, lwpc2OID, false, future10);
		
		//Verify Workers Status
		AG_WorkerStatusChange statusChangeC2 = new AG_WorkerStatusChange();
		statusChangeC2.setStatus(WorkerStatus.IN_USE);
		statusChangeC2.setWorker(workerCInfo);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeC, statusChangeC2));
	}
	
	@Category(JDLCompliantTest.class)
	@Test 
	public void test_AT_0030_LocalAndRemoteRedistributionWithSubcommunitiesConsideringMatchAndUnwantedWorkersAndNofWithJDL()
	throws Exception {

		long time = System.currentTimeMillis();
		PeerDBTestUtil peerDBTest = new PeerDBTestUtil(getComponentContext(), false);
		
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user01", "server01", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user02", "server01", "011011");
		
		//Start the peer with a trust configuration file
		component = req_010_Util.startPeer();
		PeerAcceptanceUtil.copyTrustFile(AUX_FILES_PATH + "trust.xml");
		
		//Verify peer address
		String localPeerAddress = getComponentContext().getProperty(XMPPProperties.PROP_USERNAME) + "@"
			+ getComponentContext().getProperty(XMPPProperties.PROP_XMPP_SERVERNAME);
		
		peerDBTest.verifyPeerAdrees(component, time, AcceptanceTestUtil.createList(localPeerAddress));
		
		//Workers login
		String workerServerName = "xmpp.ourgrid.org";
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workera", workerServerName, 4, null);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_PROVIDER_PEER, getPeerAddress());
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerb", workerServerName, 8, null);
		String workerBPublicKey = "workerBPublicKey";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);
		
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("workerc", workerServerName, 16, null);
		String workerCPublicKey = "workerCPublicKey";
		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCID);
		
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("workerd", workerServerName, 32, null);
		String workerDPublicKey = "workerDPublicKey";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDID);
		
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createClassAdWorkerSpec("workere", workerServerName, 64, null);
		String workerEPublicKey = "workerEPublicKey";
		DeploymentID workerEID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEID);
		
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createClassAdWorkerSpec("workerf", workerServerName, 128, null);
		String workerFPublicKey = "workerFPublicKey";
		DeploymentID workerFID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, workerFPublicKey);
		req_010_Util.workerLogin(component, workerSpecF, workerFID);
		
		WorkerSpecification workerSpecG = workerAcceptanceUtil.createClassAdWorkerSpec("workerg", workerServerName, 256, null);
		String workerGPublicKey = "workerGPublicKey";
		DeploymentID workerGID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecG, workerGPublicKey);
		req_010_Util.workerLogin(component, workerSpecG, workerGID);
		
		WorkerSpecification workerSpecH = workerAcceptanceUtil.createClassAdWorkerSpec("workerh", workerServerName, 512, null);
		String workerHPublicKey = "workerHPublicKey";
		DeploymentID workerHID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecH, workerHPublicKey);
		req_010_Util.workerLogin(component, workerSpecH, workerHID);
		
		WorkerSpecification workerSpecI = workerAcceptanceUtil.createClassAdWorkerSpec("workeri", workerServerName, 1024, null);
		String workerIPublicKey = "workerIPublicKey";
		DeploymentID workerIID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecI, workerIPublicKey);
		req_010_Util.workerLogin(component, workerSpecI, workerIID);
		
		WorkerSpecification workerSpecJ = workerAcceptanceUtil.createClassAdWorkerSpec("workerj", workerServerName, 2048, null);
		String workerJPublicKey = "workerJPublicKey";
		DeploymentID workerJID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecJ, workerJPublicKey);
		req_010_Util.workerLogin(component, workerSpecJ, workerJID);
		
		WorkerSpecification workerSpecK = workerAcceptanceUtil.createClassAdWorkerSpec("workerk", workerServerName, 4096, null);
		String workerKPublicKey = "workerKPublicKey";
		DeploymentID workerKID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecK, workerKPublicKey);
		req_010_Util.workerLogin(component, workerSpecK, workerKID);
		
		WorkerSpecification workerSpecL = workerAcceptanceUtil.createClassAdWorkerSpec("workerl", workerServerName, 8192, null);
		String workerLPublicKey = "workerLPublicKey";
		DeploymentID workerLID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecL, workerLPublicKey);
		req_010_Util.workerLogin(component, workerSpecL, workerLID);
		
		WorkerManagement workerManagementA = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerAID);
		
		//Define Status		
		List<AG_Worker> workers = new ArrayList<AG_Worker>();
		List<AG_WorkerStatusChange> workerStatusChangeInfo = new ArrayList<AG_WorkerStatusChange>();
		
		AG_Worker workerAInfo = PeerDBTestUtil.createClassAdWorkerInfo("workera", workerServerName, "4", localPeerAddress);
		workers.add(workerAInfo);
		AG_WorkerStatusChange statusChangeA = new AG_WorkerStatusChange();
		statusChangeA.setStatus(WorkerStatus.OWNER);
		statusChangeA.setWorker(workerAInfo);
		workerStatusChangeInfo.add(statusChangeA);
		
		AG_Worker workerBInfo = PeerDBTestUtil.createClassAdWorkerInfo("workerb", workerServerName, "8", localPeerAddress);
		workers.add(workerBInfo);
		AG_WorkerStatusChange statusChangeB = new AG_WorkerStatusChange();
		statusChangeB.setStatus(WorkerStatus.OWNER);
		statusChangeB.setWorker(workerBInfo);
		workerStatusChangeInfo.add(statusChangeB);
		
		AG_Worker workerCInfo = PeerDBTestUtil.createClassAdWorkerInfo("workerc", workerServerName, "16", localPeerAddress);
		workers.add(workerCInfo);
		AG_WorkerStatusChange statusChangeC = new AG_WorkerStatusChange();
		statusChangeC.setStatus(WorkerStatus.OWNER);
		statusChangeC.setWorker(workerCInfo);
		workerStatusChangeInfo.add(statusChangeC);
		
		AG_Worker workerDInfo = PeerDBTestUtil.createClassAdWorkerInfo("workerd", workerServerName, "32", localPeerAddress);
		workers.add(workerDInfo);
		AG_WorkerStatusChange statusChangeD = new AG_WorkerStatusChange();
		statusChangeD.setStatus(WorkerStatus.OWNER);
		statusChangeD.setWorker(workerDInfo);
		workerStatusChangeInfo.add(statusChangeD);
		
		AG_Worker workerEInfo = PeerDBTestUtil.createClassAdWorkerInfo("workere", workerServerName, "64", localPeerAddress);
		workers.add(workerEInfo);
		AG_WorkerStatusChange statusChangeE = new AG_WorkerStatusChange();
		statusChangeE.setStatus(WorkerStatus.OWNER);
		statusChangeE.setWorker(workerEInfo);
		workerStatusChangeInfo.add(statusChangeE);
		
		AG_Worker workerFInfo = PeerDBTestUtil.createClassAdWorkerInfo("workerf", workerServerName, "128", localPeerAddress);
		workers.add(workerFInfo);
		AG_WorkerStatusChange statusChangeF = new AG_WorkerStatusChange();
		statusChangeF.setStatus(WorkerStatus.OWNER);
		statusChangeF.setWorker(workerFInfo);
		workerStatusChangeInfo.add(statusChangeF);
		
		AG_Worker workerGInfo = PeerDBTestUtil.createClassAdWorkerInfo("workerg", workerServerName, "256", localPeerAddress);
		workers.add(workerGInfo);
		AG_WorkerStatusChange statusChangeG = new AG_WorkerStatusChange();
		statusChangeG.setStatus(WorkerStatus.OWNER);
		statusChangeG.setWorker(workerGInfo);
		workerStatusChangeInfo.add(statusChangeG);
		
		AG_Worker workerHInfo = PeerDBTestUtil.createClassAdWorkerInfo("workerh", workerServerName, "512", localPeerAddress);
		workers.add(workerHInfo);
		AG_WorkerStatusChange statusChangeH = new AG_WorkerStatusChange();
		statusChangeH.setStatus(WorkerStatus.OWNER);
		statusChangeH.setWorker(workerHInfo);
		workerStatusChangeInfo.add(statusChangeH);
		
		AG_Worker workerIInfo = PeerDBTestUtil.createClassAdWorkerInfo("workeri", workerServerName, "1024", localPeerAddress);
		workers.add(workerIInfo);
		AG_WorkerStatusChange statusChangeI = new AG_WorkerStatusChange();
		statusChangeI.setStatus(WorkerStatus.OWNER);
		statusChangeI.setWorker(workerIInfo);
		workerStatusChangeInfo.add(statusChangeI);
		
		AG_Worker workerJInfo = PeerDBTestUtil.createClassAdWorkerInfo("workerj", workerServerName, "2048", localPeerAddress);
		workers.add(workerJInfo);
		AG_WorkerStatusChange statusChangeJ = new AG_WorkerStatusChange();
		statusChangeJ.setStatus(WorkerStatus.OWNER);
		statusChangeJ.setWorker(workerJInfo);
		workerStatusChangeInfo.add(statusChangeJ);
		
		AG_Worker workerKInfo = PeerDBTestUtil.createClassAdWorkerInfo("workerk", workerServerName, "4096", localPeerAddress);
		workers.add(workerKInfo);
		AG_WorkerStatusChange statusChangeK = new AG_WorkerStatusChange();
		statusChangeK.setStatus(WorkerStatus.OWNER);
		statusChangeK.setWorker(workerKInfo);
		workerStatusChangeInfo.add(statusChangeK);
		
		AG_Worker workerLInfo = PeerDBTestUtil.createClassAdWorkerInfo("workerl", workerServerName, "8192", localPeerAddress);
		workers.add(workerLInfo);
		AG_WorkerStatusChange statusChangeL = new AG_WorkerStatusChange();
		statusChangeL.setStatus(WorkerStatus.OWNER);
		statusChangeL.setWorker(workerLInfo);
		workerStatusChangeInfo.add(statusChangeL);
		
		//Verify Workers and Workers Status
		peerDBTest.verifyWorkers(component, time, workers);
		peerDBTest.verifyWorkerStatusChange(component, time, workerStatusChangeInfo);
		
		//Login again
		time = System.currentTimeMillis();
		req_010_Util.workerLoginAgain(component, workerSpecA, workerAID);
		req_010_Util.workerLoginAgain(component, workerSpecB, workerBID);
		req_010_Util.workerLoginAgain(component, workerSpecC, workerCID);
		req_010_Util.workerLoginAgain(component, workerSpecD, workerDID);
		req_010_Util.workerLoginAgain(component, workerSpecE, workerEID);
		req_010_Util.workerLoginAgain(component, workerSpecF, workerFID);
		req_010_Util.workerLoginAgain(component, workerSpecG, workerGID);
		req_010_Util.workerLoginAgain(component, workerSpecH, workerHID);
		req_010_Util.workerLoginAgain(component, workerSpecI, workerIID);
		req_010_Util.workerLoginAgain(component, workerSpecJ, workerJID);
		req_010_Util.workerLoginAgain(component, workerSpecK, workerKID);
		req_010_Util.workerLoginAgain(component, workerSpecL, workerLID);
		
		
		//Verify Worker Status
		for (AG_WorkerStatusChange workerStatusChange : workerStatusChangeInfo)
			workerStatusChange.setStatus(WorkerStatus.OWNER);
		
		peerDBTest.verifyWorkerStatusChange(component, time, workerStatusChangeInfo);
		
		//Change workers status to IDLE
		time = System.currentTimeMillis();
		req_025_Util.changeWorkerStatusToIdle(component, workerLID);
		req_025_Util.changeWorkerStatusToIdle(component, workerKID);
		req_025_Util.changeWorkerStatusToIdle(component, workerJID);
		req_025_Util.changeWorkerStatusToIdle(component, workerIID);
		req_025_Util.changeWorkerStatusToIdle(component, workerHID);
		req_025_Util.changeWorkerStatusToIdle(component, workerGID);
		req_025_Util.changeWorkerStatusToIdle(component, workerFID);
		req_025_Util.changeWorkerStatusToIdle(component, workerEID);
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		
		//Verify Workers Status
		for (AG_WorkerStatusChange workerStatusChange : workerStatusChangeInfo)
			workerStatusChange.setStatus(WorkerStatus.IDLE);
		
		Collections.reverse(workerStatusChangeInfo);
		peerDBTest.verifyWorkerStatusChange(component, time, workerStatusChangeInfo);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Login with two valid users
		String broker1PubKey = "broker1PubKey"; 
		
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
		
		//Verify added users
		List<AG_User> users = new ArrayList<AG_User>();
		AG_User userInfo1 = PeerDBTestUtil.createUserInfo("user01@server01", localPeerAddress, null, "");
		users.add(userInfo1);
		
		peerDBTest.verifyUsers(component, time, users);
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
		//Verify login
		userInfo1.setPublicKey(broker1PubKey);
		
		List<AG_Login> loginInfo = new ArrayList<AG_Login>();
		AG_Login login1 = new AG_Login();
		login1.setLoginResult("OK");
		login1.setUser(userInfo1);
		loginInfo.add(login1);
		
		peerDBTest.verifyLogin(component, time, loginInfo);
		peerDBTest.verifyUsers(component, time, users);
		
		String broker2PubKey = "broker2PubKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Verify added user
		AG_User userInfo2 = PeerDBTestUtil.createUserInfo("user02@server01", localPeerAddress, null, "");
		users.add(userInfo2);
		
		peerDBTest.verifyUsers(component, time, users);
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		//Verify login
		userInfo2.setPublicKey(broker2PubKey);
		userInfo2.setId(0);
		
		AG_Login login2 = new AG_Login();
		login2.setLoginResult("OK");
		login2.setUser(userInfo2);
		loginInfo.add(login2);
		
		peerDBTest.verifyLogin(component, time, loginInfo);
		peerDBTest.verifyUsers(component, time, users);
		
		//Request six workers for local1
		JobSpecification jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 6);
		
		RequestSpecification requestSpec12 = new RequestSpecification(0, jobSpec, 12, buildRequirements("<", 8, null, null), 6, 0, 0);
		
		time = System.currentTimeMillis();
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec12, allocationA);
		
		//Verify Workers Status
		statusChangeA.setStatus(WorkerStatus.IN_USE);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//GIS client receive a remote worker provider (sub11)
		time = System.currentTimeMillis();
		String sub11PublicKey = "publicKey11";
		WorkerSpecification remoteWorkerSpec11 = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "SS11", 4, null);
		TestStub sub11Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec12, "rwp11User", 
				"rwp11Server", sub11PublicKey, remoteWorkerSpec11);
		
		RemoteWorkerProvider sub11 = (RemoteWorkerProvider) sub11Stub.getObject();
		
		DeploymentID sub11ID = sub11Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW11)
		String worker11PubKey = "worker11PubKey"; 
		DeploymentID rwm11OID = req_018_Util.receiveRemoteWorker(component, sub11, sub11ID, 
				remoteWorkerSpec11, worker11PubKey, broker1PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (sub2)
		String sub2PublicKey = "publicKey2";
		WorkerSpecification remoteWorkerSpec20 = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "SS2", 4, null);
		
		TestStub sub2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec12, "rwp22User", 
				"rwp2Server", sub2PublicKey, remoteWorkerSpec20);
		
		RemoteWorkerProvider sub2 = (RemoteWorkerProvider) sub2Stub.getObject();
		
		DeploymentID sub2ID = sub2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW20)
		String worker20PubKey = "worker20PubKey"; 
		DeploymentID rwm20OID = req_018_Util.receiveRemoteWorker(component, sub2, sub2ID, remoteWorkerSpec20, worker20PubKey, broker1PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (r1)
		String r1PublicKey = "r1PublicKey";
		WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", 4 , null);
		TestStub r1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec12, "rwp1User", 
				"rwp1Server", r1PublicKey, remoteWorkerSpec1);
		
		RemoteWorkerProvider r1 = (RemoteWorkerProvider) r1Stub.getObject();
		
		DeploymentID r1ID = r1Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (rw1)
		String worker1PubKey = "worker1PubKey"; 
		DeploymentID rwm1OID = req_018_Util.receiveRemoteWorker(component, r1, r1ID, remoteWorkerSpec1, worker1PubKey, broker1PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (r2)
		String r2PublicKey = "r2PublicKey";
		WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S2", 4, null);
		TestStub r2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec12, "rwp2User", 
				"rwp2Server", r2PublicKey, remoteWorkerSpec2);
		
		RemoteWorkerProvider r2 = (RemoteWorkerProvider) r2Stub.getObject();
		
		DeploymentID r2ID = r2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (rw2)
		String worker2PubKey = "worker2PubKey"; 
		TestStub rwm2Stub = req_018_Util.receiveRemoteWorker(component, r2, r2ID, remoteWorkerSpec2, worker2PubKey, broker1PubKey);
		
		DeploymentID rwm2OID = rwm2Stub.getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER (workerA)
		time = System.currentTimeMillis();
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerAID, workerSpecA, requestSpec12);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Change worker status to ALLOCATED FOR BROKER (RW11)
		time = System.currentTimeMillis();
		ObjectDeployment lwpc1OD = new ObjectDeployment(component, lwpc1OID, AcceptanceTestUtil.getBoundObject(lwpc1OID));
		
		ObjectDeployment rwmcOD = peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment();
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, rwmcOD, 
				rwm11OID, remoteWorkerSpec11, requestSpec12);
		
		//Change worker status to ALLOCATED FOR BROKER (RW20)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, rwmcOD, 
				rwm20OID, remoteWorkerSpec20, requestSpec12);
		
		//Change worker status to ALLOCATED FOR BROKER (RW1)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, rwmcOD, 
				rwm1OID, remoteWorkerSpec1, requestSpec12);
		
		//Change worker status to ALLOCATED FOR BROKER (RW2)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OD, rwmcOD, 
				rwm2OID, remoteWorkerSpec2, requestSpec12);
		
		//Accounting
		GridProcessAccounting acc = new GridProcessAccounting(requestSpec12, workerAID.toString(), 
				workerAID.getPublicKey(), 100., 0., GridProcessState.FINISHED, workerSpecA);
		acc.setTaskSequenceNumber(1);
		
		Worker workerA = EasyMock.createMock(Worker.class);
		DeploymentID workerID = new DeploymentID(workerAID.getContainerID(), "WORKER");
		AcceptanceTestUtil.publishTestObject(component, workerID, workerA, Worker.class);
		
		InitOperation initOp = new InitOperation(new GridProcessHandle(1, 1, 1), 1, workerID.toString(), "localFile.txt", "remoteFile.txt",
				"PUT", new GridProcessExecutionResult(new GridProcessHandle(1, 1, 1)));
		
		TransferTime initTime = new TransferTime();
		initTime.setInitTime();
		Thread.sleep(100);
		initTime.setEndTime();
		
		Map<InitOperation, TransferTime> init = new LinkedHashMap<InitOperation, TransferTime>();
		init.put(initOp, initTime);
		
		GetOperation getOp = new GetOperation(new GridProcessHandle(1, 1, 1), 1, workerID.toString(), "localFile.txt", "remoteFile.txt",
				"GET", new GridProcessExecutionResult(new GridProcessHandle(1, 1, 1)));
		
		TransferTime getTime = new TransferTime();
		getTime.setInitTime();
		Thread.sleep(100);
		getTime.setEndTime();
		
		Map<GetOperation, TransferTime> get = new LinkedHashMap<GetOperation, TransferTime>();
		get.put(getOp, getTime);
		
		GridProcessPhasesData phasesData = new GridProcessPhasesData();
		phasesData.setInitBeginning(123456L);
		phasesData.setInitEnd(123460L);
		phasesData.setRemoteBeginning(123470L);
		phasesData.setRemoteEnd(123480L);
		phasesData.setFinalBeginning(123490L);
		phasesData.setFinalEnd(123500L);
		phasesData.setInitOperations(init);
		phasesData.setGetOperations(get);
		
		acc.setPhasesData(phasesData);
		
		OutgoingHandle handle = (OutgoingHandle) initOp.getHandle();
		
		OutgoingTransferHandle outgoing = new OutgoingTransferHandle(handle.getId(), 
				handle.getLogicalFileName(), handle.getLocalFile(), handle.getDescription(), 
				new DeploymentID(handle.getDestinationID()));
		
		TransferProgress initProgress = new TransferProgress(outgoing, "localFile.txt", 1000, Status.complete, 
				1000, 0.59D, 10D, true);
		
		IncomingHandle handleIn = (IncomingHandle) getOp.getHandle();
		ContainerID idIn = ContainerID.parse(handleIn.getSenderContainerID());
		
		IncomingTransferHandle incoming = new IncomingTransferHandle(handleIn.getId(),
				handleIn.getLogicalFileName(), handleIn.getDescription(), handleIn.getFileSize(), idIn);
		
		TransferProgress getProgress = new TransferProgress(incoming, "localFile.txt", 1000, Status.complete, 
				1000, 0.59D, 10D, true);
		
		Map<TransferHandle, TransferProgress> progress = new LinkedHashMap<TransferHandle, TransferProgress>();
		progress.put(initProgress.getHandle(), initProgress);
		progress.put(getProgress.getHandle(), getProgress);
		
		acc.setTransfersProgress(progress);
		
		//Report a received favour (workerA)
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, acc, true);
		
		//Report a received favour (RW11)
		GridProcessAccounting acc9 = new GridProcessAccounting(requestSpec12, rwm11OID.toString(), rwm11OID.getPublicKey(), 
				80., 0., GridProcessState.FINISHED, remoteWorkerSpec11);
		acc9.setTaskSequenceNumber(2);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc9, false);
		
		//Report a received favour (RW20)
		GridProcessAccounting acc10 = new GridProcessAccounting(requestSpec12, rwm20OID.toString(), rwm20OID.getPublicKey(), 
				150., 0., GridProcessState.FINISHED, remoteWorkerSpec20);
		acc10.setTaskSequenceNumber(3);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc10, false);
		
		//Report a received favour (RW1)
		GridProcessAccounting acc11 = new GridProcessAccounting(requestSpec12, rwm1OID.toString(), rwm1OID.getPublicKey(), 
				120., 0., GridProcessState.FINISHED, remoteWorkerSpec1);
		acc11.setTaskSequenceNumber(4);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc11, false);
		
		//Report a received favour (RW2)
		GridProcessAccounting acc12 = new GridProcessAccounting(requestSpec12, rwm2OID.toString(), rwm2OID.getPublicKey(), 
				120., 0., GridProcessState.FINISHED, remoteWorkerSpec2);
		acc12.setTaskSequenceNumber(5);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc12, false);
		
		//Finish the request
		WorkerAllocation remoteAllocation11 = new WorkerAllocation(rwm11OID);
		WorkerAllocation remoteAllocation20 = new WorkerAllocation(rwm20OID);
		WorkerAllocation remoteAllocation1 = new WorkerAllocation(rwm1OID);
		WorkerAllocation remoteAllocation2 = new WorkerAllocation(rwm2OID);
		
		RemoteAllocation remotePeer11 = new RemoteAllocation(sub11, AcceptanceTestUtil.createList(remoteAllocation11));
		RemoteAllocation remotePeer20 = new RemoteAllocation(sub2, AcceptanceTestUtil.createList(remoteAllocation20));
		RemoteAllocation remotePeer1 = new RemoteAllocation(r1, AcceptanceTestUtil.createList(remoteAllocation1));
		RemoteAllocation remotePeer2 = new RemoteAllocation(r2, AcceptanceTestUtil.createList(remoteAllocation2));
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
		
		AcceptanceTestUtil.publishTestObject(component, workerAID, workerManagementA, WorkerManagement.class);
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				broker1PubKey, lwpc1OID.getServiceID(), null, requestSpec12, AcceptanceTestUtil.createList(allocationA), 
				new TestStub(sub11ID, remotePeer11), new TestStub(sub2ID, remotePeer20), new TestStub(r1ID, remotePeer1), 
				new TestStub(r2ID, remotePeer2));
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Request five workers for local2
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 6);
		RequestSpecification requestSpec13 = new RequestSpecification(0, jobSpec, 13, buildRequirements("<", 8, null, null), 6, 0, 0);
		List<TestStub> rwps = new ArrayList<TestStub>();
		
		time = System.currentTimeMillis();
		rwps.add(new TestStub(sub11ID, sub11));
		rwps.add(new TestStub(sub2ID, sub2));
		rwps.add(new TestStub(r1ID, r1));
		rwps.add(new TestStub(r2ID, r2));
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec13, rwps , allocationA);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//GIS client receive a remote worker provider (sub10)
		String sub10PublicKey = "publicKey10";
		WorkerSpecification remoteWorkerSpec10 = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "SS10", 4, null);
		TestStub sub10Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec13, "rwp10User", 
				"rwp10Server", sub10PublicKey, remoteWorkerSpec10);
		
		RemoteWorkerProvider sub10 = (RemoteWorkerProvider) sub10Stub.getObject();
		
		DeploymentID sub10ID = sub10Stub.getDeploymentID();
		
		rwps.add(new TestStub(sub10ID, sub10));
		
		//Remote worker provider client receive a remote worker (RW10)
		String worker10PubKey = "worker10PubKey"; 
		DeploymentID rwm10OID = req_018_Util.receiveRemoteWorker(component, sub10, sub10ID, remoteWorkerSpec10, worker10PubKey, broker2PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (r2)
		WorkerSpecification remoteWorkerSpec4 = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S2", 4, null);
		
		//Remote worker provider client receive a remote worker (RW2)
		rwm2OID = req_018_Util.receiveRemoteWorker(component, r2, r2ID, remoteWorkerSpec2, worker2PubKey, broker2PubKey).getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW4)
		String worker4PubKey = "worker4PubKey"; 
		DeploymentID rwm4OID = req_018_Util.receiveRemoteWorker(component, r2, r2ID, remoteWorkerSpec4, worker4PubKey, broker2PubKey).getDeploymentID();
		
		
		//Change worker status to ALLOCATED FOR BROKER (workerA)
		time = System.currentTimeMillis();
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OID, workerAID, workerSpecA, requestSpec13);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Change worker status to ALLOCATED FOR BROKER (RW10)
		ObjectDeployment lwpc2OD = new ObjectDeployment(component, lwpc2OID, AcceptanceTestUtil.getBoundObject(lwpc2OID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OD, rwmcOD, 
				rwm10OID, remoteWorkerSpec10, requestSpec13);
		
		//Change worker status to ALLOCATED FOR BROKER (RW2)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OD, rwmcOD, 
				rwm2OID, remoteWorkerSpec2, requestSpec13);
		
		//Change worker status to ALLOCATED FOR BROKER (RW4)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OD, rwmcOD, 
				rwm4OID, remoteWorkerSpec4, requestSpec13);
		
		//Report a received favour (workerA)
		GridProcessAccounting acc13 = new GridProcessAccounting(requestSpec13, workerAID.toString(), workerAID.getPublicKey(), 
				100., 0., GridProcessState.FINISHED, workerSpecA);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, 
				acc13, true);
		
		//Report a received favour (RW10)
		GridProcessAccounting acc14 = new GridProcessAccounting(requestSpec13, rwm10OID.toString(), rwm10OID.getPublicKey(), 
				300., 0., GridProcessState.FINISHED, remoteWorkerSpec10);
		acc14.setTaskSequenceNumber(2);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, 
				acc14, false);
		
		//Report a received favour (RW4)
		GridProcessAccounting acc15 = new GridProcessAccounting(requestSpec13, rwm4OID.toString(), rwm4OID.getPublicKey(), 
				130., 0., GridProcessState.FINISHED, remoteWorkerSpec4);
		acc15.setTaskSequenceNumber(3);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, 
				acc15, false);
		
		//Report a received favour (RW2)
		GridProcessAccounting acc16 = new GridProcessAccounting(requestSpec13, rwm2OID.toString(), rwm2OID.getPublicKey(), 
				40., 0., GridProcessState.FINISHED, remoteWorkerSpec2);
		acc16.setTaskSequenceNumber(4);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, 
				acc16, false);
		
		//Finish the request
		WorkerAllocation remoteAllocation10 = new WorkerAllocation(rwm10OID);
		remoteAllocation2 = new WorkerAllocation(rwm2OID);
		WorkerAllocation remoteAllocation4 = new WorkerAllocation(rwm4OID);
		
		RemoteAllocation remotePeer10 = new RemoteAllocation(sub10, AcceptanceTestUtil.createList(remoteAllocation10));
		remotePeer2 = new RemoteAllocation(r2, AcceptanceTestUtil.createList(remoteAllocation2, remoteAllocation4));
		
		time = System.currentTimeMillis();
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				broker2PubKey, lwpc2OID.getServiceID(), null, requestSpec13, AcceptanceTestUtil.createList(allocationA), 
				new TestStub(sub10ID, remotePeer10), new TestStub(r2ID, remotePeer2));
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Request five workers for R3
		DeploymentID r3ClientID = new DeploymentID(new ContainerID("rwpc3User", "rwpc3server", PeerConstants.MODULE_NAME, "r3PublicKey"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 5);
		RequestSpecification requestSpec3 = new RequestSpecification(0, jobSpec, 3, buildRequirements(null), 5, 0, 0);
		
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		WorkerAllocation allocationC = new WorkerAllocation(workerCID);
		WorkerAllocation allocationD = new WorkerAllocation(workerDID);
		WorkerAllocation allocationE = new WorkerAllocation(workerEID);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, r3ClientID, requestSpec3, 3, 
				allocationA, allocationB, allocationC, allocationD, allocationE);
		
		//Verify Workers Status
		statusChangeA.setStatus(WorkerStatus.DONATED);
		statusChangeB.setStatus(WorkerStatus.DONATED);
		statusChangeC.setStatus(WorkerStatus.DONATED);
		statusChangeD.setStatus(WorkerStatus.DONATED);
		statusChangeE.setStatus(WorkerStatus.DONATED);
		
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA, statusChangeB, 
				statusChangeC, statusChangeD, statusChangeE));
		
		//Request ten workers for R1
		//r1ID = new DeploymentID(new ContainerID("rwpc1User", "rwpc1server", PeerConstants.MODULE_NAME, "r1PublicKey"),
		//		PeerConstants.REMOTE_ACCESS_OBJECT_NAME);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 10);
		RequestSpecification requestSpec4 = new RequestSpecification(0, jobSpec, 4, buildRequirements(null), 10, 0, 0);
		
		WorkerAllocation allocationF = new WorkerAllocation(workerFID);
		WorkerAllocation allocationG = new WorkerAllocation(workerGID);
		WorkerAllocation allocationH = new WorkerAllocation(workerHID);
		WorkerAllocation allocationI = new WorkerAllocation(workerIID);
		WorkerAllocation allocationJ = new WorkerAllocation(workerJID);
		WorkerAllocation allocationK = new WorkerAllocation(workerKID);
		WorkerAllocation allocationL = new WorkerAllocation(workerLID);
		
		allocationE.addLoserConsumer(r3ClientID);
		allocationD.addLoserConsumer(r3ClientID);
		allocationC.addLoserConsumer(r3ClientID);
		
		DeploymentID r1ClientID = new DeploymentID(r1ID.getContainerID(), 
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, r1ClientID, requestSpec4, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationF, allocationG, allocationH, allocationI, allocationJ, 
				allocationK, allocationL, allocationE, allocationD, allocationC);
		
		//Verify Workers Status
		statusChangeC.setStatus(WorkerStatus.DONATED);
		statusChangeD.setStatus(WorkerStatus.DONATED);
		statusChangeE.setStatus(WorkerStatus.DONATED);
		statusChangeF.setStatus(WorkerStatus.DONATED);
		statusChangeG.setStatus(WorkerStatus.DONATED);
		statusChangeH.setStatus(WorkerStatus.DONATED);
		statusChangeI.setStatus(WorkerStatus.DONATED);
		statusChangeJ.setStatus(WorkerStatus.DONATED);
		statusChangeK.setStatus(WorkerStatus.DONATED);
		statusChangeL.setStatus(WorkerStatus.DONATED);
		
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeC, statusChangeD, 
				statusChangeE, statusChangeF, statusChangeG, statusChangeH, statusChangeI, statusChangeJ, statusChangeK, 
				statusChangeL));
		
		//Request ten workers for R2
		//r2ID = new DeploymentID(new ContainerID("rwpc2User", "rwpc2server", PeerConstants.MODULE_NAME),
		//		PeerConstants.REMOTE_ACCESS_OBJECT_NAME);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 10);
		RequestSpecification requestSpec5 = new RequestSpecification(0, jobSpec, 5, buildRequirements("other.mainMemory < 4096 && other.mainMemory > 4"), 10, 0, 0);
		
		allocationB.addLoserConsumer(r3ClientID);
		allocationC.addLoserConsumer(r1ClientID);
		allocationD.addLoserConsumer(r1ClientID);
		allocationE.addLoserConsumer(r1ClientID);
		allocationJ.addLoserConsumer(r1ClientID);
		allocationI.addLoserConsumer(r1ClientID);
		allocationH.addLoserConsumer(r1ClientID);
		allocationG.addLoserConsumer(r1ClientID);
		allocationF.addLoserConsumer(r1ClientID);
		
		DeploymentID r2ClientID = new DeploymentID(r2ID.getContainerID(),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, r2ClientID, requestSpec5, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationB, allocationC, allocationD, allocationE, 
				allocationJ, allocationI, allocationH, allocationG, allocationF);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeB, statusChangeC, statusChangeD, 
				statusChangeE, statusChangeF, statusChangeG, statusChangeH, statusChangeI, statusChangeJ));
		
		//Request four workers for Sub10
		//DeploymentID rsub10ID = new DeploymentID(new ContainerID("rwpc10User", "rwpc10server", 
		//		PeerConstants.MODULE_NAME, "publicKey10"),
		//		PeerConstants.REMOTE_ACCESS_OBJECT_NAME);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 4);
		RequestSpecification requestSpec6 = new RequestSpecification(0, jobSpec, 6, buildRequirements(">", 64, null, null), 4, 0, 0);
		
		allocationF.addLoserConsumer(r2ClientID);
		allocationG.addLoserConsumer(r2ClientID);
		allocationH.addLoserConsumer(r2ClientID);
		allocationL.addLoserConsumer(r1ClientID);
		
		DeploymentID sub10ClientID = new DeploymentID(sub10ID.getContainerID(), 
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, sub10ClientID, requestSpec6, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationL, allocationF, allocationG, allocationH);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeF, statusChangeG, statusChangeH, 
				statusChangeL));
		
		//Request four workers for Sub2
		DeploymentID rsub2ClientID = new DeploymentID(new ContainerID("rwpc20User", "rwpc20server", 
				PeerConstants.MODULE_NAME, "publicKey2"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 4);
		RequestSpecification requestSpec7 = new RequestSpecification(0, jobSpec, 7, buildRequirements(">", 256, null, null), 4, 0, 0);
		
		allocationI.addLoserConsumer(r2ClientID);
		allocationJ.addLoserConsumer(r2ClientID);
		allocationK.addLoserConsumer(r1ClientID);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, rsub2ClientID, requestSpec7, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationK, allocationI, allocationJ);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeI, statusChangeJ, statusChangeK));
		
		//Request four workers for Sub12
		DeploymentID rsub12ClientID = new DeploymentID(new ContainerID("rwpc12User", "rwpc12server", 
				PeerConstants.MODULE_NAME, "publicKey12"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 4);
		RequestSpecification requestSpec8 = new RequestSpecification(0, jobSpec, 8, buildRequirements(">", 32, null, null), 4, 0, 0);
		
		allocationE.addLoserConsumer(r2ClientID);
		allocationK.addLoserConsumer(rsub2ClientID);
		allocationI.addLoserConsumer(rsub2ClientID);
		allocationJ.addLoserConsumer(rsub2ClientID);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, rsub12ClientID, requestSpec8, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationE, allocationI, allocationJ, allocationK);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeE, statusChangeI, 
				statusChangeJ, statusChangeK));
		
		//Request five workers for Sub11
		//DeploymentID rsub11ID = new DeploymentID(new ContainerID("rwpc11User", "rwpc11server", 
		//		PeerConstants.MODULE_NAME, "publicKey11"),
		//		PeerConstants.REMOTE_ACCESS_OBJECT_NAME);
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 5);
		RequestSpecification requestSpec9 = new RequestSpecification(0, jobSpec, 9, buildRequirements(">", 8, null, null), 5, 0, 0);
		
		allocationD.addLoserConsumer(r2ClientID);
		allocationC.addLoserConsumer(r2ClientID);
		allocationI.addLoserConsumer(rsub12ClientID);
		allocationJ.addLoserConsumer(rsub12ClientID);
		allocationK.addLoserConsumer(rsub12ClientID);
		
		DeploymentID sub11ClientID = new DeploymentID(sub11ID.getContainerID(),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForRemoteClient(component, sub11ClientID, requestSpec9, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationD, allocationC, allocationK, allocationI, allocationJ);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeC, statusChangeD, 
				statusChangeI, statusChangeJ, statusChangeK));
		//Request seven workers for local1
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 7);
		RequestSpecification requestSpec10 = new RequestSpecification(0, jobSpec, 10, buildRequirements(null), 7, 0, 0);
		
		allocationA.addLoserConsumer(r3ClientID);
		allocationB.addLoserConsumer(r2ClientID);
		allocationE.addLoserConsumer(rsub12ClientID);
		allocationK.addLoserConsumer(sub11ClientID);
		allocationJ.addLoserConsumer(sub11ClientID);
		allocationL.addLoserConsumer(sub10ClientID);
		allocationI.addLoserConsumer(sub11ClientID);
		
		time = System.currentTimeMillis();
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec10, 
				allocationA, allocationB, allocationE, allocationK,
				allocationJ, allocationL, allocationI);
		
		//Verify Workers Status
		statusChangeA.setStatus(WorkerStatus.IN_USE);
		statusChangeB.setStatus(WorkerStatus.IN_USE);
		statusChangeE.setStatus(WorkerStatus.IN_USE);
		statusChangeI.setStatus(WorkerStatus.IN_USE);
		statusChangeJ.setStatus(WorkerStatus.IN_USE);
		statusChangeK.setStatus(WorkerStatus.IN_USE);
		statusChangeL.setStatus(WorkerStatus.IN_USE);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA, statusChangeB, 
				statusChangeE, statusChangeI, statusChangeJ, statusChangeK, statusChangeL));
		
		//The consumer local1 set the worker A as unwanted
		allocationA = new WorkerAllocation(workerAID);
		
		time = System.currentTimeMillis();
		ScheduledFuture<?> future10 = req_016_Util.unwantedMockWorker(component, allocationA, requestSpec10, 
				lwpc1OID, true, null);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//Request eight workers for local2
		jobSpec = createJobSpecJDL(JDLUtils.ECHO_JOB, 8);
		RequestSpecification requestSpec11 = new RequestSpecification(0, jobSpec, 11, buildRequirements(null), 8, 0, 0);
		allocationH.addLoserConsumer(sub10ClientID);
		allocationC.addLoserConsumer(sub11ClientID);
		allocationG.addLoserConsumer(sub10ClientID);
		allocationD.addLoserConsumer(sub11ClientID);
		allocationF.addLoserConsumer(sub10ClientID);
		
		time = System.currentTimeMillis();
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec11, rwps,
				allocationA, allocationH, allocationC, 
				allocationG, allocationD, allocationF);
		
		//Verify Workers Status
		statusChangeC.setStatus(WorkerStatus.IN_USE);
		statusChangeD.setStatus(WorkerStatus.IN_USE);
		statusChangeF.setStatus(WorkerStatus.IN_USE);
		statusChangeG.setStatus(WorkerStatus.IN_USE);
		statusChangeH.setStatus(WorkerStatus.IN_USE);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA, statusChangeC, 
				statusChangeD, statusChangeF, statusChangeG, statusChangeH));
		
		//The consumer local2 set the worker A as unwanted
		time = System.currentTimeMillis();
		req_016_Util.unwantedMockWorker(component, allocationA, requestSpec11, lwpc2OID, false, null);
		
		//Verify Workers Status
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeA));
		
		//The consumer local2 set the worker c as unwanted
		allocationC.addWinnerConsumer(lwpc1OID).addLoserConsumer(lwpc2OID);
		time = System.currentTimeMillis();
		req_016_Util.unwantedMockWorker(component, allocationC, requestSpec11, lwpc2OID, false, future10);
		
		//Verify Workers Status
		AG_WorkerStatusChange statusChangeC2 = new AG_WorkerStatusChange();
		statusChangeC2.setStatus(WorkerStatus.IN_USE);
		statusChangeC2.setWorker(workerCInfo);
		peerDBTest.verifyWorkerStatusChange(component, time, AcceptanceTestUtil.createList(statusChangeC, statusChangeC2));
	}	
}