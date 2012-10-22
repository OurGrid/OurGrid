package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.controller.ExecutionController;
import org.ourgrid.worker.business.controller.WorkerController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerManagementClientFailureControllerMessages;
import org.ourgrid.worker.request.WorkerManagementClientDoNotifyFailureRequestTO;
import org.ourgrid.worker.response.CancelReportAccountingActionResponseTO;

public class WorkerManagementClientDoNotifyFailureRequester implements RequesterIF<WorkerManagementClientDoNotifyFailureRequestTO> {

	public List<IResponseTO> execute(WorkerManagementClientDoNotifyFailureRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		if (workerStatusDAO.getMasterPeerAddress() == null) {
			responses.add(new LoggerResponseTO(WorkerManagementClientFailureControllerMessages.getUndefinedMasterPeerFailsMessage(request.getMonitorableID()), 
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (!request.getMonitorableAddress().equals(workerStatusDAO.getMasterPeerAddress())) {
			responses.add(new LoggerResponseTO(WorkerManagementClientFailureControllerMessages.getUnknownPeerFailsMessage(request.getMonitorableID()), 
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		boolean isWorkingState = workerStatusDAO.isWorkingState();
		
		if (workerStatusDAO.isAllocated()) {
			WorkerController.getInstance().interruptWorking(responses, true);
			workerStatusDAO.setStatus(WorkerStatus.IDLE);
		}
		
		if (isWorkingState) {
			ExecutionController.getInstance().beginAllocation(responses);
		} 
		
		responses.add(new LoggerResponseTO(WorkerManagementClientFailureControllerMessages.getMasterPeerFailsMessage(request.getMonitorableID()), 
				LoggerResponseTO.WARN));
		
		workerStatusDAO.masterPeerFails();
		
		CancelReportAccountingActionResponseTO cancelReportAccountingTO = new CancelReportAccountingActionResponseTO();
		responses.add(cancelReportAccountingTO);
		
		WorkerDAOFactory.getInstance().getWorkAccountingDAO().resetAccountings();
		
		return responses;
	}
}
