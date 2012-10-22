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
package org.ourgrid.broker.ui.async.gui.graphical;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.ourgrid.broker.ui.async.gui.graphical.actions.BrokerActionsPanel;
import org.ourgrid.broker.ui.async.gui.graphical.grid.GridPanel;
import org.ourgrid.broker.ui.async.gui.graphical.job.JobsPanel;
import org.ourgrid.broker.ui.async.gui.graphical.settings.BrokerSettingsPanel;
import org.ourgrid.broker.ui.async.gui.graphical.workers.WorkersPanel;
import org.ourgrid.broker.ui.async.model.BrokerAsyncUIListener;
import org.ourgrid.broker.ui.async.model.BrokerAsyncUIModel;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.Constants;
import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.common.ui.gui.AboutPanel;
import org.ourgrid.common.ui.gui.OGFileChooser;
import org.ourgrid.common.util.CommonUtils;

public class BrokerGUIMainFrame extends JFrame implements BrokerAsyncUIListener {

	public static final String GDF_FILE_DESCR = "Grid Description File (*.gdf)";

	public static final String GDF_FILE_EXT = ".gdf";

	public static final String JDF_FILE_DESCR = "Job Description File (*.jdf)";

	public static final String JDF_FILE_EXT = ".jdf";
	
	public static final String JDL_FILE_DESCR = "JDL File (*.jdl)";

	public static final String JDL_FILE_EXT = ".jdl";

	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger( BrokerGUIMainFrame.class );

	private static final long serialVersionUID = 1L;

	public static final String ERROR_OCCURRED = "Error occurred";
	
	private static final String PEERS = "Peers";

	private static final String JOBS = "Jobs";	
	
	private static final String WORKERS = "Workers";

	private static final String BROKER_TITLE = "OurGrid Broker " + Configuration.VERSION;

	private JTabbedPane tabbedPane;

	private JPanel masterPanel;

	private JPanel workersSection;

	private JPanel peersSection;

	private JPanel jobsSection;

	private JPanel aboutSection;

	private BrokerActionsPanel brokerActionsPanel;

	private GridPanel gridPanel;

	private JobsPanel jobsPanel;

	private WorkersPanel workersPanel;

	private BrokerSettingsPanel brokerSettingsPanel;

	protected boolean guiModuledStarted;

	private BrokerAsyncUIModel model;

	public BrokerGUIMainFrame(BufferedImage iconImage, BrokerAsyncUIModel model) {
		
		this.setLocationRelativeTo( null );
		this.setResizable( true );
		this.setIconImage( iconImage );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setTitle( BROKER_TITLE );
		this.setSize( 740, 550 );
		
		this.model = model;

		initMasterPanel();
		initTabbedPane();

		guiModuledStarted = false;
	}
	
	public BrokerGUIMainFrame() {
		this(null, null);
	}

	/** Starts the Master Panel 
	 * @param controller 
	 * @param model */
	private void initMasterPanel() {

		masterPanel = new JPanel();
		masterPanel.setLayout( new BorderLayout() );
		masterPanel.setDoubleBuffered( true );
		this.setContentPane( masterPanel );
	}


	/** Inits the Tabbed Pane adding and configuring tabs 
	 * @param controller 
	 * @param controller 
	 * @param model */
	private void initTabbedPane() {

		tabbedPane = new JTabbedPane();

		brokerActionsPanel = new BrokerActionsPanel( this );
		brokerActionsPanel.disableAllActions();
		
		masterPanel.add( brokerActionsPanel, BorderLayout.WEST );
//		masterPanel.add( tabbedPane, BorderLayout.CENTER );

		// Jobs Section
		createJobsSection();

		// Peers section
		createPeersSection();

		// Workers section
		createWorkersSection();

		this.brokerSettingsPanel = new BrokerSettingsPanel( model);

		// about
		aboutSection = new AboutPanel();
		//aboutSection.setLayout( new GridLayout( 1, 1 ) );
		//aboutSection.add( createAbout() );
		aboutSection.setBackground( Color.LIGHT_GRAY );
		
		tabbedPane.addTab( "Settings", brokerSettingsPanel );
		tabbedPane.addTab( "About", aboutSection );
		tabbedPane.setSelectedComponent( brokerSettingsPanel );
		
		masterPanel.add( tabbedPane, BorderLayout.CENTER );
	}
	
	private void createJobsSection() {
		jobsPanel = new JobsPanel( );
		jobsSection = jobsPanel.getPanel();
		tabbedPane.addTab( JOBS, jobsSection );
	}
	
	private void createPeersSection() {
		peersSection = new JPanel();
		peersSection.setLayout( new GridLayout( 1, 1 ) );
		gridPanel = new GridPanel();
		peersSection.add( gridPanel );
		tabbedPane.addTab( PEERS, peersSection );
	}

	private void createWorkersSection() {
		workersPanel = new WorkersPanel();
		workersSection = workersPanel.getPanel();
		tabbedPane.addTab( WORKERS, workersSection );
	}

