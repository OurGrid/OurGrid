package org.ourgrid.acceptance.util.broker;

import java.io.File;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.broker.BrokerAcceptanceTestCase;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.communication.actions.WorkerIsReadyMessageHandle;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.matchers.GetFileInfoMessageHandleMatcher;
import org.ourgrid.matchers.RemoteExecuteMessageHandleMatcher;
import org.ourgrid.worker.communication.processors.handle.GetFileInfoMessageHandle;
import org.ourgrid.worker.communication.processors.handle.RemoteExecuteMessageHandle;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_313_Util extends BrokerAcceptanceUtil {

	public Req_313_Util(ModuleContext context) {
		super(context);
	}

	public void callWorkerIsReady(BrokerServerModule component, List<TestStub> workers, Operations operation, Long requestID,
			TestJob testJob) {
		
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = "Class.class";
		
		String[] localFileNames = new String[1];
		localFileNames[0] = BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt";
		
		callWorkerIsReady(component, workers, operation, false, null, 1, 1,
				requestID, testJob, localFileNames, remoteFileNames);
	}

	public void callWorkerIsReady(BrokerServerModule component, List<TestStub> workers, Operations operation, TestJob testJob) {
		String[] remoteFileNames = new String[1];
		remoteFileNames[0] = "Class.class";
		
		String[] localFileNames = new String[1];
		localFileNames[0] = BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt";
		
		callWorkerIsReady(component, workers, operation, false, null, 1, 1,
				null, testJob, localFileNames, remoteFileNames);
	}

	public void callWorkerIsReady(BrokerServerModule component, List<TestStub> workers, Operations operation, boolean invalidOperation, 
			States state, int jobID, int taskID, Long requestID, TestJob testJob, String[] localFileNames, String[] remoteFileNames) {

		CommuneLogger newLogger = component.getLogger();

		WorkerClient workerClient = getWorkerClient(component);
		for (TestStub stub : workers) {

			Worker worker = (Worker) stub.getObject();
			EasyMock.reset(newLogger);
			EasyMock.reset(worker);

			if (state == null || state == States.SCHEDULED_STATE) {
				newLogger.debug("Worker is ready. Handle: 1.1.1, state: "
						+ GridProcessState.RUNNING);
			}

			if (invalidOperation) {
				if (state == States.SCHEDULED_STATE) {
					newLogger
							.error("Invalid operation: workerIsReady. The execution is on the state: Scheduled");
				} else if (state == States.INIT_STATE) {
					newLogger
							.error("Invalid operation: workerIsReady. The execution is on the state: Init");
				} else if (state == States.REMOTE_STATE) {
					newLogger
							.error("Invalid operation: workerIsReady. The execution is on the state: Remote");
				} else if (state == States.FINAL_STATE) {
					newLogger
							.error("Invalid operation: workerIsReady. The execution is on the state: Final");
				}

			} else {

				if (operation == Operations.SEND_FILE_OPERATION) {
					for (int i = 0; i < localFileNames.length; i++) {
						newLogger.info("Sending file " + localFileNames[i] + " to " + stub.getDeploymentID());
					}
					// TODO worker.transferRequestReceived(null);
				} else if (operation == Operations.EXECUTE_COMMAND_OPERATION) {
					RemoteExecuteMessageHandle remoteExecuteMessage = new RemoteExecuteMessageHandle(requestID, null, null);
					worker.sendMessage(RemoteExecuteMessageHandleMatcher.eqMatcher(remoteExecuteMessage));
				} else if (operation == Operations.SEND_FILE_INFO_OPERATION) {
					for (int i = 0; i < remoteFileNames.length; i++) {
						OutgoingTransferHandle handle = getOutgoingTransferHandle(testJob, remoteFileNames[i]);
						GridProcessHandle process = new GridProcessHandle(jobID, taskID, 1);
						File file = new File(remoteFileNames[i]);
						newLogger.debug("File info requested: " + file.getName() + ", handle: " + handle + ", replica: " + process);
						GetFileInfoMessageHandle getFileInfoMessageHandle = new GetFileInfoMessageHandle(
								handle.getId(), requestID, file.getName());
						
						worker.sendMessage(GetFileInfoMessageHandleMatcher.eqMatcher(getFileInfoMessageHandle));
					}
				}

			}

			EasyMock.replay(newLogger);
			EasyMock.replay(worker);

			AcceptanceTestUtil.setExecutionContext(component, getWorkerClientDeployment(component), stub.getDeploymentID());
			workerClient.sendMessage(new WorkerIsReadyMessageHandle());

			EasyMock.verify(newLogger);
			EasyMock.verify(worker);
		}
	}
	
	/**
	 * There is just one file name. no more difference between local end remote copy. 
	 * JDL does not support this. 
	 */
	public void callWorkerIsReady(BrokerServerModule component, List<TestStub> workers, Operations operation, boolean invalidOperation, 
			States state, int jobID, int taskID, Long requestID, TestJob testJob, String[] localFileNames) {
		
		String remoteFileNames[] = null;
		if(localFileNames != null){
			remoteFileNames = new String[localFileNames.length];
			for (int i = 0; i < remoteFileNames.length; i++) {
				remoteFileNames[i] = new File(localFileNames[i]).getName();
			}
		}
		
		callWorkerIsReady( component, workers, operation, invalidOperation, state, jobID, taskID, requestID, testJob, localFileNames, remoteFileNames );
	}
	
	public void callWorkerIsReadyWithoutWorkers(BrokerServerModule component, TestStub workerTestStub) {
		CommuneLogger newLogger = component.getLogger();
		EasyMock.reset(newLogger);

		WorkerClient workerClient = getWorkerClient(component);
		
		newLogger.warn("The worker with container ID " + workerTestStub.getDeploymentID().getContainerID() + " is not avaliable.");
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, getWorkerClientDeployment(component), workerTestStub.getDeploymentID());
		workerClient.sendMessage(new WorkerIsReadyMessageHandle());
		
		EasyMock.verify(newLogger);
	}
}
