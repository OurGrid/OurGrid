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
import org.ourgrid.acceptance.util.broker.Req_302_Util;
import org.ourgrid.acceptance.util.broker.Req_304_Util;
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

@ReqTest(reqs="REQ312")
public class Req_312_Test extends BrokerAcceptanceTestCase {
	
	private Req_302_Util req_302_Util;
	private Req_304_Util req_304_Util;
	private Req_309_Util req_309_Util;
	private Req_311_Util req_311_Util;
	private Req_312_Util req_312_Util;
	private Req_327_Util req_327_Util;
	private Req_328_Util req_328_Util;
	private Req_329_Util req_329_Util;
	private Req_330_Util req_330_Util;
	private String peerUserAtServer;
	private static final String PROPERTIES_FILENAME_2_PEERS = BROKER_TEST_DIR + "broker2peers.properties";

	
	@Override
	public void setUp() throws Exception {
		req_302_Util = new Req_302_Util(super.createComponentContext());
		req_304_Util = new Req_304_Util(super.createComponentContext());
		req_309_Util = new Req_309_Util(super.createComponentContext());
		req_311_Util = new Req_311_Util(super.createComponentContext());
		req_312_Util = new Req_312_Util(super.createComponentContext());
		req_327_Util = new Req_327_Util(super.createComponentContext());
		req_328_Util = new Req_328_Util(super.createComponentContext());
		req_329_Util = new Req_329_Util(super.createComponentContext());
		req_330_Util = new Req_330_Util(super.createComponentContext());
		peerUserAtServer = "test@servertest";
	}
	
