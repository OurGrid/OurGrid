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
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_037_Util extends PeerAcceptanceUtil {

	public Req_037_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Verifies remote workers' status, comparing it 
	 * to the <code>RemoteWorkerInfo</code> list parameter  
	 * @param remoteWorkers The expected status of the remote workers
	 */
	public void getRemoteWorkersStatus(RemoteWorkerInfo...remoteWorkers) {
		//Create client mock
        PeerStatusProviderClient statusProviderClient = EasyMock.createMock(PeerStatusProviderClient.class);

        // Get status provider
        PeerStatusProvider statusProvider = getStatusProviderProxy();
        ObjectDeployment spOD = getStatusProviderObjectDeployment();
        
        String spcPublicKey = "spcPublicKey";
		DeploymentID spcID = new DeploymentID(new ContainerID("spcUser", "spcServer", "broker", spcPublicKey), "broker");
        
        AcceptanceTestUtil.publishTestObject(application, spcID, statusProviderClient,
        		PeerStatusProviderClient.class);

        // Record mock behavior
        List<RemoteWorkerInfo> expectedResult = AcceptanceTestUtil.createList(remoteWorkers);
        
        AcceptanceTestUtil.setExecutionContext(application, spOD, spcID);
        statusProviderClient.hereIsRemoteWorkersStatus(spOD.getDeploymentID().getServiceID(), expectedResult);
        EasyMock.replay(statusProviderClient);

        // Looking up the local workers of peer
        statusProvider.getRemoteWorkersStatus(statusProviderClient);

        EasyMock.verify(statusProviderClient);
	}
}