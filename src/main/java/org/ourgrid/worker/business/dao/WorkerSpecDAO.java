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
package org.ourgrid.worker.business.dao;

import org.ourgrid.common.specification.worker.WorkerSpecification;

public class WorkerSpecDAO {
	
	
	private WorkerSpecification workerSpec;
	
	
	WorkerSpecDAO() {
		workerSpec = new WorkerSpecification();
	}
	
	
	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}
	
	/**
	 * Retrieves the total usage amount of running tasks, if exists such ones.
	 * <p>
	 * A task can be running in a virtual machine or directly over the 
	 * operating system.
	 *   
	 * @return the total usage amount in interval [0.0 , 1.0];
	 */
	public double getRunningTaskCpuUsage() {
		//TODO
		return 0.0;
	}
}
