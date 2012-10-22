package org.ourgrid.peer.business.dao;

import org.ourgrid.peer.business.dao.statistics.JobDAO;
import org.ourgrid.peer.business.dao.statistics.LoginDAO;
import org.ourgrid.peer.business.dao.statistics.PeerDAO;
import org.ourgrid.peer.business.dao.statistics.WorkerDAO;
import org.ourgrid.peer.communication.dao.PeerCertificationDAO;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.dao.trust.TrustCommunitiesDAO;


public class PeerDAOFactory {
	
	
	private static PeerDAOFactory daoFactory;
	
	
	private PeerDAO peerDAO;
	private JobDAO jobDAO;
	private LoginDAO loginDAO;
	private UsersDAO usersDAO;
	private AccountingDAO accountingDAO;
	private WorkerDAO workerDAO;
	private LocalWorkersDAO localWorkersDAO;
	private DiscoveryServiceClientDAO discoveryServiceClientDAO;
	private AllocationDAO allocationDAO;
	private ConsumerDAO consumerDAO;
	private RequestDAO requestDAO;
	private TrustCommunitiesDAO trustCommunitiesDAO;
	private PeerCertificationDAO peerCertificationDAO;
	private PeerPropertiesDAO peerPropertiesDAO;
	
	private PeerDAOFactory() {}
	
	
	public static PeerDAOFactory getInstance() {
		if (daoFactory == null)
			daoFactory = new PeerDAOFactory();
		
		return daoFactory;
	}
	
	public TrustCommunitiesDAO getTrustCommunitiesDAO() {
		if (trustCommunitiesDAO == null) {
			trustCommunitiesDAO = new TrustCommunitiesDAO();
		}
		
		return trustCommunitiesDAO;
	}
	
	public RequestDAO getRequestDAO() {
		if (requestDAO == null) {
			requestDAO = new RequestDAO();
		}
		
		return requestDAO;
	}
	
	public PeerPropertiesDAO getPeerPropertiesDAO() {
		if (peerPropertiesDAO == null) {
			peerPropertiesDAO = new PeerPropertiesDAO();
		}
		
		return peerPropertiesDAO;
	}
	
	public PeerDAO getPeerDAO() {
		if (peerDAO == null) {
			peerDAO = new PeerDAO();
		}
		
		return peerDAO;
	}
	
	public JobDAO getJobDAO() {
		if (jobDAO == null) {
			jobDAO = new JobDAO();
		}
		
		return jobDAO;
	}
	
	public LoginDAO getLoginDAO() {
		if (loginDAO == null) {
			loginDAO = new LoginDAO();
		}
		
		return loginDAO;
	}
	
	public UsersDAO getUsersDAO() {
		if (usersDAO == null) {
			usersDAO = new UsersDAO();
		}
		
		return usersDAO;
	}
	
	public WorkerDAO getWorkerDAO() {
		if (workerDAO == null) {
			workerDAO = new WorkerDAO();
		}
		
		return workerDAO;
	}
	
	public AccountingDAO getAccountingDAO() {
		if (accountingDAO == null) {
			accountingDAO = new AccountingDAO();
		}
		
		return accountingDAO;
	}
	
	public LocalWorkersDAO getLocalWorkersDAO() {
		if (localWorkersDAO == null) {
			localWorkersDAO = new LocalWorkersDAO();
		}
		
		return localWorkersDAO;
	}
	
	public AllocationDAO getAllocationDAO() {
		if (allocationDAO == null) {
			allocationDAO = new AllocationDAO();
		}
		
		return allocationDAO;
	}
	
	public DiscoveryServiceClientDAO getDiscoveryServiceClientDAO() {
		if (discoveryServiceClientDAO == null) {
			discoveryServiceClientDAO = new DiscoveryServiceClientDAO();
		}
		
		return discoveryServiceClientDAO;
	}
	
	public ConsumerDAO getConsumerDAO() {
		if (consumerDAO == null) {
			consumerDAO = new ConsumerDAO();
		}
		
		return consumerDAO;
	}
	
	public PeerCertificationDAO getPeerCertificationDAO() {
		if (peerCertificationDAO == null) {
			peerCertificationDAO = new PeerCertificationDAO();
		}
		
		return peerCertificationDAO;
	}
	
	public void reset() {
		peerDAO = null;
		jobDAO = null;
		loginDAO = null;
		usersDAO = null;
		workerDAO = null;
		accountingDAO = null;
		localWorkersDAO = null;
		discoveryServiceClientDAO = null;
		consumerDAO = null;
		trustCommunitiesDAO = null;
		allocationDAO = null;
		peerCertificationDAO = null;
		peerPropertiesDAO = null;
		requestDAO = null;
	}



}
