package org.ourgrid.broker.response;

import org.ourgrid.common.FileTransferInfo;
import org.ourgrid.common.internal.response.MessageHandleResponseTO;
import org.ourgrid.worker.communication.processors.handle.GetFilesMessageHandle;

public class GetFilesMessageHandleResponseTO extends MessageHandleResponseTO {
	
	public GetFilesMessageHandleResponseTO(long requestID, String clientAddress, FileTransferInfo... files) {
		super(new GetFilesMessageHandle(requestID, files), clientAddress);
	}
}
