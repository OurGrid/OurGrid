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
package org.ourgrid.broker;

import static java.io.File.separator;

import java.util.Map;

import org.ourgrid.common.OurGridContextFactory;

import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.context.ContextParser;

/**
 *
 */
public class BrokerComponentContextFactory extends OurGridContextFactory {
	
//	public static final String CONF_DIR = findConfDir();
	
	public BrokerComponentContextFactory(ContextParser parser) {
		super(parser);
	}

	@Override
	public Map<Object, Object> getDefaultProperties() {
		Map<Object, Object> properties = super.getDefaultProperties();
		
		properties.put( ModuleProperties.PROP_CONFDIR, findConfDir());
		
		properties.put( BrokerConfiguration.PROP_MAX_FAILS, 
				BrokerConfiguration.DEFAULT_MAX_FAILS );
		properties.put( BrokerConfiguration.PROP_HEURISTIC, 
				BrokerConfiguration.DEFAULT_HEURISTIC );
		properties.put( BrokerConfiguration.PROP_MAX_REPLICAS, 
				BrokerConfiguration.DEFAULT_MAX_REPLICAS );
		properties.put( BrokerConfiguration.PROP_PERSISTJOBID, 
				BrokerConfiguration.DEFAULT_PERSISTJOBID );
		properties.put( BrokerConfiguration.PROP_NUM_OF_REPLICA_EXECUTORS, 
				BrokerConfiguration.DEFAULT_NUM_OF_REPLICA_EXECUTORS );
		properties.put( BrokerConfiguration.PROP_MAX_BL_FAILS, 
				BrokerConfiguration.DEFAULT_MAX_BL_FAILS );
		properties.put( BrokerConfiguration.PROP_JOBCOUNTERFILEPATH, 
				BrokerConfiguration.JOBCOUNTERFILEPATH );
		properties.put( BrokerConfiguration.PROP_PEER_USER_AT_SERVER, 
				BrokerConfiguration.DEFAULT_PEER_USER_AT_SERVER );
		return properties;
	}
	
	/**
	 * Returns the configuration directory.
	 * 
	 * @return The directory.
	 */
	protected String findConfDir() {

		return System.getProperty( "user.home" ) + separator + ".broker";
	}
	
}
