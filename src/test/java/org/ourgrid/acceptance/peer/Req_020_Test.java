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

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
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
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

@ReqTest(reqs="REQ020")
public class Req_020_Test extends PeerAcceptanceTestCase {

	private String DiscoveryServiceServer = "DiscoveryService.com";
	private String DiscoveryServiceUser = "DiscoveryService.user";

	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    
	DeploymentID dsID;

	private PeerComponent component;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
		dsID = new DeploymentID(new ContainerID(DiscoveryServiceUser, DiscoveryServiceServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		component = (PeerComponent) req_010_Util.startPeer();
	}

	@After
	public void tearDown() throws Exception{
//		req_010_Util.niceStopPeer(component);
		dsID = null;
		super.tearDown();
	}
	
	/**
	 * Verify if OurGrid Peer register interest on the recovery of DiscoveryService Peer, 
	 * after initialization.
	 */
	@ReqTest(test="AT-020.2", reqs="REQ020")
	@Test public void test_AT_020_2_OurGridPeerCreation() throws Exception{
		//Verify if the peer is interested on DiscoveryService recovery
		req_020_Util.isPeerInterestedOnDiscoveryService(component, dsID.getServiceID());
	}
	
	/**
	 * Verify if, when a DiscoveryService Peer recovers, the OurGrid Peer register 
	 * failure interest on it.
	 */
	@ReqTest(test="AT-020.3", reqs="REQ020")
	@Test public void test_AT_020_3_DiscoveryServiceRecovery() throws Exception {
		//Notify DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID(DiscoveryServiceUser, DiscoveryServiceServer, DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Verify if the peer is interested on DiscoveryService failure
		req_020_Util.isPeerInterestedOnDiscoveryService(component, dsID.getServiceID());
	}
	
	/**
	 * Verify if, when a DiscoveryService Peer fails, the OurGrid Peer register recovery 
	 * interest on it.
	 */
	@ReqTest(test="AT-020.4", reqs="REQ020")
	@Test public void test_AT_020_4_DiscoveryServiceFailure() throws Exception {
		//Notify DiscoveryService recovery
        DeploymentID dsID = new DeploymentID(new ContainerID(DiscoveryServiceUser, DiscoveryServiceServer, DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		Future<?> future = req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Notify DiscoveryService failure
		req_020_Util.notifyDiscoveryServiceFailure(dsID, future);
		
		//Verify if the peer is interested on DiscoveryService recovery
		req_020_Util.isPeerInterestedOnDiscoveryService(component, dsID.getServiceID());
	}
	
	@ReqTest(test="AT-020.5", reqs="REQ020")
	@Test public void test_AT_020_5_DiscoveryServicePeerRecoveryWithAdverts() throws Exception{
        //Set worker A
        WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");

	    //Notify worker A recovery
        String workerAPublicKey = "publicKeyA";
        DeploymentID workerID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
        req_010_Util.workerLogin(component, workerSpec, workerID);
        
        //Change worker status to IDLE
        req_025_Util.changeWorkerStatusToIdle(component, workerID);

        //Notify DiscoveryService recovery
        DeploymentID dsID = new DeploymentID(new ContainerID(DiscoveryServiceUser, DiscoveryServiceServer, DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
        req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
        
	}
	
	@ReqTest(test="AT-020.6", reqs="REQ020")
	@Test public void test_AT_020_6_DiscoveryServicePeerRecoveryAndPeerReceiveWorkerProviders() throws Exception {
		
        //Set worker A
        WorkerSpecification workerSpec = workerAcceptanceUtil.createWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org");
	    List<WorkerSpecification> workers = AcceptanceTestUtil.createList(workerSpec);
        req_010_Util.workerLogin(component, workers);

        
        DeploymentID rwpID = new DeploymentID(new ContainerID("rwp1", "rwpServer",
        		PeerConstants.MODULE_NAME, "rwpPublicKey"), PeerConstants.REMOTE_WORKER_PROVIDER);
        
		req_020_Util.receiveRemoteWorkerProvider(component, rwpID);
		
		DeploymentID rwpID2 = new DeploymentID(new ContainerID("rwp2", "rwpServer",
				PeerConstants.MODULE_NAME, "rwpPublicKey"), PeerConstants.REMOTE_WORKER_PROVIDER);
		
		
		List<DeploymentID> rwpIDs = new LinkedList<DeploymentID>();
		rwpIDs.add(rwpID2);
		
		req_020_Util.receiveRemoteWorkerProvider(component, rwpIDs);
		
		Collection<String> currentRWPs = req_020_Util.getCurrentRWPs(component);
		
		assertTrue(currentRWPs.contains(rwpID2.getServiceID().toString()));
		assertTrue(!currentRWPs.contains(rwpID.getServiceID().toString()));
	}

	@ReqTest(test="AT-020.5", reqs="REQ020")
	@Category(JDLCompliantTest.class) @Test public void test_AT_020_5_DiscoveryServicePeerRecoveryWithAdvertsWithJDL() throws Exception{
	    //Set worker A
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
	    String workerAPublicKey = "publicKeyA";
	    DeploymentID workerID = req_019_Util.createAndPublishWorkerManagement(component, workerSpec, workerAPublicKey);
	    req_010_Util.workerLogin(component, workerSpec, workerID);
	    
	    //Change worker status to IDLE
	    req_025_Util.changeWorkerStatusToIdle(component, workerID);
	
	    //Notify DiscoveryService recovery
	    DeploymentID dsID = new DeploymentID(new ContainerID(DiscoveryServiceUser, DiscoveryServiceServer, DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
	    req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
	}

	@ReqTest(test="AT-020.6", reqs="REQ020")
	@Category(JDLCompliantTest.class) @Test public void test_AT_020_6_DiscoveryServicePeerRecoveryAndPeerReceiveWorkerProvidersWithJDL() throws Exception {
		
	    //Set worker A
	    WorkerSpecification workerSpec = workerAcceptanceUtil.createClassAdWorkerSpec("workerA.ourgrid.org", "xmpp.ourgrid.org", null, null);
	    List<WorkerSpecification> workers = AcceptanceTestUtil.createList(workerSpec);
	    req_010_Util.workerLogin(component, workers);
	
	    
	    DeploymentID rwpID = new DeploymentID(new ContainerID("rwp1", "rwpServer",
	    		PeerConstants.MODULE_NAME, "rwpPublicKey"), PeerConstants.REMOTE_WORKER_PROVIDER);
	    
		req_020_Util.receiveRemoteWorkerProvider(component, rwpID);
		
		DeploymentID rwpID2 = new DeploymentID(new ContainerID("rwp2", "rwpServer",
				PeerConstants.MODULE_NAME, "rwpPublicKey"), PeerConstants.REMOTE_WORKER_PROVIDER);
		
		
		List<DeploymentID> rwpIDs = new LinkedList<DeploymentID>();
		rwpIDs.add(rwpID2);
		
		req_020_Util.receiveRemoteWorkerProvider(component, rwpIDs);
		
		Collection<String> currentRWPs = req_020_Util.getCurrentRWPs(component);
		
		assertTrue(currentRWPs.contains(rwpID2.getServiceID().toString()));
		assertTrue(!currentRWPs.contains(rwpID.getServiceID().toString()));
	}
	
	
}