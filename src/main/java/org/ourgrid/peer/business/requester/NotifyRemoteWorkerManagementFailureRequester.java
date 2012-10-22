package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.RemoteWorkerFailureController;
import org.ourgrid.peer.request.NotifyRemoteWorkerManagementFailureRequestTO;

public class NotifyRemoteWorkerManagementFailureRequester implements RequesterIF<NotifyRemoteWorkerManagementFailureRequestTO> {
	
	public List<IResponseTO> execute(NotifyRemoteWorkerManagementFailureRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		RemoteWorkerFailureController.getInstance().doNotifyFailure(responses, request.getRemoteWorkerAddress(),
				request.getRemoteWorkerPublicKey());
		
		return responses;
	}

}
