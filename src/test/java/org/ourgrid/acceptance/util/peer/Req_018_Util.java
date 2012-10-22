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

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.communication.receiver.RemoteWorkerManagementClientReceiver;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_018_Util extends PeerAcceptanceUtil {

	public Req_018_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Receives a remote worker from a remote worker provider, with the pointed specifications
	 * 
	 * @param component the peer component
	 * @param rwp the remote peer
	 * @param workerSpec the worker specifications
	 * @param workerPublicKey the worker public key
	 * @param brokerPubKey the local consumer public key 
	 * @return the remote worker management object identification
	 */
	public TestStub receiveRemoteWorker(Module component, RemoteWorkerProvider rwp, DeploymentID rwpOID,
			WorkerSpecification workerSpec, String workerPublicKey, String brokerPubKey) {
		return receiveRemoteWorker(component, rwp, rwpOID, workerSpec, workerPublicKey, brokerPubKey, null);
	}

	/**
	 * Receives a remote worker from a remote worker provider, with the pointed specifications
	 * 
	 * @param component the peer component
	 * @param rwp the remote peer
	 * @param workerSpec the worker specifications
	 * @param workerPublicKey the worker public key
	 * @param brokerPubKey the local consumer public key
	 * @param future A request future to be canceled
	 * @return the remote worker management object identification
	 */
	public TestStub receiveRemoteWorker(Module component, RemoteWorkerProvider rwp, DeploymentID rwpOID,
			WorkerSpecification workerSpec, String workerPublicKey, String brokerPubKey, ScheduledFuture<?> future) {
	
		EasyMock.reset(rwp);
		
		//Replace the logger
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		CommuneLogger oldLogger = component.getLogger();
	    component.setLogger(newLogger);
	
	    //Create mocks 
		RemoteWorkerManagement remoteWorker = EasyMock.createMock(RemoteWorkerManagement.class);
		
		//Record mock behavior
		String userName = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String serverName = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		ContainerID accessPointID = new ContainerID(userName, serverName, WorkerConstants.MODULE_NAME, workerPublicKey);
		DeploymentID remoteWorkerOID = new DeploymentID(accessPointID, WorkerConstants.REMOTE_WORKER_MANAGEMENT);
		
		if (future != null) {
			EasyMock.reset(future);
			EasyMock.expect(future.cancel(true)).andReturn(true);
		}
		
		newLogger.debug("Received a worker ["+ remoteWorkerOID.getServiceID() +"] " +
				"from a remote worker provider ["+ rwpOID.getServiceID() + "].");
		
		RemoteWorkerManagementClient rwmc = getRemoteWorkerManagementClient();
				
		RemoteWorkerProviderClient rwpc = getRemoteWorkerProviderClientProxy();
		ObjectDeployment rwpcOD = getRemoteWorkerProviderClientDeployment();
		
		AcceptanceTestUtil.setExecutionContext(component, rwpcOD, rwpOID,
				AcceptanceTestUtil.getCertificateMock(rwpOID));
		
		remoteWorker.workForBroker(rwmc, brokerPubKey);
		
		PeerAcceptanceUtil.replay(rwp, newLogger, remoteWorker);
		if (future != null) {
			PeerAcceptanceUtil.replay(future);
		}
	
		AcceptanceTestUtil.publishTestObject(component, remoteWorkerOID, remoteWorker, RemoteWorkerManagement.class);
		//Deliver worker
		AcceptanceTestUtil.publishTestObject(application, rwpOID, rwp, RemoteWorkerProvider.class);
		
		rwpc.hereIsWorker(rwp, remoteWorkerOID.getServiceID(), workerSpec);
		assertTrue(AcceptanceTestUtil.isInterested(application, remoteWorkerOID.getServiceID(), 
				getRemoteWorkerMonitorDeployment().getDeploymentID()));
		
		assertTrue(isPeerInterestedOnRemoteWorker(remoteWorkerOID.getServiceID()));
		
		notifyRemoteWorkerRecovery(remoteWorkerOID);
		
		//Verify mocks
		PeerAcceptanceUtil.verify(newLogger, rwp, remoteWorker);
		if (future != null) {
			PeerAcceptanceUtil.verify(future);
		}
		
		component.setLogger(oldLogger);
	
		return new TestStub (remoteWorkerOID, remoteWorker);
	}

	public void notifyRemoteWorkerRecovery(DeploymentID remoteWorkerOID) {
		
		ObjectDeployment bcOD = getPeerControlDeployment();
		RemoteWorkerManagement workerMock = (RemoteWorkerManagement) application.getStub(
				remoteWorkerOID.getServiceID(), RemoteWorkerManagement.class);
	    
		// Get peer bound object
		RemoteWorkerManagementClientReceiver remoteWorkerMonitor = getRemoteWorkerMonitor();
	    ObjectDeployment wmOD = getRemoteWorkerMonitorDeployment();
		AcceptanceTestUtil.setExecutionContext(application, wmOD, bcOD.getDeploymentID());
	    
	    remoteWorkerMonitor.doNotifyRecovery(workerMock, remoteWorkerOID);
		
	}
	
	/**
	 * When there's no alive request that matches with a receiving worker,
	 * this worker is disposed.
	 * 
	 * @param component
	 * @param rwp
	 * @param workerSpec
	 * @param workerPublicKey
	 * @param brokerPubKey
	 * @return
	 */
	public RemoteWorkerManagement receiveAndDisposeRemoteWorker(Module component, RemoteWorkerProvider rwp, DeploymentID rwpOID,
			WorkerSpecification workerSpec, String workerPublicKey, String brokerPubKey) {
		
		EasyMock.reset(rwp);
	
		//Replace the logger
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		CommuneLogger oldLogger = component.getLogger();
	    component.setLogger(newLogger);
	
	    //Create mocks 
		RemoteWorkerManagement remoteWorker = EasyMock.createMock(RemoteWorkerManagement.class);
		
		//Record mock behavior
		String userName = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String serverName = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		ContainerID accessPointID = new ContainerID(userName, serverName, WorkerConstants.MODULE_NAME, workerPublicKey);
		DeploymentID remoteWorkerOID = new DeploymentID(accessPointID, WorkerConstants.WORKER);
		
//		AcceptanceTestUtil.publishTestObject(component, remoteWorkerOID, remoteWorker, RemoteWorkerManagement.class);
		
		newLogger.debug("Received a worker ["+ remoteWorkerOID.getServiceID() +"] " +
				"from a remote worker provider ["+ rwpOID.getServiceID() + "].");
		
		newLogger.debug("The remote worker " + remoteWorkerOID.getServiceID() +" does not match any request. Disposing it back " +
				"to its provider: " + rwpOID.getServiceID() + ".");
		
		rwp.disposeWorker(remoteWorkerOID.getServiceID());
		
		RemoteWorkerProviderClient rwpc = getRemoteWorkerProviderClientProxy();
		AcceptanceTestUtil.publishTestObject(component, rwpOID, rwp, RemoteWorkerProvider.class);
		
		EasyMock.replay(rwp);
		EasyMock.replay(newLogger);
		EasyMock.replay(remoteWorker);
	
		//Deliver worker
		rwpc.hereIsWorker(rwp, remoteWorkerOID.getServiceID(), workerSpec);
		assertTrue(AcceptanceTestUtil.isInterested(application, remoteWorkerOID.getServiceID(), 
				getRemoteWorkerMonitorDeployment().getDeploymentID()));
		notifyRemoteWorkerRecovery(remoteWorkerOID);
	
		//Verify mocks
		EasyMock.verify(rwp);
		EasyMock.verify(newLogger);
		EasyMock.verify(remoteWorker);
		
		component.setLogger(oldLogger);
	
		return remoteWorker;
	}
	
}