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
package org.ourgrid.peer.ui.sync;

import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import org.ourgrid.common.command.UIMessages;
import org.ourgrid.reqtrace.Req;

public class PeerUIMessages {

	public static final String STARTED = "OurGrid Peer was successfully started";

	private static final String PEER_COMMAND_PREFIX = "peer";


	@Req("REQ010")
	public static String getPeerSetWorkersMessage( int i ) {

		String msg;
		if ( i <= 0 ) {
			msg = "No workers were configured";
		} else if ( i == 1 ) {
			msg = "1 worker was successfully configured";
		} else {
			msg = i + " workers were successfully configured";
		}

		return msg;
	}


	public static String getSuccessMessage( String commandName ) {

		return UIMessages.getSuccessMessage( PEER_COMMAND_PREFIX, commandName );
	}


	public static String getDefaultSDFErrorMessage( Exception cause ) {

		return "Default Site Description File (SDF) could not be loaded" + LINE_SEPARATOR + "Cause: "
				+ cause.getMessage();
	}
}
