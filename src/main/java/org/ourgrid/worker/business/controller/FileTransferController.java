/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
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
package org.ourgrid.worker.business.controller;

import static org.ourgrid.common.CommonConstants.PUT_TRANSFER;
import static org.ourgrid.common.CommonConstants.STORE_TRANSFER;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.FileTransferInfo;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.CancelIncomingTransferResponseTO;
import org.ourgrid.common.internal.response.CancelOutgoingTransferResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.util.FileTransferHandlerUtils;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.business.dao.FileTransferDAO;
import org.ourgrid.worker.business.dao.WorkAccountingDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.FileTransferControllerMessages;
import org.ourgrid.worker.response.AcceptTransferResponseTO;
import org.ourgrid.worker.response.ErrorOcurredMessageHandleResponseTO;
import org.ourgrid.worker.response.RejectTransferResponseTO;
import org.ourgrid.worker.response.StartTransferResponseTO;

public class FileTransferController {

	private static FileTransferController instance = null;
	
	@Req("REQ080")
	public static synchronized FileTransferController getInstance() {
		if (instance == null) {
			instance = new FileTransferController();
		}
		return instance;
	}
	
	@Req("REQ080")
	public void cancelCurrentTransfers(List<IResponseTO> responses) {
		
		FileTransferDAO fileTransferDAO = WorkerDAOFactory.getInstance().getFileTransferDAO();
		
		for (IncomingHandle handle : fileTransferDAO.getIncomingFileHandles()) {
			CancelIncomingTransferResponseTO to = new CancelIncomingTransferResponseTO();
			to.setIncomingHandle(handle);

			responses.add(to);
			
			fileTransferDAO.removeIncomingFile(handle);
		}
		
		for (OutgoingHandle handle : fileTransferDAO.getUploadingFileHandles()) {
			CancelOutgoingTransferResponseTO to = new CancelOutgoingTransferResponseTO();
			to.setOutgoingHandle(handle);

			responses.add(to);
			
			fileTransferDAO.removeUploadingFile(handle);
		}
	}

	@Req("REQ080")
	public void rejectTransferRequest(IncomingHandle handle, List<IResponseTO> responses) {
		RejectTransferResponseTO to = new RejectTransferResponseTO();
		to.setIncomingHandle(handle);
		
		responses.add(to);
	}

	@Req("REQ081")
	private void rejectInvalidRequest(IncomingHandle handle, List<IResponseTO> responses) {
		RejectTransferResponseTO to = new RejectTransferResponseTO();
		to.setIncomingHandle(handle);
		
		responses.add(to);
		
		cancelCurrentTransfers(responses);
		
		WorkerDAOFactory.getInstance().getWorkerStatusDAO().setFileTransferErrorState(true);
	}
	
