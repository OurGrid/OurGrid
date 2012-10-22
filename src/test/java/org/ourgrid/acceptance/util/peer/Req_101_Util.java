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
package org.ourgrid.acceptance.util.peer;

import java.io.IOException;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.deployer.xmpp.XMPPAccount;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class Req_101_Util extends PeerAcceptanceUtil{

	public Req_101_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Creates and adds an user in the peer deployer
	 * 
	 * @param userName 
	 * @param serverName
	 * @param password
	 * @return User XMMPAccount
	 * @throws IOException
	 */
	public XMPPAccount createLocalUser(String userName, String serverName, String password) throws IOException {
	    XMPPAccount user = new XMPPAccount(serverName, userName, password, "5222", "5223");
	    return user;
	}

	/**
	 * Changes an user public key by adding it again 
	 * to the peer deployer
	 * @param user
	 * @param publicKey
	 * @throws IOException
	 */
	public void changeUserPublicKey(XMPPAccount user, String publicKey) throws IOException {
		PeerControl peer = getPeerControl();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		DeploymentID clientID = new DeploymentID(new ContainerID("a", "a", "a", publicKey),"a");
		createStub(peerControlClient, PeerControlClient.class, clientID);
		
	    peer.addUser(peerControlClient, user.getUsername());
	}

	/**
	 * Removers an user form the peer deployer
	 * @param user
	 * @throws IOException
	 */
	public void removeUser(XMPPAccount user) throws IOException {
		PeerControl peer = getPeerControl();
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
	    peer.removeUser(peerControlClient, user.getUsername());
	}
	
}