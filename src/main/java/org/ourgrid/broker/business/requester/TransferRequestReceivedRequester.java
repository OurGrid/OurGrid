package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.TransferRequestReceivedRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;

public class TransferRequestReceivedRequester implements RequesterIF<TransferRequestReceivedRequestTO> {

	public List<IResponseTO> execute(TransferRequestReceivedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(request.getHandle().getOppositeID());
		
		if (scheduler != null) {
			scheduler.transferRequestReceived(request.getHandle(), responses);
		}
		
		return responses;
	}
}
