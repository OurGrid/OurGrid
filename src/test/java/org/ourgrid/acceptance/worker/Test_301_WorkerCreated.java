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
import org.ourgrid.acceptance.util.worker.Req_003_Util;
import org.ourgrid.acceptance.util.worker.Req_004_Util;
import org.ourgrid.acceptance.util.worker.Req_087_Util;
import org.ourgrid.acceptance.util.worker.Req_088_Util;
import org.ourgrid.acceptance.util.worker.Req_094_Util;
import org.ourgrid.acceptance.util.worker.Req_120_Util;
import org.ourgrid.common.interfaces.control.WorkerControl;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

@ReqTest(reqs="REQ003")
public class Test_301_WorkerCreated extends WorkerAcceptanceTestCase {

	private Req_003_Util req_003_Util = new Req_003_Util(getComponentContext());
	private Req_004_Util req_004_Util = new Req_004_Util(getComponentContext());
	private Req_087_Util req_087_Util = new Req_087_Util(getComponentContext());
	private Req_088_Util req_088_Util = new Req_088_Util(getComponentContext());
	private Req_094_Util req_094_Util = new Req_094_Util(getComponentContext());
	private Req_120_Util req_120_Util = new Req_120_Util(getComponentContext());
	
	private DeploymentID peerID = null;
	private DeploymentID workerID = null;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();

		String peerPubKey = workerAcceptanceUtil.simulateAuthentication();
		peerID = new DeploymentID(new ContainerID("peeruser", "xmpp.ourgrid.org",
				PeerConstants.MODULE_NAME, peerPubKey),
				PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Verifies if the Worker's module was created and if Worker Control was 
	 * bound.
	 * 1- Create a Worker.
	 * 2- Verify if there are a module named "WORKER".
	 * 3- Lookup "WORKER_CONTROL" object and verify if it is a 
	 * org.ourgrid.common.interfaces.control.WorkerControl.
	 */
	@ReqTest(test=" AT-003.1", reqs="REQ003")
	@Test public void test_301_1_WorkerCreation() throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent();
		
		TestCase.assertTrue(isModuleStarted(component, WorkerConstants.MODULE_NAME));
		TestCase.assertTrue(isBound(component, Module.CONTROL_OBJECT_NAME, WorkerControl.class));
	}
	
	@Test public void test_301_2_Stop() throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent();
		req_120_Util.stopUnstartedWorker(component);
	}
	
	@Test public void test_301_3_Pause() throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent();
		req_087_Util.pauseUnstartedWorker(component);
	}
	
	@Test public void test_301_4_Resume() throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent();
		req_088_Util.resumeUnstartedWorker(component);
	}
	
	@Test public void test_301_5_StartWithIdlenessDetectionOn() throws Exception {
		WorkerComponent component = req_004_Util.startWorker(peerID, true);
		workerID = workerAcceptanceUtil.getPeerMonitorDeployment().getDeploymentID();
		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);
		TestCase.assertEquals(peerID.getServiceID().toString(), 
				WorkerDAOFactory.getInstance().getWorkerStatusDAO().getMasterPeerAddress());
		TestCase.assertTrue(AcceptanceTestUtil.isInterested(component, peerID.getServiceID(), workerID));
	}
	
	@Test public void test_301_6_startWithIdlenessDetectionOff() throws Exception {
		WorkerComponent component = req_004_Util.startWorker(peerID, false);
		workerID = workerAcceptanceUtil.getPeerMonitorDeployment().getDeploymentID();
		req_094_Util.getWorkerStatus(WorkerStatus.IDLE);
		
		TestCase.assertTrue(AcceptanceTestUtil.isInterested(component, peerID.getServiceID(), workerID));
	}

}
