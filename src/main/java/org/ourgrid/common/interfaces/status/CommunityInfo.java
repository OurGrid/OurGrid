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
package org.ourgrid.common.interfaces.status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.DiscoveryServiceState;

public class CommunityInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String discoveryServiceAddress;
	private final DiscoveryServiceState status;
	private final List<String> connectedPeers;
	
	
	
	public CommunityInfo(String discoveryServiceAddress, DiscoveryServiceState status, 
			List<String> connectedPeers) {
		this.connectedPeers = connectedPeers;
		this.discoveryServiceAddress = discoveryServiceAddress;
		this.status = status;
	}

	public CommunityInfo() {
		this(null, DiscoveryServiceState.CONTACTING, new ArrayList<String>());
	}

	public String getDiscoveryServiceAddress() {
		return discoveryServiceAddress;
	}

	public DiscoveryServiceState getDiscoveryServiceStatus() {
		return status;
	}

	public List<String> getConnectedPeers() {
		return connectedPeers;
	}
	
}
