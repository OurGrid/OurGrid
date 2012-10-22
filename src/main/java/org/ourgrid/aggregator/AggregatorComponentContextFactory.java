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
package org.ourgrid.aggregator;

import java.util.Map;

import org.ourgrid.common.OurGridContextFactory;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.context.ContextParser;

/**
 * 
 */
public class AggregatorComponentContextFactory extends OurGridContextFactory {

	private final String CONF_DIR = findConfDir();
	
	public AggregatorComponentContextFactory(ContextParser parser) {
		super(parser);
	}
	
	/**
	 * Returns a map of default properties.
	 * @return Map<Object, Object> properties
	 */
	@Override
	public Map<Object, Object> getDefaultProperties() {
		
		Map<Object, Object> properties = super.getDefaultProperties();
		properties.put( ModuleProperties.PROP_CONFDIR, CONF_DIR);
		
		properties.put( AggregatorConfiguration.PROP_LOGFILE, "aggregator.log" );
		
		properties.put( AggregatorConfiguration.PROP_DS_USERNAME, AggregatorConfiguration.DEFAULT_DS_USERNAME );
		properties.put( AggregatorConfiguration.PROP_DS_SERVERNAME, AggregatorConfiguration.DEFAULT_DS_SERVERNAME );

		return properties;
	}

	/**
	 * Returns the configuration directory.
	 * 
	 * @return The directory.
	 */
	@Req("REQ010")
	protected String findConfDir() {

		String prop = System.getenv( "OGROOT" );
		
		if ( prop == null || prop.equals( "" ) ) {
			prop = System.getProperty( "user.dir" );
		}
		return prop;
	}
	
}
