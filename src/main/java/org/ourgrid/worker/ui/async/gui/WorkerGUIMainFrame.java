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
package org.ourgrid.worker.ui.async.gui;

import java.awt.Cursor;
import java.awt.image.BufferedImage;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.ui.gui.AboutPanel;
import org.ourgrid.common.ui.gui.XMPPConfigurationPanel;
import org.ourgrid.worker.ui.async.model.WorkerAsyncUIListener;
import org.ourgrid.worker.ui.async.model.WorkerAsyncUIModel;

/**
 * It represents the is top-level window that adds support for the
 * worker GUI components.
 */
public class WorkerGUIMainFrame extends javax.swing.JFrame implements WorkerAsyncUIListener {
    
	private static final long serialVersionUID = 1L;
	private XMPPConfigurationPanel xmppConfigurationPanel;
	private WorkerConfigurationPanel workerConfigurationPanel;
	private WorkerStatusPanel workerStatusPanel;
	private AboutPanel aboutPanel;
	private WorkerAsyncUIModel model;
	
	
	/**
	 * Creates new form WorkerGuiMainFrame 
	 * @param iconImage The image to be displayed in the minimized icon for this frame.
	 * @param model The model of the worker.
	 * @param componentClient The worker component client.
	 */
    public WorkerGUIMainFrame(BufferedImage iconImage, WorkerAsyncUIModel model) {
    	super("OurGrid Worker " + Configuration.VERSION);
    	this.setIconImage(iconImage);
    	this.model = model;
        initComponents();
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        actionsPanel = new ActionsPanel(this);
        
        xmppConfigurationPanel = new XMPPConfigurationPanel(model);
        tabs.addTab("XMPP Configuration", xmppConfigurationPanel);
        workerConfigurationPanel = new WorkerConfigurationPanel(model);
        tabs.addTab("Worker Configuration", workerConfigurationPanel);
        workerStatusPanel = new WorkerStatusPanel(model);
        tabs.addTab("Status", workerStatusPanel);
        aboutPanel = new AboutPanel();
        tabs.addTab("About", aboutPanel);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(actionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(actionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 311, Short.MAX_VALUE)
            .add(tabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ActionsPanel actionsPanel;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables

    /**
     * Define the status of this Frame when the worker
     * is started.
     */
	public void workerStarted() {
		workerConfigurationPanel.disableInput();
		
    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.disableEditConf();
    	
    	actionsPanel.workerStarted();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

    /**
     * Define the status of this Frame when the worker
     * is sttoped.
     */
	public void workerStopped() {
		workerConfigurationPanel.enableInput();
		
    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.setXMPPUpInfo();
    	xmppConfigurationPanel.enableEditConf();
    	
    	actionsPanel.workerStopped();
    	
    	workerStatusPanel.clearFields();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void workerRestarted() {
    	actionsPanel.workerStarted();
		workerConfigurationPanel.disableInput();
		xmppConfigurationPanel.setXMPPUpInfo();
    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.disableEditConf();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void workerInited() {
    	actionsPanel.workerInited();

		workerConfigurationPanel.enableInput();
    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.setXMPPContactingInfo();
    	xmppConfigurationPanel.enableEditConf();
    	
    	workerStatusPanel.clearFields();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
	
	public void workerEditing() {
    	actionsPanel.workerInited();

		workerConfigurationPanel.enableInput();
		
    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.setXMPPDownInfo();
    	xmppConfigurationPanel.disableEditConf();
    	
    	workerStatusPanel.clearFields();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
	
    /**
     * Define the status of this Frame when the worker
     * is not inited.
     */
    public void workerInitedFailed() {
    	actionsPanel.workerInited();

    	xmppConfigurationPanel.enableFieldEdition();
    	xmppConfigurationPanel.setXMPPDownInfo();
    	workerConfigurationPanel.enableInput();
    	
    	workerStatusPanel.clearFields();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    	
    }

	public void updateCompleteStatus(WorkerCompleteStatus completeStatus) {
		workerStatusPanel.updateCompleteStatus(completeStatus);
		
	}

	public void workerPaused() {
		actionsPanel.workerPaused();
	}

	public void workerResumed() {
		actionsPanel.workerResumed();		
	}
	
	public void setStartEnabled(boolean enabled) {
		actionsPanel.setStartEnabled(enabled);
	}



	
}
