package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.CreateMessageProcessorsResponseTO;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.communication.dao.WorkerMessageProcessorDAO;
import org.ourgrid.worker.communication.processors.GetFileInfoProcessor;
import org.ourgrid.worker.communication.processors.GetFilesProcessor;
import org.ourgrid.worker.communication.processors.RemoteExecuteProcessor;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class CreateMessageProcessorsSender implements SenderIF<CreateMessageProcessorsResponseTO> {

	public void execute(CreateMessageProcessorsResponseTO response,
			ServiceManager manager) {
	WorkerMessageProcessorDAO workerMessageProcessorDAO = WorkerDAOFactory.getInstance().getWorkerMessageProcessorDAO();
	
	workerMessageProcessorDAO.
		putMessageProcessor(WorkerConstants.GET_FILE_INFO_ACTION_NAME, new GetFileInfoProcessor());
	workerMessageProcessorDAO.
		putMessageProcessor(WorkerConstants.REMOTE_EXECUTE_ACTION_NAME, new RemoteExecuteProcessor());
	workerMessageProcessorDAO.
		putMessageProcessor(WorkerConstants.GET_FILES_ACTION_NAME, new GetFilesProcessor());
	}

}
