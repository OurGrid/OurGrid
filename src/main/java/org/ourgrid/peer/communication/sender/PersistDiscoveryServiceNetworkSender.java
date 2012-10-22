package org.ourgrid.peer.communication.sender;

import java.io.IOException;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.response.PersistDiscoveryServiceNetworkResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class PersistDiscoveryServiceNetworkSender implements SenderIF<PersistDiscoveryServiceNetworkResponseTO>{


	public void execute(PersistDiscoveryServiceNetworkResponseTO response,
			ServiceManager manager) {
		
		try {
			PeerConfiguration.persistNetwork(response.getUsersAtServer(), manager.getContainerContext());
		} catch (IOException e) {
			manager.getLog().error("Discovery Service Network data could not be persisted.", e);
		}
	}

	
}
