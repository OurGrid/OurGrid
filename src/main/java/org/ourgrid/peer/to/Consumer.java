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

import java.util.List;

import org.ourgrid.reqtrace.Req;

/**
 */
public abstract class Consumer implements Comparable<Consumer>{

	private String consumerAddress;
	private String publicKey;
	
	
	public abstract Priority getPriority();
	
	public abstract List<AllocableWorker> getAllocableWorkers();
	
	public abstract void removeAllocableWorker(AllocableWorker allocableWorker);
	
	/**
	 * Verifies if the consumer is local.
	 * @return <code>TRUE</code> if it is local, <code>FALSE</code> otherwise
	 */
	public abstract boolean isLocal();
	
	/**
	 * Get the EntityID of the consumer of this <code>AllocableWorker</code>
	 * @return
	 */
	@Req("REQ038a")
	public String getConsumerAddress(){
		return this.consumerAddress;
	}
	
	public String getPublicKey() {
		return this.publicKey;
	}


	public void setConsumer(String consumerAddress, String consumerPublicKey) {
		this.consumerAddress = consumerAddress;
		this.publicKey = consumerPublicKey;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((consumerAddress == null) ? 0 : consumerAddress.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Consumer other = (Consumer) obj;
		if (consumerAddress == null) {
			if (other.consumerAddress != null)
				return false;
		} else if (!consumerAddress.equals(other.consumerAddress))
			return false;
		return true;
	}
	
	public abstract Class<?> getConsumerType();

	public int compareTo(Consumer o) {
		
		if (consumerAddress == null) {
			if (o.consumerAddress != null){
				return -1;
			} else {
				return 0;
			}
		} 
		
		if (o.consumerAddress == null) {
			return 1;
		} 
		
		return consumerAddress.compareTo(o.consumerAddress);
	}
}
