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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.Consumer;
import org.ourgrid.peer.to.Request;


/**
 * This class represents a processor, which allocates a worker in the local
 * request which has less workers and matches with the request. 
 */
class GotWorkerProcessor  {
	
	/**
	 * @param requests
	 * @param workerSpec
	 * @return
	 */
	Request getInBalanceMatchedRequest(List<Request> requests, WorkerSpecification workerSpec) {

		List<Request> matchedRequests = new ArrayList<Request>();
		
		for (Request request : requests) {
			if (Util.matchAndNeedWorkers(workerSpec, request)) {
				matchedRequests.add(request);
			}
		}
		
		List<AllocableWorker> allocables = new LinkedList<AllocableWorker>(getAllocablesRelatedTo(requests));
		
		if(!allocables.isEmpty()) {
			List<AllocationInfo> allocsInfo = Util.generateAllocationInfo(allocables,		
					allocables.size(), createConsumersMap(allocables, requests));
			List<Request> lowerRequests = downBalancedRequest(allocsInfo, matchedRequests);
			return getOldestRequest(lowerRequests); 
			
		}
		
		if (!matchedRequests.isEmpty()) {
			return getOldestRequest(matchedRequests); 
		}
		
		return null; 
	}

	private Request getOldestRequest(List<Request> requests) {

		if (requests.isEmpty()) {
			return null;
		} else {
			return requests.get(requests.size() - 1);
		}
	}

	private Collection<AllocableWorker> getAllocablesRelatedTo(Collection<Request> requests) {
        Collection<AllocableWorker> allocableWorkers = new LinkedList<AllocableWorker>();    
		
	    for (Request request : requests) {
	        allocableWorkers.addAll(request.getAllocableWorkers());
        }
		return allocableWorkers;
	}


	private <A extends AllocableWorker> Map<Consumer, List<A>> createConsumersMap(List<A> allocations, List<Request> requests) {
		
		Map<Consumer, List<A>> consumersMap = Util.createConsumersMap(allocations);
		
		for (Request request : requests) {
			
			Consumer consumer = request.getConsumer();
			
			if(! consumersMap.containsKey(consumer))  {
				consumersMap.put(consumer, new LinkedList<A>());
			}
		}
		
		return consumersMap;
	}
	
	/**
	 * @param allocsInfo
	 * @param matchedRequests
	 * @param workerSpec
	 * @return
	 */
	private List<Request> downBalancedRequest(List<AllocationInfo> allocsInfo, final List<Request> matchedRequests) {

		List<Request> lowerRequests = new LinkedList<Request>();
		
		do {
			List<AllocationInfo> downBalancedAllocations = getDownBalancedConsumers(allocsInfo);
			
			boolean matched = false;

			for (AllocationInfo downBalancedAllocation : downBalancedAllocations) {
				Request request = selectByID(matchedRequests, downBalancedAllocation.getConsumerPubKey());
				
				if (request != null) {
					lowerRequests.add(request);
					matched = true;
				}
				allocsInfo.remove(downBalancedAllocation);
			}
			
			if (matched) {
				break;
			}

		} while (!allocsInfo.isEmpty());

		Comparator<Request> requestComparator = new Comparator<Request>() {
			public int compare(Request o1, Request o2) {
				return getPosition(o1) - getPosition(o2);
			}
			private int getPosition(Request o1) {
				return matchedRequests.indexOf(o1);
			}
			
		};
		
		Collections.sort(lowerRequests,	 requestComparator);
		
		return lowerRequests;			
	}

	/**
	 * @param infos
	 * @return
	 * matched allocations.
	 */
	private <A extends AllocableWorker> List<AllocationInfo> getDownBalancedConsumers(List<AllocationInfo> infos) {
		Collections.sort(infos, Collections.reverseOrder());
		
		List<AllocationInfo> result = new LinkedList<AllocationInfo>();
		
		if (infos != null && !infos.isEmpty()) {
			
			AllocationInfo lowerConsumer = infos.get(0);
			result.add(lowerConsumer);
			
			for (int i = 1; i < infos.size(); i++) {
				AllocationInfo currentConsumer = infos.get(i);

				if (currentConsumer.getBalance() == lowerConsumer.getBalance()) {
					result.add(currentConsumer);
					
				} else {
					break;
				}
			}
		}
		
		return result;
	}

	private Request selectByID(Collection<Request> requests, String consumerPubKey) {
		
		for (Request request : requests) {
			if(request.getConsumer().getPublicKey().equals(consumerPubKey)) {
				return request;
			}
		}
		
		return null;
	}
}