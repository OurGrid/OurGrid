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
package org.ourgrid.worker.ui.async.model;

import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;

/**
 * A tagging interface that receives worker events.
 */
public interface WorkerAsyncUIListener {

	/**
	 * It permites start the worker.
	 */
	public void workerStarted();

	/**
	 * It permites stop the worker.
	 */
	public void workerStopped();

	public void updateCompleteStatus(WorkerCompleteStatus completeStatus);

	public void workerPaused();

	public void workerResumed();
	
	public void workerInited();

	public void workerInitedFailed();
	
	public void workerRestarted();
	
	public void workerEditing();

}
