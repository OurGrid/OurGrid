package org.ourgrid.peer.request;

import org.ourgrid.common.internal.IRequestTO;

public class StopPeerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.STOP_PEER;
	
	
	private boolean isDAOStarted;
	private boolean shouldJoinCommunity;
	private boolean canStatusBeUsed;
	private String myUserAtServer;
	
	
	/**
	 * @return the isDAOStarted
	 */
	public boolean isDAOStarted() {
		return isDAOStarted;
	}


	/**
	 * @param isDAOStarted the isDAOStarted to set
	 */
	public void setDAOStarted(boolean isDAOStarted) {
		this.isDAOStarted = isDAOStarted;
	}


	/**
	 * @return the shouldJoinCommunity
	 */
	public boolean shouldJoinCommunity() {
		return shouldJoinCommunity;
	}


	/**
	 * @param shouldJoinCommunity the shouldJoinCommunity to set
	 */
	public void setShouldJoinCommunity(boolean shouldJoinCommunity) {
		this.shouldJoinCommunity = shouldJoinCommunity;
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


	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}


	public String getMyUserAtServer() {
		return myUserAtServer;
	}

}
