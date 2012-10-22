package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.controller.WorkerNotificationController;
import org.ourgrid.broker.request.WorkerDoNotifyFailureRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;

public class WorkerDoNotifyFailureRequester implements RequesterIF<WorkerDoNotifyFailureRequestTO> {

	public List<IResponseTO> execute(WorkerDoNotifyFailureRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerNotificationController.getInstance().doNotifyFailure(responses, 
				request.getWorkerContainerID());
		
		return responses;
	}
}
