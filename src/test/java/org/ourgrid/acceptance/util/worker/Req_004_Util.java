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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.common.interfaces.control.WorkerControl;
import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.matchers.BeginAllocationRunnableMatcher;
import org.ourgrid.matchers.ControlOperationResultMatcher;
import org.ourgrid.matchers.PreciseControlOperationResultMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleAlreadyStartedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_004_Util extends WorkerAcceptanceUtil {

	private Req_003_Util req_003_Util = new Req_003_Util(context);
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(context);
	
	public Req_004_Util(ModuleContext context) {
		super(context);
	}

	public WorkerComponent startWorker(boolean withIdlenessDetector,
			boolean invalidPlaypenDir, boolean invalidStorageDir)
			throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent(
				withIdlenessDetector, invalidPlaypenDir, invalidStorageDir);
		startWorker(component, null, false, false, false);
		return component;
	}

	public WorkerComponent startWorker(boolean withIdlenessDetector,
			boolean withScheduleIdleness, String scheduleTime,
			String idlenessTime) throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent(
				withIdlenessDetector, withScheduleIdleness, scheduleTime,
				idlenessTime);
		startWorker(component, null, false, false, false);
		return component;
	}

	public WorkerComponent startWorker(boolean withIdlenessDetector)
			throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent(null,
				withIdlenessDetector);
		startWorker(component, null, false, false, false);
		return component;
	}

	public WorkerComponent startWorker(DeploymentID peerID,
			boolean withIdlenessDetector) throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent(
				peerID.getServiceID(), withIdlenessDetector);
		startWorker(component, null, false, false, false);
		return component;
	}

	public WorkerComponent startWorker() throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent();
		startWorker(component, null, false, false, false);
		return component;
	}

	public Future<?> startWorker(WorkerComponent component) throws Exception {
		return startWorker(component, null, false, false, false);
	}

	public Future<?> startWorkerAlreadyStarted(WorkerComponent component)
			throws Exception {
		return startWorker(component, null, true, false, false);
	}

	public WorkerComponent startWorkerWithReportSpec(String reportTime)
			throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent(true,
				reportTime);
		startWorker(component, null, false, false, false);
		return component;
	}

	public WorkerComponent startWorkerWithScheduleIdleness(String scheduleTime,
			String idlenessTime, boolean hasSyntaticalError, boolean invalidTime)
			throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent(true,
				true, scheduleTime, idlenessTime);
		startWorker(component, null, false, hasSyntaticalError, invalidTime);
		return component;
	}

	public WorkerComponent startWorker(WorkerComponent component,
			String senderPublicKey) throws Exception {
		startWorker(component, senderPublicKey, false, false, false);
		return component;
	}

	public WorkerComponent startWorker(String senderPublicKey) throws Exception {
		WorkerComponent component = req_003_Util.createWorkerComponent();
		startWorker(component, senderPublicKey, false, false, false);
		return component;
	}

	public WorkerComponent startWorker(WorkerComponent component,
			String senderPublicKey, boolean isWorkerAlreadyStarted)
			throws Exception {
		startWorker(component, senderPublicKey, isWorkerAlreadyStarted, false, false);
		return component;
	}

	@SuppressWarnings("unchecked")
	public Future<?> startWorker(WorkerComponent component, String senderPublicKey,
			boolean isWorkerAlreadyStarted, boolean hasSyntaticalError, boolean invalidTime) throws Exception {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		WorkerControl workerControl = workerAcceptanceUtil.getWorkerControl();
		ObjectDeployment wcOD = workerAcceptanceUtil.getWorkerControlDeployment();
		WorkerControlClient workerControlClientMock = EasyMock.createMock(WorkerControlClient.class);
		
		ExecutorService newThreadPool = EasyMock.createMock(ExecutorService.class);
		component.setExecutorThreadPool(newThreadPool);
		
		Future future = null;
		
		if (senderPublicKey == null) {
			senderPublicKey = wcOD.getDeploymentID().getPublicKey();
		}
		
		if (isWorkerAlreadyStarted) {
			workerControlClientMock.operationSucceed(ControlOperationResultMatcher.
					eqType(ModuleAlreadyStartedException.class));
		} else if (hasSyntaticalError) {
			workerControlClientMock.operationSucceed(
					PreciseControlOperationResultMatcher.eqCauseType("Idleness Detector Schedule " +
							"Time property could not be loaded, because has syntactical errors.",
							RuntimeException.class));
		} else if (invalidTime) {
			workerControlClientMock.operationSucceed(
					PreciseControlOperationResultMatcher.eqCauseType("Idleness Detector Schedule " +
							"Time property could not be loaded, because has invalid times.",
							RuntimeException.class));
		} else {
			
			if (wcOD.getDeploymentID().getPublicKey().equals(senderPublicKey)) {
				workerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
				
				if (!isIdlenessDetectorOn(component)){
					newLogger.debug("Worker begin allocation action, preparing to start the working.");
					future = EasyMock.createMock(Future.class);
					EasyMock.expect(
							newThreadPool.submit(
									BeginAllocationRunnableMatcher.eqMatcher(createBeginAllocationRunnable())))
									.andReturn(future).once();
					
					EasyMock.expect(
							future.isDone()).andReturn(false).anyTimes();
					EasyMock.replay(future);
				}
				
				newLogger.info("Worker has been successfully started.");
			} else {
				newLogger.warn("An unknown entity tried to start the Worker. Only the local modules can" +
						" perform this operation. Unknown entity public key: [" + senderPublicKey + "].");
			}
		}
		
		EasyMock.replay(newThreadPool);
		EasyMock.replay(newLogger);
		EasyMock.replay(workerControlClientMock);
		
		AcceptanceTestUtil.setExecutionContext(component, wcOD, senderPublicKey);
		workerControl.start(workerControlClientMock);
		
		EasyMock.verify(newThreadPool);
		EasyMock.verify(workerControlClientMock);
		EasyMock.verify(newLogger);
		EasyMock.reset(newLogger);
		
		component.setLogger(oldLogger);
		
		return future;
	}

	private boolean isIdlenessDetectorOn(WorkerComponent component) {
		return component.getContext().getProperty(WorkerConstants.PROP_IDLENESS_DETECTOR).equals("yes");
	}
}