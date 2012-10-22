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
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.LocalWorkersDAO;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.requester.RemoveWorkerRequester;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.reqtrace.Req;

/**
 * Implements Peer actions when a local worker recover or fail.
 */
public class WorkerNotificationController {

	private static WorkerNotificationController instance = null;
	
	public static WorkerNotificationController getInstance() {
		if (instance == null) {
			instance = new WorkerNotificationController();
		}
		return instance;
	}
	
	private WorkerNotificationController() {}
	
	/**
	 * Notifies that a Worker has failed
	 * @param failedWorker The Worker that has failed.
	 * @param failedWorkerID The DeploymentID of the worker that has failed.
	 */
	@Req("REQ019")
	public void doNotifyFailure(List<IResponseTO> responses, String failedWorkerAddress, 
			String failedWorkerPublicKey) {
		
		LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(responses, 
				StringUtil.addressToUserAtServer(failedWorkerAddress));
		
		if(localWorker == null){
			responses.add(new LoggerResponseTO(WorkerMessages.getNonExistentWorkerFailureMessage(failedWorkerAddress), LoggerResponseTO.ERROR));
			return;
		} 
		
		workerFailure(responses, localWorker);
	}

	/**
	 * Finishes notification that a Worker has failed by updating the interested entities.
	 * @param serviceManager The ServiceManager
	 * @param failedWorkerID The ServiceID of the worker that has failed.
	 * @param localWorker The Worker that has failed.
	 */
	public static void workerFailure(List<IResponseTO> responses, LocalWorker localWorker) {
		
		LocalWorkersDAO localWorkersDAO = PeerDAOFactory.getInstance().getLocalWorkersDAO();
		
		responses.add(
				new LoggerResponseTO(WorkerMessages.getFailedWorkerMessage(localWorker.getWorkerManagementAddress()), 
				LoggerResponseTO.INFO));
		
		localWorkersDAO.workerIsDown(localWorker.getWorkerUserAtServer());

		PeerDAOFactory.getInstance().getAllocationDAO().removeLocalAllocableWorker(localWorker.getPublicKey());
		RemoveWorkerRequester.removeWorker(responses, localWorker.getWorkerUserAtServer());
	}

	/**
	 * Notifies that a Worker has recovered
	 * @param recoveredWorkerStub The Worker that has recovered.
	 * @param recoveredWorkerID The DeploymentID of the worker that has recovered.
	 */
	@Req("REQ019")
	public void doNotifyRecovery(List<IResponseTO> responses, String recoveredWorkerAddress, 
				String recoveredWorkerPublicKey) {
		//do nothing
	}
}
