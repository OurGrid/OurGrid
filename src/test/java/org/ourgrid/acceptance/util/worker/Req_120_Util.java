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
import org.ourgrid.matchers.ControlOperationResultMatcher;
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleStoppedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_120_Util extends WorkerAcceptanceUtil{

	public Req_120_Util(ModuleContext context) {
		super(context);
	}

	public void stopWorkerWithAllocation(WorkerComponent component, Future<?> future) {
		WorkerControl workerControl = getWorkerControl();
		stopWorker(component, workerControl, null, true, false, false, null, null, future);
	}	
	
	public void stopWorkerOnWorkingState(WorkerComponent component, Future<?> future) {
		WorkerControl workerControl = getWorkerControl();
		stopWorker(component, workerControl, null, true, true, false, null, null, future);
	}	

	public void stopWorker(WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		stopWorker(component, workerControl, null, true, false, false, null, null, null);
	}

	public void stopWorkerWithCleaning(WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		stopWorker(component, workerControl, null, true, true, false, null, null, null);
	}

	public void stopWorker(WorkerComponent component, String senderPublicKey) {
		WorkerControl workerControl = getWorkerControl();
		stopWorker(component, workerControl, senderPublicKey, true, false,
				false, null, null, null);
	}

	public void stopWorkerWithFileOnTransfer(WorkerComponent component,
			boolean isIncomingFile, List<TransferHandle> handles) {
		WorkerControl workerControl = getWorkerControl();
		stopWorker(component, workerControl, null, true, true, isIncomingFile,
				null, handles, null);
	}

	public void stopWorkerWithCleaningError(WorkerComponent component,
			String problematicDir) {
		WorkerControl workerControl = getWorkerControl();
		stopWorker(component, workerControl, null, true, true, false,
				problematicDir, null, null);
	}

	public void stopUnstartedWorker(WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		stopWorker(component, workerControl, null, false, false, false, null,
				null, null);
	}

	public void stopStoppedWorker(WorkerComponent component,
			WorkerControl workerControl, String wcPublicKey) {
		stopWorker(component, workerControl, wcPublicKey, false, false, false,
				null, null, null);
	}

	public void stopWorker(WorkerComponent component, WorkerControl workerControl, 
			String senderPublicKey,	boolean isWorkerStarted, boolean cleans,
			boolean isIncomingFile,	String problematicDir, List<TransferHandle> handles,
			Future<?> prepFuture) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		WorkerControlClient workerControlClientMock = EasyMock.
				createMock(WorkerControlClient.class);

		FileTransferManager ftm = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(ftm);

		ObjectDeployment wcOD = getWorkerControlDeployment();
		
		if (!isWorkerStarted) {
			if(component.getContainerDAO().isStopped()) {
				workerControlClientMock.operationSucceed(
						ControlOperationResultMatcher.
								eqType(ModuleStoppedException.class));
			} else {
				senderPublicKey = wcOD.getDeploymentID().getPublicKey();
				workerControlClientMock.operationSucceed(
						ControlOperationResultMatcher.
								eqType(ModuleNotStartedException.class));
			}
		} else{
			
			if (senderPublicKey == null) {
				senderPublicKey = wcOD.getDeploymentID().getPublicKey();
				workerControlClientMock.operationSucceed(
						ControlOperationResultMatcher.noError());
				if (prepFuture == null) {
					newLogger.debug("Shutdowning Executor");
				}
				newLogger.info("Worker has been successfully shutdown.");

				if (prepFuture != null) {
					EasyMock.reset(prepFuture);
					EasyMock.expect(prepFuture.isDone()).andReturn(false).once();
					EasyMock.expect(prepFuture.cancel(true)).andReturn(true).once();
					EasyMock.replay(prepFuture);
					newLogger.debug("Allocation action was cancelled.");
				}

				if (cleans) {
					newLogger.debug("Cleaning Worker playpen.");
					if(problematicDir != null) {
						newLogger.error("Error while trying to clean the playpen directory [" 
								+ problematicDir + "].");
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
			} else if(!wcOD.getDeploymentID().getPublicKey().equals(senderPublicKey)) {
				newLogger.warn("An unknown entity tried to stop the Worker. Only the local" +
						" modules can perform this operation." +
						" Unknown entity public key: [" + senderPublicKey + "].");
			}
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(workerControlClientMock);

		AcceptanceTestUtil.setExecutionContext(component, wcOD, senderPublicKey);
		workerControl.stop(false, false, workerControlClientMock);

		if (handles != null) {
			EasyMock.verify(ftm);
		}

		EasyMock.verify(newLogger);
		EasyMock.verify(workerControlClientMock);

		component.setLogger(oldLogger);
	}

}
