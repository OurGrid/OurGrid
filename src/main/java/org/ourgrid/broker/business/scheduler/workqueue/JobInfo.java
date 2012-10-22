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
package org.ourgrid.broker.business.scheduler.workqueue;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.ourgrid.broker.business.dao.Request;
import org.ourgrid.common.job.Job;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.util.CommonUtils;

/**
 *
 */
public class JobInfo {
	
	private static JobInfo instance;

	/**
	 * Map associating <code>Integer</code> that defines job ids to their
	 * respective <code>Job</code>
	 */
	private final Map<Integer,Job> jobMap;
	private final Map<Integer, Set<String>> jobInteresteds;

	private JobInfo() {
		this.jobMap = CommonUtils.createSerializableMap();
		this.jobInteresteds = CommonUtils.createSerializableMap();
	}

	public static JobInfo getInstance() {
		if (instance == null) {
			instance = new JobInfo();
		}
		return instance;
	}
	
	public static void reset() {
		instance = new JobInfo();
	}
	
	public Job addJob(JobSpecification jobSpec, int jobID) {
		
		int sequence = 1;
		for (TaskSpecification taskSpec : jobSpec.getTaskSpecs()) {
			taskSpec.setTaskSequenceNumber(sequence++);
		}
		
		Job job = new Job(jobID, jobSpec);
		
		jobMap.put(jobID, job);
		
		return job;
	}
	
	public boolean hasRunningJobs() {
		for (Job job : this.jobMap.values()) {
			if (job.isRunning()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean removeJob(int jobID) {
		return (this.jobMap.remove(jobID) != null);
	}

	public Job getJob(int jobID) {
		return this.jobMap.get(jobID);
	}

	public Collection<Job> getJobsList() {
		return this.jobMap.values();
	}
	
	public Map<Integer, Job> getJobs() {
		return this.jobMap;
	}
	
	/**
	 * @param requestID
	 * @return The job associated with this request.
	 */
	public Job getJobForThisRequest(long requestID) {
		
		Set<Integer> keySet = jobMap.keySet();
		for (Integer jobId : keySet) {
			Job job = jobMap.get(jobId);
			
			Request request = job.getRequest(requestID);
			if (request != null) {
				return job;
			}
			
		}
		
		return null;
	}
	
	public void notifyWhenJobIsFinished(int jobID, String interestedID) {
		
		Set<String> interesteds = this.jobInteresteds.get(jobID);
		
		if (interesteds == null) {
			interesteds = new LinkedHashSet<String>();
			this.jobInteresteds.put(jobID, interesteds);
		}
		
		interesteds.add(interestedID);
	}
	
	public void removeJobEndInterested(String interestedID) {
		for (Map.Entry<Integer, Set<String>> entry : jobInteresteds.entrySet()) {
			if (entry.getValue().remove(interestedID)) {
				return;
			}
		}
	}
	

	public Set<String> getInterested(int jobId) {
		return this.jobInteresteds.get(jobId);
	}
	
	public void cleanAllFinishedJobs() {
		Iterator<Entry<Integer,Job>> jobIt = jobMap.entrySet().iterator();
		while ( jobIt.hasNext() ) {
			Job job = jobIt.next().getValue();

			if ( job != null && !job.isRunning() ) {
				jobIt.remove();
			}
		}
	}
	
	public void cleanFinishedJob(int jobID) {
		Job job = jobMap.get( jobID );

		if ( job != null && !job.isRunning()) {
			if ( !job.isRunning() ) {
				jobMap.remove( jobID );
			}
		}	
	}
}
