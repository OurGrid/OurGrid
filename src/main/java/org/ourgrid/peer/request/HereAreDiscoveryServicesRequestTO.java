package org.ourgrid.peer.request;


import java.util.List;

import org.ourgrid.common.internal.IRequestTO;

public class HereAreDiscoveryServicesRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.HERE_ARE_DISCOVERY_SERVICES;

	private List<String> discoveryServices;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setDiscoveryServices(List<String> discoveryServices) {
		this.discoveryServices = discoveryServices;
	}

	public List<String> getDiscoveryServices() {
		return discoveryServices;
	}
	
}
