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

import org.ourgrid.common.interfaces.Constants;

/**
 * Constants from Aggregator Module.
 *
 */
public interface AggregatorConstants extends Constants {
	
	public static final String QUERY_CMD_NAME = "query";
	
	public static final String MODULE_NAME = "AGGREGATOR_MODULE";

	public static final String OBJECT_NAME = "AGGREGATOR_OBJECT";

	public static final String DS_MONITOR_OBJECT_NAME = "AGGREGATOR_DS_MONITOR_OBJECT";

	public static final String GET_PEER_STATUS_PROVIDER_ACTION_NAME = "GET_COMPLETE_STATUS_ACTION";
	
	public static final String STATUS_PROVIDER_CLIENT_OBJECT_NAME = "AGGREGATOR_SP_CLIENT_OBJECT";
	
	public static final String STATUS_MONITOR_OBJECT_NAME = "AGGREGATOR_SP_OBJECT";
	
	public static final String CMMSP_CLIENT_OBJECT_NAME = "AGGREGATOR_CMMSP_CLIENT_OBJECT";
		
	public static final long GET_PEER_STATUS_PROVIDER_ACTION_DELAY = 5 * 60; //s

}
