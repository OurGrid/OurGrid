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
package org.ourgrid.peer.business.controller;

import java.util.List;

import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.control.PeerControl;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.messages.ConsumerMessages;
import org.ourgrid.peer.business.controller.messages.WorkerProviderMessages;
import org.ourgrid.peer.business.dao.ConsumerDAO;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.response.StopWorkingResponseTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.RemoteConsumer;
import org.ourgrid.reqtrace.Req;

/**
 * Implement remote Peer actions when the {@link RemoteWorkerProviderClient} fails.
 */
public class RemoteWorkerProviderFailureController {

	private static RemoteWorkerProviderFailureController instance = null;

	public static RemoteWorkerProviderFailureController getInstance() {
		if (instance == null) {
			instance = new RemoteWorkerProviderFailureController();
		}
		return instance;
	}

	private RemoteWorkerProviderFailureController() {}
	

	public void doNotifyRecovery(List<IResponseTO> responses, String rwpUserAtServer) {}

	/**
	 * Notifies that the {@link RemoteWorkerProvider} has failed
	 * @param monitorable The {@link RemoteWorkerProvider} that has failed.
	 * @param rwpID The DeploymentID of the {@link RemoteWorkerProvider} that has failed.
	 */
	@Req("REQ119")
	public void doNotifyFailure(List<IResponseTO> responses, String rwpUserAtServer, String rwpPublicKey) {
		
		String rwpAddress = createProviderAddress(rwpUserAtServer);
		String rwpcAddress = createProviderClientAddress(rwpUserAtServer);
		
		markProviderAsDown(rwpAddress, rwpUserAtServer, responses);
		
		ConsumerDAO consumerDAO = PeerDAOFactory.getInstance().getConsumerDAO();
		RemoteConsumer remoteConsumer = consumerDAO.getRemoteConsumer(
				rwpPublicKey);

		if (remoteConsumer != null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					ConsumerMessages.getRemoteConsumerFailureMessage(remoteConsumer.getConsumerAddress()),
					LoggerResponseTO.INFO);

			responses.add(loggerResponse);

			List<AllocableWorker> allocableWorkers = remoteConsumer.getAllocableWorkers();

			for (AllocableWorker allocableWorker : allocableWorkers) {

				allocableWorker.deallocate();

				WorkerControl.getInstance().statusChanged(responses,
						allocableWorker.getWorkerSpecification().getUserAndServer(), 
						LocalWorkerState.IDLE);

				StopWorkingResponseTO stopWorkingResponse = new StopWorkingResponseTO();
				stopWorkingResponse.setWmAddress(allocableWorker.getWorkerAddress());
				responses.add(stopWorkingResponse);
			}
			
			// Remove and release remote consumer
			consumerDAO.removeRemoteConsumer(rwpPublicKey);

			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(rwpcAddress);

			responses.add(releaseTO);
		}
	}

	public void markProviderAsDown(String rwpAddress, String rwpUserAtServer, List<IResponseTO> responses) {
		DiscoveryServiceClientDAO dao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		boolean removed = dao.removeRemoteWorkerProviderAddress(rwpAddress);
		
		if (removed) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					WorkerProviderMessages.getRemoteWorkerProviderFailureMessage(rwpAddress),
					LoggerResponseTO.INFO);
			responses.add(loggerResponse);
		
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(rwpAddress);
			
			responses.add(releaseTO);
			
			Peer peer = PeerControl.getInstance().getPeerByCommuneAddress(responses, rwpUserAtServer);
			
			if (peer != null) {
				PeerControl.getInstance().peerIsDown(responses, peer.getDNdata());
			}
		} else {
			LoggerResponseTO notRemovedLoggerResponse = new LoggerResponseTO(
					WorkerProviderMessages.getRemoteWorkerProviderNotRemovedMessage(rwpAddress),
					LoggerResponseTO.WARN);
			responses.add(notRemovedLoggerResponse);
		}
	}
	
	public static String createProviderAddress(String userAtServer) {
		return StringUtil.userAtServerToAddress(userAtServer, PeerConstants.MODULE_NAME, 
				PeerConstants.REMOTE_WORKER_PROVIDER);
	}
	
	public static String createProviderClientAddress(String userAtServer) {
		return StringUtil.userAtServerToAddress(userAtServer, PeerConstants.MODULE_NAME, 
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
	}
}