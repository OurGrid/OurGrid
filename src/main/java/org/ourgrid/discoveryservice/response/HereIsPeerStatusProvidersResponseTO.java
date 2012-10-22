package org.ourgrid.discoveryservice.response;

import java.util.List;

import org.ourgrid.common.internal.IResponseTO;

public class HereIsPeerStatusProvidersResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = DiscoveryServiceResponseConstants.HERE_IS_PEER_STATUS_PROVIDERS;

	
	private List<String> statusProviders;
	private String clientAddress;
	
	
	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setStatusProviders(List<String> statusProviders) {
		this.statusProviders = statusProviders;
	}

	public List<String> getStatusProviders() {
		return statusProviders;
	}

}
