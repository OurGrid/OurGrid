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
package org.ourgrid.broker.controlws.gatewayws;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ourgrid.broker.controlws.WSIOEntry;
import org.ourgrid.broker.controlws.WSJobSpec;
import org.ourgrid.broker.controlws.WSTaskSpec;
import org.ourgrid.broker.controlws.gatewayws.dao.JobSpec3G;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.interfaces.management.BrokerManager;
import org.ourgrid.common.interfaces.to.JobsPackage;
import org.ourgrid.common.specification.exception.JobSpecificationException;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.util.JavaFileUtil;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.InitializationContext;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncApplicationClient;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;


public class Broker3GControlWSFacade extends SyncApplicationClient<BrokerManager, Broker3GControlWSManagerClient> {

	private static final int POLL_TIMEOUT = 60 * 3;
	
	public Broker3GControlWSFacade(ModuleContext context) throws CommuneNetworkException,
		ProcessorStartException {
				
		super("BROKER_3G_SYNC_WEB_SERVICE", context);
	}
	
	@Override
	protected InitializationContext<BrokerManager, Broker3GControlWSManagerClient> createInitializationContext() {
		return new Broker3GControlWSInitializationContext();
	}
	

	public String getJobStatus(int jobId) {
		if (!isServerApplicationUp()) {
			return new String();
		}
		
		getManager().getJobsStatus(getManagerClient(), Arrays.asList(new Integer[]{jobId}));
		
		JobsPackage jobsPackage = SyncContainerUtil.waitForResponseObject(queue, JobsPackage.class, POLL_TIMEOUT);
		if (jobsPackage == null) {
			return new String();
		}
		
		JobStatusInfo jobInfo = jobsPackage.getJobs().get(jobId);
		
		if (jobInfo == null) {
			return new String();
		}
		
		String stateStr = JobStatusInfo.getState(jobInfo.getState());
		
		if (stateStr == null) {
			return new String();
		}
		
		return stateStr;
	}
	
	public boolean cancelJob(int jobID) {
		if (!isServerApplicationUp()) {
			return false;
		}
		
		getManager().cancelJob(getManagerClient(), jobID);
		ControlOperationResult operationResult = SyncContainerUtil.waitForResponseObject(
				queue, ControlOperationResult.class, POLL_TIMEOUT);
		
		if (operationResult == null || operationResult.hasAnErrorOcurred()) {
			return false;
		}
		
		return true;
	}
	

	public int submitJob(WSJobSpec job) {
		if (!isServerApplicationUp()) {
			return -1;
		}
		
		try {
			setJobFilesDirAsWritable();
		} catch (IOException e) {
			return -1;
		}
		
		setJobFilesDir(job);
		JobSpec3G job3G = new JobSpec3G(convertJobSpec(job));
		
		getManager().addJob(getManagerClient(), job3G.getJobSpec());
		ControlOperationResult operationResult = SyncContainerUtil.waitForResponseObject(
				queue, ControlOperationResult.class, POLL_TIMEOUT);
		
		if (operationResult == null || operationResult.hasAnErrorOcurred()) {
			return -1;
		}
		
		Integer jobId = (Integer) operationResult.getResult();
		job3G.setJobID(jobId);
		
		return jobId;
	}

	private void setJobFilesDirAsWritable() throws IOException {
		setDirAsWritable(new File(this.getContext().getProperty(Broker3GConstants.BROKER_3G_TMPDIR_PROP)));
		
	}
	
	private static void setDirAsWritable(File directory) throws IOException {
		File[] files = directory.listFiles();
		
		for (File file : files) {
			JavaFileUtil.setWritable(file);
			//file.setWritable(true, false);
			if (file.isDirectory()) {
				setDirAsWritable(file);
			}
		}
	}

	public boolean cleanJob(int jobID) {
		if (!isServerApplicationUp()) {
			return false;
		}
		getManager().cleanFinishedJob(getManagerClient(), jobID);
		ControlOperationResult operationResult = SyncContainerUtil.waitForResponseObject(
				queue, ControlOperationResult.class, POLL_TIMEOUT);
		
		if (operationResult == null || operationResult.hasAnErrorOcurred()) {
			return false;
		}
		
		return true;
	}
	
	private void setJobFilesDir(WSJobSpec job) {
		
		List<WSTaskSpec> taskSpecs = job.getTaskSpecs();
		
		if (taskSpecs != null) {
			
			List<WSIOEntry> initBlock = null;
			String srcFile = null;
			
			List<WSIOEntry> finalBlock = null;
			String destFile = null;
			
			String jobDir = this.getContext().getProperty(Broker3GConstants.BROKER_3G_TMPDIR_PROP);
			
			for (WSTaskSpec task : taskSpecs) {
				
				initBlock = task.getInitBlock();

				if (initBlock != null) {
					for (WSIOEntry ioEntry : initBlock) {
						srcFile = ioEntry.getSourceFile();
						ioEntry.setSourceFile(jobDir + File.separator + srcFile);
					}
				}
				
				finalBlock = task.getFinalBlock();
				
				if (finalBlock != null) {
					for (WSIOEntry ioEntry : finalBlock) {
						destFile = ioEntry.getDestination();
						ioEntry.setDestination(jobDir + File.separator + destFile);
					}
				}
			}
		}	
	}
	
	private JobSpecification convertJobSpec(WSJobSpec wsSpec) {
		
		JobSpecification spec = new JobSpecification();
		spec.setLabel(wsSpec.getLabel());
		spec.setRequirements(wsSpec.getRequirements() == null ? "" : wsSpec.getRequirements());

		List<WSTaskSpec> wsTasks = wsSpec.getTaskSpecs();
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		
		if (wsTasks != null) {
			
			TaskSpecification taskSpec = null;
			IOBlock initBlock = null;
			IOBlock finalBlock = null;
			
			for (WSTaskSpec wsTask : wsTasks) {
				
				taskSpec = new TaskSpecification();
				taskSpec.setRemoteExec(wsTask.getRemoteExec());
				taskSpec.setSabotageCheck(wsTask.getSabotageCheck());
				taskSpec.setSourceDirPath(wsTask.getSourceParentDir());
				taskSpec.setTaskSequenceNumber(wsTask.getTaskSequenceNumber());
				
				initBlock = new IOBlock();
				List<WSIOEntry> wsInitBlock = wsTask.getInitBlock();
				if (wsInitBlock != null) {
					
					for (WSIOEntry wsEntry : wsInitBlock) {
						initBlock.putEntry(new IOEntry(wsEntry.getCommand(), wsEntry.getSourceFile(),
								wsEntry.getDestination()));
					}
					
					taskSpec.setInitBlock(initBlock);
				}
				
				finalBlock = new IOBlock();
				List<WSIOEntry> wsFinalBlock = wsTask.getFinalBlock();
				if (wsFinalBlock != null) {
					
					for (WSIOEntry wsEntry : wsFinalBlock) {
						finalBlock.putEntry(new IOEntry(wsEntry.getCommand(), wsEntry.getSourceFile(),
								wsEntry.getDestination()));
					}
					
					taskSpec.setFinalBlock(finalBlock);
				}
				
				tasks.add(taskSpec);
			}
		}
		
		try {
			spec.setTaskSpecs(tasks);
		} catch (JobSpecificationException e) {
			e.printStackTrace();
		}
		
		return spec;
	}

}
