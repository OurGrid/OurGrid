package org.ourgrid.acceptance.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.easymock.classextension.EasyMock;
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
import org.ourgrid.acceptance.util.broker.Req_316_Util;
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
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest(reqs="REQ316")
public class Req_316_Test extends BrokerAcceptanceTestCase {
	private Req_302_Util req_302_Util;
	private Req_304_Util req_304_Util;
	private Req_309_Util req_309_Util;
	private Req_311_Util req_311_Util;
	private Req_312_Util req_312_Util;
	private Req_315_Util req_315_Util;
	private Req_316_Util req_316_Util;
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
		req_315_Util = new Req_315_Util(super.createComponentContext());
		req_316_Util = new Req_316_Util(super.createComponentContext());
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
     * Call HereisFileInfo? message;
     * Verify if the following warn message was logged:
     *     o Invalid operation. The execution is on the state: Scheduled
	 */
	@ReqTest(test="AT-316.1", reqs="REQ316")
	@Test public void test_at_316_1_ReceiveInfoOfAScheduledWorker() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "Echo Hello World", "Test Job", lwpList);
		req_304_Util.addJob(true, 2, broker, "Echo Hello World 2", "Test Job 2", lwpList);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				new WorkerSpecification(attributes), "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerTestStubs.add(workerTestStub);
		
		//call schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.SCHEDULED_STATE, Operations.EXECUTE_COMMAND_OPERATION, false, 
				workerTestStub, testJob1, null);
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
          o Executing replica: 1.1.1, Worker: X. Where X is the toString() method 
          	of the Worker;
    * Verify if the worker's startWork message was called;
    * Verify if the following info message was logged:
          o Sending file file.txt to X Where X is the worker deployment ID;
    * Verify if the Worker receveid a transferRequestReceived message;
    * Call HereisFileInfo? message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Init

	 * @throws Exception
	 */
	@ReqTest(test="AT-316.2", reqs="REQ316")
	@Test public void test_at_316_2_ReceiveInfoOfAInitedWorker() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		broker.setTimer(EasyMock.createNiceMock(ScheduledExecutorService.class));
		broker.setFileTransferManager(EasyMock.createMock(FileTransferManager.class));
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		IOEntry entry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, lwpList);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", lwpList);
		
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
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, null);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob, 
				BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", requestID, "Class.class");
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
    * Call HereisFileInfo? message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Remote

	 * @throws Exception
	 */
	@ReqTest(test="AT-316.3", reqs="REQ316")
	@Test public void test_at_316_3_ReceiveInfoOfARemoteWorker() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);

		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		IOEntry entry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, lwpList);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", lwpList);
		
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
		//Verify if the logged debug message
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, null);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		
		// Call outgoingTransferCompleted message;
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.REMOTE_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob, "Class.class");
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
          o Executing replica: 1.1.1, Worker: X. Where X is the toString() method of the 
          	Worker;
    * Verify if the worker's startWork message was called;
    * Verify if the following debug message was logged:
          o File info requested: fileUnknown.txt, handle: X,  replica: Y Where X is the 
          	transfer handle and Y is the replica handle.
    * Verify if the worker's getFileInfo message was called;
    * Call hereIsFileInfo message, setting the FileInfo? digest field to fileUnknown.txt;
    * Verify if the following info message was logged:
          o Sending file file.txt to X Where X is the worker deployment ID;
    * Verify if the Worker receveid a transferRequestReceived message;

	 * @throws Exception
	 */
	@ReqTest(test="AT-316.4", reqs="REQ316")
	@Test public void test_at_316_4_ReceiveFileInfo() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		IOEntry entry = new IOEntry("STORE", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, lwpList);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", lwpList);
		
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
		
		//Verify the logged debug message
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		
		String[] remoteFiles = {"Class.class"};
		String[] localFiles = {BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt"};
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_INFO_OPERATION, false, null, 
				1, 1, requestID, testJob, localFiles, remoteFiles);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, workerTestStub, testJob, BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt",
				BrokerAcceptanceTestCase.BROKER_TEST_DIR + "fileUnknown.txt", "Class.class");
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
    * Verify if the following debug message was logged:
          o File info requested: file.txt, handle: X,  replica: Y Where X is the transfer handle and Y is the replica handle.
    * Verify if the worker's getFileInfo message was called;
    * Call hereIsFileInfo message, setting the FileInfo? digest field to file.txt;
    * Verify if the following info message was logged:
          o File file.txt exists on storage. Skipping transfer;
    * Verify if the Worker receveid a remoteExecute message;

	 * @throws Exception
	 */
	@ReqTest(test="AT-316.5", reqs="REQ316")
	@Test public void test_at_316_5_ReceiveFileInfo() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		IOEntry entry = new IOEntry("STORE", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, lwpList);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", lwpList);
		
		//call here is worker with the request ID generated by job 1.
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		String[] remoteFiles = {"Class.class"};
		String[] localFiles = {BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt"};
		
		//Verify if the logged debug message
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_INFO_OPERATION, false, States.SCHEDULED_STATE,
				1, 1, requestID, testJob1, localFiles, remoteFiles);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.INIT_STATE, Operations.SEND_FILE_INFO_OPERATION, true, workerTestStub, testJob1,
				BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", requestID, "Class.class");
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
    * Call HereisFileInfo? message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Final

	 * @throws Exception
	 */
	@ReqTest(test="AT-316.6", reqs="REQ316")
	@Test public void test_at_316_6_ReceiveFileInfo() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		IOEntry entry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, lwpList);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", lwpList);
		
		//call here is worker with the request ID generated by job 1.
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, null);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		//call hereIsGridProcessResult 
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, false, peerTestStub, testJob1);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob1, "Class.class");
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
    * Call hereIsGridProcessResult message;
    * Verify if the Worker receveid a getFiles message;
    * Call fileTransferRequestReceived message;
    * Call HereisFileInfo? message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Final

	 * @throws Exception
	 */
	@ReqTest(test="AT-316.7", reqs="REQ316")
	@Test public void test_at_316_7_ExecutingDownloadAndReceiveFileInfoForExecution() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		IOEntry entry = new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class");
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "echo Hello World", "Test Job", "mem = 256", entry, lwpList);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", lwpList);
		
		//call here is worker with the request ID generated by job 1.
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, null);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		//call hereIsGridProcessResult 
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, false, peerTestStub,testJob1);
		
		//call fileTransferRequestReceived message
		req_324_Util.transferRequestReceived(broker, workerTestStub, BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", true);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob1, "Class.class");
	}
	
	
	/**
	*  Create and start the Broker with the correct public key;
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
    * Call HereisFileInfo? message;
    * Verify if the following warn message was logged:
          o Invalid operation. The execution is on the state: Final

	 * @throws Exception
	 */
	@ReqTest(test="AT-316.8", reqs="REQ316")
	@Test public void test_at_316_8_ReceiveFileInfo() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		IOBlock initBlock = new IOBlock();
		initBlock.putEntry(new IOEntry("PUT", BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", "Class.class"));
		IOBlock finalBlock = new IOBlock();
		finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
		finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
		TaskSpecification task = new TaskSpecification(initBlock, "echo Hello World", finalBlock, "echo");
		task.setSourceDirPath(BROKER_TEST_DIR);
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, "Test Job", "mem = 256", task, peers);
		req_304_Util.addJob(true, 2, broker, "echo Hello World 2", "Test Job 2", peers);
		
		//call here is worker with the request ID generated by job 1.
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, "xmpp.ourgrid.org");
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, "username");
		WorkerSpecification workerSpec = new WorkerSpecification(attributes);
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, null);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, handle, requestID);
		
		//call hereIsGridProcessResult 
		String[] fileNames = new String[2];
		fileNames[0] = "remoteFile1.txt";
		fileNames[1] = "remoteFile2.txt";
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, true, fileNames);
		
		//Call fileTransferRequestReceived message again with filename remoteFile1.txt
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile1.txt", workerTestStub, testJob1);
		
		//Call fileTransferRequestReceived message again with filename remoteFile2.txt
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile2.txt", workerTestStub, testJob1);
		
		//call incomingTransferCompleted message with fileName remoteFile1.txt;
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile1.txt", false, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//call incomingTransferCompleted message with fileName remoteFile2.txt;
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile2.txt", true, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob1, "Class.class");
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
	 * Call HereisFileInfo? message;
	 * Verify if the following warn message was logged:
	 *     o Invalid operation. The execution is on the state: Scheduled
	 */
	@ReqTest(test="AT-316.1", reqs="REQ316")
	@Category(JDLCompliantTest.class) @Test public void test_at_316_1_1_ReceiveInfoOfAScheduledWorker() throws Exception {
		//Creates and starts
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//notify and verify if the debug message was logged
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.ECHO_JOB, lwpList);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, lwpList);
		
		//try to receive worker
		List<TestStub> workerTestStubs = new ArrayList<TestStub>();
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		workerTestStubs.add(workerTestStub);
		
		//call schedule
		req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.SCHEDULED_STATE, Operations.EXECUTE_COMMAND_OPERATION, false, 
				workerTestStub, testJob1, null);
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
	      o Executing replica: 1.1.1, Worker: X. Where X is the toString() method 
	      	of the Worker;
	* Verify if the worker's startWork message was called;
	* Verify if the following info message was logged:
	      o Sending file file.txt to X Where X is the worker deployment ID;
	* Verify if the Worker receveid a transferRequestReceived message;
	* Call HereisFileInfo? message;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Init
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-316.2", reqs="REQ316")
	@Category(JDLCompliantTest.class) @Test public void test_at_316_2_1_ReceiveInfoOfAInitedWorker() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, lwpList);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, lwpList);
		
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
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, null, remoteFileNames);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob, 
				BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", requestID, "Class.class");
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
	* Call HereisFileInfo? message;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Remote
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-316.3", reqs="REQ316")
	@Category(JDLCompliantTest.class) @Test public void test_at_316_3_1_ReceiveInfoOfARemoteWorker() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
	
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		TestJob testJob = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, lwpList);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, lwpList);
		
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
		//Verify if the logged debug message
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, null, remoteFileNames);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob, "Class.class");
		
		// Call outgoingTransferCompleted message;
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFileNames[0], 1, 1, handle, requestID, false);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.REMOTE_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob, "Class.class");
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
	* Call HereisFileInfo? message;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Final
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-316.6", reqs="REQ316")
	@Category(JDLCompliantTest.class) @Test public void test_at_316_6_1_ReceiveFileInfo() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, lwpList);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, lwpList);
		
		//call here is worker with the request ID generated by job 1.
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob1.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, null, remoteFileNames);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFileNames[0], 1, 1, handle, requestID, false);
		
		//call hereIsGridProcessResult 
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, false, peerTestStub, testJob1);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob1, "Class.class");
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
	* Call hereIsGridProcessResult message;
	* Verify if the Worker receveid a getFiles message;
	* Call fileTransferRequestReceived message;
	* Call HereisFileInfo? message;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Final
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-316.7", reqs="REQ316")
	@Category(JDLCompliantTest.class) @Test public void test_at_316_7_1_ExecutingDownloadAndReceiveFileInfoForExecution() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		List<LocalWorkerProvider> lwpList = new LinkedList<LocalWorkerProvider>();
		lwpList.add((LocalWorkerProvider)peerTestStub.getObject());
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_JOB, lwpList);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, lwpList);
		
		//call here is worker with the request ID generated by job 1.
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob1.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, null, remoteFileNames);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFileNames[0], 1, 1, handle, requestID, false);
		
		//call hereIsGridProcessResult 
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, false, peerTestStub,testJob1);
		
		//call fileTransferRequestReceived message
		req_324_Util.transferRequestReceived(broker, workerTestStub, BrokerAcceptanceTestCase.BROKER_TEST_DIR + "Class.class", true);
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob1, "Class.class");
	}

	/**
	*  Create and start the Broker with the correct public key;
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
	* Call HereisFileInfo? message;
	* Verify if the following warn message was logged:
	      o Invalid operation. The execution is on the state: Final
	
	 * @throws Exception
	 */
	@ReqTest(test="AT-316.8", reqs="REQ316")
	@Category(JDLCompliantTest.class) @Test public void test_at_316_8_1_ReceiveFileInfo() throws Exception {
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		List<LocalWorkerProvider> peers = new LinkedList<LocalWorkerProvider>();
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		peers.add((LocalWorkerProvider)peerTestStub.getObject());
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publicKey1", false, false, null, peerTestStub);
		
		//add jobs
		TestJob testJob1 = req_304_Util.addJob(true, 1, broker, JDLUtils.JAVA_IO_JOB, peers);
		req_304_Util.addJob(true, 2, broker, JDLUtils.ECHO_JOB, peers);
		
		//call here is worker with the request ID generated by job 1.
		WorkerSpecification workerSpec = SDFClassAdsSemanticAnalyzer.compile( ClassAdsUtils.SIMPLE_MACHINE_WITH_MEMORY ).get( 0 );
		TestStub workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey", true, true, true, true, 
				workerSpec, "publicKey1", peerTestStub, testJob1);
		
		req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
		
		//call schedule with the correct public key
		List<TestStub> workerTestStubs = new LinkedList<TestStub>();
		workerTestStubs.add(workerTestStub);
		long requestID = req_329_Util.doSchedule(broker, workerTestStubs, peerTestStub, testJob1, new GridProcessHandle(1, 1, 1)); 
		
		//call workerIsReady
		List<Worker> workers = new LinkedList<Worker>();
		workers.add((Worker)workerTestStub.getObject());
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = testJob1.getJobSpec().getTaskSpecs().get( 0 ).getInitBlock().getEntry( "" ).get( 0 ).getSourceFile();
		
		req_313_Util.callWorkerIsReady(broker, workerTestStubs, Operations.SEND_FILE_OPERATION, false, null, 1, 1,
				null, null, remoteFileNames);
		
		OutgoingTransferHandle handle = req_321_Util.getOutgoingTransferHandle(testJob1, "Class.class");
		
		//call outgoing transfer completed
		req_321_Util.testOutgoingTransferCompleted(broker, States.INIT_STATE, Operations.SEND_FILE_OPERATION, workerTestStub, remoteFileNames[0], 1, 1, handle, requestID, false);
		
		//call hereIsGridProcessResult 
		String[] fileNames = new String[2];
		fileNames[0] = "remoteFile1.txt";
		fileNames[1] = "remoteFile2.txt";
		req_314_Util.executionResult(broker, States.REMOTE_STATE, workerTestStub, true, fileNames);
		
		//Call fileTransferRequestReceived message again with filename remoteFile1.txt
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile1.txt", workerTestStub, testJob1);
		
		//Call fileTransferRequestReceived message again with filename remoteFile2.txt
		req_324_Util.transferRequestReceived(broker, States.FINAL_STATE, "remoteFile2.txt", workerTestStub, testJob1);
		
		//call incomingTransferCompleted message with fileName remoteFile1.txt;
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile1.txt", false, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//call incomingTransferCompleted message with fileName remoteFile2.txt;
		req_325_Util.testIncomingTransferCompleted(broker, States.FINAL_STATE, true, "remoteFile2.txt", true, 
				(LocalWorkerProvider)peerTestStub.getObject(), workerTestStub, testJob1, new GridProcessHandle(1, 1, 1));
		
		//call hereIsFileInfo
		req_316_Util.receiveInfo(broker, States.FINAL_STATE, Operations.SEND_FILE_OPERATION, false, workerTestStub, testJob1, "Class.class");
	}
}
