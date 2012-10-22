package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.ScheduleActionWithFixedDelayResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class PeerScheduleActionWithFixedDelaySender implements SenderIF<ScheduleActionWithFixedDelayResponseTO>{

	public void execute(ScheduleActionWithFixedDelayResponseTO response,
			ServiceManager manager) {
		
		manager.scheduleActionWithFixedDelay(response.getActionName(), 
				response.getDelay(), response.getTimeUnit());
	}

	
}
