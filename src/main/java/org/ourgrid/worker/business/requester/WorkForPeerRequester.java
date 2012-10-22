package org.ourgrid.worker.business.requester;

import java.security.cert.X509Certificate;
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
import org.ourgrid.worker.communication.receiver.RemoteWorkerManagementReceiver;
import org.ourgrid.worker.request.WorkForPeerRequestTO;
import org.ourgrid.worker.response.StatusChangedAllocatedForPeerResponseTO;
import org.ourgrid.worker.response.StatusChangedResponseTO;

public class WorkForPeerRequester implements RequesterIF<WorkForPeerRequestTO> {

	public List<IResponseTO> execute(WorkForPeerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (!prepareToWorkForPeer(responses, request)) {
			return responses;
		}
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		List<String> usersDN = request.getUsersDN();
		if (usersDN != null) {
			workerStatusDAO.setUsersDN(usersDN);
		}

		List<X509Certificate> caCertificates = request.getCaCertificates();
		if (caCertificates != null) {
			workerStatusDAO.setCaCertificates(caCertificates);
		}
		
		return responses;
	}
	
	private boolean prepareToWorkForPeer(List<IResponseTO> responses, WorkForPeerRequestTO request) {
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		if (!workerStatusDAO.isLogged()) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getWorkerIsNotLoggedInPeerMessage(),
					LoggerResponseTO.WARN));
			
			return false;
		}

		if (workerStatusDAO.getStatus().equals(WorkerStatus.OWNER)) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getWorkForPeerOnOwnerWorkerMessage(), LoggerResponseTO.DEBUG));
			
			return false;
		}

		String senderPubKey = request.getSenderPublicKey();

		if (!senderPubKey.equals(workerStatusDAO.getMasterPeerPublicKey())) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getUnknownPeerSendsWorkForPeerMessage(senderPubKey), LoggerResponseTO.WARN));
			
			return false;
		}
		
		String masterPeerAddress = workerStatusDAO.getMasterPeerAddress();
		
		if (workerStatusDAO.isErrorState()) {
			StatusChangedResponseTO statusChangedResponseTO = new StatusChangedResponseTO();
			statusChangedResponseTO.setClientAddress(masterPeerAddress);
			statusChangedResponseTO.setStatus(workerStatusDAO.getStatus());
			responses.add(statusChangedResponseTO);

			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getMasterPeerSendsWorkForPeerToWorkerOnErrorStateMessage(),
					LoggerResponseTO.WARN));
			return false;
		}
		
		responses.add(new LoggerResponseTO(
				WorkerManagementControllerMessages.getSuccessfulWorkForPeerMessage(request.getRemotePeerPublicKey()), 
				LoggerResponseTO.INFO));
		
		//In general, local brokers has biggest preference than remote consumers
		if (!workerStatusDAO.isAllocatedForRemotePeer() && workerStatusDAO.getStatus().equals(WorkerStatus.ALLOCATED_FOR_BROKER)) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getWorkForPeerOnAllocatedForBrokerWorkerMessage(), 
					LoggerResponseTO.WARN));
		}
		
		boolean isWorkingState = workerStatusDAO.isWorkingState();
		
		if (workerStatusDAO.isAllocated()) {
			WorkerController.getInstance().interruptWorking(responses, true);
		}
		
		workerStatusDAO.setRemotePeerPublicKey(request.getRemotePeerPublicKey());
		
		if (workerStatusDAO.isPreparingAllocationState()) {
			return true;
		}
		
		if (isWorkingState) {
			ExecutionController.getInstance().beginAllocation(responses);
		} else {
			statusChangeAllocatedForPeer(request, responses);
		}
		
		return true;
	}
	
	private void statusChangeAllocatedForPeer(WorkForPeerRequestTO request, List<IResponseTO> responses) {

		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		WorkerStatus actualStatus = workerStatusDAO.getStatus();
		
		if (!actualStatus.equals(WorkerStatus.ALLOCATED_FOR_PEER)) {
			responses.add(new LoggerResponseTO(ControlMessages.getWorkerStatusChangedMessage(
					actualStatus, WorkerStatus.ALLOCATED_FOR_PEER), LoggerResponseTO.DEBUG));
			
			workerStatusDAO.setStatus(WorkerStatus.ALLOCATED_FOR_PEER);
		}
		
		//RemoteWorkerManagement remWorkerManag = new RemoteWorkerManagementReceiver();
		
		DeployServiceResponseTO deployRwmTO = new DeployServiceResponseTO();
		deployRwmTO.setServiceName(WorkerConstants.REMOTE_WORKER_MANAGEMENT);
		deployRwmTO.setServiceClass(RemoteWorkerManagementReceiver.class);
		
		responses.add(deployRwmTO);
		
		StatusChangedAllocatedForPeerResponseTO statusChangeTO = new StatusChangedAllocatedForPeerResponseTO();
		statusChangeTO.setClientAddress(request.getClientAddress());
		statusChangeTO.setRemotePeerPubKey(request.getRemotePeerPublicKey());
		//statusChangeTO.setRemoteWorkerManagement(remWorkerManag);
		
		responses.add(statusChangeTO);
	}
	
}
