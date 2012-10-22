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
package org.ourgrid.common.ui.gui;

import java.io.IOException;

import javax.swing.SpinnerNumberModel;

import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.InputFieldsUI;
import org.ourgrid.common.ui.OurGridUIModel;

import br.edu.ufcg.lsd.commune.processor.interest.InterestProperties;

/**
 * Panel that contain input fields to control the
 * heartbeat and detection time settings for LAN, WAN and localhost components.
 *
 */
public class CommuneDetectionDelaySettingsPanel extends AbstractInputFieldsPanel implements InputFieldsUI {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_LOCALHOST_DETECTION_TIME = 10;
	private static final int DEFAULT_LOCALHOST_HEARTBEAT = 2;
	private static final int DEFAULT_LAN_DETECTION_TIME = 30;
	private static final int DEFAULT_LAN_HEARTBEAT = 5;
	private static final int DEFAULT_WAN_DETECTION_TIME = 45;
	private static final int DEFAULT_WAN_HEARTBEAT = 9;

    private javax.swing.JLabel lanDetectionTimeLabel;
    private javax.swing.JSpinner lanDetectionTimeSpinner;
    private javax.swing.JLabel lanHeartbeatLabel;
    private javax.swing.JSpinner lanHeartbeatSpinner;
    private javax.swing.JPanel lanPanel;
    private javax.swing.JLabel localhostDetectionTimeLabel;
    private javax.swing.JSpinner localhostDetectionTimeSpinner;
    private javax.swing.JLabel localhostHeartbeatLabel;
    private javax.swing.JSpinner localhostHeartbeatSpinner;
    private javax.swing.JPanel localhostPanel;
    private javax.swing.JLabel wanDetectionTimeLabel;
    private javax.swing.JSpinner wanDetectionTimeSpinner;
    private javax.swing.JLabel wanHeartbeatLabel;
    private javax.swing.JSpinner wanHeartbeatSpinner;
    private javax.swing.JPanel wanPanel;
	
