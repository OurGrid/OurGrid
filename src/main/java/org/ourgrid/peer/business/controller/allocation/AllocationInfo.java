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
package org.ourgrid.peer.business.controller.allocation;

import java.util.LinkedList;
import java.util.List;

import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.Consumer;

public class AllocationInfo implements Comparable<AllocationInfo>{
		
	private final int deservedWorkers;
	private final Consumer consumer;
	private final List<AllocableWorker> temporaryAllocations;

	public AllocationInfo(int deservedWorkers, Consumer consumer) {
		this.deservedWorkers = deservedWorkers;
		this.consumer = consumer;
		
		if (consumer == null) {
			this.temporaryAllocations = new LinkedList<AllocableWorker>();
		} else {
			this.temporaryAllocations = consumer.getAllocableWorkers();
		}
	}
	
	public void removeAllocation(AllocableWorker allocableW) {
		temporaryAllocations.remove(allocableW);
	}

	public void addAllocation(AllocableWorker allocableW) {
		temporaryAllocations.add(allocableW);
	}
	
	public List<AllocableWorker> getAllocations(){
		return this.temporaryAllocations;
	}

	public int getBalance() {
		return (temporaryAllocations.size() - deservedWorkers);
	}
	
	public boolean isOverBalanced(){
		return getBalance() > 0;
	}
	
	public boolean isInBalanced(){
		return getBalance() < 0;
	}

	/**
	 * @return the deservedWorkers
	 */
	public double getDeservedWorkers() {
		return deservedWorkers;
	}
	
	/** 
	 * Sorted from the major balance to the minor
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AllocationInfo o) {
		
		if(o != null) {
			return (o.getBalance() - getBalance());
		}

		throw new NullPointerException();
	}

	/**
	 * @return
	 */
	public Consumer getConsumer() {
		return consumer;
	}
	
	/**
	 * @return the entity
	 */
	public String getConsumerPubKey() {
		return consumer.getPublicKey();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((temporaryAllocations == null) ? 0 : temporaryAllocations.hashCode());
		result = prime * result + deservedWorkers;
		result = prime * result
				+ ((temporaryAllocations == null) ? 0 : temporaryAllocations.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AllocationInfo other = (AllocationInfo) obj;
		if (temporaryAllocations == null) {
			if (other.temporaryAllocations != null)
				return false;
		} else if (!temporaryAllocations.equals(other.temporaryAllocations))
			return false;
		if (deservedWorkers != other.deservedWorkers)
			return false;
		if (getConsumerPubKey() == null) {
			if (other.getConsumerPubKey() != null)
				return false;
		} else if (!getConsumerPubKey().equals(other.getConsumerPubKey()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Allocation info: - EntityID: " + consumer.getConsumerAddress() +" deserved: "+deservedWorkers+" inbalance: "+getBalance()
					+" Allocables: "+temporaryAllocations;
	}
}