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
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.matchers.RequestRepetitionRunnableMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_016_Util extends PeerAcceptanceUtil {

	public Req_016_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Marks a worker as unwanted, without verifying any expected behavior.
	 * @param workerID The worker to be marked as unwanted 
	 * @param requestSpec The request spec for the request which provided this worker
	 * @param mgPubKey The consumer public key
	 */
	public void unwantedWorker(PeerComponent component, ServiceID workerID, RequestSpecification requestSpec, DeploymentID lwpcID) {
		LocalWorkerProvider lwp = getLocalWorkerProviderProxy();
		
		AcceptanceTestUtil.setExecutionContext(component, getLocalWorkerProviderDeployment(), lwpcID);
		lwp.unwantedWorker(workerID, requestSpec);
	}

	/**
	 * Marks a worker as unwanted.
	 * If the worker is remote, expects the worker to be disposed to its provider, 
	 * or expects it work to another local consumer.
	 * If the worker is local, expects the worker to stop working or expects it to 
	 * work to another consumer.
	 * 
	 * @param peerComponent The peer component
	 * @param worker The worker to be marked as unwanted
	 * @param allocation The expected allocation to be made after 
	 * the worker is marked as unwanted
	 * @param requestSpec The request spec for the request which caused the worker allocation
	 * @param mgPubKey The consumer public key
	 * @param reschedule If true, expects the request to be rescheduled for repetition
	 * @param future The request future to be canceled. If null, this canceling is not expected.
	 * @param rwp The <code>RemoteWorkerProvider</code> for the remote worker to be marked as unwanted. 
	 * If the worker is not a remote worker, this parameter must be null.
	 * @return The <code>ScheduledFuture</code> of the request repetition
	 */
	@SuppressWarnings("unchecked")
	public ScheduledFuture<?> unwantedWorker(PeerComponent peerComponent, Worker worker, WorkerAllocation allocation, 
			RequestSpecification requestSpec, DeploymentID mgID, boolean reschedule, ScheduledFuture<?> future, RemoteWorkerProvider rwp,
			DeploymentID rwpID) {
		
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		ScheduledExecutorService oldTimer = peerComponent.getTimer();
		ScheduledExecutorService newTimer = EasyMock.createMock(ScheduledExecutorService.class);
		
		peerComponent.setLogger(newLogger);
		peerComponent.setTimer(newTimer);
		
		if (future != null) {
			EasyMock.expect(future.cancel(true)).andReturn(true);
			EasyMock.replay(future);
		}
		
		ScheduledFuture<?> future1 = null;
		
		//schedule request
		if ( reschedule ) {
			long delay = peerComponent.getContext().parseIntegerProperty(PeerConfiguration.PROP_REPEAT_REQUEST_DELAY);
			
			newLogger.debug("Request "+requestSpec.getRequestId()+": request scheduled for repetition in "+delay+" seconds.");
			
			oldTimer = peerComponent.getTimer();
			peerComponent.setTimer(newTimer);
			
			future1 = EasyMock.createMock(ScheduledFuture.class);
			
			RepetitionRunnable runnable = createRequestWorkersRunnable(peerComponent, requestSpec.getRequestId());
        	
			EasyMock.expect((ScheduledFuture) newTimer.scheduleWithFixedDelay(RequestRepetitionRunnableMatcher.eqMatcher(runnable), 
					eq(delay), eq(delay), eq(TimeUnit.SECONDS))).andReturn(future1).once();
			
			EasyMock.replay(newTimer);
		}
	
		DeploymentID workerManagementID = allocation.workerID;
		boolean stopWorking = allocation.winnerID == null;
		
		Object workerManagement = AcceptanceTestUtil.getBoundObject(workerManagementID);
		EasyMock.reset(workerManagement);
		
		if (allocation.isLocal()) {
			WorkerManagement localWorkerManagement = (WorkerManagement) workerManagement;
			if (stopWorking) {
				//newLogger.debug("Worker <"+workerManagementID+"> is now IDLE");
				localWorkerManagement.stopWorking();
			} else {
				localWorkerManagement.workForBroker(allocation.winnerID);
			}
		} else {
			RemoteWorkerManagement remoteWorkerManagement =	(RemoteWorkerManagement) workerManagement;
			if (stopWorking) {
				newLogger.debug("The remote worker " + allocation.workerID.getServiceID() +" does not match any request. " +
						"Disposing it back to its provider: " + rwpID.getServiceID() + ".");
				EasyMock.reset(rwp);
				rwp.disposeWorker(allocation.workerID.getServiceID());
				EasyMock.replay(rwp);
			} else {
				remoteWorkerManagement.workForBroker(allocation.rwmc, allocation.winnerID.getPublicKey());
			}
		}
		
		EasyMock.replay(workerManagement);
		EasyMock.replay(newLogger);
		
		if (rwp != null) {
			AcceptanceTestUtil.publishTestObject(application, rwpID, rwp, RemoteWorkerProvider.class);
		}	
		
		//create worker mock
		unwantedWorker(peerComponent, allocation.workerID.getServiceID(), requestSpec, mgID);
		
		EasyMock.verify(workerManagement);
		EasyMock.verify(newLogger);
		
		if (future != null) {
			EasyMock.verify(future);
		}
		if (rwp != null) {
			EasyMock.verify(rwp);
		}
		if (reschedule) {
			EasyMock.verify(newTimer);
		}
		
		peerComponent.setLogger(oldLogger);
		peerComponent.setTimer(oldTimer);
		
		return future1;
	}
	
	/**
	 * A Worker mock is created and marked as unwanted
	 * @param allocation
	 * @param requestSpec
	 * @param mgPubKey
	 * @param nodeWizID
	 * @param future
	 * @return
	 */
	public ScheduledFuture<?> unwantedMockWorker(PeerComponent peerComponent, WorkerAllocation allocation, 
			RequestSpecification requestSpec, DeploymentID mgID, boolean reschedule, ScheduledFuture<?> future) {
		
		Worker worker = EasyMock.createMock(Worker.class);
		
		DeploymentID workerID = new DeploymentID(new ContainerID("worker", "workerServer", "worker", 
				allocation.workerID.getPublicKey()),"worker");
		
		AcceptanceTestUtil.publishTestObject(application, workerID, worker, Worker.class);
		
		EasyMock.replay(worker);
		
		return unwantedWorker(peerComponent, worker, allocation, requestSpec, mgID, reschedule, future, null, null);
	}

}