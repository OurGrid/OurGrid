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
package org.ourgrid.broker.ui.async.gui.graphical.grid;

import javax.swing.tree.DefaultMutableTreeNode;

import org.ourgrid.broker.status.PeerStatusInfo;
import org.ourgrid.common.specification.peer.PeerSpecification;

public class PeerNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	PeerStatusInfo entry;

	public PeerNode( PeerStatusInfo entry, String peerInfo ) {

		super( peerInfo );
		this.entry = entry;
	}

	public PeerSpecification getSpec() {
		return entry.getPeerSpec();
	}

	public boolean isNotLogged() {
		return entry.isNotLogged();
	}
	
	public boolean isDown() {
		return entry.isDown();
	}

	public boolean isLogged() {
		
		return entry.isLogged();
	}
	
}
