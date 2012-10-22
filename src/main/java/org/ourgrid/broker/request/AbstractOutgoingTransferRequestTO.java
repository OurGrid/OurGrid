package org.ourgrid.broker.request;

import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IRequestTO;




public abstract class AbstractOutgoingTransferRequestTO implements IRequestTO {

	
	private String senderAddress;
	private OutgoingHandle handle;
	private long amountWritten;
	

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setHandle(OutgoingHandle handle) {
		this.handle = handle;
	}

	public OutgoingHandle getHandle() {
		return handle;
	}

	public void setAmountWritten(long amountWritten) {
		this.amountWritten = amountWritten;
	}

	public long getAmountWritten() {
		return amountWritten;
	}
}
