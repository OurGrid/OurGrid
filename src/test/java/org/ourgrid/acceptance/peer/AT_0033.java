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
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
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
import org.ourgrid.acceptance.util.peer.Req_115_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
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

public class AT_0033 extends PeerAcceptanceTestCase {

	private static final String AUX_FILES_PATH = "it_0033" + File.separator;
	private static final String RANKING_FILEPATH = PeerAcceptanceUtil.TEST_FILES_PATH + AUX_FILES_PATH + "file.dat"; 
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
    private Req_115_Util req_115_Util = new Req_115_Util(getComponentContext());

    @After
	public void tearDown() throws Exception {
		super.tearDown();
		peerAcceptanceUtil.deleteFile(RANKING_FILEPATH);
	}
	
	@ReqTest(test="AT-0033", reqs="REQ011, REQ016, REQ027, REQ110, REQ111, REQ115")
	@Test public void test_AT_0033_LocalAndRemoteRedistributionWithSubcommunitiesConsideringMatchAndUnwantedWorkersAndNofPersistence()
			throws Exception {
	
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user01", "server01", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user02", "server01", "011011");
		
		//Start the peer with the property peer.rankingfile set to a file that does not exist
		getComponentContext().set(PeerConfiguration.PROP_RANKINGFILE, RANKING_FILEPATH);
		
