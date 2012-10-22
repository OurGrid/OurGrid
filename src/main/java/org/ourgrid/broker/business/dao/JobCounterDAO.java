package org.ourgrid.broker.business.dao;

import org.ourgrid.common.job.JobCounter;

/**
 * Requirement 302
 */
public class JobCounterDAO {

	
	private JobCounter jobCounter;

	
	JobCounterDAO() {}
	

	public void setJobCounter(JobCounter jobCounter) {
		this.jobCounter = jobCounter;
	}

	public JobCounter getJobCounter() {
		return jobCounter;
	}
	
	
}
