package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.worker.business.controller.ExecutionController;
import org.ourgrid.worker.business.controller.WorkerController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerManagementClientFailureControllerMessages;
import org.ourgrid.worker.request.RemoteWorkerManagementClientDoNotifyFailureRequestTO;
import org.ourgrid.worker.response.CancelReportAccountingActionResponseTO;

public class RemoteWorkerManagementClientDoNotifyFailureRequester 
					implements RequesterIF<RemoteWorkerManagementClientDoNotifyFailureRequestTO> {

	@Override
	public List<IResponseTO> execute(RemoteWorkerManagementClientDoNotifyFailureRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();

		if (workerStatusDAO.getMasterPeerAddress() == null) {
			responses.add(new LoggerResponseTO(
					WorkerManagementClientFailureControllerMessages.
					getUndefinedMasterPeerFailsMessage(request.getMonitorableID()), 
					LoggerResponseTO.WARN));

			return responses;
		}

		boolean isWorkingState = workerStatusDAO.isWorkingState();
		
		responses.add(new LoggerResponseTO("The remote peer [" +
				request.getMonitorableID() + "] has failed. Worker will interrupt the working," +
				" it means cancel any transfer or execution.", LoggerResponseTO.WARN));   

		if (workerStatusDAO.isAllocatedForRemotePeer()) {
			WorkerController.getInstance().interruptWorking(responses, true);
			workerStatusDAO.setStatus(WorkerStatus.IDLE);
		}

		if (isWorkingState) {
			ExecutionController.getInstance().beginAllocation(responses);
		}
		 
		CancelReportAccountingActionResponseTO cancelReportAccountingTO = 
										new CancelReportAccountingActionResponseTO();
		responses.add(cancelReportAccountingTO);
		
		WorkerDAOFactory.getInstance().getWorkAccountingDAO().resetAccountings();
		
		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(request.getMonitorableAddress());
		
		responses.add(releaseTO);
		return responses;

	}

}
