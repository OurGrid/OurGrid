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
package org.ourgrid.broker.ui.async.gui.graphical.actions;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.ourgrid.broker.ui.async.gui.graphical.BrokerGUIMainFrame;
import org.ourgrid.common.interfaces.Constants;

/**
 * This class contains the presentation screen of Broker GUI and also some
 * buttons to set Grids, add and cancel Jobs and start stop Broker.
 */
public class BrokerActionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

//	private final URL BACKGROUND_BROKER_PATH = this.getClass().getResource( "/resources/images/background.png" );

	private JXTaskPane jobActionsPanel;

	private JXTaskPane mainActionsPanel;

	private StartBrokerAction startDaemonAction;

	private StopDaemonAction stopDaemonAction;

	private AddJobAction addNewJobAction;

	private List<AddJobAction> addJobHistory;

	private CleanAllJobsAction cleanAllJobsAction;

	private CancelJobAction cancelJobAction;

	private CleanJobAction cleanJobAction;

	private ClearJobHistoryAction clearJobHistoryAction;

	private JLabel jobHistoryLabel;

	private BrokerGUIMainFrame gui;


	public BrokerActionsPanel( BrokerGUIMainFrame gui ) {

		this.gui = gui;
		this.addJobHistory = new LinkedList<AddJobAction>();

		setLayout( new BorderLayout() );
		initComponents(gui);
	}


	/** Init components */
	private void initComponents(Frame frame) {

		JXTaskPaneContainer taskPaneContainer = configureActions(frame);

		JScrollPane taskScrollPane = new JScrollPane( taskPaneContainer );

		this.add( taskScrollPane, BorderLayout.CENTER );
	}


	private JXTaskPaneContainer configureActions(Frame frame) {

		JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

		mainActionsPanel = new JXTaskPane();
		mainActionsPanel.setTitle( "Main Actions" );
		mainActionsPanel.setSpecial( true );

		JFrame jFrame = (JFrame) frame;
		startDaemonAction = new StartBrokerAction( jFrame.getContentPane() );
		mainActionsPanel.add( startDaemonAction );
		stopDaemonAction = new StopDaemonAction( jFrame.getContentPane() );
		mainActionsPanel.add( stopDaemonAction );

		jobActionsPanel = new JXTaskPane();
		jobActionsPanel.setTitle( "Job Actions" );
		jobActionsPanel.setSpecial( true );
		addNewJobAction = new AddJobAction( gui );
		cleanAllJobsAction = new CleanAllJobsAction(  );
		clearJobHistoryAction = new ClearJobHistoryAction( );
		cancelJobAction = new CancelJobAction( gui );
		jobHistoryLabel = new JLabel( "Job History" );
		resetJobActions();

		this.daemonIsDown();

		taskPaneContainer.add( mainActionsPanel );
		taskPaneContainer.add( jobActionsPanel );
		return taskPaneContainer;
	}


	public void resetJobActions() {

		jobActionsPanel.removeAll();
		jobActionsPanel.add( addNewJobAction );
		if ( cancelJobAction != null ) {
			jobActionsPanel.add( cancelJobAction );
		}
		if ( cleanJobAction != null ) {
			jobActionsPanel.add( cleanJobAction );
		}
		addJobHistory();
		
		jobActionsPanel.add( cleanAllJobsAction );
		revalidate();
	}


	private void addJobHistory() {

		if ( !addJobHistory.isEmpty() ) {

			jobActionsPanel.add( new JLabel( Constants.LINE_SEPARATOR ) );
			jobActionsPanel.add( jobHistoryLabel );

			for ( AddJobAction job : addJobHistory ) {
				jobActionsPanel.add( job );
			}

			jobActionsPanel.add( clearJobHistoryAction );
			jobActionsPanel.add(new JLabel(Constants.LINE_SEPARATOR));
		}
	}


	public void daemonIsUp() {

		startDaemonAction.setEnabled( false );
		stopDaemonAction.setEnabled( true );
		addNewJobAction.setEnabled( true );
		cleanAllJobsAction.setEnabled( true );
		clearJobHistoryAction.setEnabled( true );
		cancelJobAction.setEnabled(true);
		jobHistoryLabel.setEnabled( true );
		for ( AddJobAction action : addJobHistory ) {
			action.setEnabled( true );
		}
	}


	public void daemonIsDown() {

		startDaemonAction.setEnabled( true );
		stopDaemonAction.setEnabled( false );
		addNewJobAction.setEnabled( false );
		cancelJobAction.setEnabled(false);
		cleanAllJobsAction.setEnabled( false );
		clearJobHistoryAction.setEnabled( false );
		jobHistoryLabel.setEnabled( false );
		removeJobSpecificActions();
		for ( AddJobAction action : addJobHistory ) {
			action.setEnabled( false );
		}
	}
	
	public void daemonIsInited() {

		startDaemonAction.setEnabled( false );
		stopDaemonAction.setEnabled( false );
		addNewJobAction.setEnabled( false );
		cancelJobAction.setEnabled(false);
		cleanAllJobsAction.setEnabled( false );
		clearJobHistoryAction.setEnabled( false );
		jobHistoryLabel.setEnabled( false );
		removeJobSpecificActions();
		for ( AddJobAction action : addJobHistory ) {
			action.setEnabled( false );
		}
	}


	public void disableAllActions() {

		startDaemonAction.setEnabled( false );
		stopDaemonAction.setEnabled( false );
		cancelJobAction.setEnabled(false);
		addNewJobAction.setEnabled( false );
		cleanAllJobsAction.setEnabled( false );
		for ( AddJobAction action : addJobHistory ) {
			action.setEnabled( false );
		}
	}


	public void newAddJobHistoryAction( String jobLabel, String path ) {

		AddJobAction action = new AddJobAction( gui, jobLabel, path );
		if ( !addJobHistory.contains( action ) ) {
			addJobHistory.add( action );
		}
		resetJobActions();
	}


	public void addJobSpecificActions( int selectedJob ) {

		removeJobSpecificActions();

		//cancelJobAction = new CancelJobAction( client, gui );
		cleanJobAction = new CleanJobAction( selectedJob );

		resetJobActions();
	}


	public void removeJobSpecificActions() {

		//cancelJobAction = null;
		cleanJobAction = null;
		resetJobActions();
	}


	public void brokerStarted() {
		daemonIsUp();
		gui.enableBrokerSettingsPanel(false);
	}

	public void brokerStopped() {
		daemonIsDown();
		gui.enableBrokerSettingsPanel(true);
	}

	public void brokerInitedFailed() {
		daemonIsInited();
		gui.initBrokerSettingsPanel();
	}
	
	
	public void brokerRestarted() {
		daemonIsUp();
		gui.reinitBrokerSettingsPanel();
	}
	
	
	public void brokerInited() {
		daemonIsInited();
		gui.initBrokerSettingsPanel();
	}
	
	public void brokerEditing() {
		daemonIsInited();
		gui.editBrokerSettingsPanel();
	}

	public void clearJobHistory() {
		addJobHistory.clear();
		resetJobActions();
	}

	public void jobHistoryUpdated(List<String> jdfs) {
		addJobHistory.clear();
		for (String jdf : jdfs) {
			addJobHistory.add(new AddJobAction(gui, null, jdf));
		}
		resetJobActions();
		
	}

	public void setStartEnabled(boolean enabled) {
		startDaemonAction.setEnabled(enabled);
	}



}
