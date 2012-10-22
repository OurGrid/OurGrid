package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;
import org.ourgrid.worker.communication.processors.handle.GetFilesMessageHandle;

public class GetFilesProcessorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.GET_FILES_PROCESSOR;
	
	
	private GetFilesMessageHandle handle;
	private String senderPublicKey;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setHandle(GetFilesMessageHandle handle) {
		this.handle = handle;
	}

	public GetFilesMessageHandle getHandle() {
		return handle;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

}
