package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.dao.PeerEntry;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.LWPDoNotifyFailureRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

public class LWPDoNotifyFailureRequester implements RequesterIF<LWPDoNotifyFailureRequestTO> {

	public List<IResponseTO> execute(LWPDoNotifyFailureRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		PeerEntry peerEntry = BrokerDAOFactory.getInstance().getPeerDAO().getPeerEntry(request.getPeerAddress());
		
		if (peerEntry == null) {
			responses.add(new LoggerResponseTO(BrokerControlMessages.getNoPeerWithSuchEntityIDMessage(request.getPeerID()), LoggerResponseTO.WARN));
			
			return responses;
		} else if (peerEntry.isDown()){
			responses.add(new LoggerResponseTO(BrokerControlMessages.getPeerAlreadyDownMessage(request.getPeerID()), LoggerResponseTO.WARN));
			
			return responses;
		} 
		
		peerEntry.setAsDown();
		responses.add(new LoggerResponseTO(BrokerControlMessages.getPeerIsDownMessage(request.getPeerID()), LoggerResponseTO.DEBUG));
		
		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
		
		for(SchedulerIF scheduler: jobDAO.getSchedulers()) {
			scheduler.localWorkerProviderFailure(request.getPeerID());
		}
		
		return responses;
	}
	
}
