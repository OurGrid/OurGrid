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
package org.ourgrid.acceptance.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.ourgrid.acceptance.peer.Req_016_Test;
import org.ourgrid.acceptance.peer.Req_018_Test;
import org.ourgrid.acceptance.peer.Req_019_Test;
import org.ourgrid.acceptance.peer.Req_020_Test;
import org.ourgrid.acceptance.peer.Req_022_Test;
import org.ourgrid.acceptance.peer.Req_025_Test;
import org.ourgrid.acceptance.peer.Req_027_Test;
import org.ourgrid.acceptance.peer.Req_034_Test;

@RunWith(Suite.class)  
@SuiteClasses({  
			  Req_016_Test.class,
			  Req_018_Test.class,
			  Req_019_Test.class,
			  Req_020_Test.class,
			  Req_022_Test.class,
			  Req_025_Test.class,
			  Req_027_Test.class,
			  Req_034_Test.class
              })  
public class Suite3_Peer_2 {
}
