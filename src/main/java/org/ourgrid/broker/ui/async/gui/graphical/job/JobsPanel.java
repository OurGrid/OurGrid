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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.glite.jdl.JobAd;
import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.broker.status.GridProcessStatusInfoResult;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.JobsPackage;
import org.ourgrid.common.util.CommonUtils;

/**
 * This class contains SchedulerTreeModel and create a JTree with it.
 */
public class JobsPanel implements TreeSelectionListener {

	/** Keys to map properties to show in Job/Task/Replica properties area */
	private final static String LINE_SEPARATOR = System.getProperty( "line.separator" );

	private static final String GRIDPROCESS = "GRID PROCESS";

	private static final String TASK = "TASK";

	private static final String JOB = "JOB";

	private static final String ASSIGNED_TO = "ASSIGNED TO";
	
	private static final String DURATION_TIME = "DURATION TIME";

	private static final String STATE = "STATE";

	private static final String PHASE = "PHASE";

	private static final String INIT = "INIT";

	private static final String REMOTE = "REMOTE";

	private static final String FINAL = "FINAL";

	private static final String TOTAL_GRIDPROCESS = "TOTAL GRID PROCESSES";

	private static final String RUNNING_GRIDPROCESS = "RUNNING GRID PROCESSES";

	private static final String FINISHED_GRIDPROCESS = "FINISHED GRID PROCESSES";

	private static final String ABORTED_GRIDPROCESS = "ABORTED GRID PROCESSES";

	private static final String FAILED_GRIDPROCESS = "FAILED GRID PROCESSES";

	private static final String CANCELLED_GRIDPROCESS = "CANCELLED GRID PROCESSES";

	/*
	 * Added in 19/10/2007 @author Thiago Emmanuel Pereira,
	 * thiago.manel@gmail.com
	 */
	private static final String SABOTAGED_REPLICAS = "SABOTAGED REPLICAS";

	private static final String ACTUAL_FAILS = "ACTUAL FAILS";

	private static final String TASKSPEC = "TASKSPEC";

	private static final String ERROR_CAUSE = "ERROR CAUSE";

	private static final String REQUIREMENTS = "REQUIREMENTS";
	
	private static final String ANNOTATIONS = "ANNOTATIONS";

	private static final String NUMBER_OF_TASKS = "NUMBER OF TASKS";

	private static final String NUMBER_OF_WORKERS = "NUMBER OF WORKERS";

	private static final String DETAILED_ERROR_CAUSE = "DETAILED ERROR CAUSE";

	private static final String UNSTARTED_TASKS = "UNSTARTED TASKS";

	private static final String PROCESS_OUTPUT = "PROCESS OUTPUT";

	private static final String RUNNING_TASKS = "RUNNING TASKS";

	private static final String FINISHED_TASKS = "FINISHED TASKS";

	private static final String ABORTED_TASKS = "ABORTED TASKS";

	private static final String FAILED_TASKS = "FAILED TASKS";

	private static final String CANCELLED_TASKS = "CANCELLED TASKS";

	/** Panel thats adds JTree */
	private JPanel masterPanel;

	protected JSplitPane splitPane;

	/** Jobs TreeModel */
	private JobsTreeModel jtm;

	/** JTree */
	private JTree tree;

	/** JScrollPane to format JTree */
	private JScrollPane treeScroll;

	/** Properties Area */
	private JTextArea propertiesArea;

	/** PropertiesArea's Scroll */
	private JScrollPane propertiesScroll;

	private JPanel detailsPanel;

	private Map<GridProcessHandle,ReplicaProgressTable> replicasProgressTables;

	private JScrollPane progressScroll;

	protected JButton verticalButton;

	protected JButton horizontalButton;

	private JobsPackage jobsPackage;

