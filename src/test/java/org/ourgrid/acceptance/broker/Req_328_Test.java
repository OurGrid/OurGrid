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

@ReqTest(reqs="REQ328")
public class Req_328_Test extends BrokerAcceptanceTestCase {
	
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
     * Call doNotifyFailure passing a peer with the attribute above on the parameter;
     * 
     * Verify if the following warn message was logged:
     *     1. The Peer Entry with entity id: [X] is already down. Where X is the entity ID of the Peer.
	 */
	@ReqTest(test="AT-328.1", reqs="REQ328")
	@Test public void test_at_328_1_NotifyPeerFailure() throws Exception {
		//create and start the broker
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged
		req_328_Util.notifyPeerFailure(getPeerSpec(), objectID1, brokerComponent, true);
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
     * Call doNotifyFailure passing a peer with the attribute above on the parameter;
     * 
     * Verify if the following warn message was logged:
     *     1. The Peer Entry with object id: [X] is already down. Where X is the object ID of the Peer.
	 */
	@ReqTest(test="AT-328.2", reqs="REQ328")
	@Test public void test_at_328_2_NotifyPeerFailureWithJobAdded() throws Exception {
		//create and start the broker
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		
		//add a job
		req_304_Util.addJob(true, 1, brokerComponent, "echo Hello World", "Test Job");
		
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged
		req_328_Util.notifyPeerFailure(getPeerSpec(), objectID1, brokerComponent, true);
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
     *    
     * Call doNotifyFailure passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-328.3", reqs="REQ328")
	@Test public void test_at_328_3_NotifyPeerFailureWithPeersUp() throws Exception {
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
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		req_327_Util.notifyPeerRecovery(peer1, objectID1, broker);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objectID2, broker);
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(peer1, objectID1, broker, false);
	}
	
	/**
	 * This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing the peers with the following attributes:
     *     o First: username = test and servername = servertest
     *     
     * Call doNotifyRecovery passing a peer with the username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is UP. Where X is the objectID generated.
     *    
     * Call doNotifyFailure passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is DOWN. Where X is the objectID generated.
     *    
     * Verify if the following remote object is NOT bound:
     *     o LOCAL_WORKER_PROVIDER_CLIENT
	 */
	@ReqTest(test="AT-328.4", reqs="REQ328")
	@Test public void test_at_328_4_NotifyLastPeerFailure() throws Exception {
		//creates and starts
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID objectID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		req_327_Util.notifyPeerRecovery(getPeerSpec(), objectID, brokerComponent);
		
		//notify failure
		req_328_Util.notifyPeerFailure(getPeerSpec(), objectID, brokerComponent, false);
		
		//verify if the remote object is not bound
//		assertFalse(isBound(brokerComponent, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
//				LocalWorkerProviderClient.class));
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
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"    
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
     *    
     * Call doNotifyFailure passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *    1. Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-328.5", reqs="REQ328")
	@Test public void test_at_328_5_NotifyPeerFailureWithPeersUpAndAddedJob() throws Exception {
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
		
		//add a job
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		req_327_Util.notifyPeerRecovery(peer1, objectID1, broker);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objectID2, broker);
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(peer1, objectID1, broker, false);
	}
	

	/**
  	 * This test contains the following steps:
  	 * 
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing the peers with the following attributes:
     *      o First: username = test and servername = servertest
     *      o Second: username = test2 and servername = servertest2
     *      
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * 
     * Call doNotifyRecovery passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     1. Peer with object id: [X] is UP. Where X is the objectID generated. 
     *     
     * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     1. Peer with object id: [X] is UP. Where X is the objectID generated.
     *     
     * Call doNotifyFailure passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     1. Peer with object id: [X] is DOWN. Where X is the objectID generated.
     *     
     * Call doNotifyFailure passing a peer with username = test2 on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     1. Peer with object id: [X] is DOWN. Where X is the objectID generated.
     *     
     * Verify if the following remote object is NOT bound:
     *      o LOCAL_WORKER_PROVIDER_CLIENT
	 */
	@ReqTest(test="AT-328.6", reqs="REQ328")
	@Test public void test_at_328_6_NotifyPeerFailureWithPeersUpAndAddedJob() throws Exception {
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
		
		//add a job
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		req_327_Util.notifyPeerRecovery(peer1, objectID1, broker);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objectID2, broker);
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(peer1, objectID1, broker, false);
		
		//notify failure for peer2
		req_328_Util.notifyPeerFailure(peer2, objectID2, broker, false);
		
		//verify if the remote object is not bound
//		assertFalse(isBound(brokerComponent, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
//				LocalWorkerProviderClient.class));
	}
	
