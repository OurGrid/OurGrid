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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.matchers.BeginAllocationRunnableMatcher;
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_122_Util extends WorkerAcceptanceUtil {

	public Req_122_Util(ModuleContext context) {
		super(context);
	}

	public void unknownPeerFails(WorkerComponent component, DeploymentID unknownPeerID)  {
		masterPeerFails(component, null, unknownPeerID, true, false, false, false, false, null, null);
	}

	public void peerFailsBeforeSetPeer(WorkerComponent component, DeploymentID peerID) {
		masterPeerFails(component, null, peerID, false, true, false, false, false, null, null);
	}

	public void peerFailsAfterSetPeer(WorkerComponent component, WorkerManagementClient wmc, DeploymentID wmcID) {
		masterPeerFails(component, wmc, wmcID, false, false, false, false, false, null, null);
	}
	
	public Future<?> peerFailsWithCleaningWorker(WorkerComponent component, WorkerManagementClient wmc, DeploymentID wmcID) {
		return masterPeerFails(component, wmc, wmcID, false, false, true, false, true, null, null);
	}
	
	public Future<?> peerFailsAndWorkerHasIncomingTransfer(WorkerComponent component, WorkerManagementClient wmc, DeploymentID wmcID,
			List<TransferHandle> handles) {
		return masterPeerFails(component, wmc, wmcID, false, false, true, true, true, null, handles);
	}

	public Future<?> peerFailsAndWorkerHasOutgoingTransfer(WorkerComponent component, WorkerManagementClient wmc, DeploymentID wmcID,
			List<TransferHandle> handles) {
		return masterPeerFails(component, wmc, wmcID, false, false, true, false, true, null, handles);
	}

	public Future<?> peerFailsAndWorkerIsExecutingACommand(WorkerComponent component, WorkerManagementClient wmc, 
			DeploymentID wmcID, Future<?> executionFuture) {
		return masterPeerFails(component, wmc, wmcID, false, false, true, false, true, executionFuture, null);
	}

	private Future<?> masterPeerFails(WorkerComponent component, WorkerManagementClient wmc, DeploymentID peerID,
			boolean unknownPeer, boolean isPeerUndefined, boolean cleans,
			boolean hasIncomingTransfer, boolean isWorking, Future<?> executionFuture, List<TransferHandle> handles) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		component.setLogger(newLogger);

		if (wmc == null) {
			wmc = EasyMock.createMock(WorkerManagementClient.class);
			EasyMock.replay(wmc);
		}
		
		FileTransferManager ftm = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(ftm);
		
		ExecutorService newThreadPool = null;
		Future prepFuture = null;
		
		if (unknownPeer) {
			newLogger.warn("The unknown peer [" + peerID + "] has failed. This message was ignored.");
		} else {
			
			if (isPeerUndefined) {
				newLogger.warn("The peer [" + peerID + "] that didn't set itself as manager of this Worker has failed. This message was ignored.");
			} else {
				
				if (cleans) {
					newLogger.debug("Cleaning Worker playpen.");
				}
				
				if (isWorking) {
					
					newLogger.debug("Worker begin allocation action, preparing to start the working.");
					
					newThreadPool = EasyMock.createMock(ExecutorService.class);
					component.setExecutorThreadPool(newThreadPool);
					
					prepFuture = EasyMock.createMock(Future.class);
					EasyMock.expect(
							newThreadPool.submit(
									BeginAllocationRunnableMatcher.eqMatcher(createBeginAllocationRunnable())))
									.andReturn(prepFuture).once();
					EasyMock.replay(newThreadPool);
					
				}
				
				if (executionFuture != null) {
					EasyMock.expect(executionFuture.cancel(true)).andReturn(true);
					EasyMock.replay(executionFuture);
				}
				
				if (handles != null) {
					if(hasIncomingTransfer) {
						for (TransferHandle handle : handles) {
							ftm.cancelIncomingTransfer((IncomingTransferHandle) handle);
						}
					} else {
						for (TransferHandle handle : handles) {
							ftm.cancelOutgoingTransfer((OutgoingTransferHandle) handle);
						}
					}
				}
				
				newLogger.warn("The master peer [" + peerID + "] has failed. Worker will interrupt the working," +
						" it means cancel any transfer or execution.");
			}
		}
		EasyMock.replay(newLogger);
		
//		getMasterPeerMonitor(component).doNotifyFailure(wmc, peerID);
		
		EasyMock.verify(newLogger);
		
		if (executionFuture != null) {
			EasyMock.verify(executionFuture);
		}
		
		if (prepFuture != null) {
			EasyMock.reset(prepFuture);
			EasyMock.expect(
					prepFuture.isDone()).andReturn(false).anyTimes();
			EasyMock.replay(prepFuture);
		}
		
		component.setLogger(oldLogger);
		
		return prepFuture;
	}

	public boolean isWorkerInterestedOnMasterPeerFailure(WorkerComponent component, DeploymentID peerID) {
		return AcceptanceTestUtil.isInterested(component, peerID.getServiceID(), getMasterPeerMonitorDeployment(component).getDeploymentID());
	}

}
