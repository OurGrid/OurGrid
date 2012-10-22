package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.request.WorkerClientIsDownRequestTO;

public class WorkerClientIsDownRequester implements RequesterIF<WorkerClientIsDownRequestTO>{

	public List<IResponseTO> execute(WorkerClientIsDownRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		workerStatusDAO.setConsumerAddress(null);
		workerStatusDAO.setConsumerDeploymentID(null);
		workerStatusDAO.setStatus(WorkerStatus.IDLE);
		workerStatusDAO.setPreparingAllocationState(true);
		workerStatusDAO.setWorkingState(false);
		
		ReleaseResponseTO to = new ReleaseResponseTO();
		to.setStubAddress(request.getClientAddress());
		
		responses.add(to);
		
		return responses;
	}

}
