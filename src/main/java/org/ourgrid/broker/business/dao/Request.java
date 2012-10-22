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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.ourgrid.common.interfaces.to.RequestSpecification;

public class Request implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RequestSpecification specification;
	
	private String peerID;
	
	private Set<WorkerEntry> workers;
	
	private boolean paused;
	
	/**
	 * @return the paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * @param paused the paused to set
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public Request(RequestSpecification specification, String peerID) {
		this.specification = specification;
		this.peerID = peerID;
		this.workers = new LinkedHashSet<WorkerEntry>();
	}

	public String getPeerID() {
		return peerID;
	}

	public RequestSpecification getSpecification() {
		return specification;
	}

	@Override
	public int hashCode() {
		return specification.hashCode();
	}

	@Override
	public boolean equals( Object o ) {
		if ( o instanceof Request ) {
			Request struct = (Request) o;
			return specification.equals(struct.getSpecification());
		}
		
		return false;
	}
	
	public void addWorker(WorkerEntry worker) {
		this.workers.add(worker);
	}
	

	public Collection<WorkerEntry> getWorkers() {
		return this.workers;
	}

	public void removeWorker(WorkerEntry workerEntry) {
		this.workers.remove(workerEntry);
	}
}
