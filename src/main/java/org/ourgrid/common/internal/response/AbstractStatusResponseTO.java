package org.ourgrid.common.internal.response;

import org.ourgrid.common.internal.IResponseTO;

public abstract class AbstractStatusResponseTO implements IResponseTO {

	
	private String clientAddress;
	
	
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}
}
