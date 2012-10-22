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
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.matchers.BeginAllocationRunnableMatcher;
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_091_Util extends WorkerAcceptanceUtil {
	
	public Req_091_Util(ModuleContext context) {
		super(context);
	}

	public void stopWorkingByUnknownPeer(WorkerComponent component, String senderPubKey) {
		stopWorking(component, null, senderPubKey, false, false, false,
				false, false, false, false, false, false, null, null);
	}
	
	public void stopWorkingByUnsetMasterPeer(WorkerComponent component, String senderPubKey) {
		stopWorking(component, null, senderPubKey, true, false, false,
				false, false, false, false, false, false, null, null);
	}
	
	public void stopWorkingWithoutBeingWorking(WorkerComponent component, String senderPubKey) {
		stopWorking(component, null, senderPubKey, true, true, false,
				false, false, false, false, false, false, null, null);
	}
	
	public void stopWorkingWithoutBeingWorkingNotLogged(WorkerComponent component, String senderPubKey) {
		stopWorking(component, null, senderPubKey, true, false, false,
				false, false, false, false, false, false, null, null);
	}
	
	public void stopWorkingOnAllocatedForBrokerWorker(WorkerComponent component,
			WorkerManagementClient wmc,	String senderPubKey) {
		stopWorking(component, wmc, senderPubKey, true, true, true,
				false, true, false, false, false, false, null, null);
	}
	
	public Future<?> stopWorkingOnWorkingWorker(WorkerComponent component,
			WorkerManagementClient wmc,	String senderPubKey) {
		return stopWorking(component, wmc, senderPubKey, true, true,
				true, true, true, true, false, false, false, null, null);
	}
	
	public Future<?> stopWorkingOnRemoteWorkingWorker(WorkerComponent component,
			WorkerManagementClient wmc,	String senderPubKey) {
		return stopWorking(component, wmc, senderPubKey, true, true, true,
				true, false, false, false, false, false, null, null);
	}
	
	public Future<?> stopWorkingOnPreparingState(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey) {
		return stopWorking(component, wmc, senderPubKey, true, true, true, true,
				true, false, false, true, false, null, null);
	}
	
	public Future<?> stopWorkingOnErrorState(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey) {
		return stopWorking(component, wmc, senderPubKey, true, true, true, true,
				true, false, false, true, true,	null, null);
	}
	
	public Future<?> stopWorkingOnErrorStateAndNotLogged(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey) {
		return stopWorking(component, wmc, senderPubKey, true, false, true, true,
				true, false, false, true, true,	null, null);
	}
	
	public Future<?> stopWorkingOnAllocatedForBrokerWorker(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey, boolean cleans,
			List<TransferHandle> handles) {
		return stopWorking(component, wmc, senderPubKey, true, true, true, true,
				true, cleans, false, false, false, null, handles);
	}
	
	public Future<?> stopWorkingOnAllocatedForBrokerWorkerWithCleaningError(WorkerComponent component,
			WorkerManagementClient wmc,	String senderPubKey, String playpenDirWithError) {
		return stopWorking(component, wmc, senderPubKey, true, true, true, true,
				false, true, false, false, false, playpenDirWithError, null);
	}
	
	public Future<?> stopWorkingOnAllocatedForBrokerWorkerWithFileOnTranfer(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, boolean isIncomingFile, List<TransferHandle> handles) {
		return stopWorking(component, wmc, senderPubKey, true, true, true, true,
				true, true,	isIncomingFile, false, false, null, handles);
	}

	public void stopWorkingOnAllocatedForPeerWorker(WorkerComponent component,
			WorkerManagementClient wmc,	String senderPubKey) {
		stopWorking(component, wmc, senderPubKey, true, true, true, false, false,
				false, false, false, false, null, null);
	}
	
	@SuppressWarnings("unchecked")
	private Future<?> stopWorking(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey,
			boolean isPeerKnown, boolean isLogged, boolean isPeerAllocated,
			boolean isWorking, boolean isConsumerLocal, boolean cleans,
			boolean isIncomingFile, boolean isPreparingState,
			boolean isErrorState, String playpenDirWithError,
			List<TransferHandle> handles) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		if (wmc != null) {
			EasyMock.reset(wmc);
		}
		
		FileTransferManager ftm = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(ftm);
		
		WorkerManagement workerManagement = getWorkerManagement();
		ObjectDeployment workerManagementOD = getWorkerManagementDeployment();
		
		ExecutorService newThreadPool = null;
		Future future = null;
		
		if (!isLogged) {
			newLogger.warn("This Worker is not logged at peer.");
		} else {
			if (!isPeerKnown) {
				newLogger.warn("An unknown peer tried to command this Worker to stop working. " +
						"This message was ignored. Unknown peer public key: [" + senderPubKey + "].");
			} else {
			
				if (!isPeerAllocated) {
					newLogger.warn("This Worker was commanded to stop working, but it's" +
							" not working for any client. This message was ignored.");
				} else {
					
					if (isErrorState) {
						newLogger.warn("The master Peer tried to manage this Worker," +
								" but it's on error state.");
						wmc.statusChanged(WorkerStatus.ERROR);
					} else {
						
						newLogger.info("This Worker was commanded to stop working" +
								" for the current client.");
						
						if (!isPreparingState) {
							if (isWorking) {
								
								newLogger.debug("Worker begin allocation action," +
										" preparing to start the working.");
								
								newThreadPool = EasyMock.createMock(ExecutorService.class);
								component.setExecutorThreadPool(newThreadPool);
								
								future = EasyMock.createMock(Future.class);
								EasyMock.expect(
										newThreadPool.submit(
												BeginAllocationRunnableMatcher.eqMatcher(
														createBeginAllocationRunnable())))
												.andReturn(future).once();
								EasyMock.replay(newThreadPool);
							} else {
								
								if (isConsumerLocal) {
									newLogger.debug("Status changed from" +
											" ALLOCATED_FOR_BROKER to IDLE.");
								} else {
									newLogger.debug("Status changed from" +
											" ALLOCATED_FOR_PEER to IDLE.");
								}
								wmc.statusChanged(WorkerStatus.IDLE);
							}
						}
					}
					
					if (cleans) {
						newLogger.debug("Cleaning Worker playpen.");
					}
					
					if(playpenDirWithError != null) {
						newLogger.error("Error while trying to clean the playpen directory [" +
								playpenDirWithError + "].");
					}
					if(handles != null) {
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
			}
		}
		
		if (wmc != null) {
			EasyMock.replay(wmc);
		}

		EasyMock.replay(newLogger);
		
		DeploymentID peerID = new DeploymentID(new ContainerID("peerUser", "peerServer",
				"peerModule", senderPubKey), "peerObj");
		
		AcceptanceTestUtil.setExecutionContext(component, workerManagementOD, peerID);
		
		workerManagement.stopWorking();
		
		EasyMock.verify(newLogger);
		
		if (handles != null) {
			EasyMock.verify(ftm);
		}	
		
		if (wmc != null) {
			EasyMock.verify(wmc);
			EasyMock.reset(wmc);
		}
		
		if (newThreadPool != null) {
			EasyMock.verify(newThreadPool);
		}
		
		component.setLogger(oldLogger);
		
		return future;
	}

}
