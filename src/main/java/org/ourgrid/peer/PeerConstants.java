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

import org.ourgrid.common.interfaces.Constants;

public interface PeerConstants extends Constants {
	
	public static String LOCAL_WORKER_PROVIDER = "LOCAL_WORKERPROVIDER";
	
	public static String REMOTE_WORKER_PROVIDER = "REMOTE_WORKERPROVIDER";

	public static final String DS_INTERESTED = "DS_INTERESTED";
	
	public static final String DS_CLIENT = "DS_CLIENT_RECEIVER";
	
	public static final String MODULE_NAME = "PEER";

	public static final String PEERMANAGER_OBJECT_NAME = "PEER";

	public static final String WORKER_MANAGEMENT_CLIENT_OBJECT_NAME = "WORKER_MANAGEMENT_CLIENT";
	
	public static final String REMOTE_WORKER_MANAGEMENT_CLIENT = "REMOTE_WORKER_MANAGEMENT_CLIENT";
	
	public static final String STATUS_PROVIDER_CLIENT_OBJECT_NAME = "PEER_STATUS_PROVIDER_CLIENT";

	public static final String ADD_BROKER_CMD_NAME = "addbroker";
	
	public static final String REMOVE_BROKER_CMD_NAME = "removebroker";
	
	public static final String REMOVE_WORKER_CMD_NAME = "removeworker";

	public static final String QUERY_CMD_NAME = "query";
	
	public static final String REMOTE_WORKER_PROVIDER_CLIENT = "PEER_WORKER_PROVIDER_CLIENT";

	public static final String DS_ACTION_NAME = "DS_ACTION_NAME";
	
	public static final String DELAYED_DS_INTEREST_ACTION_NAME = "DELAYED_DS_INTEREST_ACTION_NAME";
	
	public static final String REQUEST_WORKERS_ACTION_NAME = "REQUEST_WORKERS_ACTION_NAME";

	public static final String SAVE_ACCOUNTING_ACTION_NAME = "SAVE_ACCOUNTING_ACTION_NAME";
	
	public static final String UPDATE_PEER_UPTIME_ACTION_NAME = "UPDATE_UPTIME_ACTION_NAME";
	
	public static final String INVOKE_GARBAGE_COLLECTOR_ACTION_NAME = "INVOKE_GARBAGE_COLLECTOR_ACTION_NAME";
	
	public static final int STATUS_UPDATE_DELAY = 10;
	
	//in seconds
	public static final int UPDATE_UPTIME_DELAY = 60;
	
	//in seconds
	public static final int INVOKE_GARBAGE_COLLECTOR_DELAY = 300;

	//in milliseconds
	public static final long AGGREGATOR_DATA_INTERVAL = 1000 * 60 * 60 * 24;
	
}
