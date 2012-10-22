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
package org.ourgrid.common.interfaces;

import org.ourgrid.common.specification.worker.WorkerSpecification;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * Entity that represents a remote Peer client. From this Interface, the client can receives 
 * workers.
 */
@Remote
public interface RemoteWorkerProviderClient {

	/**
	 * Gives a worker to this client according the requirements specified in WorkerSpec. 
	 * @param provider The {@link RemoteWorkerProvider}.
	 * @param worker The worker given by the {@link RemoteWorkerProvider} for this client.
	 * @param workerSpec All the information about the required worker given by the user.
	 */
	void hereIsWorker( RemoteWorkerProvider provider, ServiceID remoteWorkerManagementServiceID, WorkerSpecification workerSpec);
	
	/**
	 * This consumer request loses the worker for other request 
	 * @param worker The worker given by the {@link RemoteWorkerProvider} for this client. 
	 */
	void preemptedWorker(String workerPublicKey);
	
}
