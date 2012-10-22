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

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.controller.allocation.RedistributionController;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.response.DisposeRemoteWorkerResponseTO;
import org.ourgrid.peer.to.RemoteAllocableWorker;
import org.ourgrid.peer.to.Request;
import org.ourgrid.reqtrace.Req;

/**
 * Implement Peer actions when a remote worker fail.
 */
public class RemoteWorkerFailureController  {

	private static RemoteWorkerFailureController instance = null;
	
	public static RemoteWorkerFailureController getInstance() {
		if (instance == null) {
			instance = new RemoteWorkerFailureController();
		}
		return instance;
	}
	
	private RemoteWorkerFailureController() {}

	public void doNotifyRecovery(List<IResponseTO> responses, String remoteWorkerAddress,
			String remoteWorkerPublicKey, String myUserAtServer) {
		
		AllocationDAO allocationDAO = PeerDAOFactory.getInstance().getAllocationDAO();
		
		RemoteAllocableWorker remoteAllocableWorker = allocationDAO.getNotRecoveredRemoteAllocableWorker(remoteWorkerAddress);
		
		//DeploymentID workerDeploymentID = serviceManager.getStubDeploymentID(worker);
		if (allocationDAO.getRemoteAllocableWorker(remoteWorkerPublicKey) != null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getReceivingAlreadyAllocatedRemoteWorkerMessage(
					remoteWorkerAddress), LoggerResponseTO.WARN));
			return;
		}
		
		WorkerControl.getInstance().addRemoteWorker(responses, remoteAllocableWorker.getWorkerSpecification(), 
				remoteAllocableWorker.getProviderCertificateDN(), myUserAtServer);
		
		responses.add(new LoggerResponseTO(WorkerMessages.getReceivedRemoteWorkerMessage(remoteAllocableWorker.getProviderAddress(), 
				remoteWorkerAddress), LoggerResponseTO.DEBUG));
		
		PeerDAOFactory.getInstance().getAllocationDAO().recoverRemoteWorker(remoteWorkerAddress, remoteWorkerPublicKey);
		RedistributionController.getInstance().redistributeRemoteWorker(responses, remoteAllocableWorker);
		
	}

	/**
	 * Notifies that a remote worker has failed
	 * @param failedWorker The remote worker that has failed.
	 * @param remoteWorkerID The DeploymentID of the remote worker that has failed.
	 */
	@Req("REQ114")
	public void doNotifyFailure(List<IResponseTO> responses, String remoteWorkerAddress,
			String remoteWorkerPublicKey) {
		
		RemoteAllocableWorker remoteAllocableWorker = PeerDAOFactory.getInstance().getAllocationDAO().getRemoteAllocableWorker(
				remoteWorkerPublicKey);
		
		if (remoteAllocableWorker == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownOrDisposedRemoteWorkerFailureMessage(remoteWorkerAddress), 
					LoggerResponseTO.DEBUG));
			return;
		}
		
		if (remoteAllocableWorker.isDelivered()) {
			responses.add(new LoggerResponseTO(WorkerMessages.getAlreadyDeliveredRemoteWorkerFailureMessage(remoteWorkerAddress), 
					LoggerResponseTO.DEBUG));
		}
		
		responses.add(new LoggerResponseTO(WorkerMessages.getRemoteWorkerFailureMessage(remoteWorkerAddress), LoggerResponseTO.DEBUG));
		
		
		String providerAddress = remoteAllocableWorker.getProviderAddress();
		String workerAddress = remoteAllocableWorker.getWorkerAddress();
		String workerPubKey = remoteAllocableWorker.getWorkerPubKey();
		
		//Remove and release worker
		WorkerControl.getInstance().removeRemoteWorker(responses, StringUtil.addressToUserAtServer(workerAddress));
		PeerDAOFactory.getInstance().getAllocationDAO().removeRemoteAllocableWorker(workerPubKey);

		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(workerAddress);
		
		responses.add(releaseTO);
		
		//Dispose to its provider
		DisposeRemoteWorkerResponseTO to = new DisposeRemoteWorkerResponseTO();
		to.setProviderAddress(providerAddress);
		to.setWorkerAddress(workerAddress);
		to.setWorkerPublicKey(workerPubKey);
		
		responses.add(to);
		
		Request request = remoteAllocableWorker.getRequest();
		
		if (request != null) {
			request.removeAllocableWorker(remoteAllocableWorker);
		}
	}

}
