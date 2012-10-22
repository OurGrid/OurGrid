package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.FileTransferInfo;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.business.controller.FileTransferController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.communication.processors.handle.GetFilesMessageHandle;
import org.ourgrid.worker.request.GetFilesProcessorRequestTO;

public class GetFilesProcessorRequester implements RequesterIF<GetFilesProcessorRequestTO> {

	public List<IResponseTO> execute(GetFilesProcessorRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		GetFilesMessageHandle handle = request.getHandle();
		
		FileTransferInfo[] files = handle.getFiles();
		List<String> filePathList = createFilePathList(files);
		
		String senderPublicKey = request.getSenderPublicKey();
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();

		if(!workerStatusDAO.hasConsumer()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getClientRequestsToRecoverFilesOnUnstartedWorkerMessage(filePathList, senderPublicKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}

		if(!senderPublicKey.equals(workerStatusDAO.getConsumerPublicKey())) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getUnknownClientRequestsToRecoverFilesMessage(filePathList, senderPublicKey),
					LoggerResponseTO.WARN));

			return responses;
		}

		if(!WorkerDAOFactory.getInstance().getExecutionDAO().isExecutionFinished()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getClientRequestsToRecoverFilesBeforeExecutionFinishMessage(filePathList, senderPublicKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}

		if(workerStatusDAO.isFileTransferErrorState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getClientRequestsToRecoverFilesOnWorkerWithErrorMessage(filePathList, senderPublicKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}

		FileTransferController.getInstance().startTransfer(workerStatusDAO.getConsumerDeploymentID(), 
				workerStatusDAO.getConsumerPublicKey(), responses, files);
		
		return responses;
	}
	
	@Req("REQ081")
	private List<String> createFilePathList(FileTransferInfo... infos) {
		List<String> list = new LinkedList<String>(); 
		if(infos != null) {
			for (FileTransferInfo info : infos) {
				list.add(info.getFilePath());
			}
			return list;
		}
		return null;
	}
}
