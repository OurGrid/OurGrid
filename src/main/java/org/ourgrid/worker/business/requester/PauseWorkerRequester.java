package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;
import org.ourgrid.worker.business.controller.WorkerController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.ControlMessages;
import org.ourgrid.worker.request.PauseWorkerRequestTO;
import org.ourgrid.worker.response.StatusChangedResponseTO;

/**
 * This class provider a list of {@link IResponseTO} that will be executed.
 * Responsible for pause the worker component.
 *
 */
public class PauseWorkerRequester implements RequesterIF<PauseWorkerRequestTO> {

	/**
	 * {@inheritDoc}
	 */
	public List<IResponseTO> execute(PauseWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		//sender validation
		if (!request.canComponentBeUsed()) {
			OperationSucceedResponseTO to = new OperationSucceedResponseTO();
			to.setClientAddress(request.getClientAddress());
			to.setErrorCause(request.getErrorCause());
			responses.add(to);
			return responses;
		}
		
		if (!request.isThisMyPublicKey()) {
			LoggerResponseTO loggerResponseTO = new LoggerResponseTO();
			loggerResponseTO.setMessage(ControlMessages.
					getUnknownEntityTryingToPauseWorkerMessage(request.getSenderPublicKey()));
			loggerResponseTO.setType(LoggerResponseTO.WARN);

			responses.add(loggerResponseTO);
			return responses;
		}
		
		//actions
		WorkerStatus actualStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getStatus();
		
		if(!actualStatus.equals(WorkerStatus.OWNER) && !actualStatus.equals(WorkerStatus.ERROR)) {
			responses.add(new LoggerResponseTO(ControlMessages.getWorkerPausedMessage(), LoggerResponseTO.INFO));
			responses.add(new LoggerResponseTO(ControlMessages.getWorkerStatusChangedMessage(
					actualStatus, WorkerStatus.OWNER), LoggerResponseTO.DEBUG));
			
			WorkerController.getInstance().interruptWorkingAndCancelPreparingAllocation(responses, true);
			WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
			workerStatus.setStatus(WorkerStatus.OWNER);
			workerStatus.setPreparingAllocationState(false);
			
			String masterPeerAddress = workerStatus.getMasterPeerAddress();
			
			if (workerStatus.isLogged() && masterPeerAddress != null) {
				StatusChangedResponseTO statusChangedResponseTO = new StatusChangedResponseTO();
				statusChangedResponseTO.setClientAddress(masterPeerAddress);
				statusChangedResponseTO.setStatus(WorkerDAOFactory.getInstance().
						getWorkerStatusDAO().getStatus());
				
				responses.add(statusChangedResponseTO);
			}
		}                 
		
		OperationSucceedResponseTO operationSuccededResponseTO = new OperationSucceedResponseTO();
		operationSuccededResponseTO.setClientAddress(request.getClientAddress());
		operationSuccededResponseTO.setRemoteClient(request.isRemoteClient());
		
		responses.add(operationSuccededResponseTO);
		
		return responses;
	}
	
}
