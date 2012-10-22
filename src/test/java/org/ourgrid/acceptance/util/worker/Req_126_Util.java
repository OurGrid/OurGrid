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
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.messages.WorkerManagementClientRecoveryControllerMessages;
import org.ourgrid.worker.communication.receiver.RemoteWorkerManagementReceiver;
import org.ourgrid.worker.communication.receiver.WorkerManagementReceiver;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_126_Util extends WorkerAcceptanceUtil {

	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(context);

	public Req_126_Util(ModuleContext context) {
		super(context);
	}

	public TestStub notifyPeerRecoveryAtWorkerWithPeer(
			WorkerComponent component, DeploymentID peerID, DeploymentID workerID) {
		return notifyPeerRecovery(component, peerID, true, true, workerID);
	}

	public TestStub notifyPeerRecoveryAtWorkerWithoutPeer(
			WorkerComponent component, DeploymentID peerID, DeploymentID workerID) {
		return notifyPeerRecovery(component, peerID, false, true, workerID);
	}
	
	/**
	 * Creates a WorkerManagement interface mock, publishes it and notifies its recovery
	 * 
	 * @param component The peer Component
	 * @param peerSpec The workerSpec containing the worker attributes
	 * @param peerPublicKey The worker public key
	 * @return Returns the WorkerManagement OID 
	 */
	public TestStub notifyPeerRecovery(WorkerComponent component, DeploymentID peerID,
			boolean isMasterPeerUp, boolean peerRecoveredIsMasterPeer, DeploymentID workerID) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		EasyMock.reset(newLogger);

		ObjectDeployment workerOD = workerAcceptanceUtil.getWorkerControlDeployment();

		WorkerManagementClient workerManagementClientMock = EasyMock.createMock(
				WorkerManagementClient.class);
		AcceptanceTestUtil.publishTestObject(application, peerID,
				workerManagementClientMock, WorkerManagementClient.class);

		// Get peer bound object
		WorkerManagementReceiver peerMonitor = getPeerMonitor();
		ObjectDeployment peerMonitorOD = getPeerMonitorDeployment();

		AcceptanceTestUtil.setExecutionContext(component, peerMonitorOD,
				workerOD.getDeploymentID().getPublicKey());

		if (!peerRecoveredIsMasterPeer) {
			newLogger.warn("The peer [" + peerID + "] is not the master peer.");
		} else {
			WorkerSpecification workerSpecification = new WorkerSpecification();
			workerSpecification.putAttribute(OurGridSpecificationConstants.SERVERNAME, 
					workerID.getServerName());
			workerSpecification.putAttribute(OurGridSpecificationConstants.USERNAME, 
					workerID.getUserName());
			workerSpecification.putAttribute(WorkerSpecificationConstants.OS, 
					System.getProperty("os.name"));
			newLogger.info(WorkerManagementClientRecoveryControllerMessages.
					getMasterPeerRecoveryMessage(peerID.getServiceID().toString()));
			workerManagementClientMock.workerLogin(peerMonitor, workerSpecification);
		}

		EasyMock.replay(workerManagementClientMock);
		EasyMock.replay(newLogger);

		peerMonitor.doNotifyRecovery(workerManagementClientMock, peerID);

		EasyMock.verify(workerManagementClientMock);
		EasyMock.verify(newLogger);

		component.setLogger(oldLogger);

		TestStub testStub = new TestStub(peerID, workerManagementClientMock);

		return testStub;
	}
	
	/**
	 * Creates a WorkerManagement interface mock, publishes it and notifies its recovery
	 * 
	 * @param component The peer Component
	 * @param peerSpec The workerSpec containing the worker attributes
	 * @param peerPublicKey The worker public key
	 * @return Returns the WorkerManagement OID 
	 */
	public void notifyRemotePeerRecovery(WorkerComponent component) {

		ObjectDeployment workerOD = workerAcceptanceUtil.getWorkerControlDeployment();

		RemoteWorkerManagementClient rwmc = EasyMock.
				createMock(RemoteWorkerManagementClient.class);
		
		EasyMock.replay(rwmc);

		// Get peer bound object
		RemoteWorkerManagementReceiver peerMonitor = getRemoteWorkerManagementReceiver();
		ObjectDeployment peerMonitorOD = getRemotePeerMonitorDeployment();

		AcceptanceTestUtil.setExecutionContext(component, peerMonitorOD,
				workerOD.getDeploymentID().getPublicKey());

		peerMonitor.workerManagementClientIsUp(rwmc, null);

		EasyMock.verify(rwmc);
	}

}