	/**
	 * Creates the JobPanel
	 */
	public JobsPanel( ) {

		jobsPackage = null;
		replicasProgressTables = CommonUtils.createSerializableMap();

		masterPanel = new JPanel();
		masterPanel.setLayout( new BorderLayout() );
		masterPanel.setDoubleBuffered( true );

		jtm = new JobsTreeModel();

		tree = new JTree( jtm );
		tree.setCellRenderer( new JobsTreeCellRenderer() );
		tree.setLargeModel( true );
		tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		tree.addTreeSelectionListener( this );
		tree.setBorder( new LineBorder( Color.BLACK ) );
		tree.setDoubleBuffered( true );
		tree.setOpaque( true );

		treeScroll = new JScrollPane( tree );
		treeScroll.setViewportView( tree );
		treeScroll.setPreferredSize( new Dimension( 300, 400 ) );
		treeScroll.setDoubleBuffered( true );

		detailsPanel = new JPanel();
		detailsPanel.setLayout( new BorderLayout() );
		detailsPanel.setDoubleBuffered( true );

		propertiesArea = new JTextArea();
		//propertiesArea.setBorder( new LineBorder( Color.BLACK ) );
		propertiesArea.setEditable( false );
		propertiesArea.setFont(new Font("Monospaced", 
				propertiesArea.getFont().getStyle(), propertiesArea.getFont().getSize()));
		propertiesArea.setBackground(this.masterPanel.getBackground());
		propertiesScroll = new JScrollPane( propertiesArea );
		propertiesScroll.setDoubleBuffered( true );
		propertiesScroll.setPreferredSize( new Dimension( 200, 300 ) );

		detailsPanel.add( propertiesScroll, BorderLayout.CENTER );

		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout( 1, 1 ) );
		panel.setDoubleBuffered( true );

		progressScroll = new JScrollPane();
		panel.add( progressScroll );

		splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		splitPane.setDoubleBuffered( true );

		JPanel splitButtonsPanel = new JPanel();
		verticalButton = new JButton( "Vertically" );
		horizontalButton = new JButton( "Horizontally" );
		verticalButton.setEnabled( false );

