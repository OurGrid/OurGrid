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
package org.ourgrid.peer.business.controller;

import java.util.Collection;
import java.util.List;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.response.RemoteWorkerProviderRequestWorkersResponseTO;

/**
 * Implements Community Obtainer actions.
 */
public class CommunityObtainerController {
	
	private static CommunityObtainerController instance; 
	
	private CommunityObtainerController() {};
	
	public static CommunityObtainerController getInstance() {
		if (instance == null) {
			instance = new CommunityObtainerController();
		}
		return instance;
	}
	
	public void request(List<IResponseTO> responses, RequestSpecification requestSpec) {
		
		DiscoveryServiceClientDAO dsClientDAO = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		Collection<String> workerProviders = dsClientDAO.getRemoteWorkerProvidersAddress();

		LoggerResponseTO loggerForwardResponse = new LoggerResponseTO(
				"Request "+requestSpec.getRequestId()+": request forwarded to community.", 
				LoggerResponseTO.DEBUG);
		responses.add(loggerForwardResponse);
		
		for (String remoteWorkerProviderID : workerProviders) {

			LoggerResponseTO loggerRequestResponse = new LoggerResponseTO(
					("Request "+ requestSpec.getRequestId() +": requesting workers " +
							"from a remote worker provider ["+ remoteWorkerProviderID +"]."), 
							LoggerResponseTO.DEBUG);

			responses.add(loggerRequestResponse);

			RemoteWorkerProviderRequestWorkersResponseTO requestWorkersResponse = 
				new RemoteWorkerProviderRequestWorkersResponseTO();
			
			requestWorkersResponse.setRemoteWorkerProviderAddress(remoteWorkerProviderID);
			requestWorkersResponse.setRequestSpec(requestSpec);
			
			responses.add(requestWorkersResponse);
			
		}

	}

}