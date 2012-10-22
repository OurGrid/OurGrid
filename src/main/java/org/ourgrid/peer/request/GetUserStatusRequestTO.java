package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class GetUserStatusRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.GET_USER_STATUS;
	private String peerAdress;
	private String clientAddress;
	private boolean canStatusBeUsed;
	
	public String getPeerAdress() {
		return peerAdress;
	}

	public void setPeerAdress(String peerAdress) {
		this.peerAdress = peerAdress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setCanStatusBeUsed(boolean canStatusBeUsed) {
		this.canStatusBeUsed = canStatusBeUsed;
	}

	public boolean canStatusBeUsed() {
		return canStatusBeUsed;
	}

	
}
