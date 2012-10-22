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
package org.ourgrid.peer.ui.sync.command;

import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import java.util.Collection;

import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.status.CompleteStatus;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.status.PeerCompleteStatus;
import org.ourgrid.peer.ui.sync.PeerSyncApplicationClient;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

/**
 *
 */
public class PeerStatusCommand extends AbstractCommand<PeerSyncApplicationClient> {

	public PeerStatusCommand(PeerSyncApplicationClient componentClient) {
		super(componentClient);
		// TODO Auto-generated constructor stub
	}

	private void printCompleteStatus(PeerCompleteStatus completeStatus) {
		System.out.println( "Peer Status" + LINE_SEPARATOR );
		System.out.println( "Uptime: " + StringUtil.getTimeAsText( completeStatus.getUpTime() ) + LINE_SEPARATOR );

		printConfiguration( completeStatus );

		System.out.println( "Local Workers: " + formatWorkersStatus( completeStatus.getLocalWorkersInfo() )
				+ LINE_SEPARATOR );
		System.out.println( "Remote Workers: " + formatWorkersStatus( completeStatus.getRemoteWorkersInfo() )
				+ LINE_SEPARATOR );
		System.out.println( "Local Consumers: " + formatConsumersStatus( completeStatus.getLocalConsumersInfo() )
				+ LINE_SEPARATOR );
		System.out.println( "Remote Consumers: " + formatConsumersInfo( completeStatus.getRemoteConsumersInfo() )
				+ LINE_SEPARATOR );
		System.out.println( "Network of Favors Balances: " + LINE_SEPARATOR + completeStatus.getNetworkOfFavorsStatus().toString() );
	}
	
	private void printConfiguration( CompleteStatus completeStatus ) {

		System.out.println( "Configuration: " + LINE_SEPARATOR + completeStatus.getConfiguration() );
	}



	private String formatConsumersInfo( Collection<ConsumerInfo> consumersStatus ) {

		StringBuilder sb = new StringBuilder( LINE_SEPARATOR );

		if ( consumersStatus.size() == 0 )
			sb.append( "\tNONE" );

		for ( ConsumerInfo entry : consumersStatus ) {
			sb.append( "\t" + entry.toString() );
		}
		return sb.toString();
	}

	private String formatConsumersStatus( Collection<LocalConsumerInfo> consumersStatus ) {

		StringBuilder sb = new StringBuilder( LINE_SEPARATOR );

		if ( consumersStatus.size() == 0 )
			sb.append( "\tNONE" );

		for ( LocalConsumerInfo entry : consumersStatus ) {
			sb.append( "\t" + entry.toString() );
		}
		return sb.toString();
	}


	private String formatWorkersStatus( Collection<? extends WorkerInfo> workersStatus ) {

		StringBuilder sb = new StringBuilder( LINE_SEPARATOR );

		if ( workersStatus.size() == 0 )
			sb.append( "\tNONE" );

		for ( WorkerInfo entry : workersStatus ) {
			sb.append( "\t" + entry.toString() + LINE_SEPARATOR );
		}
		return sb.toString();
	}

	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			printCompleteStatus(getComponentClient().getCompleteStatus());
		} else {
			printNotStartedMessage();
		}
	}

	protected void validateParams(String[] params) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	private void printNotStartedMessage() {
		System.out.println("Ourgrid Peer is not started.");
	}
}
