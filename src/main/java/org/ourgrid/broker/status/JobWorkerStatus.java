package org.ourgrid.broker.status;

import java.io.Serializable;
import java.util.Map;

import org.ourgrid.common.util.CommonUtils;

public class JobWorkerStatus implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7927466232929573655L;
	private Map<Integer, JobStatusInfo> jobs;
	private Map<Integer, WorkerStatusInfo[]> workers;
	
	
	public JobWorkerStatus() {
		this.jobs = CommonUtils.createSerializableMap();
		this.workers = CommonUtils.createSerializableMap();
	}
	
	
	public JobWorkerStatus(Map<Integer, JobStatusInfo> jobs, Map<Integer, WorkerStatusInfo[]> workers) {
		this.jobs = jobs;
		this.workers = workers;
	}

	public Map<Integer, JobStatusInfo> getJobs() {
		return jobs;
	}

	public Map<Integer, WorkerStatusInfo[]> getWorkers() {
		return workers;
	}

	public void setJobs(Map<Integer, JobStatusInfo> jobs) {
		this.jobs = jobs;
	}

	public void setWorkers(Map<Integer, WorkerStatusInfo[]> workers) {
		this.workers = workers;
	}
}
