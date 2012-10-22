package org.ourgrid.broker.communication.sender;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.communication.dao.SchedulerFutureDAO;
import org.ourgrid.broker.response.ScheduleActionToRunOnceResponseTO;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class ScheduleActionToRunOnceSender implements SenderIF<ScheduleActionToRunOnceResponseTO> {

	public void execute(ScheduleActionToRunOnceResponseTO response,
			ServiceManager manager) {

		SchedulerFutureDAO schedulerFutureDAO = SchedulerFutureDAO.getInstance();
		if (!schedulerFutureDAO.isSchedulerActionActive()) {
			Future<?> schedulerFuture = manager.scheduleActionToRunOnce(BrokerConstants.SCHEDULER_ACTION_NAME, 
					BrokerConstants.SCHEDULER_INTERVAL, TimeUnit.SECONDS);
			
			schedulerFutureDAO.setSchedulerFuture(schedulerFuture);
		}
	}

}
