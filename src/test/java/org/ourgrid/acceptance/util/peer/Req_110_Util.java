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
package org.ourgrid.acceptance.util.peer;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.peer.PeerConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_110_Util extends PeerAcceptanceUtil {

	public Req_110_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Verifies the status of the Trust Communities comparing
	 * it to the <code>TrustyCommunity</code> list passed as parameter
	 * @param communitiesInfo The <code>TrustyCommunity</code> to be verified
	 */
	public void getTrustStatus(List<TrustyCommunity> communitiesInfo) {
	
		//Create client mock
	    PeerStatusProviderClient statusProviderClient = EasyMock.createMock(PeerStatusProviderClient.class);
	
	    //Get status provider
	    PeerStatusProvider statusProvider = getStatusProviderProxy();
	    ObjectDeployment spOD = getStatusProviderObjectDeployment();
	
		DeploymentID pspcID = new DeploymentID(new ContainerID("pspc", "pspcserver", "ClientModule", "psPK"),
				PeerConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME);
		
	    AcceptanceTestUtil.publishTestObject(application, pspcID, statusProviderClient,
	    		PeerStatusProviderClient.class);
	    
	    //Record mock behavior
	    statusProviderClient.hereIsTrustStatus(spOD.getDeploymentID().getServiceID(), communitiesInfo);
	    EasyMock.replay(statusProviderClient);
	    
	    //Requesting users info
	    statusProvider.getTrustStatus(statusProviderClient);
	    
	    //Verifying behavior
	    EasyMock.verify(statusProviderClient);
	}
	

	
}