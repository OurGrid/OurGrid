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
package org.ourgrid.worker.business.controller;

import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.response.PauseWorkerResponseTO;
import org.ourgrid.worker.response.ResumeWorkerResponseTO;

/**
 * @author alan
 *
 */
public class IdlenessDetectorController {

	private static IdlenessDetectorController instance = null;
	
	
	public static synchronized IdlenessDetectorController getInstance() {
		if (instance == null) {
			instance = new IdlenessDetectorController();
		}
		return instance;
	}

	public void pauseWorker(List<IResponseTO> responses) {
		WorkerStatus actualStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getStatus();
		if(!actualStatus.equals(WorkerStatus.OWNER)) {
			PauseWorkerResponseTO to = new PauseWorkerResponseTO();
			responses.add(to);
		}
	}

	public void resumeWorker(List<IResponseTO> responses) {
		WorkerStatus actualStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getStatus();
		if(actualStatus.equals(WorkerStatus.OWNER)) {
			ResumeWorkerResponseTO to = new ResumeWorkerResponseTO();
			responses.add(to);
		}
	}

}
