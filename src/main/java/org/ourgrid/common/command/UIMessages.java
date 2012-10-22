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
package org.ourgrid.common.command;

import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import org.ourgrid.aggregator.ui.sync.AggregatorUIMessages;
import org.ourgrid.discoveryservice.ui.sync.DiscoveryServiceUIMessages;
import org.ourgrid.peer.ui.sync.PeerUIMessages;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.ui.sync.WorkerUIMessages;

/**
 * Class that maintains the UI error messages.
 * 
 * @see BrokerUIMessages
 * @see PeerUIMessages
 * @see WorkerUIMessages
 * @see DiscoveryServiceUIMessages
 * @see AggregatorUIMessages
 * @see WebStatusUIMessages
 */
public class UIMessages {

	public static final String INVALID_PARAMETERS_MSG = "Invalid command parameters";
	
	public static final String INVALID_LOGIN_FORMAT_MSG = "Type user@server";

	public static final String ALREADY_RUNNING_MSG = "Component is already running";

	public static final String NOT_RUNNING_MSG = "Component is not running";

	@Req({"REQ001", "REQ003"})
	public static String getErrorMessage( Exception e, String component, String commandName ) {

		return "Could not execute command '" + component + " " + commandName + "'" + LINE_SEPARATOR + "Cause: "
				+ e.getMessage() + LINE_SEPARATOR;
	}


	public static String getSuccessMessage( String componentName, String commandName ) {

		return "Command '" + componentName + " " + commandName + "' was successfully executed";
	}


	public static String getLauchingMessage( String componentName, String commandName ) {

		return "Launching command '" + componentName + " " + commandName + "'. Please wait...";
	}


	/**
	 * Builds a String with many line separators
	 * 
	 * @param amount minimum considered is one
	 * @return a String with a least one line separator
	 */
	public static String getLineSeparators( int amount ) {

		StringBuilder separators = new StringBuilder().append( LINE_SEPARATOR );
		for ( int i = 0; i < amount - 1; i++ ) {
			separators.append( LINE_SEPARATOR );
		}
		return separators.toString();
	}
}
