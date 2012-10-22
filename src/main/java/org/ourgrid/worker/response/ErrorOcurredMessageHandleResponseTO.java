package org.ourgrid.worker.response;

import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.common.internal.response.MessageHandleResponseTO;
import org.ourgrid.worker.business.controller.GridProcessError;

public class ErrorOcurredMessageHandleResponseTO extends MessageHandleResponseTO {
	public ErrorOcurredMessageHandleResponseTO(GridProcessError error, String clientAddress) {
		super(new ErrorOcurredMessageHandle(error), clientAddress, true);
	}
}
