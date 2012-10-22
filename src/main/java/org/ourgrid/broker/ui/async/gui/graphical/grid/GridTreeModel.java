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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.ourgrid.broker.status.PeerStatusInfo;

public class GridTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	/** Default Mutable Tree Node */
	private DefaultMutableTreeNode root;

	/** List of UP and DOWN Peers */
	private Map<PeerStatusInfo,PeerNode> peer2node;

	/**
	 * Create a GridTreeModel.
	 */
	public GridTreeModel() {

		super( new DefaultMutableTreeNode( "Grid" ) );
		root = (DefaultMutableTreeNode) getRoot();
		peer2node = new LinkedHashMap<PeerStatusInfo, PeerNode>();
	}

/*
	private void resetTree() {

		root.removeAllChildren();
		reload( root );
	}
*/

	/**
	 * Refresh the tree
	 */
	public void refreshTree( Collection<PeerStatusInfo> entries ) {
		updateTree( entries );
	}


	private void updateTree( Collection<PeerStatusInfo> entriesCollection ) {

		Set<PeerStatusInfo> knownPeers = new LinkedHashSet<PeerStatusInfo>( peer2node.keySet() );
		List<PeerStatusInfo> entries = new ArrayList<PeerStatusInfo>(entriesCollection);

		synchronized ( this ) {
			for ( PeerStatusInfo entry : knownPeers ) {
				if ( !entries.contains( entry )  ) {
					DefaultMutableTreeNode node = peer2node.remove( entry );
					removeNodeFromParent( node );
				} else {
					PeerStatusInfo pe = entries.get(entries.indexOf(entry));
					if (!pe.getState().equals(entry.getState())) {
						DefaultMutableTreeNode node = peer2node.get(entry);
						entry.setState(pe.getState());
						node.setUserObject("Peer status : [ " + pe.getPeerSpec().getUserAndServer() + " ]"
								+ (pe.getLoginError() != null ? " => Broker is not logged, cause: " + pe.getLoginError() : ""));
					}
					entries.remove(pe);
				}
			}
		}

		for ( PeerStatusInfo entry : entries ) {
			PeerNode node = peer2node.get( entry );
			String peerInfo = "Peer status:  [ " + entry.getPeerSpec().getUserAndServer()
					 + " ]" + (entry.getLoginError() != null ? " => Broker is not logged, cause: " + entry.getLoginError() : "");
			
			if ( node == null ) {
				PeerNode peerNode = new PeerNode( entry, peerInfo );
				insertNodeInto( peerNode, root, root.getChildCount() );
				peer2node.put( entry, peerNode );
			} else {
				//node.setState( PeerNode.getState(entry) );
				node.setUserObject( peerInfo );
			}
		}
	
	}
}
