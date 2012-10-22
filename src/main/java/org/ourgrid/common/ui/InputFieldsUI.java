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

/**
 * Common interface for UI components that have input fields.<br>
 * Provides methods for initializing fields with default values, saving values
 * as default. Also provides methods for enabling and disabling input. 
 */
public interface InputFieldsUI {

	/**
	 * Initializes the input fields with the default values.
	 * @throws IOException
	 */
	public void initFields() throws IOException;
	
	/**
	 * Saves the values on the input fields as the default values.
	 * @throws IOException
	 */
	public void saveFieldInputs() throws IOException;
	
	/**
	 * Enables input on the fields.
	 */
	public void enableInput();
	
	/**
	 * Disable input on the fields.
	 */
	public void disableInput();
	
}
