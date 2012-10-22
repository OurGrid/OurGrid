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
package org.ourgrid.peer.dao.trust;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.to.Priority;
import org.ourgrid.peer.to.Priority.Range;
import org.ourgrid.reqtrace.Req;

/**
 * Manage the trusty communities data.
 */
@Req("REQ110")
public class TrustCommunitiesDAO{

	private List<TrustyCommunity> communities;
	

	/**
	 * @param file Path to trust.xml file
	 */
	private void loadFromFile(List<IResponseTO> responses, File file){
		this.communities = new TrustCommunitiesFileManipulator().getCommunities(responses, file);
	}
	
	/**
	 * @param responses 
	 * @return The trusty communities list, sorted by priority and name 
	 */
	public List<TrustyCommunity> getTrustyCommunities(List<IResponseTO> responses){
		
		if (communities == null) {
			loadFromFile(responses, new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME));
		}
		
		return Collections.unmodifiableList(communities);
	}
	
	/**
	 * Gets the greatest {@link Priority} related to a peer. If there is
	 * no entry mapped to peer name and public key returns a {@link Priority}
	 * with ALLOC_FOR_UNKNOW_COMMUNITY {@link Range}
	 * 
	 * @param peerPublicKey
	 * @return
	 */
	public Priority getPriority(List<IResponseTO> responses,String peerPublicKey) {
		
		List<TrustyCommunity> filteredCommunities = 
						filterByPresence(peerPublicKey, getTrustyCommunities(responses));
		
		if(filteredCommunities.isEmpty()) {
			return Priority.UNKNOWN_PEER;
		}

		Collections.sort(filteredCommunities);
		int greatestPriority = filteredCommunities.get(filteredCommunities.size() - 1).getPriority();

		return new Priority(Priority.Range.ALLOC_FOR_TRUST_COMMUNITY, greatestPriority);
	}

	private List<TrustyCommunity> filterByPresence(String peerPublicKey, List<TrustyCommunity> trustyCommunities) {
		
		List<TrustyCommunity> responseCommunities = new LinkedList<TrustyCommunity>();
		
		for (TrustyCommunity trustyCommunity : trustyCommunities) {
			if(trustyCommunity.containsPeer(peerPublicKey)) {
				responseCommunities.add(trustyCommunity);
			}
		}
		
		return responseCommunities;
	}
	
}
