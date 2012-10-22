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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import org.ourgrid.broker.business.dao.Request;
import org.ourgrid.broker.business.dao.WorkerEntry;
import org.ourgrid.common.interfaces.to.GridProcessExecutionResult;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessPhase;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.CommonUtils;

/**
 * The <code>Job</code> is an abstraction representing a set of
 * <code>Task</code>s that the user requested to be executed.
 * 
 * @see Task
 * @see GridProcess
 */
public class Job implements Serializable, Comparable<Job> {

	private static final long serialVersionUID = 40L;

	private final Set<WorkerEntry> blackListedWorkers;
	
	private Map<Long, Request> requests;
	
	/**
	 * ID of this job.
	 */
	private int jobid;

	/**
	 * Specification for this Job.
	 */
	private JobSpecification jobSpec;

	/**
	 * Collection of <code>Task</code>s that compose this job.
	 */
	private ArrayList<Task> tasks;

	/**
	 * Current state of this job.
	 */
	private GridProcessState state;

	/**
	 * Creation time of this Job
	 */
	private long creationTime;
	
	/**
	 * Time when this Job finished its execution
	 */
	private long finalizationTime;

	/**
	 * Constant used to indicated that this task has not been finalized.
	 */
	public static final int TIME_NOT_FINALIZED = -1;

	/**
	 * Creates a new <code>Job</code>.
	 * 
	 * @param jobid ID of the <code>Job</code>.
	 * @param jobSpec Specification that defines this <code>Job</code>.
	 * @param maxFails Maximum number of <code>Replica</code>s that may fail
	 *        on <code>Task</code>s that compose this <code>Job</code>.
	 * @param maxReplicas Maximum number of replicas that may run simultaneously
	 *        for <code>Tasks</code> of this job
	 */
	public Job( int jobid, JobSpecification jobSpec) {

		this.jobid = jobid;
		this.jobSpec = jobSpec;
		this.tasks = new ArrayList<Task>();
		this.state = GridProcessState.UNSTARTED;
		this.creationTime = System.currentTimeMillis();
		this.finalizationTime = TIME_NOT_FINALIZED;
		this.requests = CommonUtils.createSerializableMap();
		this.blackListedWorkers = new LinkedHashSet<WorkerEntry>();
		
		int taskid = 1;
		for ( TaskSpecification taskSpec : jobSpec.getTaskSpecs() ) {
			this.tasks.add( new Task( this, taskid++, taskSpec) );
		}
	}


	/**
	 * Informs the <code>Job</code> the result of a <code>Replica</code>.
	 * 
	 * @param replicaResult Result of the <code>Replica</code>.
	 * @param newState New state of the <code>Replica</code>.
	 * @param maxFailed 
	 */
	public void newReplicaResult( GridProcessExecutionResult replicaResult, GridProcessState newState, boolean maxFailed,
			boolean canReplicate) {

		Task task = getTaskByID( replicaResult.getReplicaHandle().getTaskID() );

		if ( task != null ) {
			task.newReplicaResult( replicaResult, newState, maxFailed, canReplicate );

			if ( !GridProcessState.CANCELLED.equals( state ) ) {
				updateState( task );
				
				if (GridProcessState.FINISHED.equals(state) || GridProcessState.FAILED.equals(state)) {
					this.finalizationTime = System.currentTimeMillis();
				}
			}
		}
	}


	/**
	 * Updates the state of this <code>Job</code> according to the last
	 * <code>Task</code> that had it's state changed.
	 * 
	 * @param task Last <code>Task</code> to change state.
	 */
	private void updateState( Task task ) {

		boolean taskRunning = hasTaskInExecution();

		if ( GridProcessState.FINISHED.equals( task.getState() ) ) {
			if ( taskRunning ) {
				state = GridProcessState.RUNNING;
			} else {
				state = hasAnyTaskFailed() ? GridProcessState.FAILED : GridProcessState.FINISHED;
			}
		} else if ( GridProcessState.FAILED.equals( task.getState() ) ) {
			state = taskRunning ? GridProcessState.RUNNING : GridProcessState.FAILED;
		}
	}

