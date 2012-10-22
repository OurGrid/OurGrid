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
package org.ourgrid.discoveryservice;

import java.util.Map;

import org.ourgrid.common.OurGridContextFactory;
import org.ourgrid.discoveryservice.config.DiscoveryServiceConfiguration;

import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.context.ContextParser;

public class DiscoveryServiceComponentContextFactory extends OurGridContextFactory {

	private final String CONF_DIR = findConfDir();
	
	public DiscoveryServiceComponentContextFactory(ContextParser parser) {
		super(parser);
	}
	
	@Override
	public Map<Object, Object> getDefaultProperties() {
		
		Map<Object, Object> properties = super.getDefaultProperties();
		properties.put(ModuleProperties.PROP_CONFDIR, CONF_DIR);
		properties.put(DiscoveryServiceConfiguration.PROP_OVERLOAD_THRESHOLD, DiscoveryServiceConfiguration.DEF_OVERLOAD_THRESHOLD);
		properties.put(DiscoveryServiceConfiguration.PROP_MAX_RESPONSE_SIZE, DiscoveryServiceConfiguration.DEF_MAX_RESPONSE_SIZE);
		
		return properties;
	}

	/**
	 * Returns the configuration directory.
	 * 
	 * @return The directory.
	 */
	protected String findConfDir() {
		String prop = System.getenv( "OGROOT" );
		if ( prop == null || prop.equals( "" ) ) {
			prop = System.getProperty( "user.dir" );
		}
		return prop;
	}
	
}
