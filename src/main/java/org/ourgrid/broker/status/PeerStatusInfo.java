package org.ourgrid.broker.status;

import java.io.Serializable;

import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.status.PeerState;

public class PeerStatusInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String state;
	private PeerSpecification peerSpec;
	private String loginError;
	
	public PeerStatusInfo() {}
	
	public PeerStatusInfo(String state, PeerSpecification spec) {
		this(state, spec, null);
	}
	
	public PeerStatusInfo(String state, PeerSpecification spec, String loginError) {
		this.state = state;
		this.peerSpec = spec;
		this.loginError = loginError;
	}

	public PeerSpecification getPeerSpec() {
		return peerSpec;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public boolean isDown() {
		return this.state.equals(PeerState.DOWN.toString()); 
	}
	
	public boolean isNotLogged() {
		return this.state.equals(PeerState.NOT_LOGGED.toString()); 
	}
	
	public boolean isLogged() {
		return this.state.equals(PeerState.LOGGED.toString()); 
	}

	public void setPeerSpec(PeerSpecification peerSpec) {
		this.peerSpec = peerSpec;
	}

	public void setLoginError(String loginError) {
		this.loginError = loginError;
	}

	public String getLoginError() {
		return loginError;
	}

}
