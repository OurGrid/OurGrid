package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.request.GetMasterPeerRequestTO;
import org.ourgrid.worker.response.HereIsMasterPeerResponseTO;

public class GetMasterPeerRequester implements RequesterIF<GetMasterPeerRequestTO> {

	public List<IResponseTO> execute(GetMasterPeerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (request.canStatusBeUsed()) {
			HereIsMasterPeerResponseTO hereIsMasterPeerResponseTO = new HereIsMasterPeerResponseTO();
			hereIsMasterPeerResponseTO.setClientAddress(request.getClientAddress());
			hereIsMasterPeerResponseTO.setMasterPeerAddress(WorkerDAOFactory.getInstance().
					getWorkerStatusDAO().getMasterPeerAddress());
			
			responses.add(hereIsMasterPeerResponseTO);
		}
		
		return responses;
	}
	
	
}
