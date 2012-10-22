package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class PauseRequestRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.PAUSE_REQUEST;
	
	private Long requestId;
	private String brokerPublicKey;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setBrokerPublicKey(String brokerPublicKey) {
		this.brokerPublicKey = brokerPublicKey;
	}

	public String getBrokerPublicKey() {
		return brokerPublicKey;
	}

}
