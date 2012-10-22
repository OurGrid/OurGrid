package org.ourgrid.worker.response;

import org.ourgrid.broker.communication.actions.HereIsFileInfoMessageHandle;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.internal.response.MessageHandleResponseTO;

public class HereIsFileInfoMessageHandleResponseTO extends MessageHandleResponseTO {
	
	
	public HereIsFileInfoMessageHandleResponseTO(long handleId, FileInfo fileInfo, String clientAddress) {
		super(new HereIsFileInfoMessageHandle(handleId, fileInfo), clientAddress);
	}
}
