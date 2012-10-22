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
import org.ourgrid.acceptance.util.broker.Req_315_Util;
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
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.ReqTest;
import org.ourgrid.worker.business.controller.GridProcessError;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ315")
public class Req_315_Test extends BrokerAcceptanceTestCase {
	private Req_302_Util req_302_Util = new Req_302_Util(getComponentContext());
	private Req_304_Util req_304_Util = new Req_304_Util(getComponentContext());
	private Req_309_Util req_309_Util = new Req_309_Util(getComponentContext());
	private Req_311_Util req_311_Util = new Req_311_Util(getComponentContext());
	private Req_312_Util req_312_Util = new Req_312_Util(getComponentContext());
	private Req_314_Util req_314_Util = new Req_314_Util(getComponentContext());
	private Req_315_Util req_315_Util = new Req_315_Util(getComponentContext());
	private Req_321_Util req_321_Util = new Req_321_Util(getComponentContext());
	private Req_327_Util req_327_Util = new Req_327_Util(getComponentContext());
	private Req_328_Util req_328_Util = new Req_328_Util(getComponentContext());
	private Req_329_Util req_329_Util = new Req_329_Util(getComponentContext());
	private Req_313_Util req_313_Util = new Req_313_Util(getComponentContext());
	private Req_324_Util req_324_Util = new Req_324_Util(getComponentContext());
	private Req_325_Util req_325_Util = new Req_325_Util(getComponentContext());
	private Req_330_Util req_330_Util = new Req_330_Util(getComponentContext());
	private String peerUserAtServer;

	
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
     * Call ErrorOcurred? message;
     * Verify if the following warn message was logged:
     *     o Invalid operation. The execution is on the state: Scheduled
	 */
	@ReqTest(test="AT-315.1", reqs="REQ315")
	@Test public void test_at_315_1_ExecutionErrorForScheduledWorker() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		List<LocalWorkerProvider> peerList = new LinkedList<LocalWorkerProvider>();
		peerList.add((LocalWorkerProvider) peerTestStub.getObject());
		
