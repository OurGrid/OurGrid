package org.ourgrid.worker.response;

import org.ourgrid.broker.communication.actions.HereIsWorkerSpecMessageHandle;
import org.ourgrid.common.internal.response.MessageHandleResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class HereIsWorkerSpecMessageHandleResponseTO extends MessageHandleResponseTO {
	public HereIsWorkerSpecMessageHandleResponseTO(WorkerSpecification workerSpec, String clientAddress) {
		super(new HereIsWorkerSpecMessageHandle(workerSpec), clientAddress);
	}
}
