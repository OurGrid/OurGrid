package org.ourgrid.broker.response;

import java.util.Map;

import org.ourgrid.common.internal.response.MessageHandleResponseTO;
import org.ourgrid.worker.communication.processors.handle.RemoteExecuteMessageHandle;

public class RemoteExecuteMessageHandleResponseTO extends MessageHandleResponseTO {
	
	public RemoteExecuteMessageHandleResponseTO(long requestID, String commands, Map<String, String> vars, 
			String clientAddress) {
		super(new RemoteExecuteMessageHandle(requestID, commands, vars), clientAddress);
	}
}
