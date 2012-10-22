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
package org.ourgrid.acceptance.util;

import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.to.RequestSpecification;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 *
 */
public class WorkerAllocation {

	public final DeploymentID workerID;
	public DeploymentID winnerID = null;
	public DeploymentID loserID = null;
	public RequestSpecification loserRequestSpecification = null;
	public RemoteWorkerManagementClient rwmc = null;
	
	/**
	 * @param workerID
	 */
	public WorkerAllocation(DeploymentID workerID) {
		this.workerID = workerID;
	}

	public WorkerAllocation addWinnerConsumer(DeploymentID winnerID){
		this.winnerID = winnerID;
		return this;
	}
	
	public WorkerAllocation addLoserConsumer(DeploymentID loserID){
		this.loserID = loserID;
		return this;
	}
	
	public WorkerAllocation addLoserRequestSpec(RequestSpecification requestSpecification){
		this.loserRequestSpecification = requestSpecification;
		return this;
	}
	
	public WorkerAllocation addRemoteWorkerManagementClient(RemoteWorkerManagementClient rwmc){
		this.rwmc = rwmc;
		return this;
	}

	public boolean isLocal() {
		return rwmc == null;
	}
	
	public void workForBroker(DeploymentID brokerID, Object ep) {
		
		if (isLocal()) { //Local Worker
			
			WorkerManagement worker = (WorkerManagement) ep;
			worker.workForBroker(brokerID);
		} else {
			
			RemoteWorkerManagement worker = (RemoteWorkerManagement) ep;
			worker.workForBroker(rwmc, brokerID.getPublicKey());
		}
		
	}
}