package org.ourgrid.acceptance.broker;

import static org.junit.Assert.assertFalse;
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
import org.ourgrid.broker.communication.receiver.LocalWorkerProviderClientReceiver;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
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

@ReqTest(reqs="REQ309")
public class Req_309_Test extends BrokerAcceptanceTestCase {

	private Req_301_Util req_301_Util;
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
	private static final String PROPERTIES_FILENAME_3_PEERS = BROKER_TEST_DIR + "broker3peers.properties";

	
	@Override
	public void setUp() throws Exception {
		req_301_Util = new Req_301_Util(createComponentContext());
		req_302_Util = new Req_302_Util(createComponentContext());
		req_304_Util = new Req_304_Util(createComponentContext());
		req_309_Util = new Req_309_Util(createComponentContext());
		req_311_Util = new Req_311_Util(createComponentContext());
		req_312_Util = new Req_312_Util(createComponentContext());
		req_327_Util = new Req_327_Util(createComponentContext());
		req_328_Util = new Req_328_Util(createComponentContext());
		req_329_Util = new Req_329_Util(createComponentContext());
		req_330_Util = new Req_330_Util(createComponentContext());
		peerUserAtServer = "test@servertest";
	}
	
	private TestContext createComponentContext_3_peers() {
		return new TestContext(
				new BrokerComponentContextFactory(
						new PropertiesFileParser(PROPERTIES_FILENAME_3_PEERS
								)).createContext());
	}
	
	/**
	 * This test contains the following steps:
	 * 
     * Create a Broker with the public key property set to "publicKey1";
     * 
     * Call setPeers with an empty peers list;
     * 
     * Verify if the following error message was logged:
     *   1. BrokerComponent is not started.
	 * 
	 */
	@ReqTest(test="AT-309.1", reqs="REQ309")
	@Test public void test_at_309_1_SetPeersWithoutBrokerStart() throws Exception {
		req_301_Util.createBrokerModule();
	}
	
	/**
	 * This test contains the following steps:
	 * 
     * Create a Broker with the public key property set to "publicKey1";
     * 
     * Call setPeers giving a list containing one peer;
     * 
     * Verify if the following error message was logged:
     *   1. BrokerComponent is not started.
     * Verify if the Control Operation Result contains an exception with the message above.
	 */
	@ReqTest(test="AT-309.2", reqs="REQ309")
	@Test public void test_at_309_2_SetOnePeerWithoutBrokerStart() throws Exception {
		req_301_Util.createBrokerModule();
	}
	
	/**
	 *  This test contains the following steps:
	 *  *
     * Create a Broker with the public key property set to "publicKey1" and start the Broker;
     * 
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o username = test
     *     o servername = servertest
     *     
     * Verify if the operation returned an empty Control Operation Result;
     * 
     * Get(lookup) the remote object "PEER_MONITOR_OBJECT_NAME" and verify if its type is:
     *     o org.ourgrid.jic.EventProcessor.FailureInterestedEventProcessor
     *     
     * Verify if the remote object "PEER_MONITOR_OBJECT_NAME" was registered like an interested by the peer added.
	 */
	@ReqTest(test="AT-309.3", reqs="REQ309")
	@Test public void test_at_309_3_StartBrokerAndSetOnePeer() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		assertTrue(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, LocalWorkerProviderClientReceiver.class));
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(getPeerSpec(), "publicKey1", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
	}
	
	/**
	 * Create a Broker with the public key property set to "publicKey1"
     * Start the Broker with the correct public key;
     * Add a job with the attributes: label: "Test Job" and one Task with remote attribute "echo Hello World"
     * Add another job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World again"
     * Call setPeers giving a list containing one peer with the following attributes:
     *     o username = test
     *     o servername = servertest
     * Verify if the operation returned an empty Control Opera2tion Result;
     * Get(lookup) the remote object "PEER_MONITOR_OBJECT_NAME" and verify if its type is:
     *     o org.ourgrid.jic.EventProcessor.FailureInterestedEventProcessor
     * Verify if the remote object "PEER_MONITOR_OBJECT_NAME" was registered like an interested by the peer added.
	 */
	@ReqTest(test="AT-309.4", reqs="REQ309")
	@Test public void test_at_309_4_StartBrokerAddJobsAndSetPeers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job");
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2");
		assertTrue(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, LocalWorkerProviderClientReceiver.class));
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(getPeerSpec(), "publicKey1", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
	}

	/**
	 * Create a Broker with the public key property set to "publicKey1" and start the Broker;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *       username = test
	 *       servername = servertest
	 * Verify if the operation returned an empty Control Operation Result;
	 * Get(lookup) the remote object "PEER_MONITOR_OBJECT_NAME" and verify if its type is:
	 *       org.ourgrid.jic.EventProcessor.FailureInterestedEventProcessor
	 * Verify if the remote object "PEER_MONITOR_OBJECT_NAME" was registered like an interested by the peer added.
	 * Call again setPeers giving a list containing two peer with the following attributes:
	 *       First peer = username = test2 and servername = servertest2
	 *       Second peer = username = test3 and servername = servertest3
	 * Verify if the operation returned an empty Control Operation Result;
	 * Verify if the remote object "PEER_MONITOR_OBJECT_NAME" was registered like an interested by the peer with servername = servertest2
	 * Verify if the remote object "PEER_MONITOR_OBJECT_NAME" was registered like an interested by the peer with servername = servertest3
	 * Verify if the remote object "PEER_MONITOR_OBJECT_NAME" was unregistered like an interested by the peer with servername = servertest
	 *
	 */
	@ReqTest(test="AT-309.5", reqs="REQ309")
	@Test public void test_at_309_5_SetNewPeers() throws Exception {

		req_302_Util = new Req_302_Util(createComponentContext_3_peers());
		List<String> peersUserAtServer = new LinkedList<String>();
		
		peersUserAtServer.add(peerUserAtServer);
		peersUserAtServer.add("test2@servertest2");
		peersUserAtServer.add("test3@servertest3");

		BrokerServerModule broker = req_302_Util.startBroker(peersUserAtServer);
		
		PeerSpecification peer1 = req_309_Util.createPeerSpec("test", "servertest");
		PeerSpecification peer2 = req_309_Util.createPeerSpec("test2", "servertest2");
		PeerSpecification peer3 = req_309_Util.createPeerSpec("test3", "servertest3");
		
		assertTrue(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, LocalWorkerProviderClientReceiver.class));
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(peer1, "publicKey1", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
		
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(peer2, "publicKey2", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(peer3, "publicKey3", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(peer1, "publicKey1", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
	}
	
		
	@ReqTest(test="AT-309.4", reqs="REQ309")
	@Category(JDLCompliantTest.class) @Test public void test_at_309_4_1_StartBrokerAddJobsAndSetPeers() throws Exception {
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, new ArrayList<LocalWorkerProvider>());
		assertTrue(isBound(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT, LocalWorkerProviderClientReceiver.class));
		assertTrue(AcceptanceTestUtil.isInterested(broker, req_309_Util.createServiceID(getPeerSpec(), "publicKey1", PeerConstants.LOCAL_WORKER_PROVIDER), 
				getBoundDeploymentID(broker, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)));
	}
}
