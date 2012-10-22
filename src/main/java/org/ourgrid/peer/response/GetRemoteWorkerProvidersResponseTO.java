package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class GetRemoteWorkerProvidersResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.GET_REMOTE_WORKER_PROVIDERS;
	private String dsClientObjectName;
	private String dsAddress;
	private int dsRequestSize;
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setDsClientObjectName(String dsClientObjectName) {
		this.dsClientObjectName = dsClientObjectName;
	}

	public String getDsClientObjectName() {
		return dsClientObjectName;
	}

	public void setDsAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}

	public String getDsAddress() {
		return dsAddress;
	}

	public int getDsRequestSize() {
		return dsRequestSize;
	}

	public void setDsRequestSize(int dsRequestSize) {
		this.dsRequestSize = dsRequestSize;
	}
}