	/**
  	 * This test contains the following steps:
	 *
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
     *     
     * Do login with the public key property set to "publicKey2" in the second peer.
     * 
     * Call doNotifyFailure passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     o The Peer Entry with entity id: [X] is already down. Where X is the entity ID of the Peer.
	 */
	@ReqTest(test="AT-328.7", reqs="REQ328")
	@Test public void test_at_328_7_SetMoreThanOnePeerAndNotifyPeerFailure() throws Exception {
		//create and starts
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), objectID1, broker);
		
		//try to login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//set two peers
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		peers.add(getPeerSpec());
		peers.add(peer2);
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		//verify the remote objects
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(getPeerSpec(), "publicKey1", 
				PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(peer2, "publicKey2", 
				PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
		
		////notify and verify if the debug message was logged for peer2
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub peerTestStub2 = req_327_Util.notifyPeerRecovery(peer2, objectID2, broker);
		
		//try to login on provider2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, peerTestStub2);
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(getPeerSpec(), objectID1, broker, false);
	}
	
	/**
	 * This test contains the following steps:
	 *
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
     * Call doNotifyFailure passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is DOWN. Where X is the objectID generated.
     *     
     * Verify if the following remote object is NOT bound:
     *     o LOCAL_WORKER_PROVIDER_CLIENT
	 */
	@ReqTest(test="AT-328.8", reqs="REQ328")
	@Test public void test_at_328_8_LogInAPeerAndNotifyPeerFailure() throws Exception {
		//creates andd starts
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID objectID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), objectID, brokerComponent);
		
		//try to login
		req_311_Util.verifyLogin(brokerComponent, "publicKey1", false, false, null, peerTestStub);
		
		//notify failure
		req_328_Util.notifyPeerFailure(getPeerSpec(), objectID, brokerComponent, false);
		
		//verify if the remote object is not bound
