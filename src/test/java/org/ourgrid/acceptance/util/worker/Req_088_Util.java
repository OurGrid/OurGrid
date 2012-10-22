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
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleStoppedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_088_Util extends WorkerAcceptanceUtil{

	public Req_088_Util(ModuleContext context) {
		super(context);
	}

	public void resumeNotOwnerWorker(WorkerComponent component) {
		resumeNotOwnerWorker(component, null);
	}

	public Future<?> resumeOwnerWorker(WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		return resumeWorker(component, null, workerControl, true, true, true);
	}
	
	public void resumeWorker(WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		resumeWorker(component, null, workerControl, true, true, true);
	}
	
	public void resumeIdleWorker(WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		resumeWorker(component, null, workerControl, true, true, false);
	}
	
	public void resumePreparingWorker(WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		resumeWorker(component, null, workerControl, true, true, false);
	}

	public void resumeNotOwnerWorker(WorkerComponent component, String senderPublicKey) {
		WorkerControl workerControl = getWorkerControl();
		resumeWorker(component, senderPublicKey, workerControl, true, true, false);
	}

	public Future<?> resumeOwnerWorker(WorkerComponent component, String senderPublicKey) {
		WorkerControl workerControl = getWorkerControl();
		return resumeWorker(component, senderPublicKey, workerControl, true, true, true);
	}
	
	public void resumeUnstartedWorker(WorkerComponent component) {
		WorkerControl workerControl = getWorkerControl();
		resumeWorker(component, null, workerControl, true, false, false);
	}
	
	public void resumeStoppedWorker(WorkerComponent component, WorkerControl workerControl) {
		resumeWorker(component, "publicKey", workerControl, true, false, false);
	}
	
	public void resumeWorkerByUnknownEntity(WorkerComponent component, String senderPubKey) {
		WorkerControl workerControl = getWorkerControl();
		resumeWorker(component, senderPubKey, workerControl, false, false, false);
	}

	@SuppressWarnings("unchecked")
	private Future<?> resumeWorker(WorkerComponent component, String senderPublicKey,
			WorkerControl workerControl, boolean isEntityKnown, boolean isWorkerStarted,
			boolean isWorkerOwner) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		WorkerControlClient workerControlClientMock = EasyMock.createMock(WorkerControlClient.class);
		
		ExecutorService newThreadPool = null;
		Future future = null;
		
		ObjectDeployment wcOD = getWorkerControlDeployment();
		
		if (!isEntityKnown) {
			newLogger.warn("An unknown entity tried to resume the Worker. Only the local" +
					" modules can perform this operation." +
					" Unknown entity public key: [" + senderPublicKey + "].");
		} else {
			if (senderPublicKey == null) { 
				senderPublicKey = wcOD.getDeploymentID().getPublicKey();
			}

			if (!isWorkerStarted) {
				
				if (component.getContainerDAO().isStopped()) {
					workerControlClientMock.operationSucceed(
							ControlOperationResultMatcher.eqType(ModuleStoppedException.class));
				} else {
					workerControlClientMock.operationSucceed(
							ControlOperationResultMatcher.eqType(ModuleNotStartedException.class));
				}
			} else {
				workerControlClientMock.operationSucceed(
						ControlOperationResultMatcher.noError());
				
				if (isWorkerOwner) {
					newLogger.info("Worker has been RESUMED.");
					newLogger.debug("Status changed from OWNER to IDLE.");
					newLogger.debug("Worker begin allocation action, preparing" +
										" to start the working.");
					
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
			}
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(workerControlClientMock);
	
		AcceptanceTestUtil.setExecutionContext(component, wcOD, senderPublicKey);
		DeploymentID clientID = new DeploymentID(new ServiceID("a","b","c","d"));
		createStub(workerControlClientMock, WorkerControlClient.class, clientID);
		
		workerControl.resume(workerControlClientMock);
		
		EasyMock.verify(newLogger);
		EasyMock.verify(workerControlClientMock);
		
		if (newThreadPool != null) {
			EasyMock.verify(newThreadPool);
			EasyMock.reset(newThreadPool);
		}
		
		EasyMock.reset(newLogger);
		EasyMock.reset(workerControlClientMock);
		
		component.setLogger(oldLogger);
		
		return future;
	}
}
