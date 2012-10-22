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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.common.specification.worker.WorkerSpecification;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class WorkerEntry implements Serializable {

	private static final long serialVersionUID = 40L;
	
	private transient List<Integer> tasksBlacklisted;

	private WorkerSpecification workerSpecification;

	private boolean allocated;

	private GridProcess gridProcess;

	private Request request;

	private String workerID;
	
	private String workerPublicKey;
	
	private boolean isUp;
	
	public WorkerEntry(WorkerSpecification workerSpecification, Request request, String workerID) {
		this.workerSpecification = workerSpecification;
		this.workerID = workerID;
		this.tasksBlacklisted = new ArrayList<Integer>();
		this.request = request;
		this.allocated = false;
		this.isUp = false;
		this.gridProcess = null;
	}

	public WorkerSpecification getWorkerSpecification() {
		return workerSpecification;
	}
	
	public ServiceID getServiceID() {
		return new DeploymentID(workerID).getServiceID();
	}
	
	public void addBlacklistedTask(int taskid) {
		this.tasksBlacklisted.add(taskid);
	}

	public boolean allocated() {
		return allocated;
	}

	public boolean taskBlacklisted(int taskid) {
		return tasksBlacklisted.contains(taskid);
	}

	public void allocate(GridProcess gridProcess) {
		this.gridProcess = gridProcess;
		this.allocated = true;
	}
	
	public GridProcess getGridProcess() {
		return gridProcess;
	}
	
	@Override
	public int hashCode() {
		return getServiceID().hashCode();
	}

	@Override
	public boolean equals( Object o ) {
		if ( o instanceof WorkerEntry ) {
			WorkerEntry entry = (WorkerEntry) o;
			return getWorkerSpecification().equals( entry.getWorkerSpecification() );
		}
		
		return false;
	}

	public void deallocate() {
		this.allocated = false;
		this.gridProcess = null;
	}

	public int getNumberOfBlacklistedTasks() {
		return this.tasksBlacklisted.size();
	}

	public void dispose() {
		removeWorker();
	}

	
	private void removeWorker() {
		this.request.removeWorker(this);
	}

	public void unwant() {
		removeWorker();
	}

	/**
	 * @return
	 */
	public String getPeerID() {
		return this.request.getPeerID();
	}
	
	public long getRequestID() {
		return this.request.getSpecification().getRequestId();
	}
	
	public RequestSpecification getRequestSpecification() {
		return this.request.getSpecification();
	}
	
	public boolean canWorkerRunThisTask(int taskid) {
		return !taskBlacklisted(taskid);
	}

	public Request getRequest() {
		return request;
	}
	
	public String getWorkerID() {
		return this.workerID;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}

	public boolean isUp() {
		return isUp;
	}

	public void setWorkerID(String workerDeploymentID) {
		this.workerID = workerDeploymentID;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}
	
}