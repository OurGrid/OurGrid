package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class GetPeerStatusProvidersRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.GET_PEER_STATUS_PROVIDERS;
	
	
	private String clientAddress;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}
}
