package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.worker.business.dao.WorkAccountingDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.request.ReportWorkAccountingActionRequestTO;
import org.ourgrid.worker.response.ReportWorkAccountingActionResponseTO;

public class ReportWorkAccountingActionRequester implements RequesterIF<ReportWorkAccountingActionRequestTO> {

	public List<IResponseTO> execute(ReportWorkAccountingActionRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		WorkAccountingDAO accountingDAO = WorkerDAOFactory.getInstance().getWorkAccountingDAO();
		List<WorkAccounting> workAccountings = accountingDAO.getWorkAccountings();

		WorkAccounting actualAccounting = accountingDAO.getCurrentWorkAccounting();

		if (actualAccounting != null) {

			if (actualAccounting.isTimmingStarted()) {
				actualAccounting.stopCPUTiming();
			}

			workAccountings.add(new WorkAccounting
					(actualAccounting.getConsumerPeerDN(), actualAccounting.getAccountings()));
			
			actualAccounting.restartWorkAccounting();
		}

		if (workAccountings.isEmpty()) {
			return responses;
		}
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();

		ReportWorkAccountingActionResponseTO to = new ReportWorkAccountingActionResponseTO();
		to.setMasterPeerAddress(workerStatusDAO.getMasterPeerAddress());
		to.setWorkAccountings(workAccountings);
		responses.add(to);
		
		accountingDAO.resetAccountings();
		
		return responses;
	}
}


