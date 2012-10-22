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

import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.InputFieldsUI;
import org.ourgrid.common.ui.OurGridUIModel;

/**
 * Panel with input fields for configuring the Commune settings
 */
public class CommuneSettingsPanel extends AbstractInputFieldsPanel implements InputFieldsUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private KeysPanel keysPanel;
	private CertificationPanel certPanel;

	/**
	 * Creates a new SettingsPanel
	 * @param model OurGrid UI model
	 * @param controller class that sends UI commands to the respective Commune Component
	 */
	public CommuneSettingsPanel(OurGridUIModel model) {
		super(model);
		initComponents();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.ourgrid.common.ui.AbstractInputFieldsPanel#initComponents()
	 */
	public void initComponents() {
		keysPanel = new org.ourgrid.common.ui.gui.KeysPanel(model);
		certPanel = new CertificationPanel(model);
		
		keysPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Key settings"));
		certPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Certification settings"));
		
		org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(panelLayout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(keysPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(panelLayout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(certPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(198, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .add(keysPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(certPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
	}

	/**
	 * Enables input on all fields
	 */
	private void enableFieldEdition() {
		keysPanel.enableFieldEdition();
		certPanel.enableFieldEdition();
	}

	/**
	 * Saves the values at the input field as default values
	 */
	private void saveProperties() {
		keysPanel.saveProperties();
		certPanel.saveProperties();
	}

	/**
	 * Disables field input
	 */
	private void disableFieldEdition() {
		keysPanel.disableFieldEdition();
		certPanel.disableFieldEdition();
	}

	/*
	 * (non-Javadoc)
	 * @see org.ourgrid.common.ui.InputFieldsUI#initFields()
	 */
	public void initFields() throws IOException {
		keysPanel.initFields();
		certPanel.disableFieldEdition();
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
