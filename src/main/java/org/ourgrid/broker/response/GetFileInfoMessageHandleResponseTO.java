package org.ourgrid.broker.response;

import org.ourgrid.common.internal.response.MessageHandleResponseTO;
import org.ourgrid.worker.communication.processors.handle.GetFileInfoMessageHandle;

public class GetFileInfoMessageHandleResponseTO extends MessageHandleResponseTO {
	
	public GetFileInfoMessageHandleResponseTO(long handleId, long requestID, String destinationFileName, 
			String clientAddress) {
		super(new GetFileInfoMessageHandle(handleId, requestID, destinationFileName), clientAddress);
	}
}