		verticalButton.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {

				splitPane.setOrientation( JSplitPane.HORIZONTAL_SPLIT );
				splitPane.setDividerLocation( 0.5 );
				verticalButton.setEnabled( false );
				horizontalButton.setEnabled( true );
			}
		} );

		horizontalButton.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {

				splitPane.setOrientation( JSplitPane.VERTICAL_SPLIT );
				splitPane.setDividerLocation( 0.5 );
				verticalButton.setEnabled( true );
				horizontalButton.setEnabled( false );
			}
		} );

		splitButtonsPanel.add( new JLabel( "Divide panel: " ) );
		splitButtonsPanel.add( verticalButton );
		splitButtonsPanel.add( horizontalButton );

		splitPane.setLeftComponent( treeScroll );
		splitPane.setRightComponent( detailsPanel );

		masterPanel.add( splitButtonsPanel, BorderLayout.NORTH );
		masterPanel.add( splitPane, BorderLayout.CENTER );
		masterPanel.setDoubleBuffered( true );

		configurePopupMenu();
	}


	private void configurePopupMenu() {

		// popup
		final JPopupMenu menu = new JPopupMenu();

		// Create and add a menu item
		JMenuItem item1 = new JMenuItem( "Expand all" );
		item1.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent evt ) {

				expandAllActionPerformed();
			}
		} );
		menu.add( item1 );
		JMenuItem item2 = new JMenuItem( "Collapse all" );
		item2.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent evt ) {

				collapseAllActionPerformed();
			}
		} );
		menu.add( item2 );

		// Set the component to show the popup menu
		tree.addMouseListener( new MouseAdapter() {

			@Override
			public void mousePressed( MouseEvent evt ) {

				if ( evt.isPopupTrigger() ) {
					menu.show( evt.getComponent(), evt.getX(), evt.getY() );
				}
			}


			@Override
			public void mouseReleased( MouseEvent evt ) {

				if ( evt.isPopupTrigger() ) {
					menu.show( evt.getComponent(), evt.getX(), evt.getY() );
				}
			}
		} );
	}


	protected void collapseAllActionPerformed() {

		for ( int i = tree.getRowCount() - 1; i >= 0; i-- ) {
			tree.collapseRow( i );
		}
	}


	protected void expandAllActionPerformed() {

		for ( int i = 0; i < tree.getRowCount(); i++ ) {
			tree.expandRow( i );
		}
	}


	/** Returns JTree's Panel */
	public JPanel getPanel() {

		return masterPanel;
	}


	public void refresh( /*Collection<JobManagerUpdatedArea> updatedAreas*/ ) {
/*
		jobManagerReflectionImpl.lock();
		try {
			JobManager image = jobManagerReflectionImpl.getImage();
			if ( image != null ) {

				if ( updatedAreas.contains( JobManagerUpdatedArea.JOBTREE ) ) {
					synchronized ( jtm ) {
						jtm.refreshTree( image.getJobs() );
					}
					fillDetails();
				}

				if ( updatedAreas.contains( JobManagerUpdatedArea.TRANSFER_PROGRESS ) ) {

					for ( ReplicaProgressTable state : replicasProgressTables.values() ) {
						state.markInactive();
					}

					for ( Job job : image.getJobs().values() ) {
						for ( Task task : job.getTasks() ) {
							for ( Replica replica : task.getReplicas() ) {
								ReplicaProgressTable replicaProgressTable;
								if ( replicasProgressTables.containsKey( replica.getHandle() ) ) {
									replicaProgressTable = replicasProgressTables.get( replica.getHandle() );
								} else {
									replicaProgressTable = new ReplicaProgressTable( replica );
									replicasProgressTables.put( replica.getHandle(), replicaProgressTable );
								}
								replicaProgressTable.update();
							}
						}
					}

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

					ReplicaHandle selectedReplica = getSelectedReplica( node );
					if ( selectedReplica != null ) {
						showTransfersProgress( selectedReplica, true );
					} else {
						progressScroll.setViewportView( null );
					}

					Iterator<Entry<ReplicaHandle,ReplicaProgressTable>> it = replicasProgressTables.entrySet()
						.iterator();
					while ( it.hasNext() ) {
						ReplicaProgressTable value = it.next().getValue();
						if ( !value.isActive() ) {
							it.remove();
						}
					}
				}
			}
		} finally {
			jobManagerReflectionImpl.unlock();
		}
		*/
	}


	private void fillDetails() {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		int selectedJob = getSelectedJob( node );
		if ( selectedJob != -1 ) {
			showPropertiesOfJob( selectedJob );
		} else {
			
			TaskHandle selectedTask = getSelectedTask( node );
			if ( selectedTask != null ) {
				showPropertiesOfTask( selectedTask );
			} else {
				GridProcessHandle selectedReplica = getSelectedReplica( node );
				if ( selectedReplica != null ) {
					showPropertiesOfReplica( selectedReplica );
				}
			}
			
		}
		
	}


	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged( TreeSelectionEvent e ) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if ( node == null ) {
			propertiesArea.setText( "" );
			return;
		}

		//setSelectedJob(getSelectedJob( node ));
		
		resetDetailsPanel();

		String nodeInfo = (String) node.getUserObject();

		if ( nodeInfo.equals( "Jobs" ) ) {
			propertiesArea.setText( "Click on a job for information" );
			return;
		}

		fillDetails();
	}


	private int getSelectedJob( DefaultMutableTreeNode node ) {

		if ( node == null ) {
			return -1;
		}

		String nodeInfo = (String) node.getUserObject();

		if ( nodeInfo.startsWith( "Job " ) ) {
			return getJobId( nodeInfo );
		}
		return -1;
	}


	private TaskHandle getSelectedTask( DefaultMutableTreeNode node ) {

		if ( node == null ) {
			return null;
		}

		String nodeInfo = (String) node.getUserObject();

		if ( nodeInfo.startsWith( "Task" ) ) {
			String jobInfo = (String) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
			return new TaskHandle( getJobId( jobInfo ), getTaskId( nodeInfo )); //TODO it is not 0
		}
		return null;
	}


	private GridProcessHandle getSelectedReplica( DefaultMutableTreeNode node ) {

		if ( node == null ) {
			return null;
		}

		String nodeInfo = (String) node.getUserObject();

		if ( nodeInfo.startsWith( "Replica" ) ) {
			DefaultMutableTreeNode taskNode = (DefaultMutableTreeNode) node.getParent();
			DefaultMutableTreeNode jobNode = (DefaultMutableTreeNode) taskNode.getParent();
			return new GridProcessHandle( getJobId( (String) jobNode.getUserObject() ), getTaskId( (String) taskNode
				.getUserObject() ), getReplicaId( nodeInfo ) );
		}
		return null;
	}


	private void resetDetailsPanel() {

		detailsPanel.removeAll();
		detailsPanel.add( propertiesScroll, BorderLayout.CENTER );
		detailsPanel.validate();
	}


	/**
	 * Search properties of a job and put in properties area.
	 * 
	 * @param selectedJob Job Label in tree.
	 * @throws RemoteException
	 */
	private synchronized void showPropertiesOfJob( int selectedJob ) {

		//jobManagerReflectionImpl.lock();
		//JobManager jobManager = jobManagerReflectionImpl.getImage();

//		if ( jobManager == null ) {
//			jobManagerReflectionImpl.unlock();
//			return;
//		}

		if ( jobsPackage == null ) {
			return;
		}

		JobStatusInfo job = jobsPackage.getJobs().get(selectedJob);
		if ( job != null ) {
			Map<String,String> p = CommonUtils.createSerializableMap();
			p.put( JOB, String.valueOf( job.getJobId() ) );
			p.put( REQUIREMENTS, job.getSpec().getRequirements() );
			p.put( STATE, JobStatusInfo.getState(job.getState()) );
			p.put( NUMBER_OF_TASKS, String.valueOf( job.getTasks().size() ) );
			
			long durationTime = 0;
			
			if (job.getState() == JobStatusInfo.UNSTARTED || job.getState() == JobStatusInfo.RUNNING) {
				durationTime = System.currentTimeMillis() - job.getCreationTime();
			} else {
				durationTime = job.getFinalizationTime() - job.getCreationTime();
			}
			
			p.put( DURATION_TIME, new Long(durationTime).toString());
			
			
			StringBuffer annotations = new StringBuffer();
			for ( Entry<String, String> tag : job.getSpec().getAnnotations().entrySet() ) {
			
				annotations.append(tag.getKey() + "=" + tag.getValue() );				
			}
			
			if (annotations.length() > 0) {
				annotations.deleteCharAt(annotations.length()-1);
			}
			
			p.put( ANNOTATIONS,  annotations.toString());


			int runningReplicas = 0;
			int finishedReplicas = 0;
			int abortedReplicas = 0;
			int cancelledReplicas = 0;
			int failedReplicas = 0;
			int sabotagedReplicas = 0;
			int unstartedTasks = 0;
			int runningTasks = 0;
			int finishedTasks = 0;
			int abortedTasks = 0;
			int cancelledTasks = 0;
			int failedTasks = 0;

			for ( TaskStatusInfo simpleTask : job.getTasks() ) {

				if (simpleTask.getState().equals(GridProcessState.UNSTARTED.toString())) {
					unstartedTasks++;
				} else if (simpleTask.getState().equals(GridProcessState.RUNNING.toString())) {
					runningTasks++;
				} else if (simpleTask.getState().equals(GridProcessState.FINISHED.toString())) {
					finishedTasks++;
				} else if (simpleTask.getState().equals(GridProcessState.ABORTED.toString())) {
					abortedTasks++;
				} else if (simpleTask.getState().equals(GridProcessState.CANCELLED.toString())) {
					cancelledTasks++;
				} else if (simpleTask.getState().equals(GridProcessState.FAILED.toString())) {
					failedTasks++;
				} 

				for ( GridProcessStatusInfo simpleReplica : simpleTask.getGridProcesses() ) {
					
					if (simpleReplica.getState().equals(GridProcessState.RUNNING.toString())) {
						runningReplicas++;
					} else if (simpleReplica.getState().equals(GridProcessState.FINISHED.toString())) {
						finishedReplicas++;
					} else if (simpleReplica.getState().equals(GridProcessState.ABORTED.toString())) {
						abortedReplicas++;
					} else if (simpleReplica.getState().equals(GridProcessState.FAILED.toString())) {
						failedReplicas++;
					} else if (simpleReplica.getState().equals(GridProcessState.CANCELLED.toString())) {
						cancelledReplicas++;
					}
				}

			}

			p.put( UNSTARTED_TASKS, String.valueOf( unstartedTasks ) );
			p.put( RUNNING_TASKS, String.valueOf( runningTasks ) );
			p.put( FINISHED_TASKS, String.valueOf( finishedTasks ) );
			p.put( ABORTED_TASKS, String.valueOf( abortedTasks ) );
			p.put( FAILED_TASKS, String.valueOf( failedTasks ) );
			p.put( CANCELLED_TASKS, String.valueOf( cancelledTasks ) );

			p.put( RUNNING_GRIDPROCESS, String.valueOf( runningReplicas ) );
			p.put( FINISHED_GRIDPROCESS, String.valueOf( finishedReplicas ) );
			p.put( ABORTED_GRIDPROCESS, String.valueOf( abortedReplicas ) );
			p.put( FAILED_GRIDPROCESS, String.valueOf( failedReplicas ) );
			p.put( CANCELLED_GRIDPROCESS, String.valueOf( cancelledReplicas ) );
			p.put( SABOTAGED_REPLICAS, String.valueOf( sabotagedReplicas ) );

			if ( job.getState() != JobStatusInfo.CANCELLED ) {
				p.put( NUMBER_OF_WORKERS, String.valueOf( runningReplicas ) );
			} else {
				p.put( NUMBER_OF_WORKERS, String.valueOf( 0 ) );
			}

			writeJobInTextArea( p );
			//jobManagerReflectionImpl.unlock();
			return;
		}
		//jobManagerReflectionImpl.unlock();
		propertiesArea.setText( "" );
		
	}


	/**
	 * Search the Task properties and put in properties area.
	 * 
	 * @param taskHandle TODO
	 * @param taskId Task label in tree.
	 * @throws RemoteException
	 */
	
	private synchronized void showPropertiesOfTask( TaskHandle taskHandle ) {

//		jobManagerReflectionImpl.lock();
//		if ( jobManagerReflectionImpl.getImage() == null ) {
//			jobManagerReflectionImpl.unlock();
//			return;
//		}
		
		
		TaskStatusInfo task = getTask( taskHandle );

		if ( task != null ) {
			Map<String,String> p = CommonUtils.createSerializableMap();
			p.put( TASK, String.valueOf( task.getTaskId() ) );
			p.put( JOB, String.valueOf( task.getJobId() ) );
			p.put( STATE, task.getState() );
			p.put( TOTAL_GRIDPROCESS, String.valueOf( task.getGridProcesses().size() ) );
			p.put( RUNNING_GRIDPROCESS, String.valueOf( task.getNumberOfRunningReplicas() ) );
			p.put( ACTUAL_FAILS, String.valueOf( task.getActualFails() ) );
			String jdlExpression = task.getSpec().getExpression();
			if(jdlExpression != null && !(jdlExpression.length() == 0)){
				JobAd jobAd;
				try {
					jobAd = new JobAd(task.getSpec().getExpression());
					p.put( TASKSPEC,  jobAd.toString( true, true ));
				} catch ( Exception e ) {
					p.put( TASKSPEC, "Error parsing JDL: " + e.getMessage() );
				}
			}else{
				p.put( TASKSPEC, task.getSpec().toString() );
			}

			writeTaskInTextArea( p );
//			jobManagerReflectionImpl.unlock();
			return;
		}

//		jobManagerReflectionImpl.unlock();
		propertiesArea.setText( "" );
	}


	
	private TaskStatusInfo getTask( TaskHandle taskHandle ) {

		JobStatusInfo job = getJob( taskHandle.getJobID() );
		return job == null ? null : job.getTaskByID( taskHandle.getTaskID() );
	}


	private JobStatusInfo getJob( int jobID ) {

		JobStatusInfo job = jobsPackage.getJobs().get( jobID );
		return job;
	}


	/**
	 * Search the Replica properties and put in properties Area.
	 * 
	 * @param replicaHandle TODO
	 * @param jobID Job label in tree.
	 * @param taskID Task label in tree.
	 * @param replicaID Replica label in tree.
	 * @throws RemoteException
	 */
	private synchronized void showPropertiesOfReplica( GridProcessHandle replicaHandle ) {

//		jobManagerReflectionImpl.lock();
//		JobManager jobManager = jobManagerReflectionImpl.getImage();
//
//		if ( jobManager == null ) {
//			jobManagerReflectionImpl.unlock();
//			return;
//		}
		if (jobsPackage == null) return;

		GridProcessStatusInfo replica = getReplica( replicaHandle );

		if ( replica != null ) {
			Map<String,String> p = CommonUtils.createSerializableMap();
			p.put( GRIDPROCESS, String.valueOf( replica.getId() ) );
			p.put( TASK, String.valueOf( replica.getTaskId() ) );
			p.put( JOB, String.valueOf( replica.getJobId() ) );
			p.put( ASSIGNED_TO, replica.getWorkerInfo().getWorkerSpec().getUserAndServer() );
			String replicaState = replica.getState();
			
			p.put( STATE, replicaState );
			p.put( PHASE, replica.getCurrentPhase());

			if ( replicaState.equals(GridProcessState.FAILED.toString()) 
					|| replicaState.equals(GridProcessState.FINISHED.toString())) {
				final GridProcessStatusInfoResult replicaResult = replica.getReplicaResult();
				
				if ( replicaState.equals(GridProcessState.FAILED.toString()) ) {
					if ( replicaResult != null ) {
							String errorCause = replicaResult.getExecutionError();
							p.put( ERROR_CAUSE, errorCause );

							String detailedErrorCause = replicaResult.getExecutionErrorCause();
							if ( detailedErrorCause != null ) {
								p.put( DETAILED_ERROR_CAUSE, detailedErrorCause.toString() );
							}
					}
				} else if ( replicaState.equals(GridProcessState.FINISHED.toString())) {

					p.put( INIT, String.valueOf( replicaResult.getInitDataTime() ) );
					p.put( REMOTE, String.valueOf( replicaResult.getRemoteDataTime() ) );
					p.put( FINAL, String.valueOf( replicaResult.getFinalDataTime() ) );
				}

				ExecutorResult executorResult = replicaResult.getExecutorResult();
				if ( executorResult != null ) {
					p.put( PROCESS_OUTPUT, executorResult.toString() );
				}
			}

			writeReplicaInTextArea( p );
			showTransfersProgress( replica.getHandle(), false );
			//jobManagerReflectionImpl.unlock();
			return;
		}

		//jobManagerReflectionImpl.unlock();
		propertiesArea.setText( "" );
	}


	private GridProcessStatusInfo getReplica( GridProcessHandle replicaHandle ) {

		TaskStatusInfo task = getTask( new TaskHandle( replicaHandle.getJobID(), replicaHandle.getTaskID() ) );
		return task == null ? null : task.getReplicaByID( replicaHandle.getReplicaID() );
	}


	private void showTransfersProgress( GridProcessHandle handle, boolean alreadySelected ) {

		ReplicaProgressTable table = replicasProgressTables.get( handle );
		if ( table != null ) {
			progressScroll.setViewportView( table.getTable() );
			int newHeigth = 20 + table.getTable().getRowCount() * 20;
			if ( newHeigth > 200 ) {
				newHeigth = 200;
			}
			progressScroll.setPreferredSize( new Dimension( 300, newHeigth ) );

			if ( !alreadySelected ) {
				detailsPanel.removeAll();
				detailsPanel.add( progressScroll, BorderLayout.SOUTH );
				detailsPanel.add( propertiesScroll, BorderLayout.CENTER );
				detailsPanel.validate();
			}
			table.getTable().validate();
		}
	}


	/**
	 * Put all properties in properties area.
	 * 
	 * @param p Properties.
	 */
	private void writeReplicaInTextArea( Map<String,String> p ) {

		propertiesArea.setText( "" );

		StringBuffer sb = new StringBuffer();
		sb.append( " JOB: " + p.get( JOB ) );
		sb.append( LINE_SEPARATOR + " TASK: " + p.get( TASK ) );
		sb.append( LINE_SEPARATOR + " REPLICA: " + p.get( GRIDPROCESS ) );
		sb.append( LINE_SEPARATOR + " ASSIGNED TO: " + p.get( ASSIGNED_TO ) );
		sb.append( LINE_SEPARATOR + " STATE: " + p.get( STATE ) );
		sb.append( LINE_SEPARATOR + " " + PHASE + ": " + p.get( PHASE ) );

		if ( p.get( ERROR_CAUSE ) != null ) {
			sb.append( LINE_SEPARATOR + " -----" + LINE_SEPARATOR + " ERROR CAUSE: " + LINE_SEPARATOR
					+ p.get( ERROR_CAUSE ) );
		}

		if ( p.get( DETAILED_ERROR_CAUSE ) != null ) {
			sb.append( LINE_SEPARATOR + "" + LINE_SEPARATOR + " ADDITIONAL INFORMATION: " + LINE_SEPARATOR
					+ p.get( DETAILED_ERROR_CAUSE ) );
		}

		if ( p.get( INIT ) != null ) {

			sb.append( LINE_SEPARATOR + " -----" + LINE_SEPARATOR + " EXECUTION TIMES: " );
			sb.append( LINE_SEPARATOR + "    INIT PHASE: " + p.get( INIT ) + " ms." );
			sb.append( LINE_SEPARATOR + "    REMOTE PHASE: " + p.get( REMOTE ) + " ms." );
			sb.append( LINE_SEPARATOR + "    FINAL PHASE: " + p.get( FINAL ) + " ms." );
		}

		Object processOutput = p.get( PROCESS_OUTPUT );
		if ( processOutput != null ) {
			sb.append( LINE_SEPARATOR + " -----" + LINE_SEPARATOR + " PROCESS OUTPUT: " + LINE_SEPARATOR );
			sb.append( processOutput );
		}

		propertiesArea.setText( sb.toString() );
	}


	/**
	 * Put all properties in properties
	 * area.rid.broker.ui.gui.JobsPanel.getJobId(JobsPanel.java:436)
	 * 
	 * @param p Properties.
	 */
	private void writeTaskInTextArea( Map<String,String> p ) {

		propertiesArea.setText( "" );

		StringBuffer sb = new StringBuffer();
		sb.append( " JOB: " + p.get( JOB ) );
		sb.append( LINE_SEPARATOR + " TASK: " + p.get( TASK ) );
		sb.append( LINE_SEPARATOR + " TOTAL REPLICAS: " + p.get( TOTAL_GRIDPROCESS ) );
		sb.append( LINE_SEPARATOR + " RUNNING REPLICAS: " + p.get( RUNNING_GRIDPROCESS ) );
		sb.append( LINE_SEPARATOR + " ACTUAL FAILS: " + p.get( ACTUAL_FAILS ) );
		sb.append( LINE_SEPARATOR + " STATE: " + p.get( STATE ) );
		sb.append( LINE_SEPARATOR + "-----" + LINE_SEPARATOR + " TASK SPEC:" );
		sb.append( LINE_SEPARATOR + p.get( TASKSPEC ) );

		propertiesArea.setText( sb.toString() );
	}


	/**
	 * Put all properties in properties area.
	 * 
	 * @param p Properties.
	 */
	private void writeJobInTextArea( Map<String,String> p ) {

		propertiesArea.setText( "" );

		StringBuffer sb = new StringBuffer();
		sb.append( " Job	: " + p.get( JOB ) );

		String requirements = p.get( REQUIREMENTS );
		if ( requirements == null || requirements.equals( "" ) ) {
			requirements = "unspecified";
		}

		String annotations = p.get( ANNOTATIONS );
		if ( annotations == null || annotations.equals( "" ) ) {
			annotations = "unspecified";
		}		
		
		sb.append( LINE_SEPARATOR + " REQUIREMENTS: " + requirements );
		sb.append( LINE_SEPARATOR + " ANNOTATIONS: " + annotations );
		sb.append( LINE_SEPARATOR + " STATE: " + p.get( STATE ) );
		sb.append( LINE_SEPARATOR + "----" + LINE_SEPARATOR + "TASKS:" + LINE_SEPARATOR );
		sb.append( LINE_SEPARATOR + " NUMBER OF TASKS: " + p.get( NUMBER_OF_TASKS ) );
		sb.append( LINE_SEPARATOR + " UNSTARTED: " + p.get( UNSTARTED_TASKS ) );
		sb.append( LINE_SEPARATOR + " RUNNING: " + p.get( RUNNING_TASKS ) );
		sb.append( LINE_SEPARATOR + " FINISHED: " + p.get( FINISHED_TASKS ) );
		sb.append( LINE_SEPARATOR + " FAILED: " + p.get( FAILED_TASKS ) );
		sb.append( LINE_SEPARATOR + " CANCELLED: " + p.get( CANCELLED_TASKS ) );
		sb.append( LINE_SEPARATOR + "----" + LINE_SEPARATOR + "REPLICAS:" + LINE_SEPARATOR );
		sb.append( LINE_SEPARATOR + " RUNNING: " + p.get( RUNNING_GRIDPROCESS ) );
		sb.append( LINE_SEPARATOR + " FINISHED: " + p.get( FINISHED_GRIDPROCESS ) );
		sb.append( LINE_SEPARATOR + " ABORTED: " + p.get( ABORTED_GRIDPROCESS ) );
		sb.append( LINE_SEPARATOR + " FAILED: " + p.get( FAILED_GRIDPROCESS ) );
		sb.append( LINE_SEPARATOR + " CANCELLED: " + p.get( CANCELLED_GRIDPROCESS ) );
		sb.append( LINE_SEPARATOR + " SABOTAGED REPLICAS: " + p.get( SABOTAGED_REPLICAS ) );
		sb.append( LINE_SEPARATOR + "" + LINE_SEPARATOR + " CURRENT NUMBER OF WORKERS: " + p.get( NUMBER_OF_WORKERS ) );
		sb.append( LINE_SEPARATOR + "" + LINE_SEPARATOR + " DURATION TIME (ms): " + p.get( DURATION_TIME ) );
		
		propertiesArea.setText( sb.toString() );
	}


	/**
	 * Returns the id of a Replica.
	 * 
	 * @param replicaLabel Label thats showed in the tree.
	 * @return Integer with the replica id.
	 */
	private int getReplicaId( String replicaLabel ) {

		int i = 8;
		while ( replicaLabel.charAt( i ) != ':' )
			i++;
		String idString = replicaLabel.substring( 8, i );
		return Integer.parseInt( idString );
	}


	/**
	 * Returns the id of a Task.
	 * 
	 * @param taskLabel Label that's showed in the tree.
	 * @return Integer with the task id.
	 */
	private int getTaskId( String taskLabel ) {

		int i = 5;
		while ( taskLabel.charAt( i ) != ':' )
			i++;
		String idString = taskLabel.substring( 5, i );
		return Integer.parseInt( idString );
	}


	/**
	 * Returns the id of a Job.
	 * 
	 * @param jobLabel Label that's showed in the tree.
	 * @return Integer with the job id.
	 */
	private int getJobId( String jobLabel ) {

		int i = 4;
		while ( jobLabel.charAt( i ) != ':' )
			i++;
		String idString = jobLabel.substring( 4, i );
		return Integer.parseInt( idString );
	}


	/**
	 * Returns the job id of the selected job in tree.
	 * 
	 * @return job id, -1 if selected node is not a job.
	 */
	public int getJobIdToCancel() {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if ( node == null )
			return -1;

		String nodeInfo = (String) node.getUserObject();

		if ( !nodeInfo.startsWith( "Job" ) || nodeInfo.startsWith( "Jobs" ) )
			return -1;

		return getJobId( nodeInfo );
	}


	public synchronized void updateStatus(final JobsPackage jobsPackage) {
		this.jobsPackage = jobsPackage;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jtm.refreshTree(jobsPackage.getJobs());
				valueChanged(null);
				tree.updateUI();
			}
		});
	}

	/* TODO get rid of this */
	private class TaskHandle implements Serializable {

		/**
		 * Serial version ID.
		 */
		private static final long serialVersionUID = 40L;

		/**
		 * The ID of the job that holds the task to which this handle is associated.
		 */
		private int jobID;

		/**
		 * The ID of the task that holds the task to which this handle is
		 * associated.
		 */
		private int taskID;


		public TaskHandle( int jobid, int taskid ) {

			jobID = jobid;
			taskID = taskid;
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

			if ( obj instanceof TaskHandle ) {
				TaskHandle otherHandle = (TaskHandle) obj;
				return otherHandle.getJobID() == this.getJobID() && otherHandle.getTaskID() == this.getTaskID();
			}
			return false;
		}


		@Override
		public int hashCode() {

			return (this.getJobID() + "." + this.getTaskID()).hashCode();
		}


		@Override
		public String toString() {

			return getJobID() + "." + getTaskID();
		}

	}

}

