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

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.Serializable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.ourgrid.common.interfaces.to.PeersPackage;


/**
 * This class generates a panel with connected Peers.
 */
public class GridPanel extends JPanel implements Serializable {

	private static final long serialVersionUID = 1L;

	/** JTree */
	private JTree tree;

	/** Tree's Scroll */
	private JScrollPane scroll;

	/** DefaultTreeModel */
	private GridTreeModel gtm;
	
	
	/**
	 * Creates a new <code>GridPanel</code>
	 */
	public GridPanel() {

		setLayout( new BorderLayout() );
		setDoubleBuffered( true );

		gtm = new GridTreeModel();

		// Tree
		configureTree( gtm );

		// Scroll
		configureScroll();
	}


	private void configureScroll() {

		scroll = new JScrollPane( tree );
		scroll.setViewportView( tree );
		this.add( scroll, BorderLayout.CENTER );
	}


	private void configureTree( DefaultTreeModel treeModel ) {

		tree = new JTree( treeModel );
		tree.setLargeModel( true );
		tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		tree.setCellRenderer( new GridTreeCellRenderer() );
		tree.setDoubleBuffered( true );
		tree.setOpaque( true );
		tree.setBorder( new LineBorder( Color.BLACK ) );
		
	}


	public void refreshTree() {
		//TODO
		//gtm.refreshTree(entries);
	}


	/**
	 * Clean the table
	 */
	public void clean() {

		tree.repaint();
		scroll.repaint();
	}


	public synchronized void updateStatus(PeersPackage peersPackage) {
		gtm.refreshTree(peersPackage.getPeers());
	}
}
