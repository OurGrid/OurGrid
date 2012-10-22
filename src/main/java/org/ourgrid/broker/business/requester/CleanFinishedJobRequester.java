package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.CleanFinishedJobRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

public class CleanFinishedJobRequester implements RequesterIF<CleanFinishedJobRequestTO> {

	public List<IResponseTO> execute(CleanFinishedJobRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		responses.add(new LoggerResponseTO("Trying to clean finished job.", LoggerResponseTO.INFO));
		
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
		
		int jobID = request.getJobID();
		
		SchedulerIF scheduler = jobDAO.getJobScheduler(jobID);
		scheduler.cleanFinishedJob(jobID, responses);
		
		return responses;
	}
}
