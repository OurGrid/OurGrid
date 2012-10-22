package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.WorkerNotificationController;
import org.ourgrid.peer.request.NotifyWorkerManagementFailureRequestTO;

public class NotifyWorkerManagementFailureRequester implements RequesterIF<NotifyWorkerManagementFailureRequestTO> {
	
	public List<IResponseTO> execute(NotifyWorkerManagementFailureRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerNotificationController.getInstance().doNotifyFailure(responses, request.getFailedWorkerAddress(), 
				request.getFailedWorkerPublicKey());
		
		return responses;
	}
}
