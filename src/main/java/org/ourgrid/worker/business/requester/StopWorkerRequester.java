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

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.controller.WorkerController;
import org.ourgrid.worker.business.messages.ControlMessages;
import org.ourgrid.worker.request.StopWorkerRequestTO;

public class StopWorkerRequester implements RequesterIF<StopWorkerRequestTO> {

	public List<IResponseTO> execute(StopWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (request.isStopSenderPublicKeyValid() && request.canComponentBeUsed()) {
			
			WorkerController.getInstance().interruptWorkingAndCancelPreparingAllocation(responses, true);
			
			LoggerResponseTO loggerResponseTO = new LoggerResponseTO();
			loggerResponseTO.setMessage(ControlMessages.getSuccessfullyShutdownWorkerMessage());
			loggerResponseTO.setType(LoggerResponseTO.INFO);
			responses.add(loggerResponseTO);
			
		}
		if(!request.isStopSenderPublicKeyValid()) {
			responses.add(new LoggerResponseTO(
					ControlMessages.getUnknownEntityTryingToStopWorkerMessage(
							request.getStopSenderPublicKey()),LoggerResponseTO.WARN));
		}
		
		return responses;
	}
}
