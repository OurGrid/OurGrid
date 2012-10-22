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
import org.ourgrid.acceptance.util.broker.Operations;
import org.ourgrid.acceptance.util.broker.Req_302_Util;
import org.ourgrid.acceptance.util.broker.Req_304_Util;
import org.ourgrid.acceptance.util.broker.Req_309_Util;
import org.ourgrid.acceptance.util.broker.Req_311_Util;
import org.ourgrid.acceptance.util.broker.Req_312_Util;
import org.ourgrid.acceptance.util.broker.Req_313_Util;
import org.ourgrid.acceptance.util.broker.Req_314_Util;
import org.ourgrid.acceptance.util.broker.Req_319_Util;
import org.ourgrid.acceptance.util.broker.Req_320_Util;
import org.ourgrid.acceptance.util.broker.Req_321_Util;
import org.ourgrid.acceptance.util.broker.Req_324_Util;
import org.ourgrid.acceptance.util.broker.Req_325_Util;
import org.ourgrid.acceptance.util.broker.Req_327_Util;
import org.ourgrid.acceptance.util.broker.Req_328_Util;
import org.ourgrid.acceptance.util.broker.Req_329_Util;
import org.ourgrid.acceptance.util.broker.Req_330_Util;
import org.ourgrid.acceptance.util.broker.States;
import org.ourgrid.acceptance.util.broker.TestJob;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ320")
public class Req_320_Test extends BrokerAcceptanceTestCase {
	private Req_320_Util req_320_Util;
	private Req_319_Util req_319_Util;
	private Req_302_Util req_302_Util;
	private Req_304_Util req_304_Util;
	private Req_309_Util req_309_Util;
	private Req_311_Util req_311_Util;
	private Req_312_Util req_312_Util;
	private Req_324_Util req_324_Util;
	private Req_327_Util req_327_Util;
	private Req_328_Util req_328_Util;
	private Req_329_Util req_329_Util;
	private Req_313_Util req_313_Util;
	private Req_321_Util req_321_Util;
	private Req_314_Util req_314_Util;
	private Req_325_Util req_325_Util;
	private Req_330_Util req_330_Util;
	private String peerUserAtServer;

	
	@Override
	public void setUp() throws Exception {
		req_302_Util = new Req_302_Util(super.createComponentContext());
		req_304_Util = new Req_304_Util(super.createComponentContext());
		req_309_Util = new Req_309_Util(super.createComponentContext());
		req_311_Util = new Req_311_Util(super.createComponentContext());
		req_312_Util = new Req_312_Util(super.createComponentContext());
		req_313_Util = new Req_313_Util(super.createComponentContext());
		req_314_Util = new Req_314_Util(super.createComponentContext());
		req_319_Util = new Req_319_Util(getComponentContext());
		req_320_Util = new Req_320_Util(getComponentContext());
		req_321_Util = new Req_321_Util(super.createComponentContext());
		req_324_Util = new Req_324_Util(super.createComponentContext());
		req_325_Util = new Req_325_Util(super.createComponentContext());
		req_327_Util = new Req_327_Util(super.createComponentContext());
		req_328_Util = new Req_328_Util(super.createComponentContext());
		req_329_Util = new Req_329_Util(super.createComponentContext());
		req_330_Util = new Req_330_Util(super.createComponentContext());
		peerUserAtServer = "test@servertest";
	}

