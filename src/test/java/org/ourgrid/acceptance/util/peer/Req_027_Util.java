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
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.to.PeerBalance;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


public class Req_027_Util extends PeerAcceptanceUtil {

	
	public Req_027_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Reports a replica accounting without verifying any behavior.
	 * Mainly used for input validation tests.
	 * @param brokerPublicKey The consumer public key.
	 * @param replicaAccounting The replica accounting to be reported
	 */
	public void reportReplicaAccounting(PeerComponent component, GridProcessAccounting replicaAccounting, DeploymentID brokerID) {
		LocalWorkerProvider localWorkerProvider = getLocalWorkerProvider();
		
//		AcceptanceTestUtil.publishTestObject(component, brokerID, EasyMock.createMock(LocalWorkerProviderClient.class), LocalWorkerProviderClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, getLocalWorkerProviderDeployment(), brokerID);
		
		localWorkerProvider.reportReplicaAccounting(replicaAccounting);
	}
	
	/**
	 * Reports a local replica accounting.
	 * @param component The peer component
	 * @param brokerID The objectID for the <code>LocalWorkerProvider</code> interface of the consumer.
	 * @param replicaAccounting The replica accounting to be reported
	 */
	public void reportReplicaAccounting(PeerComponent component, DeploymentID brokerID, 
			GridProcessAccounting replicaAccounting) {
		reportReplicaAccounting(component, brokerID, replicaAccounting, false);
	}

	/**
	 * Reports a replica accounting.
	 * @param component The peer component
	 * @param brokerID The objectID for the <code>LocalWorkerProvider</code> interface of the consumer.
	 * @param replicaAccounting The replica accounting to be reported
	 * @param localWorker True if the worker is a local worker, false otherwise.
	 */
	public void reportReplicaAccounting(PeerComponent component, DeploymentID brokerID, 
			GridProcessAccounting replicaAccounting, boolean localWorker) {

//		RequestSpec requestSpec = replicaAccounting.getRequestSpec();
		Double cpu = replicaAccounting.getAccountings().getCPUTime();
		Double data = replicaAccounting.getAccountings().getData();
		boolean aborted = replicaAccounting.getState().equals(GridProcessState.ABORTED);

		//Changes temporarily the logger mock
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		// Record mock
		String abortedStr = (aborted) ? "n aborted " : " ";
		String localWorkerStr = (localWorker) ? "local " : "";
		newLogger.debug("Received a" + abortedStr + "replica accounting from the user [" + brokerID.getServiceID() + 
				"] referring to the " + localWorkerStr + "worker [" + replicaAccounting.getWorkerID() + "], on request " + 
				replicaAccounting.getRequestId() + ": cpu=" + cpu + ", data=" + data + ".");

		EasyMock.replay(newLogger);

		LocalWorkerProvider localWorkerProvider = getLocalWorkerProvider();
		AcceptanceTestUtil.setExecutionContext(component, getLocalWorkerProviderDeployment(), brokerID);
		
		localWorkerProvider.reportReplicaAccounting(replicaAccounting);

		component.setLogger(oldLogger);
	}

	/**
	 * Reports work accountings without verifying any behavior.
	 * Mainly used for input validation tests.
	 * @param workerPublicKey The worker public key
	 * @param workAccountings The work accountings to be reported
	 */
	public void reportWorkAccounting(PeerComponent component, DeploymentID workerID, WorkAccounting... workAccountings) {

		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		AcceptanceTestUtil.setExecutionContext(component, getWorkerManagementClientDeployment(), workerID);
		workerManagementClient.reportWorkAccounting(AcceptanceTestUtil.createList(workAccountings));
	}

