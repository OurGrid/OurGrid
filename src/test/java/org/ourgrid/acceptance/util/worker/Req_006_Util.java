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
import org.ourgrid.matchers.ServiceIDMatcher;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


public class Req_006_Util extends WorkerAcceptanceUtil {

	public Req_006_Util(ModuleContext context) {
		super(context);
	}

	public void workForPeerOnErrorWorkerNotLogged(WorkerComponent component,
			WorkerManagementClient wmc) {
		workForPeer(component, wmc, null, null, true, true, true, false,
				false, false, false, false, true, false, false, false, null,
				null);
	}
	
	public void workForPeerOnErrorWorker(WorkerComponent component,
			WorkerManagementClient wmc) {
		workForPeer(component, wmc, null, null, true, true, true, false,
				false, false, false, false, true, true, false, false, null,
				null);
	}

	public void workForPeerOnOwnerWorker(WorkerComponent component,
			String senderPubKey) {
		workForPeer(component, null, senderPubKey, null, true, true, true,
				false, false, false, false, false, false, false, false,
				false, null, null);
	}

	public void workForPeerOnOwnerWorkerLoggedInPeer(WorkerComponent component,
			String senderPubKey) {
		workForPeer(component, null, senderPubKey, null, true, true, true,
				false, false, false, false, false, false, true, false,
				false, null, null);
	}

	public void workForPeerByNotMasterPeer(WorkerComponent component,
			String senderPubKey) {
		workForPeer(component, null, senderPubKey, null, true, false, false,
				false, false, false, false, false, false, false, false,
				false, null, null);
	}

	public void workForPeerByUnknownPeer(WorkerComponent component,
			String senderPubKey) {
		workForPeer(component, null, senderPubKey, null, false, false, false,
				false, false, false, false, false, false, false, false,
				false, null, null);
	}

