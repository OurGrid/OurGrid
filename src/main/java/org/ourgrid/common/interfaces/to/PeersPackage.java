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
package org.ourgrid.common.interfaces.to;

import java.io.Serializable;
import java.util.List;

import org.ourgrid.broker.status.PeerStatusInfo;


public class PeersPackage implements Serializable {

	private static final long serialVersionUID = 40L;
	
	private List<PeerStatusInfo> peers;
	
	public PeersPackage() {}

	public PeersPackage(List<PeerStatusInfo> peers) {
		this.peers = peers;
	}

	public List<PeerStatusInfo> getPeers() {
		return peers;
	}

	public void setPeers(List<PeerStatusInfo> peers) {
		this.peers = peers;
	}
}
