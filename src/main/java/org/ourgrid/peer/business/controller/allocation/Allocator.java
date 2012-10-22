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

import java.util.Collection;
import java.util.List;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.Consumer;
import org.ourgrid.peer.to.Request;
import org.ourgrid.reqtrace.Req;

import sun.security.provider.certpath.X509CertPath;

/**
 * Interface for allocation algorithms 
 */
@Req("REQ011")
public interface Allocator {

	/**
	 * Select the workers that will be allocated for a local request
	 * 
	 * @param request The request that will be served
	 * @param allAllocableWorkers Collection containing all the current 
	 *         allocable workers in this peer
	 * @return A list with the workers to be allocated for this request
	 */
	@Req("REQ011")
	List<AllocableWorker> getAllocableWorkersForLocalRequest(List<IResponseTO> responses, 
			X509CertPath myCertPath, Request request, Collection<AllocableWorker> allAllocableWorkers);
	
	/**
	 * Select the workers that will be allocated for a remote request
	 * 
	 * @param consumerPubKey Remote peer public key
	 * @param requestSpecification Remote request specification
	 * @param allocableWorkers Collection containing all the current 
	 *         allocable workers in this peer
	 * @return A list with the workers to be allocated for this remote request
	 */
	@Req("REQ011")
	List<AllocableWorker> getAllocableWorkersForRemoteRequest(List<IResponseTO> responses, Consumer consumer, 
			RequestSpecification requestSpecification, String peerDNData, List<AllocableWorker> allocableWorkers);

	/**
	 * Determines a local request to be associated to a worker
	 * @param workerSpecification Worker specification
	 * @return The request for which the worker will be allocated
	 */
	@Req("REQ018")
	Request getRequestForWorkerSpecification(WorkerSpecification workerSpecification);
}