	/**
	 * Report work accountings
	 * @param peerComponent The peer component
	 * @param workAccounting The work accounting to be reported
	 * @param workerOID The DeploymentID for <code>WorkerManagement</code> 
	 * interface of the worker
	 */
	public void reportWorkAccounting(PeerComponent peerComponent, WorkAccounting workAccounting, DeploymentID workerOID) {
		//Changes temporarily the logger mock
		CommuneLogger logger = peerComponent.getLogger();
		EasyMock.reset(logger);
		
		//record mock behavior
		double cpuTime = workAccounting.getAccountings().getAttribute(PeerBalance.CPU_TIME);
		double dataSize = workAccounting.getAccountings().getAttribute(PeerBalance.DATA);
		if(workAccounting.getConsumerPeerDN() != null && cpuTime > 0 && dataSize >= 0) {
			logger.debug("Received a work accounting from the worker [" + workerOID.getServiceID() + "] " +
					"referring to the consumer with certificate DN: " + workAccounting.getConsumerPeerDN() + ". " +
					"cpu=" + workAccounting.getAccountings().getAttribute(PeerBalance.CPU_TIME) + ", " +
					"data=" + workAccounting.getAccountings().getAttribute(PeerBalance.DATA) + ".");
		}
		
		EasyMock.replay(logger);
		
		//Report work accountings
		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), workerOID);
		workerManagementClient.reportWorkAccounting(AcceptanceTestUtil.createList(workAccounting));
		
		//Verify logger mock
		EasyMock.verify(logger);
		EasyMock.reset(logger);
		
	}
	
	public void reportWorkAccountingLocal(PeerComponent peerComponent, WorkAccounting workAccounting,
			DeploymentID workerManagOID) {
		CommuneLogger logger = peerComponent.getLogger();
		EasyMock.reset(logger);

		logger.warn("Ignoring a work accounting from the worker ["+workerManagOID.getServiceID()+"] " +
				"referring to this local peer as the consumer");
		
		EasyMock.replay(logger);
		
		//Report work accountings
		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), workerManagOID);
		workerManagementClient.reportWorkAccounting(AcceptanceTestUtil.createList(workAccounting));

		//Verify logger mock
		EasyMock.verify(logger);
		EasyMock.reset(logger);

	}

	public void reportWorkAccoutingWithoutReceivedRemoteWorkProvider(PeerComponent peerComponent, WorkAccounting workAccounting, 
			DeploymentID workerOID) {
		//Changes temporarily the logger mock
		CommuneLogger logger = peerComponent.getLogger();
		EasyMock.reset(logger);
		
		//record mock behavior
		double cpuTime = workAccounting.getAccountings().getAttribute(PeerBalance.CPU_TIME);
		double dataSize = workAccounting.getAccountings().getAttribute(PeerBalance.DATA);
		if(workAccounting.getConsumerPeerDN() != null && cpuTime > 0 && dataSize >= 0) {
			logger.debug("Received a work accounting from the worker [" + workerOID.getServiceID() + "] " +
					"referring to the consumer with certificate DN: " + workAccounting.getConsumerPeerDN() + ". " +
					"cpu=" + cpuTime + ", " + "data=" + dataSize + ".");
		}
		
		String remotePeerDN = workAccounting.getConsumerPeerDN();
		
		logger.warn("The remote peer with certificate subject DN: " + remotePeerDN + " is not received.");
		
		EasyMock.replay(logger);
		
		//Report work accountings
		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), workerOID);
		workerManagementClient.reportWorkAccounting(AcceptanceTestUtil.createList(workAccounting));
		
		//Verify logger mock
		EasyMock.verify(logger);
		EasyMock.reset(logger);
	}
	
	public void reportWorkAccoutingWithIllegalCPUTime(PeerComponent peerComponent, WorkAccounting workAccounting,
			DeploymentID workerManagOID, String publicKey) {
		//Changes temporarily the logger mock
		CommuneLogger logger = peerComponent.getLogger();
		EasyMock.reset(logger);
		
		//record mock behavior
		logger.warn("Ignoring a work accounting from the worker ["+workerManagOID.getServiceID()+"], " +
				"referring to the consumer with public key: " + publicKey + ", because the CPU accounting must be positive.");
		
		EasyMock.replay(logger);
		
		//Report work accountings
		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), workerManagOID);
		workerManagementClient.reportWorkAccounting(AcceptanceTestUtil.createList(workAccounting));
		
		//Verify logger mock
		EasyMock.verify(logger);
		EasyMock.reset(logger);
	}
	
	public void reportWorkAccoutingWithIllegalData(PeerComponent peerComponent, WorkAccounting workAccounting,
			DeploymentID workerManagOID, String publicKey) {
		
		//Changes temporarily the logger mock
		CommuneLogger logger = peerComponent.getLogger();
		EasyMock.reset(logger);
		
		//record mock behavior
		logger.warn("Ignoring a work accounting from the worker ["+workerManagOID.getServiceID()+"], " +
				"referring to the consumer with public key: " + publicKey + ", because the DATA accounting must not be negative.");
		
		EasyMock.replay(logger);
		
		//Report work accountings
		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), workerManagOID);
		workerManagementClient.reportWorkAccounting(AcceptanceTestUtil.createList(workAccounting));
		
		//Verify logger mock
		EasyMock.verify(logger);
		EasyMock.reset(logger);
	}
	
	public void reportWorkAccoutingWithoutConsumer(PeerComponent peerComponent, WorkAccounting workAccounting,
			DeploymentID workerManagOID) {
		//Changes temporarily the logger mock
		CommuneLogger logger = peerComponent.getLogger();
		EasyMock.reset(logger);
		
		//record mock behavior
		logger.warn("Ignoring a work accounting with no consumer from worker: ["+workerManagOID.getServiceID()+"]");
		
		EasyMock.replay(logger);
		
		//Report work accountings
		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), workerManagOID);
		workerManagementClient.reportWorkAccounting(AcceptanceTestUtil.createList(workAccounting));
		
		//Verify logger mock
		EasyMock.verify(logger);
		EasyMock.reset(logger);
		
	}

}