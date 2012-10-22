package org.ourgrid.discoveryservice.business.dao;


/**
 * Requirement 502
 */
public class DiscoveryServiceDAOFactory {
	
	
	private static DiscoveryServiceDAOFactory daoFactory;
	
	
	private DiscoveryServiceDAO discoveryServiceDAO;
	
	
	private DiscoveryServiceDAOFactory() {}
	
	
	public static DiscoveryServiceDAOFactory getInstance() {
		if (daoFactory == null)
			daoFactory = new DiscoveryServiceDAOFactory();
		
		return daoFactory;
	}
	
	
	public DiscoveryServiceDAO getDiscoveryServiceDAO() {
		if (discoveryServiceDAO == null) {
			discoveryServiceDAO = new DiscoveryServiceDAO();
		}
		
		return discoveryServiceDAO;
	}
	
	public void reset() {
		discoveryServiceDAO = null;
	}
}