//		assertFalse(isBound(brokerComponent, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
//				LocalWorkerProviderClient.class));
	}
	
	/**
	 *This test contains the following steps:
	 *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o First peer = username = test and servername = servertest
     *     o Second peer = username = test2 and servername = servertest2
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
     * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is UP. Where X is the objectID generated.
     *     
     * Do login with the public key property set to "publicKey2"in the worker provider.
     * 
     * Call doNotifyFailure passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-328.9", reqs="REQ328")
	@Test public void test_at_328_9_LogInAPeerAddJobsAndNotifyPeerFailure() throws Exception {
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
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, objectID1, broker);
		
		//try to login on provider
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peersStub = new ArrayList<LocalWorkerProvider>();
		peersStub.add((LocalWorkerProvider) peerTestStub.getObject());
		
		//add jobs
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peersStub);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peersStub);
		jobs.add(testJob1.getJobSpec());
		jobIDs.add(1);
		jobs.add(testJob2.getJobSpec());
		jobIDs.add(2);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub peerTestStub2 = req_327_Util.notifyPeerRecovery(peer1, objectID2, broker);
		
		//try to login on provider2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, peerTestStub2, jobs, jobIDs);
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(peer1, objectID1, broker, false);
	}
	
	/**
	 * This test contains the following steps:
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
     * Call doNotifyFailure passing a peer with username = test on the parameter;
     * 
     * Verify if the following debug message was logged:
     *     o Peer with object id: [X] is DOWN. Where X is the objectID generated.
     *     
     * Verify if the following remote object is NOT bound:
     *     o LOCAL_WORKER_PROVIDER_CLIENT
	 */
	@ReqTest(test="AT-328.10", reqs="REQ328")
	@Test public void test_at_328_10_LogInAPeerAddJobsAndNotifyLastPeerFailure() throws Exception {
		//creates and starts
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID objectID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), objectID, brokerComponent);
		
		List<LocalWorkerProvider> peersStub = new ArrayList<LocalWorkerProvider>();
		peersStub.add((LocalWorkerProvider) peerTestStub.getObject());
		
		//try to login
		req_311_Util.verifyLogin(brokerComponent, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		req_304_Util.addJob(true, 1, brokerComponent, "echo Hello World", "Test Job", peersStub);
		req_304_Util.addJob(true, 2, brokerComponent, "echo Hello World2", "Test Job2", peersStub);
		
		//notify failure
		req_328_Util.notifyPeerFailure(getPeerSpec(), objectID, brokerComponent, false);
		
		//verify if the remote object is not bound
//		assertFalse(isBound(brokerComponent, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
//				LocalWorkerProviderClient.class));
	}
	
	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *          First peer = username = test and servername = servertest
	 *          Second peer = username = test2 and servername = servertest2
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *          Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
	 * Verify if the following debug message was logged:
	 *          Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey2"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated from the peer with username = test;
	 * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated from the peer with username = test2;
	 * Call doNotifyFailure passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *          Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey3" and the request ID generated from the peer with username = test2;
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated from the peer with username = test;
	 * Verify if the following debug message was logged:
	 *          The peer with public key [publicKey1], which is down, delivered a worker with public key: [workerPublicKey].
	 *
	 */
	@ReqTest(test="AT-328.11", reqs="REQ328")
	@Test public void test_at_328_11_PeerFailureWithLoggedBrokerAndWorker() throws Exception {
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
		
		DeploymentID peer1DeploymentID = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		DeploymentID peer2DeploymentID = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		
		//notify and verify if the debug message was logged for peer1
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, peer1DeploymentID, broker);
		
		//try to login on provider1
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);

		//notify and verify if the debug message was logged for peer2
		TestStub peerTestStub2 = req_327_Util.notifyPeerRecovery(peer2, peer2DeploymentID, broker);
		
		//try to login on provider2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, peerTestStub2);
		
		List<LocalWorkerProvider> peersStub = new ArrayList<LocalWorkerProvider>();
		peersStub.add((LocalWorkerProvider) peerTestStub.getObject());
		peersStub.add((LocalWorkerProvider) peerTestStub2.getObject());

		//add jobs
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", peersStub);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, "echo Hello World2", "Test Job2", peersStub);
		
		//try to receive workers of both peers
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), 
				"publickey1", peerTestStub, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub1.getDeploymentID());
		Map<String, String> attributes2 = new HashMap<String, String>();
		attributes2.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes2.put(OurGridSpecificationConstants.ATT_USERNAME, "username2");
		TestStub workerTestStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, new WorkerSpecification(attributes2), 
				"publickey1", peerTestStub, testJob2);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub2.getDeploymentID());
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(peer1, peer1DeploymentID, broker, false);
		
		//try to receive workers of both peers again
		Map<String, String> attributes3 = new HashMap<String, String>();
		attributes3.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes3.put(OurGridSpecificationConstants.ATT_USERNAME, "username3");
		TestStub workerTestStub3 = req_312_Util.receiveWorker(broker, "workerPublicKey3", true, false, false, true, new WorkerSpecification(attributes3), 
				"publickey1", peerTestStub, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub3.getDeploymentID());
		Map<String, String> attributes4 = new HashMap<String, String>();
		attributes4.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes4.put(OurGridSpecificationConstants.ATT_USERNAME, "username4");
		workerTestStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, false, false, true, new WorkerSpecification(attributes4), 
				"publickey1", peerTestStub, testJob2);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub1.getDeploymentID());
	}

	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *        First peer = username = test and servername = servertest
	 *        Second peer = username = test2 and servername = servertest2
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey2"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated from the peer with username = test;
	 * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated from the peer with username = test2;
	 * Call doNotifyFailure passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 * Call doNotifyFailure passing a peer with username = test2 on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated from the peer with username = test;
	 * Verify if the following debug message was logged:
	 *        The peer with public key [publicKey1], which is down, delivered a worker with public key: [workerPublicKey].
	 * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated from the peer with username = test2;
	 * Verify if the following debug message was logged:
	 *        The peer with public key [publicKey2], which is down, delivered a worker with public key: [workerPublicKey2].
	 * Verify if the following remote object is NOT bound:
	 *        LOCAL_WORKER_PROVIDER_CLIENT
	 *
	 */
	@ReqTest(test="AT-328.12", reqs="REQ328")
	@Test public void test_at_328_12_LatsPeerFailureWithLoggedBrokerAndWorker() throws Exception {
		//creates and starts
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule brokerComponent = req_302_Util.startBroker(peersUserAtServer);
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		DeploymentID peer1DeploymentID = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		DeploymentID peer2DeploymentID = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		
		//notify and verify if the debug message was logged for peer1
		TestStub peerTestStub1 = req_327_Util.notifyPeerRecovery(peer1, peer1DeploymentID, brokerComponent);
		
		//try to login on provider1
		req_311_Util.verifyLogin(brokerComponent, "publicKey1", false, false, null, peerTestStub1);

		//notify and verify if the debug message was logged for peer2
		TestStub peerTestStub2 = req_327_Util.notifyPeerRecovery(peer2, peer2DeploymentID, brokerComponent);
		
		//try to login on provider2
		req_311_Util.verifyLogin(brokerComponent, "publicKey2", false, false, null, peerTestStub2);
		
		List<LocalWorkerProvider> peersStub = new ArrayList<LocalWorkerProvider>();
		peersStub.add((LocalWorkerProvider) peerTestStub1.getObject());
		peersStub.add((LocalWorkerProvider) peerTestStub2.getObject());

		//add jobs
		TestJob testJob1 = req_304_Util.addJob(true, 1, brokerComponent, "echo Hello World", "Test Job", peersStub);
		TestJob testJob2 = req_304_Util.addJob(true, 2, brokerComponent, "echo Hello World2", "Test Job2", peersStub);
		
		//try to receive workers of both peers
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		Map<String, String> attributes2 = new HashMap<String, String>();
		attributes2.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes2.put(OurGridSpecificationConstants.ATT_USERNAME, "username2");
		TestStub workerStub1 = req_312_Util.receiveWorker(brokerComponent, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), 
				"publickey1", peerTestStub1, testJob1);
		req_330_Util.notifyWorkerRecovery(brokerComponent, workerStub1.getDeploymentID());
		TestStub workerStub2 = req_312_Util.receiveWorker(brokerComponent, "workerPublicKey2", true, true, true, true, new WorkerSpecification(attributes2), 
				"publickey2", peerTestStub2, testJob2);
		req_330_Util.notifyWorkerRecovery(brokerComponent, workerStub2.getDeploymentID());
		
		//notify failure for both peers
		req_328_Util.notifyPeerFailure(peer1, peer1DeploymentID, brokerComponent, false);
		req_328_Util.notifyPeerFailure(peer2, peer2DeploymentID, brokerComponent, false);
		
		//try to receive workers of both peers again
