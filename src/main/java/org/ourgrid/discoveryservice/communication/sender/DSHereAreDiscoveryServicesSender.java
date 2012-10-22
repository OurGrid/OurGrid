package org.ourgrid.discoveryservice.communication.sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.response.DSHereAreDiscoveryServicesResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
/**
 * Requirement 511
 */
public class DSHereAreDiscoveryServicesSender implements SenderIF<DSHereAreDiscoveryServicesResponseTO> {

	public void execute(DSHereAreDiscoveryServicesResponseTO response,
			ServiceManager manager) {
		Set<DiscoveryServiceInfo> discoveryServices = response.getDiscoveryServices();
		
		List<ServiceID> dsIDs = new ArrayList<ServiceID>();
		
		for (DiscoveryServiceInfo ds : discoveryServices) {
			dsIDs.add(ServiceID.parse(ds.getDsAddress()));
		}
		
		DiscoveryService ds = (DiscoveryService) manager.getStub(ServiceID.parse(response.getStubAddress()), DiscoveryService.class);
		
		ds.hereAreDiscoveryServices(dsIDs);
	}

}
