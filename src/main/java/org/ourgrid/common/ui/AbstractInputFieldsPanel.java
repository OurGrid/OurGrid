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
package org.ourgrid.common.ui;

import java.io.IOException;

import javax.swing.JPanel;

/**
 * Base class that implements basic functionality and has basic attributes for
 * an OurGUI Panel.<p>
 * 
 * Subclasses must implement the initComponents method, it should initialize the 
 * components of the GUI. 
 *
 */
public abstract class AbstractInputFieldsPanel extends JPanel implements InputFieldsUI {

	protected OurGridUIModel model;
	
	/**
	 * Creates a new AbstractInputFieldsPanel
	 * @param model OurGrid UI model
	 * @param controller class that sends UI commands to the respective Component
	 */
	public AbstractInputFieldsPanel(OurGridUIModel model) {
		setModel(model);
		initComponents();
		try {
			initFields();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes GUI components
	 */
	protected abstract void initComponents();

	/**
	 * Sets the model
	 * @param model
	 */
	protected void setModel(OurGridUIModel model) {
		this.model = model;
	}

}
