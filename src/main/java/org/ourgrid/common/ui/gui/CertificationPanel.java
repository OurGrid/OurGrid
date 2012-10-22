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
import java.io.IOException;

import javax.swing.JTextField;

import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.InputFieldsUI;
import org.ourgrid.common.ui.OurGridUIModel;
import org.ourgrid.peer.ui.async.gui.OurGridFileChooser;

import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;

/**
 * Panel with input fields for setting a certificate path file.
 * Provides a button to load the certificate from a file
 *
 */
public class CertificationPanel extends AbstractInputFieldsPanel implements InputFieldsUI {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private javax.swing.JButton loadButton;
    private javax.swing.JLabel certificateLabel;
    private JTextField certificateTextField;
	
	/** 
	 * Creates new KeysPanel 
	 * @param model OurGrid UI model
	 * @param controller class that sends UI commands to the respective Component 
	 */
    public CertificationPanel(OurGridUIModel model) {
    	super(model);
    }
    
    /*
     * (non-Javadoc)
     * @see org.ourgrid.common.ui.AbstractInputFieldsPanel#initComponents()
     */
    protected void initComponents() {

        certificateLabel = new javax.swing.JLabel();
        loadButton = new javax.swing.JButton();
        certificateTextField = new javax.swing.JTextField();
        
        certificateLabel.setText("My certificate file:");

        loadButton.setText("Load...");
        loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCertificateButtonActionPerformed(e);
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
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        	.add(org.jdesktop.layout.GroupLayout.LEADING, certificateLabel)
                        	.add(certificateTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 
                        			org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                			.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                			    .add(loadButton))
                .addContainerGap())
        )));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(certificateLabel)
                .add(8,8,8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                		.add(certificateTextField)
                		.add(8,8,8)
                		.add(loadButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void loadCertificateButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	
    	OurGridFileChooser fileChooser = new OurGridFileChooser("Certificate file", "cer");
    	File propertiesFile = fileChooser.getFile();
    	
    	if (propertiesFile != null) {
    		certificateTextField.setText(propertiesFile.getPath());
    	}
    }

    /**
     * Disables all input fields edition
     */
    public void disableFieldEdition() {
		loadButton.setEnabled(false);
		certificateTextField.setEnabled(false);
	}
    
    /**
     * Enables all input fields edition
     */
    public void enableFieldEdition() {
		loadButton.setEnabled(true);
		certificateTextField.setEnabled(true);
	}
    
    /**
     * Initializes the input fields with the default values
     */
    private void initTextFields() {
    	if (model != null) {
    		certificateTextField.setText(model.getProperty(
    				FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH));
    	}
	}
    
    /**
     * Saves the properties at the input values as default 
     */
    public void saveProperties() {
    	/*controller.setProperty(FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH, 
    			certificateTextField.getText());*/
    	
    	model.setProperty(FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH, 
    			certificateTextField.getText());
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
