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
package org.ourgrid.broker.business.dao;

import java.util.Map;

import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.CommonUtils;

/**
 * Maintains information about the current 
 * workers providers of this Broker.
 * 
 */
public class WorkerDAO {

	private Map<String, WorkerSpecification> workerSpecs;
	private Map<String, String> workersPublicKeys;
	
	public WorkerDAO() {
		this.workerSpecs = CommonUtils.createMap();
		this.workersPublicKeys = CommonUtils.createSerializableMap();
	}
	
	public void addWorker(String workerAddress, WorkerSpecification workerSpec) {
		this.workerSpecs.put(workerAddress, workerSpec);
	}
	
	public void setWorkerPublicKey(String workerAddress, String workerPublicKey) {
		this.workersPublicKeys.put(workerAddress, workerPublicKey);
	}
	
	public void removeWorker(String workerAddress) {
		this.workerSpecs.remove(workerAddress);
		this.workersPublicKeys.remove(workerAddress);
	}
	
	public String getWorkerPublicKey(String workerAddress) {
		return this.workersPublicKeys.get(workerAddress);
	}
	
	public WorkerSpecification getWorkerSpec(String workerAddress) {
		return this.workerSpecs.get(workerAddress);
	}
	
	public void updateWorkerSpec(String workerAddress, WorkerSpecification workerSpec) {
		this.workerSpecs.put(workerAddress, workerSpec);
	}
}
