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
import org.ourgrid.worker.business.messages.RemoteWorkerManagementControllerMessages;
import org.ourgrid.worker.communication.receiver.WorkerReceiver;
import org.ourgrid.worker.request.RemoteWorkForBrokerRequestTO;
import org.ourgrid.worker.response.RemotePeerStatusChangedAllocatedForBrokerResponseTO;

public class RemoteWorkForBrokerRequester implements RequesterIF<RemoteWorkForBrokerRequestTO> {

	public List<IResponseTO> execute(RemoteWorkForBrokerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		String senderPublicKey = request.getSenderPublicKey();
		String brokerPubKey = request.getConsumerPublicKey();

		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		String consumerPubKey = workerStatusDAO.getConsumerPublicKey();

		if (!senderPublicKey.equals(request.getRemotePeerPublicKey()) || 
				!senderPublicKey.equals(workerStatusDAO.getRemotePeerPublicKey())) {
			
			responses.add(new LoggerResponseTO(RemoteWorkerManagementControllerMessages.getUnknownPeerSendsWorkForBrokerMessage(senderPublicKey), 
					LoggerResponseTO.WARN));
			
			return responses;
		}

		boolean isWorkingState = workerStatusDAO.isWorkingState();

		if (workerStatusDAO.isAllocated()) {
			WorkerController.getInstance().interruptWorking(responses, false);
		}

		doAllocation(responses, request);

		if (!workerStatusDAO.isPreparingAllocationState()) {
			if (consumerPubKey != null && !consumerPubKey.equals(brokerPubKey)
					&& isWorkingState) {

				ExecutionController.getInstance().beginAllocation(responses);

			} else {
				statusChangeAllocatedForBroker(responses, request);
			}
		}

		return responses;
	}

	private void doAllocation(List<IResponseTO> responses, RemoteWorkForBrokerRequestTO request) {
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();

		if (workerStatusDAO.getStatus().equals(WorkerStatus.ALLOCATED_FOR_PEER)) {
			responses.add(new LoggerResponseTO(ControlMessages.getWorkerStatusChangedMessage(
					WorkerStatus.ALLOCATED_FOR_PEER, WorkerStatus.ALLOCATED_FOR_BROKER), LoggerResponseTO.DEBUG));
		}

		workerStatusDAO.setStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		workerStatusDAO.setRemotePeerConsumer(request.getRemotePeerDID());
		workerStatusDAO.setConsumerPublicKey(request.getConsumerPublicKey());
		workerStatusDAO.setRemotePeerDN(request.getRemotePeerDN());

		responses.add(new LoggerResponseTO(RemoteWorkerManagementControllerMessages.getRemotePeerCommandedWorkerToWorkForBrokerMessage(
				request.getConsumerPublicKey()), LoggerResponseTO.INFO));
	}

	private void statusChangeAllocatedForBroker(List<IResponseTO> responses, RemoteWorkForBrokerRequestTO request) {
		DeployServiceResponseTO deployTO = new DeployServiceResponseTO();
		deployTO.setServiceClass(WorkerReceiver.class);
		deployTO.setServiceName(WorkerConstants.WORKER);
		responses.add(deployTO);

		RemotePeerStatusChangedAllocatedForBrokerResponseTO remotePeerTO = new RemotePeerStatusChangedAllocatedForBrokerResponseTO();
		remotePeerTO.setRemotePeerAddress(request.getRemotePeerDID());
		responses.add(remotePeerTO);
	}

}
