package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.UpdateTransferProgressRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.util.StringUtil;

public class UpdateTransferProgressRequester implements RequesterIF<UpdateTransferProgressRequestTO> {

	public List<IResponseTO> execute(UpdateTransferProgressRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(
				StringUtil.addressToContainerID(request.getSenderAddress()));
		
		if (scheduler != null) {
			scheduler.updateTransferProgress(request.getTransferProgress(), responses);
		}
		
		return responses;
	}
}
