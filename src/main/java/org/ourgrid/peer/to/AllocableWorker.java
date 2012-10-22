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

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.Req;

/**
 * A local worker that can be allocated to a consumer.
 */
@Req("REQ025")
public abstract class AllocableWorker {

	protected Consumer consumer;
	protected Request request;
	
	private String providerCertificateDN;
	
	private long lastTimeStamp = 0;
	private static final long globalZeroTime = System.nanoTime();
	private boolean delivered;
	
	/**
	 * @param localWorker
	 */
	protected AllocableWorker(){
		this.delivered = false;
		updateTimeStamp();
	}

	/**
	 * Gets the current status of this <code>AllocableWorker</code>
	 * @return
	 */
	public abstract LocalWorkerState getStatus();

	/**
	 * Gets the WorkerSpec of this <code>AllocableWorker</code>
	 * @return
	 */
	public abstract WorkerSpecification getWorkerSpecification();
	
	/**
	 * Verifies if the worker is local
	 * @return
	 */
	public abstract boolean isWorkerLocal();
	
	/**
	 * Sets the status of this <code>AllocableWorker</code>
	 * @param state new status
	 */
	public abstract void setStatus(LocalWorkerState state);

	/**
	 * Deallocate machine and mark as IDLE
	 */
	public void deallocate() {
		setStatus( LocalWorkerState.IDLE );
		clear();
		setAsNotDelivered();
	}
	
		/**
	 * @return True if the worker was delivered to a client
	 */
	public boolean isDelivered() {
		return delivered;
	}

	/**
	 * Marks the worker as delivered to client
	 */
	public void setAsDelivered() {
		delivered = true;
	}
	
	public void setAsNotDelivered() {
		delivered = false;
	}
	
	/**
	 * @return the consumer
	 */
	public Consumer getConsumer() {
		return consumer;
	}

	/**
	 * @param consumer the consumer to set
	 */
	public synchronized void setConsumer(Consumer consumer) {
		this.consumer = consumer;
		updateTimeStamp();
	}

	/**
	 * @return
	 */
	private void updateTimeStamp() {
		lastTimeStamp = (System.nanoTime() - globalZeroTime);
	}
	
	/**
	 * The allocation algorithms are based in the allocation 
	 * aging. This method provides a time stamp of the last
	 * attribution.
	 * 
	 * @return
	 */
	public synchronized long getLastConsumerAssignTimeStamp(){
		return lastTimeStamp;
	}
	
	/**
	 * @param brokerListener
	 * @param broker
	 */
	public abstract void workForBroker(List<IResponseTO> responses);
	
	/**
	 * @return
	 */
	public abstract String getWorkerAddress();
	
	/**
	 * @return
	 */
	public abstract String getProviderAddress();
	
	public abstract void setProviderAddress(String providerAddress);
	
	public Priority getPriority() {
		
		if (consumer == null) {
			return Priority.IDLE;
		}
		return consumer.getPriority();
	}
	
	public void setRequest(Request request) {
        this.request = request;
	}
	
	public Request getRequest() {
	    return request;
	}

	public void clear() {
		if (request != null) {
			request.removeAllocableWorker(this);
			request = null;
		}
		
		if(consumer != null) {
			consumer.removeAllocableWorker(this);
		}
		consumer = null;
		setAsNotDelivered();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((consumer == null) ? 0 : consumer.hashCode());
		result = PRIME * result + ((request == null) ? 0 : request.hashCode());
		result = PRIME * result + ((getWorkerSpecification() == null) ? 0 : getWorkerSpecification().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AllocableWorker other = (AllocableWorker) obj;
		if (consumer == null) {
			if (other.consumer != null)
				return false;
		} else if (!consumer.equals(other.consumer))
			return false;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		if (getWorkerSpecification() == null) {
			if (other.getWorkerSpecification() != null)
				return false;
		} else if (!getWorkerSpecification().equals(other.getWorkerSpecification()))
			return false;
		return true;
	}

	public abstract Class<?> getMonitorableType();
	
	public abstract String getMonitorName();

	public void setProviderCertificateDN(String providerCertificateDN) {
		this.providerCertificateDN = providerCertificateDN;
	}

	public String getProviderCertificateDN() {
		return providerCertificateDN;
	}

	public String getConsumerPublicKey() {
		if (consumer == null) {
			return null;
		}
		
		return consumer.getPublicKey();
	}

	
}
