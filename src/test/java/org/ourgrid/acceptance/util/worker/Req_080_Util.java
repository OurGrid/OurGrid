/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.acceptance.util.worker;

import java.io.File;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.common.CommonConstants;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.util.FileTransferHandlerUtils;
import org.ourgrid.matchers.ErrorOcurredMessageHandleMatcher;
import org.ourgrid.matchers.FileTransferErrorMessageMatcher;
import org.ourgrid.matchers.FileTransferMatcher;
import org.ourgrid.matchers.IncomingTransferFailedMessageMatcher;
import org.ourgrid.matchers.TransferRequestAcceptedMessageMatcher;
import org.ourgrid.matchers.TransferRequestIncomingFileMessageMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.controller.GridProcessError;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferReceiver;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_080_Util extends WorkerAcceptanceUtil {

	public Req_080_Util(ModuleContext context) {
		super(context);
	}

	public void requestToTransferFileByUnknownClient(WorkerComponent component,
			long requestID, ContainerID senderId, String filePath,
			String logicalFileName, long handleId, long fileSize) {
		requestToTransferFile(component, requestID, senderId, true, false,
				false, false, false, false, false, false, filePath, fileSize,
				null, CommonConstants.PUT_TRANSFER, handleId, logicalFileName,
				null, null);
	}

	public void requestToTransferFileWithInvalidDescription(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, long handleId,
			long fileSize, String invalidDescription) {
		requestToTransferFile(component, requestID, senderId, false, false,
				false, false, false, true, false, false, filePath, fileSize,
				null, invalidDescription, handleId, logicalFileName, null, null);
	}

	public void requestToTransferFileWithInvalidVariable(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, long handleId,
			long fileSize) {
		requestToTransferFileWithInvalidDirectory(component, requestID,
				senderId, filePath, logicalFileName, handleId, fileSize,
				"Invalid variable found.");
	}

	public void requestToTransferFileWithNotRelativePath(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, long handleId,
			long fileSize, String absolutePath) {
		requestToTransferFileWithInvalidDirectory(component, requestID,
				senderId, filePath, logicalFileName, handleId, fileSize,
				"File path is not relative to " + absolutePath + " directory.");
	}

	private void requestToTransferFileWithInvalidDirectory(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, long handleId,
			long fileSize, String errorCause) {

		requestToTransferFile(component, requestID, senderId, false, false,
				false, false, false, false, false, false, filePath, fileSize,
				errorCause, CommonConstants.PUT_TRANSFER, handleId,
				logicalFileName, null, null);
	}

	public void requestToTransferFileOnUnstartedWorker(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, long handleId,
			long fileSize) {
		requestToTransferFile(component, requestID, senderId, false, true,
				false, false, false, false, false, false, filePath, fileSize,
				null, CommonConstants.PUT_TRANSFER, handleId, logicalFileName,
				null, null);
	}

	public void requestToTransferFileWithRepeatedHandle(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, long handleId,
			long fileSize, WorkerClient workerClient) {
		requestToTransferFile(component, requestID, senderId, false, false,
				false, true, false, false, false, false, filePath, fileSize,
				null, CommonConstants.PUT_TRANSFER, handleId, logicalFileName,
				workerClient, null);
	}

	public void requestToTransferFileWithRepeatedHandleAndFilesOnTransfer(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, long handleId,
			long fileSize, WorkerClient workerClient,
			List<IncomingTransferHandle> handlesToCancel) {
		requestToTransferFile(component, requestID, senderId, false, false,
				false, true, false, false, false, false, filePath, fileSize,
				null, CommonConstants.PUT_TRANSFER, handleId, logicalFileName,
				workerClient, handlesToCancel);
	}

	public void requestToTransferWithIncomingFile(WorkerComponent component,
			long requestID, ContainerID senderId, String filePath,
			String logicalFileName, long handleId, long fileSize,
			List<IncomingTransferHandle> handles) {
		requestToTransferFile(component, requestID, senderId, false, false,
				false, false, true, false, false, false, filePath, fileSize,
				null, CommonConstants.PUT_TRANSFER, handleId, logicalFileName,
				null, handles);
	}

	public IncomingTransferHandle requestToTransferFile(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, String operationType,
			long handleId, long fileSize) {
		return requestToTransferFile(component, requestID, senderId, false,
				false, true, false, false, false, false, false, filePath,
				fileSize, null, operationType, handleId, logicalFileName, null,
				null);
	}

	public IncomingTransferHandle requestToTransferFileOnLocalExecuting(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, String operationType,
			long handleId, long fileSize) {
		return requestToTransferFile(component, requestID, senderId, false,
				false, false, false, false, false, false, false, filePath,
				fileSize, null, operationType, handleId, logicalFileName, null,
				null);
	}

	public IncomingTransferHandle requestToTransferFileOnLocalExecutionFinished(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, String operationType,
			long handleId, long fileSize) {

		return requestToTransferFile(component, requestID, senderId, false,
				false, false, false, false, false, false, true, filePath,
				fileSize, null, operationType, handleId, logicalFileName, null,
				null);
	}

	public IncomingTransferHandle requestToTransferFileWithTaskFailed(
			WorkerComponent component, long requestID, ContainerID senderId,
			String filePath, String logicalFileName, String operationType,
			long handleId, long fileSize) {
		return requestToTransferFile(component, requestID, senderId, false,
				false, false, false, false, false, true, false, filePath,
				fileSize, null, operationType, handleId, logicalFileName, null,
				null);
	}

	private IncomingTransferHandle requestToTransferFile(
			WorkerComponent component, long requestID, ContainerID senderID,
			boolean unknownClient, boolean isUnstartedWorker,
			boolean withSucess, boolean repeatedHandle, boolean isIncomingFile,
			boolean isInvalidDescription, boolean isFileTransferError,
			boolean isExecutionFinished, String filePath, long fileSize,
			String errorCause, String operationType, long handleId,
			String logicalFileName, WorkerClient workerClient,
			List<IncomingTransferHandle> handles) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		Worker worker = getWorker();

		FileTransferManager fileTransferManager = EasyMock
				.createMock(FileTransferManager.class);
		component.setFileTransferManager(fileTransferManager);

		if (workerClient != null) {
			EasyMock.reset(workerClient);
		} else {
			workerClient = EasyMock.createMock(WorkerClient.class);
		}

		IncomingTransferHandle opHandle = createIncomingFileTransfer(handleId,
				senderID, filePath, logicalFileName, operationType, fileSize);

		if (unknownClient) {
			newLogger.warn("An unknown client tried to transfer the file ["
					+ filePath + "] with size " + fileSize + " bytes. Handle: "
					+ opHandle + ". Unknown client id: [" + senderID + "].");
			fileTransferManager.rejectTransfer(opHandle);
		}
		if (isUnstartedWorker) {
			newLogger
					.warn("The client tried to transfer a file, "
							+ "but this Worker was not commanded to start the work yet."
							+ " This message was ignored. Client public key: ["
							+ senderID.getPublicKey() + "].");
			fileTransferManager.rejectTransfer(opHandle);
		} else {

			if (isInvalidDescription) {
				newLogger
						.warn("The client tried to transfer a file with a"
								+ " invalid transfer description. This message was ignored."
								+ " Transfer description: [" + operationType
								+ "]." + " Client public key: ["
								+ senderID.getPublicKey() + "].");
				fileTransferManager.rejectTransfer(opHandle);
			}

			if (isIncomingFile) {
				newLogger.warn(TransferRequestIncomingFileMessageMatcher
						.eqMatcher(filePath, senderID.getPublicKey()));
				fileTransferManager.rejectTransfer(opHandle);

				if (handles != null) {
					for (IncomingTransferHandle handle : handles) {
						fileTransferManager.cancelIncomingTransfer(handle);
					}
				}
			}

			if (repeatedHandle) {
				newLogger
						.error("The client tried to transfer a file with a repeated handle."
								+ " This message was ignored. Client public key: ["
								+ senderID.getPublicKey()
								+ "]. Handle: "
								+ opHandle + ".");
				workerClient.sendMessage(new ErrorOcurredMessageHandle(
						new GridProcessError(
								GridProcessErrorTypes.INVALID_SESSION)));
				workerClient
						.sendMessage(ErrorOcurredMessageHandleMatcher
								.eqMatcher(new ErrorOcurredMessageHandle(
										new GridProcessError(
												GridProcessErrorTypes.INVALID_SESSION))));

				if (handles != null) {
					for (IncomingTransferHandle handle : handles) {
						fileTransferManager.cancelIncomingTransfer(handle);
					}
				}
			}
			boolean toPlaypen = true;

			if (operationType != null
					&& operationType.equals(CommonConstants.STORE_TRANSFER)) {
				toPlaypen = false;
			}

			if (errorCause != null) {
				newLogger.warn(FileTransferErrorMessageMatcher.eqMatcher(
						errorCause, filePath, fileSize, opHandle,
						senderID.getPublicKey()));
				fileTransferManager.rejectTransfer(opHandle);
			}

			if (withSucess) {

				String directoryRoot;
				String filePathWithoutEnvVar;

				if (toPlaypen) {
					filePathWithoutEnvVar = filePath.replace("$"
							+ WorkerConstants.ENV_PLAYPEN + File.separator, "");
					directoryRoot = component.getContext().getProperty(
							WorkerConstants.PROP_PLAYPEN_ROOT);
				} else {
					filePathWithoutEnvVar = filePath.replace("$"
							+ WorkerConstants.ENV_STORAGE + File.separator, "");
					directoryRoot = component.getContext().getProperty(
							WorkerConstants.PROP_STORAGE_DIR);
				}

				File localFile = new File(filePathWithoutEnvVar);

				opHandle.setLocalFile(localFile);
				newLogger.debug(TransferRequestAcceptedMessageMatcher
						.eqMatcher(directoryRoot, filePathWithoutEnvVar,
								opHandle, senderID.getPublicKey(), toPlaypen));

				fileTransferManager.acceptTransfer(
						(IncomingTransferHandle) EasyMock.eq(opHandle),
						EasyMock.same((TransferReceiver) worker),
						FileTransferMatcher.eqMatcher(filePathWithoutEnvVar));
			} else {
				if (isFileTransferError) {
					newLogger
							.warn("The client tried to transfer a file. "
									+ "This message was ignored, because a error already ocurred."
									+ " This message was ignored. File path:"
									+ " [" + filePath + "]. Handle: "
									+ handleId + ". Size: " + fileSize
									+ " bytes." + " Client public key: ["
									+ senderID.getPublicKey() + "].");
				} else if (isExecutionFinished) {
					newLogger
							.warn("The client tried to transfer a file, but this Worker finished "
									+ "the execution. This message was ignored. Client public "
									+ "key: [" + senderID.getPublicKey() + "].");
					fileTransferManager.rejectTransfer(opHandle);
				} else {
					newLogger
							.warn("The client tried to transfer a file, but this"
									+ " Worker is in executing state."
									+ " This message was ignored. Client public key: ["
									+ senderID.getPublicKey() + "].");
					fileTransferManager.rejectTransfer(opHandle);
				}
			}
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(fileTransferManager);

		if (workerClient != null) {
			EasyMock.replay(workerClient);
		}

		ObjectDeployment workerOD = getWorkerDeployment();

		DeploymentID wcID = new DeploymentID(senderID,
				BrokerConstants.WORKER_CLIENT);
		AcceptanceTestUtil.publishTestObject(application, wcID, workerClient,
				WorkerClient.class);
		AcceptanceTestUtil.setExecutionContext(component, workerOD, wcID);

		worker.transferRequestReceived(opHandle);

		EasyMock.verify(newLogger);
		EasyMock.verify(fileTransferManager);
		EasyMock.reset(newLogger);

		component.setLogger(oldLogger);

		return opHandle;
	}

	public void receiveIncomingTransferFailedFromUnknownClient(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, ContainerID senderId, String filePath,
			String logicalFileName, String operationType, long handleId,
			long fileSize, Exception exception, long writtenData,
			DeploymentID brokerID) {
		receiveIncomingTransferFailed(component, worker, workerClient,
				createIncomingFileTransfer(handleId, senderId, filePath,
				logicalFileName, operationType, fileSize), exception,
				writtenData, true, false, false, false, false, false, brokerID);
	}

	public void receiveIncomingTransferFailedWithUnknownHandle(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, ContainerID senderId, String filePath,
			String logicalFileName, String operationType, long handleId,
			long fileSize, Exception exception, int writtenData,
			DeploymentID brokerID) {
		receiveIncomingTransferFailed(component, worker, workerClient,
				createIncomingFileTransfer(handleId, senderId, filePath,
				logicalFileName, operationType, fileSize), exception,
				writtenData, false, true, false, false, false, false, brokerID);
	}

	public void receiveIncomingTransferFailed(WorkerComponent component,
			Worker worker, WorkerClient workerClient,
			IncomingTransferHandle opHandle, Exception exception,
			long writtenData, DeploymentID brokerID) {
		receiveIncomingTransferFailed(component, worker, workerClient,
				opHandle, exception, writtenData, false, false, false, false,
				false, false, brokerID);
	}

	public void receiveIncomingTransferFailedUnstartedWorker(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, IncomingTransferHandle opHandle,
			Exception exception, long writtenData, DeploymentID brokerID) {
		receiveIncomingTransferFailed(component, worker, workerClient,
				opHandle, exception, writtenData, false, false, true, false,
				false,false, brokerID);
	}
	
	public void receiveIncomingTransferFailedTaskFailedWorker(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, IncomingTransferHandle opHandle,
			Exception exception, long writtenData, DeploymentID brokerID) {
		receiveIncomingTransferFailed(component, worker, workerClient,
				opHandle, exception, writtenData, false, false, false, true,
				true, true, brokerID);
	}

	public void receiveIncomingTransferFailedWorkingWorker(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, IncomingTransferHandle opHandle,
			Exception exception, long writtenData, DeploymentID brokerID) {
		receiveIncomingTransferFailed(component, worker, workerClient,
				opHandle, exception, writtenData, false, false, false, true,
				false, false, brokerID);
	}

	public void receiveIncomingTransferFailedDownloadingStateWorker(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, IncomingTransferHandle opHandle,
			Exception exception, long writtenData, DeploymentID brokerID) {
		
		receiveIncomingTransferFailed(component, worker, workerClient,
				opHandle, exception, writtenData, false, false, false, true,
				true, false, brokerID);
	}

	private void receiveIncomingTransferFailed(WorkerComponent component,
			Worker worker, WorkerClient workerClient,
			IncomingTransferHandle opHandle, Exception exception,
			long writtenData, boolean unknownClient, boolean unknownHandle,
			boolean unStartedWorker, boolean isWorking, boolean isIncomingFile, 
			boolean workerWithError, DeploymentID brokerID) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);

		component.setLogger(newLogger);

		EasyMock.reset(workerClient);

		ObjectDeployment workerOD = getWorkerDeployment();

		if (unknownClient) {
			newLogger
					.warn("The worker received an incoming transfer failed message"
							+ " from a unknown client. This message was ignored."
							+ " Client publ0ic key: ["
							+ brokerID.getPublicKey() + "].");
		} else if (unStartedWorker) {
			newLogger
					.warn(WorkerControllerMessages
							.getWorkerIsNotInWorkingStateIncomingTrasferFailedMessage(brokerID
									.getPublicKey()));
		}

		else if (isWorking && !isIncomingFile) {
			newLogger.warn(WorkerControllerMessages
					.getWorkerDoesNotRequestedAnyTransferMessage(brokerID
							.getPublicKey()));
		} else if (workerWithError) {
			newLogger.warn("The worker received an incoming transfer failed message. This message was ignored, because an error already ocurred. "
				+ "Handle: "
				+ opHandle.getId()
				+ ". Amount of data uploaded: "
				+ writtenData
				+ " bytes. Client public key: ["
				+ brokerID.getPublicKey() + "].");
		}

		else {
			newLogger
					.error("Error ocurred: FILE_TRANSFER_ERROR - When a"
							+ " FILE_TRANSFER_ERROR occurs it means that the file transfer has failed ",
							exception);

			String filePath = FileTransferHandlerUtils
					.getDestinationFile(opHandle.getDescription());
			newLogger.error(IncomingTransferFailedMessageMatcher.eqMatcher(
					filePath, writtenData, brokerID.getPublicKey()));

			workerClient
					.sendMessage(ErrorOcurredMessageHandleMatcher
							.eqMatcher(new ErrorOcurredMessageHandle(
									new GridProcessError(
											GridProcessErrorTypes.FILE_TRANSFER_ERROR))));

		}

		EasyMock.replay(newLogger);
		EasyMock.replay(workerClient);

		AcceptanceTestUtil.publishTestObject(component, brokerID, workerClient,
				WorkerClient.class);
		AcceptanceTestUtil.setExecutionContext(component, workerOD,
				brokerID.getPublicKey());

		worker.incomingTransferFailed(opHandle, exception, writtenData);

		EasyMock.verify(workerClient);
		EasyMock.verify(newLogger);

		component.setLogger(oldLogger);
	}

	public void receiveIncomingTransferCompletedWithUnknownHandle(
			WorkerComponent component, Worker worker, ContainerID senderId,
			String filePath, String logicalFileName, String operationType,
			long handleId, long fileSize, int amountWritten, String senderPubKey) {
		receiveIncomingTransferCompleted(component,	worker,
				createIncomingFileTransfer(handleId, senderId, filePath,
				logicalFileName, operationType, fileSize),
				amountWritten, false, true, false, false, false, senderPubKey);
	}

	public void receiveIncomingTransferCompletedFromUnknownClient(
			WorkerComponent component, Worker worker, ContainerID senderId,
			String filePath, String logicalFileName, String operationType,
			long handleId, long fileSize, int amountWritten, String senderPubKey) {
		receiveIncomingTransferCompleted(component,	worker,
				createIncomingFileTransfer(handleId, senderId, filePath,
				logicalFileName, operationType, fileSize),
				amountWritten, true, false, false, false, false, senderPubKey);
	}

	public void receiveIncomingTransferCompleted(WorkerComponent component,
			Worker worker, IncomingTransferHandle opHandle, int amountWritten,
			String senderPubKey) {
		receiveIncomingTransferCompleted(component, worker, opHandle,
				amountWritten, false, false, false, true, true, senderPubKey);
	}

	public void receiveIncomingTransferCompletedWorkingWorker(
			WorkerComponent component, Worker worker,
			IncomingTransferHandle opHandle, int amountWritten,
			String senderPubKey) {
		receiveIncomingTransferCompleted(component, worker, opHandle,
				amountWritten, false, false, false, true, false, senderPubKey);
	}

	public void receiveIncomingTransferCompletedUnstartedWorker(
			WorkerComponent component, Worker worker, ContainerID senderId,
			String filePath, String logicalFileName, String operationType,
			long handleId, long fileSize, int amountWritten, String senderPubKey) {

		receiveIncomingTransferCompleted(component,	worker,
				createIncomingFileTransfer(handleId, senderId, filePath,
				logicalFileName, operationType, fileSize),
				amountWritten, false, false, true, false, false, senderPubKey);
	}

	private void receiveIncomingTransferCompleted(WorkerComponent component,
			Worker worker, IncomingTransferHandle opHandle, int amountWritten,
			boolean unknownClient, boolean unknownHandle,
			boolean unstartedWorker, boolean isWorking,
			boolean isDownloadingState, String senderPublicKey) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);

		component.setLogger(newLogger);

		ObjectDeployment workerOD = getWorkerDeployment();

		if (unknownClient) {
			newLogger.warn("The worker received an incoming transfer completed"
					+ " message from a unknown client."
					+ " This message was ignored. Client public key: ["
					+ senderPublicKey + "].");
		} else if (unstartedWorker) {
			newLogger
					.warn(WorkerControllerMessages
							.getWorkerIsNotInWorkingStateIncomingTrasferCompleteMessage(senderPublicKey));
		}

		else if (isWorking && !isDownloadingState) {
			newLogger
					.warn(WorkerControllerMessages
							.getWorkerDoesNotRequestedAnyTransferMessage(senderPublicKey));
		} else {
			newLogger.debug("File successfully received from client."
					+ " Client public key: [" + senderPublicKey + "]. Handle: "
					+ opHandle + "." + " Amount of data received: "
					+ amountWritten + " bytes.");
		}

		EasyMock.replay(newLogger);

		AcceptanceTestUtil.setExecutionContext(component, workerOD,
				senderPublicKey);

		worker.incomingTransferCompleted(opHandle, amountWritten);

		EasyMock.verify(newLogger);

		component.setLogger(oldLogger);
	}

}
