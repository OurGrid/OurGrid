package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.RemoteWorkerFailureController;
import org.ourgrid.peer.request.NotifyRemoteWorkerManagementRecoveryRequestTO;

public class NotifyRemoteWorkerManagementRecoveryRequester implements RequesterIF<NotifyRemoteWorkerManagementRecoveryRequestTO> {
	
	public List<IResponseTO> execute(NotifyRemoteWorkerManagementRecoveryRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		RemoteWorkerFailureController.getInstance().doNotifyRecovery(responses, request.getRemoteWorkerAddress(),
				request.getRemoteWorkerPublicKey(), request.getMyUserAtServer());
		
		return responses;
	}

}
