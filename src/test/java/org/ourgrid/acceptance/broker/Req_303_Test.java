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

import static org.junit.Assert.assertFalse;

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
import org.ourgrid.acceptance.util.broker.Req_303_Util;
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
import org.ourgrid.broker.communication.receiver.BrokerControlReceiver;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.Scheduler;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ303")
public class Req_303_Test extends BrokerAcceptanceTestCase{
	
	private Req_301_Util req_301_Util = new Req_301_Util(getComponentContext());
	private Req_302_Util req_302_Util = new Req_302_Util(getComponentContext());
	private Req_303_Util req_303_Util = new Req_303_Util(getComponentContext());
	private Req_304_Util req_304_Util = new Req_304_Util(getComponentContext());
	private Req_311_Util req_311_Util = new Req_311_Util(getComponentContext());
	private Req_312_Util req_312_Util = new Req_312_Util(getComponentContext());
	private Req_327_Util req_327_Util = new Req_327_Util(getComponentContext());
	private Req_328_Util req_328_Util = new Req_328_Util(getComponentContext());
	private Req_329_Util req_329_Util = new Req_329_Util(getComponentContext());
	private Req_330_Util req_330_Util = new Req_330_Util(getComponentContext());
	private String peerUserAtServer = "test@servertest";

	/**
	 * 1. Create a Broker with the public key property set to "publicKey1";
	 * 2. Start a Broker with the correct public key "publicKey1";
	 * 3. Stop the Broker with the public key "wrongPublicKey" - Verify if the following warn message was logged:
	 *      1. An unknown entity tried to perform a control operation on the Broker. Only the local modules can 
	 *      perform this operation. Unknown entity public key: [senderPublicKey].
	 *
	 * 
	 */
	@ReqTest(test=" AT-303.1", reqs="REQ303")
	@Test public void test_at_303_1_StopBrokerWithWrongPublicKey() throws Exception {
		BrokerServerModule component = req_302_Util.startBroker(peerUserAtServer);
		req_303_Util.stopBroker(component, "wrongPublicKey");
	}
	
