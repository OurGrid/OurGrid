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

import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.status.AggregatorCompleteStatus;
import org.ourgrid.aggregator.ui.sync.AggregatorSyncComponentClient;
import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.interfaces.Constants;
import org.ourgrid.common.util.StringUtil;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

/**
 * This class is used to show the Status of the Aggregator.
 */
public class AggregatorStatusCommand extends AbstractCommand<AggregatorSyncComponentClient> {

	public AggregatorStatusCommand(AggregatorSyncComponentClient componentClient) {
		super(componentClient);
	}

	private void printStatus(AggregatorCompleteStatus aggCompleteStatus) {
		System.out.println( "Aggregator Status" + Constants.LINE_SEPARATOR );
		
		System.out.println( "Uptime: " + StringUtil.getTimeAsText( 
				aggCompleteStatus.getUpTime() ) + Constants.LINE_SEPARATOR );
		System.out.println( "Configuration: " + Constants.LINE_SEPARATOR + 
				aggCompleteStatus.getConfiguration() );
		
		System.out.println(getDsUserAtSeverMessage(
				aggCompleteStatus.getDiscoveryServiceUserAtSever()));
		
		System.out.println( Constants.LINE_SEPARATOR );
	}

	private String getDsUserAtSeverMessage(String discoveryServiceUserAtSever) {
		if(discoveryServiceUserAtSever == null){
			return AggregatorControlMessages.getCommunityStatusProviderIsWrongOrDownMessage();
		}
		
		return "Discovery Service: " + discoveryServiceUserAtSever;
	}

	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			printStatus(getComponentClient().getAggregatorCompleteStatus());
		} else {
			printNotStartedMessage();
		}
		
	}
	
	protected void validateParams(String[] params) throws Exception {
		if ( params.length != 0 ) {
			throw new IllegalArgumentException( UIMessages.INVALID_PARAMETERS_MSG );
		}
	}

	private void printNotStartedMessage() {
		System.out.println("Ourgrid Aggregator is not started.");
	}
}
