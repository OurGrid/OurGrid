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
package org.ourgrid.broker.ui.async.gui.graphical.workers;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.common.util.CommonUtils;


/**
 * This class generate the Worker Tree.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class WorkersTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	/** The Root Node */
	private DefaultMutableTreeNode jobRoot;

	/** All peers that already had been added in Tree */
	private Map<String,DefaultMutableTreeNode> mapNodeJobs;

	/** All workers that already had been added in Tree */
	private Map<String,DefaultMutableTreeNode> mapNodeWorkers;


	/**
	 * Default empty constructor.
	 */
	public WorkersTreeModel() {

		super( new DefaultMutableTreeNode( "Workers" ) );
		jobRoot = (DefaultMutableTreeNode) getRoot();
		mapNodeJobs = CommonUtils.createSerializableMap();
		mapNodeWorkers = CommonUtils.createSerializableMap();
	}


	/**
	 * Get workers' list from BrokerUIServices and update the tree.
	 * @param map A map with new information.
	 */
	public void refreshTree( Map<Integer,Set<WorkerStatusInfo>> map ) {
		updateTree( map );
	}


	/**
	 * Updates tree components.
	 * @param map A map with new information.
	 */
	private void updateTree( Map<Integer,Set<WorkerStatusInfo>> map ) {

		Set<String> knownJobs = new LinkedHashSet<String>( mapNodeJobs.keySet() );

		// Removing jobs that were deleted
		synchronized ( this ) {
			for ( String jobID : knownJobs ) {
				int id = Integer.parseInt( jobID );

				// Job has been removed
				if ( !map.containsKey( id ) ) {
					DefaultMutableTreeNode jobNode = mapNodeJobs.remove( jobID );
					this.removeNodeFromParent( jobNode );
				}
			}
			if ( knownJobs.isEmpty() ) {
				mapNodeJobs.clear();
				mapNodeWorkers.clear();
			}
			
			Set<String> workers = new LinkedHashSet<String>();
			for ( Set<WorkerStatusInfo> workersSet : map.values() ) {
				for ( WorkerStatusInfo workerStatusInfo : workersSet ) {
					workers.add( workerStatusInfo.getWorkerSpec().getUserAndServer() );
				}
			}
			
			Set<String> knownWorkers = new LinkedHashSet<String>( mapNodeWorkers.keySet() );
			for ( String knownWorker : knownWorkers ) {
				if(!workers.contains( knownWorker )){
					DefaultMutableTreeNode workerNode = mapNodeWorkers.remove( knownWorker );
					this.removeNodeFromParent( workerNode );
				}
			}
		}
		
		for ( Entry<Integer,Set<WorkerStatusInfo>> entry : map.entrySet() ) {
			Integer jobID = entry.getKey();
			
			updateJobNode( jobID );
			
			Set<WorkerStatusInfo> workersInfo = entry.getValue();
			for ( WorkerStatusInfo workerStatusInfo : workersInfo ) {
				DefaultMutableTreeNode taskNode = updateWorkerNode( jobID, workerStatusInfo );
			}
		}
	}


	/**
	 * Update job node information.
	 * @param jobID
	 * @return
	 */
	private DefaultMutableTreeNode updateJobNode( Integer jobID ) {

		DefaultMutableTreeNode jobNode = mapNodeJobs.get( jobID.toString() );
		String jobInfo = "Job ".concat( jobID.toString() );

		if ( jobNode == null ) {
			jobNode = new DefaultMutableTreeNode( jobInfo );
			this.insertNodeInto( jobNode, jobRoot, jobRoot.getChildCount() );
			mapNodeJobs.put( jobID.toString(), jobNode );
		} else {
			jobNode.setUserObject( jobInfo );
		}
		return jobNode;
	}

	
	/**
	 * Update worker node information.
	 * @param jobID
	 * @param workerStatusInfo
	 * @return
	 */
	private DefaultMutableTreeNode updateWorkerNode( Integer jobID, WorkerStatusInfo workerStatusInfo ) {

		final String id = jobID + "." + workerStatusInfo.getWorkerID();
		DefaultMutableTreeNode workerNode = mapNodeWorkers.get( id );
		String workerInfo = workerStatusInfo.getWorkerSpec().getUserAndServer();

		if ( workerNode == null ) {
			workerNode = new DefaultMutableTreeNode( workerInfo );
			DefaultMutableTreeNode jobNode = mapNodeJobs.get( jobID.toString() );
			this.insertNodeInto( workerNode, jobNode, jobNode.getChildCount() );
			mapNodeWorkers.put( id, workerNode );
		} else {
			workerNode.setUserObject( workerInfo );
		}
		return workerNode;
	}


	/** This method is friendly because test needs a reference to Node Root */
	DefaultMutableTreeNode getJobRoot() {

		return jobRoot;
	}

}
