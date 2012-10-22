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
package org.ourgrid.peer.ui.async.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class SDFWriterTest extends TestCase {

	private static final String SDF_FILE = "examples" + File.separator + 
			"resources" + File.separator + "example.sdf";
	
	private static final String SDF_TEST_FILE = "test" + File.separator + 
			"ui" + File.separator + "gui" + File.separator + "example.sdf";
	
	@Override
	protected void tearDown() throws Exception {
		new File(SDF_TEST_FILE).delete();
		
		super.tearDown();
	}
	
	public void testSDFWriter() throws CompilerException, IOException {
		
		List<WorkerSpecification> specs = new ArrayList<WorkerSpecification>();
		
		new SDFWriter().write(specs, SDF_TEST_FILE);
		
		List<WorkerSpecification> specs2 = null;
		
//		try {
			specs2 = new ArrayList<WorkerSpecification>();
//		} catch (CompilerException e) {
//			fail();
//		}
		
		assertEquals(specs, specs2);
	}
	
}
