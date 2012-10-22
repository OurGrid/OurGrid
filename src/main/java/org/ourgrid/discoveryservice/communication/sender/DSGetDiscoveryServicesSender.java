package org.ourgrid.discoveryservice.communication.sender;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.response.DSGetDiscoveryServicesResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
/**
 * Requirement 511
 */
public class DSGetDiscoveryServicesSender implements SenderIF<DSGetDiscoveryServicesResponseTO> {

	public void execute(DSGetDiscoveryServicesResponseTO response,
			ServiceManager manager) {
		DiscoveryService discoveryService = (DiscoveryService) manager.getStub(ServiceID.parse(response.getDsAddress()), DiscoveryService.class);
		
		DiscoveryService myDiscoveryService = (DiscoveryService) manager.getObjectDeployment(
				DiscoveryServiceConstants.DS_OBJECT_NAME).getObject();
		
		discoveryService.getDiscoveryServices(myDiscoveryService);
	}

}
