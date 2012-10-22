package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.ErrorOcurredProcessorRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.worker.business.controller.GridProcessError;

public class ErrorOcurredProcessorRequester implements RequesterIF<ErrorOcurredProcessorRequestTO> {

	public List<IResponseTO> execute(ErrorOcurredProcessorRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(
				StringUtil.addressToContainerID(request.getWorkerAddress()));
		GridProcessError gridProcessError = request.getGridProcessError();
		
		String errorCause = null;
		
		if (gridProcessError.getErrorCause() != null) {
			errorCause = gridProcessError.getErrorCause().getMessage();
		}
		
		scheduler.errorOcurred(request.getWorkerContainerID().toString(), 
				errorCause, gridProcessError.getType().getName(), responses);
		
		return responses;
	}
}
