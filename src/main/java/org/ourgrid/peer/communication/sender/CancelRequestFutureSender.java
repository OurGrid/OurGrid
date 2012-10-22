package org.ourgrid.peer.communication.sender;

import java.util.concurrent.Future;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.communication.dao.ScheduledRequestDAO;
import org.ourgrid.peer.response.CancelRequestFutureResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class CancelRequestFutureSender implements SenderIF<CancelRequestFutureResponseTO>{

	public void execute(CancelRequestFutureResponseTO response, ServiceManager manager) {

		ScheduledRequestDAO dao = ScheduledRequestDAO.getInstance();
		Future<?> future = dao.removeFuture(response.getRequestId());

		if (future != null) {
			future.cancel(true);
		}
	}


}
