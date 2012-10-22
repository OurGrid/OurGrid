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
package org.ourgrid.system;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllSystemTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("System tests");
		//$JUnit-BEGIN$
//		suite.addTestSuite(BugTests.class);//+
//		suite.addTestSuite(MygridBasicActionsTest.class);//-
//		suite.addTestSuite(PutGetSystemTest.class);//-
//		suite.addTestSuite(StartInOrderSystemTest.class);//+-
//		suite.addTestSuite(StartOutOfOrderSystemTest.class);//+-
//		suite.addTestSuite(StoreSystemTest.class);//-

		//$JUnit-END$
		return suite;
	}

}
