/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
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
package org.ourgrid.acceptance.worker;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.worker.Req_004_Util;
import org.ourgrid.acceptance.util.worker.Req_087_Util;
import org.ourgrid.acceptance.util.worker.Req_088_Util;
import org.ourgrid.acceptance.util.worker.Req_094_Util;
import org.ourgrid.acceptance.util.worker.Req_120_Util;
import org.ourgrid.acceptance.util.worker.Req_126_Util;
import org.ourgrid.acceptance.util.worker.Req_127_Util;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


public class Test_302_WorkerOwner extends WorkerAcceptanceTestCase {
	
	private Req_004_Util req_004_Util = new Req_004_Util(getComponentContext());
	private Req_087_Util req_087_Util = new Req_087_Util(getComponentContext());
	private Req_088_Util req_088_Util = new Req_088_Util(getComponentContext());
	private Req_094_Util req_094_Util = new Req_094_Util(getComponentContext());
	private Req_120_Util req_120_Util = new Req_120_Util(getComponentContext());
	private Req_126_Util req_126_Util = new Req_126_Util(getComponentContext());
	private Req_127_Util req_127_Util = new Req_127_Util(getComponentContext());

	private DeploymentID peerID = null;
	private DeploymentID workerID = null;
	private WorkerComponent component = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		String peerPubKey = workerAcceptanceUtil.simulateAuthentication();
		peerID = new DeploymentID(new ContainerID("peerUser", "peerServer", PeerConstants.MODULE_NAME,
				peerPubKey), PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
		component = req_004_Util.startWorker(peerID, true);
		workerID = workerAcceptanceUtil.getPeerMonitorDeployment().getDeploymentID();	
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test public void test_302_1_Start() throws Exception {
		req_004_Util.startWorkerAlreadyStarted(component);
	}

	@Test public void test_302_2_Pause() throws Exception {
		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);

		req_087_Util.pauseOwnerWorker(component);

		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);
	}

	@Test public void test_302_3_Status() throws Exception {
		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);
	}

	@Test public void test_302_6_WorkerManagementClientIsDown() throws Exception {
		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);

		req_127_Util.notifyPeerFailureAtOwnerWorker(component, peerID);

		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);
	}

	@Test public void test_302_7_WorkerManagementClientIsUp() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();

		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);
		TestCase.assertTrue(AcceptanceTestUtil.isInterested(component, peerID.getServiceID(), workerID));
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());

		req_126_Util.notifyPeerRecoveryAtWorkerWithoutPeer(component, peerID, workerAcceptanceUtil.getPeerMonitorDeployment().getDeploymentID());

		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
	}

	@Test public void test_302_8_Resume() throws Exception {
		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);

		req_088_Util.resumeOwnerWorker(component);

		req_094_Util.getWorkerStatus(WorkerStatus.IDLE);
	}

	@Test public void test_302_15_Stop() throws Exception {
		req_120_Util.stopWorker(component);
	}

}
