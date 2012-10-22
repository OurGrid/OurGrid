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
package org.ourgrid.acceptance.util.worker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.interfaces.status.WorkerStatusProvider;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_093_Util extends WorkerAcceptanceUtil {

	public Req_093_Util(ModuleContext context) {
		super(context);
	}

	public void getMasterPeer(DeploymentID expectedID) {
		WorkerControlClient wspc = EasyMock.createMock(WorkerControlClient.class);
		wspc.hereIsMasterPeer(expectedID.getServiceID());
		EasyMock.replay(wspc);
		
		WorkerStatusProvider wsp = getWorkerStatusProvider();
		DeploymentID wspcID = new DeploymentID(new ContainerID("wspc", "wspcserver", DiscoveryServiceConstants.MODULE_NAME, "wspcPubKey"), 
		"PEER");
		
		AcceptanceTestUtil.publishTestObject(application, wspcID, wspc, WorkerControlClient.class);
		ObjectDeployment wmOD = getWorkerManagementDeployment();
		AcceptanceTestUtil.setExecutionContext(application, wmOD, wspcID);
		
		wsp.getMasterPeer(wspc);
		
		EasyMock.verify(wspc);
	}

}
