package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.status.PeerCompleteStatus;

public class HereIsCompleteStatusResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.HERE_IS_COMPLETE_STATUS;
	private PeerCompleteStatus peerCompleteStatus;
	private String clientAddress;
	private String peerAddress;
	
	
	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setPeerCompleteStatus(PeerCompleteStatus peerCompleteStatus) {
		this.peerCompleteStatus = peerCompleteStatus;
	}

	public PeerCompleteStatus getPeerCompleteStatus() {
		return peerCompleteStatus;
	}

}
