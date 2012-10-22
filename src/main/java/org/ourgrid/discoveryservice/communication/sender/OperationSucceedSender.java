package org.ourgrid.discoveryservice.communication.sender;

import org.ourgrid.common.interfaces.control.DiscoveryServiceControlClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class OperationSucceedSender implements SenderIF<OperationSucceedResponseTO> {

	public void execute(OperationSucceedResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		DiscoveryServiceControlClient client = (DiscoveryServiceControlClient) 
											manager.getStub(clientID, DiscoveryServiceControlClient.class);
		
		ControlOperationResult controlOperationResult = new ControlOperationResult();
		controlOperationResult.setErrorCause(response.getErrorCause());
		controlOperationResult.setResult(response.getResult());
		
		client.operationSucceed(controlOperationResult);
	}
}
