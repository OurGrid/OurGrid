package org.ourgrid.discoveryservice.request;

import java.util.List;

import org.ourgrid.common.internal.IRequestTO;

/**
 * Requirement 502
 */
public class StartDiscoveryServiceRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.START_DISCOVERY_SERVICE;
	
	
	private List<String> networkAddresses;
	private String myAddress;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setNetworkAddresses(List<String> networkAddresses) {
		this.networkAddresses = networkAddresses;
	}

	public List<String> getNetworkAddresses() {
		return networkAddresses;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}
}
