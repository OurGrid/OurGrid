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

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.ourgrid.acceptance.util.JDLCompliantTest;

//@RunWith(Suite.class)  
@RunWith(Categories.class)
@ExcludeCategory(JDLCompliantTest.class)
@SuiteClasses({
	Req_010_Test.class,
	Req_011_Test.class,
	Req_014_Test.class,
	Req_015_Test.class,
	Req_016_Test.class,
	Req_018_Test.class,
	Req_019_Test.class,
	Req_020_Test.class,
	Req_022_Test.class,
	Req_025_Test.class,
	Req_027_Test.class,
	Req_034_Test.class,
	Req_035_Test.class,
	Req_036_Test.class,
	Req_037_Test.class,
	Req_038a_Test.class,
	Req_106_Test.class,
	Req_107_Test.class,
	Req_108_Test.class,
	Req_110_Test.class,
	Req_111_Test.class,
	Req_112_Test.class,
	Req_114_Test.class,
	Req_115_Test.class,
	Req_116_Test.class,
	Req_117_Test.class,
	Req_118_Test.class,
	Req_119_Test.class,
	AT_0004.class,
	AT_0005.class,
	AT_0006.class,
	AT_0007.class,
	AT_0008.class,
	AT_0009.class,
	AT_0010.class,
	AT_0012.class,
	AT_0013.class,
	AT_0014.class,
	AT_0019.class,
	AT_0020.class,
	AT_0021.class,
	AT_0022.class,
	AT_0023.class,
	AT_0024.class,
	AT_0027.class,
	AT_0028.class,
	AT_0029.class,
	AT_0030.class,
	AT_0031.class,
	AT_0032.class,
	AT_0033.class,
	AT_0034.class,
	AT_0035.class,
	AT_0036.class,
	AT_0037.class,
	AT_0038.class,
	AT_0039.class,
	AT_0040.class,
	AT_0041.class,
	AT_0042.class,
	AT_0043.class
})
public class AllPeerAcceptanceTests { }
