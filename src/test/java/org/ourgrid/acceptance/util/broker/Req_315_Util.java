package org.ourgrid.acceptance.util.broker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.business.scheduler.workqueue.JobInfo;
import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.job.Job;
import org.ourgrid.matchers.GridProcessAccountingMatcher;
import org.ourgrid.matchers.MessageStartsWithMatcher;
import org.ourgrid.worker.business.controller.GridProcessError;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_315_Util extends BrokerAcceptanceUtil {
	
	public Req_315_Util(ModuleContext context) {
		super(context);
	}
	
	public void executionError(BrokerServerModule component, States state) {
		executionError(component, state, null, null, null, false, null);
	}
	
	public void executionError(BrokerServerModule component, States state, LocalWorkerProvider lwp, TestStub testStub, GridProcessError error,
			 boolean invalidOperation, TestJob jobStub) {
		executionError(component, state, lwp, testStub, error, invalidOperation, 1, 1, jobStub);
	}
	
	public void executionError(BrokerServerModule component, States state, LocalWorkerProvider lwp, TestStub testStub, GridProcessError error,
			 boolean invalidOperation, int jobID, int tasks, TestJob jobStub) {
		
		CommuneLogger newLogger = component.getLogger();
		EasyMock.reset(newLogger);
		
//		Worker worker = (Worker) testStub.getObject();
		
		WorkerClient workerClient = getWorkerClient(component);
		ObjectDeployment wcOD = getWorkerClientDeployment(component);

		if(invalidOperation){
			if(state == States.FINAL_STATE){
				newLogger.warn("Invalid operation. The execution is on the state: Final");
			}
			else if (state == States.SCHEDULED_STATE){
				newLogger.warn("Invalid operation. The execution is on the state: Scheduled");
			} 
			else if (state == States.INIT_STATE){
				newLogger.error("Invalid operation. The execution is on the state: Init");
			} 
		}
		else {
			newLogger.debug("Adding to blacklist. Task: " + tasks +", Worker: " + testStub.getDeploymentID());
			newLogger.debug("Worker unwanted: " + testStub.getDeploymentID());
	
			RequestSpecification requestSpec = jobStub.getRequestByPeer(application, lwp);
			EasyMock.reset(lwp);
			lwp.reportReplicaAccounting(GridProcessAccountingMatcher.eqMatcher());
			lwp.unwantedWorker(testStub.getDeploymentID().getServiceID(), requestSpec);
			
			Job job = JobInfo.getInstance().getJob(jobID);
			
			boolean jobEnded = hasJobEnded(job);
			
			GridProcessHandle process = new GridProcessHandle(jobID, tasks, 1);

			newLogger.debug(MessageStartsWithMatcher.eqMatcher(
					"Grid process FAILED " + process + ". Job ended: " + jobEnded));
			
			if (jobEnded) {
				lwp.finishRequest(requestSpec);
			}
			
			lwp.resumeRequest(requestSpec.getRequestId());

			EasyMock.replay(lwp);
		}
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, testStub.getDeploymentID());
		workerClient.sendMessage(new ErrorOcurredMessageHandle(error));
		
		EasyMock.verify(newLogger);

		if (lwp != null) {
			EasyMock.verify(lwp);
		}

	}
	
	private boolean hasJobEnded(Job job) {
		GridProcessState state = job.getState();
		
		return state.equals(GridProcessState.FAILED) || 
			state.equals(GridProcessState.CANCELLED) || state.equals(GridProcessState.FINISHED);
	}
	
	public void executionErrorWithoutWorkers(BrokerServerModule component, TestStub testStub, GridProcessError error){
		CommuneLogger newLogger = component.getLogger();
		ObjectDeployment wcOD = getWorkerClientDeployment(component);
		WorkerClient workerClient = getWorkerClient(component);
		
		EasyMock.reset(newLogger);
		
		newLogger.warn("The worker with container ID " + testStub.getDeploymentID().getContainerID() + " is not avaliable.");
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, testStub.getDeploymentID());
		workerClient.sendMessage(new ErrorOcurredMessageHandle(error));
		
		EasyMock.verify(newLogger);
	}
	
	
	
}


