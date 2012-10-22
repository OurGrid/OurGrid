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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.InputFieldsUI;
import org.ourgrid.common.ui.OurGridUIModel;
import org.ourgrid.peer.ui.async.model.PeerAsyncUIModel;

import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;

/**
 * Panel that contains fields for inputting the XMPP settings<p>
 * 
 * The fields are:
 * <ul> 
 * <li> User name
 * <li> User password
 * <li> Server name
 * <li> Server port
 * <li> Secure port
 * </ul>
 * 
 * Also provides a button to test the connection.
 *
 */
public class XMPPConfigurationPanel extends AbstractInputFieldsPanel implements InputFieldsUI {
    
	
	private static final ImageIcon UPICON = new ImageIcon(PeerAsyncUIModel.XMPP_ONLINE_IMAGE_PATH, "UP");
	
	private static final ImageIcon DOWNICON = new ImageIcon(PeerAsyncUIModel.XMPP_OFFLINE_IMAGE_PATH, "DOWN");
	
	private static final ImageIcon CONTACTINGICON = new ImageIcon(PeerAsyncUIModel.XMPP_CONTACTING_ICON_IMAGE_PATH, "CONTACTING");
	
	private static final ImageIcon EDITINGICON = new ImageIcon(PeerAsyncUIModel.XMPP_EDITING_ICON_IMAGE_PATH, "EDITING");
	
	private static final String UP_MSG = "The XMPP server is Up";
	
	private static final String DOWN_MSG = "The XMPP server is Down";
	
	private static final String CONTACTING_MSG = "Trying to connect to XMPP server";
	
	private static final String EDIT_MSG = "Editing the XMPP configuration";
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordText;
    private javax.swing.JButton saveAndConnectButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel securePortLabel;
    private javax.swing.JTextField securePortText;
    private javax.swing.JLabel serverNameLabel;
    private javax.swing.JTextField serverNameText;
    private javax.swing.JLabel serverPortLabel;
    private javax.swing.JTextField serverPortText;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextField userNameText;
    
    private javax.swing.JTextField statusText;
    
	private JLabel iconLabel;
	
    /**
	 * Creates a new XMPPConfigurationPanel 
	 * @param model
	 * @param controller  
	 */
    public XMPPConfigurationPanel(OurGridUIModel model) {
    	super(model);
    }
    
    /*
     * (non-Javadoc)
     * @see org.ourgrid.common.ui.AbstractInputFieldsPanel#initComponents()
     */
    protected void initComponents() {

        userNameLabel = new javax.swing.JLabel();
        userNameText = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordText = new javax.swing.JPasswordField();
        jPanel1 = new javax.swing.JPanel();
        serverNameLabel = new javax.swing.JLabel();
        serverNameText = new javax.swing.JTextField();
        serverPortText = new javax.swing.JTextField();
        serverPortLabel = new javax.swing.JLabel();
        securePortLabel = new javax.swing.JLabel();
        securePortText = new javax.swing.JTextField();
        saveAndConnectButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        
        statusText = new javax.swing.JTextField();
        statusText.setEditable(false);

        iconLabel = new JLabel(CONTACTINGICON, JLabel.LEFT);

        setBorder(null);
        
        serverPortText.setName("serverPortText");
        
        serverNameText.setName("serverNameText");
        
        securePortText.setName("securePortText");
        
        userNameLabel.setText("XMPP User Name");

        passwordLabel.setText("XMPP User Password");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("XMPP Server"));

        serverNameLabel.setText("XMPP Server Name");

        serverPortLabel.setText("Server Port");

