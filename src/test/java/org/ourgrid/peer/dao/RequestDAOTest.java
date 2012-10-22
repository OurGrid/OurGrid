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

import java.util.List;

import junit.framework.TestCase;

import org.ourgrid.acceptance.peer.PeerAcceptanceTestCase;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.peer.PeerComponentContextFactory;
import org.ourgrid.peer.business.dao.RequestDAO;
import org.ourgrid.peer.to.LocalConsumer;
import org.ourgrid.peer.to.Request;

import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;

public class RequestDAOTest extends TestCase {

	
	protected PeerAcceptanceUtil peerAcceptanceUtil = new PeerAcceptanceUtil(createComponentContext());
	
	protected TestContext createComponentContext() {
		return new TestContext(
				new PeerComponentContextFactory(
						new PropertiesFileParser(PeerAcceptanceTestCase.PEER_PROP_FILEPATH
						)).createContext());
	}

	
	/**
	 * This method was created to assert a characteristic on RequestDAO method
	 * getRunningRequests.
	 * The tested method must always return the requests stored by the DAO in
	 * reverse order, that is, the newer requests come first in the list, while
	 * the older ones are the last.
	 * @throws ProcessorStartException 
	 * @throws CommuneNetworkException 
	 */
	public final void testGetRunningRequests() throws CommuneNetworkException, ProcessorStartException {
		// Creating the Requests DAO
		RequestDAO requestDAO = new RequestDAO();
		
		// Creating LocalWorkerProviderClient mock
		DeploymentID lwpcID = new DeploymentID(new ContainerID("userName", "serverName", "moduleName", "pubKey"), "objName");
		
		
		// Creating request specifications
		RequestSpecification spec1 = new RequestSpecification(0, new JobSpecification("label"), 123, "", 1, 0, 0);
		RequestSpecification spec2 = new RequestSpecification(0, new JobSpecification("label"), 456, "", 1, 0, 0);
		
		// Creating local consumer mock
		LocalConsumer consumer = new LocalConsumer();
		
		// Creating requests on the DAO
		Request request1 = requestDAO.createRequest(lwpcID.toString(), lwpcID.getPublicKey(), spec1, consumer);
		Request request2 = requestDAO.createRequest(lwpcID.toString(), lwpcID.getPublicKey(), spec2, consumer);
		
		// Getting all running requests
		List<Request> runningRequests = requestDAO.getRunningRequests();
		
		// Verifying if the last request done if the first in the list
		Request firstRequest = runningRequests.get(0);
		
		assertTrue(firstRequest.equals(request2));
		assertFalse(firstRequest.equals(request1));
	}

}
