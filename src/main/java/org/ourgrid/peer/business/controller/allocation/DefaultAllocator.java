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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.statistics.control.AccountingControl;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.Consumer;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.peer.to.Priority;
import org.ourgrid.peer.to.Request;
import org.ourgrid.reqtrace.Req;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

/**
 * This class provides resources to requests. It attends remote and 
 * local requests.
 * 
 * This implementation uses ranges of priority in the allocation mechanism.
 * The request also is in a priority range, so the algorithm tries to respond
 * using the less priority ranges than request. If are not enough, it tries
 * the same priority allocations. None greater priority allocation could be used.
 */
@Req("REQ011")
public class DefaultAllocator implements Allocator {

	private static DefaultAllocator instance = null;
	
	private DefaultAllocator() {}
	
	public static DefaultAllocator getInstance() {
		if (instance == null) {
			instance = new DefaultAllocator();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see Allocator#getAllocableWorkersForLocalRequest(Request, Collection)
	 */
	public List<AllocableWorker> getAllocableWorkersForLocalRequest(List<IResponseTO> responses, 
			X509CertPath myCertPath, Request request, Collection<AllocableWorker> allAllocableWorkers) {
		
		Consumer consumer = request.getConsumer();
		Map<String, PeerBalance> balances = AccountingControl.getInstance().getBalances(
				responses, CertificationUtils.getCertSubjectDN(myCertPath));
		
		return getRangeBasedPriorityAllocation(consumer, allAllocableWorkers, request.getSpecification().getRequirements(), 
						Priority.LOCAL_CONSUMER, request.getNeededWorkers(), balances, request.getSpecification().getAnnotations());
	}
	
	/* (non-Javadoc)
	 * @see Allocator#getAllocableWorkersForRemoteRequest(Consumer, RequestSpec, List)
	 */
	public List<AllocableWorker> getAllocableWorkersForRemoteRequest(List<IResponseTO> responses, Consumer consumer, RequestSpecification requestSpecification, String peerDNData,
																		List<AllocableWorker> allocableWorkers) {
		
		Priority requestPriority = PeerDAOFactory.getInstance().getTrustCommunitiesDAO().getPriority(responses, consumer.getPublicKey());
		Map<String, PeerBalance> balances = AccountingControl.getInstance().getBalances(responses, peerDNData);
		
		return getRangeBasedPriorityAllocation(consumer, allocableWorkers, requestSpecification.getRequirements(), 
						requestPriority, requestSpecification.getRequiredWorkers(), balances, requestSpecification.getAnnotations());
	}
	
	/**
	 * Take the workers beginning from the lower priority ranges, until the request priority.
     * In the request priority range, workers can be taken according to the network of favors balance.
     * If two workers consumers has the same priority (or if they belong to the same consumer), 
     * the most recent allocated worker is taken first.   
	 * The taken workers must match with the requests requirements.
	 * The result size is lower or equals requestNecessity.
	 * 
	 * @param consumerPubKey Consumer public key
	 * @param allocablesByPriority Map with the allocable workers, classified by priority
	 * @param requirements Request requirements, used to match the workers specifications
	 * @param requestPriority The request (consumer) priority
	 * @param requestNecessity The number of workers the request need
	 * @param balances 
	 * @return The list of workers to be allocated for this request
	 */
	public <A extends AllocableWorker> List<AllocableWorker> getRangeBasedPriorityAllocation(Consumer consumer,
			Collection<AllocableWorker> allAllocableWorkers, String requirements, Priority requestPriority, 
			int requestNecessity, Map<String, PeerBalance> balances, Map<String, String> jobAnnotations) {

		Map<Priority, List<AllocableWorker>> allocablesByPriority = createPriorityMap(allAllocableWorkers);
		int totalAllocableWorkers = getTotalOfAllocables(allocablesByPriority.values());
		PriorityProcessor<AllocableWorker> samePriorityProcessor = 
			new SamePriorityProcessor<AllocableWorker>(consumer, requirements, totalAllocableWorkers, balances, jobAnnotations);
		PriorityProcessor<AllocableWorker> lowerPriorityProcessor = 
			new LowerPriorityProcessor<AllocableWorker>(consumer, requirements, totalAllocableWorkers, balances, jobAnnotations);
		
		List<Priority> priorityOrderedList = new ArrayList<Priority>(allocablesByPriority.keySet());
		Collections.sort(priorityOrderedList);
		
		List<AllocableWorker> workersToAllocate = new LinkedList<AllocableWorker>();
		int allocationsLeft = requestNecessity;

		//sorted from minor to major priority
		for (Priority currentPriorityRange : priorityOrderedList) {
			allocationsLeft = requestNecessity - workersToAllocate.size();//update the allocation necessity
			
			if((allocationsLeft > 0)) {
				
				// FIXME: the priority list need be updated when the workers are donated to local consumers

			    List<AllocableWorker> workersInRange = allocablesByPriority.get(currentPriorityRange);
			    
			    if(requestPriority.compareTo(currentPriorityRange) < 0) {
			    	break;
			    }
			    
			    // FIXME: the priority list includes the workers donated to local consumers
			    // this for loop isn't clear, it seemed to sweep throughout all the priorities
			    
			    //taking from the same range and return
				if( (requestPriority.compareTo(currentPriorityRange) == 0)) {
					samePriorityProcessor.process(allocationsLeft, workersInRange, workersToAllocate);
					break;
				}

				//Taking allocations from less priority ranges
				lowerPriorityProcessor.process(allocationsLeft, workersInRange, workersToAllocate);

			} else {
				break;
			}
		}
		
		return workersToAllocate;
	}
	
	private <A extends AllocableWorker> int getTotalOfAllocables(Collection<List<A>> allocables) {
		/* intern contract */
		assert (allocables != null) : "Error in the DefaultAllocator.getRangeBasedPriorityAllocation(), the" +
				"DefaultAllocator.getTotalOfAllocables() received a null Collection";
		
		int count = 0;
		
		for (List<A> list : allocables) {
			count += list.size();
		}
		
		return count;
	}

	
	/**
	 * Maps <code>AllocableWorker</code> based in its <code>Priority</code>.
	 * 
	 * @param workers Allocable workers to be mapped
	 * @return A map with the priority as key and the worker as value
	 */
	private <A extends AllocableWorker> Map<Priority, List<A>> createPriorityMap(Collection<A> workers) {
		Map<Priority, List<A>> priorityMap = CommonUtils.createSerializableMap();
		
		for (A allocableWorker : workers) {
			
			Priority priority = allocableWorker.getPriority();
			List<A> sameRangeWorkers = priorityMap.get(priority);
			
			if(sameRangeWorkers == null) {
				sameRangeWorkers = new LinkedList<A>();
				priorityMap.put(priority, sameRangeWorkers);
			}
			
			sameRangeWorkers.add(allocableWorker);
		}
		
		return priorityMap;
	}

	/* (non-Javadoc)
	 * @see Allocator#getRequestForWorkerSpec(WorkerSpec)
	 */
	public Request getRequestForWorkerSpecification(WorkerSpecification remoteWorkerSpecification) {
		GotWorkerProcessor processor = new GotWorkerProcessor();
		List<Request> requests = PeerDAOFactory.getInstance().getRequestDAO().getRunningRequests();
		return processor.getInBalanceMatchedRequest(requests, remoteWorkerSpecification);
	}
}