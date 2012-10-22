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
import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_110_Util;
import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

@ReqTest(reqs="REQ110")
public class Req_110_Test extends PeerAcceptanceTestCase {

	private static final String COMM_FILE_PATH = "test"+File.separator+"acceptance"+File.separator+"req_110";
	private PeerComponent component;
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_110_Util req_110_Util = new Req_110_Util(getComponentContext());
    
	@Before
	public void setUp() throws Exception{
		super.setUp();
		File trustFile = new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME);
		trustFile.delete();
	}
	
	protected String getRootForTrustFile() {
		return COMM_FILE_PATH;
	}

	@Test public void test_AT_110_1_InexistentSubCommunityFile() throws Exception{

		//Start the peer without trust configuration file
        component = req_010_Util.startPeer();
		
        //Verify if the peer has not subcommunities
        List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        req_110_Util.getTrustStatus(info);
	}
	
	@Test public void test_AT_110_2_BadFormatted() throws Exception{

		copyTrustFile("110-2.xml");
		
		//Start the peer with a mal formed trust configuration file
        component = req_010_Util.startPeer();
		
        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
        
		loggerMock.warn("The trust configuration file is malformed. Ignoring all subcommunities.");
		
		EasyMock.replay(loggerMock);
		
        //Verify if the peer has not subcommunities
		List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        req_110_Util.getTrustStatus(info);
        
        EasyMock.verify(loggerMock);
	}

	@Test public void test_AT_110_3_BadFormatted() throws Exception{
		
		copyTrustFile("110-3.xml");
		//Start the peer with a mal formed trust configuration file
        component = req_010_Util.startPeer();
		
        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
        
		loggerMock.warn("The trust configuration file is malformed. Ignoring all subcommunities.");
		
		EasyMock.replay(loggerMock);
		
        //Verify if the peer has not subcommunities
        List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        req_110_Util.getTrustStatus(info);

        EasyMock.verify(loggerMock);
	}
	
	@Test public void test_AT_110_4_Well_Formatted() throws Exception{
		
		copyTrustFile("110-4.xml");
		//Start the peer with a well formed trust configuration file
        component = req_010_Util.startPeer();
		
        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
        
		loggerMock.info("Trust configuration file loaded with 2 subcommunities.");
		
		EasyMock.replay(loggerMock);
		
        //Verify if the peer has the subcommunities
		TrustyCommunity community1 = new TrustyCommunity.Builder("educationalInstitutions", 1)
												.addEntity("peerE", "keyxptopeerE")
												.addEntity("peerF", "keyxptopeerF")
												.build();
		
		TrustyCommunity community2 = new TrustyCommunity.Builder("lesssecure", 2)
												.addEntity("peerA", "keyxptopeerA")
												.addEntity("peerB", "keyxptopeerB")
												.build();
		
		
        List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        info.add(community1);
        info.add(community2);
        
        req_110_Util.getTrustStatus(info);
        
        EasyMock.verify(loggerMock);

	}
	
	@Test public void test_AT_110_5_Same_Priority() throws Exception{
		
		copyTrustFile("110-5.xml");
		//Start the peer with a well formed trust configuration file
        component = req_010_Util.startPeer();
		
        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
        
		loggerMock.info("Trust configuration file loaded with 2 subcommunities.");
		
		EasyMock.replay(loggerMock);
		
        //Verify if the peer has the subcommunities
		TrustyCommunity community1 = new TrustyCommunity.Builder("A", 1)
												.addEntity("peerA", "keyxptopeerA")
												.build();
		
		TrustyCommunity community2 = new TrustyCommunity.Builder("B", 1)
												.addEntity("peerB", "keyxptopeerB")
												.build();
        
		List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        info.add(community1);
        info.add(community2);
        
        req_110_Util.getTrustStatus(info);
        
        EasyMock.verify(loggerMock);
	}
	
	@Test public void test_AT_110_6_WellFormatted_No_Communities() throws Exception{
		
		copyTrustFile("110-6.xml");
		//Start the peer with a well formed trust configuration file
        component = req_010_Util.startPeer();
		
        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
        
		loggerMock.info("Trust configuration file loaded with 0 subcommunities.");
		
		EasyMock.replay(loggerMock);
		
        //Verify if the peer has not subcommunities
        List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        req_110_Util.getTrustStatus(info);
        
        EasyMock.verify(loggerMock);
	}
	
	@Test public void test_AT_110_7_WellFormatted_Communities_WithoutEntities() throws Exception{
		
		copyTrustFile("110-7.xml");
		//Start the peer with a well formed trust configuration file
        component = req_010_Util.startPeer();
		
        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
        
		loggerMock.info("Trust configuration file loaded with 1 subcommunity.");
		
		EasyMock.replay(loggerMock);
		
        //Verify if the peer has the subcommunity
		TrustyCommunity community1 = new TrustyCommunity.Builder("A", 1)
												.build();
		
		List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        info.add(community1);
        
        req_110_Util.getTrustStatus(info);
        
        EasyMock.verify(loggerMock);
	}


