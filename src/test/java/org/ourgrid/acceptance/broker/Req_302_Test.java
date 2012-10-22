/*
 * Copyright (c) 2002-2008 Universidade Federal de Campina Grande
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.ourgrid.acceptance.broker;


import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.ClassAdsUtils;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.JDLUtils;
import org.ourgrid.acceptance.util.broker.Req_301_Util;
import org.ourgrid.acceptance.util.broker.Req_302_Util;
import org.ourgrid.acceptance.util.broker.Req_304_Util;
import org.ourgrid.acceptance.util.broker.Req_311_Util;
import org.ourgrid.acceptance.util.broker.Req_312_Util;
import org.ourgrid.acceptance.util.broker.Req_327_Util;
import org.ourgrid.acceptance.util.broker.Req_328_Util;
import org.ourgrid.acceptance.util.broker.Req_329_Util;
import org.ourgrid.acceptance.util.broker.Req_330_Util;
import org.ourgrid.acceptance.util.broker.TestJob;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.status.BrokerStatusProvider;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_302_Test extends BrokerAcceptanceTestCase{
	
	private Req_301_Util req_301_Util = new Req_301_Util(getComponentContext());
	private Req_302_Util req_302_Util = new Req_302_Util(getComponentContext());
	private Req_304_Util req_304_Util = new Req_304_Util(getComponentContext());
	private Req_311_Util req_311_Util = new Req_311_Util(getComponentContext());
	private Req_312_Util req_312_Util = new Req_312_Util(getComponentContext());
	private Req_327_Util req_327_Util = new Req_327_Util(getComponentContext());
	private Req_328_Util req_328_Util = new Req_328_Util(getComponentContext());
	private Req_329_Util req_329_Util = new Req_329_Util(getComponentContext());
	private Req_330_Util req_330_Util = new Req_330_Util(getComponentContext());
	
	private String peerUserAtServer = "test@servertest";
	
	/**
     *  Create a Broker with a public key and start it with another public key
	 */
	@Test public void test_at_302_1_BrokerInitializationWithWrongPublicKey() throws Exception {
		
		// Create the Broker with default public key
		BrokerServerModule brokerModule = req_301_Util.createBrokerModule();
		
		// Start the Broker with the public key "wrongPublicKey"
		req_302_Util.startBroker(brokerModule, "wrongPublicKey", peerUserAtServer);
	}
	
	/**
	 * Verifies the remote object deployment, after broker start.
	 */
	@Test public void test_at_302_2_BrokerInitialization() throws Exception {
		// start a Broker with the correct public key
		BrokerServerModule module = req_302_Util.startBroker(peerUserAtServer);
		
		// get(lookup) the remote object "CONTROL_OBJECT_NAME" and verify if its type is: org.ourgrid.common.interfaces.control.BrokerControl
		assertTrue(isBound(module, Module.CONTROL_OBJECT_NAME, 
				BrokerStatusProvider.class));
				
		// verify if the object "LOCAL_WORKER_PROVIDER_CLIENT" org.ourgrid.common.interfaces.LocalWorkerProviderClient is bound
		assertTrue(isBound(module, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));

		// verify if the object "WORKER_CLIENT" (org.ourgrid.common.interfaces.WorkerClient) is bound
		assertTrue(isBound(module, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
	}
	
	/**
	 * Try to start a Broker already started and throw an error.

	 * Create a Broker with the public key property set to "publicKey1"
     * Start a Broker with the correct public key;
     * Start the same Broker again with the correct public key.
     * Verify if the Control Result Operation contains an exception whose type is:
     *     org.ourgrid.jicx.control.ComponentAlreadyStartedException
     *
	 */
	@ReqTest(test="AT-302.3", reqs="REQ302")
	@Test public void test_at_302_3_StartABrokerAlreadyStarted() throws Exception {
		// start a Broker with the correct public key
		BrokerServerModule module = req_302_Util.startBroker(peerUserAtServer);
		// start the same Broker again with the correct public key
		req_302_Util.startBrokerAgain(module, peerUserAtServer);
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1";
	 * Start the Broker with the correct public key;
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World";
	 * Start the same Broker with the correct public key - Verify if the following warn message was logged:
	 *          The Broker has already been started.
	 *
	 */
	@ReqTest(test="AT-302.4", reqs="REQ302")
	@Test public void test_at_302_4_StartBrokerWithAddedJobs() throws Exception {
		// creates and starts a broker with correct public key 
		BrokerServerModule module = req_302_Util.startBroker(peerUserAtServer);
		
		// add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
		req_304_Util.addJob(true, 1, module, "echo Hello World", "Test Job");
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1" and 
	 * 	start the Broker;
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o username = test
     *     o servername = servertest
     * Start the same Broker with the correct public key - Verify if the following warn 
     * 	message was logged:
     *     o The Broker has already been started.
	 */
	@ReqTest(test = "AT-302.5", reqs="REQ302")
	@Test public void test_at_305_5_StartABrokerThatAlreadyHasPerrs()throws Exception {
		BrokerServerModule module = req_302_Util.startBroker(peerUserAtServer);
		req_302_Util.startBrokerAgain(module, peerUserAtServer);
	}
	
	/**
	 * This test contains the following steps:
	 *
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *         username = test
	 *         servername = servertest
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Start the same Broker with the correct public key - Verify if the following warn message was logged:
	 *         The Broker has already been started.
	 *
	 */
	@ReqTest(test="AT-302.6", reqs="REQ302")
	@Test public void test_at_302_6_StartBrokerWithAddedJobsAndPeers() throws Exception {
		//Creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}
	
	/**
	 *This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o username = test
     *     o servername = servertest
     *     
     * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
     *    
	 * Start the same Broker with the correct public key - Verify if the following warn message was logged:
     *     o The Broker has already been started.
	 */
	@ReqTest(test="AT-302.7", reqs="REQ302")
	@Test public void test_at_302_7_StartBrokerWithPeersUp() throws Exception {
		//Creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}
	
	/**
	 * This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o username = test
     *     o servername = servertest
     *     
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * 
     * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
     *    
     * Start the same Broker with the correct public key - Verify if the following warn message was logged:
     *     o The Broker has already been started.
	 */
	@ReqTest(test="AT-302.8", reqs="REQ302")
	@Test public void test_at_302_8_StartBrokerWithPeersUpAndAddedJobs() throws Exception {
		//Creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *       username = test
	 *       servername = servertest
	 * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
	 * Verify if the following debug message was logged:
	 *       Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Start the same Broker with the correct public key - Verify if the following warn message was logged:
	 *       The Broker has already been started.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-302.9", reqs="REQ302")
	@Test public void test_at_302_9_StartBokerWithLoggedPeer() throws Exception {
		//Creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}

	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *       username = test
	 *       servername = servertest
	 * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
	 * Verify if the following debug message was logged:
	 *       Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Start the same Broker with the correct public key - Verify if the following warn message was logged:
	 *       The Broker has already been started.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-302.10", reqs="REQ302")
	@Test public void test_at_302_10_StartBrokerWithLoggedPeerAndJobs() throws Exception {
		//Creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider) testStub.getObject());
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		//adds and verifies if the operation result contains a jobID with value 1.
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}
	
	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *         First peer = username = test and servername = servertest
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *         Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated.
	 * Start the same Broker with the correct public key - Verify if the following warn message was logged:
	 *         The Broker has already been started.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-302.11", reqs="REQ302")
	@Test public void test_at_302_11_StarBrokerWithWorkers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("peerPublicKey", getPeerSpec());
		
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) testStub.getObject());
		req_311_Util.verifyLogin(broker, "peerPublicKey", false, false, null, testStub);
		
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), "peerPublicKey", testStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}
	
	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *        First peer = username = test and servername = servertest
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated.
	 * Call schedule with the correct public key;
	 * Verify if the worker's startWork message was called;
	 * Start the same Broker with the correct public key - Verify if the following warn message was logged:
	 *        The Broker has already been started.
	 *
	 */
	@ReqTest(test="AT-302.12", reqs="REQ302")
	@Test public void test_at_302_12_StartBrokerWithSecheduledWorkers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("peerPublicKey", getPeerSpec());
		
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
		
		req_311_Util.verifyLogin(broker, "peerPublicKey", false, false, null, peerTestStub);
		
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//hereIsWorker call
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("servername", "xmpp.ourgrid.org");
		attributes.put("username", "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), "peerPublicKey", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerTestStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1));
		
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}

	@ReqTest(test="AT-302.10", reqs="REQ302")
	@Category(JDLCompliantTest.class)@Test public void test_at_302_10_1_StartBrokerWithLoggedPeerAndJobs() throws Exception {
		//Creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider) testStub.getObject());
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		//adds and verifies if the operation result contains a jobID with value 1.
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}

	@ReqTest(test="AT-302.11", reqs="REQ302")
	@Category(JDLCompliantTest.class)@Test public void test_at_302_11_1_StarBrokerWithWorkers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("peerPublicKey", getPeerSpec());
		
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) testStub.getObject());
		req_311_Util.verifyLogin(broker, "peerPublicKey", false, false, null, testStub);
		
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, "peerPublicKey", testStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}

	@ReqTest(test="AT-302.12", reqs="REQ302")
	@Category(JDLCompliantTest.class)@Test public void test_at_302_12_1_StartBrokerWithSecheduledWorkers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("peerPublicKey", getPeerSpec());
		
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
		
		req_311_Util.verifyLogin(broker, "peerPublicKey", false, false, null, peerTestStub);
		
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//hereIsWorker call
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, "peerPublicKey", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerTestStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1));
		
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}

	/**
	 * Create a Broker with the public key property set to "publicKey1";
	 * Start the Broker with the correct public key;
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World";
	 * Start the same Broker with the correct public key - Verify if the following warn message was logged:
	 *          The Broker has already been started.
	 *
	 */
	@ReqTest(test="AT-302.4", reqs="REQ302")
	@Category(JDLCompliantTest.class)@Test public void test_at_302_4_1_StartBrokerWithAddedJobs() throws Exception {
		// creates and starts a broker with correct public key 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		// add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
	}

	@ReqTest(test="AT-302.6", reqs="REQ302")
	@Category(JDLCompliantTest.class)@Test public void test_at_302_6_1_StartBrokerWithAddedJobsAndPeers() throws Exception {
		//Creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_302_Util.startBrokerAgain(broker,peerUserAtServer);
	}

	@ReqTest(test="AT-302.8", reqs="REQ302")
	@Category(JDLCompliantTest.class)@Test public void test_at_302_8_1_StartBrokerWithPeersUpAndAddedJobs() throws Exception {
		//Creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_302_Util.startBrokerAgain(broker, peerUserAtServer);
	}
}
