package org.ourgrid.worker.business.dao;

import org.ourgrid.worker.communication.dao.ExecutorDAO;
import org.ourgrid.worker.communication.dao.FutureDAO;
import org.ourgrid.worker.communication.dao.WorkerMessageProcessorDAO;


public class WorkerDAOFactory {
	
	
	private EnvironmentDAO environmentDAO;
	private ExecutionDAO executionDAO;
	private FileTransferDAO fileTransferDAO;
	private IdlenessDetectorDAO idlenessDetectorDAO;
	private WorkAccountingDAO workAccountingDAO;
	private WorkerSpecDAO workerSpecDAO;
	private WorkerStatusDAO workerStatusDAO;
	
	//communication
	private FutureDAO futureDAO;
	private ExecutorDAO executorDAO;
	private WorkerMessageProcessorDAO workerMessageProcessorDAO;
	
	private static WorkerDAOFactory daoFactory;
	
	private WorkerDAOFactory() {}
	
	
	public static WorkerDAOFactory getInstance() {
		if (daoFactory == null)
			daoFactory = new WorkerDAOFactory();
		
		return daoFactory;
	}
	
	
	public EnvironmentDAO getEnvironmentDAO() {
		if (environmentDAO == null)
			environmentDAO = new EnvironmentDAO();
		
		return environmentDAO;
	}
	
	public ExecutionDAO getExecutionDAO() {
		if (executionDAO == null)
			executionDAO = new ExecutionDAO();
		
		return executionDAO;
	}
	
	public FileTransferDAO getFileTransferDAO() {
		if (fileTransferDAO == null)
			fileTransferDAO = new FileTransferDAO();
		
		return fileTransferDAO;
	}
	
	public WorkAccountingDAO getWorkAccountingDAO() {
		if (workAccountingDAO == null)
			workAccountingDAO = new WorkAccountingDAO();
		
		return workAccountingDAO;
	}
	
	public WorkerSpecDAO getWorkerSpecDAO() {
		if (workerSpecDAO == null)
			workerSpecDAO = new WorkerSpecDAO();
		
		return workerSpecDAO;
	}
	
	public WorkerStatusDAO getWorkerStatusDAO() {
		if (workerStatusDAO == null)
			workerStatusDAO = new WorkerStatusDAO();
		
		return workerStatusDAO;
	}

	public IdlenessDetectorDAO getIdlenessDetectorDAO() {
		if (idlenessDetectorDAO == null)
			idlenessDetectorDAO = new IdlenessDetectorDAO();
		
		return idlenessDetectorDAO;
	}
	
	public FutureDAO getFutureDAO() {
		if (futureDAO == null)
			futureDAO = new FutureDAO();
		
		return futureDAO;
	}

	public ExecutorDAO getExecutorDAO() {
		if (executorDAO == null)
			executorDAO = new ExecutorDAO();
		
		return executorDAO;
	}

	
	public WorkerMessageProcessorDAO getWorkerMessageProcessorDAO() {
		if (workerMessageProcessorDAO == null)
			workerMessageProcessorDAO = new WorkerMessageProcessorDAO();
		
		return workerMessageProcessorDAO;
	}

	public void reset() {
		environmentDAO = null;
		executionDAO = null;
		fileTransferDAO = null;
		workAccountingDAO = null;
		workerSpecDAO = null;
		workerStatusDAO = null;
		
		futureDAO = null;
		executorDAO = null;
		workerMessageProcessorDAO = null;
	}

}
