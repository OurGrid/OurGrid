package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.TransferRejectedRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.util.StringUtil;

public class TransferRejectedRequester implements RequesterIF<TransferRejectedRequestTO> {

	public List<IResponseTO> execute(TransferRejectedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(
				StringUtil.addressToContainerID(request.getHandle().getDestinationID()));
		
		if (scheduler != null) {
			scheduler.transferRejected(request.getHandle(), responses);
		}
		
		return responses;
	}
}
