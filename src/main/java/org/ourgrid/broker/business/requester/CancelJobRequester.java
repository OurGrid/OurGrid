package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.CancelJobRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;

public class CancelJobRequester implements RequesterIF<CancelJobRequestTO> {

	public List<IResponseTO> execute(CancelJobRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		responses.add(new LoggerResponseTO("Trying to cancel a job.", LoggerResponseTO.INFO));
		
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
		
		int jobID = request.getJobID();
		
		if (!jobDAO.jobExists(jobID)) {
			responses.add(new LoggerResponseTO(BrokerControlMessages.getNoSuchJobToCancelMessage(jobID), LoggerResponseTO.WARN));
			
			responses.add(new LoggerResponseTO("Operation cancel job succeed.", LoggerResponseTO.INFO));
			
			OperationSucceedResponseTO to = new OperationSucceedResponseTO();
			to.setClientAddress(request.getBrokerControlClientAddress());
			to.setErrorCause(new Exception(BrokerControlMessages.getNoSuchJobToCancelMessage(jobID)));
			
			responses.add(to);
			
			return responses;
		}
		
		
		jobDAO.setBrokerControlClientAddress(request.getBrokerControlClientAddress());
		
		
		SchedulerIF scheduler = jobDAO.getJobScheduler(jobID);
		scheduler.cancelJob(jobID, responses);
		
		jobDAO.cancelJob(jobID);
		
		return responses;
	}
}
