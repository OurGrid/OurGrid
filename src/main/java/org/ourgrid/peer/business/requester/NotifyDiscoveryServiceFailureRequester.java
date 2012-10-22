package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.ds.DiscoveryServiceNotificationController;
import org.ourgrid.peer.request.NotifyDiscoveryServiceFailureRequestTO;

public class NotifyDiscoveryServiceFailureRequester implements RequesterIF<NotifyDiscoveryServiceFailureRequestTO> {

	public List<IResponseTO> execute(NotifyDiscoveryServiceFailureRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceNotificationController.getInstance().doNotifyFailure(responses, 
				request.getDSServiceID());
		
		return responses;
	}

}
