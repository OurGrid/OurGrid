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

import java.io.File;

import org.ourgrid.common.interfaces.Constants;

/**
 * This interface will keep the constants used by <code>Worker</code> module.
 * 
 * @since 5.0
 */
public interface WorkerConstants extends Constants {
	
	/** ******************** GENERAL USE CONSTANTS **************************** */

	/**
	 * Name of the <tt>Playpen</tt> environment variable.
	 */
	static final String ENV_PLAYPEN = "PLAYPEN";

	/**
	 * Name of the <tt>Playpen</tt> environment variable.
	 */
	static final String ENV_STORAGE = "STORAGE";

	/**
	 * The default time (in milliseconds) that Worker will wait ( in shutdown
	 * process ) the execution <code>Threads</code> fininish before raises an
	 * exception.
	 */
	static final long DEFAULT_AWAIT_TERMINATION_TIME = 20000;

	/**
	 * The default value for the size of playpen
	 */
	static final int DEFAULT_PLAYPEN_SIZE = 0;

	/**
	 * Default value for the worker control client object name
	 */
	static final String CONTROL_CLIENT_OBJECT_NAME = "WORKER_CONTROL_CLIENT";
	
	static final String KILLER_CLIENT_NAME = "KILLER_CLIENT_NAME";
	
	static final String SYNC_KILLER_EXECUTION_MODULE_NAME = "KILLER_EXECUTION_MODULE_NAME";

	
	/**
	 * Default value for the module name
	 */
	static final String MODULE_NAME = "WORKER";
	
	static final String CLIENT_OBJECT_NAME = "WORKER_CLIENT";
	
	static final String WORKER_SYSINFO_COLLECTOR = "SYSINFO_COLLECTOR"; 
	
	static final String WORKER_EXECUTION_SERVICE_MODULE_NAME = "EXECUTION_SERVICE_MODULE";
	
	static final String WORKER_EXECUTION_SERVICE_OBJECT_NAME = "WORKER_EXECUTION_SERVICE";
	
	static final String LOCAL_WORKER_MANAGEMENT = "LOCAL_WORKER_MANAGEMENT";
	
	static final String REMOTE_WORKER_MANAGEMENT = "REMOTE_WORKER_MANAGEMENT";
	
	static final String WORKER = "WORKER";
	
	static final String WORKER_STATUS_PROVIDER = "WORKER_STATUS_PROVIDER";

	static final String WORKER_EXECUTION_CLIENT = "WORKER_EXECUTION_CLIENT";
	
	static final String WORKER_ACCOUNTING_REPORTER = "WORKER_ACCOUNTING_REPORTER";
	
	/** **************** ACTION CONSTANTS ******************** */
	static final String EXECUTOR_ACTION_NAME = "EXECUTE_COMMAND_ACTION_NAME";
	static final String GET_FILES_ACTION_NAME = "GET_FILES_COMMAND_ACTION_NAME";
	static final String GET_FILE_INFO_ACTION_NAME = "GET_FILE_INFO_COMMAND_ACTION_NAME";
	static final String REMOTE_EXECUTE_ACTION_NAME = "REMOTE_EXECUTE_COMMAND_ACTION_NAME";
	static final String REPORT_WORK_ACCOUNTING_ACTION_NAME = "REPORT_WORK_ACCOUNTING_COMMAND_ACTION_NAME";
	static final String REPORT_WORKER_SPEC_ACTION_NAME = "REPORT_WORK_SPEC_COMMAND_ACTION_NAME";
	static final String SYS_INFO_GATHERING_ACTION_NAME = "SYS_INFO_GATHERING_COMMAND_ACTION_NAME";
	
	
	/** **************** CONFIGURATION CONSTANTS ******************** */

	/** ******* PROPERTIES DEFINITIONS ******************* */

	public static final String PREFIX = "worker.";

	static final String ROOT_DIR = System.getenv("OGROOT");

	static final String PROPERTIES_FILENAME = ROOT_DIR + File.separator	+ "worker.properties";

	static final String PROP_STORAGE_DIR = PREFIX + "storagedir";

	static final String PROP_STORAGE_SIZE = PREFIX + "storagesize";

	static final String PROP_STORAGE_SHARED = PREFIX + "storageshared";
	
	static final String PROP_PLAYPEN_ROOT = PREFIX + "playpenroot";

	static final String PROP_PLAYPEN_SIZE = PREFIX + "playpensize";

	static final String PROP_IDLENESS_DETECTOR = PREFIX + "idlenessdetector";

	static final String PROP_IDLENESS_TIME = PREFIX + "idlenesstime";
	
	static final String PROP_WORKER_PEER_PUBLIC_KEY = PREFIX + "peer.publickey";

	/** ******* DEFAULT WORKER.PROPERTIES VALUES ********* */

	static final String DEF_PROP_STORAGE_DIR = ".mgstorage";

	static final String DEF_PROP_STORAGE_SIZE = "0";

	static final String DEF_PROP_STORAGE_SHARED = "no";

	static final String DEF_PROP_PLAYPEN_ROOT = File.separator + "tmp";

	static final String DEF_PROP_PLAYPEN_SIZE = "0";

	static final String DEF_PROP_LOGFILE = "worker.log";

	static final String DEF_PROP_IDLENESS_DETECTOR = "no";

	static final String DEF_PROP_IDLENESS_TIME = "1200"; // 20 minutes

	static final String DEF_PROP_LOG_PROPERTIES_FILE = "worker.log.properties";

	/** **************** SPEC DEFINITION CONSTANTS ******************** */

	static final String ATT_TYPE = "type";

	static final String ATT_PLAYPEN_ROOT = "playpenroot";

	static final String ATT_PLAYPEN_SIZE = "playpensize";

	static final String ATT_STORAGE_DIR = "storagedir";

	static final String ATT_STORAGE_SIZE = "storagesize";

	static final String ATT_OS = "os";

	static final String ATT_MEM = "mem";

	static final String ATT_SITE = "site";

	static final String ATT_STORAGE_SHARED = "storageshared";

	static final String TYPE_UA_LINUX = "ualinux";

	static final String TYPE_UA_WINDOWS = "uawindows";

	static final String TYPE_UA_SWAN = "uaswan";

	static final String OS_LINUX = "linux";

	static final String OS_WINDOWS = "windows";

	static final String OS_LINUX_XEN = "/proc/xen";

	static final String STORED_FILES_ENV_VAR = "OG_TASK_STORED_FILES";

	static final int QUERY_FREQ = 120000;

	static final String FILE_PERMISSIONS = "700";
	
	/**          Commands            **/
	static final String PAUSE_CMD_NAME = "pause";
	
	static final String RESUME_CMD_NAME = "resume";

	static final String IDLENESSDETECTOR_ACTION_NAME = "IDLENESSDETECTOR_ACTION";

	static final long IDLENESSDETECTOR_VERIFICATION_TIME = 30000; //ms
	
	static final long REPORT_WORK_ACCOUNTING_TIME = 120; //s

	static final String IDLENESS_DETECTOR_WORKER_CONTROL_CLIENT = "IDLENESS_DETECTOR_WORKER_CONTROL_CLIENT";

	static final String CERTIFICATE_FILE_NAME = ".usercert";
}