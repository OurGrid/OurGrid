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

import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * 
 * Interaction interface between the entities: Peer and LocalWorkerProviderClient.
 * It provides services related to: User login, request manipulation and worker management.
 * 
 * since 18/06/2007
 */
@Remote
public interface LocalWorkerProvider {
	
	/**
	 * Indicates that the worker with this ServiceID and allocated to this RequestSpec is an
	 * unwanted worker.
	 * @param workerID Worker ServiceID 
	 * @param requestSpec Request specification 
	 */
	@Req("REQ016")
	void unwantedWorker( ServiceID workerID, RequestSpecification requestSpec );
	
	/**
	 * Receives the login request from LocalWorkerProviderClient
	 * @param workerProviderClient The client callback stub
	 */
	void login(LocalWorkerProviderClient workerProviderClient);
	
	/**
	 * Requests workers from the LocalWorkerProvider according this RequestSpec 
	 * @param requestSpec Request specification
	 */
	void requestWorkers(RequestSpecification requestSpec);
	
	/**
	 * Indicates that the request is no longer necessary to the Broker
	 * @param requestSpec
	 */
	void finishRequest( RequestSpecification requestSpec );
	
	
	
	/**
	 * Indicates that the worker with this ServiceID and allocated to this RequestSpec is no
	 * longer necessary to the Broker
	 * @param workerServiceID The worker's ServiceID
	 */
	void disposeWorker( ServiceID workerServiceID);
	
	
	/**
	 * Allows the Scheduler (Broker) to update the specification of a workers requisition.
	 * @param requestSpec Request specification to be updated
	 */
	@Req("REQ116")
	void updateRequest( RequestSpecification requestSpec );
	
	/**
	 * Indicates that the Broker does not need more workers from the Peer for this request, even 
	 * though the number of workers requested in RequestSpec has not been reached
	 * @param requestID RequestSpec Identifier 
	 */
	@Req("REQ117")
	void pauseRequest( long requestID );
	
	/**
	 * After a pause request, a broker may lose workers through failure or a preemption. Thus, it 
	 * may be necessary for the Broker restart the request to obtain back workers from the Peer.
     *
	 * @param requestSpec Request specification to be resumed
	 */
	@Req("REQ118")
	void resumeRequest( long requestID );
	
	void hereIsJobStats(JobStatusInfo jobStatusInfo);
	
	void init(ServiceManager serviceManager);
	
	/**
	 * This method is called by Broker, after every replica finish.
	 * If the provider is a remote peer, then the balance is stored to have its
	 * relative power calculated and incremented in the provider balance.
	 * Else, if the is the local peer, its balance is stored to server as a
	 * reference on the relative power calculation.
	 * These temporary data are stored until the request finish. After this, 
	 * they are erased.
	 * 
	 * @param replicaAccounting Replica accounting data, composed of:
	 *    The request which the replica belongs to;
	 *    The public key of the peer which provided the worker that executed 
	 *    the replica;
	 *    The replica balance, which will be incremented for the provider
	 */
	void reportReplicaAccounting( GridProcessAccounting replicaAccounting );
}