        securePortLabel.setText("Secure Port");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(serverNameLabel)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(serverPortText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(serverPortLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(securePortText)
                                    .add(securePortLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .add(362, 362, 362)))
                        .addContainerGap())
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(serverNameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(66, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                    		.addContainerGap()
                    		.add(statusText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 178, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    		.addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    		.add(iconLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    		.addContainerGap(159, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(serverNameLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(serverNameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(serverPortLabel)
                    .add(securePortLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(serverPortText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(securePortText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, statusText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, iconLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap()
                .add(12, 12, 12))
        );
        
        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        saveAndConnectButton.setText("Save and Connect");
        saveAndConnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
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
                        .add(userNameLabel)
                        .addContainerGap(429, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(passwordLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                        .add(404, 404, 404))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, passwordText)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, userNameText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                        .addContainerGap(376, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                    	.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.HORIZONTAL)
                            .add(editButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.HORIZONTAL)
                            .add(saveAndConnectButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
        );
        
        
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(userNameLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(userNameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(passwordLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(passwordText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                	.add(editButton)
                	.add(saveAndConnectButton))
                	.addContainerGap()
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                
        );
    }

    private void initTextFields() {
    	if (model != null) {
    		userNameText.setText(model.getProperty(XMPPProperties.PROP_USERNAME));
    		passwordText.setText(model.getProperty(XMPPProperties.PROP_PASSWORD));
    		serverNameText.setText(model.getProperty(XMPPProperties.PROP_XMPP_SERVERNAME));
    		serverPortText.setText(model.getProperty(XMPPProperties.PROP_XMPP_SERVERPORT));
    		securePortText.setText(model.getProperty(XMPPProperties.PROP_XMPP_SERVER_SECURE_PORT));
    	}
	}

	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
		savePropertiesAndConnect();
    }

	private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
		editXMPPConf();
    }
	
    private void editXMPPConf() {
    	model.editXMPPConf();
    	enableFieldEdition();
		disableEditConf();
		setXMPPEditingInfo();
	}
    
    private void savePropertiesAndConnect() {
    	setXMPPContactingInfo();
		enableEditConf();
		disableFieldEdition();
		saveProperties();
		model.propertiesSaved();
    }

	private void saveProperties() {
		
    	/*controller.setProperty(XMPPProperties.PROP_USERNAME, userNameText.getText());
    	controller.setProperty(XMPPProperties.PROP_PASSWORD, new String(passwordText.getPassword()));
    	controller.setProperty(XMPPProperties.PROP_XMPP_SERVERNAME, serverNameText.getText());
    	controller.setProperty(XMPPProperties.PROP_XMPP_SERVERPORT, serverPortText.getText());
    	controller.setProperty(XMPPProperties.PROP_XMPP_SERVER_SECURE_PORT, securePortText.getText());
    	
    	try {
			controller.saveProperties();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), 
					"IO Error" , JOptionPane.ERROR_MESSAGE);
		} finally {
			controller.loadProperties();
		}*/
    	
    	model.setProperty(XMPPProperties.PROP_USERNAME, userNameText.getText());
    	model.setProperty(XMPPProperties.PROP_PASSWORD, new String(passwordText.getPassword()));
    	model.setProperty(XMPPProperties.PROP_XMPP_SERVERNAME, serverNameText.getText());
    	model.setProperty(XMPPProperties.PROP_XMPP_SERVERPORT, serverPortText.getText());
    	model.setProperty(XMPPProperties.PROP_XMPP_SERVER_SECURE_PORT, securePortText.getText());
    	
    	try {
    		model.saveProperties();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), 
					"IO Error" , JOptionPane.ERROR_MESSAGE);
		} finally {
			model.loadProperties();
		}
		
		//model.propertiesSaved();
	}

	/**
	 * Disables all fields edition
	 */
	public void disableFieldEdition() {
		passwordText.setEnabled(false);
		securePortText.setEnabled(false);
		serverNameText.setEnabled(false);
		serverPortText.setEnabled(false);
		userNameText.setEnabled(false);
		saveAndConnectButton.setEnabled(false);
	}
	
	public void disableEditConf() {
		editButton.setEnabled(false);
		
	}
	
	public void enableEditConf() {
		editButton.setEnabled(true);
		
	}
	
	/**
	 * Enables all fields edition
	 */
	public void enableFieldEdition() {
		passwordText.setEnabled(true);
		securePortText.setEnabled(true);
		serverNameText.setEnabled(true);
		serverPortText.setEnabled(true);
		userNameText.setEnabled(true);
		saveAndConnectButton.setEnabled(true);
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
		//TODO - do some validation?
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

	public void setXMPPUpInfo() {
		iconLabel.setIcon(UPICON);
		statusText.setText(UP_MSG);
	}
	
	public void setXMPPDownInfo() {
		iconLabel.setIcon(DOWNICON);
		statusText.setText(DOWN_MSG);
	}
	
	public void setXMPPContactingInfo() {
		iconLabel.setIcon(CONTACTINGICON);
		statusText.setText(CONTACTING_MSG);
	}
	
	public void setXMPPEditingInfo() {
		iconLabel.setIcon(EDITINGICON);
		statusText.setText(EDIT_MSG);
	}

    
}
