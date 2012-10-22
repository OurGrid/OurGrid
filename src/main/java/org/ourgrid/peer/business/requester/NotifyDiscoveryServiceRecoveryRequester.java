package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.ds.DiscoveryServiceNotificationController;
import org.ourgrid.peer.request.NotifyDiscoveryServiceRecoveryRequestTO;

public class NotifyDiscoveryServiceRecoveryRequester implements RequesterIF<NotifyDiscoveryServiceRecoveryRequestTO> {

	public List<IResponseTO> execute(NotifyDiscoveryServiceRecoveryRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceNotificationController.getInstance().doNotifyRecovery(responses, 
				request.getDsServiceID(), request.getDsRequestSize());
		
		return responses;
	}

}
