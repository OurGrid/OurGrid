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
package org.ourgrid.broker.business.scheduler.workqueue;

import java.util.Map;

import org.ourgrid.broker.business.dao.WorkerEntry;
import org.ourgrid.common.util.CommonUtils;

public class WorkerInfo  {

	private Map<String, WorkerEntry> workers;
	private static WorkerInfo instance;
	
	
	private WorkerInfo() {
		this.workers = CommonUtils.createMap();
	}
	
	public static WorkerInfo getInstance() {
		if (instance == null) {
			instance = new WorkerInfo();
		}
		return instance;
	}
	
	public static void reset() {
		instance = new WorkerInfo();
	}
	
	public void addWorkerEntry(String workerContainerID, WorkerEntry workerEntry) {
		this.workers.put(workerContainerID, workerEntry);
	}
	
	public WorkerEntry removeWorker(String containerID) {
		return this.workers.remove(containerID);
	}
	
	public WorkerEntry getWorker(String containerID) {
		return this.workers.get(containerID);
	}
	
}
