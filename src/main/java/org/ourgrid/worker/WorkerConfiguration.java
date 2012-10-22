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
package org.ourgrid.worker;

import static java.io.File.separator;
import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.context.ModuleContext;

public class WorkerConfiguration extends Configuration {

	private static final long serialVersionUID = 1L;

	public static final String WORKER = WorkerConfiguration.class.getName();
	
	public static final String PREFIX = "worker.";

	private static final String CONF_DIR = findConfDir();

	public static final String PROPERTIES_FILENAME = CONF_DIR + separator + "worker.properties";
	
	public static final String PROP_SPEC_FILENAME = PREFIX + "spec.file";
	
	public static final String PROP_XIDLETIME64_PATH = PREFIX + "xidletime64.path";
	
	public static final String PROP_XIDLETIME32_PATH = PREFIX + "xidletime32.path";
	
	public static final String PROP_LOG_PROPERTIES_FILE = PREFIX + "log.properties";

	public static final String PROP_PEER_ADDRESS = PREFIX + "peer.address";
	
	public static final String PROP_STORAGE_DIR = PREFIX + "storagedir";

	public static final String PROP_STORAGE_SIZE = PREFIX + "storagesize";

	public static final String PROP_PLAYPEN_ROOT = PREFIX + "playpenroot";

	public static final String PROP_PLAYPEN_SIZE = PREFIX + "playpensize";

	public static final String PROP_IDLENESS_DETECTOR = PREFIX + "idlenessdetector";

	public static final String PROP_IDLENESS_TIME = PREFIX + "idlenesstime";
	
	public static final String PROP_WORKER_LOGFILE = PREFIX + PROP_LOGFILE;
	
	public static final String PROP_WORKER_SPEC_REPORT = PREFIX + "spec.report";
	
	public static final String PROP_WORKER_SPEC_REPORT_TIME = PREFIX + "spec.reporttime";
	
	public static final String PROP_USE_IDLENESS_SCHEDULE = PREFIX + "use.idlenessdetector.schedule";

	public static final String PROP_IDLENESS_SCHEDULE_TIME  = PREFIX + "idlenessdetector.scheduletime";

	public static final String ATT_PLAYPEN_ROOT = PREFIX + "playpenroot";

	public static final String ATT_PLAYPEN_SIZE = PREFIX + "playpensize";

	public static final String ATT_STORAGE_DIR = PREFIX + "storagedir";

	public static final String ATT_STORAGE_SIZE = PREFIX + "storagesize";

	public static final String ATT_OS = PREFIX + "os";

	public static final String ATT_MEM = PREFIX + "mem";

	public static final String ATT_SITE = PREFIX + "site";

	public static final String ATT_STORAGE_SHARED = PREFIX + "storageshared";

	public static final String PROP_EXECUTOR_TYPE = PREFIX + "executor";
	
	public static final String OS_LINUX = "linux";

	public static final String OS_WINDOWS = "windows";

	public static final String OS_LINUX_XEN = "/proc/xen";

	/**
	 * worker.properties default values.
	 */
	
	static final String DEF_PROP_XIDLETIME64_PATH = "lib" +
			separator + "libxidletime-1.0_amd64.so";
	
	static final String DEF_PROP_XIDLETIME32_PATH = "lib" +
			separator + "libxidletime-1.0_i386.so";
	
	static final String DEF_PROP_SPEC_FILENAME = "worker.spec.properties";

	static final String DEF_PROP_STORAGE_DIR = ".brokerstorage";

	static final String DEF_PROP_STORAGE_SIZE = "0";

	static final String DEF_PROP_PLAYPEN_ROOT = separator + "tmp";

	static final String DEF_PROP_PLAYPEN_SIZE = "0";

	static final String DEF_PROP_LOGFILE = "worker.log";

	public static final String DEF_WORKER_SPEC_REPORT_TIME = "120"; //seconds
	
	public static final int DEF_SYS_INFO_GATHERING_TIME = 5; //seconds
	
	public static final String DEF_PROP_IDLENESS_DETECTOR = "no";
	
	public static final String DEF_WORKER_SPEC_REPORT = "no";

	public static final String DEF_PROP_IDLENESS_TIME = "1200"; // 20 minutes
	
	public static final String DEF_PROP_USE_IDLENESS_SCHEDULE = "no";
	
	public static final String DEF_PROP_IDLENESS_SCHEDULE_TIME = "";

