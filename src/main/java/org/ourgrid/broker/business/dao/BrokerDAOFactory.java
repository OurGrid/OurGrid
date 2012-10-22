package org.ourgrid.broker.business.dao;

import org.ourgrid.broker.communication.dao.BrokerMessageProcessorDAO;



/**
 * Requirement 302
 */
public class BrokerDAOFactory {
	
	
	private static BrokerDAOFactory daoFactory;
	
	
	private JobCounterDAO jobCounterDAO;
	private JobDAO jobDAO;
	private PeerDAO peerDAO;
	private WorkerDAO workerDAO;
	private BrokerMessageProcessorDAO brokerMessageProcessorDAO;
	
	
	private BrokerDAOFactory() {}
	
	
	public static BrokerDAOFactory getInstance() {
		if (daoFactory == null)
			daoFactory = new BrokerDAOFactory();
		
		return daoFactory;
	}
	
	public JobCounterDAO getJobCounterDAO() {
		if(jobCounterDAO == null){
			jobCounterDAO = new JobCounterDAO();
		}
		return jobCounterDAO;
	}
	
	public JobDAO getJobDAO() {
		if(jobDAO == null){
			jobDAO = new JobDAO();
		}
		return jobDAO;
	}

	public PeerDAO getPeerDAO() {
		if(peerDAO == null){
			peerDAO = new PeerDAO();
		}
		return peerDAO;
	}
	
	public WorkerDAO getWorkerDAO() {
		if (workerDAO == null) {
			workerDAO = new WorkerDAO();
		}
		
		return workerDAO;
	}
	
	public BrokerMessageProcessorDAO getBrokerMessageProcessorDAO() {
		if (brokerMessageProcessorDAO == null) {
			brokerMessageProcessorDAO = new BrokerMessageProcessorDAO();
		}
		
		return brokerMessageProcessorDAO;
	}
	
	public void reset() {
		jobCounterDAO = null;
		jobDAO = null;
		peerDAO = null;
		workerDAO = null;
		brokerMessageProcessorDAO = null;
	}
}
