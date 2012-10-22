package org.ourgrid.peer.response;

import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.internal.IResponseTO;

public class HereIsNetworkOfFavorsStatusResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.HERE_IS_NETWORK_OF_FAVORS_STATUS;
	private NetworkOfFavorsStatus nofStatus;
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

	public void setNofStatus(NetworkOfFavorsStatus nofStatus) {
		this.nofStatus = nofStatus;
	}

	public NetworkOfFavorsStatus getNofStatus() {
		return nofStatus;
	}
	
}
