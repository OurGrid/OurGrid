package org.ourgrid.worker.communication.sender;

import java.util.concurrent.Future;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.communication.dao.FutureDAO;
import org.ourgrid.worker.response.CancelBeginAllocationActionResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class CancelBeginAllocationActionSender implements SenderIF<CancelBeginAllocationActionResponseTO> {

	public void execute(CancelBeginAllocationActionResponseTO response, ServiceManager manager) {
		FutureDAO futureDAO = WorkerDAOFactory.getInstance().getFutureDAO();
		Future<?> beginAllocationFuture = futureDAO.getBeginAllocationFuture();
		
		beginAllocationFuture.cancel(true);
	}

}