		//Expect the peer to schedule the file save of the network of favors data
		component = req_010_Util.startPeer();
		
		
		String workerServerName = "xmpp.ourgrid.org";

		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workera", workerServerName);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_MEM, "4");
		
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("workerB", workerServerName);
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_MEM, "32");
		String workerBPublicKey = "workerBPublicKey";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("workerC", workerServerName);
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_MEM, "64");
		String workerCPublicKey = "workerCPublicKey";
		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("workerD", workerServerName);
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_MEM, "128");
		String workerDPublicKey = "workerDPublicKey";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDID);

		WorkerSpecification workerSpecE = workerAcceptanceUtil.createWorkerSpec("workerE", workerServerName);
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_MEM, "256");
		String workerEPublicKey = "workerEPublicKey";
		DeploymentID workerEID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEID);

		WorkerSpecification workerSpecF = workerAcceptanceUtil.createWorkerSpec("workerF", workerServerName);
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		String workerFPublicKey = "workerFPublicKey";
		DeploymentID workerFID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, workerFPublicKey);
		req_010_Util.workerLogin(component, workerSpecF, workerFID);

		WorkerSpecification workerSpecG = workerAcceptanceUtil.createWorkerSpec("workerG", workerServerName);
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_MEM, "1024");
		String workerGPublicKey = "workerGPublicKey";
		DeploymentID workerGID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecG, workerGPublicKey);
		req_010_Util.workerLogin(component, workerSpecG, workerGID);

		WorkerSpecification workerSpecH = workerAcceptanceUtil.createWorkerSpec("workerH", workerServerName);
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_MEM, "2048");
		String workerHPublicKey = "workerHPublicKey";
		DeploymentID workerHID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecH, workerHPublicKey);
		req_010_Util.workerLogin(component, workerSpecH, workerHID);

		WorkerSpecification workerSpecI = workerAcceptanceUtil.createWorkerSpec("workeri", workerServerName);
		workerSpecI.putAttribute(OurGridSpecificationConstants.ATT_MEM, "1024");
		String workerIPublicKey = "workerIPublicKey";
		DeploymentID workerIID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecI, workerIPublicKey);
		req_010_Util.workerLogin(component, workerSpecI, workerIID);

		WorkerSpecification workerSpecJ = workerAcceptanceUtil.createWorkerSpec("workerj", workerServerName);
		workerSpecJ.putAttribute(OurGridSpecificationConstants.ATT_MEM, "2048");
		String workerJPublicKey = "workerJPublicKey";
		DeploymentID workerJID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecJ, workerJPublicKey);
		req_010_Util.workerLogin(component, workerSpecJ, workerJID);

		WorkerSpecification workerSpecK = workerAcceptanceUtil.createWorkerSpec("workerk", workerServerName);
		workerSpecK.putAttribute(OurGridSpecificationConstants.ATT_MEM, "4096");
		String workerKPublicKey = "workerKPublicKey";
		DeploymentID workerKID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecK, workerKPublicKey);
		req_010_Util.workerLogin(component, workerSpecK, workerKID);
		
		WorkerSpecification workerSpecL = workerAcceptanceUtil.createWorkerSpec("workerl", workerServerName);
		workerSpecL.putAttribute(OurGridSpecificationConstants.ATT_MEM, "8192");
		String workerLPublicKey = "workerLPublicKey";
		DeploymentID workerLID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecL, workerLPublicKey);
		req_010_Util.workerLogin(component, workerSpecL, workerLID);		
		
		//Change workers status to IDLE
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
		
		DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		
		LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
		
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
		
		DeploymentID lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2OID);
		
		//Request six workers for local1
		JobSpecification jobSpec = createJobSpec("label", 6);
		RequestSpecification requestSpec1 = new RequestSpecification(0, jobSpec, 1, "mem < 8", 6, 0, 0);
		
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec1, allocationA);
		
		//GIS client receive a remote worker provider (sub11)
		String sub11PublicKey = "publicKey11";
		WorkerSpecification remoteWorkerSpec11 = workerAcceptanceUtil.createWorkerSpec("U1", "SS11");
		remoteWorkerSpec11.putAttribute(OurGridSpecificationConstants.ATT_MEM, "4");
		TestStub sub11Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp11User", 
				"rwp11Server", sub11PublicKey, remoteWorkerSpec11);
		
		RemoteWorkerProvider sub11 = (RemoteWorkerProvider) sub11Stub.getObject();
		
		DeploymentID sub11ID = sub11Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW11)
		String worker11PubKey = "worker11PubKey"; 
		DeploymentID rwm11OID = req_018_Util.receiveRemoteWorker(component, sub11, sub11ID, remoteWorkerSpec11, worker11PubKey, broker1PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (sub2)
		String sub2PublicKey = "publicKey2";
		WorkerSpecification remoteWorkerSpec20 = workerAcceptanceUtil.createWorkerSpec("U1", "SS2");
		remoteWorkerSpec20.putAttribute(OurGridSpecificationConstants.ATT_MEM, "4");

		TestStub sub2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp22User", 
				"rwp22Server", sub2PublicKey, remoteWorkerSpec20);
		
		RemoteWorkerProvider sub2 = (RemoteWorkerProvider) sub2Stub.getObject();
		
		DeploymentID sub2ID = sub2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW20)
		String worker20PubKey = "worker20PubKey"; 
		DeploymentID rwm20OID = req_018_Util.receiveRemoteWorker(component, sub2, sub2ID, remoteWorkerSpec20, worker20PubKey, broker1PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (r1)
		String r1PublicKey = "r1PublicKey";
		WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		remoteWorkerSpec1.putAttribute(OurGridSpecificationConstants.ATT_MEM, "4");
		TestStub r1Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp1User", 
				"rwp1Server", r1PublicKey, remoteWorkerSpec1);
		
		RemoteWorkerProvider r1 = (RemoteWorkerProvider) r1Stub.getObject();
		
		DeploymentID r1ID = r1Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (rw1)
		String worker1PubKey = "worker1PubKey"; 
		DeploymentID rwm1OID = req_018_Util.receiveRemoteWorker(component, r1, r1ID, remoteWorkerSpec1, worker1PubKey, broker1PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (r2)
		String r2PublicKey = "r2PublicKey";
		WorkerSpecification remoteWorkerSpec2 = workerAcceptanceUtil.createWorkerSpec("U1", "S2");
		remoteWorkerSpec2.putAttribute(OurGridSpecificationConstants.ATT_MEM, "4");
		TestStub r2Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwp2User", 
				"rwp2Server", r2PublicKey, remoteWorkerSpec2);
		
		RemoteWorkerProvider r2 = (RemoteWorkerProvider) r2Stub.getObject();
		
		DeploymentID r2ID = r2Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (rw2)
		String worker2PubKey = "worker2PubKey"; 
		DeploymentID rwm2OID = req_018_Util.receiveRemoteWorker(component, r2, r2ID, remoteWorkerSpec2, worker2PubKey, broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER (workerA)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc1OID, workerAID, workerSpecA, requestSpec1);
		
		//Change worker status to ALLOCATED FOR BROKER (RW11)
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
		
		//Report a received favour (workerA)
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				new GridProcessAccounting(requestSpec1, workerAID.toString(), workerAID.getPublicKey(), 
						100., 0., GridProcessState.FINISHED, workerSpecA), true);
		
		//Report a received favour (RW11)
		GridProcessAccounting acc1 = new GridProcessAccounting(requestSpec1, rwm11OID.toString(), rwm11OID.getPublicKey(), 
				80., 0., GridProcessState.FINISHED, remoteWorkerSpec11);
		acc1.setTaskSequenceNumber(2);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc1, false);
		
		//Report a received favour (RW20)
		GridProcessAccounting acc2 = new GridProcessAccounting(requestSpec1, rwm20OID.toString(), rwm20OID.getPublicKey(), 
				150., 0., GridProcessState.FINISHED, remoteWorkerSpec20);
		acc2.setTaskSequenceNumber(3);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc2, false);
		
		//Report a received favour (RW1)
		GridProcessAccounting acc3 = new GridProcessAccounting(requestSpec1, rwm1OID.toString(), rwm1OID.getPublicKey(), 
				120., 0., GridProcessState.FINISHED, remoteWorkerSpec1);
		acc3.setTaskSequenceNumber(4);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc3, false);
		
		//Report a received favour (RW2)
		GridProcessAccounting acc4 = new GridProcessAccounting(requestSpec1, rwm2OID.toString(), rwm2OID.getPublicKey(), 
				120., 0., GridProcessState.FINISHED, remoteWorkerSpec2);
		acc4.setTaskSequenceNumber(5);
		req_027_Util.reportReplicaAccounting(component, lwpc1OID, 
				acc4, false);
		
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
		
		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD, 
				broker1PubKey, lwpc1OID.getServiceID(), null, requestSpec1, AcceptanceTestUtil.createList(allocationA), 
				new TestStub(sub11ID, remotePeer11), new TestStub(sub2ID, remotePeer20), new TestStub(r1ID, remotePeer1), 
				new TestStub(r2ID, remotePeer2));
		
		//Request five workers for local2
		jobSpec = createJobSpec("label", 5);
		RequestSpecification requestSpec2 = new RequestSpecification(0, jobSpec, 2, "mem < 8", 5, 0, 0);
		
		List<TestStub> rwps = new ArrayList<TestStub>();
		rwps.add(new TestStub(sub11ID, sub11));
		rwps.add(new TestStub(sub2ID, sub2));
		rwps.add(new TestStub(r1ID, r1));
		rwps.add(new TestStub(r2ID, r2));
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec2, rwps, 
				allocationA);
		
		//GIS client receive a remote worker provider (sub10)
		String sub10PublicKey = "publicKey10";
		WorkerSpecification remoteWorkerSpec10 = workerAcceptanceUtil.createWorkerSpec("U1", "SS10");
		remoteWorkerSpec10.putAttribute(OurGridSpecificationConstants.ATT_MEM, "4");
		TestStub sub10Stub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec2, 
				"rwp10User", "rwp10Server", sub10PublicKey, remoteWorkerSpec10);
		
		RemoteWorkerProvider sub10 = (RemoteWorkerProvider) sub10Stub.getObject();
		
		DeploymentID sub10ID = sub10Stub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW10)
		String worker10PubKey = "worker10PubKey"; 
		DeploymentID rwm10OID = req_018_Util.receiveRemoteWorker(component, sub10, sub10ID, remoteWorkerSpec10, worker10PubKey, broker2PubKey).getDeploymentID();
		
		//GIS client receive a remote worker provider (r2)
		WorkerSpecification remoteWorkerSpec4 = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
		remoteWorkerSpec4.putAttribute(OurGridSpecificationConstants.ATT_MEM, "4");
		
		//Remote worker provider client receive a remote worker (RW2)
		rwm2OID = req_018_Util.receiveRemoteWorker(component, r2, r2ID, remoteWorkerSpec2, worker2PubKey, broker2PubKey).getDeploymentID();
		
		//Remote worker provider client receive a remote worker (RW4)
		String worker4PubKey = "worker4PubKey"; 
		DeploymentID rwm4OID = req_018_Util.receiveRemoteWorker(component, r2, r2ID, remoteWorkerSpec4, worker4PubKey, broker2PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER (workerA)
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpc2OID, workerAID, workerSpecA, requestSpec2);
		
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
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, 
				new GridProcessAccounting(requestSpec2, workerAID.toString(), workerAID.getPublicKey(), 
						100., 0., GridProcessState.FINISHED, workerSpecA), true);
		
		//Report a received favour (RW10)
		GridProcessAccounting acc5 = new GridProcessAccounting(requestSpec2, rwm10OID.toString(), rwm10OID.getPublicKey(), 
				300., 0., GridProcessState.FINISHED, remoteWorkerSpec10);
		acc5.setTaskSequenceNumber(2);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, acc5, false);
		
		//Report a received favour (RW4)
		GridProcessAccounting acc6 = new GridProcessAccounting(requestSpec2, rwm4OID.toString(), rwm4OID.getPublicKey(), 
				130., 0., GridProcessState.FINISHED, remoteWorkerSpec4);
		acc6.setTaskSequenceNumber(3);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, acc6, false);
		
		//Report a received favour (RW2)
		GridProcessAccounting acc7 = new GridProcessAccounting(requestSpec2, rwm2OID.toString(), rwm2OID.getPublicKey(), 
				40., 0., GridProcessState.FINISHED, remoteWorkerSpec2);
		acc7.setTaskSequenceNumber(4);
		req_027_Util.reportReplicaAccounting(component, lwpc2OID, acc7, false);
		
		
		//Finish the request
		WorkerAllocation remoteAllocation10 = new WorkerAllocation(rwm10OID);
		remoteAllocation2 = new WorkerAllocation(rwm2OID);
		WorkerAllocation remoteAllocation4 = new WorkerAllocation(rwm4OID);
		
		RemoteAllocation remotePeer10 = new RemoteAllocation(sub10, AcceptanceTestUtil.createList(remoteAllocation10));
		remotePeer2 = new RemoteAllocation(r2, AcceptanceTestUtil.createList(remoteAllocation2, remoteAllocation4));

		req_014_Util.finishRequestWithLocalAndRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), lwpOD,  
				broker2PubKey, lwpc2OID.getServiceID(), null, requestSpec2, AcceptanceTestUtil.createList(allocationA), 
				new TestStub(sub10ID, remotePeer10), new TestStub(r2ID, remotePeer2));
		
		//Stop the Peer
		req_010_Util.notNiceStopPeer(component);
		//BindRegistry.reset();
		PeerAcceptanceUtil.copyTrustFile(AUX_FILES_PATH+"trust.xml");
		
		//Start the peer with the property peer.rankingfile set to the file that was previously used
		component = peerAcceptanceUtil.createPeerComponent(getComponentContext());
		
		//Expect the peer to schedule the file save of the network of favors data
		req_115_Util.startPeerVerifyingAccountingSavingWithNoError(component);
		
		
		workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

		workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDID);

		workerEID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEID);

		workerFID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, workerFPublicKey);
		req_010_Util.workerLogin(component, workerSpecF, workerFID);

		workerGID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecG, workerGPublicKey);
		req_010_Util.workerLogin(component, workerSpecG, workerGID);

		workerHID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecH, workerHPublicKey);
		req_010_Util.workerLogin(component, workerSpecH, workerHID);

		workerIID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecI, workerIPublicKey);
		req_010_Util.workerLogin(component, workerSpecI, workerIID);

		workerJID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecJ, workerJPublicKey);
		req_010_Util.workerLogin(component, workerSpecJ, workerJID);

		workerKID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecK, workerKPublicKey);
		req_010_Util.workerLogin(component, workerSpecK, workerKID);

		workerLID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecL, workerLPublicKey);
		req_010_Util.workerLogin(component, workerSpecL, workerLID);

		//Change workers status to IDLE
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
		
		//DiscoveryService recovery
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Login with two valid users
		lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
		lwpc2OID = req_108_Util.login(component, user2, broker2PubKey);
		
		
		//Request five workers for R3
		jobSpec = createJobSpec("label", 5);
		RequestSpecification requestSpec3 = new RequestSpecification(0, jobSpec, 3, "", 5, 0, 0);
		DeploymentID r3ID = new DeploymentID(new ContainerID("rwpc3User", "rwpc3Server", PeerConstants.MODULE_NAME, "r3PubKey"),
				PeerConstants.REMOTE_WORKER_PROVIDER);
		
		allocationA = new WorkerAllocation(workerAID);	
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		WorkerAllocation allocationC = new WorkerAllocation(workerCID);
		WorkerAllocation allocationD = new WorkerAllocation(workerDID);
		WorkerAllocation allocationE = new WorkerAllocation(workerEID);
		
		req_011_Util.requestForRemoteClient(component, r3ID, requestSpec3, 3, 
				allocationA, allocationB, allocationC, allocationD, allocationE);
				
		//Request ten workers for R1
		jobSpec = createJobSpec("label", 10);
		RequestSpecification requestSpec4 = new RequestSpecification(0, jobSpec, 4, "", 10, 0, 0);
		
		WorkerAllocation allocationF = new WorkerAllocation(workerFID);
		WorkerAllocation allocationG = new WorkerAllocation(workerGID);
		WorkerAllocation allocationH = new WorkerAllocation(workerHID);
		WorkerAllocation allocationI = new WorkerAllocation(workerIID);
		WorkerAllocation allocationJ = new WorkerAllocation(workerJID);
		WorkerAllocation allocationK = new WorkerAllocation(workerKID);
		WorkerAllocation allocationL = new WorkerAllocation(workerLID);
		allocationE.addLoserConsumer(r3ID);
		allocationD.addLoserConsumer(r3ID);
		allocationC.addLoserConsumer(r3ID);
		
		req_011_Util.requestForRemoteClient(component, r1ID, requestSpec4, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationF, allocationG, allocationH, allocationI, allocationJ, 
				allocationK, allocationL, allocationE, allocationD, allocationC);
		
		
		//Request ten workers for R2
		jobSpec = createJobSpec("label", 10);
		RequestSpecification requestSpec5 = new RequestSpecification(0, jobSpec, 5, "(mem < 4096) AND (mem > 4)", 10, 0, 0);
		
		allocationB.addLoserConsumer(r3ID);
		allocationC.addLoserConsumer(r1ID);
		allocationD.addLoserConsumer(r1ID);
		allocationE.addLoserConsumer(r1ID);
		allocationJ.addLoserConsumer(r1ID);
		allocationI.addLoserConsumer(r1ID);
		allocationH.addLoserConsumer(r1ID);
		allocationG.addLoserConsumer(r1ID);
		allocationF.addLoserConsumer(r1ID);
		
		req_011_Util.requestForRemoteClient(component, r2ID, requestSpec5, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationB, allocationC, allocationD, allocationE, 
				allocationJ, allocationI, allocationH, allocationG, allocationF);
		
		//Request four workers for Sub10
		jobSpec = createJobSpec("label", 4);
		RequestSpecification requestSpec6 = new RequestSpecification(0, jobSpec, 6, "mem > 64", 4, 0, 0);
		
		allocationF.addLoserConsumer(r2ID);
		allocationG.addLoserConsumer(r2ID);
		allocationH.addLoserConsumer(r2ID);
		allocationL.addLoserConsumer(r1ID);
		
		req_011_Util.requestForRemoteClient(component, sub10ID, requestSpec6, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationL, allocationF, allocationG, allocationH);
		
		//Request four workers for Sub2
		jobSpec = createJobSpec("label", 4);
		RequestSpecification requestSpec7 = new RequestSpecification(0, jobSpec, 7, "mem > 256", 4, 0, 0);
		
		allocationI.addLoserConsumer(r2ID);
		allocationJ.addLoserConsumer(r2ID);
		allocationK.addLoserConsumer(r1ID);
		
		req_011_Util.requestForRemoteClient(component, sub2ID, requestSpec7, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationK, allocationI, allocationJ);
		
		//Request four workers for Sub12
		DeploymentID rsub12ID = new DeploymentID(new ContainerID("rwpc12User", "rwpc12server", 
				PeerConstants.MODULE_NAME, "publicKey12"), PeerConstants.REMOTE_WORKER_PROVIDER);
		jobSpec = createJobSpec("label", 4);
		RequestSpecification requestSpec8 = new RequestSpecification(0, jobSpec, 8, "mem > 32", 4, 0, 0);
		
		allocationE.addLoserConsumer(r2ID);
		allocationK.addLoserConsumer(sub2ID);
		allocationI.addLoserConsumer(sub2ID);
		allocationJ.addLoserConsumer(sub2ID);
		
		req_011_Util.requestForRemoteClient(component, rsub12ID, requestSpec8, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationE, allocationI, allocationJ, allocationK);
		
		//Request five workers for Sub11
		 jobSpec = createJobSpec("label", 5);
		RequestSpecification requestSpec9 = new RequestSpecification(0, jobSpec, 9, "mem > 8", 5, 0, 0);
		
		allocationD.addLoserConsumer(r2ID);
		allocationC.addLoserConsumer(r2ID);
		allocationI.addLoserConsumer(rsub12ID);
		allocationJ.addLoserConsumer(rsub12ID);
		allocationK.addLoserConsumer(rsub12ID);
		
		req_011_Util.requestForRemoteClient(component, sub11ID, requestSpec9, Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES, 
				allocationD, allocationC, allocationK, allocationI, allocationJ);

		//Request seven workers for local1
		jobSpec = createJobSpec("label", 7);
		RequestSpecification requestSpec10 = new RequestSpecification(0, jobSpec, 10, "", 7, 0, 0);
		
		allocationA.addLoserConsumer(r3ID);
		allocationB.addLoserConsumer(r2ID);
		allocationE.addLoserConsumer(rsub12ID);
		allocationK.addLoserConsumer(sub11ID);
		allocationJ.addLoserConsumer(sub11ID);
		allocationL.addLoserConsumer(sub10ID);
		allocationI.addLoserConsumer(sub11ID);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc1), requestSpec10, 
				allocationA, allocationB, allocationE, allocationK,
				allocationJ, allocationL, allocationI);
		
		//The consumer local1 set the worker A as unwanted
		allocationA = new WorkerAllocation(workerAID);
		
		ScheduledFuture<?> future10 = req_016_Util.unwantedMockWorker(component, allocationA, requestSpec10, 
				lwpc1OID, true, null);
		
		//Request eight workers for local2
		jobSpec = createJobSpec("label", 8);
		RequestSpecification requestSpec11 = new RequestSpecification(0, jobSpec, 11, "", 8, 0, 0);
		allocationH.addLoserConsumer(sub10ID);
		allocationC.addLoserConsumer(sub11ID);
		allocationG.addLoserConsumer(sub10ID);
		allocationD.addLoserConsumer(sub11ID);
		allocationF.addLoserConsumer(sub10ID);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2OID, lwpc2), requestSpec11, 
				allocationA, allocationH, allocationC, 
				allocationG, allocationD, allocationF);
		
		//The consumer local2 set the worker A as unwanted
		req_016_Util.unwantedMockWorker(component, allocationA, requestSpec11, lwpc2OID, false, null);
		
		//The consumer local2 set the worker c as unwanted
		allocationC.addWinnerConsumer(lwpc1OID);
		req_016_Util.unwantedMockWorker(component, allocationC, requestSpec11, lwpc2OID, false, future10);

	}
}