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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Level;
import org.ourgrid.common.logger.LogManager;
import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.InputFieldsUI;
import org.ourgrid.common.ui.OurGridUIModel;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.ui.async.gui.OurGridFileChooser;

/**
 * Panel that contain input fields for modifying the logging settings<p>
 * The fields are:
 * <ul>
 * <li> Enable logging
 * <li> Log filename
 * <li> Log level
 * <li> Log properties file
 * </ul>
 *
 */
public class LogSettingsPanel extends AbstractInputFieldsPanel implements InputFieldsUI {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private javax.swing.JComboBox logLevelComboBox1;
    private javax.swing.JLabel logFilenameLabel;
    private javax.swing.JTextField logFilenameTextField;
    private javax.swing.JLabel logLevelLabel;
    private javax.swing.JButton logFilenameButton;
    
    private LogManager logManager;
	
	/** 
	 * Creates new form LogSettingsPanel 
	 * @param model OurGrid UI model
	 * @param controller class that sends UI commands to the respective Component
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
    public LogSettingsPanel(OurGridUIModel model) throws FileNotFoundException, IOException {
        super(model);
        if (model != null) {
        	logManager = new LogManager();
        }
        initFields();
        //TODO - handle the logmanager exceptions in a better way
    }
    
    /*
     * (non-Javadoc)
     * @see org.ourgrid.common.ui.AbstractInputFieldsPanel#initComponents()
     */
    protected void initComponents() {

        logFilenameLabel = new javax.swing.JLabel();
        logLevelLabel = new javax.swing.JLabel();
        logFilenameTextField = new javax.swing.JTextField();
        logLevelComboBox1 = new javax.swing.JComboBox();
        logFilenameButton = new javax.swing.JButton();
        
        logLevelComboBox1.addItem(Level.OFF);
        logLevelComboBox1.addItem(Level.TRACE);
        logLevelComboBox1.addItem(Level.DEBUG);
        logLevelComboBox1.addItem(Level.INFO);
        logLevelComboBox1.addItem(Level.WARN);
        logLevelComboBox1.addItem(Level.ERROR);
        logLevelComboBox1.addItem(Level.FATAL);
        
        logLevelComboBox1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				logManager.setLevel((Level) logLevelComboBox1.getSelectedItem());
				
			}
        	
        });
        
        logFilenameLabel.setText("Log filename:");

        logLevelLabel.setText("Log level:");
        
        logFilenameButton.setText("...");

        logFilenameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFilenameChooser(e);
			}
        	
        });
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(logFilenameLabel)
                            .add(logLevelLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(logLevelComboBox1, 15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(logFilenameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(logFilenameButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(logFilenameLabel)
                    .add(logFilenameTextField)
                    .add(logFilenameButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE))

                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                		.add(logLevelLabel)
                		.add(logLevelComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))

                .addContainerGap(20, Short.MAX_VALUE))
        );
    }    
    
    
    private void openFilenameChooser(ActionEvent e) {
    	OurGridFileChooser fileChooser = new OurGridFileChooser("properties", "log");
    	File file = fileChooser.getFile();
    	if (file != null) {
    		logFilenameTextField.setText(file.toString());
    	}
    	//TODO		
	}
    
    /**
     * Disables all input fields
     */
    public void disableFieldEdition() {
		logFilenameTextField.setEnabled(false);
		logLevelComboBox1.setEnabled(false);
		
	}
    
    /**
     * Enables all input fields
     */
    public void enableFieldEdition() {
		logFilenameTextField.setEnabled(true);
		logLevelComboBox1.setEnabled(true);
		
	}
    
    /**
     * Initializes text fields with the default values
     */
    private void initTextFields() {
    	if (model != null) {
    		logFilenameTextField.setText(model.getProperty(PeerConfiguration.PROP_LOGFILE));
    	}
    	if (logManager != null) {
    		Level level = logManager.getLogLevel();
    		logLevelComboBox1.setSelectedItem(level);
    	}
	}
    
    /**
     * Saves the values at the input fields as the default values
     * @throws IOException 
     */
    public void saveProperties() throws IOException {
    	/*controller.setProperty(PeerConfiguration.PROP_LOGFILE, logFilenameTextField.getText());*/
    	
    	model.setProperty(PeerConfiguration.PROP_LOGFILE, logFilenameTextField.getText());
    	logManager.save();
    	logManager.setLogFile(new File(logFilenameTextField.getText()));
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
