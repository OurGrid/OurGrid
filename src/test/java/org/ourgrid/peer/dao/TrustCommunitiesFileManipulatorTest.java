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
package org.ourgrid.peer.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.dao.trust.TrustCommunitiesFileManipulator;

/**
 * since 23/08/2007
 */
public class TrustCommunitiesFileManipulatorTest extends TestCase {

	private static final String COMM_FILE_PATH = "test"+File.separator+"acceptance"+File.separator+"req_110";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public final void testGetCommunities(){
		
		TrustyCommunity community1 = new TrustyCommunity.Builder("educationalInstitutions", 1)
			.addEntity("peerE", "keyxptopeerE")
			.addEntity("peerF", "keyxptopeerF")
			.build();

		TrustyCommunity community2 = new TrustyCommunity.Builder("lesssecure", 2)
			.addEntity("peerA", "keyxptopeerA")
			.addEntity("peerB", "keyxptopeerB")
			.build();
		
		List<TrustyCommunity> expectedComm = new LinkedList<TrustyCommunity>();
		expectedComm.add(community1);
		expectedComm.add(community2);
		
		
		TrustCommunitiesFileManipulator fileManip = new TrustCommunitiesFileManipulator();
		
		List<TrustyCommunity> resultComm = 
			fileManip.getCommunities(new ArrayList<IResponseTO>(), new File(COMM_FILE_PATH+File.separator+"110-4.xml"));
		
		assertEquals(2, resultComm.size());
		
		assertTrue(resultComm.contains(community1));
		assertTrue(resultComm.contains(community2));
		assertEquals(expectedComm, resultComm);
	}
	
	public final void testGetCommunitiesSort(){
		
		//same priority case
		TrustyCommunity community1 = new TrustyCommunity.Builder("A", 1)
			.addEntity("peerA", "keyxptopeerA")
			.build();

		TrustyCommunity community2 = new TrustyCommunity.Builder("B", 1)
			.addEntity("peerB", "keyxptopeerB")
			.build();
		
		
		List<TrustyCommunity> expectedComm = new LinkedList<TrustyCommunity>();
		expectedComm.add(community2);
		expectedComm.add(community1);
		
		TrustCommunitiesFileManipulator fileManip = new TrustCommunitiesFileManipulator();
		List<TrustyCommunity> resultComm = 
			fileManip.getCommunities(new ArrayList<IResponseTO>(), new File(COMM_FILE_PATH+File.separator+"110-5.xml"));
		
		assertEquals(2, resultComm.size());
		
		assertTrue(resultComm.contains(community1));
		assertTrue(resultComm.contains(community2));
		
		//sort expected list. The comparison rules are: first priority and after name 
		Collections.sort(expectedComm);
		
		assertEquals(expectedComm, resultComm);
	}
	
	public final void testFormatError(){
		
		List<TrustyCommunity> expectedComm = new LinkedList<TrustyCommunity>();
		
		TrustCommunitiesFileManipulator fileManip = new TrustCommunitiesFileManipulator();
		List<TrustyCommunity> resultComm = 
			fileManip.getCommunities(new ArrayList<IResponseTO>(), new File(COMM_FILE_PATH+File.separator+"110-2.xml"));
		
		assertEquals(0, resultComm.size());
		assertEquals(expectedComm, resultComm);
	}

}
