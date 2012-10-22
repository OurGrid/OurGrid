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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.matchers.BeginAllocationRunnableMatcher;
import org.ourgrid.matchers.ServiceIDMatcher;
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


public class Req_121_Util extends WorkerAcceptanceUtil {
	
	public Req_121_Util(ModuleContext context) {
		super(context);
	}

	public void workForBrokerByUnknownRemotePeer(WorkerComponent component,
			RemoteWorkerManagementClient rwmc, String senderPublicKey,
			ServiceID brokerServiceID) {
		workForBrokerByRemotePeer(component, rwmc, senderPublicKey,
				brokerServiceID, false, false, false, false, false, false,
				false, null, null);
	}

	public Future<?> workForBrokerOnAllocatedForPeerWorker(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			String remotePeerPubKey, ServiceID brokerServiceID) {
		return workForBrokerByRemotePeer(component, rwmc, remotePeerPubKey,
				brokerServiceID, true, true, false, false, false, false, false,
				null, null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorker(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			String remotePeerPubKey, ServiceID brokerServiceID) {
		return workForBrokerByRemotePeer(component, rwmc, remotePeerPubKey,
				brokerServiceID, true, false, false, false, false, false,
				false, null, null);
	}

	public Future<?> workForBrokerOnWorkingWorkerAndDiffPubKey(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			String remotePeerPubKey, ServiceID brokerServiceID) {
		return workForBrokerByRemotePeer(component, rwmc, remotePeerPubKey,
				brokerServiceID, true, false, true, true, false, false, true,
				null, null);
	}

	public Future<?> workForBrokerOnWorkingWorker(WorkerComponent component,
			RemoteWorkerManagementClient rwmc, String remotePeerPubKey,
			ServiceID brokerServiceID) {
		return workForBrokerByRemotePeer(component, rwmc, remotePeerPubKey,
				brokerServiceID, true, false, true, true, false, false, false,
				null, null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorkerWithCleaningError(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			String remotePeerPubKey, ServiceID brokerServiceID,
			String playpenDirWithError) {
		return workForBrokerByRemotePeer(component, rwmc, remotePeerPubKey,
				brokerServiceID, true, false, true, true, false, false, true,
				playpenDirWithError, null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorkerWithFileOnTransfer(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			String remotePeerPubKey, ServiceID brokerServiceID,
			boolean isIncomingFile, List<TransferHandle> handles) {
		return workForBrokerByRemotePeer(component, rwmc, remotePeerPubKey,
				brokerServiceID, true, false, true, true, isIncomingFile,
				false, true, null, handles);
	}
	
	@SuppressWarnings("unchecked")
	private Future<?> workForBrokerByRemotePeer(WorkerComponent component,
			RemoteWorkerManagementClient rwmc, String remotePeerPubKey,
			ServiceID brokerServiceID, boolean isPeerKnown,
			boolean isWorkerAllocatedForPeer, boolean isWorkingState,
			boolean cleans, boolean isIncomingFile, boolean isPreparingState,
			boolean toDiffBroker, String playpenDirWithError,
			List<TransferHandle> handles) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		component.setLogger(newLogger);
		
		FileTransferManager ftm = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(ftm);
		
		RemoteWorkerManagement rWorkerManag = getRemoteWorkerManagement();
		ObjectDeployment rwmOD = getRemoteWorkerManagementDeployment();
		
		Future future = null;
		
		ExecutorService newThreadPool = EasyMock.createMock(ExecutorService.class);
		component.setExecutorThreadPool(newThreadPool);
		
		if (!isPeerKnown) {
			newLogger
					.warn("An unknown remote peer tried to command this " +
							"Worker to work for a remote consumer."
							+ " This message was ignored. Unknown remote peer public key: ["
							+ remotePeerPubKey + "].");
		} else {
			
			newLogger.info("Remote peer commanded this Worker to work for a remote consumer." +
					" Remote consumer public key: [" + brokerServiceID.getPublicKey() + "].");
			
			if (isWorkerAllocatedForPeer) {
				newLogger.debug("Status changed from " +
						"ALLOCATED_FOR_PEER to ALLOCATED_FOR_BROKER.");
			}
			
			if (!isPreparingState) {
				
				if (isWorkingState && toDiffBroker) {
					
					newLogger.debug("Worker begin allocation action, " +
							"preparing to start the working.");
					
					newThreadPool = EasyMock.createMock(ExecutorService.class);
					component.setExecutorThreadPool(newThreadPool);
					
					future = EasyMock.createMock(Future.class);
					EasyMock.expect(
							newThreadPool.submit(
									BeginAllocationRunnableMatcher.
									eqMatcher(createBeginAllocationRunnable())))
									.andReturn(future).once();
					EasyMock.replay(newThreadPool);
					
				} else {
					
					rwmc.statusChangedAllocatedForBroker((ServiceID)
							ServiceIDMatcher.eqMatcher(brokerServiceID));
				}
			}
			
			if (cleans) {
				newLogger.debug("Cleaning Worker playpen.");
			}
			
			if (playpenDirWithError != null) {
				newLogger.error("Error while trying to clean the playpen directory [" 
						+ playpenDirWithError + "].");
			}
			
			if (handles != null) {
				if(isIncomingFile) {
					for (TransferHandle handle : handles) {
						ftm.cancelIncomingTransfer((IncomingTransferHandle) handle);
						
					}
				} else {
					for (TransferHandle handle : handles) {
						ftm.cancelOutgoingTransfer((OutgoingTransferHandle) handle);
					}
				}
				EasyMock.replay(ftm);
			}
		}

		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.setExecutionContext(component, rwmOD, remotePeerPubKey);
		
		rWorkerManag.workForBroker(rwmc, brokerServiceID.getPublicKey());
		
		EasyMock.verify(newLogger);
		
		if (handles != null) {
			EasyMock.verify(ftm);
		}
		
		if (future != null) {
			EasyMock.reset(future);
			EasyMock.expect(
					future.isDone()).andReturn(false).anyTimes();
			EasyMock.replay(future);
		}
		
		component.setLogger(oldLogger);
		
		return future;
	}
	
}
