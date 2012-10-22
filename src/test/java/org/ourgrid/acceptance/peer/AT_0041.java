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

import java.util.concurrent.ScheduledExecutorService;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.matchers.LoginResultMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class AT_0041 extends PeerAcceptanceTestCase {

    private PeerComponent component;
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
	
    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
	/**
	 * This test contains the following steps:
	 *  1. An user successfully login the peer;
	 *  2. The same user, with the correct password, but from a different location tries to login. Expect the peer to return a login error result to the client;
	 *  3. Verify the user status - Expect the user to be LOGGED;
	 *  4. The same user tries to login again from the same location - Expect the user to login successfully.
     */
 	@ReqTest(test="AT-0041", reqs="REQ108")
	@Test public void test_AT_0041_LoggedUserLoginOnThePeerWithOtherBroker() throws Exception {
 		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user01", "server011", "011011");

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		
		//Login with the user01
		String brokerPubKey = "publicKey1";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID lwpcOID = req_108_Util.login(component, user, brokerPubKey);
		
		//Login with the same user but different location on the objectID
		lwpcOID = req_108_Util.login(component, user.getUsername(), user.getServerAddress(), brokerPubKey, null, false);

		//Verify if the consumer is marked as LOGGED
        UserInfo userInfo = new UserInfo(user.getUsername(), user.getServerAddress(), brokerPubKey, UserState.LOGGED);
        req_106_Util.getUsersStatus(AcceptanceTestUtil.createList(userInfo));
		
		//Login again with the same objectID
        
        LocalWorkerProviderClient workerProviderClient = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
        LocalWorkerProvider workerProvider = peerAcceptanceUtil.getLocalWorkerProvider();
        
        EasyMock.reset(workerProviderClient);
        
        workerProviderClient.loginSucceed(EasyMock.same(workerProvider), LoginResultMatcher.noError());   
        
        EasyMock.replay(workerProviderClient);
        
        AcceptanceTestUtil.setExecutionContext(component, peerAcceptanceUtil.getLocalWorkerProviderDeployment(), lwpcOID);
	    workerProvider.login(workerProviderClient);
        
 	}
}