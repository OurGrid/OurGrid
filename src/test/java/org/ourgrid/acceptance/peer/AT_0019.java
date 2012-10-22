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
import java.util.LinkedList;

import junit.framework.Assert;

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
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_034_Util;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class AT_0019 extends PeerAcceptanceTestCase {

	private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_015_Util req_015_Util = new Req_015_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_034_Util req_034_Util = new Req_034_Util(getComponentContext());

    public static final String COMM_FILE_PATH = "it_0019"+File.separator;
	
    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
	/**
	* Verifies if there is no remote consumer registered on a Peer 
	* after the remote consumer disposes all the workers he was using
    */
	@ReqTest(test="AT-0019", reqs="")
	@Test public void test_AT_0019_checkStatusOfConsumerThatDisposedAllWorkers() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"0019_blank.xml");
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S1");
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		//Request a worker for the remote consumer 1
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("user", "server", "consumerPublicKey1");
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, "", 2, 0, 0);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0, 
						new WorkerAllocation(workerCDeploymentID), new WorkerAllocation(workerBDeploymentID));
		
		//Change workers status to ALLOCATED FOR PEER
		RemoteWorkerManagement rwmC = 
			req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerCDeploymentID, workerSpecC, consumer1ID);
		RemoteWorkerManagement rwmB = 
			req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDeploymentID, workerSpecB, consumer1ID);
		
		//The remote peer disposes the workers
		req_015_Util.remoteDisposeLocalWorker(component, consumer1ID, rwmC, workerCDeploymentID);
		req_015_Util.remoteDisposeLocalWorker(component, consumer1ID, rwmB, workerBDeploymentID);
		
		//Verify if the peer has no consumers
		req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());
		Assert.assertFalse(req_015_Util.isPeerInterestedOnRemoteWorker(consumer1ID.getServiceID()));
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0019_checkStatusOfConsumerThatDisposedAllWorkersWithJDL() throws Exception {
		PeerAcceptanceUtil.copyTrustFile(COMM_FILE_PATH+"0019_blank.xml");
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S1", null, null);
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBPublicKey";
		String workerCPublicKey = "workerCPublicKey";
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);

		DeploymentID workerCDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, workerCPublicKey);
		req_010_Util.workerLogin(component, workerSpecC, workerCDeploymentID);

		//Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCDeploymentID);
		
		//Request a worker for the remote consumer 1
		DeploymentID consumer1ID = PeerAcceptanceUtil.createRemoteConsumerID("user", "server", "consumerPublicKey1");
		RequestSpecification requestSpec = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(null), 2, 0, 0);
		RemoteWorkerProviderClient rwpc = 
			req_011_Util.requestForRemoteClient(component, consumer1ID, requestSpec, 0, 
						new WorkerAllocation(workerCDeploymentID), new WorkerAllocation(workerBDeploymentID));
		
		//Change workers status to ALLOCATED FOR PEER
		RemoteWorkerManagement rwmC = 
			req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerCDeploymentID, workerSpecC, consumer1ID);
		RemoteWorkerManagement rwmB = 
			req_025_Util.changeWorkerStatusToAllocatedForPeer(component, rwpc, workerBDeploymentID, workerSpecB, consumer1ID);
		
		//The remote peer disposes the workers
		req_015_Util.remoteDisposeLocalWorker(component, consumer1ID, rwmC, workerCDeploymentID);
		req_015_Util.remoteDisposeLocalWorker(component, consumer1ID, rwmB, workerBDeploymentID);
		
		//Verify if the peer has no consumers
		req_034_Util.getRemoteConsumersStatus(new LinkedList<ConsumerInfo>());
		Assert.assertFalse(req_015_Util.isPeerInterestedOnRemoteWorker(consumer1ID.getServiceID()));
	}
}