package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.WorkerSpecListenerController;
import org.ourgrid.peer.request.UpdateWorkerSpecRequestTO;


public class UpdateWorkerSpecRequester implements RequesterIF<UpdateWorkerSpecRequestTO> {

	public List<IResponseTO> execute(UpdateWorkerSpecRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		WorkerSpecListenerController.getInstance().updateWorkerSpec(responses, 
				request.getWorkerSpec(), request.getWorkerPublicKey(), request.getWorkerUserAtServer(), 
				request.getMyUserAtServer());
		
		return responses;
	}

}
