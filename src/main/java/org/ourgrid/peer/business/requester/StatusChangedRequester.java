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
import org.ourgrid.peer.business.controller.allocation.RedistributionController;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.request.StatusChangedRequestTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.Request;

public class StatusChangedRequester implements RequesterIF<StatusChangedRequestTO> {
	
	public List<IResponseTO> execute(StatusChangedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String workerPublicKey = request.getWorkerPublicKey();
		
		String workerUserAtServer = request.getWorkerUserAtServer();
		
		WorkerStatus status = request.getStatus();
		
		LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(responses, workerUserAtServer);

		if(localWorker == null){
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownWorkerChangingStatusMessage(workerPublicKey, status), LoggerResponseTO.WARN));
			return responses;
		} 
		
		if(!checkKey(localWorker.getPublicKey(), workerPublicKey)){
			responses.add(new LoggerResponseTO(WorkerMessages.getWrongPublicKeyForWorkerMessage(StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress())), LoggerResponseTO.ERROR));
			return responses;
		} 

		switch ( status ) {
		case IDLE:
			
			changeStatusIdle(responses, localWorker, workerUserAtServer, request.getLocalWorkerProviderAddress(), request.getMyCertPathDN());
			break;
		
		case OWNER:
			changeStatusToOwner(responses, localWorker, workerUserAtServer);
			break;
			
		case ERROR:
			changeStatusToError(responses, localWorker, workerUserAtServer);
			break;
			
		default:
			break;
		}
		
		return responses;
	}
	

	private void changeStatusIdle(List<IResponseTO> responses, LocalWorker localWorker, String workerUserAtServer, String localWorkerProviderAddress, String myCertPathDN) {
		
		switch (localWorker.getStatus()) {
		case IDLE:
		case OWNER:
		case DONATED:
		case IN_USE:
			
			RedistributionController.getInstance().createAllocableWorker(localWorker, localWorkerProviderAddress, myCertPathDN);
				
			WorkerControl.getInstance().statusChanged(responses, workerUserAtServer, LocalWorkerState.IDLE);
				
			responses.add(new LoggerResponseTO(WorkerMessages.getStatusChangedMessage(StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress()), LocalWorkerState.IDLE), 
					LoggerResponseTO.DEBUG));
			
			RedistributionController.getInstance().redistributeIdleWorker(responses, localWorker);
			break;

		default:
			responses.add(new LoggerResponseTO(WorkerMessages.getIgnoredStatusChangeMessage(StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress()), localWorker.getStatus(),
					WorkerStatus.IDLE), LoggerResponseTO.ERROR));
		}
	}

	private void changeStatusToError(List<IResponseTO> responses, LocalWorker localWorker, String workerUserAtServer) {
		
		if(!localWorker.getStatus().isError()){
			changeStatus(responses, localWorker, workerUserAtServer, LocalWorkerState.ERROR);
		}
		
	}
	private void changeStatusToOwner(List<IResponseTO> responses, LocalWorker localWorker, String workerUserAtServer) {
		if(!localWorker.getStatus().isOwner()){
			changeStatus(responses, localWorker, workerUserAtServer, LocalWorkerState.OWNER);
		}
	}
	
	private void changeStatus(List<IResponseTO> responses, LocalWorker localWorker, String workerUserAtServer, LocalWorkerState state) {
		
		responses.add(new LoggerResponseTO(WorkerMessages.getStatusChangedMessage(
				StringUtil.addressToContainerID(localWorker.getWorkerManagementAddress()), state), 
				LoggerResponseTO.INFO));
		
		AllocationDAO allocationDAO = PeerDAOFactory.getInstance().getAllocationDAO();
		//String publicKey = workerID.getPublicKey();
		String publicKey = localWorker.getPublicKey();
		AllocableWorker localAllocableWorker = allocationDAO.getAllocableWorker(publicKey);
		
		if (localAllocableWorker != null) {
			
			Request request = localAllocableWorker.getRequest();
			if (request != null) {
				request.removeAllocableWorker(localAllocableWorker);
			}
			
			allocationDAO.removeLocalAllocableWorker(publicKey);
		}
		
		localWorker.setStatus(state);
		
		WorkerControl.getInstance().statusChanged(responses, workerUserAtServer, state);
	}
}
