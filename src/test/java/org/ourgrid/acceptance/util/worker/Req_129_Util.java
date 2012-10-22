/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
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
import org.ourgrid.common.WorkerLoginResult;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.messages.WorkerLoginMessages;
import org.ourgrid.worker.business.messages.WorkerManagementControllerMessages;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_129_Util extends WorkerAcceptanceUtil {

	public Req_129_Util(ModuleContext context) {
		super(context);
	}
	
	public void loginCompletePreparing(WorkerComponent component, String peerPublicKey,
			DeploymentID peerID, TestStub testStub) {
		verifyLogin(component, peerPublicKey, true, false, WorkerLoginResult.OK,
				peerID, testStub, WorkerStatus.IDLE, false);
	}
	
	public void loginPreparingAlreadyLogged(WorkerComponent component, String peerPublicKey,
			DeploymentID peerID, TestStub testStub) {
		verifyLogin(component, peerPublicKey, true, false, WorkerLoginResult.OK,
				peerID, testStub, WorkerStatus.IDLE, true);
	}

	public void loginCompleteIdle(WorkerComponent component, String peerPublicKey,
			DeploymentID peerID, TestStub testStub) {
		verifyLogin(component, peerPublicKey, false, false, WorkerLoginResult.OK,
				peerID, testStub, WorkerStatus.IDLE, false);
	}
	
	public void loginCompleteOwner(WorkerComponent component, String peerPublicKey,
			DeploymentID peerID, TestStub testStub) {
		verifyLogin(component, peerPublicKey, false, false, WorkerLoginResult.OK,
				peerID, testStub, WorkerStatus.OWNER, false);
	}
	
	public void loginCompleteErrorState(WorkerComponent component, String peerPublicKey,
			DeploymentID peerID, TestStub testStub) {
		verifyLogin(component, peerPublicKey, false, true, WorkerLoginResult.OK,
				peerID, testStub, WorkerStatus.ERROR, false);
	}
	
	public void loginErrorStateAlreadyLogged(WorkerComponent component, String peerPublicKey,
			DeploymentID peerID, TestStub testStub) {
		verifyLogin(component, peerPublicKey, false, true, WorkerLoginResult.OK,
				peerID, testStub, WorkerStatus.ERROR, true);
	}
	
	public void loginAlreadyLoggedInIdle(WorkerComponent component, String peerPublicKey,
			DeploymentID peerID, TestStub testStub) {
		verifyLogin(component, peerPublicKey, false, false,
				WorkerLoginResult.OK, peerID, testStub, WorkerStatus.IDLE, true);
	}
	
	public void loginAlreadyLoggedInOwner(WorkerComponent component, String peerPublicKey,
			DeploymentID peerID, TestStub testStub) {
		verifyLogin(component, peerPublicKey, false, false,
				WorkerLoginResult.OK, peerID, testStub, WorkerStatus.OWNER, true);
	}
	
	public WorkerManagementClient verifyLogin(WorkerComponent component,
			String peerPublicKey, boolean isPreparingState, boolean errorState,
			String loginResultMessage, DeploymentID peerID, TestStub testStub, WorkerStatus status,
			boolean alreadyLogged) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		WorkerManagementClient peerMock = (WorkerManagementClient) testStub.getObject();
		
		WorkerLoginResult result = new WorkerLoginResult(loginResultMessage);
		component.setLogger(newLogger);
		EasyMock.reset(peerMock);
		
		WorkerManagement workerManagement = getWorkerManagement();

		if (alreadyLogged) {
			newLogger.warn("This Worker is already logged in. This message will be ignored.");
		} else {
			if(loginResultMessage.equals(WorkerLoginResult.OK)) {
				newLogger.info(WorkerLoginMessages.getWorkerLoginSucceededMessage());
				if(!isPreparingState) {
					peerMock.statusChanged(status);
				}
			}
			if (errorState) {
				newLogger.warn(WorkerManagementControllerMessages.getMasterPeerTryingToCommandWorkerOnErrorStateMessage());
			}
			
			AcceptanceTestUtil.notifyRecovery(component, testStub.getDeploymentID());
		}

		EasyMock.replay(peerMock);
		EasyMock.replay(newLogger);
		
		if (!peerPublicKey.equals("peerPublicKey")) {
			DeploymentID correctID = testStub.getDeploymentID();
			DeploymentID wrongID = new DeploymentID(new ContainerID(correctID.
					getUserName(), correctID.getServerName(), 
					PeerConstants.MODULE_NAME, peerPublicKey), 
					PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
			
			AcceptanceTestUtil.publishTestObject(component, wrongID, peerMock,
					WorkerManagementClient.class);
			
			AcceptanceTestUtil.setExecutionContext(component, 
					getWorkerManagementDeployment(), wrongID);
		} else {
			testStub.getDeploymentID().setPublicKey(peerPublicKey);
			AcceptanceTestUtil.publishTestObject(component, peerID, peerMock,
					WorkerManagementClient.class);

			AcceptanceTestUtil.setExecutionContext(component,
					getWorkerManagementDeployment(), testStub.getDeploymentID());
		}

		workerManagement.loginSucceeded(peerMock, result);
		
		EasyMock.verify(peerMock);
		EasyMock.verify(newLogger);
		
		component.setLogger(oldLogger);
		
		return peerMock;
	}
}