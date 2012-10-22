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
package org.ourgrid.acceptance.peer;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class AT_0012 extends PeerAcceptanceTestCase {

	private PeerComponent component;
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());

	/**
	 * Verifies a peer with local users, who had already logged in. It must 
	 * return a list of local users with public key.
	 */
	@ReqTest(test="AT-0012", reqs="")
	@Test public void test_AT_0012_LocalUsers() throws Exception{
		//Start the Peer
		component = req_010_Util.startPeer();
		
		//Create userA and userB
		String userAName = "userA";
		String userBName = "userB";
		String server = "serverTi12";
		String passwordA = "xyz123";
		String passwordB = "xyz456";
		String publicKeyB = "publicKeyB";
		XMPPAccount userA = req_101_Util.createLocalUser(userAName, server, passwordA);
		XMPPAccount userB = req_101_Util.createLocalUser(userBName, server, passwordB);
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment peerControlDeployment = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID(userB.getUsername(), userB.getServerAddress(), publicKeyB), "broker");
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, peerControlDeployment, pccID);
		
		try {
			peerControl.addUser(peerControlClient, userB.getUsername() + "@" + userB.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID(userB.getUsername(), userA.getServerAddress(), ""),"broker");
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, peerControlDeployment, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, userA.getUsername() + "@" + userA.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		
		//Login with userB
		req_108_Util.login(component, userB, publicKeyB);
		
		//Verify users status - the logged user become first and has public key
		UserInfo userInfo2 = new UserInfo(userB.getUsername().toLowerCase(), server.toLowerCase(), publicKeyB, UserState.LOGGED);
		UserInfo userInfo1 = new UserInfo(userA.getUsername().toLowerCase(), server.toLowerCase(), "", UserState.NEVER_LOGGED);
		List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo2, userInfo1);
		req_106_Util.getUsersStatus(usersInfo);
	}	
}