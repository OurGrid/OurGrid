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
package org.ourgrid.common.job;

import java.io.Serializable;
import java.util.Map;

import org.ourgrid.broker.business.dao.WorkerEntry;
import org.ourgrid.broker.business.scheduler.RunningState;
import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.broker.communication.operations.GridProcessOperations;
import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessExecutionResult;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessPhase;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.util.CommonUtils;

/**
 * A replica is the lower level abstraction that is executed on the
 * <code>Worker</code>s. It contains all necessary information to run a a job
 * task. <br>
 * All replicas of a task differ only in their identification, the remaining
 * information is the same to all replicas. In other words, a replica is a
 * representation of a task to be executed on the remote machines.
 * 
 * @see Job
 * @see Task
 */
public class GridProcess implements Serializable {

	private static final long serialVersionUID = 40L;

	/** The task */
	private transient final Task task;

	/** Current state for this <code>Replica</code> * */
	private GridProcessState state;

	/**
	 * Result for this <code>Replica</code>'s execution.
	 */
	private GridProcessExecutionResult replicaResult;

	private GridProcessHandle replicaHandle;

	private WorkerEntry workerEntry;

	private Map<GenericTransferHandle, GenericTransferProgress> transfersProgress;

	private GridProcessPhase currentPhase;

	/**
	 * Creation time of this Replica
	 */
	private long creationTime;
	
	/**
	 * Time when this Replica finished its execution
	 */
	private long finalizationTime;

	private transient GridProcessOperations operations;
	
	private transient RunningState runningState;
	
	private transient GridProcessAccounting replicaAccounting;
	
	/**
	 * Constant used to indicated that this task has not been finalized.
	 */
	public static final int TIME_NOT_FINALIZED = -1;
	

	/**
	 * The constructor.
	 * 
	 * @param id The replica id
	 * @param taskid The task id
	 * @param jobid The job id
	 */
	public GridProcess( int id, Task task) {

		this.task = task;
		this.state = GridProcessState.UNSTARTED;
		this.currentPhase = GridProcessPhase.INIT;
		this.replicaHandle = new GridProcessHandle( task.getJob().getJobId(), task.getTaskid(), id );
		this.creationTime = System.currentTimeMillis();
		this.finalizationTime = TIME_NOT_FINALIZED;
		this.transfersProgress = CommonUtils.createSerializableMap();
		this.replicaResult = new GridProcessExecutionResult(
				new GridProcessHandle(getJobId(), getTaskId(), getId()));
	}


	/**
	 * Set the result for this <code>Replica</code> and changes state.
	 * 
	 * @param replicaResult Result of the <code>Replica</code>.
	 * @param newState New state of the <code>Replica</code>.
	 */
	public void setGridProcessState( GridProcessState newState ) {
		this.state = newState;
	}

	/**
	 * Set the result for this <code>Replica</code> and changes state.
	 * 
	 * @param replicaResult Result of the <code>Replica</code>.
	 * @param newState New state of the <code>Replica</code>.
	 */
	public void setGridProcessResult( GridProcessState newState ) {
		this.setGridProcessState(newState);
		this.finalizationTime = System.currentTimeMillis();
	}
	
	
	/**
	 * Mark's this <code>GridProcess</code> as running, in other words this
	 * replica will change state to <code>GridProcessState.RUNNING</code> if it
	 * is in <code>GridProcessState.UNSTARTED</code>.
	 * 
	 * @param chosenWorker Spec of the worker that will run this replica.
	 */
	public void allocate( WorkerEntry chosenWorker ) {

		if ( !GridProcessState.UNSTARTED.equals( this.state ) ) {
			throw new IllegalResultException( "This replica is already running or has already finished execution",
				replicaHandle );
		}

		this.workerEntry = chosenWorker;
		RequestSpecification requestSpec = workerEntry.getRequestSpecification();
		
		if (requestSpec != null) {
			this.replicaAccounting = new GridProcessAccounting(requestSpec.getRequestId(), requestSpec.getJobId(),
					requestSpec.getRequiredWorkers(), requestSpec.getMaxFails(), requestSpec.getMaxReplicas(),
					getWorkerEntry().getWorkerID(), getWorkerEntry().getWorkerPublicKey(),
					workerEntry.getWorkerSpecification());
		}
	}


	/**
	 * Get's the result for this <code>GridProcess</code>'s execution.
	 * 
	 * @return Result for the <code>Replica</code> or null if no result
	 *         exists.
	 */
	public GridProcessExecutionResult getResult() {

		return replicaResult;
	}


