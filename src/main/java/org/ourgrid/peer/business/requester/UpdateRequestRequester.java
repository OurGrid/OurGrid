package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.peer.business.controller.messages.RequestMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.util.RequestUtils;
import org.ourgrid.peer.request.UpdateRequestRequestTO;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.peer.to.Request;


public class UpdateRequestRequester implements RequesterIF<UpdateRequestRequestTO> {

	public List<IResponseTO> execute(UpdateRequestRequestTO requestTO) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String userPubKey = requestTO.getBrokerPublicKey();
		
		if (!UserControl.getInstance().userExists(responses, userPubKey)) {
			
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getUnknownConsumerUpdatingRequestMessage(userPubKey),
					LoggerResponseTO.WARN);
			
			responses.add(loggerResponse);
			
			return responses;
		}
		
		PeerUserReference loggedUser = PeerDAOFactory.getInstance().getUsersDAO().getLoggedUser(userPubKey);
		if (loggedUser == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getUnknownConsumerUpdatingRequestMessage(userPubKey),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return responses;
		}
		
		String lwpcAddress = loggedUser.getWorkerProviderClientAddress();
		
		RequestSpecification requestSpec = requestTO.getRequestSpec();
		
		if(requestSpec  == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getNullRequestUpdateMessage(lwpcAddress),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return responses;
		}
		
		long requestID = requestSpec.getRequestId();
		Request request = PeerDAOFactory.getInstance().getRequestDAO().getRequest(requestID);
		
		if (RequestUtils.isRequestUnknown( userPubKey, request )) {	
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getUnknownRequestUpdateMessage(lwpcAddress, requestID),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return responses;
		}
		
		if(requestSpec.getRequiredWorkers() < 1) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getNonPositiveNoOfWorkersMessage(lwpcAddress, requestID),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return responses;
		}
		
		RequestUtils.cancelScheduledRequest(responses, requestID);

		request.setSpecification(requestSpec);
		
		if (request.needMoreWorkers()) {
			RequestUtils.scheduleRequest(responses, requestSpec);
		}
		
		return responses;
	}

}
