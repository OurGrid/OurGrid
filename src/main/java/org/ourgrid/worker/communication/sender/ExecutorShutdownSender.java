package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.response.ExecutorShutdownResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * Sender of Executor shutdown method. 
 */
public class ExecutorShutdownSender implements SenderIF<ExecutorShutdownResponseTO>{

	/* (non-Javadoc)
	 * @see org.ourgrid.common.internal.SenderIF#execute(org.ourgrid.common.internal.IResponseTO, br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager)
	 */
	
	public void execute(ExecutorShutdownResponseTO response, ServiceManager manager) {
		try {
			// Calls the shutdown method from the actual worker executor.
			WorkerDAOFactory.getInstance().getExecutorDAO().getExecutor().shutdown();
		} catch (ExecutorException e) {
			//TODO log
		}
	}
}