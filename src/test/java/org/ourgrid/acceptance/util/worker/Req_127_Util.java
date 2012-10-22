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
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.matchers.BeginAllocationRunnableMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.communication.receiver.RemoteWorkerManagementReceiver;
import org.ourgrid.worker.communication.receiver.WorkerManagementReceiver;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_127_Util extends WorkerAcceptanceUtil {

	public Req_127_Util(ModuleContext context) {
		super(context);
	}

	public void notifyPeerFailureAtOwnerWorker(WorkerComponent component,
			DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, true, false, false, false,
				false, null, null);
	}

	public void notifyPeerFailureAtIdleWorker(WorkerComponent component,
			DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, true, false, false, false,
				false, null, null);
	}

	public void notifyPeerFailureAtPreparingWorker(WorkerComponent component,
			DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, true, false, false, false,
				false, null, null);
	}

	public void notifyPeerFailureAtPreparingWithPeerWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, false, false, false, true,
				false, null, null);
	}
	
	public void notifyPeerFailureAtIdleWithPeerWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, false, false, false, false,
				false, null, null);
	}

	public void notifyPeerFailureAtOwnerWithPeerWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, false, false, false, true,
				false, null, null);
	}

	public void notifyPeerFailureAtErrorWorker(WorkerComponent component,
			DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, true, false, false, true,
				false, null, null);
	}

	public void notifyPeerFailureAtErrorWithPeerWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, false, false, false, true,
				false, null, null);
	}

	public void notifyPeerFailureAtWorkerAtWorkingState(
			WorkerComponent component, DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, false, true, true, true,
				false, null, null);
	}

	public void notifyPeerFailureAtWorkerAtRemoteDownloadingState(
			WorkerComponent component, DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, false, true, true, true,
				true, null, null);
	}
	
	public void notifyPeerFailureAtWorkerAtPreparingAllocatedForBroker(
			WorkerComponent component, DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, false, true, false, false,
				false, null, null);
	}

	public void notifyPeerFailureAtPreparingAllocatedForRemoteBrokerWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyPeerFailure(component, peerID, false, false, true, false, false,
				false, null, null);
	}

	/**
	 * Creates a WorkerManagement mock and notifies its failure
	 * @param workerDeploymentID The objectID assigned to the WorkerManagement mock
	 */
	private void notifyPeerFailure(WorkerComponent component,
			DeploymentID peerID, boolean isUnknownPeer,
			boolean undefinedMasterPeer, boolean isAllocated,
			boolean isWorkingState, boolean cleans, boolean isIncomingFile,
			String playpenDirWithError, List<TransferHandle> handles) {
		
		WorkerManagementClient peerMock = EasyMock.createMock(WorkerManagementClient.class);
		EasyMock.replay(peerMock);

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		FileTransferManager ftm = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(ftm);
		
		ExecutorService newThreadPool = null;
		Future future = null;
		
		if (isUnknownPeer) {
			newLogger.warn("The unknown peer [" + peerID + 
					"] has failed. This message was ignored.");
			
		} else {
			
			if (isAllocated) {
				verifyInterruptWorking(cleans,
						isIncomingFile, playpenDirWithError, handles, newLogger, ftm);
			}
			
			if (isWorkingState) {
				newLogger.debug("Worker begin allocation action," +
						" preparing to start the working.");
				
				newThreadPool = EasyMock.createMock(ExecutorService.class);
				component.setExecutorThreadPool(newThreadPool);
				
				future = EasyMock.createMock(Future.class);
				EasyMock.expect(
						newThreadPool.submit(
								BeginAllocationRunnableMatcher.
								eqMatcher(createBeginAllocationRunnable())))
								.andReturn(future).once();
				EasyMock.replay(newThreadPool);		
			}
			
			newLogger.warn("The master peer [" + peerID + "] has" +
					" failed. Worker will interrupt the working," +
					" it means cancel any transfer or execution.");		
		}
		
		EasyMock.replay(newLogger);

		WorkerManagementReceiver peerMonitor = getPeerMonitor();
	    peerMonitor.doNotifyFailure(peerMock, peerID);
	    component.setStubDown(peerMock);
	    
	    if (handles != null) {
			EasyMock.verify(ftm);
		}
		if (newThreadPool != null) {
			EasyMock.verify(newThreadPool);
		}
	    EasyMock.verify(peerMock);
	    EasyMock.verify(newLogger);
	    
	    component.setLogger(oldLogger);
	    
	}
	
	public void notifyRemotePeerFailureAtAllocatedForPeerWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyRemotePeerFailure(component, peerID, false, false, true, false,
				false, false, null, null);
	}
	
	public void notifyRemotePeerFailureAtWorkingWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyRemotePeerFailure(component, peerID, false, false, true, true,
				true, false, null, null);
	}
	
	public void notifyRemotePeerFailureAtRemoteWorkingWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyRemotePeerFailure(component, peerID, false, false, true, true,
				true, false, null, null);
	}
	
	public void notifyRemotePeerFailureAtAllocatedForRemoteBrokerWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyRemotePeerFailure(component, peerID, false, false, true, false,
				false, false, null, null);
	}

	public void notifyRemotePeerFailureAtRemoteDownloadingWorker(
			WorkerComponent component, DeploymentID peerID) {
		notifyRemotePeerFailure(component, peerID, false, false, false, false,
				true, false, null, null);
	}

	public void notifyRemotePeerFailureAtPreparingAllocatedForRemoteBroker(
			WorkerComponent component, DeploymentID peerID) {
		notifyRemotePeerFailure(component, peerID, false, false, true, false,
				false, false, null, null);
	}
	
	/**
	 * Creates a WorkerManagement mock and notifies its failure
	 * @param workerDeploymentID The objectID assigned to the WorkerManagement mock
	 */
	private void notifyRemotePeerFailure(WorkerComponent component,
			DeploymentID peerID, boolean isUnknownPeer,
			boolean undefinedMasterPeer, boolean isAllocatedForRemotePeer,
			boolean isWorkingState, boolean cleans, boolean isIncomingFile,
			String playpenDirWithError, List<TransferHandle> handles) {

		RemoteWorkerManagementClient peerMock = EasyMock.createMock(
				RemoteWorkerManagementClient.class);
		EasyMock.replay(peerMock);

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		FileTransferManager ftm = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(ftm);
		
		ExecutorService newThreadPool = null;
		Future future = null;
		
		if (isUnknownPeer) {
			newLogger.warn("The unknown remote peer [" + peerID + 
					"] has failed. This message was ignored.");
			
		} else if (undefinedMasterPeer) {
			newLogger.warn("The remote peer [" + peerID + 
					"] that didn't set itself as manager of this Worker has failed." +
					" This message was ignored.");
			
		} else {
			
			if (isAllocatedForRemotePeer) {
				verifyInterruptWorking(cleans, isIncomingFile, playpenDirWithError,
						handles, newLogger, ftm);
			}
			
			if (isWorkingState) {
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
			}
			
			newLogger.warn("The remote peer [" + peerID + "] has failed. " +
					"Worker will interrupt the working," +
					" it means cancel any transfer or execution.");		
		}
		
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.publishTestObject(component, peerID,
				peerMock, RemoteWorkerManagementClient.class);

		AcceptanceTestUtil.setExecutionContext(component, 
				getRemoteWorkerManagementDeployment(), peerID);

		RemoteWorkerManagementReceiver peerMonitor = getRemoteWorkerManagementReceiver();
	    peerMonitor.workerManagementClientIsDown(peerMock, peerID);
	    component.setStubDown(peerMock);
	    
	    if (handles != null) {
			EasyMock.verify(ftm);
		}
		if (newThreadPool != null) {
			EasyMock.verify(newThreadPool);
		}
	    EasyMock.verify(peerMock);
	    EasyMock.verify(newLogger);
	    
	    component.setLogger(oldLogger);

	}

	private void verifyInterruptWorking(boolean cleans, boolean isIncomingFile,
			String playpenDirWithError, List<TransferHandle> handles,
			CommuneLogger newLogger, FileTransferManager ftm) {
		
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