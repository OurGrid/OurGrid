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
package org.ourgrid.worker.ui.sync.command;

import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.interfaces.Constants;
import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.worker.status.PeerStatusInfo;
import org.ourgrid.worker.ui.sync.WorkerSyncComponentClient;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

/**
 *
 */
public class GetWorkerStatusCommand extends AbstractCommand<WorkerSyncComponentClient> {

	public GetWorkerStatusCommand(WorkerSyncComponentClient componentClient) {
		super(componentClient);
	}

	private void printStatus(WorkerCompleteStatus workerCompleteStatus) {
		System.out.println( "Worker Status" + Constants.LINE_SEPARATOR );
		
		System.out.println( "Uptime: " + StringUtil.getTimeAsText( 
				workerCompleteStatus.getUpTime() ) + Constants.LINE_SEPARATOR );
		System.out.println( "Configuration: " + Constants.LINE_SEPARATOR + 
				workerCompleteStatus.getConfiguration() );
		
		PeerStatusInfo peerInfo = workerCompleteStatus.getPeerInfo();
		
		String loginError = peerInfo.getLoginError();
		String loggedStatus = " [" + peerInfo.getState() + "]" + (loginError != null ? 
				" => Cause: " + loginError : "");
		
		System.out.println( "Status: " + workerCompleteStatus.getStatus());
		System.out.println( "Master Peer: " + peerInfo.getPeerUserAtServer() + loggedStatus);
		System.out.println( "Current Playpen Dir: " + workerCompleteStatus.getCurrentPlaypenDirPath());
		System.out.println( "Current Storage Dir: " + workerCompleteStatus.getCurrentStorageDirPath());
		System.out.println( Constants.LINE_SEPARATOR );
	}

	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			printStatus(getComponentClient().getWorkerCompleteStatus());
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
		System.out.println("Ourgrid Worker is not started.");
	}
}
