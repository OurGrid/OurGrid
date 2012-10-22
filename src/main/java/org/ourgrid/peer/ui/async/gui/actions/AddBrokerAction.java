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
package org.ourgrid.peer.ui.async.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import org.ourgrid.peer.ui.async.gui.AddUserDialog;

/**
 * It represents an Action to add a new peer user.
 */
public class AddBrokerAction extends AbstractAction {

    private Frame frame;
    private JDialog addUserDialog;

	/** Creates new form AddPeerUserAction */
	public AddBrokerAction(Frame frame) {
        super("Add broker");
        this.frame = frame;
    }
    
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
    public void actionPerformed(ActionEvent arg0) {
    	addUserDialog = new AddUserDialog(frame, true);
    	addUserDialog.setVisible(true);
    }

}
