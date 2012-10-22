package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.StopBrokerRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

public class StopBrokerRequester implements RequesterIF<StopBrokerRequestTO> {

	public List<IResponseTO> execute(StopBrokerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPublicKey = request.getSenderPublicKey();
		responses.add(new LoggerResponseTO(BrokerControlMessages.getTryingToStopBrokerMessage(), LoggerResponseTO.INFO));
		
		if(!request.isThisMyPublicKey()) {
			responses.add(new LoggerResponseTO(BrokerControlMessages.getUnknownSenderControllingBrokerMessage(senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (!request.canComponentBeUsed()) {
			responses.add(new LoggerResponseTO(BrokerControlMessages.getComponentNotStartedMessage(), LoggerResponseTO.ERROR));
			
			return responses;
		}
		
		killJobDAO();
		finishRequests(responses);
		
		for(SchedulerIF scheduler: BrokerDAOFactory.getInstance().getJobDAO().getSchedulers()) {
			scheduler.stop();
		}
		
		responses.add(new LoggerResponseTO(BrokerControlMessages.getSuccessfullyShutdownBrokerMessage(), LoggerResponseTO.INFO));
		
		return responses;
	}
	
	/**
	 * Shuts the JobCounter down, by saving its last jobID into a file
	 */
	private void killJobDAO() {
		BrokerDAOFactory.getInstance().getJobCounterDAO().getJobCounter().shutdown(true);				
	}
	
	protected void finishRequests(List<IResponseTO> responses) {
		
		for(SchedulerIF scheduler: BrokerDAOFactory.getInstance().getJobDAO().getSchedulers()) {
			scheduler.finishRequests(responses);
		}
	}
}