	@Req("REQ080")
	public void acceptTransferRequest(IncomingHandle handle, String destinationFile, List<IResponseTO> responses) {
		
		String path = destinationFile;
		String transferDescription = FileTransferHandlerUtils.getOperationType(handle.getDescription());
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		FileTransferDAO fileTransferDAO = WorkerDAOFactory.getInstance().getFileTransferDAO();
		String consumerPublicKey = workerStatusDAO.getConsumerPublicKey();
		
		if (!validateTransferDescription(transferDescription)) {
			responses.add(new LoggerResponseTO(FileTransferControllerMessages.
					getClientRequestsToTransferFileWithInvalidTransferDescription(transferDescription, consumerPublicKey), 
					LoggerResponseTO.WARN));
			
			
			rejectInvalidRequest(handle, responses);
			return;
		}
		
		if (fileTransferDAO.containsHandle(handle)) {
			responses.add(new LoggerResponseTO(FileTransferControllerMessages.
					getClientRequestsToTransferFileWithRepeatedHandleMessage(handle.getId(), consumerPublicKey), 
					LoggerResponseTO.ERROR));
			
			errorOcurred(GridProcessErrorTypes.INVALID_SESSION, responses);
			cancelCurrentTransfers(responses);
			return;
		}
		
		String solvedDir;
		
		try {
			if (transferDescription.equals(PUT_TRANSFER)) {
				solvedDir = EnvironmentController.getInstance().solveDir(path);
			} else {
				solvedDir = EnvironmentController.getInstance().solveStorageDir(path);
			}
		} catch (IOException e) {
			responses.add(new LoggerResponseTO(FileTransferControllerMessages.
					getClientRequestsToTranferFileButErrorOccuredOnSolvingPathMessage(path, handle.getFileSize(),
							handle.getId(), consumerPublicKey, e.getMessage()), LoggerResponseTO.WARN));
			
			rejectInvalidRequest(handle, responses);
			return;
		}
		
		if (fileTransferDAO.containsIncomingFile(solvedDir)) {
			responses.add(new LoggerResponseTO(FileTransferControllerMessages.
					getClientRequestsToTranferIncomingFileMessage(solvedDir, consumerPublicKey), LoggerResponseTO.WARN));
			
			rejectInvalidRequest(handle, responses);
			return;
		}
		
		responses.add(new LoggerResponseTO(FileTransferControllerMessages.
				getTransferRequestAcceptedMessage(solvedDir, handle.getId(), consumerPublicKey), LoggerResponseTO.DEBUG));
		
		File file = new File(solvedDir);
		fileTransferDAO.addIncomingFile(handle, solvedDir);
		
		AcceptTransferResponseTO to = new AcceptTransferResponseTO();
		to.setIncomingHandle(handle);
		to.setFile(file);
		
		responses.add(to);
	}
	
	private boolean validateTransferDescription(String transferDescription) {
		return (PUT_TRANSFER.equals(transferDescription) || STORE_TRANSFER.equals(transferDescription));
	}
	
	private void increaseWorkDataAccounting(long amount, boolean finishAccount) {
		WorkAccountingDAO workAccountingDAO = WorkerDAOFactory.getInstance().getWorkAccountingDAO();
		WorkAccounting currentWorkAccounting = workAccountingDAO.getCurrentWorkAccounting();

		if (currentWorkAccounting != null) {
			currentWorkAccounting.incDataTransfered(amount);
			
			//finish current accounting
			if (finishAccount) {
				workAccountingDAO.addWorkAccounting(currentWorkAccounting);
				workAccountingDAO.setCurrentWorkAccounting(null);
			}
		}
	}

	@Req("REQ080")
	public void incomingTransferFailed(IncomingHandle handle, Exception failCause, long amountWritten,
			List<IResponseTO> responses) {
		
		String incomingFileFailedPath =  WorkerDAOFactory.getInstance().getFileTransferDAO().removeIncomingFile(handle);
		String consumerPublicKey = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getConsumerPublicKey();
		
		increaseWorkDataAccounting(amountWritten, false);
		
		responses.add(new LoggerResponseTO(FileTransferControllerMessages.
				getIncomingTransferFailedMessage(incomingFileFailedPath, amountWritten, consumerPublicKey), LoggerResponseTO.ERROR));
		
		errorOcurred(failCause, GridProcessErrorTypes.FILE_TRANSFER_ERROR, responses);
	}
	
	@Req("REQ080")
	public void incomingTransferCompleted(IncomingHandle handle, long amountWritten, List<IResponseTO> responses) {
		
		WorkerDAOFactory.getInstance().getFileTransferDAO().removeIncomingFile(handle);
		String consumerPublicKey = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getConsumerPublicKey();
		
		increaseWorkDataAccounting(amountWritten, false);
		
		responses.add(new LoggerResponseTO(FileTransferControllerMessages.
				getIncomingTransferCompletedMessage(handle.getId(), amountWritten, consumerPublicKey), LoggerResponseTO.DEBUG));
	}
	
