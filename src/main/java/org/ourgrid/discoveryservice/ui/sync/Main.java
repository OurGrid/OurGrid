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
package org.ourgrid.discoveryservice.ui.sync;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.ourgrid.common.command.InvalidCommandException;
import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.ui.MainUtil;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceComponentContextFactory;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.config.DiscoveryServiceConfiguration;
import org.ourgrid.discoveryservice.ui.sync.command.DiscoveryServiceQueryCommand;
import org.ourgrid.discoveryservice.ui.sync.command.GetDiscoveryServiceStatusCommand;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.Command;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.StartCommand;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.StopCommand;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.network.ConnectionListenerAdapter;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 *
 */
public class Main {

	public static void main(String[] args) {
		final String commandName = args[0];
		String[ ] commandArgs = MainUtil.discardFirstParameter( args );
		
		try {
			ModuleContext context = initComponents();
			executeCommand(commandName, commandArgs, context);
		} catch ( Exception e ) {
			System.out.println(UIMessages.getErrorMessage( e, "ds", commandName ));
			System.exit( 1 );
		}
	}

	/**
	 * @param commandName
	 * @param args
	 * @param componentContext
	 * @throws Exception
	 */
	public static void executeCommand(String commandName, String[] args, ModuleContext componentContext) throws Exception {
		
		boolean isStartCmd = DiscoveryServiceConstants.START_CMD_NAME.equals(commandName);
			
		if (isStartCmd) {
			try {
				createDiscoveryServiceComponent(componentContext);
			} catch(CommuneNetworkException cne) {
				treatMessage(cne);
			}
		}
		
		DiscoveryServiceSyncComponentClient componentClient = new DiscoveryServiceSyncComponentClient(
				componentContext, isStartCmd);
		
		Command command = getCommand( commandName, componentClient );
		
		
		command.run( args );
		
		if ( command instanceof StartCommand ) {
			System.out.println( DiscoveryServiceUIMessages.STARTED );
		} else {
			System.out.println( DiscoveryServiceUIMessages.getSuccessMessage( commandName ) );
			System.exit( 0 );
		}
		componentClient.stop();
		
	}
	
	
	private static void createDiscoveryServiceComponent(ModuleContext componentContext)
			throws CommuneNetworkException, ProcessorStartException {
		new DiscoveryServiceComponent(componentContext) {
			@Override
			protected void moduleCreated() {
				setConnectionListener(createConnectionListener());
			}
		};
	}

	private static ConnectionListenerAdapter createConnectionListener() {
		return new ConnectionListenerAdapter() {
			@Override
			public void connectionFailed(Exception e) {
				treatMessage(e);
			}
		};
	}
	
	private static void treatMessage(Exception cne) {
		String message = cne.getMessage();
		System.out.println(message);
		System.exit(1);
	}
	
	/**
	 * @param commandName
	 * @param componentClient
	 * @return
	 */
	private static Command getCommand(String commandName,
			DiscoveryServiceSyncComponentClient componentClient) {
		
		if ( DiscoveryServiceConstants.START_CMD_NAME.equals( commandName ) ) {
			return new StartCommand( componentClient );
		} else if ( DiscoveryServiceConstants.STOP_CMD_NAME.equals( commandName ) ) {
			return new StopCommand( componentClient );
		} else if ( DiscoveryServiceConstants.STATUS_CMD_NAME.equals( commandName ) ) {
			return new GetDiscoveryServiceStatusCommand( componentClient );
		} else if ( DiscoveryServiceConstants.QUERY_CMD_NAME.equals( commandName )) {
			return new DiscoveryServiceQueryCommand( componentClient );
		}
		
		throw new InvalidCommandException( commandName );
	}

	/**
	 * @return 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws HandlerStartException
	 */
	private static ModuleContext initComponents() throws FileNotFoundException, IOException {
		DiscoveryServiceComponentContextFactory contextFactory = new DiscoveryServiceComponentContextFactory (
				new PropertiesFileParser(DiscoveryServiceConfiguration.PROPERTIES_FILENAME));
		
	
		ModuleContext context = contextFactory.createContext();
		saveProperties(context);
		
		return context;
	}
	
	private static void saveProperties(ModuleContext context) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.putAll(context.getProperties());
		properties.store(new FileOutputStream(DiscoveryServiceConfiguration.PROPERTIES_FILENAME), null);
	}

	
}
