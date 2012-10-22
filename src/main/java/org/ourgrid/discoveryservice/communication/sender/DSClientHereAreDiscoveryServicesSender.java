package org.ourgrid.discoveryservice.communication.sender;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.response.DSClientHereAreDiscoveryServicesResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DSClientHereAreDiscoveryServicesSender implements SenderIF<DSClientHereAreDiscoveryServicesResponseTO> {

	public void execute(DSClientHereAreDiscoveryServicesResponseTO response,
			ServiceManager manager) {
		DiscoveryServiceClient client = (DiscoveryServiceClient) manager.getStub(ServiceID.parse(response.getStubAddress()), DiscoveryServiceClient.class);
		
		List<String> dsAddresses = new ArrayList<String>();
		
		for (DiscoveryServiceInfo ds : response.getDiscoveryServices()) {			
			dsAddresses.add(ds.getDsAddress());
		}
		
		client.hereAreDiscoveryServices(dsAddresses);
	}

}
