package org.ourgrid.discoveryservice.response;

import java.util.Set;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;

public class PersistNetworkResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = DiscoveryServiceResponseConstants.PERSIST_NETWORK;
	
	
	private Set<DiscoveryServiceInfo> discoveryServicesAddresses;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}
	

	public void setDiscoveryServicesAddresses(
			Set<DiscoveryServiceInfo> discoveryServicesAddresses) {
		this.discoveryServicesAddresses = discoveryServicesAddresses;
	}

	public Set<DiscoveryServiceInfo> getDiscoveryServicesAddresses() {
		return discoveryServicesAddresses;
	}
	
}
