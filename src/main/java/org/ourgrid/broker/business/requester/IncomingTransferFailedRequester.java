package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.IncomingTransferFailedRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;

public class IncomingTransferFailedRequester implements RequesterIF<IncomingTransferFailedRequestTO> {

	public List<IResponseTO> execute(IncomingTransferFailedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(request.getHandle().getOppositeID());
		
		if (scheduler != null) {
			scheduler.incomingTransferFailed(request.getHandle(), request.getFailCauseMessage(), request.getAmountWritten(), responses);
		}
		
		return responses;
	}
}
