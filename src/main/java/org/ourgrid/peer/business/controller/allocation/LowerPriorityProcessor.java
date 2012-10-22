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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.Consumer;
import org.ourgrid.peer.to.PeerBalance;

/**
 * This class represents a processor, which takes workers allocated in lower
 * priority ranges. 
 *
 * @param <A> AllocableWorker type
 */
class LowerPriorityProcessor <A extends AllocableWorker> extends PriorityProcessor<A> {
	
	protected LowerPriorityProcessor(Consumer consumer, String requirements, int totalAllocableWorkers, Map<String, PeerBalance> balances, Map<String, String> jobAnnotations) {
		super(consumer, requirements, totalAllocableWorkers, balances, jobAnnotations);
	}

	@Override
	public void process(int allocationsLeft, List<AllocableWorker> workersInRange, List<AllocableWorker> workersToAllocate) {
		List<AllocableWorker> possibleWorkersToAllocate = 
			getAllocationsPriorityRange(consumer, workersInRange, totalAllocableWorkers, workersToAllocate);

        takeNeededWorkers(possibleWorkersToAllocate, allocationsLeft, workersToAllocate);
	}
	
	/**
	 * List the workers that a consumer can take now. It do not consider the
	 * number of workers requested.
	 * 
	 * @param consumerPubKey
	 * @param workersInRange 
	 * @param totalAllocableWorkers
	 * @param workersToAllocate 
	 * @param requirements
	 * @return
	 */
	private List<AllocableWorker> getAllocationsPriorityRange( Consumer consumer,
									List<AllocableWorker> workersInRange, int totalAllocableWorkers, 
									List<AllocableWorker> workersToAllocate) {
		
		List<AllocableWorker> possibleWorkersToAllocate = new LinkedList<AllocableWorker>();
		
		//Generate the allocation info for the Allocations of a range. 
		List<AllocationInfo> othersConsumersInfo = 
			generateAllocationInfoList(consumer, workersInRange, totalAllocableWorkers );

		/* Removing the request AllocationInfo, this should not be in the taking algorithm */
		AllocationInfo requestInfo = getRequestInfo(othersConsumersInfo, consumer.getPublicKey());
		for (AllocableWorker worker : workersToAllocate) {
			requestInfo.addAllocation(worker);
		}
		othersConsumersInfo.remove(requestInfo);
		
		AllocableWorker alloc = null;

		do {
			alloc = takeLeastNOFBalanced(othersConsumersInfo, null);
		
			if(alloc != null) {
				workersInRange.remove(alloc);
				requestInfo.addAllocation(alloc);
				possibleWorkersToAllocate.add(alloc);
			}
			
		} while(alloc != null);
		
		return possibleWorkersToAllocate;
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.peer.controller.allocation.PriorityProcessor#takeLeastNOFBalanced(java.util.List, org.ourgrid.peer.controller.allocation.AllocationInfo)
	 */
	@Override
	protected AllocableWorker takeLeastNOFBalanced(List<AllocationInfo> infos, AllocationInfo requestInfo) {
		
		//picks allocations that match requirements
		List<AllocationInfo> matchedAllocations = getMatchedAllocations(infos);
		
		if (matchedAllocations.isEmpty()) {
			return null;
		}
		//if exists an idle worker, picks the newer 
		if (isThereAnIdleAllocation(matchedAllocations)) {
			return takeNewerMatchedAllocation(matchedAllocations.iterator().next());
		}
		
		//picks minimal prejudice removal
		AllocationInfo allocationToTake = getLeastNOFBalanced(matchedAllocations, infos, requestInfo);;
		Double leastPrejudice = getPrejudice(allocationToTake, requestInfo, infos);
		
		
		double currentPrejudice = MAX_PREJUDICE;
		
		if (leastPrejudice > currentPrejudice || 
				(leastPrejudice == currentPrejudice && getPeerBalance(consumer) < getPeerBalance(allocationToTake.getConsumer()))) {
			return null;
		}

		//picks newer allocable
		return takeNewerMatchedAllocation(allocationToTake);
	}
	

}