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

import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.ui.sync.BrokerSyncApplicationClient;
import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.interfaces.to.JobsPackage;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

public class BrokerJobStatusCommand extends AbstractCommand<BrokerSyncApplicationClient> {

	public BrokerJobStatusCommand( BrokerSyncApplicationClient application) {
		super(application);

	}

	protected void execute(String[] params) {
		if (isComponentStarted()) {
			int jobId = Integer.parseInt(params[0]);
			JobsPackage jobsPackage = getComponentClient().getJobStatus(jobId);
			JobStatusInfo jobStatus = jobsPackage.getJobs().get(jobId);
			
			printJobStatus(jobId, jobStatus);
		} else {
			printNotStartedMessage();
		}
	}


	private void printJobStatus(int jobId, JobStatusInfo jobStatus) {
		if (jobStatus != null) {
			System.out.println("id: " + jobId);
			System.out.println("state: " + jobStatus.getState());
			System.out.println("creation: " + jobStatus.getCreationTime());
			System.out.println("end: " + jobStatus.getFinalizationTime());
			System.out.println("elapsed time: " + (jobStatus.getFinalizationTime() -
					jobStatus.getCreationTime()));
		} else {
			throw new IllegalArgumentException("There is no job with id [" + jobId + "]");
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
