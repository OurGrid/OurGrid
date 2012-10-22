package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ourgrid.common.WorkerLoginResult;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ScheduleActionWithFixedDelayResponseTO;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.controller.ExecutionController;
import org.ourgrid.worker.business.controller.WorkerController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerLoginMessages;
import org.ourgrid.worker.business.messages.WorkerManagementControllerMessages;
import org.ourgrid.worker.request.WorkerLoginSucceededRequestTO;
import org.ourgrid.worker.response.StatusChangedResponseTO;

public class WorkerLoginSucceededRequester implements RequesterIF<WorkerLoginSucceededRequestTO> {

	@Override
	public List<IResponseTO> execute(WorkerLoginSucceededRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		if (workerStatusDAO.isLogged()) {
			responses.add(new LoggerResponseTO(WorkerLoginMessages.getWorkerAlreadyLoggedMessage(), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		WorkerLoginResult result = request.getResult();		
		WorkerStatus workerStatus = workerStatusDAO.getStatus(); 

		if (!result.getResultMessage().equals(WorkerLoginResult.OK)) {
			responses.add(new LoggerResponseTO(WorkerLoginMessages.
					getWorkerCanNotLoggedMessage(result.toString()), LoggerResponseTO.WARN));
			workerStatusDAO.setLoginError(result.getResultMessage());
			
			return responses;
		}

		responses.add(new LoggerResponseTO(WorkerLoginMessages.
				getWorkerLoginSucceededMessage(),LoggerResponseTO.INFO));
		workerStatusDAO.setMasterPeerPublicKey(request.getSenderPublicKey());
		workerStatusDAO.setLoginError(null);
		
		if (workerStatusDAO.isErrorState()) {
			StatusChangedResponseTO statusChangedResponseTO = new StatusChangedResponseTO();
			statusChangedResponseTO.setClientAddress(workerStatusDAO.getMasterPeerAddress());
			statusChangedResponseTO.setStatus(workerStatus);
			responses.add(statusChangedResponseTO);
			
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getMasterPeerTryingToCommandWorkerOnErrorStateMessage(),
					LoggerResponseTO.WARN));
			return responses;
		}
		
		boolean isWorkingState = workerStatusDAO.isWorkingState();
		
		WorkerController.getInstance().interruptWorking(responses, true);

		WorkerStatus newStatus = request.isIdlenessDetectorOn() ? WorkerStatus.OWNER : WorkerStatus.IDLE;
		workerStatusDAO.setStatus(newStatus);

		// preemption
		if (isWorkingState) {
			ExecutionController.getInstance().beginAllocation(responses);
		} 
		
		if (!workerStatusDAO.isPreparingAllocationState()) {
			StatusChangedResponseTO statusChangedResponseTO = new StatusChangedResponseTO();
			statusChangedResponseTO.setClientAddress(workerStatusDAO.getMasterPeerAddress());
			statusChangedResponseTO.setStatus(newStatus);
			responses.add(statusChangedResponseTO);
		}
		
		createReportWorkAccountingAction(responses);
		
		if	(request.isWorkerSpecReportPropOn()) { 
			ScheduleActionWithFixedDelayResponseTO scheduleAction2 = new ScheduleActionWithFixedDelayResponseTO();
			scheduleAction2.setActionName(WorkerConstants.REPORT_WORKER_SPEC_ACTION_NAME);
			scheduleAction2.setDelay(request.getWorkerSpecReportTime());
			scheduleAction2.setTimeUnit(TimeUnit.MILLISECONDS);
			
			responses.add(scheduleAction2);
		}
		
		return responses;
	}

	private void createReportWorkAccountingAction(List<IResponseTO> responses) {
		ScheduleActionWithFixedDelayResponseTO scheduleAction1 = new ScheduleActionWithFixedDelayResponseTO();
		scheduleAction1.setActionName(WorkerConstants.REPORT_WORK_ACCOUNTING_ACTION_NAME);
		scheduleAction1.setDelay(WorkerConstants.REPORT_WORK_ACCOUNTING_TIME);
		scheduleAction1.setInitialDelay(WorkerConstants.REPORT_WORK_ACCOUNTING_TIME);
		scheduleAction1.setTimeUnit(TimeUnit.SECONDS);
		scheduleAction1.setStoreFuture(true);
		
		responses.add(scheduleAction1);
	}
}
