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
package org.ourgrid.broker.ui.async.gui.graphical.workers;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Provides information to plot a tree cell.
 *   
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class WorkersTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private final ImageIcon runningIcon;
	
	/**
	 * 
	 */
	public final int RUNNING = 0;


	/**
	 * Default empty constructor.
	 */
	public WorkersTreeCellRenderer() {
		this.runningIcon = createIcon( "running.gif" );
	}


	/**
	 * @param fileName
	 * @return
	 */
	private ImageIcon createIcon( String fileName ) {
		ImageIcon icon = new ImageIcon( this.getClass().
				getResource("/resources/images/"+fileName) ); //resource );
		return icon;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
													boolean leaf, int row, boolean hasFocus ) {

		super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
		setIcon( runningIcon );
		setToolTipText( "Running" );
		setDoubleBuffered( true );
		return this;
	}


	/**
	 * @param value
	 * @return
	 */
	protected int getType( Object value ) {
		return RUNNING;
	}
}
