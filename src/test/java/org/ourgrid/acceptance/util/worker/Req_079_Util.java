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

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.broker.communication.actions.WorkerIsReadyMessageHandle;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.matchers.ErrorOcurredMessageHandleMatcher;
import org.ourgrid.matchers.MessageHandleMatcher;
import org.ourgrid.matchers.StartWorkErrorMessageMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.controller.GridProcessError;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_079_Util extends WorkerAcceptanceUtil {

	public Req_079_Util(ModuleContext context) {
		super(context);
	}

	public void startWorkByUnknownClient(WorkerComponent component,
			Worker worker, String senderPubKey, DeploymentID brokerID) {
		startWork(component, worker, senderPubKey, brokerID, null, true, false,
				false, false, false, null, null);
	}

	public void startWorkWithPlaypenError(WorkerComponent component,
			Worker worker, DeploymentID brokerID, String problematicDir) {
		startWork(component, worker, brokerID.getPublicKey(), brokerID,
				problematicDir, false, true, true, false, false, null, null);
	}

	public void startWorkWithCleaningError(WorkerComponent component,
			Worker worker, DeploymentID brokerID, String problematicDir,
			String playpenDirWithError) {
		startWork(component, worker, brokerID.getPublicKey(), brokerID,
				problematicDir, false, true, true, true, false,
				playpenDirWithError, null);
	}

	public void startWorkWithStorageError(WorkerComponent component,
			Worker worker, DeploymentID brokerID, String problematicDir) {
		startWork(component, worker, brokerID.getPublicKey(), brokerID,
				problematicDir, false, true, false, false, false, null, null);
	}

	public WorkerClient startWorkSuccessfully(WorkerComponent component,
			Worker worker, DeploymentID brokerID) {
		return startWork(component, worker, brokerID.getPublicKey(), brokerID,
				null, false, false, false, false, false, null, null);
	}

	public WorkerClient startWorkSuccessfullyCleaning(
			WorkerComponent component, Worker worker, DeploymentID brokerID,
			List<TransferHandle> handles) {
		return startWork(component, worker, brokerID.getPublicKey(), brokerID,
				null, false, false, false, true, false, null, handles);
	}

	public WorkerClient startWorkSuccessfullyCleaning(
			WorkerComponent component, Worker worker, DeploymentID brokerID) {
		return startWork(component, worker, brokerID.getPublicKey(), brokerID,
				null, false, false, false, true, false, null, null);
	}

	public WorkerClient startWorkSuccessfullyWithFileOnTransfer(
			WorkerComponent component, Worker worker, DeploymentID brokerID,
			boolean isIncomingFile, List<TransferHandle> handles) {
		return startWork(component, worker, brokerID.getPublicKey(), brokerID,
				null, false, false, false, true, isIncomingFile, null, handles);
	}

	public WorkerClient startWork(WorkerComponent component, Worker worker,
			String senderPubKey, DeploymentID brokerID, String problematicDir,
			boolean isClientUnknown, boolean ioError, boolean playpenError,
			boolean cleans, boolean isIncomingFile, String playpenDirWithError,
			List<TransferHandle> handles) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		WorkerClient workerClient = EasyMock.createMock(WorkerClient.class);
		AcceptanceTestUtil.publishTestObject(application, brokerID,
				workerClient, WorkerClient.class);

		FileTransferManager fileTransferManager = EasyMock
				.createMock(FileTransferManager.class);
		component.setFileTransferManager(fileTransferManager);

		ServiceID brokerServiceID = brokerID.getServiceID();

		if (isClientUnknown) {
			newLogger.warn("The unknown client [" + brokerServiceID
					+ "] tried to start the work of this Worker. "
					+ "This message was ignored. Unknown client public key: ["
					+ senderPubKey + "].");
		} else {

			if (cleans) {
				newLogger.debug("Cleaning Worker playpen.");
			}

			if (playpenDirWithError != null) {
				newLogger
						.error("Error while trying to clean the playpen directory ["
								+ playpenDirWithError + "].");
			}

			if (handles != null) {
				if (isIncomingFile) {
					for (TransferHandle handle : handles) {
						fileTransferManager
								.cancelIncomingTransfer((IncomingTransferHandle) handle);
					}
				} else {
					for (TransferHandle handle : handles) {
						fileTransferManager
								.cancelOutgoingTransfer((OutgoingTransferHandle) handle);
					}
				}
			}

			if (ioError) {
				newLogger.error(StartWorkErrorMessageMatcher.eqMatcher(
						brokerServiceID, problematicDir, playpenError));
				workerClient.sendMessage(ErrorOcurredMessageHandleMatcher
						.eqMatcher(new ErrorOcurredMessageHandle(
								new GridProcessError(
										GridProcessErrorTypes.IO_ERROR))));
			} else {
				newLogger.info("Worker is ready to start working for client ["
						+ brokerServiceID + "].");
				workerClient
						.sendMessage((WorkerIsReadyMessageHandle) (MessageHandleMatcher
								.eqMatcher(new WorkerIsReadyMessageHandle())));
			}
		}

		EasyMock.replay(workerClient);
		EasyMock.replay(newLogger);
		EasyMock.replay(fileTransferManager);

		AcceptanceTestUtil.setExecutionContext(component,
				getWorkerDeployment(), brokerID);

		worker.startWork(workerClient, 0, null);

		EasyMock.verify(newLogger);
		EasyMock.verify(workerClient);
		EasyMock.verify(fileTransferManager);

		component.setLogger(oldLogger);

		return workerClient;
	}
}
