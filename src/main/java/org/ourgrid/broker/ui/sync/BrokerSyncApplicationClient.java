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
package org.ourgrid.broker.ui.sync;

import java.util.LinkedList;
import java.util.List;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.common.interfaces.management.BrokerManager;
import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;
import org.ourgrid.common.interfaces.to.JobEndedInterested;
import org.ourgrid.common.interfaces.to.JobsPackage;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.peer.PeerSpecification;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.InitializationContext;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncApplicationClient;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;


public class BrokerSyncApplicationClient extends SyncApplicationClient<BrokerManager, BrokerSyncManagerClient> {

	public BrokerSyncApplicationClient(ModuleContext context) throws CommuneNetworkException,
	ProcessorStartException {
		super("BROKER_SYNC_UI", context);
	}
	
	public BrokerSyncApplicationClient(ModuleContext context, boolean waitForever) throws CommuneNetworkException,
	ProcessorStartException {
		super("BROKER_SYNC_UI", context, waitForever);
	}

	@Override
	protected InitializationContext<BrokerManager, BrokerSyncManagerClient> createInitializationContext() {
		return new BrokerSyncInitializationContext();
	}

	public ControlOperationResult addJob( JobSpecification theJob ) {
		getManager().addJob(getManagerClient(), theJob);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}

	public void waitForJob( int jobID ) {

		this.deploy(BrokerConstants.JOB_ENDED_INTERESTED,
				new JobEndedListener(jobID, queue));

		notifyWhenJobIsFinished(jobID);

		SyncContainerUtil.waitForeverForResponseObject(queue);

		this.undeploy(BrokerConstants.JOB_ENDED_INTERESTED);
	}

	public void notifyWhenJobIsFinished(int jobID) {
		getManager().notifyWhenJobIsFinished(getManagerClient(),
				getEndedJobInterested(), jobID);
	}

	private JobEndedInterested getEndedJobInterested() {
		return (JobEndedInterested) this.
		getObjectRepository().get(BrokerConstants.JOB_ENDED_INTERESTED).getObject();
	}

	public ControlOperationResult cancelJob( int jobID ) {
		getManager().cancelJob(getManagerClient(), jobID);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}

	public ControlOperationResult cleanAllFinishedJobs() {
		getManager().cleanAllFinishedJobs(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());

	}

	public ControlOperationResult cleanFinishedJob( int jobID ) {
		getManager().cleanFinishedJob(getManagerClient(), jobID);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}

	public BrokerCompleteStatus getBrokerCompleteStatus() {
		getManager().getCompleteStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, BrokerCompleteStatus.class, getQueueTimeout());
	}
	
	public JobsPackage getJobStatus(int jobId) {
		List<Integer> jobsIds = new LinkedList<Integer>();
		jobsIds.add(jobId);
		getManager().getJobsStatus(getManagerClient(), jobsIds);
		
		return SyncContainerUtil.waitForResponseObject(queue, JobsPackage.class, getQueueTimeout());
	}
	
	public JobsPackage getAllJobStatus(int jobId) {
		List<Integer> jobsIds = new LinkedList<Integer>();
		
		for (int i = 1; i <= jobId; i++) {
			jobsIds.add(i);
		}

		getManager().getJobsStatus(getManagerClient(), jobsIds);
		
		return SyncContainerUtil.waitForResponseObject(queue, JobsPackage.class, 2*getQueueTimeout());
	}
}
