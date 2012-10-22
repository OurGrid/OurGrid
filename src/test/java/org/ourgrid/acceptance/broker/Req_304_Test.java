package org.ourgrid.acceptance.broker;

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
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ304")
public class Req_304_Test extends BrokerAcceptanceTestCase {
	
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
	 * This test contains the following steps:
	 *1. Create a Broker with the public key property set to "publicKey1";
   	 *2. Add a new job to the Broker;
     *3. Verify if the following error message was logged:
     *   1. BrokerComponent is not started.
	 */
	@ReqTest(test="AT-304.1", reqs="REQ304")
	@Test public void test_at_304_1_AddJobWithoutStartBroker() throws Exception {
		BrokerServerModule broker = req_301_Util.createBrokerModule();
		req_304_Util.addJob(false, 1, broker, "echo Hello World", "Test Job");
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1"
     * Start the Broker with the correct public key;
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 */
	@ReqTest(test=" AT-304.2", reqs="REQ304")
	@Test public void test_at_304_2_StartBrokerAndAddJob() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1";
	 * Start the Broker with the correct public key;
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World";
	 * Verify if the operation result contains a jobID with value 1.
	 * Add another job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World again"
	 * Verify if the operation result contains a jobID with value 2.
	 *
	 */
	@ReqTest(test=" AT-304.3", reqs="REQ304")
	@Test public void test_at_304_3_AddJobs() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2");
		
	}
	
	/**
	 * This test contains the following steps:
	 *
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *          username = test
	 *          servername = servertest
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * 
	 */
	@ReqTest(test=" AT-304.4", reqs="REQ304")
	@Test public void test_at_304_4_AddJobsToBrokerWithPeers() throws Exception {
		//creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		//adds and verifies
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");

	}

	/**
	 * This test contains the following steps:
	 *
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *          username = test
	 *          servername = servertest
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World2"
	 * Verify if the operation result contains a jobID with value 2.
	 *
	 */
	@ReqTest(test=" AT-304.5", reqs="REQ304")
	@Test public void test_at_304_5_AddJobWithPeersAndJobs() throws Exception {
		//creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		//adds and verifies
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		//adds and verifies
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2");
	}
	
