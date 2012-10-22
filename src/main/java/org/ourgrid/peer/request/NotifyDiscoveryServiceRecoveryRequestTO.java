package org.ourgrid.peer.request;

import org.ourgrid.common.internal.IRequestTO;

public class NotifyDiscoveryServiceRecoveryRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.NOTIFY_DS_RECOVERY;
	private String dsServiceID;
	private int dsRequestSize;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setDSServiceID(String dsServiceID) {
		this.dsServiceID = dsServiceID;
	}

	public String getDsServiceID() {
		return dsServiceID;
	}

	public int getDsRequestSize() {
		return dsRequestSize;
	}

	public void setDsRequestSize(int dsRequestSize) {
		this.dsRequestSize = dsRequestSize;
	}
	
}
