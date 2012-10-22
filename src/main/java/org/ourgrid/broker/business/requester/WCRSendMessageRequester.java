package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.WCRSendMessageRequestTO;
import org.ourgrid.broker.response.BrokerMessageProcessorResponseTO;
import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.util.StringUtil;

public class WCRSendMessageRequester implements RequesterIF<WCRSendMessageRequestTO> {

	public List<IResponseTO> execute(WCRSendMessageRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(
				StringUtil.addressToContainerID(request.getSenderAddress()));
		
		if (scheduler != null) {
			
			MessageHandle handle = request.getHandle();
			
			MessageProcessor<MessageHandle> processor = BrokerDAOFactory.getInstance().getBrokerMessageProcessorDAO().getMessageProcessor(
					handle.getActionName());
			
			if (processor != null) {
				BrokerMessageProcessorResponseTO to = new BrokerMessageProcessorResponseTO();
				to.setHandle(handle);
				to.setProcessor(processor);
				
				responses.add(to);
			} else {
				scheduler.sendMessage(handle);
			}
			
		} else {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					"No scheduler is set for worker [" + request.getSenderAddress()+"]", 
					LoggerResponseTO.WARN);
			
			responses.add(loggerResponse);
		}

		return responses;
	}
}
