package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IRequestTO;




public class TransferRejectedRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.TRANSFER_REJECTED;
	
	private String senderAddress;
	private OutgoingHandle handle;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

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
}
