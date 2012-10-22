package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.response.CancelExecutionActionResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class CancelExecutionActionSender implements SenderIF<CancelExecutionActionResponseTO> {

	public void execute(CancelExecutionActionResponseTO response, ServiceManager manager) {
		WorkerDAOFactory.getInstance().getFutureDAO().getExecutionActionFuture().cancel(true);
	}

}
