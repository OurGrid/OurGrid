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

import static java.io.File.separator;
import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import org.ourgrid.common.config.Configuration;

/**
 * Class that stores some configurations from this module.
 */
public class AggregatorConfiguration extends Configuration {

	private static final long serialVersionUID = 1L;

	public static final String AGGREGATOR = AggregatorConfiguration.class.getName();
	
	public static final String PREFIX = "aggregator.";

	public static final String CONF_DIR = findConfDir();

	public static final String PROPERTIES_FILENAME = CONF_DIR + separator + "aggregator.properties";

	public static final String DEFAULT_AGG_USERNAME = "aggregator";

	public static final String DEFAULT_AGG_SERVERNAME = "xmpp.ourgrid.org";

	public static final String PROP_AGG_USERNAME = "username";

	public static final String PROP_AGG_SERVERNAME = "servername";
	
	public static final String PROP_DS_USERNAME = PREFIX + "ds.username";

	public static final String PROP_DS_SERVERNAME = PREFIX + "ds.servername";
	
	public static final String DEFAULT_DS_USERNAME = "lsd-ds";

	public static final String DEFAULT_DS_SERVERNAME = "xmpp.ourgrid.org";

	@Override
	public String getConfDir() {
		return CONF_DIR;
	}

	@Override
	public String getLogPath() {
		String logfile = getProperty( PROP_LOGFILE );
		if ( logfile == null ) {
			logfile = getConfDir() + separator + "log" +
					separator + "aggregator.log";
		}

		return logfile;
	}

	public static String findConfDir() {
		String property = System.getenv( "OGROOT" );
		return property == null ? "." : property;
	}

	@Override
	public String toString() {

		StringBuilder conf = new StringBuilder( super.toString() );

		conf.append( "\tAggregator username: " );
		conf.append( this.getProperty( PROP_AGG_USERNAME ) );
		conf.append( LINE_SEPARATOR );

		conf.append( "\tAggregator servername: " );
		conf.append( this.getProperty( PROP_AGG_SERVERNAME ) );
		conf.append( LINE_SEPARATOR );
		
		conf.append( "\tDiscoveryService username: " );
		conf.append( this.getProperty( PROP_DS_USERNAME ) );
		conf.append( LINE_SEPARATOR );

		conf.append( "\tDiscoveryService servername: " );
		conf.append( this.getProperty( PROP_DS_SERVERNAME ) );
		conf.append( LINE_SEPARATOR );

		return conf.toString();
	}
}
