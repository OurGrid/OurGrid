package org.ourgrid.discoveryservice.request;

import java.util.List;

import org.ourgrid.common.internal.IRequestTO;

public class HereAreDiscoveryServicesRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.HERE_ARE_DISCOVERY_SERVICES;
	
	
	private List<String> discoveryServicesAddresses;
	private String myAddress;
	private String senderAddress;
	

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setDiscoveryServicesAddresses(
			List<String> discoveryServicesAddresses) {
		this.discoveryServicesAddresses = discoveryServicesAddresses;
	}

	public List<String> getDiscoveryServicesAddresses() {
		return discoveryServicesAddresses;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}
}
