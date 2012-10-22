/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.broker.business.dao;

import java.io.Serializable;

import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.status.PeerState;

public class PeerEntry implements Serializable {

	private static final long serialVersionUID = 40L;
	
	private PeerSpecification peerSpec;
	private String loginError;
	private PeerState state;
	
	
	public PeerEntry(PeerSpecification peerSpec) {
		this.peerSpec = peerSpec;
		setAsDown();
	}

	
	public void setAsDown() {
		this.state = PeerState.DOWN;
	}
	
	public void setAsNotLogged(String loginError) {
		this.state = PeerState.NOT_LOGGED;
		this.setLoginError(loginError);
	}

	public void setAsLogged() {
		this.state = PeerState.LOGGED;
	}
	
	public boolean isDown() {
		return this.state.equals(PeerState.DOWN); 
	}
	
	public boolean isUp() {
		return this.state.equals(PeerState.NOT_LOGGED); 
	}
	
	public boolean isLogged() {
		return this.state.equals(PeerState.LOGGED); 
	}
	
	public PeerSpecification getPeerSpec() {
		return peerSpec;
	}

	public PeerState getState() {
		return state;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof PeerEntry) {
			PeerEntry other = (PeerEntry) obj;
			
			return other.getPeerSpec().equals(this.getPeerSpec());
		}
		
		return false;
	}

	public void setState(PeerState state) {
		this.state = state;
	}


	public void setLoginError(String loginError) {
		this.loginError = loginError;
	}


	public String getLoginError() {
		return loginError;
	}
}
