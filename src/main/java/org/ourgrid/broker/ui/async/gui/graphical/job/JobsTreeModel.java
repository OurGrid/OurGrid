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
package org.ourgrid.broker.ui.async.gui.graphical.job;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.CommonUtils;


/**
 * This class generate the Job Tree. It access a </code>SynchronizedEBJobManager</code>
 * to the necessary information. It also has maps to store all jobs, tasks and
 * replicas that have already been inserted in tree to prevent exhausting
 * algorithms to verify if jobs, tasks and replicas already had been inserted in
 * the tree.
 */
public class JobsTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	/** The Root Node */
	private DefaultMutableTreeNode jobRoot;

	/** All jobs that already had been added in Tree */
	private Map<String,DefaultMutableTreeNode> mapNodeJobs;

	/** All tasks that already had been added in Tree */
	private Map<String,DefaultMutableTreeNode> mapNodeTasks;

	/** All replicas that already had been added in Tree */
	private Map<String,DefaultMutableTreeNode> mapNodeReplicas;


	public JobsTreeModel() {

		super( new DefaultMutableTreeNode( "Jobs" ) );
		jobRoot = (DefaultMutableTreeNode) getRoot();
		mapNodeJobs = CommonUtils.createSerializableMap();
		mapNodeTasks = CommonUtils.createSerializableMap();
		mapNodeReplicas = CommonUtils.createSerializableMap();
	}


	/**
	 * Get job's list from BrokerUIServices and update the tree
	 */
	
	public void refreshTree( Map<Integer,JobStatusInfo> jobsList ) {

		updateTree( jobsList );
	}


	private void updateTree( Map<Integer,JobStatusInfo> allJobs ) {

		Set<String> knownJobs = new LinkedHashSet<String>( mapNodeJobs.keySet() );

		// Removing jobs that were deleted
		synchronized ( this ) {
			for ( String jobID : knownJobs ) {
				int id = Integer.parseInt( jobID );

				// Job has been removed
				if ( !allJobs.containsKey( id ) ) {
					DefaultMutableTreeNode jobNode = mapNodeJobs.remove( jobID );
					this.removeNodeFromParent( jobNode );
				}
			}
			if ( knownJobs.isEmpty() ) {
				mapNodeTasks.clear();
				mapNodeReplicas.clear();
				mapNodeJobs.clear();
			}
		}

		// Updating known jobs.
		for ( JobStatusInfo job : allJobs.values() ) {
			DefaultMutableTreeNode jobNode = updateJobNode( job );

			for ( TaskStatusInfo task : job.getTasks() ) {
				DefaultMutableTreeNode taskNode = updateTaskNode( jobNode, task );

				for ( GridProcessStatusInfo replica : task.getGridProcesses() ) {
					updateReplicaNode( taskNode, replica );
				}
			}
		}
	}


	private DefaultMutableTreeNode updateJobNode( JobStatusInfo job ) {

		DefaultMutableTreeNode jobNode = mapNodeJobs.get( job.getJobId() + "" );
		String jobInfo = getJobInfo( job );

		if ( jobNode == null ) {
			jobNode = new DefaultMutableTreeNode( jobInfo );
			this.insertNodeInto( jobNode, jobRoot, jobRoot.getChildCount() );
			mapNodeJobs.put( job.getJobId() + "", jobNode );
		} else {
			jobNode.setUserObject( jobInfo );
		}
		return jobNode;
	}


	private DefaultMutableTreeNode updateTaskNode( DefaultMutableTreeNode jobNode, TaskStatusInfo task ) {

		final String id = task.getJobId() + "." + task.getTaskId();
		DefaultMutableTreeNode taskNode = mapNodeTasks.get( id );
		String taskInfo = getTaskInfo( task );

		if ( taskNode == null ) {
			taskNode = new DefaultMutableTreeNode( taskInfo );
			this.insertNodeInto( taskNode, jobNode, jobNode.getChildCount() );
			mapNodeTasks.put( id, taskNode );
		} else {
			taskNode.setUserObject( taskInfo );
		}
		return taskNode;
	}


	private void updateReplicaNode( DefaultMutableTreeNode taskNode, GridProcessStatusInfo replica ) {

		DefaultMutableTreeNode replicaNode = mapNodeReplicas.get( replica.getJobId() + "." + replica.getTaskId() + "."
				+ replica.getId() );
		String replicaInfo = getReplicaInfo( replica );

		if ( replicaNode == null ) {
			replicaNode = new DefaultMutableTreeNode( replicaInfo );
			this.insertNodeInto( replicaNode, taskNode, taskNode.getChildCount() );
			mapNodeReplicas.put( replica.getJobId() + "." + replica.getTaskId() + "." + replica.getId(), replicaNode );
		} else {
			replicaNode.setUserObject( replicaInfo );
		}
	}


	private String getReplicaInfo( GridProcessStatusInfo replica ) {

		WorkerSpecification workerSpec = replica.getWorkerInfo().getWorkerSpec();

		if ( workerSpec != null ) {
			return "Replica " + replica.getId() + ":"
					+ formatStatus( workerSpec.toString(), replica.getState(), true );
		}

		return "Replica " + replica.getId() + ":" + formatStatus( "", replica.getState(), true );
	}


	private String getTaskInfo( TaskStatusInfo task ) {

		return "Task " + task.getTaskId() + ":" 
			+ formatStatus( "", task.getState(), false );
	}


	private String getJobInfo( JobStatusInfo job ) {

		return job.getSpec().getLabel() != null ? "Job " + job.getJobId() + ": " + job.getSpec().getLabel() + "  ["
				+ JobStatusInfo.getState(job.getState()) + "]" : "Job " + job.getJobId() 
				+ ": [" + JobStatusInfo.getState(job.getState()) + "]";
	}


	/**
	 * Return a formated string with task status
	 * 
	 * @param workerName the grid machine name that is running the task
	 * @param status the status of the task
	 * @param isReplica tells if the task running is a replica
	 * @return A string with the formated task status
	 */
	private static String formatStatus( String workerName, String status, boolean isReplica ) {

		String statusFormated = "";
		if ( status.equals( "Running" ) && isReplica )
			statusFormated = " [" + status + "] at: " + workerName;
		else
			statusFormated = " [" + status + "]";
		return statusFormated;
	}


	/** This method is friendly because test needs a reference to Node Root */
	DefaultMutableTreeNode getJobRoot() {

		return jobRoot;
	}

}
