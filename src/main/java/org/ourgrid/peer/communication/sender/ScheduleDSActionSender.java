package org.ourgrid.peer.communication.sender;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.communication.dao.DiscoveryServiceAdvertDAO;
import org.ourgrid.peer.response.ScheduleDSActionResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class ScheduleDSActionSender implements SenderIF<ScheduleDSActionResponseTO>{

	public void execute(ScheduleDSActionResponseTO response, ServiceManager manager) {
		
		Future<?> advertActionFuture = manager.scheduleActionWithFixedDelay(
				PeerConstants.DS_ACTION_NAME, 
				manager.getContainerContext().parseIntegerProperty(
						PeerConfiguration.PROP_DS_UPDATE_INTERVAL), 
				TimeUnit.SECONDS);
		
		DiscoveryServiceAdvertDAO.getInstance().setAdvertActionFuture(advertActionFuture);
	}

	
}
