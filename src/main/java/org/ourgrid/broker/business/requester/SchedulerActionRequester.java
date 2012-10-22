package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.SchedulerActionRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;

public class SchedulerActionRequester implements RequesterIF<SchedulerActionRequestTO> {

	public List<IResponseTO> execute(SchedulerActionRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		Set<SchedulerIF> schedulers = BrokerDAOFactory.getInstance().getJobDAO().getSchedulers();
		
		for (SchedulerIF schedulerIF : schedulers) {
			schedulerIF.schedule(responses);
		}

		return responses;
	}
}
