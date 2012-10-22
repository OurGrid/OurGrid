package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;

public class HereIsCompleteHistoryStatusResponseTO implements IResponseTO {
	
	private static final String RESPONSE_TYPE = PeerResponseConstants.HERE_IS_COMPLETE_HISTORY_STATUS;
	
	
	private PeerCompleteHistoryStatus peerCompleteHistoryStatus;
	private String clientAddress;
	private String peerAddress;
	private long untilTime;
	

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setPeerCompleteHistoryStatus(PeerCompleteHistoryStatus peerCompleteHistoryStatus) {
		this.peerCompleteHistoryStatus = peerCompleteHistoryStatus;
	}

	public PeerCompleteHistoryStatus getPeerCompleteHistoryStatus() {
		return peerCompleteHistoryStatus;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setUntilTime(long untilTime) {
		this.untilTime = untilTime;
	}

	public long getUntilTime() {
		return untilTime;
	}
	
}