		//add jobs
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, "Echo Hello World", "Test Job", peerList);
		req_304_Util.addJob(true, 2, broker, "Echo Hello World 2", "Test Job 2", peerList);
		
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, new WorkerSpecification(attributes), 
				"publicKey1", peerTestStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerTestStubs.add(workerTestStub);
		
		//call schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, jobStub, new GridProcessHandle(1, 1, 1));
		
		//call errorOcurred
		req_315_Util.executionError(broker, States.SCHEDULED_STATE, (LocalWorkerProvider) peerTestStub.getObject(), workerTestStub, 
				new GridProcessError(GridProcessErrorTypes.EXECUTION_ERROR), false, jobStub);
	}
	
	/**
	 *     *  Create and start the Broker with the correct public key;
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
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote 
    * 	attribute "echo Hello World 2"
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
    * Call ErrorOcurred? message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Init

	 * @throws Exception
	 */
	@ReqTest(test="AT-315.2", reqs="REQ315")
	@Test public void test_at_315_2_ExecutionErrorForInitedWorker() throws Exception {
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
		IOEntry entry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		List<Worker> workers = new ArrayList<Worker>();
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workers.add((Worker) workerTestStub.getObject());
		workerTestStubs.add(workerTestStub);
		
		//call scheduler
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, jobStub, new GridProcessHandle(1, 1, 1));
		
		
		String[] remoteFiles = {"Class.class"};
		String[] localFiles = {BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt"};
		
		//Verify if the logged debug message
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, States.SCHEDULED_STATE,
				1, 1, requestID, null, localFiles, remoteFiles);
		
		//call ErrorOcurred message
		req_315_Util.executionError(broker, States.INIT_STATE, (LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, 
				new GridProcessError(GridProcessErrorTypes.EXECUTION_ERROR), false, jobStub);
	}
	
	/**
	 *     *  Create and start the Broker with the correct public key;
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
          o File info requested: file.txt, handle: X, replica: 1.1.1 Where X is the 
          	TransferHandle ID;
    * Verify if the Worker receveid a GetFileInfo? message;
    * Call ErrorOcurred message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Init

	 * @throws Exception
	 */
	@ReqTest(test="AT-315.3", reqs="REQ315")
	@Test public void test_at_315_3_ExecutionErrorForInitedWorker() throws Exception {
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
		IOEntry entry = new IOEntry("STORE", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		List<Worker> workers = new ArrayList<Worker>();
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workers.add((Worker) workerTestStub.getObject());
		workerTestStubs.add(workerTestStub);
		
		//call scheduler
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		
		String[] remoteFiles = {"Class.class"};
		String[] localFiles = {BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt"};
		
		//Verify if the logged debug message
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_INFO_OPERATION, false, States.SCHEDULED_STATE, 
				1, 1, requestID, testJob1, localFiles, remoteFiles);
		
		//Call ErrorOcurred message
		req_315_Util.executionError(broker, States.INIT_STATE, (LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, 
				new GridProcessError(GridProcessErrorTypes.EXECUTION_ERROR), false, testJob1);
	}
	
	/**
	 *     *  Create and start the Broker with the correct public key;
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
    * Call errorOcurred message, giving a failCause with the message error and type EXECUTION_ERROR;
    * Verify if the following debug message was logged:
          o Adding to blacklist. Task: X, Worker: Y Where X is the task ID and Y is the worker deployment ID.
    * Verify if the following debug message was logged:
          o Worker unwanted: X Where X is the worker deployment ID.
    * Verify if the Worker Provider with username = test received an unwantedWorker message;
    * Verify if the following debug message was logged:
          o Grid process FAILED X. Job ended: true Where X is the Grid Process handle.
    * Verify if the Worker Provider with username = test received a finishRequest message;
    * Verify if the Worker Provider with username = test received a resumeRequest message;


	 * @throws Exception
	 */
	@ReqTest(test="AT-315.4", reqs="REQ315")
	@Test public void test_at_315_4_ErrorMessageOnExecutionInRemoteState() throws Exception {
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
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		List<Worker> workers = new ArrayList<Worker>();
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workers.add((Worker) workerTestStub.getObject());
		workerTestStubs.add(workerTestStub);
		
		//call scheduler
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		String[] remoteFiles = {"Class.class"};
		String[] localFiles = {BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt"};
		
		//Verify if the logged debug message
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, States.SCHEDULED_STATE, 
				1, 1, requestID, null, localFiles, remoteFiles);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//Call outgoindTransferCompleted
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		//Call ErrorOcurred message
		GridProcessError error = new GridProcessError(new Throwable("error"), GridProcessErrorTypes.EXECUTION_ERROR);
		req_315_Util.executionError(broker, States.REMOTE_STATE, (LocalWorkerProvider)peerTestStub.getObject(), 
				workerTestStub, error, false, testJob1);
		
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
                + Final Block:
                      # Condition: mem = 256
                      # IOEntry:
                            * Command: GET
                            * Source File: remoteFile1.txt
                            * Destination localFile1.txt
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
    * Call errorOcurred message, giving a failCause with the message error and type IO_ERROR;
    * Verify if the following debug message was logged:
          o Adding to blacklist. Task: X, Worker: Y Where X is the task ID and Y is the worker deployment ID.
    * Verify if the following debug message was logged:
          o Worker unwanted: X Where X is the worker deployment ID.
    * Verify if the Worker Provider with username = test received an unwantedWorker message;
    * Verify if the following debug message was logged:
          o Grid process FAILED X. Job ended: true Where X is the Grid Process handle.
    * Verify if the Worker Provider with username = test received a finishRequest message;
    * Verify if the Worker Provider with username = test received a resumeRequest message;
    * @throws Exception
	*/
	@ReqTest(test="AT-315.6", reqs="REQ315")
	@Test public void test_at_315_6_ErrorMessageOnExecutionInFinalState() throws Exception {
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
		IOBlock initBlock = new IOBlock();
		initBlock.putEntry(new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class"));
		IOBlock finalBlock = new IOBlock();
		finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
		TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, null);
		List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>();
		taskSpecs.add(task);
		JobSpecification jobSpec = new JobSpecification("Test Job", "mem = 256", taskSpecs);
		TestJob testJob1 = req_304_Util.addJob(true, jobSpec, 1, broker, peers);
		
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		List<Worker> workers = new ArrayList<Worker>();
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workers.add((Worker) workerTestStub.getObject());
		workerTestStubs.add(workerTestStub);
		
		//call scheduler
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		String[] remoteFiles = {"Class.class"};
		String[] localFiles = {BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt"};
		
		//call workerIsReady
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, States.SCHEDULED_STATE, 
				1, 1, requestID, null, localFiles, remoteFiles);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		//call hereIsGridProcessResult 
		
		String[] fileNames = new String[1];
		fileNames[0] = "remoteFile1.txt";
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, true, peerTestStub, testJob1, fileNames);
		
		//Call fileTransferRequestReceived message
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile1.txt", workerTestStub, testJob1);
		
		//Call ErrorOcurred message
		GridProcessError error = new GridProcessError(new Throwable("error"), GridProcessErrorTypes.IO_ERROR);
		req_315_Util.executionError(broker, States.FINAL_STATE, (LocalWorkerProvider)peerTestStub.getObject(),
				workerTestStub, error, false, testJob1);
	}
	
	
	/**
	 *     *  Create and start the Broker with the correct public key;
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
    * Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute 
    * 	"echo Hello World 2"
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
    * Call errorOcurred message, giving a failCause with the message error and type IO_ERROR;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Final

	 * @throws Exception
	 */
	@ReqTest(test="AT-315.7", reqs="REQ315")
	@Test public void test_at_315_7_ErrorMessage() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		LocalWorkerProvider lwp = req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		List<LocalWorkerProvider> peersList = new LinkedList<LocalWorkerProvider>();
		peersList.add(lwp);
		
		//add jobs
		IOBlock initBlock = new IOBlock();
		initBlock.putEntry(new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class"));
		IOBlock finalBlock = new IOBlock();
		finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
		finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
		TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
		task.setSourceDirPath(BROKER_TEST_DIR);
		List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>();
		taskSpecs.add(task);
		JobSpecification jobSpec = new JobSpecification("Test Job", "mem = 256", taskSpecs);
		TestJob testJob1 = req_304_Util.addJob(true, jobSpec, 1, broker, peersList);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peersList);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		List<Worker> workers = new ArrayList<Worker>();
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workers.add((Worker) workerTestStub.getObject());
		workerTestStubs.add(workerTestStub);
		
		//call scheduler
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//call workerIsReady
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, null);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		//call hereIsGridProcessResult 
		String[] fileNames = new String[2];
		fileNames[0] = "remoteFile1.txt";
		fileNames[1] = "remoteFile2.txt";
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, true, peerTestStub, testJob1, fileNames);
		
		//Call fileTransferRequestReceived message again with filename remoteFile1.txt
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile1.txt", workerTestStub, testJob1);
		
		//Call fileTransferRequestReceived message again with filename remoteFile2.txt
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile2.txt", workerTestStub, testJob1);
		
		//call incomingTransferCompleted message with fileName remoteFile1.txt;
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile1.txt", false, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob1);
		
		//call incomingTransferCompleted message with fileName remoteFile2.txt;
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile2.txt", true, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//Call ErrorOcurred message
		GridProcessError error = new GridProcessError(new Throwable("error"), GridProcessErrorTypes.IO_ERROR);
		req_315_Util.executionErrorWithoutWorkers(broker, workerTestStub, error);
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
	 * Call ErrorOcurred? message;
	 * Verify if the following warn message was logged:
	 *     o Invalid operation. The execution is on the state: Scheduled
	 */
	@ReqTest(test="AT-315.1", reqs="REQ315")
	@Category(JDLCompliantTest.class) @Test public void test_at_315_1_1_ExecutionErrorForScheduledWorker() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		List<LocalWorkerProvider> peerList = new LinkedList<LocalWorkerProvider>();
		peerList.add((LocalWorkerProvider) peerTestStub.getObject());
		
		//add jobs
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, peerList);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peerList);
		
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, workerSpec, 
				"publicKey1", peerTestStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerTestStubs.add(workerTestStub);
		
		//call schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, jobStub, new GridProcessHandle(1, 1, 1));
		
		//call errorOcurred
		req_315_Util.executionError(broker, States.SCHEDULED_STATE, (LocalWorkerProvider) peerTestStub.getObject(), workerTestStub, 
				new GridProcessError(GridProcessErrorTypes.EXECUTION_ERROR), false, jobStub);
	}

	/**
	 *     *  Create and start the Broker with the correct public key;
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
	* Add a job with the attributes: label: "Test Job 2" and one Task with remote 
	* 	attribute "echo Hello World 2"
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
	* Call ErrorOcurred? message;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Init
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-315.2", reqs="REQ315")
	@Category(JDLCompliantTest.class) @Test public void test_at_315_2_1_ExecutionErrorForInitedWorker() throws Exception {
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
		TestJob jobStub = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, peers); 
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		List<Worker> workers = new ArrayList<Worker>();
		
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, jobStub);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workers.add((Worker) workerTestStub.getObject());
		workerTestStubs.add(workerTestStub);
		
		//call scheduler
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, jobStub, new GridProcessHandle(1, 1, 1));
		
		
		String[] remoteFiles = new String[1];
		remoteFiles[0] = jobStub.getJobSpec().getTaskSpecs().get(0).getInitBlock().getEntry("").get(0).getSourceFile();
	
		//Verify if the logged debug message
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, States.SCHEDULED_STATE,
				1, 1, requestID, null, remoteFiles);
		
		//call ErrorOcurred message
		req_315_Util.executionError(broker, States.INIT_STATE, (LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, 
				new GridProcessError(GridProcessErrorTypes.EXECUTION_ERROR), false, jobStub);
	}

	/**
	 *     *  Create and start the Broker with the correct public key;
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
	* Call errorOcurred message, giving a failCause with the message error and type EXECUTION_ERROR;
	* Verify if the following debug message was logged:
	      o Adding to blacklist. Task: X, Worker: Y Where X is the task ID and Y is the worker deployment ID.
	* Verify if the following debug message was logged:
	      o Worker unwanted: X Where X is the worker deployment ID.
	* Verify if the Worker Provider with username = test received an unwantedWorker message;
	* Verify if the following debug message was logged:
	      o Grid process FAILED X. Job ended: true Where X is the Grid Process handle.
	* Verify if the Worker Provider with username = test received a finishRequest message;
	* Verify if the Worker Provider with username = test received a resumeRequest message;
	
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-315.4", reqs="REQ315")
	@Category(JDLCompliantTest.class) @Test public void test_at_315_4_1_ErrorMessageOnExecutionInRemoteState() throws Exception {
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
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, peers); 
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
	
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		List<Worker> workers = new ArrayList<Worker>();
		
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workers.add((Worker) workerTestStub.getObject());
		workerTestStubs.add(workerTestStub);
		
		//call scheduler
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		String[] remoteFiles = new String[1];
		remoteFiles[0] = testJob1.getJobSpec().getTaskSpecs().get(0).getInitBlock().getEntry("").get(0).getSourceFile();
		//Verify if the logged debug message
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, States.SCHEDULED_STATE, 
				1, 1, requestID, null, remoteFiles);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//Call outgoindTransferCompleted
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFiles[0], 1, 1, handle, requestID, false);
		
		//Call ErrorOcurred message
		GridProcessError error = new GridProcessError(new Throwable("error"), GridProcessErrorTypes.EXECUTION_ERROR);
		req_315_Util.executionError(broker, States.REMOTE_STATE, (LocalWorkerProvider)peerTestStub.getObject(), 
				workerTestStub, error, false, testJob1);
		
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
	            + Final Block:
	                  # Condition: mem = 256
	                  # IOEntry:
	                        * Command: GET
	                        * Source File: remoteFile1.txt
	                        * Destination localFile1.txt
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
	* Call errorOcurred message, giving a failCause with the message error and type IO_ERROR;
	* Verify if the following debug message was logged:
	      o Adding to blacklist. Task: X, Worker: Y Where X is the task ID and Y is the worker deployment ID.
	* Verify if the following debug message was logged:
	      o Worker unwanted: X Where X is the worker deployment ID.
	* Verify if the Worker Provider with username = test received an unwantedWorker message;
	* Verify if the following debug message was logged:
	      o Grid process FAILED X. Job ended: true Where X is the Grid Process handle.
	* Verify if the Worker Provider with username = test received a finishRequest message;
	* Verify if the Worker Provider with username = test received a resumeRequest message;
	* @throws Exception
	*/
	@ReqTest(test="AT-315.6", reqs="REQ315")
	@Category(JDLCompliantTest.class) @Test public void test_at_315_6_1_ErrorMessageOnExecutionInFinalState() throws Exception {
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
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_OUTPUT_JOB, peers);
		
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		List<Worker> workers = new ArrayList<Worker>();
		
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workers.add((Worker) workerTestStub.getObject());
		workerTestStubs.add(workerTestStub);
		
		//call scheduler
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		String[] remoteFiles = new String[1];
		remoteFiles[0] = testJob1.getJobSpec().getTaskSpecs().get(0).getInitBlock().getEntry("").get(0).getSourceFile();
		//call workerIsReady
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, States.SCHEDULED_STATE, 
				1, 1, requestID, null, remoteFiles);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFiles[0], 1, 1, handle, requestID, false);
		
		//call hereIsGridProcessResult 
		
		String[] fileNames = new String[1];
		fileNames[0] = "remoteFile1.txt";
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, true, peerTestStub, testJob1, fileNames);
		
		//Call fileTransferRequestReceived message
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile1.txt", workerTestStub, testJob1);
		
		//Call ErrorOcurred message
		GridProcessError error = new GridProcessError(new Throwable("error"), GridProcessErrorTypes.IO_ERROR);
		req_315_Util.executionError(broker, States.FINAL_STATE, (LocalWorkerProvider)peerTestStub.getObject(),
				workerTestStub, error, false, testJob1);
	}

	/**
	 *     *  Create and start the Broker with the correct public key;
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
	* Add a job with the attributes: label: "Test Job 2" and one Task with remote attribute 
	* 	"echo Hello World 2"
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
	* Call errorOcurred message, giving a failCause with the message error and type IO_ERROR;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Final
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-315.7", reqs="REQ315")
	@Category(JDLCompliantTest.class) @Test public void test_at_315_7_1_ErrorMessage() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		LocalWorkerProvider lwp = req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		List<LocalWorkerProvider> peersList = new LinkedList<LocalWorkerProvider>();
		peersList.add(lwp);
		
		//add jobs
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_IO_JOB, peersList);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peersList);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		List<Worker> workers = new ArrayList<Worker>();
		
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		
		TestStub workerTestStub  = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workers.add((Worker) workerTestStub.getObject());
		workerTestStubs.add(workerTestStub);
		
		//call scheduler
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//call workerIsReady
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob1.getJobSpec().getTaskSpecs().get(0).getInitBlock().getEntry("").get(0).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, null, remoteFileNames);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFileNames[0], 1, 1, handle, requestID, false);
		
		//call hereIsGridProcessResult 
		String[] fileNames = new String[2];
		fileNames[0] = "remoteFile1.txt";
		fileNames[1] = "remoteFile2.txt";
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, true, peerTestStub, testJob1, fileNames);
		
		//Call fileTransferRequestReceived message again with filename remoteFile1.txt
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile1.txt", workerTestStub, testJob1);
		
		//Call fileTransferRequestReceived message again with filename remoteFile2.txt
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile2.txt", workerTestStub, testJob1);
		
		//call incomingTransferCompleted message with fileName remoteFile1.txt;
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile1.txt", false, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob1);
		
		//call incomingTransferCompleted message with fileName remoteFile2.txt;
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile2.txt", true, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//Call ErrorOcurred message
		GridProcessError error = new GridProcessError(new Throwable("error"), GridProcessErrorTypes.IO_ERROR);
		req_315_Util.executionErrorWithoutWorkers(broker, workerTestStub, error);
	}
}
