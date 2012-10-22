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
package org.ourgrid.broker.business.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.util.CommonUtils;

public class JobDAO {
	
	private String brokerControlClientAddress;
	
	private List<SchedulerIF> schedulers;
	private Map<Integer, SchedulerIF> jobScheduler;
	private Map<Integer, List<Long>> jobRequests;
	private Map<String, Integer> worker2Job;
	private Map<Integer, JobSpecification> job2Specs;

	
	public JobDAO() {
		this.jobScheduler = CommonUtils.createMap();
		this.jobRequests = CommonUtils.createMap();
		this.worker2Job = CommonUtils.createSerializableMap();
		this.schedulers = new ArrayList<SchedulerIF>();
		this.job2Specs = CommonUtils.createMap();
	}
	
	
	public void addJobSpec(int jobID, JobSpecification spec) {
		this.job2Specs.put(jobID, spec);
	}
	
	public JobSpecification getJobSpec(int jobID) {
		return this.job2Specs.get(jobID);
	}
	
	public void addWorker2Job(String workerContainerID, Integer jobID) {
		this.worker2Job.put(workerContainerID, jobID);
	}
	
	
	public Integer getJobFromWorker(String workerContainerID) {
		return this.worker2Job.get(workerContainerID);
	}
	
	public void addJob(Integer jobID) {
		this.jobScheduler.put(jobID, getHeadScheduler());
	}
	
	public void addJobRequest(int jobID, long requestID) {
		
		List<Long> requests = this.jobRequests.get(jobID);
		
		if (requests == null) {
			requests = new ArrayList<Long>();
			this.jobRequests.put(jobID, requests);
		}
		
		requests.add(requestID);
	}
	
	public void removeJobRequest(int jobID, long requestID) {
		
		if (this.jobRequests.containsKey(jobID)) {
			this.jobRequests.get(jobID).remove(requestID);
		}
		
	}
	
	public int getJobForThisRequest(long requestID) {
		
		for (Entry<Integer, List<Long>> entry : this.jobRequests.entrySet()) {
			if (entry.getValue().contains(requestID)) {
				return entry.getKey();
			}
		}
		
		return -1;
	}
	
	public SchedulerIF getJobScheduler(int jobID) {
		return this.jobScheduler.get(jobID);
	}
	
	public Set<SchedulerIF> getSchedulers() {
		return new LinkedHashSet<SchedulerIF>(this.schedulers);
	}

	public SchedulerIF getHeadScheduler() {
		return schedulers.get(0);
	}

	public String getBrokerControlClientAddress() {
		return brokerControlClientAddress;
	}

	public void setBrokerControlClientAddress(String brokerControlClientAddress) {
		this.brokerControlClientAddress = brokerControlClientAddress;
	}
	
	public List<Integer> getJobIDs() {
		return new ArrayList<Integer>(this.jobScheduler.keySet());
	}
	
	public void addScheduler(SchedulerIF scheduler) {
		this.schedulers.add(scheduler);
	}
	
	public boolean jobExists(int jobID) {
		return this.jobScheduler.containsKey(jobID);
	}
	
	public void cancelJob(int jobID) {
		this.jobScheduler.remove(jobID);
	}
}
