package org.ourgrid.broker.request;

import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.internal.IRequestTO;




public abstract class AbstractIncomingTransferRequestTO implements IRequestTO {

	
	private String senderAddress;
	private IncomingHandle handle;
	private long amountWritten;
	

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderAddress() {
		return senderAddress;
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
