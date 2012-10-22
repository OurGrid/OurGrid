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
package org.ourgrid.acceptance.util.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.matchers.BeginAllocationRunnableMatcher;
import org.ourgrid.matchers.RequestRepetitionRunnableMatcher;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


public class Req_090_Util extends WorkerAcceptanceUtil {

	public Req_090_Util(ModuleContext context) {
		super(context);
	}

	public WorkerManagementClient setUnknownPeer(WorkerComponent component, DeploymentID peerID) {
		WorkerManagementClient wmc = createWorkerManagementClient(peerID);
		setPeer(component, peerID, true, null, false, false, false, false, false, wmc);
		
		return wmc;
	}
	
	public WorkerManagementClient setKnownPeer(WorkerComponent component, DeploymentID peerID, WorkerStatus workerStatus) {
		WorkerManagementClient wmc = createWorkerManagementClient(peerID);
		setPeer(component, peerID, false, workerStatus, false, false, false, false, false, wmc);
		return wmc;
	}
	
	public WorkerManagementClient setKnownPeerOnErrorState(WorkerComponent component, DeploymentID peerID, WorkerStatus workerStatus) {
		WorkerManagementClient wmc = createWorkerManagementClient(peerID);
		setPeer(component, peerID, false, workerStatus, false, false, false, false, true, wmc);
		return wmc;
	}
	
	public WorkerManagementClient setKnownPeerOnPreparingState(WorkerComponent component, DeploymentID peerID) {
		WorkerManagementClient wmc = createWorkerManagementClient(peerID);
		setPeer(component, peerID, false, null, false, false, false, true, false, wmc);
		return wmc;
	}
	
	public Future<?> setKnownPeerOnWorkingWorker(WorkerComponent component,
			DeploymentID peerID, WorkerStatus workerStatus, WorkerManagementClient wmc) {
		return setKnownPeerOnWorkingWorker(component, peerID, workerStatus, false, wmc);
	}
	
	public Future<?> setKnownPeerOnWorkingWorker(WorkerComponent component,
			DeploymentID peerID, WorkerStatus workerStatus, boolean cleans, WorkerManagementClient wmc) {
		return setPeer(component, peerID, false, workerStatus, false, true, false, cleans, false, wmc);
	}
	
	public WorkerManagementClient setPeerByMasterPeerFromSameLocation(WorkerComponent component, DeploymentID peerID,
			WorkerStatus status, WorkerManagementClient wmc) {
		setPeer(component, peerID, false, status, true, false, false, false, false, wmc);
		return wmc;
	}
	
	public WorkerManagementClient setPeerByMasterPeerFromDiffLocation(WorkerComponent component, DeploymentID peerID,
			WorkerStatus status) {
		WorkerManagementClient wmc = createWorkerManagementClient(peerID);
		setPeer(component, peerID, false, status, false, false, false, false, false, wmc);
		return wmc;
	}
	
	public WorkerManagementClient setPeerByMasterPeerFromDiffLocationAndOnPreparingState(WorkerComponent component, DeploymentID peerID,
			WorkerStatus status) {
		WorkerManagementClient wmc = createWorkerManagementClient(peerID);
		setPeer(component, peerID, false, status, false, false, false, true, false, wmc);
		return wmc;
	}
	
	public WorkerManagementClient setPeerByMasterPeerFromDiffLocationOnWorkingWorker(WorkerComponent component, DeploymentID peerID,
			WorkerStatus status) {
		WorkerManagementClient wmc = createWorkerManagementClient(peerID);
		setPeer(component, peerID, false, status, false, true, true, false, false, wmc);
		return wmc;
	}
	
	public WorkerManagementClient setPeerByMasterPeerFromDiffLocationOnErrorState(WorkerComponent component, DeploymentID peerID,
			WorkerStatus status) {
		WorkerManagementClient wmc = createWorkerManagementClient(peerID);
		setPeer(component, peerID, false, status, false, false, false, false, true, wmc);
		return wmc;
	}
	
