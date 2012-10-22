package org.ourgrid.discoveryservice.response;



public class DSHereAreDiscoveryServicesResponseTO extends AbstractHereAreDiscoveryServicesResponseTO {
	
	private static final String REQUEST_TYPE = DiscoveryServiceResponseConstants.DS_HERE_ARE_DISCOVERY_SERVICES;

	
	public String getResponseType() {
		return REQUEST_TYPE;
	}
}
