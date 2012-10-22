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
package org.ourgrid.worker.ui.sync;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.ourgrid.common.command.InvalidCommandException;
import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.ui.MainUtil;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerComponentContextFactory;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.ui.sync.command.GetWorkerStatusCommand;
import org.ourgrid.worker.ui.sync.command.PauseWorkerCommand;
import org.ourgrid.worker.ui.sync.command.ResumeWorkerCommand;

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
		
		if (args.length != 1) {
			System.out.println(WorkerUIMessages.getWrongUsageMessage());
			System.exit( 1 );
		}
		
		final String commandName = args[0];
		String[ ] commandArgs = MainUtil.discardFirstParameter( args );
		
		try {
			ModuleContext context = initComponents();
			executeCommand(commandName, commandArgs, context);
		} catch ( Exception e ) {
			System.out.println(UIMessages.getErrorMessage( e, "worker", commandName ));
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
		
		boolean isStartCmd = WorkerConstants.START_CMD_NAME.equals(commandName);
		
		if (isStartCmd) {
			try {
				createWorkerComponent(componentContext);
			} catch(CommuneNetworkException cne) {
				treatMessage(cne);
			}
		}
		
		WorkerSyncComponentClient componentClient = new WorkerSyncComponentClient(componentContext, isStartCmd);
		
		Command command = getCommand( commandName, componentClient );
		command.run( args );
		
		if ( command instanceof StartCommand ) {
			System.out.println( WorkerUIMessages.STARTED );
		} else {
			System.out.println( WorkerUIMessages.getSuccessMessage( commandName ) );
			System.exit( 0 );
		}
		componentClient.stop();
		
	}

	private static void createWorkerComponent(ModuleContext componentContext)
			throws CommuneNetworkException, ProcessorStartException {
		new WorkerComponent(componentContext) {
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
	
	/**
	 * @param commandName
	 * @param componentClient
	 * @return
	 */
	private static Command getCommand(String commandName,
			WorkerSyncComponentClient componentClient) {
		
		if ( WorkerConstants.START_CMD_NAME.equals( commandName ) ) {
			return new StartCommand( componentClient );
		} else if ( WorkerConstants.STOP_CMD_NAME.equals( commandName ) ) {
			return new StopCommand( componentClient );
		} else if ( WorkerConstants.STATUS_CMD_NAME.equals( commandName ) ) {
			return new GetWorkerStatusCommand( componentClient );
		} else if ( WorkerConstants.PAUSE_CMD_NAME.equals( commandName ) ) {
			return new PauseWorkerCommand( componentClient );
		} else if ( WorkerConstants.RESUME_CMD_NAME.equals( commandName ) ) {
			return new ResumeWorkerCommand( componentClient );
		}
		
		throw new InvalidCommandException( commandName );
	}

	/**
	 * @return 
	 */
	private static ModuleContext initComponents() throws FileNotFoundException, IOException {
		WorkerComponentContextFactory contextFactory = new WorkerComponentContextFactory(
				new PropertiesFileParser(WorkerConfiguration.PROPERTIES_FILENAME));
		ModuleContext context = contextFactory.createContext();
		saveProperties(context);
		return contextFactory.createContext();
		
	}
	
	private static void saveProperties(ModuleContext context) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.putAll(context.getProperties());
		properties.store(new FileOutputStream(WorkerConfiguration.PROPERTIES_FILENAME), null);
	}

}
