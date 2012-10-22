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
package org.ourgrid.common.ui.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.ourgrid.common.interfaces.Constants;

public class BusyDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static final String EOS = "EOS";

	private JPanel mainPanel;

	private JScrollPane outputScroll;

	private JTextArea outputText;

	private JLabel pleaseWaitLabel;

	private JButton okButton;

	protected BufferedReader reader;


	public BusyDialog( Frame parentFrame ) throws HeadlessException {

		this( parentFrame, null );
	}


	public BusyDialog( Frame parentFrame, PipedOutputStream out ) {

		super( parentFrame, "Please wait..." );
		this.setLayout( new BorderLayout() );
		initComponents();
		this.setLocationRelativeTo( parentFrame );
		this.setComponentOrientation( parentFrame.getComponentOrientation() );
		this.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		pack();

		if ( out != null ) {
			PipedInputStream snk = new PipedInputStream();
			this.reader = new BufferedReader( new InputStreamReader( snk ) );
			try {
				out.connect( snk );
			} catch ( IOException e ) {}

			new Thread( new PipedStreamGobbler() ).start();
		}
	}

	private class PipedStreamGobbler implements Runnable {

		public PipedStreamGobbler() {

			super();
		}


		public void run() {
			
			String line;
			do {
				line = null;

				try {
					line = reader.readLine();
				} catch (IOException e) {}
				
				if ( line != null && !line.equals(EOS)) {
					addOutputText(line);
				}
				
			} while (line == null || !line.equals(EOS));
			
			try {
				if ( reader != null ) {
					reader.close();
				}
			} catch ( IOException e ) {}
		}
	}


	private void initComponents() {

		mainPanel = new JPanel();
		outputScroll = new JScrollPane();
		outputText = new JTextArea();
		pleaseWaitLabel = new JLabel();
		okButton = new JButton();

		this.add( mainPanel, BorderLayout.CENTER );

		outputText.setText( "" );
		outputText.setColumns( 30 );
		outputText.setRows( 30 );
		outputScroll.setViewportView( outputText );
		outputScroll.setAutoscrolls( false );

		pleaseWaitLabel.setText( "Please wait..." );

		okButton.setText( "OK" );
		okButton.setEnabled( false );

		okButton.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {

				dispose();
			}
		} );

//		GroupLayout layout = new GroupLayout( mainPanel );
//		mainPanel.setLayout( layout );
//		layout.setHorizontalGroup( layout.createParallelGroup( GroupLayout.LEADING ).add(
//			layout.createSequentialGroup().add(
//				layout.createParallelGroup( GroupLayout.LEADING ).add(
//					layout.createSequentialGroup().add( 60, 60, 60 ).add( pleaseWaitLabel ) ).add(
//					layout.createSequentialGroup().add( 145, 145, 145 ).add( okButton ) ).add(
//					layout.createSequentialGroup().addContainerGap().add( outputScroll, GroupLayout.DEFAULT_SIZE, 322,
//						Short.MAX_VALUE ) ) ).addContainerGap() ) );
//		layout.setVerticalGroup( layout.createParallelGroup( GroupLayout.LEADING ).add(
//			layout.createSequentialGroup().addContainerGap().add( pleaseWaitLabel, GroupLayout.PREFERRED_SIZE, 25,
//				GroupLayout.PREFERRED_SIZE ).add( 1, 1, 1 ).add( outputScroll, GroupLayout.PREFERRED_SIZE, 168,
//				GroupLayout.PREFERRED_SIZE ).addPreferredGap( LayoutStyle.RELATED ).add( okButton,
//				GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE ).addContainerGap() ) );
	}


	public void addOutputText( String text ) {

		outputText.setText( outputText.getText() + Constants.LINE_SEPARATOR + text );
		outputText.validate();
	}


	public void error() {

		pleaseWaitLabel.setText( "Error" );
		allowClose();
	}


	public void allowClose() {

		pleaseWaitLabel.setText( "Done" );
		okButton.setEnabled( true );
	}


	public JTextArea getTextArea() {

		return outputText;
	}
}
