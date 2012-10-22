package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.statistics.control.JobControl;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.controller.CommunityObtainerController;
import org.ourgrid.peer.business.controller.allocation.DefaultAllocator;
import org.ourgrid.peer.business.controller.messages.RequestMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.util.RequestUtils;
import org.ourgrid.peer.request.RequestWorkersRequestTO;
import org.ourgrid.peer.response.LocalPreemptedWorkerResponseTO;
import org.ourgrid.peer.response.RemotePreemptedWorkerResponseTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalConsumer;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.peer.to.Request;

import sun.security.provider.certpath.X509CertPath;


public class RequestWorkersRequester implements RequesterIF<RequestWorkersRequestTO> {

	public List<IResponseTO> execute(RequestWorkersRequestTO requestTO) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String userPubKey = requestTO.getBrokerPublicKey();
		RequestSpecification requestSpec = requestTO.getRequestSpec();
		
		if(UserControl.getInstance().userExists(responses, userPubKey)) {
			
			if (!validateRequest(responses, requestSpec, userPubKey)) {
				return responses;
			}
			
			PeerUserReference loggedUser = PeerDAOFactory.getInstance().getUsersDAO().getLoggedUser(
					userPubKey);
			
			if (loggedUser == null) {
				
				LoggerResponseTO loggerResponse = new LoggerResponseTO(
						RequestMessages.getUserDownMessage(requestSpec.getRequestId(), 
								userPubKey), 
						LoggerResponseTO.WARN);
				responses.add(loggerResponse);
				
				return responses;
			}
			
			String localWorkerProviderClientAddress = loggedUser.getWorkerProviderClientAddress();
			
			if (localWorkerProviderClientAddress == null) {
				LoggerResponseTO loggerResponse = new LoggerResponseTO(
						RequestMessages.getUserDownMessage(requestSpec.getRequestId(), 
								userPubKey), 
						LoggerResponseTO.WARN);
				responses.add(loggerResponse);
				
				return responses;
			}
			
			//Do not allow Broker to repeat the request
			if(requestIsRunning(requestSpec)) {
				
				LoggerResponseTO loggerResponse = new LoggerResponseTO(
						RequestMessages.getRequestIDAlreadyExistsMessage(requestSpec.getRequestId(), 
								localWorkerProviderClientAddress), 
						LoggerResponseTO.WARN);
				responses.add(loggerResponse);
				
				return responses;
			} 
			
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getNewRequestMessage(requestSpec, localWorkerProviderClientAddress), 
					LoggerResponseTO.INFO);
			responses.add(loggerResponse);
			
	        PeerUser user = UserControl.getInstance().getUserByPublicKey(responses, userPubKey);
			LocalConsumer localConsumer = PeerDAOFactory.getInstance().getConsumerDAO().createLocalConsumer(user);

			Request request = PeerDAOFactory.getInstance().getRequestDAO().createRequest(
					localWorkerProviderClientAddress, userPubKey, 
					requestSpec, localConsumer);

			doRequest(responses, requestTO.getMyCertPath(), requestSpec, requestTO.getMyPublicKey(), userPubKey, request, false);
		
