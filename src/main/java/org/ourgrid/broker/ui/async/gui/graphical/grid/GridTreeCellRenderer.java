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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Description:
 * 
 * @version 1.0 Created on 26/08/2004
 */
public class GridTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private static final Icon upIcon = createIcon( "peer_online.gif" );
	private static final Icon loggedIcon = createIcon( "peer_idle.gif" );
	private static final Icon downIcon = createIcon( "peer_offline.gif" );


	public GridTreeCellRenderer() {}

	private static Icon createIcon( String fileName ) {
		ImageIcon icon = new ImageIcon(GridTreeCellRenderer.class.
				getResource("/resources/images/"+fileName));
		
		return icon;
	}


	@Override
	public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
													boolean leaf, int row, boolean hasFocus ) {

		super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );

		if ( value instanceof PeerNode ) {

			PeerNode node = (PeerNode) value;
			if ( node.isNotLogged() ) {
				setIcon( upIcon );
			} else if (node.isDown()) {
				setIcon( downIcon );
			} else if (node.isLogged()) {
				setIcon( loggedIcon );
			}
		}

		return this;
	}
}
