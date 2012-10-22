package org.ourgrid.peer.business.requester;

import static org.ourgrid.common.util.CommonUtils.checkKey;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.UsersDAO;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.request.StatusChangedAllocatedForBrokerRequestTO;
import org.ourgrid.peer.response.LocalHereIsWorkerResponseTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.PeerUserReference;

public class StatusChangedAllocatedForBrokerRequester implements RequesterIF<StatusChangedAllocatedForBrokerRequestTO> {
	
	public List<IResponseTO> execute(StatusChangedAllocatedForBrokerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPublicKey = request.getSenderPublicKey();
		String senderUserAtServer = request.getSenderUserAtServer();
		
		LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(responses, senderUserAtServer);
		
		if(localWorker == null){
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownWorkerChangingStatusMessage(senderPublicKey, 
					WorkerStatus.ALLOCATED_FOR_BROKER), LoggerResponseTO.ERROR));

			return responses;
		} 

		if(!checkKey(localWorker.getPublicKey(), senderPublicKey)){
			responses.add(new LoggerResponseTO(WorkerMessages.getWrongPublicKeyForWorkerMessage(
					StringUtil.addressToContainerID(request.getWorkerAddress())), LoggerResponseTO.ERROR));
			
			return responses;
		}
		
		switch (localWorker.getStatus()) {
		
		case IN_USE:
			
			changeStatusToAllocatedForBroker(responses, localWorker, request.getWorkerAddress(), request.getBrokerPublicKey());
			break;
			
		default:
			responses.add(new LoggerResponseTO(WorkerMessages.getInvalidStatusChangeMessage(StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress()), 
					localWorker.getStatus()), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		return responses;
	}
	
	private void changeStatusToAllocatedForBroker(List<IResponseTO> responses, LocalWorker localWorker, String workerAddress,
			String brokerPublicKey) {
		
		if (workerAddress == null){
			responses.add(new LoggerResponseTO(WorkerMessages.getNullWorkerStatusChangedMessage(StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress()), 
					WorkerStatus.ALLOCATED_FOR_BROKER), LoggerResponseTO.WARN));
			
			return;
		} 
		
		AllocationDAO dao = PeerDAOFactory.getInstance().getAllocationDAO();
		AllocableWorker allocable = dao.getAllocableWorker(localWorker.getPublicKey());
		
		if (allocable.getConsumer() == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getNullConsumerChangeStatusToAllocatedForBroker(), LoggerResponseTO.WARN));
			
			return;
		}
		
		String consumerPublicKey = allocable.getConsumer().getPublicKey();
		if (brokerPublicKey == null || !consumerPublicKey.equals(brokerPublicKey)) {
			responses.add(new LoggerResponseTO(WorkerMessages.getDifferentConsumerChangeStatusToAllocatedForBroker(), 
					LoggerResponseTO.WARN));
			return;
		}
		
		allocable.setAsDelivered();
		
		responses.add(new LoggerResponseTO(WorkerMessages.getStatusChangedMessage(StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress()),
				LocalWorkerState.IN_USE), LoggerResponseTO.INFO));
		
		responses.add(new LoggerResponseTO(WorkerMessages.getGivingWorkerMessage(StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress()),
				StringUtil.addressToContainerID(allocable.getConsumer().getConsumerAddress())), LoggerResponseTO.INFO));
		
		
		UsersDAO usersDAO = PeerDAOFactory.getInstance().getUsersDAO();
		
		PeerUserReference loggedUser = usersDAO.getLoggedUser(allocable.getConsumer().getPublicKey());
		
		if (loggedUser == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownConsumerAllocatedForBroker(allocable.getConsumer().getPublicKey()), LoggerResponseTO.WARN));
			
			return;
		}
		
		
		String lwpcAddress = loggedUser.getWorkerProviderClientAddress();
		

		if (lwpcAddress != null) {
			LocalHereIsWorkerResponseTO to = new LocalHereIsWorkerResponseTO();
			to.setLwpcAddress(lwpcAddress);
			to.setRequestSpec(allocable.getRequest().getSpecification());
			to.setWorkerAddress(workerAddress);
			to.setWorkerSpec(allocable.getWorkerSpecification());
			to.setWorkerPublicKey(localWorker.getPublicKey());
			
			responses.add(to);
			
			WorkerControl.getInstance().statusChanged(responses, 
					StringUtil.addressToUserAtServer(workerAddress), LocalWorkerState.IN_USE);
		}
	}
}