	@Req("REQ081")
	private void errorOcurred(Exception exception, GridProcessErrorTypes error, List<IResponseTO> responses) {
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		responses.add(new ErrorOcurredMessageHandleResponseTO(new GridProcessError(exception, error), workerStatusDAO.getConsumerAddress()));
		cancelCurrentTransfers(responses);
		
		workerStatusDAO.setFileTransferErrorState(true);
	}
	
	@Req("REQ081")
	private void errorOcurred(GridProcessErrorTypes error, List<IResponseTO> responses) {
		errorOcurred(null, error, responses);
	}
	
	@Req("REQ081")
	public void fileRejected(OutgoingHandle handle, List<IResponseTO> responses) {
		
		String consumerPublicKey = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getConsumerPublicKey();
		String rejectedFilePath = WorkerDAOFactory.getInstance().getFileTransferDAO().getUploadingFile(handle).getAbsolutePath();
		
		responses.add(new LoggerResponseTO(FileTransferControllerMessages.getWorkerReceivesAFileTransferRejectedMessage(
				rejectedFilePath, handle.getId(), consumerPublicKey), LoggerResponseTO.ERROR));

		errorOcurred(GridProcessErrorTypes.APPLICATION_ERROR, responses);
	}
	
	@Req("REQ081")
	public void outgoingTransferFailed(OutgoingHandle handle, Exception exception,
			long amountUploaded, List<IResponseTO> responses) {
		
		String consumerPublicKey = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getConsumerPublicKey();
		String failedFilePath = WorkerDAOFactory.getInstance().getFileTransferDAO().getUploadingFile(handle).getAbsolutePath();
		
		increaseWorkDataAccounting(amountUploaded, true);
		
		responses.add(new LoggerResponseTO(FileTransferControllerMessages.
				getWorkerReceivesAnOutgoingFileTransferFailedMessage(
						failedFilePath, handle.getId(), amountUploaded, consumerPublicKey), LoggerResponseTO.ERROR));
		
		errorOcurred(GridProcessErrorTypes.APPLICATION_ERROR, responses);
		
	}
	
	@Req("REQ081")
	public void outgoingTransferCancelled(OutgoingHandle handle, long amountUploaded, List<IResponseTO> responses) {
	
		String consumerPublicKey = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getConsumerPublicKey();
		String cancelledFilePath = WorkerDAOFactory.getInstance().getFileTransferDAO().getUploadingFile(handle).getAbsolutePath();
		
		increaseWorkDataAccounting(amountUploaded, true);

		responses.add(new LoggerResponseTO(FileTransferControllerMessages.
				getWorkerReceivesAnOutgoingFileTransferCancelledMessage(
						cancelledFilePath, handle.getId(), amountUploaded, consumerPublicKey), LoggerResponseTO.ERROR));
		
		errorOcurred(GridProcessErrorTypes.APPLICATION_ERROR, responses);
		
	}

	@Req("REQ081")
	public void outgoingTransferCompleted(OutgoingHandle handle, long amountUploaded, List<IResponseTO> responses) {
		
		FileTransferDAO fileTransferDAO = WorkerDAOFactory.getInstance().getFileTransferDAO();
		
		String consumerPublicKey = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getConsumerPublicKey();
		String filePath = fileTransferDAO.removeUploadingFile(handle);
		
		boolean hasUploadingFile = fileTransferDAO.hasUploadingFile();
		increaseWorkDataAccounting(amountUploaded, !hasUploadingFile);

		responses.add(new LoggerResponseTO(FileTransferControllerMessages.
				getWorkerReceivesAnOutgoingFileTransferCompletedMessage(filePath, handle.getId(), amountUploaded, consumerPublicKey), 
				LoggerResponseTO.DEBUG));
		
		if(!hasUploadingFile) {
			responses.add(new LoggerResponseTO(FileTransferControllerMessages.getAllUploadsFinishMessage(), 
					LoggerResponseTO.INFO));
		}
	}
	
