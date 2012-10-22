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
package org.ourgrid.peer.to;

import junit.framework.TestCase;

/**
 * @author
 * since 28/08/2007
 */
public class PriorityTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link org.ourgrid.peer.to.Priority#compareTo(org.ourgrid.peer.to.Priority)}.
	 */
	public final void testCompareTo() {
		
		
		//Different ranges. The ordinal number does not matter
		Priority idle = new Priority(Priority.Range.IDLE, 1);
		Priority localRequest = new Priority(Priority.Range.ALLOC_FOR_LOCAL_REQUEST, 1);
		Priority trustCommunity = new Priority(Priority.Range.ALLOC_FOR_TRUST_COMMUNITY, 1);
		Priority unknownCommunity = new Priority(Priority.Range.ALLOC_FOR_UNKNOWN_COMMUNITY, 1);
		
		//idle < unknowcomm < communit < local
		
		//local request is the greater priority
		assertTrue(localRequest.compareTo(idle) > 0);
		assertTrue(localRequest.compareTo(trustCommunity) > 0);
		assertTrue(localRequest.compareTo(unknownCommunity) > 0);

		assertTrue(idle.compareTo(localRequest) < 0);
		assertTrue(trustCommunity.compareTo(localRequest) < 0);
		assertTrue(unknownCommunity.compareTo(localRequest) < 0);
		
		assertTrue(trustCommunity.compareTo(unknownCommunity) > 0);
		assertTrue(trustCommunity.compareTo(idle) > 0);
		
		assertTrue(idle.compareTo(trustCommunity) < 0);
		assertTrue(unknownCommunity.compareTo(trustCommunity) < 0);
		
		assertTrue(unknownCommunity.compareTo(idle) > 0);
		assertTrue(idle.compareTo(unknownCommunity) < 0);
		
		//Same ranges, diff priority numbers
		Priority idle2 = new Priority(Priority.Range.IDLE, 2);
		Priority localRequest2 = new Priority(Priority.Range.ALLOC_FOR_LOCAL_REQUEST, 2);
		Priority trustCommunity2 = new Priority(Priority.Range.ALLOC_FOR_TRUST_COMMUNITY, 2);
		Priority unknownCommunity2 = new Priority(Priority.Range.ALLOC_FOR_UNKNOWN_COMMUNITY, 2);
		
		assertTrue(idle.compareTo(idle2) > 0);
		assertTrue(localRequest.compareTo(localRequest2) > 0);
		assertTrue(trustCommunity.compareTo(trustCommunity2) > 0);
		assertTrue(unknownCommunity.compareTo(unknownCommunity2) > 0);
		
		//all the same
		
		Priority idle3 = new Priority(Priority.Range.IDLE, 2);
		Priority localRequest3 = new Priority(Priority.Range.ALLOC_FOR_LOCAL_REQUEST, 2);
		Priority trustCommunity3 = new Priority(Priority.Range.ALLOC_FOR_TRUST_COMMUNITY, 2);
		Priority unknownCommunity3 = new Priority(Priority.Range.ALLOC_FOR_UNKNOWN_COMMUNITY, 2);
		
		assertTrue(idle3.compareTo(idle2) == 0);
		assertTrue(localRequest3.compareTo(localRequest2) == 0);
		assertTrue(trustCommunity3.compareTo(trustCommunity2) == 0);
		assertTrue(unknownCommunity3.compareTo(unknownCommunity2) == 0);
	}

}
