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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.OurGridUIModel;

public class SchedulingSettingsPanel extends AbstractInputFieldsPanel {

	public static final String[ ] HEURISTICS = new String[ ] { "workqueue" };

	private static final long serialVersionUID = 1L;

	private static final int SPINNER_VALUE_INCREMENT = 1;

	private static final int MAX_SPINNER_VALUE = 100;

	private static final int MIN_SPINNER_VALUE = 1;

	private JComboBox heuristicComboBox;

	private JLabel heuristicLabel;

	private JSpinner maxBLFailsSpinner;

	private JTextArea maxBLFailsText;

	private JSpinner maxFailsSpinner;

	private JTextArea maxFailsText;

	private JSpinner maxReplicasSpinner;

	private JTextArea maxReplicasText;


	public SchedulingSettingsPanel() {

		this(null);
	}
	
	public SchedulingSettingsPanel(OurGridUIModel model) {

		super(model);

		setBorder( BorderFactory.createTitledBorder( "Scheduling settings" ) );
		setDoubleBuffered( false );
	}


	protected void initComponents() {

		heuristicLabel = new JLabel();
		heuristicComboBox = new JComboBox();

		heuristicComboBox.setEditable( false );

		maxReplicasText = new JTextArea();
		maxReplicasText.setFocusable( false );

		maxReplicasSpinner = new JSpinner();
		maxReplicasSpinner.setModel( new SpinnerNumberModel(1, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE,
			SPINNER_VALUE_INCREMENT ) );

		maxFailsText = new JTextArea();
		maxFailsText.setFocusable( false );

		maxFailsSpinner = new JSpinner();
		maxFailsSpinner.setModel( new SpinnerNumberModel( 1,
			MIN_SPINNER_VALUE, MAX_SPINNER_VALUE, SPINNER_VALUE_INCREMENT ) );

		maxBLFailsText = new JTextArea();
		maxBLFailsText.setFocusable( false );

		maxBLFailsSpinner = new JSpinner();
		maxBLFailsSpinner.setModel( new SpinnerNumberModel(1, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE,
			SPINNER_VALUE_INCREMENT ) );

		maxReplicasText.setBackground( this.getBackground() );
		maxReplicasText.setFont( new JLabel().getFont() );
		maxReplicasText.setLineWrap( true );
		maxReplicasText.setRows( 1 );
		maxReplicasText.setText( "Maximum simultaneous replicas?" );
		maxReplicasText.setWrapStyleWord( true );

		heuristicLabel.setText( "Scheduling Heuristic" );

		heuristicComboBox.setModel( new DefaultComboBoxModel( HEURISTICS ) );

		maxFailsText.setBackground( this.getBackground() );
		maxFailsText.setFont( new JLabel().getFont() );
		maxFailsText.setLineWrap( true );
		maxFailsText.setRows( 1 );
		maxFailsText.setText( "Max execution attempts before a task fails?" );
		maxFailsText.setWrapStyleWord( true );

		maxBLFailsText.setBackground( this.getBackground() );
		maxBLFailsText.setFont( new JLabel().getFont() );
		maxBLFailsText.setLineWrap( true );
		maxBLFailsText.setRows( 1 );
		maxBLFailsText.setText( "Max machine fails before blacklisting?" );
		maxBLFailsText.setWrapStyleWord( true );

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, maxFailsText)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(heuristicLabel)
                        .add(18, 18, 18)
                        .add(heuristicComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, maxFailsSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, maxReplicasSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, maxBLFailsSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, maxReplicasText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, maxBLFailsText))
                .addContainerGap(259, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(heuristicLabel)
                    .add(heuristicComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(9, 9, 9)
                .add(maxReplicasText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(maxReplicasSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(maxFailsText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(maxFailsSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(maxBLFailsText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(maxBLFailsSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

		setDefaults();
	}


	public void setDefaults() {

		heuristicComboBox.setSelectedIndex( 0 );
		maxFailsSpinner.setValue( Integer.parseInt(BrokerConfiguration.DEFAULT_MAX_FAILS) );
		maxReplicasSpinner.setValue( Integer.parseInt(BrokerConfiguration.DEFAULT_MAX_REPLICAS) );
		maxBLFailsSpinner.setValue( Integer.parseInt(BrokerConfiguration.DEFAULT_MAX_BL_FAILS) );
		
	}

	public JComboBox getHeuristicComboBox() {

		return this.heuristicComboBox;
	}


	public JSpinner getMaxBLFailsSpinner() {

		return this.maxBLFailsSpinner;
	}


	public JSpinner getMaxFailsSpinner() {

		return this.maxFailsSpinner;
	}


	public JSpinner getMaxReplicasSpinner() {

		return this.maxReplicasSpinner;
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
		if(this.model != null) {
			try {
				this.maxBLFailsSpinner.setValue(Integer.parseInt(model.getProperty(BrokerConfiguration.PROP_MAX_BL_FAILS)));
			} catch (Exception e) {
				this.maxBLFailsSpinner.setValue(Integer.parseInt(BrokerConfiguration.DEFAULT_MAX_BL_FAILS));	
			}
			try {
				this.maxFailsSpinner.setValue(Integer.parseInt(model.getProperty(BrokerConfiguration.PROP_MAX_FAILS)));
			} catch (Exception e) {
				this.maxFailsSpinner.setValue(Integer.parseInt(BrokerConfiguration.DEFAULT_MAX_FAILS));	
			}
			try {
				this.maxReplicasSpinner.setValue(Integer.parseInt(model.getProperty(BrokerConfiguration.PROP_MAX_REPLICAS)));
			} catch (Exception e) {
				this.maxReplicasSpinner.setValue(Integer.parseInt(BrokerConfiguration.DEFAULT_MAX_REPLICAS));	
			}
			
			
		}
		
	}


	public void saveFieldInputs() throws IOException {
		/*if (controller == null) return;
		controller.setProperty(BrokerConfiguration.PROP_MAX_REPLICAS, 
				maxReplicasSpinner.getValue().toString());
		controller.setProperty(BrokerConfiguration.PROP_MAX_BL_FAILS, 
				maxBLFailsSpinner.getValue().toString());
		controller.setProperty(BrokerConfiguration.PROP_MAX_FAILS, 
				maxFailsSpinner.getValue().toString());*/
		
		model.setProperty(BrokerConfiguration.PROP_MAX_REPLICAS, 
				maxReplicasSpinner.getValue().toString());
		model.setProperty(BrokerConfiguration.PROP_MAX_BL_FAILS, 
				maxBLFailsSpinner.getValue().toString());
		model.setProperty(BrokerConfiguration.PROP_MAX_FAILS, 
				maxFailsSpinner.getValue().toString());
		
	}
}
