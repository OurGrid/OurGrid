package org.ourgrid.peer.request;

import org.ourgrid.common.internal.IRequestTO;

public class NotifyDiscoveryServiceFailureRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.NOTIFY_DS_FAILURE;
	private String dsServiceId;

	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setDSServiceID(String dsServiceId) {
		this.dsServiceId = dsServiceId;
	}

	public String getDSServiceID() {
		return dsServiceId;
	}
}
