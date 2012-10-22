package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.internal.IRequestTO;




public class TransferRequestReceivedRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.TRANSFER_REQUEST_RECEIVED;
	
	private String senderAddress;
	private IncomingHandle handle;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

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
}
