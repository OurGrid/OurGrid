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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.After;
import org.junit.Test;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.status.PeerCompleteStatus;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

@ReqTest(reqs="REQ038")
public class Req_038a_Test extends PeerAcceptanceTestCase {

	private long tickA;
	private long tickB;
	private PeerComponent component;
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    
    @After
	public void tearDown() throws Exception{
		req_010_Util.niceStopPeer(component);
		super.tearDown();
	}
	
	/**
	 * Verifies peer's EntityID, up time and configuration.
	 */
	@ReqTest(test="AT-038A.1", reqs="REQ038A")
	@Test public void test_AT_038a_1_peerCompleteStatus() throws Exception{
		tickA = System.currentTimeMillis();
		
        component = req_010_Util.startPeer();

		//Get remote reference
		PeerStatusProvider statusProvider = peerAcceptanceUtil.getStatusProviderProxy();
		ObjectDeployment stOD = peerAcceptanceUtil.getStatusProviderObjectDeployment();
		
		ServiceID peerEntityID = stOD.getDeploymentID().getServiceID();

		//Create callback mock
		PeerStatusProviderClient statusProviderClient = getMock(NOT_NICE, PeerStatusProviderClient.class);
		
		DeploymentID pspcID = new DeploymentID(new ContainerID("pspc", "pspcServe", "ds", "dsPK"), "ds");
		
		AcceptanceTestUtil.publishTestObject(component, pspcID, statusProviderClient, PeerStatusProviderClient.class);

		//Record Mock behavior
		resetActiveMocks();
		statusProviderClient.hereIsCompleteStatus(EasyMock.eq(peerEntityID), createPeerCompleteStatusMatcher());
		replayActiveMocks();
		
		//Looking up the peer complete status
		statusProvider.getCompleteStatus(statusProviderClient);
		
		verifyActiveMocks();
	}
	
	private PeerCompleteStatus createPeerCompleteStatusMatcher(){
		EasyMock.reportMatcher(new SimplePeerCompleteStatusMatcher());
		return null;	
	}

	private class SimplePeerCompleteStatusMatcher implements IArgumentMatcher{

		/**
		 * Matches PeerCompleteStatus's configurations and if up time <= (tickB - tickA).
		 * Tick b is marked in this method.
		 * Object arg0
		 */
		public boolean matches(Object arg0) {
			
			tickB = System.currentTimeMillis();

			PeerCompleteStatus completeStatus = ((PeerCompleteStatus)arg0); 
			//TODO
			String configuration = completeStatus.getConfiguration();
			
			assertTrue(configuration.contains(getComponentContext().getProperty(XMPPProperties.PROP_USERNAME)));
			assertTrue(configuration.contains(getComponentContext().getProperty(XMPPProperties.PROP_XMPP_SERVERNAME)));
			assertTrue(configuration.contains(getComponentContext().getProperty(ModuleProperties.PROP_CONFDIR)));
			
			assertTrue(completeStatus.getLocalConsumersInfo().isEmpty());
			assertTrue(completeStatus.getLocalWorkersInfo().isEmpty());
			assertEquals(completeStatus.getNetworkOfFavorsStatus(),new NetworkOfFavorsStatus(new HashMap<String, PeerBalance>()));
			assertTrue(completeStatus.getRemoteConsumersInfo().isEmpty());
			assertTrue(completeStatus.getRemoteWorkersInfo().isEmpty());
			assertTrue(completeStatus.getUsersInfo().isEmpty());
			assertTrue(completeStatus.getUpTime() <= (tickB - tickA));
			return true;
		}

		public void appendTo(StringBuffer arg0) {}
	}
	
}
