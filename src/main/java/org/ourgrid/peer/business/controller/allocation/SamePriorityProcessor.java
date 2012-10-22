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


public class SamePriorityProcessor <A extends AllocableWorker> extends PriorityProcessor<A> {

	
	public SamePriorityProcessor(Consumer consumer, String requirements, int totalAllocableWorkers, Map<String, PeerBalance> balances, Map<String, String> jobAnnotations) {
		super(consumer, requirements, totalAllocableWorkers, balances, jobAnnotations);
	}

	@Override
	public void process(int allocationsLeft, List<AllocableWorker> workersInRange, List<AllocableWorker> workersToAllocate) {
		if (workersInRange == null) {
			return;
		}
		
		List<AllocableWorker> possibleWorkersToAllocate = 
			getPossibleWorkersToAllocate(consumer, workersInRange,
					workersInRange.size() + workersToAllocate.size(), workersToAllocate, requirements);
		
		takeNeededWorkers(possibleWorkersToAllocate, allocationsLeft, workersToAllocate);
	}

	/**
	 * @param consumerPubKey
	 * @param allocables
	 * @param totalAllocableWorkers
	 * @param workersToAllocate 
	 * @param allocationsLeft 
	 * @param requirements
	 * @return
	 */
	private List<AllocableWorker> getPossibleWorkersToAllocate(Consumer consumer, List<AllocableWorker> allocables,
															int totalAllocableWorkers, List<AllocableWorker> workersToAllocate, String requirements) {
		
		List<AllocationInfo> othersConsumersInfo = generateAllocationInfoList(consumer, allocables,
				totalAllocableWorkers);
		
		/* Removing the request AllocationInfo, this should not be in the taking algorithm */
		AllocationInfo requestInfo = getRequestInfo(othersConsumersInfo, consumer.getPublicKey());
		for (AllocableWorker worker : workersToAllocate) {
			requestInfo.addAllocation(worker);
		}
		othersConsumersInfo.remove(requestInfo);

		
		List<AllocableWorker> result = new LinkedList<AllocableWorker>();
		AllocableWorker alloc = null;
		
		do {
			alloc = takeLeastNOFBalanced(othersConsumersInfo, requestInfo);
			if(alloc == null) {
				break;
			}
			requestInfo.addAllocation(alloc);
			result.add(alloc);
		
		} while (alloc != null);
		
		return result;
	}

	protected AllocableWorker takeLeastNOFBalanced(List<AllocationInfo> infos, AllocationInfo requestInfo) {
		
		//picks allocations that match requirements
		List<AllocationInfo> matchedAllocations = getMatchedAllocations(infos);
		if (matchedAllocations.isEmpty()) {
			return null;
		}
		
		//adds request info
		List<AllocationInfo> allAllocationsWithWinner = new LinkedList<AllocationInfo>(infos);
		if (requestInfo != null) {
			allAllocationsWithWinner.add(requestInfo);
		}
		
		//picks minimal prejudice removal
		AllocationInfo allocationToTake = getLeastNOFBalanced(matchedAllocations, allAllocationsWithWinner, requestInfo);;
		if (isEquallyBalanced(requestInfo, allocationToTake)) {
			return null;
		}
		
		Double leastPrejudice = getPrejudice(allocationToTake, requestInfo, allAllocationsWithWinner);
		
		double currentPrejudice = getPrejudice(null, null, allAllocationsWithWinner);
		
		if (leastPrejudice > currentPrejudice || 
				(leastPrejudice == currentPrejudice && getPeerBalance(consumer) < getPeerBalance(allocationToTake.getConsumer()))) {
			return null;
		}
		
		//picks newer allocable
		return takeNewerMatchedAllocation(allocationToTake);
	}
	
	private boolean isEquallyBalanced(AllocationInfo allocationA, AllocationInfo allocationB) {
		return getPeerBalance(allocationA.getConsumer()).equals(getPeerBalance(allocationB.getConsumer())) && 
			(allocationA.getBalance() >= 0 || allocationB.getBalance() < 0);
	}
}