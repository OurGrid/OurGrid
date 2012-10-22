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
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.matchers.BeginAllocationRunnableMatcher;
import org.ourgrid.matchers.ServiceIDMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.messages.ExecutionControllerMessages;

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

public class Req_092_Util extends WorkerAcceptanceUtil{

	public Req_092_Util(ModuleContext context) {
		super(context);
	}
	
	public void workForBrokerOnErrorWorker(WorkerComponent component,
			WorkerManagementClient wmc) {
		workForBroker(component, wmc, null, null, false, true, false, false,
				false, false, false, false, true, true, false, null, null);
	}

	public void workForBrokerOnErrorWorkerNotLogged(WorkerComponent component,
			WorkerManagementClient wmc) {
		workForBroker(component, wmc, null, null, false, true, false, false,
				false, false, false, false, true, false, false, null, null);
	}

	public void workForBrokerOnOwnerWorker(WorkerComponent component,
			WorkerManagementClient wmc) {
		workForBroker(component, wmc, null, null, false, true, false, false,
				false, false, false, false, false, false, false, null, null);
	}

	public void workForBrokerOnOwnerWorkerLoggedInPeer(
			WorkerComponent component, WorkerManagementClient wmc) {
		workForBroker(component, wmc, null, null, false, true, false, false,
				false, false, false, false, false, true, false, null, null);
	}

	public Future<?> workForBrokerOnIdleWorker(WorkerComponent component,
			WorkerManagementClient wmc, String brokerPubKey) {
		return workForBroker(component, wmc, null, null, true, false, false,
				false, false, false, false, false, false, false, false, null,
				null);
	}

	public void workForBrokerOnIdleWorkerLoggedOnPeer(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		workForBroker(component, wmc, null, brokerPubKey, true, false, false,
				false, false, false, false, false, false, true, false, null,
				null);
	}

	public Future<?> workForBrokerOnPreparingWorker(WorkerComponent component,
			WorkerManagementClient wmc, String brokerPubKey) {
		return workForBroker(component, wmc, null, null, true, false, false,
				false, false, false, false, true, false, false, false, null,
				null);
	}

	public Future<?> workForBrokerOnPreparingWorkerLoggedPeer(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, null, true, false, false,
				false, false, false, false, true, false, true, true, null, null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorkerWithCleaning(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, false, true, false, false, false, false, true, true,
				null, null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, false, false, false, false, false, false, true, true,
				null, null);
	}

	public Future<?> workForBrokerOnPreparingAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, false, false, false, false, true, false, true, true,
				null, null);
	}

	public Future<?> workForBrokerOnAllocatedForRemoteBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, false, false, true, false, false, true, true,
				null, null);
	}

	public Future<?> workForBrokerOnPreparingAllocatedForRemoteBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, false, false, false, true, true, false, true, true,
				null, null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey, boolean cleans) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, false, cleans, false, false, false, false, true, true,
				null, null);
	}

	public Future<?> workForBrokerOnWorkingWorker(WorkerComponent component,
			WorkerManagementClient wmc, String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, true, false, true, false, false, true, true, null,
				null);
	}

	public Future<?> workForRemoteBrokerOnWorkingWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, true, false, true, false, false, true, true, null,
				null);
	}

	public Future<?> workForBrokerOnWorkingWorkerAndDiffPubKey(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, true, false, true, false, false, false, false,
				null, null);
	}

	public Future<?> workForBrokerOnLocalExecute(WorkerComponent component,
			WorkerManagementClient wmc, String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, true, false, true, false, false, true, true, null,
				null);
	}

	public Future<?> workForBrokerOnLocalExecuting(WorkerComponent component,
			WorkerManagementClient wmc, String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, true, false, true, false, false, true, true, null,
				null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerAndPreparingState(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, false, false, true, true, false, false, true,
				null, null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorkerWithFileOnTransfer(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey, boolean isIncomingTransfer,
			List<TransferHandle> handles) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, true, isIncomingTransfer, true, false, false,
				false, true, null, handles);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorkerWithCleaningError(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey, String playpenDirWithError) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, true, false, false, false, false, false, true,
				playpenDirWithError, null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorkerWithCleaningErrorAndDiffPubKey(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey, String playpenDirWithError) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, true, false, true, false, false, false, true,
				playpenDirWithError, null);
	}

	public Future<?> workForBrokerOnAllocatedForBrokerWorkerWithCleaningErrorAndFileOnTransfer(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey, String playpenDirWithError,
			List<TransferHandle> handles) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				false, true, true, true, true, false, false, false, true,
				playpenDirWithError, handles);
	}

	public Future<?> workForBrokerOnAllocatedForPeerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				true, false, false, false, false, false, false, true, false,
				null, null);
	}

	public Future<?> workForBrokerOnAllocatedForPeerWorkerAndPreparingState(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				true, false, false, false, false, true, false, false, false,
				null, null);
	}

	public Future<?> workForBrokerOnPreparingAllocatedForPeerWorkerAndPreparingState(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				true, false, false, false, true, true, false, true, false,
				null, null);
	}

	public Future<?> workForBrokerOnAllocatedForPeerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			String brokerPubKey, boolean cleans) {
		return workForBroker(component, wmc, null, brokerPubKey, false, false,
				true, false, cleans, false, false, false, false, false, false,
				null, null);
	}

	public void workForBrokerBeforeSetPeer(WorkerComponent component) {
		workForBroker(component, null);
	}

	public void workForBroker(WorkerComponent component, String senderPublicKey) {
		workForBroker(component, null, senderPublicKey, null, false, false,
				false, false, false, false, false, false, false, false, false,
				null, null);
	}

	@SuppressWarnings("unchecked") 
	private Future<?> workForBroker(WorkerComponent component,
			WorkerManagementClient wmc, String senderPublicKey,
			String brokerPubKey, boolean isWorkerIdle, boolean isWorkerOwner,
			boolean isWorkerAllocatedForPeer, boolean isWorkingState,
			boolean cleans, boolean isIncomingFile, boolean toDiffBroker,
			boolean isPreparingState, boolean isErrorState, boolean isLogged,
			boolean isAllocatedForBroker, String playpenDirWithError,
			List<TransferHandle> handles) {


		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		DeploymentID brokerID = new DeploymentID(new ContainerID("username", "server", "module", brokerPubKey), "broker");

		WorkerManagement workerManag = getWorkerManagement();
		ObjectDeployment wmOD = getWorkerManagementDeployment();

		ServiceID workerServiceID = createWorkerServiceID();

		FileTransferManager ftm = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(ftm);

		ExecutorService newThreadPool = null;
		Future future = null;

		if (senderPublicKey != null) {
			newLogger.warn("An unknown peer tried to command this Worker to work for a local consumer. This message was ignored." +
					" Unknown peer public key: [" + senderPublicKey + "].");
		} else {
			if (wmc != null) {

				if (!isLogged) {
					newLogger.warn("This Worker is not logged at peer.");
				} else {
					if (isErrorState) {
						newLogger.warn("Peer commanded this Worker to work for a local consumer, but it's on error state.");
						wmc.statusChanged(WorkerStatus.ERROR);
					} else {
						if (isWorkerIdle && !isPreparingState) {
							newLogger.info("Peer commanded this Worker to work for a local consumer." +
									" Local consumer public key: [" +  brokerPubKey + "].");
							newLogger.debug("Status changed from IDLE to ALLOCATED_FOR_BROKER.");
							wmc.statusChangedAllocatedForBroker(workerServiceID,
									brokerPubKey);

						} else {

							if (!isWorkerOwner) {
								newLogger.info("Peer commanded this Worker to work for a local consumer." +
										" Local consumer public key: [" +  brokerPubKey + "].");

								if (cleans) {
									newLogger.debug("Cleaning Worker playpen.");
								} 

								if (playpenDirWithError != null) {
									newLogger.error("Error while trying to clean the playpen directory [" + playpenDirWithError +"].");	
								}

								if (handles != null) {
									if (isIncomingFile) {
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

								if (!isPreparingState) {

									if (isWorkerAllocatedForPeer) {
										newLogger.debug("Status changed from ALLOCATED_FOR_PEER to ALLOCATED_FOR_BROKER.");
									}

									if (isWorkingState && toDiffBroker) {

										newLogger.debug("Worker begin allocation action, preparing to start the working.");

										newThreadPool = EasyMock.createMock(ExecutorService.class);
										component.setExecutorThreadPool(newThreadPool);

										future = EasyMock.createMock(Future.class);

										EasyMock.expect(
												newThreadPool.submit(
														BeginAllocationRunnableMatcher.eqMatcher(createBeginAllocationRunnable())))
														.andReturn(future).once();
										EasyMock.replay(newThreadPool);
									} else {
										wmc.statusChangedAllocatedForBroker(workerServiceID,
												brokerPubKey);
									}
								} 

							} else {
								newLogger.debug("This Worker was commanded to work for a local consumer," +
										" but it is in the OWNER status. This message was ignored.");
							}
						}
					}
				}
				EasyMock.replay(wmc);
			} else {
				newLogger.debug("The master Peer tried to manage this Worker before setting itself as manager of this Worker." +
						" This message was ignored.");
			}
		}

		EasyMock.replay(newLogger);

		DeploymentID peerID = new DeploymentID(new ContainerID("peerUser", "peerServer", "peer", 
				simulateAuthentication()), "broker");

		AcceptanceTestUtil.setExecutionContext(component, wmOD, peerID);

		workerManag.workForBroker(brokerID);

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

		if (wmc != null) {
			EasyMock.verify(wmc);
			EasyMock.reset(wmc);
		}

		if (newThreadPool != null) {
			EasyMock.verify(newThreadPool);
			EasyMock.reset(newThreadPool);
		}

		component.setLogger(oldLogger);

		return future;
	}

	public void prepareAllocationCompletedOnOwnerWorker(
			WorkerComponent component, Future<?> future) {
		prepareAllocationCompleted(component, null, null, null, true, null,
				null, false, future, false, WorkerStatus.OWNER);
	}

	public void prepareAllocationCompletedOnPreparingWithPeerWorker(
			WorkerComponent component) {
		prepareAllocationCompleted(component, null, null, null, true, null,
				null, false, null, true, null);
	}
	
	public void prepareAllocationCompletedOnPreparingLoggedPeerWorker(
			WorkerComponent component, WorkerManagementClient wmc) {
		prepareAllocationCompleted(component, wmc, null, null, true, null,
				null, false, null, true, null);
	}

	public void prepareAllocationCompletedOnPreparingWorker(
			WorkerComponent component) {
		prepareAllocationCompleted(component, null, null, null, true, null,
				null, true, null, true, null);
	}

	public void prepareAllocationCompletedOnIdleWorker(WorkerComponent component) {
		prepareAllocationCompleted(component, null, null, null, true, null,
				null, true, null, false, WorkerStatus.IDLE);
	}

	public void prepareAllocationCompletedOnErrorWorker(
			WorkerComponent component) {
		prepareAllocationCompleted(component, null, null, null, true, null,
				null, true, null, false, WorkerStatus.ERROR);
	}

	public void prepareAllocationCompletedWithFailedPeer(
			WorkerComponent component) {
		prepareAllocationCompleted(component, null, null, null, true, null,
				null, true, null, false, WorkerStatus.OWNER);
	}

	public void prepareAllocationCompleted(WorkerComponent component,
			WorkerManagementClient wmc) {
		prepareAllocationCompleted(component, wmc, null, null, true, null,
				null, false, null, false, WorkerStatus.OWNER);
	}

	public void prepareAllocationCompleted(WorkerComponent component,
			Future<?> future) {
		prepareAllocationCompleted(component, null, null, null, false, null,
				null, false, future, false, WorkerStatus.OWNER);
	}

	public void prepareAllocationCompleted(WorkerComponent component,
			WorkerManagementClient wmc, Future<?> future) {
		prepareAllocationCompleted(component, wmc, null, null, true, null,
				null, false, future, true, WorkerStatus.OWNER);
	}

	public void prepareAllocationCompletedOnRemoteAllocatedForBrokerWorker(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			Future<?> future, WorkerStatus oldStatus) {
		prepareAllocationCompleted(component, null, rwmc, oldStatus, false,
				null, null, false, future, false, WorkerStatus.OWNER);
	}

	public void prepareAllocationCompletedOnRemoteExecuteWorker(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			Future<?> future, WorkerStatus oldStatus) {
		prepareAllocationCompleted(component, null, rwmc, oldStatus, false,
				null, null, false, future, false,
				WorkerStatus.ALLOCATED_FOR_BROKER);
	}

	public void prepareAllocationCompletedOnRemoteExecutingWorker(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			Future<?> future, WorkerStatus oldStatus) {
		prepareAllocationCompleted(component, null, rwmc, oldStatus, false,
				null, null, false, future, false,
				WorkerStatus.ALLOCATED_FOR_BROKER);
	}

	public void prepareAllocationCompletedOnRemoteAllocatedForBrokerWorker(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			Future<?> future) {
		prepareAllocationCompleted(component, null, rwmc, null, false, null,
				null, false, future, false, WorkerStatus.OWNER);
	}
	
	public void prepareAllocationCompletedOnAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			Future<?> future, WorkerStatus oldStatus) {
		prepareAllocationCompleted(component, wmc, null, oldStatus, false,
				null, null, false, future, false,
				WorkerStatus.ALLOCATED_FOR_BROKER);
	}

	public void prepareAllocationCompletedOnAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			Future<?> future) {
		prepareAllocationCompleted(component, wmc, null, null, false, null,
				null, false, future, false, WorkerStatus.ALLOCATED_FOR_BROKER);
	}

	public void prepareAllocationCompletedOnRemoteWorkingWorker(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			Future<?> future) {
		prepareAllocationCompleted(component, null, rwmc, null, false, null,
				null, false, future, false, WorkerStatus.ALLOCATED_FOR_BROKER);
	}

	public void prepareAllocationCompletedOnAllocatedForPeerWorker(
			WorkerComponent component, WorkerManagementClient wmc,
			Future<?> future) {
		prepareAllocationCompleted(component, wmc, null, null, false, null,
				null, false, future, false, WorkerStatus.ALLOCATED_FOR_PEER);
	}

	public void prepareAllocationCompletedOnPreparingAllocatedForPeerWorker(
			WorkerComponent component, WorkerManagementClient wmc, String peerPubKey,
			Future<?> future) {
		prepareAllocationCompleted(component, wmc, null,
				WorkerStatus.ALLOCATED_FOR_BROKER, false, peerPubKey, null, false,
				future, true, null);
	}

	public void prepareAllocationCompletedOnAllocatedForPeerWorker(
			WorkerComponent component, WorkerManagementClient wmc, String peerPubKey,
			Future<?> future, WorkerStatus oldStatus) {
		prepareAllocationCompleted(component, wmc, null, oldStatus, false,
				peerPubKey, null, false, future, false, WorkerStatus.OWNER);
	}

	public void prepareAllocationCompletedOnPreparingAllocatedForBrokerWorker(
			WorkerComponent component, WorkerManagementClient wmc, String brokerPubKey,
			Future<?> future) {
		prepareAllocationCompleted(component, wmc, null, null, false, null,
				brokerPubKey, false, future, true, WorkerStatus.ALLOCATED_FOR_BROKER);
	}

	public void prepareAllocationCompletedOnPreparingAllocatedForRemoteBrokerWorker(
			WorkerComponent component, RemoteWorkerManagementClient rwmc,
			Future<?> future) {
		prepareAllocationCompleted(component, null, rwmc, null, false, null,
				null, false, future, true, WorkerStatus.ALLOCATED_FOR_BROKER);
	}

	public void prepareAllocationCompleted(WorkerComponent component, WorkerManagementClient wmc,
			RemoteWorkerManagementClient rwmc, WorkerStatus oldStatus, boolean toIdle,
			String remotePeerPubKey, String brokerPubKey, boolean peerFailed,
			Future<?> future, boolean isPreparingAllocationState, WorkerStatus status) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		if(!isPreparingAllocationState) {
			newLogger.warn(ExecutionControllerMessages.
			getNotInPrepareAllocationStateMessage(WorkerDAOFactory.getInstance().
					getWorkerStatusDAO().getStatus().toString()));
		}

		if (future != null) {
			EasyMock.reset(future);
			EasyMock.expect(
					future.isDone()).andReturn(true).anyTimes();
			EasyMock.replay(future);
		}

		WorkerStatus newStatus = null;

		if (wmc != null) {
			EasyMock.reset(wmc);

			
			if (!peerFailed) {
				
				if (toIdle) {
					wmc.statusChanged(WorkerStatus.IDLE);
					
				} else if (remotePeerPubKey != null){
					newStatus = WorkerStatus.ALLOCATED_FOR_PEER;
					wmc.statusChangedAllocatedForPeer(createRemoteWorkerManagementServiceID(), remotePeerPubKey);
					
				} else if (brokerPubKey != null){
					newStatus = WorkerStatus.ALLOCATED_FOR_BROKER;				
					wmc.statusChangedAllocatedForBroker(createWorkerServiceID(), brokerPubKey);
					
				} 
			}
			EasyMock.replay(wmc);
		}
		
		if (rwmc != null){
			newStatus = WorkerStatus.ALLOCATED_FOR_BROKER;
			EasyMock.reset(rwmc);
			rwmc.statusChangedAllocatedForBroker(createWorkerServiceID());
			EasyMock.replay(rwmc);
		}
		
		if (oldStatus != null) {
			newLogger.debug("Status changed from " + oldStatus + " to " + newStatus + ".");
		}
		
		if (isPreparingAllocationState) {
			newLogger.debug("Allocation action was completed.");
		}

		EasyMock.replay(newLogger);

		getWorkerExecutionClient().readyForAllocation();

		if (wmc != null && isPreparingAllocationState) {
			EasyMock.verify(wmc);
			EasyMock.reset(wmc);
		}

		if (rwmc != null && isPreparingAllocationState) {
			EasyMock.verify(rwmc);
			EasyMock.reset(rwmc);
		}

		EasyMock.verify(newLogger);
		component.setLogger(oldLogger);
	}

}
