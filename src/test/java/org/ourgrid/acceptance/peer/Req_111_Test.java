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

import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.acceptance.util.peer.Req_117_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import condor.classad.ClassAdParser;
import condor.classad.RecordExpr;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ111")
public class Req_111_Test extends PeerAcceptanceTestCase {

	//WORKERS ATTRIBUTES
	
	//111.1
	public static final Integer WORKER1_MEM = 256;
	public static final String WORKER1_OS = "linux";

	public static final Integer WORKER2_MEM = 512;
	public static final String WORKER2_OS = "linux";

	public static final Integer WORKER3_MEM = 1024;
	public static final String WORKER3_OS = "windows";

	public static final Integer WORKER4_MEM = 256;
	public static final String WORKER4_OS = "linux";

	public static final Integer WORKER5_MEM = 1024;
	public static final String WORKER5_OS = "windows";

	public static final Integer WORKER6_MEM = 256;
	public static final String WORKER6_OS = "linux";

	public static final Integer WORKER7_MEM = 512;
	public static final String WORKER7_OS = "linux";

	public static final Integer WORKER8_MEM = 512;
	public static final String WORKER8_OS = "linux";

	public static final Integer WORKER9_MEM = 1024;
	public static final String WORKER9_OS = "windows";

	public static final Integer WORKER10_MEM = 256;
	public static final String WORKER10_OS = "linux";
	
	public static final Integer WORKER11_MEM = 1024;
	public static final String WORKER11_OS = "windows";

	public static final Integer WORKER12_MEM = 256;
	public static final String WORKER12_OS = "linux";

	public static final Integer WORKER13_MEM = 1024;
	public static final String WORKER13_OS = "windows";

	public static final Integer WORKER14_MEM = 256;
	public static final String WORKER14_OS = "linux";

	public static final Integer WORKER15_MEM = 512;
	public static final String WORKER15_OS = "linux";

	public static final Integer WORKER16_MEM = 1024;
	public static final String WORKER16_OS = "windows";
	
	//111.3
	public static final Integer WORKER4_3_MEM = 1024;
	public static final String WORKER4_3_OS = "windows";

	public static final Integer WORKER5_3_MEM = 256;
	public static final String WORKER5_3_OS = "linux";

	public static final Integer WORKER6_3_MEM = 512;
	public static final String WORKER6_3_OS = "linux";

	public static final Integer WORKER7_3_MEM = 1024;
	public static final String WORKER7_3_OS = "windows";
	
	//111.4
	public static final Integer WORKER1_4_MEM = 512;
	public static final String WORKER1_4_OS = "linux";
	
	public static final Integer WORKER2_4_MEM = 512;
	public static final String WORKER2_4_OS = "linux";
	

	private PeerComponent component; 
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
	private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
	private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
	private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
	private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
	private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
	private Req_117_Util req_117_Util = new Req_117_Util(getComponentContext());

