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

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.broker.communication.actions.HereIsGridProcessResultMessageHandle;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.matchers.ErrorOcurredMessageHandleMatcher;
import org.ourgrid.matchers.ExecutionCommandMessageMatcher;
import org.ourgrid.matchers.HereIsGridProcessResultMessageHandleMatcher;
import org.ourgrid.matchers.WorkerExecutionRunnableMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.controller.GridProcessError;
import org.ourgrid.worker.communication.processors.handle.RemoteExecuteMessageHandle;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_084_Util extends WorkerAcceptanceUtil{

	public Req_084_Util(ModuleContext context) {
		super(context);
	}

	public Future<?> remoteExecutionByUnknownClient(WorkerComponent component,
			Worker worker, String publicKey, String command) {
		DeploymentID wcID = new DeploymentID(new ContainerID("a", "a",
				"module", publicKey), "WorkerClient");
		return remoteExecute(component, worker, null, wcID, 0, null, command,
				false, false, false, false, false, false, false, null, null, 0);
	}

	public Future<?> remoteExecutionInANonWorkingWorker(
			WorkerComponent component, Worker worker, String publicKey,
			String command) {
		DeploymentID wcID = new DeploymentID(new ContainerID("a", "a",
				"module", publicKey), "WorkerClient");
		return remoteExecute(component, worker, null, wcID, 0, null, command,
				true, false, false, false, false, false, false, null, null, 0);
	}

	public Future<?> remoteExecutionWithConcurrencyError(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, DeploymentID clientID, long requestID,
			Map<String, String> environmentVars, String command) {
		return remoteExecute(component, worker, workerClient, clientID,
				requestID, environmentVars, command, true, true, true, false,
				false, false, false, null, null, 0);
	}

	public Future<?> remoteExecutionWithNoExecutionResult(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, DeploymentID clientID, long requestID,
			Map<String, String> environmentVars, String command, int handlerNo) {
		return remoteExecute(component, worker, workerClient, clientID,
				requestID, environmentVars, command, true, true, false, false,
				false, false, false, null, null, handlerNo);
	}

	public Future<?> remoteExecutionWithNoExecutionResultOnRemoteExecuting(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, DeploymentID clientID, long requestID,
			Map<String, String> environmentVars, String command, int handlerNo,
			String storageDir, String playpenDir) {
		return remoteExecute(component, worker, workerClient, clientID,
				requestID, environmentVars, command, true, true, false, false,
				false, false, false, playpenDir, storageDir, handlerNo);
	}

	public Future<?> remoteExecutionWithNoExecutionResultOnTaskFailed(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, DeploymentID clientID, long requestID,
			Map<String, String> environmentVars, String command, int handlerNo) {
		return remoteExecute(component, worker, workerClient, clientID,
				requestID, environmentVars, command, true, true, false, false,
				false, true, false, null, null, handlerNo);
	}

	public Future<?> remoteExecutionWithWokerInExecutionState(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, DeploymentID clientID, long requestID,
			Map<String, String> environmentVars, String command, int handlerNo) {
		return remoteExecute(component, worker, workerClient, clientID,
				requestID, environmentVars, command, true, true, false, false,
				true, false, false, null, null, handlerNo);
	}

	public Future<?> remoteExecutionWithWokerInExecutionFinishedState(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, DeploymentID clientID, long requestID,
			Map<String, String> environmentVars, String command, int handlerNo) {

		return remoteExecute(component, worker, workerClient, clientID,
				requestID, environmentVars, command, true, true, false, false,
				false, false, true, null, null, handlerNo);
	}

	public Future<?> remoteExecutionWithWokerWithIncomingFile(
			WorkerComponent component, Worker worker,
			WorkerClient workerClient, DeploymentID clientID, long requestID,
			Map<String, String> environmentVars, String command, int handlerNo) {

		return remoteExecute(component, worker, workerClient, clientID,
				requestID, environmentVars, command, true, true, false, false,
				false, false, false, null, null, handlerNo);
	}

	public Future<?> remoteExecutionWithSuccess(WorkerComponent component,
			Worker worker, WorkerClient workerClient, DeploymentID clientID,
			long requestID, Map<String, String> environmentVars,
			String command, String playpenDir, String storageDir, int handlerNo) {
		return remoteExecute(component, worker, workerClient, clientID,
				requestID, environmentVars, command, true, true, false, true,
				false, false, false, playpenDir, storageDir, handlerNo);
	}
	
	@SuppressWarnings("unchecked")
	public Future<?> remoteExecute(WorkerComponent component, Worker worker,
			WorkerClient workerClient, DeploymentID brokerID, long requestID,
			Map<String, String> environmentVars, String command,
			boolean isKnownClient, boolean isWorkerWorking,
			boolean concurrencyError, boolean releaseHandler,
			boolean isExecutingState, boolean isFileTransferError,
			boolean isExecutionFinished, String playpenDir, String storageDir,
			int handlerNo) {

		String clientPubKey = brokerID.getPublicKey();

		if (workerClient == null) {
			workerClient = EasyMock.createMock(WorkerClient.class);
			brokerID = new DeploymentID(new ContainerID("mgusername",
					"mgserver", "mgmodule", clientPubKey), "broker");

		} else {
			EasyMock.reset(workerClient);
		}

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		EasyMock.reset(newLogger);

		ExecutorService newThreadPool = EasyMock
				.createMock(ExecutorService.class);
		component.setExecutorThreadPool(newThreadPool);

		String newCommand = command.replaceAll("\\$PLAYPEN", playpenDir);
		newCommand = newCommand.replaceAll("\\$STORAGE", storageDir);

		Future future = null;

		if (!isKnownClient) {
			newLogger.warn("An unknown client tried to execute the command ["
					+ newCommand + "]. " + "Unknown client public key: ["
					+ clientPubKey + "].");
		} else if (!isWorkerWorking) {
			newLogger.warn("A client tried to execute the command ["
					+ newCommand + "] but did not send startwork message. "
					+ "Client public key: [" + clientPubKey + "].");

		} else if (isExecutingState) {
			newLogger.warn("A client tried to execute the command ["
					+ newCommand + "] but is already executing. "
					+ "Client public key: [" + clientPubKey + "].");

		} else if (isFileTransferError) {
			newLogger
					.warn("A client tried to execute the command ["
							+ newCommand
							+ "]. This message was ignored, because a error already ocurred. "
							+ "Client public key: [" + clientPubKey + "].");

		} else if (concurrencyError) {
			newLogger
					.error("A client is trying to EXECUTE more than one command simultaneously. "
							+ "RequestID: "
							+ requestID
							+ " ; command: "
							+ newCommand
							+ " ; Environment variables: "
							+ environmentVars);

			workerClient
					.sendMessage(ErrorOcurredMessageHandleMatcher
							.eqMatcher(new ErrorOcurredMessageHandle(
									new GridProcessError(
											GridProcessErrorTypes.CONCURRENT_RUNNING))));

		} else if (isExecutionFinished) {
			newLogger.warn("A client tried to execute the command ["
					+ newCommand + "] but the worker finished the execution. "
					+ "Client public key: [" + clientPubKey + "].");
		}

		else {

			if (playpenDir == null && storageDir == null) {
				newLogger.info(ExecutionCommandMessageMatcher.eqMatcher(
						environmentVars, clientPubKey, newCommand, requestID));
			} else {
				newLogger.info(ExecutionCommandMessageMatcher.eqMatcher(
						playpenDir, storageDir, clientPubKey, newCommand,
						requestID));
			}

			future = EasyMock.createMock(Future.class);

			EasyMock.expect(
					newThreadPool.submit(WorkerExecutionRunnableMatcher
							.eqMatcher(createExecutorRunnable(component,
									handlerNo)))).andReturn(future).once();

			if (releaseHandler) {
				workerClient
						.sendMessage(HereIsGridProcessResultMessageHandleMatcher
								.eqMatcher(new HereIsGridProcessResultMessageHandle(
										null)));
			}

		}

		EasyMock.replay(newLogger);
		EasyMock.replay(workerClient);
		EasyMock.replay(newThreadPool);

		ObjectDeployment workerOD = getWorkerDeployment();

		AcceptanceTestUtil.setExecutionContext(component, workerOD,
				clientPubKey);
//		AcceptanceTestUtil.publishTestObject(component, brokerID, workerClient,
//				WorkerClient.class);

		MessageHandle handle = new RemoteExecuteMessageHandle(requestID,
				command, environmentVars);
		worker.sendMessage(handle);

		if (releaseHandler) {
			getWorkerExecutionClient().executionResult(null);
		}

		EasyMock.verify(newLogger);
		EasyMock.verify(workerClient);
		EasyMock.verify(newThreadPool);

		EasyMock.reset(newLogger);

		component.setLogger(oldLogger);

		return future;
	}

}