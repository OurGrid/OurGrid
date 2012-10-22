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
package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.request.WorkerClientIsUpRequestTO;

public class WorkerClientIsUpRequester implements RequesterIF<WorkerClientIsUpRequestTO>{

	public List<IResponseTO> execute(WorkerClientIsUpRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		responses.add(new LoggerResponseTO(WorkerControllerMessages.
				getTryToDoWorkerClientIsUpMessage(),
				LoggerResponseTO.WARN));
		
		return responses;
	}

}
