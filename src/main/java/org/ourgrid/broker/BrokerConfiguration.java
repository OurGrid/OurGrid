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
import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.reqtrace.Req;

/**
 * Description: Broker configuration properties
 */
public class BrokerConfiguration extends Configuration {

	private static final long serialVersionUID = 1L;

	public static final String BROKER = BrokerConfiguration.class.getName();

	public static final String CONF_DIR = findConfDir();

	/** Broker property prefix */
	public static final String PREFIX = "broker.";

	
	/**
	 * Broker properties file
	 */
	public static final String PROPERTIES_FILENAME =  CONF_DIR + separator + "broker.properties";

	/**
	 * The property key for the Maximum Number of Failed Replicas to consider
	 * the Task as Failed
	 */
	public static final String PROP_MAX_FAILS = PREFIX + "maxfails";

	/** The property key for the Heuristic type */
	public static final String PROP_HEURISTIC = PREFIX + "heuristic";

	/** The property key for the Maximum Number of Replicas */
	public static final String PROP_MAX_REPLICAS = PREFIX + "maxreplicas";

	public static final String PROP_PERSISTJOBID = PREFIX + "persist.jobid";
	
	public static final String PROP_JOBCOUNTERFILEPATH = PREFIX + "persist.jobid.path";

	public static final String JOBCOUNTERFILEPATH = CONF_DIR + separator + "broker.jobid";

	public static final String PROP_NUM_OF_REPLICA_EXECUTORS = PREFIX + "replica.executors";

	public static final String PROP_MAXTHREADS_PER_REPLICA_EXECUTOR = PREFIX + "replica.executor.maxthreads";

	public static final String PROP_BROKER_LOGFILE = PREFIX + PROP_LOGFILE;
	
	public static final String PROP_LOG_PROPERTIES_FILE = PREFIX + "log.properties";
	
	/**
	 * DataDiscovery cache
	 */
	public static final String PROP_CACHE_DDFILE = PREFIX + "cache.ddfile";
	
	/**
	 * Broker log properties file
	 */
	public static final String LOG_PROPERTIES_FILENAME = "broker.log.properties";

	/**
	 * Broker log properties file destination (in .broker folder)
	 */
	public static final String LOG_PROPERTIES_FILENAME_DESTINATION = CONF_DIR + separator + LOG_PROPERTIES_FILENAME;

	/**
	 * Max number of fails of an given machine executing a given job to enter in
	 * the black list.
	 */
	public static final String PROP_MAX_BL_FAILS = PREFIX + "max.blacklist.fails";

	/**
	 * The default value to the property PROP_MAX_BL_FAILS
	 */
	public static final String DEFAULT_MAX_BL_FAILS = "1";

	/**
	 * The default value to the property PROP_MAX_FAILS
	 */
	public static final String DEFAULT_MAX_FAILS = "3";

	public static final String DEFAULT_MAX_REPLICAS = "1";
	
	public static final String DEFAULT_HEURISTIC = "workqueue";
	
	public static final String DEFAULT_PERSISTJOBID = "yes";
	
	public static final String DEFAULT_LOG_FILENAME = "broker.log";
	
	public static final String DEFAULT_NUM_OF_REPLICA_EXECUTORS = "2";
	
	public static final String PROP_PEER_USER_AT_SERVER = PREFIX + "peer.address";
	
	public static final String DEFAULT_PEER_USER_AT_SERVER = "peer-user@peer-server";
	
	/**
	 * The initial job ID value for when the job ID is not persistant
	 */
	public static final String INITIAL_JOB_ID = "1";


	/**
	 * Broker log properties file
	 */
	public static final String DEF_PROP_BROKER_LOG_FILE = "broker.log" ;

	/**
	 * Default constructor
	 */
	@Req("REQ001")
	public BrokerConfiguration() {

		super();
		setProperty( PROP_MAX_FAILS, DEFAULT_MAX_FAILS );
		setProperty( PROP_HEURISTIC, "workqueue" );
		setProperty( PROP_MAX_REPLICAS, DEFAULT_MAX_REPLICAS);
		setProperty( PROP_CACHE_DDFILE, "no" );
		setProperty( PROP_PERSISTJOBID, "yes" );
		setProperty( PROP_BROKER_LOGFILE, DEF_PROP_BROKER_LOG_FILE);
		setProperty( PROP_NUM_OF_REPLICA_EXECUTORS, "2" );
		setProperty( PROP_MAXTHREADS_PER_REPLICA_EXECUTOR, "2" );
		setProperty( PROP_LOG_PROPERTIES_FILE, LOG_PROPERTIES_FILENAME );
		setProperty( PROP_MAX_BL_FAILS, DEFAULT_MAX_BL_FAILS );
		setProperty( PROP_PEER_USER_AT_SERVER, DEFAULT_PEER_USER_AT_SERVER );

		loadPropertiesFromFile( PROPERTIES_FILENAME );
	}


	/**
	 * Returns the configuration directory.
	 * 
	 * @return The directory.
	 */
	@Req("REQ001")
	//TODO portability  
	public static String findConfDir() {

		String prop = System.getenv( "BROKERROOT" );
		if ( prop == null || prop.equals( "" ) ) {
			prop = System.getProperty( "user.home" ) + separator + ".broker";
		}

		return prop;
	}
	
	public static String findOgRoot() {

		String prop = System.getenv( "OGROOT" );
		
		return prop == null ? "." : prop;
	}


	/**
	 * Returns the name of user that was running the Broker
	 * 
	 * @return The name
	 */
	public static String getUserName() {

		return System.getProperty( "user.name" );
	}

	@Override
	@Req("REQ001")
	public String getConfDir() {

		return CONF_DIR;
	}

	@Override
	public String toString() {

		StringBuilder conf = new StringBuilder( super.toString() );

		conf.append( "\tMaximum replicas to create: " );
		conf.append( this.getProperty( BrokerConfiguration.PROP_MAX_REPLICAS ) );
		conf.append( LINE_SEPARATOR );

		conf.append( "\tMaximum replica fails: " );
		conf.append( this.getProperty( BrokerConfiguration.PROP_MAX_FAILS ) );
		conf.append( LINE_SEPARATOR );

		conf.append( "\tMaximum fails to enter blacklist: " );
		conf.append( this.getProperty( BrokerConfiguration.PROP_MAX_BL_FAILS ) );
		conf.append( LINE_SEPARATOR );

		return conf.toString();
	}

}
