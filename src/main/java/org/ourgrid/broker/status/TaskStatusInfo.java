package org.ourgrid.broker.status;

import java.io.Serializable;
import java.util.List;

import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.specification.job.TaskSpecification;

public class TaskStatusInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5522961239389120279L;
	private int id;
	private int jobID;
	private String state;
	private List<GridProcessStatusInfo> gridProcesses;
	private int actualFails;
	private TaskSpecification spec;
	private long creationTime;
	private long finalizationTime;
	
	public TaskStatusInfo() {}
	
	public TaskStatusInfo(int id, int jobID, String state, int actualFails, TaskSpecification spec, 
			List<GridProcessStatusInfo> gridProcesses, long creationTime, long finalizationTime) {
		this.id = id;
		this.jobID = jobID;
		this.state = state;
		this.actualFails = actualFails;
		this.spec = spec;
		this.gridProcesses = gridProcesses;
		this.creationTime = creationTime;
		this.finalizationTime = finalizationTime;
	}
	

	public String getState() {
		return state;
	}

	public List<GridProcessStatusInfo> getGridProcesses() {
		return gridProcesses;
	}

	public int getTaskId() {
		return id;
	}

	public int getJobId() {
		return jobID;
	}
	
	public int getNumberOfRunningReplicas() {

		int returnValue = 0;
		for ( GridProcessStatusInfo replica : gridProcesses ) {
			if (  GridProcessState.RUNNING.toString().equals(replica.getState())  ) {
				++returnValue;
			}
		}

		return returnValue;
	}

	public int getActualFails() {
		return actualFails;
	}

	public TaskSpecification getSpec() {
		return spec;
	}
	
	public GridProcessStatusInfo getReplicaByID( int replicaid ) {
		return gridProcesses.get( replicaid - 1 );
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setGridProcesses(List<GridProcessStatusInfo> gridProcesses) {
		this.gridProcesses = gridProcesses;
	}

	public void setSpec(TaskSpecification spec) {
		this.spec = spec;
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

	public void setActualFails(int actualFails) {
		this.actualFails = actualFails;
	}
	
	
}
