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
package org.ourgrid;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ourgrid.common.interfaces.to.TrustyCommunityTest;
import org.ourgrid.peer.controller.allocation.DefaultAllocatorTest;
import org.ourgrid.peer.controller.allocation.SamePriorityProcessorTest;
import org.ourgrid.peer.controller.matcher.ExpressionTranslatorTest;
import org.ourgrid.peer.controller.matcher.IntervalTest;
import org.ourgrid.peer.controller.matcher.MatcherTest;
import org.ourgrid.peer.dao.LocalWorkersDAOTest;
import org.ourgrid.peer.dao.RequestDAOTest;
import org.ourgrid.peer.dao.TrustCommunitiesFileManipulatorTest;
import org.ourgrid.peer.to.PriorityTest;

/**
 * since 21/08/2007
 */
public class AllUnitTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Unit tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(LocalWorkersDAOTest.class);
		suite.addTestSuite(RequestDAOTest.class);
		suite.addTestSuite(PriorityTest.class);
		suite.addTestSuite(TrustCommunitiesFileManipulatorTest.class);
		suite.addTestSuite(TrustyCommunityTest.class);
		suite.addTestSuite(ExpressionTranslatorTest.class);
		suite.addTestSuite(IntervalTest.class);
		suite.addTestSuite(MatcherTest.class);
		suite.addTestSuite(DefaultAllocatorTest.class);
		suite.addTestSuite(SamePriorityProcessorTest.class);
		//$JUnit-END$
		return suite;
	}
}
