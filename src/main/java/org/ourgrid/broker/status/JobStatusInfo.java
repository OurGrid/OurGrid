package org.ourgrid.broker.status;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.specification.job.JobSpecification;

public class JobStatusInfo implements Serializable, Comparable<JobStatusInfo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5595989079578058777L;
	public static final int FAILED = 0;
	public static final int FINISHED = 1;
	public static final int ABORTED = 2;
	public static final int CANCELLED = 3;
	public static final int UNSTARTED = 4;
	public static final int RUNNING = 5;
	public static final int SABOTAGED = 6;

	private int jobID;
	private JobSpecification jobSpec;
	private int state;
	private List<TaskStatusInfo> tasks;
	
	private long creationTime;
	private long finalizationTime;

	private Map<String, Long> peersToRequests;
	
	public JobStatusInfo() {}
	
	public JobStatusInfo(int jobID, JobSpecification jobSpec, int state, List<TaskStatusInfo> tasks, long creationTime,
			long finalizationTime) {
		this.jobID = jobID;
		this.jobSpec = jobSpec;
		this.state = state;
		this.tasks = tasks;
		this.creationTime = creationTime;
		this.finalizationTime = finalizationTime;
	}
	
	public int getJobId() {
		return jobID;
	}
	
	public JobSpecification getSpec() {
		return jobSpec;
	}
	
	public int getState() {
		return state;
	}
	
	public List<TaskStatusInfo> getTasks() {
		return tasks;
	}
	
	public TaskStatusInfo getTaskByID(int id) {
		
		for (TaskStatusInfo task : this.tasks) {
			if (task.getTaskId() == id) {
				return task;
			}
		}
		
		return null;
	}
	
	public static String getState(int state) {
		
		String description;
		switch (state) {
		
			case ABORTED:
				description = "ABORTED";
				break;
				
			case CANCELLED:
				description = "CANCELLED";
				break;
				
			case FAILED:
				description = "FAILED";
				break;
				
			case FINISHED:
				description = "FINISHED";
				break;
				
			case RUNNING:
				description = "RUNNING";
				break;
				
			case SABOTAGED:
				description = "SABOTAGED";
				break;
				
			case UNSTARTED:
				description = "UNSTARTED";
				break;
				
			default:
				description = null; 
		}
		
		return description;
	}
	
	public boolean isRunning() {

		return RUNNING == state || UNSTARTED == state;
	}

	public void setJobId(int jobID) {
		this.jobID = jobID;
	}

	public JobSpecification getJobSpec() {
		return jobSpec;
	}

	public void setJobSpec(JobSpecification jobSpec) {
		this.jobSpec = jobSpec;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setTasks(List<TaskStatusInfo> tasks) {
		this.tasks = tasks;
	}
	
	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getFinalizationTime() {
		return finalizationTime;
	}

	public void setFinalizationTime(long finalizationTime) {
		this.finalizationTime = finalizationTime;
	}

	public void setPeersToRequests(Map<String, Long> peersToRequests) {
		this.peersToRequests = peersToRequests;
	}

	public Map<String, Long> getPeersToRequests() {
		return peersToRequests;
	}

	public int compareTo(JobStatusInfo o) {
		return jobID - o.jobID;
	}
}
