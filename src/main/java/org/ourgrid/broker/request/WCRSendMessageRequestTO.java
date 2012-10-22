package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.internal.IRequestTO;

public class WCRSendMessageRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = BrokerRequestConstants.WCR_SEND_MESSAGE;
	
	
	private String senderAddress;
	private MessageHandle handle;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setHandle(MessageHandle handle) {
		this.handle = handle;
	}

	public MessageHandle getHandle() {
		return handle;
	}
}