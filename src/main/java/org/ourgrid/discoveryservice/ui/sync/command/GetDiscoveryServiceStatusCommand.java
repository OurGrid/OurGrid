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
package org.ourgrid.discoveryservice.ui.sync.command;

import java.util.Map;
import java.util.Set;

import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.interfaces.Constants;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;
import org.ourgrid.discoveryservice.ui.sync.DiscoveryServiceSyncComponentClient;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

/**
 *
 */
public class GetDiscoveryServiceStatusCommand extends AbstractCommand<DiscoveryServiceSyncComponentClient> {

	public GetDiscoveryServiceStatusCommand(DiscoveryServiceSyncComponentClient componentClient) {
		super(componentClient);
	}

	private void printStatus(DiscoveryServiceCompleteStatus dsCompleteStatus) {
		System.out.println( "Discovery Service Status" + Constants.LINE_SEPARATOR );
		
		System.out.println( "Uptime: " + StringUtil.getTimeAsText( 
				dsCompleteStatus.getUpTime() ) + Constants.LINE_SEPARATOR );
		System.out.println( "Configuration: " + Constants.LINE_SEPARATOR + 
				dsCompleteStatus.getConfiguration() );
		
		Map<DiscoveryServiceInfo, Set<String>> networkStatus =  dsCompleteStatus.getNetwork();
		
		for (DiscoveryServiceInfo dsInfo : networkStatus.keySet()) {
				System.out.println(dsInfo.getDsAddress().equals(dsCompleteStatus.getMyAddress())? "Local DS":"Remote DS");
				System.out.println("\t"+dsInfo);
				System.out.println( "\tConnected peers: ");
				
				Set<String> peers = networkStatus.get(dsInfo);
			
				if ( peers.isEmpty() ) {
					System.out.println("\t\tEmpty!");
				} else {
					for (String peerID : peers) {
						System.out.println( "\t\t" + peerID);
					}			
				}
	
				System.out.println( Constants.LINE_SEPARATOR );
			
		}
	}

	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			printStatus(getComponentClient().getDiscoveryServiceCompleteStatus());
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
		System.out.println("Ourgrid Discovery Service is not started.");
	}
}
