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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.interfaces.to.GridProcessExecutionResult;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessPhase;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.worker.business.controller.GridProcessError;

/**
 * A <code>Task</code> represents grid processes that compose the <code>Job</code>,
 * <code>Task</code>s are replicated in the form of <code>Replica</code>s.
 * 
 * @see Job
 * @see GridProcess
 */
public class Task implements Serializable {


	private static final long serialVersionUID = 40L;

	/**
	 * <code>Job</code> that this <code>Task</code> belongs to.
	 */
	private Job job;

	/**
	 * ID of this <code>Task</code>
	 */
	private int taskid;

	/**
	 * Specification for this <code>Task</code>
	 */
	private TaskSpecification taskSpec;

	/**
	 * Actual number of fails that occurred on this task
	 */
	private int actualFails;

	/**
	 * Current state of this task.
	 */
	private GridProcessState state;

	/**
	 * Replicas of this task.
	 */
	private ArrayList<GridProcess> gridProcesses;

	/**
	 * Creation time of this Task
	 */
	private long creationTime;
	
	/**
	 * Time when this Task finished its grid process
	 */
	private long finalizationTime;
	
	/**
	 * Constant used to indicated that this task has not been finalized.
	 */
	public static final int TIME_NOT_FINALIZED = -1;

	
	/**
	 * Creates a new <code>Task</code>.
	 * 
	 * @param jobid ID of the <code>Job</code> which this task belongs to.
	 * @param taskid ID of this <code>Task</code>.
	 * @param taskSpec Specification that defines this <code>Task</code> and
	 *        <code>Replica</code>s of this <code>Task</code>.
	 * @param maxFails Maximum number of <code>Replica</code>s that may fail
	 *        on this <code>Task</code>.
	 * @param maxReplicas Maximum number of replicas that may run simultaneously
	 *        for this <code>Task</code>.
	 */
	public Task( Job job, int taskid, TaskSpecification taskSpec ) {

		this.job = job;
		this.taskid = taskid;
		this.taskSpec = taskSpec;
		this.state = GridProcessState.UNSTARTED;
		this.gridProcesses = new ArrayList<GridProcess>();
		this.creationTime = System.currentTimeMillis();
		this.finalizationTime = TIME_NOT_FINALIZED;
		this.actualFails = 0;
	}


	/**
	 * Informs the <code>Task</code> the result of a <code>Replica</code>.
	 * 
	 * @param replicaResult Result of the <code>Replica</code>.
	 * @param newState New state of the <code>Replica</code>.
	 * @param maxFailed 
	 */
	public void newReplicaResult( GridProcessExecutionResult replicaResult, GridProcessState newState, boolean maxFailed,
			boolean canReplicate) {

		GridProcess replica = getReplicaByID( replicaResult.getReplicaHandle().getReplicaID() );

		if ( replica != null ) {
			boolean hasFinishedReplica = hasAnySisterReplicaFinished(replicaResult.getReplicaHandle());
			if ( GridProcessState.FINISHED.equals( newState ) && hasFinishedReplica ) {
				throw new IllegalResultException( "Task can not have two " + GridProcessState.FINISHED + " Replicas",
					job.getJobId(), taskid );
			}
			if ( GridProcessState.ABORTED.equals( newState ) && !hasFinishedReplica ) {
				throw new IllegalResultException( "Task can not have " + GridProcessState.ABORTED
						+ " Replicas without a " + GridProcessState.FINISHED + " Replica", job.getJobId(), taskid );
			}

			replica.setGridProcessState( newState );

			if ( !GridProcessState.CANCELLED.equals( state ) ) {
				updateState( replica, maxFailed, canReplicate );
				
				if (GridProcessState.FINISHED.equals(state) || GridProcessState.FAILED.equals(state)) {
					this.finalizationTime = System.currentTimeMillis();
				}
			}
		}
	}


	/**
	 * Updates the state of this <code>Task</code> according to the last
	 * <code>Replica</code> that had it's state changed.
	 * 
	 * @param replica Last <code>Replica</code> to change state.
	 * @param maxFailed 
	 */
	private void updateState( GridProcess replica, boolean maxFailed, boolean canReplicate ) {

		if ( GridProcessState.FINISHED.equals( replica.getState() ) || GridProcessState.ABORTED.equals( replica.getState() ) ) {
			boolean stillRunning = hasGridProcessInExecution() || canReplicate;

			if ( stillRunning ) {
				state = GridProcessState.RUNNING;
			} else {
				state = GridProcessState.FINISHED;
			}
		} else {
			if ( replica.getState().equals( GridProcessState.FAILED ) ) { //sabotage or failure
				
				GridProcessError ee = replica.getResult().getExecutionError();

				// Only errors caused by the user increment the max fails. Even if the actualFails
				//meets the maxFails, a task will not enter the FAILED state if there are replica
				//running, but the task will not be able to replicate. @see Task#canReplicate()
				if(ee != null) {
					
					if(ee.getType().causedByUserApplication()) {
						incrementFails();
					}
					
					if(maxFailed) {
						state = GridProcessState.FAILED;
					}
					else {
						state = hasFinishedReplica() && !hasGridProcessInExecution() ? GridProcessState.FINISHED
								: GridProcessState.RUNNING;
					}
					
				}else {
					state = hasFinishedReplica() && !hasGridProcessInExecution() ? GridProcessState.FINISHED
							: GridProcessState.RUNNING;
				}
			}
		}
	}

