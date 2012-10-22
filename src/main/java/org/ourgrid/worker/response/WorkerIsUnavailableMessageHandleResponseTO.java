package org.ourgrid.worker.response;

import org.ourgrid.broker.communication.actions.WorkerIsUnavailableMessageHandle;
import org.ourgrid.common.internal.response.MessageHandleResponseTO;

public class WorkerIsUnavailableMessageHandleResponseTO extends MessageHandleResponseTO {
	public WorkerIsUnavailableMessageHandleResponseTO(String clientAddress) {
		super(new WorkerIsUnavailableMessageHandle(), clientAddress);
	}
}
