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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_025_Util extends PeerAcceptanceUtil {
	
	public Req_025_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Changes a local worker's status to allocated for broker
	 * @param peerComponent The peer component
	 * @param lwpcID The objectID of the consumer's LocalWorkerProviderClient interface
	 * @param wmOID The objectID of the worker's WorkerManagement interface
	 * @param workerSpecA The WorkerSpec containing the worker's attributes
	 * @param requestSpec The RequestSpec containing request info
	 * @return The Worker mock created
	 */
	public TestStub changeWorkerStatusToAllocatedForBroker(PeerComponent peerComponent, DeploymentID lwpcID, 
			DeploymentID wmOID, WorkerSpecification workerSpecA, 
			RequestSpecification requestSpec) {
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcID);
	
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		peerComponent.setLogger(newLogger);
		
		Worker worker = EasyMock.createMock(Worker.class);
		DeploymentID workerDID = new DeploymentID("a@b/d", "e");
		workerDID.setPublicKey(wmOID.getPublicKey());
		
		newLogger.info("Worker <" + wmOID.getContainerID() + "> is now IN_USE");
		newLogger.info("Giving Worker <" + wmOID.getContainerID() + "> to <" + lwpcID.getContainerID() + ">");
		
		EasyMock.reset(lwpc);
		
		AcceptanceTestUtil.publishTestObject(application, workerDID, worker, Worker.class);
		
		lwpc.hereIsWorker(workerDID.getServiceID(), workerSpecA, requestSpec);
		
		EasyMock.replay(lwpc);
		EasyMock.replay(newLogger);
		EasyMock.replay(worker);
	
		changeWorkerStatusToAllocatedForBroker(peerComponent, workerDID.getServiceID(), lwpcID.getPublicKey(), wmOID, 
				(WorkerManagement) AcceptanceTestUtil.getBoundObject(wmOID));
		
		EasyMock.verify(lwpc);
		EasyMock.verify(newLogger);
		EasyMock.verify(worker);
		
		peerComponent.setLogger(oldLogger);
		
		return new TestStub(workerDID, worker);
	}

	/**
	 * Changes a worker's status to allocated for broker, without verifying logger, 
	 * mainly used for input validation.
	 * @param remoteWorker The Worker interface of the worker
	 * @param workerPublicKey The worker public key
	 */
	public void changeWorkerStatusToAllocatedForBroker(PeerComponent peerComponent, ServiceID workerServiceID, 
			String brokerPublicKey, DeploymentID wmOID, WorkerManagement workerManagement) {
		
		WorkerManagementClient wmc = getWorkerManagementClient();
		
		AcceptanceTestUtil.publishTestObject(peerComponent, wmOID, workerManagement, WorkerManagement.class);
	    AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), wmOID);
		wmc.statusChangedAllocatedForBroker(workerServiceID, brokerPublicKey);
	}
	
	/**
	 * Changes a worker's status to allocated for broker, without verifying logger, 
	 * mainly used for input validation.
	 * @param remoteWorker The Worker interface of the worker
	 * @param workerPublicKey The worker public key
	 */
	public void changeWorkerStatusToAllocatedForBroker(PeerComponent peerComponent,
			String workerPublicKey) {
		
		WorkerManagementClient wmc = getWorkerManagementClient();
		
	    AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), workerPublicKey);
		wmc.statusChangedAllocatedForBroker(null, null);
	}
	
	public void changeWorkerStatusToAllocatedForBroker(PeerComponent peerComponent, ServiceID workerServiceID, DeploymentID wmOID, 
			RemoteWorkerManagement workerManagement) {
		
		RemoteWorkerManagementClient rwmc = getRemoteWorkerManagementClient();

	    AcceptanceTestUtil.setExecutionContext(peerComponent, getRemoteWorkerManagementClientDeployment(), wmOID);
		rwmc.statusChangedAllocatedForBroker(workerServiceID);
	}
	
	/**
	 * Changes a remote worker's status to allocated for broker
	 * @param peerComponent The peer component
	 * @param lwpc The LocalWorkerProviderClient interface of the consumer
	 * @param rwmc The RemoteWorkerManagementClient interface of the peer
	 * @param rwmOID The objectID of the worker's RemoteWorkerManagement interface
	 * @param workerSpecA The WorkerSpec containing the worker's attributes
	 * @param requestSpec The RequestSpec containing request info
	 * @return The Worker mock created
	 */
	public TestStub changeWorkerStatusToAllocatedForBroker(PeerComponent peerComponent, ObjectDeployment lwpc, 
			ObjectDeployment rwmc, DeploymentID rwmOID, WorkerSpecification workerSpecA, RequestSpecification requestSpec) {
		
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		peerComponent.setLogger(newLogger);
		
		Worker worker = EasyMock.createMock(Worker.class);
		DeploymentID workerOID = new DeploymentID("a@b/d", "e");
		workerOID.setPublicKey(rwmOID.getPublicKey());
		
		DeploymentID lwpcOID = lwpc.getDeploymentID();
		
		newLogger.debug("Giving the remote worker [" + rwmOID.getContainerID() + "] to [" + lwpcOID.getContainerID() + "].");
		
		AcceptanceTestUtil.publishTestObject(application, workerOID, worker, Worker.class);
		
		EasyMock.reset(lwpc.getObject());
		((LocalWorkerProviderClient)lwpc.getObject()).hereIsWorker(workerOID.getServiceID(), workerSpecA, requestSpec);
		
		EasyMock.replay(lwpc.getObject());
		EasyMock.replay(newLogger);
		EasyMock.replay(worker);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, rwmc, rwmOID);
		((RemoteWorkerManagementClient)rwmc.getObject()).statusChangedAllocatedForBroker(workerOID.getServiceID());
		
		EasyMock.verify(lwpc.getObject());
		EasyMock.verify(newLogger);
		EasyMock.verify(worker);
		
		peerComponent.setLogger(oldLogger);
		
		return new TestStub(workerOID, worker);
		
	}

	/**
	 * Change a worker's status to OWNER
	 * @param workerID The public key of the worker
	 */
	public void changeWorkerStatusToOwner(PeerComponent peerComponent, DeploymentID workerID) {
	    changeStatus(peerComponent, WorkerStatus.OWNER, workerID);
	}
	

	/**
	 * Changes a worker's status to allocated for peer
	 * @param peerComponent The peer component
	 * @param rwpc The RemoteWorkerProviderClient interface of the peer
	 * @param workerManagementDID The objectID of the WorkerManagament interface of the worker
	 * @param workerSpecA The WorkerSpec of the worker containing its attributes
	 * @return The RemoteWorkerManagement mock created 
	 */
	public RemoteWorkerManagement changeWorkerStatusToAllocatedForPeer(PeerComponent peerComponent, 
			RemoteWorkerProviderClient rwpc, DeploymentID workerManagementDID, WorkerSpecification workerSpecA,
			DeploymentID consumerID) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		peerComponent.setLogger(newLogger);
		
		RemoteWorkerManagement remoteWorkerManagement = EasyMock.createMock(RemoteWorkerManagement.class);
		
		DeploymentID rwmDeploymentID = new DeploymentID(workerManagementDID.getContainerID(), WorkerConstants.REMOTE_WORKER_MANAGEMENT);
		
		AcceptanceTestUtil.publishTestObject(peerComponent, rwmDeploymentID, remoteWorkerManagement, RemoteWorkerManagement.class);
		AcceptanceTestUtil.publishTestObject(peerComponent, consumerID, rwpc, RemoteWorkerProviderClient.class);
		
		EasyMock.reset(rwpc);
		
		newLogger.info("Worker <" + workerManagementDID.getContainerID() + "> is now DONATED");
		newLogger.info("Donating Worker <" + workerManagementDID.getContainerID() + "> to <" + consumerID.getContainerID() + ">");
		rwpc.hereIsWorker(getRemoteWorkerProvider(), rwmDeploymentID.getServiceID(), workerSpecA);
		
		EasyMock.replay(rwpc);
		EasyMock.replay(newLogger);
		EasyMock.replay(remoteWorkerManagement);
		
		changeStatusAllocatedForPeer(rwmDeploymentID, consumerID.getPublicKey(), peerComponent);
		
		EasyMock.verify(newLogger);
		EasyMock.verify(rwpc);
		EasyMock.reset(newLogger);
		
		return remoteWorkerManagement;
	}
	
	/**
	 * Changes a worker's status to allocated for peer, without verifying logger, 
	 * mainly used for input validation.
	 * @param remoteWorker The RemoteWorkerManagement interface of the worker
	 * @param workerPublicKey The worker public key
	 */
	public void changeStatusAllocatedForPeer(String remotePeerPubKey, PeerComponent peerComponent) {
	    //Get bound object
	    WorkerManagementClient workerManagementClient = getWorkerManagementClient();
	
	    //Change status of worker
	    AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), remotePeerPubKey);
	    workerManagementClient.statusChangedAllocatedForPeer(null, remotePeerPubKey);
	}
	
	/**
	 * Changes a worker's status to allocated for peer, without verifying logger, 
	 * mainly used for input validation.
	 * @param remoteWorker The RemoteWorkerManagement interface of the worker
	 * @param workerPublicKey The worker public key
	 */
	public void changeStatusAllocatedForPeer(DeploymentID remoteWorkerID, String remotePeerPubKey, PeerComponent peerComponent) {
	    //Get bound object
	    WorkerManagementClient workerManagementClient = getWorkerManagementClient();
	
	    //Change status of worker
	    AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), remoteWorkerID);
	    workerManagementClient.statusChangedAllocatedForPeer(remoteWorkerID == null ? null : remoteWorkerID.getServiceID(),
	    		remotePeerPubKey);
	}
	
	public void changeStatusAllocatedForPeer(DeploymentID senderId, ServiceID workerID, 
			String remotePeerPubKey, PeerComponent peerComponent) {
	    //Get bound object
	    WorkerManagementClient workerManagementClient = getWorkerManagementClient();
	
	    //Change status of worker
	    AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), senderId);
	    workerManagementClient.statusChangedAllocatedForPeer(workerID, remotePeerPubKey);
	}
	
	/**
     * Change worker status to IDLE and schedules a Worker advert
     * @param peerComponent The peer component
     * @param workerSpec The WorkerSpec containing worker attributes
     * @param workerOID The worker's object ID
     * @param dsOID The nodeWiz object ID
	 * @return The scheduled future
     */
	public void changeWorkerStatusToIdleWithAdvert(PeerComponent peerComponent, WorkerSpecification workerSpec, 
    		DeploymentID workerOID, DeploymentID dsOID) {
    	
    	//Changes temporarily the timer mock
		ScheduledExecutorService oldTimer = peerComponent.getTimer();
		ScheduledExecutorService newTimer = EasyMock.createMock(ScheduledExecutorService.class);
		peerComponent.setTimer(newTimer);
		
		//Records mock behavior
		DiscoveryService discoveryService = (DiscoveryService) AcceptanceTestUtil.getBoundObject(dsOID);
		EasyMock.reset(discoveryService);
		
		EasyMock.replay(discoveryService);
		EasyMock.replay(newTimer);

		//Change worker status
		changeWorkerStatusToIdle(peerComponent, workerOID);
		
		//Verify mocks
		EasyMock.verify(newTimer);
		EasyMock.verify(discoveryService);
		EasyMock.reset(discoveryService);
		
		//Set oldTimer back in peer component
		peerComponent.setTimer(oldTimer);
		
	}

    /**
	 * Changes a worker's status to IDLE, verifying if another 
	 * consumes the worker after it becomes IDLE.
	 * @param workerOID The objectID for the interface WorkerManagement
	 * @param lwpcID The objectID for LocalWorkerProviderClient 
	 * which consumer will receive the worker
	 */
    public void changeWorkerStatusToIdleWorkingForBroker(DeploymentID workerOID, DeploymentID lwpcID, PeerComponent component) {
    	changeWorkerStatusToIdleWorkingForBroker(workerOID, lwpcID, null, component); 
    }
    
	/**
	 * Changes a worker's status to IDLE, verifying if another 
	 * consumes the worker after it becomes IDLE.
	 * @param workerOID The objectID for the interface WorkerManagement
	 * @param lwpcID The objectID for LocalWorkerProviderClient 
	 * which consumer will receive the worker
	 * @param future The request future to be canceled
	 */
	public void changeWorkerStatusToIdleWorkingForBroker(DeploymentID workerOID, DeploymentID lwpcID, 
			ScheduledFuture<?> future, PeerComponent component) {
		WorkerManagement workerManagement = (WorkerManagement) AcceptanceTestUtil.getBoundObject(workerOID);
		EasyMock.reset(workerManagement);
		workerManagement.workForBroker(lwpcID);
		EasyMock.replay(workerManagement);
		
		if (future != null) {
			EasyMock.reset(future);
			EasyMock.expect(future.cancel(true)).andReturn(true);
			EasyMock.replay(future);
		}
		
		changeStatus(component, WorkerStatus.IDLE, workerOID);
		
		EasyMock.verify(workerManagement);
		if (future != null) {
			EasyMock.verify(future);
		}
	}
	
	/**
	 * Changes a worker's status to IDLE
	 * @param peerComponent The peer component
	 * @param workerDeploymentID The DeploymentID for the interface WorkerManagement
	 */
	public void changeWorkerStatusToIdle(PeerComponent peerComponent, DeploymentID workerDeploymentID) {
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		peerComponent.setLogger(newLogger);
		
		newLogger.debug("Worker <"+workerDeploymentID.getContainerID()+"> is now IDLE");
		EasyMock.replay(newLogger);
		
		changeStatus(peerComponent, WorkerStatus.IDLE, workerDeploymentID);
		
		EasyMock.verify(newLogger);
		peerComponent.setLogger(oldLogger);
		
	}

	/**
	 * Changes a worker's status, without verifying logger, 
	 * mainly used for input validation.
	 * @param status The new status of the worker
	 * @param workerID The worker public key
	 */
	public void changeStatus(PeerComponent peerComponent, WorkerStatus status, DeploymentID workerID) {
	
	    //Get bound object
	    WorkerManagementClient workerManagementClient = getWorkerManagementClient();
	    
	    //Change status of worker
	    AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), 
	    		workerID);
	    workerManagementClient.statusChanged(status);
	}
	
	public void changeUnknowWorkerStatusToIdle(PeerComponent peerComponent, DeploymentID workerID,  String workerPublicKey) {
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		peerComponent.setLogger(newLogger);
		
		newLogger.warn("Unknown worker changed status: "+ workerPublicKey +"/IDLE");
		EasyMock.replay(newLogger);
		
	    //Get bound object
	    WorkerManagementClient workerManagementClient = getWorkerManagementClient();
	    
	    //Change status of worker
	    AcceptanceTestUtil.setExecutionContext(peerComponent, getWorkerManagementClientDeployment(), 
	    		workerID);
	    
	    workerManagementClient.statusChanged(WorkerStatus.IDLE);

		
		EasyMock.verify(newLogger);
		peerComponent.setLogger(oldLogger);
	}

} 