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

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

public class AT_0043 extends PeerAcceptanceTestCase {
	
	//FIXME rewrite testcases
    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_019_Util req_019_Util = new Req_019_Util(getComponentContext());

    @After
	public void tearDown() throws Exception {
		super.tearDown();
		peerAcceptanceUtil.deleteNOFRankingFile();
	}
	
    /**
     * This test contains the following steps:
     * 
     *    1. Create a Peer with the public key property set to "publicKey1";
     *    2. Start the Peer with the public key "wrongPublicKey" - Verify if the following warn message was logged:
     *          1. An unknown entity tried to start the Peer. Only the local modules can perform this operation. Unknown entity public key: [wrongPublicKey].
     *    3. Start the Peer with the correct public key "publicKey1";
     *    4. Set the Workers "A", and "B" with the public key "wrongPublicKey" - Verify if the following warn message was logged:
     *          1. An unknown entity tried to set the workers. Only the local modules can perform this operation. Unknown entity public key: [wrongPublicKey].
     *    5. Stop the Peer with the public key "wrongPublicKey" - Verify if the following warn message was logged:
     *          1. An unknown entity tried to stop the Peer. Only the local modules can perform this operation. Unknown entity public key: [wrongPublicKey].
     * 
     */
    @ReqTest(test="AT-0043", reqs="")
	@Test public void test_AT_043_KeyValidationOnControlCalls() throws Exception {
//		// Create a Peer with the default public key
//		component = peerAcceptanceUtil.createPeerComponent(getComponentContext());
//		
//		// Start the Peer with a wrong public key - expect a warn to be logged
//		req_010_Util.startPeer(component, "wrongPubKey");
//		
//		// Start the Peer with the right public key
//		req_010_Util.startPeer(component);
//		
//		// Workers A login with wrong public key - expect a warn to be logged
//		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("workerF", "xmpp.ourgrid.org");
//
//		DeploymentID workerAID = req_019_Util.createAndPublishWorkerManagement(component, workerSpecA, "workerAPubKey");
//		req_010_Util.workerLoginInvalidCertificate(component, workerSpecA, workerAID);
//
//		
//		// Stop the peer with wrong public key - expect a warn to be logged
//		req_010_Util.notNiceStopPeer(component, "wrongPubKey");
	}
    
    @Category(JDLCompliantTest.class)
    @Test public void test_AT_043_KeyValidationOnControlCallsWithJDL() throws Exception {
//		// Create a Peer with the default public key
//		component = peerAcceptanceUtil.createPeerComponent(getComponentContext());
//		
//		// Start the Peer with a wrong public key - expect a warn to be logged
//		req_010_Util.startPeer(component, "wrongPubKey");
//		
//		// Start the Peer with the right public key
//		req_010_Util.startPeer(component);
//		
//		// Workers login A and B with wrong public key - expect a warn to be logged
//		List<WorkerSpecification> workers = new ArrayList<WorkerSpecification>(2);
//		workers.add(workerAcceptanceUtil.createClassAdWorkerSpec("workerA", "xmpp.ourgrid.org", null, null));
//		workers.add(workerAcceptanceUtil.createClassAdWorkerSpec("workerB", "xmpp.ourgrid.org", null, null));
//		req_010_Util.setWorkersWithWrongPubKey(component, workers , "wrongPubKey");
//		
//		// Stop the peer with wrong public key - expect a warn to be logged
//		req_010_Util.notNiceStopPeer(component, "wrongPubKey");
	}
    
}