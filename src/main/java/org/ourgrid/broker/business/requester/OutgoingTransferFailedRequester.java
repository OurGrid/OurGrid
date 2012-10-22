package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.OutgoingTransferFailedRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.util.StringUtil;

public class OutgoingTransferFailedRequester implements RequesterIF<OutgoingTransferFailedRequestTO> {

	public List<IResponseTO> execute(OutgoingTransferFailedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(
				StringUtil.addressToContainerID(request.getHandle().getDestinationID()));
		
		if (scheduler != null) {
			scheduler.outgoingTransferFailed(request.getHandle(), request.getFailCauseMessage(), request.getAmountWritten(), responses);
		}
		
		return responses;
	}
}
