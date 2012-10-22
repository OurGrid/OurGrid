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
package org.ourgrid.discoveryservice.status;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ourgrid.common.status.CompleteStatus;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;

/**
 * @author alan
 *
 */
public class DiscoveryServiceCompleteStatus extends CompleteStatus {

	private static final long serialVersionUID = 3699074602385829538L;

	private final List<String> connectedPeers;
	private final Map<DiscoveryServiceInfo, Set<String>> network;
	private final String myAddress;


	/**
	 * @param upTime
	 * @param configuration
	 */
	public DiscoveryServiceCompleteStatus(Map<DiscoveryServiceInfo, Set<String>> network, List<String> connectedPeers,
			long upTime, String configuration, String myAddress) {
		super(upTime, configuration);
		this.connectedPeers = connectedPeers;
		this.network = network;
		this.myAddress = myAddress;
	}
	
	public Map<DiscoveryServiceInfo, Set<String>> getNetwork() {
		return network;
	}
	
	public List<String> getConnectedPeers() {
		return connectedPeers;
	}

	public String getMyAddress() {
		return myAddress;
	}
	
	
	
	

}
