package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.business.controller.IdlenessDetectorController;
import org.ourgrid.worker.request.WorkerSpecBasedIdlenessDetectorActionRequestTO;

public class WorkerSpecBasedIdlenessDetectorActionRequester extends AbstractScheduledIdlenessDetectorActionRequester<WorkerSpecBasedIdlenessDetectorActionRequestTO> {

	public List<IResponseTO> execute(WorkerSpecBasedIdlenessDetectorActionRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (isIdle()) {
			IdlenessDetectorController.getInstance().resumeWorker(responses);
		} else {
			IdlenessDetectorController.getInstance().pauseWorker(responses);
		}
		
		return responses;
	}
	
	protected boolean isIdle() {
		return super.isIdle() && thereIsOneCoreIdle();
	}
	
	public boolean thereIsOneCoreIdle() {
		return false;
	}
}
