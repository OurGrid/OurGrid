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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationAnnotationsComparator;
import org.ourgrid.peer.business.controller.matcher.MatcherImpl;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.Consumer;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.peer.to.RemoteConsumer;


public abstract class PriorityProcessor <A extends AllocableWorker> {
	
	private MatcherImpl matcherImpl = new MatcherImpl();
	
	protected static final double MAX_PREJUDICE = Double.MAX_VALUE;
	
	protected final Consumer consumer;
	protected final String requirements;
	protected final int totalAllocableWorkers;
	protected final Map<String, PeerBalance> balances;
	private final Map<String, String> jobAnnotations;

	protected PriorityProcessor(Consumer consumer, String requirements, int totalAllocableWorkers, 
			Map<String, PeerBalance> balances, Map<String, String> jobAnnotations) {
		this.consumer = consumer;
		this.requirements = requirements;
		this.totalAllocableWorkers = totalAllocableWorkers;
		this.balances = balances;
		this.jobAnnotations = jobAnnotations;
	}

	public abstract void process(int allocationsLeft, List<AllocableWorker> workersInRange, List<AllocableWorker> workersToAllocate);
	
    protected void takeNeededWorkers(List<AllocableWorker> possibleWorkersToAllocate, int allocationsLeft, List<AllocableWorker> workersToAllocate) {
        List<AllocableWorker> response = new LinkedList<AllocableWorker>();
        
        Iterator<AllocableWorker> iterator = possibleWorkersToAllocate.iterator();
        for (int i = 0; (i < allocationsLeft) && (iterator.hasNext()); i++) {
            response.add(iterator.next());
        }
        
        workersToAllocate.addAll(response);
    }
    
	protected boolean isThereAnIdleAllocation(List<AllocationInfo> allocations) {
		for (AllocationInfo info : allocations) {
			if (info.getConsumer() == null) {
				return true;
			}
		}
		
		return false;
	}
    
	protected abstract AllocableWorker takeLeastNOFBalanced(List<AllocationInfo> infos, AllocationInfo requestInfo);

	/**
	 * Sorts the allocations using the NetworkOfFavors criteria and gets the head of the list
	 * 
	 * @param allocations
	 * @param allAllocationsWithWinner
	 * @param requestInfo
	 * @return
	 */
	protected AllocationInfo getLeastNOFBalanced(List<AllocationInfo> allocations, final List<AllocationInfo> allAllocationsWithWinner, 
			final AllocationInfo requestInfo) {
	
		Collections.sort(allocations, new Comparator<AllocationInfo>() {
			public int compare(AllocationInfo o1, AllocationInfo o2) {
				Double prejudice1 = getPrejudice(o1, requestInfo, allAllocationsWithWinner);
				Double prejudice2 = getPrejudice(o2, requestInfo, allAllocationsWithWinner);
				
				if (!prejudice1.equals(prejudice2)) {
					return prejudice1.compareTo(prejudice2);
				}
				
				Double peerBalance1 = getPeerBalance(o1.getConsumer());
				Double peerBalance2 = getPeerBalance(o2.getConsumer());
				
				if (!peerBalance1.equals(peerBalance2)) {
					return getPeerBalance(o1.getConsumer()).compareTo(getPeerBalance(o2.getConsumer()));
				}
				
				Integer diff1 = o1.getBalance();
				Integer diff2 = o2.getBalance();
				
				
				if (!diff1.equals(diff2)) {
					return diff2.compareTo(diff1);
				}
				
				return compareNewerAllocations(o2, o1);
			}
		});
		
		return allocations.get(0);
	}
	
	/**
	 * Decide which consumer has the newer allocation
	 * @param o1
	 * @param o2
	 * @return
	 */
	private int compareNewerAllocations(AllocationInfo o1, AllocationInfo o2) {
		long newerTimeStamp1 = Long.MAX_VALUE;
		for (AllocableWorker allocable : o1.getAllocations()) {
			if (allocable.getLastConsumerAssignTimeStamp() < newerTimeStamp1) {
				newerTimeStamp1 = allocable.getLastConsumerAssignTimeStamp();
			}
		}
		
		long newerTimeStamp2 = Long.MAX_VALUE;
		for (AllocableWorker allocable : o2.getAllocations()) {
			if (allocable.getLastConsumerAssignTimeStamp() < newerTimeStamp2) {
				newerTimeStamp2 = allocable.getLastConsumerAssignTimeStamp();
			}
		}
		
		return newerTimeStamp1 > newerTimeStamp2 ? 1 : -1;
	}