//		workerStub1 = req_312_Util.receiveWorker(brokerComponent, "workerPublicKey", false, false, false, true, new WorkerSpec(attributes), 
//				"publickey1", peerTestStub1, testJob1);
		workerStub1 = req_312_Util.receiveWorker(brokerComponent, "workerPublicKey", true, false, false, true, new WorkerSpecification(attributes), 
				"publickey1", peerTestStub1, testJob1);
		
		req_330_Util.notifyWorkerRecovery(brokerComponent, workerStub1.getDeploymentID());
//		workerStub2 = req_312_Util.receiveWorker(brokerComponent, "workerPublicKey2", false, false, false, true, new WorkerSpec(attributes2), 
//				"publickey2", peerTestStub2, testJob2);
		workerStub2 = req_312_Util.receiveWorker(brokerComponent, "workerPublicKey2", true, false, false, true, new WorkerSpecification(attributes2), 
				"publickey2", peerTestStub2, testJob2);
		req_330_Util.notifyWorkerRecovery(brokerComponent, workerStub2.getDeploymentID());
		
		//verify if the remote object is not bound
//		assertFalse(isBound(brokerComponent, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
//				LocalWorkerProviderClient.class));
	}

	
	/**
	* Create and start the Broker with the correct public key;
    * Call setPeers giving a list containing one peer with the following attributes:
          o First peer = username = test and servername = servertest
          o Second peer = username = test2 and servername = servertest2
    * Call doNotifyRecovery passing a peer with username = test on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is UP. Where X is the objectID generated.
    * Do login with the public key property set to "publicKey1"in the worker provider;
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
    * Do login with the public key property set to "publicKey2" in the worker provider;
    * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the 
    * 	request ID generated from peer with username = test and job 2;
    * Call schedule with the correct public key;
    * Verify if the worker's startWork message was called;
    * Call doNotifyFailure passing a peer with username = test on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is DOWN. Where X is the objectID generated.=
    * Call doNotifyFailure passing a peer with username = test2 on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is DOWN. Where X is the objectID generated.=
    * Call hereIsWorker giving a worker with public key "workerPublicKey2" and  
    * 	the request ID generated from peer with username = test and job 2;
    * Verify if the following warn message was logged:
          o The peer with public key [publicKey2], which is down, delivered a 
          	worker with public key: [workerPublicKey2].
    * Verify if the following remote object is NOT bound:
          o LOCAL_WORKER_PROVIDER_CLIENT

	 * @throws Exception
	 */
	@ReqTest(test="AT-328.13", reqs="REQ328")
	@Test public void test_at_328_13_PeerFailure() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//set two peers
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID1 = req_328_Util.createPeerDeploymentID("publickey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, objID1, broker);
		
		//do login with peer1
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peerStubs = new LinkedList<LocalWorkerProvider>();
		peerStubs.add((LocalWorkerProvider)peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello Word", "Test Job", peerStubs);
		//add the second Job
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, "echo Hello Word 2", "Test Job 2", peerStubs);
		
		//call here is worker
		Map<String, String> attributes1 = new HashMap<String, String>();
		attributes1.put(OurGridSpecificationConstants.ATT_USERNAME, "worker1");
		attributes1.put(OurGridSpecificationConstants.ATT_SERVERNAME, "serverr1");
		
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		TestStub workerTestStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes1), 
				"publickey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub1.getDeploymentID());
		
		workerTestStubs.add(workerTestStub1);
		
		//call Schedule with the correct public key
		List<TestJob> jobs = new ArrayList<TestJob>();
		jobs.add(testJob1);
		
		List<GridProcessHandle> handles = new ArrayList<GridProcessHandle>();
		handles.add(new GridProcessHandle(1, 1, 1));
		
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(peerTestStub);
		
		req_329_Util.doSchedule(broker, workerTestStubs, stubs, jobs, handles); 
		
		//call doNotifyRecovery passing a peer with username test2
		DeploymentID objID2 = req_328_Util.createPeerDeploymentID("publickey2", peer2);
		TestStub peerTestStub2 = req_327_Util.notifyPeerRecovery(peer2, objID2, broker);
		
		//do login with peer2
		List<JobSpecification> specs = new ArrayList<JobSpecification>();
		specs.add(testJob2.getJobSpec());
		
		List<Integer> jobIDs = new ArrayList<Integer>();
		jobIDs.add(2);
		
		req_311_Util.verifyLogin(broker, "publickey2", false, false, null, peerTestStub2, specs, jobIDs);
		
		/*call here is worker with the request ID generated from peer with username test 
		and job 2*/
		Map<String, String> attributes2 = new HashMap<String, String>();
		attributes2.put(OurGridSpecificationConstants.ATT_USERNAME, "worker2");
		attributes2.put(OurGridSpecificationConstants.ATT_SERVERNAME, "serverr2");
		
		workerTestStubs.clear();
		TestStub workerTestStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, new WorkerSpecification(attributes2), 
				"publickey1", peerTestStub, testJob2);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub2.getDeploymentID());
		
		workerTestStubs.add(workerTestStub2);
		
		//call schedule with the correct public key
		jobs.clear();
		jobs.add(testJob2);
		
		handles = new ArrayList<GridProcessHandle>();
		handles.add(new GridProcessHandle(2, 1, 1));
		
		stubs.add(peerTestStub2);
		
		req_329_Util.doSchedule(broker, workerTestStubs, stubs, jobs, handles); 
		
		//call notifyFailure to peer1
		req_328_Util.notifyPeerFailure(peer1, objID1, broker, false);
		
		//call notifyFailure to peer2
		req_328_Util.notifyPeerFailure(peer2, objID2, broker, false);
		
		/*call here is worker with the request ID generated from peer with username test 
		and job 2*/
		workerTestStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, false, false, true, 
				new WorkerSpecification(), "publickey1", peerTestStub, testJob2);
		
	}

	/**
	 * This test contains the following steps:
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
	 * Call doNotifyFailure passing a peer with username = test on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *     o Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 *     
	 * Verify if the following remote object is NOT bound:
	 *     o LOCAL_WORKER_PROVIDER_CLIENT
	 */
	@ReqTest(test="AT-328.10", reqs="REQ328")
	@Category(JDLCompliantTest.class) @Test public void test_at_328_10_1_LogInAPeerAddJobsAndNotifyLastPeerFailure() throws Exception {
		//creates and starts
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID objectID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), objectID, brokerComponent);
		
		List<LocalWorkerProvider> peersStub = new ArrayList<LocalWorkerProvider>();
		peersStub.add((LocalWorkerProvider) peerTestStub.getObject());
		
		//try to login
		req_311_Util.verifyLogin(brokerComponent, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		req_304_Util.addJob(true, 1, brokerComponent, JDLUtils.ECHO_JOB, peersStub);
		req_304_Util.addJob(true, 2, brokerComponent, JDLUtils.ECHO_JOB, peersStub);
		
		//notify failure
		req_328_Util.notifyPeerFailure(getPeerSpec(), objectID, brokerComponent, false);
		
		//verify if the remote object is not bound
//		assertFalse(isBound(brokerComponent, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
//				LocalWorkerProviderClient.class));
	}

	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *          First peer = username = test and servername = servertest
	 *          Second peer = username = test2 and servername = servertest2
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *          Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
	 * Verify if the following debug message was logged:
	 *          Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey2"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated from the peer with username = test;
	 * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated from the peer with username = test2;
	 * Call doNotifyFailure passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *          Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey3" and the request ID generated from the peer with username = test2;
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated from the peer with username = test;
	 * Verify if the following debug message was logged:
	 *          The peer with public key [publicKey1], which is down, delivered a worker with public key: [workerPublicKey].
	 *
	 */
	@ReqTest(test="AT-328.11", reqs="REQ328")
	@Category(JDLCompliantTest.class) @Test public void test_at_328_11_1_PeerFailureWithLoggedBrokerAndWorker() throws Exception {
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
		
		DeploymentID peer1DeploymentID = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		DeploymentID peer2DeploymentID = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		
		//notify and verify if the debug message was logged for peer1
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, peer1DeploymentID, broker);
		
		//try to login on provider1
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
	
		//notify and verify if the debug message was logged for peer2
		TestStub peerTestStub2 = req_327_Util.notifyPeerRecovery(peer2, peer2DeploymentID, broker);
		
		//try to login on provider2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, peerTestStub2);
		
		List<LocalWorkerProvider> peersStub = new ArrayList<LocalWorkerProvider>();
		peersStub.add((LocalWorkerProvider) peerTestStub.getObject());
		peersStub.add((LocalWorkerProvider) peerTestStub2.getObject());
	
		//add jobs
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peersStub);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peersStub);
		
		//try to receive workers of both peers
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE );
		WorkerSpecification workerSpec = specs.get( 0 );
		TestStub workerTestStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, 
				"publickey1", peerTestStub, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub1.getDeploymentID());
		workerSpec = specs.get( 1 );
		TestStub workerTestStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, workerSpec, 
				"publickey1", peerTestStub, testJob2);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub2.getDeploymentID());
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(peer1, peer1DeploymentID, broker, false);
		
		//try to receive workers of both peers again
		workerSpec = specs.get( 2 );
		TestStub workerTestStub3 = req_312_Util.receiveWorker(broker, "workerPublicKey3", true, false, false, true, workerSpec,  
				"publickey1", peerTestStub, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub3.getDeploymentID());
		workerSpec = specs.get( 3 );
		workerTestStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, false, false, true, workerSpec,  
				"publickey1", peerTestStub, testJob2);
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub1.getDeploymentID());
	}

	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *        First peer = username = test and servername = servertest
	 *        Second peer = username = test2 and servername = servertest2
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey2"in the worker provider.
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated from the peer with username = test;
	 * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated from the peer with username = test2;
	 * Call doNotifyFailure passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 * Call doNotifyFailure passing a peer with username = test2 on the parameter;
	 * Verify if the following debug message was logged:
	 *        Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 * Call hereIsWorker giving a worker with public key "workerPublicKey" and the request ID generated from the peer with username = test;
	 * Verify if the following debug message was logged:
	 *        The peer with public key [publicKey1], which is down, delivered a worker with public key: [workerPublicKey].
	 * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the request ID generated from the peer with username = test2;
	 * Verify if the following debug message was logged:
	 *        The peer with public key [publicKey2], which is down, delivered a worker with public key: [workerPublicKey2].
	 * Verify if the following remote object is NOT bound:
	 *        LOCAL_WORKER_PROVIDER_CLIENT
	 *
	 */
	@ReqTest(test="AT-328.12", reqs="REQ328")
	@Category(JDLCompliantTest.class) @Test public void test_at_328_12_1_LatsPeerFailureWithLoggedBrokerAndWorker() throws Exception {
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
		
		DeploymentID peer1DeploymentID = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		DeploymentID peer2DeploymentID = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		
		//notify and verify if the debug message was logged for peer1
		TestStub peerTestStub1 = req_327_Util.notifyPeerRecovery(peer1, peer1DeploymentID, broker);
		
		//try to login on provider1
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub1);
	
		//notify and verify if the debug message was logged for peer2
		TestStub peerTestStub2 = req_327_Util.notifyPeerRecovery(peer2, peer2DeploymentID, broker);
		
		//try to login on provider2
		req_311_Util.verifyLogin(broker, "publicKey2", false, false, null, peerTestStub2);
		
		List<LocalWorkerProvider> peersStub = new ArrayList<LocalWorkerProvider>();
		peersStub.add((LocalWorkerProvider) peerTestStub1.getObject());
		peersStub.add((LocalWorkerProvider) peerTestStub2.getObject());
	
		//add jobs
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peersStub);
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peersStub);
		
		//try to receive workers of both peers
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE );
		TestStub workerStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, specs.get( 0 ), 
				"publickey1", peerTestStub1, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerStub1.getDeploymentID());
		TestStub workerStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, specs.get( 1 ), 
				"publickey2", peerTestStub2, testJob2);
		req_330_Util.notifyWorkerRecovery(broker, workerStub2.getDeploymentID());
		
		
		//notify failure for both peers
		req_328_Util.notifyPeerFailure(peer1, peer1DeploymentID, broker, false);
		req_328_Util.notifyPeerFailure(peer2, peer2DeploymentID, broker, false);
		
		//try to receive workers of both peers again
