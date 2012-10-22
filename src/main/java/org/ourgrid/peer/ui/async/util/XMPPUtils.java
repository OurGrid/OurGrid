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
package org.ourgrid.peer.ui.async.util;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.ourgrid.deployer.xmpp.XMPPAccount;

/**
 * It provides utitility methods to 
 *
 */
public class XMPPUtils {

	/**
	 * Tests the XMPP connection, using the host and port provided.
	 * @param host The host of the XMPP connection.
	 * @param port The port of the XMPP connection.
	 * @throws XMPPException If the connection is not ok. 
	 */
	public void testConnection(String host, int port) throws XMPPException{
		ConnectionConfiguration configuration = new ConnectionConfiguration(host, port);
		XMPPConnection connection = new XMPPConnection(configuration);
		connection.connect();
		connection.disconnect();
	}
	
	/**
	 * Adds a new user to the peer.
	 * @param serverName The user server name.
	 * @param userName The user name.
	 * @param password The user password.
	 * @param port The port.
	 * @param securePort The secure port.
	 * @return An account to the new peer user.
	 * @throws XMPPException If the connection is not ok.
	 */
    public XMPPAccount addUser(String serverName, String userName, 
    		String password, String port, String securePort) throws XMPPException {
    	
    	ConnectionConfiguration configuration = new ConnectionConfiguration(serverName, Integer.parseInt(port));
		XMPPConnection connection = new XMPPConnection(configuration);
    	connection.connect();
    	
    	AccountManager manager = new AccountManager(connection);
    	manager.createAccount(userName, password);
    	
    	connection.disconnect();
    	
    	return new XMPPAccount(serverName, userName, password, port, securePort);

    }

	
}
