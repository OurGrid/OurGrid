package org.ourgrid.discoveryservice.response;

import org.ourgrid.common.internal.IResponseTO;

/**
 * ResponseTO for method DSIsOverloaded
 */
public class DSIsOverloadedResponseTO implements IResponseTO{

	private static final String RESPONSE_TYPE = DiscoveryServiceResponseConstants.DS_IS_OVERLOADED;
	private String dsAddress;
	private String clientAddress;
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setDSAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}

	public String getDSAddress() {
		return dsAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
}
