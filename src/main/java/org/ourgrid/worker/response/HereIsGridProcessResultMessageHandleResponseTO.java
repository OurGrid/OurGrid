package org.ourgrid.worker.response;

import org.ourgrid.broker.communication.actions.HereIsGridProcessResultMessageHandle;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.internal.response.MessageHandleResponseTO;

public class HereIsGridProcessResultMessageHandleResponseTO extends MessageHandleResponseTO {
	public HereIsGridProcessResultMessageHandleResponseTO(ExecutorResult result, String clientAddress) {
		super(new HereIsGridProcessResultMessageHandle(result), clientAddress);
	}
}