	/**
	* Create a Broker with the public key property set to "publicKey1" and start the Broker;
    * Call setPeers giving a list containing one peer with the following attributes:
          o username = test
          o servername = servertest
    * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
    * Verify if the following debug message was logged:
         1. Peer with object id: [X] is UP. Where X is the objectID generated.
    * Add a job with the attributes: label: "Test Job " and one Task with remote 
    * 	attribute "echo Hello World"
    * Verify if the operation result contains a jobID with value 1.

	* @throws Exception
	*/
	@ReqTest(test=" AT-304.6", reqs="REQ304")
	@Test public void test_at_304_6_AddJobToBrokerWithAUPPeer() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.notifyPeerRecovery(getPeerSpec(), "publickey1", broker);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *         username = test
	 *         servername = servertest
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
	 * Verify if the following debug message was logged:
     *      1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 *
	 * @throws Exception
	 */
	@ReqTest(test=" AT-304.7", reqs="REQ304")
	@Test public void test_at_304_7_AddJobWithPeerUpAndJobs() throws Exception {
		//creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		//adds and verifies
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_304_Util.notifyPeerRecovery(getPeerSpec(), "publickey1", broker);
		//adds and verifies
		req_304_Util .addJob(true, 2, broker, "echo Hello World 2", "Test Job 2");
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
	 *
	 * @throws Exception
	 */
	@ReqTest(test=" AT-304.8", reqs="REQ304")
	@Test public void test_at_304_8_AddJobWithLoggedPeer() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) testStub.getObject());
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		//adds job and verify if the operation result contains a jobID with value 1
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
	}
	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes: * username = test * servername = servertest
	 * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
	 * Verify if the following debug message was logged:
	 *         Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 *
	 * @throws Exception
	 */
	@ReqTest(test=" AT-304.9", reqs="REQ304")
	@Test public void test_at_304_9_AddJobWithLoggedPeerWithJob() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());

		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) testStub.getObject());
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		//and job Verify if the operation result contains a jobID with value 1
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
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
     * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated.
     * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
     * Verify if the operation result contains a jobID with value 2.
	 */
	@ReqTest(test=" AT-304.10", reqs="REQ304")
	@Test public void test_at_304_10_AddJobInABrokerWithWorkers() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) testStub.getObject());
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		
		//receive workers
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), "publicKey1", testStub,jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//and job Verify if the operation result contains a jobID with value 2
		req_304_Util.addJob(true, 2, broker, "echo Hello World2", "Test Job2", peers);
	}
	
	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *          First peer = username = test and servername = servertest
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *          Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated.
	 * Call schedule with the correct public key;
	 * Verify if the worker's startWork message was called;
	 * Add a job with the attributes: label: "Test Job 3" and one Task with remote attribute "echo Hello World 3"
	 * Verify if the operation result contains a jobID with value #.
	 *
	 */
	@ReqTest(test=" AT-304.11", reqs="REQ304")
	@Test public void test_at_304_11_AddJobInABrokerWithScheduledWorker() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
		
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//and job Verify if the operation result contains a valid jobID 
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//hereIsWorker call
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), 
				"publicKey1", peerTestStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());

		workerTestStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, jobStub, new GridProcessHandle(1, 1, 1));

		req_304_Util.addJob(true, 3, broker, "echo Hello World 3", "Test Job 3", peers);
	}

	@ReqTest(test="AT-304.1", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_1_1_AddJobWithoutStartBroker() throws Exception {
		BrokerServerModule broker = req_301_Util.createBrokerModule();
		req_304_Util.addJob(false, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
	}

	@ReqTest(test=" AT-304.10", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_10_1_AddJobInABrokerWithWorkers() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) testStub.getObject());
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		
		//receive workers
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, "publicKey1", testStub,jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//and job Verify if the operation result contains a jobID with value 2
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
	}

	@ReqTest(test=" AT-304.11", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_11_1_AddJobInABrokerWithScheduledWorker() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
		
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//and job Verify if the operation result contains a valid jobID 
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//hereIsWorker call
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, 
				"publicKey1", peerTestStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerTestStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, jobStub, new GridProcessHandle(1, 1, 1));
	
		req_304_Util.addJob(true, 3, broker, JDLUtils.ECHO_JOB, peers);
	}

	@ReqTest(test=" AT-304.2", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_2_1_StartBrokerAndAddJob() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
	}

	@ReqTest(test=" AT-304.3", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_3_1_AddJobs() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
	}

	@ReqTest(test=" AT-304.4", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_4_1_AddJobsToBrokerWithPeers() throws Exception {
		//creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		//adds and verifies
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
	
	}

	@ReqTest(test=" AT-304.5", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_5_1_AddJobWithPeersAndJobs() throws Exception {
		//creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		//adds and verifies
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		//adds and verifies
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
	}

	@ReqTest(test=" AT-304.6", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_6_1_AddJobToBrokerWithAUPPeer() throws Exception{
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.notifyPeerRecovery(getPeerSpec(), "publickey1", broker);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
	}

	@ReqTest(test=" AT-304.7", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_7_1_AddJobWithPeerUpAndJobs() throws Exception {
		//creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		//adds and verifies
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_304_Util.notifyPeerRecovery(getPeerSpec(), "publickey1", broker);
		//adds and verifies
		req_304_Util .addJob(true, 2, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
	}

	@ReqTest(test=" AT-304.8", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_8_1_AddJobWithLoggedPeer() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) testStub.getObject());
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		//adds job and verify if the operation result contains a jobID with value 1
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
	}

	@ReqTest(test=" AT-304.9", reqs="REQ304")
	@Category(JDLCompliantTest.class) @Test public void test_at_304_9_1_AddJobWithLoggedPeerWithJob() throws Exception {
		//creates and starts 
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
	
		//notify and verify if the debug message was logged:
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider) testStub.getObject());
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		//and job Verify if the operation result contains a jobID with value 1
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		//and job Verify if the operation result contains a jobID with value 1
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
	}

}	