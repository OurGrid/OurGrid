package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class OperationSucceedSender implements SenderIF<OperationSucceedResponseTO> {

	public void execute(OperationSucceedResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		
		WorkerControlClient client = (WorkerControlClient) (response.isRemoteClient() ? 
				manager.getStub(clientID, WorkerControlClient.class) :
				manager.getObjectDeployment(clientID.getServiceName()).getObject());
		
		ControlOperationResult controlOperationResult = new ControlOperationResult();
		controlOperationResult.setErrorCause(response.getErrorCause());
		controlOperationResult.setResult(response.getResult());
		
		client.operationSucceed(controlOperationResult);
	}
}