	/**
	 * Generates the <code>AllocationInfo</code> related to current consumers 
	 * plus the new consumer.
	 *  
	 * @param newConsumerPubKey
	 * @param allocations
	 * @param totalWorkers
	 * @return
	 */
	protected List<AllocationInfo> generateAllocationInfoList( Consumer newConsumer, 
																List<AllocableWorker> allocations, int totalWorkers) {
		
		Map<Consumer, List<AllocableWorker>> consumer2Allocables = Util.createConsumersMap(allocations);

		/* Add a map entry without allocations, for the new consumer, if necessary */
		if(! consumer2Allocables.containsKey(newConsumer)) {
			consumer2Allocables.put(newConsumer, new LinkedList<AllocableWorker>());
		}
		
		return Util.generateAllocationInfo(allocations, totalWorkers, consumer2Allocables);
	}

	protected AllocationInfo getRequestInfo(List<AllocationInfo> infos, String consumerPubKey) {

		for(AllocationInfo info : infos){
			if(info.getConsumerPubKey().equals(consumerPubKey)){
				return info;
			}
		}
		
		return null;
	}
	
	/**
	 * Calculates the prejudice for a given scenario of allocations
	 * @param allocationToTake
	 * @param allocationToWin
	 * @param allAllocations
	 * @return
	 */
	protected Double getPrejudice(AllocationInfo allocationToTake, AllocationInfo allocationToWin, List<AllocationInfo> allAllocations) {
		double totalAllocations = 0;
		double totalPeerBalance = 0;
		
		for (AllocationInfo info : allAllocations) {
			totalAllocations += info.getAllocations().size();
			totalPeerBalance += getPeerBalance(info.getConsumer());
		}
		
		if (allocationToTake != null && allocationToWin == null) {
			totalAllocations--;
		}
		
		double totalPrejudice = 0;
		for (AllocationInfo info : allAllocations) {
			double balance = (totalPeerBalance > 0) ? getPeerBalance(info.getConsumer()) / totalPeerBalance : totalPeerBalance;
			double allocations = info.getAllocations().size();
			
			if (info == allocationToTake) {
				allocations--;
			} else if (info == allocationToWin) {
				allocations++;
			}
			
			totalPrejudice += Math.abs(balance - (allocations / totalAllocations));
		}
		return round(totalPrejudice);
	}

	/**
	 * Rounds the decimal using 5 decimal digits
	 * @param numberToTrunc
	 * @return
	 */
	private Double round(Double numberToTrunc) {
		return Math.round(numberToTrunc * 1E5) / 1E5;
	}

	
	/**
	 * Returns the peer balance of a remote peer, 
	 * 0 if there was no accounting reported for this peer.
	 * @param consumer
	 * @return
	 */
	protected Double getPeerBalance(Consumer consumer) {
		if (!(consumer instanceof RemoteConsumer)) {
			return 0.;
		}
		
		PeerBalance peerBalance = balances.get(((RemoteConsumer)consumer).getConsumerDN());
		return (peerBalance == null) ? 0. : peerBalance.getCPUTime();
	}

	protected AllocableWorker takeNewerMatchedAllocation(AllocationInfo allocation) {
		List<AllocableWorker> matchedAllocations = getMatchedAllocations(allocation, requirements);
		AllocableWorker allocableW = getNewerAllocation(matchedAllocations);
		
		allocation.removeAllocation(allocableW);
		
		return allocableW;
	}

	/**
	 * @param jobRequirement
	 * @param matchedAllocableWorkers
	 * @param allocableWorker
	 */
	private boolean workerMatchWithRequeriments(String jobRequirement, AllocableWorker allocableWorker) {
		return matcherImpl.match(jobRequirement, allocableWorker.getWorkerSpecification().getAttributes());
	}
	
