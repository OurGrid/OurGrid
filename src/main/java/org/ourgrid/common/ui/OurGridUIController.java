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

public interface OurGridUIController {

	/**
	 * Starts the component
	 */
	public void start();

	
	/**
	 * Stops the component
 	 * @param callExit if true, the component's control implementation must call System.exit(0),
 	 *  causing the component's VM to shutdown.
	 * @param force if true, the component must shutdown immediately; otherwise, it may shutdown gracefully,
	 *  waiting to a specific work to finish or notifying its listeners about that it is going down.
	 */
	public void stop( boolean callExit, boolean force);
	
	/**
	 * Sets a property on the component
	 * @param prop the property id
	 * @param value the new property value
	 */
	public void setProperty(String prop, String value);
	
	/**
	 * Saves the properties as the default values
	 * @throws IOException
	 */
	public void saveProperties() throws IOException;
	
	/**
	 * Loads the default properties' values
	 */
	public void loadProperties();


	public void restoreDefaultPropertiesValues();
	
}
