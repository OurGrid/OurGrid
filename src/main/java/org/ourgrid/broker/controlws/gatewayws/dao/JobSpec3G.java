package org.ourgrid.broker.controlws.gatewayws.dao;

import org.ourgrid.common.specification.job.JobSpecification;

public class JobSpec3G {

	private int jobID;
	private final JobSpecification jobSpec;
	
	public JobSpec3G(JobSpecification jobSpec) {
		this.jobSpec = jobSpec;
		this.jobID = -1;
	}
	
	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public JobSpecification getJobSpec() {
		return jobSpec;
	}
	
}
