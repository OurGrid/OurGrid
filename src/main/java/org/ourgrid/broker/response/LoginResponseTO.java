package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;

public class LoginResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = BrokerResponseConstants.LOGIN;
	
	private String stubAddress;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setStubAddress(String stubAddress) {
		this.stubAddress = stubAddress;
	}

	public String getStubAddress() {
		return stubAddress;
	}
}
