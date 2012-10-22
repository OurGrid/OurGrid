package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.CleanAllFinishedJobsRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

public class CleanAllFinishedJobsRequester implements RequesterIF<CleanAllFinishedJobsRequestTO> {

	public List<IResponseTO> execute(CleanAllFinishedJobsRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		responses.add(new LoggerResponseTO("Trying to clean all finished jobs.", LoggerResponseTO.INFO));

		
		String senderPubKey = request.getSenderPublicKey();
		if (!request.isThisMyPublicKey()) {
			responses.add(new LoggerResponseTO(BrokerControlMessages.getUnknownSenderControllingBrokerMessage(senderPubKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (!request.canComponentBeUsed()) {
			responses.add(new LoggerResponseTO(BrokerControlMessages.getComponentNotStartedMessage(), LoggerResponseTO.ERROR));
			
			return responses;
		}
		
		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
		jobDAO.setBrokerControlClientAddress(request.getBrokerControlClientAddress());
		
		
		for(SchedulerIF scheduler: jobDAO.getSchedulers()) {
			scheduler.cleanAllFinishedJobs(responses);
		}
		
		return responses;
	}
}
