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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerComponentContextFactory;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.ui.async.client.PeerAsyncInitializer;
import org.ourgrid.peer.ui.async.model.PeerAsyncUIModel;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;

/**
 * It represents an Action to start the peer.
 */
public class StartPeerAction extends AbstractAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Component panel;

	private static final long START_PEER_VERIFICATION_DELAY = 1000;
	private static final long START_PEER_VERIFICATION_TIMES = 20;
	
	public static final String PEER_PROPERTIES_FILE = PeerConfiguration.PROPERTIES_FILENAME;
	
	/** Creates new form StartPeerAction */
	public StartPeerAction(Component contentPane) {
        super("Start peer");
        this.panel = contentPane;
    }
    
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
    public void actionPerformed(ActionEvent arg0) {
    	
    	this.setEnabled(false);
		
		this.panel.setCursor(new java.awt.Cursor(Cursor.WAIT_CURSOR));
		
		PeerComponentContextFactory contextFactory = new PeerComponentContextFactory(
				new PropertiesFileParser(PEER_PROPERTIES_FILE));
    	
		PeerAsyncUIModel model = null;
    	try {
    		ModuleContext context = contextFactory.createContext();
    		model = PeerAsyncInitializer.getInstance().getModel();
			new PeerComponent(context);
			model.setPeerStartOnRecovery(true);
			model.peerStarted();
//			PeerAsyncInitializer.getInstance().initComponentClient(context, model);
			waitForPeerToStart();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error on peer startup", JOptionPane.ERROR_MESSAGE);
			model.peerStopped();
		}
    }
    
	private void waitForPeerToStart() throws Exception {
		for (int i = 0; i < START_PEER_VERIFICATION_TIMES; i++) {
			if (PeerAsyncInitializer.getInstance().getComponentClient().isServerApplicationUp()) {
				return;
			}
			Thread.sleep(START_PEER_VERIFICATION_DELAY);
		}
		if (!PeerAsyncInitializer.getInstance().getComponentClient().isServerApplicationUp()) {
			throw new Exception("Peer component could not be started.");
		}
	}
}
