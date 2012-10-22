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

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;


/**
 */
public class RemoteConsumer extends Consumer{

	private Priority priority;
	private Set<AllocableWorker> allocatedWorkers = new LinkedHashSet<AllocableWorker>();
	
	private String consumerDN;
	
	/* (non-Javadoc)
	 * @see org.ourgrid.peer.to.Consumer#getPriority()
	 */
	public Priority getPriority() {
		return priority;
	}
	
	/*
	 * @param priority
	 */
	public void setPriority(Priority priority){
		this.priority = priority; 
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.peer.to.Consumer#isLocal()
	 */
	public boolean isLocal() {
		return false;
	} 
	
	public void addWorker(AllocableWorker worker) {
	    allocatedWorkers.add(worker);
	}

	@Override
	public List<AllocableWorker> getAllocableWorkers() {
		List<AllocableWorker> allocables = new LinkedList<AllocableWorker>();
		allocables.addAll(allocatedWorkers);
		return allocables;
	}

	@Override
	public void removeAllocableWorker(AllocableWorker allocableWorker) {
		allocatedWorkers.remove(allocableWorker);
	}

	@Override
	public Class<?> getConsumerType() {
		return RemoteWorkerProviderClient.class;
	}

	public void setConsumerDN(String consumerDN) {
		this.consumerDN = consumerDN;
	}

	public String getConsumerDN() {
		return consumerDN;
	}
}
