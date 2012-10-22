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
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ329")
public class Req_329_Test extends BrokerAcceptanceTestCase {

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

	
	/**
	* Create and start the Broker with the correct public key;
    * Call setPeers giving a list containing one peer with the following attributes:
          o First peer = username = test and servername = servertest
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

	 * @throws Exception
	 */
	@ReqTest(test="AT-329.1", reqs="REQ329")
	@Test public void test_at_329_1_ScheduleTests() throws Exception{
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID1 = req_328_Util.createPeerDeploymentID("publickey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), objID1, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello Word", "Test Job", peers);
		//add the second Job
		req_304_Util.addJob(true, 2, broker, "echo Hello Word 2", "Test Job 2", peers);
		
		Map<String, String> workerAttributes1 = new HashMap<String, String>();
		workerAttributes1.put(OurGridSpecificationConstants.ATT_USERNAME, "worker1");
		workerAttributes1.put(OurGridSpecificationConstants.ATT_SERVERNAME, "server1");
		
		//call here is worker
		List<TestStub> testStubs = new ArrayList<TestStub>();
		TestStub testStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(workerAttributes1), 
				"publickey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, testStub.getDeploymentID());
		
		testStubs.add(testStub);
		
		//call schedule with the correct public key
		req_329_Util.doSchedule(broker, testStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
	}
	
	/**
	* Create and start the Broker with the correct public key;
    * Call setPeers giving a list containing one peer with the following attributes:
          o First peer = username = test and servername = servertest
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
    * Call hereIsWorker giving a worker with public key "workerPublicKey2" and the 
    * 	request ID generated by job 2;
    * Call schedule with the correct public key;
    * Verify if the worker with public key = "workerPublicKey2" startWork message was called;

	 * @throws Exception
	 */
	@ReqTest(test="AT-329.2", reqs="REQ329")
	@Test public void test_at_329_2_ScheduleTests() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID = req_328_Util.createPeerDeploymentID("publickey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(),	objID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello Word", "Test Job", peers);
		//add the second Job
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, "echo Hello Word 2", "Test Job 2", peers);
		
		Map<String, String> workerAttributes1 = new HashMap<String, String>();
		workerAttributes1.put(OurGridSpecificationConstants.ATT_USERNAME, "worker1");
		workerAttributes1.put(OurGridSpecificationConstants.ATT_SERVERNAME, "server1");
		
		Map<String, String> workerAttributes2 = new HashMap<String, String>();
		workerAttributes2.put(OurGridSpecificationConstants.ATT_USERNAME, "worker2");
		workerAttributes2.put(OurGridSpecificationConstants.ATT_SERVERNAME, "server2");
		
		//call here is worker from peer with username test
		List<TestStub> testStubs = new ArrayList<TestStub>();
		TestStub testStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(workerAttributes1), 
				"publickey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, testStub1.getDeploymentID());
		
		testStubs.add(testStub1);
		
		//call schedule with the correct public key
		req_329_Util.doSchedule(broker, testStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		/*call here is worker with the request ID generated from peer with username test 
		and job 2*/
		testStubs.clear();
		TestStub testStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, new WorkerSpecification(workerAttributes2), 
				"publickey1", peerTestStub, testJob2);
		
		req_330_Util.notifyWorkerRecovery(broker, testStub2.getDeploymentID());
		
		testStubs.add(testStub2);
		
		List<TestJob> jobs = new ArrayList<TestJob>();
//		jobs.add(testJob1);
		jobs.add(testJob2);
		
		List<GridProcessHandle> handles = new ArrayList<GridProcessHandle>();
		handles.add(new GridProcessHandle(2, 1, 1));

		//call schedule with the correct public key
		List<TestStub> peerStubs = new ArrayList<TestStub>();
		peerStubs.add(peerTestStub);
		
		req_329_Util.doSchedule(broker, testStubs, peerStubs, jobs, handles); 
	}

	/**
	* Create and start the Broker with the correct public key;
	* Call setPeers giving a list containing one peer with the following attributes:
	      o First peer = username = test and servername = servertest
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
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-329.1", reqs="REQ329")
	@Category(JDLCompliantTest.class) @Test public void test_at_329_1_1_ScheduleTests() throws Exception{
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID1 = req_328_Util.createPeerDeploymentID("publickey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), objID1, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		//add the second Job
		req_304_Util.addJob(true, 2, broker, "echo Hello Word 2", "Test Job 2", peers);
		
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		
		//call here is worker
		List<TestStub> testStubs = new ArrayList<TestStub>();
		TestStub testStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, 
				"publickey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, testStub.getDeploymentID());
		
		testStubs.add(testStub);
		
		//call schedule with the correct public key
		req_329_Util.doSchedule(broker, testStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
	}

	/**
	* Create and start the Broker with the correct public key;
	* Call setPeers giving a list containing one peer with the following attributes:
	      o First peer = username = test and servername = servertest
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
	* Call hereIsWorker giving a worker with public key "workerPublicKey2" and the 
	* 	request ID generated by job 2;
	* Call schedule with the correct public key;
	* Verify if the worker with public key = "workerPublicKey2" startWork message was called;
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-329.2", reqs="REQ329")
	@Category(JDLCompliantTest.class) @Test public void test_at_329_2_1_ScheduleTests() throws Exception {
		//create and start the broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID objID = req_328_Util.createPeerDeploymentID("publickey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(),	objID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider) peerTestStub.getObject());
		//add the first job
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		//add the second Job
		TestJob testJob2 = req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE );
		
		//call here is worker from peer with username test
		List<TestStub> testStubs = new ArrayList<TestStub>();
		TestStub testStub1 = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, specs.get( 0 ), 
				"publickey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, testStub1.getDeploymentID());
		
		testStubs.add(testStub1);
		
		//call schedule with the correct public key
		req_329_Util.doSchedule(broker, testStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		/*call here is worker with the request ID generated from peer with username test 
		and job 2*/
		testStubs.clear();
		TestStub testStub2 = req_312_Util.receiveWorker(broker, "workerPublicKey2", true, true, true, true, specs.get( 1 ), 
				"publickey1", peerTestStub, testJob2);
		
		req_330_Util.notifyWorkerRecovery(broker, testStub2.getDeploymentID());
		
		testStubs.add(testStub2);
		
		List<TestJob> jobs = new ArrayList<TestJob>();
//		jobs.add(testJob1);
		jobs.add(testJob2);
		
		List<GridProcessHandle> handles = new ArrayList<GridProcessHandle>();
		handles.add(new GridProcessHandle(2, 1, 1));
	
		//call schedule with the correct public key
		List<TestStub> peerStubs = new ArrayList<TestStub>();
		peerStubs.add(peerTestStub);
		
		req_329_Util.doSchedule(broker, testStubs, peerStubs, jobs, handles); 
	}
}
