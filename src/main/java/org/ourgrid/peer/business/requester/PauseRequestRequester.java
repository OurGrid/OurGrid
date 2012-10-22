package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.peer.business.controller.messages.RequestMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.util.RequestUtils;
import org.ourgrid.peer.request.PauseRequestRequestTO;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.peer.to.Request;


public class PauseRequestRequester implements RequesterIF<PauseRequestRequestTO> {

	public List<IResponseTO> execute(PauseRequestRequestTO requestTO) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPubKey = requestTO.getBrokerPublicKey();
		
		if (!UserControl.getInstance().userExists(responses, senderPubKey)) {
			LoggerResponseTO to = new LoggerResponseTO(
					RequestMessages.getUnknownConsumerPausingRequestMessage(senderPubKey),
					LoggerResponseTO.WARN);
			responses.add(to);
			
			return responses;
		}

		Long requestId = requestTO.getRequestId();
		Request request = PeerDAOFactory.getInstance().getRequestDAO().getRequest(requestId);
		PeerUserReference loggedUser = PeerDAOFactory.getInstance().getUsersDAO().getLoggedUser(senderPubKey);
		
		if (loggedUser == null) {
			LoggerResponseTO to = new LoggerResponseTO(
					RequestMessages.getUnknownConsumerPausingRequestMessage(senderPubKey),
					LoggerResponseTO.WARN);
			responses.add(to);
			
			return responses;
		}
		
		String lwpcAddress = loggedUser.getWorkerProviderClientAddress();
		
		if (RequestUtils.isRequestUnknown( senderPubKey, request )) {
			
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getUnknownRequestPauseMessage(requestId, lwpcAddress),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return responses;
		}
		
		if(!request.isPaused()) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getRequestPausedMessage(requestId, lwpcAddress),
					LoggerResponseTO.DEBUG);
			responses.add(loggerResponse);
			
			RequestUtils.cancelScheduledRequest(responses, requestId);
			request.pause();
		} else {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getRequestAlreadyPausedMessage(requestId, lwpcAddress),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
		}
		
		return responses;
	}

}
