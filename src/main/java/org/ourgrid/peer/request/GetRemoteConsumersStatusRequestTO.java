package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class GetRemoteConsumersStatusRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.GET_REMOTE_CONSUMERS_STATUS;
	
	
	private boolean canStatusBeUsed;
	private String peerAddress;
	private String clientAddress;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setCanStatusBeUsed(boolean canStatusBeUsed) {
		this.canStatusBeUsed = canStatusBeUsed;
	}

	public boolean canStatusBeUsed() {
		return canStatusBeUsed;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}
}
