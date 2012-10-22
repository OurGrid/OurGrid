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
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class AT_0009 extends PeerAcceptanceTestCase {
	
	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
	

	@After
	public void tearDown() throws Exception{
		super.tearDown();
		
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		if (trustFile.exists()) {
			trustFile.delete();
		}
	}
	
	/**
	 * Verifies a peer with donated local Workers. The donated workers must become first in result.
	 */
	@ReqTest(test="AT-0009", reqs="")
	@Test public void test_AT_0009_LocalWorkers_in_Remote_Allocation() throws Exception{
		
		copyTrustFile("011_blank.xml");
		
		//Start peer and set mock for logger
		component = req_010_Util.startPeer();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S1");
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);
		
		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDDeploymentID);
		
		//Change workers A and C status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		resetActiveMocks();

		//Request two workers for client1
		ServiceID serviceID = 
			new ServiceID("client1", "server", PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER);
		DeploymentID client1DeploymentID = new DeploymentID(serviceID);
		
		String client1PubKey = "client1PubKey";
		client1DeploymentID.setPublicKey(client1PubKey);
		
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		req_011_Util.requestForRemoteClient(component, client1DeploymentID, requestSpec, 1, 
				allocationC, allocationA);
		
		resetActiveMocks();

		//Change worker B status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);

		//Verify the workers' status
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, client1DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IDLE, null);
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, client1DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null);
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoC, workerInfoB, workerInfoD);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	/**
	 * Verifies a peer with donated local Workers. The donated workers must become first in result.
	 */
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0009_LocalWorkers_in_Remote_AllocationWithJDL() throws Exception{
		
		copyTrustFile("011_blank.xml");
		
		//Start peer and set mock for logger
		component = req_010_Util.startPeer();
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		WorkerSpecification workerSpecD = workerAcceptanceUtil.createClassAdWorkerSpec("U4", "S1", null, null);
		
		String workerAPubKey = "publicKeyWA";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);
		
		String workerBPubKey = "publicKeyWB";
		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);
		
		String workerCPubKey = "publicKeyWC";
		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);
		
		String workerDPubKey = "publicKeyWD";
		DeploymentID workerDDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecD, workerDPubKey);
		req_010_Util.workerLogin(component, workerSpecD, workerDDeploymentID);
		
		//Change workers A and C status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		resetActiveMocks();

		//Request two workers for client1
		ServiceID serviceID = 
			new ServiceID("client1", "server", PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER);
		DeploymentID client1DeploymentID = new DeploymentID(serviceID);
		
		String client1PubKey = "client1PubKey";
		client1DeploymentID.setPublicKey(client1PubKey);
		
		
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, 
				buildRequirements(null), 2, 0, 0);
		
		WorkerAllocation allocationC = new WorkerAllocation(workerCDeploymentID);
		WorkerAllocation allocationA = new WorkerAllocation(workerADeploymentID);
		req_011_Util.requestForRemoteClient(component, client1DeploymentID, requestSpec, 1, 
				allocationC, allocationA);
		
		resetActiveMocks();

		//Change worker B status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);

		//Verify the workers' status
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.DONATED, client1DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.IDLE, null);
		WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.DONATED, client1DeploymentID.getServiceID().toString());
		WorkerInfo workerInfoD = new WorkerInfo(workerSpecD, LocalWorkerState.OWNER, null);
		
		List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoC, workerInfoB, workerInfoD);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
}
