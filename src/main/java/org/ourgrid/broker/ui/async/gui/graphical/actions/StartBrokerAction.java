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
package org.ourgrid.broker.ui.async.gui.graphical.actions;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.BrokerComponentContextFactory;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.ui.async.client.BrokerAsyncInitializer;
import org.ourgrid.broker.ui.async.model.BrokerAsyncUIModel;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;

public class StartBrokerAction extends AbstractBrokerAction {

	private static final long serialVersionUID = 1L;
	private static final String ACTION_NAME = "Start";
	private Component panel;
	
	private static final long START_BROKER_VERIFICATION_DELAY = 1000;
	private static final long START_BROKER_VERIFICATION_TIMES = 20;
	
	public StartBrokerAction( Component contentPane ) {
		this (ACTION_NAME, contentPane);
	}


	public StartBrokerAction( String title, Component contentPane ) {
		super(title);
		this.panel = contentPane;
	}


	public void actionPerformed( ActionEvent event ) {
			
		this.setEnabled(false);
		this.panel.setCursor(new java.awt.Cursor(Cursor.WAIT_CURSOR));
		
		BrokerComponentContextFactory contextFactory = new BrokerComponentContextFactory(
				new PropertiesFileParser(BrokerConfiguration.PROPERTIES_FILENAME));
    	
		BrokerAsyncUIModel model = null;
		ModuleContext context = null;
    	try {
    		
    		context = contextFactory.createContext();
    		model = BrokerAsyncInitializer.getInstance().getModel();
			new BrokerServerModule(context);
			model.setBrokerStartOnRecovery(true);
			model.brokerStarted();
			this.panel.setCursor(new java.awt.Cursor(Cursor.WAIT_CURSOR));
//			BrokerAsyncInitializer.getInstance().initComponentClient(context, model);
			waitForBrokerToStart();
		
    	} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error on broker startup", JOptionPane.ERROR_MESSAGE);
			model.brokerStopped();
		}
		
		this.panel.setCursor(new java.awt.Cursor(Cursor.DEFAULT_CURSOR));
	}

	private void waitForBrokerToStart() throws Exception {
		for (int i = 0; i < START_BROKER_VERIFICATION_TIMES; i++) {
			if (BrokerAsyncInitializer.getInstance().getComponentClient().isServerApplicationUp()) {
				return;
			}
			Thread.sleep(START_BROKER_VERIFICATION_DELAY);
		}
		if (!BrokerAsyncInitializer.getInstance().getComponentClient().isServerApplicationUp()) {
			throw new Exception("Broker component could not be started.");
		}
	}
}
