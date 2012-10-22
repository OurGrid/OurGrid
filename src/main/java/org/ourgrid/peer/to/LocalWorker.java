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
package org.ourgrid.peer.to;

import java.util.Map;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConstants;


/**
 * Represents a reference to a local worker under this peer.
 * This reference include five objects:
 * <ul>
 * <li> A stub to the Worker interface;
 * <li> A stub to the WorkerManagement interface;
 * <li> A WorkerSpecification object.
 * <li> The Worker PublicKey
 * <li> The LocalWorkerStatus
 * </ul>
 */
@Req("REQ010")
public class LocalWorker {

	private LocalWorkerState status;
	private WorkerSpecification workerSpecification;
	private String workerManagementAddress;
	private String workerUserAtServer;
	private String workerPublicKey;

	
	public LocalWorker(WorkerSpecification workerSpecification, String workerUserAtServer, String workerPublicKey) {
		this.workerSpecification = workerSpecification;
		this.status = LocalWorkerState.OWNER;
		this.workerUserAtServer = workerUserAtServer;
		this.workerPublicKey = workerPublicKey;
		setWorkerManagementAddress(StringUtil.userAtServerToAddress(
				workerUserAtServer, WorkerConstants.MODULE_NAME, 
				WorkerConstants.LOCAL_WORKER_MANAGEMENT));
	}

	public LocalWorker(WorkerSpecification workerSpecification, String workerUserAtServer) {
		this(workerSpecification, workerUserAtServer, null);
	}

	public void setWorkerManagementAddress(String workerManagementAddress) {
		this.workerManagementAddress = workerManagementAddress;
	}

	/**
	 * Gets the attributes from this <code>LocalWorker</code>
	 * @return a Map from String to String
	 */
	public Map<String,String> getAttributes() {
		return this.workerSpecification.getAttributes();
	}

	/**
	 * Gets the annotations from this <code>LocalWorker</code>
	 * @return a Map from String to String
	 */
	public Map<String,String> getAnnotations() {
		return this.workerSpecification.getAnnotations();
	}

	/**
	 * Gets the <code>WorkerSpecification</code> of this <code>LocalWorker</code>
	 * @return 
	 */
	public WorkerSpecification getWorkerSpecification() {
		return this.workerSpecification;
	}

	/**
	 * Gets the WorkerManagement</code> address of this <code>LocalWorker</code>
	 * @return
	 */
	public String getWorkerManagementAddress() {
		return workerManagementAddress;
	}
	
	/**
	 * Gets this <code>LocalWorker</code>'s status
	 * @return
	 */
	public LocalWorkerState getStatus() {
		return status;
	}

	/**
	 * Sets the status of this Worker
	 * @param status the new status
	 */
	public void setStatus(LocalWorkerState status) {
		this.status = status;
	}
	
	/**
	 * Sets the worker specification
	 * @param workerSpecification the new worker specification
	 */
	public void setWorkerSpecification(WorkerSpecification workerSpecification) {
		this.workerSpecification = workerSpecification;
	}
	
	/**
	 * Gets this <code>LocalWorker</code>'s <code>PublicKey</code>
	 * @return
	 */
	public String getPublicKey() {
		return this.workerPublicKey;
	}
	
	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerUserAtServer() {
		return this.workerUserAtServer;
	}

}