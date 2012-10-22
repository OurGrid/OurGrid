package org.ourgrid.acceptance.util.broker;

import java.io.File;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.matchers.GridProcessAccountingMatcher;
import org.ourgrid.matchers.MessageStartsWithMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_322_Util extends BrokerAcceptanceUtil {

	public Req_322_Util(ModuleContext context) {
		super(context);
	}
	
	public void testOutgoingTransferFailed(BrokerServerModule component, States state, Operations operation, TestJob testJob, TestStub peerTestStub, 
			TestStub workerTestStub, String remoteFile) {
		testOutgoingTransferFailed(component, state, operation, testJob, remoteFile, peerTestStub, workerTestStub, 1, 1);
	}
	
	public void testOutgoingTransferFailed(BrokerServerModule component, States state, Operations operation, TestJob testJob, 
			String fileName, TestStub peerTestStub, TestStub workerTestStub, int jobID, int taskID){
		CommuneLogger newLogger = component.getLogger();
		EasyMock.reset(newLogger);
		
		OutgoingTransferHandle handle = getOutgoingTransferHandle(testJob, fileName);
		WorkerClient workerClient = getWorkerClient(component);
		
		ObjectDeployment wcOD = getWorkerClientDeployment(component); 
		LocalWorkerProvider lwp = (LocalWorkerProvider) peerTestStub.getObject();

		if(state == States.SCHEDULED_STATE){
			newLogger.error("Invalid operation: outgoingTransferFailed. The execution is on the state: Scheduled");
		} 
		else if (state == States.INIT_STATE) {
			
			if(operation == Operations.SEND_FILE_INFO_OPERATION){
				newLogger.error("Invalid operation: outgoingTransferFailed. The execution is on the state: Init");
			}
			else{
				newLogger.error("Outgoing transfer failed: " + handle + " fail");
				newLogger.debug("Adding to blacklist. Task: " + taskID +", Worker: " + workerTestStub.getDeploymentID());
				newLogger.debug("Worker unwanted: " + workerTestStub.getDeploymentID());
				
				EasyMock.reset(lwp);
				lwp.reportReplicaAccounting(GridProcessAccountingMatcher.eqMatcher());
				lwp.unwantedWorker(workerTestStub.getDeploymentID().getServiceID(), testJob.getRequestByPeer(component, lwp));
				
				GridProcessHandle process = new GridProcessHandle(jobID, taskID, 1);
				newLogger.debug(MessageStartsWithMatcher.eqMatcher(
						"Grid process FAILED " + process + ". Job ended: false"));
				
				lwp.resumeRequest(testJob.getRequestByPeer(component, lwp).getRequestId());
				
				EasyMock.replay(lwp);
			}
			
		}
		else if(state == States.REMOTE_STATE){
			newLogger.error("Invalid operation: outgoingTransferFailed. The execution is on the state: Remote");
		}else if(state == States.FINAL_STATE){
			newLogger.warn("The worker with container ID " + workerTestStub.getDeploymentID().getContainerID() + " is not avaliable.");
		}
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, workerTestStub.getDeploymentID());
		
		if (handle == null) {
			workerClient.outgoingTransferFailed(new OutgoingTransferHandle(fileName, 
					new File(fileName), "", workerTestStub.getDeploymentID()), new Exception("fail"), 0L);
		} else {
			workerClient.outgoingTransferFailed(handle,  new Exception("fail"), 0L);
		}
		
		EasyMock.verify(newLogger);
	}
}
