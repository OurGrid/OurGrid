package org.ourgrid.acceptance.broker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.ourgrid.acceptance.util.broker.Req_302_Util;
import org.ourgrid.acceptance.util.broker.Req_304_Util;
import org.ourgrid.acceptance.util.broker.Req_309_Util;
import org.ourgrid.acceptance.util.broker.Req_311_Util;
import org.ourgrid.acceptance.util.broker.Req_312_Util;
import org.ourgrid.acceptance.util.broker.Req_314_Util;
import org.ourgrid.acceptance.util.broker.Req_327_Util;
import org.ourgrid.acceptance.util.broker.Req_328_Util;
import org.ourgrid.acceptance.util.broker.Req_329_Util;
import org.ourgrid.acceptance.util.broker.Req_330_Util;
import org.ourgrid.acceptance.util.broker.TestJob;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.WorkerEntry;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.business.scheduler.workqueue.JobInfo;
import org.ourgrid.broker.communication.actions.WorkerIsReadyMessageHandle;
import org.ourgrid.broker.communication.sender.BrokerResponseControl;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.control.BrokerControl;
import org.ourgrid.common.interfaces.control.BrokerControlClient;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.common.job.Job;
import org.ourgrid.common.job.Task;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponentContextFactory;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class TestManyJob extends BrokerAcceptanceTestCase {
	
	@After
    public void tearDown() throws Exception {
		createComponentContext("1", "1");
		super.tearDown();
    }
	private String peerUserAtServer = "test@servertest";
	
	@Test public void test_Replicaton_ManyWorkers() throws Exception {
		
		
		final String maxReplicas = "2";
		
		TestContext context = createComponentContext(maxReplicas);
		
		Req_302_Util req_302_Util = new Req_302_Util(context);
		Req_314_Util req_314_Util = new Req_314_Util(context);
		Req_329_Util req_329_Util = new Req_329_Util(context);

		
		//start broker
		BrokerServerModule broker = req_302_Util.startBroker(peerUserAtServer);
		
		//adding jobs
		List<TestStub> workerStubs = prepareTestObjetcts(broker, context, 5);
		
		WorkerClient workerClient = req_314_Util.getWorkerClient(broker);
		ObjectDeployment wcOD = req_329_Util.getWorkerClientDeployment(broker);
		
		//sending workerIsReady message
		for (TestStub workerStub : workerStubs) {
			AcceptanceTestUtil.setExecutionContext(broker, wcOD, workerStub.getDeploymentID());
			workerClient.sendMessage(new WorkerIsReadyMessageHandle());
		}
		
		Thread.sleep(15000);
		
		Job job1 = getJobInfo().getJob(1);
		Job job2 = getJobInfo().getJob(1);
		Job job3 = getJobInfo().getJob(1);
		Job job4 = getJobInfo().getJob(1);
		Job job5 = getJobInfo().getJob(1);
		
		
		
		
		
		/*List<WorkerEntry> workers = getTaskWorkers(1, 1, job, GridProcessState.RUNNING);
		assertTrue(workers.size() == 1);
		
		
		Thread.sleep(15000);
		
		job = getJobInfo().getJob(1);

		workers = getTaskWorkers(1, 1, job, GridProcessState.RUNNING);
		assertTrue(workers.size() == 0);
		
		workers = getTaskWorkers(1, 2, job, GridProcessState.RUNNING);
		assertTrue(workers.size() == 0);
		
		workers = getTaskWorkers(1, 3, job, GridProcessState.RUNNING);
		assertTrue(workers.size() == 0);
		
		workers = getTaskWorkers(1, 4, job, GridProcessState.RUNNING);
		assertTrue(workers.size() == 0);
		
		workers = getTaskWorkers(1, 5, job, GridProcessState.RUNNING);
		assertTrue(workers.size() == 0);
		
		workers = getTaskWorkers(1, 1, job, GridProcessState.FINISHED);
		assertTrue(workers.size() == 1);
		
		workers = getTaskWorkers(1, 2, job, GridProcessState.FINISHED);
		assertTrue(workers.size() == 1);
		
		workers = getTaskWorkers(1, 3, job, GridProcessState.FINISHED);
		assertTrue(workers.size() == 1);
		
		workers = getTaskWorkers(1, 4, job, GridProcessState.FINISHED);
		assertTrue(workers.size() == 1);
		
		workers = getTaskWorkers(1, 5, job, GridProcessState.FINISHED);
		assertTrue(workers.size() == 1);*/
	}
	
	
	private DeploymentID getWorkerID(List<TestStub> stubs, ServiceID serviceID) {
		
		String login = serviceID.getUserName() + "@" + serviceID.getServerName();
		
		ServiceID workerServiceID = null;
		for (TestStub testStub : stubs) {
			workerServiceID = testStub.getDeploymentID().getServiceID();
			
			String workerLogin = workerServiceID.getUserName() + "@" + workerServiceID.getServerName();
			if (login.equals(workerLogin)) {
				return testStub.getDeploymentID();
			}
		}
		
		return null;
	}
	
	private List<TestStub> prepareTestObjetcts(BrokerServerModule broker, TestContext context, int jobs) 
		throws Exception {
		
		Req_309_Util req_309_Util = new Req_309_Util(context);
		Req_311_Util req_311_Util = new Req_311_Util(context);
		Req_327_Util req_327_Util = new Req_327_Util(context);
		Req_328_Util req_328_Util = new Req_328_Util(context);
		Req_312_Util req_312_Util = new Req_312_Util(context);
		Req_329_Util req_329_Util = new Req_329_Util(context);
		Req_330_Util req_330_Util = new Req_330_Util(context);
		
		//call doNotifyRecovery passing a peer with username test
		DeploymentID deploymentID = req_328_Util.createPeerDeploymentID("publicKey1", getPeerSpec());
		TestStub peerTestStub = req_327_Util.notifyPeerRecovery(getPeerSpec(), deploymentID, broker);
		
		//do login with peer
		req_311_Util.verifyLogin(broker, "publickey1", false, false, null, peerTestStub);
		
		List<TestJob> testJobs = prepareJobs(jobs, broker, context, peerTestStub, true);
		
		//Receveing workers
		List<TestStub> workerStubs = new ArrayList<TestStub>();
		
		TestStub workerTestStub = null;
		WorkerSpecification workerSpec = null;
		Map<String, String> attributes = null;
		
		for (int i = 1; i <= jobs; i++) {
			
			attributes = new HashMap<String, String>();
			attributes.put(OurGridSpecificationConstants.ATT_MEM, "mem = 256");
			
			workerSpec = new WorkerSpecification(attributes);
			workerSpec.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, "worker" + i);
			workerSpec.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, "ortho.ece.ubc.ca");
			
			TestJob testJob = testJobs.get(i-1);
			
			workerTestStub = req_312_Util.receiveWorker(broker, "workerPublicKey" + i, true, true, true, true, 
					workerSpec, "publickey1", peerTestStub, testJob);
			req_330_Util.notifyWorkerRecovery(broker, workerTestStub.getDeploymentID());
			
			workerStubs.add(workerTestStub);
		}
		
		//Scheduling
		EasyMock.reset(broker.getLogger());
		
		for (TestStub testStub : workerStubs) {
			Worker worker = (Worker) testStub.getObject();
			EasyMock.reset(worker);
		}
		
		EasyMock.reset(broker.getLogger());
		
		Set<SchedulerIF> schedulers = BrokerDAOFactory.getInstance().getJobDAO().getSchedulers();
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		for (SchedulerIF schedulerIF : schedulers) {
			schedulerIF.schedule(responses);
		}
		
		ObjectDeployment od = req_329_Util.getBrokerControlDeployment(broker);
		BrokerResponseControl.getInstance().execute(responses, broker.createServiceManager(od));
		
		return workerStubs;
	}
	
	private List<TestJob> prepareJobs(int jobs, BrokerServerModule broker, TestContext context, 
			TestStub peerTestStub, boolean hasGetFiles) 
		throws Exception {
		
		Req_304_Util req_304_Util = new Req_304_Util(context);
		Req_314_Util req_314_Util = new Req_314_Util(context);
		
		//Job creation
		List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>();
		for (int i = 1; i <= 1; i++) {
			
			TaskSpecification taskSpec = null;
			if (hasGetFiles) {
				IOBlock finalBlock = new IOBlock();
				finalBlock.putEntry(new IOEntry("GET", "remoteFile1.txt", "localFile1.txt"));
				finalBlock.putEntry(new IOEntry("GET", "remoteFile2.txt", "localFile2.txt"));
				taskSpec = new TaskSpecification(null, "echo", finalBlock, "echo");
			} else {
				taskSpec = new TaskSpecification(null, "echo", null, "echo");
			}
			
			taskSpec.setSourceDirPath(BrokerAcceptanceTestCase.BROKER_TEST_DIR);
			taskSpecs.add(taskSpec);
		}
		
		BrokerControl brokerControl = req_304_Util.getBrokerControl(broker);
		ObjectDeployment brokerOD = req_314_Util.getBrokerControlDeployment(broker);
		BrokerControlClient brokerControlClientMock = EasyMock.createNiceMock(BrokerControlClient.class);
		
		LocalWorkerProvider lwp = (LocalWorkerProvider) peerTestStub.getObject();
		EasyMock.reset(lwp);
		EasyMock.reset(broker.getLogger());
		
		AcceptanceTestUtil.setExecutionContext(broker, brokerOD, brokerOD.getDeploymentID().getPublicKey());
		
		List<TestJob> testJobs = new ArrayList<TestJob>();
		
		for (int i = 1; i <= jobs; i++) {
			JobSpecification jobSpec = new JobSpecification("Test Job", "mem = 256", taskSpecs);
			brokerControl.addJob(brokerControlClientMock, jobSpec);
			Job job = getJobInfo().getJob(i);
			testJobs.add(new TestJob(job, jobSpec));
		}
		
		return testJobs;
	}
	
	
	private List<WorkerEntry> getTaskWorkers(int jobID, int taskID, Job job, GridProcessState state) {
		List<WorkerEntry> taskWorkers = new ArrayList<WorkerEntry>();
		
		for (Task task : job.getTasks()) {
			if (task.getJobId() == jobID && task.getTaskid() == taskID && task.getState().equals(state)) {
				for (GridProcess process : task.getGridProcesses()) {
					if (process.getState().equals(state)) {
						taskWorkers.add(process.getWorkerEntry());
					}	
				}
			}	
		}
		
		return taskWorkers;
	}
	
	protected TestContext createComponentContext(String maxReplicas, String maxBLFails, String maxFails) {
		TestContext testContext = new TestContext(
				new PeerComponentContextFactory(
						new PropertiesFileParser(BrokerAcceptanceTestCase.PROPERTIES_FILENAME
						)).createContext());
		
		testContext.set(BrokerConfiguration.PROP_MAX_REPLICAS, maxReplicas);
		testContext.set(BrokerConfiguration.PROP_MAX_BL_FAILS, maxBLFails);
		testContext.set(BrokerConfiguration.PROP_MAX_FAILS, maxFails);
		
		Properties properties = new Properties();
		for (Entry<String, String> entry : testContext.getProperties().entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue());
		}
		
		try {
			properties.store(new FileOutputStream(BrokerAcceptanceTestCase.PROPERTIES_FILENAME), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return testContext;
	}
	
	
	
	protected TestContext createComponentContext(String maxReplicas) {
		return createComponentContext(maxReplicas, "1", "3");
	}
	
	protected TestContext createComponentContext(String maxReplicas, String maxBLFails) {
		return createComponentContext(maxReplicas, maxBLFails, "3");
	}
	
	private JobInfo getJobInfo() {
		return JobInfo.getInstance();
	}
	
}