	@SuppressWarnings("unchecked")
	private Future<?> setPeer(WorkerComponent component, DeploymentID peerID, boolean isPeerUnknown,
			WorkerStatus workerStatus, boolean isMasterPeerHasSameDeployId, boolean isWorkingState,
			boolean cleans, boolean isPreparingState, boolean isErrorState, WorkerManagementClient wmc) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		EasyMock.reset(wmc);
		
		ScheduledExecutorService newTimer = EasyMock.createMock(ScheduledExecutorService.class);;
		ScheduledExecutorService oldTimer = component.getTimer();
		
		component.setTimer(newTimer);
		
		ExecutorService newThreadPool = null;
		Future prepFuture = null;
		
		if(isPeerUnknown) {
			newLogger.warn("The unknown peer [" + peerID + "] tried to set itself as manager of this Worker." +
					" This message was ignored. Unknown peer public key: [" + peerID.getPublicKey() + "].");
		} else if (isMasterPeerHasSameDeployId){
			newLogger.warn("The peer [" + peerID + "] set itself as manager of this Worker. This message was ignored." +
			" Because the master peer did not notify fail.");
		} else {

			newLogger.info("The peer [" + peerID + "] set itself as manager of this Worker.");
			
			if (cleans) {
				newLogger.debug("Cleaning Worker playpen.");
			}
			
			if (isErrorState) {
				newLogger.warn("The master Peer tried to manage this Worker, but it's on error state.");
				wmc.statusChanged(WorkerStatus.ERROR);
			} else {
				
				if (!isPreparingState) {
					if (isWorkingState) {
						
						newLogger.debug("Worker begin allocation action, preparing to start the working.");
						
						newThreadPool = EasyMock.createMock(ExecutorService.class);
						component.setExecutorThreadPool(newThreadPool);
						
						prepFuture = EasyMock.createMock(Future.class);
						EasyMock.expect(
								newThreadPool.submit(
										BeginAllocationRunnableMatcher.eqMatcher(createBeginAllocationRunnable())))
										.andReturn(prepFuture).once();
						EasyMock.replay(newThreadPool);
						
					} else {
						
						wmc.statusChanged(workerStatus);
					}
				}
				
				ScheduledFuture scheduledFuture = EasyMock.createMock(ScheduledFuture.class);
				
				EasyMock.expect(newTimer.scheduleWithFixedDelay(
						RequestRepetitionRunnableMatcher.eqMatcher(
								createReportWorkAccountingRunnable(component)), EasyMock.eq(WorkerConstants.REPORT_WORK_ACCOUNTING_TIME),
								EasyMock.eq(WorkerConstants.REPORT_WORK_ACCOUNTING_TIME),
								EasyMock.eq(TimeUnit.SECONDS))).andReturn(scheduledFuture);
			}
		}

		EasyMock.replay(wmc);
		EasyMock.replay(newLogger);
		EasyMock.replay(newTimer);
		
//		WorkerManagement workerManag = getWorkerManagement();
		ObjectDeployment wmOD = getWorkerManagementDeployment();
		
		AcceptanceTestUtil.setExecutionContext(component, wmOD, peerID.getPublicKey());
		
		DeploymentID accID = new DeploymentID(new ContainerID("acc", "accServer", DiscoveryServiceConstants.MODULE_NAME, "dsPK"),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		DeploymentID workerSpecListenerID = new DeploymentID(new ContainerID("peerUser", "peerServer",
				PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME, "listenerPk"),
				DiscoveryServiceConstants.DS_OBJECT_NAME);		
		
//		workerManag.setPeer(wmc);

		if (newThreadPool != null) {
			EasyMock.verify(newThreadPool);
		}
		
		EasyMock.verify(wmc);
		EasyMock.verify(newLogger);
		
		EasyMock.reset(wmc);
		
		EasyMock.verify(newTimer);
		
		component.setTimer(oldTimer);
		component.setLogger(oldLogger);
		
		return prepFuture;
	}
	
}