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
package org.ourgrid.common.interfaces.to;

import junit.framework.TestCase;

/**
 * since 04/09/2007
 */
public class TrustyCommunityTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link org.ourgrid.common.interfaces.to.TrustyCommunity#containsPeer(java.lang.String)}.
	 */
	public final void testContainsPeer() {
		
		//created in reason of a bug
		
		String ent1 = "entity1";
		String ent1PK = "entityPubKey";
		
		String ent2 = "ent2";
		String ent2PK = "ent2PK";
		
		TrustyCommunity tComm = new TrustyCommunity.Builder("commName", 1)
										.addEntity(ent1, ent1PK)
										.addEntity(ent2, ent2PK)
										.build();
		
		assertTrue(tComm.containsPeer(ent1PK));
		assertTrue(tComm.containsPeer(ent2PK));
		
		assertFalse(tComm.containsPeer("fooEntPK"));
		
	}

}