	/**
	 *This test contains the following steps:
	 *
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
     * Call schedule with the correct public key;
     * Verify if the worker's startWork message was called;
     * Call outgoingTransferCancelled message;
     * Verify if the following warn message was logged:
     *     o Invalid operation. The execution is on the state: Scheduled
	 */
	@ReqTest(test="AT-320.1", reqs="REQ320")
	@Test public void test_at_320_1_RejectedFileForScheduledWorker() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "Echo Hello World", "Test Job", peers);
		req_304_Util.addJob(true, 2, broker, "Echo Hello World 2", "Test Job 2", peers);
		
		//try to receive worker
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				new WorkerSpecification(attributes), "publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		workerTestStubs.add(workerTestStub);
		
		//call schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1));
		
		//call outgoingTransferCancelled
		req_320_Util.cancelOutgoingTransfer(broker, States.SCHEDULED_STATE, Operations.EXECUTE_COMMAND_OPERATION, testJob, 
				peerTestStub, workerTestStub, "Class.class");
	}

	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *      o First peer = username = test and servername = servertest
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *      o Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes:
	 *      o Label: Test Job
	 *      o Task Spec:
	 *            + Remote Execute: echo Hello World;
	 *            + Init Block:
	 *                  # Condition: mem = 256
	 *                  # IOEntry:
	 *                        * Command: PUT
	 *                        * Source File: file.txt
	 *                        * Destination Class.class
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with the attributes:
	 *      o Public Key: workerPublicKey;
	 *      o Request ID: generated by job 1;
	 *      o Worker Spec: an attribute mem = 256;
	 * Call schedule with the correct public key;
	 * Verify if the following debug message was logged:
	 *      o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of the Worker;
	 * Verify if the worker's startWork message was called;
	 * Verify if the following info message was logged:
	 *      o Sending file file.txt to X Where X is the worker deployment ID;
	 * Verify if the Worker receveid a transferRequestReceived message;
	 * Call outgoingTransferCancelled message;
	 * Verify if the following debug message was logged:
	 *      o Outgoing file transfer cancelled: X, amount written: Y Where X is the TransferHandle? and Y is the file size.
	 * 		o Adding to blacklist. Task: X Worker: Y
	 */
	@ReqTest(test="AT-320.2", reqs="REQ320")
	@Test public void test_at_320_2_RejectedFileForInitialExecution() throws Exception{
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		IOEntry iOEntry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class"); 

		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//adds and verifies
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", iOEntry, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);

		//call here is worker with the request ID generated by job 1.
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob);

		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1));
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, testJob);
			
		req_320_Util.cancelOutgoingTransfer(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
	}
	
	/**
	* Create and start the Broker with the correct public key;
    * Call setPeers giving a list containing one peer with the following attributes:
          o First peer = username = test and servername = servertest
    * Call doNotifyRecovery passing a peer with username = test on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is UP. Where X is the objectID generated.
    * Do login with the public key property set to "publicKey1"in the worker provider.
    * Add a job with the attributes:
          o Label: Test Job
          o Task Spec:
                + Remote Execute: echo Hello World;
                + Init Block:
                      # Condition: mem = 256
                      # IOEntry:
                            * Command: STORE
                            * Source File: file.txt
                            * Destination Class.class
    * Verify if the operation result contains a jobID with value 1.
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote 
    * 	attribute "echo Hello World 2"
    * Verify if the operation result contains a jobID with value 2.
    * Call hereIsWorker giving a worker with the attributes:
          o Public Key: workerPublicKey;
          o Request ID: generated by job 1;
          o Worker Spec: an attribute mem = 256;
    * Call schedule with the correct public key;
    * Verify if the following debug message was logged:
          o Executing replica: 1.1.1, Worker: X. Where X is the toString() method 
          	of the Worker;
    * Verify if the worker's startWork message was called;
    * Verify if the following info message was logged:
          o File info requested: file.txt, handle: X, replica: 1.1.1 Where X is 
          	the TransferHandle? ID;
    * Verify if the Worker receveid a GetFileInfo? message;
    * Call outgoingTransferCancelled message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Init

	 * @throws Exception
	 */
	@ReqTest(test="AT-320.3", reqs="REQ320")
	@Test public void test_at_320_3_RejectedFile() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//add jobs
		IOEntry entry = new IOEntry("STORE", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//call here is worker with the request ID generated by job 1.
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1)); 
		
		//Verify if the logged debug message
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker) workerTestStub.getObject());
		
		String[] remoteFiles = {"Class.class"};
		String[] localFiles = {BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt"};
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_INFO_OPERATION, false, States.SCHEDULED_STATE,
				1, 1, requestID, testJob, localFiles, remoteFiles);		
		
		//call outgoingTransferCancelled
		req_320_Util.cancelOutgoingTransfer(broker, States.INIT_STATE, Operations.SEND_FILE_INFO_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
	}
	
	/**
	* Create and start the Broker with the correct public key;
    * Call setPeers giving a list containing one peer with the following attributes:
          o First peer = username = test and servername = servertest
    * Call doNotifyRecovery passing a peer with username = test on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is UP. Where X is the objectID generated.
    * Do login with the public key property set to "publicKey1"in the worker provider.
    * Add a job with the attributes:
          o Label: Test Job
          o Task Spec:
                + Remote Execute: echo Hello World;
                + Init Block:
                      # Condition: mem = 256
                      # IOEntry:
                            * Command: PUT
                            * Source File: file.txt
                            * Destination Class.class
    * Verify if the operation result contains a jobID with value 1.
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute 
    * 	"echo Hello World 2"
    * Verify if the operation result contains a jobID with value 2.
    * Call hereIsWorker giving a worker with the attributes:
          o Public Key: workerPublicKey;
          o Request ID: generated by job 1;
          o Worker Spec: an attribute mem = 256;
    * Call schedule with the correct public key;
    * Verify if the following debug message was logged:
          o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of 
          	the Worker;
    * Verify if the worker's startWork message was called;
    * Verify if the following info message was logged:
          o Sending file file.txt to X Where X is the worker deployment ID;
    * Verify if the Worker receveid a transferRequestReceived message;
    * Call outgoingTransferCompleted message;
    * Verify if the following debug message was logged:
          o File transfer finished: file.txt, replica: X Where X is the Grid 
          	Process Handle.
    * Call outgoingTransferCancelled message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Remote

	 * @throws Exception
	 */
	@ReqTest(test="AT-320.4", reqs="REQ320")
	@Test public void test_at_320_4_RejectedFile() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//add jobs
		IOEntry entry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//call here is worker with the request ID generated by job 1.
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec,"publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1)); 
		
		//Verify if the logged debug message
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, testJob);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		
		// Call outgoingTransferCompleted message;
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		//call outgoingTransferCancelled
		req_320_Util.cancelOutgoingTransfer(broker, States.REMOTE_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
	}
	
	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *      o First peer = username = test and servername = servertest
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *      o Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes:
	 *      o Label: Test Job
	 *      o Task Spec:
	 *            + Remote Execute: echo Hello World;
	 *            + Init Block:
	 *                  # Condition: mem = 256
	 *                  # IOEntry:
	 *                        * Command: PUT
	 *                        * Source File: file.txt
	 *                        * Destination Class.class
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with the attributes:
	 *      o Public Key: workerPublicKey;
	 *      o Request ID: generated by job 1;
	 *      o Worker Spec: an attribute mem = 256;
	 * Call schedule with the correct public key;
	 * Verify if the following debug message was logged:
	 *      o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of the Worker;
	 * Verify if the worker's startWork message was called;
	 * Call workerIsReady message;
	 * Verify if the following info message was logged:
	 *      o Sending file file.txt to X Where X is the worker deployment ID;
	 * Verify if the Worker receveid a transferRequestReceived message;
	 * Call outgoingTransferCompleted message;
	 * Verify if the following debug message was logged:
	 *      o File transfer finished: file.txt, replica: X Where X is the Grid Process Handle.
	 * Call hereIsGridProcessResult message;
	 * Verify if the Worker receveid a getFiles message;
	 * Call outgoingTransferCancelled message;
	 * Verify if the following warn message was logged:
	 *      o Invalid operation. The execution is on the state: Final
	 *
	 */
	@ReqTest(test="AT-320.5", reqs="REQ320")
	@Test public void test_at_320_5_RejectedFileOnFinalState() throws Exception{
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//add jobs
		IOEntry entry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//call here is worker with the request ID generated by job 1.
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1)); 
		
		//Verify if the logged debug message
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, testJob);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		
		// Call outgoingTransferCompleted message;
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);

		// Call hereIsGridProcessResult message;
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, false, peerTestStub, testJob);
		
		// Call outgoingTransferCancelled message;
		req_320_Util.cancelOutgoingTransfer(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
	}
	
	/**
	* Create and start the Broker with the correct public key;
    * Call setPeers giving a list containing one peer with the following attributes:
          o First peer = username = test and servername = servertest
    * Call doNotifyRecovery passing a peer with username = test on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is UP. Where X is the objectID generated.
    * Do login with the public key property set to "publicKey1"in the worker provider.
    * Add a job with the attributes:
          o Label: Test Job
          o Task Spec:
                + Remote Execute: echo Hello World;
                + Init Block:
                      # Condition: mem = 256
                      # IOEntry:
                            * Command: PUT
                            * Source File: file.txt
                            * Destination Class.class
    * Verify if the operation result contains a jobID with value 1.
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
    * Verify if the operation result contains a jobID with value 2.
    * Call hereIsWorker giving a worker with the attributes:
          o Public Key: workerPublicKey;
          o Request ID: generated by job 1;
          o Worker Spec: an attribute mem = 256;
    * Call schedule with the correct public key;
    * Verify if the following debug message was logged:
          o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of the Worker;
    * Verify if the worker's startWork message was called;
    * Call workerIsReady message;
    * Verify if the following info message was logged:
          o Sending file file.txt to X Where X is the worker deployment ID;
    * Verify if the Worker receveid a transferRequestReceived message;
    * Call outgoingTransferCompleted message;
    * Verify if the following debug message was logged:
          o File transfer finished: file.txt, replica: X Where X is the Grid Process Handle.
    * Call hereIsGridProcessResult message;
    * Verify if the Worker receveid a getFiles message;
    * Call fileTransferRequestReceived message;
    * Call outgoingTransferCancelled message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Final

	 * @throws Exception
	 */
	@ReqTest(test="AT-320.6", reqs="REQ320")
	@Test public void test_at_320_6_RejectedFileOnFinalState() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//add jobs
		IOEntry entry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//call here is worker with the request ID generated by job 1.
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec,"publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, null);
		//Call outgoingTransferCompleted message
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		// Call hereIsGridProcessResult message
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, false, peerTestStub, testJob);
		
		// Call fileTransferRequestReceived message
		req_324_Util.transferRequestReceived(broker, workerTestStub, BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", true);
		
		// Call outgoingTransferCancelled message
		req_320_Util.cancelOutgoingTransfer(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
		
	}
	
	/**
	*    *  Create and start the Broker with the correct public key;
    * Call setPeers giving a list containing one peer with the following attributes:
          o First peer = username = test and servername = servertest
    * Call doNotifyRecovery passing a peer with username = test on the parameter;
    * Verify if the following debug message was logged:
          o Peer with object id: [X] is UP. Where X is the objectID generated.
    * Do login with the public key property set to "publicKey1"in the worker provider.
    * Add a job with the attributes:
          o Label: Test Job
          o Task Spec:
                + Remote Execute: echo Hello World;
                + Init Block:
                      # Condition: mem = 256
                      # IOEntry:
                            * Command: PUT
                            * Source File: file.txt
                            * Destination Class.class
                + Final Block:
                      # Condition: mem = 256
                      # IOEntry:
                            * Command: GET
                            * Source File: remoteFile1.txt
                            * Destination localFile1.txt
                      # IOEntry:
                            * Command: GET
                            * Source File: remoteFile2.txt
                            * Destination localFile2.txt
    * Verify if the operation result contains a jobID with value 1.
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
    * Verify if the operation result contains a jobID with value 2.
    * Call hereIsWorker giving a worker with the attributes:
          o Public Key: workerPublicKey;
          o Request ID: generated by job 1;
          o Worker Spec: an attribute mem = 256;
    * Call schedule with the correct public key;
    * Verify if the following debug message was logged:
          o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of the Worker;
    * Verify if the worker's startWork message was called;
    * Call workerIsReady message;
    * Verify if the following info message was logged:
          o Sending file file.txt to X Where X is the worker deployment ID;
    * Verify if the Worker receveid a transferRequestReceived message;
    * Call outgoingTransferCompleted message;
    * Verify if the following debug message was logged:
          o File transfer finished: file.txt, replica: X Where X is the Grid Process Handle.
    * Call hereIsGridProcessResult message;
    * Verify if the Worker receveid a getFiles message;
    * Call fileTransferRequestReceived message with fileName remoteFile1.txt;
    * Call fileTransferRequestReceived message again with filename remoteFile2.txt;
    * Call incomingTransferCompleted message with fileName remoteFile1.txt;
    * Call incomingTransferCompleted message again with filename remoteFile2.txt;
    * Verify if the following info message was logged:
          o Worker dispose X Where X is the worker deployment ID;
    * Verify if the local worker provider's disposeWorker message was called;
    * Verify if the local worker provider's finishRequest message was called;
    * Call outgoingTransferCancelled message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Final

	 * @throws Exception
	 */
	@ReqTest(test="AT-320.7", reqs="REQ320")
	@Test public void test_at_320_7_UploadCancelledForFinalizedExecution() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		//add jobs
		IOEntry initEntry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		IOEntry finalEntry1 = new IOEntry("GET", "remoteFile1.txt", "localFile1.txt");
		IOEntry finalEntry2 = new IOEntry("GET", "remoteFile2.txt", "localFile2.txt");
		
		List<IOEntry> initEntries = new LinkedList<IOEntry>();
		List<IOEntry> finalEntries = new LinkedList<IOEntry>();
		
		initEntries.add(initEntry);
		finalEntries.add(finalEntry1);
		finalEntries.add(finalEntry2);
		
		List<LocalWorkerProvider> lwps = new ArrayList<LocalWorkerProvider>();
		lwps.add((LocalWorkerProvider) peerTestStub.getObject());

		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", initEntries, finalEntries, lwps);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", lwps);
		
		//call here is worker with the request ID generated by job 1.
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, testJob);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		//call hereIsGridProcessResult 
		String[] fileNames = new String[2];
		fileNames[0] = "remoteFile1.txt";
		fileNames[1] = "remoteFile2.txt";
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, true, fileNames);
		
		//call fileTransferRequestReceived message
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile1.txt", workerTestStub, testJob);
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile2.txt", workerTestStub, testJob);
		
		//call incomingTransferCompleted message
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile1.txt", false, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob);
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile2.txt", true, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob, new GridProcessHandle(1, 1, 1));
		
		//call outgoingTransferCancelled message;
		req_320_Util.cancelOutgoingTransfer(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
	}

	/**
	 *This test contains the following steps:
	 *
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
	 * Call schedule with the correct public key;
	 * Verify if the worker's startWork message was called;
	 * Call outgoingTransferCancelled message;
	 * Verify if the following warn message was logged:
	 *     o Invalid operation. The execution is on the state: Scheduled
	 */
	@ReqTest(test="AT-320.1", reqs="REQ320")
	@Category(JDLCompliantTest.class) @Test public void test_at_320_1_1_RejectedFileForScheduledWorker() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peers);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//try to receive worker
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		workerTestStubs.add(workerTestStub);
		
		//call schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1));
		
		//call outgoingTransferCancelled
		req_320_Util.cancelOutgoingTransfer(broker, States.SCHEDULED_STATE, Operations.EXECUTE_COMMAND_OPERATION, testJob, 
				peerTestStub, workerTestStub, "Class.class");
	}

	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *      o First peer = username = test and servername = servertest
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *      o Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes:
	 *      o Label: Test Job
	 *      o Task Spec:
	 *            + Remote Execute: echo Hello World;
	 *            + Init Block:
	 *                  # Condition: mem = 256
	 *                  # IOEntry:
	 *                        * Command: PUT
	 *                        * Source File: file.txt
	 *                        * Destination Class.class
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with the attributes:
	 *      o Public Key: workerPublicKey;
	 *      o Request ID: generated by job 1;
	 *      o Worker Spec: an attribute mem = 256;
	 * Call schedule with the correct public key;
	 * Verify if the following debug message was logged:
	 *      o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of the Worker;
	 * Verify if the worker's startWork message was called;
	 * Verify if the following info message was logged:
	 *      o Sending file file.txt to X Where X is the worker deployment ID;
	 * Verify if the Worker receveid a transferRequestReceived message;
	 * Call outgoingTransferCancelled message;
	 * Verify if the following debug message was logged:
	 *      o Outgoing file transfer cancelled: X, amount written: Y Where X is the TransferHandle? and Y is the file size.
	 * 		o Adding to blacklist. Task: X Worker: Y
	 */
	@ReqTest(test="AT-320.2", reqs="REQ320")
	@Category(JDLCompliantTest.class) @Test public void test_at_320_2_1_RejectedFileForInitialExecution() throws Exception{
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//adds and verifies
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, peers);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
	
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
	
		//call here is worker with the request ID generated by job 1.
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
	
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1));
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, testJob, remoteFileNames);
			
		req_320_Util.cancelOutgoingTransfer(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
	}

	/**
	* Create and start the Broker with the correct public key;
	* Call setPeers giving a list containing one peer with the following attributes:
	      o First peer = username = test and servername = servertest
	* Call doNotifyRecovery passing a peer with username = test on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is UP. Where X is the objectID generated.
	* Do login with the public key property set to "publicKey1"in the worker provider.
	* Add a job with the attributes:
	      o Label: Test Job
	      o Task Spec:
	            + Remote Execute: echo Hello World;
	            + Init Block:
	                  # Condition: mem = 256
	                  # IOEntry:
	                        * Command: PUT
	                        * Source File: file.txt
	                        * Destination Class.class
	* Verify if the operation result contains a jobID with value 1.
	* Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute 
	* 	"echo Hello World 2"
	* Verify if the operation result contains a jobID with value 2.
	* Call hereIsWorker giving a worker with the attributes:
	      o Public Key: workerPublicKey;
	      o Request ID: generated by job 1;
	      o Worker Spec: an attribute mem = 256;
	* Call schedule with the correct public key;
	* Verify if the following debug message was logged:
	      o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of 
	      	the Worker;
	* Verify if the worker's startWork message was called;
	* Verify if the following info message was logged:
	      o Sending file file.txt to X Where X is the worker deployment ID;
	* Verify if the Worker receveid a transferRequestReceived message;
	* Call outgoingTransferCompleted message;
	* Verify if the following debug message was logged:
	      o File transfer finished: file.txt, replica: X Where X is the Grid 
	      	Process Handle.
	* Call outgoingTransferCancelled message;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Remote
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-320.4", reqs="REQ320")
	@Category(JDLCompliantTest.class) @Test public void test_at_320_4_1_RejectedFile() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//add jobs
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, peers);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//call here is worker with the request ID generated by job 1.
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec,"publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1)); 
		
		//Verify if the logged debug message
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, testJob, remoteFileNames);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		
		// Call outgoingTransferCompleted message;
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFileNames[0], 1, 1, handle, requestID, false);
		
		//call outgoingTransferCancelled
		req_320_Util.cancelOutgoingTransfer(broker, States.REMOTE_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
	}

	/**
	 * Create and start the Broker with the correct public key;
	 * Call setPeers giving a list containing one peer with the following attributes:
	 *      o First peer = username = test and servername = servertest
	 * Call doNotifyRecovery passing a peer with username = test on the parameter;
	 * Verify if the following debug message was logged:
	 *      o Peer with object id: [X] is UP. Where X is the objectID generated.
	 * Do login with the public key property set to "publicKey1"in the worker provider.
	 * Add a job with the attributes:
	 *      o Label: Test Job
	 *      o Task Spec:
	 *            + Remote Execute: echo Hello World;
	 *            + Init Block:
	 *                  # Condition: mem = 256
	 *                  # IOEntry:
	 *                        * Command: PUT
	 *                        * Source File: file.txt
	 *                        * Destination Class.class
	 * Verify if the operation result contains a jobID with value 1.
	 * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	 * Verify if the operation result contains a jobID with value 2.
	 * Call hereIsWorker giving a worker with the attributes:
	 *      o Public Key: workerPublicKey;
	 *      o Request ID: generated by job 1;
	 *      o Worker Spec: an attribute mem = 256;
	 * Call schedule with the correct public key;
	 * Verify if the following debug message was logged:
	 *      o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of the Worker;
	 * Verify if the worker's startWork message was called;
	 * Call workerIsReady message;
	 * Verify if the following info message was logged:
	 *      o Sending file file.txt to X Where X is the worker deployment ID;
	 * Verify if the Worker receveid a transferRequestReceived message;
	 * Call outgoingTransferCompleted message;
	 * Verify if the following debug message was logged:
	 *      o File transfer finished: file.txt, replica: X Where X is the Grid Process Handle.
	 * Call hereIsGridProcessResult message;
	 * Verify if the Worker receveid a getFiles message;
	 * Call outgoingTransferCancelled message;
	 * Verify if the following warn message was logged:
	 *      o Invalid operation. The execution is on the state: Final
	 *
	 */
	@ReqTest(test="AT-320.5", reqs="REQ320")
	@Category(JDLCompliantTest.class) @Test public void test_at_320_5_1_RejectedFileOnFinalState() throws Exception{
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//add jobs
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, peers);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//call here is worker with the request ID generated by job 1.
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1)); 
		
		//Verify if the logged debug message
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, testJob, remoteFileNames);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		
		// Call outgoingTransferCompleted message;
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFileNames[0], 1, 1, handle, requestID, false);
	
		// Call hereIsGridProcessResult message;
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, false, peerTestStub, testJob);
		
		// Call outgoingTransferCancelled message;
		req_320_Util.cancelOutgoingTransfer(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
	}

	/**
	* Create and start the Broker with the correct public key;
	* Call setPeers giving a list containing one peer with the following attributes:
	      o First peer = username = test and servername = servertest
	* Call doNotifyRecovery passing a peer with username = test on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is UP. Where X is the objectID generated.
	* Do login with the public key property set to "publicKey1"in the worker provider.
	* Add a job with the attributes:
	      o Label: Test Job
	      o Task Spec:
	            + Remote Execute: echo Hello World;
	            + Init Block:
	                  # Condition: mem = 256
	                  # IOEntry:
	                        * Command: PUT
	                        * Source File: file.txt
	                        * Destination Class.class
	* Verify if the operation result contains a jobID with value 1.
	* Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	* Verify if the operation result contains a jobID with value 2.
	* Call hereIsWorker giving a worker with the attributes:
	      o Public Key: workerPublicKey;
	      o Request ID: generated by job 1;
	      o Worker Spec: an attribute mem = 256;
	* Call schedule with the correct public key;
	* Verify if the following debug message was logged:
	      o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of the Worker;
	* Verify if the worker's startWork message was called;
	* Call workerIsReady message;
	* Verify if the following info message was logged:
	      o Sending file file.txt to X Where X is the worker deployment ID;
	* Verify if the Worker receveid a transferRequestReceived message;
	* Call outgoingTransferCompleted message;
	* Verify if the following debug message was logged:
	      o File transfer finished: file.txt, replica: X Where X is the Grid Process Handle.
	* Call hereIsGridProcessResult message;
	* Verify if the Worker receveid a getFiles message;
	* Call fileTransferRequestReceived message;
	* Call outgoingTransferCancelled message;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Final
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-320.6", reqs="REQ320")
	@Category(JDLCompliantTest.class) @Test public void test_at_320_6_1_RejectedFileOnFinalState() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//add jobs
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, peers);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//call here is worker with the request ID generated by job 1.
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec,"publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, null, remoteFileNames);
		//Call outgoingTransferCompleted message
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFileNames[0], 1, 1, handle, requestID, false);
		
		// Call hereIsGridProcessResult message
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, false, peerTestStub, testJob);
		
		// Call fileTransferRequestReceived message
		req_324_Util.transferRequestReceived(broker, workerTestStub, BrokerAcceptanceTestCase.BROKER_TEST_DIR + "Class.class", true);
		
		// Call outgoingTransferCancelled message
		req_320_Util.cancelOutgoingTransfer(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
		
	}

	/**
	*    *  Create and start the Broker with the correct public key;
	* Call setPeers giving a list containing one peer with the following attributes:
	      o First peer = username = test and servername = servertest
	* Call doNotifyRecovery passing a peer with username = test on the parameter;
	* Verify if the following debug message was logged:
	      o Peer with object id: [X] is UP. Where X is the objectID generated.
	* Do login with the public key property set to "publicKey1"in the worker provider.
	* Add a job with the attributes:
	      o Label: Test Job
	      o Task Spec:
	            + Remote Execute: echo Hello World;
	            + Init Block:
	                  # Condition: mem = 256
	                  # IOEntry:
	                        * Command: PUT
	                        * Source File: file.txt
	                        * Destination Class.class
	            + Final Block:
	                  # Condition: mem = 256
	                  # IOEntry:
	                        * Command: GET
	                        * Source File: remoteFile1.txt
	                        * Destination localFile1.txt
	                  # IOEntry:
	                        * Command: GET
	                        * Source File: remoteFile2.txt
	                        * Destination localFile2.txt
	* Verify if the operation result contains a jobID with value 1.
	* Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute "echo Hello World 2"
	* Verify if the operation result contains a jobID with value 2.
	* Call hereIsWorker giving a worker with the attributes:
	      o Public Key: workerPublicKey;
	      o Request ID: generated by job 1;
	      o Worker Spec: an attribute mem = 256;
	* Call schedule with the correct public key;
	* Verify if the following debug message was logged:
	      o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of the Worker;
	* Verify if the worker's startWork message was called;
	* Call workerIsReady message;
	* Verify if the following info message was logged:
	      o Sending file file.txt to X Where X is the worker deployment ID;
	* Verify if the Worker receveid a transferRequestReceived message;
	* Call outgoingTransferCompleted message;
	* Verify if the following debug message was logged:
	      o File transfer finished: file.txt, replica: X Where X is the Grid Process Handle.
	* Call hereIsGridProcessResult message;
	* Verify if the Worker receveid a getFiles message;
	* Call fileTransferRequestReceived message with fileName remoteFile1.txt;
	* Call fileTransferRequestReceived message again with filename remoteFile2.txt;
	* Call incomingTransferCompleted message with fileName remoteFile1.txt;
	* Call incomingTransferCompleted message again with filename remoteFile2.txt;
	* Verify if the following info message was logged:
	      o Worker dispose X Where X is the worker deployment ID;
	* Verify if the local worker provider's disposeWorker message was called;
	* Verify if the local worker provider's finishRequest message was called;
	* Call outgoingTransferCancelled message;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Final
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-320.7", reqs="REQ320")
	@Category(JDLCompliantTest.class) @Test public void test_at_320_7_1_UploadCancelledForFinalizedExecution() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwps = new ArrayList<LocalWorkerProvider>();
		lwps.add((LocalWorkerProvider) peerTestStub.getObject());
	
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_IO_JOB, lwps);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, lwps);
		
		//call here is worker with the request ID generated by job 1.
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, testJob, remoteFileNames);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFileNames[0], 1, 1, handle, requestID, false);
		
		//call hereIsGridProcessResult 
		String[] fileNames = new String[2];
		fileNames[0] = "remoteFile1.txt";
		fileNames[1] = "remoteFile2.txt";
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, true, fileNames);
		
		//call fileTransferRequestReceived message
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile1.txt", workerTestStub, testJob);
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile2.txt", workerTestStub, testJob);
		
		//call incomingTransferCompleted message
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile1.txt", false, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob);
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile2.txt", true, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob, new GridProcessHandle(1, 1, 1));
		
		//call outgoingTransferCancelled message;
		req_320_Util.cancelOutgoingTransfer(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, testJob, peerTestStub, 
				workerTestStub, "Class.class");
	}
}
