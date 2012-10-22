package org.ourgrid.discoveryservice.response;

import java.util.Set;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;

public abstract class AbstractHereAreDiscoveryServicesResponseTO implements IResponseTO {

	private Set<DiscoveryServiceInfo> discoveryServices;
	private String stubAddress;
	
	
	public void setStubAddress(String stubAddress) {
		this.stubAddress = stubAddress;
	}

	public String getStubAddress() {
		return stubAddress;
	}

	public void setDiscoveryServices(Set<DiscoveryServiceInfo> discoveryServices) {
		this.discoveryServices = discoveryServices;
	}

	public Set<DiscoveryServiceInfo> getDiscoveryServices() {
		return discoveryServices;
	}
}