	/**
	 * Returns the <code>TaskSpec</code> of this replica.
	 * 
	 * @return The <code>TaskSpec</code>
	 */
	public TaskSpecification getSpec() {

		return task.getSpec();
	}


	/**
	 * Get's the current state of this <code>Replica</code>.
	 * 
	 * @return Current state of the <code>Replica</code>.
	 */
	public GridProcessState getState() {

		return state;
	}


	/**
	 * Returns the owner job identification.
	 * 
	 * @return The job identification
	 */
	public int getJobId() {

		return this.replicaHandle.getJobID();
	}


	/**
	 * Returns the owner task identification.
	 * 
	 * @return The task identification
	 */
	public int getTaskId() {

		return this.replicaHandle.getTaskID();
	}


	/**
	 * Returns the identification of this replica.
	 * 
	 * @return The identification
	 */
	public int getId() {

		return this.replicaHandle.getReplicaID();
	}


	/**
	 * Get's the specification of the <code>Worker</code> that will execute
	 * this replica.
	 * 
	 * @return <code>WorkerSpec</code> specification of the
	 *         <code>Worker</code>.
	 */
	public WorkerEntry getWorkerEntry() {

		return workerEntry;
	}

	/**
	 * Return the timestamp on which the task was added
	 * 
	 * @return timestamp in milliseconds since epoch
	 * @see System#currentTimeMillis()
	 */
	public long getCreationTime() {
		return this.creationTime;
	}

	/**
	 * Return the timestamp on which task was finished
	 * 
	 * @return timestamp in milliseconds since epoch
	 * @see System#currentTimeMillis()
	 */
	public long getFinalizationTime() {
		return finalizationTime;
	}


	/**
	 * Returns a unique string representation for this replica.
	 * 
	 * @return A string representation for this replica.
	 */
	@Override
	public String toString() {

		return getJobId() + "." + getTaskId() + "." + getId();
	}


	@Override
	public boolean equals( Object o ) {

		if ( o instanceof GridProcess ) {
			GridProcess otherReplica = (GridProcess) o;
			return otherReplica.getHandle().equals( this.getHandle() );
		}

		return false;
	}

	public boolean isReadyToRun() {
		return this.state.equals(GridProcessState.UNSTARTED) && (this.workerEntry != null);
	}
	
	
	@Override
	public int hashCode() {

		return this.getHandle().hashCode();
	}


	public GridProcessHandle getHandle() {

		return replicaHandle;
	}


	public void setAsCancelled() {

		if ( state == GridProcessState.RUNNING || state == GridProcessState.UNSTARTED ) {
			state = GridProcessState.CANCELLED;
			this.finalizationTime = System.currentTimeMillis();
		}
	}


	public void fileTransferProgressUpdate( GenericTransferProgress fileTransferProgress ) {

		transfersProgress.put( fileTransferProgress.getHandle(), fileTransferProgress );
	}


	public void gridProcessPhaseUpdate( GridProcessPhase newPhase ) {

		this.currentPhase = newPhase;
	}


	public GridProcessPhase getCurrentPhase() {

		return this.currentPhase;
	}


	public Map<GenericTransferHandle, GenericTransferProgress> getTransfersProgress() {

		return this.transfersProgress;
	}


	public GenericTransferProgress getTransferProgress( GenericTransferHandle handle ) {

		return this.transfersProgress.get( handle );
	}

	public void setAsRunning() {
		
		if ( !GridProcessState.UNSTARTED.equals( this.state ) ) {
			throw new IllegalResultException( "This replica is already running or has already finished execution",
				replicaHandle );
		}
		
		this.state = GridProcessState.RUNNING;
	}

	public void setOperations(GridProcessOperations executionOperations) {
		this.operations = executionOperations;
	}
	
	public GridProcessOperations getOperations() {
		return operations;
	}


	public void setRunningState(RunningState state) {
		this.runningState = state;
	}


	public RunningState getRunningState() {
		return runningState;
	}
	
	public Job getJob() {
		return this.task.getJob();
	}
	
	public Task getTask() {
		return this.task;
	}
	
	public void incDataTransfered(long dataTransfered) {
		this.replicaAccounting.incDataTransfered(dataTransfered);
	}
	
	public void startCPUTiming() {
		this.replicaAccounting.startCPUTiming();
	}
	
	public void stopCPUTiming() {
		this.replicaAccounting.stopCPUTiming();
	}


	public GridProcessAccounting getReplicaAccounting() {
		return replicaAccounting;
	}
	
	public String getWorkerProviderID() {
		return getWorkerEntry().getPeerID();
	}


	public boolean hasFinalStateStarted() {
		return replicaResult.getFinalData().getStartTime() > -1;
	}


}
