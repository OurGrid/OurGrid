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
package org.ourgrid.aggregator.business.requester;

/**
 * Enumeration that maintains some constants of this Module.
 */
public enum AggregatorRequestConstants {
	
	START_AGGREGATOR ("START_AGGREGATOR"),
	STOP_AGGREGATOR ("STOP_AGGREGATOR"),
	COMMUNITY_STATUS_PROVIDER_IS_DOWN ("COMMUNITY_STATUS_PROVIDER_IS_DOWN"),
	HERE_IS_COMPLETE_HISTORY_STATUS ("HERE_IS_COMPLETE_HISTORY_STATUS"),
	HERE_IS_STATUS_PROVIDER_LIST ("HERE_IS_STATUS_PROVIDER_LIST"),
	HERE_IS_PEER_STATUS_CHANGE_HISTORY ("HERE_IS_PEER_STATUS_CHANGE_HISTORY"),
	COMMUNITY_STATUS_PROVIDER_IS_UP ("COMMUNITY_STATUS_PROVIDER_IS_UP"),
	PEER_STATUS_PROVIDER_IS_DOWN ("PEER_STATUS_PROVIDER_IS_DOWN"),
	PEER_STATUS_PROVIDER_IS_UP ("PEER_STATUS_PROVIDER_IS_UP"),
	GET_PEER_STATUS_PROVIDER ("GET_PEER_STATUS_PROVIDER"),
	GET_COMPLETE_STATUS ("GET_COMPLETE_STATUS");
	
	private String name;
    
	AggregatorRequestConstants (String name) {
		this.name = name;
    }
    
    public String getString() {
    	return name;
    }	

}
