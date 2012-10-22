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

import org.ourgrid.common.BrokerLoginResult;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * Entity that represents a Peer client. From this Interface, the client receives 
 * informations about login attempt from the peer,  and also can receive workers.
 *
 * @see {@link WorkerSpecification} , {@link BrokerLoginResult}
 */
@Remote
public interface LocalWorkerProviderClient {

	/**
	 * This method gives information to a Peer client about its login attempt.
	 * @param workerProvider The peer which the client is trying to connect.
	 * @param result The information about the login attempt.
	 */
	void loginSucceed( LocalWorkerProvider workerProvider, BrokerLoginResult result);
	
	/**
	 * Gives a worker to this client according the requirements specified in the RequestSpec. 
	 * @param worker The worker given by the {@link LocalWorkerProvider} for this client. 
	 * @param workerSpec All the information about the required worker given by the user.
	 * @param requestSpec The request made by the client.
	 */
	void hereIsWorker( ServiceID workerServiceID, WorkerSpecification workerSpec, RequestSpecification requestSpec);
	
	/**
	 * This consumer request loses the worker for other request 
	 * @param worker The worker given by the {@link LocalWorkerProvider} for this client. 
	 */
	void preemptedWorker( ServiceID workerServiceID );
}