package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.OutgoingTransferCancelledRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.util.StringUtil;

public class OutgoingTransferCancelledRequester implements RequesterIF<OutgoingTransferCancelledRequestTO> {

	public List<IResponseTO> execute(OutgoingTransferCancelledRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(
				StringUtil.addressToContainerID(request.getHandle().getDestinationID()));
		
		if (scheduler != null) {
			scheduler.outgoingTransferCancelled(request.getHandle(), request.getAmountWritten(), responses);
		}
		
		return responses;
	}
}