//		workerStub1 = req_312_Util.receiveWorker(brokerComponent, "workerPublicKey", false, false, false, true, specs.get( 0 ),
//				"publickey1", peerTestStub1, testJob1);
		workerStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, false, false, true, specs.get( 0 ),
				"publickey1", peerTestStub1, testJob1);
		req_330_Util.notifyWorkerRecovery(broker, workerStub1.getDeploymentID());
//		workerStub2 = req_312_Util.receiveWorker(brokerComponent, "workerPublicKey2", false, false, false, true, specs.get( 1 ),  
//				"publickey2", peerTestStub2, testJob2);
		workerStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, false, false, true, specs.get( 1 ),
				"publickey2", peerTestStub2, testJob2);
		req_330_Util.notifyWorkerRecovery(broker, workerStub2.getDeploymentID());
		
		//verify if the remote object is not bound
//		assertFalse(isBound(brokerComponent, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
//				LocalWorkerProviderClient.class));
	}

	/**
	* Create and start the Broker with the correct public key;
	* Call setPeers giving a list containing one peer with the following attributes:
	      o First peer = username = test and servername = servertest
	      o Second peer = username = test2 and servername = servertest2
	* Call doNotifyRecovery passing a peer with username = test on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is UP. Where X is the objectID generated.
	* Do login with the public key property set to "publicKey1"in the worker provider;
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
	* Do login with the public key property set to "publicKey2" in the worker provider;
	* Call hereIsWorker giving a worker with public key "workerPublicKey2" and the 
	* 	request ID generated from peer with username = test and job 2;
	* Call schedule with the correct public key;
	* Verify if the worker's startWork message was called;
	* Call doNotifyFailure passing a peer with username = test on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is DOWN. Where X is the objectID generated.=
	* Call doNotifyFailure passing a peer with username = test2 on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is DOWN. Where X is the objectID generated.=
	* Call hereIsWorker giving a worker with public key "workerPublicKey2" and  
	* 	the request ID generated from peer with username = test and job 2;
	* Verify if the following warn message was logged:
	      o The peer with public key [publicKey2], which is down, delivered a 
	      	worker with public key: [workerPublicKey2].
	* Verify if the following remote object is NOT bound:
	      o LOCAL_WORKER_PROVIDER_CLIENT
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-328.13", reqs="REQ328")
	@Category(JDLCompliantTest.class) @Test public void test_at_328_13_1_PeerFailure() throws Exception{
		//create and start the broker
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		//set two peers
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID1 = req_328_Util.createPeerDeploymentID("publickey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, objID1, broker);
		
		//do login with peer1
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peerStubs = new LinkedList<LocalWorkerProvider>();
		peerStubs.add((LocalWorkerProvider)peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peerStubs);
		//add the second Job
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peerStubs);
		
		//call here is worker
		List<WorkerSpecification> workerSpecs = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE );
		
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		TestStub workerTestStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpecs.get( 0 ), 
				"publickey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub1.getDeploymentID());
		
		workerTestStubs.add(workerTestStub1);
		
		//call Schedule with the correct public key
		List<TestJob> jobs = new ArrayList<TestJob>();
		jobs.add(testJob1);
		
		List<GridProcessHandle> handles = new ArrayList<GridProcessHandle>();
		handles.add(new GridProcessHandle(1, 1, 1));
		
		List<TestStub> stubs = new ArrayList<TestStub>();
		stubs.add(peerTestStub);
		
		req_329_Util.doSchedule(broker, workerTestStubs, stubs, jobs, handles); 
		
		//call doNotifyRecovery passing a peer with username test2
		DeploymentID objID2 = req_328_Util.createPeerDeploymentID("publickey2", peer2);
		TestStub peerTestStub2 = req_327_Util.notifyPeerRecovery(peer2, objID2, broker);
		
		//do login with peer2
		List<JobSpecification> specs = new ArrayList<JobSpecification>();
		specs.add(testJob2.getJobSpec());
		
		List<Integer> jobIDs = new ArrayList<Integer>();
		jobIDs.add(2);
		
		req_311_Util.verifyLogin(broker, "publickey2", false, false, null, peerTestStub2, specs, jobIDs);
		
		/*call here is worker with the request ID generated from peer with username test 
		and job 2*/
		
		workerTestStubs.clear();
		TestStub workerTestStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, workerSpecs.get( 1 ), 
				"publickey1", peerTestStub, testJob2);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub2.getDeploymentID());
		
		workerTestStubs.add(workerTestStub2);
		
		//call schedule with the correct public key
		jobs.clear();
		jobs.add(testJob2);
		
		handles = new ArrayList<GridProcessHandle>();
		handles.add(new GridProcessHandle(2, 1, 1));
		
		stubs.add(peerTestStub2);
		
		req_329_Util.doSchedule(broker, workerTestStubs, stubs, jobs, handles); 
		
		//call notifyFailure to peer1
		req_328_Util.notifyPeerFailure(peer1, objID1, broker, false);
		
		//call notifyFailure to peer2
		req_328_Util.notifyPeerFailure(peer2, objID2, broker, false);
		
		/*call here is worker with the request ID generated from peer with username test 
		and job 2*/
		workerTestStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", 
				true, false, false, true, new WorkerSpecification(), "publickey1", peerTestStub, testJob2);
		
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
	 * Call doNotifyFailure passing a peer with the attribute above on the parameter;
	 * 
	 * Verify if the following warn message was logged:
	 *     1. The Peer Entry with object id: [X] is already down. Where X is the object ID of the Peer.
	 */
	@ReqTest(test="AT-328.2", reqs="REQ328")
	@Category(JDLCompliantTest.class) @Test public void test_at_328_2_1_NotifyPeerFailureWithJobAdded() throws Exception {
		//create and start the broker
		BrokerServerModule brokerComponent = req_302_Util.startBroker(peerUserAtServer);
		
		//add a job
		req_304_Util.addJob(true, 1, brokerComponent, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		
		//notify and verify if the debug message was logged
		req_328_Util.notifyPeerFailure(getPeerSpec(), objectID1, brokerComponent, true);
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
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"    
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
	 *    
	 * Call doNotifyFailure passing a peer with username = test on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *    1. Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-328.5", reqs="REQ328")
	@Category(JDLCompliantTest.class) @Test public void test_at_328_5_1_NotifyPeerFailureWithPeersUpAndAddedJob() throws Exception {
		//creates and starts
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule brokerComponent = req_302_Util.startBroker(peersUserAtServer);
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		//add a job
		req_304_Util.addJob(true, 1, brokerComponent, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		req_327_Util.notifyPeerRecovery(peer1, objectID1, brokerComponent);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objectID2, brokerComponent);
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(peer1, objectID1, brokerComponent, false);
	}

	/**
	 * This test contains the following steps:
	 * 
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * 
	 * Call setPeers giving a list containing the peers with the following attributes:
	 *      o First: username = test and servername = servertest
	 *      o Second: username = test2 and servername = servertest2
	 *      
	 * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
	 * 
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *     1. Peer with object id: [X] is UP. Where X is the objectID generated. 
	 *     
	 * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *     1. Peer with object id: [X] is UP. Where X is the objectID generated.
	 *     
	 * Call doNotifyFailure passing a peer with username = test on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *     1. Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 *     
	 * Call doNotifyFailure passing a peer with username = test2 on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *     1. Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 *     
	 * Verify if the following remote object is NOT bound:
	 *      o LOCAL_WORKER_PROVIDER_CLIENT
	 */
	@ReqTest(test="AT-328.6", reqs="REQ328")
	@Category(JDLCompliantTest.class) @Test public void test_at_328_6_1_NotifyPeerFailureWithPeersUpAndAddedJob() throws Exception {
		//creates and starts
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule brokerComponent = req_302_Util.startBroker(peersUserAtServer);
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		//add a job
		req_304_Util.addJob(true, 1, brokerComponent, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		req_327_Util.notifyPeerRecovery(peer1, objectID1, brokerComponent);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		req_327_Util.notifyPeerRecovery(peer2, objectID2, brokerComponent);
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(peer1, objectID1, brokerComponent, false);
		
		//notify failure for peer2
		req_328_Util.notifyPeerFailure(peer2, objectID2, brokerComponent, false);
		
		//verify if the remote object is not bound
//		assertFalse(isBound(brokerComponent, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, 
//				LocalWorkerProviderClient.class));
		
	}

	/**
	 *This test contains the following steps:
	 *
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * 
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *     o First peer = username = test and servername = servertest
	 *     o Second peer = username = test2 and servername = servertest2
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
	 * Call doNotifyRecovery passing a peer with username = test2 on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *     o Peer with object id: [X] is UP. Where X is the objectID generated.
	 *     
	 * Do login with the public key property set to "publicKey2"in the worker provider.
	 * 
	 * Call doNotifyFailure passing a peer with username = test on the parameter;
	 * 
	 * Verify if the following debug message was logged:
	 *     o Peer with object id: [X] is DOWN. Where X is the objectID generated.
	 */
	@ReqTest(test="AT-328.9", reqs="REQ328")
	@Category(JDLCompliantTest.class) @Test public void test_at_328_9_1_LogInAPeerAddJobsAndNotifyPeerFailure() throws Exception {
		//creates and starts
		req_302_Util = new Req_302_Util(createComponentContext_2_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");

		BrokerServerModule brokerComponent = req_302_Util.startBroker(peersUserAtServer);
		List<PeerSpecification> peers = new ArrayList<PeerSpecification>();
		
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		
		peers.add(peer1);
		peers.add(peer2);
		
		List<String> publicKeys = new ArrayList<String>();
		publicKeys.add("publicKey1");
		publicKeys.add("publicKey2");
		
		//notify and verify if the debug message was logged for peer1
		DeploymentID objectID1 = req_328_Util.createPeerDeploymentID("publicKey1", peer1);
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(peer1, objectID1, brokerComponent);
		
		//try to login on provider
		req_311_Util.verifyLogin(brokerComponent, "publicKey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peersStub = new ArrayList<LocalWorkerProvider>();
		peersStub.add((LocalWorkerProvider) peerTestStub.getObject());
		
		//add jobs
		List<JobSpecification> jobs = new ArrayList<JobSpecification>();
		List<Integer> jobIDs = new ArrayList<Integer>();
		TestJob testJob1 = req_304_Util.addJob(true, 1, brokerComponent, JDLUtils.ECHO_JOB, peersStub);
		TestJob testJob2 = req_304_Util.addJob(true, 2, brokerComponent, JDLUtils.ECHO_JOB, peersStub);
		jobs.add(testJob1.getJobSpec());
		jobIDs.add(1);
		jobs.add(testJob2.getJobSpec());
		jobIDs.add(2);
		
		//notify and verify if the debug message was logged for peer2
		DeploymentID objectID2 = req_328_Util.createPeerDeploymentID("publicKey2", peer2);
		TestStub peerTestStub2 = req_327_Util.notifyPeerRecovery(peer1, objectID2, brokerComponent);
		
		//try to login on provider2
		req_311_Util.verifyLogin(brokerComponent, "publicKey2", false, false, null, peerTestStub2, jobs, jobIDs);
		
		//notify failure for peer1
		req_328_Util.notifyPeerFailure(peer1, objectID1, brokerComponent, false);
	}
}
