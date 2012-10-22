package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class GetPeerStatusChangeHistoryRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.GET_PEER_STATUS_CHANGE_HISTORY;
	
	
	private String clientAddress;
	private long since;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setSince(long since) {
		this.since = since;
	}

	public long getSince() {
		return since;
	}
}
