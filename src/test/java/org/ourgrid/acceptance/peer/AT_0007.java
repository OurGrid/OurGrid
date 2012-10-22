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

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0007 extends PeerAcceptanceTestCase {

	private PeerComponent component;
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
	
	/**
	 * Verifies if the query is stored in OurGrid Peer and is done only after DiscoveryService Peer recovery.
	 */
	@ReqTest(test="AT-0007", reqs={"REQ011", "REQ020"})
	@Test public void test_AT_0007_QueryAfterDiscoveryServiceRecovery() throws Exception {
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		String brokerPubKey = "publicKeyA";
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
		
		//Client login and request workers before ds recovery
		String localUserPubKey = "localUserPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(component, user, localUserPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, "os = windows AND mem > 256", 3, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), spec);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0007_QueryAfterDiscoveryServiceRecoveryWithJDL() throws Exception {
		
		//Create an user account
		XMPPAccount user = req_101_Util.createLocalUser("user011", "server011", "011011");
		
		//Start peer and set mocks for logger and timer
		component = req_010_Util.startPeer();
		
		String brokerPubKey = "publicKeyA";
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
		
		//Client login and request workers before ds recovery
		String localUserPubKey = "localUserPublicKey";
		DeploymentID lwpcOID = req_108_Util.login(component, user, localUserPubKey);
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
		
		//Client login and request workers after ds recovery - expect OG peer to query ds
		RequestSpecification spec = new RequestSpecification(0, new JobSpecification("label"), 1, buildRequirements(">", 256, "==", "windows"), 3, 0, 0);
		req_011_Util.requestForLocalConsumer(component, new TestStub(lwpcOID, lwpc), spec);
	}
}