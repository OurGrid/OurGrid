package org.ourgrid.peer.ui.async.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.ourgrid.common.interfaces.to.WorkerInfo;

/**
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class WorkersPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;
	private WorkerTablePanel workersTable;


	/**
	 * 
	 */
	public WorkersPanel() {

		initComponents();
	}


	private void initComponents() {

		this.setLayout( new BorderLayout() );
		this.setDoubleBuffered( true );

		JPanel splitButtonsPanel = new JPanel();
		
		splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		splitPane.setDoubleBuffered( true );
		splitPane.setDividerLocation( 0.5 );

		final JButton verticalButton = new JButton( "Vertically" );
		final JButton horizontalButton = new JButton( "Horizontally" );
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

		workersTable = new WorkerTablePanel();
		workersTable.setPreferredSize( new Dimension( 300, 400 ) );
		
		JPanel detailsPanel = new JPanel();
		detailsPanel.setLayout( new BorderLayout() );
		detailsPanel.setDoubleBuffered( true );
		
		JTextArea propertiesArea = new JTextArea("Click on a worker for information");
		propertiesArea.setEditable( false );
		propertiesArea.setFont(new Font("Monospaced", 
				propertiesArea.getFont().getStyle(), propertiesArea.getFont().getSize()));
		propertiesArea.setBackground(this.getBackground());
		
		JScrollPane propertiesScroll = new JScrollPane( propertiesArea );
		propertiesScroll.setDoubleBuffered( true );
		propertiesScroll.setPreferredSize( new Dimension( 200, 300 ) );

		detailsPanel.add( propertiesScroll, BorderLayout.CENTER );
		
		workersTable.setTextArea(propertiesArea);
		
		splitPane.setLeftComponent( workersTable );
		splitPane.setRightComponent( detailsPanel );
		
		this.add( splitButtonsPanel, BorderLayout.NORTH );
		this.add( splitPane, BorderLayout.CENTER );
		this.setDoubleBuffered( true );
		
        Box box = Box.createHorizontalBox();
        box.add(new JLabel("UNAVAILABLE   ", new ImageIcon(WorkerTableModel.WORKER_UNAVAILABLE_IMAGE_PATH, "UNAVAILABLE"), JLabel.LEFT));
        box.add(new JLabel("CONTACTING   ", new ImageIcon(WorkerTableModel.WORKER_CONTACTING_IMAGE_PATH, "CONTACTING"), JLabel.LEFT));
        box.add(new JLabel("IDLE   ", new ImageIcon(WorkerTableModel.WORKER_IDLE_IMAGE_PATH, "IDLE"), JLabel.LEFT));
        box.add(new JLabel("OWNER   ", new ImageIcon(WorkerTableModel.WORKER_OWNER_IMAGE_PATH, "OWNER"), JLabel.LEFT));
        box.add(new JLabel("DONATED   ", new ImageIcon(WorkerTableModel.WORKER_DONATED_IMAGE_PATH, "DONATED"), JLabel.LEFT));
        box.add(new JLabel("INUSE   ", new ImageIcon(WorkerTableModel.WORKER_INUSE_IMAGE_PATH, "INUSE"), JLabel.LEFT));
        box.add(new JLabel("ERROR   ", new ImageIcon(WorkerTableModel.WORKER_ERROR_IMAGE_PATH, "ERROR"), JLabel.LEFT));
        
        add(box, BorderLayout.SOUTH);
        
        this.addComponentListener( new ComponentAdapter() {
        	@Override
        	public void componentResized( ComponentEvent e ) {
        		splitPane.setDividerLocation( 0.5 );
        	}
		});
	}


	/**
	 * 
	 */
	public void peerStopped() {
		workersTable.peerStopped();
	}


	/**
	 * @param localWorkersInfo
	 */
	public void setTableModelData( List<WorkerInfo> localWorkersInfo ) {
		workersTable.setTableModelData( localWorkersInfo );
	}
}
