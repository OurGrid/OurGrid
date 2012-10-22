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
package org.ourgrid.broker.ui.sync;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.ourgrid.broker.BrokerComponentContextFactory;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.ui.sync.command.BrokerAddJobCommand;
import org.ourgrid.broker.ui.sync.command.BrokerCancelJobCommand;
import org.ourgrid.broker.ui.sync.command.BrokerCleanCommand;
import org.ourgrid.broker.ui.sync.command.BrokerJobStatusCommand;
import org.ourgrid.broker.ui.sync.command.BrokerStartCommand;
import org.ourgrid.broker.ui.sync.command.BrokerStatusCommand;
import org.ourgrid.broker.ui.sync.command.BrokerWaitForJobCommand;
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

public class Main {

	public static void main(String[] args) {
		
		final String commandName = args[0];
		String[ ] commandArgs = MainUtil.discardFirstParameter( args );
		
		try {
			ModuleContext context = initComponents();
			executeCommand(commandName, commandArgs, context);
		} catch ( Exception e ) {
			System.out.println(UIMessages.getErrorMessage( e, "broker", commandName ));
			System.exit( 1 );
		}
	}

	public static void executeCommand(String commandName, String[] args, ModuleContext componentContext) throws Exception {
		
		boolean isStartCmd = BrokerConstants.START_CMD_NAME.equals(commandName);
			
		if (isStartCmd) {
			try {
				createBrokerComponent(componentContext);
			} catch(CommuneNetworkException cne) {
				treatMessage(cne);
			}
		}
		
		BrokerSyncApplicationClient componentClient = new BrokerSyncApplicationClient(componentContext, isStartCmd);
		
		Command command = getCommand( commandName, componentClient );
		command.run( args );
		
		if ( command instanceof StartCommand ) {
			System.out.println( BrokerUIMessages.STARTED );
			componentClient.callExitOnOperationSucceed(false);
		} else {
			System.out.println( BrokerUIMessages.getSuccessMessage( commandName ) );
			componentClient.callExitOnOperationSucceed(true);
		}
		componentClient.stop();
		
	}

	private static void createBrokerComponent(ModuleContext componentContext)
			throws CommuneNetworkException, ProcessorStartException {
		new BrokerServerModule(componentContext) {
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

	private static Command getCommand(String commandName, BrokerSyncApplicationClient componentClient) {
		if ( BrokerConstants.START_CMD_NAME.equals(commandName) ) {
			return new BrokerStartCommand( componentClient );
		} else if ( BrokerConstants.STOP_CMD_NAME.equals(commandName) ) {
			return new StopCommand( componentClient );
		} else if ( BrokerConstants.CLEAN_CMD_NAME.equals(commandName) ) {
			return new BrokerCleanCommand(componentClient);
		} else if ( BrokerConstants.CANCELJOB_CMD_NAME.equals(commandName) ) {
			return new BrokerCancelJobCommand(componentClient);
		} else if ( BrokerConstants.ADDJOB_CMD_NAME.equals(commandName) ) {
			return new BrokerAddJobCommand(componentClient);
		} else if ( BrokerConstants.STATUS_CMD_NAME.equals(commandName) ) {
			return new BrokerStatusCommand(componentClient);
		} else if ( BrokerConstants.WAIT_FOR_JOB_CMD_NAME.equals(commandName) ) {
			return new BrokerWaitForJobCommand(componentClient);
		} else if ( BrokerConstants.JOB_STATUS_CMD_NAME.equals(commandName) ) {
			return new BrokerJobStatusCommand(componentClient);
		}
		
		throw new InvalidCommandException(commandName);
	}

	private static ModuleContext initComponents() throws FileNotFoundException, IOException {
		BrokerComponentContextFactory contextFactory = new BrokerComponentContextFactory(
				new PropertiesFileParser(BrokerConfiguration.PROPERTIES_FILENAME));
		ModuleContext context = contextFactory.createContext();
		saveProperties(context);
		
		return context;
	}

	private static void saveProperties(ModuleContext context) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.putAll(context.getProperties());
		properties.store(new FileOutputStream(BrokerConfiguration.PROPERTIES_FILENAME), null);
	}
	
}
