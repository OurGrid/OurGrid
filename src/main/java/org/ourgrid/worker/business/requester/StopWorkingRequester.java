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
package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.controller.ExecutionController;
import org.ourgrid.worker.business.controller.WorkerController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.ControlMessages;
import org.ourgrid.worker.business.messages.WorkerManagementControllerMessages;
import org.ourgrid.worker.request.StopWorkingRequestTO;
import org.ourgrid.worker.response.StatusChangedResponseTO;

public class StopWorkingRequester implements RequesterIF<StopWorkingRequestTO> {

	public List<IResponseTO> execute(StopWorkingRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPubKey = request.getSenderPublicKey();
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		if (!workerStatusDAO.isLogged()) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.getWorkerIsNotLoggedInPeerMessage(),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (!senderPubKey.equals(workerStatusDAO.getMasterPeerPublicKey())) {
			responses.add(new LoggerResponseTO(
					WorkerManagementControllerMessages.
					getStopWorkingByUnknownPeerMessage(senderPubKey),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		String masterPeerAddress = workerStatusDAO.getMasterPeerAddress();
		
		if (workerStatusDAO.isErrorState()) {
			StatusChangedResponseTO statusChangedResponseTO = new StatusChangedResponseTO();
			statusChangedResponseTO.setClientAddress(masterPeerAddress);
			statusChangedResponseTO.setStatus(workerStatusDAO.getStatus());
			responses.add(statusChangedResponseTO);

			responses.add(new LoggerResponseTO(
					WorkerManagementControllerMessages.
					getMasterPeerTryingToCommandWorkerOnErrorStateMessage(), 
					LoggerResponseTO.WARN));

			return responses;
		}
		
		boolean isWorkingState = workerStatusDAO.isWorkingState();
		if (!isWorkingState) {
			responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.
					getStopWorkingOnNotWorkingWorkerMessage(), 
					LoggerResponseTO.WARN));

			return responses;
		}

		WorkerController.getInstance().interruptWorking(responses, true);

		responses.add(new LoggerResponseTO(WorkerManagementControllerMessages.
				getSuccessfulStopWorkingMessage(), 
				LoggerResponseTO.INFO));

		WorkerStatus oldStatus = workerStatusDAO.getStatus();
		WorkerStatus newStatus = WorkerStatus.IDLE;
		workerStatusDAO.setStatus(newStatus);

		if (workerStatusDAO.isPreparingAllocationState()) {
			return responses;
		}
		
		if (isWorkingState) {
			ExecutionController.getInstance().beginAllocation(responses);
		} else {
			
			responses.add(new LoggerResponseTO(ControlMessages.
					getWorkerStatusChangedMessage(oldStatus, newStatus), 
					LoggerResponseTO.DEBUG));
			
			StatusChangedResponseTO to = new StatusChangedResponseTO();
			to.setClientAddress(masterPeerAddress);
			to.setStatus(newStatus);
			
			responses.add(to);
		}

		return responses;
	}
	
}
