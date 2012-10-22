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
package org.ourgrid.peer.dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.RemoteAllocableWorker;
import org.ourgrid.reqtrace.Req;

/**
 * Stores <code>AllocableWorker</code>s, that is, recovered Workers that can be allocated to local users
 */
public class AllocationDAO {

	private final Map<String, LocalAllocableWorker> localAllocableWorkersByPubKey;
	private final Map<String, RemoteAllocableWorker> remoteAllocableWorkersByPubKey;
	private final Map<String, RemoteAllocableWorker> notRecoveredRemoteAllocableWorkersByAddress;
	
	
	@Req("REQ010")
	public AllocationDAO() {
		localAllocableWorkersByPubKey = CommonUtils.createMap();
		remoteAllocableWorkersByPubKey = CommonUtils.createMap();
		notRecoveredRemoteAllocableWorkersByAddress = CommonUtils.createMap();
	}
	
	/**
	 * Get all <code>AllocableWorker</code>s stored in this manager that are on the specified status.
	 * @param status The status to filter the workers
	 * @return A <code>Collection</code> containing all <code>AllocableWorker</code> on the
	 * specified status.
	 */
	@Req({"REQ020", "REQ036", "REQ038a"})
	public Collection<? extends AllocableWorker> getLocalAllocableWorkers(LocalWorkerState status){
		return getAllocableWorkers(status, localAllocableWorkersByPubKey.values());
	}
	
	/**
	 * Get all <code>AllocableWorker</code>s stored in this manager that are on the specified status and
	 * held by a specific <code>AllocableWorker</code> collection;
	 * @param <A>
	 * @param status
	 * @param allocables
	 * @return
	 */
	public <A extends AllocableWorker> Collection<A> getAllocableWorkers(LocalWorkerState status, 
						Collection<A> allocables){
		
		Collection<A> result = new LinkedList<A>();
		
		for ( A localWorker : allocables ) {
			if(localWorker.getStatus().equals(status)){
				result.add(localWorker);
			}
		}
		return result;
	}
	
	/**
	 * @param wManagementPubKey
	 * @return
	 */
	@Req({"REQ010","REQ025"})
	public LocalAllocableWorker removeLocalAllocableWorker(String wManagementPubKey) {
		return localAllocableWorkersByPubKey.remove(wManagementPubKey);
	}

	/**
	 * @param remoteWorkerManagementPubKey
	 * @return
	 */
	@Req({"REQ015"})
	public RemoteAllocableWorker removeRemoteAllocableWorker(String remoteWorkerManagementPubKey) {
		return remoteAllocableWorkersByPubKey.remove(remoteWorkerManagementPubKey);
	}
	
	/**
	 * @param wManagementPubKey
	 * @return
	 */
	@Req("REQ025")
	public AllocableWorker getAllocableWorker(String wManagementPubKey) {

		AllocableWorker localAlloc = localAllocableWorkersByPubKey.get(wManagementPubKey);
		AllocableWorker remoteAlloc = remoteAllocableWorkersByPubKey.get(wManagementPubKey);

		assert !(localAlloc != null && remoteAlloc != null) : "Error on AllocationDAO" +
				"there are two allocable with the same pubkey"; 

		return (localAlloc != null) ? localAlloc : remoteAlloc;
	}
	
	/**
	 * @param wManagementePubKey
	 * @return
	 */
	@Req({"REQ025", "REQ112"})
	public RemoteAllocableWorker getRemoteAllocableWorker(String wManagementePubKey) {
		return remoteAllocableWorkersByPubKey.get(wManagementePubKey);
	}
	
	public RemoteAllocableWorker getNotRecoveredRemoteAllocableWorker(String wManagementeAddress) {
		return notRecoveredRemoteAllocableWorkersByAddress.get(wManagementeAddress);
	}
	
	/**
	 * Adds a <code>AllocableWorker</code> to this manager
	 * @param allocableWorker The <code>AllocableWorker</code> to add
	 */
	@Req("REQ025")
	public void addLocalAllocableWorker(String lwmPublicKey, LocalAllocableWorker allocableWorker) {
		localAllocableWorkersByPubKey.put(lwmPublicKey, allocableWorker);
	}
	
	/**
	 * @param remoteAllloc
	 */
	@Req({"REQ018"})
	public void addRemoteAllocableWorker(String rwmPublicKey, RemoteAllocableWorker remoteAllloc) {
		remoteAllocableWorkersByPubKey.put(rwmPublicKey, remoteAllloc);
	}

	public void addNotRecoveredRemoteAllocableWorker(String rwmAddress, RemoteAllocableWorker remoteAllloc) {
		notRecoveredRemoteAllocableWorkersByAddress.put(rwmAddress, remoteAllloc);
	}
	
	public void recoverRemoteWorker(String rwmAddress, String rwmPublicKey) {
		RemoteAllocableWorker remoteWorker = notRecoveredRemoteAllocableWorkersByAddress.remove(rwmAddress);
		remoteWorker.setWorkerPubKey(rwmPublicKey);
		remoteAllocableWorkersByPubKey.put(rwmPublicKey, remoteWorker);
	}
	
	/**
	 * Get the <code>LocalAllocableWorker</code> to advert.
	 * 
	 * @return
	 */
	@Req("REQ020")
	public Collection<LocalAllocableWorker> getAllocableWorkersToAdvert() {
		
		Collection<LocalAllocableWorker> result = getAllocableWorkers(LocalWorkerState.IDLE, 
							localAllocableWorkersByPubKey.values());
		
		result.addAll(getAllocableWorkers(LocalWorkerState.DONATED, 
							localAllocableWorkersByPubKey.values()));
		return result;
	}

	/**
	 * @return
	 */
	public List<AllocableWorker> getAllAllocableWorkers(){
		List<AllocableWorker> allAllocableWorkers = new LinkedList<AllocableWorker>();
		allAllocableWorkers.addAll(getLocalAllocableWorkers());
		allAllocableWorkers.addAll(getRemoteAllocableWorkers());
		return allAllocableWorkers;
	}
	
	/**
	 * @return
	 */
	public List<AllocableWorker> getLocalAllocableWorkers() {
		List<AllocableWorker> localAllocableWorkers = new LinkedList<AllocableWorker>();
		localAllocableWorkers.addAll(localAllocableWorkersByPubKey.values());
		return localAllocableWorkers;
	}
	
	/**
	 * @param workerPubKey
	 * @return
	 */
	public LocalAllocableWorker getLocalAllocableWorker(String workerPubKey) {
		return localAllocableWorkersByPubKey.get(workerPubKey);
	}
	
	/**
	 * @return
	 */
	@Req({"REQ038a", "REQ037"})
	public List<RemoteAllocableWorker> getRemoteAllocableWorkers() {
		List<RemoteAllocableWorker> remoteAllocableWorkers = new LinkedList<RemoteAllocableWorker>();
		remoteAllocableWorkers.addAll(remoteAllocableWorkersByPubKey.values());
		return remoteAllocableWorkers;
	}
}
