package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class DSGetDiscoveryServicesRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.DS_GET_DISCOVERY_SERVICES;
	
	private String dsAddress;

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setDsAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}

	public String getDsAddress() {
		return dsAddress;
	}

}
