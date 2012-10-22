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
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.matchers.WorkerCompleteStatusMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerConfiguration;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


public class Req_095_Util extends WorkerAcceptanceUtil {

	public Req_095_Util(ModuleContext context) {
		super(context);
	}

	public void getCompleteStatus(WorkerComponent component, DeploymentID peerID, WorkerStatus status, String playpenDirPath,
			String storageDirPath, long initialTick) {
		//Create callback mock
		WorkerControlClient controlClient = EasyMock.createMock(WorkerControlClient.class);
		EasyMock.reset(controlClient);
		
		if (playpenDirPath == null && storageDirPath == null) {
			playpenDirPath = application.getContext().getProperty(WorkerConfiguration.PROP_PLAYPEN_ROOT);
			storageDirPath = application.getContext().getProperty(WorkerConfiguration.PROP_STORAGE_DIR);
		}
		
		//Record Mock behavior
		controlClient.hereIsCompleteStatus(WorkerCompleteStatusMatcher.eqMatcher(component.getContext(),
				peerID.getContainerID().getUserAtServer(), status, playpenDirPath, storageDirPath, initialTick));
		
		EasyMock.replay(controlClient);
		
		DeploymentID wspcID = new DeploymentID(new ContainerID("wspc", "wspcserver", DiscoveryServiceConstants.MODULE_NAME, "wspcPubKey"), 
		"PEER");
		
		AcceptanceTestUtil.publishTestObject(application, wspcID, controlClient, WorkerControlClient.class);
		
		ObjectDeployment wmOD = getWorkerManagementDeployment();
		AcceptanceTestUtil.setExecutionContext(component, wmOD, wspcID);
		
		//Looking up the worker complete status
		WorkerStatusProvider wsp = getWorkerStatusProvider();
		wsp.getCompleteStatus(controlClient);

		EasyMock.verify(controlClient);
	}
}
