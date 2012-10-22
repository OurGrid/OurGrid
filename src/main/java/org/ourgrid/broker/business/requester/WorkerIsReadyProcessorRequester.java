package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.WorkerIsReadyProcessorRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.util.StringUtil;

public class WorkerIsReadyProcessorRequester implements RequesterIF<WorkerIsReadyProcessorRequestTO> {

	public List<IResponseTO> execute(WorkerIsReadyProcessorRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(StringUtil.addressToContainerID(
				request.getWorkerAddress()));
		
		scheduler.workerIsReady(request.getWorkerContainerID(), responses);

		
		return responses;
	}
}