	private int workerAdMatchWithRequeriments(String jobRequirement, AllocableWorker allocableWorker) {
		return matcherImpl.match(jobRequirement, allocableWorker.getWorkerSpecification().getExpression());
	}
	
	public List<AllocableWorker> getMatchedAllocations(AllocationInfo allocations, String requirements) {
		
		List<AllocableWorker> matchedAllocations = new LinkedList<AllocableWorker>();
		
		//Evaluating if it's using ClassAd and JDL or JDF
		if(allocations.getAllocations().size() > 0){
			if(allocations.getAllocations().get(0).getWorkerSpecification().usingClassAd()){
				getClassAdMatchedAllocations(matchedAllocations, allocations, requirements);
			}else{//JDF
				for (AllocableWorker allocable : allocations.getAllocations()) {
					if(workerMatchWithRequeriments(requirements, allocable)) {
						matchedAllocations.add(allocable);
					}
				}
			}
		}
		
		return matchedAllocations;
	}
	
	/**
	 * This method considers the rank value return by the matchmaking using ClassAd in order
	 * to sort AllocableWorkers that will be returned
	 * @param matchedAllocations 
	 * @param allocations
	 * @param requirements2 
	 * @return
	 */
	private void getClassAdMatchedAllocations(List<AllocableWorker> matchedAllocations, AllocationInfo allocations, String requirements) {
		TreeMap<Integer, List<AllocableWorker>> orderedWorkers = new TreeMap<Integer, List<AllocableWorker>>();
		String requirementsToUse = ( requirements == null ) ? this.requirements : requirements;
		
		for (AllocableWorker allocable : allocations.getAllocations()) {
			int matchedNumber = workerAdMatchWithRequeriments(requirementsToUse, allocable);
			if(matchedNumber != -1){
				List<AllocableWorker> list = orderedWorkers.get(matchedNumber);
				if(list == null){
					list = new ArrayList<AllocableWorker>();
					orderedWorkers.put(matchedNumber, list);
				}
				list.add(allocable);
			}
		}
		
		ArrayList<Integer> arrayList = new ArrayList<Integer>(orderedWorkers.keySet());
		Collections.sort( arrayList );
		Collections.reverse( arrayList );
		
		for (Integer matchedNumber : arrayList) {
			matchedAllocations.addAll(orderedWorkers.get(matchedNumber));
		}
	}

	public List<AllocationInfo> getMatchedAllocations(List<AllocationInfo> allAllocations) {
		List<AllocationInfo> allocations = new LinkedList<AllocationInfo>();
		for (AllocationInfo info : allAllocations) {
			if (hasAMatchedAllocation(info)) {
				allocations.add(info);
			}
		}
		
		return allocations;
	}

	private boolean hasAMatchedAllocation(AllocationInfo allocation) {
		return !getMatchedAllocations(allocation, requirements).isEmpty();
	}
	
	protected AllocableWorker getNewerAllocation(List<AllocableWorker> matchedAllocations) {
		
		//private contract, errors in the callee methods 
		assert (matchedAllocations != null) : "Error in the DefaultAllocator.getYougerAllocation method";

		allocationSort(matchedAllocations);
		return ( ! matchedAllocations.isEmpty() ) ? matchedAllocations.get(0) : null;//the newer
	}

	
    /**
     * @param matchedAllocations
     */
    //TODO CHANGE HERE
    private void allocationSort(List<AllocableWorker> matchedAllocations) {
        Collections.sort(matchedAllocations, new AllocableWorkerComparator());
    }

    class AllocableWorkerComparator implements Comparator< AllocableWorker >{

            private Comparator<WorkerSpecification> workerSpecAnnotationsComparator = new WorkerSpecificationAnnotationsComparator(jobAnnotations);

            public int compare( AllocableWorker o1, AllocableWorker o2 ) {

                    int firstComparison = workerSpecAnnotationsComparator.compare( o1.getWorkerSpecification(), o2.getWorkerSpecification());
                    if( firstComparison != 0)
                            return firstComparison;

                    return o2.getLastConsumerAssignTimeStamp() > o1.getLastConsumerAssignTimeStamp() ? 1 : -1;
            }

    }
	
	
}