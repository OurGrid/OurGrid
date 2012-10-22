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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
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
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

@ReqTest(reqs="REQ106")
public class Req_106_Test extends PeerAcceptanceTestCase {

	private PeerComponent component;
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
    
	@Before
	public void setUp() throws Exception {
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.delete();
		super.setUp();
		component = req_010_Util.startPeer();
	} 
	
	@After
	public void tearDown() throws Exception {
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.delete();
		super.tearDown();
	}

	/**
	 * Verifies a peer without registered users. 
	 * It must return an empty list of users information.
	 */
	@ReqTest(test="AT-106.1", reqs="REQ106")
	@Test public void test_AT_106_1_PeerWithoutRegisteredUsers() throws Exception{
        List<UserInfo> emptyList = new LinkedList<UserInfo>();
        req_106_Util.getUsersStatus(emptyList);
	}
	
	/**
	 * Verifies a peer with registered users, who never logged in.
	 * It must return a list of local users without public key.
	 */
	@ReqTest(test="AT-106.2", reqs="REQ106")
	@Test public void test_AT_106_2_PeerWithSomeUsers() throws Exception{
		//Create users
		XMPPAccount user1 = req_101_Util.createLocalUser("user1.xptolab.org", "xmpp.xptolab.org", "");
		XMPPAccount user3 = req_101_Util.createLocalUser("user3.xptolab.org", "xmpp.xptolab.org", "");
		XMPPAccount user2 = req_101_Util.createLocalUser("user2.xptolab.org", "xmpp.xptolab.org", "");
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), "broker1PubKey");
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient2 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID2 = new DeploymentID(new ContainerID("pcc2", "broker2", "broker"), "broker2PubKey");
		AcceptanceTestUtil.publishTestObject(component, pccID2, peerControlClient2, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID2);
		
		try {
			peerControl.addUser(peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		PeerControlClient peerControlClient3 = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID3 = new DeploymentID(new ContainerID("pcc3", "broker3", "broker"), "broker3PubKey");
		AcceptanceTestUtil.publishTestObject(component, pccID3, peerControlClient3, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID3);
		
		try {
			peerControl.addUser(peerControlClient3, user3.getUsername() + "@" + user3.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		//Get the current users status
        UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), "", UserState.NEVER_LOGGED);
        UserInfo userInfo2 = new UserInfo(user2.getUsername(), user2.getServerAddress(), "", UserState.NEVER_LOGGED);
        UserInfo userInfo3 = new UserInfo(user3.getUsername(), user3.getServerAddress(), "", UserState.NEVER_LOGGED);
		
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1, userInfo2, userInfo3);
		
		req_106_Util.getUsersStatus(usersInfo);
	}
	
/*	*//**
	 * 
	 * It must return a list of local users without public key.
	 * @author erick, paulor, giovanni
	 * @date 11/04/2008
	 * @throws Exception
	 * 
	 *//*
	@ReqTest(test="AT-106.2", reqs="REQ106")
	@Test public void test_AT_106_2_PeerWithOneUserAndEmptyPublicKey() throws Exception{
		//Create users
		XMPPAccount user1 = req_101_Util.createLocalUser("user1a.xptolab.org", "xmpp.xptolab.org", "");
		
		 //login user
        Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
        String user1PubKey = "";
        
        PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID(user1.getUsername(), user1.getServerAddress(), "broker"), user1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID, user1PubKey);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
        req_108_Util.login(component, user1, user1PubKey);
		
		//Get the current users status
        UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), user1PubKey, UserState.LOGGED);
        
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
        
		req_106_Util.getUsersStatus(usersInfo);
	}*/
	
	/**
	 * 
	 * @author erick, paulor, giovanni
	 * @date 14/04/2008
	 * @throws Exception
	 */
	@ReqTest(test="AT-106.2", reqs="REQ106")
	@Test public void test_AT_106_2_PeerWithOneLoggedUserWithNullPublicKey() throws Exception{
		//Create users
		XMPPAccount user1 = req_101_Util.createLocalUser("user1b.xptolab.org", "xmpp.xptolab.org", "");
		
		 //login user
        Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
        String user1PubKey = "user1PubKey";
        
        PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), user1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
        req_108_Util.login(component, user1, user1PubKey);
		
		//Get the current users status
        UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), 
        		user1PubKey, UserState.LOGGED);
        
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
        
		req_106_Util.getUsersStatus(usersInfo);
	}
	
	/**
	 * 
	 * @author erick, paulor, giovanni
	 * @date 14/04/2008
	 * @throws Exception
	 */
	@ReqTest(test="AT-106.2", reqs="REQ106")
	@Test public void test_AT_106_2_PeerWithOneLoggedUser() throws Exception{
		//Create users
		XMPPAccount user1 = req_101_Util.createLocalUser("user1c.xptolab.org", "xmpp.xptolab.org", "");
		
		 //login user
        Req_108_Util req_108_Util = new Req_108_Util(getComponentContext()); 
        String publicKey = "publicKey";
        
        PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), publicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
        req_108_Util.login(component, user1, publicKey);
		
		//Get the current users status
        UserInfo userInfo1 = new UserInfo(user1.getUsername(), user1.getServerAddress(), publicKey, UserState.LOGGED);
        
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo1);
        
		req_106_Util.getUsersStatus(usersInfo);
	}
}