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
package org.ourgrid.broker.business.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.reqtrace.Req;

/**
 * Maintains information about the current 
 * workers providers of this Broker.
 * 
 */
public class PeerDAO {

	private final Map<String, PeerEntry> currentPeers;
	
	
	public PeerDAO() {
		this.currentPeers = CommonUtils.createMap();
	}

	@Req("REQ309")
	public void setPeers(Collection<PeerSpecification> peers) {
		
		currentPeers.clear();
		
		for (PeerSpecification peer : peers) {
			currentPeers.put(peer.getServiceID().toString(), 
					new PeerEntry(peer));
		}
	}
	
	public Collection<String> getPeersAddresses() {
		return new ArrayList<String>(this.currentPeers.keySet());
	}
	
	public Collection<PeerEntry> getPeers() {
		return new ArrayList<PeerEntry>(this.currentPeers.values());
	}

	public boolean containsPeer(String peerAddress) {
		return this.currentPeers.containsKey(peerAddress);
	}

	/**
	 * @param entityID
	 * @return
	 */
	public PeerEntry getPeerEntry(String peerAddress) {
		return this.currentPeers.get(peerAddress);
	}

	public boolean hasPeerUp() {
		for (PeerEntry peerEntry : currentPeers.values()) {
			if (!peerEntry.isDown()) {
				return true;
			}
		}
		
		return false;
	}

	public Collection<PeerEntry> getLoggedPeersEntries() {
		Collection<PeerEntry> loggedPeers = new ArrayList<PeerEntry>();
		
		for (PeerEntry entry : this.currentPeers.values()) {
			if (entry.isLogged()) {
				loggedPeers.add(entry);
			}
		}
		
		return loggedPeers;
	}

	public void removePeer(String peerID) {
		this.currentPeers.remove(peerID);
	}
}
