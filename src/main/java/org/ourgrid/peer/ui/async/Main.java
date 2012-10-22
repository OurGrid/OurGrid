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
package org.ourgrid.peer.ui.async;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.ourgrid.peer.PeerComponentContextFactory;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.ui.async.client.PeerAsyncInitializer;
import org.ourgrid.peer.ui.async.gui.PeerGUIMainFrame;
import org.ourgrid.peer.ui.async.model.PeerAsyncUIModel;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;

/**
 * Main class for invoking PeerGUI main frame.
 */
public class Main {

	private static final String PEERGUI_PROPERTIES = PeerConfiguration.PROPERTIES_FILENAME;

	/**
	 * Main method.
	 * @param args the command line arguments.
	 * @throws IOException If there is some problem to write on the SDF file.
	 */
	public static void main(String[] args) throws IOException {
		
		try {
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		PeerAsyncUIModel model = new PeerAsyncUIModel();
		model.loadProperties();
		PeerAsyncInitializer.getInstance().setModel(model);
		
		BufferedImage iconImage = ImageIO.read(Main.class.
				getResourceAsStream("/resources/images/icon.png"));
		try {
			final PeerGUIMainFrame frame = new PeerGUIMainFrame(iconImage, model);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			model.addListener(frame);
					
			ModuleContext context = new PeerComponentContextFactory(
					new PropertiesFileParser(PEERGUI_PROPERTIES)).createContext();
			saveProperties(context);
			PeerAsyncInitializer.getInstance().initComponentClient(context, model);
			frame.peerInited();
		}catch (HeadlessException e) {
			printGUIErrorMessage(e);
			System.exit(0);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error on peer startup", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void printGUIErrorMessage(HeadlessException e) {
		System.out.println("Could not execute command 'peer gui'.");
		System.out.print("Cause: ");
		System.out.println(e.getMessage());
	}

	private static void saveProperties(ModuleContext context) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.putAll(context.getProperties());
		properties.store(new FileOutputStream(PEERGUI_PROPERTIES), null);
	}
	
	


}
