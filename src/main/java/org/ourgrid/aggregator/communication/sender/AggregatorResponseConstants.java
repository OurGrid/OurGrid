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
package org.ourgrid.aggregator.communication.sender;

import org.ourgrid.common.internal.OurGridResponseConstants;

/**
 * Interface that maintains some constants of this Module.
 *
 */
public interface AggregatorResponseConstants extends OurGridResponseConstants {

	public static final String SET_ADVERTACTION_FUTURE = "SET_ADVERTACTION_FUTURE";
	public static final String GET_COMPLETE_STATUS = "GET_COMPLETE_STATUS";
	public static final String CANCEL_FUTURE = "CANCEL_FUTURE";
	public static final String GET_PEER_STATUS_PROVIDER_REPEATED_ACTION = "GET_PEER_STATUS_PROVIDER_REPEATED_ACTION";
	public static final String HERE_IS_COMPLETE_STATUS = "HERE_IS_COMPLETE_STATUS";;
	
}
