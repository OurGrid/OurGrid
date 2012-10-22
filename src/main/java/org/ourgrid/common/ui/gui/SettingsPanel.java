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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.ourgrid.broker.ui.async.model.BrokerAsyncUIModel;
import org.ourgrid.common.ui.AbstractInputFieldsPanel;
import org.ourgrid.common.ui.InputFieldsUI;

public abstract class SettingsPanel extends AbstractInputFieldsPanel implements InputFieldsUI {

	private static final long serialVersionUID = 1L;

	public static final String LOG_TAB_TITLE = "Log";
	
	protected static final String FILE_TRANSFER_TAB_TITLE = "File transfer";

	public static final String ADVANCED_TAB_TITLE = "Advanced";
	
	public static final String FD_TAB_TITLE = "Failure Detection";
	
	public static final String BASIC_TAB_TITLE = "Basic";

	public static final String RESTORE_DEFAULTS = "Restore Defaults";

	public static final String SAVE = "Save";

	public static final String SAVE_TOOLTIP_DISABLED = "Properties cannot be saved now (either XMPP properties are not filled or Broker daemon is running)";

	public static final String SAVE_TOOLTIP_ENABLED = "Click to Save Peer properties";

	protected JButton restoreDefaultsButton;

	protected JButton saveButton;

	protected JTabbedPane propertiesPane;

	protected JPanel settingsButtonsPanel;


	public SettingsPanel() {
		this(null);
	}
	
	public SettingsPanel(BrokerAsyncUIModel model) {
		super(model);
	}

	public void updateSaveButton( boolean enable ) {

		if ( !enable ) {
			saveButton.setToolTipText( SAVE_TOOLTIP_DISABLED );
		} else {
			saveButton.setToolTipText( SAVE_TOOLTIP_ENABLED );
		}
		saveButton.setEnabled( enable );
	}

}
