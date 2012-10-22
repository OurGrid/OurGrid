package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.request.DSGetDiscoveryServicesRequestTO;
import org.ourgrid.discoveryservice.response.DSHereAreDiscoveryServicesResponseTO;
import org.ourgrid.discoveryservice.response.PersistNetworkResponseTO;

/**
 * Requirement 511
 */
public class DSGetDiscoveryServicesRequester implements RequesterIF<DSGetDiscoveryServicesRequestTO>{
	
	public List<IResponseTO> execute(DSGetDiscoveryServicesRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceDAO discoveryServiceDAO = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		String dsAddress = request.getDsAddress();
		
		DiscoveryServiceInfo dsInfo = discoveryServiceDAO.getDSInfo(dsAddress);
		
		if (dsInfo != null) {
			dsInfo.setAsUp();
		} else {
			discoveryServiceDAO.addDiscoveryService(new DiscoveryServiceInfo(dsAddress, true), new LinkedHashSet<String>());
			
			PersistNetworkResponseTO to = new PersistNetworkResponseTO();
			to.setDiscoveryServicesAddresses(discoveryServiceDAO.getAllDiscoveryServicesInfos());
			
			responses.add(to);
		}
		
		responses.add(new LoggerResponseTO("The Discovery Service " + dsAddress + " requested my network list", 
				LoggerResponseTO.DEBUG));
		
		DSHereAreDiscoveryServicesResponseTO to = new DSHereAreDiscoveryServicesResponseTO();
		to.setDiscoveryServices(discoveryServiceDAO.getAllDiscoveryServicesInfos());
		to.setStubAddress(dsAddress);
		
		responses.add(to);
		
		return responses;
	}
	
}
