package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.DeployServiceResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.controller.ExecutionController;
import org.ourgrid.worker.business.controller.WorkerController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.ControlMessages;
import org.ourgrid.worker.business.messages.WorkerManagementControllerMessages;
import org.ourgrid.worker.communication.receiver.WorkerReceiver;
import org.ourgrid.worker.request.WorkForBrokerRequestTO;
import org.ourgrid.worker.response.MasterPeerStatusChangedAllocatedForBrokerResponseTO;
import org.ourgrid.worker.response.StatusChangedResponseTO;

public class WorkForBrokerRequester implements RequesterIF<WorkForBrokerRequestTO> {

	public List<IResponseTO> execute(WorkForBrokerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		 
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		if (!workerStatusDAO.isLogged()) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getWorkerIsNotLoggedInPeerMessage(),
					LoggerResponseTO.WARN));
			return responses;
		}

		if (!request.getSenderPublicKey().equals(workerStatusDAO.getMasterPeerPublicKey())) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getUnknownPeerTryingToCommandWorkerToWorkForBrokerMessage(request.getSenderPublicKey()),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		String consumerPubKey = workerStatusDAO.getConsumerPublicKey();
		String masterPeerAddress = workerStatusDAO.getMasterPeerAddress();

		if (workerStatusDAO.isErrorState()) {
			StatusChangedResponseTO statusChangedResponseTO = new StatusChangedResponseTO();
			statusChangedResponseTO.setClientAddress(masterPeerAddress);
			statusChangedResponseTO.setStatus(workerStatusDAO.getStatus());
			responses.add(statusChangedResponseTO);
			
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.
					getMasterPeerSendsWorkForBrokerToWorkerOnErrorStateMessage(),
					LoggerResponseTO.WARN));
			return responses;
		}
		
		
		if (masterPeerAddress == null) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getMasterPeerTryingToCommandWorkerBeforeSettingAsManagerMessage(),
					LoggerResponseTO.DEBUG));			
			
			return responses;
		}
		
		
		WorkerStatus currentStatus = workerStatusDAO.getStatus();
		if (currentStatus.equals(WorkerStatus.OWNER)) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getMasterPeerCommandedOwnerWorkerToWorkForBrokerMessage(),
					LoggerResponseTO.DEBUG));	
			
			return responses;
		}
		
		responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getMasterPeerCommandedWorkerToWorkForBrokerMessage(request.getBrokerPublicKey()),
				LoggerResponseTO.INFO));
		
		boolean isWorkingState = workerStatusDAO.isWorkingState();
		
		if (workerStatusDAO.isAllocated()) {
			WorkerController.getInstance().interruptWorking(responses, workerStatusDAO.isAllocatedForRemotePeer());
		}
		
		doAllocation(responses, request.getBrokerPublicKey());

		if (workerStatusDAO.isPreparingAllocationState()) {
			return responses;
		}
		
		if (consumerPubKey != null && !consumerPubKey.equals(request.getBrokerPublicKey())
				&& isWorkingState) {
			ExecutionController.getInstance().beginAllocation(responses);
		} else {
			statusChangeAllocatedForBroker(responses, request.getBrokerPublicKey());
		}

		return responses;
	}
	
	private void doAllocation(List<IResponseTO> responses, String brokerPublicKey) {
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		workerStatusDAO.setConsumerPublicKey(brokerPublicKey);
    }
	
	private void statusChangeAllocatedForBroker(List<IResponseTO> responses, String brokerPublicKey) {

		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		WorkerStatus currentStatus = workerStatusDAO.getStatus();
		
		if (!currentStatus.equals(WorkerStatus.ALLOCATED_FOR_BROKER)) {
			responses.add(new LoggerResponseTO(ControlMessages.getWorkerStatusChangedMessage(
					currentStatus, WorkerStatus.ALLOCATED_FOR_BROKER), LoggerResponseTO.DEBUG));
			workerStatusDAO.setStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		}
		
		DeployServiceResponseTO deployTO = new DeployServiceResponseTO();
		deployTO.setServiceClass(WorkerReceiver.class);
		deployTO.setServiceName(WorkerConstants.WORKER);
		responses.add(deployTO);
		
		MasterPeerStatusChangedAllocatedForBrokerResponseTO to = new MasterPeerStatusChangedAllocatedForBrokerResponseTO();
		to.setMasterPeerAddress(workerStatusDAO.getMasterPeerAddress());
		to.setBrokerPublicKey(brokerPublicKey);
		responses.add(to);
	}
	
}
