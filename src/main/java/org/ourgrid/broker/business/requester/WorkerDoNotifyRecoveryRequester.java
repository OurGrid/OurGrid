package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.controller.WorkerNotificationController;
import org.ourgrid.broker.request.WorkerDoNotifyRecoveryRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;

public class WorkerDoNotifyRecoveryRequester implements RequesterIF<WorkerDoNotifyRecoveryRequestTO> {

	public List<IResponseTO> execute(WorkerDoNotifyRecoveryRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerNotificationController.getInstance().doNotifyRecovery(responses, request.getWorkerDeploymentID(),
				request.getWorkerPublicKey());
		
		return responses;
	}
}
