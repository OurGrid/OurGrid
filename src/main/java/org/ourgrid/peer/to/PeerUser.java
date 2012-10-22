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
package org.ourgrid.peer.to;

import org.ourgrid.reqtrace.Req;

/**
 * Represents a user of the Peer Component
 * A <code>PeerUser</code> was previously registered in the Peer
 */
@Req({"REQ022", "REQ038a", "REQ106"})
public class PeerUser implements Comparable<PeerUser>{

	private String username;
	private String XMPPServer;
	private String publicKey;
	private boolean logged;
	
	public PeerUser(String username, String server, String pubKey, boolean logged) {
		this.username = username;
		this.XMPPServer = server;
		this.publicKey = pubKey;
		this.logged = logged;
	}
	
	public String getPublicKey() {
		return publicKey;
	}

	public String getUsername() {
		return username;
	}

	public String getXMPPServer() {
		return XMPPServer;
	}
	
	public boolean isLogged() {
		return this.logged;
	}

	@Override
	public String toString() {
		return username + "@" + XMPPServer + "/" + publicKey;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		
		final PeerUser other = (PeerUser) obj;
		
		if (XMPPServer == null) {
			if (other.XMPPServer != null) {
				return false;
			}
		} else if (!XMPPServer.equals(other.XMPPServer)){
			return false;
		}
		
		if (publicKey == null) {
			if (other.publicKey != null){
				return false;
			}
		} else if (!publicKey.equals(other.publicKey)){
			return false;
		}
		
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)){
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((XMPPServer == null) ? 0 : XMPPServer.hashCode());
		result = prime * result
				+ ((publicKey == null) ? 0 : publicKey.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}
	
	public int compareTo(PeerUser other) {
		String thisUser = username + "@" + XMPPServer + "/" + publicKey;
		String otherUser = other.getUsername() + "@" + other.getXMPPServer() + "/" + other.getPublicKey();
		return thisUser.compareTo(otherUser);
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getLogin() {
		return getUsername() + "@" + getXMPPServer();
	}

}
