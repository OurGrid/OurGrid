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
import java.util.concurrent.Future;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.common.interfaces.control.WorkerControl;
import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.matchers.ControlOperationResultMatcher;
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleStoppedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_087_Util extends WorkerAcceptanceUtil{

	public Req_087_Util(ModuleContext context) {
		super(context);
	}

	public void pauseIdleWorker(WorkerComponent component) {
		pauseIdleWorker(component, null);
	}
	
	public void pauseIdleWorker(Future<?> prepFuture, WorkerComponent component,
			WorkerManagementClient wmc) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false, false,
				null, WorkerStatus.IDLE, null, null, prepFuture);
	}
	
	public void pausePreparingWorker(Future<?> prepFuture, WorkerComponent component,
			WorkerManagementClient wmc) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false, false,
				null, WorkerStatus.IDLE, null, null, prepFuture);
	}
	
	public void pauseIdleWorker(Future<?> prepFuture, WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false, false,
				null, WorkerStatus.IDLE, null, null, prepFuture);
	}
	
	public void pauseOwnerWorker(WorkerComponent component) {
		pauseOwnerWorker(component, null);
	}

	public void pauseIdleWorkerWithoutCleaning(WorkerComponent component,
			WorkerManagementClient wmc) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false, false,
				wmc, WorkerStatus.IDLE, null, null, null);
	}
	
	public void pauseIdleWorkerCleaning(WorkerComponent component,
			WorkerManagementClient wmc) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, true, false,
				wmc, WorkerStatus.IDLE, null, null, null);
	}

	public void pauseIdleWorker(WorkerComponent component, String senderPublicKey) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, senderPublicKey, workerControl, true,
				true, false, false, null, WorkerStatus.IDLE, null, null, null);
	}

	public void pauseOwnerWorker(WorkerComponent component, String senderPublicKey) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, senderPublicKey, workerControl,
				true, true, false, false, null, WorkerStatus.OWNER, null, null, null);
	}
	
	public void pauseAllocatedForBrokerWorkerWithCleaningError(WorkerComponent component,
			WorkerManagementClient wmc, String playpenDirWithError) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, true,
				false, wmc, WorkerStatus.ALLOCATED_FOR_BROKER,
				playpenDirWithError, null, null);
	}
	
	public void pauseAllocatedForBrokerWorkerWithFileOnTransfer(
			WorkerComponent component, WorkerManagementClient wmc, boolean isIncomingFile,
			List<TransferHandle> handles) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, true, isIncomingFile,
				wmc, WorkerStatus.ALLOCATED_FOR_BROKER, null, handles, null); 
	}
	
	public void pauseAllocatedForPeerWorker(WorkerComponent component,
			WorkerManagementClient wmc,	Future<?> prepFuture) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false, false, wmc,
				WorkerStatus.ALLOCATED_FOR_PEER, null, null, prepFuture);
	}
	
	public void pausePreparingAllocatedForPeerWorker(WorkerComponent component,
			WorkerManagementClient wmc,	Future<?> prepFuture) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false, false, wmc,
				WorkerStatus.ALLOCATED_FOR_BROKER, null, null, prepFuture);
	}
	
	public void pauseAllocatedForBrokerWorkerCleaning(
			WorkerComponent component, WorkerManagementClient wmc) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, true, false,
				wmc, WorkerStatus.ALLOCATED_FOR_BROKER, null, null, null);

	}

	public void pauseUnstartedWorker(WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, false, false, false,
				null, null, null, null, null);
	}

	public void pauseStoppedWorker(WorkerComponent component, WorkerControl workerControl) {
		pauseWorker(component, null, workerControl, true, false, false, false,
				null, null, null, null, null);
	}
	
	public void pauseWorkerByUnknownEntity(WorkerComponent component, String senderPubKey) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, senderPubKey, workerControl, false, false, false,
				false, null, null, null, null, null);
	}
	
	public void pauseWorkerOnPreparingState(WorkerComponent component,
			WorkerStatus oldStatus, Future<?> prepFuture) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false, false,
				null, oldStatus, null, null, prepFuture);
	}
	
	public void pauseWorkerOnWorkingState(WorkerComponent component,
			WorkerManagementClient wmc, Future<?> prepFuture) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true,
				true, false, wmc, WorkerStatus.ALLOCATED_FOR_BROKER,
				null, null, prepFuture);
	}
	
	public void pauseWorkerOnLocalExecuteState(WorkerComponent component,
			WorkerManagementClient wmc,	Future<?> prepFuture) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, true,
				false, wmc, WorkerStatus.ALLOCATED_FOR_BROKER, null, null, prepFuture);
	}
	
	public void pauseWorkerOnLocalExecutingState(WorkerComponent component,
			WorkerManagementClient wmc,	Future<?> prepFuture) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, true,
				false, wmc, WorkerStatus.ALLOCATED_FOR_BROKER, null, null, prepFuture);
	}
	
	public void pauseWorkerOnLocalExecutionFinishedState(WorkerComponent component,
			WorkerManagementClient wmc,	Future<?> prepFuture) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, true,
				false, wmc, WorkerStatus.ALLOCATED_FOR_BROKER, null, null, prepFuture);
	}
	
	public void pauseErrorWorker(Future<?> prepFuture, WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false, false,
				null, WorkerStatus.ERROR, null, null, prepFuture);
	}
	
	public void pausePreparingAllocatedForBrokerWorker(WorkerComponent component,
			WorkerManagementClient wmc, Future<?> prepFuture) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false, false,
				wmc, WorkerStatus.ALLOCATED_FOR_BROKER, null, null, prepFuture);
	}

	public void pauseAllocatedForBrokerWorker(WorkerComponent component,
			WorkerManagementClient wmc, Future<?> prepFuture) {
		WorkerControl workerControl = getWorkerControl();
		pauseWorker(component, null, workerControl, true, true, false,
				false, wmc, WorkerStatus.ALLOCATED_FOR_BROKER, null, null, prepFuture);
	}	
	
	private void pauseWorker(WorkerComponent component, String senderPublicKey,
			WorkerControl workerControl, boolean isEntityKnown,
			boolean isWorkerStarted, boolean cleans, boolean isIncomingFile,
			WorkerManagementClient wmc, WorkerStatus oldStatus,
			String playpenDirWithError, List<TransferHandle> handles,
			Future<?> prepareAllocationFuture) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		component.setLogger(newLogger);
		
		EasyMock.reset(newLogger);
		
		ObjectDeployment wcOD = getWorkerControlDeployment();

		FileTransferManager ftm = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(ftm);
		
		WorkerControlClient workerControlClientMock = EasyMock.createMock(WorkerControlClient.class);

		if(!isEntityKnown) {
			newLogger.warn("An unknown entity tried to pause the Worker. " +
					"Only the local modules can perform this operation." +
					" Unknown entity public key: [" + senderPublicKey + "].");
		} else {
			if (!isWorkerStarted) {

				if (component.getContainerDAO().isStopped()) {
					workerControlClientMock.operationSucceed(
							ControlOperationResultMatcher.eqType(ModuleStoppedException.class));
				} else {
					workerControlClientMock.operationSucceed(
							ControlOperationResultMatcher.eqType(ModuleNotStartedException.class));
				}
			} else {
				
				if (senderPublicKey == null) {
					senderPublicKey = wcOD.getDeploymentID().getPublicKey();
				}
				
				workerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());

				if(wmc != null) {
					EasyMock.reset(wmc);
					wmc.statusChanged(WorkerStatus.OWNER);
					EasyMock.replay(wmc);
				}

				if(oldStatus != WorkerStatus.OWNER && oldStatus != WorkerStatus.ERROR) {
					newLogger.info("Worker has been PAUSED.");
					newLogger.debug("Status changed from " + oldStatus + " to OWNER.");
					
					if (prepareAllocationFuture != null) {
						EasyMock.reset(prepareAllocationFuture);
						EasyMock.expect(prepareAllocationFuture.isDone()).andReturn(false);
						EasyMock.expect(prepareAllocationFuture.cancel(true)).andReturn(true);
						EasyMock.replay(prepareAllocationFuture);
						newLogger.debug("Allocation action was cancelled.");
					}
					
					if(cleans) {
						newLogger.debug("Cleaning Worker playpen.");
					}
					
					if(playpenDirWithError != null) {
						newLogger.error("Error while trying to clean the playpen directory ["
									+ playpenDirWithError +"].");
					}
					
					if(handles != null){
						if(handles != null) {
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
					}
				}
			}
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(workerControlClientMock);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, senderPublicKey);
		DeploymentID clientID = new DeploymentID(new ServiceID("a", "b", "c", "d"));
		createStub(workerControlClientMock, WorkerControlClient.class, clientID);
		workerControl.pause(workerControlClientMock);

		EasyMock.verify(newLogger);
		EasyMock.verify(workerControlClientMock);

		if(wmc != null) {
			EasyMock.verify(wmc);
			EasyMock.reset(wmc);
			EasyMock.replay(wmc);
		}

		if (handles != null) {
			EasyMock.verify(ftm);
			EasyMock.reset(ftm);
		}
		
		if (prepareAllocationFuture != null) {
			EasyMock.verify(prepareAllocationFuture);
			EasyMock.reset(prepareAllocationFuture);
		}
		
		EasyMock.reset(workerControlClientMock);
		EasyMock.reset(newLogger);
		
		component.setLogger(oldLogger);
	}

}
