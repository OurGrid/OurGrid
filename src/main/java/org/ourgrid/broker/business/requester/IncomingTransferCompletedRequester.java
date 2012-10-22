package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.IncomingTransferCompletedRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;

public class IncomingTransferCompletedRequester implements RequesterIF<IncomingTransferCompletedRequestTO> {

	public List<IResponseTO> execute(IncomingTransferCompletedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(request.getHandle().getOppositeID());
		
		if (scheduler != null) {
			scheduler.incomingTransferCompleted(request.getHandle(), request.getAmountWritten(), responses);
		}
		
		return responses;
	}
}
