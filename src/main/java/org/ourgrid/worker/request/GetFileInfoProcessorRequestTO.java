package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;
import org.ourgrid.worker.communication.processors.handle.GetFileInfoMessageHandle;

public class GetFileInfoProcessorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.GET_FILE_INFO_PROCESSOR;
	
	
	private GetFileInfoMessageHandle handle;
	private String senderPublicKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setHandle(GetFileInfoMessageHandle handle) {
		this.handle = handle;
	}

	public GetFileInfoMessageHandle getHandle() {
		return handle;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}
}
