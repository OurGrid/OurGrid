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
package org.ourgrid.worker.ui.async.gui.actions;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerComponentContextFactory;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.ui.async.client.WorkerAsyncInitializer;
import org.ourgrid.worker.ui.async.model.WorkerAsyncUIModel;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;

public class StartWorkerAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Component panel;
	
	private static final long START_WORKER_VERIFICATION_DELAY = 1000;
	private static final long START_WORKER_VERIFICATION_TIMES = 20;

	private static final String WORKERGUI_PROPERTIES = WorkerConfiguration.PROPERTIES_FILENAME;
	
	public StartWorkerAction(Component contentPane) {
		super("Start");
		this.panel = contentPane;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		this.setEnabled(false);
		
		this.panel.setCursor(new java.awt.Cursor(Cursor.WAIT_CURSOR));
		
		WorkerComponentContextFactory contextFactory = new WorkerComponentContextFactory(
				new PropertiesFileParser(WORKERGUI_PROPERTIES));
    	
		WorkerAsyncUIModel model = null;
    	try {
    		ModuleContext context = contextFactory.createContext();
    		model = WorkerAsyncInitializer.getInstance().getModel();
			new WorkerComponent(context);
			model.setWorkerStartOnRecovery(true);
			model.workerStarted();
//			WorkerAsyncInitializer.getInstance().initComponentClient(context, model);
			waitForWorkerToStart();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error on worker startup", JOptionPane.ERROR_MESSAGE);
			model.workerStopped();
		}
	}

	private void waitForWorkerToStart() throws Exception {
		for (int i = 0; i < START_WORKER_VERIFICATION_TIMES; i++) {
			if (WorkerAsyncInitializer.getInstance().getComponentClient().isServerApplicationUp()) {
				return;
			}
			Thread.sleep(START_WORKER_VERIFICATION_DELAY);
		}
		if (!WorkerAsyncInitializer.getInstance().getComponentClient().isServerApplicationUp()) {
			throw new Exception("Worker component could not be started.");
		}
	}
}
