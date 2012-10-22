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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.ui.async.gui.graphical.BrokerGUIMainFrame;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.OurGridUIModel;
import org.ourgrid.common.ui.gui.OGFileChooser;

public class AdvancedBrokerSettingsPanel extends AbstractInputFieldsPanel {

	private static final int MAX_NUM_OF_REPLICA_EXECUTORS = 10;

	private static final long serialVersionUID = 1L;

	private JLabel numOfReplicaExecutorsLabel;

	private JSpinner numOfReplicaExecutorsSpinner;

	private JCheckBox persistJobIDCheckBox;

	private JButton defaultGDFBrowseButton;

	protected JTextField defaultGDFField;

	private JLabel defaultGDFLabel;


	public AdvancedBrokerSettingsPanel(OurGridUIModel model) {
		super(model);
		setBorder( BorderFactory.createTitledBorder( "Advanced Broker Settings" ) );
	}
	
	public AdvancedBrokerSettingsPanel() {

		this(null);
	}


	protected void initComponents() {
		persistJobIDCheckBox = new javax.swing.JCheckBox();
        defaultGDFLabel = new javax.swing.JLabel();
        defaultGDFField = new javax.swing.JTextField();
        defaultGDFBrowseButton = new javax.swing.JButton();

        persistJobIDCheckBox.setText("Keep job counting across sections");

        defaultGDFLabel.setText("Default Grid Description File");

        defaultGDFBrowseButton.setText("Browse");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(persistJobIDCheckBox)
                    .add(defaultGDFLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(defaultGDFField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(defaultGDFBrowseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(persistJobIDCheckBox)
                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(defaultGDFLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(defaultGDFBrowseButton)
                    .add(defaultGDFField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

//		defaultGDFBrowseButton.addActionListener( new ActionListener() {
//
//			public void actionPerformed( ActionEvent e ) {
//
//				File file = new OGFileChooser( BrokerGUIMainFrame.GDF_FILE_DESCR, BrokerGUIMainFrame.GDF_FILE_EXT )
//					.selectFile( AdvancedBrokerSettingsPanel.this );
//				if ( file != null ) {
//					try {
//						DescriptionFileCompile.compileGDF( file.getAbsolutePath() );
//						defaultGDFField.setText( file.getAbsolutePath() );
//					} catch ( CompilerException e1 ) {
//						JOptionPane.showMessageDialog( AdvancedBrokerSettingsPanel.this, file.getName()
//								+ " is not a valid Grid Description File", BrokerGUIMainFrame.ERROR_OCCURRED,
//							JOptionPane.ERROR_MESSAGE );
//					}
//				}
//				
//			}
//		} );

		setDefaults();
	}


	public void setDefaults() {
/*
		numOfReplicaExecutorsSpinner
			.setValue( Integer.parseInt( BrokerConfiguration.DEFAULT_NUM_OF_REPLICA_EXECUTORS ) );
		persistJobIDCheckBox.setSelected( true );
		*/
	}


	public JTextField getDefaultGDFField() {

		return this.defaultGDFField;
	}


	public JSpinner getNumOfReplicaExecutorsSpinner() {

		return this.numOfReplicaExecutorsSpinner;
	}


	public JCheckBox getPersistJobIDCheckBox() {

		return this.persistJobIDCheckBox;
	}


	public void loadPropertiesFromConfiguration() {
/*
		numOfReplicaExecutorsSpinner.setValue( BrokerConfiguration.getInstance().parseIntegerProperty(
			BrokerConfiguration.PROP_NUM_OF_REPLICA_EXECUTORS ) );
		persistJobIDCheckBox.setSelected( BrokerConfiguration.getInstance().isEnabled(
			BrokerConfiguration.PROP_PERSISTJOBID ) );
			*/
	}


	@Override
	public void setEnabled( boolean enabled ) {

		super.setEnabled( enabled );
		Component[ ] components = getComponents();

		for ( int i = 0; i < components.length; i++ ) {
			components[i].setEnabled( enabled );
		}
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
			persistJobIDCheckBox.setSelected(model.getProperty(BrokerConfiguration.PROP_PERSISTJOBID).trim().toLowerCase().equals("yes"));
		} catch (Exception e) {
			persistJobIDCheckBox.setSelected(BrokerConfiguration.DEFAULT_PERSISTJOBID.trim().toLowerCase().equals("yes"));
		}
		
	}


	public void saveFieldInputs() throws IOException {
		/*if (controller == null) return;
		
		controller.setProperty(BrokerConfiguration.PROP_PERSISTJOBID,
				persistJobIDCheckBox.isSelected() ? "yes" : "no");
		if (defaultGDFField.getText() != null && !defaultGDFField.getText().trim().equals("")) {
			controller.setProperty(BrokerConfiguration.PROP_DEFAULT_GDF, defaultGDFField.getText());
		}	*/
		
		model.setProperty(BrokerConfiguration.PROP_PERSISTJOBID,
				persistJobIDCheckBox.isSelected() ? "yes" : "no");
	}
}
