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

import org.ourgrid.common.interfaces.Constants;

public interface BrokerConstants extends Constants{
	
	public static final String MODULE_NAME = "BROKER";

	public static final String CONTROL_OBJECT_NAME = "BROKER_CONTROL";
	
	public static final String STATUS_PROVIDER_OBJECT_NAME = "BROKER_STATUS_PROVIDER";

	public static final String LOCAL_WORKER_PROVIDER_CLIENT = "LOCAL_WORKER_PROVIDER_CLIENT";
	
	public static final String WORKER_CLIENT = "WORKER_CLIENT";
	
	public static final String SCHEDULER_ACTION_NAME = "SCHEDULER_ACTION_NAME";

	public static final String SCHEDULER_OBJECT_NAME = "SCHEDULER_OBJECT_NAME";
	
	public static final String JOB_ENDED_INTERESTED = "JOB_ENDED_INTERESTED";
	
	static final String WORKER_IS_UNAVAILABLE_ACTION_NAME = "WORKER_IS_UNAVAILABLE_COMMAND_ACTION_NAME";
	static final String WORKER_IS_READY_ACTION_NAME = "WORKER_IS_READY_COMMAND_ACTION_NAME";
	static final String HERE_IS_WORKER_SPEC_ACTION_NAME = "HERE_IS_WORKER_SPEC_ACTION_NAME";
	static final String HERE_IS_EXECUTION_RESULT_ACTION_NAME = "HERE_IS_EXECUTION_COMMAND_RESULT_ACTION_NAME";
	static final String ERROR_OCURRED_ACTION_NAME = "ERROR_OCURRED_COMMAND_ACTION_NAME";
	static final String HERE_IS_FILE_INFO_ACTION_NAME = "HERE_IS_FILE_INFO_COMMAND_ACTION_NAME";
	
	public static final String CLEAN_CMD_NAME = "clean";

	public static final String CANCELJOB_CMD_NAME = "canceljob";

	public static final String ADDJOB_CMD_NAME = "addjob";

	public static final String WAIT_FOR_JOB_CMD_NAME = "waitforjob";
	
	public static final String JOB_STATUS_CMD_NAME = "jobstatus";
	
	public static final String TEMP_DOWNLOAD_PATH = "temp";
	
	/* in seconds */
	public static final int SCHEDULER_INTERVAL = 10;

}
