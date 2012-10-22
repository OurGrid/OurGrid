package org.ourgrid.worker.status;

import java.io.Serializable;

public class PeerStatusInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String state;
	private String loginError;
	private String peerUserAtServer;
	
	
	public PeerStatusInfo(String state, String loginError, String peerUserAtServer) {
		this.state = state;
		this.loginError = loginError;
		this.peerUserAtServer = peerUserAtServer;
	}


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLoginError() {
		return loginError;
	}

	public void setLoginError(String loginError) {
		this.loginError = loginError;
	}

	public String getPeerUserAtServer() {
		return peerUserAtServer;
	}

	public void setPeerUserAtServer(String peerUserAtServer) {
		this.peerUserAtServer = peerUserAtServer;
	}
}
