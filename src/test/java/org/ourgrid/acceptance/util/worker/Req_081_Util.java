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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.common.FileTransferInfo;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.matchers.ErrorOcurredMessageHandleMatcher;
import org.ourgrid.matchers.FileRecoverErrorMessageMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.controller.GridProcessError;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.communication.processors.handle.GetFilesMessageHandle;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_081_Util extends WorkerAcceptanceUtil {

	public Req_081_Util(ModuleContext context) {
		super(context);
	}

	public void requestToRecoverFilesByUnknownClient(WorkerComponent component,
			long requestID, String senderPubKey, String... filePath) {
		requestToRecoverFile(component, requestID, senderPubKey, null, null,
				null, null, null, true, false, false, false, false, filePath);
	}

	public void requestToRecoverFilesWithInvalidVariable(
			WorkerComponent component, long requestID, String senderPubKey,
			String problematicPath, WorkerClient workerClient,
			String... filePath) {
		requestToRecoverFilesWithInvalidDirectory(component, requestID,
				senderPubKey, "Invalid variable found.", problematicPath,
				workerClient, true, filePath);
	}

	public void requestToRecoverFilesWithNotRelativePath(
			WorkerComponent component, long requestID, String senderPubKey,
			String problematicPath, WorkerClient workerClient,
			String absolutePath, String... filePath) {
		requestToRecoverFilesWithInvalidDirectory(component, requestID,
				senderPubKey, "File path is not relative to " + absolutePath
						+ " directory.", problematicPath, workerClient, true,
				filePath);
	}

	public void requestToRecoverFilesWithSamePaths(WorkerComponent component,
			long requestID, String senderPubKey, String problematicPath,
			WorkerClient workerClient, String... filePath) {
		requestToRecoverFilesWithInvalidDirectory(component, requestID,
				senderPubKey, "There are files with same path.",
				problematicPath, workerClient, false, filePath);
	}

	public void requestToRecoverAnInexistentFile(WorkerComponent component,
			long requestID, String senderPubKey, String problematicPath,
			WorkerClient workerClient, String... filePath) {
		requestToRecoverFilesWithInvalidDirectory(component, requestID,
				senderPubKey, "File not found.", problematicPath, workerClient,
				false, filePath);
	}

	public void requestToRecoverAnUnreadableFile(WorkerComponent component,
			long requestID, String senderPubKey, String problematicPath,
			WorkerClient workerClient, String... filePath) {
		requestToRecoverFilesWithInvalidDirectory(component, requestID,
				senderPubKey, "File cannot be read.", problematicPath,
				workerClient, true, filePath);
	}

	private void requestToRecoverFilesWithInvalidDirectory(
			WorkerComponent component, long requestID, String senderPubKey,
			String errorCause, String problematicPath,
			WorkerClient workerClient, boolean ioError, String... filePath) {
		requestToRecoverFile(component, requestID, senderPubKey, workerClient,
				errorCause, problematicPath, null, null, false, false, true,
				ioError, false, filePath);
	}

	public void requestToRecoverFilesOnUnstartedWorker(
			WorkerComponent component, long requestID, String senderPubKey,
			String... filePath) {
		requestToRecoverFile(component, requestID, senderPubKey, null, null,
				null, null, null, false, true, false, false, false, filePath);
	}

	public void requestToRecoverFilesBeforeExecutionFinish(
			WorkerComponent component, long requestID, String senderPubKey,
			String... filePath) {
		requestToRecoverFile(component, requestID, senderPubKey, null, null,
				null, null, null, false, false, false, false, false, filePath);
	}

	public void requestToRecoverFilesRemoteTaskFailedWorker(
			WorkerComponent component, long requestID, String senderPubKey,
			String... filePath) {
		requestToRecoverFile(component, requestID, senderPubKey, null, null,
				null, null, null, false, false, true, false, true, filePath);
	}

	public void requestToRecoverFilesExecutionFinished(String playpenDir,
			String storageDir, WorkerComponent component, long requestID,
			String senderPubKey, String... filePath) {
		requestToRecoverFile(component, requestID, senderPubKey, null, null,
				null, playpenDir, storageDir, false, false, true, false, false,
				filePath);
	}

	public List<TransferHandle> requestToRecoverFilesWithSuccess(
			WorkerComponent component, long requestID, String senderPubKey,
			WorkerClient workerClient, String playpenDir, String storageDir,
			String... filePath) {
		return requestToRecoverFile(component, requestID, senderPubKey,
				workerClient, null, null, playpenDir, storageDir, false, false,
				true, false, false, filePath);
	}

	public void requestToRecoverFilesThatAlreadyBeingUploaded(
			WorkerComponent component, long requestID, String senderPubKey,
			WorkerClient workerClient, String problematicDir,
			String playpenDir, String storageDir, String... filePath) {
		requestToRecoverFile(component, requestID, senderPubKey, workerClient,
				null, problematicDir, playpenDir, storageDir, false, false,
				true, false, false, filePath);
	}

	public void requestToRecoverFilesOnWorkerWithError(
			WorkerComponent component, long requestID,
			WorkerClient workerClient, String senderPubKey, String... filePath) {
		requestToRecoverFile(component, requestID, senderPubKey, workerClient,
				null, null, null, null, false, false, true, false, true,
				filePath);
	}

	private List<TransferHandle> requestToRecoverFile(WorkerComponent component, long requestID,
			String senderPubKey, WorkerClient workerClient,	String errorCause,
			String problematicPath, String playpenDir, String storageDir, boolean unknownClient,
			boolean isUnstartedWorker, boolean isExecutionFinish, boolean ioError,
			boolean isWorkerWithError, String... filePath) {

		FileTransferManager fileTransferManager = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(fileTransferManager);

		DeploymentID workerClientDeploymentID = null;

		if (workerClient == null) {
			workerClient = EasyMock.createMock(WorkerClient.class);	
			workerClientDeploymentID = new DeploymentID(new ContainerID("mgusername",
					"mgserver", "mgmodule", senderPubKey), "broker");

			createStub(workerClient, WorkerClient.class, workerClientDeploymentID);
		} else {
			EasyMock.reset(workerClient);
		}

		CommuneLogger oldLogger = component.getLogger();

		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);


		Worker worker = getWorker();
		ObjectDeployment workerOD = getWorkerDeployment();

		FileTransferInfo[] infos = createInfos(filePath);
		List<TransferHandle> handles = new LinkedList<TransferHandle>();

		if (unknownClient) {
			newLogger.warn("An unknown client tried to recover the files " + 
					Arrays.toString(filePath) + ". Unknown client public key: [" + senderPubKey + "].");
		} else {
			if (isUnstartedWorker) {
				newLogger.warn("A client tried to recover the files " 
			+ Arrays.toString(filePath) + ", but this Worker was not commanded to start " +
						"the work yet. This message was ignored. Client public key: [" 
			+ senderPubKey + "].");
				
			} else if (!isExecutionFinish) {
				newLogger.warn("A client tried to recover the files " + Arrays.toString(filePath) + 
						", but the Broker cannot download " +
						"files before the execution finish. This message was ignored." +
						" Client public key: [" + senderPubKey + "].");
			} else {

				if (isWorkerWithError) {
					newLogger.warn("A client tried to recover the files " + Arrays.toString(filePath) +
							". This message was ignored, because an error already ocurred." +
							" Client public key: [" + senderPubKey + "].");
				} else {
					if (errorCause != null) {
						newLogger.warn(FileRecoverErrorMessageMatcher
								.eqMatcher(errorCause, problematicPath,
										senderPubKey));

						if (ioError) {
							workerClient
									.sendMessage(ErrorOcurredMessageHandleMatcher
											.eqMatcher(new ErrorOcurredMessageHandle(
													new GridProcessError(
															GridProcessErrorTypes.IO_ERROR))));

						} else {
							workerClient
									.sendMessage(ErrorOcurredMessageHandleMatcher
											.eqMatcher(new ErrorOcurredMessageHandle(
													new GridProcessError(
															GridProcessErrorTypes.APPLICATION_ERROR))));
						}
					} else {

						for (FileTransferInfo info : infos) {

							String path = info.getFilePath();

							String completePath = path.startsWith("$"
									+ WorkerConstants.ENV_STORAGE) ? storageDir
									+ File.separator
									+ path.replace("$"
											+ WorkerConstants.ENV_STORAGE
											+ File.separator, "") : playpenDir
									+ File.separator
									+ path.replace("$"
											+ WorkerConstants.ENV_PLAYPEN
											+ File.separator, "");

							File file = new File(completePath);
							OutgoingTransferHandle outgoingFileTransfer = createOutgoingFileTransfer(
									info.getTransferHandleID(),
									workerClientDeploymentID, file.getName(),
									file);
							handles.add(outgoingFileTransfer);

							if (path.equals(problematicPath)) {
								newLogger
										.warn("The client tried to recover the" +
												" files that already being uploaded."
												+ " File path: ["
												+ completePath
												+ "]. Client public key: ["
												+ senderPubKey + "].");
							} else {
								newLogger
										.debug("The client tried to recover the files." +
												" Worker accepted the transfer request and is starting "
												+ "to upload the file. File path: ["
												+ completePath
												+ "]. Handle: "
												+ info.getTransferHandleID()
												+ ". Client public key: ["
												+ senderPubKey + "].");
								fileTransferManager.startTransfer(
										outgoingFileTransfer, worker);
							}
						}
					}
				}
			}
		}

		EasyMock.replay(fileTransferManager);
		EasyMock.replay(newLogger);
		EasyMock.replay(workerClient);

		MessageHandle handle = new GetFilesMessageHandle(0L, infos);
		AcceptanceTestUtil.setExecutionContext(component, workerOD, senderPubKey);
		worker.sendMessage(handle);

		EasyMock.verify(newLogger);
		EasyMock.verify(fileTransferManager);

		EasyMock.reset(newLogger);
		component.setLogger(oldLogger);

		return handles;
	}

	private FileTransferInfo[] createInfos(String[] filePath) {
		FileTransferInfo[] infos = null;
		if (filePath != null) {
			int handleCont = 1;
			infos =  new FileTransferInfo[filePath.length];
			for (int i = 0; i < filePath.length; i++) {
				infos[i] = new FileTransferInfo(handleCont++, filePath[i]);
			}
		}
		return infos;
	}

	public void receiveFileRejectWithUnknownHandle(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, DeploymentID brokerID) {
		receiveFileReject(component, workerClient, clientPublicKey, handle,
				null, true, false, false, false, false, false, brokerID);
	}

	public void receiveFileRejectUnstartedWorker(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, DeploymentID brokerID) {
		receiveFileReject(component, workerClient, clientPublicKey, handle,
				null, false, false, true, false, false, false, brokerID);
	}

	public void receiveFileRejectWorkingWorker(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, DeploymentID brokerID) {
		receiveFileReject(component, workerClient, clientPublicKey, handle,
				null, false, false, false, true, false, false, brokerID);
	}

	public void receiveFileRejectExecutionFinishedWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			DeploymentID brokerID) {

		receiveFileReject(component, workerClient, clientPublicKey, handle,
				null, false, false, false, true, false, true, brokerID);
	}

	public void receiveFileRejectOnWorkerWithError(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, String filePath,
			DeploymentID brokerID) {
		receiveFileReject(component, workerClient, clientPublicKey, handle,
				filePath, false, true, false, false, false, false, brokerID);
	}

	public void receiveFileReject(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, String filePath,
			DeploymentID brokerID) {
		receiveFileReject(component, workerClient, clientPublicKey, handle,
				filePath, false, false, false, false, true, false, brokerID);
	}

	public void receiveFileRejectWithError(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, String filePath,
			DeploymentID brokerID) {
		receiveFileReject(component, workerClient, clientPublicKey, handle,
				filePath, false, true, false, false, true, true, brokerID);
	}

	public void receiveFileRejectDownloadingStateWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			DeploymentID brokerID) {
		receiveFileReject(component, workerClient, clientPublicKey, handle,
				null, false, false, false, true, false, false, brokerID);
	}

	private void receiveFileReject(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, String filePath,
			boolean isUnknownHandle, boolean isWorkerWithError,
			boolean isUnstartedWorker, boolean isWorking,
			boolean isOutgoingFile, boolean isExecutionFinished,
			DeploymentID brokerID) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		FileTransferManager fileTransferManager = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(fileTransferManager);
		
		EasyMock.reset(workerClient);

		Worker worker = getWorker();

		if (isUnstartedWorker) {
			newLogger.warn(WorkerControllerMessages.
					getWorkerIsNotInWorkingStateTrasferRejectedMessage(clientPublicKey));
		}
		else if (isUnknownHandle) {
			newLogger.warn("The worker received a file reject message" +
					" with unknown handle. This message was ignored." +
					" Handle: " + handle + ". Client public key: [" + clientPublicKey + "].");
		} 

		else if (isWorking && !isOutgoingFile) {
			newLogger.warn(WorkerControllerMessages.
					getWorkerDoesNotRequestedAnyTransferMessage(clientPublicKey));
		}

		else {
			if (isWorkerWithError) {
				newLogger.warn("The worker received a file reject message from the client." +
						" This message was ignored, because an error already" +
						" ocurred. File path: [" + filePath + "]. Handle: " + handle +
						". Client public key: [" + clientPublicKey + "].");
				workerClient.sendMessage(ErrorOcurredMessageHandleMatcher.eqMatcher(
						new ErrorOcurredMessageHandle(new GridProcessError(
								GridProcessErrorTypes.APPLICATION_ERROR))));
				
			}else {
				newLogger.error("The worker received a file reject message from" +
						" the client. This message was successfully accepted. " +
						"File path: [" + filePath + "]. Handle: " + handle + "." +
								" Client public key: [" + clientPublicKey + "].");
				
				newLogger.error("Error ocurred: APPLICATION_ERROR - When a " +
						"APPLICATION_ERROR occurs it means that the task being executed failed " +
						"because of the user's task description. Verify your task description.", null);
				
				fileTransferManager.cancelOutgoingTransfer(handle);
				
				workerClient.sendMessage(ErrorOcurredMessageHandleMatcher.eqMatcher(
						new ErrorOcurredMessageHandle(new GridProcessError(
								GridProcessErrorTypes.APPLICATION_ERROR))));
			}
		}

		EasyMock.replay(fileTransferManager);
		EasyMock.replay(workerClient);
		EasyMock.replay(newLogger);

		ObjectDeployment workerOD = getWorkerDeployment();

		AcceptanceTestUtil.publishTestObject(application, brokerID,
				workerClient, WorkerClient.class);
		AcceptanceTestUtil.setExecutionContext(component, workerOD,
				brokerID);

		worker.transferRejected(handle);

		EasyMock.verify(fileTransferManager);
		EasyMock.verify(newLogger);
		component.setLogger(oldLogger);
	}

	public void receiveFileTransferCancelledWithUnstartedWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			long amountDataUploaded, DeploymentID brokerID) {
		receiveFileTransferCancelled(component, workerClient, clientPublicKey,
				handle, null, amountDataUploaded, false, false, true, false,
				false, false, false, brokerID);
	}

	public void receiveFileTransferCancelledWithUnknownHandle(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			long amountDataUploaded, DeploymentID brokerID) {
		receiveFileTransferCancelled(component, workerClient, clientPublicKey,
				handle, null, amountDataUploaded, true, false, false, false,
				false, false, false, brokerID);
	}

	public void receiveFileTransferCancelledOnWorkerWithError(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, DeploymentID brokerID) {
		receiveFileTransferCancelled(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, false, true, false,
				false, false, false, false, brokerID);
	}

	public void receiveFileTransferCancelledWorkingWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, DeploymentID brokerID) {
		receiveFileTransferCancelled(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, false, false, false,
				true, false, false, false, brokerID);
	}

	public void receiveFileTransferCancelledTaskFailedWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, DeploymentID brokerID) {
		receiveFileTransferCancelled(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, false, true, false, true,
				false, true, false, brokerID);
	}

	public void receiveFileTransferCancelledExecutionFinishedWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, DeploymentID brokerID) {

		receiveFileTransferCancelled(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, false, false, false,
				true, false, true, false, brokerID);
	}

	public void receiveFileTransferCancelledWithSuccess(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, DeploymentID brokerID) {

		receiveFileTransferCancelled(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, false, false, false,
				true, false, false, true, brokerID);
	}

	public void receiveFileTransferCancelledOnDownloadingStateWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, DeploymentID brokerID) {
		receiveFileTransferCancelled(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, false, false, false,
				true, true, false, false, brokerID);
	}

	private void receiveFileTransferCancelled(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, String filePath,
			long amountDataUploaded, boolean isUnknownHandle,
			boolean isWorkerWithError, boolean isWorkerUnstarted,
			boolean isWorking, boolean isDownloadingState,
			boolean isExecutionFinished, boolean isOutgoingFile,
			DeploymentID brokerID) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		FileTransferManager fileTransferManager = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(fileTransferManager);

		component.setLogger(newLogger);

		EasyMock.reset(workerClient);

		Worker worker = getWorker();
		if (isWorkerUnstarted) {
			newLogger.warn(WorkerControllerMessages.
					getWorkerIsNotInWorkingStateTrasferCancelledMessage(clientPublicKey));
		}

		else if (isUnknownHandle) {
			newLogger.warn("The worker received an outgoing transfer cancelled message" +
					" with unknown handle. This message was ignored." +
					" Handle: " + handle + ". Amount of data uploaded: " 
					+ amountDataUploaded + " bytes. Client public key: [" +
					clientPublicKey + "].");
		}

		 else {
			if (isWorkerWithError) {
				newLogger.warn("The worker received an outgoing transfer cancelled message" +
						" from the client. This message was ignored," +
						" because a error already ocurred. File path: " +
						"[" + filePath + "]. Handle: " + handle + ". " +
								"Amount of data uploaded: " +
						amountDataUploaded + " bytes. Client public key: " +
								"[" + clientPublicKey + "].");
				workerClient.sendMessage(ErrorOcurredMessageHandleMatcher.eqMatcher(
						new ErrorOcurredMessageHandle(new GridProcessError(
								GridProcessErrorTypes.APPLICATION_ERROR))));
				
			} else if (!isOutgoingFile) {
				newLogger.warn(WorkerControllerMessages.
							workerReceivedAnOutGoingTransferCanceledAndDoNotStartedTheUploadMessage(
									clientPublicKey));
			} else {
				
				newLogger.error("Error ocurred: APPLICATION_ERROR - When a " +
						"APPLICATION_ERROR occurs it means that the task being executed failed " +
						"because of the user's task description. Verify your task description.", null);
				
				newLogger.error("The worker received an outgoing transfer cancelled message" +
						" from the client. This message was successfully accepted. " +
						"File path: [" + filePath + "]. Handle: " + handle + "." +
								" Amount of data uploaded: " + amountDataUploaded + " bytes. " +
						"Client public key: [" + clientPublicKey + "].");
				
				
				fileTransferManager.cancelOutgoingTransfer(handle);
				workerClient.sendMessage(ErrorOcurredMessageHandleMatcher.eqMatcher(
						new ErrorOcurredMessageHandle(new GridProcessError(
								GridProcessErrorTypes.APPLICATION_ERROR))));
			}
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(workerClient);
		EasyMock.replay(fileTransferManager);

		ObjectDeployment workerOD = getWorkerDeployment();

		AcceptanceTestUtil.publishTestObject(application, brokerID,
				workerClient, WorkerClient.class);
		AcceptanceTestUtil.setExecutionContext(component, workerOD, brokerID);

		worker.outgoingTransferCancelled(handle, amountDataUploaded);

		EasyMock.verify(newLogger);
		EasyMock.verify(fileTransferManager);
		component.setLogger(oldLogger);
	}

	public void receiveFileTransferFailedWithUnknownHandle(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			long amountDataUploaded, Exception failCause, DeploymentID brokerID) {
		receiveFileTransferFailed(component, workerClient, clientPublicKey,
				handle, null, amountDataUploaded, failCause, true, false,
				false, false, false, false, brokerID);
	}

	public void receiveFileTransferFailedWithUnstartedWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			long amountDataUploaded, Exception failCause, DeploymentID brokerID) {
		receiveFileTransferFailed(component, workerClient, clientPublicKey,
				handle, null, amountDataUploaded, failCause, false, false,
				true, false, false, false, brokerID);
	}

	public void receiveFileTransferFailedOnWorkerWithError(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, Exception failCause,
			DeploymentID brokerID) {
		receiveFileTransferFailed(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, failCause, false, true,
				false, false, false, false, brokerID);
	}

	public void receiveFileTransferFailed(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, String filePath,
			long amountDataUploaded, Exception failCause, DeploymentID brokerID) {
		receiveFileTransferFailed(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, failCause, false, false,
				false, false, false, false, brokerID);
	}

	public void receiveFileTransferFailedTaskFailed(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, String filePath,
			long amountDataUploaded, Exception failCause, DeploymentID brokerID) {
		receiveFileTransferFailed(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, failCause, false, true,
				false, false, false, false, brokerID);
	}

	public void receiveFileTransferFailedOnWorkingWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, Exception failCause,
			DeploymentID brokerID) {

		receiveFileTransferFailed(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, failCause, false, false,
				false, true, false, false, brokerID);
	}

	public void receiveFileTransferFailedOnExecutionFinishedWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, Exception failCause,
			DeploymentID brokerID) {

		receiveFileTransferFailed(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, failCause, false, false,
				false, true, false, true, brokerID);
	}

	public void receiveFileTransferFailedOnDownloadingStateWorker(
			WorkerComponent component, WorkerClient workerClient,
			String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded, Exception failCause,
			DeploymentID brokerID) {

		receiveFileTransferFailed(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, failCause, false, false,
				false, true, false, false, brokerID);
	}

	private void receiveFileTransferFailed(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey, 
			OutgoingTransferHandle handle, String filePath,
			long amountDataUploaded, Exception failCause, boolean isUnknownHandle,
			boolean isWorkerWithError, boolean isWorkerUnstarted, boolean isWorking,
			boolean isOutgoingFile, boolean isExecutionFinished, DeploymentID brokerID) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		FileTransferManager fileTransferManager = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(fileTransferManager);

		component.setLogger(newLogger);

		EasyMock.reset(workerClient);

		Worker worker = getWorker();

		if (isWorkerUnstarted) {
			newLogger.warn(WorkerControllerMessages.
					getWorkerIsNotInWorkingStateTrasferFailedMessage(clientPublicKey));
		}
		else if (isUnknownHandle) {
			newLogger.warn("The worker received an outgoing transfer failed message with" +
					" unknown handle. This message was ignored." +
					" Handle: " + handle + ". Amount of data uploaded: "
					+ amountDataUploaded + " bytes. Client public key: [" +
					clientPublicKey + "].");
		}

		else if (isWorking && (!isOutgoingFile)) {
			newLogger.warn(WorkerControllerMessages.
					getWorkerDoesNotRequestedAnyTransferMessage(clientPublicKey));
		}

		else {

			if (isWorkerWithError) {
				newLogger.warn("The worker received an outgoing transfer " +
						"failed message from the client. This message was ignored," +
						" because an error already ocurred. File path: " +
						"[" + filePath + "]. Handle: " + handle + ". Amount of data uploaded: " +
						amountDataUploaded + " bytes. Client public key:" +
								" [" + clientPublicKey + "].");
				
				workerClient.sendMessage(ErrorOcurredMessageHandleMatcher.eqMatcher(
						new ErrorOcurredMessageHandle(new GridProcessError(
								GridProcessErrorTypes.APPLICATION_ERROR))));
			
			} else if (isExecutionFinished) {
				newLogger.warn("The worker received an outgoing transfer failed" +
						" message with unknown handle. This message was ignored. Handle: "
						+ handle + ". Amount of data uploaded: " +
						amountDataUploaded + " bytes. Client public key: [" + clientPublicKey + "].");
				
			} else {
				newLogger.error("The worker received an outgoing transfer failed message" +
						" from the client. This message was successfully accepted. " +
						"File path: [" + filePath + "]. Handle: " + handle + ". Amount of " +
								"data uploaded: " + amountDataUploaded + " bytes. " +
						"Client public key: [" + clientPublicKey + "].");
				
				newLogger.error("Error ocurred: APPLICATION_ERROR - When a " +
						"APPLICATION_ERROR occurs it means that the task being " +
						"executed failed because of the user's task description. " +
						"Verify your task description.", null);
				
				fileTransferManager.cancelOutgoingTransfer(handle);
				
				workerClient.sendMessage(ErrorOcurredMessageHandleMatcher.eqMatcher(
						new ErrorOcurredMessageHandle(new GridProcessError(
								GridProcessErrorTypes.APPLICATION_ERROR))));
			}
		}

		EasyMock.replay(workerClient);
		EasyMock.replay(newLogger);
		EasyMock.replay(fileTransferManager);

		ObjectDeployment workerOD = getWorkerDeployment();

		AcceptanceTestUtil.publishTestObject(application,
				brokerID, workerClient, WorkerClient.class);
		AcceptanceTestUtil.setExecutionContext(component, workerOD, brokerID);


		worker.outgoingTransferFailed(handle, failCause, amountDataUploaded);

		EasyMock.verify(newLogger);
		EasyMock.verify(fileTransferManager);
		component.setLogger(oldLogger);
	}

	public void receiveFileTransferCompletedWithUnknownHandle(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey, OutgoingTransferHandle handle,
			long amountDataUploaded) {
		receiveFileTransferCompleted(component, workerClient, clientPublicKey, handle,
				null, amountDataUploaded, true, false, false, false, false, false, false);
	}

	public void receiveFileTransferCompletedWithUnstartedWorker(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, long amountDataUploaded) {
		receiveFileTransferCompleted(component, workerClient, clientPublicKey, handle,
				null, amountDataUploaded, true, false, false, true, false, false, false);
	}

	public void receiveFileTransferCompletedOnWorkerWithError(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded) {
		receiveFileTransferCompleted(component, workerClient, clientPublicKey, handle,
				filePath, amountDataUploaded, false, true, false, false, false, false, false);
	}

	public void receiveFileTransferCompleted(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, String filePath,
			long amountDataUploaded) {
		receiveFileTransferCompleted(component, workerClient, clientPublicKey,
				handle, filePath, amountDataUploaded, false, false, false,
				false, false, true, false);
	}

	public void receiveFileTransferCompletedForLastUploadingFile(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded) {
		receiveFileTransferCompleted(component, workerClient, clientPublicKey, handle,
				filePath, amountDataUploaded, false, false, true, false, false, true, false);
	}

	public void receiveFileTransferCompletedOnWorkingWorker(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded) {
		receiveFileTransferCompleted(component, workerClient, clientPublicKey, handle,
				filePath, amountDataUploaded, false, false, false, false, true, false, false);
	}
	
	public void receiveFileTransferCompletedOnExecutionFinishedWorker(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded) {

		receiveFileTransferCompleted(component, workerClient, clientPublicKey, handle,
				filePath, amountDataUploaded, false, false, false, false, true, false, true);
	}
	
	public void receiveFileTransferCompletedOnRemoteTaskFailedWorker(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded) {

		receiveFileTransferCompleted(component, workerClient, clientPublicKey, handle,
				filePath, amountDataUploaded, false, true, false, false, true, true, true);
	}

	public void receiveFileTransferCompletedOnDownloadingStateWorker(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey, OutgoingTransferHandle handle,
			String filePath, long amountDataUploaded) {

		receiveFileTransferCompleted(component, workerClient, clientPublicKey, handle,
				filePath, amountDataUploaded, false, false, false, false, true, false, false);
	}

	private void receiveFileTransferCompleted(WorkerComponent component,
			WorkerClient workerClient, String clientPublicKey,
			OutgoingTransferHandle handle, String filePath,
			long amountDataUploaded, boolean isUnknownHandle,
			boolean isWorkerWithError, boolean allUploadsFinish,
			boolean isWorkerUnstarted, boolean isWorking,
			boolean isOutgoingFile, boolean isExecutionFinished) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);

		component.setLogger(newLogger);

		EasyMock.reset(workerClient);

		Worker worker = getWorker();
		if (isWorkerUnstarted) {
			newLogger.warn(WorkerControllerMessages.
					getWorkerIsNotInWorkingStateTrasferCompletedMessage(clientPublicKey));
		}
		else if (isUnknownHandle) {
			newLogger.warn("The worker received an outgoing transfer completed" +
					" message with unknown handle. This message was ignored." +
					" Handle: " + handle + ". Amount of data uploaded: "
					+ amountDataUploaded + " bytes. Client public key: [" +
					clientPublicKey + "].");
		}
		else if (!isOutgoingFile) {
			newLogger.warn(WorkerControllerMessages.
					getWorkerDoesNotRequestedAnyTransferMessage(clientPublicKey));
		}
		else {
			if (isWorkerWithError) {
				newLogger.warn("The worker received an outgoing transfer completed" +
						" message from the client. This message was ignored," +
						" because an error already ocurred. File path: [" + 
						filePath + "]. Handle: " + handle + ". Amount of data uploaded: " +
						amountDataUploaded + " bytes. Client public key: [" 
						+ clientPublicKey + "].");
				workerClient.sendMessage(new ErrorOcurredMessageHandle(
						new GridProcessError(GridProcessErrorTypes.APPLICATION_ERROR)));
				
			} else {
				newLogger.debug("The worker received an outgoing transfer completed" +
						" message from the client. This message was successfully accepted. " +
						"File path: [" + filePath + "]. Handle: " + handle + 
						". Amount of data uploaded: " + amountDataUploaded + " bytes. " +
						"Client public key: [" + clientPublicKey + "].");

				if (allUploadsFinish) {
					newLogger.info("All current uploading files has been finished.");
				}
			}
		}

		EasyMock.replay(workerClient);
		EasyMock.replay(newLogger);

		ObjectDeployment workerOD = getWorkerDeployment();

		DeploymentID wcID = new DeploymentID(handle.getOppositeID(), BrokerConstants.WORKER_CLIENT);
		AcceptanceTestUtil.publishTestObject(application, wcID, workerClient, WorkerClient.class);
		AcceptanceTestUtil.setExecutionContext(component, workerOD, wcID);


		worker.outgoingTransferCompleted(handle, amountDataUploaded);

		EasyMock.verify(newLogger);
		component.setLogger(oldLogger);
	}


}
