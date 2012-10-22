package org.ourgrid.common.executor.gateway;

import java.util.Map;

import org.ourgrid.gateway.wssubmitter.client.Job;


public class GatewayJob {

	private final String jobId;
	
	private final Job job;

	private final Map<String, String> outputs;

	public String getJobId() {
		return jobId;
	}

	public Job getJob() {
		return job;
	}

	public GatewayJob(String jobId, Job job, Map<String, String> resolvedOutputs) {
		this.jobId = jobId;
		this.job = job;
		this.outputs = resolvedOutputs;
	}

	public Map<String, String> getOutputs() {
		return outputs;
	}

	
}
