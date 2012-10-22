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
import org.ourgrid.peer.request.StatusChangedAllocatedForPeerRequestTO;
import org.ourgrid.peer.response.RemoteHereIsWorkerResponseTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalWorker;

public class StatusChangedAllocatedForPeerRequester implements RequesterIF<StatusChangedAllocatedForPeerRequestTO> {
	
	public List<IResponseTO> execute(StatusChangedAllocatedForPeerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		
		String workerPublicKey = request.getWorkerPublicKey();
		
		String workerUserAtServer = request.getWorkerUserAtServer();
		
		LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(responses, workerUserAtServer);

		if(localWorker == null){
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownWorkerChangingStatusMessage(workerPublicKey, WorkerStatus.ALLOCATED_FOR_PEER), LoggerResponseTO.WARN));
			return responses;
		} 

		if(!checkKey(localWorker.getPublicKey(),workerPublicKey)){
			responses.add(new LoggerResponseTO(WorkerMessages.getWrongPublicKeyForWorkerMessage(StringUtil.addressToContainerID(
					localWorker.getWorkerManagementAddress())), LoggerResponseTO.ERROR));
			return responses;
		} 

		switch (localWorker.getStatus()) {
		
		case DONATED:

			changeStatusToAllocatedForPeer(responses, localWorker, request.getRemoteWorkerManagementAddress(),
					request.getPeerPublicKey());
			break;
			
		default:
			responses.add(new LoggerResponseTO(WorkerMessages.getIgnoredStatusChangeMessage(StringUtil.addressToContainerID(
					localWorker.getWorkerManagementAddress()), localWorker.getStatus(),
					WorkerStatus.ALLOCATED_FOR_PEER), LoggerResponseTO.WARN));
			return responses;
		}
		
		return responses;
	}
	
	private void changeStatusToAllocatedForPeer(List<IResponseTO> responses, LocalWorker localWorker, 
			String remoteWorkerManagementAddress, String peerPublicKey) {
		
		if (remoteWorkerManagementAddress == null ){
			responses.add(new LoggerResponseTO(WorkerMessages.getNullWorkerStatusChangedMessage(StringUtil.addressToContainerID(
					localWorker.getWorkerManagementAddress()), WorkerStatus.ALLOCATED_FOR_PEER), LoggerResponseTO.WARN));
			return;
		}
		
		AllocableWorker allocable = PeerDAOFactory.getInstance().getAllocationDAO().getAllocableWorker(localWorker.getPublicKey());
		
		if (allocable.getConsumer() == null ){
			responses.add(new LoggerResponseTO(WorkerMessages.getNullConsumerChangeStatusToAllocatedForPeer(), 
					LoggerResponseTO.WARN));
			return;
		}
		
		String consumerPublicKey = allocable.getConsumer().getPublicKey();
		if (!consumerPublicKey.equals(peerPublicKey)) {
			responses.add(new LoggerResponseTO(WorkerMessages.getDifferentConsumerChangeStatusToAllocatedForPeer(), 
					LoggerResponseTO.WARN));
			return;
		}
		
		allocable.setAsDelivered();
		
		localWorker.setStatus(LocalWorkerState.DONATED);
		
		responses.add(new LoggerResponseTO(WorkerMessages.getStatusChangedMessage(StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress()), 
				LocalWorkerState.DONATED), LoggerResponseTO.INFO));
		responses.add(new LoggerResponseTO(WorkerMessages.getDonatingWorkerMessage(StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress()), 
				StringUtil.addressToContainerID(allocable.getConsumer().getConsumerAddress())), LoggerResponseTO.INFO));

		RemoteHereIsWorkerResponseTO to = new RemoteHereIsWorkerResponseTO();
		to.setRwmAddress(remoteWorkerManagementAddress);
		to.setRwpcAddress(allocable.getConsumer().getConsumerAddress());
		to.setWorkerSpec(allocable.getWorkerSpecification());
		
		responses.add(to);
		
		WorkerControl.getInstance().statusChanged(responses, StringUtil.addressToUserAtServer(localWorker.getWorkerManagementAddress()), LocalWorkerState.DONATED,
				StringUtil.addressToUserAtServer(allocable.getConsumer().getConsumerAddress()));
	}
}
