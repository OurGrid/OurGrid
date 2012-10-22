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
package org.ourgrid.peer.ui.sync;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.ourgrid.common.command.InvalidCommandException;
import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.ui.MainUtil;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerComponentContextFactory;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.ui.sync.command.PeerAddBrokerCommand;
import org.ourgrid.peer.ui.sync.command.PeerQueryCommand;
import org.ourgrid.peer.ui.sync.command.PeerRemoveBrokerCommand;
import org.ourgrid.peer.ui.sync.command.PeerRemoveWorkerCommand;
import org.ourgrid.peer.ui.sync.command.PeerStartCommand;
import org.ourgrid.peer.ui.sync.command.PeerStatusCommand;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.Command;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.StartCommand;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.StopCommand;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.network.ConnectionListenerAdapter;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class Main {


	public static final String PEER_PROPERTIES = PeerConfiguration.PROPERTIES_FILENAME;

	public static void main(String[] args) {
		
		final String commandName = args[0];
		String[ ] commandArgs = MainUtil.discardFirstParameter( args );
		
		try {
			ModuleContext context = initComponents();
			executeCommand(commandName, commandArgs, context);
		} catch ( Exception e ) {
			System.out.println(UIMessages.getErrorMessage( e, "peer", commandName ));
			System.exit( 1 );
		}
	}

	public static void executeCommand(String commandName, String[] args, ModuleContext componentContext) throws Exception {
		
		boolean isStartCmd = PeerConstants.START_CMD_NAME.equals(commandName);
			
		if (isStartCmd) {
			try {
				createPeerComponent(componentContext);
			} catch(CommuneNetworkException cne) {
				treatMessage(cne);
			}
		}
		
		PeerSyncApplicationClient componentClient = new PeerSyncApplicationClient(componentContext, isStartCmd);
		
		Command command = getCommand( commandName, componentClient );
		command.run( args );
		
		if ( command instanceof StartCommand ) {
			System.out.println( PeerUIMessages.STARTED );
			componentClient.callExitOnOperationSucceed(false);
		} else {
			System.out.println( PeerUIMessages.getSuccessMessage( commandName ) );
			componentClient.callExitOnOperationSucceed(true);
		}
		componentClient.stop();
		
	}
	
	private static void createPeerComponent(ModuleContext componentContext)
			throws CommuneNetworkException, ProcessorStartException {
		new PeerComponent(componentContext) {
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

	private static Command getCommand(String commandName, PeerSyncApplicationClient componentClient) {
		if ( PeerConstants.START_CMD_NAME.equals( commandName ) ) {
			return new PeerStartCommand( componentClient );
		} else if ( PeerConstants.STOP_CMD_NAME.equals( commandName ) ) {
			return new StopCommand( componentClient );
		} else if ( PeerConstants.STATUS_CMD_NAME.equals( commandName ) ) {
			return new PeerStatusCommand( componentClient );
		} else if ( PeerConstants.ADD_BROKER_CMD_NAME.equals( commandName ) ) {
			return new PeerAddBrokerCommand( componentClient );
		} else if ( PeerConstants.REMOVE_BROKER_CMD_NAME.equals( commandName ) ) {
			return new PeerRemoveBrokerCommand( componentClient );
		} else if ( PeerConstants.REMOVE_WORKER_CMD_NAME.equals( commandName ) ) {
			return new PeerRemoveWorkerCommand( componentClient );
		} else if ( commandName.equals( PeerConstants.QUERY_CMD_NAME) ){
			return new PeerQueryCommand( componentClient );
		}
		
		throw new InvalidCommandException( commandName );
	}

	/**
	 * @return 
	 */
	private static ModuleContext initComponents() throws FileNotFoundException, IOException {
		PeerComponentContextFactory contextFactory = new PeerComponentContextFactory(
				new PropertiesFileParser(PEER_PROPERTIES));
		ModuleContext context = contextFactory.createContext();
		saveProperties(context);
		
		return context;
		
	}

	private static void saveProperties(ModuleContext context) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.putAll(context.getProperties());
		properties.store(new FileOutputStream(PEER_PROPERTIES), null);
	}

}
