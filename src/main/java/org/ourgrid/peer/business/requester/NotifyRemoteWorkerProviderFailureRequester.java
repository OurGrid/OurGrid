package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.RemoteWorkerProviderFailureController;
import org.ourgrid.peer.request.NotifyRemoteWorkerProviderFailureRequestTO;

public class NotifyRemoteWorkerProviderFailureRequester implements RequesterIF<NotifyRemoteWorkerProviderFailureRequestTO> {
	
	public List<IResponseTO> execute(NotifyRemoteWorkerProviderFailureRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		RemoteWorkerProviderFailureController.getInstance().doNotifyFailure(
				responses, request.getRwpUserAtServer(), request.getRwpPublicKey());
		
		return responses;
	}
	
}
