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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.RemoteAllocation;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.to.RequestSpecification;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Req_014_Util extends PeerAcceptanceUtil {

	public Req_014_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Finishes a request that caused only local workers allocations.
	 * @param component The peer component
	 * @param lwp The LocalWorkerProvider interface of the peer
	 * @param brokerPubKey The public key for the request's consumer
	 * @param requestSpec The RequestSpec of the request to be finished
	 * @param allocations The expected workers' allocations to be performed 
	 * after this request is finished
	 */
	public void finishRequestWithLocalWorkers(Module component, LocalWorkerProvider lwp, ObjectDeployment lwpOID, String brokerPubKey,
			ServiceID lwpcID, RequestSpecification requestSpec, List<WorkerAllocation> allocations) {
	
		finishRequestWithLocalAndRemoteWorkers(component, lwp, lwpOID, brokerPubKey, lwpcID, null, requestSpec, allocations);
	}

	/**
	 * Finishes a request that caused only local workers allocations.
	 * The request future is expected to be canceled 
	 * @param component The peer component
	 * @param lwp The LocalWorkerProvider interface of the peer
	 * @param brokerPubKey The public key for the request's consumer
	 * @param future The request future to be canceled
	 * @param requestSpec The RequestSpec of the request to be finished
	 * @param workersOID The expected workers' allocations to be performed 
	 * after this request is finished
	 */
	public void finishRequestWithLocalWorkers(Module component, LocalWorkerProvider lwp, ObjectDeployment lwpOID, String brokerPubKey,
			ServiceID lwpcID, ScheduledFuture<?> future, RequestSpecification requestSpec, List<WorkerAllocation> workersOID) {
		
		finishRequestWithLocalAndRemoteWorkers(component, lwp, lwpOID, brokerPubKey, lwpcID, future, requestSpec, workersOID);
	}

	/**
	 * Finishes a request that caused only remote workers allocations.
	 * @param component The peer component
	 * @param lwp The LocalWorkerProvider interface of the peer
	 * @param brokerPubKey The public key for the request's consumer
	 * @param requestSpec The RequestSpec of the request to be finished
	 * @param remotePeers Contains remote workers allocations to be performed 
	 * after this request is finished
	 */
	public void finishRequestWithRemoteWorkers(Module component, LocalWorkerProvider lwp, ObjectDeployment lwpOID, 
			String brokerPubKey, ServiceID lwpcID, RequestSpecification requestSpec, 
			TestStub... remotePeers) {
		
		finishRequestWithLocalAndRemoteWorkers(component, lwp, lwpOID, brokerPubKey, lwpcID, null, requestSpec, new LinkedList<WorkerAllocation>(), remotePeers);
	}

	/**
	 * Finishes a request that caused only remote workers allocations.
	 * The request future is expected to be canceled 
	 * @param component The peer component
	 * @param lwp The LocalWorkerProvider interface of the peer
	 * @param brokerPubKey The public key for the request's consumer
	 * @param future The request future to be canceled
	 * @param requestSpec The RequestSpec of the request to be finished
	 * @param remotePeers Contains remote workers allocations to be performed 
	 * after this request is finished
	 */
	public void finishRequestWithRemoteWorkers(Module component, LocalWorkerProvider lwp, ObjectDeployment lwpOID, String brokerPubKey, 
			ServiceID lwpcID, ScheduledFuture<?> future, RequestSpecification requestSpec, TestStub... remotePeers) {
		
		finishRequestWithLocalAndRemoteWorkers(component, lwp, lwpOID, brokerPubKey, lwpcID, future, requestSpec, new LinkedList<WorkerAllocation>(), remotePeers);
	}

	/**
	 * Finishes a request that caused only remote workers allocations.
	 * @param component The peer component
	 * @param lwp The LocalWorkerProvider interface of the peer
	 * @param brokerPubKey The public key for the request's consumer
	 * @param requestSpec The RequestSpec of the request to be finished
	 * @param localWorkers The expected local workers' allocations to be performed 
	 * after this request is finished
	 * @param remotePeers Contains remote workers allocations to be performed 
	 * after this request is finished
	 */
	public void finishRequestWithLocalAndRemoteWorkers(Module component, LocalWorkerProvider lwp, ObjectDeployment lwpOID, String brokerPubKey,
			ServiceID lwpcID, RequestSpecification requestSpec, List<WorkerAllocation> localWorkers, TestStub remotePeers) {
	
		finishRequestWithLocalAndRemoteWorkers(component, lwp, lwpOID, brokerPubKey, lwpcID, null, requestSpec, localWorkers, remotePeers);
	}

	/**
	 * Finishes a request that caused only remote workers allocations.
	 * The request future is expected to be canceled 
	 * @param component The peer component
	 * @param lwp The LocalWorkerProvider interface of the peer
	 * @param brokerPubKey The public key for the request's consumer
	 * @param future The request future to be canceled
	 * @param requestSpec The RequestSpec of the request to be finished
	 * @param localWorkers The expected local workers' allocations to be performed 
	 * after this request is finished
	 * @param remotePeers Contains remote workers allocations to be performed 
	 * after this request is finished
	 */
	public void finishRequestWithLocalAndRemoteWorkers(Module component, LocalWorkerProvider lwp, ObjectDeployment lwpOID, String brokerPubKey, 
			ServiceID lwpcID, ScheduledFuture<?> future, RequestSpecification requestSpec, List<WorkerAllocation> localWorkers, TestStub... remotePeers) {
	
		CommuneLogger oldLoggerMock = component.getLogger();
		
		//Create Mocks
		CommuneLogger newLoggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLoggerMock);
		
		if(future != null){
			EasyMock.expect(future.cancel(true)).andReturn(true).anyTimes();
			EasyMock.replay(future);
		}
		
		newLoggerMock.debug("Request " + requestSpec.getRequestId() + 
				" finished by [" + lwpcID + "].");
		
		for (WorkerAllocation localWorker : localWorkers) {
	
			DeploymentID localWorkerOID = localWorker.workerID;
			//Looks up the worker management mock
			WorkerManagement workerManag = (WorkerManagement) AcceptanceTestUtil.getBoundObject(localWorkerOID);
			
			EasyMock.reset(workerManag);
			
			//Records mock behavior
			DeploymentID winnerID = localWorker.winnerID;
			
			if(winnerID != null) {
				workerManag.workForBroker(winnerID);
				EasyMock.replay(workerManag);
				continue;
			}
			
			//newLoggerMock.debug("Worker <" + localWorkerOID + "> is now IDLE");
			workerManag.stopWorking();
			EasyMock.replay(workerManag);
		}
		
		for (TestStub stub : remotePeers) {
			
			RemoteAllocation peer = (RemoteAllocation) stub.getObject();
			RemoteWorkerProvider rwp = peer.rwp;
			
			DeploymentID rwpOID = stub.getDeploymentID();
			EasyMock.reset(rwp);
			
			AcceptanceTestUtil.publishTestObject(application, rwpOID, rwp, RemoteWorkerProvider.class);
			
			List<WorkerAllocation> remoteWorkers = peer.remoteWorkers;
			
			for (WorkerAllocation remoteWorker : remoteWorkers) {
				
				DeploymentID remoteWorkerOID = remoteWorker.workerID;
				
				//Looks up the worker management mock
				RemoteWorkerManagement rWorkerManag = (RemoteWorkerManagement) AcceptanceTestUtil.getBoundObject(remoteWorkerOID);
				
				//Records remote worker mock behavior
				EasyMock.reset(rWorkerManag);
				
				DeploymentID winnerID = remoteWorker.winnerID;
				
				if(winnerID != null){
					remoteWorker.workForBroker(winnerID, rWorkerManag);
					EasyMock.replay(rWorkerManag);
					continue;
				}
				
				newLoggerMock.debug("The remote worker " + remoteWorkerOID.getServiceID() + " does not match any request. " +
						"Disposing it back to its provider: " + rwpOID.getServiceID() + ".");
				
				//Records remote worker provider mock behavior
				rwp.disposeWorker(remoteWorkerOID.getServiceID());
				EasyMock.replay(rWorkerManag);
			}
			
			EasyMock.replay(rwp);
		}
		
		EasyMock.replay(newLoggerMock);
		
		//Finishes request
		AcceptanceTestUtil.setExecutionContext(component, lwpOID, brokerPubKey);
		lwp.finishRequest(requestSpec);
		
		EasyMock.verify(newLoggerMock);
		
		for (WorkerAllocation localWorker : localWorkers) {
			
			DeploymentID localWorkerOID = localWorker.workerID;
			WorkerManagement workerManag = (WorkerManagement) AcceptanceTestUtil.getBoundObject(localWorkerOID);
			EasyMock.verify(workerManag);
		}
		
		for (TestStub stub : remotePeers) {
			
			RemoteAllocation peer = (RemoteAllocation) stub.getObject();
			
			List<WorkerAllocation> remoteWorkers = peer.remoteWorkers;
			
			for (WorkerAllocation remoteWorker : remoteWorkers) {
				
				DeploymentID remoteWorkerOID = remoteWorker.workerID;
				RemoteWorkerManagement rWorkerManag = (RemoteWorkerManagement) AcceptanceTestUtil.getBoundObject(
						remoteWorkerOID);
				EasyMock.verify(rWorkerManag);
			}
		}
		
		if(future != null){
			EasyMock.verify(future);
		}
		
		component.setLogger(oldLoggerMock);
	}
}