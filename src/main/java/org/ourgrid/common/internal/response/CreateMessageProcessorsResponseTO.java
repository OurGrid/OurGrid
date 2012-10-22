package org.ourgrid.common.internal.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;


/**
 * Requirement 302
 */
public class CreateMessageProcessorsResponseTO implements IResponseTO {
	

	private final String RESPONSE_TYPE = BrokerResponseConstants.CREATE_MESSAGE_PROCESSORS;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
}