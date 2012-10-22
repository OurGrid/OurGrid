package org.ourgrid.acceptance.util.broker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.broker.BrokerAcceptanceTestCase;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.matchers.RemoteExecuteMessageHandleMatcher;
import org.ourgrid.worker.communication.processors.handle.RemoteExecuteMessageHandle;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_321_Util extends BrokerAcceptanceUtil {

	private int countFiles = 0;

	public Req_321_Util(ModuleContext context) {
		super(context);
	}

	public void testOutgoingTransferCompleted(BrokerServerModule component, States state, Operations operation, TestStub testStub,
			OutgoingTransferHandle handle, Long requestID) {
		testOutgoingTransferCompleted(component, state, operation, testStub, BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt", 1, 1, handle, requestID, false);
	}
	
	public void testOutgoingTransferCompleted(BrokerServerModule component, States state, Operations operation, TestStub testStub,
			String sourceFileName, int jobID, int taskID, OutgoingTransferHandle handle, Long requestID, boolean hasAnotherFile) {
		
		CommuneLogger newLogger = component.getLogger();
		
		EasyMock.reset(newLogger);
		
		ObjectDeployment wcOD = getWorkerClientDeployment(component); 
		
		WorkerClient workerClient = getWorkerClient(component);
		Worker worker = (Worker) testStub.getObject();
		
		EasyMock.reset(worker);
		
		if (state == States.SCHEDULED_STATE){
			newLogger.error("Invalid operation: outgoingTransferCompleted. The execution is on the state: Scheduled");
		} else if (state == States.INIT_STATE){
			if (operation == Operations.SEND_FILE_INFO_OPERATION){
				newLogger.error("Invalid operation: outgoingTransferCompleted. The execution is on the state: Init");
			} else {
				GridProcessHandle process = new GridProcessHandle(jobID, taskID, 1);
				newLogger.debug("File transfer finished: " + sourceFileName + ", replica: " + process);

				if (!hasAnotherFile) {
					RemoteExecuteMessageHandle remoteExecuteMessage = new RemoteExecuteMessageHandle(requestID, null, null);
					worker.sendMessage(RemoteExecuteMessageHandleMatcher.eqMatcher(remoteExecuteMessage));
					
					AcceptanceTestUtil.publishTestObject(component, testStub.getDeploymentID(), worker, Worker.class);
				}
			}
		} else if (state == States.REMOTE_STATE){
			newLogger.error("Invalid operation: outgoingTransferCompleted. The execution is on the state: Remote");
		} else if (state == States.FINAL_STATE) {
			newLogger.warn("The worker with container ID " + testStub.getDeploymentID().getContainerID() + " is not avaliable.");
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(worker);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, testStub.getDeploymentID());

		workerClient.outgoingTransferCompleted(handle, 0);

		EasyMock.verify(worker);
		EasyMock.verify(newLogger);
	}
}
