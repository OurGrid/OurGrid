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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.matchers.RequestRepetitionRunnableMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;



public class Req_015_Util extends PeerAcceptanceUtil {

	
	public Req_015_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * A local consumer disposes a local worker 
	 * 
	 * @param component The peer component
	 * @param worker The worker to be disposed
	 * @param allocation This allocation must contain information about
	 * the consumer that disposed the worker (loserID), the consumer that
	 * will receive the worker (winnerID) and the request.
	 * If the winnerID is null, the worker is commanded to stop working
	 */
	public void localConsumerDisposesLocalWorker(PeerComponent component, Worker worker, 
			WorkerAllocation allocation) {
		localConsumerDisposesLocalWorker(component, worker, allocation, null);
	}
	
	/**
	 * A local consumer disposes a local worker 
	 * 
	 * @param component The peer component
	 * @param worker The worker to be disposed
	 * @param allocation This allocation must contain information about
	 * the consumer that disposed the worker (loserID), the consumer that
	 * will receive the worker (winnerID) and the request.
	 * If the winnerID is null, the worker is commanded to stop working
	 * @param future The future of the request to be canceled
	 */
	public void localConsumerDisposesLocalWorker(PeerComponent component, Worker worker, 
			WorkerAllocation allocation, ScheduledFuture<?> future) {
		//Changes temporarily the logger mock
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
	
		WorkerManagement workerManag = (WorkerManagement) AcceptanceTestUtil.getBoundObject(allocation.workerID);
		EasyMock.reset(workerManag);
	
		newLogger.debug("Request " + allocation.loserRequestSpecification.getRequestId() + ": ["+allocation.loserID.getServiceID()+"] " +
				"disposed the worker ["+allocation.workerID.getServiceID()+"].");
		
		if (allocation.winnerID != null) {
			workerManag.workForBroker(allocation.winnerID);
		} else {
			//newLogger.debug("Worker <" + allocation.workerID + "> is now IDLE");
			workerManag.stopWorking();
		}
	
		if (future != null) {
			EasyMock.reset(future);
			EasyMock.expect(future.cancel(true)).andReturn(true);
			EasyMock.replay(future);
		}
		
		EasyMock.replay(workerManag);
		EasyMock.replay(newLogger);
	
		ObjectDeployment lwpOD = getLocalWorkerProviderDeployment();
		LocalWorkerProvider lwp = getLocalWorkerProvider();
		
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, allocation.loserID.getPublicKey());
		lwp.disposeWorker(allocation.workerID.getServiceID());
	
		if (future != null) {
			EasyMock.verify(future);
		}
		EasyMock.verify(workerManag);
		EasyMock.verify(newLogger);
		
