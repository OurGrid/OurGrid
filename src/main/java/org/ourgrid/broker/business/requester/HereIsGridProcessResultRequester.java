package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.HereIsGridProcessResultProcessorRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.util.StringUtil;

public class HereIsGridProcessResultRequester implements RequesterIF<HereIsGridProcessResultProcessorRequestTO> {

	public List<IResponseTO> execute(HereIsGridProcessResultProcessorRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(StringUtil.addressToContainerID(
				request.getWorkerAddress()));
		
		scheduler.hereIsGridProcessResult(request.getWorkerContainerID(), request.getResult(), responses);
		
		
		return responses;
	}
}
