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
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.main.DescriptionFileCompile;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

public class BrokerAddJobCommand extends AbstractCommand<BrokerSyncApplicationClient> {

	public BrokerAddJobCommand( BrokerSyncApplicationClient componentClient ) {
		super(componentClient);
	}

	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			JobSpecification theJob = DescriptionFileCompile.compileJDF( params[0] );
			int id = Integer.parseInt(getComponentClient().addJob( theJob ).getResult().toString());
			System.out.println( BrokerUIMessages.getJobAddedMessage( id ) );			
		} else {
			printNotStartedMessage();
		}
	}

	protected void validateParams(String[] params) throws Exception {
		if ( (params == null) || (params.length != 1) ) {
			throw new IllegalArgumentException( UIMessages.INVALID_PARAMETERS_MSG );
		}
	}
	
	private void printNotStartedMessage() {
		System.out.println("Ourgrid Broker is not started.");
	}
}
