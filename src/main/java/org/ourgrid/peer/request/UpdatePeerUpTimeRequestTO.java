package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class UpdatePeerUpTimeRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.UPDATE_PEER_UP_TIME;
	
	
	private String myUserAtServer;
	private String senderPublicKey;
	private boolean isThisMyPublicKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}

	public String getMyUserAtServer() {
		return myUserAtServer;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public void setThisMyPublicKey(boolean isThisMyPublicKey) {
		this.isThisMyPublicKey = isThisMyPublicKey;
	}

	public boolean isThisMyPublicKey() {
		return isThisMyPublicKey;
	}

}
