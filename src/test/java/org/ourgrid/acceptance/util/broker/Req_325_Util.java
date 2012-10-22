package org.ourgrid.acceptance.util.broker;

import java.io.File;
import java.io.IOException;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.broker.BrokerAcceptanceTestCase;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.matchers.GridProcessAccountingMatcher;
import org.ourgrid.matchers.JobStatusInfoMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Req_325_Util extends BrokerAcceptanceUtil {

	public Req_325_Util(ModuleContext context) {
		super(context);
	}
	
	public void testIncomingTransferCompleted(BrokerServerModule component, States state, 
			boolean fileTransferRequestReceived, TestStub testStub, TestJob testJob){
		testIncomingTransferCompleted(component,state, fileTransferRequestReceived, BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", false, null, testStub, testJob);
	}
	
	public void testIncomingTransferCompleted(BrokerServerModule component, States state, boolean fileTransferRequestReceived, 
			String fileName, boolean isLastFile, LocalWorkerProvider lwp, TestStub workerStub, TestJob testJob, 
			GridProcessHandle gridHandle){
		
		CommuneLogger newLogger = component.getLogger();
		
		EasyMock.reset(newLogger);
		
		WorkerClient workerClient = getWorkerClient(component);
		ObjectDeployment wcOD = getWorkerClientDeployment(component);
		IncomingTransferHandle handle;
		
		if(state == States.FINAL_STATE){
			handle = getIncomingTransferHandle(testJob, fileName);
			handle.setSenderID(workerStub.getDeploymentID().getContainerID());
			
			handle.setWritable(true);
			handle.setReadable(true);
			handle.setExecutable(true);
		}
		else{
			handle = new IncomingTransferHandle((long) fileName.hashCode(), fileName, "", 0L,
					workerStub.getDeploymentID().getContainerID());
			
			handle.setWritable(true);
			handle.setReadable(true);
			handle.setExecutable(true);
		}
		
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(state == States.SCHEDULED_STATE){
			newLogger.warn("Invalid operation: incomingTransferCompleted. The execution is on the state: Scheduled");
		} 
		else if (state == States.INIT_STATE){
			newLogger.warn("Invalid operation: incomingTransferCompleted. The execution is on the state: Init");
		} 
		else if (state == States.REMOTE_STATE) {
			newLogger.warn("Invalid operation: incomingTransferCompleted. The execution is on the state: Remote");
		} 
		else if (state == States.FINAL_STATE) {
			if(fileTransferRequestReceived){
				if(isLastFile){
					EasyMock.reset(lwp);
					newLogger.debug("Worker dispose: " + workerStub.getDeploymentID().getServiceID());
					newLogger.info("Stub to be released: " + workerStub.getDeploymentID().getServiceID());
					lwp.reportReplicaAccounting(GridProcessAccountingMatcher.eqMatcher());
					lwp.disposeWorker(workerStub.getDeploymentID().getServiceID());
					lwp.hereIsJobStats(JobStatusInfoMatcher.eqMatcher(testJob.getJob().getJobId(), testJob.getJobSpec()));
					lwp.finishRequest(testJob.getRequestByPeer(application, lwp));
					EasyMock.replay(lwp);
					
					String dirName = testJob.getJob().getTaskByID(gridHandle.getTaskID()).getSpec().getSourceParentDir();
					String command = testJob.getJob().getTaskByID(gridHandle.getTaskID()).getSpec().getSabotageCheck();
					
					newLogger.debug("Running sabotage check command: " + command + " replica: " + gridHandle);
					
//					newLogger.debug("Creating script on dir..." + dirName + " for command " + command);
//					
//					File dir = new File( dirName );
//					newLogger.debug("Will create file on dir " + dir + " is Directory: " + dir.isDirectory());
//					
//					newLogger.debug("Will create file on dir " + dir + " command: " + command);
//					newLogger.debug(ValidStringMatcher.eqMatcher());
				}
			}
			else{
				newLogger.warn("Invalid operation. The execution is on the state: Final");
			}
		}
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD,	workerStub.getDeploymentID());
		
		workerClient.incomingTransferCompleted(handle, 0L);
		
		EasyMock.verify(newLogger);
		if (lwp != null)
			EasyMock.verify(lwp);
	}

	public void testIncomingTransferCompleted(BrokerServerModule component, States state, boolean fileTransferRequestReceived, 
			String fileName, boolean isLastFile, LocalWorkerProvider lwp, TestStub workerStub, TestJob testJob){
		
		testIncomingTransferCompleted(component,state,fileTransferRequestReceived, fileName,isLastFile, lwp, workerStub, 
				testJob, null);
	}
	
	public void testIncomingTransferCompletedNoWorker(BrokerServerModule component, TestStub testStub){
		CommuneLogger newLogger = component.getLogger();
		ObjectDeployment wcOD = getWorkerClientDeployment(component);
		WorkerClient workerClient = getWorkerClient(component);
		
		EasyMock.reset(newLogger);
		
		newLogger.warn("The worker with container ID " + testStub.getDeploymentID().getContainerID() + " is not avaliable.");
		
		EasyMock.replay(newLogger);
		
		IncomingTransferHandle handle = new IncomingTransferHandle(0L, "", "", 1, testStub.getDeploymentID().getContainerID());
		handle.setWritable(true);
		handle.setReadable(true);
		handle.setExecutable(true);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD,	testStub.getDeploymentID());
		workerClient.incomingTransferCompleted(handle, 1);
		
		EasyMock.verify(newLogger);
	}
}