		component.setLogger(oldLogger);
	}

	/**
	 * A local consumer disposes a remote worker and another consumer receives it
	 * 
	 * @param component The peer component
	 * @param worker The worker to bo disposed
	 * @param allocation This allocation must contain information about
	 * the consumer that disposed the worker (loserID), the consumer that
	 * will receive the worker (winnerID) and the request. The winnerID
	 * must not be null, even if the consumer that disposed the worker receives it back
	 * @param rescheduleRequest Determines whether the request must be rescheduled
	 */
	public void localDisposeRemoteWorker(PeerComponent component, TestStub workerStub, WorkerAllocation allocation, boolean rescheduleRequest) {
		localDisposeRemoteWorker(component, workerStub, allocation, null, null, rescheduleRequest);
	}
	
	/**
	 * A local consumer disposes a remote worker
	 * 
	 * @param component The peer component
	 * @param worker The worker to be disposed
	 * @param allocation This allocation must contain information about
	 * the consumer that disposed the worker (loserID), the consumer that
	 * will receive the worker (winnerID) and the request.
	 * If the winnerID is not provided, it is assumed that the worker is
	 * going to be disposed to its remote provider.
	 * In this case the RemoteWorkerProvider must not be null.
	 * @param rwp The RemoteWorkerProvider that provided the worker to be disposed
	 * @param rescheduleRequest Determines whether the request must be rescheduled
	 */
	@SuppressWarnings("unchecked")
	public void localDisposeRemoteWorker(PeerComponent component, 
			TestStub workerStub, WorkerAllocation allocation, RemoteWorkerProvider rwp, DeploymentID rwpOID, boolean rescheduleRequest) {
		
		//Changes temporarily the logger and timer mock
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		ScheduledExecutorService oldTimer = component.getTimer();
		ScheduledExecutorService newTimer = EasyMock.createMock(ScheduledExecutorService.class);
		
		component.setLogger(newLogger);
		component.setTimer(newTimer);
		
		RemoteWorkerManagement remoteWorkerManag = (RemoteWorkerManagement) AcceptanceTestUtil.getBoundObject(allocation.workerID);
		EasyMock.reset(remoteWorkerManag);
	
		newLogger.debug("Request " + allocation.loserRequestSpecification.getRequestId() + ": ["+allocation.loserID.getServiceID() +"] " +
				"disposed the worker ["+workerStub.getDeploymentID().getServiceID()+"].");
		
		if (allocation.winnerID == null) {
			//No request fits with the disposed worker, dispose it back to its provider
			EasyMock.reset(rwp);
			rwp.disposeWorker(allocation.workerID.getServiceID());
			
			EasyMock.replay(rwp);
			
			newLogger.debug("The remote worker " + allocation.workerID.getServiceID() +" does not match any request. " +
					"Disposing it back to its provider: " + rwpOID.getServiceID() + ".");
		} else {
			//A request fits with the disposed worker
			RemoteWorkerManagementClient rwmc = getRemoteWorkerManagementClient();
			remoteWorkerManag.workForBroker(rwmc, allocation.winnerID.getPublicKey());
		}
		
		if (rescheduleRequest) {
			//No local worker fits the request. Reschedule request for repetition.
			long delay = context.parseIntegerProperty(PeerConfiguration.PROP_REPEAT_REQUEST_DELAY);
			newLogger.debug("Request " + allocation.loserRequestSpecification.getRequestId() + ": request scheduled for " +
		    		"repetition in " + delay + " seconds.");
		
			RepetitionRunnable runnable = 
				createRequestWorkersRunnable(component, allocation.loserRequestSpecification.getRequestId());
        	
			EasyMock.expect((ScheduledFuture)newTimer.scheduleWithFixedDelay(RequestRepetitionRunnableMatcher.eqMatcher(runnable), 
					eq(delay), eq(delay), eq(TimeUnit.SECONDS))).andReturn(null).once();
			
			EasyMock.replay(newTimer);
		}
		
		EasyMock.replay(remoteWorkerManag);
		EasyMock.replay(newLogger);
	
		LocalWorkerProvider lwp = getLocalWorkerProviderProxy();
		
		if (rwp != null) {
			AcceptanceTestUtil.publishTestObject(component, rwpOID, rwp, RemoteWorkerProvider.class);
		}
		
		AcceptanceTestUtil.publishTestObject(component, workerStub.getDeploymentID(), workerStub.getObject(),
				Worker.class);
		
		AcceptanceTestUtil.publishTestObject(component, allocation.workerID, remoteWorkerManag,
				RemoteWorkerManagement.class);
		
		AcceptanceTestUtil.setExecutionContext(component, getLocalWorkerProviderDeployment(), allocation.loserID);
		
		lwp.disposeWorker(workerStub.getDeploymentID().getServiceID());
	
		if (allocation.winnerID == null) {
			EasyMock.verify(rwp);
		}
		if (rescheduleRequest) {
			EasyMock.verify(newTimer);
		}
		
		EasyMock.verify(remoteWorkerManag);
		EasyMock.verify(newLogger);
		
		//Set old timer and logger back
		component.setLogger(oldLogger);
		component.setTimer(oldTimer);
	}


	/**
	 * A remote consumer disposes a local worker 
	 * 
	 * @param component Peer component
	 * @param remoteClientOID ID of the Peer which is disposing the worker
	 * @param rwm The worker that is being disposed
	 * @param wmOID ID of the Worker Management object
	 */
	public void remoteDisposeLocalWorker(PeerComponent component, DeploymentID remoteClientOID, 
			RemoteWorkerManagement rwm, DeploymentID wmOID) {
		
		//Changes temporarily the logger mock
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
	
		WorkerManagement workerManag = (WorkerManagement) AcceptanceTestUtil.getBoundObject(wmOID);
		EasyMock.reset(workerManag);
		
		newLogger.debug("The remote client [" + remoteClientOID.getServiceID() + "] " +
				"disposed the worker [" + wmOID.getServiceID() + "].");
		workerManag.stopWorking();
	
		EasyMock.replay(workerManag);
		EasyMock.replay(newLogger);
	
		RemoteWorkerProvider rwp = getRemoteWorkerProviderProxy();
		
		AcceptanceTestUtil.publishTestObject(component, wmOID, workerManag, WorkerManagement.class);
		AcceptanceTestUtil.setExecutionContext(component, getRemoteWorkerProviderDeployment(), remoteClientOID);
		rwp.disposeWorker(wmOID.getServiceID());
	
		EasyMock.verify(workerManag);
		EasyMock.verify(newLogger);
		
		component.setLogger(oldLogger);
	}

	
}