	@Test 
	public void test_AT_111_1_simpleOperators() throws Exception {
		//Creating user's account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Start the peer
		component = req_010_Util.startPeer();

		//Login with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		WorkerSpecification workerSpec0 = workerAcceptanceUtil.createWorkerSpec("worker0", workerServer);
		DeploymentID worker0ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec0 , "worker0PublicKey");
		req_010_Util.workerLogin(component, workerSpec0, worker0ID);

		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer,
				WORKER1_MEM, WORKER1_OS);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("worker2", workerServer,
				WORKER2_MEM, WORKER2_OS);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);

		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createWorkerSpec("worker3", workerServer,
				WORKER3_MEM, WORKER3_OS);
		DeploymentID worker3ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec3 , "worker3PublicKey");
		req_010_Util.workerLogin(component, workerSpec3, worker3ID);

		//Changing workers status to idle
		req_025_Util.changeWorkerStatusToIdle(component, worker0ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker3ID);

		//Request workers with memory size lower than 512
		requestWorkers(component, 1, "mem < 512", 3, lwpcID, worker1ID);

		//Recovering worker4 to replace worker1
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createWorkerSpec("worker4", workerServer,
				WORKER4_MEM, WORKER4_OS);
		DeploymentID worker4ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec4 , "worker4PublicKey");
		req_010_Util.workerLogin(component, workerSpec4, worker4ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker4ID);

		//Requesting workers with memory size greater than 512
		requestWorkers(component,2, "mem > 512", 3, lwpcID, worker3ID);

		//Recovering worker5 to replace worker3
		WorkerSpecification workerSpec5 = workerAcceptanceUtil.createWorkerSpec("worker5", workerServer,
				WORKER5_MEM, WORKER5_OS);
		DeploymentID worker5ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec5 , "worker5PublicKey");
		req_010_Util.workerLogin(component, workerSpec5, worker5ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker5ID);


		//Requesting workers with memory size lower or equals 512
		requestWorkers(component, 3, "mem <= 512", 3, lwpcID, worker2ID, worker4ID);

		//Recovering worker6 and worker7 to replace worker2 and worker4
		WorkerSpecification workerSpec6 = workerAcceptanceUtil.createWorkerSpec("worker6", workerServer,
				WORKER6_MEM, WORKER6_OS);
		DeploymentID worker6ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec6 , "worker6PublicKey");
		req_010_Util.workerLogin(component, workerSpec6, worker6ID);

		WorkerSpecification workerSpec7 = workerAcceptanceUtil.createWorkerSpec("worker7", workerServer,
				WORKER7_MEM, WORKER7_OS);
		DeploymentID worker7ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec7 , "worker7PublicKey");
		req_010_Util.workerLogin(component, workerSpec7, worker7ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker6ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker7ID);

		//Requesting workers with memory size greater or equals 512
		requestWorkers(component, 4, "mem >= 512", 3, lwpcID, worker5ID, worker7ID);

		//Recovering worker8 and worker9 to replace worker5 and worker7
		WorkerSpecification workerSpec8 = workerAcceptanceUtil.createWorkerSpec("worker8", workerServer,
				WORKER8_MEM, WORKER8_OS);
		DeploymentID worker8ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec8 , "worker8PublicKey");
		req_010_Util.workerLogin(component, workerSpec8, worker8ID);

		WorkerSpecification workerSpec9 = workerAcceptanceUtil.createWorkerSpec("worker9", workerServer,
				WORKER9_MEM, WORKER9_OS);
		DeploymentID worker9ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec9 , "worker9PublicKey");
		req_010_Util.workerLogin(component, workerSpec9, worker9ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker8ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker9ID);

		//Requesting workers with memory size equals 256
		requestWorkers(component, 5, "mem = 256", 3, lwpcID, worker6ID);

		//Recovering worker10 to replace worker6
		WorkerSpecification workerSpec10 = workerAcceptanceUtil.createWorkerSpec("worker10", workerServer,
				WORKER10_MEM, WORKER10_OS);
		DeploymentID worker10ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec10 , "worker10PublicKey");
		req_010_Util.workerLogin(component, workerSpec10, worker10ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker10ID);

		//Requesting workers with memory size equals 1024
		requestWorkers(component, 6, "mem == 1024", 3, lwpcID, worker9ID);

		//Recovering worker11 to replace worker9
		WorkerSpecification workerSpec11 = workerAcceptanceUtil.createWorkerSpec("worker11", workerServer,
				WORKER11_MEM, WORKER11_OS);
		DeploymentID worker11ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec11 , "worker11PublicKey");
		req_010_Util.workerLogin(component, workerSpec11, worker11ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker11ID);

		//Requesting workers with memory size not equals 512
		requestWorkers(component, 7, "mem != 512", 3, lwpcID, worker10ID, worker11ID);

		//Recovering worker12 and worker13 to replace worker10 and worker11
		WorkerSpecification workerSpec12 = workerAcceptanceUtil.createWorkerSpec("worker12", workerServer,
				WORKER12_MEM, WORKER12_OS);
		DeploymentID worker12ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec12 , "worker12PublicKey");
		req_010_Util.workerLogin(component, workerSpec12, worker12ID);

		WorkerSpecification workerSpec13 = workerAcceptanceUtil.createWorkerSpec("worker13", workerServer,
				WORKER13_MEM, WORKER13_OS);
		DeploymentID worker13ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec13 , "worker13PublicKey");
		req_010_Util.workerLogin(component, workerSpec13, worker13ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker12ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker13ID);

		//Requesting workers with os equals linux
		requestWorkers(component, 8, "OS = linux", 3, lwpcID, worker8ID, worker12ID);

		//Recovering worker14 and worker15 to replace worker8 and worker12
		WorkerSpecification workerSpec14 = workerAcceptanceUtil.createWorkerSpec("worker14", workerServer,
				WORKER14_MEM, WORKER14_OS);
		DeploymentID worker14ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec14 , "worker14PublicKey");
		req_010_Util.workerLogin(component, workerSpec14, worker14ID);

		WorkerSpecification workerSpec15 = workerAcceptanceUtil.createWorkerSpec("worker15", workerServer,
				WORKER15_MEM, WORKER15_OS);
		DeploymentID worker15ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec15 , "worker15PublicKey");
		req_010_Util.workerLogin(component, workerSpec15, worker15ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker14ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker15ID);

		//Requesting workers with os equals windows
		requestWorkers(component, 9, "OS = windows", 3, lwpcID, worker13ID);
		WorkerSpecification workerSpec16 = workerAcceptanceUtil.createWorkerSpec("worker16", workerServer,
				WORKER16_MEM, WORKER16_OS);
		DeploymentID worker16ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec16 , "worker16PublicKey");
		req_010_Util.workerLogin(component, workerSpec16, worker16ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker16ID);

		//Requesting workers with os not equals linux
		requestWorkers(component, 10, "OS != linux", 3, lwpcID, worker16ID);
	}

	private void requestWorkers(PeerComponent component, int requestID, String requirements, int requiredWorkers, DeploymentID lwpcID, 
			DeploymentID... workerIDs) {

		WorkerAllocation[] allocations = new WorkerAllocation[workerIDs.length];
		int counter = 0;
		for (DeploymentID DeploymentID : workerIDs) {
			allocations[counter] = new WorkerAllocation(DeploymentID);
			counter++;
		}

		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);

		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), requestID, requirements, requiredWorkers, 0, 0);
		ScheduledFuture<?> future = req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcID, lwpc), requestSpec, allocations);

		req_117_Util.pauseRequest(component, lwpcID, requestID, future);
	}

	@Test public void test_AT_111_2_negationOperators() throws Exception {
		//Creating user's account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Start the peer
		component = req_010_Util.startPeer();

		//Login with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		
		WorkerSpecification workerSpec0 = workerAcceptanceUtil.createWorkerSpec("worker0", workerServer);
		DeploymentID worker0ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec0 , "worker0PublicKey");
		req_010_Util.workerLogin(component, workerSpec0, worker0ID);

		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer,
				WORKER1_MEM, WORKER1_OS);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("worker2", workerServer,
				WORKER2_MEM, WORKER2_OS);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);

		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createWorkerSpec("worker3", workerServer,
				WORKER3_MEM, WORKER3_OS);
		DeploymentID worker3ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec3 , "worker3PublicKey");
		req_010_Util.workerLogin(component, workerSpec3, worker3ID);

		//Change workers status to idle
		req_025_Util.changeWorkerStatusToIdle(component, worker0ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker3ID);

		//Request workers with memory size lower than 512
		requestWorkers(component, 1, "NOT (mem >= 512)", 3, lwpcID, worker1ID);

		//Recovering worker4 to replace worker1
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createWorkerSpec("worker4", workerServer,
				WORKER4_MEM, WORKER4_OS);
		DeploymentID worker4ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec4 , "worker4PublicKey");
		req_010_Util.workerLogin(component, workerSpec4, worker4ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker4ID);

		//Request workers with memory size greater than 512
		requestWorkers(component, 2, "!(mem <= 512)", 3, lwpcID, worker3ID);

		//Recovering worker5 to replace worker3
		WorkerSpecification workerSpec5 = workerAcceptanceUtil.createWorkerSpec("worker5", workerServer,
				WORKER5_MEM, WORKER5_OS);
		DeploymentID worker5ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec5 , "worker5PublicKey");
		req_010_Util.workerLogin(component, workerSpec5, worker5ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker5ID);

		//Request workers with memory size lower or equals 512
		requestWorkers(component, 3, "NOT(mem > 512)", 3, lwpcID, worker2ID, worker4ID);

		//Recovering worker6 and worker7 to replace worker2 and worker4
		WorkerSpecification workerSpec6 = workerAcceptanceUtil.createWorkerSpec("worker6", workerServer,
				WORKER6_MEM, WORKER6_OS);
		DeploymentID worker6ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec6 , "worker6PublicKey");
		req_010_Util.workerLogin(component, workerSpec6, worker6ID);
		
		WorkerSpecification workerSpec7 = workerAcceptanceUtil.createWorkerSpec("worker7", workerServer,
				WORKER7_MEM, WORKER7_OS);
		DeploymentID worker7ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec7 , "worker7PublicKey");
		req_010_Util.workerLogin(component, workerSpec7, worker7ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker6ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker7ID);

		//Request workers with memory size greater or equals 512
		requestWorkers(component, 4, "!(mem < 512)", 3, lwpcID, worker5ID, worker7ID);

		//Recovering worker8 and worker9 to replace worker5 and worker7
		WorkerSpecification workerSpec8 = workerAcceptanceUtil.createWorkerSpec("worker8", workerServer,
				WORKER8_MEM, WORKER8_OS);
		DeploymentID worker8ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec8 , "worker8PublicKey");
		req_010_Util.workerLogin(component, workerSpec8, worker8ID);
		
		WorkerSpecification workerSpec9 = workerAcceptanceUtil.createWorkerSpec("worker9", workerServer,
				WORKER9_MEM, WORKER9_OS);
		DeploymentID worker9ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec9 , "worker9PublicKey");
		req_010_Util.workerLogin(component, workerSpec9, worker9ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker8ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker9ID);

		//Request workers with memory size equals 256
		requestWorkers(component, 5, "NOT(mem != 256)", 3, lwpcID, worker6ID);

		//Recovering worker10 to replace worker6
		WorkerSpecification workerSpec10 = workerAcceptanceUtil.createWorkerSpec("worker10", workerServer,
				WORKER10_MEM, WORKER10_OS);
		DeploymentID worker10ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec10 , "worker10PublicKey");
		req_010_Util.workerLogin(component, workerSpec10, worker10ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker10ID);

		//Request workers with memory size equals 1024
		requestWorkers(component, 6, "!(mem != 1024)", 3, lwpcID, worker9ID);

		//Recovering worker11 to replace worker9
		WorkerSpecification workerSpec11 = workerAcceptanceUtil.createWorkerSpec("worker11", workerServer,
				WORKER11_MEM, WORKER11_OS);
		DeploymentID worker11ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec11 , "worker11PublicKey");
		req_010_Util.workerLogin(component, workerSpec11, worker11ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker11ID);

		//Request workers with memory size not equals 512
		requestWorkers(component, 7, "!(mem = 512)", 3, lwpcID, worker10ID, worker11ID);

		//Recovering worker12 and worker 13 to replace worker10 and worker11
		WorkerSpecification workerSpec12 = workerAcceptanceUtil.createWorkerSpec("worker12", workerServer,
				WORKER12_MEM, WORKER12_OS);
		DeploymentID worker12ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec12 , "worker12PublicKey");
		req_010_Util.workerLogin(component, workerSpec12, worker12ID);
		
		WorkerSpecification workerSpec13 = workerAcceptanceUtil.createWorkerSpec("worker13", workerServer,
				WORKER13_MEM, WORKER13_OS);
		DeploymentID worker13ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec13 , "worker13PublicKey");
		req_010_Util.workerLogin(component, workerSpec13, worker13ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker12ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker13ID);

		//Request workers with os equals linux
		requestWorkers(component, 8, "NOT(OS != linux)", 3, lwpcID, worker8ID, worker12ID);

		//Recovering worker14 and worker15 to replace worker8 and worker12
		WorkerSpecification workerSpec14 = workerAcceptanceUtil.createWorkerSpec("worker14", workerServer,
				WORKER14_MEM, WORKER14_OS);
		DeploymentID worker14ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec14 , "worker14PublicKey");
		req_010_Util.workerLogin(component, workerSpec14, worker14ID);
		
		WorkerSpecification workerSpec15 = workerAcceptanceUtil.createWorkerSpec("worker15", workerServer,
				WORKER15_MEM, WORKER15_OS);
		DeploymentID worker15ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec15 , "worker15PublicKey");
		req_010_Util.workerLogin(component, workerSpec15, worker15ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker14ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker15ID);

		//Request workers with os equals windows
		requestWorkers(component, 9, "!(OS != windows)", 3, lwpcID, worker13ID);

		//Recovering worker16 to replace worker13
		WorkerSpecification workerSpec16 = workerAcceptanceUtil.createWorkerSpec("worker16", workerServer,
				WORKER16_MEM, WORKER16_OS);
		DeploymentID worker16ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec16 , "worker16PublicKey");
		req_010_Util.workerLogin(component, workerSpec16, worker16ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker16ID);

		//Request workers with os not equals linux
		requestWorkers(component, 10, "NOT(OS = linux)", 3, lwpcID, worker16ID);
	}

	@Test public void test_AT_111_3_unionAndIntersectionOperators() throws Exception {
		//Creating user's account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Starting peer
		component = req_010_Util.startPeer();

		//Login with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";


		WorkerSpecification workerSpec0 = workerAcceptanceUtil.createWorkerSpec("worker0", workerServer);
		DeploymentID worker0ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec0 , "worker0PublicKey");
		req_010_Util.workerLogin(component, workerSpec0, worker0ID);

		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer,
				WORKER1_MEM, WORKER1_OS);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("worker2", workerServer,
				WORKER2_MEM, WORKER2_OS);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);

		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createWorkerSpec("worker3", workerServer,
				WORKER3_MEM, WORKER3_OS);
		DeploymentID worker3ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec3 , "worker3PublicKey");
		req_010_Util.workerLogin(component, workerSpec3, worker3ID);

		//Changing workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker0ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker3ID);


		//Requesting workers with memory size greater than 256 AND OS equals Windows
		requestWorkers(component, 1, "mem > 256 && OS = windows", 3, lwpcID, worker3ID);

		//Recovering worker4 to replace worker3
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createWorkerSpec("worker4", workerServer,
				WORKER4_3_MEM, WORKER4_3_OS);
		DeploymentID worker4ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec4 , "worker4PublicKey");
		req_010_Util.workerLogin(component, workerSpec4, worker4ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker4ID);

		//Requesting workers with memory size lower than 512 AND OS equals Linux
		requestWorkers(component, 2, "mem < 512 AND OS == linux", 3, lwpcID, worker1ID);

		//Recovering worker5 to replace worker1
		WorkerSpecification workerSpec5 = workerAcceptanceUtil.createWorkerSpec("worker5", workerServer,
				WORKER5_3_MEM, WORKER5_3_OS);
		DeploymentID worker5ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec5 , "worker5PublicKey");
		req_010_Util.workerLogin(component, workerSpec5, worker5ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker5ID);

		//Requesting workers with memory size greater or equals 512 OR OS equals Windows
		requestWorkers(component, 3, "mem >= 512 || OS == windows", 3, lwpcID, worker2ID, worker4ID);

		//Recovering worker6 and worker7 to replace worker2 and worker4
		WorkerSpecification workerSpec6 = workerAcceptanceUtil.createWorkerSpec("worker6", workerServer,
				WORKER6_3_MEM, WORKER6_3_OS);
		DeploymentID worker6ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec6 , "worker6PublicKey");
		req_010_Util.workerLogin(component, workerSpec6, worker6ID);
		
		WorkerSpecification workerSpec7 = workerAcceptanceUtil.createWorkerSpec("worker7", workerServer,
				WORKER7_3_MEM, WORKER7_3_OS);
		DeploymentID worker7ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec7 , "worker7PublicKey");
		req_010_Util.workerLogin(component, workerSpec7, worker7ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker6ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker7ID);

		//Requesting workers with memory size lower than 512 OR OS equals Linux
		requestWorkers(component, 4, "mem < 512 OR OS = linux", 3, lwpcID, worker5ID, worker6ID);
	}

	@Test public void test_AT_111_4_operatorsPrecedence() throws Exception {
		//Creating an user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Starting peer
		component = req_010_Util.startPeer();

		//Loging with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		//Notify worker1 recovery

		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer,
				WORKER1_4_MEM, WORKER1_4_OS);
		workerSpec1.putAttribute("environment", "brams");
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		//Changing worker1 status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Requesting a worker with a true complex expression - expect AND has greater precedence than OR
		requestWorkers(component, 1, "OS = windows && mem >= 512 || environment = brams", 1, lwpcID, worker1ID);

		//Recovering worker2 to replace worker1
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("worker2", workerServer,
				WORKER2_4_MEM, WORKER2_4_OS);
		workerSpec2.putAttribute("environment", "brams");
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);

		//Requesting a worker with other true complex expression - expect AND has greater precedence than OR
		requestWorkers(component, 2, "environment = brams OR mem >= 512 AND OS = windows", 1, lwpcID, worker2ID);
	}

	@Test public void test_AT_111_5_parenthesesPrecedence() throws Exception{
		//Creating a user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Starting the peer
		component = req_010_Util.startPeer();

		//Loging in with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		//Notifying worker1 recovery
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer,
				WORKER1_MEM, WORKER1_OS);
		workerSpec1.putAttribute("environment", "brams");
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		//Changing worker1 status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Request a worker with a false complex expression - expect the parenthesis give precedence to OR
		requestWorkers(component, 1, "OS = windows && (mem >= 512 || environment = brams)", 1, lwpcID);

		//Requesting a worker with a true complex expression - expect the parenthesis give precedence to AND
		requestWorkers(component, 2, "(OS = windows && mem >= 512) || environment = brams", 1, lwpcID, worker1ID);

		//Recovering worker2 to replace worker1
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("worker2", workerServer,
				WORKER2_4_MEM, WORKER2_4_OS);
		workerSpec2.putAttribute("environment", "brams");
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);

		//Requesting a worker with other false complex expression - expect the parenthesis give precedence to OR
		requestWorkers(component, 3, "(environment = brams OR mem >= 512) AND OS = windows", 1, lwpcID);

		//Requesting a worker with other true complex expression - expect the parenthesis give precedence to AND		
		requestWorkers(component, 4, "environment = brams OR (mem >= 512 AND OS = windows)", 1, lwpcID, worker2ID);
	}

	@Test public void test_AT_111_6_typingErrors() throws Exception {
		//Creating user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Starting the peer
		component = req_010_Util.startPeer();

		//Loging in with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		//Notifying worker1 recovery
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer,
				WORKER1_4_MEM, WORKER1_4_OS);
		workerSpec1.putAttribute("environment", "brams");
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		//Changing worker1 status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Requesting a worker with a wrongly typed expression - expect the expression be evaluated to false
		requestWorkers(component, 1, "environment >= 512", 1, lwpcID);

		//Requesting a worker with a true wrongly typed expression - expect the expression be evaluated to true
		requestWorkers(component, 2, "mem >= 512 OR environment > 1", 1, lwpcID, worker1ID);
	}

	@Test public void test_AT_111_7_caseSensitive() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Start peer  
		component = req_010_Util.startPeer();

		//Login with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		String workerServer = "xmpp.ourgrid.org";

		//Notify worker1 recovery
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createWorkerSpec("worker1", workerServer,
				WORKER1_4_MEM, WORKER1_4_OS);
		workerSpec1.putAttribute("environment", "brams");
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		//Change worker1 status to idle
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Request a worker - Test attributes case
		requestWorkers(component, 1, "os = linux && MEM >= 512", 1, lwpcID, worker1ID);

		//Recovering worker2 to replace worker1
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createWorkerSpec("worker2", workerServer,
				WORKER2_4_MEM, WORKER2_4_OS);
		workerSpec1.putAttribute("environment", "brams");
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);

		//Request a worker - Test values case
		requestWorkers(component, 2, "os = LInux", 1, lwpcID, worker2ID);
	}

	@Category(JDLCompliantTest.class) 
	@Test 
	public void test_AT_111_1_simpleOperatorsWithJDL() throws Exception {
		//Creating user's account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Start the peer
		component = req_010_Util.startPeer();

		//Login with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		WorkerSpecification workerSpec0 = workerAcceptanceUtil.createClassAdWorkerSpec("worker0", workerServer);
		DeploymentID worker0ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec0 , "worker0PublicKey");
		req_010_Util.workerLogin(component, workerSpec0, worker0ID);

		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("worker1", workerServer,
				WORKER1_MEM, WORKER1_OS);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("worker2", workerServer,
				WORKER2_MEM, WORKER2_OS);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);

		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createClassAdWorkerSpec("worker3", workerServer,
				WORKER3_MEM, WORKER3_OS);
		DeploymentID worker3ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec3 , "worker3PublicKey");
		req_010_Util.workerLogin(component, workerSpec3, worker3ID);

		//Changing workers status to idle
		req_025_Util.changeWorkerStatusToIdle(component, worker0ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker3ID);

		//Request workers with memory size lower than 512
		requestWorkers(component, 1, PeerAcceptanceTestCase.buildRequirements("<", 512, null, null), 3, lwpcID, worker1ID);

		//Recovering worker4 to replace worker1
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createClassAdWorkerSpec("worker4", workerServer,
				WORKER4_MEM, WORKER4_OS);
		DeploymentID worker4ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec4 , "worker4PublicKey");
		req_010_Util.workerLogin(component, workerSpec4, worker4ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker4ID);

		//Requesting workers with memory size greater than 512
		requestWorkers(component,2, PeerAcceptanceTestCase.buildRequirements(">", 512, null, null), 3, lwpcID, worker3ID);

		//Recovering worker5 to replace worker3
		WorkerSpecification workerSpec5 = workerAcceptanceUtil.createClassAdWorkerSpec("worker5", workerServer,
				WORKER5_MEM, WORKER5_OS);
		DeploymentID worker5ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec5 , "worker5PublicKey");
		req_010_Util.workerLogin(component, workerSpec5, worker5ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker5ID);


		//Requesting workers with memory size lower or equals 512
		requestWorkers(component, 3, PeerAcceptanceTestCase.buildRequirements("<=", 512, null, null), 3, lwpcID, worker2ID, worker4ID);

		//Recovering worker6 and worker7 to replace worker2 and worker4
		WorkerSpecification workerSpec6 = workerAcceptanceUtil.createClassAdWorkerSpec("worker6", workerServer,
				WORKER6_MEM, WORKER6_OS);
		DeploymentID worker6ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec6 , "worker6PublicKey");
		req_010_Util.workerLogin(component, workerSpec6, worker6ID);

		WorkerSpecification workerSpec7 = workerAcceptanceUtil.createClassAdWorkerSpec("worker7", workerServer,
				WORKER7_MEM, WORKER7_OS);
		DeploymentID worker7ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec7 , "worker7PublicKey");
		req_010_Util.workerLogin(component, workerSpec7, worker7ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker6ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker7ID);

		//Requesting workers with memory size greater or equals 512
		requestWorkers(component, 4, buildRequirements( ">=",  512, null, null), 3, lwpcID, worker5ID, worker7ID);

		//Recovering worker8 and worker9 to replace worker5 and worker7
		WorkerSpecification workerSpec8 = workerAcceptanceUtil.createClassAdWorkerSpec("worker8", workerServer,
				WORKER8_MEM, WORKER8_OS);
		DeploymentID worker8ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec8 , "worker8PublicKey");
		req_010_Util.workerLogin(component, workerSpec8, worker8ID);

		WorkerSpecification workerSpec9 = workerAcceptanceUtil.createClassAdWorkerSpec("worker9", workerServer,
				WORKER9_MEM, WORKER9_OS);
		DeploymentID worker9ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec9 , "worker9PublicKey");
		req_010_Util.workerLogin(component, workerSpec9, worker9ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker8ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker9ID);

		//Requesting workers with memory size equals 256
		requestWorkers(component, 5, buildRequirements( "==",  256, null, null), 3, lwpcID, worker6ID);

		//Recovering worker10 to replace worker6
		WorkerSpecification workerSpec10 = workerAcceptanceUtil.createClassAdWorkerSpec("worker10", workerServer,
				WORKER10_MEM, WORKER10_OS);
		DeploymentID worker10ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec10 , "worker10PublicKey");
		req_010_Util.workerLogin(component, workerSpec10, worker10ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker10ID);

		//Requesting workers with memory size equals 1024
		requestWorkers(component, 6, buildRequirements( "==",  1024, null, null), 3, lwpcID, worker9ID);

		//Recovering worker11 to replace worker9
		WorkerSpecification workerSpec11 = workerAcceptanceUtil.createClassAdWorkerSpec("worker11", workerServer,
				WORKER11_MEM, WORKER11_OS);
		DeploymentID worker11ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec11 , "worker11PublicKey");
		req_010_Util.workerLogin(component, workerSpec11, worker11ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker11ID);

		//Requesting workers with memory size not equals 512
		requestWorkers(component, 7, buildRequirements( "!=",  512, null, null), 3, lwpcID, worker10ID, worker11ID);

		//Recovering worker12 and worker13 to replace worker10 and worker11
		WorkerSpecification workerSpec12 = workerAcceptanceUtil.createClassAdWorkerSpec("worker12", workerServer,
				WORKER12_MEM, WORKER12_OS);
		DeploymentID worker12ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec12 , "worker12PublicKey");
		req_010_Util.workerLogin(component, workerSpec12, worker12ID);

		WorkerSpecification workerSpec13 = workerAcceptanceUtil.createClassAdWorkerSpec("worker13", workerServer,
				WORKER13_MEM, WORKER13_OS);
		DeploymentID worker13ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec13 , "worker13PublicKey");
		req_010_Util.workerLogin(component, workerSpec13, worker13ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker12ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker13ID);

		//Requesting workers with os equals linux
		requestWorkers(component, 8, buildRequirements( null,  null, "==", "linux"), 3, lwpcID, worker8ID, worker12ID);

		//Recovering worker14 and worker15 to replace worker8 and worker12
		WorkerSpecification workerSpec14 = workerAcceptanceUtil.createClassAdWorkerSpec("worker14", workerServer,
				WORKER14_MEM, WORKER14_OS);
		DeploymentID worker14ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec14 , "worker14PublicKey");
		req_010_Util.workerLogin(component, workerSpec14, worker14ID);

		WorkerSpecification workerSpec15 = workerAcceptanceUtil.createClassAdWorkerSpec("worker15", workerServer,
				WORKER15_MEM, WORKER15_OS);
		DeploymentID worker15ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec15 , "worker15PublicKey");
		req_010_Util.workerLogin(component, workerSpec15, worker15ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker14ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker15ID);

		//Requesting workers with os equals windows
		requestWorkers(component, 9, buildRequirements( null,  null, "==", "windows"), 3, lwpcID, worker13ID);
		WorkerSpecification workerSpec16 = workerAcceptanceUtil.createClassAdWorkerSpec("worker16", workerServer,
				WORKER16_MEM, WORKER16_OS);
		DeploymentID worker16ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec16 , "worker16PublicKey");
		req_010_Util.workerLogin(component, workerSpec16, worker16ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker16ID);

		//Requesting workers with os not equals linux
		requestWorkers(component, 10, buildRequirements( null,  null, "!=", "linux"), 3, lwpcID, worker16ID);
	}

	@Category(JDLCompliantTest.class) @Test public void test_AT_111_2_negationOperatorsWithJDL() throws Exception {
		//Creating user's account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Start the peer
		component = req_010_Util.startPeer();

		//Login with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		
		WorkerSpecification workerSpec0 = workerAcceptanceUtil.createClassAdWorkerSpec("worker0", workerServer);
		DeploymentID worker0ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec0 , "worker0PublicKey");
		req_010_Util.workerLogin(component, workerSpec0, worker0ID);

		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("worker1", workerServer,
				WORKER1_MEM, WORKER1_OS);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("worker2", workerServer,
				WORKER2_MEM, WORKER2_OS);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);

		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createClassAdWorkerSpec("worker3", workerServer,
				WORKER3_MEM, WORKER3_OS);
		DeploymentID worker3ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec3 , "worker3PublicKey");
		req_010_Util.workerLogin(component, workerSpec3, worker3ID);

		//Change workers status to idle
		req_025_Util.changeWorkerStatusToIdle(component, worker0ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker3ID);

		//Request workers with memory size lower than 512
		requestWorkers(component, 1, buildRequirements( "<", 512, null, null ), 3, lwpcID, worker1ID);

		//Recovering worker4 to replace worker1
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createClassAdWorkerSpec("worker4", workerServer,
				WORKER4_MEM, WORKER4_OS);
		DeploymentID worker4ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec4 , "worker4PublicKey");
		req_010_Util.workerLogin(component, workerSpec4, worker4ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker4ID);

		//Request workers with memory size greater than 512
		requestWorkers(component, 2, buildRequirements( ">", 512, null, null ), 3, lwpcID, worker3ID);

		//Recovering worker5 to replace worker3
		WorkerSpecification workerSpec5 = workerAcceptanceUtil.createClassAdWorkerSpec("worker5", workerServer,
				WORKER5_MEM, WORKER5_OS);
		DeploymentID worker5ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec5 , "worker5PublicKey");
		req_010_Util.workerLogin(component, workerSpec5, worker5ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker5ID);

		//Request workers with memory size lower or equals 512
		requestWorkers(component, 3, buildRequirements( "<=", 512, null, null ), 3, lwpcID, worker2ID, worker4ID);

		//Recovering worker6 and worker7 to replace worker2 and worker4
		WorkerSpecification workerSpec6 = workerAcceptanceUtil.createClassAdWorkerSpec("worker6", workerServer,
				WORKER6_MEM, WORKER6_OS);
		DeploymentID worker6ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec6 , "worker6PublicKey");
		req_010_Util.workerLogin(component, workerSpec6, worker6ID);
		
		WorkerSpecification workerSpec7 = workerAcceptanceUtil.createClassAdWorkerSpec("worker7", workerServer,
				WORKER7_MEM, WORKER7_OS);
		DeploymentID worker7ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec7 , "worker7PublicKey");
		req_010_Util.workerLogin(component, workerSpec7, worker7ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker6ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker7ID);

		//Request workers with memory size greater or equals 512
		requestWorkers(component, 4, buildRequirements( ">=", 512, null, null ), 3, lwpcID, worker5ID, worker7ID);

		//Recovering worker8 and worker9 to replace worker5 and worker7
		WorkerSpecification workerSpec8 = workerAcceptanceUtil.createClassAdWorkerSpec("worker8", workerServer,
				WORKER8_MEM, WORKER8_OS);
		DeploymentID worker8ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec8 , "worker8PublicKey");
		req_010_Util.workerLogin(component, workerSpec8, worker8ID);
		
		WorkerSpecification workerSpec9 = workerAcceptanceUtil.createClassAdWorkerSpec("worker9", workerServer,
				WORKER9_MEM, WORKER9_OS);
		DeploymentID worker9ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec9 , "worker9PublicKey");
		req_010_Util.workerLogin(component, workerSpec9, worker9ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker8ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker9ID);

		//Request workers with memory size equals 256
		requestWorkers(component, 5, buildRequirements( "==", 256, null, null ), 3, lwpcID, worker6ID);

		//Recovering worker10 to replace worker6
		WorkerSpecification workerSpec10 = workerAcceptanceUtil.createClassAdWorkerSpec("worker10", workerServer,
				WORKER10_MEM, WORKER10_OS);
		DeploymentID worker10ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec10 , "worker10PublicKey");
		req_010_Util.workerLogin(component, workerSpec10, worker10ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker10ID);

		//Request workers with memory size equals 1024
		requestWorkers(component, 6, buildRequirements( "==", 1024, null, null ), 3, lwpcID, worker9ID);

		//Recovering worker11 to replace worker9
		WorkerSpecification workerSpec11 = workerAcceptanceUtil.createClassAdWorkerSpec("worker11", workerServer,
				WORKER11_MEM, WORKER11_OS);
		DeploymentID worker11ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec11 , "worker11PublicKey");
		req_010_Util.workerLogin(component, workerSpec11, worker11ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker11ID);

		//Request workers with memory size not equals 512
		requestWorkers(component, 7, buildRequirements( "!=", 512, null, null ), 3, lwpcID, worker10ID, worker11ID);

		//Recovering worker12 and worker 13 to replace worker10 and worker11
		WorkerSpecification workerSpec12 = workerAcceptanceUtil.createClassAdWorkerSpec("worker12", workerServer,
				WORKER12_MEM, WORKER12_OS);
		DeploymentID worker12ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec12 , "worker12PublicKey");
		req_010_Util.workerLogin(component, workerSpec12, worker12ID);
		
		WorkerSpecification workerSpec13 = workerAcceptanceUtil.createClassAdWorkerSpec("worker13", workerServer,
				WORKER13_MEM, WORKER13_OS);
		DeploymentID worker13ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec13 , "worker13PublicKey");
		req_010_Util.workerLogin(component, workerSpec13, worker13ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker12ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker13ID);

		//Request workers with os equals linux
		requestWorkers(component, 8, buildRequirements( null, null, "==", "linux"), 3, lwpcID, worker8ID, worker12ID);

		//Recovering worker14 and worker15 to replace worker8 and worker12
		WorkerSpecification workerSpec14 = workerAcceptanceUtil.createClassAdWorkerSpec("worker14", workerServer,
				WORKER14_MEM, WORKER14_OS);
		DeploymentID worker14ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec14 , "worker14PublicKey");
		req_010_Util.workerLogin(component, workerSpec14, worker14ID);
		
		WorkerSpecification workerSpec15 = workerAcceptanceUtil.createClassAdWorkerSpec("worker15", workerServer,
				WORKER15_MEM, WORKER15_OS);
		DeploymentID worker15ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec15 , "worker15PublicKey");
		req_010_Util.workerLogin(component, workerSpec15, worker15ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker14ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker15ID);

		//Request workers with os equals windows
		requestWorkers(component, 9, buildRequirements( null, null, "==", "windows"), 3, lwpcID, worker13ID);

		//Recovering worker16 to replace worker13
		WorkerSpecification workerSpec16 = workerAcceptanceUtil.createClassAdWorkerSpec("worker16", workerServer,
				WORKER16_MEM, WORKER16_OS);
		DeploymentID worker16ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec16 , "worker16PublicKey");
		req_010_Util.workerLogin(component, workerSpec16, worker16ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker16ID);

		//Request workers with os not equals linux
		requestWorkers(component, 10, buildRequirements( null, null, "!=", "linux"), 3, lwpcID, worker16ID);
	}

	@Category(JDLCompliantTest.class) @Test public void test_AT_111_3_unionAndIntersectionOperatorsWithJDL() throws Exception {
		//Creating user's account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Starting peer
		component = req_010_Util.startPeer();

		//Login with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";


		WorkerSpecification workerSpec0 = workerAcceptanceUtil.createClassAdWorkerSpec("worker0", workerServer);
		DeploymentID worker0ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec0 , "worker0PublicKey");
		req_010_Util.workerLogin(component, workerSpec0, worker0ID);

		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("worker1", workerServer,
				WORKER1_MEM, WORKER1_OS);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("worker2", workerServer,
				WORKER2_MEM, WORKER2_OS);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);

		WorkerSpecification workerSpec3 = workerAcceptanceUtil.createClassAdWorkerSpec("worker3", workerServer,
				WORKER3_MEM, WORKER3_OS);
		DeploymentID worker3ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec3 , "worker3PublicKey");
		req_010_Util.workerLogin(component, workerSpec3, worker3ID);

		//Changing workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker0ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker3ID);


		//Requesting workers with memory size greater than 256 AND OS equals Windows
		requestWorkers(component, 1, buildRequirements( ">", 256, "==", "windows"), 3, lwpcID, worker3ID);

		//Recovering worker4 to replace worker3
		WorkerSpecification workerSpec4 = workerAcceptanceUtil.createClassAdWorkerSpec("worker4", workerServer,
				WORKER4_3_MEM, WORKER4_3_OS);
		DeploymentID worker4ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec4 , "worker4PublicKey");
		req_010_Util.workerLogin(component, workerSpec4, worker4ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker4ID);

		//Requesting workers with memory size lower than 512 AND OS equals Linux
		requestWorkers(component, 2, buildRequirements( "<", 512, "==", "linux"), 3, lwpcID, worker1ID);

		//Recovering worker5 to replace worker1
		WorkerSpecification workerSpec5 = workerAcceptanceUtil.createClassAdWorkerSpec("worker5", workerServer,
				WORKER5_3_MEM, WORKER5_3_OS);
		DeploymentID worker5ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec5 , "worker5PublicKey");
		req_010_Util.workerLogin(component, workerSpec5, worker5ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker5ID);

		//Requesting workers with memory size greater or equals 512 OR OS equals Windows
		requestWorkers(component, 3, buildRequirements("other.mainMemory >= 512 || other.OS == \"windows\""), 3, lwpcID, worker2ID, worker4ID);

		//Recovering worker6 and worker7 to replace worker2 and worker4
		WorkerSpecification workerSpec6 = workerAcceptanceUtil.createClassAdWorkerSpec("worker6", workerServer,
				WORKER6_3_MEM, WORKER6_3_OS);
		DeploymentID worker6ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec6 , "worker6PublicKey");
		req_010_Util.workerLogin(component, workerSpec6, worker6ID);
		
		WorkerSpecification workerSpec7 = workerAcceptanceUtil.createClassAdWorkerSpec("worker7", workerServer,
				WORKER7_3_MEM, WORKER7_3_OS);
		DeploymentID worker7ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec7 , "worker7PublicKey");
		req_010_Util.workerLogin(component, workerSpec7, worker7ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker6ID);
		req_025_Util.changeWorkerStatusToIdle(component, worker7ID);

		//Requesting workers with memory size lower than 512 OR OS equals Linux
		requestWorkers(component, 4, buildRequirements("other.MainMemory < 512 || other.OS == \"linux\""), 3, lwpcID, worker5ID, worker6ID);
	}

	@Category(JDLCompliantTest.class) @Test public void test_AT_111_4_operatorsPrecedenceWithJDL() throws Exception {
		//Creating an user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Starting peer
		component = req_010_Util.startPeer();

		//Loging with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		//Notify worker1 recovery

		WorkerSpecification workerSpec1 = createClassAdWorkerSpec("worker1", workerServer,
				WORKER1_4_MEM, WORKER1_4_OS);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		//Changing worker1 status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Requesting a worker with a true complex expression - expect AND has greater precedence than OR
		requestWorkers(component, 1, buildRequirements( "other.OS == \"windows\" && other.MainMemory >= 512 || member(\"brams\", other.software.name)"), 1, lwpcID, worker1ID);

		//Recovering worker2 to replace worker1
		WorkerSpecification workerSpec2 = createClassAdWorkerSpec("worker2", workerServer,
				WORKER2_4_MEM, WORKER2_4_OS);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);

		//Requesting a worker with other true complex expression - expect AND has greater precedence than OR
		requestWorkers(component, 2, buildRequirements( "member(\"brams\", other.software.name) || other.MainMemory >= 512 && other.OS == \"windows\""), 1, lwpcID, worker2ID);
	}
	
	private WorkerSpecification createClassAdWorkerSpec(String userName, String serverName, Integer memory, String os) {
		String attributes = "";
		if(memory != null){
			attributes += "MainMemory = " + memory + ";";
		}
		if(os != null){
			attributes += "OS = \"" + os + "\";";
		}
		
		attributes += "software={brams};" +
					  "brams=[name = \"brams\";];";
		
		RecordExpr expr = (RecordExpr) new ClassAdParser("[" +
				"username=\"" + userName + "\";" +
				"servername=\"" + serverName + "\";" +
				attributes + 
				"Requirements=TRUE;" +
				"Rank=0;" +
				"]").parse();
		return new WorkerSpecification( expr );
	}

	@Category(JDLCompliantTest.class) @Test public void test_AT_111_5_parenthesesPrecedenceWithJDL() throws Exception{
		//Creating a user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Starting the peer
		component = req_010_Util.startPeer();

		//Loging in with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		//Notifying worker1 recovery
		WorkerSpecification workerSpec1 = createClassAdWorkerSpec("worker1", workerServer,
				WORKER1_4_MEM, WORKER1_4_OS);
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		//Changing worker1 status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Request a worker with a false complex expression - expect the parenthesis give precedence to OR
		requestWorkers(component, 1, buildRequirements( "other.OS == \"windows\" && (other.MainMemory >= 512 || member(\"brams\", other.software.name))"), 1, lwpcID);

		//Requesting a worker with a true complex expression - expect the parenthesis give precedence to AND
		requestWorkers(component, 2, buildRequirements( "(other.OS == \"windows\" && other.MainMemory >= 512) || member(\"brams\", other.software.name)"), 1, lwpcID, worker1ID);

		//Recovering worker2 to replace worker1
		WorkerSpecification workerSpec2 = createClassAdWorkerSpec("worker2", workerServer,
				WORKER2_4_MEM, WORKER2_4_OS);
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);
		
		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);

		//Requesting a worker with other false complex expression - expect the parenthesis give precedence to OR
		requestWorkers(component, 3, buildRequirements("(member(\"brams\", other.software.name) || other.MainMemory >= 512) && other.OS == \"windows\""), 1, lwpcID);

		//Requesting a worker with other true complex expression - expect the parenthesis give precedence to AND		
		requestWorkers(component, 4, buildRequirements("member(\"brams\", other.software.name) || (other.MainMemory >= 512 && other.OS == \"windows\")"), 1, lwpcID, worker2ID);
	}

	@Category(JDLCompliantTest.class) @Test public void test_AT_111_6_typingErrorsWithJDL() throws Exception {
		//Creating user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Starting the peer
		component = req_010_Util.startPeer();

		//Loging in with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		//Worker server
		String workerServer = "xmpp.ourgrid.org";

		//Notifying worker1 recovery
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("worker1", workerServer,
				WORKER1_4_MEM, WORKER1_4_OS);
		workerSpec1.putAttribute("environment", "brams");
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		//Changing worker1 status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Requesting a worker with a wrongly typed expression - expect the expression be evaluated to false
		requestWorkers(component, 1, buildRequirements( "other.software.name >= 512" ), 1, lwpcID);

		//Requesting a worker with a true wrongly typed expression - expect the expression be evaluated to true
		requestWorkers(component, 2, buildRequirements( "other.MainMemory >= 512 || other.software.name > 1" ), 1, lwpcID, worker1ID);
	}

	@Category(JDLCompliantTest.class) @Test public void test_AT_111_7_caseSensitiveWithJDL() throws Exception{
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user111", "server111", "111111");

		//Start peer  
		component = req_010_Util.startPeer();

		//Login with a valid user
		String brokerPublicKey = "publicKeyA";

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);

		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);

		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

		DeploymentID lwpcID = req_108_Util.login(component, user, brokerPublicKey);

		String workerServer = "xmpp.ourgrid.org";

		//Notify worker1 recovery
		WorkerSpecification workerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("worker1", workerServer,
				WORKER1_4_MEM, WORKER1_4_OS);
		workerSpec1.putAttribute("environment", "brams");
		DeploymentID worker1ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec1 , "worker1PublicKey");
		req_010_Util.workerLogin(component, workerSpec1, worker1ID);

		//Change worker1 status to idle
		req_025_Util.changeWorkerStatusToIdle(component, worker1ID);

		//Request a worker - Test attributes case
		requestWorkers(component, 1, buildRequirements( ">=", 512, "==", "linux" ), 1, lwpcID, worker1ID);

		//Recovering worker2 to replace worker1
		WorkerSpecification workerSpec2 = workerAcceptanceUtil.createClassAdWorkerSpec("worker2", workerServer,
				WORKER2_4_MEM, WORKER2_4_OS);
		workerSpec1.putAttribute("environment", "brams");
		DeploymentID worker2ID = req_019_Util.createAndPublishWorkerManagement(component, 
				workerSpec2 , "worker2PublicKey");
		req_010_Util.workerLogin(component, workerSpec2, worker2ID);

		req_025_Util.changeWorkerStatusToIdle(component, worker2ID);

		//Request a worker - Test values case
		requestWorkers(component, 2, buildRequirements( null, null, "==", "LInux" ), 1, lwpcID, worker2ID);
	}
}