	static final String DEF_PROP_LOG_PROPERTIES_FILE = "worker.log.properties";

	public static final String ATT_OUTPUTFILES = "OUTPUTFILES";
	
	public static final String ATT_INPUTFILES = "INPUTFILES";
	
	public static final String SEPARATOR_CHAR = ";";

	public static final String DEF_PEER_ADDRESS = "peer-lsd@xmpp.ourgrid.org";

	/**
	 * Default constructor.
	 */
    @Req("REQ003")
	public WorkerConfiguration() {

    	setProperty( PROP_SPEC_FILENAME, DEF_PROP_SPEC_FILENAME );
    	setProperty( PROP_XIDLETIME64_PATH, DEF_PROP_XIDLETIME64_PATH );
    	setProperty( PROP_XIDLETIME32_PATH, DEF_PROP_XIDLETIME32_PATH );
		setProperty( PROP_STORAGE_DIR, DEF_PROP_STORAGE_DIR );
		setProperty( PROP_STORAGE_SIZE, DEF_PROP_STORAGE_SIZE );
		setProperty( PROP_PLAYPEN_ROOT, DEF_PROP_PLAYPEN_ROOT );
		setProperty( PROP_PLAYPEN_SIZE, DEF_PROP_PLAYPEN_SIZE );
		setProperty( PROP_WORKER_LOGFILE, DEF_PROP_LOGFILE );
		setProperty( PROP_LOG_PROPERTIES_FILE, DEF_PROP_LOG_PROPERTIES_FILE );
		setProperty( PROP_IDLENESS_DETECTOR, DEF_PROP_IDLENESS_DETECTOR );
		setProperty( PROP_IDLENESS_TIME, DEF_PROP_IDLENESS_TIME );

		loadPropertiesFromFile( PROPERTIES_FILENAME );
	}

	@Override
	public String getConfDir() {

		return CONF_DIR;
	}

	@Req("REQ003")
	public static String findConfDir() {
		String env = System.getenv( "OGROOT" );
		if (env != null) {
			return env;
		}
		
		String prop = System.getProperty( "OGROOT" );
		if (prop != null) {
			return prop;
		}
		
		return ".";
	}

	@Override
	public String toString() {

		StringBuilder conf = new StringBuilder( super.toString() );

		appendConfiguration(conf, "\tIdleness detection enabled: ", 
				this.getProperty( WorkerConfiguration.PROP_IDLENESS_DETECTOR ));
		
		appendConfiguration(conf, "\tIdleness detection time: ", 
				this.getProperty( WorkerConfiguration.PROP_IDLENESS_TIME ));
		
		appendConfiguration(conf, "\tPlaypen root directory: ", 
				this.getProperty( WorkerConfiguration.PROP_PLAYPEN_ROOT ));
		
		appendConfiguration(conf, "\tStorage directory: ", 
				this.getProperty( WorkerConfiguration.PROP_STORAGE_DIR ));

		return conf.toString();
	}
	
	public static String toString(ModuleContext context) {
		
		StringBuilder conf = new StringBuilder();
		
		appendConfiguration(conf, "\tVersion: ", 
				Configuration.VERSION);

		appendConfiguration(conf, "\tConfiguration directory: ", 
				context.getProperty( ModuleProperties.PROP_CONFDIR ));

		conf.append( context.toString() );

		appendConfiguration(conf, "\tIdleness detection enabled: ", 
				context.getProperty( WorkerConfiguration.PROP_IDLENESS_DETECTOR ));
		
		appendConfiguration(conf, "\tIdleness detection time: ", 
				context.getProperty( WorkerConfiguration.PROP_IDLENESS_TIME ));
		
		appendConfiguration(conf, "\tPlaypen root directory: ", 
				context.getProperty( WorkerConfiguration.PROP_PLAYPEN_ROOT ));
		
		appendConfiguration(conf, "\tStorage directory: ", 
				context.getProperty( WorkerConfiguration.PROP_STORAGE_DIR ));

		return conf.toString();
		
	}
	
	private static void appendConfiguration(StringBuilder configurations, String configurationName, String configurationValue){
		configurations.append( configurationName )
							.append(configurationValue)
							.append( LINE_SEPARATOR );
	}
	
	private static void appendConfiguration(StringBuilder configurations, String configurationName, Object configurationValue){
		appendConfiguration(configurations, configurationName, String.valueOf(configurationValue));
	}
}
