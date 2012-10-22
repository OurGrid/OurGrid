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
package org.ourgrid.acceptance.util.peer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.RemoteAllocation;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagement;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_022_Util extends PeerAcceptanceUtil {

	public Req_022_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Notifies broker failure. 
	 * Expects the requests' futures for this consumer to be canceled
	 * @param peerComponent The peer component
	 * @param lwpcID The <code>LocalWorkerProvider</code> consumer interface DeploymentID
	 * @param futures The futures to be canceled
	 */
	public void notifyBrokerFailure(Module peerComponent,
			DeploymentID lwpcID, ScheduledFuture<?>... futures) {

		notifyBrokerFailure(peerComponent, lwpcID,
				new LinkedList<WorkerAllocation>(),
				new LinkedList<TestStub>(), futures);
	}

	/**
	 * Notifies broker failure. 
	 * Expects the requests' futures for this consumer to be canceled
	 * Expects workers that were allocated for this consumer to be reallocated.
	 * @param peerComponent The peer component
	 * @param allocations Local workers allocations to be made
	 * @param remotePeers Remote workers allocations to be made
	 * @param lwpcID The <code>LocalWorkerProvider</code> consumer interface DeploymentID
	 * @param futures The futures to be canceled
	 */
	public void notifyBrokerFailure(Module peerComponent,
			DeploymentID lwpcID, List<WorkerAllocation> allocations,
			List<TestStub> remotePeers, ScheduledFuture<?>... futures) {
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);

		peerComponent.setLogger(newLogger);

		// Expecting futures canceling
		for (ScheduledFuture<?> future : futures) {
			if (future != null) {
				EasyMock.reset(future);
				EasyMock.expect(future.cancel(true)).andReturn(true);
				EasyMock.replay(future);
			}
		}

		newLogger.info("The local consumer [" + lwpcID.getContainerID()
				+ "] with publicKey [" + lwpcID.getPublicKey() + "] has failed. Canceling his requests.");

		for (WorkerAllocation allocation : allocations) {
			WorkerManagement workerL = (WorkerManagement) AcceptanceTestUtil
					.getBoundObject(allocation.workerID);
			EasyMock.reset(workerL);

			if (allocation.winnerID == null) {
				workerL.stopWorking();
				
				//It was removed. Status change will be sent by the worker after stopWorking
				/*newLogger.debug("Worker <" + allocation.workerID
						+ "> is now IDLE");*/
			} else {
				allocation.workForBroker(allocation.winnerID, workerL);
			}

			EasyMock.replay(workerL);

		}

		for (TestStub stub : remotePeers) {
			
			RemoteAllocation remotePeer = (RemoteAllocation) stub.getObject();
			
			EasyMock.reset(remotePeer.rwp);
			
			AcceptanceTestUtil.publishTestObject(application, stub.getDeploymentID(), remotePeer.rwp,
					RemoteWorkerProvider.class);
			for (WorkerAllocation allocation : remotePeer.remoteWorkers) {
				RemoteWorkerManagement workerR = (RemoteWorkerManagement) AcceptanceTestUtil
						.getBoundObject(allocation.workerID);
				EasyMock.reset(workerR);

				if (allocation.winnerID == null) {
					remotePeer.rwp.disposeWorker(allocation.workerID.getServiceID());
					newLogger.debug("The remote worker " + allocation.workerID.getServiceID()
							+ " does not match any request. "
							+ "Disposing it back to its provider: " + stub.getDeploymentID().getServiceID()
							+ ".");
				} else {
					allocation.workForBroker(allocation.winnerID, workerR);
				}

				createStub(workerR, RemoteWorkerManagement.class, allocation.workerID);
				EasyMock.replay(workerR);
			}
			EasyMock.replay(remotePeer.rwp);
		}

		EasyMock.replay(newLogger);

		notifyBrokerFailure(lwpcID);

		EasyMock.verify(newLogger);
		// Verifying mocks
		for (ScheduledFuture<?> future : futures) {
			if (future != null) {
				EasyMock.verify(future);
			}
		}

		for (WorkerAllocation allocation : allocations) {
			WorkerManagement workerL = (WorkerManagement) AcceptanceTestUtil
					.getBoundObject(allocation.workerID);
			EasyMock.verify(workerL);
		}

		for (TestStub stub : remotePeers) {
			
			RemoteAllocation remotePeer = (RemoteAllocation) stub.getObject();
			EasyMock.verify(remotePeer.rwp);
			for (WorkerAllocation allocation : remotePeer.remoteWorkers) {
				RemoteWorkerManagement workerR = (RemoteWorkerManagement) AcceptanceTestUtil
						.getBoundObject(allocation.workerID);
				EasyMock.verify(workerR);
			}
		}

		peerComponent.setLogger(oldLogger);
	}

	/**
	 * Notifies broker failure without verifying any behavior.
	 * Mainly used for input validation tests.
	 * @param lwpcID
	 */
	public void notifyBrokerFailure(DeploymentID lwpcID) {
		// Mocking monitorable (EventProcessor)
		LocalWorkerProviderClient monitorable = EasyMock.createMock(LocalWorkerProviderClient.class);
		createStub(monitorable, LocalWorkerProviderClient.class, lwpcID);
		EasyMock.replay(monitorable);

		// Notifying LocalWorkerProviderClient failure
		getClientMonitor().doNotifyFailure(monitorable, lwpcID);
	}
	
}