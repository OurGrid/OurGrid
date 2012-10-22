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
package org.ourgrid.broker.ui.async.gui.graphical.settings;

import java.awt.Component;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.OurGridUIModel;

import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProperties;


public class BrokerFileTransferSettingsPanel extends AbstractInputFieldsPanel {

	private static final long serialVersionUID = 1L;

	private JLabel fileTransferTimeoutLabel;

	private JSpinner fileTransferTimeoutSpinner;

	private JLabel maxFileTransfersLabel;

	private JSpinner maxFileTransfersSpinner;

	private JCheckBox seeProgressCheckBox;


	public BrokerFileTransferSettingsPanel(OurGridUIModel model) {
		super(model);
		setBorder( BorderFactory.createTitledBorder( "File transfer settings" ) );
	}
	
	public BrokerFileTransferSettingsPanel() {
		this(null);
	}


	public JSpinner getFileTransferTimeoutSpinner() {

		return this.fileTransferTimeoutSpinner;
	}


	public JSpinner getMaxFileTransfersSpinner() {

		return this.maxFileTransfersSpinner;
	}


	public JCheckBox getSeeProgressCheckBox() {

		return this.seeProgressCheckBox;
	}

	@Override
	public void setEnabled( boolean enabled ) {

		super.setEnabled( enabled );

		Component[ ] components = getComponents();

		for ( int i = 0; i < components.length; i++ ) {
			components[i].setEnabled( enabled );

		}
	}


	protected void initComponents() {

		maxFileTransfersLabel = new javax.swing.JLabel();
        fileTransferTimeoutLabel = new javax.swing.JLabel();
        seeProgressCheckBox = new javax.swing.JCheckBox();
        maxFileTransfersSpinner = new javax.swing.JSpinner();
        fileTransferTimeoutSpinner = new javax.swing.JSpinner();

        maxFileTransfersLabel.setText("Maximum simultaneous file transfers");

        fileTransferTimeoutLabel.setText("File transfer inactvity timeout (ms)");

        seeProgressCheckBox.setText("See progress of file transfers");
        seeProgressCheckBox.setSelected(true);

        maxFileTransfersSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(5), Integer.valueOf(1), null, Integer.valueOf(1)));

        fileTransferTimeoutSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(600000), Integer.valueOf(0), null, Integer.valueOf(1)));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(seeProgressCheckBox)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(maxFileTransfersLabel)
                            .add(fileTransferTimeoutLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(maxFileTransfersSpinner)
                            .add(fileTransferTimeoutSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(maxFileTransfersLabel)
                    .add(maxFileTransfersSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(fileTransferTimeoutLabel)
                    .add(fileTransferTimeoutSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(seeProgressCheckBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
	}

	public void disableInput() {
		// TODO Auto-generated method stub
		
	}


	public void enableInput() {
		// TODO Auto-generated method stub
		
	}


	public void initFields() throws IOException {
		if (model == null) return;
		
		try {
			this.fileTransferTimeoutSpinner.setValue(
					Integer.parseInt(model.getProperty(TransferProperties.PROP_FILE_TRANSFER_TIMEOUT)));
		} catch (Exception e) {
			this.fileTransferTimeoutSpinner.setValue(Integer.parseInt(TransferProperties.DEFAULT_FILE_TRANSFER_TIMEOUT));
		}
		
		try {
			this.maxFileTransfersSpinner.setValue(
					Integer.parseInt(model.getProperty(TransferProperties.PROP_FILE_TRANSFER_MAX_OUT)));
		} catch (Exception e) {
			this.maxFileTransfersSpinner.setValue(Integer.parseInt(TransferProperties.DEFAULT_FILE_TRANSFER_MAX_OUT));
		}
		
		try {
			this.seeProgressCheckBox.setSelected(
					model.getProperty(TransferProperties.PROP_FILE_TRANSFER_NOTIFY_PROGRESS).toLowerCase().
					trim().equals("yes"));
		} catch (Exception e) {
			this.seeProgressCheckBox.setSelected(TransferProperties.DEFAULT_FILE_TRANSFER_NOTIFY_PROGRESS
					.toLowerCase().trim().equals("yes"));
		}
		
	}


	public void saveFieldInputs() throws IOException {
		/*if (controller == null) return;
		
		controller.setProperty(TransferProperties.PROP_FILE_TRANSFER_MAX_OUT, 
				this.maxFileTransfersSpinner.getValue().toString());
		controller.setProperty(TransferProperties.PROP_FILE_TRANSFER_TIMEOUT, 
				this.fileTransferTimeoutSpinner.getValue().toString());
		controller.setProperty(TransferProperties.PROP_FILE_TRANSFER_NOTIFY_PROGRESS, 
				this.seeProgressCheckBox.isSelected() == true ? "yes" : "no" );*/
		
		model.setProperty(TransferProperties.PROP_FILE_TRANSFER_MAX_OUT, 
				this.maxFileTransfersSpinner.getValue().toString());
		model.setProperty(TransferProperties.PROP_FILE_TRANSFER_TIMEOUT, 
				this.fileTransferTimeoutSpinner.getValue().toString());
		model.setProperty(TransferProperties.PROP_FILE_TRANSFER_NOTIFY_PROGRESS, 
				this.seeProgressCheckBox.isSelected() == true ? "yes" : "no" );
		
	}
}
