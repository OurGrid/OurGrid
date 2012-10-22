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
package org.ourgrid.deployer.xmpp;

import org.ourgrid.reqtrace.Req;

/**
 * This class represents the XMPP account data
 */
@Req("REQ100")
public class XMPPAccount {
	   
	private final String serverAddress;
	private final String username;
	private final String userpassword;
	private final String serverPort;
	private final String secureServerPort;
	
	/**
	 * @param address
	 * @param username
	 * @param userpassword
	 * @param serverPort
	 * @param securePort
	 */
	public XMPPAccount(String address, String username, String userpassword, String serverPort, String securePort) {
		this.serverAddress = address;
		this.username = username;
		this.userpassword = userpassword;
		this.serverPort = serverPort;
		this.secureServerPort = securePort;
	}

	/**
	 * @return Returns the serverAddress.
	 */
	public String getServerAddress() {
		return serverAddress;
	}

	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return Returns the userpassword.
	 */
	public String getUserpassword() {
		return userpassword;
	}

	/**
	 * @return Returns the secureServerPort.
	 */
	public String getSecureServerPort() {
		return secureServerPort;
	}

	/**
	 * @return Returns the serverPort.
	 */
	public String getServerPort() {
		return serverPort;
	}
}