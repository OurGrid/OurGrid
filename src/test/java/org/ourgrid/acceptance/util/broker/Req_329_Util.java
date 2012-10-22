package org.ourgrid.acceptance.util.broker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.business.scheduler.workqueue.JobInfo;
import org.ourgrid.broker.business.scheduler.workqueue.WorkQueueExecutionController;
import org.ourgrid.broker.communication.sender.BrokerResponseControl;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.job.Job;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_329_Util extends BrokerAcceptanceUtil {
	
	private int maxReplicas = 1;

	public Req_329_Util(ModuleContext context) {
		super(context);
		maxReplicas = context.parseIntegerProperty(BrokerConfiguration.PROP_MAX_REPLICAS);
	}
	
	public List<Long> doSchedule(BrokerServerModule component, List<TestStub> workerTestStubs, List<TestStub> peerStubs, List<TestJob> testJobs, 
			List<GridProcessHandle> replicaHandles) {
		
		CommuneLogger newLogger = component.getLogger();
		component.setLogger(newLogger);
		
		WorkerClient workerClient = getWorkerClient(component);
		
		List<Long> requests = new ArrayList<Long>();
		int handleIndex = 0;
		
		Iterator<TestStub> itWorkers = workerTestStubs.iterator();
		
		EasyMock.reset(newLogger);
		
		for (TestStub peerStub : peerStubs) {
			LocalWorkerProvider lwp = (LocalWorkerProvider) peerStub.getObject();
			EasyMock.reset(lwp);
		}	
		
		for (TestJob testJob : testJobs) {
			testJob.setJob(JobInfo.getInstance().getJob(testJob.getJob().getJobId()));
			
			for (TestStub peerStub : peerStubs) {
				LocalWorkerProvider lwp = (LocalWorkerProvider) peerStub.getObject();
			
				RequestSpecification requestByPeer = testJob.getRequestByPeer(application, lwp);
				
				if (requestByPeer != null) {
					long requestID = requestByPeer.getRequestId();
					requests.add(requestID);
					Job job = testJob.getJob();
					
					if (!isJobSatisfied(job, context)) {
						for (int i = 0; i < maxReplicas; i++) {
							if (itWorkers.hasNext()) {
								TestStub testStub = itWorkers.next();
								Worker worker = (Worker) testStub.getObject();
								EasyMock.reset(worker);
								newLogger.debug("Executing replica: " + replicaHandles.get(handleIndex) + ", Worker: " + testStub.getDeploymentID());
								worker.startWork(workerClient, requestID, replicaHandles.get(handleIndex));
								EasyMock.replay(worker);
								handleIndex++;
							}
						}	
					} 
					
					lwp.pauseRequest(requestID);
				}	
			}	
		}
		
		EasyMock.replay(newLogger);
		
		for (Iterator<TestStub> iterator2 = peerStubs.iterator(); iterator2.hasNext();) {
			TestStub peerStub = (TestStub) iterator2.next();
			LocalWorkerProvider lwp = (LocalWorkerProvider) peerStub.getObject();
			EasyMock.replay(lwp);
		} 	
		
		Set<SchedulerIF> schedulers = BrokerDAOFactory.getInstance().getJobDAO().getSchedulers();
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		for (SchedulerIF schedulerIF : schedulers) {
			schedulerIF.schedule(responses);
		}
		
/*		ObjectDeployment od = getBrokerControlDeployment(component);
		CommandExecutor.getInstance().execute(outputCommands, component.createServiceManager(od));*/
		
		ObjectDeployment od = getBrokerControlDeployment(component);
		BrokerResponseControl.getInstance().execute(responses, component.createServiceManager(od));
		
			
		for (Iterator<TestStub> iterator2 = peerStubs.iterator(); iterator2.hasNext();) {
			TestStub peerStub = (TestStub) iterator2.next();
			LocalWorkerProvider lwp = (LocalWorkerProvider) peerStub.getObject();
			EasyMock.verify(lwp);
		} 
		
		for(TestStub testStub: workerTestStubs){
			Worker worker = (Worker) testStub.getObject();
			EasyMock.verify(newLogger);
			EasyMock.verify(worker);
		}
		
		return requests;
	}
		
	
	public long doSchedule(BrokerServerModule component, List<TestStub> workerTestStubs, TestStub peerStub, TestJob testJob, 
			GridProcessHandle replicaHandle) {
		
		List<TestJob> jobs = new ArrayList<TestJob>();
		jobs.add(testJob);
		
		List<GridProcessHandle> handles = new ArrayList<GridProcessHandle>();
		handles.add(replicaHandle);
		
		List<TestStub> peerStubs = new ArrayList<TestStub>();
		peerStubs.add(peerStub);
		
		List<Long> requests = doSchedule(component, workerTestStubs, peerStubs, jobs, handles);
		
		long requestID = 0;
		if (requests != null && !requests.isEmpty()) {
			requestID = requests.get(0);
		}
		
		return requestID;
	}
	
	private boolean isJobSatisfied(Job job, ModuleContext context) {
		
		String replicas = context.getProperty(BrokerConfiguration.PROP_MAX_REPLICAS);
		String fails = context.getProperty(BrokerConfiguration.PROP_MAX_FAILS);
		String blFails = context.getProperty(BrokerConfiguration.PROP_MAX_BL_FAILS);
		
		WorkQueueExecutionController heuristic = new WorkQueueExecutionController(
				Integer.valueOf(replicas), Integer.valueOf(fails), Integer.valueOf(blFails)); 
		
		return heuristic.isJobSatisfied(job);
	}
}
