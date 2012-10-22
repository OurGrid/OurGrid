package org.ourgrid.discoveryservice.response;

import org.ourgrid.common.internal.IResponseTO;



public class DSGetDiscoveryServicesResponseTO implements IResponseTO {
	
	private static final String REQUEST_TYPE = DiscoveryServiceResponseConstants.DS_GET_DISCOVERY_SERVICES;

	
	private String dsAddress;
	
	
	public String getResponseType() {
		return REQUEST_TYPE;
	}

	public void setDsAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}

	public String getDsAddress() {
		return dsAddress;
	}
	
}
