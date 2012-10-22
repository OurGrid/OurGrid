package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.internal.IResponseTO;

public class BrokerMessageProcessorResponseTO implements IResponseTO {
	
	
	private static final String RESPONSE_TYPE = BrokerResponseConstants.BROKER_MESSAGE_PROCESSOR;
	
	
	private MessageProcessor<MessageHandle> processor;
	private MessageHandle handle;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setProcessor(MessageProcessor<MessageHandle> processor) {
		this.processor = processor;
	}

	public MessageProcessor<MessageHandle> getProcessor() {
		return processor;
	}

	public void setHandle(MessageHandle handle) {
		this.handle = handle;
	}

	public MessageHandle getHandle() {
		return handle;
	}
}