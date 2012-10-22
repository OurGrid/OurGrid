package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class LeaveCommunityRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.LEAVE_COMMUNITY;
	
	
	private String clientAddress;
	private String clientUserAtServer;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientUserAtServer(String clientUserAtServer) {
		this.clientUserAtServer = clientUserAtServer;
	}

	public String getClientUserAtServer() {
		return clientUserAtServer;
	}
}
