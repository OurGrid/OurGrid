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

import static org.easymock.EasyMock.eq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.matchers.RequestRepetitionRunnableMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_011_Util extends PeerAcceptanceUtil {

	public Req_011_Util(ModuleContext context) {
		super(context);
	}

	public static final int DO_NOT_LOAD_SUBCOMMUNITIES = -1;
	
	/**
	 * Request local workers for a remote client. For the first time
	 * a request is done, it loads the subcommunities trust file, and then
	 * it should not load it again. The integer constant DO_NOT_LOAD_SUBCOMMUNITIES
	 * must be used in order to flag this second scenario.
	 * 
	 * @param peerComponent The peer component
	 * @param clientID The DeploymentID for the consumer's LocalWorkerProviderClient interface 
	 * @param requestSpec The request spec
	 * @param expectedNumberOfSubCommunities The expected number of loaded subcommunities 
	 * when calling this method
	 * @param workerAllocations The workers that will be allocated for the consumer through this request
	 * @return The remote consumer RemoteWorkerProviderClient interface
	 */
	public RemoteWorkerProviderClient requestForRemoteClient(Module peerComponent, DeploymentID clientID,
			RequestSpecification requestSpec, int expectedNumberOfSubCommunities, WorkerAllocation... workerAllocations) {
		
		CommuneLogger logger = peerComponent.getLogger();
		EasyMock.reset(logger);
		
		if (expectedNumberOfSubCommunities > Req_011_Util.DO_NOT_LOAD_SUBCOMMUNITIES) {
			String pluralSubCom = expectedNumberOfSubCommunities == 1 ? "y." : "ies.";
			logger.info("Trust configuration file loaded with " + expectedNumberOfSubCommunities + " subcommunit"+pluralSubCom);
		}
		
		String plural = requestSpec.getRequiredWorkers() > 1 ? "s" : ""; 
		
		logger.info("Request " + requestSpec.getRequestId() + ": [" + clientID.getContainerID() + "] requested " + requestSpec.getRequiredWorkers() + " worker" + plural);
		
		ObjectDeployment peerDeployment = getRemoteWorkerProviderDeployment();
		RemoteWorkerProvider peer = (RemoteWorkerProvider) peerDeployment.getObject();
		RemoteWorkerProviderClient client = EasyMock.createMock(RemoteWorkerProviderClient.class);
		EasyMock.replay(client);
		
		String pubKeyClient = clientID.getPublicKey();
		
		List<WorkerManagement> workerManagements = new LinkedList<WorkerManagement>();
		List<String> workerIds = new LinkedList<String>();
		
		for (WorkerAllocation allocation : workerAllocations) {
			
			if (allocation.loserID != null) {
				logger.info("Request " + requestSpec.getRequestId() + ": Taking worker " +
						"[" + allocation.workerID.getServiceID().toString() + "] from " +
						"[" + allocation.loserID.getServiceID().toString() + "]");
			}
			
			WorkerManagement workermA = (WorkerManagement) AcceptanceTestUtil.getBoundObject(allocation.workerID);
			workerIds.add(allocation.workerID.toString());
			EasyMock.reset(workermA);
			workermA.workForPeer(pubKeyClient);
			EasyMock.replay(workermA);
			workerManagements.add(workermA);
		}
		EasyMock.replay(logger);
		
		AcceptanceTestUtil.publishTestObject(peerComponent, clientID, client, RemoteWorkerProviderClient.class);
		AcceptanceTestUtil.setExecutionContext(peerComponent, peerDeployment, clientID, 
				AcceptanceTestUtil.getCertificateMock(clientID));
		
		peer.requestWorkers(client, requestSpec);
	
		for (WorkerManagement workerManagement : workerManagements) {
			EasyMock.verify(workerManagement);
		}
		
		EasyMock.verify(logger);
		
		return client;
	}

	/**
	 * Requests worker for a local consumer
	 * 
	 * @param peerComponent The peer component
	 * @param lwpcOID The DeploymentID for the consumer's LocalWorkerProviderClient interface
	 * @param requestSpec The request spec 
	 * @param allocatedWorkersOID The workers that will be allocated for the consumer through this request
	 * @return ScheduledFuture for the request repetition
	 */
	public ScheduledFuture<?> requestForLocalConsumer(PeerComponent peerComponent, TestStub lwpcStub,
			RequestSpecification requestSpec, WorkerAllocation... allocatedWorkersOID) {
		
		return requestForLocalConsumer(peerComponent, lwpcStub, requestSpec, new ArrayList<TestStub>(),
				allocatedWorkersOID);
	}
	
	@SuppressWarnings("unchecked")
	public ScheduledFuture<?> requestForLocalConsumer(PeerComponent peerComponent,
			TestStub lwpcStub, RequestSpecification requestSpec, List<TestStub> rwps, WorkerAllocation... allocatedWorkers) {
		
		ScheduledFuture<?> future1 = null;
		
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLoggerMock = EasyMock.createMock(CommuneLogger.class);
//		EasyMock.reset(newLoggerMock);
		
		peerComponent.setLogger(newLoggerMock);
		
		String plural = ((requestSpec.getRequiredWorkers() > 1) ? "s":"");
		newLoggerMock.info("Request "+requestSpec.getRequestId()+": [" + lwpcStub.getDeploymentID().getServiceID()+"] " +
				"requested "+ requestSpec.getRequiredWorkers()+" worker"+plural);
		
		if ( !isRequestSatisfiedLocally(requestSpec.getRequiredWorkers(), allocatedWorkers) ) {
			newLoggerMock.debug("Request "+requestSpec.getRequestId()+": request forwarded to community.");
			for (TestStub tStub : rwps) {
				
				RemoteWorkerProvider rwp= (RemoteWorkerProvider) tStub.getObject();
				getRemoteWorkerProviderClient().workerProviderIsUp(rwp, tStub.getDeploymentID(),
						AcceptanceTestUtil.getCertificateMock(tStub.getDeploymentID()));
				
				newLoggerMock.debug("Request "+ requestSpec.getRequestId() +": requesting workers " +
						"from a remote worker provider ["+tStub.getDeploymentID().getServiceID()+"].");
				
				EasyMock.reset(rwp);
				rwp.requestWorkers(getRemoteWorkerProviderClient(), requestSpec);
				EasyMock.replay(rwp);
			}
		}
		
		//Request workers
		//requestForLocalConsumer(peerComponent, lwpcOID, requestID, requiredWorkers, requirements, allocatedWorkersOID);
		
		ScheduledExecutorService oldTimerMock = null;
		ScheduledExecutorService newTimerMock = EasyMock.createMock(ScheduledExecutorService.class);
		
		if ( !isRequestSatisfiedLocally(requestSpec.getRequiredWorkers(), allocatedWorkers) ) {
			long delay = context.parseIntegerProperty(PeerConfiguration.PROP_REPEAT_REQUEST_DELAY);
			
			newLoggerMock.debug("Request "+requestSpec.getRequestId()+": request scheduled for repetition in "+delay+" seconds.");
			
			oldTimerMock = peerComponent.getTimer();
			peerComponent.setTimer(newTimerMock);
			
			future1 = EasyMock.createMock(ScheduledFuture.class);
			
	        RepetitionRunnable runnable = createRequestWorkersRunnable(peerComponent, requestSpec.getRequestId());
	    	
			EasyMock.expect((ScheduledFuture)newTimerMock.scheduleWithFixedDelay(RequestRepetitionRunnableMatcher.eqMatcher(runnable), 
					eq(delay), eq(delay), eq(TimeUnit.SECONDS))).andReturn(future1).once();
			
		}
		
		LocalWorkerProvider peer = getLocalWorkerProviderProxy();
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) lwpcStub.getObject();
		
		List<Object> workerManagements = new LinkedList<Object>();
		
		for (WorkerAllocation allocation : allocatedWorkers) {
			if (allocation.loserID != null) {
				newLoggerMock.info("Request "+requestSpec.getRequestId()+": Taking worker " +
						"["+allocation.workerID.getServiceID()+"] from ["+allocation.loserID.getServiceID()+"]");
			
				RequestSpecification loserRequestSpec = allocation.loserRequestSpecification;
				if(loserRequestSpec != null){
					peerComponent.setTimer(newTimerMock);
					long delay = context.parseIntegerProperty(PeerConfiguration.PROP_REPEAT_REQUEST_DELAY);
					
					newLoggerMock.debug("Request "+loserRequestSpec.getRequestId()+": request scheduled for repetition in "+delay+" seconds.");
	
					ScheduledFuture<?> future2 = EasyMock.createMock(ScheduledFuture.class);
					
		            RepetitionRunnable runnable = createRequestWorkersRunnable(peerComponent, loserRequestSpec.getRequestId());
		        	
					EasyMock.expect((ScheduledFuture)newTimerMock.scheduleWithFixedDelay(RequestRepetitionRunnableMatcher.eqMatcher(runnable), 
							eq(delay), eq(delay), eq(TimeUnit.SECONDS))).andReturn(future2).once();
	
				}
			}
			
			Object workerm = AcceptanceTestUtil.getBoundObject(allocation.workerID);
			EasyMock.reset(workerm);
			allocation.workForBroker(lwpcStub.getDeploymentID(), workerm);
			EasyMock.replay(workerm);
			workerManagements.add(workerm);
		}
		
		EasyMock.replay(newTimerMock);
		
		if(AcceptanceTestUtil.getBoundObject(lwpcStub.getDeploymentID()) == null){
			AcceptanceTestUtil.publishTestObject(peerComponent, lwpcStub.getDeploymentID(), lwpc,
					LocalWorkerProviderClient.class);
		}
		
		//EasyMock.replay(lwpc);
		EasyMock.replay(newLoggerMock);
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, 
				getLocalWorkerProviderDeployment(), lwpcStub.getDeploymentID(), 
				AcceptanceTestUtil.getCertificateMock(lwpcStub.getDeploymentID()));
		
		for (TestStub stub : rwps) {
			AcceptanceTestUtil.publishTestObject(peerComponent, stub.getDeploymentID(), stub.getObject(), 
					RemoteWorkerProvider.class);
		}
		
		peer.requestWorkers(requestSpec);
		
		for (Object workerManagement : workerManagements) {
			EasyMock.verify(workerManagement);
		}
		
		EasyMock.verify(lwpc);
		EasyMock.verify(newLoggerMock);
		
		if ( !isRequestSatisfiedLocally(requestSpec.getRequiredWorkers(), allocatedWorkers) ) {
			EasyMock.verify(newTimerMock);
			for (TestStub tStub : rwps) {
				EasyMock.verify(tStub.getObject());
			}
			peerComponent.setTimer(oldTimerMock);
		}
		
		peerComponent.setLogger(oldLogger);
		
		return future1;
	}

	private boolean isRequestSatisfiedLocally(int requiredWorkers, WorkerAllocation... allocatedWorkersOID) {
		return allocatedWorkersOID.length >= requiredWorkers;
	}

}