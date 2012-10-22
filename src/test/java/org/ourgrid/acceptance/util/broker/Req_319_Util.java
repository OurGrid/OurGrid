package org.ourgrid.acceptance.util.broker;

import java.io.File;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.business.scheduler.workqueue.JobInfo;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.job.Job;
import org.ourgrid.matchers.GridProcessAccountingMatcher;
import org.ourgrid.matchers.MessageStartsWithMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_319_Util extends BrokerAcceptanceUtil {
	
	public Req_319_Util(ModuleContext context) {
		super(context);
	}
	
	public void transferRejected(BrokerServerModule component, States state, Operations operation, LocalWorkerProvider lwp, 
			TestStub workerStub, OutgoingTransferHandle handle, TestJob testJob) {
		transferRejected(component, state, operation, lwp, workerStub, 1, 1, handle, testJob);
	}
	
	public void transferRejected(BrokerServerModule component, States state, Operations operation, LocalWorkerProvider lwp, 
			TestStub workerStub, int jobID, int tasks, OutgoingTransferHandle handle, TestJob testJob) {
		CommuneLogger newLogger = component.getLogger();
		
		ObjectDeployment wcOD = getWorkerClientDeployment(component); 
		
		WorkerClient wc = getWorkerClient(component);
		Worker worker = (Worker) workerStub.getObject();
		
		EasyMock.reset(newLogger);
		EasyMock.reset(lwp);
		EasyMock.reset(worker);
		
		if(state == States.SCHEDULED_STATE){
			newLogger.warn("Invalid operation: fileRejected. The execution is on the state: Scheduled");
		} else if(state == States.INIT_STATE){
			
			if(operation == Operations.SEND_FILE_INFO_OPERATION){
				newLogger.error("Invalid operation: fileRejected. The execution is on the state: Init");
			} else {
				newLogger.debug("Adding to blacklist. Task: " + tasks +", Worker: " 
						+ workerStub.getDeploymentID());
				newLogger.debug("Worker unwanted: " + workerStub.getDeploymentID());
				
				RequestSpecification requestSpec = testJob.getRequestByPeer(application, lwp);
				lwp.reportReplicaAccounting(GridProcessAccountingMatcher.eqMatcher());
				lwp.unwantedWorker(workerStub.getDeploymentID().getServiceID(), requestSpec); 
				
				GridProcessHandle process = new GridProcessHandle(jobID, tasks, 1);
				
				Job job = JobInfo.getInstance().getJob(jobID);
				
				boolean jobEnded = hasJobEnded(job);
				
				newLogger.debug(MessageStartsWithMatcher.eqMatcher(
						"Grid process FAILED " + process + ". Job ended: " + jobEnded));
				
				if (jobEnded) {
					lwp.finishRequest(requestSpec);
				} else {
					lwp.resumeRequest(requestSpec.getRequestId());
				}
				
			}
			
		} else if(state == States.REMOTE_STATE) {
			if(operation == Operations.SEND_FILE_OPERATION) {
				newLogger.warn("Invalid operation: fileRejected. The execution is on the state: Remote");
			}
		}
		else if(state == States.FINAL_STATE){
			newLogger.warn("The worker with container ID " + workerStub.getDeploymentID().getServiceID().getContainerID() + " is not avaliable.");
			//newLogger.warn("Invalid operation. The execution is on the state: Final");
		}
		
		EasyMock.replay(newLogger);
		EasyMock.replay(lwp);
		EasyMock.replay(worker);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, workerStub.getDeploymentID());
		
		if(handle == null){
			//in the first test case, any handle works with a file name != null
			wc.transferRejected(new OutgoingTransferHandle("test.txt", 
					new File("test.txt"), null, workerStub.getDeploymentID()));
		}else{
			wc.transferRejected(handle);
		}
		
		EasyMock.verify(newLogger);
		EasyMock.verify(lwp);
		EasyMock.verify(worker);

	}
	
	private boolean hasJobEnded(Job job) {
		GridProcessState state = job.getState();
		
		return state.equals(GridProcessState.FAILED) || 
			state.equals(GridProcessState.CANCELLED) || state.equals(GridProcessState.FINISHED);
	}
}
