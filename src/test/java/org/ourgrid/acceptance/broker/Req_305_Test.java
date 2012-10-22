package org.ourgrid.acceptance.broker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.ClassAdsUtils;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.JDLUtils;
import org.ourgrid.acceptance.util.LocalWorkerData;
import org.ourgrid.acceptance.util.broker.Req_301_Util;
import org.ourgrid.acceptance.util.broker.Req_302_Util;
import org.ourgrid.acceptance.util.broker.Req_304_Util;
import org.ourgrid.acceptance.util.broker.Req_305_Util;
import org.ourgrid.acceptance.util.broker.Req_309_Util;
import org.ourgrid.acceptance.util.broker.Req_311_Util;
import org.ourgrid.acceptance.util.broker.Req_312_Util;
import org.ourgrid.acceptance.util.broker.Req_327_Util;
import org.ourgrid.acceptance.util.broker.Req_328_Util;
import org.ourgrid.acceptance.util.broker.Req_329_Util;
import org.ourgrid.acceptance.util.broker.Req_330_Util;
import org.ourgrid.acceptance.util.broker.TestJob;
import org.ourgrid.broker.BrokerComponentContextFactory;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ305")
public class Req_305_Test extends BrokerAcceptanceTestCase{

	private Req_301_Util req_301_Util = new Req_301_Util(getComponentContext());
	private Req_302_Util req_302_Util = new Req_302_Util(getComponentContext());
	private Req_304_Util req_304_Util = new Req_304_Util(getComponentContext());
	private Req_305_Util req_305_Util = new Req_305_Util(getComponentContext());
	private Req_309_Util req_309_Util = new Req_309_Util(getComponentContext());
	private Req_311_Util req_311_Util = new Req_311_Util(getComponentContext());
	private Req_312_Util req_312_Util = new Req_312_Util(getComponentContext());
	private Req_327_Util req_327_Util = new Req_327_Util(getComponentContext());
	private Req_328_Util req_328_Util = new Req_328_Util(getComponentContext());
	private Req_329_Util req_329_Util = new Req_329_Util(getComponentContext());
	private Req_330_Util req_330_Util = new Req_330_Util(getComponentContext());
	private String peerUserAtServer = "test@servertest";
	private static final String PROPERTIES_FILENAME_2_PEERS = BROKER_TEST_DIR + "broker2peers.properties";

	private TestContext createComponentContext_2_peers() {
		return new TestContext(
				new BrokerComponentContextFactory(
						new PropertiesFileParser(PROPERTIES_FILENAME_2_PEERS
								)).createContext());
	}

	/**
	 *  This test contains the following steps:
	 *
	 *  1. Create a Broker with the public key property set to "publicKey1";
	 *  2. Cancel a job with id = 1;
	 *  3. Verify if the following error message was logged:
	 *         1. BrokerComponent is not started.
	 *
	 * 
	 */
	@ReqTest(test=" AT-305.1", reqs="REQ305")
	@Test public void test_at_305_1_CancelJobWithoutStartBroker() throws Exception{
		BrokerServerModule broker = req_301_Util.createBrokerModule();
		req_305_Util.cancelJob(false, false, 1, broker);
	}
	
	/**
	 * This test contains the following steps:
	 *
	 * 1. Create a Broker with the public key property set to "publicKey1";
	 * 2. Start the Broker with the correct public key;
	 * 3. Cancel a job with id = 1;
	 * 4. Verify if the following warn message was logged:
	 *        1. Job [1] was not cancelled, there is no job with such id.
	 *
	 */
	@ReqTest(test=" AT-305.2", reqs="REQ305")
	@Test public void test_at_305_2_CancelJobInicializedBrokerWithoutAddJob() throws Exception {
		//Creates and starts a broker with correct public key 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_305_Util.cancelJob(true, false, 1, broker);
		
	}
	
	/**
	* Create a Broker with the public key property set to "publicKey1"
	* 
    * Start the Broker with the correct public key;
    * 
    * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
    * 
    * Verify if the operation result contains a jobID with value 1.
    * 
    * Add another job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World again"
    * 
    * Verify if the operation result contains a jobID with value 2;
    * 
    * Cancel the job with ID = 2;
    * 
    * Verify if the following debug message was logged:
    *      o Job [2] was cancelled.
    *      
    * Verify if the Control operation result doesn't return an error.
	*/
	
