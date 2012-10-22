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

import org.junit.Test;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class AT_L008 extends PeerAcceptanceTestCase {
	

	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());
	
	/**
	 * Verifies a peer with idle local Workers. The idle workers must become first in result.
	 */
	@ReqTest(test="AT-0008", reqs="")
	@Test public void test_AT_0008_PeerWithSomeIdleLocalWorkers() throws Exception{
	    //Start the peer
		component = req_010_Util.startPeer();
		
        //Set the workers A, B, C and D
        String publicKeyA = "publicKeyA";
        String publicKeyB = "publicKeyB";
        String publicKeyC = "publicKeyC";
        WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
        WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
        WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S3");
        WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S4");
        List<WorkerSpecification> workers = AcceptanceTestUtil.createList(workerSpecA, workerSpecB, workerSpecC, workerSpecD);

        req_010_Util.workerLogin(component, workers);
        
        //Notify recovery of workers A, B and C
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, publicKeyA);
        req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, publicKeyB);
        DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, publicKeyC);
        
        //Change status of workers A and C to idle
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
        req_025_Util.changeWorkerStatusToIdle(component, workerCID);
        
        //Verify workers status
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
        WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IDLE, null);
        WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.OWNER, null);
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoC, workerInfoB);

        req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
	
	
	/**
	 * Verifies a peer with idle local Workers. The idle workers must become first in result.
	 */
	@ReqTest(test="AT-L008", reqs="") //TODO Lauro I changed it
	@Test public void test_AT_L008_AddLocalWorkersWithTags() throws Exception{
	   	//Start the peer
		component = req_010_Util.startPeer();
		
        //Set the workers A, B, C and D
        String publicKeyA = "publicKeyA";
        String publicKeyB = "publicKeyB";
        String publicKeyC = "publicKeyC";
        WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
        List<String> tagsWorkerA = AcceptanceTestUtil.createList("workerSpecA", "stringA1", "stringA2", "stringA3");
        WorkerAcceptanceUtil.setAnnotationsWorkerSpec(workerSpecA, tagsWorkerA);
        WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
        WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S3");
        List<String> tagsWorkerC = AcceptanceTestUtil.createList("workerSpecC", "stringC1", "stringC2", "stringC3");
        WorkerAcceptanceUtil.setAnnotationsWorkerSpec(workerSpecC, tagsWorkerC);
        WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S4");
        List<WorkerSpecification> workers = AcceptanceTestUtil.createList(workerSpecA, workerSpecB, workerSpecC, workerSpecD);

        req_010_Util.workerLogin(component, workers);
        
      //Notify recovery of workers A, B and C
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, publicKeyA);
        req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, publicKeyB);
        DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, publicKeyC);
        
        
        //Change status of workers A and C to idle
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
        req_025_Util.changeWorkerStatusToIdle(component, workerCID);
                
        //Verify workers status
        WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.IDLE, null);
        WorkerInfo workerInfoC = new WorkerInfo(workerSpecC, LocalWorkerState.IDLE, null);
        WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.OWNER, null);
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoC, workerInfoB);

        req_036_Util.getLocalWorkersStatus(localWorkersInfo);      
	}
	
	/**
	 * Verifies a peer with idle local Workers. The idle workers must become first in result.
	 */
	@ReqTest(test="AT-L008", reqs="") //TODO Lauro I changed it
	@Test public void test_AT_L008_UpdateTagsLocalWorkers() throws Exception{
	   	//Start the peer
		component = req_010_Util.startPeer();
		
        //Set the workers A, B, C and D
        String publicKeyA = "publicKeyA";
        String publicKeyB = "publicKeyB";
        String publicKeyC = "publicKeyC";
        WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
        WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
        WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S3");
        WorkerSpecification workerSpecD = workerAcceptanceUtil.createWorkerSpec("U4", "S4");
        List<WorkerSpecification> workers = AcceptanceTestUtil.createList(workerSpecA, workerSpecB, workerSpecC, workerSpecD);

        req_010_Util.workerLogin(component, workers);
        
      //Notify recovery of workers A, B and C
		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, publicKeyA);
        req_019_Util.createAndPublishWorkerManagement(component, workerSpecB, publicKeyB);
        DeploymentID workerCID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecC, publicKeyC);
        
        
        //Change status of workers A and C to idle
		req_025_Util.changeWorkerStatusToIdle(component, workerAID);
        req_025_Util.changeWorkerStatusToIdle(component, workerCID);
                
                
        List<String> tagsWorkerA = AcceptanceTestUtil.createList("workerSpecA", "stringA1", "stringA2", "stringA3");
        WorkerSpecification newWorkerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
        WorkerAcceptanceUtil.setAnnotationsWorkerSpec(newWorkerSpecA, tagsWorkerA);
                
        WorkerSpecification newWorkerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S2");
        
        List<String> tagsWorkerC = AcceptanceTestUtil.createList("workerSpecC", "stringC1", "stringC2", "stringC3");
        WorkerSpecification newWorkerSpecC = workerAcceptanceUtil.createWorkerSpec("U3", "S3");
        WorkerAcceptanceUtil.setAnnotationsWorkerSpec(newWorkerSpecC, tagsWorkerC);
        
        //Verify workers status
        WorkerInfo workerInfoA = new WorkerInfo(newWorkerSpecA, LocalWorkerState.IDLE, null);        
        WorkerInfo workerInfoC = new WorkerInfo(newWorkerSpecC, LocalWorkerState.IDLE, null);
        WorkerInfo workerInfoB = new WorkerInfo(newWorkerSpecB, LocalWorkerState.OWNER, null);
        List<WorkerInfo> localWorkersInfo = AcceptanceTestUtil.createList(workerInfoA, workerInfoC, workerInfoB);
                
        req_036_Util.getLocalWorkersStatus(localWorkersInfo);      
	}

	
	
}