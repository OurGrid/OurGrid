package org.ourgrid.matchers;
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
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;

public class DiscoveryServiceCompleteStatusMatcher implements IArgumentMatcher {
	
	private Map<DiscoveryServiceInfo, Set<String>> network;
	
	public DiscoveryServiceCompleteStatusMatcher(Map<DiscoveryServiceInfo, Set<String>> network) {
		this.network = network;
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {
		
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object arg0) {
		
		if (arg0 == null) {
			return false; 
		}

		if ( !(DiscoveryServiceCompleteStatus.class.isInstance(arg0)) ) {
			return false;
		}
		
		DiscoveryServiceCompleteStatus otherCompleteStatus = (DiscoveryServiceCompleteStatus) arg0;
		Map<DiscoveryServiceInfo, Set<String>> otherNetwork = otherCompleteStatus.getNetwork();

		if (network.keySet().size() != otherNetwork.keySet().size()) {
			return false;
		}
		
		for ( DiscoveryServiceInfo dsInfo : otherNetwork.keySet()) {
			if (! network.containsKey(dsInfo)) {
				return false;
			}
			
			if (! otherNetwork.get(dsInfo).equals(network.get(dsInfo))) {
				return false;
			}			
		}
		
		return true;
	}

	public static DiscoveryServiceCompleteStatus eqMatcher(Map<DiscoveryServiceInfo, Set<String>> network) {
		EasyMock.reportMatcher(new DiscoveryServiceCompleteStatusMatcher(network));
		return null;
	}
}
