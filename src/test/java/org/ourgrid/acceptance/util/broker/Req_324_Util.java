package org.ourgrid.acceptance.util.broker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.WorkerClient;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_324_Util extends BrokerAcceptanceUtil {

	public Req_324_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * It consider the execution isn't finished.
	 */
	public void transferRequestReceived(BrokerServerModule component, States state, TestStub workerStub, TestJob testJob){
		transferRequestReceived(component, state, "", false, workerStub, testJob);
	}
	

	public void transferRequestReceived(BrokerServerModule component, TestStub workerStub, String fileName, boolean executionIsFinished){
		transferRequestReceived(component, null, fileName, executionIsFinished, workerStub, null);
	}
	
	public void transferRequestReceived(BrokerServerModule component, States state, String fileName, boolean executionIsFinished, 
			TestStub workerStub, TestJob testJob) {
		transferRequestReceived(component, state, fileName, executionIsFinished, workerStub, testJob, true);
	}
	
	public void transferRequestReceived(BrokerServerModule component, States state, String fileName, boolean executionIsFinished, 
			TestStub workerStub, TestJob testJob, boolean hasFinalBlock) {
		CommuneLogger newLogger = component.getLogger();
		
		EasyMock.reset(newLogger);
		
		WorkerClient workerClient = getWorkerClient(component);
		ObjectDeployment wcOD = getWorkerClientDeployment(component);
		
		if(!hasFinalBlock){
			newLogger.warn("The worker with container ID " + workerStub.getDeploymentID().getContainerID() +
			" is not avaliable.");
		}
		else if(state == States.SCHEDULED_STATE){
			newLogger.warn("Invalid operation: fileTransferRequestReceived. The execution is on the state: Scheduled");
		}
		else if(state == States.INIT_STATE){
			newLogger.warn("Invalid operation: fileTransferRequestReceived. The execution is on the state: Init");
		}
		else if( state == States.REMOTE_STATE){
			newLogger.warn("Invalid operation: fileTransferRequestReceived. The execution is on the state: Remote");
		}
		
		if(executionIsFinished){
			newLogger.warn("The worker with container ID " + workerStub.getDeploymentID().getContainerID() + " is not avaliable.");
		}
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, workerStub.getDeploymentID());
		
		if (state == States.FINAL_STATE && hasFinalBlock) {
			IncomingTransferHandle handle = getIncomingTransferHandle(testJob, fileName);
			handle.setWritable(true);
			handle.setReadable(true);
			handle.setExecutable(true);
			
			handle.setSenderID(workerStub.getDeploymentID().getContainerID());
			workerClient.transferRequestReceived(handle);
		} else {
			IncomingTransferHandle handle = new IncomingTransferHandle((long) fileName.hashCode(), fileName, "", 0L, 
			workerStub.getDeploymentID().getContainerID());
			
			handle.setWritable(true);
			handle.setReadable(true);
			handle.setExecutable(true);
			
			workerClient.transferRequestReceived(handle);		
		}
		EasyMock.verify(newLogger);
		
	}
	
	public void transferRequestReceived(BrokerServerModule component, String fileName, TestStub workerStub, TestJob testJob) {
		CommuneLogger newLogger = component.getLogger();
		
		EasyMock.reset(newLogger);
		
		WorkerClient workerClient = getWorkerClient(component);
		ObjectDeployment wcOD = getWorkerClientDeployment(component);
		
		newLogger.warn("The worker with container ID " + workerStub.getDeploymentID().getContainerID() + " is not avaliable.");
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, workerStub.getDeploymentID());
		
		IncomingTransferHandle handle = new IncomingTransferHandle((long) fileName.hashCode(), fileName, "", 0L, 
		workerStub.getDeploymentID().getContainerID());
		handle.setWritable(true);
		handle.setReadable(true);
		handle.setExecutable(true);
		
		workerClient.transferRequestReceived(handle);		
		EasyMock.verify(newLogger);
		
	}
	
	/**
	 * It consider the execution isn't finished and has to get files.
	 */
	public void transferRequestReceived(BrokerServerModule component, States state, String fileName, TestStub workerStub, TestJob testJob) {
		transferRequestReceived(component, state, fileName, false, workerStub, testJob);
	}
}
