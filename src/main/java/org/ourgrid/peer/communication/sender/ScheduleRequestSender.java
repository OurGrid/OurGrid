package org.ourgrid.peer.communication.sender;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.communication.dao.ScheduledRequestDAO;
import org.ourgrid.peer.response.ScheduleRequestResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class ScheduleRequestSender implements SenderIF<ScheduleRequestResponseTO>{

	public void execute(ScheduleRequestResponseTO response, ServiceManager manager) {
		
		Future<?> future = manager.scheduleActionWithFixedDelay(
				PeerConstants.REQUEST_WORKERS_ACTION_NAME, 
				response.getDelay(), TimeUnit.SECONDS, response.getRequestId());
		
		ScheduledRequestDAO.getInstance().putScheduledRequest(response.getRequestId(), 
				future);
		
	}


	
}
