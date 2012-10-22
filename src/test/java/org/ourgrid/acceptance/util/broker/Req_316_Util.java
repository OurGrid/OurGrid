package org.ourgrid.acceptance.util.broker;

import java.io.File;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.broker.BrokerAcceptanceTestCase;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.communication.actions.HereIsFileInfoMessageHandle;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.util.JavaFileUtil;
import org.ourgrid.matchers.RemoteExecuteMessageHandleMatcher;
import org.ourgrid.worker.communication.processors.handle.RemoteExecuteMessageHandle;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_316_Util extends BrokerAcceptanceUtil {

	public Req_316_Util(ModuleContext context) {
		super(context);
	}
	
	public void receiveInfo(BrokerServerModule component, States state, Operations operation, boolean fileAlreadyExists, 
			TestStub workerTestStub, TestJob testJob, String remoteFile) throws Exception {
		receiveInfo(component, state, operation, fileAlreadyExists, workerTestStub, testJob, BrokerAcceptanceTestCase.BROKER_TEST_DIR + "file.txt",
				null, remoteFile);
	}

	
	public void receiveInfo(BrokerServerModule component, States state, Operations operation, boolean fileAlreadyExists, 
			TestStub workerTestStub, TestJob testJob, String fileName, Long requestID, String remoteFile) throws Exception {
		CommuneLogger newLogger = component.getLogger();
		EasyMock.reset(newLogger);
		
		Worker worker = (Worker) workerTestStub.getObject();
		EasyMock.reset(worker);
		
		WorkerClient wc = getWorkerClient(component);
		ObjectDeployment wcOD = getWorkerClientDeployment(component);
		File file = new File(fileName);

		if(state == States.SCHEDULED_STATE){
			newLogger.warn("Invalid operation: hereIsFileInfo. The execution is on the state: Scheduled");
		}
		else if(state == States.FINAL_STATE){
			newLogger.warn("The worker with container ID " + workerTestStub.getDeploymentID().getContainerID() + " is not avaliable.");
		}
		else if(operation == Operations.SEND_FILE_OPERATION){
			
			if(state == States.INIT_STATE){
				newLogger.error("Invalid operation: hereIsFileInfo. The execution is on the state: Init");
			}
			else if(state == States.REMOTE_STATE){
				newLogger.warn("Invalid operation: hereIsFileInfo. The execution is on the state: Remote");
			}
			
		}
		else if(operation == Operations.SEND_FILE_INFO_OPERATION){
			
			if(fileAlreadyExists){
				newLogger.debug("File " + remoteFile + " exists on storage. Skipping transfer");
				RemoteExecuteMessageHandle remoteExecuteMessage = new RemoteExecuteMessageHandle(requestID, "", null);
				worker.sendMessage(RemoteExecuteMessageHandleMatcher.eqMatcher(remoteExecuteMessage));
			}
			else{
				newLogger.info("Sending file " + fileName + " to " + workerTestStub.getDeploymentID());
				//TODO worker.transferRequestReceived(getIncomingTransferHandle(testJob, fileName));
			}
			
		}
		
		EasyMock.replay(newLogger);
		EasyMock.replay(worker);
		
		AcceptanceTestUtil.setExecutionContext(application, wcOD, workerTestStub.getDeploymentID());
		
		String localFileDigest = JavaFileUtil.getDigestRepresentation(file);

		
		long id = 0;
		
		OutgoingTransferHandle outgoingTransferHandle = getOutgoingTransferHandle(testJob, remoteFile);
		if (outgoingTransferHandle != null) {
			id = outgoingTransferHandle.getId();
		}
		
		wc.sendMessage(new HereIsFileInfoMessageHandle(id, new FileInfo(fileName, localFileDigest)));
		
		EasyMock.verify(newLogger);
		EasyMock.verify(worker);
	}


	public void receiveInfo(BrokerServerModule component, TestStub workerTestStub, TestJob testJob, String fileName,
			String anotherFileName, String remoteFileName) throws Exception {
		CommuneLogger newLogger = component.getLogger();
		EasyMock.reset(newLogger);
		
		Worker worker = (Worker) workerTestStub.getObject();
		EasyMock.reset(worker);
		
		WorkerClient wc = getWorkerClient(component);
		ObjectDeployment wcOD = getWorkerClientDeployment(component);
		File file = new File(anotherFileName);
	
		newLogger.info("Sending file " + fileName + " to " + workerTestStub.getDeploymentID());
		//TODO worker.transferRequestReceived(getIncomingTransferHandle(testJob, fileName));
	
		EasyMock.replay(newLogger);
		EasyMock.replay(worker);
		
		AcceptanceTestUtil.setExecutionContext(application, wcOD, workerTestStub.getDeploymentID());
		
		String localFileDigest = JavaFileUtil.getDigestRepresentation(file);
		
		long id = 0;
		
		OutgoingTransferHandle outgoingTransferHandle = getOutgoingTransferHandle(testJob, remoteFileName);
		if (outgoingTransferHandle != null) {
			id = outgoingTransferHandle.getId();
		}
	
		wc.sendMessage(new HereIsFileInfoMessageHandle(id, new FileInfo(anotherFileName, localFileDigest)));
		
		EasyMock.verify(newLogger);
		EasyMock.verify(worker);
	}
}
