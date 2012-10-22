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
package org.ourgrid.broker.ui.async.gui.graphical.job;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Description:
 * 
 * @version 1.0 Created on 26/08/2004
 */
public class JobsTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private final ImageIcon runningIcon;

	private final ImageIcon finishedIcon;

	private final ImageIcon abortedIcon;

	private final ImageIcon cancelledIcon;

	private final ImageIcon failedIcon;

	private final ImageIcon readyIcon;

	private final ImageIcon sabotagedIcon;
	
	public final int RUNNING = 0;

	public final int FINISHED = 1;

	public final int ABORTED = 2;

	public final int CANCELLED = 3;

	public final int FAILED = 4;

	public final int READY = 5;
	
	public final int SABOTAGED = 6;


	public JobsTreeCellRenderer() {
		this.runningIcon = createIcon( "running.gif" );
		this.finishedIcon = createIcon( "finished.gif" );
		this.abortedIcon = createIcon( "aborted.gif" );
		this.cancelledIcon = createIcon( "cancelled.gif" );
		this.failedIcon = createIcon( "error.gif" );
		this.readyIcon = createIcon( "ready.gif" );
//		this.sabotagedIcon = createIcon( "sabotaged.gif" );
		this.sabotagedIcon = createIcon( "error.gif" );
	}


	private ImageIcon createIcon( String fileName ) {
		//URL resource = ClassLoader.getSystemResource("resources/images/"+fileName);
//		ImageIcon icon = new ImageIcon( "resources/images/"+fileName); //resource );
		
		ImageIcon icon = new ImageIcon( this.getClass().
				getResource("/resources/images/"+fileName) ); //resource );
		
		return icon;
	}


	@Override
	public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
													boolean leaf, int row, boolean hasFocus ) {

		super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );

		switch ( getType( value ) ) {

			case RUNNING: {

				setIcon( runningIcon );
				setToolTipText( "Running" );
				break;
			}

			case FINISHED: {
				setIcon( finishedIcon );
				setToolTipText( "Finished" );
				break;
			}

			case ABORTED: {
				setIcon( abortedIcon );
				setToolTipText( "Aborted" );
				break;
			}

			case CANCELLED: {
				setIcon( cancelledIcon );
				setToolTipText( "Cancelled" );
				break;
			}

			case FAILED: {
				setIcon( failedIcon );
				setToolTipText( "Failed" );
				break;
			}

			case READY: {
				setIcon( readyIcon );
				setToolTipText( "Unstarted" );
				break;
			}
			
			case SABOTAGED: {
				setIcon( sabotagedIcon );
				setToolTipText( "Sabotaged" );
				break;
			}

		}

		setDoubleBuffered( true );
		return this;
	}


	protected int getType( Object value ) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

		String title = ((String) node.getUserObject()).toUpperCase();

		if ( title.indexOf( "RUNNING" ) >= 0 ) {
			return RUNNING;
		}

		if ( title.indexOf( "FINISHED" ) >= 0 ) {
			return FINISHED;
		}

		if ( title.indexOf( "ABORTED" ) >= 0 ) {
			return ABORTED;
		}

		if ( title.indexOf( "CANCEL" ) >= 0 ) {
			return CANCELLED;
		}

		if ( title.indexOf( "FAILED" ) >= 0 ) {
			return FAILED;
		}

		if ( title.indexOf( "UNSTARTED" ) >= 0 ) {
			return READY;
		}
		
		if ( title.indexOf( "SABOTAGED" ) >= 0 ) {
			return SABOTAGED;
		}
		

		return -1;

	}
}
