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
package org.ourgrid.discoveryservice.business.dao;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ourgrid.common.util.CommonUtils;

public class DiscoveryServiceDAO {

	private final Map<DiscoveryServiceInfo, Set<String>> network;
	private final Set<String> myPeers;
	
	/**
	 * @param application
	 */
	public DiscoveryServiceDAO() {
		this.network = CommonUtils.createSerializableMap();
		this.myPeers = new LinkedHashSet<String>();
	}
	
	public DiscoveryServiceInfo getDSInfo(String dsAddress){
		for ( DiscoveryServiceInfo dsInfo : getAllDiscoveryServicesInfos() ){
			if ( dsInfo.getDsAddress().equals(dsAddress) ){
				return dsInfo;
			}
		}
		return null;
	}

	public void peerIsUp( String peerUserAtServer ) {
		myPeers.add(peerUserAtServer);
	}
	
	public void peerIsDown(String peerUserAtServer) {
		this.myPeers.remove(peerUserAtServer);
	}

	public boolean isPeerUp(String peerUserAtServer) {
		return this.myPeers.contains(peerUserAtServer);
	}

	public boolean isOverloaded(int overloadThreshold) {
		return this.myPeers.size() >= overloadThreshold;
	}
	
	public boolean addDiscoveryService(DiscoveryServiceInfo newDS, Set<String> peers) {
		boolean notCommunityMember = !network.containsKey(newDS);
		network.put(newDS, peers);
		
		return notCommunityMember;
	}
	
	public void dsIsUp(String dsAddress) {
		DiscoveryServiceInfo dsInfo = getDSInfo(dsAddress);
		
		if (dsInfo != null) {
			dsInfo.setAsUp();
		}
	}
	
	public Set<DiscoveryServiceInfo> getAllDiscoveryServicesInfos() {
		return network.keySet();
	}
	
	public boolean removeFromNetwork(String failedDSAddress) {
		DiscoveryServiceInfo dsInfo = getDSInfo(failedDSAddress);
		
		if (dsInfo != null) {
			network.remove(dsInfo);

			dsInfo.setAsDown();
			network.put(dsInfo, new LinkedHashSet<String>());
			
			return true;
		}
		
		return false;
	}

	public List<String> getAllOnlinePeers() {
		return new LinkedList<String>(groupNetworkPeers());
	}
	
	public List<String> getMyOnlinePeers() {
		return new LinkedList<String>(this.myPeers);
	}
	
	public List<String> getDSOnlinePeers( String dsAddress ) {
		return new LinkedList<String>(network.get(new DiscoveryServiceInfo(dsAddress, true)));
	}
	
	public Map<DiscoveryServiceInfo, Set<String>> getNetwork() {
		return network;
	}
	
	private Set<String> groupNetworkPeers() {
		
		Set<String> group = new LinkedHashSet<String>();
		
		group.addAll(myPeers);
		
		Iterator<Set<String>> iterator = network.values().iterator();
		
		while ( iterator.hasNext() ){
			group.addAll(iterator.next());
		}
		
		return group;
	}
}