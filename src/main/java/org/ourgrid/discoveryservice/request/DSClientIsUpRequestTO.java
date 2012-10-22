package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class DSClientIsUpRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.DS_CLIENT_IS_UP;
	
	
	private String clientUserAtServer;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setClientUserAtServer(String clientUserAtServer) {
		this.clientUserAtServer = clientUserAtServer;
	}

	public String getClientUserAtServer() {
		return clientUserAtServer;
	}
}
