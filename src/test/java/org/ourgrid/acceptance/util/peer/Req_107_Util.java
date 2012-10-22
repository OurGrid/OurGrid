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

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


public class Req_107_Util extends PeerAcceptanceUtil {

	public Req_107_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Updates a worker spec. 
	 * If the worker is known by the peer, expects the peer to log it.
	 * Otherwise expects the peer to ignore this message.
	 * @param component
	 * @param updateWorkerSpec
	 * @param workerDeploymentID
	 * @param isUnknown
	 */
	public void updateWorkerSpec(PeerComponent component, WorkerSpecification updateWorkerSpec, DeploymentID workerDeploymentID, boolean isUnknown) {
		
		CommuneLogger oldLoggerMock = component.getLogger();
		
		//Create Mocks
		CommuneLogger newLoggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLoggerMock);
		
		//Records mock behavior
		WorkerSpecification newUpdateWorkerSpec = new WorkerSpecification(updateWorkerSpec.getAttributes());
		newUpdateWorkerSpec.removeAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		newUpdateWorkerSpec.removeAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		
		if (isUnknown) {
			newLoggerMock.debug("An unknown Worker has updated its specification. This message was ignored." +
					" Unknown worker public key: [" + workerDeploymentID.getPublicKey() + "]");
		} else {
			newLoggerMock.debug("The Worker [" + workerDeploymentID.getServiceID() + "] updated its specification. Updated attributes: " +
					newUpdateWorkerSpec.getAttributes() + ".");
		}
		
		EasyMock.replay(newLoggerMock);
		
		WorkerManagementClient wspecListener = getWorkerManagementClient();
		
		if (!isUnknown) {
			AcceptanceTestUtil.notifyRecovery(application, workerDeploymentID);
		}
			
		AcceptanceTestUtil.setExecutionContext(component, getWorkerManagementClientDeployment(), workerDeploymentID);
		
		wspecListener.updateWorkerSpec(updateWorkerSpec);
		
		//Verify mock behavior
		EasyMock.verify(newLoggerMock);
		component.setLogger(oldLoggerMock);
	}
	

}