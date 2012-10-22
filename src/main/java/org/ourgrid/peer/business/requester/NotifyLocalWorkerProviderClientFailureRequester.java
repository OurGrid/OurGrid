package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.WorkerProviderClientFailureController;
import org.ourgrid.peer.request.NotifyLocalWorkerProviderClientFailureRequestTO;

public class NotifyLocalWorkerProviderClientFailureRequester implements 
	RequesterIF<NotifyLocalWorkerProviderClientFailureRequestTO> {

	public List<IResponseTO> execute(
			NotifyLocalWorkerProviderClientFailureRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();		
		
		WorkerProviderClientFailureController.getInstance().doNotifyFailure(responses,
				request.getMyCertPathDN(), request.getBrokerContainerID(), request.getBrokerPublicKey(), 
				request.getBrokerUserAtServer(), request.getBrokerAddress());
		
		return responses;
	}


	
}
