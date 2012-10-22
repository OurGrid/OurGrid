package org.ourgrid.discoveryservice.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;

public class HereIsCompleteStatusResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = DiscoveryServiceResponseConstants.HERE_IS_COMPLETE_STATUS;

	
	private DiscoveryServiceCompleteStatus discoveryServiceCompleteStatus;
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

	public void setDiscoveryServiceCompleteStatus(
			DiscoveryServiceCompleteStatus discoveryServiceCompleteStatus) {
		this.discoveryServiceCompleteStatus = discoveryServiceCompleteStatus;
	}

	public DiscoveryServiceCompleteStatus getDiscoveryServiceCompleteStatus() {
		return discoveryServiceCompleteStatus;
	}

}
