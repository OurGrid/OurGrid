package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.response.ExecutorKillCommandResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class ExecutorKillCommandSender implements SenderIF<ExecutorKillCommandResponseTO>{

	public void execute(ExecutorKillCommandResponseTO response, ServiceManager manager) {
		try {
			WorkerDAOFactory.getInstance().getExecutorDAO().getExecutor().killCommand(response.getHandle());
		} catch (ExecutorException e) {
			manager.getLog().error(WorkerControllerMessages.getExecutorExceptionMessage(), e);
		}
	}

}
