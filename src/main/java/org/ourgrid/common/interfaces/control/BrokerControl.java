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
package org.ourgrid.common.interfaces.control;

import java.util.List;

import org.ourgrid.common.interfaces.to.JobEndedInterested;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.peer.PeerSpecification;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.control.ModuleControl;

/**
 * <p>
 * OurGrid Broker will receive every job related instruction through this interface. The
 * basic allowed job operations are:
 * <ul>
 * <li> Add a job;
 * <li> Cancel a job;
 * <li> Clean all information regarding a finished job or all finished jobs;
 * <li> Request to be notified when a job is finished.
 * </ul>
 * <p>
 * The correspondent callback calls or error notification should be made on the
 * <code>SchedulerClient</code> interface.
 */
@Remote
public interface BrokerControl extends ModuleControl {

	public static final String OBJECT_NAME = "SCHEDULER_CONTROL";
	
	/**
	 * Adds a job
	 * 
	 * @param callback a reference to the callback object. The generated jobID
	 *        must be returned to the client
	 * @param theJob a pre-compiled job specification containing all information
	 *        about the job
	 */
	void addJob( BrokerControlClient callback, JobSpecification theJob );


	/**
	 * Cancels a jobs. If the job is running the Scheduler is responsible of
	 * notifying the ReplicaExecutor to cancel all running replica. This call
	 * does not necessarily demand a callback call in case of success. However,
	 * if some problem occurs while cancelling the job, an error should be
	 * reported
	 * 
	 * @param callback a reference to the callback object
	 * @param jobID an identification of the job to be canceled
	 */
	void cancelJob( BrokerControlClient callback, int jobID );


	/**
	 * Cleans the list of all finished jobs
	 * 
	 * @param callback a reference to the callback object
	 */
	void cleanAllFinishedJobs( BrokerControlClient callback );


	/**
	 * Cleans a specific job, if it has finished. An error will be reported if
	 * the specified job has not finished yet
	 * 
	 * @param callback a reference to the callback object
	 * @param jobID an identification of the job to be cleaned
	 */
	void cleanFinishedJob( BrokerControlClient callback, int jobID );


	/**
	 * Through this method a client may request to be notified when a job
	 * finishes. When such event occurs the Scheduler must send a callback
	 * message through the SchedulerClient#jobIsFinished method.
	 * 
	 * @param callback a reference to the callback object
	 * @param jobID an identification to the job that the client is waiting to
	 *        finish
	 */
	void notifyWhenJobIsFinished( BrokerControlClient callback, JobEndedInterested interested, int jobID );

}