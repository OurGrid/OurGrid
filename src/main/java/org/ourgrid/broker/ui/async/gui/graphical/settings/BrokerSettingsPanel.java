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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.ourgrid.broker.ui.async.gui.graphical.BrokerGUIMainFrame;
import org.ourgrid.broker.ui.async.model.BrokerAsyncUIModel;
import org.ourgrid.common.ui.gui.CommuneDetectionDelaySettingsPanel;
import org.ourgrid.common.ui.gui.CommuneSettingsPanel;
import org.ourgrid.common.ui.gui.SettingsPanel;
import org.ourgrid.common.ui.gui.XMPPConfigurationPanel;

public class BrokerSettingsPanel extends SettingsPanel {

	private static final long serialVersionUID = 1L;

	private AdvancedBrokerSettingsPanel advancedBrokerSettingsPanel;

	private JPanel advancedPropertiesPanel;

	private CommuneSettingsPanel advancedXmppSettingsPanel;

	private JPanel basicPropertiesPanel;

//	private LogSettingsPanel brokerLogSettingsPanel;

	private XMPPConfigurationPanel xmppBasicSettingsPanel;

	private SchedulingSettingsPanel schedulingSettingsPanel;

	private BrokerFileTransferSettingsPanel fileTransferSettingsPanel;

	private CommuneDetectionDelaySettingsPanel fdPanel;


	public BrokerSettingsPanel(BrokerAsyncUIModel model) {
		super(model);
		setLayout( new BorderLayout() );
		initComponents();
	}


	protected void initComponents() {

		advancedPropertiesPanel = new JPanel();
		advancedPropertiesPanel.setLayout( new BorderLayout() );

		basicPropertiesPanel = new JPanel();
		basicPropertiesPanel.setLayout( new BorderLayout() );

		propertiesPane = new JTabbedPane();
		xmppBasicSettingsPanel = new XMPPConfigurationPanel(model);
		schedulingSettingsPanel = new SchedulingSettingsPanel(model);

		advancedXmppSettingsPanel = new CommuneSettingsPanel(model);
		advancedBrokerSettingsPanel = new AdvancedBrokerSettingsPanel(model);
		
		fileTransferSettingsPanel = new BrokerFileTransferSettingsPanel(model);
		fdPanel = new CommuneDetectionDelaySettingsPanel(model);
		
//		try {
//			brokerLogSettingsPanel = new LogSettingsPanel(model, controller);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		basicPropertiesPanel.add( xmppBasicSettingsPanel, BorderLayout.NORTH );

		basicPropertiesPanel.add( schedulingSettingsPanel, BorderLayout.CENTER );

		propertiesPane.addTab( BASIC_TAB_TITLE, basicPropertiesPanel );

		advancedPropertiesPanel.add( advancedBrokerSettingsPanel, BorderLayout.NORTH );

		advancedPropertiesPanel.add( advancedXmppSettingsPanel, BorderLayout.CENTER );

		propertiesPane.addTab( ADVANCED_TAB_TITLE, advancedPropertiesPanel );

		propertiesPane.addTab( FD_TAB_TITLE, fdPanel);
		
		propertiesPane.addTab( FILE_TRANSFER_TAB_TITLE, fileTransferSettingsPanel );
		
//			propertiesPane.addTab( LOG_TAB_TITLE, brokerLogSettingsPanel );
//		}

		this.add( propertiesPane, BorderLayout.CENTER );
		this.
		configureButtonPanel();

		//xmppBasicSettingsPanel.checkIfAllXMPPPropertiesAreFilled();
	}


	private void configureButtonPanel() {

		settingsButtonsPanel = new JPanel();

		saveButton = new JButton();
		saveButton.setText( SAVE );
		settingsButtonsPanel.add( saveButton );

		restoreDefaultsButton = new JButton();
		restoreDefaultsButton.setText( RESTORE_DEFAULTS );
		settingsButtonsPanel.add( restoreDefaultsButton );

		this.add( settingsButtonsPanel, BorderLayout.SOUTH );

		saveButton.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {

				settingsSaved();
			}
		} );

		restoreDefaultsButton.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {

				try {
					restoreDefaults();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(BrokerSettingsPanel.this, "Failed to load default properties", 
							BrokerGUIMainFrame.ERROR_OCCURRED, JOptionPane.ERROR_MESSAGE);
				}
			}
		} );
	}


	public void restoreDefaults() throws IOException {
		/*controller.restoreDefaultPropertiesValues();*/
		
		model.restoreDefaultPropertiesValues();
		this.initFields();
/*
		xmppBasicSettingsPanel.setDefaults();
		schedulingSettingsPanel.setDefaults();
		advancedBrokerSettingsPanel.setDefaults();
		advancedXmppSettingsPanel.setDefaults();
		brokerLogSettingsPanel.setDefaults();
		brokerFileTransferSettingsPanel.setDefaults();
		*/
	}

	public void settingsSaved() {
		try {
			this.saveFieldInputs();
			/*controller.saveProperties();*/
			model.saveProperties();
		} catch (IOException e) {;
			JOptionPane.showMessageDialog(this, "Failed to save properties", 
					BrokerGUIMainFrame.ERROR_OCCURRED, JOptionPane.ERROR_MESSAGE);
		}
		
	}


	public AdvancedBrokerSettingsPanel getAdvancedBrokerSettingsPanel() {

		return this.advancedBrokerSettingsPanel;
	}


	public CommuneSettingsPanel getAdvancedXmppSettingsPanel() {

		return this.advancedXmppSettingsPanel;
	}


