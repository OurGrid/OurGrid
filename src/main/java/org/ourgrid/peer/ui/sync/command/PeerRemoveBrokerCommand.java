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

import org.ourgrid.common.command.UIMessages;
import org.ourgrid.peer.ui.sync.PeerSyncApplicationClient;
import org.ourgrid.peer.ui.sync.PeerUIMessages;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

/**
 * @author alan
 *
 */
public class PeerRemoveBrokerCommand extends AbstractCommand<PeerSyncApplicationClient> {

	public PeerRemoveBrokerCommand(PeerSyncApplicationClient componentClient) {
		super(componentClient);
	}

	private void printNotStartedMessage() {
		System.out.println("Ourgrid Peer is not started.");
	}

	@Override
	protected void execute(String[] params) throws Exception {
		
		if (isComponentStarted()) {
			String login = params[0];
			
			ControlOperationResult result = getComponentClient().removeUser(login);
			
			checkForErrorCause(result);
			
			printSuccessfulMessage(login);	
			
		} else {
			printNotStartedMessage();
		}
	}

	/**
	 * @param params
	 */
	protected void printSuccessfulMessage(String login) {
		System.out.println( PeerUIMessages.getSuccessMessage( "The broker <" + login + 
				"> was successfuly removed from the peer." ) );
	}

	@Override
	protected void validateParams(String[] params) throws Exception {
		if ( params == null || params.length != 1 ) {
			throw new IllegalArgumentException( UIMessages.INVALID_PARAMETERS_MSG );
		}
	}
	
	protected void checkForErrorCause(ControlOperationResult result)
			throws Exception {
		if (result.getErrorCause() != null) {
			throw result.getErrorCause();
		}
	}
}
