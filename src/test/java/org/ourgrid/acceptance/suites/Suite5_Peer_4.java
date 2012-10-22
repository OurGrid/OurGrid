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
import org.ourgrid.acceptance.peer.Req_110_Test;
import org.ourgrid.acceptance.peer.Req_111_Test;
import org.ourgrid.acceptance.peer.Req_112_Test;
import org.ourgrid.acceptance.peer.Req_114_Test;
import org.ourgrid.acceptance.peer.Req_116_Test;
import org.ourgrid.acceptance.peer.Req_117_Test;
import org.ourgrid.acceptance.peer.Req_118_Test;
import org.ourgrid.acceptance.peer.Req_119_Test;
import org.ourgrid.acceptance.peer.Req_L11_Test;

@RunWith(Suite.class)
@SuiteClasses( {
		Req_110_Test.class,
		Req_111_Test.class,
		Req_112_Test.class,
		Req_114_Test.class,
		Req_116_Test.class,
		Req_117_Test.class,
		Req_118_Test.class,
		Req_119_Test.class,
		Req_L11_Test.class

})
public class Suite5_Peer_4 {
}
