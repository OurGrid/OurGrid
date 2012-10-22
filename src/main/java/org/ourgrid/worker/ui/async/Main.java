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
package org.ourgrid.worker.ui.async;

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

import org.ourgrid.worker.WorkerComponentContextFactory;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.ui.async.client.WorkerAsyncInitializer;
import org.ourgrid.worker.ui.async.gui.WorkerGUIMainFrame;
import org.ourgrid.worker.ui.async.model.WorkerAsyncUIModel;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 * Main class for invoking WorkerGUI main frame.
 */
public class Main {

	private static final String WORKERGUI_PROPERTIES = WorkerConfiguration.PROPERTIES_FILENAME;

	/**
	 * Main method.
	 * @param args the command line arguments.
	 * @throws IOException If there is some problem to write on some file.
	 * @throws ProcessorStartException 
	 * @throws CommuneNetworkException 
	 */
	public static void main(String[] args) throws IOException, CommuneNetworkException, ProcessorStartException {

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

		BufferedImage iconImage = ImageIO.read(Main.class.
				getResourceAsStream("/resources/images/icon.png"));
		WorkerAsyncUIModel model = new WorkerAsyncUIModel();
		model.loadProperties();
		WorkerAsyncInitializer.getInstance().setModel(model);
		
		WorkerGUIMainFrame frame = new WorkerGUIMainFrame(iconImage, model);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		model.addListener(frame);
		
		try {
			ModuleContext context = new WorkerComponentContextFactory(
					new PropertiesFileParser(WORKERGUI_PROPERTIES)).createContext();
			saveProperties(context);
			
			WorkerAsyncInitializer.getInstance().initComponentClient(context, model);
			frame.workerInited();
		} catch (Exception cre) {
			JOptionPane.showMessageDialog(null, cre.getMessage(), "Error on worker startup", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void saveProperties(ModuleContext context) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.putAll(context.getProperties());
		properties.store(new FileOutputStream(WORKERGUI_PROPERTIES), null);
	}

}
