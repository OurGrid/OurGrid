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
import org.ourgrid.peer.request.ResumeRequestRequestTO;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.peer.to.Request;


public class ResumeRequestRequester implements RequesterIF<ResumeRequestRequestTO> {

	public List<IResponseTO> execute(ResumeRequestRequestTO requestTO) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPubKey = requestTO.getBrokerPublicKey();
		Long requestID = requestTO.getRequestId();
		
		if (!UserControl.getInstance().userExists(responses, senderPubKey)) {
			LoggerResponseTO to = new LoggerResponseTO(
					RequestMessages.getUnknownConsumerResumingRequestMessage(senderPubKey),
					LoggerResponseTO.WARN);
			responses.add(to);
			
			return responses;
		}
		
		Request request = PeerDAOFactory.getInstance().getRequestDAO().getRequest(requestID);
		PeerUserReference loggedUser = PeerDAOFactory.getInstance().
								getUsersDAO().getLoggedUser(senderPubKey);
		
		if (loggedUser == null) {
			LoggerResponseTO to = new LoggerResponseTO(
					RequestMessages.getUnknownConsumerResumingRequestMessage(senderPubKey),
					LoggerResponseTO.WARN);
			responses.add(to);
			
			return responses;
		}
		
		String lwpcAddress = loggedUser.getWorkerProviderClientAddress();
		
		if(RequestUtils.isRequestUnknown( senderPubKey, request )) {
			LoggerResponseTO to = new LoggerResponseTO(
					RequestMessages.getUnknownRequestResumedMessage(requestID, lwpcAddress),
					LoggerResponseTO.WARN);
			responses.add(to);
			
			return responses;
		}
		
		if(request.isPaused()) {
			LoggerResponseTO to = new LoggerResponseTO(
					RequestMessages.getRequestResumedMessage(requestID, lwpcAddress),
					LoggerResponseTO.DEBUG);
			responses.add(to);
			
			request.resume();
			
			if(request.needMoreWorkers()) {
				RequestUtils.scheduleRequest(responses, request.getSpecification());
			}
		} else {
			LoggerResponseTO to = new LoggerResponseTO(
					RequestMessages.getNotPausedRequestMessage(requestID, lwpcAddress),
					LoggerResponseTO.WARN);
			responses.add(to);
		}
		
		return responses;
	}

}
