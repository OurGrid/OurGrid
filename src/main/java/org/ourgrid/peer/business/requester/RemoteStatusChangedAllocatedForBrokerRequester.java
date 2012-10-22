package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.request.RemoteStatusChangedAllocatedForBrokerRequestTO;
import org.ourgrid.peer.response.LocalHereIsWorkerResponseTO;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.peer.to.RemoteAllocableWorker;

public class RemoteStatusChangedAllocatedForBrokerRequester implements RequesterIF<RemoteStatusChangedAllocatedForBrokerRequestTO> {
	
	public List<IResponseTO> execute(RemoteStatusChangedAllocatedForBrokerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
	
		String wmPublicKey = request.getWmPublicKey();
		
		AllocationDAO allocationDAO = PeerDAOFactory.getInstance().getAllocationDAO();
		RemoteAllocableWorker remoteAllocableWorker = allocationDAO.getRemoteAllocableWorker(wmPublicKey);
		
		if (remoteAllocableWorker == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownRemoteWorkerStatusChangedMessage(wmPublicKey),LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (request.getWorkerAddress() == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getNullRemoteWorkerStatusChangedMessage(StringUtil.addressToContainerID(remoteAllocableWorker.getWorkerAddress())),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		String lwpcPubKey = remoteAllocableWorker.getConsumer().getPublicKey();
		
		PeerUserReference loggedUser = PeerDAOFactory.getInstance().getUsersDAO().getLoggedUser(lwpcPubKey);
		
		if (loggedUser == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownConsumerAllocatedForBroker(lwpcPubKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		String lwpcAddress = loggedUser.getWorkerProviderClientAddress();
		
		if (lwpcAddress != null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getGivingRemoteWorkerMessage(StringUtil.addressToContainerID(lwpcAddress),StringUtil.addressToContainerID(remoteAllocableWorker.getWorkerAddress())),
					LoggerResponseTO.DEBUG));
			
			LocalHereIsWorkerResponseTO to = new LocalHereIsWorkerResponseTO();
			to.setRequestSpec(remoteAllocableWorker.getRequest().getSpecification());
			to.setWorkerAddress(request.getWorkerAddress());
			to.setWorkerSpec(remoteAllocableWorker.getWorkerSpecification());
			to.setLwpcAddress(lwpcAddress);
			to.setWorkerPublicKey(remoteAllocableWorker.getWorkerPubKey());
			
			responses.add(to);
			
			remoteAllocableWorker.setAsDelivered();
		}
		
		return responses;
	}
}
