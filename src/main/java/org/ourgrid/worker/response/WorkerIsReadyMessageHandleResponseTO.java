package org.ourgrid.worker.response;

import org.ourgrid.broker.communication.actions.WorkerIsReadyMessageHandle;
import org.ourgrid.common.internal.response.MessageHandleResponseTO;

public class WorkerIsReadyMessageHandleResponseTO extends MessageHandleResponseTO {
	public WorkerIsReadyMessageHandleResponseTO(String clientAddress) {
		super(new WorkerIsReadyMessageHandle(), clientAddress);
	}
}
