package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.controller.ExecutionController;
import org.ourgrid.worker.business.dao.ExecutionDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.communication.processors.handle.RemoteExecuteMessageHandle;
import org.ourgrid.worker.request.RemoteExecuteProcessorRequestTO;

public class RemoteExecuteProcessorRequester implements RequesterIF<RemoteExecuteProcessorRequestTO> {

	public List<IResponseTO> execute(RemoteExecuteProcessorRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		RemoteExecuteMessageHandle handle = request.getHandle();
		
		long requestID = handle.getRequestID();
		String command = handle.getCommand();
		Map<String, String> environmentVars = handle.getEnvironmentVars();

		String senderPublicKey = request.getSenderPublicKey();

		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		ExecutionDAO executionDAO = WorkerDAOFactory.getInstance().getExecutionDAO();
		
		if (executionDAO.getCurrentHandle() != null) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.getRemoteExecuteInAnAlreadyExecutingWorkerMessage(command, senderPublicKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if(!workerStatusDAO.hasConsumer()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.getRemoteExecuteInANonWorkingWorkerMessage(command, senderPublicKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}

		if(!senderPublicKey.equals(workerStatusDAO.getConsumerPublicKey())) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.getUnknownClientTriesToRemoteExecuteMessage(command, senderPublicKey),
					LoggerResponseTO.WARN));

			return responses;
		}

		if(workerStatusDAO.isFileTransferErrorState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.getRemoteExecuteOnWorkerWithErrorMessage(command, senderPublicKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (executionDAO.isExecutionFinished()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getRemoteExecuteInANExecutionFinishedWorkerMessage(command, senderPublicKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		ExecutionController.getInstance().scheduleCommand(requestID, environmentVars, command,
				senderPublicKey, responses, request.isExecutionClientDeployed());

		return responses;
	}
	
}
