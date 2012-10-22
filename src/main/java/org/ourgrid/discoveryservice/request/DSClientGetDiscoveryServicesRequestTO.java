package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class DSClientGetDiscoveryServicesRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.DS_CLIENT_GET_DISCOVERY_SERVICES;
	
	private String clientAddress;
	private String clientUserAtServer;
	private String myAddress;
	private int overloadThreshold;
	
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

	public String getMyAddress() {
		return myAddress;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public int getOverloadThreshold() {
		return overloadThreshold;
	}

	public void setOverloadThreshold(int overloadThreshold) {
		this.overloadThreshold = overloadThreshold;
	}
}
