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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class AT_0004 extends PeerAcceptanceTestCase {

	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
	
	
	/**
	 * Verifies if the advertise is stored in OurGrid Peer and is done only 
	 * after DiscoveryService Peer recovery.
	 */
	@ReqTest(test="AT-0004", reqs="")
	@Test public void test_AT_0004_AdvertBeforeDiscoveryServiceRecovery() throws Exception {
	    //Start a peer 
		component = (PeerComponent) req_010_Util.startPeer();

        //Workers A, B and C login
        WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
        WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
        WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S3");

		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "publicKeyA");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "publicKeyB");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "publicKeyC");
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

        //Change workers status to idle
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);

        //Notify ds recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(getDSUserName(), getDSServerName(), DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0004_AdvertBeforeDiscoveryServiceRecoveryWithJDL() throws Exception {
	    //Start a peer 
		component = (PeerComponent) req_010_Util.startPeer();

        //Workers A, B and C login
        WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
        WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S2", null, null);
        WorkerSpecification workerSpecC = workerAcceptanceUtil.createClassAdWorkerSpec("U3", "S3", null, null);
        
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "publicKeyA");
		req_010_Util.workerLogin(component, workerSpecA, workerAID);

		DeploymentID workerBID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, "publicKeyB");
		req_010_Util.workerLogin(component, workerSpecB, workerBID);

		DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, "publicKeyC");
		req_010_Util.workerLogin(component, workerSpecC, workerCID);

        //Change workers status to idle
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
		req_025_Util.changeWorkerStatusToIdle(component, workerBID);
		req_025_Util.changeWorkerStatusToIdle(component, workerCID);

        //Notify ds recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(getDSUserName(), getDSServerName(), DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
	}
}