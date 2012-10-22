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
package org.ourgrid.peer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.ourgrid.common.OurGridContextFactory;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.context.ContextParser;

public class PeerComponentContextFactory extends OurGridContextFactory {

	private final String CONF_DIR = findConfDir();
	
	public PeerComponentContextFactory(ContextParser parser) {
		super(parser);
	}
	
	@Override
	public Map<Object, Object> getDefaultProperties() {
		
		Map<Object, Object> properties = super.getDefaultProperties();
		properties.put( ModuleProperties.PROP_CONFDIR, CONF_DIR);
		
		properties.put( PeerConfiguration.PROP_LABEL, getHostname() );
		properties.put( PeerConfiguration.PROP_RANKINGFILE, CONF_DIR + File.separator + "rankings.dat" );

		properties.put( PeerConfiguration.PROP_JOIN_COMMUNITY, "no" );
		properties.put( PeerConfiguration.PROP_USE_VOMS, "no" );
		
		properties.put( PeerConfiguration.PROP_VOMS_URL, "voms.eela.ufrj.br:8443/voms/oper.vo.eu-eela.eu" );
		
		properties.put( PeerConfiguration.PROP_EMAIL, "no e-mail available" );
		properties.put( PeerConfiguration.PROP_DESCRIPTION, "no description available" );
		properties.put( PeerConfiguration.PROP_DS_NETWORK, PeerConfiguration.DEF_DS_NETWORK );
		properties.put( PeerConfiguration.PROP_DS_REQUEST_SIZE, PeerConfiguration.DEF_DS_REQUEST_SIZE);
		properties.put( PeerConfiguration.PROP_DELAY_OVERLOADED_DS_INTEREST, PeerConfiguration.DEF_DELAY_OVERLOADED_DS_INTEREST);
		properties.put( PeerConfiguration.PROP_ONDEMAND_PEER, PeerConfiguration.DEF_PROP_ONDEMAND_PEER);
		properties.put( PeerConfiguration.PROP_VOLUNTARY_PEER, PeerConfiguration.DEF_VOLUNTARY_PEER);
		
		properties.put( PeerConfiguration.PROP_REPEAT_REQUEST_DELAY, "120");//seconds

		// times in milliseconds
		properties.put( PeerConfiguration.PROP_SAVING_INTERVAL, "1800000" ); // 30 minutes
		properties.put( PeerConfiguration.PROP_LATITUDE, "0" );
		properties.put( PeerConfiguration.PROP_LONGITUDE, "0" );

		// accounting values
		properties.put( PeerConfiguration.PROP_ACC_VALUE_CPUUNIT, "1" );
		properties.put( PeerConfiguration.PROP_ACC_VALUE_DATAUNIT, "1" );
		
		properties.put( PeerConfiguration.PROP_DS_UPDATE_INTERVAL , "60");
		
		return properties;
	}
	
	
	/**
	 * @return local host name.
	 */
	public final String getHostname() {

		String hostname = "localhost";

		try {
			hostname = InetAddress.getLocalHost().getCanonicalHostName();
		} catch ( UnknownHostException e ) {
			// Using default peer name.
		}

		return hostname;

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
