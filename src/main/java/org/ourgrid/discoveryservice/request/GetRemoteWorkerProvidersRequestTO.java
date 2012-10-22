package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class GetRemoteWorkerProvidersRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.GET_REMOTE_WORKER_PROVIDERS;
	
	private String clientAddress;
	private String clientUserAtServer;
	private String myAddress;
	private int overloadThreshold;
	private int maxResponseSize;
	private int dsMaxResponse;

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

	public void setMaxResponseSize(int maxResponseSize) {
		this.maxResponseSize = maxResponseSize;
	}
	
	public int getMaxResponseSize() {
		return maxResponseSize;
	}

	public int getOverloadThreshold() {
		return overloadThreshold;
	}

	public void setOverloadThreshold(int overloadThreshold) {
		this.overloadThreshold = overloadThreshold;
	}

	public int getDsMaxResponse() {
		return dsMaxResponse;
	}

	public void setDsMaxResponse(int dsMaxResponse) {
		this.dsMaxResponse = dsMaxResponse;
	}
}
