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
package org.ourgrid.acceptance.worker;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.worker.WorkerComponentContextFactory;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;

import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestCase;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;

public class WorkerAcceptanceTestCase extends AcceptanceTestCase {

	public static final String SEP = File.separator;

	public static final String RESOURCE_DIR = "test" + SEP + "acceptance" + SEP + "worker";
	
	public static final String PROPERTIES_FILEPATH = RESOURCE_DIR + SEP + "worker.properties";
	
	protected WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	
	@Before
    public void setUp() throws Exception {
        workerAcceptanceUtil.setUp();
        WorkerDAOFactory.getInstance().reset();
    }
	
	@After
    public void tearDown() throws Exception {
		
		workerAcceptanceUtil.tearDown();
    }

	@Override
	protected TestContext createComponentContext() {
		return new TestContext(
				new WorkerComponentContextFactory(
						new PropertiesFileParser(PROPERTIES_FILEPATH
						)).createContext());
	}


}