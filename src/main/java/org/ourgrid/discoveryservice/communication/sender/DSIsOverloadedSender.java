package org.ourgrid.discoveryservice.communication.sender;

import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.response.DSIsOverloadedResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * Sender for method DSIsOverloaded
 */
public class DSIsOverloadedSender implements SenderIF<DSIsOverloadedResponseTO> {

	/* (non-Javadoc)
	 * @see org.ourgrid.common.internal.SenderIF#execute(org.ourgrid.common.internal.IResponseTO, br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager)
	 */
	@Override
	public void execute(DSIsOverloadedResponseTO response,
			ServiceManager manager) {
		
		DiscoveryServiceClient client = (DiscoveryServiceClient) manager.getStub(ServiceID.parse(response.getClientAddress()), DiscoveryServiceClient.class);
		
		client.dsIsOverloaded(response.getDSAddress());
		
	}
	

}
