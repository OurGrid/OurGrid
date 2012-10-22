package org.ourgrid.acceptance.util.broker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.communication.actions.HereIsGridProcessResultMessageHandle;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.matchers.GetFileMatcher;
import org.ourgrid.matchers.JobStatusInfoMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_314_Util extends BrokerAcceptanceUtil {
	
	public Req_314_Util(ModuleContext context) {
		super(context);
	}
	
	public void executionResult(BrokerServerModule component, States state, TestStub workerStub, boolean getFiles, TestStub peerStub,
			TestJob testJob, String... fileNames) {
		CommuneLogger newLogger = component.getLogger();

		EasyMock.reset(newLogger);
		
		WorkerClient workerClient = getWorkerClient(component);
		ObjectDeployment wcOD = getWorkerClientDeployment(component);
		
		Worker worker = (Worker) workerStub.getObject();
		LocalWorkerProvider lwp = null;
		
		if(state == States.SCHEDULED_STATE){
			newLogger.warn("Invalid operation: hereIsExecutionResult. The execution is on the state: Scheduled");
		} else if(state == States.INIT_STATE){
			newLogger.warn("Invalid operation: hereIsExecutionResult. The execution is on the state: Init");
		} else if (state == States.FINAL_STATE) {
			newLogger.warn("The worker with container ID " + workerStub.getDeploymentID().getContainerID() + " is not avaliable.");
		} else if (state == States.REMOTE_STATE) {
				EasyMock.reset(worker);
				
				if (peerStub != null) {
					lwp = (LocalWorkerProvider) peerStub.getObject();
				}	
				
				if (lwp != null) {
					EasyMock.reset(lwp);
				}	
				
				if (!getFiles) {
					newLogger.debug("Worker dispose: " + workerStub.getDeploymentID().getServiceID());
					newLogger.info("Stub to be released: " + workerStub.getDeploymentID().getServiceID());
					lwp.disposeWorker(workerStub.getDeploymentID().getServiceID());
					lwp.hereIsJobStats(JobStatusInfoMatcher.eqMatcher(testJob.getJob().getJobId(), testJob.getJobSpec()));
					lwp.finishRequest(testJob.getRequestByPeer(application, lwp));
					
					lwp.reportReplicaAccounting((GridProcessAccounting) EasyMock.anyObject());
					
				} else {
					worker.sendMessage(GetFileMatcher.eqMatcher(fileNames));
				}
				
				EasyMock.replay(worker);
				
				if (lwp != null) {
					EasyMock.replay(lwp);
				}
		}

		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, workerStub.getDeploymentID());
		
		workerClient.sendMessage(new HereIsGridProcessResultMessageHandle(new ExecutorResult()));
	
		EasyMock.verify(newLogger);
		EasyMock.verify(worker);
		
		if (lwp != null) {
			EasyMock.verify(lwp);
		}
	}
	
	public void executionResult(BrokerServerModule component, States state, TestStub workerStub, boolean getFiles, TestStub peerStub,
			TestJob testJob) {
		executionResult(component, state, workerStub, getFiles, peerStub, testJob, (String[]) null);
	}
	
	public void executionResult(BrokerServerModule component, States state, TestStub workerStub, boolean getFiles) {
		executionResult(component, state, workerStub, getFiles, null, null);
	}
	
	public void executionResult(BrokerServerModule component, States state, TestStub workerStub, boolean getFiles, String... fileNames) {
		executionResult(component, state, workerStub, getFiles, null, null, fileNames);
	}
}
