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
package org.ourgrid.peer.business.controller.actions;

import java.io.Serializable;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.dao.PeerDAOFactory;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DiscoveryServiceUpdateAction implements RepeatedAction{

	public void run(Serializable handler, ServiceManager serviceManager) {
		
		String dsAddress = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO().getAliveDiscoveryServiceAddress();
		
		DiscoveryService ds = serviceManager.getStub(ServiceID.parse(dsAddress), DiscoveryService.class);
		
		int dsRequestSize = serviceManager.getContainerContext().parseIntegerProperty(PeerConfiguration.PROP_DS_REQUEST_SIZE);
		
		if (ds != null) {
			DiscoveryServiceClient dsClient = (DiscoveryServiceClient) serviceManager.getObjectDeployment(
					PeerConstants.DS_CLIENT).getObject();
			ds.getRemoteWorkerProviders(dsClient, dsRequestSize);
			ds.getDiscoveryServices(dsClient);
		}
	}

}
