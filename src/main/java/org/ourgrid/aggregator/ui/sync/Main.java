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
package org.ourgrid.aggregator.ui.sync;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.aggregator.AggregatorComponentContextFactory;
import org.ourgrid.aggregator.AggregatorConfiguration;
import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.ui.sync.command.AggregatorQueryCommand;
import org.ourgrid.aggregator.ui.sync.command.AggregatorStatusCommand;
import org.ourgrid.common.command.InvalidCommandException;
import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.ui.MainUtil;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.Command;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.StartCommand;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.StopCommand;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.network.ConnectionListenerAdapter;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 * The Main class of Aggregator Module
 */
public class Main {

	public static void main(String[] args) {
		final String commandName = args[0];
		String[ ] commandArgs = MainUtil.discardFirstParameter( args );
		
		try {
			ModuleContext context = initComponents();
			executeCommand(commandName, commandArgs, context);
		} catch ( Exception e ) {
			System.out.println(UIMessages.getErrorMessage( e, "aggregator", commandName ));
			System.exit( 1 );
		}
	}

	/**
	 * This method execute the command name that was passed by parameter.
	 * @param commandName {@link String}
	 * @param args {@link String[]}
	 * @param componentContext {@link ModuleContext}
	 * @throws Exception {@link Exception}
	 */
	public static void executeCommand(String commandName, String[] args, 
			ModuleContext componentContext) throws Exception {
		
		boolean isStartCmd = AggregatorConstants.START_CMD_NAME.equals(commandName);
			
		if (isStartCmd) {
			try {
				createAggregatorComponent(componentContext);
			} catch(CommuneNetworkException cne) {
				treatMessage(cne);
			}
		}
		
		AggregatorSyncComponentClient componentClient = new AggregatorSyncComponentClient(componentContext, isStartCmd);
		
		Command command = getCommand( commandName, componentClient );
		
		
		command.run( args );
		
		if ( command instanceof StartCommand ) {
			System.out.println( AggregatorUIMessages.STARTED );
		} else {
			System.out.println( AggregatorUIMessages.getSuccessMessage( commandName ) );
			System.exit( 0 );
		}
		componentClient.stop();
		
	}
	
	private static void createAggregatorComponent(ModuleContext componentContext)
			throws CommuneNetworkException, ProcessorStartException {
		new AggregatorComponent(componentContext) {
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
	
	private static void treatMessage(Exception e) {
		String message = e.getMessage();
		System.out.println(message);
		System.exit(1);
	}
	
	private static Command getCommand(String commandName,
			AggregatorSyncComponentClient componentClient) {
		
		if ( AggregatorConstants.START_CMD_NAME.equals( commandName ) ) {
			return new StartCommand( componentClient );
		} else if ( AggregatorConstants.STOP_CMD_NAME.equals( commandName ) ) {
			return new StopCommand( componentClient );
		} else if ( AggregatorConstants.STATUS_CMD_NAME.equals( commandName ) ) {
			return new AggregatorStatusCommand( componentClient );
		} else if ( AggregatorConstants.QUERY_CMD_NAME.equals( commandName )) {
			return new AggregatorQueryCommand( componentClient );
		}
		
		throw new InvalidCommandException( commandName );
	}

	private static ModuleContext initComponents() throws FileNotFoundException, IOException {
		AggregatorComponentContextFactory contextFactory = new AggregatorComponentContextFactory(
				new PropertiesFileParser(AggregatorConfiguration.PROPERTIES_FILENAME));
		
		ModuleContext context = contextFactory.createContext();
		saveProperties(context);
		
		return context;
	}
	
	private static void saveProperties(ModuleContext context) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.putAll(context.getProperties());
		properties.store(new FileOutputStream(AggregatorConfiguration.PROPERTIES_FILENAME), null);
	}
	
}
