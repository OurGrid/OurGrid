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

import org.ourgrid.peer.ui.async.gui.AddXMPPUserDialog;

/**
 * It represents an Action to add a XMPP user.
 */
public class AddXMPPUserAction extends AbstractAction {

	private Frame frame;
	private JDialog addXMPPUserDialog;
	
	/** Creates new form AddXMPPUserAction */
    public AddXMPPUserAction(Frame frame) {
        super("Add XMPP user");
        this.frame = frame;
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
    	addXMPPUserDialog = new AddXMPPUserDialog(frame, true);
    	addXMPPUserDialog.setVisible(true);
    }

}
