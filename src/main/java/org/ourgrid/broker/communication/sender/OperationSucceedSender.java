package org.ourgrid.broker.communication.sender;

import org.ourgrid.common.interfaces.control.BrokerControlClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class OperationSucceedSender implements SenderIF<OperationSucceedResponseTO> {

	public void execute(OperationSucceedResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		BrokerControlClient client = (BrokerControlClient) manager.getStub(clientID, BrokerControlClient.class);
		
		ControlOperationResult controlOperationResult = new ControlOperationResult();
		controlOperationResult.setErrorCause(response.getErrorCause());
		controlOperationResult.setResult(response.getResult());
		
		client.operationSucceed(controlOperationResult);
	}
}
