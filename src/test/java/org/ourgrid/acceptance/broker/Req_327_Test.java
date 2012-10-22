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

@ReqTest(reqs="REQ327")
public class Req_327_Test extends BrokerAcceptanceTestCase{
	
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
     *      o username = test
     *      o servername = servertest
     *      
     * Verify if the operation returned an empty Control Operation Result;
     * 
     * Call doNotifyRecovery passing a peer with the attribute above on the parameter;
     * Verify if the following debug message was logged:
   	 *	1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-327.1", reqs="REQ327")
	@Test public void test_at_327_1_NotifyPeerRevovery() throws Exception {
		//create and start the broker
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID objectID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		req_327_Util.notifyPeerRecovery(getPeerSpec(), objectID, brokerComponent);
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
     * Verify if the operation returned an empty Control Operation Result;
     * 
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * 
     * Call doNotifyRecovery passing a peer with the attribute above on the parameter;
     * 
     * Verify if the following debug message was logged:
   	 *	  1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-327.2", reqs="REQ327")
	@Test public void test_at_327_2_NotifyPeerRevoveryWithJobsAdded() throws Exception {
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, brokerComponent, "echo Hello World", "Test Job");
		DeploymentID objectID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		req_327_Util.notifyPeerRecovery(getPeerSpec(), objectID, brokerComponent);
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
     * Call doNotifyRecovery passing a peer with the username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
     *    
     * Call doNotifyRecovery passing a peer with the username = test2 on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-327.3", reqs="REQ327")
	@Test public void test_at_327_3_NotifyPeersRecovery() throws Exception {
		
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		req_327_Util.notifyPeerRecovery(peer1, objectID1, broker);
		
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objectID2, broker);
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
     * Call doNotifyRecovery passing a peer with the username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
     *    
     * Call doNotifyRecovery passing a peer with the username = test2 on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-327.4", reqs="REQ327")
	@Test public void test_at_327_4_NotifyPeersRecoveryWithAddedJob() throws Exception {
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
		
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		req_327_Util.notifyPeerRecovery(peer1, objectID1, broker);
		
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objectID2, broker);
	}
	
	/**
	* Create and start the Broker with the correct public key;
    * Call setPeers giving a list containing one peer with the following attributes:
          o First peer = username = test and servername = servertest
          o Second peer = username = test2 and servername = servertest2
    * Verify if the operation returned an empty Control Operation Result;
    * Call doNotifyRecovery passing a peer with username = test on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is UP. Where X is the objectID generated.
    * Do login with the public key property set to "publicKey1"in the worker provider.
    * Add a job with the attributes: label: "Test Job" and one Task with remote 
    * 	attribute "echo Hello World"
    * Verify if the operation result contains a jobID with value 1.
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote 
    * 	attribute "echo Hello World 2"
    * Verify if the operation result contains a jobID with value 2.
    * Call hereIsWorker giving a worker with public key "workerPublicKey" and the 
    * 	request ID generated.
    * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is UP. Where X is the objectID generated.
          
	 * @throws Exception
	 */
	
	@ReqTest(test="AT-327.7", reqs="REQ327")
	@Test public void test_at_327_7_PeerRecovery() throws Exception{
		//create a broker and set peers
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);

		List<PeerSpecification> peersSpecs = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");

		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID1 = req_328_Util.createPeerDeploymentID("publickey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, objID1, broker);
		
		//do login with peer1
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello Word", "Test Job", peers);
		//add the second Job
		req_304_Util.addJob(true, 2, broker, "echo Hello Word 2", "Test Job 2", peers);
		
		//call here is worker

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), "publickey1", peerTestStub, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		//call doNotifyRecovery passing a peer with username test2
		DeploymentID objID2 = req_328_Util.createPeerDeploymentID("publickey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objID2, broker);
	}
	
