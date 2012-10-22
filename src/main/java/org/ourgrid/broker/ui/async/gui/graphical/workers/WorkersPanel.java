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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.common.interfaces.to.WorkersPackage;
import org.ourgrid.common.specification.worker.WorkerSpecification;

/**
 * Builds the "workers in use" panel.
 *   
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class WorkersPanel implements TreeSelectionListener {

	/** Panel thats adds JTree */
	private JPanel masterPanel;

	protected JSplitPane splitPane;

	/** Workers TreeModel */
	private WorkersTreeModel wtm;

	/** JTree */
	private JTree tree;

	/** JScrollPane to format JTree */
	private JScrollPane treeScroll;

	/** Properties Area */
	private JTextArea propertiesArea;

	/** PropertiesArea's Scroll */
	private JScrollPane propertiesScroll;

	private JPanel detailsPanel;

	private JScrollPane progressScroll;

	protected JButton verticalButton;

	protected JButton horizontalButton;

	private WorkersPackage workersPackage;

	/**
	 * Default constructor
	 */
	public WorkersPanel( ) {

		masterPanel = new JPanel();
		masterPanel.setLayout( new BorderLayout() );
		masterPanel.setDoubleBuffered( true );

		wtm = new WorkersTreeModel();

		tree = new JTree( wtm );
		tree.setCellRenderer( new WorkersTreeCellRenderer() );
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


	/**  
	 * @return JTree's Panel 
	 */
	public JPanel getPanel() {

		return masterPanel;
	}


	/**
	 * 
	 */
	private void fillDetails() {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		showPropertiesOfWorker( parseJobID(node), (String) node.getUserObject() );
	}


	/**
	 * Parses Job ID from the node description.
	 * @param node
	 * @return The Job ID
	 */
	private Integer parseJobID( DefaultMutableTreeNode node ) {
		Object userObject = ((DefaultMutableTreeNode)node.getParent()).getUserObject();
		String [] info = ((String)userObject).split( " +" );
		return Integer.parseInt( info[1].trim() );
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

		resetDetailsPanel();

		String nodeInfo = (String) node.getUserObject();

		if ( nodeInfo.equals( "Workers" ) ) {
			propertiesArea.setText( "Click on a job for information" );
			return;
		}

		if ( nodeInfo.startsWith( "Job" ) ) {
			propertiesArea.setText( "Click on a worker for information" );
			return;
		}

		fillDetails();
	}


	private void resetDetailsPanel() {

		detailsPanel.removeAll();
		detailsPanel.add( propertiesScroll, BorderLayout.CENTER );
		detailsPanel.validate();
	}


	/**
	 * Format worker properties to show.
	 * @param jobID The job ID
	 * @param workerUserAndServer Worker ID 
	 */
	private synchronized void showPropertiesOfWorker( Integer jobID, String workerUserAndServer ) {
		
		Set<WorkerStatusInfo> workers = workersPackage.getWorkersByJob().get( jobID );
		Iterator<WorkerStatusInfo> workersIt = workers.iterator();
		
		while ( workersIt.hasNext() ) {
			WorkerStatusInfo workerStatusInfo = workersIt.next();
			WorkerSpecification workerSpec = workerStatusInfo.getWorkerSpec();
			if(workerSpec.getUserAndServer().equals( workerUserAndServer )){
				writeWorkerInTextArea( workerSpec );
			}
		}
	}


	/**
	 * Writes formated worker properties in a text area.
	 * @param workerSpec A worker specification.
	 */
	private void writeWorkerInTextArea( WorkerSpecification workerSpec ) {
			String expression = workerSpec.getExpression();
			if(expression != null){
				try {
					propertiesArea.setText( new JobAd( expression ).toString( true, true ) );
				} catch ( Exception e ) {
					propertiesArea.setText( "Error parsing JobAd: " + e.getMessage() );
				}
			}else{
				propertiesArea.setText( formatMap( workerSpec.getAttributes() ) );
			}
	}


	/**
	 * Update information on this panel.
	 * 
	 * @param workersPackage The source of new information.
	 */
	public synchronized void updateStatus(final WorkersPackage workersPackage) {
		
		this.workersPackage = workersPackage;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				wtm.refreshTree(workersPackage.getWorkersByJob());
				valueChanged(null);
				tree.updateUI();
			}
		});
	}
	
	/**
	 * Format a map of properties to a single {@link String}.
	 * @param map
	 * @return
	 */
	private String formatMap( Map<String,String> map ) {
		StringBuilder sb = new StringBuilder("[\n");
		Set<Entry<String,String>> entrySet = map.entrySet();
		for ( Entry<String,String> entry : entrySet ) {
			sb.append( "\t" + entry.getKey() + ": " + entry.getValue() + "\n" );
		}
		return sb.append( "]\n" ).toString();
	}
}