	/**
	 * Searches for any task that is either in state
	 * <code>ExecutionState.UNSTARTED</code> or
	 * <code>ExecutionState.RUNNING</code>.
	 * 
	 * @return True if any tasks are on either state.
	 */
	private boolean hasTaskInExecution() {

		for ( Task task : tasks ) {
			if ( GridProcessState.RUNNING.equals( task.getState() ) || GridProcessState.UNSTARTED.equals( task.getState() ) ) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Searches for any task that is in <code>ExecutionState.FAILED</code>
	 * state.
	 * 
	 * @return True if any tasks are on that state.
	 */
	private boolean hasAnyTaskFailed() {

		for ( Task task : tasks ) {
			if ( GridProcessState.FAILED.equals( task.getState() ) ) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Sets this <code>Job</code> as cancelled meaning that no more
	 * <code>Replica</code>s can be created from <code>Task</code>s that
	 * compose this <code>Job</code>.
	 */
	public void setAsCanceled() {

		if ( isRunning() ) {
			this.state = GridProcessState.CANCELLED;
			this.finalizationTime = System.currentTimeMillis();
		}
	}


	/**
	 * Get's <code>Task</code> according to the given taskid.
	 * 
	 * @param taskid ID of the task.
	 * @return The <code>Task</code> that has such id or null.
	 * @see Task
	 */
	public Task getTaskByID( int taskid ) {

		return tasks.get( taskid - 1 );
	}


	/**
	 * Get's the id for this <code>Job</code>.
	 * 
	 * @return ID of this <code>Job</code>.
	 */
	public int getJobId() {

		return jobid;
	}


	/**
	 * Get's the specification of this <code>Job</code>.
	 * 
	 * @return <code>JobSpec</code> determining the specification for this
	 *         <code>Job</code>.
	 */
	public JobSpecification getSpec() {

		return jobSpec;
	}

	/**
	 * Returns the current state of this <code>Job</code>.
	 * 
	 * @return State of this <code>Job</code>.
	 */
	public GridProcessState getState() {

		return state;
	}


	/**
	 * Return the timestamp on which the job was added
	 * 
	 * @return timestamp in milliseconds since epoch
	 * @see System#currentTimeMillis()
	 */
	public long getCreationTime() {
		return this.creationTime;
	}

	/**
	 * Return the timestamp on which job was finished
	 * 
	 * @return timestamp in milliseconds since epoch
	 * @see System#currentTimeMillis()
	 */
	public long getFinalizationTime() {
		return finalizationTime;
	}

	/**
	 * Create's a new <code>Replica</code> of the <code>Task</code>
	 * corresponding to the given id.
	 * 
	 * @param taskid ID of the <code>Task</code>.
	 * @param canReplicate 
	 * @return Newly created <code>Replica</code>.
	 */
	private GridProcess createNewExecution( int taskid ) {

		Task task = getTaskByID( taskid );
		if ( task != null ) {
			if ( isRunning() ) {
				this.state = GridProcessState.RUNNING;
				return task.createNewReplica();
			}
		}

		return null;
	}


	/**
	 * Verifies if this <code>Job</code> is in state
	 * <code>ExecutionState.RUNNING</code> or
	 * <code>ExecutionState.UNSTARTED</code>.
	 * 
	 * @return <code>true</code> if it is.
	 */
	public boolean isRunning() {

		return GridProcessState.RUNNING.equals( state ) || GridProcessState.UNSTARTED.equals( state );
	}


	public List<Task> getTasks() {
		
		Set<Task> availableTasks = null;
		
		availableTasks = new TreeSet<Task>(new Comparator<Task>(){
			public int compare(Task t1, Task t2) {
				return new Integer(t1.getTaskid()).compareTo(new Integer(t2.getTaskid()));
			}
		});

		if (this.tasks != null) {
			availableTasks.addAll(this.tasks);
			return new ArrayList<Task>(availableTasks);
		}
		
		return null;
	}


	@Override
	public String toString() {

		return "" + getJobId();
	}

	public void replicaPhaseUpdate( GridProcessHandle replicaHandle, GridProcessPhase newPhase ) {

		Task task = getTaskByID( replicaHandle.getTaskID() );
		if ( task != null ) {
			task.replicaPhaseUpdate( replicaHandle, newPhase );
		}
	}
	
	public Collection<WorkerEntry> getAvailableWorkers() {
		return getAvailableWorkers(null);
	}

	public Collection<WorkerEntry> getAvailableWorkers(Comparator<WorkerEntry> comparator) {
		Collection<WorkerEntry> returnValue = new LinkedHashSet<WorkerEntry>();
		
		if(comparator == null){
			returnValue = new LinkedHashSet<WorkerEntry>();
		}
		else{
			returnValue = new PriorityQueue<WorkerEntry>(tasks.size(), comparator);
		}		
		
		for (Request request : this.requests.values()) {
			for (WorkerEntry entry : request.getWorkers()) {
				
				//TODO id UP ?/if (entry.isUp() && !entry.allocated() && !blackListedWorkers.contains(entry)) {
				if (entry.isUp() && !entry.allocated() && !blackListedWorkers.contains(entry)) {
					returnValue.add(entry);
				}
			}
		}		
		
		return returnValue;
	}


	public GridProcess createAndAllocateExecution(int taskid, WorkerEntry chosenWorker) {
		if ( isRunning() ) {
			GridProcess execution = createNewExecution( taskid );
			
			if (execution != null) {
				execution.allocate( chosenWorker );
				return execution;
			}
		}

		return null;
		
	}


	public Collection<WorkerEntry> getDeallocatedWorkerEntries() {
		Set<WorkerEntry> returnValue = new LinkedHashSet<WorkerEntry>();
		
		for (Request request : requests.values()) {
			for (WorkerEntry entry : request.getWorkers()) {
				if (!entry.allocated()) {
					returnValue.add(entry);
				}
			}
		}
		
		return returnValue;
	}

	public void addRequest(RequestSpecification requestSpec, String peerID) {
		Request request = new Request(requestSpec, peerID);
		this.requests.put(requestSpec.getRequestId(), request);
	}
	
	/**
	 * Remove the requests made on a peer
	 * @param peerEntry
	 */
	public void removeRequests(String peerID) {
		
		List<Request> values = new ArrayList<Request>(this.requests.values());
		
		for (Request request : values) {
			if (request.getPeerID().equals(peerID)) {
				this.requests.remove(request.getSpecification().getRequestId());
			}
		}
	}
	
	public void removeRequest(Long idRequest) {
		this.requests.remove(idRequest);
	}
	
	public Collection<Request> getRequests() {
		return this.requests.values();
	}
	
	public void finishRequests() {
		this.requests = CommonUtils.createSerializableMap();
	}


	public WorkerEntry addWorker(long id, WorkerSpecification workerSpec, String workerID) {
		Request request = requests.get(id);
		WorkerEntry workerEntry = new WorkerEntry(workerSpec, request, workerID);
		request.addWorker(workerEntry);
		return workerEntry;
	}

	public void unwantWorker(WorkerEntry workerEntry) {
		this.blackListedWorkers.add(workerEntry);
	}

	public Request getRequest(long requestId) {
		return requests.get(requestId);
	}
	
	public boolean hasUnallocatedTasks() {
		
		for (Task task : this.tasks) {
			if (task.getState().equals(GridProcessState.UNSTARTED) || (!task.getState().equals(GridProcessState.FINISHED) 
					&& !task.hasRunnigGridProcess())) {
				return true;
			}
		}
		return false;
	}
	
	public Set<WorkerEntry> getBlackListedWorkers() {
		return blackListedWorkers;
	}


	public int compareTo(Job o) {
		return jobid - o.jobid;
	}
}