	/**
	* Create and start the Broker with the correct public key;
    * Call setPeers giving a list containing one peer with the following attributes:
          o First peer = username = test and servername = servertest
          o Second peer = username = test2 and servername = servertest2
    * Call doNotifyRecovery passing a peer with username = test on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is UP. Where X is the objectID generated.
    * Do login with the public key property set to "publicKey1"in the worker provider.
    * Add a job with the attributes: label: "Test Job" and one Task with remote 
    * 	attribute "echo Hello World"
    * Verify if the operation result contains a jobID with value 1.
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote 
    * 	attribute "echo Hello World 2"
    * Verify if the operation result contains a jobID with value 2.
    * Call hereIsWorker giving a worker with public key "workerPublicKey" and the 
    * 	request ID generated.
    * Call schedule with the correct public key;
    * Verify if the worker's startWork message was called;
    * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is UP. Where X is the objectID generated.

	 * @throws Exception
	 */
	
	@ReqTest(test="AT-327.8", reqs="REQ327")
	@Test public void test_at_327_8_PeerRecovery() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//set two peers
		List<PeerSpecification> peersSpecs = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peersSpecs.add(peer1);
		peersSpecs.add(peer2);
	
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID1 = req_328_Util.createPeerDeploymentID("publickey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, objID1, broker);
		
		//do login with peer1
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello Word", "Test Job", peers);
		//add the second Job
		req_304_Util.addJob(true, 2, broker, "echo Hello Word 2", "Test Job 2", peers);
		
		//call here is worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), 
				"publickey1", peerTestStub, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		workerTestStubs.add(workerTestStub);
		
		//call Schedule with the correct public key
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		//call doNotifyRecovery passing a peer with username test2
		DeploymentID objID2 = req_328_Util.createPeerDeploymentID("publickey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objID2, broker);
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
	 * Verify if the operation returned an empty Control Operation Result;
	 * 
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * 
	 * Call doNotifyRecovery passing a peer with the attribute above on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *	  1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-327.2", reqs="REQ327")
	@Category(JDLCompliantTest.class) @Test public void test_at_327_2_1_NotifyPeerRevoveryWithJobsAdded() throws Exception {
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, brokerComponent, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		DeploymentID objectID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		req_327_Util.notifyPeerRecovery(getPeerSpec(), objectID, brokerComponent);
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
	 * Call doNotifyRecovery passing a peer with the username = test on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 *    
	 * Call doNotifyRecovery passing a peer with the username = test2 on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-327.4", reqs="REQ327")
	@Category(JDLCompliantTest.class) @Test public void test_at_327_4_1_NotifyPeersRecoveryWithAddedJob() throws Exception {
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
		
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		req_327_Util.notifyPeerRecovery(peer1, objectID1, broker);
		
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objectID2, broker);
	}

	/**
	 * This test contains the following steps:
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * 
	 * Call setPeers giving a list containing one peer with the following attributes: 
	 * * username = test * servername = servertest
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
	 * Call doNotifyRecovery passing a peer with username = test2
	 * 
	 * Verify if the following debug message was logged:
	 *     o Peer with object id: [X] is UP. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-327.5", reqs="REQ327")
	@Category(JDLCompliantTest.class) @Test public void test_at_327_5_1_NotifyPeersRecoveryWithBrokerLogged() throws Exception {
		//create and starts
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//add a job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs =  new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged
		DeploymentID objectID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), objectID, broker);
		
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub, jobs, jobIDs);
		
		//set two peers
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(getPeerSpec());
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
				
		//verify the remote objects
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(getPeerSpec(), "publicKey1",PeerConstants.LOCAL_WORKER_PROVIDER),
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(peer2, "publicKey2", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
		
		//notify again
		req_327_Util.notifyPeerRecovery(peer2, objectID, broker);
	}

	/**
	 * This test contains the following steps:
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * 
	 * Call setPeers giving a list containing one peer with the following attributes: 
	 * * username = test * servername = servertest
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
	 * Call doNotifyRecovery passing a peer with username = test2
	 * 
	 * Verify if the following debug message was logged:
	 *     o Peer with object id: [X] is UP. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-327.6", reqs="REQ327")
	@Category(JDLCompliantTest.class) @Test public void test_at_327_6_1_NotifyPeersRecoveryWithBrokerLoggedAndAddedJobs() throws Exception {
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
	
		//set two peers
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(peer1);
		peers.add(peer2);
	
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
				
		//add a job
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs =  new ArrayList<Integer>();
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		jobs.add(testJob.getJobSpec());
		jobIDs.add(1);
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, objectID1, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub, jobs, jobIDs);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objectID2, broker);
	}

	/**
	* Create and start the Broker with the correct public key;
	* Call setPeers giving a list containing one peer with the following attributes:
	      o First peer = username = test and servername = servertest
	      o Second peer = username = test2 and servername = servertest2
	* Verify if the operation returned an empty Control Operation Result;
	* Call doNotifyRecovery passing a peer with username = test on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is UP. Where X is the objectID generated.
	* Do login with the public key property set to "publicKey1"in the worker provider.
	* Add a job with the attributes: label: "Test Job" and one Task with remote 
	* 	attribute "echo Hello World"
	* Verify if the operation result contains a jobID with value 1.
	* Add a job with the attributes: label: "Test Job 2" and one Task with remote 
	* 	attribute "echo Hello World 2"
	* Verify if the operation result contains a jobID with value 2.
	* Call hereIsWorker giving a worker with public key "workerPublicKey" and the 
	* 	request ID generated.
	* Call doNotifyRecovery passing a peer with username = test2 on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is UP. Where X is the objectID generated.
	      
	 * @throws Exception
	 */
	@ReqTest(test="AT-327.7", reqs="REQ327")
	@Category(JDLCompliantTest.class) @Test public void test_at_327_7_1_PeerRecovery() throws Exception{
		//create a broker and set peers
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		List<PeerSpecification> peersSpecs = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peersSpecs.add(peer1);
		peersSpecs.add(peer2);
	
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID1 = req_328_Util.createPeerDeploymentID("publickey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, objID1, broker);
		
		//do login with peer1
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		//add the second Job
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//call here is worker
	
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, "publickey1", peerTestStub, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call doNotifyRecovery passing a peer with username test2
		DeploymentID objID2 = req_328_Util.createPeerDeploymentID("publickey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objID2, broker);
	}

	/**
	* Create and start the Broker with the correct public key;
	* Call setPeers giving a list containing one peer with the following attributes:
	      o First peer = username = test and servername = servertest
	      o Second peer = username = test2 and servername = servertest2
	* Call doNotifyRecovery passing a peer with username = test on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is UP. Where X is the objectID generated.
	* Do login with the public key property set to "publicKey1"in the worker provider.
	* Add a job with the attributes: label: "Test Job" and one Task with remote 
	* 	attribute "echo Hello World"
	* Verify if the operation result contains a jobID with value 1.
	* Add a job with the attributes: label: "Test Job 2" and one Task with remote 
	* 	attribute "echo Hello World 2"
	* Verify if the operation result contains a jobID with value 2.
	* Call hereIsWorker giving a worker with public key "workerPublicKey" and the 
	* 	request ID generated.
	* Call schedule with the correct public key;
	* Verify if the worker's startWork message was called;
	* Call doNotifyRecovery passing a peer with username = test2 on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is UP. Where X is the objectID generated.
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-327.8", reqs="REQ327")
	@Category(JDLCompliantTest.class) @Test public void test_at_327_8_1_PeerRecovery() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//set two peers
		List<PeerSpecification> peersSpecs = new ArrayList<PeerSpecification>();
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peersSpecs.add(peer1);
		peersSpecs.add(peer2);
	
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID1 = req_328_Util.createPeerDeploymentID("publickey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, objID1, broker);
		
		//do login with peer1
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		//add the second Job
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//call here is worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, 
				"publickey1", peerTestStub, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		workerTestStubs.add(workerTestStub);
		
		//call Schedule with the correct public key
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		//call doNotifyRecovery passing a peer with username test2
		DeploymentID objID2 = req_328_Util.createPeerDeploymentID("publickey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objID2, broker);
	}
}