	@ReqTest(test=" AT-305.3", reqs="REQ305")
	@Test public void test_at_305_3_CancelJobOfABrokerWithMoreThenOneJob() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_304_Util.addJob(true, 2, broker, "echo Hello World again", "Test Job 2");
		req_305_Util.cancelJob(true, true, 2, broker);
	}
	
	/**
	* Create a Broker with the public key property set to "publicKey1"
	* 
    * Start the Broker with the correct public key;
    * 
    * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
    * 
    * Verify if the operation result contains a jobID with value 1.
    * 
    * Add another job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World again"
    * 
    * Verify if the operation result contains a jobID with value 2;
    * 
    * Cancel the job with ID = 2;
    * 
    * Verify if the following debug message was logged:
    *      o Job [2] was cancelled.
    *      
    * Verify if the Control operation result doesn't return an error.
	*/
	
	@ReqTest(test=" AT-305.4", reqs="REQ305")
	@Test public void test_at_305_4_CancelJobsOfABrokerWithMoreThenOneJob() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_304_Util.addJob(true, 2, broker, "echo Hello World again", "Test Job 2");
		req_305_Util.cancelJob(true, true, 2, broker);
		req_305_Util.cancelJob(true, true, 1, broker);
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
     * Cancel the job with ID = 1;
     * 
     * Verify if the following warn message was logged:
     *     o Job [1] was not cancelled, there is no job with such id.
     *     
     * Verify if the Control operation result contains an exception with the message above.
	 */
	@ReqTest(test=" AT-305.5", reqs="REQ305")
	@Test public void test_at_305_5_CancelJobOfABrokerWithoutJobsAndSetedPeers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_305_Util.cancelJob(true, true, 1, broker);
	}
	
	/**
	* This test contains the following steps:

    * Create a Broker with the public key property set to "publicKey1" and start the Broker;
    * Call setPeers giving a list containing one peer with the following attributes:
          o username = test
          o servername = servertest
    * Add a job with the attributes: label: "Test Job" and one Task with remote 
    * 	attribute "echo Hello World"
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote 
    * 	attribute "echo Hello World2"
    * Cancel the job with ID = 1;
    * Verify if the following debug message was logged:
          o Job [1] was cancelled.

	 * @throws Exception
	 */
	@ReqTest(test="AT-305.6", reqs = "REQ305")
	@Test public void test_at_305_6_CancelJobOfABrokerWithTwoJobsAndSetedPeers() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "Test Job", "echo Hello World");
		req_304_Util.addJob(true, 2, broker, "Test Job 2", "echo Hello World 2");
		
		req_305_Util.cancelJob(true, true, 1, broker);
	}

	/**
	* This test contains the following steps:

    * Create a Broker with the public key property set to "publicKey1" and start the Broker;
    * Call setPeers giving a list containing one peer with the following attributes:
          o username = test
          o servername = servertest
    * Add a job with the attributes: label: "Test Job" and one Task with remote 
    * 	attribute "echo Hello World"
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote 
    * 	attribute "echo Hello World2"
    * Cancel the job with ID = 1;
    * Verify if the following debug message was logged:
          o Job [1] was cancelled.
    * Verify if the Control operation result doesn't return an error.
    * Cancel the job with ID = 2;
    * Verify if the following debug message was logged:
          o Job [2] was cancelled.
    * Verify if the Control operation result doesn't return an error.

	 * @throws Exception
	 */
	@ReqTest(test="AT-305.7", reqs = "REQ305")
	@Test public void test_at_305_7_CancelJobOfABrokerWithTwoJobsAndSetedPeers() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "Test Job", "echo Hello World");
		req_304_Util.addJob(true, 2, broker, "Test Job 2", "echo Hello World 2");
		
		req_305_Util.cancelJob(true, true, 1, broker);
		req_305_Util.cancelJob(true, true, 2, broker);
	}
	
	/**
	* Create a Broker with the public key property set to "publicKey1" and start ]
	* 	the Broker;
    * Call setPeers giving a list containing one peer with the following attributes:
          o username = test
          o servername = servertest
    * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
    * Verify if the following debug message was logged:
         1. Peer with object id: [X] is UP. Where X is the objectID generated.
    * Cancel the job with ID = 1;
    * Verify if the following warn message was logged:
          o Job [1] was not cancelled, there is no job with such id.

	 * @throws Exception
	 */
	@ReqTest(test="AT-305.8", reqs = "REQ305")
	@Test public void test_at_305_8_CancelJobOfABrokerWithoutJobsAndUPPeer() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.notifyPeerRecovery(getPeerSpec(), "publickey1", broker);
		req_305_Util.cancelJob(true, false, 1, broker);
	}
	
	/**
	* This test contains the following steps:
	*
    * Create a Broker with the public key property set to "publicKey1" and start the Broker;
    * 
    * Call setPeers giving a list containing one peer with the following attributes:
    *      o username = test
    *      o servername = servertest
    *      
    * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
    * 
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
    * 
    * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
    * 
    * Verify if the following debug message was logged:
    *     1. Peer with object id: [X] is UP. Where X is the objectID generated.
    *     
    * Cancel the job with ID = 1;
    * 
    * Verify if the following debug message was logged:
    *      o Job [1] was cancelled.
    *      
    * Verify if the Control operation result doesn't return an error.
	*/
	@ReqTest(test="AT-305.9", reqs = "REQ305")
	@Test public void test_at_305_9_CancelJobOfABrokerWithJobsAndUPPeer() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		req_304_Util.addJob(true, 1, broker, "Test Job", "echo Hello World");
		req_304_Util.addJob(true, 2, broker, "Test Job 2", "echo Hello World 2");
		
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_305_Util.cancelJob(true, true, 1, broker);
	}
	
	/**
	* This test contains the following steps:
	*
    * Create a Broker with the public key property set to "publicKey1" and start the Broker;
    * 
    * Call setPeers giving a list containing one peer with the following attributes:
    *      o username = test
    *      o servername = servertest
    *      
    * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
    * 
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
    * 
    * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
    * 
    * Verify if the following debug message was logged:
    *     1. Peer with object id: [X] is UP. Where X is the objectID generated.
    *     
    * Cancel the job with ID = 1;
    * 
    * Verify if the following debug message was logged:
    *      o Job [1] was cancelled.
    *      
    * Verify if the Control operation result doesn't return an error.
    * 
    * Verify if the following debug message was logged:
    * Job [2] was cancelled.
	*
	* Verify if the Control operation result doesn't return an error. 
	*/
	@ReqTest(test="AT-305.10", reqs = "REQ305")
	@Test public void test_at_305_10_CancelAllJobsOfABrokerWithJobsAndUPPeer() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		req_304_Util.addJob(true, 1, broker, "Test Job", "echo Hello World");
		req_304_Util.addJob(true, 2, broker, "Test Job 2", "echo Hello World 2");
		
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_305_Util.cancelJob(true, true, 1, broker);
		req_305_Util.cancelJob(true, true, 2, broker);
	}

	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes: * username = test * servername = servertest
	 * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Cancel the job with ID = 1;
	 * Verify if the following warn message was logged:
	 *        Job [1] was not canceled, there is no job with such id.
	 * Verify if the Control operation result contains an exception with the message above.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-305.11", reqs = "REQ305")
	@Test public void test_at_305_11_CancelJobWithLoggedPeerWithoutJobs() throws Exception {
		//creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());

		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		req_305_Util.cancelJob(true, false, 1, broker);
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing the peers with the following attributes:
	 *       First: username = test and servername = servertest
	 *       Second: username = test2 and servername = servertest2
	 * Call doNotifyRecovery passing a peer with username test on the parameter;
	 * Verify if the following debug message was logged:
	 *       Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Call doNotifyRecovery passing a peer with username test2 on the parameter;
	 * Verify if the following debug message was logged:
	 *       Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Do login with the public key property set to "publicKey2"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Cancel the job with ID = 2;
	 * Verify if the following debug message was logged:
	 *       Job [2] was cancelled.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-305.12", reqs = "REQ305")
	@Test public void test_at_305_12_CancelJobsWithLoggedPeerAndJobs() throws Exception { 
		//creates and starts
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();
		TestStub testStub1 = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		peerStubs.add((LocalWorkerProvider) testStub1.getObject());
		peerStubs.add((LocalWorkerProvider) testStub2.getObject());
		
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub1);
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, testStub2);
		
		//adds and verifies
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peerStubs);
		TestJob testJob = req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peerStubs);

		req_305_Util.cancelJob(true, true, 2, broker, testJob, peerStubs);
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing the peers with the following attributes:
	 *        First: username = test and servername = servertest
	 *        Second: username = test2 and servername = servertest2
	 * Call doNotifyRecovery passing a peer with username test on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Call doNotifyRecovery passing a peer with username test2 on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Do login with the public key property set to "publicKey2"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Cancel the job with ID = 2;
	 * Verify if the following debug message was logged:
	 *        Job [2] was cancelled.
	 * Cancel the job with ID = 1;
	 * Verify if the following debug message was logged:
	 *        Job [1] was cancelled.
	 * Cancel the job with ID = 1;
	 * Verify if the following warn message was logged:
	 *        Job [1] was not cancelled, there is no job with such id.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-305.13", reqs = "REQ305")
	@Test public void test_at_305_13_CancelLastJobWithLoggedPeer() throws Exception {
		//creates and starts
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();
		TestStub testStub1 = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		peerStubs.add((LocalWorkerProvider) testStub1.getObject());
		peerStubs.add((LocalWorkerProvider) testStub2.getObject());

		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub1);
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, testStub2);
		
		//adds and verifies
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "Test Job", "echo Hello World", peerStubs);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, "Test Job 2", "echo Hello World 2", peerStubs);

		//cancels and verifies
		req_305_Util.cancelJob(true, true, 2, broker, testJob2, peerStubs);
		req_305_Util.cancelJob(true, true, 1, broker, testJob1, peerStubs);
		req_305_Util.cancelJob(true, false, 1, broker, testJob1, null);
	}
	
	/**
	 * This test contains the following steps:
     * Create and start the Broker with the correct public key;
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o First peer = username = test and servername = servertest
     * Call doNotifyRecovery passing a peer with username = test on the parameter;
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     * Do login with the public key property set to "publicKey1"in the worker provider.
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * Verify if the operation result contains a jobID with value 1.
     * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
     * Verify if the operation result contains a jobID with value 2.
     * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated.
     * Cancel the job with ID = 1;
     * Verify if the following debug message was logged:
     *     o Job [1] was cancelled.
     * Cancel the job with ID = 1;
     * Verify if the following warn message was logged:
     *     o Job [1] was not cancelled, there is no job with such id.
	 */
	@ReqTest(test="AT-305.14", reqs = "REQ305")
	@Test public void test_at_305_14_CancelJobOfABrokerWithWorkers() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();		
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peerStubs.add((LocalWorkerProvider) peerTestStub.getObject());

		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peerStubs);
		//and job Verify if the operation result contains a jobID with value 2
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peerStubs);
		
		//receive workers
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), 
				"publicKey1", peerTestStub, jobStub);

		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//cancels and verifies
		List<LocalWorkerData> workersToDispose = new ArrayList<LocalWorkerData>();
		LocalWorkerData wAllocation = new LocalWorkerData((LocalWorkerProvider) peerTestStub.getObject(), workerTestStub.getDeploymentID());
		workersToDispose.add(wAllocation);
		
		req_305_Util.cancelJob(true, true, false, 1, broker, jobStub, peerStubs, workersToDispose);
		req_305_Util.cancelJob(true, false, 1, broker, jobStub, null);
	}
	
	/**
	 *This test contains the following steps:
     * Create and start the Broker with the correct public key;
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o First peer = username = test and servername = servertest
     * Call doNotifyRecovery passing a peer with username = test on the parameter;
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     * Do login with the public key property set to "publicKey1"in the worker provider.
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * Verify if the operation result contains a jobID with value 1.
     * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
     * Verify if the operation result contains a jobID with value 2.
     * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated.
     * Cancel the job with ID = 1;
     * Verify if the following debug message was logged:
     *     o Job [1] was cancelled.
     * Cancel the job with ID = 1;
     * Verify if the following warn message was logged:
     *     o Job [1] was not cancelled, there is no job with such id.
     * Cancel the job with ID = 2;
     * Verify if the following debug message was logged:
     *     o Job [2] was cancelled.
	 */
	@ReqTest(test="AT-305.15", reqs = "REQ305")
	@Test public void test_at_305_15_CancelJobOfABrokerWithWorkers() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();		
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peerStubs.add((LocalWorkerProvider) peerTestStub.getObject());

		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peerStubs);
		//and job Verify if the operation result contains a jobID with value 2
		TestJob jobStub2 = req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peerStubs);

		//receive workers
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), "publicKey1", 
				peerTestStub, jobStub1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());

		//cancels and verifies
		List<LocalWorkerData> workersToDispose = new ArrayList<LocalWorkerData>();
		LocalWorkerData wAllocation = new LocalWorkerData((LocalWorkerProvider) peerTestStub.getObject(), workerTestStub.getDeploymentID());
		workersToDispose.add(wAllocation);
		
		req_305_Util.cancelJob(true, true, false, 1, broker, jobStub1, peerStubs, workersToDispose);
		req_305_Util.cancelJob(true, false, 1, broker, jobStub1, null);
		req_305_Util.cancelJob(true, true, 2, broker, jobStub2, peerStubs);
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
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated by job 1.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated by job 2.
	 * Call schedule with the correct public key;
	 * Verify if the worker's startWork message was called;
	 * Cancel the job with ID = 1;
	 * Verify if the following debug message was logged:
	 *        Job [1] was cancelled.
	 * Cancel the job with ID = 1;
	 * Verify if the following warn message was logged:
	 *        Job [1] was not cancelled, there is no job with such id.
	 * Cancel the job with ID = 2;
	 * Verify if the following debug message was logged:
	 *        Job [2] was cancelled.
	 * Cancel the job with ID = 2;
	 * Verify if the following warn message was logged:
	 *        Job [2] was not cancelled, there is no job with such id.
	 *
	 */
	@ReqTest(test="AT-305.16", reqs = "REQ305")
	@Test public void test_at_305_16_CancelJobAtBrokerWithScheduledWorker() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();		
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peerStubs.add((LocalWorkerProvider) peerTestStub.getObject());

		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peerStubs);
		//and job Verify if the operation result contains a jobID with value 2
		TestJob jobStub2 = req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peerStubs);
		
		//hereIsWorker call
		List<TestStub> workerStubs = new ArrayList<TestStub>();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), 
				"publicKey1", peerTestStub, jobStub1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, workerStubs, peerTestStub, jobStub1, new GridProcessHandle(1, 1, 1));
		
		//cancels and verifies the warn message
		List<LocalWorkerData> workersToDispose = new ArrayList<LocalWorkerData>();
		LocalWorkerData wAllocation = new LocalWorkerData((LocalWorkerProvider) peerTestStub.getObject(), workerTestStub.getDeploymentID());
		workersToDispose.add(wAllocation);
		
		req_305_Util.cancelJob(true, true, true, 1, broker, jobStub1, peerStubs, workersToDispose);
		req_305_Util.cancelJob(true, false, 1, broker, jobStub1, null);
		req_305_Util.cancelJob(true, true, 2, broker, jobStub2, peerStubs);
		req_305_Util.cancelJob(true, false, 2, broker, jobStub2, null);
	}

	@ReqTest(test="AT-305.10", reqs = "REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_10_1_CancelAllJobsOfABrokerWithJobsAndUPPeer() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_305_Util.cancelJob(true, true, 1, broker);
		req_305_Util.cancelJob(true, true, 2, broker);
	}

	@ReqTest(test="AT-305.12", reqs = "REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_12_1_CancelJobsWithLoggedPeerAndJobs() throws Exception { 
		//creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();
		TestStub testStub1 = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		peerStubs.add((LocalWorkerProvider) testStub1.getObject());
		peerStubs.add((LocalWorkerProvider) testStub2.getObject());
		
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub1);
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, testStub2);
		
		//adds and verifies
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peerStubs);
		TestJob testJob = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peerStubs);
	
		req_305_Util.cancelJob(true, true, 2, broker, testJob, peerStubs);
	}

	@ReqTest(test="AT-305.13", reqs = "REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_13_1_CancelLastJobWithLoggedPeer() throws Exception {
		//creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey1", peer2);
		
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();
		TestStub testStub1 = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		peerStubs.add((LocalWorkerProvider) testStub1.getObject());
		peerStubs.add((LocalWorkerProvider) testStub2.getObject());
	
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub1);
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, testStub2);
		
		//adds and verifies
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peerStubs);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peerStubs);
	
		//cancels and verifies
		req_305_Util.cancelJob(true, true, 2, broker, testJob2, peerStubs);
		req_305_Util.cancelJob(true, true, 1, broker, testJob1, peerStubs);
		req_305_Util.cancelJob(true, false, 1, broker, testJob1, null);
	}

	@ReqTest(test="AT-305.14", reqs = "REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_14_1_CancelJobOfABrokerWithWorkers() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();		
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peerStubs.add((LocalWorkerProvider) peerTestStub.getObject());
	
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peerStubs);
		//and job Verify if the operation result contains a jobID with value 2
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peerStubs);
		
		//receive workers
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, 
				"publicKey1", peerTestStub, jobStub);
	
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//cancels and verifies
		List<LocalWorkerData> workersToDispose = new ArrayList<LocalWorkerData>();
		LocalWorkerData wAllocation = new LocalWorkerData((LocalWorkerProvider) peerTestStub.getObject(), workerTestStub.getDeploymentID());
		workersToDispose.add(wAllocation);
		
		req_305_Util.cancelJob(true, true, false, 1, broker, jobStub, peerStubs, workersToDispose);
		req_305_Util.cancelJob(true, false, 1, broker, jobStub, null);
	}

	@ReqTest(test="AT-305.15", reqs = "REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_15_1_CancelJobOfABrokerWithWorkers() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();		
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peerStubs.add((LocalWorkerProvider) peerTestStub.getObject());
	
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peerStubs);
		//and job Verify if the operation result contains a jobID with value 2
		TestJob jobStub2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peerStubs);
	
		//receive workers
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, "publicKey1", 
				peerTestStub, jobStub1);
	
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//cancels and verifies
		List<LocalWorkerData> workersToDispose = new ArrayList<LocalWorkerData>();
		LocalWorkerData wAllocation = new LocalWorkerData((LocalWorkerProvider) peerTestStub.getObject(), workerTestStub.getDeploymentID());
		workersToDispose.add(wAllocation);
		
		req_305_Util.cancelJob(true, true, false, 1, broker, jobStub1, peerStubs, workersToDispose);
		req_305_Util.cancelJob(true, false, 1, broker, jobStub1, null);
		req_305_Util.cancelJob(true, true, 2, broker, jobStub2, peerStubs);
	}

	@ReqTest(test="AT-305.16", reqs = "REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_16_1_CancelJobAtBrokerWithScheduledWorker() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peerStubs = new ArrayList<LocalWorkerProvider>();		
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peerStubs.add((LocalWorkerProvider) peerTestStub.getObject());
	
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peerStubs);
		//and job Verify if the operation result contains a jobID with value 2
		TestJob jobStub2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peerStubs);
		
		//hereIsWorker call
		List<TestStub> workerStubs = new ArrayList<TestStub>();
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, 
				"publicKey1", peerTestStub, jobStub1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, workerStubs, peerTestStub, jobStub1, new GridProcessHandle(1, 1, 1));
		
		//cancels and verifies the warn message
		List<LocalWorkerData> workersToDispose = new ArrayList<LocalWorkerData>();
		LocalWorkerData wAllocation = new LocalWorkerData((LocalWorkerProvider) peerTestStub.getObject(), workerTestStub.getDeploymentID());
		workersToDispose.add(wAllocation);
		
		req_305_Util.cancelJob(true, true, true, 1, broker, jobStub1, peerStubs, workersToDispose);
		req_305_Util.cancelJob(true, false, 1, broker, jobStub1, null);
		req_305_Util.cancelJob(true, true, 2, broker, jobStub2, peerStubs);
		req_305_Util.cancelJob(true, false, 2, broker, jobStub2, null);
	}

	@ReqTest(test=" AT-305.3", reqs="REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_3_1_CancelJobOfABrokerWithMoreThenOneJob() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_305_Util.cancelJob(true, true, 2, broker);
	}

	@ReqTest(test=" AT-305.4", reqs="REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_4_1_CancelJobsOfABrokerWithMoreThenOneJob() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_304_Util.addJob(true, 2, broker, "echo Hello World again", "Test Job 2");
		req_305_Util.cancelJob(true, true, 2, broker);
		req_305_Util.cancelJob(true, true, 1, broker);
	}

	@ReqTest(test=" AT-305.5", reqs="REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_5_1_CancelJobOfABrokerWithoutJobsAndSetedPeers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_305_Util.cancelJob(true, true, 1, broker);
	}

	@ReqTest(test="AT-305.6", reqs = "REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_6_1_CancelJobOfABrokerWithTwoJobsAndSetedPeers() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		req_305_Util.cancelJob(true, true, 1, broker);
	}

	@ReqTest(test="AT-305.7", reqs = "REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_7_1_CancelJobOfABrokerWithTwoJobsAndSetedPeers() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		req_305_Util.cancelJob(true, true, 1, broker);
		req_305_Util.cancelJob(true, true, 2, broker);
	}

	@ReqTest(test="AT-305.9", reqs = "REQ305")
	@Category(JDLCompliantTest.class) @Test public void test_at_305_9_1_CancelJobOfABrokerWithJobsAndUPPeer() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_305_Util.cancelJob(true, true, 1, broker);
	}
}
