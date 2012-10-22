package org.ourgrid.worker.communication.sender;

import java.util.concurrent.Future;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.response.CancelReportAccountingActionResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class CancelReportAccountingActionSender implements SenderIF<CancelReportAccountingActionResponseTO> {

	public void execute(CancelReportAccountingActionResponseTO response, ServiceManager manager) {
		Future<?> reportAccountingActionFuture = WorkerDAOFactory.getInstance().getFutureDAO().getReportAccountingActionFuture();
		
		if (reportAccountingActionFuture != null) {
			reportAccountingActionFuture.cancel(true);
		}
	}

}