	/**
	 * When addJob button is clicked
	 */
	public void addJobActionPerformed( String jdfPath ) {

		File jdfFile = null;
		if ( jdfPath == null ) {
			Map<String, String> map = CommonUtils.createSerializableMap();
			map.put( JDF_FILE_EXT, JDF_FILE_DESCR );
			map.put( JDL_FILE_EXT, JDL_FILE_DESCR );
			jdfFile = new OGFileChooser( map ).selectFile( this );
		} else {
			jdfFile = new File( jdfPath );
		}

		if ( jdfFile == null )
			return;

		
		List<JobSpecification> spec;
		try {
			if(jdfFile.getName().endsWith( JDL_FILE_EXT )){
				spec = Arrays.asList( DescriptionFileCompile.compileJDF( jdfFile.getAbsolutePath() ) );
			}else{
				spec = DescriptionFileCompile.compileJDL( jdfFile.getAbsolutePath() );
			}
		} catch ( CompilerException e ) {
			showErrorToUser( e );
			return;
		}
		
		for ( JobSpecification jobSpec : spec ) {
			new Thread( new UIManagerAddJobContacter( jobSpec ) ).start();
			brokerActionsPanel.newAddJobHistoryAction( jobSpec.getLabel(), jdfFile.getAbsolutePath() );
		}
	}


	/**
	 * When cancel job button is clicked
	 */
	public void cancelJobActionPerformed( int jobId ) {

		int option = JOptionPane.showConfirmDialog( this, "Really Cancel Job " + jobId + "?", "Confirm Cancel Job",
			JOptionPane.YES_NO_OPTION );

		if ( option == JOptionPane.YES_OPTION ) {
			new Thread( new UIManagerCancelJobContacter( jobId ) ).start();
		}
	}


	public void cleanAllActionPerformed() {

		new Thread( new UIManagerCleanAllContacter() ).start();
	}


	public void cleanActionPerformed( int jobId ) {

		new Thread( new UIManagerCleanJobContacter( jobId ) ).start();
	}


	/**
	 * Shows the user a message when an exception occurs.
	 * 
	 * @param e The exception occurred.
	 */
	void showErrorToUser( Exception e ) {

		JOptionPane.showMessageDialog( this, e.getMessage(), "Error ocurred", JOptionPane.ERROR_MESSAGE );
	}


	public void daemonIsDown( boolean failure ) {

		LOG.debug( "Broker daemon is DOWN. Failure: " + failure );

		resetPanels();
		setFrameTitleDown();
		brokerActionsPanel.daemonIsDown();
		brokerSettingsPanel.daemonIsDown();
		if ( failure ) {
			String message = "The GUI has lost communication with the Broker daemon; " + Constants.LINE_SEPARATOR
					+ "it might have been shutdown or it has failed. " + Constants.LINE_SEPARATOR
					+ "This GUI will keep trying to contact it until it comes back. ";
			JOptionPane.showMessageDialog( this, message, "Broker Down", JOptionPane.WARNING_MESSAGE );
		}
	}
	
	public void enableBrokerSettingsPanel(boolean enable) {
		this.brokerSettingsPanel.setEnabled(enable);
	}
	
	public void initBrokerSettingsPanel() {
		this.brokerSettingsPanel.initSettingsPanel();
	}
	
	public void reinitBrokerSettingsPanel() {
		this.brokerSettingsPanel.reinitSettingsPanel();
	}
	
	public void editBrokerSettingsPanel() {
		this.brokerSettingsPanel.editSettingsPanel();
	}


	private void setFrameTitleDown() {
	}


	private void resetPanels() {

	}

	class UIManagerAddJobContacter implements Runnable {

		private final JobSpecification spec;


		public UIManagerAddJobContacter( JobSpecification spec ) {

			super();
			this.spec = spec;
		}


		public void run() {
		}
	}

	private class UIManagerCancelJobContacter implements Runnable {

		private final int jobId;


		public UIManagerCancelJobContacter( int jobId ) {

			this.jobId = jobId;
		}


		public void run() {
		}
	}

	private class UIManagerCleanJobContacter implements Runnable {

		private final int jobId;


		public UIManagerCleanJobContacter( int jobId ) {

			this.jobId = jobId;
		}


		public void run() {
		}
	}

	class UIManagerCleanAllContacter implements Runnable {

		public void run() {
		}
	}
	
	public void noJobSelected() {

		brokerActionsPanel.removeJobSpecificActions();
	}


	public void jobIsSelected( int selectedJob ) {

		brokerActionsPanel.addJobSpecificActions( selectedJob );
	}

	public void brokerStarted() {
		this.brokerActionsPanel.brokerStarted();
		getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void brokerStopped() {
		this.brokerActionsPanel.brokerStopped();
		
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void brokerInited() {
		
		initMasterPanel();
		initTabbedPane();
		
		this.brokerActionsPanel.brokerInited();
		
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	public void brokerEditing() {
		
		this.brokerActionsPanel.brokerEditing();
		
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	public void brokerInitedFailed() {
	
		initMasterPanel();
		initTabbedPane();
		
		this.brokerActionsPanel.brokerInitedFailed();
		
    	getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
	
	public void brokerRestarted() {
		this.brokerActionsPanel.brokerRestarted();
		getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
	
	public void jobHistoryUpdated(List<String> jobHistory) {
		this.brokerActionsPanel.jobHistoryUpdated(jobHistory);
		
	}

	public void updateCompleteStatus(BrokerCompleteStatus status) {
		this.jobsPanel.updateStatus(status.getJobsPackage());
		this.gridPanel.updateStatus(status.getPeersPackage());
		this.workersPanel.updateStatus(status.getWorkersPackage());
	}

	public int getSelectedJob() {
		return this.jobsPanel.getJobIdToCancel();
	}
	
	public void setStartEnabled(boolean enabled) {
		brokerActionsPanel.setStartEnabled(enabled);
	}


}
