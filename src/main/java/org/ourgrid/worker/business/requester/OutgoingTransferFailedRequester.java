package org.ourgrid.worker.business.requester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.controller.FileTransferController;
import org.ourgrid.worker.business.dao.FileTransferDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.request.OutgoingTransferFailedRequestTO;

public class OutgoingTransferFailedRequester implements RequesterIF<OutgoingTransferFailedRequestTO>{

	public List<IResponseTO> execute(OutgoingTransferFailedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		OutgoingHandle handle = request.getHandle();
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		String senderPublicKey = request.getSenderPublicKey();
		String filePath = null;
		if (request.getHandle() != null) {
			filePath = request.getHandle().getLocalFile().getAbsolutePath();
		}
		
		
		if(!workerStatusDAO.isWorkingState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerIsNotInWorkingStateTrasferFailedMessage(senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		FileTransferDAO fileTransrfeDAO = WorkerDAOFactory.getInstance().getFileTransferDAO(); 

		if(workerStatusDAO.isFileTransferErrorState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerWithErrorReceivesAnOutgoingFileTransferFailedMessage(
							filePath, handle.getId(), request.getAmountUploaded(), senderPublicKey), LoggerResponseTO.WARN));
			return responses;
		}
		
		if(workerStatusDAO.isWorkingState() && fileTransrfeDAO.getUploadingFileHandles().isEmpty()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerDoesNotRequestedAnyTransferMessage(senderPublicKey),
													LoggerResponseTO.WARN));
			
			return responses;
		}
		
		File fileRejected = WorkerDAOFactory.getInstance().getFileTransferDAO().getUploadingFile(handle);

		if(fileRejected == null) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerReceivesAnOutgoingFileTransferFailedWithUnknownHandleMessage(
							handle.getId(), request.getAmountUploaded(), senderPublicKey), LoggerResponseTO.WARN));
			return responses;
		}
		
		FileTransferController.getInstance().outgoingTransferFailed(handle, request.getException(), request.getAmountUploaded(), responses);
		
		return responses;
	}

}
