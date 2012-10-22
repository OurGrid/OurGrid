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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DiscoveryServiceClientDAO {
	
	private static int MAX_NUMBER_OF_DS = 10;

	private static int MAX_NUMBER_OF_ALIVE_DS = 2;
	
	private Set<String> providersAddress;
	
	private Set<String> aliveDiscoveryServicesAddress;

	private final LinkedList<String> dsAddresses = new LinkedList<String>();
	
	/**
	 * @param component
	 */
	public DiscoveryServiceClientDAO() {
		this.providersAddress = new LinkedHashSet<String>();
		this.aliveDiscoveryServicesAddress = new LinkedHashSet<String>();
	}
	


	public String getAliveDiscoveryServiceAddress() {
		if (isAliveDsListEmpty()) {
			return null;
		}
		return aliveDiscoveryServicesAddress.iterator().next();
	}

	public boolean isAliveDsListEmpty() {
		return aliveDiscoveryServicesAddress.isEmpty();
	}
	
	/**
	 * 
	 * @param discoveryService
	 * @return true if the discovery service list is full, false otherwise
	 */
	public boolean addAliveDiscoveryServiceAddress(String address) {
		aliveDiscoveryServicesAddress.add(address);
		return aliveDiscoveryServicesAddress.size() >= MAX_NUMBER_OF_ALIVE_DS;
	}
	
	public Collection<String> getRemoteWorkerProvidersAddress() {
		return this.providersAddress;
	}
	
	public void addRemoteWorkerProviderAddress(String providerAddress) {
		if (!providersAddress.contains(providerAddress)) {
			this.providersAddress.add(providerAddress);
		}
	}
	
	public boolean removeRemoteWorkerProviderAddress(String providerAddress) {
		return this.providersAddress.remove(providerAddress);
	}
	
	public void clear() {
		this.providersAddress.clear();
	}

	public boolean isConnected() {
		return aliveDiscoveryServicesAddress != null && !aliveDiscoveryServicesAddress.isEmpty();
	}
	

	/**
	 * @return the dsAdresses
	 */
	public List<String> getDsAddresses() {
		return new ArrayList<String>(dsAddresses);
	}
	
	public boolean addDsAddress(String dsAddress) {
		boolean changed = false;
		if (!dsAddresses.contains(dsAddress)){
			dsAddresses.addFirst(dsAddress);
			if (dsAddresses.size() > MAX_NUMBER_OF_DS){
				dsAddresses.removeLast();
			}
			changed = true;
		}
		return changed;
	}
	
	public void removeAliveDiscoveryService(String address) {
		aliveDiscoveryServicesAddress.remove(address);
	}

	public boolean isDsAlive( String address ) {
		return aliveDiscoveryServicesAddress.contains(address);
	}
	
}
