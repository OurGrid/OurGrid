package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.executor.Executor;
import org.ourgrid.common.executor.ExecutorFactory;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.response.CreateExecutorResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class CreateExecutorSender implements SenderIF<CreateExecutorResponseTO> {

	public void execute(CreateExecutorResponseTO response, ServiceManager manager) {
		
		Executor executor = new ExecutorFactory(manager.getContainerContext(),
				manager.getLog()).buildExecutor();
		
		WorkerDAOFactory.getInstance().getExecutorDAO().setExecutor(executor);
	}

}
