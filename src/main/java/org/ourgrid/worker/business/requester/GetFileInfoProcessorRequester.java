package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.controller.EnvironmentController;
import org.ourgrid.worker.business.controller.GridProcessError;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.communication.processors.handle.GetFileInfoMessageHandle;
import org.ourgrid.worker.request.GetFileInfoProcessorRequestTO;
import org.ourgrid.worker.response.ErrorOcurredMessageHandleResponseTO;
import org.ourgrid.worker.response.HereIsFileInfoMessageHandleResponseTO;

public class GetFileInfoProcessorRequester implements RequesterIF<GetFileInfoProcessorRequestTO> {

	public List<IResponseTO> execute(GetFileInfoProcessorRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		GetFileInfoMessageHandle handle = request.getHandle();
		
		String filePath = handle.getFilePath();
		String senderPubKey = request.getSenderPublicKey();
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		if (!senderPubKey.equals(workerStatusDAO.getConsumerPublicKey())) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getUnknownClientTriesToGetFileInfoMessage(filePath, senderPubKey), 
					LoggerResponseTO.WARN));
			
			return responses;
		}

		if(!workerStatusDAO.isWorkingState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getClientWithoutStartingWorkTriesToGetFileInfoMessage(filePath, senderPubKey), 
					LoggerResponseTO.WARN));
			return responses;
		}
		
		if(!workerStatusDAO.hasConsumer()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getClientWithoutStartingWorkTriesToGetFileInfoMessage(filePath, senderPubKey), 
					LoggerResponseTO.DEBUG));
			
			return responses;
		}

		String consumerAddress = workerStatusDAO.getConsumerAddress();
		
		String solvedStorageDir;
		
		try {
			solvedStorageDir = EnvironmentController.getInstance().solveStorageDir(filePath);
			String fileDigest = EnvironmentController.getInstance().getFileDigest(solvedStorageDir);
			
			responses.add(new HereIsFileInfoMessageHandleResponseTO(handle.getHandleId(),
					new FileInfo(filePath, fileDigest), consumerAddress));
		} catch (Exception exception) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getErrorWhileGettingFileInfoMessage(filePath,
					senderPubKey, exception.getMessage()), 
					LoggerResponseTO.WARN));
			
			responses.add(new ErrorOcurredMessageHandleResponseTO(new GridProcessError(exception,
					GridProcessErrorTypes.APPLICATION_ERROR), consumerAddress));

			return responses;
		}

		responses.add(new LoggerResponseTO(WorkerControllerMessages.getSuccessfulGetFileInfoMessage(
				solvedStorageDir, senderPubKey), 
				LoggerResponseTO.DEBUG));
		
		return responses;
	}
	
	
}
