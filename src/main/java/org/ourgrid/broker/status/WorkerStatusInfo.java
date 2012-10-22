package org.ourgrid.broker.status;

import java.io.Serializable;

import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class WorkerStatusInfo implements Serializable, Comparable<WorkerStatusInfo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7576640598860550345L;
	private WorkerSpecification spec;
	private GridProcessHandle replicaHandle;
	private String workerID;
	private String processState;
	
	public WorkerStatusInfo() {}
	
	public WorkerStatusInfo(WorkerSpecification spec, GridProcessHandle handle) {
		this.spec = spec;
		this.replicaHandle = handle;
	}
	
	public WorkerStatusInfo(WorkerSpecification spec, GridProcessHandle handle, String workerID, String processState) {
		this.spec = spec;
		this.replicaHandle = handle;
		this.workerID = workerID;
		this.processState = processState;
	}

	public WorkerSpecification getWorkerSpec() {
		return spec;
	}

	public GridProcessHandle getReplicaHandle() {
		return replicaHandle;
	}

	public String getWorkerID() {
		return workerID;
	}

	public String getProcessState() {
		return processState;
	}

	public WorkerSpecification getSpec() {
		return spec;
	}

	public void setSpec(WorkerSpecification spec) {
		this.spec = spec;
	}

	public void setReplicaHandle(GridProcessHandle replicaHandle) {
		this.replicaHandle = replicaHandle;
	}

	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}

	public void setProcessState(String processState) {
		this.processState = processState;
	}

	public int compareTo(WorkerStatusInfo o) {
		return replicaHandle.compareTo(o.replicaHandle);
	}
}
