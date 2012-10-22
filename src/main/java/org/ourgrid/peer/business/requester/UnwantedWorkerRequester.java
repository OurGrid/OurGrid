package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.peer.business.controller.allocation.RedistributionController;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.request.UnwantedWorkerRequestTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.peer.to.Request;

public class UnwantedWorkerRequester implements RequesterIF<UnwantedWorkerRequestTO> {
	
	public List<IResponseTO> execute(UnwantedWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String brokerPublicKey = request.getSenderPublicKey();
		String workerAddress = request.getWorkerAddress();
		
		PeerUser user = UserControl.getInstance().getUserByPublicKey(responses, brokerPublicKey);
		
		if (user == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownConsumerMarkedUnwantedWorkerMessage(brokerPublicKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		PeerUserReference loggedUser = PeerDAOFactory.getInstance().getUsersDAO().getLoggedUser(brokerPublicKey);
		
		if (loggedUser == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownConsumerMarkedUnwantedWorkerMessage(brokerPublicKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		String lwpcAddress = loggedUser.getWorkerProviderClientAddress();
		
		if (workerAddress == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getNullUnwantedWorkerMessage(lwpcAddress),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		AllocableWorker allocable = PeerDAOFactory.getInstance().getAllocationDAO().getAllocableWorker(request.getWorkerPublicKey());
		
		if (allocable == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownUnwantedWorkerMessage(lwpcAddress, workerAddress),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (!allocable.getConsumer().getConsumerAddress().equals(lwpcAddress)) {
			responses.add(new LoggerResponseTO(WorkerMessages.getNotAllocatedUnwantedWorkerMessage(lwpcAddress, workerAddress),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		RequestSpecification requestSpec = request.getRequestSpec();
		
		if (requestSpec == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getNullRequestUnwantedWorkerMessage(lwpcAddress, workerAddress, allocable.isWorkerLocal()),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (!allocable.getRequest().getSpecification().equals(requestSpec)) {
			responses.add(new LoggerResponseTO(WorkerMessages.getInvalidRequestUnwantedWorkerMessage(requestSpec.getRequestId(), lwpcAddress, 
					workerAddress, allocable.isWorkerLocal()), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		Request r = PeerDAOFactory.getInstance().getRequestDAO().getRequest(requestSpec.getRequestId());
		r.addUnwantedWorker(allocable.getWorkerSpecification());
		
		RedistributionController.getInstance().redistributeWorker(responses, allocable);
		
		
		return responses;
	}
}
