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
package org.ourgrid.common.interfaces.to;

import org.ourgrid.common.statistics.beans.status.WorkerStatus;
import org.ourgrid.reqtrace.Req;

/**
 * This enumeration represents the status that a local worker can assume. These
 * status can be classified in the following way:
 * <ul>
 * <ul>
 * <li><b>IN_USE</b>: When it's a locally allocated worker</li>
 * <li><b>DONATED</b>: When the worker is local, but is donated for the community</li>
 * <li><b>IDLE</b>: The worker is available for usage</li>
 * <li><b>OWNER</b>: The worker in use by owner machine</li>
 * <li><b>CONTACTING</b>: The worker hasn't be detected yet</li>
 * <li><b>REMOVED</b>: The worker was removed from peer but is maintained in the statistics history</li>
 * </ul>
 * </ul>
 */
@Req({"REQ36","REQ38a"})
public enum LocalWorkerState {
	
	/* LocalWorker status */
	    ERROR(WorkerStatus.ERROR), IN_USE(WorkerStatus.IN_USE), 
		IDLE(WorkerStatus.IDLE), DONATED(WorkerStatus.DONATED), 
		OWNER(WorkerStatus.OWNER);

	private WorkerStatus workerStatus;
	
	private LocalWorkerState(WorkerStatus workerStatus){
		this.workerStatus = workerStatus;
	}
		
	public boolean isAllocated() {

		return (this.equals( IN_USE ) || this.equals( DONATED ));
	}

	public boolean isInUse() {

		return this.equals( IN_USE );
	}
	
	public boolean isDonated() {

		return this.equals( DONATED );
	}


	public boolean isIdle() {

		return this.equals( IDLE );
	}


	public boolean isOwner() {

		return this.equals( OWNER );
	}
	
	public boolean isError() {

		return this.equals( ERROR );
	}

	public boolean equalsWorkerStatus(WorkerStatus workerStatus){
		return this.workerStatus.equals(workerStatus);
	}
	
	public static LocalWorkerState parse(WorkerStatus workerStatus){
		
		for (LocalWorkerState localWorkerState: values()) {
			if(localWorkerState.equalsWorkerStatus(workerStatus)){
				return localWorkerState;
			}
		}
		return null;
	}
}
