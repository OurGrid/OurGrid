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

import static java.io.File.separator;
import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

//FIXME: Remove this class
public class PeerConfiguration extends Configuration {

	private static final long serialVersionUID = 1L;

	public static final String PEER = PeerConfiguration.class.getName();
	
	public static final String PREFIX = "peer.";

	public static final String CONF_DIR = findConfDir();

	public static final String PROPERTIES_FILENAME = CONF_DIR + separator + "peer.properties";
	
	public static final String PROP_ONDEMAND_PEER = PREFIX + "register.ondemand";
	
	public static final String TRUSTY_COMMUNITIES_FILENAME = CONF_DIR + separator + "trusts.xml";
	
	public static final String USER_PASSWORD = "password";
	
	public static final String USER_PUBLIC_KEY = "publickey";
	
	public static final String PROP_LABEL = PREFIX + "label";

	public static final String PROP_RANKINGFILE = PREFIX + "rankingfile";

	public static final String PROP_JOIN_COMMUNITY = PREFIX + "joincommunity";

	public static final String PROP_DS_NETWORK = PREFIX + "ds.network";

	public static final String PROP_DS_UPDATE_INTERVAL = PREFIX + "ds.update";
	
	public static final String PROP_DS_REQUEST_SIZE = PREFIX + "ds.requestsize";

	public static final String DEF_DS_REQUEST_SIZE = "500";
	
	public static final String PROP_EMAIL = PREFIX + "email";

	public static final String PROP_DESCRIPTION = PREFIX + "description";

	public static final String PROP_SAVING_INTERVAL = PREFIX + "interval.saving";

	public static final String PROP_LATITUDE = PREFIX + "latitude";

	public static final String PROP_LONGITUDE = PREFIX + "longitude";
	
	public static final String PROP_REPEAT_REQUEST_DELAY = PREFIX + "repeatrequest";

	public static final int QUERY_FREQ = 120000;

	public static final String PROP_ACC_VALUE_CPUUNIT = PREFIX + "accounting.cpuunit";

	public static final String PROP_ACC_VALUE_DATAUNIT = PREFIX + "accounting.dataunit";

	public static final String USERS_PROPERTIES_SEPARATOR = "$";
	
	public static final String DEF_PROP_ONDEMAND_PEER = "no";

	public static final Object DEF_PROP_JOIN_COMMUNITY = "no";
	
	public static final String DEF_DS_NETWORK = "lsd-ds@xmpp.ourgrid.org";

	public static final String PROP_DELAY_OVERLOADED_DS_INTEREST = PREFIX + "ds.overloaded.retrydelay";
	
	public static final String DEF_DELAY_OVERLOADED_DS_INTEREST = "300";
	
	public static final String PROP_REQUESTING_CACERTIFICATE_PATH = PREFIX + "requesting.cacertificate.path";
	
	public static final String PROP_RECEIVING_CACERTIFICATE_PATH = PREFIX + "receiving.cacertificate.path";
	
	public static final String PROP_USE_VOMS = PREFIX + "usevomsauth";
	
	public static final String PROP_VOMS_URL = PREFIX + "vomsurl";
	
	//JDL Code
	public static final String PROP_TAGS_FILE_PATH = PREFIX + "tagsfilepath";

	public static final String PROP_VOLUNTARY_PEER = PREFIX + "voluntary";
	
	public static final String DEF_VOLUNTARY_PEER = "no";

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

	@Override
	@Req("REQ010")
	public String getConfDir() {

		return CONF_DIR;
	}

	@Override
	public String toString() {

		StringBuilder conf = new StringBuilder( super.toString() );

		conf.append( "\tLabel: " );
		conf.append( this.getProperty( PeerConfiguration.PROP_LABEL ) );
		conf.append( LINE_SEPARATOR );

		conf.append( "\tJoin community: " );
		conf.append( this.getProperty( PeerConfiguration.PROP_JOIN_COMMUNITY ) );
		conf.append( LINE_SEPARATOR );

		return conf.toString();
	}

	public static List<ServiceID> parseNetwork(ServiceManager serviceManager) {
		List<ServiceID> dsIDs = new LinkedList<ServiceID>();
		
		String networkStr = serviceManager.getContainerContext().getProperty(PROP_DS_NETWORK);
		
		if (networkStr == null) {
			return dsIDs;
		}
		
		return StringUtil.splitDiscoveryServiceAddresses(networkStr);
	}

	public static void persistNetwork(Set<String> discoveryServicesAddresses, ModuleContext context) throws IOException {
		Properties props = new Properties();
		
		for (Entry<String, String> entry : context.getProperties().entrySet()) {
			props.put(entry.getKey(), entry.getValue());
		}
		
		props.put(PROP_DS_NETWORK, StringUtil.concatAddresses(discoveryServicesAddresses));
		FileOutputStream out = new FileOutputStream(PROPERTIES_FILENAME);
		
		props.store(out, null);
		
		out.flush();
		out.close();
	}
}