//	public LogSettingsPanel getLogSettingsPanel() {
//
//		return this.brokerLogSettingsPanel;
//	}


	public SchedulingSettingsPanel getSchedulingSettingsPanel() {

		return this.schedulingSettingsPanel;
	}


	public XMPPConfigurationPanel getXmppBasicSettingsPanel() {

		return this.xmppBasicSettingsPanel;
	}
	
	public void setEnabled(boolean enabled) {
		
		if (enabled) {
			daemonIsDown();
			fdPanel.enableFieldEdition();
		} else {
			daemonIsUp();
			fdPanel.disableFieldEdition();
		}
		
		basicPropertiesPanel.setEnabled(enabled);
		fileTransferSettingsPanel.setEnabled(enabled);
		advancedBrokerSettingsPanel.setEnabled(enabled);
		
	}

	public void initSettingsPanel() {
		
		daemonIsInited();
		fdPanel.disableFieldEdition();
		
		basicPropertiesPanel.setEnabled(false);
		fileTransferSettingsPanel.setEnabled(false);
		advancedBrokerSettingsPanel.setEnabled(false);
		
	}
	
	public void reinitSettingsPanel() {
		
		daemonIsRestarted();
		fdPanel.disableFieldEdition();
		
		basicPropertiesPanel.setEnabled(false);
		fileTransferSettingsPanel.setEnabled(false);
		advancedBrokerSettingsPanel.setEnabled(false);
		
	}
	
	public void editSettingsPanel() {
		
		daemonIsDisconnected();
		fdPanel.disableFieldEdition();
		
		basicPropertiesPanel.setEnabled(false);
		fileTransferSettingsPanel.setEnabled(false);
		advancedBrokerSettingsPanel.setEnabled(false);
		
	}
	
	public void daemonIsUp() {

		updateSaveButton( false );
		restoreDefaultsButton.setEnabled( false );
		xmppBasicSettingsPanel.setEnabled( false );
		xmppBasicSettingsPanel.disableInput();
    	xmppBasicSettingsPanel.disableEditConf();
		schedulingSettingsPanel.setEnabled( false );
		advancedBrokerSettingsPanel.setEnabled( false );
		advancedXmppSettingsPanel.setEnabled( false );
		advancedXmppSettingsPanel.disableInput();
//		brokerLogSettingsPanel.setEnabled( false );

	}
	
	public void daemonIsInited() {

		updateSaveButton( true );
		restoreDefaultsButton.setEnabled( true );
		xmppBasicSettingsPanel.disableFieldEdition();
		xmppBasicSettingsPanel.setXMPPContactingInfo();
		xmppBasicSettingsPanel.enableEditConf();
		schedulingSettingsPanel.setEnabled( true );
		advancedBrokerSettingsPanel.setEnabled( true );
		advancedXmppSettingsPanel.setEnabled( true );
		advancedXmppSettingsPanel.enableInput();
//		brokerLogSettingsPanel.setEnabled( true );

	}
	
	public void daemonIsRestarted() {

		updateSaveButton( true );
		restoreDefaultsButton.setEnabled( true );
		xmppBasicSettingsPanel.disableFieldEdition();
		xmppBasicSettingsPanel.setXMPPUpInfo();
		xmppBasicSettingsPanel.disableEditConf();
		schedulingSettingsPanel.setEnabled( true );
		advancedBrokerSettingsPanel.setEnabled( true );
		advancedXmppSettingsPanel.setEnabled( true );
		advancedXmppSettingsPanel.enableInput();
//		brokerLogSettingsPanel.setEnabled( true );

	}
	
	public void daemonIsDisconnected() {

		updateSaveButton( true );
		restoreDefaultsButton.setEnabled( true );
		xmppBasicSettingsPanel.disableFieldEdition();
		xmppBasicSettingsPanel.setXMPPEditingInfo();
		xmppBasicSettingsPanel.disableEditConf();
		schedulingSettingsPanel.setEnabled( true );
		advancedBrokerSettingsPanel.setEnabled( true );
		advancedXmppSettingsPanel.setEnabled( true );
		advancedXmppSettingsPanel.enableInput();
//		brokerLogSettingsPanel.setEnabled( true );

	}
	
	public void daemonIsDown() {

		updateSaveButton( true );
		restoreDefaultsButton.setEnabled( true );
		
		xmppBasicSettingsPanel.disableFieldEdition();
		xmppBasicSettingsPanel.setXMPPUpInfo();
    	xmppBasicSettingsPanel.enableEditConf();
    	
		schedulingSettingsPanel.setEnabled( true );
		advancedBrokerSettingsPanel.setEnabled( true );
		advancedXmppSettingsPanel.setEnabled( false );
		advancedXmppSettingsPanel.disableInput();
//		brokerLogSettingsPanel.setEnabled( true );
	}


	public void disableInput() {
		// TODO Auto-generated method stub
		
	}


	public void enableInput() {
		// TODO Auto-generated method stub
		
	}


	public void initFields() throws IOException {
		advancedBrokerSettingsPanel.initFields();
		advancedXmppSettingsPanel.initFields();
//		brokerLogSettingsPanel.initFields();
		xmppBasicSettingsPanel.initFields();
		schedulingSettingsPanel.initFields();
		fileTransferSettingsPanel.initFields();
		fdPanel.initFields();
	}


	public void saveFieldInputs() throws IOException {
		advancedBrokerSettingsPanel.saveFieldInputs();
		advancedXmppSettingsPanel.saveFieldInputs();
//		brokerLogSettingsPanel.saveFieldInputs();
		xmppBasicSettingsPanel.saveFieldInputs();
		schedulingSettingsPanel.saveFieldInputs();
		fileTransferSettingsPanel.saveFieldInputs();
		fdPanel.saveFieldInputs();
	}
}
