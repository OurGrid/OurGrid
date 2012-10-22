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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_022_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.common.BrokerLoginResult;
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
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

@ReqTest(reqs="REQ108")
public class Req_108_Test extends PeerAcceptanceTestCase {

	private static final int NUMUSERS = 10;
	private static final int TIMES = 30;
	private PeerComponent component;
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
    private Req_106_Util req_106_Util = new Req_106_Util(getComponentContext());
    private Req_022_Util req_022_Util = new Req_022_Util(getComponentContext());
    
	/**
	 * Verifies if a registered user can log into the Peer, using authentication
	 */
	@ReqTest(test="AT-108.1", reqs="REQ108")
	@Test public void test_AT_108_1_LoginRegisteredUser() throws Exception{		
		//Creating user's account
		String password = "xyz123";
		String serverName = "server1";
		String userName = "user108_1";
		XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);

		//Start the peer
		component = req_010_Util.startPeer();

		//Login into peer and verify if the peer is interested on Broker failure
        String brokerPublicKey = "publicKeyA";
        
        PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
        DeploymentID workerProviderClientID = req_108_Util.login(component, user, brokerPublicKey);
        assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(workerProviderClientID.getServiceID()));

        //Verify current users status
        UserInfo userInfo = new UserInfo(userName, serverName, brokerPublicKey, UserState.LOGGED);
        List<UserInfo> expectedUsersInfo = AcceptanceTestUtil.createList(userInfo);
        req_106_Util.getUsersStatus(expectedUsersInfo);
	}
	
	/**
	 * Verifies if some registered users can log into the Peer, using authentication
	 * @author paulor, giovanni
	 * @date 22/04/2008
	 */
	@ReqTest(test="AT-108.1", reqs="REQ108")
	@Test public void test_AT_108_1_LoginRegisteredUser2() throws Exception{		
		//Start the peer
		component = req_010_Util.startPeer();
		List<UserInfo> usersInfo = new LinkedList<UserInfo>();
		for (int i = 0; i < NUMUSERS; i++) {			
			String password = "xyz123" + i;
			String serverName = "server1";
			String userName = "user108_1" + i;
			XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);
			
			//Login into peer and verify if the peer is interested on Broker failure
			String brokerPublicKey = "publicKeyA" + i;
			
			PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
			ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
			
			PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
			
			DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
			AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
			
			AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
			
			try {
				peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
			} catch (CommuneRuntimeException e) {
				//do nothing - the user is already added.
			}
			
			DeploymentID workerProviderClientID = req_108_Util.login(component, user, brokerPublicKey);
			assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(workerProviderClientID.getServiceID()));
			UserInfo userInfo = new UserInfo(userName, serverName, brokerPublicKey, UserState.LOGGED);
			usersInfo.add(userInfo);
			
		}
        //Verify current users status
        req_106_Util.getUsersStatus(usersInfo);
	}
	
	/**
	 * Verifies if a registered user can log into the Peer, using authentication
	 * 
	 * @author paulor, erick
	 * @date 22/04/2008
	 */
	@ReqTest(test="AT-108.1", reqs="REQ108")
	@Test public void test_AT_108_1_LoginUserWithLongPassword() throws Exception{		
		//Creating user's account
		/*String password = "laskjf aslfkj aslfkj aslfkj aslçdkfj aslkçdfj aslkdfasldkf asdlç asdflçk 2ri [-2ri qwçdklfj asdfu9 qpwokf 2=" +
				"ri0 wpdjiof askldfj aw[-f0i qwpofk qwf qwfi apwofj asçdlfk a[pwof qwkp asdfjkl asf 1t=- 'pufpqwouf qipqwfup " +
				"=i qwer-ifowdifqfk adflçjkasçldfka çfka sdfio 134iŕ -u pfkj qfu -rui dflçk alsdfj ]ÇL LKJ COPA poPOFR " +
				"WLÇKF OÇ qop fçlwejk opwei rpwoadjf AL as apfo a]WFP QWDFOÇJ [P RASOÇFJ ADFIO Q[PWDFIO KLÇQjr-qrieÇKF [PQWOI " +
				"QWOPKF ASDOFJ '03RI ÕÇSDFIJ AOPSDUF POUD fi aF QPSODUF [i 	oip fopsdfjvgklçsdjg klçJ FPOSDFIG ASLKF ÇLKdf[p" +
				"oifk ]sadfkas fjk asdfklrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"ri0 wpdjiof askldfj aw[-f0i qwpofk qwf qwfi apwofj asçdlfk a[pwof qwkp asdfjkl asf 1t=- 'pufpqwouf qipqwfup " +
				"=i qwer-ifowdifqfk adflçjkasçldfka çfka sdfio 134iŕ -u pfkj qfu -rui dflçk alsdfj ]ÇL LKJ COPA poPOFR " +
				"WLÇKF OÇ qop fçlwejk opwei rpwoadjf AL as apfo a]WFP QWDFOÇJ [P RASOÇFJ ADFIO Q[PWDFIO KLÇQjr-qrieÇKF [PQWOI " +
				"QWOPKF ASDOFJ '03RI ÕÇSDFIJ AOPSDUF POUD fi aF QPSODUF [i 	oip fopsdfjvgklçsdjg klçJ FPOSDFIG ASLKF ÇLKdf[p" +
				"oifk ]sadfkas fjk asdfklrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"ri0 wpdjiof askldfj aw[-f0i qwpofk qwf qwfi apwofj asçdlfk a[pwof qwkp asdfjkl asf 1t=- 'pufpqwouf qipqwfup " +
				"=i qwer-ifowdifqfk adflçjkasçldfka çfka sdfio 134iŕ -u pfkj qfu -rui dflçk alsdfj ]ÇL LKJ COPA poPOFR " +
				"WLÇKF OÇ qop fçlwejk opwei rpwoadjf AL as apfo a]WFP QWDFOÇJ [P RASOÇFJ ADFIO Q[PWDFIO KLÇQjr-qrieÇKF [PQWOI " +
				"QWOPKF ASDOFJ '03RI ÕÇSDFIJ AOPSDUF POUD fi aF QPSODUF [i 	oip fopsdfjvgklçsdjg klçJ FPOSDFIG ASLKF ÇLKdf[p" +
				"oifk ]sadfkas fjk asdfklrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"ri0 wpdjiof askldfj aw[-f0i qwpofk qwf qwfi apwofj asçdlfk a[pwof qwkp asdfjkl asf 1t=- 'pufpqwouf qipqwfup " +
				"=i qwer-ifowdifqfk adflçjkasçldfka çfka sdfio 134iŕ -u pfkj qfu -rui dflçk alsdfj ]ÇL LKJ COPA poPOFR " +
				"WLÇKF OÇ qop fçlwejk opwei rpwoadjf AL as apfo a]WFP QWDFOÇJ [P RASOÇFJ ADFIO Q[PWDFIO KLÇQjr-qrieÇKF [PQWOI " +
				"QWOPKF ASDOFJ '03RI ÕÇSDFIJ AOPSDUF POUD fi aF QPSODUF [i 	oip fopsdfjvgklçsdjg klçJ FPOSDFIG ASLKF ÇLKdf[p" +
				"oifk ]sadfkas fjk asdfklrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"ri0 wpdjiof askldfj aw[-f0i qwpofk qwf qwfi apwofj asçdlfk a[pwof qwkp asdfjkl asf 1t=- 'pufpqwouf qipqwfup " +
				"=i qwer-ifowdifqfk adflçjkasçldfka çfka sdfio 134iŕ -u pfkj qfu -rui dflçk alsdfj ]ÇL LKJ COPA poPOFR " +
				"WLÇKF OÇ qop fçlwejk opwei rpwoadjf AL as apfo a]WFP QWDFOÇJ [P RASOÇFJ ADFIO Q[PWDFIO KLÇQjr-qrieÇKF [PQWOI " +
				"QWOPKF ASDOFJ '03RI ÕÇSDFIJ AOPSDUF POUD fi aF QPSODUF [i 	oip fopsdfjvgklçsdjg klçJ FPOSDFIG ASLKF ÇLKdf[p" +
				"oifk ]sadfkas fjk asdfklrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
				"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr";*/
		
		String password = "laskjf aslfkj aslfkj aslfkj as";
		String serverName = "server1";
		String userName = "user1";
		XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);

		//Start the peer
		component = req_010_Util.startPeer();

		//Login into peer and verify if the peer is interested on Broker failure
        String brokerPublicKey = "publicKeyA";
        
        PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
        DeploymentID workerProviderClientID = req_108_Util.login(component, user, brokerPublicKey);
        assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(workerProviderClientID.getServiceID()));

        //Verify current users status
        UserInfo userInfo = new UserInfo(userName, serverName, brokerPublicKey, UserState.LOGGED);
        List<UserInfo> expectedUsersInfo = AcceptanceTestUtil.createList(userInfo);
        req_106_Util.getUsersStatus(expectedUsersInfo);
	}
	
	/**
	 * @author paulor, erick
	 * @date 22/04/2008
	 * @throws Exception
	 */
	@ReqTest(test="AT-108.1", reqs="REQ108")
	@Test public void test_AT_108_1_LoginWithUpperCaseServerName() throws Exception{		
		//Start the peer
		component = req_010_Util.startPeer();
		List<UserInfo> usersInfo = new LinkedList<UserInfo>();
		String password = "xyz123";
		String serverName = "A";
		String userName = "user108_1";
		XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);

		//Login into peer and verify if the peer is interested on Broker failure
		String brokerPublicKey = "publicKeyA";
		
		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
		DeploymentID workerProviderClientID = req_108_Util.login(component, user, brokerPublicKey);
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(workerProviderClientID.getServiceID()));
		UserInfo userInfo = new UserInfo(userName, serverName.toLowerCase(), brokerPublicKey, UserState.LOGGED);
			usersInfo.add(userInfo);
			
        //Verify current users status
        req_106_Util.getUsersStatus(usersInfo);
	}

	/**
	 * Verifies if an unknown user can't log into the Peer, using authentication
	 */
	@ReqTest(test="AT-108.3", reqs="REQ108")
	@Test public void test_AT_108_3_LoginUnknownUser() throws Exception{
        //Start the peer
        component = req_010_Util.startPeer();
        
        //Login into peer with a wrong password and expect the respective message
        String brokerPublicKey = "publicKeyA";
        DeploymentID workerProviderClientID = 
            req_108_Util.wrongLogin(component, "unknown", "unknown", brokerPublicKey, 
                    BrokerLoginResult.UNKNOWN_USER);

        //Verify if the peer is not interested on Broker failure
        assertFalse(peerAcceptanceUtil.isPeerInterestedOnBroker(workerProviderClientID.getServiceID()));
        
        //Verify current users status
        List<UserInfo> usersInfo = new LinkedList<UserInfo>();
        req_106_Util.getUsersStatus(usersInfo);
	}
	
	/**
	 * Verifies if a registered user can log any times into the Peer, using authentication
	 */
	@ReqTest(test="AT-108.4", reqs="REQ108")
	@Test public void test_AT_108_4_LoginAnyTimes() throws Exception{
	    //Creating user's account
        String password = "xyz123";
        String serverName = "server1";
        String userName = "user108_4";
        XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);

        //Start the peer
        String brokerPublicKey = "publicKeyA";
        component = req_010_Util.startPeer();
        
        PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

        //Login into peer three times and verify if the peer is interested on Broker failure
		
		//Creating mocks
		LocalWorkerProviderClient workerProviderClient = getMock(NOT_NICE, LocalWorkerProviderClient.class);
		
		//Setting mocks DeploymentIDs
		DeploymentID workerProviderClientDeploymentID = new DeploymentID(new ContainerID(userName, serverName, BrokerConstants.MODULE_NAME,
				brokerPublicKey), BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
		
		peerAcceptanceUtil.createStub(workerProviderClient, LocalWorkerProviderClient.class, 
				workerProviderClientDeploymentID);
        
		//Getting bound Objects
		LocalWorkerProvider workerProvider = peerAcceptanceUtil.getLocalWorkerProvider();
		LocalWorkerProvider workerProviderProxy = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		
		//Setting expected behavior
		workerProviderClient.loginSucceed(EasyMock.same(workerProvider), LoginResultMatcher.noError());
		workerProviderClient.loginSucceed(EasyMock.same(workerProvider), LoginResultMatcher.noError());
		workerProviderClient.loginSucceed(EasyMock.same(workerProvider), LoginResultMatcher.noError());
        replayActiveMocks();

		//Login the peer
		DeploymentID brokerDeploymentID = new DeploymentID(new ContainerID("broker", "broker", "broker", brokerPublicKey), "broker");
		
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment();
	    AcceptanceTestUtil.setExecutionContext(component, lwpOD, brokerDeploymentID);
        workerProviderProxy.login(workerProviderClient);
        
	    AcceptanceTestUtil.setExecutionContext(component, lwpOD, brokerDeploymentID);
        workerProviderProxy.login(workerProviderClient);
        
	    AcceptanceTestUtil.setExecutionContext(component, lwpOD, brokerDeploymentID);
        workerProviderProxy.login(workerProviderClient);

		//Verifying behavior
        verifyActiveMocks();

		//Verify if the peer is interested on Broker failure
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(workerProviderClientDeploymentID.getServiceID()));

		//Verify current users status
        UserInfo userInfo = new UserInfo(userName, serverName,brokerPublicKey,UserState.LOGGED);
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo);
        req_106_Util.getUsersStatus(usersInfo);
	}
	
	/**
	 * Verifies if a registered user can log any times (more than 3) into the Peer, using authentication
	 * 
	 * @author paulor
	 * @date 23/04/2008
	 */
	@ReqTest(test="AT-108.4", reqs="REQ108")
	@Test public void test_AT_108_4_LoginAnyTimes2() throws Exception{
	    //Creating user's account
        String password = "xyz123";
        String serverName = "server1";
        String userName = "user108_4";
        XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);

        //Start the peer
        String brokerPublicKey = "publicKeyA";
        component = req_010_Util.startPeer();
        
        PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

        //Login into peer three times and verify if the peer is interested on Broker failure
		
		//Creating mocks
		LocalWorkerProviderClient workerProviderClient = getMock(NOT_NICE, LocalWorkerProviderClient.class);
		
		//Setting mocks DeploymentIDs
       
		DeploymentID workerProviderClientDeploymentID = new DeploymentID(new ContainerID(userName, serverName, BrokerConstants.MODULE_NAME,
				brokerPublicKey), BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
		
		AcceptanceTestUtil.publishTestObject(component, workerProviderClientDeploymentID, workerProviderClient,
				LocalWorkerProviderClient.class);
		
		//Getting bound Objects
		LocalWorkerProvider workerProvider = peerAcceptanceUtil.getLocalWorkerProvider();
		LocalWorkerProvider workerProviderProxy = peerAcceptanceUtil.getLocalWorkerProviderProxy();
		
		//Setting expected behavior
		for (int i = 0; i < TIMES; i++) {
			workerProviderClient.loginSucceed(EasyMock.same(workerProvider), LoginResultMatcher.noError());		
		}
		replayActiveMocks();

		//Login the peer
		for (int j = 0; j < TIMES; j++) {
			AcceptanceTestUtil.setExecutionContext(component, peerAcceptanceUtil.getLocalWorkerProviderDeployment(), 
					workerProviderClientDeploymentID);
			workerProviderProxy.login(workerProviderClient);		
		}

		//Verifying behavior
        verifyActiveMocks();

		//Verify if the peer is interested on Broker failure
		assertTrue(peerAcceptanceUtil.isPeerInterestedOnBroker(workerProviderClientDeploymentID.getServiceID()));

		//Verify current users status
        UserInfo userInfo = new UserInfo(userName, serverName,brokerPublicKey,UserState.LOGGED);
        List<UserInfo> usersInfo = AcceptanceTestUtil.createList(userInfo);
        req_106_Util.getUsersStatus(usersInfo);
	}
	
	/**
	 * Verify if a user with a different public key can't log into the Peer
	 */
	@ReqTest(test="AT-108.5", reqs="REQ108")
	@Test public void test_AT_108_5_LoginWrongPublicKey() throws Exception{
	    //Creating user's account
        String password = "xyz123";
        String serverName = "server1";
        String userName = "user108_1";
        XMPPAccount user = req_101_Util.createLocalUser(userName, serverName, password);

        //Start the peer
        component = req_010_Util.startPeer();

        //Login into peer 
        String brokerPublicKey = "publicKeyA";
        
        PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), brokerPublicKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user.getUsername() + "@" + user.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}
		
        DeploymentID workerProviderClientID = req_108_Util.login(component, user, brokerPublicKey);

        //Login into peer with other public key
        String otherPublicKey = "publicKeyB";
        DeploymentID otherWorkerProviderClientID = 
            req_108_Util.wrongLogin(component, userName, serverName, otherPublicKey, 
                    BrokerLoginResult.WRONG_PUBLIC_KEY);

        //Verify if the peer is interested on original Broker failure
	    assertFalse(peerAcceptanceUtil.isPeerInterestedOnBroker(workerProviderClientID.getServiceID()));
        assertFalse(peerAcceptanceUtil.isPeerInterestedOnBroker(otherWorkerProviderClientID.getServiceID()));
        
        //Verify current users status
        UserInfo userInfo = new UserInfo(userName, serverName, brokerPublicKey, UserState.LOGGED);
        List<UserInfo> expectedUsersInfo = AcceptanceTestUtil.createList(userInfo);
        req_106_Util.getUsersStatus(expectedUsersInfo);
	}
}