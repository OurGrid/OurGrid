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

import java.util.ArrayList;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.worker.WorkerAcceptanceTestCase;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.business.dao.LocalWorkersDAO;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerComponentContextFactory;

import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;

public class LocalWorkersDAOTest extends TestCase {

	/*
	 * Test method for 'org.ourgrid.refactoring.peer.dao.LocalWorkersDAO.getLocalWorker(String)'
	 */
	public final void testGetLocalWorkerString() {

		//This test case was created in reason of bug described in the jira OG-317 
		WorkerComponent component = EasyMock.createNiceMock(WorkerComponent.class);
		EasyMock.replay(component);
		
		LocalWorkersDAO dao = new LocalWorkersDAO();

		String workerA = "UA";
        String serverA = "SA";
        
        TestContext context = new TestContext(
				new WorkerComponentContextFactory(
						new PropertiesFileParser(WorkerAcceptanceTestCase.PROPERTIES_FILEPATH
						)).createContext());
        
        WorkerSpecification workerSpecA = new WorkerAcceptanceUtil(context).createWorkerSpec(workerA, serverA);
		
		//LocalWorker localWorker = new LocalWorker(workerSpecA, workerSpecA.getServiceID());
		//dao.addLocalWorker(localWorker);
		
		assertNull(dao.getLocalWorker(new ArrayList<IResponseTO>(), 
				workerSpecA.getServiceID().getContainerID().getUserAtServer()));
	}

}
