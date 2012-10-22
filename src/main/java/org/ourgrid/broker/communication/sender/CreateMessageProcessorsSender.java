package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.communication.dao.BrokerMessageProcessorDAO;
import org.ourgrid.broker.communication.processors.ErrorOcurredProcessor;
import org.ourgrid.broker.communication.processors.HereIsFileInfoProcessor;
import org.ourgrid.broker.communication.processors.HereIsGridProcessResultProcessor;
import org.ourgrid.broker.communication.processors.HereIsWorkerSpecProcessor;
import org.ourgrid.broker.communication.processors.WorkerIsReadyProcessor;
import org.ourgrid.broker.communication.processors.WorkerIsUnavailableProcessor;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.CreateMessageProcessorsResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class CreateMessageProcessorsSender implements SenderIF<CreateMessageProcessorsResponseTO> {

	public void execute(CreateMessageProcessorsResponseTO response,
			ServiceManager manager) {
		BrokerMessageProcessorDAO brokerMessageProcessorDAO = 
			BrokerDAOFactory.getInstance().getBrokerMessageProcessorDAO();
		
		brokerMessageProcessorDAO.
			putMessageProcessor(BrokerConstants.WORKER_IS_UNAVAILABLE_ACTION_NAME, new WorkerIsUnavailableProcessor());
		brokerMessageProcessorDAO.
			putMessageProcessor(BrokerConstants.WORKER_IS_READY_ACTION_NAME, new WorkerIsReadyProcessor());
		brokerMessageProcessorDAO.
			putMessageProcessor(BrokerConstants.HERE_IS_EXECUTION_RESULT_ACTION_NAME, new HereIsGridProcessResultProcessor());
		brokerMessageProcessorDAO.
			putMessageProcessor(BrokerConstants.ERROR_OCURRED_ACTION_NAME, new ErrorOcurredProcessor());
		brokerMessageProcessorDAO.
			putMessageProcessor(BrokerConstants.HERE_IS_FILE_INFO_ACTION_NAME, new HereIsFileInfoProcessor());
		brokerMessageProcessorDAO.
			putMessageProcessor(BrokerConstants.HERE_IS_WORKER_SPEC_ACTION_NAME, new HereIsWorkerSpecProcessor());
	}

}
