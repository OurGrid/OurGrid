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
package org.ourgrid.broker.ui.sync;

import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.config.Configuration;

public class BrokerUIMessages {

	public static final String STARTED = "OurGrid Broker was successfully started";
	
	private static final String BROKER_COMMAND_PREFIX = "broker";
	
	public static final String COMMAND_PREFIX = "broker";

	public static final String CLEAN_ALL_MSG = "Jobs were successfully cleaned";

	public static final String MG_PROPERTIES_NOT_FOUND_MSG = "File 'mg.properties' was not found at '"
			+ BrokerConfiguration.CONF_DIR + "'!";

	public static final String CREATING_NEW_MG_PROP_MSG = "Creating a new one... ";

	public static final String ERROR_COPYING_LOG_PROP_MSG = "Broker Configuration Wizard could not copy LOG properties file.";

	public static final String STARTING = "Starting Broker " + Configuration.VERSION.toString() + ". Please wait...";

	public static final String CANNOT_RECONFIGURE_BROKER = "Error. Cannot reconfigure Broker properties when it is running. Run 'broker stop' and try again";

	public static String getJobAddedMessage( int jobID ) {

		return "Job [" + jobID + "] was successfully added";
	}

	public static String getWaitForJobMessage( int jobID ) {

		return "Waiting for the Job [" + jobID + "] finish.";
	}

	public static String getJobCanceledMessage( int jodID ) {

		return "Job [" + jodID + "] was successfully canceled";
	}


	public static String getJobRemovedMessage( int jobID ) {

		return "Job [" + jobID + "] was successfully removed";
	}


	public static String getSetPeersMessage( int i ) {

		String msg;
		if ( i <= 0 ) {
			msg = "No peers were configured";
		} else if ( i == 1 ) {
			msg = "1 peer was successfully configured";
		} else {
			msg = i + " peers were successfully configured";
		}

		return msg;
	}


	public static String getInvalidJobIDMessage( String invalidID ) {

		return "Invalid Job ID paramter: " + invalidID;
	}

	
	public static String getSuccessMessage( String commandName ) {

		return UIMessages.getSuccessMessage( BROKER_COMMAND_PREFIX, commandName );
	}

	public static String getWrongUsageMessage() {
		return "Usage: broker {start|stop|addjob|canceljob|cleanjob|cleanalljobs|setpeers}";
	}
	
	
}
