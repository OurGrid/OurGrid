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

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.Req;


/**
 * Represents a request made by some consumer. It is used by the peer to store
 * information about a request. The instances of <code>RequestEntry</code> are
 * managed by the <code>RequestDAO</code>
 * 
 */
public class Request implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<AllocableWorker> allocatedWorkers = new LinkedList<AllocableWorker>(); 

	/**
	 * Indicates if the request is paused
	 */
	private boolean paused;

	/**
	 * The request specification contains all the information about the request
	 * provided by the consumer.
	 * 
	 */
	private RequestSpecification requestSpecification;

	/**
	 * Workers that are not wanted by this request.
	 */
	private Set<WorkerSpecification> unwantedWorkers;

	/**
	 * This request's consumer.
	 */
	private LocalConsumer localConsumer;

	/**
	 * Default constructor.
	 * 
	 * @param workerProviderClient
	 * 
	 * @param requestSpecification
	 *            the request specification provided by the consumer
	 */
	public Request(RequestSpecification requestSpecification) {

		this.requestSpecification = requestSpecification;
		this.unwantedWorkers = new LinkedHashSet<WorkerSpecification>();
		this.paused = false;
	}

	public RequestSpecification getSpecification() {

		return this.requestSpecification;
	}

	/**
	 * Verifies if a <code>Worker</code> is unwanted
	 * @param workerSpecification The Worker to verify
	 * @return true if the Worker is unwanted, false otherwise
	 */
	@Req("REQ016")
	public boolean isWorkerUnwanted(WorkerSpecification workerSpecification) {

		return this.unwantedWorkers.contains(workerSpecification);
	}
	
	/**
	 * Adds an unwanted worker to this request's unwanted workers list.
	 * @param workerSpecification this request's unwanted worker
	 */
	@Req("REQ016")
	public void addUnwantedWorker(WorkerSpecification workerSpecification) {
		this.unwantedWorkers.add(workerSpecification);
	}

	/**
	 * Gets the number of currently allocated Workers
	 * @return the number of allocated workers
	 */
	public int numberOfAllocatedWorkers() {

		return this.allocatedWorkers.size();
	}

	/**
	 * Checks if this request is paused
	 * @return
	 */
	@Req("REQ117")
	public boolean isPaused() {

		return this.paused;
	}

	/**
	 * Pauses the request
	 */
	@Req("REQ117")
	public void pause() {
		this.paused = true;
	}
	
	/**
	 * Resumes the request
	 */
	@Req("REQ117")
	public void resume() {
		this.paused = false;
	}

	/**
	 * Gets the number of Workers needed by this request
	 * @return
	 */
	public int getNeededWorkers() {
		//Max to avoid negative value, but a request must never receive more workers then requested.
		return Math.max(this.getSpecification().getRequiredWorkers() - this.numberOfAllocatedWorkers(), 0);
	}

	/**
	 * Verifies if this request still need more Workers
	 * @return true if more Workers are needed, false otherwise
	 */
	public boolean needMoreWorkers() {
		return !this.isPaused() && this.getNeededWorkers() > 0;
	}

	@Override
	public String toString() {

		return this.getSpecification().toString() + " Status: " + (isPaused()? "PAUSED" : "ALIVE");
	}
	
	@Override
	public int hashCode() {
		return Long.toString( getSpecification().getRequestId() ).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Request) {
			Request entry = (Request) o;
			return this.getSpecification().equals(entry.getSpecification());
		}
		
		return false;
	}

	public void setSpecification(RequestSpecification requestSpecification) {
		this.requestSpecification = requestSpecification;		
	}

    public void addAllocableWorker(AllocableWorker allocableWorker) {
        this.allocatedWorkers.add(allocableWorker);
    }

	public void removeAllocableWorker(AllocableWorker allocableWorker) {
       this. allocatedWorkers.remove(allocableWorker);
	}
    
    public List<AllocableWorker> getAllocableWorkers() {
        return this.allocatedWorkers;
    }

    public void setConsumer(LocalConsumer localConsumer) {
        this.localConsumer = localConsumer;
    }
    
    public LocalConsumer getConsumer() {
        return this.localConsumer;
    }
	
}