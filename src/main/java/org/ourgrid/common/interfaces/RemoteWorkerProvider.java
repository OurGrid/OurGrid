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

import org.ourgrid.common.interfaces.to.RequestSpecification;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * Represents a Peer module running in another site. Performs the interaction
 * between the remote Peer and the RemoteWorkerProviderClient.
 */
@Remote
public interface RemoteWorkerProvider {

	/**
	 * Requests workers from the RemoteWorkerProvider according this RequestSpec. 
	 * @param workerProviderClient The client of the RemoteWorkerProvider.
	 * @param requestSpec Request specification.
	 */
	void requestWorkers(RemoteWorkerProviderClient workerProviderClient, RequestSpecification requestSpec);
	
	
	/**
	 * Indicates that the worker with this ServiceID is no longer necessary to the Broker
	 * @param workerServiceID The worker's ServiceID
	 */
	void disposeWorker( ServiceID workerServiceID);

}
