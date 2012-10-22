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

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_016_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0029 extends PeerAcceptanceTestCase {

	public static final String AUX_FILES_PATH = "it_0029" + File.separator;
	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_016_Util req_016_Util = new Req_016_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());

	@ReqTest(test="AT-0029", reqs="REQ011, REQ016, REQ110, REQ111")
	@Test public void test_AT_0029_LocalAndRemoteRedistributionWithSubcommunitiesConsideringMatchAndUnwantedWorkers()
			throws Exception {
		
		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user01", "server01", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user02", "server01", "011011");
		
		// Start the peer with a trust configuration file
		component = req_010_Util.startPeer();
		PeerAcceptanceUtil.copyTrustFile(AUX_FILES_PATH + "trust.xml");
		
		
		String workerServerName = "xmpp.ourgrid.org";

		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerA", workerServerName);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_MEM, "16");
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

		// Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);
		req_025_Util.changeWorkerStatusToIdle(component, workerEID);
		req_025_Util.changeWorkerStatusToIdle(component, workerFID);
		req_025_Util.changeWorkerStatusToIdle(component, workerGID);
		req_025_Util.changeWorkerStatusToIdle(component, workerHID);

		// Login with two valid users
		String broker1PublicKey = "mg1PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc1ID = req_108_Util.login(component, user1, broker1PublicKey);
		LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		String broker2PublicKey = "mg2PublicKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request two workers for local1
		int request1ID = 1;
		String requirements1 = "mem < 128";
		int requiredWorkers1 = 2;
		
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		WorkerAllocation allocationC = new WorkerAllocation(workerCID);
		WorkerAllocation allocationD = new WorkerAllocation(workerDID);
		WorkerAllocation allocationE = new WorkerAllocation(workerEID);
		WorkerAllocation allocationF = new WorkerAllocation(workerFID);
		WorkerAllocation allocationG = new WorkerAllocation(workerGID);
		WorkerAllocation allocationH = new WorkerAllocation(workerHID);
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, requirements1, requiredWorkers1, 0, 0);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc1), requestSpec1, allocationB, allocationC);
		
		// The consumer local1 set the worker B as unwanted - expect to command the worker B to stop working
		req_016_Util.unwantedMockWorker(component, allocationB, requestSpec1, lwpc1ID, true, null);
		
		// Request ten workers for remote1
		int request2ID = 2;
		String requirements2 = "";
		int requiredWorkers2 = 10;
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, requirements2, requiredWorkers2, 0, 0);
		
		DeploymentID rwpcID = new DeploymentID(new ContainerID("rwpcName", "rwpcServer", WorkerConstants.MODULE_NAME, "publicKeyR1"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		req_011_Util.requestForRemoteClient(component, rwpcID, requestSpec2, 2,
				allocationB, allocationH, allocationG, allocationF, 
				allocationE, allocationD, allocationA);
		
		// Request ten workers for remote2
		int request3ID = 3;
		String requirements3 = "mem > 512";
		int requiredWorkers3 = 10;
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), request3ID, requirements3, requiredWorkers3, 0, 0);

		DeploymentID rwpc2ID = new DeploymentID(new ContainerID("rwpc2Name", "rwpc2Server", PeerConstants.MODULE_NAME, "publicKeyR2"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);

		req_011_Util.requestForRemoteClient(component, rwpc2ID, requestSpec3,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				allocationG.addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				allocationH.addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2));
		
		// Request three workers for sub11
		int request4ID = 4;
		String requirements4 = "mem >= 256";
		int requiredWorkers4 = 3;
		RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), request4ID, requirements4, requiredWorkers4, 0, 0);

		DeploymentID rwpc3ID = new DeploymentID(new ContainerID("rwpc3Name", "rwpc3Server", PeerConstants.MODULE_NAME, "publicKey11"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);

		req_011_Util.requestForRemoteClient(component, rwpc3ID, requestSpec4,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				allocationE.addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				allocationF.addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				allocationH.addLoserConsumer(rwpc2ID).addLoserRequestSpec(requestSpec3));
		
		// Request three workers for sub12
		int request5ID = 5;
		String requirements5 = "mem > 512";
		int requiredWorkers5 = 3;
		RequestSpecification requestSpec5 = new RequestSpecification(0, new JobSpecification("label"), request5ID, requirements5, requiredWorkers5, 0, 0);

		DeploymentID rwpc4ID = new DeploymentID(new ContainerID("sub12", "rwpc4Server", PeerConstants.MODULE_NAME, "publicKey12"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);

		req_011_Util.requestForRemoteClient(component, rwpc4ID, requestSpec5,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				new WorkerAllocation(workerGID).addLoserConsumer(rwpc2ID).addLoserRequestSpec(requestSpec3),
				new WorkerAllocation(workerHID).addLoserConsumer(rwpc3ID).addLoserRequestSpec(requestSpec4));
		
		// Request three workers for sub2
		int request6ID = 6;
		String requirements6 = "";
		int requiredWorkers6 = 3;
		RequestSpecification requestSpec6 = new RequestSpecification(0, new JobSpecification("label"), request6ID, requirements6, requiredWorkers6, 0, 0);

		DeploymentID rwpc5ID = new DeploymentID(new ContainerID("sub2", "rwpc5Server", PeerConstants.MODULE_NAME, "publicKey2"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);

		req_011_Util.requestForRemoteClient(component, rwpc5ID, requestSpec6,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				new WorkerAllocation(workerDID).addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				new WorkerAllocation(workerBID).addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				new WorkerAllocation(workerAID).addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2));
		
		// Request eight workers for local2
		int request7ID = 7;
		String requirements7 = "mem > 32";
		int requiredWorkers7 = 8;
		
		RequestSpecification requestSpec7 = new RequestSpecification(0, new JobSpecification("label"), request7ID, requirements7, requiredWorkers7, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2),	requestSpec7,
				new WorkerAllocation(workerDID).addLoserConsumer(rwpc5ID), new WorkerAllocation(workerGID).addLoserConsumer(rwpc4ID),
				new WorkerAllocation(workerEID).addLoserConsumer(rwpc3ID), new WorkerAllocation(workerHID).addLoserConsumer(rwpc4ID),
				new WorkerAllocation(workerFID).addLoserConsumer(rwpc3ID));
		
		// Request ten workers for local1
		int request8ID = 8;
		String requirements8 = "";
		int requiredWorkers8 = 10;
		
		RequestSpecification requestSpec8 = new RequestSpecification(0, new JobSpecification("label"), request8ID, requirements8, requiredWorkers8, 0, 0);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc1),	requestSpec8,
				new WorkerAllocation(workerAID).addLoserConsumer(rwpc5ID), new WorkerAllocation(workerBID).addLoserConsumer(rwpc5ID),
				new WorkerAllocation(workerEID).addLoserConsumer(lwpc2ID));
	}
	
	@Category(JDLCompliantTest.class)
	@Test 
	public void test_AT_0029_LocalAndRemoteRedistributionWithSubcommunitiesConsideringMatchAndUnwantedWorkersWithJDL()
	throws Exception {

		//Create two user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser("user01", "server01", "011011");
		XMPPAccount user2 = req_101_Util.createLocalUser("user02", "server01", "011011");
		
		// Start the peer with a trust configuration file
		component = req_010_Util.startPeer();
		PeerAcceptanceUtil.copyTrustFile(AUX_FILES_PATH + "trust.xml");
		
		// Workers login
		String workerServerName = "xmpp.ourgrid.org";
		
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("workerA", workerServerName, 16, null);
		String workerAPublicKey = "workerAPublicKey";
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerAID);
		
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("workerB", workerServerName, 32, null);
		String workerBPublicKey = "workerBPublicKey";
		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBID);
		
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("workerC", workerServerName, 64, null);
		String workerCPublicKey = "workerCPublicKey";
		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCID);
		
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("workerD", workerServerName, 128, null);
		String workerDPublicKey = "workerDPublicKey";
		DeploymentID workerDID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPublicKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDID);
		
		WorkerSpecification workerSpecE = workerAcceptanceUtil.createClassAdWorkerSpec("workerE", workerServerName, 256, null);
		String workerEPublicKey = "workerEPublicKey";
		DeploymentID workerEID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecE, workerEPublicKey);
		req_010_Util.workerLogin(component, workerSpecE, workerEID);
		
		WorkerSpecification workerSpecF = workerAcceptanceUtil.createClassAdWorkerSpec("workerF", workerServerName, 512, null);
		String workerFPublicKey = "workerFPublicKey";
		DeploymentID workerFID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecF, workerFPublicKey);
		req_010_Util.workerLogin(component, workerSpecF, workerFID);
		
		WorkerSpecification workerSpecG = workerAcceptanceUtil.createClassAdWorkerSpec("workerG", workerServerName, 1024, null);
		String workerGPublicKey = "workerGPublicKey";
		DeploymentID workerGID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecG, workerGPublicKey);
		req_010_Util.workerLogin(component, workerSpecG, workerGID);
		
		WorkerSpecification workerSpecH = workerAcceptanceUtil.createClassAdWorkerSpec("workerH", workerServerName, 2048, null);
		String workerHPublicKey = "workerHPublicKey";
		DeploymentID workerHID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecH, workerHPublicKey);
		req_010_Util.workerLogin(component, workerSpecH, workerHID);
		
		// Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);
		req_025_Util.changeWorkerStatusToIdle(component, workerDID);
		req_025_Util.changeWorkerStatusToIdle(component, workerEID);
		req_025_Util.changeWorkerStatusToIdle(component, workerFID);
		req_025_Util.changeWorkerStatusToIdle(component, workerGID);
		req_025_Util.changeWorkerStatusToIdle(component, workerHID);
		
		// Login with two valid users
		String broker1PublicKey = "mg1PublicKey";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc1ID = req_108_Util.login(component, user1, broker1PublicKey);
		LocalWorkerProviderClient lwpc1 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1ID);
		
		String broker2PublicKey = "mg2PublicKey";
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), broker2PublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpc2ID = req_108_Util.login(component, user2, broker2PublicKey);
		LocalWorkerProviderClient lwpc2 = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc2ID);
		
		// Request two workers for local1
		int request1ID = 1;
		String requirements1 = buildRequirements("<", 128, null, null);
		int requiredWorkers1 = 2;
		
		WorkerAllocation allocationA = new WorkerAllocation(workerAID);
		WorkerAllocation allocationB = new WorkerAllocation(workerBID);
		WorkerAllocation allocationC = new WorkerAllocation(workerCID);
		WorkerAllocation allocationD = new WorkerAllocation(workerDID);
		WorkerAllocation allocationE = new WorkerAllocation(workerEID);
		WorkerAllocation allocationF = new WorkerAllocation(workerFID);
		WorkerAllocation allocationG = new WorkerAllocation(workerGID);
		WorkerAllocation allocationH = new WorkerAllocation(workerHID);
		
		RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, requirements1, requiredWorkers1, 0, 0);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc1), requestSpec1, allocationB, allocationC);
		
		// The consumer local1 set the worker B as unwanted - expect to command the worker B to stop working
		req_016_Util.unwantedMockWorker(component, allocationB, requestSpec1, lwpc1ID, true, null);
		
		// Request ten workers for remote1
		int request2ID = 2;
		String requirements2 = buildRequirements(null);
		int requiredWorkers2 = 10;
		RequestSpecification requestSpec2 = new RequestSpecification(0, new JobSpecification("label"), request2ID, requirements2, requiredWorkers2, 0, 0);
		
		DeploymentID rwpcID = new DeploymentID(new ContainerID("rwpcName", "rwpcServer", WorkerConstants.MODULE_NAME, "publicKeyR1"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		req_011_Util.requestForRemoteClient(component, rwpcID, requestSpec2, 2,
				allocationB, allocationH, allocationG, allocationF, 
				allocationE, allocationD, allocationA);
		
		// Request ten workers for remote2
		int request3ID = 3;
		String requirements3 = buildRequirements(">", 512, null, null);
		int requiredWorkers3 = 10;
		RequestSpecification requestSpec3 = new RequestSpecification(0, new JobSpecification("label"), request3ID, requirements3, requiredWorkers3, 0, 0);
		
		DeploymentID rwpc2ID = new DeploymentID(new ContainerID("rwpc2Name", "rwpc2Server", PeerConstants.MODULE_NAME, "publicKeyR2"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		req_011_Util.requestForRemoteClient(component, rwpc2ID, requestSpec3,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				allocationG.addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				allocationH.addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2));
		
		// Request three workers for sub11
		int request4ID = 4;
		String requirements4 = buildRequirements(">=", 256, null, null);
		int requiredWorkers4 = 3;
		RequestSpecification requestSpec4 = new RequestSpecification(0, new JobSpecification("label"), request4ID, requirements4, requiredWorkers4, 0, 0);
		
		DeploymentID rwpc3ID = new DeploymentID(new ContainerID("rwpc3Name", "rwpc3Server", PeerConstants.MODULE_NAME, "publicKey11"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		req_011_Util.requestForRemoteClient(component, rwpc3ID, requestSpec4,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				allocationE.addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				allocationF.addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				allocationH.addLoserConsumer(rwpc2ID).addLoserRequestSpec(requestSpec3));
		
		// Request three workers for sub12
		int request5ID = 5;
		String requirements5 = buildRequirements(">", 512, null, null);
		int requiredWorkers5 = 3;
		RequestSpecification requestSpec5 = new RequestSpecification(0, new JobSpecification("label"), request5ID, requirements5, requiredWorkers5, 0, 0);
		
		DeploymentID rwpc4ID = new DeploymentID(new ContainerID("sub12", "rwpc4Server", PeerConstants.MODULE_NAME, "publicKey12"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		req_011_Util.requestForRemoteClient(component, rwpc4ID, requestSpec5,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				new WorkerAllocation(workerGID).addLoserConsumer(rwpc2ID).addLoserRequestSpec(requestSpec3),
				new WorkerAllocation(workerHID).addLoserConsumer(rwpc3ID).addLoserRequestSpec(requestSpec4));
		
		// Request three workers for sub2
		int request6ID = 6;
		String requirements6 = buildRequirements(null);
		int requiredWorkers6 = 3;
		RequestSpecification requestSpec6 = new RequestSpecification(0, new JobSpecification("label"), request6ID, requirements6, requiredWorkers6, 0, 0);
		
		DeploymentID rwpc5ID = new DeploymentID(new ContainerID("sub2", "rwpc5Server", PeerConstants.MODULE_NAME, "publicKey2"),
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		
		req_011_Util.requestForRemoteClient(component, rwpc5ID, requestSpec6,
				Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES,
				new WorkerAllocation(workerDID).addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				new WorkerAllocation(workerBID).addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2),
				new WorkerAllocation(workerAID).addLoserConsumer(rwpcID).addLoserRequestSpec(requestSpec2));
		
		// Request eight workers for local2
		int request7ID = 7;
		String requirements7 = buildRequirements(">", 32, null, null);
		int requiredWorkers7 = 8;
		
		RequestSpecification requestSpec7 = new RequestSpecification(0, new JobSpecification("label"), request7ID, requirements7, requiredWorkers7, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc2ID, lwpc2),	requestSpec7,
				new WorkerAllocation(workerDID).addLoserConsumer(rwpc5ID), new WorkerAllocation(workerGID).addLoserConsumer(rwpc4ID),
				new WorkerAllocation(workerEID).addLoserConsumer(rwpc3ID), new WorkerAllocation(workerHID).addLoserConsumer(rwpc4ID),
				new WorkerAllocation(workerFID).addLoserConsumer(rwpc3ID));
		
		// Request ten workers for local1
		int request8ID = 8;
		String requirements8 = buildRequirements(null);
		int requiredWorkers8 = 10;
		
		RequestSpecification requestSpec8 = new RequestSpecification(0, new JobSpecification("label"), request8ID, requirements8, requiredWorkers8, 0, 0);
		
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1ID, lwpc1),	requestSpec8,
				new WorkerAllocation(workerAID).addLoserConsumer(rwpc5ID), new WorkerAllocation(workerBID).addLoserConsumer(rwpc5ID),
				new WorkerAllocation(workerEID).addLoserConsumer(lwpc2ID));
	}
	
}