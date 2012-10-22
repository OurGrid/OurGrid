package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.request.AddJobRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

/**
 * Add Job message executor.
 */
public class AddJobRequester implements RequesterIF<AddJobRequestTO> {

	/**
	 * {@inheritDoc}
	 */
	public List<IResponseTO> execute(AddJobRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		responses.add(new LoggerResponseTO("Trying to add a job.", LoggerResponseTO.INFO));
		
		String senderPublicKey = request.getSenderPublicKey();
		
		if (!request.isThisMyPublicKey()) {
			responses.add(new LoggerResponseTO(BrokerControlMessages.getUnknownSenderControllingBrokerMessage(senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (!request.canComponentBeUsed()) {
			responses.add(new LoggerResponseTO(BrokerControlMessages.getComponentNotStartedMessage(), LoggerResponseTO.ERROR));
			
			return responses;
		}
		
		int jobID = BrokerDAOFactory.getInstance().getJobCounterDAO().getJobCounter().nextJobId();
		
		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
		
		jobDAO.setBrokerControlClientAddress(request.getBrokerControlClientAddress());
		jobDAO.addJobSpec(jobID, request.getJobSpec());
		jobDAO.addJob(jobID);
		
		jobDAO.getHeadScheduler().addJob(request.getJobSpec(), jobID, responses);
		
		return responses;
	}
}
