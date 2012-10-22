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

import static org.easymock.EasyMock.eq;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_035_Util extends PeerAcceptanceUtil {

	public Req_035_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Verifies NOF status, comparing it 
	 * to the <code>NetworkOfFavorsStatus</code> passed as parameter  
	 * @param nofStatus The expected status of the Network of Favors
	 */
	public void getNetworkOfFavoursStatus(NetworkOfFavorsStatus nofStatus) {
	    
		//Create client mock
	    PeerStatusProviderClient statusProviderClient = EasyMock.createMock(PeerStatusProviderClient.class);
	
	    //Get status provider
	    PeerStatusProvider statusProvider = getStatusProviderProxy();
	    ObjectDeployment spOD = getStatusProviderObjectDeployment();
	    
		DeploymentID clientID = new DeploymentID(new ContainerID("client", "server", "ds", "dsPublicKey"),"ds");
	
	    AcceptanceTestUtil.publishTestObject(application, clientID, statusProviderClient,
	    		PeerStatusProviderClient.class);
	    
	    //Record mock behavior
	    statusProviderClient.hereIsNetworkOfFavorsStatus(eq(spOD.getDeploymentID().getServiceID()), eq(nofStatus));
	    EasyMock.replay(statusProviderClient);
	    
	    //Requesting users info
	    statusProvider.getNetworkOfFavorsStatus(statusProviderClient);
	    
	    //Verifying behavior
	    EasyMock.verify(statusProviderClient);
	}
	

	
}