	/**
	 * Increments the number of fails that occurred on this <code>Task</code>.
	 * 
	 * @return Current number of fails that occurred on the <code>Task</code>.
	 */
	private int incrementFails() {

		return ++actualFails;
	}


	/**
	 * Searches for any replica that is either in state
	 * <code>ExecutionState.UNSTARTED</code> or
	 * <code>ExecutionState.RUNNING</code>.
	 * 
	 * @return True if any replicas are on either state.
	 */
	public boolean hasGridProcessInExecution() {

		for ( GridProcess replica : gridProcesses ) {
			if ( GridProcessState.RUNNING.equals( replica.getState() )
					|| GridProcessState.UNSTARTED.equals( replica.getState() ) ) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Searches for any replica that is in state
	 * <code>ExecutionState.FINISHED</code>.
	 * 
	 * @return True if any replicas are on the state.
	 */
	public boolean hasFinishedReplica() {

		for ( GridProcess replica : gridProcesses ) {
			if ( GridProcessState.FINISHED.equals( replica.getState() ) ) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Searches for any replica that is in state
	 * <code>ExecutionState.RUNNING</code>.
	 * 
	 * @return True if any replicas are on the state.
	 */
	public boolean hasRunnigGridProcess() {

		for ( GridProcess replica : gridProcesses ) {
			if ( GridProcessState.RUNNING.equals( replica.getState() ) ) {
				return true;
			}
		}

		return false;
	}

	public boolean hasAnySisterReplicaFinished(GridProcessHandle handle) {

		for ( GridProcess replica : gridProcesses ) {
			if ( !handle.equals(replica.getHandle()) && GridProcessState.FINISHED.equals( replica.getState() ) ) {
				return true;
			}
		}

		return false;
	}

	
	
	/**
	 * Set's this <code>Task</code> as cancelled, meaning that it will no
	 * longer be replicated.
	 */
	public void setAsCancelled() {

		if ( GridProcessState.RUNNING.equals( state ) || GridProcessState.UNSTARTED.equals( state ) ) {
			this.state = GridProcessState.CANCELLED;
			this.finalizationTime = System.currentTimeMillis();
		}
	}


	/**
	 * Creates a new <code>Replica</code> of this <code>Task</code> if
	 * allowed. A new <code>Replica</code> can be created if the task is still
	 * in the <code>RUNNING</code> state and the current number of running
	 * <code>Replica</code>s is lesser than the <code>maxreplicas</code>
	 * defined for this <code>Task</code>.
	 * 
	 * @return Newly created <code>Replica</code> or <code>null</code> if
	 *         one could not be created.
	 */
	protected GridProcess createNewReplica() {

		state = GridProcessState.RUNNING;

		GridProcess replica = new GridProcess( gridProcesses.size() + 1, this);
		gridProcesses.add( replica );

		return replica;

	}
	
	/**
	 * Get's <code>Replica</code> according to the given taskid.
	 * 
	 * @param replicaid ID of the replica.
	 * @return The <code>Replica</code> that has such id or null.
	 * @see GridProcess
	 */
	public GridProcess getReplicaByID( int replicaid ) {

		return gridProcesses.get( replicaid - 1 );
	}


	/**
	 * Get's the id for this <code>Task</code>.
	 * 
	 * @return ID of this <code>Task</code>.
	 */
	public int getTaskid() {

		return taskid;
	}


	/**
	 * @return The <code>Job</code> that this <code>Task</code>
	 * belongs to.
	 */
	public Job getJob() {
		return this.job;
	}
	

	/**
	 * Get's the specification of this <code>Task</code>.
	 * 
	 * @return <code>TaskSpec</code> determining the specification for this
	 *         <code>Task</code>.
	 */
	public TaskSpecification getSpec() {

		return taskSpec;
	}

	/**
	 * Returns the current state of this <code>Task</code>.
	 * 
	 * @return State of this <code>Task</code>.
	 */
	public GridProcessState getState() {

		return state;
	}


	public int getNumberOfRunningReplicas() {

		int returnValue = 0;

		for ( GridProcess replica : gridProcesses ) {
			if ( GridProcessState.RUNNING.equals( replica.getState() ) ) {
				++returnValue;
			}
		}

		return returnValue;
	}

	public Collection<GridProcess> getReadyToRunGridProcesses() {
		Set<GridProcess> readyToRunExecs = new LinkedHashSet<GridProcess>();
		
		for (GridProcess gridProcess : this.gridProcesses) {
			if (gridProcess.isReadyToRun()) {
				readyToRunExecs.add(gridProcess);
			}
		}
		
		return readyToRunExecs;
	}

	public List<GridProcess> getGridProcesses() {

		return gridProcesses;
	}

	public int getActualFails() {

		return actualFails;
	}


	@Override
	public String toString() {

		return getJob().getJobId() + "." + getTaskid();
	}


	public void replicaPhaseUpdate( GridProcessHandle replicaHandle, GridProcessPhase newPhase ) {

		GridProcess replica = getReplicaByID( replicaHandle.getReplicaID() );

		if ( replica != null ) {
			replica.gridProcessPhaseUpdate( newPhase );
		}
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


	public int getJobId() {
		return getJob().getJobId();
	}

}
