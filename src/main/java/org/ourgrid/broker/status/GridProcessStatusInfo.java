package org.ourgrid.broker.status;

import java.io.Serializable;

import org.ourgrid.common.interfaces.to.GridProcessHandle;

public class GridProcessStatusInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1109548973911511944L;
	public static final int INIT = 0;
	public static final int REMOTE = 1;
	public static final int FINAL = 2;
	public static final int FINISHED = 3;
	
	private int id;
	private int taskID;
	private int jobID;
	private String state;
	private String phase;
	private WorkerStatusInfo workerInfo;
	private GridProcessStatusInfoResult replicaResult;
	private GridProcessHandle handle;
	private long creationTime;
	private long finalizationTime;
	
	public GridProcessStatusInfo() {}
	
	public GridProcessStatusInfo(int id, int taskID, int jobID, String state, String phase, WorkerStatusInfo workerInfo,
			GridProcessStatusInfoResult replicaResult, GridProcessHandle handle) {
		
		this.id = id;
		this.taskID = taskID;
		this.jobID = jobID;
		this.state = state;
		this.phase = phase;
		this.workerInfo = workerInfo;
		this.replicaResult = replicaResult;
		this.handle = handle;
	}
	

	public String getState() {
		return state;
	}

	public int getId() {
		return id;
	}

	public int getTaskId() {
		return taskID;
	}

	public int getJobId() {
		return jobID;
	}

	public String getCurrentPhase() {
		return phase;
	}

	public GridProcessStatusInfoResult getReplicaResult() {
		return replicaResult;
	}

	public GridProcessHandle getHandle() {
		return handle;
	}

	public WorkerStatusInfo getWorkerInfo() {
		return workerInfo;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setWorkerInfo(WorkerStatusInfo workerInfo) {
		this.workerInfo = workerInfo;
	}

	public void setReplicaResult(GridProcessStatusInfoResult replicaResult) {
		this.replicaResult = replicaResult;
	}

	public void setHandle(GridProcessHandle handle) {
		this.handle = handle;
	}
	
	public static String getDescription(int state) {
		
		String description;
		switch (state) {
		
			case INIT:
				description = "INIT";
				break;
				
			case REMOTE:
				description = "REMOTE";
				break;
				
			case FINAL:
				description = "FINAL";
				break;
				
			case FINISHED:
				description = "FINISHED";
				break;
				
			default:
				description = null; 
		}
		
		return description;
	}

	public void setFinalizationTime(long finalizationTime) {
		this.finalizationTime = finalizationTime;
	}

	public long getFinalizationTime() {
		return finalizationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getCreationTime() {
		return creationTime;
	}

}
