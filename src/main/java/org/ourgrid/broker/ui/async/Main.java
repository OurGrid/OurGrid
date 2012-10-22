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
package org.ourgrid.broker.ui.async;

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

import org.ourgrid.broker.BrokerComponentContextFactory;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.ui.async.client.BrokerAsyncInitializer;
import org.ourgrid.broker.ui.async.gui.graphical.BrokerGUIMainFrame;
import org.ourgrid.broker.ui.async.model.BrokerAsyncUIModel;
import org.ourgrid.common.command.UIMessages;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;

public class Main {

	private static final String BROKERGUI_PROPERTIES = BrokerConfiguration.PROPERTIES_FILENAME;
	
	static transient org.apache.log4j.Logger LOG;

	public static void main( String[ ] args ) {

		try {

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
			BrokerAsyncUIModel model = new BrokerAsyncUIModel();
			model.loadProperties();
			BrokerAsyncInitializer.getInstance().setModel(model);
			
			BrokerGUIMainFrame frame = new BrokerGUIMainFrame(iconImage, model);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			model.addListener(frame);
			
			frame.setStartEnabled(false);
			ModuleContext context = null;
			try {
				context = new BrokerComponentContextFactory(
						new PropertiesFileParser(BROKERGUI_PROPERTIES)).createContext();
				saveProperties(context);
				
				frame.brokerInited();
				BrokerAsyncInitializer.getInstance().initComponentClient(context, model);
			} catch (Exception cre) {
				JOptionPane.showMessageDialog(null, cre.getMessage(), "Error on broker startup", JOptionPane.ERROR_MESSAGE);
			}
			
//			if (componentClient.getModel().isBrokerUp()) {
//				frame.brokerStarted();
//			} else {
//				frame.brokerStopped();
//			}

		} catch ( Exception e ) {
			final String errorMessage = UIMessages.getErrorMessage( e, "broker", "gui" );
			System.err.println( errorMessage );
			System.exit( 1 );
		}
	}
	
	private static void saveProperties(ModuleContext context) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.putAll(context.getProperties());
		properties.store(new FileOutputStream(BROKERGUI_PROPERTIES), null);
	}

}
