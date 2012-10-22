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

import java.io.File;
import java.util.Map;

import org.ourgrid.common.util.CommonUtils;

/**
 * This class defines an abstraction for the operating system dependent part of
 * executing configuration. The concrete implementors of this class must provide the
 * correct behavior for setting the default properties.
 *
 */
public abstract class AbstractExecutorConfiguration implements ExecutorConfiguration {

	protected final Map<String, String> properties = CommonUtils.createSerializableMap();
	private final String[] propsNames;
	private final File rootDir;
	
	/**
	 * @param rootDir the root directory where the files and scripts will be stored
	 * @param propNames the names (keys) of the properties which will be set in the properties maps
	 */
	public AbstractExecutorConfiguration(File rootDir, String... propNames) {
		this.rootDir = rootDir;
		this.propsNames = propNames;
		this.setDefaultProperties();
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.config.ExecutorConfiguration#loadCustomProperty(java.util.Map)
	 */
	public final void loadCustomProperty(Map<String, String> properties) {
		this.properties.putAll(properties);
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.config.ExecutorConfiguration#getProperty(java.lang.String)
	 */
	public final String getProperty(String name) {
		return properties.get(name);
	}

	public final File getRootDir() {
		return rootDir;
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.config.ExecutorConfiguration#getDefaultPropertiesNames()
	 */
	public String[] getDefaultPropertiesNames() {
		return propsNames;
	}
	
	/**
	 * Sets the default properties of the virtual machine in the properties map
	 */
	public abstract void setDefaultProperties();

}
