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

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

/**
 *
 */
public class PeerAddBrokerCommand extends AbstractCommand<PeerSyncApplicationClient> {

	public PeerAddBrokerCommand(PeerSyncApplicationClient componentClient) {
		super(componentClient);
	}

	@Override
	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			String login = params[0]; 
					
			checkForAtSignal(login);
			
			ControlOperationResult result = getComponentClient().addUser(login);
			
			checkForErrorCause(result);
		} else {
			printNotStartedMessage();
		}
	}

	/**
	 * @param result
	 * @throws Exception
	 */
	protected void checkForErrorCause(ControlOperationResult result)
			throws Exception {
		if (result.getErrorCause() != null) {
			throw result.getErrorCause();
		}
	}

	/**
	 * @param login
	 */
	protected void checkForAtSignal(String login) {
		if (login.indexOf('@') < 0 ) {
			throw new IllegalArgumentException( UIMessages.INVALID_LOGIN_FORMAT_MSG );
		}
	}

	@Override
	protected void validateParams(String[] params) throws Exception {
		if ( params == null || params.length != 2 ) {
			throw new IllegalArgumentException( UIMessages.INVALID_PARAMETERS_MSG );
		}
	}
	
	private void printNotStartedMessage() {
		System.out.println("Ourgrid Peer is not started.");
	}
}
