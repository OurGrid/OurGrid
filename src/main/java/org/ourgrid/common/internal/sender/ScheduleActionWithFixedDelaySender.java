package org.ourgrid.common.internal.sender;

import java.io.Serializable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.ScheduleActionWithFixedDelayResponseTO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class ScheduleActionWithFixedDelaySender implements SenderIF<ScheduleActionWithFixedDelayResponseTO> {

	public void execute(ScheduleActionWithFixedDelayResponseTO response, ServiceManager manager) {
		String actionName = response.getActionName();
		long delay = response.getDelay();
		long initialDelay = response.getInitialDelay();
		TimeUnit timeUnit = response.getTimeUnit();
		Serializable handler = response.getHandler();
		
		Future<?> future = response.hasInitialDelay() ? manager.scheduleActionWithFixedDelay(actionName, initialDelay, delay, timeUnit, handler) :
						   manager.scheduleActionWithFixedDelay(actionName, delay, timeUnit, handler);
			
		if (response.storeFuture()) {
			WorkerDAOFactory.getInstance().getFutureDAO().setReportAccountingActionFuture(future);
		}
	}
}