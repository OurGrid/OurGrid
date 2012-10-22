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
package org.ourgrid.broker.ui.sync.command;

import org.ourgrid.broker.ui.sync.BrokerSyncApplicationClient;
import org.ourgrid.broker.ui.sync.BrokerUIMessages;
import org.ourgrid.common.command.UIMessages;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

public class BrokerCleanCommand extends AbstractCommand<BrokerSyncApplicationClient> {

	public BrokerCleanCommand( BrokerSyncApplicationClient componentClient ) {
		super(componentClient);
	}

	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			if ( params.length == 0 ) {
				getComponentClient().cleanAllFinishedJobs();
				System.out.println( BrokerUIMessages.CLEAN_ALL_MSG );
			} else {
				int jobID;
				try {
					jobID = Integer.parseInt( params[0] );
				} catch ( NumberFormatException e ) {
					throw new IllegalArgumentException( BrokerUIMessages.getInvalidJobIDMessage( params[0] ), e );
				}
				getComponentClient().cleanFinishedJob( jobID );
				System.out.println( BrokerUIMessages.getJobRemovedMessage( jobID ) );
			}
		} else {
			printNotStartedMessage();
		}
	}

	protected void validateParams(String[] params) throws Exception {
		if ( (params == null) || (params.length > 1) ) {
			throw new IllegalArgumentException( UIMessages.INVALID_PARAMETERS_MSG );
		}
	}
	
	private void printNotStartedMessage() {
		System.out.println("Ourgrid Broker is not started.");
	}
}
