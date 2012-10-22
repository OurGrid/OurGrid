package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.controller.WorkerNotificationController;
import org.ourgrid.broker.request.PreemptedWorkerRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;

public class PreemptedWorkerRequester implements RequesterIF<PreemptedWorkerRequestTO> {

	public List<IResponseTO> execute(PreemptedWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerNotificationController.getInstance().doNotifyFailure(responses, 
				request.getWorkerContainerID());
		
		return responses;
	}
}
