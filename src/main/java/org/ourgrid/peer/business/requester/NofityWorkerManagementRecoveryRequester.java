package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.WorkerNotificationController;
import org.ourgrid.peer.request.NotifyWorkerManagementRecoveryRequestTO;

public class NofityWorkerManagementRecoveryRequester implements RequesterIF<NotifyWorkerManagementRecoveryRequestTO> {
	
	public List<IResponseTO> execute(NotifyWorkerManagementRecoveryRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerNotificationController.getInstance().doNotifyRecovery(responses, 
				request.getRecoveredWorkerAddress(),
				request.getRecoveredWorkerPublicKey());
		
		return responses;
	}
}
