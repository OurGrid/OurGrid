package org.ourgrid.worker.request;

import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class IncomingTransferCompletedRequestTO implements IRequestTO {
	
	private String REQUEST_TYPE = WorkerRequestConstants.INCOMING_TRANSFER_COMPLETED;
	
	
	private String senderPublicKey;
	private IncomingHandle handle;
	private long amountWritten;

	
	public String getSenderPublicKey() {
		return senderPublicKey;
	}
	
	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setHandle(IncomingHandle handle) {
		this.handle = handle;
	}

	public IncomingHandle getHandle() {
		return handle;
	}

	public void setAmountWritten(long amountWritten) {
		this.amountWritten = amountWritten;
	}

	public long getAmountWritten() {
		return amountWritten;
	}
}