	/** Creates new form AdvancedSettings */
    public CommuneDetectionDelaySettingsPanel(OurGridUIModel model) {
    	super(model);
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the GUI components.
     */
    protected void initComponents() {
    	
        localhostPanel = new javax.swing.JPanel();
        localhostDetectionTimeLabel = new javax.swing.JLabel();
        localhostDetectionTimeSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(DEFAULT_LOCALHOST_DETECTION_TIME, 1, null, 1));
        localhostHeartbeatLabel = new javax.swing.JLabel();
        localhostHeartbeatSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(DEFAULT_LOCALHOST_HEARTBEAT, 1, null, 1));
        lanPanel = new javax.swing.JPanel();
        lanDetectionTimeLabel = new javax.swing.JLabel();
        lanDetectionTimeSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(DEFAULT_LAN_DETECTION_TIME, 1, null, 1));
        lanHeartbeatLabel = new javax.swing.JLabel();
        lanHeartbeatSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(DEFAULT_LAN_HEARTBEAT, 1, null, 1));
        wanPanel = new javax.swing.JPanel();
        wanDetectionTimeLabel = new javax.swing.JLabel();
        wanDetectionTimeSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(DEFAULT_WAN_DETECTION_TIME, 1, null, 1));
        wanHeartbeatLabel = new javax.swing.JLabel();
        wanHeartbeatSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(DEFAULT_WAN_HEARTBEAT, 1, null, 1));

        localhostPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Failure detector values for local host (sec)"));

        localhostDetectionTimeLabel.setText("Detection time:");

        localhostHeartbeatLabel.setText("Heartbeat:");

        org.jdesktop.layout.GroupLayout localhostPanelLayout = new org.jdesktop.layout.GroupLayout(localhostPanel);
        localhostPanel.setLayout(localhostPanelLayout);
        localhostPanelLayout.setHorizontalGroup(
            localhostPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(localhostPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(localhostDetectionTimeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(localhostDetectionTimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(localhostHeartbeatLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(localhostHeartbeatSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addContainerGap())
        );
        localhostPanelLayout.setVerticalGroup(
            localhostPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(localhostPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(localhostPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(localhostDetectionTimeLabel)
                    .add(localhostDetectionTimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(localhostHeartbeatLabel)
                    .add(localhostHeartbeatSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lanPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Failure detector values for LAN (sec)"));

        lanDetectionTimeLabel.setText("Detection time:");

        lanHeartbeatLabel.setText("Heartbeat:");

        org.jdesktop.layout.GroupLayout lanPanelLayout = new org.jdesktop.layout.GroupLayout(lanPanel);
        lanPanel.setLayout(lanPanelLayout);
        lanPanelLayout.setHorizontalGroup(
            lanPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lanDetectionTimeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lanDetectionTimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(lanHeartbeatLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lanHeartbeatSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                .addContainerGap())
        );
        lanPanelLayout.setVerticalGroup(
            lanPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lanPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lanDetectionTimeLabel)
                    .add(lanDetectionTimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lanHeartbeatLabel)
                    .add(lanHeartbeatSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        wanPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Failure detector values for WAN (sec)"));

        wanDetectionTimeLabel.setText("Detection time:");

        wanHeartbeatLabel.setText("Heartbeat:");

        org.jdesktop.layout.GroupLayout wanPanelLayout = new org.jdesktop.layout.GroupLayout(wanPanel);
        wanPanel.setLayout(wanPanelLayout);
        wanPanelLayout.setHorizontalGroup(
            wanPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(wanDetectionTimeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wanDetectionTimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(wanHeartbeatLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wanHeartbeatSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addContainerGap())
        );
        wanPanelLayout.setVerticalGroup(
            wanPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(wanPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(wanDetectionTimeLabel)
                    .add(wanDetectionTimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(wanHeartbeatLabel)
                    .add(wanHeartbeatSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, wanPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lanPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, localhostPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(localhostPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lanPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wanPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
    }
    
    /**
     * Disables input fields edition.
     */
    public void disableFieldEdition() {
		disableLanFields();
		disableWanFields();
		disableLocalHostFields();
	}
    
    /**
     * Enables input fields edition.
     */
    public void enableFieldEdition() {
		enableLanFields();
		enableWanFields();
		enableLocalHostFields();
	}

    /**
	 * Disables input on the localhost settings fields.
	 */
    private void disableLocalHostFields() {
    	localhostHeartbeatSpinner.setEnabled(false);
    	localhostDetectionTimeSpinner.setEnabled(false);
	}
	
    /**
	 * Enables input on the localhost settings fields.
	 */
	private void enableLocalHostFields() {
		localhostHeartbeatSpinner.setEnabled(true);
		localhostDetectionTimeSpinner.setEnabled(true);
	}
    
	/**
	 * Disables input on the WAN settings fields.
	 */
    private void disableWanFields() {
		wanHeartbeatSpinner.setEnabled(false);
		wanDetectionTimeSpinner.setEnabled(false);
	}
	
    /**
	 * Enables input on the WAN settings fields.
	 */
	private void enableWanFields() {
		wanHeartbeatSpinner.setEnabled(true);
		wanDetectionTimeSpinner.setEnabled(true);
	}

	/**
	 * Disables input on the LAN settings fields.
	 */
	private void disableLanFields() {
		lanHeartbeatSpinner.setEnabled(false);
		lanDetectionTimeSpinner.setEnabled(false);
	}
	
	/**
	 * Enables input on the LAN settings fields.
	 */
	private void enableLanFields() {
		lanHeartbeatSpinner.setEnabled(true);
		lanDetectionTimeSpinner.setEnabled(true);
	}
	
	/**
	 * Loads the default values to the input fields.
	 */
    private void initTextFields() {
    	if (model != null) {
    		
    		String localHeartBeatDelay = model.getProperty(InterestProperties.PROP_LOCAL_HEARTBEAT_DELAY);
			if (localHeartBeatDelay != null) {
	    		localhostHeartbeatSpinner.setValue(Integer.parseInt(
	    				localHeartBeatDelay));
    		}
			
    		String localDetectionDelay = model.getProperty(InterestProperties.PROP_LOCAL_DETECTION_TIME);
			if (localDetectionDelay != null) {
	    		localhostDetectionTimeSpinner.setValue(Integer.parseInt(
	    				localDetectionDelay));
    		}
			
    		String wanHeartBeatDelay = model.getProperty(InterestProperties.PROP_WAN_HEARTBEAT_DELAY);
			if (wanHeartBeatDelay  != null) {
	    		wanHeartbeatSpinner.setValue(Integer.parseInt(
	    				wanHeartBeatDelay));
    		}
			
    		String wanDetectionTime = model.getProperty(InterestProperties.PROP_WAN_DETECTION_TIME);
			if (wanDetectionTime != null) {
	    		wanDetectionTimeSpinner.setValue(Integer.parseInt(
	    				wanDetectionTime));
    		}
			
    		String lanHeartBeatDelay = model.getProperty(InterestProperties.PROP_LAN_HEARTBEAT_DELAY);
			if (lanHeartBeatDelay != null) {
	    		lanHeartbeatSpinner.setValue(Integer.parseInt(
	    				lanHeartBeatDelay));
    		}
			
    		String lanDetectionTime = model.getProperty(InterestProperties.PROP_LAN_DETECTION_TIME);
			if (lanDetectionTime != null) {
	    		lanDetectionTimeSpinner.setValue(Integer.parseInt(
	    				lanDetectionTime));
    		}
    	}
	}
    
    /**
     * Save properties (inputs on the fields) as default values.
     */
    public void saveProperties() {
    	/*controller.setProperty(InterestProperties.PROP_LOCAL_HEARTBEAT_DELAY, String.valueOf(
    			localhostHeartbeatSpinner.getValue()));
    	controller.setProperty(InterestProperties.PROP_LOCAL_DETECTION_TIME, String.valueOf(
    			localhostDetectionTimeSpinner.getValue()));
    	controller.setProperty(InterestProperties.PROP_WAN_HEARTBEAT_DELAY, String.valueOf(
    			wanHeartbeatSpinner.getValue()));
    	controller.setProperty(InterestProperties.PROP_WAN_DETECTION_TIME, String.valueOf(
    			wanDetectionTimeSpinner.getValue()));
    	controller.setProperty(InterestProperties.PROP_LAN_HEARTBEAT_DELAY, String.valueOf(
    			lanHeartbeatSpinner.getValue()));
    	controller.setProperty(InterestProperties.PROP_LAN_DETECTION_TIME, String.valueOf(
    			lanDetectionTimeSpinner.getValue()));*/
    	
    	model.setProperty(InterestProperties.PROP_LOCAL_HEARTBEAT_DELAY, String.valueOf(
    			localhostHeartbeatSpinner.getValue()));
    	model.setProperty(InterestProperties.PROP_LOCAL_DETECTION_TIME, String.valueOf(
    			localhostDetectionTimeSpinner.getValue()));
    	model.setProperty(InterestProperties.PROP_WAN_HEARTBEAT_DELAY, String.valueOf(
    			wanHeartbeatSpinner.getValue()));
    	model.setProperty(InterestProperties.PROP_WAN_DETECTION_TIME, String.valueOf(
    			wanDetectionTimeSpinner.getValue()));
    	model.setProperty(InterestProperties.PROP_LAN_HEARTBEAT_DELAY, String.valueOf(
    			lanHeartbeatSpinner.getValue()));
    	model.setProperty(InterestProperties.PROP_LAN_DETECTION_TIME, String.valueOf(
    			lanDetectionTimeSpinner.getValue()));
	}

    /*
     * (non-Javadoc)
     * @see org.ourgrid.common.ui.InputFieldsUI#initFields()
     */
	public void initFields() throws IOException {
		initTextFields();
	}

	/*
	 * (non-Javadoc)
	 * @see org.ourgrid.common.ui.InputFieldsUI#saveFieldInputs()
	 */
	public void saveFieldInputs() throws IOException {
		saveProperties();
	}

	/*
	 * (non-Javadoc)
	 * @see org.ourgrid.common.ui.InputFieldsUI#disableInput()
	 */
	public void disableInput() {
		disableFieldEdition();
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.ourgrid.common.ui.InputFieldsUI#enableInput()
	 */
	public void enableInput() {
		enableFieldEdition();
		
	}
	
}
