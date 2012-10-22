package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.request.HereIsWorkerSpecProcessorRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;

public class HereIsWorkerSpecRequester implements RequesterIF<HereIsWorkerSpecProcessorRequestTO> {

	public List<IResponseTO> execute(HereIsWorkerSpecProcessorRequestTO request) {
		BrokerDAOFactory.getInstance().getWorkerDAO().updateWorkerSpec(request.getWorkerAddress(), request.getWorkerSpec());
		
		return new ArrayList<IResponseTO>();
	}
}
