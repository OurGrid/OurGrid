package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerSpecDAO;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.request.ReportWorkerSpecActionRequestTO;
import org.ourgrid.worker.response.HereIsWorkerSpecMessageHandleResponseTO;
import org.ourgrid.worker.response.UpdateWorkerSpecListenerResponseTO;

public class ReportWorkerSpecActionRequester implements RequesterIF<ReportWorkerSpecActionRequestTO> {

	public List<IResponseTO> execute(ReportWorkerSpecActionRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		WorkerSpecDAO workerSpecDAO = WorkerDAOFactory.getInstance().getWorkerSpecDAO();
		WorkerSpecification workerSpec = workerSpecDAO.getWorkerSpec();
		
		WorkerStatusDAO workertatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		UpdateWorkerSpecListenerResponseTO updateWorkerSpecListenerTO = new UpdateWorkerSpecListenerResponseTO();
		updateWorkerSpecListenerTO.setWorkerSpec(workerSpec);
		updateWorkerSpecListenerTO.setMasterPeerAddress(workertatusDAO.getMasterPeerAddress());
		
		responses.add(updateWorkerSpecListenerTO);
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		if (workerStatusDAO.hasConsumer()) {
			responses.add(new HereIsWorkerSpecMessageHandleResponseTO(workerSpec, workerStatusDAO.getConsumerAddress()));
		}
		
		return responses;
	}
}
