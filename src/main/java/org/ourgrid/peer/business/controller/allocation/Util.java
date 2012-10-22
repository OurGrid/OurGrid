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

import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.business.controller.matcher.MatcherImpl;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.Consumer;
import org.ourgrid.peer.to.Request;
import org.ourgrid.reqtrace.Req;

/**
 * Utilitary method for allocation
 */
@Req("REQ011")
public class Util  {

	private static MatcherImpl matcherImpl = new MatcherImpl();

	/**
	 * @param <A>
	 * @param allocations
	 * @param totalWorkers
	 * @param consumer2Allocables
	 * @return
	 */
	static <A extends AllocableWorker> List<AllocationInfo> generateAllocationInfo(List<A> allocations, int totalWorkers,
									Map<Consumer, List<A>> consumer2Allocables) {
		
		int totalConsumers = consumer2Allocables.keySet().size();
		List<AllocationInfo> info = new LinkedList<AllocationInfo>();
		
		for (Map.Entry<Consumer, List<A>> allocationEntry : consumer2Allocables.entrySet()) {
			
			int deservedWorkers = totalWorkers/totalConsumers;
			info.add(new AllocationInfo(deservedWorkers, allocationEntry.getKey()));
		}
		
		List<A> noConsumerAllocables = new LinkedList<A>();
		/* Create AllocationInfo for the Allocables that have no consumer */
		for (A allocableWorker : allocations) {
			if(allocableWorker.getConsumer() ==  null) {
				noConsumerAllocables.add(allocableWorker);
			}
		}
		
		if(!noConsumerAllocables.isEmpty()) {
			AllocationInfo noConsumerAllocations = new AllocationInfo(0, null);
			
			for (A a : noConsumerAllocables) {
				noConsumerAllocations.addAllocation(a);
			}
			
			info.add(noConsumerAllocations);
		}
		
		return info;
	}

	/**
	 * @param <A>
	 * @param allocations
	 * @return
	 */
	static <A extends AllocableWorker> Map<Consumer, List<A>> createConsumersMap(List<A> allocations) {
		Map<Consumer, List<A>> consumersMap = CommonUtils.createSerializableMap();
		
		for (A allocableWorker : allocations) {
			Consumer consumer = allocableWorker.getConsumer();
			
			if(consumer != null) {//Some allocable may not have a consumer, the idle ones.
				
				if(! consumersMap.containsKey(consumer))  {
					consumersMap.put(consumer, new LinkedList<A>());
				}
				
				List<A> allocs = consumersMap.get(consumer);
				allocs.add(allocableWorker);
			}
		}
		
		return consumersMap;
	}

	public static boolean matchAndNeedWorkers(WorkerSpecification workerSpecification, Request request) {
		if(workerSpecification.getExpression() != null){
			
			return matcherImpl.match(request.getSpecification().getRequirements(), workerSpecification.getExpression()) != -1
			&& !request.isWorkerUnwanted(workerSpecification) 
			&& !request.isPaused() 
			&& request.needMoreWorkers();
		}
		
		return matcherImpl.match(request.getSpecification().getRequirements(), workerSpecification.getAttributes()) 
			&& !request.isWorkerUnwanted(workerSpecification) 
			&& !request.isPaused() 
			&& request.needMoreWorkers();
	}
}