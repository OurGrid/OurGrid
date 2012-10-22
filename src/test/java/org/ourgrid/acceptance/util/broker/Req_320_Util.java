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

public class Req_320_Util extends BrokerAcceptanceUtil {
	
	public Req_320_Util(ModuleContext context) {
		super(context);
	}
	
	public void cancelOutgoingTransfer(BrokerServerModule component, States brokerState, Operations operation, TestJob testJob,
			TestStub peerTestStub, TestStub workerTestStub, String remoteFile) {
		cancelOutgoingTransfer(component, brokerState, operation, remoteFile, testJob, 
				peerTestStub, workerTestStub, 1, 1);
	}
	
	public void cancelOutgoingTransfer(BrokerServerModule component, States brokerState, Operations operation, String fileName,
			TestJob testJob, TestStub peerTestStub, TestStub workerTestStub, int jobID, int taskID) {
		CommuneLogger newLogger = component.getLogger();
		EasyMock.reset(newLogger);
		
		component.setLogger(newLogger);
		
		ObjectDeployment wcOD = getWorkerClientDeployment(component); 
		
		WorkerClient wc = getWorkerClient(component);
		OutgoingTransferHandle handle = getOutgoingTransferHandle(testJob, fileName);
		LocalWorkerProvider lwp = (LocalWorkerProvider) peerTestStub.getObject();

		if(brokerState == States.SCHEDULED_STATE){
			newLogger.error("Invalid operation: outgoingTransferCancelled. The execution is on the state: Scheduled");
		}
		else if(brokerState == States.REMOTE_STATE){
			newLogger.error("Invalid operation: outgoingTransferCancelled. The execution is on the state: Remote");
		}
		else if(brokerState == States.FINAL_STATE){
			newLogger.warn("The worker with container ID " + workerTestStub.getDeploymentID().getContainerID() + " is not avaliable.");
		}
		else if(brokerState == States.INIT_STATE) {
			
			if (operation == Operations.SEND_FILE_INFO_OPERATION){
				newLogger.error("Invalid operation: outgoingTransferCancelled. The execution is on the state: Init");
			} else {
				newLogger.error("Outgoing file transfer cancelled: " + handle + ", amount written: 0");
				newLogger.debug("Adding to blacklist. Task: " + taskID +", Worker: " + workerTestStub.getDeploymentID());
				newLogger.debug("Worker unwanted: " + workerTestStub.getDeploymentID());
				
				EasyMock.reset(lwp);
				lwp.reportReplicaAccounting(GridProcessAccountingMatcher.eqMatcher());
				lwp.unwantedWorker(workerTestStub.getDeploymentID().getServiceID(), testJob.getRequestByPeer(application, lwp));
				
				GridProcessHandle process = new GridProcessHandle(jobID, taskID, 1);
				newLogger.debug(MessageStartsWithMatcher.eqMatcher(
						"Grid process FAILED " + process + ". Job ended: false"));
				
				lwp.resumeRequest(testJob.getRequestByPeer(application, lwp).getRequestId());
				
				EasyMock.replay(lwp);
			}
			
		}
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, workerTestStub.getDeploymentID());
		
		if (handle == null) {
			wc.outgoingTransferCancelled(new OutgoingTransferHandle(fileName, new File(fileName), 
					"", workerTestStub.getDeploymentID()), 0L);
		} else {
			wc.outgoingTransferCancelled(handle, 0L);
			EasyMock.verify(lwp);
		}
		
		EasyMock.verify(newLogger);
	}
}
