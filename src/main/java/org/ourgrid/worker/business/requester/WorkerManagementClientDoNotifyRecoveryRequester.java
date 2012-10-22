package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerManagementClientRecoveryControllerMessages;
import org.ourgrid.worker.request.WorkerManagementClientDoNotifyRecoveryRequestTO;
import org.ourgrid.worker.response.WorkerLoginResponseTO;

public class WorkerManagementClientDoNotifyRecoveryRequester implements RequesterIF<WorkerManagementClientDoNotifyRecoveryRequestTO> {

	@Override
	public List<IResponseTO> execute(WorkerManagementClientDoNotifyRecoveryRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		String wmcAddress = workerStatusDAO.getMasterPeerAddress();
		
		if (!request.getWorkerManagementClientAddress().equals(
				workerStatusDAO.getMasterPeerAddress())) {
			responses.add(new LoggerResponseTO(
							WorkerManagementClientRecoveryControllerMessages
									.getPeerAddressIsNotTheMasterPeerAddressMessage(wmcAddress),
							LoggerResponseTO.WARN));
			return responses;
		}
		
		WorkerLoginResponseTO to = new WorkerLoginResponseTO();
		to.setWorkerManagementClientAddress(request.getWorkerManagementClientAddress());
		to.setWorkerSpecification(request.getWorkerSpecification());
		responses.add(to);
		
		responses.add(new LoggerResponseTO(
				WorkerManagementClientRecoveryControllerMessages
						.getMasterPeerRecoveryMessage(wmcAddress),
				LoggerResponseTO.INFO));
		
		return responses;
	}

}
