package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.request.UpdateTransferProgressRequestTO;

public class UpdateTransferProgressRequester implements RequesterIF<UpdateTransferProgressRequestTO>{

	public List<IResponseTO> execute(UpdateTransferProgressRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkAccounting accounting = WorkerDAOFactory.getInstance().getWorkAccountingDAO().getCurrentWorkAccounting();
		
		if (accounting == null) {
			return responses;
		}
		
		WorkerDAOFactory.getInstance().getWorkAccountingDAO().getCurrentWorkAccounting().incDataTransfered(request.getAmountWritten());
		return responses;
	}

}