		//Request repetition
		} else if (userPubKey.equals(requestTO.getMyPublicKey())) {
			
			Request request = PeerDAOFactory.getInstance().getRequestDAO().getRequest(requestSpec.getRequestId());
			
			if(request != null) {
				doRequest(responses, requestTO.getMyCertPath(), requestSpec, 
						requestTO.getMyPublicKey(), request.getConsumer().getPublicKey(), request, true);
			}
			
		} else {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getUnknownUserMessage(requestSpec, userPubKey), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
		}
		
		return responses;
	}

	public void doRequest(List<IResponseTO> responses, X509CertPath myCertPath, 
			RequestSpecification requestSpec, String myPublicKey, String userPublicKey, 
			Request request, boolean isRepetition) {
		
		DefaultAllocator allocator = DefaultAllocator.getInstance();

		List<AllocableWorker> allAllocableWorkers = PeerDAOFactory.getInstance().getAllocationDAO().getAllAllocableWorkers();
		List<AllocableWorker> myAllocableWorkersSorted = allocator.getAllocableWorkersForLocalRequest(
				responses, myCertPath, request, allAllocableWorkers);

        LocalConsumer localConsumer = PeerDAOFactory.getInstance().getConsumerDAO().getLocalConsumer(userPublicKey);

		for (AllocableWorker allocableWorker : myAllocableWorkersSorted) {
            dispatchAllocation(responses, request, allocableWorker, localConsumer);
		}
		
		if( request.getNeededWorkers() > 0 ) {
			forwardToCommunity(responses, requestSpec);
			
			if (!myPublicKey.equals(userPublicKey)) {
				RequestUtils.scheduleRequest(responses, requestSpec);
			}
		}
		
		if (!isRepetition) {
			JobControl.getInstance().addRequest(responses, request);
		}
	}
	
	private void forwardToCommunity(List<IResponseTO> responses, RequestSpecification requestSpec) {
		CommunityObtainerController.getInstance().request(responses, requestSpec);
	}
	
	private void dispatchAllocation(List<IResponseTO> responses, Request request, 
			AllocableWorker allocableWorker, LocalConsumer localConsumer) {

		if (allocableWorker.getConsumer() != null) {//preemption

			String workerManagementAddress = allocableWorker.getWorkerAddress();

			LoggerResponseTO loggerResponseTo = new LoggerResponseTO(
					"Request " + request.getSpecification().getRequestId() + ": Taking worker [" + 
					workerManagementAddress + "] from [" + allocableWorker.getConsumer().getConsumerAddress()+ "]",
					LoggerResponseTO.INFO);
			
			responses.add(loggerResponseTo);
			
			if (allocableWorker.getConsumer().isLocal()) {
				LocalPreemptedWorkerResponseTO to = new LocalPreemptedWorkerResponseTO();
				to.setLwpcAddress(allocableWorker.getConsumer().getConsumerAddress());
				to.setWorkerAddress(workerManagementAddress);
				
				Request loserRequest = allocableWorker.getRequest();
				loserRequest.removeAllocableWorker(allocableWorker);

				RequestSpecification spec = loserRequest.getSpecification();
				if (loserRequest.needMoreWorkers() ) {
					RequestUtils.scheduleRequest(responses, spec);
				}
			} else {
				RemotePreemptedWorkerResponseTO to = new RemotePreemptedWorkerResponseTO();
				
				LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(responses, 
						StringUtil.addressToUserAtServer(workerManagementAddress));
				
				to.setRwmPublicKey(localWorker.getPublicKey());
			}

//			RegisterInterestResponseTO registerInterestTo = new RegisterInterestResponseTO();
//			registerInterestTo.setMonitorName(allocableWorker.getMonitorName());
//			registerInterestTo.setMonitorableType(allocableWorker.getMonitorableType());
//			registerInterestTo.setMonitorableAddress(workerManagementAddress);
//			
//			responses.add(registerInterestTo);
		}

		allocableWorker.clear();

		allocableWorker.setStatus(LocalWorkerState.IN_USE);

		if (allocableWorker.isWorkerLocal()) {
			WorkerControl.getInstance().statusChanged(
					responses,
					StringUtil.addressToUserAtServer(allocableWorker.getWorkerAddress()), 
					LocalWorkerState.IN_USE, 
					StringUtil.addressToUserAtServer(localConsumer.getConsumerAddress()));
		}	

		allocableWorker.setConsumer(localConsumer);
		allocableWorker.setRequest(request);
		request.addAllocableWorker(allocableWorker);

		allocableWorker.workForBroker(responses);
	}
	
	private boolean requestIsRunning(RequestSpecification requestSpec) {
		return PeerDAOFactory.getInstance().getRequestDAO().isRunning(requestSpec);
	}
	
	private boolean validateRequest(List<IResponseTO> responses, RequestSpecification requestSpec, String userPubKey) {
		PeerUserReference loggedUser = PeerDAOFactory.getInstance().getUsersDAO().getLoggedUser(userPubKey);
		
		if (loggedUser == null) {
			return false;
		}
		
		String userID = loggedUser.getWorkerProviderClientAddress();

		if(requestSpec == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getNullRequestMessage(userID),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			return false;
		}
		
		if(requestSpec.getRequiredWorkers() < 1) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					RequestMessages.getRequestWithLessThanOneWorkerMessage(requestSpec, userID),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			return false;
		}
		
		return true;
	}

}
