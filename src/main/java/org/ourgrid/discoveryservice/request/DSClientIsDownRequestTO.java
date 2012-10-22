package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class DSClientIsDownRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.DS_CLIENT_IS_DOWN;
	
	
	private String clientUserAtServer;
	private String clientAddress;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setClientUserAtServer(String clientUserAtServer) {
		this.clientUserAtServer = clientUserAtServer;
	}

	public String getClientUserAtServer() {
		return clientUserAtServer;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}
}
