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
package org.ourgrid.aggregator.ui.sync.command;

import org.ourgrid.aggregator.ui.sync.AggregatorSyncComponentClient;
import org.ourgrid.common.command.UIMessages;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

/**
 * This class is used to retrieve a query in Aggregator's databse.
 */
public class AggregatorQueryCommand extends AbstractCommand<AggregatorSyncComponentClient> {

	public AggregatorQueryCommand(AggregatorSyncComponentClient componentClient) {
		super(componentClient);
	}

	private void printNotStartedMessage() {
		System.out.println("Ourgrid Aggregator is not started.");
	}

	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			String query = params[0];
			
			
			ControlOperationResult result = getComponentClient().query(query);
			
			if (result != null) {
				
				Exception errorCause = result.getErrorCause();
				if (errorCause == null) {
					System.out.println(result.getResult());
					
				} else {
					throw (Exception) errorCause;
				}
				
			}
			
		} else {
			printNotStartedMessage();
		}
	}

	protected void validateParams(String[] params) throws Exception {
		if ( params == null || params.length != 1 ) {
			throw new IllegalArgumentException( UIMessages.INVALID_PARAMETERS_MSG );
		}
	}
}