	@Req("REQ081")
	public void startTransfer(String destinationID, String destPublicKey, List<IResponseTO> responses, FileTransferInfo... files) {
		
		List<FileTransferInfo> solvedInfos = solveFilesInfo(responses, files);
		
		FileTransferDAO fileTransferDAO = WorkerDAOFactory.getInstance().getFileTransferDAO();
		
		if(solvedInfos != null) {
			for (FileTransferInfo info : solvedInfos) {
				
				File file = new File(info.getFilePath());
				String filePath = file.getAbsolutePath();
				
				if (fileTransferDAO.containsUploadingFile(filePath)) {
					responses.add(new LoggerResponseTO(FileTransferControllerMessages.
							getClientRequestsToRecoverAnUploadingFileMessage(filePath, destPublicKey), 
							LoggerResponseTO.WARN));

					return;
				}
				
				responses.add(new LoggerResponseTO(FileTransferControllerMessages.getWorkerStartsFileTransferWithSuccess(filePath,
						info.getTransferHandleID(), destPublicKey), LoggerResponseTO.DEBUG));

				
				OutgoingHandle outgoingHandle = new OutgoingHandle(info.getTransferHandleID(),
						file.getName(), file, null, destinationID);
				
				StartTransferResponseTO to = new StartTransferResponseTO();
				to.setOutgoingHandle(outgoingHandle);
				
				responses.add(to);
				
				fileTransferDAO.addUploadingFile(outgoingHandle, filePath);
			}
		}
	}
	
	@Req("REQ081")
	private List<FileTransferInfo> solveFilesInfo(List<IResponseTO> responses, FileTransferInfo... files) {
		
		String consumerPublicKey = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getConsumerPublicKey();
		
		if(files == null) {
			responses.add(new LoggerResponseTO(FileTransferControllerMessages.
					getClientRequestsToRecoverFilesWithNullFilesMessage(consumerPublicKey), LoggerResponseTO.WARN));
			
			errorOcurred(GridProcessErrorTypes.APPLICATION_ERROR, responses);
			return null;
		}
		
		List<FileTransferInfo> solvedInfoList = new LinkedList<FileTransferInfo>();
		
		for (FileTransferInfo info : files) {
			
			String filePath = info.getFilePath();
			
			try {
				String solvedDir = EnvironmentController.getInstance().solveDir(filePath);

				if (hasInfoWithSamePath(solvedInfoList, solvedDir)) {
					responses.add(new LoggerResponseTO(FileTransferControllerMessages.
							getClientRequestsToRecoverFilesButThereAreFilesWithSamePathMessage(filePath, consumerPublicKey), 
							LoggerResponseTO.WARN));
					
					errorOcurred(GridProcessErrorTypes.APPLICATION_ERROR, responses);
					return null;
				}
				
				File file = new File(solvedDir);
				if (!file.exists()) {
					responses.add(new LoggerResponseTO(FileTransferControllerMessages.
							getClientRequestsToRecoverAnInexistentFileMessage(solvedDir, consumerPublicKey), 
							LoggerResponseTO.WARN));
					
					errorOcurred(GridProcessErrorTypes.APPLICATION_ERROR, responses);
					return null;
				}
				
				solvedInfoList.add(new FileTransferInfo(info.getTransferHandleID(), solvedDir));
			} catch (IOException e) {
				responses.add(new LoggerResponseTO(FileTransferControllerMessages.
						getClientRequestsToRecoverFilesButErrorOccuredOnSolvingPathMessage(filePath, consumerPublicKey, e.getMessage()), 
						LoggerResponseTO.WARN));
				
				errorOcurred(e, GridProcessErrorTypes.IO_ERROR, responses);
				return null;
			}
		}
		
		return solvedInfoList;
	}
	
	@Req("REQ081")
	private boolean hasInfoWithSamePath(List<FileTransferInfo> solvedInfoList, String filePath) {
		for (FileTransferInfo info : solvedInfoList) {
			if (info.getFilePath().equals(filePath)) {
				return true;
			}
		}
		return false;
	}

}