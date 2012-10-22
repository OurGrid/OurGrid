package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.control.JobControl;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.peer.business.controller.accounting.AccountingCommitController;
import org.ourgrid.peer.business.controller.allocation.RedistributionController;
import org.ourgrid.peer.business.controller.messages.RequestMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.RequestDAO;
import org.ourgrid.peer.business.util.RequestUtils;
import org.ourgrid.peer.request.FinishRequestRequestTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.peer.to.Request;


public class FinishRequestRequester implements RequesterIF<FinishRequestRequestTO> {

	public List<IResponseTO> execute(FinishRequestRequestTO requestTO) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String brokerPublicKey = requestTO.getBrokerPublicKey();
		RequestSpecification requestSpec = requestTO.getRequestSpec();
		
		PeerUser user = UserControl.getInstance().getUserByPublicKey(responses, brokerPublicKey);
		
		if (user == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getUnknownUserFinishingRequestMessage(brokerPublicKey),
					LoggerResponseTO.WARN);
			
			responses.add(loggerResponse);
			return responses;
		}
		
		RequestDAO requestDAO = PeerDAOFactory.getInstance().getRequestDAO();
		Request request = requestDAO.getRequest(requestSpec.getRequestId());

		PeerUserReference loggedUser = PeerDAOFactory.getInstance().getUsersDAO().getLoggedUser(brokerPublicKey);
		
		if (loggedUser == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getUnknownUserFinishingRequestMessage(brokerPublicKey),
					LoggerResponseTO.WARN);
			
			responses.add(loggerResponse);
			return responses;
		}
		
		String lwpcAddress = loggedUser.getWorkerProviderClientAddress();
		
		if (RequestUtils.isRequestUnknown( brokerPublicKey, request )) {
			
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getUnknownRequestFinishedMessage(requestSpec, lwpcAddress),
					LoggerResponseTO.WARN);
			
			responses.add(loggerResponse);
			return responses;
		}
		
		LoggerResponseTO loggerResponse = new LoggerResponseTO(
				RequestMessages.getFinishRequestMessage(requestSpec, lwpcAddress),
				LoggerResponseTO.DEBUG);
		responses.add(loggerResponse);
		
		requestDAO.removeRequest(requestSpec.getRequestId());
		
		List<Request> runningRequests = requestDAO.getRunningRequests();

		boolean hasAnotherRequest = false;
		for (Request runningRequest: runningRequests) {
			if (runningRequest.getConsumer().getPublicKey().equals(brokerPublicKey)) {
				hasAnotherRequest = true;
				break;
			}
		}
		
		if (!hasAnotherRequest) {
			PeerDAOFactory.getInstance().getConsumerDAO().removeLocalConsumer(brokerPublicKey);
		}
		
		RequestUtils.cancelScheduledRequest(responses, requestSpec.getRequestId());
		
		List<AllocableWorker> allocableWorkers = new LinkedList<AllocableWorker>(request.getAllocableWorkers());
		
		for (AllocableWorker worker : allocableWorkers) {
			RedistributionController.getInstance().redistributeWorker(responses, worker);
		}
		
		AccountingCommitController.getInstance().commitAccounting(responses, requestSpec, requestTO.getMyCertPathDN());
		
		JobControl.getInstance().finishRequest(responses, request, false);
		
		return responses;
	}

}
