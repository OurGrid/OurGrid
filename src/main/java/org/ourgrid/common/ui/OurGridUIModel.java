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

public interface OurGridUIModel {

	/**
	 * Gets a property from the model
	 * @param prop the id of the property
	 * @return the property value
	 */
	public String getProperty(String prop);
	
	public void setProperty(String property, String value);
	
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
	
	public void propertiesSaved();
	
	public void editXMPPConf();
	
}
