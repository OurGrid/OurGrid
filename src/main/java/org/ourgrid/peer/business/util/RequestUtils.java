package org.ourgrid.peer.business.util;

import java.util.List;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.peer.business.controller.messages.RequestMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.RequestDAO;
import org.ourgrid.peer.response.CancelRequestFutureResponseTO;
import org.ourgrid.peer.response.ScheduleRequestResponseTO;
import org.ourgrid.peer.to.Request;

public class RequestUtils {

	public static boolean isRequestUnknown( String userPubKey, Request request ) {
		return request == null || !request.getConsumer().getPublicKey().equals(userPubKey);
	}

	public static void scheduleRequest(List<IResponseTO> responses, RequestSpecification requestSpec) {
		RequestDAO requestDAO = PeerDAOFactory.getInstance().getRequestDAO();
		
		if(! requestDAO.containsScheduledRequest(requestSpec.getRequestId())) {
			
			int delay = PeerDAOFactory.getInstance().getPeerPropertiesDAO().getRequestRepeatDelayInSeconds();
			
			long requestID = requestSpec.getRequestId();
			
			ScheduleRequestResponseTO scheduleRequest = new ScheduleRequestResponseTO();
			scheduleRequest.setDelay(delay);
			scheduleRequest.setRequestId(requestID);
			
			responses.add(scheduleRequest);
			
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getRequestRepetitionMessage(delay, requestID), 
					LoggerResponseTO.DEBUG);
			
			responses.add(loggerResponse);
			
			requestDAO.addScheduledRequest(requestID);
		}
		
	}

	public static void cancelScheduledRequest(List<IResponseTO> responses, Long requestId) {
		if (PeerDAOFactory.getInstance().getRequestDAO().removeScheduledRequest(requestId)) {
			CancelRequestFutureResponseTO to = new CancelRequestFutureResponseTO();
			to.setRequestId(requestId);
			responses.add(to);
		}
	}

}
