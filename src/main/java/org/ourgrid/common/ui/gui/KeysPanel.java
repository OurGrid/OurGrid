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
import java.security.KeyPair;
import java.util.Properties;

import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.InputFieldsUI;
import org.ourgrid.common.ui.OurGridUIModel;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.ui.async.gui.OurGridFileChooser;

import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.signature.Util;

/**
 * Panel with input fields for setting a private and a public key.<p>
 * Provides automatic keys generation button and also a button to load the
 * keys from a file
 *
 */
public class KeysPanel extends AbstractInputFieldsPanel implements InputFieldsUI {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private javax.swing.JButton generateButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton loadButton;
    private javax.swing.JLabel privateKeyLabel;
    private javax.swing.JTextArea privateKeyTextArea;
    private javax.swing.JLabel publicKeyLabel;
    private javax.swing.JTextArea publicKeyTextArea;
	
	/** 
	 * Creates new KeysPanel 
	 * @param model OurGrid UI model
	 * @param controller class that sends UI commands to the respective Component 
	 */
    public KeysPanel(OurGridUIModel model) {
    	super(model);
    }
    
    /*
     * (non-Javadoc)
     * @see org.ourgrid.common.ui.AbstractInputFieldsPanel#initComponents()
     */
    protected void initComponents() {

        publicKeyLabel = new javax.swing.JLabel();
        privateKeyLabel = new javax.swing.JLabel();
        generateButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        publicKeyTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        privateKeyTextArea = new javax.swing.JTextArea();

        privateKeyTextArea.setLineWrap(true);
        
        publicKeyTextArea.setLineWrap(true);
        
        publicKeyLabel.setText("Public key");

        privateKeyLabel.setText("Private key");

        generateButton.setText("Generate");

        generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateKeyPairButtonActionPerformed(e);
			}
        });
        
        loadButton.setText("Load...");
        loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadKeyPairButtonActionPerformed(e);
			}
        });
        
        publicKeyTextArea.setColumns(20);
        publicKeyTextArea.setRows(5);
        jScrollPane1.setViewportView(publicKeyTextArea);

        privateKeyTextArea.setColumns(20);
        privateKeyTextArea.setRows(5);
        jScrollPane2.setViewportView(privateKeyTextArea);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 388, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 388, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, publicKeyLabel)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, privateKeyLabel)
                    .add(layout.createSequentialGroup()
                        .add(loadButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(generateButton)
                        .add(6, 6, 6)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(publicKeyLabel)
                .add(8, 8, 8)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(privateKeyLabel)
                .add(8, 8, 8)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(9, 9, 9)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(loadButton)
                    .add(generateButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void generateKeyPairButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	KeyPair keyPair = Util.generateKeyPair(); 
        
        String privateKey = Util.encodeArrayToBase64String(keyPair.getPrivate().getEncoded());
        String publicKey = Util.encodeArrayToBase64String(keyPair.getPublic().getEncoded());
    	
        privateKeyTextArea.setText(privateKey);
        publicKeyTextArea.setText(publicKey);
    }

    private void loadKeyPairButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	
    	OurGridFileChooser fileChooser = new OurGridFileChooser("Properties file", "properties");
    	File propertiesFile = fileChooser.getFile();
    	
    	if (propertiesFile != null) {
    		Properties prop = CommonUtils.loadProperties(propertiesFile);
    		
    		privateKeyTextArea.setText(prop.getProperty(SignatureProperties.PROP_PRIVATE_KEY));
    		publicKeyTextArea.setText(prop.getProperty(SignatureProperties.PROP_PUBLIC_KEY));
    		
    	}
    	
    }

    /**
     * Disables all input fields edition
     */
    public void disableFieldEdition() {
		generateButton.setEnabled(false);
		loadButton.setEnabled(false);
		privateKeyTextArea.setEnabled(false);
		publicKeyTextArea.setEnabled(false);
	}
    
    /**
     * Enables all input fields edition
     */
    public void enableFieldEdition() {
		generateButton.setEnabled(true);
		loadButton.setEnabled(true);
		privateKeyTextArea.setEnabled(true);
		publicKeyTextArea.setEnabled(true);
	}
    
    /**
     * Initializes the input fields with the default values
     */
    private void initTextFields() {
    	if (model != null) {
    		privateKeyTextArea.setText(model.getProperty(SignatureProperties.PROP_PRIVATE_KEY));
    		publicKeyTextArea.setText(model.getProperty(SignatureProperties.PROP_PUBLIC_KEY));
    	}
	}
    
    /**
     * Saves the properties at the input values as default 
     */
    public void saveProperties() {
    	/*controller.setProperty(SignatureProperties.PROP_PRIVATE_KEY, privateKeyTextArea.getText());
    	controller.setProperty(SignatureProperties.PROP_PUBLIC_KEY, publicKeyTextArea.getText());*/
    	
    	model.setProperty(SignatureProperties.PROP_PRIVATE_KEY, privateKeyTextArea.getText());
    	model.setProperty(SignatureProperties.PROP_PUBLIC_KEY, publicKeyTextArea.getText());
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
