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
package org.ourgrid.common.executor.config;

import java.util.Map;

/**
 * This interface defines the contract between platform dependent layer and the
 * upper layers. It describes the methods which will be used to set the properties 
 * of the virtual machine.
 *
 */
public interface ExecutorConfiguration {

	/**
	 * Loads the properties received setting them up in the local properties map.
	 * @param properties
	 */
	public void loadCustomProperty(Map<String, String> properties);
	
	/**
	 * Returns the value in the properties map for the key (property) passed as argument
	 * @param name the property which represents a key in the properties map
	 * @return the value for the passed key (property)
	 */
	public String getProperty(String name);
	
	/**
	 * Returns an array with the default names of the properties set in the properties map
	 * @return an array with the default names of the properties set in the properties map
	 */
	public String[] getDefaultPropertiesNames();
	
}
