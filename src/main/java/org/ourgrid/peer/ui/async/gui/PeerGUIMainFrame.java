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
package org.ourgrid.peer.ui.async.gui;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.util.List;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.ui.gui.AboutPanel;
import org.ourgrid.common.ui.gui.XMPPConfigurationPanel;
import org.ourgrid.peer.status.PeerCompleteStatus;
import org.ourgrid.peer.ui.async.model.PeerAsyncUIListener;
import org.ourgrid.peer.ui.async.model.PeerAsyncUIModel;

/**
 * It represents the is top-level window that adds support for the
 * peer GUI components.
 */
public class PeerGUIMainFrame extends javax.swing.JFrame implements PeerAsyncUIListener {
    
	private static final long serialVersionUID = 1L;
	private PeerAsyncUIModel model;
	private CommunityPanel communityPanel;
	

	
	/** Creates new form PeerGuiMainFrame 
	 * @param iconImage The image to be displayed in the minimized icon for this frame.
	 * @param model The model of the peer.
	 * @param componentClient The peer component client.
	 */
    public PeerGUIMainFrame(BufferedImage iconImage, PeerAsyncUIModel model) {
    	super("OurGrid Peer " + Configuration.VERSION);
    	this.setIconImage(iconImage);
    	this.model = model;
        initComponents();
    }
    
    /**
     * Define the status of this Frame when the peer
     * is started.
     */
    public void peerStarted() {
    	actionsPanel.peerStarted();
    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.disableEditConf();
    	peerConfigurationPanel.disableFieldEdition();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    public void peerRestarted() {
    	actionsPanel.peerStarted();
    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.setXMPPUpInfo();
    	xmppConfigurationPanel.disableEditConf();
    	peerConfigurationPanel.disableFieldEdition();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Define the status of this Frame when the peer
     * is stopped.
     */
    public void peerStopped() {
    	actionsPanel.peerStopped();

    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.setXMPPUpInfo();
    	xmppConfigurationPanel.enableEditConf();
    	peerConfigurationPanel.enableFieldEdition();
    	workersPanel.peerStopped();
    	brokerTablePanel.peerStopped();
    	communityPanel.peerStopped();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Define the status of this Frame when the peer
     * is inited.
     */
    public void peerInited() {
    	actionsPanel.peerInited();

    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.setXMPPContactingInfo();
    	xmppConfigurationPanel.enableEditConf();
    	peerConfigurationPanel.enableFieldEdition();
    	workersPanel.peerStopped();
    	brokerTablePanel.peerStopped();
    	communityPanel.peerStopped();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void peerEditing() {
    	actionsPanel.peerInited();

    	xmppConfigurationPanel.disableFieldEdition();
    	xmppConfigurationPanel.setXMPPDownInfo();
    	xmppConfigurationPanel.disableEditConf();
    	peerConfigurationPanel.enableFieldEdition();
    	workersPanel.peerStopped();
    	brokerTablePanel.peerStopped();
    	communityPanel.peerStopped();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    	
    }

    /**
     * Define the status of this Frame when the peer
     * is not inited.
     */
    public void peerInitedFailed() {
    	actionsPanel.peerInited();

    	xmppConfigurationPanel.enableFieldEdition();
    	xmppConfigurationPanel.setXMPPDownInfo();
    	peerConfigurationPanel.enableFieldEdition();
    	workersPanel.peerStopped();
    	brokerTablePanel.peerStopped();
    	communityPanel.peerStopped();
    	
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    	
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     * @param model 
     * @param componentClient 
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        brokerTablePanel = new org.ourgrid.peer.ui.async.gui.BrokerTablePanel();
        workersPanel = new WorkersPanel();
        actionsPanel = new org.ourgrid.peer.ui.async.gui.ActionsPanel(this);
        aboutPanel = new org.ourgrid.common.ui.gui.AboutPanel();
        communityPanel = new CommunityPanel();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        xmppConfigurationPanel = new XMPPConfigurationPanel(model);
        tabs.addTab("XMPP Configuration", xmppConfigurationPanel);
        peerConfigurationPanel = new PeerConfigurationPanel(model);
        tabs.addTab("Peer Configuration", peerConfigurationPanel);
        tabs.addTab("Brokers", brokerTablePanel);
        tabs.addTab("Workers", workersPanel);
        tabs.addTab("Community", communityPanel);
        tabs.addTab("About", aboutPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(actionsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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
    private org.ourgrid.peer.ui.async.gui.ActionsPanel actionsPanel;
    private org.ourgrid.peer.ui.async.gui.BrokerTablePanel brokerTablePanel;
    private javax.swing.JTabbedPane tabs;
    private WorkersPanel workersPanel;
    private AboutPanel aboutPanel;
    // End of variables declaration//GEN-END:variables
    
    private XMPPConfigurationPanel xmppConfigurationPanel;
    private PeerConfigurationPanel peerConfigurationPanel;

    /**
     * Updates the complete status of the peer.
     * @param completeStatus The status of the peer.
     */
	public void updateCompleteStatus(PeerCompleteStatus completeStatus) {
		brokerTablePanel.setTableModelData(completeStatus.getUsersInfo());
		workersPanel.setTableModelData(completeStatus.getLocalWorkersInfo());
		communityPanel.setTableModelData(completeStatus.getCommunityInfo());
	}

	/**
     * Updates the peer users status.
     * @param usersInfo The informations about the peer users.
     */
	public void updateUsersStatus(List<UserInfo> usersInfo) {
	}

	/**
     * Updates the workers status.
     * @param localWorkers The informations about the local workers.
     */
	public void updateWorkersStatus(List<WorkerInfo> localWorkers) {
	}
	
	public void setStartEnabled(boolean enabled) {
		actionsPanel.setStartEnabled(enabled);
	}



}