	/**
	 * 1. Start a Broker
	 * 2. Stop the Broker with the correct public key;
	 * 3. Verify if the following remote objects are NOT bound:
	 *      1. BROKER_STATUS_PROVIDER
	 *      2. LOCAL_WORKER_PROVIDER_CLIENT
	 *      3. WORKER_CLIENT
	 *      4. PEER_MONITOR_OBJECT_NAME
	 *      5. WORKER_MONITOR_OBJECT_NAME
	 *      6. SCHEDULER_OBJECT_NAME
	 * 4. Verify if the ControlClient received the operation succeed message.
	 *
	 */
	@ReqTest(test=" AT-303.2", reqs="REQ303")
	@Test public void test_at_303_2_BrokerStopCommand() throws Exception {
		//stop broker
		BrokerServerModule component = req_302_Util.startBroker(peerUserAtServer);
		//stop broker with correct public key
		req_303_Util.stopBroker(component, true);
		
		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(component, Module.CONTROL_OBJECT_NAME, BrokerControlReceiver.class));
		assertFalse(isBound(component, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(component, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(component, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}
	
	/**
	 * This test contains the following steps:
	 *1. Create a Broker with the public key property set to "publicKey1";
	 *2. Stop the Broker with the correct public key;
	 *3. Verify if the following error message was logged:
	 *      1. BrokerComponent is not started.
	 */
	@ReqTest(test="AT-303.3", reqs="REQ303")
	@Test public void test_at_303_3_StopANotStartedBroker() throws Exception {
		BrokerServerModule component = req_301_Util.createBrokerModule();
		req_303_Util.stopBroker(component, false);
	}

	/**
	 *  Create a Broker with the public key property set to "publicKey1"
	 * Start the Broker with the correct public key;
	 * Add a job with the attributes: label: "Test Job" and one Task with 
	 * remote attribute "echo Hello World"
	 * Stop the Broker with the correct public key;
	 * Verify if the following remote objects are NOT bound:
	 *     BROKER_STATUS_PROVIDER
	 *     LOCAL_WORKER_PROVIDER_CLIENT
	 *     WORKER_CLIENT
	 *     PEER_MONITOR_OBJECT_NAME
	 *     WORKER_MONITOR_OBJECT_NAME
	 *     SCHEDULER_OBJECT_NAME
	 *  Verify if the ControlClient received the operation succeed message.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-303.4", reqs="REQ303")
	@Test public void test_at_303_4_StopABrokerWithJobs() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		//create a job and add the one to the job
		req_304_Util.addJob(true, 1, broker, "Test Job", "echo Hello World");
		req_303_Util.stopBroker(broker, true);
		
		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *          username = test
	 *          servername = servertest
	 * Stop the Broker with the correct public key;
	 * Verify if the following remote objects are NOT bound:
	 *          BROKER_STATUS_PROVIDER
	 *          LOCAL_WORKER_PROVIDER_CLIENT
	 *          WORKER_CLIENT
	 *          PEER_MONITOR_OBJECT_NAME
	 *          WORKER_MONITOR_OBJECT_NAME
	 *          SCHEDULER_OBJECT_NAME
	 * Verify if the ControlClient received the operation succeed message.
	 *
	 */
	@ReqTest(test="AT-303.5", reqs="REQ303")
	@Test public void test_at_303_5_StopBrokerWithPeers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_303_Util.stopBroker(broker, true);

		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *       username = test
	 *       servername = servertest
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Stop the Broker with the correct public key;
	 * Verify if the following remote objects are NOT bound:
	 *       BROKER_STATUS_PROVIDER
	 *       LOCAL_WORKER_PROVIDER_CLIENT
	 *       WORKER_CLIENT
	 *       PEER_MONITOR_OBJECT_NAME
	 *       WORKER_MONITOR_OBJECT_NAME
	 *       SCHEDULER_OBJECT_NAME
	 * Verify if the ControlClient received the operation succeed message.
	 *
	 */
	@ReqTest(test="AT-303.6", reqs="REQ303")
	@Test public void test_at_303_6_StopBrokerWithPeersAndJobs() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_303_Util.stopBroker(broker, true);

		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}
	
	/**
	 *This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o username = test
     *     o servername = servertest
     * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
     * Verify if the following debug message was logged:
         1. Peer with object id: [X] is UP. Where X is the objectID generated.
     * Stop the Broker with the correct public key;
     * Verify if the following remote objects are NOT bound:
     *     o BROKER_STATUS_PROVIDER
     *     o LOCAL_WORKER_PROVIDER_CLIENT
     *     o WORKER_CLIENT
     *     o PEER_MONITOR_OBJECT_NAME
     *     o WORKER_MONITOR_OBJECT_NAME
     *     o SCHEDULER_OBJECT_NAME
     * Verify if the ControlClient received the operation succeed message.
	 */
	@ReqTest(test="AT-303.7", reqs="REQ303")
	@Test public void test_at_303_7_StopBrokerWithPeerUP() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_303_Util.stopBroker(broker, true);

		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}
	
	/**
	 *This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o username = test
     *     o servername = servertest
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
     * Verify if the following debug message was logged:
         1. Peer with object id: [X] is UP. Where X is the objectID generated.
     * Stop the Broker with the correct public key;
     * Verify if the following remote objects are NOT bound:
     *     o BROKER_STATUS_PROVIDER
     *     o LOCAL_WORKER_PROVIDER_CLIENT
     *     o WORKER_CLIENT
     *     o PEER_MONITOR_OBJECT_NAME
     *     o WORKER_MONITOR_OBJECT_NAME
     *     o SCHEDULER_OBJECT_NAME
     * Verify if the ControlClient received the operation succeed message.
	 */
	@ReqTest(test="AT-303.8", reqs="REQ303")
	@Test public void test_at_303_8_StopBrokerWithPeerUpAndAddedJob() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_303_Util.stopBroker(broker, true);

		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *      username = test 
	 *      servername = servertest
	 * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
	 * Verify if the following debug message was logged:
	 *       Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Stop the Broker with the correct public key;
	 * Verify if the following remote objects are NOT bound:
	 *       BROKER_STATUS_PROVIDER
	 *       LOCAL_WORKER_PROVIDER_CLIENT
	 *       WORKER_CLIENT
	 *       PEER_MONITOR_OBJECT_NAME
	 *       WORKER_MONITOR_OBJECT_NAME
	 *       SCHEDULER_OBJECT_NAME
	 * Verify if the ControlClient received the operation succeed message.
	 *
	 */
	@ReqTest(test="AT-303.9", reqs="REQ303") 
	@Test public void test_at_303_9_StopBrokerWithLoggedPeer() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());

		//notify and verify if the debug message was logged:
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		req_303_Util.stopBroker(broker, true);

		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes: * username = test * servername = servertest
	 * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
	 * Verify if the following debug message was logged:
	 *       Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Stop the Broker with the correct public key;
	 * Verify if the following remote objects are NOT bound:
	 *       BROKER_STATUS_PROVIDER
	 *       LOCAL_WORKER_PROVIDER_CLIENT
	 *       WORKER_CLIENT
	 *       PEER_MONITOR_OBJECT_NAME
	 *       WORKER_MONITOR_OBJECT_NAME
	 *       SCHEDULER_OBJECT_NAME
	 * Verify if the ControlClient received the operation succeed message.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-303.10", reqs="REQ303")
	@Test public void test_at_303_10_StopBrokerWithLoggedPeerAndJobs() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		LocalWorkerProvider lwp = req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add(lwp);
		
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		
		List<TestJob> testJobs = new ArrayList<TestJob>();
		testJobs.add(testJob);
		
