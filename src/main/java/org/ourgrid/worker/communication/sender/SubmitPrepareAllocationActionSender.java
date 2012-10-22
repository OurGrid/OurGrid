package org.ourgrid.worker.communication.sender;

import java.util.concurrent.Future;

import org.ourgrid.common.executor.Executor;
import org.ourgrid.common.interfaces.WorkerExecutionServiceClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.communication.actions.PrepareAllocationAction;
import org.ourgrid.worker.response.SubmitPrepareAllocationActionResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class SubmitPrepareAllocationActionSender implements SenderIF<SubmitPrepareAllocationActionResponseTO> {

	public void execute(SubmitPrepareAllocationActionResponseTO response, ServiceManager manager) {
		Executor executor = WorkerDAOFactory.getInstance().getExecutorDAO().getExecutor();
		
		WorkerExecutionServiceClient executionClient = 
			(WorkerExecutionServiceClient) manager.getObjectDeployment(
					WorkerConstants.WORKER_EXECUTION_CLIENT).getProxy();
		
		PrepareAllocationAction prepareAllocationAction = 
			new PrepareAllocationAction(executor, executionClient);

		WorkerComponent workerComponent = (WorkerComponent) manager.getApplication();
		Future<?> future = workerComponent.submitAction(prepareAllocationAction);
		
		WorkerDAOFactory.getInstance().getFutureDAO().setBeginAllocationFuture(future);
	}

}
