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
package org.ourgrid.common.interfaces.to;

import java.io.Serializable;

import org.ourgrid.reqtrace.Req;

/**
 * Stores information about a user of the Peer
 */
@Req({"REQ022", "REQ106","REQ38a"})
public class UserInfo implements Serializable, Comparable<UserInfo>{

	private static final long serialVersionUID = 1L;
	
	private String username;
	private String XMPPServer;
	private String publicKey;
	private UserState status;
	
	/**
	 * @param username
	 * @param server
	 * @param pubKey
	 * @param status
	 */
	public UserInfo(String username, String server, String pubKey, UserState status) {
		this.username = username;
		XMPPServer = server;
		this.publicKey = pubKey;
		this.status = status;
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

	public UserState getStatus() {
		return status;
	}

	public void setStatus(UserState status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return username + "@" + XMPPServer + ":" + status + "/" + publicKey;
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( !(obj instanceof UserInfo) )
			return false;
		final UserInfo other = (UserInfo) obj;

		if ( !this.username.equals( other.username ) )
			return false;
		if ( !this.XMPPServer.equals( other.XMPPServer ) )
			return false;
		if ( !(this.publicKey.equals( other.publicKey )) )
			return false;
		if ( !(this.status.equals( other.status )) )
			return false;
		return true;
	}

	public int compareTo(UserInfo other) {
		if (!this.getUsername().equals(other.getUsername())) {
			return this.getUsername().compareTo(other.getUsername());
		}
		return this.getXMPPServer().compareTo(other.getXMPPServer());
	}
}
