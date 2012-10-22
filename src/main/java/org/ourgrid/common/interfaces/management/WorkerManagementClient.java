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
package org.ourgrid.common.interfaces.management;

import java.util.List;

import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.specification.worker.WorkerSpecification;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * Interface that manages the status changes in a local <code>Worker</code>.
 */
@Remote
public interface WorkerManagementClient {
	
	/**
	 * Receives the login request from WorkerManagement
	 * @param workerManagement The WorkerManagement callback stub
	 * @param workerSpecification 
	 */
	void workerLogin(WorkerManagement workerManagement, WorkerSpecification workerSpecification);

	/**
	 * Receives change of status to Allocated for Broker from the <code>Worker</code>.
	 * @param workerServiceID The <code>Worker</code> that changed status.
	 * @param brokerPublicKey The <code>Broker</code> that the <code>Worker</code> is allocated.
	 */
	void statusChangedAllocatedForBroker( ServiceID workerServiceID, String brokerPublicKey );

	/**
	 * Receives change of status to Allocated for Peer from the <code>RemoteWorkerManagement</code>.
	 * @param remoteWorkerManagementServiceID The <code>RemoteWorkerManagement</code> that changed status.
	 * @param peerPublicKey The <code>Peer</code> that the <code>RemoteWorkerManagement</code> is allocated.
	 */
	void statusChangedAllocatedForPeer( ServiceID remoteWorkerManagementServiceID, String peerPublicKey );

	/**
	 * Receives change of status from the <code>Worker</code>.
	 * @param worker The <code>Worker</code> that changed status.
	 */
	void statusChanged( WorkerStatus status);
	
	void init(ServiceManager serviceManager);

	/**
	 * This method is periodically called by the worker, to inform the balance
	 * decrement of the consumers.
	 * 
	 * @param consumersBalances A map whose key is the consumer's public key 
	 * 			and the value is its balance.
	 */
	void reportWorkAccounting( List<WorkAccounting> consumersBalances );
	
	/**
	 * Update the workerSpec, with the new values for dynamic attributes
	 * @param workerSpec New values for dynamic attributes
	 */
	void updateWorkerSpec(WorkerSpecification workerSpec);
	
	/**
	 * <p>
	 * Saves the current ranking to a file.
	 * </p>
	 */
	public void saveRanking( );
}
