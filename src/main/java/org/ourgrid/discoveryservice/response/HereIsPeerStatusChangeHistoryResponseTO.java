package org.ourgrid.discoveryservice.response;

import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;

public class HereIsPeerStatusChangeHistoryResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = DiscoveryServiceResponseConstants.HERE_IS_PEER_STATUS_CHANGE_HISTORY;

	
	private List<DS_PeerStatusChange> peerStatusChangesHistory;
	private String clientAddress;
	
	
	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setPeerStatusChangesHistory(List<DS_PeerStatusChange> peerStatusChangesHistory) {
		this.peerStatusChangesHistory = peerStatusChangesHistory;
	}

	public List<DS_PeerStatusChange> getPeerStatusChangesHistory() {
		return peerStatusChangesHistory;
	}
}
