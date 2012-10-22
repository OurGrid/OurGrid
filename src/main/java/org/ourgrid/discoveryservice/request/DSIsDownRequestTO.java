package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

/**
 * Requirement 508
 */
public class DSIsDownRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.DS_IS_DOWN;
	
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
