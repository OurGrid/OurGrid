package org.ourgrid.common.internal.response;

import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.OurGridResponseConstants;

public abstract class MessageHandleResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = OurGridResponseConstants.MESSAGE_HANDLE;
	

	private MessageHandle messageHandle;
	private String clientAddress;
	private boolean isErrorMessage;
	
	
	public MessageHandleResponseTO(MessageHandle messageHandle, String clientAddress) {
		this(messageHandle, clientAddress, false);
	}
	
	public MessageHandleResponseTO(MessageHandle messageHandle, String clientAddress, 
			boolean isErrorMessage) {
		this.messageHandle = messageHandle;
		this.clientAddress = clientAddress;
		this.setErrorMessage(isErrorMessage);
	}
	
	
	public void setMessageHandle(MessageHandle messageHandle) {
		this.messageHandle = messageHandle;
	}
	
	public MessageHandle getMessageHandle() {
		return this.messageHandle;
	}
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setErrorMessage(boolean isErrorMessage) {
		this.isErrorMessage = isErrorMessage;
	}

	public boolean isErrorMessage() {
		return isErrorMessage;
	}
}
