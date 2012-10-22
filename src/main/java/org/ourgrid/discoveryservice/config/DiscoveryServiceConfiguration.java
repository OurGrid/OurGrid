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
package org.ourgrid.discoveryservice.config;

import static java.io.File.separator;

import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DiscoveryServiceConfiguration extends Configuration {

	private static final long serialVersionUID = 1L;

	public static final String DISCOVERY_SERVICE = DiscoveryServiceConfiguration.class.getName();
	
	public static final String PREFIX = "ds.";

	public static final String CONF_DIR = findConfDir();

	public static final String PROPERTIES_FILENAME = CONF_DIR + separator + "ds.properties";

	public static final String PROP_DS_NETWORK = PREFIX + "network";
	
	public static final String PROP_DS_OG_PROPERTIES_FILE = PREFIX + PROP_LOG_PROPERTIES_FILE;
	
	public static final String DEF_PROP_LOG_PROPERTIES_FILE = "ds.log.properties" ;

	public static final String PROP_OVERLOAD_THRESHOLD = PREFIX + "overload.threshold";

	public static final Object DEF_OVERLOAD_THRESHOLD = "1000";

	public static final String PROP_MAX_RESPONSE_SIZE = PREFIX + "max.reponse.size";
	
	public static final String DEF_MAX_RESPONSE_SIZE = "10";
	
	
	/**
	 * Creates a DiscoveryService configuration, defining the default values and overwriting
	 * it with the values of ds.properties 
	 */
	@Req("REQ010")
	public DiscoveryServiceConfiguration() {
		setProperty(PROP_DS_OG_PROPERTIES_FILE, DEF_PROP_LOG_PROPERTIES_FILE);
		loadPropertiesFromFile( PROPERTIES_FILENAME );
	}


	/**
	 * Returns the configuration directory.
	 * 
	 * @return The directory.
	 */
	@Req("REQ010")
	public static String findConfDir() {
		String property = System.getenv( "OGROOT" );
		return property == null ? "." : property;
	}

	
	/**
	 * Requirement 502
	 */
	public static List<ServiceID> parseNetwork(ServiceManager serviceManager) {
		
		String networkStr = serviceManager.getContainerContext().getProperty(PROP_DS_NETWORK);
		
		if (networkStr == null) {
			return new LinkedList<ServiceID>();
		}
		
		return StringUtil.splitDiscoveryServiceAddresses(networkStr);
	}

	@Override
	@Req("REQ010")
	public String getConfDir() {
		
		return CONF_DIR;
	}


	@Override
	public String getLogPath() {
		String logfile = getProperty( PROP_LOGFILE );
		if ( logfile == null ) {
			logfile = getConfDir() + "/log/ds.log";
		}
		return logfile;
	}

}
