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
package org.ourgrid.common.interfaces.to;

import java.io.Serializable;

/**
 * This type must be used to uniquely identify a replica in the system.
 * 
 * @since 4.0
 */
public class GridProcessHandle implements Serializable, Comparable<GridProcessHandle> {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 40L;

	/**
	 * The ID of the job that holds the replica to which this handle is
	 * associated.
	 */
	private int jobID;

	/**
	 * The ID of the task that holds the replica to which this handle is
	 * associated.
	 */
	private int taskID;

	/**
	 * The ID of the replica to which this handle is associated.
	 */
	private int replicaID;
	
	public GridProcessHandle() {}


	/**
	 * Constructs a new instance of this type, based on the given Job, Task and
	 * Replica IDs.
	 * 
	 * @param jobid The ID of the job that holds the replica to which this
	 *        handle is associated.
	 * @param taskid The ID of the task that holds the replica to which this
	 *        handle is associated.
	 * @param replicaid The ID of the replica to which this handle is
	 *        associated.
	 */
	public GridProcessHandle( int jobid, int taskid, int replicaid ) {

		jobID = jobid;
		taskID = taskid;
		replicaID = replicaid;
	}


	/**
	 * Gets the ID of the job that holds the replica to which this handle is
	 * associated.
	 * 
	 * @return The ID of the job that holds the replica to which this handle is
	 *         associated.
	 */
	public int getJobID() {

		return jobID;
	}


	/**
	 * Gets the ID of the replica to which this handle is associated.
	 * 
	 * @return The ID of the replica to which this handle is associated.
	 */
	public int getReplicaID() {

		return replicaID;
	}


	/**
	 * Gets the ID of the task that holds the replica to which this handle is
	 * associated.
	 * 
	 * @return The ID of the task that holds the replica to which this handle is
	 *         associated.
	 */
	public int getTaskID() {

		return taskID;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( obj instanceof GridProcessHandle ) {
			GridProcessHandle otherHandle = (GridProcessHandle) obj;
			return otherHandle.getJobID() == this.getJobID() && otherHandle.getTaskID() == this.getTaskID()
					&& otherHandle.getReplicaID() == this.getReplicaID();
		}
		return false;
	}


	@Override
	public int hashCode() {

		return (this.getJobID() + "." + this.getTaskID() + "." + this.getReplicaID()).hashCode();
	}


	@Override
	public String toString() {

		return getJobID() + "." + getTaskID() + "." + getReplicaID();
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public void setReplicaID(int replicaID) {
		this.replicaID = replicaID;
	}


	public int compareTo(GridProcessHandle o) {
		if (getJobID() > o.getJobID()) {
			return 1;
		} 
		
		if (getJobID() < o.getJobID()) {
			return -1;
		} 
		
		if (getTaskID() > o.getTaskID()) {
			return 1;
		} 
		
		if (getTaskID() < o.getTaskID()) {
			return -1;
		} 
		
		return getReplicaID() - o.getReplicaID(); 
	}
}
