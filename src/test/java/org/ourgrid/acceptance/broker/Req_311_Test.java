package org.ourgrid.acceptance.broker;

import static org.junit.Assert.assertTrue;

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
import org.ourgrid.acceptance.util.broker.Req_301_Util;
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
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ311")
public class Req_311_Test extends BrokerAcceptanceTestCase {
	
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
	 *This test contains the following steps:
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
     * Do login with the public key property set to "wrongPublicKey" in the worker provider.
     * 
     * Verify if the following warn message was logged:
     *    1. An unknown peer sent a login response. Peer public key: [wrongPublicKey].
	 */
	@ReqTest(test="AT-311.1", reqs="REQ311")
	@Test public void test_at_311_1_LoginFailure() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);

		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "wrongPublicKey", false, true, null, testStub, jobs, jobIDs);
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * 
     * Call setPeers giving a list containing one peer with the following attributes:
     * 	o username = test o servername = servertest
     * 
     * Add a job with the attributes: label: "Test Job" and one Task with remote 
     * 	attribute "echo Hello World"
     * 
     * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
     * 
     * Verify if the following debug message was logged:
     *   1. Peer with object id: [X] is UP. Where X is the objectID generated.=
     *   
     * Do login with the public key property set to "publicKey1" in the worker provider;
     * 
     * Do login again with the public key property set to "publicKey1" in the worker provider.
     * 
     * Verify if the following warn message was logged:
     *   1. The peer with public key [publicKey1] tried to send a login response, but it is already logged.
	 *
	 */
	@ReqTest(test="AT-311.2", reqs="REQ311")
	@Test public void test_at_311_2_LoginFailure() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub, jobs, jobIDs);
		
		//try to login again
		req_311_Util.verifyLogin(broker, "publicKey1", true, true, "error message", testStub, jobs, jobIDs);
	}
	
	/**
	 * This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing one peer with the following attributes: 
     *     * username = test * servername = servertest
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * 
     * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     *     
     * Do login with the public key property set to "publicKey1" and Login Result with an error 
     * message "error message" in the worker provider.
     * 
     * Verify if the following warn message was logged:
     *     o An error ocurred while logging in the peer with public key : [publicKey1] - error message
	 *
	 */
	@ReqTest(test="AT-311.3", reqs="REQ311")
	@Test public void test_at_311_3_LoginFailure() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, true, "error message", testStub, jobs, jobIDs);
	}
	
	/**
	 * This test contains the following steps:
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o username = test
     *     o servername = servertest
     *     
     * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     * Do login with the public key property set to "publicKey1"in the worker provider.
	 */
	@ReqTest(test="AT-311.4", reqs="REQ311")
	@Test public void test_at_311_4_LoginSuccess() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub);
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
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     *     
     * Do login with the public key property set to "publicKey1"in the worker provider.
	 */
	@ReqTest(test="AT-311.5", reqs="REQ311")
	@Test public void test_at_311_5_LoginSuccessWithAddedJobs() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub, jobs, jobIDs);
	}
	
	/**
	 *This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing one peer with the following attributes: 
     * * username = test * servername = servertest
     * 
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * 
     * Call doNotifyRecovery passing a peer with the attributes above on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     *     
     * Do login with the public key property set to "publicKey1"in the worker provider.
     * 
     * Call again setPeers giving a list containing one peer with the following attributes:
     *     o First peer = username = test and servername = servertest
     *     o Second peer = username = test2 and servername = servertest2
     *     
     * Verify if the operation returned an empty Control Operation Result;
     * 
     * Verify if the remote object "PEER_MONITOR_OBJECT_NAME" was registered like an 
     * interested by the peer with servername = servertest
     * 
     * Verify if the remote object "PEER_MONITOR_OBJECT_NAME" was registered like an 
     * interested by the peer with servername = servertest2
     * 
     * Do login with the public key property set to "publicKey2" in the second peer.
	 *
	 */
	@ReqTest(test="AT-311.6", reqs="REQ311")
	@Test public void test_at_311_6_LoginSuccessWithAddedJobs() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add job
		List<JobSpecification> jobs = new LinkedList<JobSpecification>();
		List<Integer> jobIDs = new LinkedList<Integer>();
		List<TestJob> testJobs = new LinkedList<TestJob>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		testJobs.add(testJob);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		List<LocalWorkerProvider> peerStubs = new LinkedList<LocalWorkerProvider>();
		TestStub testStub1 = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peerStubs.add((LocalWorkerProvider) testStub1.getObject());
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub1, jobs, jobIDs);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey2");
		publicKeys.add("publicKey3");
				
		//notify and verify if the debug message was logged for peer2
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", getPeerSpec());
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID2, broker);
		
		//verifies if the remote objects were registered  
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(getPeerSpec(), "publicKey2", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(getPeerSpec(), "publicKey3", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
		
		//try to login on peer2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, testStub2, jobs, jobIDs);
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey" and start the Broker;
	 * Call setPeers giving a list containing two peer with the following attributes:
	 *        First peer = username = test and servername = servertest
	 *        Second peer = username = test2 and servername = servertest2
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Call doNotifyRecovery passing a peer with username test2 on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey2" in the second peer.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-311.7", reqs="REQ311")
	@Test public void test_at_311_7_LoginSuccessWithLoggedPeerAndJobs() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);

		//set peers
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		TestStub testStub1 = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		
		//try to login on peer1
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub1, jobs, jobIDs);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		
		//try to login on peer2
		req_311_Util.verifyLogin(broker, "publicKey2", true, false, null, testStub2, jobs, jobIDs);
	}
	
	/**
	 * Create and start the Broker with the correct public key;
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o First peer = username = test and servername = servertest
     *     o Secondpeer = username = test2 and servername = servertest2
     * Call doNotifyRecovery passing a peer with username = test on the parameter;
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     * Do login with the public key property set to "publicKey1"in the worker provider.
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * Verify if the operation result contains a jobID with value 1.
     * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
     * Verify if the operation result contains a jobID with value 2.
     * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated from peer with username = test
     * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     * Do login with the public key property set to "publicKey2" in the second peer.
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-311.8", reqs="REQ311")
	@Test public void test_at_311_8_LoginSuccessWithLoggedPeerAndJobs() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//set peers
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");

		//notify and verify if the debug message was logged
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		
		List<LocalWorkerProvider> peerStubs = new LinkedList<LocalWorkerProvider>();
		peerStubs.add((LocalWorkerProvider) peerTestStub.getObject());
		
		//try to login on peer1
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peerStubs);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peerStubs);
		jobs.add(testJob1.getJobSpec());
		jobIDs.add(1);
		jobs.add(testJob2.getJobSpec());
		jobIDs.add(2);
		
		//try to receive workers
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), "publicKey1", peerTestStub, 
				testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		
		//try to login on peer2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, testStub2, jobs, jobIDs);
	}
	
	/**
     * Create and start the Broker with the correct public key;
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o First peer = username = test and servername = servertest
     *     o Secondpeer = username = test2 and servername = servertest2
     * Call doNotifyRecovery passing a peer with username = test on the parameter;
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     * Do login with the public key property set to "publicKey1"in the worker provider.
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * Verify if the operation result contains a jobID with value 1.
     * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
     * Verify if the operation result contains a jobID with value 2.
     * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated.
     * Call schedule with the correct public key;
     * Verify if the worker's startWork message was called;
     * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     * Do login with the public key property set to "publicKey2" in the worker provider;
     * Verify if the peer with publick key = "publicKey2" requestWorkers message was called for the job 2;
	 *
	 * @throws Exception
	 */
	@ReqTest(test="AT-311.9", reqs="REQ311")
	@Test public void test_at_311_9_LoginSuccessWithScheduledWorkers() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//set peers
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");

		//notify and verify if the debug message was logged for peer1
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		TestStub peerTestStub1 = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		
		//try to login on peer1
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub1);
		
		List<LocalWorkerProvider> peerStubs = new LinkedList<LocalWorkerProvider>();
		peerStubs.add((LocalWorkerProvider) peerTestStub1.getObject());
		
		//add jobs
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peerStubs);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peerStubs);
		jobs.add(testJob2.getJobSpec());
		jobIDs.add(2);
		
		//hereIsWorker call
		List<TestStub> testStubs = new ArrayList<TestStub>();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				new WorkerSpecification(attributes), "publicKey1", peerTestStub1, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		testStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, testStubs, peerTestStub1, testJob1, new GridProcessHandle(1, 1, 1));
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		
		//try to login on peer2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, testStub2, jobs, jobIDs);
	}

	@ReqTest(test="AT-311.1", reqs="REQ311")
	@Category(JDLCompliantTest.class) @Test public void test_at_311_1_1_LoginFailure() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
	
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "wrongPublicKey", false, true, null, testStub, jobs, jobIDs);
	}

	@ReqTest(test="AT-311.2", reqs="REQ311")
	@Category(JDLCompliantTest.class) @Test public void test_at_311_2_1_LoginFailure() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub, jobs, jobIDs);
		
		//try to login again
		req_311_Util.verifyLogin(broker, "publicKey1", true, true, "error message", testStub, jobs, jobIDs);
	}

	@ReqTest(test="AT-311.3", reqs="REQ311")
	@Category(JDLCompliantTest.class) @Test public void test_at_311_3_1_LoginFailure() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, true, "error message", testStub, jobs, jobIDs);
	}

	@ReqTest(test="AT-311.5", reqs="REQ311")
	@Category(JDLCompliantTest.class) @Test public void test_at_311_5_1_LoginSuccessWithAddedJobs() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub testStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub, jobs, jobIDs);
	}

	@ReqTest(test="AT-311.7", reqs="REQ311")
	@Category(JDLCompliantTest.class) @Test public void test_at_311_7_1_LoginSuccessWithLoggedPeerAndJobs() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//set peers
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
	
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		TestStub testStub1 = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		
		//try to login on peer1
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, testStub1, jobs, jobIDs);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		
		//try to login on peer2
		req_311_Util.verifyLogin(broker, "publicKey2", true, false, null, testStub2, jobs, jobIDs);
	}

	@ReqTest(test="AT-311.8", reqs="REQ311")
	@Category(JDLCompliantTest.class) @Test public void test_at_311_8_1_LoginSuccessWithLoggedPeerAndJobs() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//set peers
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
	
		//notify and verify if the debug message was logged
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		
		List<LocalWorkerProvider> peerStubs = new LinkedList<LocalWorkerProvider>();
		peerStubs.add((LocalWorkerProvider) peerTestStub.getObject());
		
		//try to login on peer1
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peerStubs);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peerStubs);
		jobs.add(testJob1.getJobSpec());
		jobIDs.add(1);
		jobs.add(testJob2.getJobSpec());
		jobIDs.add(2);
		
		//try to receive workers
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, "publicKey1", peerTestStub, 
				testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		
		//try to login on peer2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, testStub2, jobs, jobIDs);
	}

	@ReqTest(test="AT-311.9", reqs="REQ311")
	@Category(JDLCompliantTest.class) @Test public void test_at_311_9_1_LoginSuccessWithScheduledWorkers() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//set peers
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID deploymentID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		TestStub peerTestStub1 = req_327_Util.notifyPeerRecovery(peer1, deploymentID1, broker);
		
		//try to login on peer1
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub1);
		
		List<LocalWorkerProvider> peerStubs = new LinkedList<LocalWorkerProvider>();
		peerStubs.add((LocalWorkerProvider) peerTestStub1.getObject());
		
		//add jobs
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peerStubs);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peerStubs);
		jobs.add(testJob2.getJobSpec());
		jobIDs.add(2);
		
		//hereIsWorker call
		List<TestStub> testStubs = new ArrayList<TestStub>();
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub1, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		testStubs.add(workerTestStub);
		
		//do Schedule
		req_329_Util.doSchedule(broker, testStubs, peerTestStub1, testJob1, new GridProcessHandle(1, 1, 1));
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID deploymentID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub testStub2 = req_327_Util.notifyPeerRecovery(peer2, deploymentID2, broker);
		
		//try to login on peer2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, testStub2, jobs, jobIDs);
	}
}
