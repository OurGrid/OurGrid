package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.response.ExecutorKillPreparingAllocationResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class ExecutorKillPreparingAllocationSender implements SenderIF<ExecutorKillPreparingAllocationResponseTO>{

	public void execute(ExecutorKillPreparingAllocationResponseTO response, ServiceManager manager) {
		try {
			WorkerDAOFactory.getInstance().getExecutorDAO().getExecutor().killPreparingAllocation();
			
			WorkerDAOFactory.getInstance().getExecutionDAO().setExecutingKillPreparingAllocation(false);
			WorkerDAOFactory.getInstance().getFutureDAO().setBeginAllocationFuture(null);
		} catch (ExecutorException e) {
			manager.getLog().error(WorkerControllerMessages.getExecutorExceptionMessage(), e);
		}
	}

}
