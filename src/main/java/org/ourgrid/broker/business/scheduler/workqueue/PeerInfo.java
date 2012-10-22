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
package org.ourgrid.broker.business.scheduler.workqueue;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class PeerInfo {
	
	private static PeerInfo instance;
	
	private List<String> loggedPeersID;

	private PeerInfo() {
		this.loggedPeersID = new ArrayList<String>();
	}

	public static PeerInfo getInstance() {
		if (instance == null) {
			instance = new PeerInfo();
		}
		return instance;
	}
	
	public static void reset() {
		instance = new PeerInfo();
	}
	
	public void addPeerLogged(String peerID) {
		this.loggedPeersID.add(peerID);
	}
	
	public void removePeerLogged(String peerID) {
		this.loggedPeersID.remove(peerID);
	}
	
	public void removePeersLogged() {
		this.loggedPeersID = new ArrayList<String>();
	}
	
	public List<String> getLoggedPeersIDs() {
		return this.loggedPeersID;
	}
	
}