	private TestContext createComponentContext_2_peers() {
		return new TestContext(
				new BrokerComponentContextFactory(
						new PropertiesFileParser(PROPERTIES_FILENAME_2_PEERS
								)).createContext());
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
     * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
     *    
     * Call hereIsWorker giving a worker with public key "workerPublicKey" and senderPublicKey "senderPublicKey";
     * 
     * Verify if the following warn message was logged:
     *    1. The broker is not logged in the peer with public key [senderPublicKey]. This worker with public key [workerPublicKey] delivery was ignored.
	 */
	@ReqTest(test="AT-312.1", reqs="REQ312")
	@Test public void test_at_312_1_ReceiveWorkersWithoutLoggedInAPeer() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());

		
		//notify and verify if the debug message was logged
		TestStub peerStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to receive worker
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, false, true, true, new WorkerSpecification(), "publicKey1", peerStub, null);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
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
     * Call hereIsWorker giving a worker with public key "workerPublicKey" and senderPublicKey "senderPublicKey";
     * 
     * Verify if the following warn message was logged:
     *    1. The broker is not logged in the peer with public key [senderPublicKey]. 
     *    This worker with public key [workerPublicKey] delivery was ignored.
	 */
	@ReqTest(test="AT-312.2", reqs="REQ312")
	@Test public void test_at_312_2_ReceiveWorkersWithoutLoggedInAPeerAndAddedJobs() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add a job
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		
		////notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to receive worker
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, false, true, true, new WorkerSpecification(), "publicKey1", peerStub, null);
	
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	}
	
	/**
	 * This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing the peers with the following attributes:
     *     o First: username = test and servername = servertest
     *     o Second: username = test2 and servername = servertest2
     *     
     * Call doNotifyRecovery passing a peer with the username = test2 on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
     *    
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * 
     * Call hereIsWorker from the peer with username = test giving a worker with public key "workerPublicKey";
     * 
     * Verify if the following warn message was logged:
     *    1. The peer with public key [publicKey1], which is down, delivered a worker with public key: [workerPublicKey].
	 */
	@ReqTest(test="AT-312.3", reqs="REQ312")
	@Test public void test_at_312_3_ReceiveWorkersOfAPeerDown() throws Exception {
		//Creates and starts
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
				
		//notify and verify if the debug message was logged
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub peerStub = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		
		//add a job
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		
		//try to receive worker
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, false, true, true, new WorkerSpecification(), "publicKey1", 
				peerStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
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
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     *     
     * Do login with the public key property set to "publicKey1"in the worker provider.
     * 
     * Call hereIsWorker giving a worker with public key "workerPublicKey";
     * 
     * Verify if the following warn message was logged:
     *     o The broker has no running jobs. Disposing worker with public key: 
     *      [workerPublicKey] to peer with public key: [publicKey1].
	 */
	@ReqTest(test="AT-312.4", reqs="REQ312")
	@Test public void test_at_312_4_ReceiveWorkersWithoutAddedJobs() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		//try to receive worker
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, false, new WorkerSpecification(), "publicKey1", testStub, null);
	
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	}
	
	/**
	 *This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o First peer = username = test and servername = servertest
     *     
     * Call doNotifyRecovery passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     *     
     * Do login with the public key property set to "publicKey1"in the worker provider.
     * 
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * 
     * Verify if the operation result contains a jobID with value 1.
     * 
     * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
     * 
     * Verify if the operation result contains a jobID with value 2.
     * 
     * Call hereIsWorker giving a worker with public key "workerPublicKey" and randomize the request ID;
	 */
	@ReqTest(test="AT-312.5", reqs="REQ312")
	@Test public void test_at_312_5_ReceiveWorkersWithAddedJobs() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add another peer
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
				
		//add jobs
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		TestJob testJob = req_304_Util.addJob(true, 2, broker, "echo Hello World2", "Test Job2", peers);
		
		//try to receive worker
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), "publicKey1", 
				peerTestStub, testJob );
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	}
	
	/**
	 *This test contains the following steps:
	 *
     * Create and start the Broker with the correct public key;
     * 
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o First peer = username = test and servername = servertest
     *     
     * Call doNotifyRecovery passing a peer with username = test on the parameter;
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     *     
     * Do login with the public key property set to "publicKey1"in the worker provider.
     * 
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * 
     * Verify if the operation result contains a jobID with value 1.
     * 
     * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
     * 
     * Verify if the operation result contains a jobID with value 2.
     * 
     * Call hereIsWorker giving a worker with public key "workerPublicKey1" and the request ID generated by job 1.
     * 
     * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated by job 2.
	 */
	@ReqTest(test="AT-312.6", reqs="REQ312")
	@Test public void test_at_312_6_ReceiveWorkerAndReceiveAgain() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		//add another peer 
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)testStub.getObject());
		
		//add jobs
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		TestJob jobStub2 = req_304_Util.addJob(true, 2, broker, "echo Hello World2", "Test Job2", peers);
		
		//try to receive workers
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey1", true, true, true, true, new WorkerSpecification(attributes), "publicKey1", testStub, jobStub1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		Map<String, String> attributes2 = new HashMap<String, String>();
		attributes2.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes2.put(OurGridSpecificationConstants.ATT_USERNAME, "username2");
		workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, new WorkerSpecification(attributes2), "publicKey1", testStub, jobStub2);
	
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	}
	
	/**
	 *This test contains the following steps:
	 *
     * Create and start the Broker with the correct public key;
     * 
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o First peer = username = test and servername = servertest
     *     
     * Call doNotifyRecovery passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    o Peer with object id: [X] is UP. Where X is the objectID generated.
     *    
     * Do login with the public key property set to "publicKey1"in the worker provider.
     * 
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * 
     * Verify if the operation result contains a jobID with value 1.
     * 
     * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
     * 
     * Verify if the operation result contains a jobID with value 2.
     * 
     * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated by job 1.
     * 
     * Call schedule with the correct public key;
     * 
     * Verify if the worker's startWork message was called;
     * 
     * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated by job 2.
	 */
	@ReqTest(test="AT-312.7", reqs="REQ312")
	@Test public void test_at_312_7_ReceiveWorkerWithAlreadyScheduledWorkers() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add another peer 
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//add jobs
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		TestJob jobStub2 = req_304_Util.addJob(true, 2, broker, "echo Hello World2", "Test Job2", peers);
		
		//hereIsWorker call
		List<TestStub> testStubs = new ArrayList<TestStub>();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), 
				"publicKey1", peerTestStub, jobStub1);

		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		testStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, testStubs, peerTestStub, jobStub1, new GridProcessHandle(1, 1, 1));
			
		//try to receive another worker
		Map<String, String> attributes2 = new HashMap<String, String>();
		attributes2.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes2.put(OurGridSpecificationConstants.ATT_USERNAME, "username2");
		workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, new WorkerSpecification(attributes2), "publicKey1", 
				peerTestStub, jobStub2);

		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
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
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and senderPublicKey "senderPublicKey";
	 * 
	 * Verify if the following warn message was logged:
	 *    1. The broker is not logged in the peer with public key [senderPublicKey]. 
	 *    This worker with public key [workerPublicKey] delivery was ignored.
	 */
	@ReqTest(test="AT-312.2", reqs="REQ312")
	@Category(JDLCompliantTest.class) @Test public void test_at_312_2_1_ReceiveWorkersWithoutLoggedInAPeerAndAddedJobs() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add a job
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		////notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to receive worker
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, false, true, true, new WorkerSpecification(), "publicKey1", peerStub, null);
	
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	}

	/**
	 * This test contains the following steps:
	 *
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * 
	 * Call setPeers giving a list containing the peers with the following attributes:
	 *     o First: username = test and servername = servertest
	 *     o Second: username = test2 and servername = servertest2
	 *     
	 * Call doNotifyRecovery passing a peer with the username = test2 on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 *    
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * 
	 * Call hereIsWorker from the peer with username = test giving a worker with public key "workerPublicKey";
	 * 
	 * Verify if the following warn message was logged:
	 *    1. The peer with public key [publicKey1], which is down, delivered a worker with public key: [workerPublicKey].
	 */
	@ReqTest(test="AT-312.3", reqs="REQ312")
	@Category(JDLCompliantTest.class) @Test public void test_at_312_3_1_ReceiveWorkersOfAPeerDown() throws Exception {
		//Creates and starts
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
				
		//notify and verify if the debug message was logged
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub peerStub = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		
		//add a job
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		//try to receive worker
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, false, true, true, new WorkerSpecification(), "publicKey1", 
				peerStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	}

	/**
	 *This test contains the following steps:
	 *
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * 
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *     o First peer = username = test and servername = servertest
	 *     
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *     o Peer with object id: [X] is UP. Where X is the objectID generated.
	 *     
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * 
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * 
	 * Verify if the operation result contains a jobID with value 1.
	 * 
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * 
	 * Verify if the operation result contains a jobID with value 2.
	 * 
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and randomize the request ID;
	 */
	@ReqTest(test="AT-312.5", reqs="REQ312")
	@Category(JDLCompliantTest.class) @Test public void test_at_312_5_1_ReceiveWorkersWithoutAddedJobs() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add another peer
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
				
		//add jobs
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peers);
		TestJob testJob = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//try to receive worker
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, "publicKey1", 
				peerTestStub, testJob );
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	}

	/**
	 *This test contains the following steps:
	 *
	 * Create and start the Broker with the correct public key;
	 * 
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *     o First peer = username = test and servername = servertest
	 *     
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *     o Peer with object id: [X] is UP. Where X is the objectID generated.
	 *     
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * 
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * 
	 * Verify if the operation result contains a jobID with value 1.
	 * 
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * 
	 * Verify if the operation result contains a jobID with value 2.
	 * 
	 * Call hereIsWorker giving a worker with public key "workerPublicKey1" and the request ID generated by job 1.
	 * 
	 * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated by job 2.
	 */
	@ReqTest(test="AT-312.6", reqs="REQ312")
	@Category(JDLCompliantTest.class) @Test public void test_at_312_6_1_ReceiveWorkerAndReceiveAgain() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
		
		//add another peer 
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)testStub.getObject());
		
		//add jobs
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		TestJob jobStub2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//try to receive workers
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey1", true, true, true, true, workerSpec, "publicKey1", testStub, jobStub1);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, workerSpec, "publicKey1", testStub, jobStub2);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	}

	/**
	 *This test contains the following steps:
	 *
	 * Create and start the Broker with the correct public key;
	 * 
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *     o First peer = username = test and servername = servertest
	 *     
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *    o Peer with object id: [X] is UP. Where X is the objectID generated.
	 *    
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * 
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * 
	 * Verify if the operation result contains a jobID with value 1.
	 * 
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * 
	 * Verify if the operation result contains a jobID with value 2.
	 * 
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated by job 1.
	 * 
	 * Call schedule with the correct public key;
	 * 
	 * Verify if the worker's startWork message was called;
	 * 
	 * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated by job 2.
	 */
	@ReqTest(test="AT-312.7", reqs="REQ312")
	@Category(JDLCompliantTest.class) @Test public void test_at_312_7_1_ReceiveWorkerWithAlreadyScheduledWorkers() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add another peer 
		List<LocalWorkerProvider> peers = new ArrayList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//add jobs
		TestJob jobStub1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		TestJob jobStub2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//hereIsWorker call
		List<TestStub> testStubs = new ArrayList<TestStub>();
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, 
				"publicKey1", peerTestStub, jobStub1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		testStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, testStubs, peerTestStub, jobStub1, new GridProcessHandle(1, 1, 1));
			
		//try to receive another worker
		workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 1 );
		workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, workerSpec, "publicKey1", 
				peerTestStub, jobStub2);

		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	}
}