	public void workForPeerOnIdleWorker(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey,
			String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, false, true, false, false, false, false, false,
				false, false, null, null);
	}

	public void workForPeerOnIdleWorkerLoggedPeer(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey,
			String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, false, true, false, false, false, false, true,
				false, false, null, null);
	}

	public void workForPeerOnPreparingWorker(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey,
			String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, false, true, false, false, true, false, false,
				false, false, null, null);
	}

	public void workForPeerOnPreparingWorkerLoggedPeer(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, false, true, false, false, true, false, true,
				false, false, null, null);
	}

	public void workForPeerOnAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey, boolean cleans) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, true, false, cleans, false, false, false, false,
				false, false, null, null);
	}

	public void workForPeerOnAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, true, false, false, false, false, false, true,
				false, false, null, null);
	}

	public void workForPeerOnAllocatedForRemoteBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, true, false, false, false, false, false, true,
				true, false, null, null);
	}

	public void workForPeerOnRemoteAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, true, false, false, false, false, false, false,
				true, false, null, null);
	}

	public Future<?> workForPeerOnAllocatedForBrokerWorkerCleaning(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, false,
				false, false, false, false, null, null);
	}

	public void workForPeerOnAllocatedForBrokerWorkerWithCleaningError(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey,
			String playpenDirWithError) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, true, false, true, false, false, false, false,
				false, false, playpenDirWithError, null);
	}

	public Future<?> workForPeerOnRemoteAllocatedForBrokerWorkerWithCleaningErrorAndFileOnTransfer(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey,
			String playpenDirWithError, List<TransferHandle> handles) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, true, false,
				false, false, true, false, playpenDirWithError, handles);
	}

	public Future<?> workForPeerOnRemoteAllocatedForBrokerWorkerWithFileOnTranfer(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey,
			boolean isIncomingFile, List<TransferHandle> handles) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, false,
				false, false, true, false, null, handles);
	}

	public Future<?> workForPeerOnAllocatedForBrokerWorkerOnWorkingState(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, false,
				false, true, false, false, null, null);
	}

	public Future<?> workForPeerOnRemoteDownloadsFinishedState(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, false,
				false, true, true, false, null, null);
	}

	public Future<?> workForPeerOnRemoteExecuteState(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey,
			String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, false,
				false, true, true, false, null, null);
	}

	public Future<?> workForPeerOnRemoteExecutingState(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, false,
				false, true, true, false, null, null);
	}

	public Future<?> workForPeerOnAllocatedForBrokerWorkerOnWorkingStateWithIncomingFile(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, true, false, false,
				true, false, false, null, null);
	}

	public Future<?> workForPeerOnAllocatedForRemoteBrokerWorkerOnWorkingStateWithIncomingFile(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, true, false,
				false, true, true, false, null, null);
	}

	public Future<?> workForPeerOnRemoteAllocatedForBrokerWorkerOnWorkingState(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, false,
				false, false, true, false, null, null);
	}

	public Future<?> workForPeerOnRemoteAllocatedForBrokerWorkerOnErrorState(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, false,
				true, false, true, false, null, null);
	}

	public Future<?> workForPeerOnRemoteAllocatedForBrokerWorkerOnPreparingState(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, true,
				false, false, true, false, null, null);
	}

	public void workForPeerOnAllocatedForPeerWorker(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey,
			String remotePeerPubKey, boolean cleans) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, false, false, cleans, false, false, false, false,
				true, false, null, null);
	}

	public void workForPeerOnAllocatedForPeerWorker(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey,
			String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, false, false, false, false, false, false, true,
				true, false, null, null);
	}

	public void workForPeerOnPreparingAllocatedForPeerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, false, false, false, false, true, false, true,
				true, false, null, null);
	}

	public void workForPeerOnPreparingAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, true, false, false, false, true, false, true,
				false, false, null, null);
	}

	public void workForPeerOnPreparingAllocatedForRemoteBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, true, false, false, false, true, false, true,
				true, false, null, null);
	}

	public Future<?> workForPeerOnRemoteWorkingWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String senderPubKey, String remotePeerPubKey) {
		return workForPeer(component, wmc, senderPubKey, remotePeerPubKey,
				true, true, false, true, false, true, false, false,
				false, true, true, false, null, null);
	}

	public void workForPeerOnLocalTaskFailedWorker(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey,
			String remotePeerPubKey) {
		workForPeer(component, wmc, senderPubKey, remotePeerPubKey, true, true,
				false, true, false, true, false, false, false, true,
				false, true, null, null);
	}
	
	
	private Future<?> workForPeer(WorkerComponent component,
			WorkerManagementClient wmc, String senderPubKey,
			String remotePeerPubKey, boolean isPeerKnown, boolean isPeerMaster,
			boolean isWorkerOwner,
			boolean isAllocatedForBrokerState, boolean isWorkerIdle,
			boolean isWorkingState, boolean isIncomingFile,
			boolean isPreparingState, boolean isErrorState, boolean isLogged,
			boolean isAllocatedForPeer, boolean isFileTransferError,
			String playpenDirWithError, List<TransferHandle> handles) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);

		component.setLogger(newLogger);

		ExecutorService newThreadPool = null;
		Future prepFuture = null;

		FileTransferManager ftm = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(ftm);
		
		if (!isLogged) {
			newLogger.warn("This Worker is not logged at peer.");
		} else {
			if(!isPeerKnown) { 
				newLogger.warn("An unknown peer tried to command this Worker to work for a remote peer. " +
						"This message was ignored. Unknown peer public key: [" + senderPubKey + "].");
			} else {
				if(!isPeerMaster) { 
					newLogger.debug("The master Peer tried to manage this Worker before" +
							" setting itself as manager of this Worker. This message was ignored.");
				} else {
					
					if (isAllocatedForBrokerState && !isAllocatedForPeer) { 
						newLogger.warn("Strange behavior: This Worker was allocated to a local consumer, " +
								"but was now commanded to work for a remote peer.");
					}
					
					if (isErrorState) { 
						newLogger.warn("Peer commanded this Worker to work for a remote peer," +
								" but it's on error state.");
						wmc.statusChanged(WorkerStatus.ERROR);
					} else {
						
						if(isWorkerOwner) {
							newLogger.debug("This Worker was commanded to work for a remote peer, " +
									"but it is in the OWNER status. This message was ignored.");
						} else {
							newLogger.info("Peer commanded this Worker to work for a remote peer. " +
									"Remote peer public key: [" + remotePeerPubKey + "].");
							
							if (!isPreparingState) {
								
								if (!isWorkingState) {
									
									if (isAllocatedForBrokerState) {
										newLogger.debug("Status changed from ALLOCATED_FOR_BROKER to ALLOCATED_FOR_PEER.");
									} else if (isWorkerIdle) {
										newLogger.debug("Status changed from IDLE to ALLOCATED_FOR_PEER.");
									}
									
									EasyMock.reset(wmc);
									wmc.statusChangedAllocatedForPeer((ServiceID) 
											ServiceIDMatcher.eqMatcher(createRemoteWorkerManagementServiceID()),
											EasyMock.matches(remotePeerPubKey));
								} else {
									
									newLogger.debug("Cleaning Worker playpen.");
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
							}
							
							
							if (handles != null) {
								if(isIncomingFile) {
									for (TransferHandle handle : handles) {
										ftm.cancelIncomingTransfer((IncomingTransferHandle) handle);
									}
								}
								
								if (playpenDirWithError != null) {
									newLogger.error("Error while trying to clean the playpen" +
											" directory [" + playpenDirWithError + "].");
								}
								
								if (handles != null) {
									if(isIncomingFile) {
										for (TransferHandle handle : handles) {
											ftm.cancelIncomingTransfer((IncomingTransferHandle) handle);
										}
									} else {
										for (TransferHandle handle : handles) {
											ftm.cancelOutgoingTransfer((OutgoingTransferHandle)handle);
										}
									}
									EasyMock.replay(ftm);
								}
							}
						}
					}
				}
				
			}
		}


		if (wmc != null) {
			EasyMock.replay(wmc);
		}

		EasyMock.replay(newLogger);

		DeploymentID peerID = getServiceManager().getStubDeploymentID(wmc);

		if (peerID == null) {
			peerID = new DeploymentID(new ContainerID("peerUser", "peerServer",
					PeerConstants.MODULE_NAME, senderPubKey), "peerObj");
		}

		WorkerManagement workerManag = getWorkerManagement();
		ObjectDeployment wmOD = getWorkerManagementDeployment();

		AcceptanceTestUtil.setExecutionContext(component, wmOD, peerID);

		if (isWorkerOwner) {
			workerManag.workForPeer(senderPubKey);
		} else {
			workerManag.workForPeer(remotePeerPubKey);
		}			

		EasyMock.verify(newLogger);

		if (prepFuture != null) {
			EasyMock.reset(prepFuture);
			EasyMock.expect(
					prepFuture.isDone()).andReturn(false).anyTimes();
			EasyMock.replay(prepFuture);
		}

		if (wmc != null) {
			EasyMock.verify(wmc);
			EasyMock.reset(wmc);
		}

		if (handles != null) {
			EasyMock.verify(ftm);
			EasyMock.reset(ftm);
		}

		if (newThreadPool != null) {
			EasyMock.verify(newThreadPool);
		}

		component.setLogger(oldLogger);

		return prepFuture;
	}

}