//***************************** Qualification tests **************************************

	
	
	/**
	 * @author Melina
	 * @Date 16/042008
	 *//*
	@Test public void test_verifyTrustyCommunityList2() throws Exception {

		copyTrustFile("110-13.xml");

		component = req_010_Util.startPeer();

		List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();

		req_110_Util.getTrustStatus(info);
	}
	
	*//**
	 * Verify if isn't possible to create a trusty community xml file without sub community name
	 * @author gustavopf
	 * @Date 17/042008
	 *//*
	@Test public void test_AT_110_EmptyTrustyCommunityNames() throws Exception{
		
		copyTrustFile("110-14.xml");
		//Start the peer with a malformed trust configuration file
        component = req_010_Util.startPeer();
        
        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
        
        component.setLogger(loggerMock);
        
        loggerMock.warn("The trust configuration file is malformed. Ignoring all subcommunities.");
        EasyMock.replay(loggerMock);
        
        
        //Verify if the peer has not subcommunities
        List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        
        req_110_Util.getTrustStatus(info);

        EasyMock.verify(loggerMock);
	}*/
	
	/**
	 * Verify if isn't possible to create a trusty community xml file without sub community priority 
	 * @author gustavopf
	 * @Date 17/042008
	 */
	@Test public void test_AT_110_EmptyTrustyCommunityPriority() throws Exception{
		
		copyTrustFile("110-15.xml");
		//Start the peer with a mal formed trust configuration file
        component = req_010_Util.startPeer();
		
        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
        
        component.setLogger(loggerMock);
        
        loggerMock.warn("The trust configuration file is malformed. Ignoring all subcommunities.");
        EasyMock.replay(loggerMock);
        
        //Verify if the peer has not subcommunities
        List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        
        req_110_Util.getTrustStatus(info);
        EasyMock.verify(loggerMock);
	}
	
	/**
	 * Verify if isn't possible to create a trusty community xml file without Peer's public key 
	 * @author gustavopf
	 * @Date 17/042008
	 */
	@Test public void test_AT_110_EmptyTrustyCommunityPeerPublicKey() throws Exception{
		
		copyTrustFile("110-16.xml");
		//Start the peer with a mal formed trust configuration file
        component = req_010_Util.startPeer();
		
        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
        
        component.setLogger(loggerMock);
        
        loggerMock.warn("The trust configuration file is malformed. Ignoring all subcommunities.");
        EasyMock.replay(loggerMock);
        
        //Verify if the peer has not subcommunities
        List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();
        
        req_110_Util.getTrustStatus(info);
        EasyMock.verify(loggerMock);
	}
	
	/**
	 * @author Melina
	 * @Date 16/042008
	 */
	@Test public void testNegativePriorityTrustyCommunityList() throws Exception {

		component = req_010_Util.startPeer();
		
		//This file contains one TrustyCommunity with negative priority. 
		copyTrustFile("110-9.xml");

        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
        
        component.setLogger(loggerMock);
        
        loggerMock.warn("The trust configuration file is malformed. Ignoring all subcommunities.");
        EasyMock.replay(loggerMock);
		
		//Then, the list with TrustyCommunity does not have anyone TrustyCommunity
		List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();

		req_110_Util.getTrustStatus(info);
		
		EasyMock.verify(loggerMock);
	}

	/**
	 * @author Melina
	 * @Date 16/042008
	 */
	@Test public void test_verify_letterPriority_TrustyCommunityList()
			throws Exception {

		//This file contains TrustyCommunity that the priority is a letter .
		copyTrustFile("110-10.xml");

		component = req_010_Util.startPeer();

        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
        
        component.setLogger(loggerMock);
        
        loggerMock.warn("The trust configuration file is malformed. Ignoring all subcommunities.");
        EasyMock.replay(loggerMock);
		
		//Then, the list with TrustyCommunity does not have anyone TrustyCommunity
		List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();

		req_110_Util.getTrustStatus(info);

		EasyMock.verify(loggerMock);
	}
	
	/**
	 * @author gustavopf
	 * @Date 17/042008
	 */
	@Test public void testEmptyTrustyFile()
			throws Exception {

		component = req_010_Util.startPeer();
		
		//This file contains TrustyCommunity that the priority is a letter .
		copyTrustFile("110-8.xml");

        CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
        
        component.setLogger(loggerMock);
        
        loggerMock.warn("The trust configuration file is malformed. Ignoring all subcommunities.");
        EasyMock.replay(loggerMock);
		
		//Then, the list with TrustyCommunity does not have anyone TrustyCommunity
		List<TrustyCommunity> info = new LinkedList<TrustyCommunity>();

		req_110_Util.getTrustStatus(info);

		EasyMock.verify(loggerMock);
	}
	
}
