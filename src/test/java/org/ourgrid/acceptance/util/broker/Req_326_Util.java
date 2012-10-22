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

public class Req_326_Util extends BrokerAcceptanceUtil {

	public Req_326_Util(ModuleContext context) {
		super(context);
	}
	
	public void testIncomingTransferFailed(BrokerServerModule component, States state, TestJob testJob, String fileName, 
			TestStub workerStub){
		
		testIncomingTransferFailed(component, state, testJob, fileName, workerStub, true);
		
	}
	
	public void testIncomingTransferFailed(BrokerServerModule component, States state, TestJob testJob, String fileName, 
			TestStub workerStub, boolean hasFinaFase){
		CommuneLogger newLogger = component.getLogger();

		
		ObjectDeployment wcOD = getWorkerClientDeployment(component); 
		EasyMock.reset(newLogger);
		
		WorkerClient workerClient = getWorkerClient(component);
		
		IncomingTransferHandle handle;
		if(state == States.FINAL_STATE && hasFinaFase){
			handle = getIncomingTransferHandle(testJob, fileName);
			handle.setSenderID(workerStub.getDeploymentID().getContainerID());
		}
		else{
			handle = new IncomingTransferHandle((long) fileName.hashCode(), fileName, "", 0L, 
					workerStub.getDeploymentID().getContainerID());
		}
		
		if(!hasFinaFase){
			newLogger.warn("The worker with container ID " + workerStub.getDeploymentID().getContainerID() +
					" is not avaliable.");
		}
		else if(state == States.SCHEDULED_STATE){
			newLogger.warn("Invalid operation: incomingTransferFailed. The execution is on the state: Scheduled");
		}
		else if(state == States.INIT_STATE){
			newLogger.warn("Invalid operation: incomingTransferFailed. The execution is on the state: Init");
		}
		else if(state == States.REMOTE_STATE){
			newLogger.error("Invalid operation: incomingTransferFailed. The execution is on the state: Remote");
		}
		else if(state == States.FINAL_STATE) {
			newLogger.warn("Invalid operation: incomingTransferFailed. The execution is on the state: Final");
		}
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, workerStub.getDeploymentID());
		
		handle.setExecutable(true);
		handle.setReadable(true);
		handle.setWritable(true);
		
		workerClient.incomingTransferFailed(handle, new Exception(""), 0L);
		
		EasyMock.verify(newLogger);
	}
	
	public void testFileTransferRequesReceived(BrokerServerModule component, States state) {
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		WorkerClient workerClient = getWorkerClient(component);
		workerClient.transferRequestReceived(new IncomingTransferHandle(null, null, 0, null));
		
		if(state == States.SCHEDULED_STATE){
			newLogger.warn("Invalid operation. The execution is on the state: Scheduled");
		}
		else if(state == States.INIT_STATE){
			newLogger.warn("Invalid operation. The execution is on the state: Init");
		}
		else if(state == States.REMOTE_STATE){
			newLogger.warn("Invalid operation. The execution is on the state: Remote");
		}
		
		EasyMock.replay(newLogger);
		EasyMock.verify(newLogger);
		component.setLogger(oldLogger);		
	}
}
