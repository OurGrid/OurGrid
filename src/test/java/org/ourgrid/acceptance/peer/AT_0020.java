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

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class AT_0020 extends PeerAcceptanceTestCase {

    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
	
    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
	/**
	 * Verifies if the peer mark the failed workers as CONTACTING.
	 */
	@ReqTest(test="AT-0020", reqs="")
	@Test public void test_AT_0020_WorkerOwnerAndWorkerIdleFail() throws Exception {
		
		//Set a mock log
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S1");
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);
		
		//Change workerA status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Change workerB status to OWNER
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		
		//Notify workers failure
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		req_019_Util.notifyWorkerFailure(workerBDeploymentID, component);
		
		//Verify workers status
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList();
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0020_WorkerOwnerAndWorkerIdleFailWithJDL() throws Exception {
		
		//Set a mock log
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S1", null, null);
		
		String workerAPublicKey = "workerApublicKey";
		String workerBPublicKey = "workerBPublicKey";
		
		DeploymentID workerADeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, workerAPublicKey);
		req_010_Util.workerLogin(component, workerSpecA, workerADeploymentID);

		DeploymentID workerBDeploymentID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, workerBPublicKey);
		req_010_Util.workerLogin(component, workerSpecB, workerBDeploymentID);
		
		//Change workerA status to IDLE
		req_025_Util.changeWorkerStatusToIdle(component, workerADeploymentID);
		
		//Change workerB status to OWNER
		req_025_Util.changeWorkerStatusToIdle(component, workerBDeploymentID);
		
		//Notify workers failure
		req_019_Util.notifyWorkerFailure(workerADeploymentID, component);
		req_019_Util.notifyWorkerFailure(workerBDeploymentID, component);
		
		//Verify workers status
        
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList();
		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
		
	}
}