		req_303_Util.stopBroker(broker, null, true, testJobs, peers);

		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
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
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated.
	 * Stop the Broker with the correct public key;
	 * Verify if the following remote objects are NOT bound:
	 *        BROKER_STATUS_PROVIDER
	 *        LOCAL_WORKER_PROVIDER_CLIENT
	 *        WORKER_CLIENT
	 *        PEER_MONITOR_OBJECT_NAME
	 *        WORKER_MONITOR_OBJECT_NAME
	 *        SCHEDULER_OBJECT_NAME
	 * Verify if the ControlClient received the operation succeed message.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-303.11", reqs="REQ303")
	@Test public void test_at_303_11_StopBrokerWithWorkers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		LocalWorkerProvider lwp = req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add(lwp);
		
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		
		List<TestJob> testJobs = new ArrayList<TestJob>();
		testJobs.add(jobStub);
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "publicKey1", true, true, true, true, new WorkerSpecification(attributes), "publicKey1", testStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		req_303_Util.stopBroker(broker, null, true, testJobs, peers);

		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *       First peer = username = test and servername = servertest
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *       Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated.
	 * Call schedule with the correct public key;
	 * Verify if the worker's startWork message was called;
	 * Stop the Broker with the correct public key;
	 * Verify if the following remote objects are NOT bound:
	 *       BROKER_STATUS_PROVIDER
	 *       LOCAL_WORKER_PROVIDER_CLIENT
	 *       WORKER_CLIENT
	 *       PEER_MONITOR_OBJECT_NAME
	 *       WORKER_MONITOR_OBJECT_NAME
	 *       SCHEDULER_OBJECT_NAME
	 * Verify if the ControlClient received the operation succeed message.
	 *
	 */
	@ReqTest(test="AT-303.12", reqs="REQ303")
	@Test public void test_at_303_12_StopBrokerWithScheduledWoreker() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		LocalWorkerProvider lwp = req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add(lwp);
		
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		TestJob jobStub = req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		List<TestJob> testJobs = new ArrayList<TestJob>();
		testJobs.add(jobStub1);
		testJobs.add(jobStub);
		
		//hereIsWorker call
		List<TestStub> workerStubs = new ArrayList<TestStub>();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), 
				"publicKey1", peerTestStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, workerStubs, peerTestStub, jobStub, new GridProcessHandle(2, 1, 1));
		
		req_303_Util.stopBroker(broker, null, true, testJobs, peers);

		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	@ReqTest(test="AT-303.10", reqs="REQ303")
	@Category(JDLCompliantTest.class) @Test public void test_at_303_10_1_StopBrokerWithLoggedPeerAndJobs() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		LocalWorkerProvider lwp = req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add(lwp);
		
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		
		List<TestJob> testJobs = new ArrayList<TestJob>();
		testJobs.add(testJob);
		
		req_303_Util.stopBroker(broker, null, true, testJobs, peers);
	
		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	@ReqTest(test="AT-303.11", reqs="REQ303")
	@Category(JDLCompliantTest.class) @Test public void test_at_303_11_1_StopBrokerWithWorkers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		LocalWorkerProvider lwp = req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add(lwp);
		
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		
		List<TestJob> testJobs = new ArrayList<TestJob>();
		testJobs.add(jobStub);
		
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "publicKey1", true, true, true, true, workerSpec, "publicKey1", testStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		req_303_Util.stopBroker(broker, null, true, testJobs, peers);
	
		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	@ReqTest(test="AT-303.12", reqs="REQ303")
	@Category(JDLCompliantTest.class) @Test public void test_at_303_12_1_StopBrokerWithScheduledWoreker() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		LocalWorkerProvider lwp = req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add(lwp);
		
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		TestJob jobStub = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		List<TestJob> testJobs = new ArrayList<TestJob>();
		testJobs.add(jobStub1);
		testJobs.add(jobStub);
		
		//hereIsWorker call
		List<TestStub> workerStubs = new ArrayList<TestStub>();
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, 
				"publicKey1", peerTestStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, workerStubs, peerTestStub, jobStub, new GridProcessHandle(2, 1, 1));
		
		req_303_Util.stopBroker(broker, null, true, testJobs, peers);
	
		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	@ReqTest(test="AT-303.4", reqs="REQ303")
	@Category(JDLCompliantTest.class) @Test public void test_at_303_4_1_StopABrokerWithJobs() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		//create a job and add the one to the job
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_303_Util.stopBroker(broker, true);
		
		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	@ReqTest(test="AT-303.6", reqs="REQ303")
	@Category(JDLCompliantTest.class) @Test public void test_at_303_6_1_StopBrokerWithPeersAndJobs() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_303_Util.stopBroker(broker, true);
	
		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}

	@ReqTest(test="AT-303.8", reqs="REQ303")
	@Category(JDLCompliantTest.class) @Test public void test_at_303_8_1_StopBrokerWithPeerUpAndAddedJob() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_303_Util.stopBroker(broker, true);
	
		//Verify if the following remote objects are NOT bound
		assertFalse(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
				LocalWorkerProviderClient.class));
		assertFalse(isBound(broker, BrokerConstants.WORKER_CLIENT, 
				WorkerClient.class));
		assertFalse(isBound(broker, BrokerConstants.SCHEDULER_OBJECT_NAME, Scheduler.class)); 
	}
}
