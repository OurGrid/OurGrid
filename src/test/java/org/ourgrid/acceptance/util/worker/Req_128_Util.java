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

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.broker.communication.actions.HereIsGridProcessResultMessageHandle;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.matchers.ErrorOcurredMessageHandleMatcher;
import org.ourgrid.matchers.HereIsGridProcessResultMessageHandleMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.controller.GridProcessError;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_128_Util extends WorkerAcceptanceUtil{

	public Req_128_Util(ModuleContext context) {
		super(context);
	}

	public void executionResultOnPreparingWorker(WorkerComponent component) {
		executionResult(component, true, false, null, null);
	}

	public void executionResultOnOwnerWorker(WorkerComponent component) {
		executionResult(component, true, false, null, null);
	}

	public void executionResultOnIdleWorker(WorkerComponent component) {
		executionResult(component, true, false, null, null);
	}
	
	public void executionResultOnRemoteDownloaginWorker(
			WorkerComponent component, DeploymentID brokerID) {
		executionResult(component, true, false, null, brokerID);
	}

	public void executionResultOnLocalExecutingWorker(WorkerComponent component,
			ExecutorResult executorResult, DeploymentID brokerID) {
		executionResult(component, true, true, executorResult, brokerID);
	}

	public void executionResultOnLocalExecuteWorker(WorkerComponent component,
			DeploymentID brokerID) {
		executionResult(component, true, false, null, brokerID);
	}

	public void executionResultOnErrorWorker(WorkerComponent component) {
		executionResult(component, true, false, null, null);
	}

	public void executionResult(WorkerComponent component, boolean isKnownClient,
			boolean isExecutingState, ExecutorResult executorResult, DeploymentID brokerID) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		WorkerClient workerClient = EasyMock.createMock(WorkerClient.class);

		EasyMock.reset(newLogger);

		if (!isExecutingState) {
			newLogger.warn("Can't get the execution result:" +
							" the worker is not executing anything.");
		} else {
			EasyMock.reset(workerClient);
			workerClient.sendMessage((HereIsGridProcessResultMessageHandleMatcher.
					eqMatcher(new HereIsGridProcessResultMessageHandle(executorResult))));

			ObjectDeployment workerOD = getWorkerDeployment();

			AcceptanceTestUtil.setExecutionContext(component, workerOD,
					brokerID.getPublicKey());
			AcceptanceTestUtil.publishTestObject(component, brokerID,
					workerClient, WorkerClient.class);
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(workerClient);

		if (brokerID != null) {

			ContainerID senderID = brokerID.getContainerID();
			DeploymentID wcID = new DeploymentID(senderID, 
					BrokerConstants.WORKER_CLIENT);
			AcceptanceTestUtil.publishTestObject(application, wcID, 
					workerClient, WorkerClient.class);

			ObjectDeployment workerOD = getWorkerDeployment();
			AcceptanceTestUtil.setExecutionContext(component, workerOD, wcID);

		}

		getWorkerExecutionClient().executionResult(executorResult);

		EasyMock.verify(workerClient);
		EasyMock.verify(newLogger);

		EasyMock.reset(newLogger);
		component.setLogger(oldLogger);
	}

	public void executionIsRunningOnPreparingWorker(WorkerComponent component) {
		executionIsRunning(component, true, false, null);
	}

	public void executionIsRunningOnOwnerWorker(WorkerComponent component) {
		executionIsRunning(component, true, false, null);
	}

	public void executionIsRunningOnIdleWorker(WorkerComponent component) {
		executionIsRunning(component, true, false, null);
	}

	public void executionIsRunningOnIdleLocalExecuteWorker(WorkerComponent component,
			ExecutorHandle handle) {
		executionIsRunning(component, true, true, handle);
	}

	public void executionIsRunningOnIdleRemoteExecuteWorker(WorkerComponent component,
			ExecutorHandle handle) {
		executionIsRunning(component, true, true, handle);
	}

	public void executionIsRunningOnErrorWorker(WorkerComponent component) {
		executionIsRunning(component, true, false, null);
	}

	public void executionIsRunning(WorkerComponent component, boolean isKnownClient,
			boolean isExecuteState, ExecutorHandle handle) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		EasyMock.reset(newLogger);

		if (!isExecuteState) {
			newLogger.warn("The Execution is not running: the worker" +
								" is not in execute state.");
		} 

		EasyMock.replay(newLogger);

		getWorkerExecutionClient().executionIsRunning(handle);

		EasyMock.verify(newLogger);
		EasyMock.reset(newLogger);
		component.setLogger(oldLogger);
	}

	public void executionErrorOnPreparingWorker(WorkerComponent component) {
		executionError(component, true, false, false, null, null);
	}
	
	public void executionErrorOnRemoteDownloadingWorker(WorkerComponent component,
			DeploymentID brokerID) {
		executionError(component, true, false, false, null, brokerID);
	}

	public void executionErrorOnIdleWorker(WorkerComponent component) {
		executionError(component, true, false, false, null, null);
	}

	public void executionErrorExecutingWorker(WorkerComponent component,
			DeploymentID brokerID) {
		executionError(component, true, true, false, new ExecutorException(), brokerID);
	}
	
	public void executionErrorExecutionFinishedWorker(WorkerComponent component,
			DeploymentID brokerID) {
		executionError(component, true, false, false, null, brokerID);
	}

	public void executionErrorOnErrorWorker(WorkerComponent component) {
		executionError(component, true, false, false, null, null);
	}

	public void executionErrorOnOwnerWorker(WorkerComponent component) {
		executionError(component, true, false, false, null, null);
	}

	public void executionErrorOnExecuteWorker(WorkerComponent component,
			DeploymentID brokerID) {
		executionError(component, true, true, true, new ExecutorException(), brokerID);
	}

	public void executionError(WorkerComponent component, boolean isKnownClient,
			boolean isExecutingState, boolean isExecuteState,
			ExecutorException errorCause, DeploymentID brokerID) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		WorkerClient workerClient = EasyMock.createMock(WorkerClient.class);

		EasyMock.reset(newLogger);

		if (!isExecutingState && !isExecuteState) {
			newLogger.warn("Can not ocurred any execution error: the worker" +
					" is not in execute or executing state.");
		} 

		else if (errorCause != null) {
			newLogger.error("Error ocurred: EXECUTION_ERROR - " +
					"When a EXECUTION_ERROR occurs it means that the task being" +
					" executed failed because of the user's application. Verify" +
					" the command line submitted in the remote phase of the task.",
					errorCause);

			workerClient.sendMessage(ErrorOcurredMessageHandleMatcher.eqMatcher(
					new ErrorOcurredMessageHandle(new GridProcessError(
							GridProcessErrorTypes.EXECUTION_ERROR))));

			ObjectDeployment workerOD = getWorkerDeployment();

			AcceptanceTestUtil.setExecutionContext(component, workerOD,
					brokerID.getPublicKey());
			AcceptanceTestUtil.publishTestObject(component, brokerID, 
					workerClient, WorkerClient.class);

		}

		EasyMock.replay(newLogger);
		EasyMock.replay(workerClient);

		getWorkerExecutionClient().executionError(errorCause);

		EasyMock.verify(newLogger);
		EasyMock.verify(workerClient);

		EasyMock.reset(newLogger);
		EasyMock.reset(workerClient);

		component.setLogger(oldLogger);
	}

}