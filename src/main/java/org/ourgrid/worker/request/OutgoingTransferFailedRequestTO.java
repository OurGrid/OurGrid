package org.ourgrid.worker.request;

import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class OutgoingTransferFailedRequestTO implements IRequestTO {
	
	private String REQUEST_TYPE = WorkerRequestConstants.OUTGOING_TRANSFER_FAILED;

	
	private String senderPublicKey;
	private OutgoingHandle handle;
	private long amountUploaded;
	private Exception exception;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}


	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}


	public String getSenderPublicKey() {
		return senderPublicKey;
	}


	public void setHandle(OutgoingHandle handle) {
		this.handle = handle;
	}


	public OutgoingHandle getHandle() {
		return handle;
	}


	public void setAmountUploaded(long amountUploaded) {
		this.amountUploaded = amountUploaded;
	}


	public long getAmountUploaded() {
		return amountUploaded;
	}


	public void setException(Exception exception) {
		this.exception = exception;
	}


	public Exception getException() {
		return exception;
	}

	
}
