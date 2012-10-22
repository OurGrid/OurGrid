package org.ourgrid.peer.communication.sender;

import java.util.concurrent.TimeUnit;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.ScheduleDelayedInterestOnDSResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class ScheduleDelayedInterestOnDSActionSender implements SenderIF<ScheduleDelayedInterestOnDSResponseTO>{

	public void execute(ScheduleDelayedInterestOnDSResponseTO response, ServiceManager manager) {
		
		manager.scheduleActionWithFixedDelay(
				PeerConstants.DELAYED_DS_INTEREST_ACTION_NAME, 
				manager.getContainerContext().parseIntegerProperty(
						PeerConfiguration.PROP_DELAY_OVERLOADED_DS_INTEREST), 
				TimeUnit.SECONDS, response.getDsAddress());
		
	}

	
}
